# OrangeHRM API Automation (C# + RestSharp)

Runnable API automation tests written with **RestSharp** + **NUnit**.

## Configuration

Create a `.env` (not committed) based on `OrangeHrm.ApiTests/.env.example` or set environment variables:

- `BASE_URL`
- `API_BASE_PATH` (default `/api/v2`)
- `ADMIN_USERNAME`, `ADMIN_PASSWORD`

## Run

From `api-automation-csharp/`:

```bash
dotnet test
```

Override with environment variables:

```bash
BASE_URL=http://localhost:8080 ADMIN_USERNAME=Admin ADMIN_PASSWORD=admin123 dotnet test
```

## Notes on authentication

These sample tests use cookie-based login through the UI login endpoint (`/web/index.php/auth/validate`) because it is commonly available.

If your environment uses OAuth2 bearer tokens for API auth, set `API_BEARER_TOKEN` and the client will prefer it.
