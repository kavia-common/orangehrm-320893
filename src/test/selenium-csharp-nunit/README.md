## OrangeHRM Selenium (C#/.NET + NUnit) - Skeleton

This directory provides a **C#/.NET + NUnit** Selenium UI automation framework skeleton aligned with the existing suites:

- `src/test/selenium` (Node.js + selenium-webdriver)
- `src/test/selenium-java-junit` (Java + JUnit 5)

### Goals

- Provide a clean C# structure (pages, config, driver management, sample tests).
- Support **discovery-only validation by default** (CI-safe): tests are discoverable and runnable without launching a real browser.

---

## Environment Variables (aligned with Node/Java suites)

When real browser mode is enabled, the suite reads:

- `ORANGEHRM_BASE_URL` (example: `http://php80/orangehrm/web/index.php`)
- `ORANGEHRM_ADMIN_USERNAME` (example: `Admin`)
- `ORANGEHRM_ADMIN_PASSWORD` (example: `admin123`)
- `BROWSER` (optional: `chrome` or `firefox`, default: `chrome`)
- `HEADLESS` (optional: `true`)

---

## Discovery-only mode (default)

By default, the suite **does not create a Selenium WebDriver** and will skip any browser test logic.

Run:

```bash
cd src/test/selenium-csharp-nunit
dotnet test
```

This should:
- Restore packages
- Discover tests
- Execute them in "discovery-only" mode
- Not attempt to launch Chrome/Firefox or require drivers

---

## Enabling real browser mode (intentional)

If you want to actually run browser automation:

1. Provide drivers on PATH (e.g. `chromedriver` / `geckodriver`) and a running OrangeHRM instance.
2. Set required env vars (`ORANGEHRM_BASE_URL`, `ORANGEHRM_ADMIN_USERNAME`, `ORANGEHRM_ADMIN_PASSWORD`).
3. Run:

```bash
dotnet test -p:OrangeHrmDiscoveryOnly=false
```

Notes:
- Default is `OrangeHrmDiscoveryOnly=true`
- In real mode, the sample test will attempt a minimal navigation to the login page.

---

## Structure

- `OrangeHrm.SeleniumNUnit/`
  - `Config/` : env/config loading
  - `Driver/` : driver factory + discovery-only guard
  - `Pages/` : page objects (minimal example)
  - `Tests/` : example NUnit tests
  - `Utilities/` : small helpers

---
"""
