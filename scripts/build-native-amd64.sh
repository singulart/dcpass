#!/bin/bash
# Build amd64 native binary on arm64 Mac (uses Docker + QEMU emulation).
# Output: ./dcpass-amd64 (Linux amd64 executable for your VM)
#
# Prerequisite: Docker Desktop → Settings → Resources → Memory: 8GB or more
#
# Usage: ./scripts/build-native-amd64.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
OUTPUT_DIR="${PROJECT_ROOT}"
OUTPUT_BINARY="${OUTPUT_DIR}/dcpass-amd64"

cd "$PROJECT_ROOT"

echo "Building amd64 native binary (this may take 15-30 min on arm64 Mac)..."
docker buildx build --platform linux/amd64 \
  --target builder \
  -f src/main/docker/Dockerfile.native \
  -t dcpass-builder .
echo "Build complete. Extracting binary..."

CONTAINER_ID=$(docker create dcpass-builder)
docker cp "$CONTAINER_ID:/build/target/dcpass" "$OUTPUT_BINARY"
docker rm "$CONTAINER_ID"
chmod +x "$OUTPUT_BINARY"

echo "Done. Binary: $OUTPUT_BINARY"
echo "Copy to your VM: scp $OUTPUT_BINARY user@your-vm:/opt/dcpass/dcpass"
