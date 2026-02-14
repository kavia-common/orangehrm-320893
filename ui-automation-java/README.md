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

### Default (Chrome)
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
- The suite includes 150+ regression scenarios as scenario outlines + example matrices (tagged by module).
