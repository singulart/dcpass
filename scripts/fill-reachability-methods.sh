#!/usr/bin/env bash
# Fill missing methods on io.argorand.* types in GraalVM reachability-metadata.json
# using Java reflection. Writes a copy next to the original (does not overwrite).
#
# Usage:
#   ./scripts/fill-reachability-methods.sh
#   ./scripts/fill-reachability-methods.sh --compile
#   ./scripts/fill-reachability-methods.sh --input /path/to/reachability-metadata.json
#   ./scripts/fill-reachability-methods.sh --verbose
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if ! command -v python3 >/dev/null 2>&1; then
  echo "python3 is required" >&2
  exit 1
fi

if ! command -v java >/dev/null 2>&1; then
  echo "java is required" >&2
  exit 1
fi

exec python3 "$ROOT/scripts/fill-reachability-methods.py" "$@"
