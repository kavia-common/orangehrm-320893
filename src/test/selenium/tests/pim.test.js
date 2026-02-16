'use strict';

const {performLogin} = require('../lib/flows/auth');
const {menu, shell} = require('../lib/locators');
const {waitVisible, waitClickable, waitUrlContains} = require('../lib/waits');

// PUBLIC_INTERFACE
async function runPimScenario(driver, cfg, scenario) {
  /**
   * PIM scenarios:
   * - Navigate to PIM and assert page loads
   * - Boundary validations focus on not breaking UI and required fields indicated where applicable
   */
  await performLogin(driver, {
    baseUrl: cfg.baseUrl,
    username: cfg.adminUsername,
    password: cfg.adminPassword,
  });

  const pimMenu = await waitClickable(driver, menu.pim);
  await pimMenu.click();
  await waitUrlContains(driver, '/pim');
  await waitVisible(driver, shell.sidePanel);

  const t = scenario.data || {};
  if (t.action && !['add', 'search'].includes(String(t.action))) {
    // Negative variants may pass different expectations; treat unknown as invalid data setup.
    throw new Error(`Unsupported PIM action "${t.action}"`);
  }

  // Smoke-level: ensure top bar exists.
  await waitVisible(driver, shell.topBarHeader);
}

module.exports = {runPimScenario};
