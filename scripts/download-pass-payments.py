#!/usr/bin/env python3
"""Download the full DC Open Data "Payments from PASS" dataset as one CSV.

Source (the only public locator this script needs):
  https://opendata.dc.gov/datasets/3ab69ad2cbf049b1851f14cb2fcdd440_17/api

That Hub API page identifies item ``3ab69ad2cbf049b1851f14cb2fcdd440``,
layer ``17``. Open Data DC then exposes:

  * a cached full-table CSV via the Hub download API (fast; often days old)
  * the live ArcGIS GeoService (current rows; 1,000 per request)

Usage:
  python3 scripts/download-pass-payments.py
  python3 scripts/download-pass-payments.py -o /tmp/PASS_Payments.csv
  python3 scripts/download-pass-payments.py --via query
"""

from __future__ import annotations

import argparse
import csv
import json
import sys
import threading
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import datetime, timezone
from pathlib import Path
from typing import Any
from urllib.error import HTTPError, URLError
from urllib.parse import urlencode
from urllib.request import Request, urlopen

DATASET_API_URL = (
    "https://opendata.dc.gov/datasets/3ab69ad2cbf049b1851f14cb2fcdd440_17/api"
)
ITEM_ID = "3ab69ad2cbf049b1851f14cb2fcdd440"
LAYER_ID = 17
HUB_CSV_URL = (
    f"https://opendata.dc.gov/api/download/v1/items/{ITEM_ID}/csv?layers={LAYER_ID}"
)
ITEM_META_URL = (
    f"https://www.arcgis.com/sharing/rest/content/items/{ITEM_ID}?f=json"
)
USER_AGENT = "dcpass-download/1.0 (+https://opendata.dc.gov)"
PAGE_SIZE = 1000
HUB_POLL_SECONDS = 10
HUB_POLL_ATTEMPTS = 60
HTTP_TIMEOUT = 120
CHUNK_SIZE = 1024 * 1024
DATE_FIELDS = {
    "PAYMENTDATE",
    "INVOICEDATE",
    "ESTPAYMENTDATE",
    "RECORDUPDATEDDATE",
    "RECORDCREATED",
    "DCS_REC_CRT_DTTM",
    "DCS_LAST_MOD_DTTM",
}

ROOT = Path(__file__).resolve().parent.parent
DEFAULT_OUTPUT = ROOT / "PASS_Payments.csv"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Download the entire Payments from PASS dataset as one CSV."
    )
    parser.add_argument(
        "-o",
        "--output",
        type=Path,
        default=DEFAULT_OUTPUT,
        help=f"Destination CSV (default: {DEFAULT_OUTPUT})",
    )
    parser.add_argument(
        "--via",
        choices=("auto", "hub", "query"),
        default="auto",
        help="hub = cached Hub extract (fast, may be stale); "
        "query = live GeoService paging (current data); "
        "auto tries hub then falls back (default).",
    )
    parser.add_argument(
        "--workers",
        type=int,
        default=8,
        help="Parallel GeoService requests for --via query (default: 8).",
    )
    parser.add_argument(
        "--max-rows",
        type=int,
        default=None,
        help="Stop after N data rows (query mode only; useful for a smoke test).",
    )
    return parser.parse_args()


def http_get(url: str, timeout: int = HTTP_TIMEOUT):
    request = Request(url, headers={"User-Agent": USER_AGENT, "Accept": "*/*"})
    return urlopen(request, timeout=timeout)


def http_get_json(url: str) -> Any:
    with http_get(url) as response:
        return json.loads(response.read().decode("utf-8"))


def resolve_layer_url() -> str:
    """Read the GeoService URL advertised on the Hub item."""
    meta = http_get_json(ITEM_META_URL)
    url = (meta.get("url") or "").rstrip("/")
    if not url:
        raise RuntimeError(f"Hub item {ITEM_ID} did not include a GeoService URL")
    return url


def format_bytes(n: int) -> str:
    value = float(n)
    for unit in ("B", "KB", "MB", "GB"):
        if value < 1024 or unit == "GB":
            return f"{value:.1f} {unit}" if unit != "B" else f"{int(value)} B"
        value /= 1024
    return f"{n} B"


def stream_response_to_file(response, dest: Path) -> int:
    total = response.headers.get("Content-Length")
    expected = int(total) if total and total.isdigit() else None
    written = 0
    last_report = 0
    dest.parent.mkdir(parents=True, exist_ok=True)
    with dest.open("wb") as handle:
        while True:
            chunk = response.read(CHUNK_SIZE)
            if not chunk:
                break
            handle.write(chunk)
            written += len(chunk)
            if written - last_report >= 5 * CHUNK_SIZE:
                extra = f" / {format_bytes(expected)}" if expected else ""
                print(f"  downloaded {format_bytes(written)}{extra}", flush=True)
                last_report = written
    return written


