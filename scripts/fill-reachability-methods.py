#!/usr/bin/env python3
"""Fill missing methods on io.argorand.* types in GraalVM reachability metadata.

Uses Java reflection against compiled classes, then writes a copy of the JSON
next to the original (does not overwrite it).
"""

from __future__ import annotations

import argparse
import json
import os
import subprocess
import sys
from pathlib import Path

TYPE_PREFIX = "io.argorand."
DEFAULT_METADATA = (
    Path("src")
    / "main"
    / "resources"
    / "META-INF"
    / "native-image"
    / "io.argorand.poc"
    / "dcpass"
    / "reachability-metadata.json"
)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Add missing methods for io.argorand.* reflection types by comparing "
            "with compiled Java classes via reflection."
        )
    )
    parser.add_argument(
        "--input",
        type=Path,
        default=None,
        help="Path to reachability-metadata.json (default: project copy)",
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=None,
        help="Output path (default: <input-stem>.filled.json beside the input)",
    )
    parser.add_argument(
        "--prefix",
        default=TYPE_PREFIX,
        help=f'Type name prefix to process (default: "{TYPE_PREFIX}")',
    )
    parser.add_argument(
        "--compile",
        action="store_true",
        help="Run ./mvnw compile even if target/classes already exists",
    )
    parser.add_argument(
        "--verbose",
        action="store_true",
        help="Print every skipped runtime-generated type (CGLIB, Hibernate accessors, etc.)",
    )
    return parser.parse_args()


def repo_root() -> Path:
    return Path(__file__).resolve().parent.parent


def ensure_compiled(root: Path, force: bool) -> Path:
    classes_dir = root / "target" / "classes"
    marker = classes_dir / "io" / "argorand"
    if force or not marker.exists():
        print("Compiling Java classes with ./mvnw compile ...", file=sys.stderr)
        subprocess.run(
            [
                str(root / "mvnw"),
                "-q",
                "-Dskip.installnodenpm",
                "-Dskip.npm",
                "-DskipTests",
                "compile",
            ],
            cwd=root,
            check=True,
        )
    if not classes_dir.exists():
        raise SystemExit(f"Compiled classes not found at {classes_dir}")
    return classes_dir


def maven_classpath(root: Path) -> str:
    cp_file = root / "target" / "reachability-classpath.txt"
    cp_file.parent.mkdir(parents=True, exist_ok=True)
    print("Resolving Maven compile classpath ...", file=sys.stderr)
    subprocess.run(
        [
            str(root / "mvnw"),
            "-q",
            "-Dskip.installnodenpm",
            "-Dskip.npm",
            "dependency:build-classpath",
            f"-Dmdep.outputFile={cp_file}",
        ],
        cwd=root,
        check=True,
    )
    return cp_file.read_text(encoding="utf-8").strip()


GENERATED_TYPE_MARKERS = (
    "$$SpringCGLIB$$",
    "__Accessor_",
    "BeanInfo",
    "Customizer",
)


def is_generated_type(type_name: str) -> bool:
    return any(marker in type_name for marker in GENERATED_TYPE_MARKERS)


def report_helper_stderr(stderr: str, verbose: bool) -> None:
    generated_skips = 0
    other_lines: list[str] = []
    for raw_line in stderr.splitlines():
        line = raw_line.strip()
        if not line:
            continue
        if line.startswith("SKIP ") and is_generated_type(line):
            generated_skips += 1
            if verbose:
                print(line, file=sys.stderr)
            continue
        other_lines.append(line)
    for line in other_lines:
        print(line, file=sys.stderr)
    if generated_skips and not verbose:
        print(
            f"Skipped {generated_skips} runtime-generated types "
            "(CGLIB proxies, Hibernate accessors, BeanInfo). Use --verbose to list them.",
            file=sys.stderr,
        )


def load_reflected_methods(
    root: Path, classes_dir: Path, type_names: list[str], verbose: bool
) -> dict[str, list[dict] | None]:
    helper = Path(__file__).resolve().parent / "ListDeclaredMethods.java"
    if not helper.exists():
        raise SystemExit(f"Missing reflection helper: {helper}")

    classpath = os.pathsep.join(
        part for part in (str(classes_dir), maven_classpath(root)) if part
    )
    env = os.environ.copy()
    env["CLASSPATH"] = classpath

    result = subprocess.run(
        ["java", str(helper)],
        input="\n".join(type_names) + "\n",
        cwd=root,
        capture_output=True,
        text=True,
        check=False,
        env=env,
    )
    if result.stderr:
        report_helper_stderr(result.stderr, verbose)
    if result.returncode != 0:
        raise SystemExit(
            f"ListDeclaredMethods.java failed with exit code {result.returncode}"
        )
    if not result.stdout.strip():
        raise SystemExit("ListDeclaredMethods.java produced no output")
    return json.loads(result.stdout)


def method_key(method: dict) -> tuple[str, tuple[str, ...]]:
    params = method.get("parameterTypes") or []
    return (method["name"], tuple(params))


def merge_methods(existing: list[dict] | None, reflected: list[dict]) -> tuple[list[dict], int]:
    current = list(existing or [])
    seen = {method_key(method) for method in current if "name" in method}
    added = 0
    for method in reflected:
        key = method_key(method)
        if key in seen:
            continue
        current.append({"name": method["name"], "parameterTypes": list(method.get("parameterTypes") or [])})
        seen.add(key)
        added += 1
    return current, added


def main() -> int:
    args = parse_args()
    root = repo_root()
    input_path = (args.input or (root / DEFAULT_METADATA)).resolve()
    if not input_path.is_file():
        raise SystemExit(f"Metadata file not found: {input_path}")

    output_path = args.output
    if output_path is None:
        output_path = input_path.with_name(f"{input_path.stem}.filled{input_path.suffix}")
    else:
        output_path = output_path.resolve()

    with input_path.open(encoding="utf-8") as handle:
        metadata = json.load(handle)

    reflection = metadata.get("reflection")
    if not isinstance(reflection, list):
        raise SystemExit('Expected top-level "reflection" array in metadata JSON')

    type_names: list[str] = []
    entries_by_type: dict[str, list[dict]] = {}
    for entry in reflection:
        if not isinstance(entry, dict):
            continue
        type_name = entry.get("type")
        if not isinstance(type_name, str) or not type_name.startswith(args.prefix):
            continue
        type_names.append(type_name)
        entries_by_type.setdefault(type_name, []).append(entry)

    if not type_names:
        raise SystemExit(f'No reflection types starting with "{args.prefix}" found')

    unique_types = list(dict.fromkeys(type_names))
    classes_dir = ensure_compiled(root, args.compile)
    reflected = load_reflected_methods(root, classes_dir, unique_types, args.verbose)

    updated = 0
    already_complete = 0
    skipped = 0
    added_methods = 0

    for type_name in unique_types:
        members = reflected.get(type_name)
        if members is None:
            skipped += 1
            continue
        for entry in entries_by_type[type_name]:
            merged, added = merge_methods(entry.get("methods"), members)
            if added:
                entry["methods"] = merged
                updated += 1
                added_methods += added
            else:
                already_complete += 1

    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", encoding="utf-8") as handle:
        json.dump(metadata, handle, indent=2, ensure_ascii=False)
        handle.write("\n")

    print(f"Processed {len(unique_types)} {args.prefix}* types")
    print(f"  updated:           {updated}")
    print(f"  already complete:  {already_complete}")
    print(f"  skipped (no class): {skipped}")
    print(f"  methods added:     {added_methods}")
    print(f"Wrote {output_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
