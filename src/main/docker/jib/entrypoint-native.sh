#!/bin/sh
# Entrypoint for GraalVM native binary (no java, no JVM opts)

echo "The application will start in ${JHIPSTER_SLEEP:-0}s..." && sleep ${JHIPSTER_SLEEP:-0}

exec /app/application "$@"