def is_csv_response(response, url: str) -> bool:
    content_type = (response.headers.get("Content-Type") or "").lower()
    disposition = (response.headers.get("Content-Disposition") or "").lower()
    return (
        "csv" in content_type
        or "csv" in disposition
        or url.lower().split("?", 1)[0].endswith(".csv")
        or "octet-stream" in content_type
    )


def download_via_hub(dest: Path) -> None:
    print(f"Requesting Hub CSV extract:\n  {HUB_CSV_URL}")
    url = HUB_CSV_URL
    for attempt in range(1, HUB_POLL_ATTEMPTS + 1):
        with http_get(url) as response:
            content_type = response.headers.get("Content-Type", "")
            final_url = response.geturl()
            if response.status == 202 or "json" in content_type.lower():
                raw = response.read().decode("utf-8")
                try:
                    payload = json.loads(raw) if raw.strip() else {}
                except json.JSONDecodeError as exc:
                    raise RuntimeError(
                        f"Hub returned JSON content-type but body was not JSON: {raw[:200]!r}"
                    ) from exc
                next_url = (
                    payload.get("downloadUrl")
                    or payload.get("dataUrl")
                    or payload.get("url")
                    or payload.get("location")
                )
                status = payload.get("status") or payload.get("jobStatus") or "generating"
                print(
                    f"  Hub extract not ready (attempt {attempt}/{HUB_POLL_ATTEMPTS}, "
                    f"status={status!r}); retrying in {HUB_POLL_SECONDS}s"
                )
                if isinstance(next_url, str) and next_url.startswith("http"):
                    url = next_url
                time.sleep(HUB_POLL_SECONDS)
                continue
            if not is_csv_response(response, final_url):
                preview = response.read(300).decode("utf-8", errors="replace")
                raise RuntimeError(
                    f"Hub did not return CSV (content-type={content_type!r}): {preview!r}"
                )
            print(f"  streaming {final_url}")
            written = stream_response_to_file(response, dest)
        print(f"Wrote {format_bytes(written)}")
        return
    raise RuntimeError(
        f"Hub extract was not ready after {HUB_POLL_ATTEMPTS * HUB_POLL_SECONDS}s"
    )


def query_layer(layer_url: str, params: dict[str, Any]) -> dict[str, Any]:
    query_url = f"{layer_url}/query?{urlencode(params)}"
    last_error: Exception | None = None
    for delay in (0, 2, 5, 10):
        if delay:
            time.sleep(delay)
        try:
            return http_get_json(query_url)
        except (HTTPError, URLError, TimeoutError, json.JSONDecodeError) as exc:
            last_error = exc
            print(f"  query retry after {exc}", flush=True)
    raise RuntimeError(f"GeoService query failed: {last_error}") from last_error


def format_cell(field: str, value: Any) -> str:
    if value is None:
        return ""
    if field in DATE_FIELDS and isinstance(value, (int, float)):
        # ArcGIS date fields are milliseconds since epoch.
        dt = datetime.fromtimestamp(value / 1000, tz=timezone.utc)
        return dt.strftime("%Y/%m/%d %H:%M:%S+00")
    return str(value)


def field_names(layer_url: str) -> list[str]:
    meta = http_get_json(f"{layer_url}?f=json")
    fields = [f["name"] for f in meta.get("fields", []) if f.get("name")]
    if not fields:
        raise RuntimeError(f"Layer {layer_url} advertised no fields")
    return fields


def _attr(attrs: dict[str, Any], *names: str) -> Any:
    lookup = {key.lower(): value for key, value in attrs.items()}
    for name in names:
        if name.lower() in lookup:
            return lookup[name.lower()]
    return None


def objectid_bounds(layer_url: str) -> tuple[int, int]:
    payload = query_layer(
        layer_url,
        {
            "where": "1=1",
            "outStatistics": json.dumps(
                [
                    {
                        "statisticType": "min",
                        "onStatisticField": "OBJECTID",
                        "outStatisticFieldName": "min_id",
                    },
                    {
                        "statisticType": "max",
                        "onStatisticField": "OBJECTID",
                        "outStatisticFieldName": "max_id",
                    },
                ]
            ),
            "f": "json",
        },
    )
    features = payload.get("features") or []
    if not features:
        raise RuntimeError("GeoService did not return OBJECTID min/max")
    attrs = features[0].get("attributes") or {}
    min_id = _attr(attrs, "min_id")
    max_id = _attr(attrs, "max_id")
    if min_id is None or max_id is None:
        raise RuntimeError(f"Unexpected OBJECTID statistics: {attrs!r}")
    return int(min_id), int(max_id)


def query_page(layer_url: str, where: str) -> list[dict[str, Any]]:
    payload = query_layer(
        layer_url,
        {
            "where": where,
            "outFields": "*",
            "returnGeometry": "false",
            "orderByFields": "OBJECTID",
            "resultRecordCount": PAGE_SIZE,
            "f": "json",
        },
    )
    features = payload.get("features") or []
    return [f.get("attributes") or {} for f in features]


