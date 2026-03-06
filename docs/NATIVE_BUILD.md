# GraalVM Native Image Build

## Prerequisites

1. **Install GraalVM** (includes `native-image`):

   ```bash
   # Using SDKMAN (recommended)
   sdk install java 25.0.2-graal
   sdk use java 25.0.2-graal

   ```

2. **Verify**:
   ```bash
   java -version   # Should show GraalVM
   gu install native-image   # If native-image is not included
   ```

## Build Options

### Option A: Local native binary (no Docker)

```bash
./mvnw -Pnative -Pprod package
```

Produces `target/dcpass` (Linux) or `target/dcpass.exe` (Windows).

Run locally:
```bash
./target/dcpass
```

### Option B: Docker image via Spring Boot Buildpacks

```bash
./mvnw -Pnative -Pprod spring-boot:build-image
```

Creates a Docker image (default name: `dcpass:0.0.1-SNAPSHOT`).

### Option C: Docker image via custom Dockerfile (forces amd64 on arm64 Mac)

Builds entirely inside Docker with `--platform linux/amd64` so the image runs on Linux amd64 VMs:

```bash
docker buildx build --platform linux/amd64 -f src/main/docker/Dockerfile.native -t dcpass:native .
```

On arm64 Mac this uses QEMU emulation (~15–30 min). Verify architecture:
```bash
docker inspect dcpass:native --format '{{.Architecture}}'  # should print: amd64
```

### Option D: Native binary only (amd64 on arm64 Mac)

Builds the binary and extracts it—no Docker image. Use when you want to run the binary directly on your VM:

```bash
./scripts/build-native-amd64.sh
```

Produces `dcpass-amd64` in the project root. Copy to your VM:

```bash
scp dcpass-amd64 user@your-vm:/opt/dcpass/dcpass
```

Or manually:

```bash
docker buildx build --platform linux/amd64 --target builder -f src/main/docker/Dockerfile.native -t dcpass-builder .
docker create --name dcpass-extract dcpass-builder
docker cp dcpass-extract:/build/target/dcpass ./dcpass-amd64
docker rm dcpass-extract
chmod +x dcpass-amd64
```

## Notes

- **Docker memory**: For native builds in Docker, allocate at least 8GB to Docker (Docker Desktop → Settings → Resources → Memory).
- **First build** can take 5–15 minutes (native compilation).
- **JHipster** apps may need native hints for Liquibase, JPA, OAuth2, etc. Add hints if you see `ClassNotFoundException` or reflection errors.
- **Memory**: Native builds need ~4–8GB RAM. Use `-Dspring.aot.enabled=true` if needed.
- **Fallback**: Remove `--no-fallback` from the native plugin config if the build fails (produces a hybrid image).
