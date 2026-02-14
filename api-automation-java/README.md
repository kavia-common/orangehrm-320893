# OrangeHRM API Automation (Java + REST Assured)

This module contains runnable API automation tests written with **REST Assured** + **JUnit 5**.

## What is covered

- Authentication flow validations (authorized vs unauthorized access)
- Employee workflows (create/list, basic lifecycle checks)
- Backend business logic validations (pagination, schema-ish checks, error contract)

> Note: OrangeHRM API auth can vary by deployment. This project supports:
> 1) Cookie-based login (typical for OrangeHRM web session), and
> 2) Bearer token auth (if your environment provides OAuth2 tokens).
>
> By default, these sample tests use cookie-based login.

## Configuration

Create a local `.env` file (not committed) based on `.env.example`, or provide environment variables.

Required:
- `BASE_URL` (e.g., `https://opensource-demo.orangehrmlive.com`)
- `API_BASE_PATH` (default `/api/v2`)
- `ADMIN_USERNAME`, `ADMIN_PASSWORD`

## Run

From `api-automation-java/`:

```bash
mvn test
```

Override at runtime:

```bash
mvn test -DBASE_URL=http://localhost:8080 -DADMIN_USERNAME=Admin -DADMIN_PASSWORD=admin123
```

## Test structure

- `com.orangehrm.api.support`: config + HTTP client helpers
- `com.orangehrm.api.tests`: JUnit tests (sample suites)
- `com.orangehrm.api.tests.*IT`: integration-style tests if you want to separate
"""
