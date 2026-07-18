# DC PASS (Procurement Automated Support System)

### End-to-end tests (Playwright)

Live-app E2E tests live under `e2e/`. They expect the frontend and backend to already be running locally:

```bash
./npmw run backend:start
./npmw run start
```

In another terminal:

```bash
./npmw run e2e
```

Optional: `./npmw run e2e:ui` (Playwright UI) or `./npmw run e2e:headed`. Override URLs with `E2E_BASE_URL` (default `http://localhost:4200`) and `E2E_BACKEND_URL` (default `http://localhost:8080`).

Coverage includes authentication, account settings/password, PASS contract search/CRUD, admin screens (users, metrics, health, configuration, logs, API docs, authorities), the contracts widget, and MCP SSE tools/resources.

#### E2E code coverage

Collect **frontend (Angular)** and **backend (Java)** coverage while the suite runs.

1. Start the backend **with the JaCoCo agent** (not plain `backend:start`):

```bash
./npmw run backend:start:coverage
./npmw run start
```

2. Run the suite (Playwright + dump/report JaCoCo):

```bash
./npmw run e2e:coverage
```

3. Review reports later:

```bash
./npmw run e2e:coverage:open            # Angular (V8) HTML
./npmw run e2e:coverage:open:summary    # Playwright + frontend summary
./npmw run e2e:coverage:open:backend    # Java (JaCoCo) HTML
```

| Report   | Location                                                                 |
| -------- | ------------------------------------------------------------------------ |
| Frontend | `coverage-e2e/index.html`, `lcov.info`                                   |
| Backend  | `target/site/jacoco-e2e/index.html` (scoped to `io.argorand.poc.dcpass`) |

`e2e:coverage` fails fast if the backend was not started with `backend:start:coverage` (JaCoCo TCP port 6300). Plain `./npmw run e2e` skips all coverage collection.

## Others

### Code quality using Sonar

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```bash
docker compose -f src/main/docker/sonar.yml up -d
```

Note: we have turned off forced authentication redirect for UI in [src/main/docker/sonar.yml](src/main/docker/sonar.yml) for out of the box experience while trying out SonarQube, for real use cases turn it back on.

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis:

```bash
./mvnw -Pprod clean verify sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```bash
./mvnw initialize sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

Additionally, Instead of passing `sonar.password` and `sonar.login` as CLI arguments, these parameters can be configured from [sonar-project.properties](sonar-project.properties) as shown below:

```bash
sonar.login=admin
sonar.password=admin
```

For more information, refer to the [Code quality page][].

### Docker Compose support

JHipster generates a number of Docker Compose configuration files in the [src/main/docker/](src/main/docker/) folder to launch required third party services.

For example, to start required services in Docker containers, run:

```bash
docker compose -f src/main/docker/services.yml up -d
```

To stop and remove the containers, run:

```bash
docker compose -f src/main/docker/services.yml down
```

[Spring Docker Compose Integration](https://docs.spring.io/spring-boot/reference/features/dev-services.html) is enabled by default. It's possible to disable it in `application.yml`:

```yaml
spring:
  ...
  docker:
    compose:
      enabled: false
```

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a Docker image of your app by running:

```bash
npm run java:docker
```

Or build an arm64 Docker image when using an arm64 processor OS, i.e., Apple Silicon chips (M\*), running:

```bash
npm run java:docker:arm64
```

Then run:

```bash
docker compose -f src/main/docker/app.yml up -d
```

For more information refer to [Docker and Docker-Compose](https://www.jhipster.tech/documentation-archive/v9.1.0/docker-compose/), this page also contains information on the Docker Compose sub-generator (`jhipster docker-compose`), which is able to generate Docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration](https://www.jhipster.tech/documentation-archive/v9.1.0/setting-up-ci/) page for more information.
