'use strict';

/**
 * Entry point for Selenium regression suite.
 *
 * Loads base fixtures, expands them into scenarios, then executes them in parallel
 * using a configurable worker pool. Each worker uses its own WebDriver session.
 *
 * This is designed for Selenium Grid execution (remote) to reduce overall wall time.
 *
 * Exit code:
 * - 0 if all scenarios pass
 * - 1 if any scenario fails
 */

require('dotenv').config();

const fs = require('fs');
const path = require('path');

const {loadConfig} = require('../lib/config');
const {buildDriver} = require('../lib/driverFactory');
const {generateExpandedScenarios, saveExpandedScenarios} = require('./scenarioGenerator');

const {runLoginScenario} = require('../tests/login.test');
const {runDashboardScenario} = require('../tests/dashboard.test');
const {runAdminScenario} = require('../tests/admin.test');
const {runPimScenario} = require('../tests/pim.test');
const {runLeaveScenario} = require('../tests/leave.test');
const {runRecruitmentScenario} = require('../tests/recruitment.test');
const {runTimeScenario} = require('../tests/time.test');

function loadJson(relPath) {
  const abs = path.join(__dirname, '..', relPath);
  return JSON.parse(fs.readFileSync(abs, 'utf8'));
}

const DISPATCH = {
  Login: runLoginScenario,
  Dashboard: runDashboardScenario,
  Admin: runAdminScenario,
  PIM: runPimScenario,
  Leave: runLeaveScenario,
  Recruitment: runRecruitmentScenario,
  Time: runTimeScenario,
};

async function runScenarioInFreshSession(cfg, scenario) {
  const handler = DISPATCH[scenario.module];
  if (!handler) {
    throw new Error(`No handler for module="${scenario.module}"`);
  }

  const driver = await buildDriver({
    browser: cfg.browser,
    headless: cfg.headless,
    remoteUrl: cfg.remoteUrl,
  });

  try {
    await driver.manage().setTimeouts({implicit: 0, pageLoad: 60000, script: 30000});
    await handler(driver, cfg, scenario);
  } finally {
    await driver.quit();
  }
}

async function runWithConcurrency(items, concurrency, workerFn) {
  const results = new Array(items.length);
  let nextIndex = 0;

  async function workerLoop(workerId) {
    while (true) {
      const i = nextIndex++;
      if (i >= items.length) return;

      const item = items[i];
      const started = Date.now();
      try {
        await workerFn(item, i, workerId);
        results[i] = {status: 'passed', durationMs: Date.now() - started};
      } catch (err) {
        results[i] = {
          status: 'failed',
          durationMs: Date.now() - started,
          error: err && err.stack ? err.stack : String(err),
        };
      }
    }
  }

  const workers = [];
  const c = Math.max(1, Math.min(concurrency, items.length));
  for (let w = 0; w < c; w++) {
    workers.push(workerLoop(w));
  }
  await Promise.all(workers);

  return results;
}

(async () => {
  const cfg = loadConfig();

  const baseFixtures = {
    login: loadJson('fixtures/loginData.json'),
    pim: loadJson('fixtures/pimData.json'),
    leave: loadJson('fixtures/leaveData.json'),
    recruitment: loadJson('fixtures/recruitmentData.json'),
    time: loadJson('fixtures/timeData.json'),
    admin: loadJson('fixtures/adminData.json'),
    dashboard: loadJson('fixtures/dashboardData.json'),
  };

  const scenarios = generateExpandedScenarios(baseFixtures);
  saveExpandedScenarios(path.join(__dirname, '..', 'fixtures', '_expandedScenarios.json'), scenarios);

  const results = {
    total: scenarios.length,
    passed: 0,
    failed: 0,
    failures: [],
  };

  // eslint-disable-next-line no-console
  console.log(
    `Running ${scenarios.length} scenarios with PARALLEL_WORKERS=${cfg.parallelWorkers}` +
      (cfg.remoteUrl ? ` on GRID=${cfg.remoteUrl}` : ' using LOCAL drivers'),
  );

  const perScenario = await runWithConcurrency(scenarios, cfg.parallelWorkers, async (s) => {
    const started = Date.now();
    try {
      await runScenarioInFreshSession(cfg, s);
      // eslint-disable-next-line no-console
      console.log(`[PASS] ${s.id} [${s.module}] ${s.title} (${Date.now() - started}ms)`);
    } catch (err) {
      // eslint-disable-next-line no-console
      console.error(`[FAIL] ${s.id} [${s.module}] ${s.title}\n${err && err.stack ? err.stack : err}`);
      throw err;
    }
  });

  for (let i = 0; i < perScenario.length; i++) {
    const r = perScenario[i];
    const s = scenarios[i];
    if (r.status === 'passed') {
      results.passed++;
    } else {
      results.failed++;
      results.failures.push({id: s.id, module: s.module, title: s.title, error: r.error});
    }
  }

  // eslint-disable-next-line no-console
  console.log('\n=== Selenium Regression Summary ===');
  // eslint-disable-next-line no-console
  console.log(`Total: ${results.total} | Passed: ${results.passed} | Failed: ${results.failed}`);

  if (results.failed > 0) {
    // eslint-disable-next-line no-console
    console.log('\nFailures:');
    for (const f of results.failures.slice(0, 50)) {
      // eslint-disable-next-line no-console
      console.log(`- ${f.id} [${f.module}] ${f.title}`);
    }
    process.exit(1);
  }
  process.exit(0);
})().catch((e) => {
  // eslint-disable-next-line no-console
  console.error(e && e.stack ? e.stack : e);
  process.exit(1);
});
