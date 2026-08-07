# AGENTS.md

This file governs Codex and every other agentic CLI working anywhere in this repository. Follow it together with the user's request. More deeply nested `AGENTS.md` files, if added later, may refine these rules for their subtree.

## Project map

This is a full-stack workout tracker with two applications:

- `tracker-app/`: React 19, TypeScript, Vite, Tailwind/DaisyUI, TanStack Query, Axios, Vitest, Testing Library, and Playwright.
- `workout-tracker/`: Java 21, Spring Boot, Spring MVC/Security, Spring Data JPA, PostgreSQL, Maven, JUnit 5, Mockito, AssertJ, MockMvc, H2, and Testcontainers.
- `compose.yml`: local PostgreSQL, API, and frontend stack.
- `README.md`: product behavior, architecture, setup, API overview, and calculations.
- `workout-tracker/TESTING.md`: backend test, mutation-test, and performance-test guidance.

Before changing code, read the relevant build configuration, nearby source files, and nearby tests. Preserve unrelated user changes and never commit secrets, `.env` files, generated build output, or dependency directories.

## Required test-first workflow

All agents MUST use test-driven development for behavior changes and bug fixes.

1. Translate the requested behavior into an observable assertion at the lowest useful test level.
2. Add or update the test **before writing the implementation**.
3. Run that test and confirm it fails for the expected reason. A test that passes before implementation does not prove the new behavior; strengthen or correct it.
4. Write the smallest implementation that makes the test pass.
5. Run the focused test again, then the relevant wider suite and static checks.
6. Refactor only while tests remain green.

Do not weaken, delete, skip, or over-mock a test merely to make a change pass. Test public behavior and meaningful boundaries rather than private implementation details. When reproducing a bug, the first test must fail on the bug and pass with the fix. If a failing test cannot be run because of an environmental limitation, preserve the test-first ordering and clearly report what blocked execution.

Pure documentation, formatting, configuration-only, or behavior-preserving refactors may not require a new test. Agents must still run proportionate existing checks. If a refactor changes observable behavior, it is not exempt.

## Architecture and placement

### Backend

Keep backend code under `workout-tracker/src/main/java/com/pxbzi/workout_tracker` and organize it by domain (`analytics`, `data_transfers`, `exercises`, `gemini`, `muscles`, `weights`, `workouts`, or `workout_sets`).

- Controllers own HTTP routing, request validation, status codes, and response construction. Keep business rules out of controllers.
- Services own business logic, entity/DTO mapping orchestration, transactions, and repository coordination.
- Repositories own persistence queries.
- Put entities and request/response DTOs in the domain's `models` package. Keep API payloads as DTOs; do not expose persistence entities as the contract.
- Preserve the `/v1` API namespace and existing global error-response behavior.
- Put unit tests in the matching package under `src/test/java`. Put cross-layer HTTP/database/contract tests in `com.pxbzi.workout_tracker.integration`.
- Use unit tests with JUnit 5, Mockito, and AssertJ for service logic; MockMvc or Spring integration tests for HTTP, validation, serialization, transactions, and persistence behavior.
- Use the `test` Spring profile and existing H2 test configuration unless PostgreSQL-specific behavior requires the Testcontainers compatibility test.
- Do not make real Gemini calls in automated tests. Mock or fake the gateway and test failure behavior as well as successful responses.

### Frontend

Keep frontend code under `tracker-app/src` according to its existing responsibility:

- `api/`: Axios client configuration and endpoint functions.
- `hooks/`: TanStack Query hooks and query keys.
- `components/`: UI, forms, charts, cards, and tables.
- `lib/`: shared TypeScript DTOs and non-UI types.
- `src/tests/`: all frontend tests and shared setup. Mirror the source layout beneath it, such as `tests/api/`, `tests/components/`, `tests/hooks/`, and `tests/e2e/`; do not colocate test files with production code.

Use API modules from hooks/components rather than issuing ad hoc HTTP requests. Keep server-state handling in TanStack Query and preserve types across backend DTOs, frontend DTOs, and API functions. Prefer accessible roles and labels, and query those in Testing Library and Playwright tests. Test loading, error, empty, and successful states when the change affects them.

## Cross-stack contracts and domain rules

- Treat backend JSON DTOs and frontend TypeScript DTOs as one contract. When a payload changes, update both sides and add contract/API tests plus affected frontend tests.
- Preserve the domain invariants documented in `README.md`, including primary-muscle membership, workout/set ownership, bodyweight calculations, date semantics, and weekly analytics behavior.
- Keep calculation rules in backend services, not duplicated in presentation components.
- Database schema/entity changes require persistence coverage. Consider PostgreSQL compatibility whenever behavior may differ from H2.
- Never expose `GEMINI_API_KEY`, database credentials, or other secrets in source, tests, logs, fixtures, or responses.
- Do not introduce new libraries, frameworks, architectural layers, or broad rewrites unless the task requires them. Prefer the existing patterns and dependencies.

## Verification commands

Run focused tests during development, then the checks relevant to the changed area. From `tracker-app/`:

```bash
pnpm test
pnpm lint
pnpm typecheck
pnpm build
pnpm test:e2e
```

From `workout-tracker/` on Windows:

```powershell
.\mvnw.cmd test
```

On macOS/Linux:

```bash
./mvnw test
```

Use `mvn clean test` when a clean backend build is important. The Testcontainers PostgreSQL test runs when Docker is available and otherwise skips. Mutation analysis and k6 smoke tests are optional unless the change is high-risk or explicitly requests them; see `workout-tracker/TESTING.md`.

Playwright starts both applications and requires Java 21, frontend dependencies, and a Chromium installation. Do not claim a check passed unless it was actually run. In the final handoff, list tests/checks run, their results, and any checks not run with the reason.

## Change discipline

- Keep changes scoped to the request; avoid opportunistic rewrites.
- Follow the style of the surrounding file. Do not perform repository-wide formatting for a localized task.
- Update `README.md`, API examples, or `TESTING.md` when setup, commands, endpoints, payloads, calculations, or testing practices change.
- Avoid destructive Git or filesystem commands. Do not discard, overwrite, or stage unrelated work.
- Do not edit generated artifacts such as `target/`, `dist/`, coverage output, Playwright reports, or lockfiles unless dependency changes explicitly require it.
- Before handoff, inspect the diff for accidental changes, debug logging, secrets, and mismatched frontend/backend contracts.
