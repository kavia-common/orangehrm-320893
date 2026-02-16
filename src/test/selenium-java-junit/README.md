## OrangeHRM Selenium (Java + JUnit 5) - Skeleton

This directory provides a **Java + JUnit 5** Selenium UI automation framework skeleton aligned with the existing Node.js suite at:

- `src/test/selenium` (Node.js + selenium-webdriver)

### Goals

- Provide a clean Java structure (pages, config, driver management, sample tests).
- Support **discovery-only validation by default** (CI-safe): tests are discoverable and runnable without launching a real browser.

---

## Environment Variables (aligned with Node suite)

The existing Node suite uses:

- `ORANGEHRM_BASE_URL` (example: `http://php80/orangehrm/web/index.php`)
- `ORANGEHRM_ADMIN_USERNAME` (example: `Admin`)
- `ORANGEHRM_ADMIN_PASSWORD` (example: `admin123`)
- `BROWSER` (optional: `chrome` or `firefox`, default: `chrome`)
- `HEADLESS` (optional: `true`)

This Java skeleton reads the same variables (only when real browser mode is enabled).

---

## Discovery-only mode (default)

By default, the suite **does not create a Selenium WebDriver** and will skip any browser test logic.

Run:

```bash
cd src/test/selenium-java-junit
mvn -q test
```

This should:
- Discover tests
- Execute them in "discovery-only" mode
- Not attempt to launch Chrome/Firefox or require drivers

---

## Cucumber BDD dry-run (default)

This suite also includes a minimal **Cucumber (JUnit Platform)** setup.

Feature files live under:

- `src/test/resources/features/*.feature`

Step definitions and runner live under:

- `src/test/java/org/orangehrm/bdd/steps/*`
- `src/test/java/org/orangehrm/bdd/CucumberTest.java`

By default, Maven is configured to run Cucumber in **dry-run mode**, which means it will:
- Parse all `.feature` files
- Validate that each step has a matching step definition ("step binding")
- **Not execute step bodies** (so it will not launch browsers)

Run:

```bash
cd src/test/selenium-java-junit
mvn -q test
```

To intentionally disable dry-run (not recommended in CI):

```bash
mvn -q test -Dcucumber.dryRun=false
```

---

## Enabling real browser mode (intentional)

If you want to actually run browser automation:

1. Provide drivers on PATH (e.g. `chromedriver` / `geckodriver`) and a running OrangeHRM instance.
2. Set required env vars (`ORANGEHRM_BASE_URL`, `ORANGEHRM_ADMIN_USERNAME`, `ORANGEHRM_ADMIN_PASSWORD`).
3. Run:

```bash
mvn -q test -Dorangehrm.discoveryOnly=false
```

---

## Structure

- `src/test/java/org/orangehrm/selenium/config` : env/config loading
- `src/test/java/org/orangehrm/selenium/driver` : driver factory + discovery-only driver
- `src/test/java/org/orangehrm/selenium/pages` : page objects (minimal example)
- `src/test/java/org/orangehrm/selenium/tests` : example JUnit tests
- `src/test/java/org/orangehrm/selenium/util` : small utilities

---
""
