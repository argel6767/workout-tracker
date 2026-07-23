# API testing

Run the normal unit, controller, H2 integration, validation, security, and contract suites:

```bash
mvn clean test
```

The PostgreSQL compatibility test uses Testcontainers. It runs automatically when Docker is available and is skipped otherwise.

Run mutation analysis separately because it is intentionally slower:

```bash
mvn test-compile org.pitest:pitest-maven:mutationCoverage
```

The HTML report is generated under `target/pit-reports`.

Run the opt-in performance smoke test against a running API with [k6](https://grafana.com/docs/k6/latest/):

```bash
k6 run performance/workout-api-smoke.js
```

Override the target when necessary:

```bash
API_BASE_URL=http://localhost:8080 k6 run performance/workout-api-smoke.js
```