def write_rows(
    writer: csv.DictWriter, columns: list[str], page: list[dict[str, Any]]
) -> None:
    for attrs in page:
        writer.writerow({name: format_cell(name, attrs.get(name)) for name in columns})


def download_via_query(
    dest: Path, max_rows: int | None = None, workers: int = 8
) -> None:
    layer_url = resolve_layer_url()
    workers = max(1, workers)
    sequential = max_rows is not None or workers == 1
    mode = "sequential" if sequential else f"{workers} workers"
    print(
        f"Paging live GeoService ({PAGE_SIZE} rows/request, {mode}):\n"
        f"  {layer_url}"
    )
    count_payload = query_layer(
        layer_url, {"where": "1=1", "returnCountOnly": "true", "f": "json"}
    )
    total = count_payload.get("count")
    if total is not None:
        print(f"  {total:,} live records")
    columns = field_names(layer_url)
    dest.parent.mkdir(parents=True, exist_ok=True)

    if sequential:
        written_rows = _download_query_sequential(
            dest, layer_url, columns, total, max_rows
        )
    else:
        written_rows = _download_query_parallel(
            dest, layer_url, columns, total, workers
        )
    print(f"Wrote {written_rows:,} rows")


def _progress(written_rows: int, total: int | None, extra: str = "") -> None:
    suffix = extra if extra else ""
    if total:
        print(f"  {written_rows:,} / {total:,} rows{suffix}", flush=True)
    else:
        print(f"  {written_rows:,} rows{suffix}", flush=True)


def _download_query_sequential(
    dest: Path,
    layer_url: str,
    columns: list[str],
    total: int | None,
    max_rows: int | None,
) -> int:
    written_rows = 0
    last_oid = 0
    with dest.open("w", newline="", encoding="utf-8") as handle:
        writer = csv.DictWriter(handle, fieldnames=columns, extrasaction="ignore")
        writer.writeheader()
        while True:
            page = query_page(layer_url, f"OBJECTID > {last_oid}")
            if not page:
                break
            if max_rows is not None:
                page = page[: max(0, max_rows - written_rows)]
            write_rows(writer, columns, page)
            written_rows += len(page)
            last_oid = int(page[-1]["OBJECTID"])
            _progress(written_rows, total, f" (OBJECTID ≤ {last_oid})")
            if max_rows is not None and written_rows >= max_rows:
                break
            if len(page) < PAGE_SIZE:
                break
    return written_rows


def _download_query_parallel(
    dest: Path,
    layer_url: str,
    columns: list[str],
    total: int | None,
    workers: int,
) -> int:
    min_id, max_id = objectid_bounds(layer_url)
    ranges = [
        (lo, min(lo + PAGE_SIZE - 1, max_id))
        for lo in range(min_id, max_id + 1, PAGE_SIZE)
    ]
    print(f"  OBJECTID {min_id:,} … {max_id:,} ({len(ranges):,} pages)")
    written_rows = 0
    lock = threading.Lock()
    last_report = 0

    with dest.open("w", newline="", encoding="utf-8") as handle:
        writer = csv.DictWriter(handle, fieldnames=columns, extrasaction="ignore")
        writer.writeheader()

        def fetch_range(lo: int, hi: int) -> list[dict[str, Any]]:
            return query_page(layer_url, f"OBJECTID >= {lo} AND OBJECTID <= {hi}")

        with ThreadPoolExecutor(max_workers=workers) as pool:
            futures = [pool.submit(fetch_range, lo, hi) for lo, hi in ranges]
            for fut in as_completed(futures):
                page = fut.result()
                with lock:
                    write_rows(writer, columns, page)
                    written_rows += len(page)
                    if written_rows - last_report >= 25_000:
                        _progress(written_rows, total)
                        last_report = written_rows

        last_oid = max_id
        while True:
            page = query_page(layer_url, f"OBJECTID > {last_oid}")
            if not page:
                break
            write_rows(writer, columns, page)
            written_rows += len(page)
            last_oid = int(page[-1]["OBJECTID"])
            _progress(written_rows, total, f" (OBJECTID ≤ {last_oid})")
            if len(page) < PAGE_SIZE:
                break
        if written_rows != last_report:
            _progress(written_rows, total)
    return written_rows


def main() -> int:
    args = parse_args()
    output: Path = args.output
    partial = output.with_name(output.name + ".partial")
    print(f"Dataset API: {DATASET_API_URL}")
    print(f"Output:      {output}")
    try:
        if args.via in ("auto", "hub"):
            if args.max_rows is not None:
                print("Note: --max-rows is ignored for Hub downloads (full file).")
            try:
                download_via_hub(partial)
            except Exception as exc:
                if args.via == "hub":
                    raise
                print(f"Hub download failed ({exc}); falling back to GeoService paging")
                download_via_query(partial, args.max_rows, args.workers)
        else:
            download_via_query(partial, args.max_rows, args.workers)
        partial.replace(output)
    except KeyboardInterrupt:
        print("\nInterrupted; leaving partial file:", partial, file=sys.stderr)
        return 130
    except Exception as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 1
    print("Done.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
