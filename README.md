# PayLink — Project Foundation

Java 21 + Spring Boot + Maven + SQL Server + Docker Compose.

This repository is a **starter foundation**, not the completed PayLink system. It provides a bootable Spring Boot application, SQL Server container configuration, a first migration, one smoke test, and a GitHub Actions build/test workflow.

## Prerequisites

- Docker Desktop on Windows, or Docker Engine + Docker Compose on Fedora
- Git
- Java 21 and Maven only if you want to run/build outside Docker

## Run with Docker Compose

1. Copy `.env.example` to `.env`.
2. Change `DB_SA_PASSWORD` in `.env` to a strong local password.
3. Run:

   ```bash
   docker compose up --build
   ```

4. Check:
   - App: http://localhost:8080/
   - Health: http://localhost:8080/actuator/health

Stop with `docker compose down`. To also delete the local SQL Server data volume, use `docker compose down -v` (this permanently deletes the container database data).

## Run tests locally

Requires Java 21 and Maven:

```bash
mvn clean verify
```

The GitHub Actions workflow currently runs the same Maven verification command on pushes and pull requests targeting `main`.

## Project structure

```text
.
├── .github/workflows/ci.yml
├── docs/
│   ├── adr/
│   ├── design/
│   ├── testing/
│   ├── requirements.md
│   └── prompt-log.md
├── src/
│   ├── main/
│   │   ├── java/com/paylink/
│   │   │   └── shared/api/
│   │   └── resources/db/migration/
│   └── test/java/com/paylink/
├── Dockerfile
├── compose.yaml
└── pom.xml
```

## Architecture direction

Start as a modular monolith. Planned modules (to be implemented behind clear package boundaries):

- `accounts` — accounts, wallets, ledger, holds and refunds
- `billercatalog` — billers, services and required customer inputs
- `payment` — payment lifecycle, idempotency and orchestration
- `billerintegration` — adapters for external/mock billers
- `fees` — fee and agent commission strategies
- `notifications` — SMS/push/email delivery and retry
- `fraud` — optional fraud rules
- `reconciliation` — optional settlement matching

Each module should eventually contain its own `api`, `application`, `domain`, and `infrastructure` packages where appropriate. Keep money values in `BigDecimal`; never use floating-point types for money.

## Important next steps

1. Agree on module boundaries and package conventions with the team.
2. Complete `/docs/requirements.md` with at least 25 user stories and measurable NFRs.
3. Add the required PlantUML/Mermaid source diagrams under `/docs/design/`.
4. Design the domain and database schema before adding business tables/migrations.
5. Implement domain logic with tests first; target at least 70% domain line coverage.
6. Expand the API to at least 20 documented REST endpoints and add OpenAPI.
7. Add mock biller services and the eight required hard-case automated tests.
8. Extend CI to report coverage and build images as the project matures.

## Notes

- The SQL Server image uses the Developer edition for local development only.
- Never commit `.env`, credentials, real customer data, or secrets.
- The SQL Server data is persisted in the `sqlserver_data` Docker volume.
- This foundation has **not been executed or verified locally** in the environment where it was generated. Run it on your machine and share any errors before treating it as validated.
