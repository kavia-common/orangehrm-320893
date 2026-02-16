# Allure reporting (Java JUnit + C# NUnit Selenium skeletons)

This repo includes **demo-mode / discovery-only** Selenium skeletons for:
- Java + JUnit 5: `src/test/selenium-java-junit`
- C#/.NET + NUnit: `src/test/selenium-csharp-nunit`

The goal is to generate **Allure raw results** (`allure-results/`) and then build an **HTML report** (`allure-report/`) **without launching a real browser**.

## Prerequisites: Allure CLI

Allure CLI is installed via npm **user prefix** (no sudo):

```bash
npm config set prefix /home/kavia/.local
npm i -g allure-commandline@2.27.0
export PATH=/home/kavia/.local/bin:$PATH
allure --version
```

## C# (NUnit) demo run (no browser)

From repo root:

```bash
cd src/test/selenium-csharp-nunit
export PATH=/home/kavia/.local/bin:$PATH
ORANGEHRM_DISCOVERY_ONLY=true dotnet test -c Release --logger "trx;LogFileName=TestResults.trx" \
  -- TestRunParameters.Parameter\(name="allureResultsDir",value="../allure-results"\)
```

Results folder:
- `src/test/selenium-csharp-nunit/allure-results`

Generate HTML report:

```bash
cd src/test/selenium-csharp-nunit
export PATH=/home/kavia/.local/bin:$PATH
allure generate ./allure-results -o ./allure-report --clean
```

Open:
- `src/test/selenium-csharp-nunit/allure-report/index.html`

## Java (JUnit 5) demo run (no browser)

> Note: the environment where this task ran did **not** have Maven (`mvn`) installed, so Java execution may need a Maven-enabled runner.

```bash
cd src/test/selenium-java-junit
mvn -q test -Dorangehrm.discoveryOnly=true
```

Results folder:
- `src/test/selenium-java-junit/allure-results`

Generate HTML report:

```bash
cd src/test/selenium-java-junit
export PATH=/home/kavia/.local/bin:$PATH
allure generate ./allure-results -o ./allure-report --clean
```

Open:
- `src/test/selenium-java-junit/allure-report/index.html`
