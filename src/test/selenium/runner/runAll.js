'use strict';

/**
 * Entry point for Selenium regression suite.
 *
 * Loads base fixtures, expands them into 150+ scenarios, then executes them sequentially
 * (to reduce flakiness and shared-state issues).
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

  // One browser session across suite to reduce startup cost; each scenario should navigate as needed.
  const driver = await buildDriver({browser: cfg.browser, headless: cfg.headless});

  try {
    // Make UI interactions more stable by keeping a consistent viewport.
    await driver.manage().setTimeouts({implicit: 0, pageLoad: 60000, script: 30000});

    for (const s of scenarios) {
      const handler = DISPATCH[s.module];
      if (!handler) {
        results.failed++;
        results.failures.push({id: s.id, module: s.module, title: s.title, error: 'No handler'});
        continue;
      }

      const started = Date.now();
      try {
        await handler(driver, cfg, s);
        results.passed++;
        // eslint-disable-next-line no-console
        console.log(`[PASS] ${s.id} [${s.module}] ${s.title} (${Date.now() - started}ms)`);
      } catch (err) {
        results.failed++;
        results.failures.push({
          id: s.id,
          module: s.module,
          title: s.title,
          error: err && err.stack ? err.stack : String(err),
        });
        // eslint-disable-next-line no-console
        console.error(`[FAIL] ${s.id} [${s.module}] ${s.title}\n${err && err.stack ? err.stack : err}`);
      }
    }
  } finally {
    await driver.quit();
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
