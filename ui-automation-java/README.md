# OrangeHRM UI Automation (Java + Cucumber)

Selenium WebDriver UI automation for OrangeHRM demo: https://opensource-demo.orangehrmlive.com  
- Framework: Java 17 + Cucumber + JUnit Platform  
- Execution: local Chrome / Firefox / Edge (no Grid)  
- Credentials/config: placeholders via `.env` file

## Prerequisites
- Java 17+
- Maven 3.9+
- Chrome, Firefox, and/or Edge installed locally

## Configuration
Copy `.env.example` to `.env` and fill in values.

## Running tests

### Demo default (single stable demo suite)
Runs only the demo login feature(s) under `src/test/resources/features/demo/`.

```bash
mvn -q test
```

### Choose browser
```bash
mvn -q test -Dbrowser=chrome
mvn -q test -Dbrowser=firefox
mvn -q test -Dbrowser=edge
```

### Run by tags (examples)
```bash
mvn -q test -Dcucumber.filter.tags="@smoke"
mvn -q test -Dcucumber.filter.tags="@regression and not @wip"
mvn -q test -Dcucumber.filter.tags="@login or @dashboard"
```

## Notes
- This suite is designed for the public OrangeHRM demo, which can be rate-limited and occasionally flaky.
- Tests use explicit waits and avoid brittle sleeps, but UI responsiveness may vary.
- For containerized execution (CI/demo), Chrome is always started headless and with stability flags: `--headless=new --no-sandbox --disable-dev-shm-usage --disable-gpu --window-size=1920,1080`.
- If Chrome is installed in a non-standard location, set `CHROME_BINARY` in `.env` to the full path (e.g., `/usr/bin/google-chrome`) so WebDriverManager can download a matching ChromeDriver.
- The suite includes 150+ regression scenarios as scenario outlines + example matrices (tagged by module).
