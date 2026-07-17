#!/usr/bin/env bash
# Dump JaCoCo data from a running backend agent and generate an HTML report.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

JACOCO_VERSION="${JACOCO_VERSION:-0.8.14}"
JACOCO_PORT="${JACOCO_PORT:-6300}"
DESTFILE="${JACOCO_DESTFILE:-target/jacoco-e2e.exec}"
REPORT_DIR="${JACOCO_REPORT_DIR:-target/site/jacoco-e2e}"

if ! python3 - <<PY >/dev/null 2>&1
import socket
s = socket.socket()
s.settimeout(1)
try:
    s.connect(("127.0.0.1", int("${JACOCO_PORT}")))
    s.close()
except OSError:
    raise SystemExit(1)
PY
then
  echo "ERROR: No JaCoCo agent listening on port ${JACOCO_PORT}." >&2
  echo "Restart the backend with coverage enabled:" >&2
  echo "  ./npmw run backend:start:coverage" >&2
  exit 1
fi

mkdir -p "$(dirname "$DESTFILE")"
echo "Dumping JaCoCo coverage from localhost:${JACOCO_PORT} → ${DESTFILE}"
./mvnw -q -ntp "org.jacoco:jacoco-maven-plugin:${JACOCO_VERSION}:dump" \
  "-Djacoco.address=127.0.0.1" \
  "-Djacoco.port=${JACOCO_PORT}" \
  "-Djacoco.destFile=${DESTFILE}" \
  "-Djacoco.reset=false" \
  "-Djacoco.append=true"

if [[ ! -s "$DESTFILE" ]]; then
  echo "ERROR: JaCoCo dump produced an empty file at ${DESTFILE}" >&2
  exit 1
fi

echo "Generating backend coverage report → ${REPORT_DIR}"
./mvnw -q -ntp -Pe2e-jacoco jacoco:report@report-e2e

if [[ ! -f "${REPORT_DIR}/index.html" ]]; then
  echo "ERROR: Expected report at ${REPORT_DIR}/index.html was not created" >&2
  exit 1
fi

echo "Backend coverage report: ${REPORT_DIR}/index.html"
echo "Open with: ./npmw run e2e:coverage:open:backend"
