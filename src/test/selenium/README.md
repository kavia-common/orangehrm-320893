## OrangeHRM Selenium WebDriver UI Regression Suite

This folder contains **Node.js + Selenium WebDriver** data-driven UI automation tests targeting **150+ regression scenarios** across:
- Login
- Dashboard
- Admin
- PIM
- Leave
- Recruitment
- Time

### Why data-driven?
The base scenarios are defined in `fixtures/*.json`. The runner **expands** those base cases into positive/negative/boundary variants to exceed 150 scenarios while keeping authoring small and maintainable.

---

## Prerequisites

1. Node.js installed.
2. A running OrangeHRM instance accessible via URL.
3. A browser driver available on PATH:
   - **Chrome**: `chromedriver`
   - **Firefox**: `geckodriver`

> Note: This project does not auto-download drivers to avoid CI surprises. Provide drivers via your CI image or PATH.

---

## Environment Variables

Create/export the following environment variables (do not commit secrets):

- `ORANGEHRM_BASE_URL` (example: `http://php80/orangehrm/web/index.php`)
- `ORANGEHRM_ADMIN_USERNAME` (example: `Admin`)
- `ORANGEHRM_ADMIN_PASSWORD` (example: `admin123`)
- `BROWSER` (optional: `chrome` or `firefox`, default: `chrome`)
- `HEADLESS` (optional: `true` to run headless)

---

## Install & Run

From `src/test/selenium`:

```bash
npm install
npm run test:headless
```

Or choose browser:

```bash
BROWSER=firefox HEADLESS=true npm test
```

---

## Structure

- `fixtures/` : base test data (and generated expanded suite)
- `lib/` : webdriver helpers, selectors, assertions, navigation
- `tests/` : module test definitions (Login, PIM, Leave, Recruitment, Time, Admin, Dashboard)
- `runner/runAll.js` : executes all scenarios and prints summary

---

## Notes / Stability

- Uses explicit waits and resilient selectors.
- Avoids brittle sleeps except as last resort (none by default).
- On failure, logs the scenario ID and step details for quick triage.
