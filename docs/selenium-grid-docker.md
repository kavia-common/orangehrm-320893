# Selenium Grid (Docker) + Parallel Execution (Chrome/Firefox/Edge)

This repository includes multiple Selenium-based automation suites (Node.js, Java/JUnit, C#/NUnit). This document explains how to run them against a **Docker-based Selenium Grid 4** with **parallel execution** to target completing full regression in ~30 minutes (depending on host CPU/RAM).

## 1) Start Selenium Grid with Docker

Grid compose file:
- `docker/selenium-grid/docker-compose.yml`

Start (defaults):
```bash
docker compose -f docker/selenium-grid/docker-compose.yml up -d
```

Grid UI:
- http://localhost:4444/ui

Remote WebDriver endpoint:
- `http://localhost:4444/wd/hub`

### Scale browser nodes (recommended for ~30 min target)

You can scale the number of browser **containers** and the number of **sessions per container**.

Key variables:
- `CHROME_NODES`, `FIREFOX_NODES`, `EDGE_NODES`: number of containers per browser
- `SE_NODE_MAX_SESSIONS`: sessions per container (set to 1 for maximum stability; increase if host is strong)
- `SE_NODE_OVERRIDE_MAX_SESSIONS`: typically `true` when setting max sessions explicitly

Example (stable parallelism):
```bash
export SE_NODE_MAX_SESSIONS=1
export SE_NODE_OVERRIDE_MAX_SESSIONS=true
export CHROME_NODES=4
export FIREFOX_NODES=3
export EDGE_NODES=2

docker compose -f docker/selenium-grid/docker-compose.yml up -d \
  --scale chrome=${CHROME_NODES} \
  --scale firefox=${FIREFOX_NODES} \
  --scale edge=${EDGE_NODES}
```

Example (more aggressive, faster but potentially less stable):
```bash
export SE_NODE_MAX_SESSIONS=2
export SE_NODE_OVERRIDE_MAX_SESSIONS=true
export CHROME_NODES=4

docker compose -f docker/selenium-grid/docker-compose.yml up -d --scale chrome=${CHROME_NODES}
```

Stop:
```bash
docker compose -f docker/selenium-grid/docker-compose.yml down
```

## 2) Common environment variables (used by all suites)

You must provide application URL + credentials to run real UI tests:

- `ORANGEHRM_BASE_URL`
- `ORANGEHRM_ADMIN_USERNAME`
- `ORANGEHRM_ADMIN_PASSWORD`

Selenium Grid endpoint:
- `SELENIUM_REMOTE_URL` (e.g. `http://localhost:4444/wd/hub`)

Browser:
- `BROWSER` (one of: `chrome`, `firefox`, `edge`)

## 3) Node.js Selenium suite (src/test/selenium)

Folder:
- `src/test/selenium`

### Parallel execution
This suite supports parallel execution by running scenarios concurrently (each scenario uses its own RemoteWebDriver session).

Env vars:
- `SELENIUM_REMOTE_URL` (required to use the grid)
- `PARALLEL_WORKERS` (default: 4)

Example (Chrome, 8 workers):
```bash
cd src/test/selenium
export ORANGEHRM_BASE_URL="http://your-app"
export ORANGEHRM_ADMIN_USERNAME="admin"
export ORANGEHRM_ADMIN_PASSWORD="adminPass"
export SELENIUM_REMOTE_URL="http://localhost:4444/wd/hub"
export BROWSER="chrome"
export PARALLEL_WORKERS=8

npm test
```

## 4) Java + JUnit Selenium suite (src/test/selenium-java-junit)

Folder:
- `src/test/selenium-java-junit`

### Important: discovery-only default
This module is **discovery-only by default** and will not start real browsers unless explicitly enabled.

To run real UI on Grid:
- `-Dorangehrm.discoveryOnly=false`

Grid settings:
- `SELENIUM_REMOTE_URL`
- `BROWSER` (`chrome|firefox|edge`)

Parallelism (JUnit 5):
- `JUNIT_PARALLEL_ENABLED` (default: true when real mode)
- `JUNIT_PARALLELISM` (default: 4)

Example:
```bash
cd src/test/selenium-java-junit
export ORANGEHRM_BASE_URL="http://your-app"
export ORANGEHRM_ADMIN_USERNAME="admin"
export ORANGEHRM_ADMIN_PASSWORD="adminPass"
export SELENIUM_REMOTE_URL="http://localhost:4444/wd/hub"
export BROWSER="firefox"
export JUNIT_PARALLELISM=6

mvn -q test -Dorangehrm.discoveryOnly=false
```

## 5) C# + NUnit Selenium suite (src/test/selenium-csharp-nunit)

Folder:
- `src/test/selenium-csharp-nunit/OrangeHrm.SeleniumNUnit`

### Important: discovery-only default
This module is **discovery-only by default**.

To run real UI on Grid:
- `ORANGEHRM_DISCOVERY_ONLY=false`

Grid settings:
- `SELENIUM_REMOTE_URL`
- `BROWSER` (`chrome|firefox|edge`)

Parallelism (NUnit):
- `NUNIT_NUMBER_OF_TEST_WORKERS` (default: 4)
- Optionally, per-fixture/test parallelization is controlled in code via attributes (already enabled for UI tests).

Example:
```bash
cd src/test/selenium-csharp-nunit/OrangeHrm.SeleniumNUnit
export ORANGEHRM_BASE_URL="http://your-app"
export ORANGEHRM_ADMIN_USERNAME="admin"
export ORANGEHRM_ADMIN_PASSWORD="adminPass"
export ORANGEHRM_DISCOVERY_ONLY=false
export SELENIUM_REMOTE_URL="http://localhost:4444/wd/hub"
export BROWSER="edge"
export NUNIT_NUMBER_OF_TEST_WORKERS=6

dotnet test
```

## 6) Notes on hitting ~30 minutes

To achieve ~30 minutes for full regression, you typically need:
- enough Grid capacity (nodes * max sessions) to match your suite concurrency
- test suites configured to run parallel workers (`PARALLEL_WORKERS`, `JUNIT_PARALLELISM`, `NUNIT_NUMBER_OF_TEST_WORKERS`)
- sufficient host resources (CPU/RAM; browsers are heavy)

Start with:
- `SE_NODE_MAX_SESSIONS=1`
- Chrome nodes scaled up (most stable)
- workers ~= total Grid sessions available for the chosen browser

Example targeting 8 concurrent sessions:
- `CHROME_NODES=8`, `SE_NODE_MAX_SESSIONS=1`, `PARALLEL_WORKERS=8`

If stable, increase `SE_NODE_MAX_SESSIONS` to 2 and reduce node count.

## 7) Troubleshooting

- If sessions fail to start:
  - Check Grid UI: http://localhost:4444/ui
  - Ensure `SELENIUM_REMOTE_URL` is reachable from your test process
- If tests are slow/flaky:
  - reduce workers
  - keep `SE_NODE_MAX_SESSIONS=1`
  - increase `shm_size` (already set to 2gb)
