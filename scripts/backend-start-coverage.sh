#!/usr/bin/env bash
# Start the Spring Boot backend with:
# - JaCoCo TCP agent (E2E Java coverage dump on port 6300)
# - GraalVM native-image-agent (reachability metadata under target/native/agent-output)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

JACOCO_VERSION="${JACOCO_VERSION:-0.8.14}"
JACOCO_PORT="${JACOCO_PORT:-6300}"
DESTFILE="${JACOCO_DESTFILE:-target/jacoco-e2e.exec}"
NATIVE_AGENT_OUT="${NATIVE_AGENT_OUT:-target/native/agent-output}"
AGENT_DIR="$HOME/.m2/repository/org/jacoco/org.jacoco.agent/${JACOCO_VERSION}"
AGENT_JAR="${AGENT_DIR}/org.jacoco.agent-${JACOCO_VERSION}-runtime.jar"

if [[ ! -f "$AGENT_JAR" ]]; then
  echo "Downloading JaCoCo agent ${JACOCO_VERSION}..."
  ./mvnw -q -ntp dependency:get -Dartifact="org.jacoco:org.jacoco.agent:${JACOCO_VERSION}:runtime"
fi

if [[ ! -f "$AGENT_JAR" ]]; then
  echo "JaCoCo agent jar not found at ${AGENT_JAR}" >&2
  exit 1
fi

mkdir -p "$(dirname "$DESTFILE")" "$NATIVE_AGENT_OUT"
rm -f "$DESTFILE"

# native-image-agent refuses to start if .lock exists; remove it only when the
# recorded PID is gone (leftover from an abrupt stop). Keep it if still alive.
LOCK_FILE="${NATIVE_AGENT_OUT}/.lock"
if [[ -f "$LOCK_FILE" ]]; then
  LOCK_PID="$(tr -d '[:space:]' < "$LOCK_FILE" || true)"
  if [[ -n "$LOCK_PID" ]] && kill -0 "$LOCK_PID" 2>/dev/null; then
    echo "ERROR: native-image-agent output dir is locked by running process ${LOCK_PID}." >&2
    echo "  Stop that JVM first, or point NATIVE_AGENT_OUT at a different directory." >&2
    exit 1
  fi
  echo "Removing stale native-image-agent lock (PID ${LOCK_PID:-unknown} is not running)"
  rm -f "$LOCK_FILE"
fi

JACOCO_ARGS="destfile=${DESTFILE},output=tcpserver,port=${JACOCO_PORT},address=*,append=true"
JVM_ARGS="-javaagent:${AGENT_JAR}=${JACOCO_ARGS} -agentlib:native-image-agent=config-output-dir=${NATIVE_AGENT_OUT}"

echo "Starting backend with JaCoCo + native-image-agent"
echo "  JaCoCo agent:  ${AGENT_JAR}"
echo "  JaCoCo dump:   TCP :${JACOCO_PORT} → ${DESTFILE}"
echo "  Native agent:  ${NATIVE_AGENT_OUT}"
echo "After E2E: ./npmw run e2e:coverage  (dumps + generates target/site/jacoco-e2e/)"

exec ./mvnw -Dskip.installnodenpm -Dskip.npm -ntp --batch-mode \
  "-Dspring-boot.run.jvmArguments=${JVM_ARGS}"
