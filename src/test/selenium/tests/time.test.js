'use strict';

const {performLogin} = require('../lib/flows/auth');
const {menu, shell} = require('../lib/locators');
const {waitVisible, waitClickable, waitUrlContains} = require('../lib/waits');

// PUBLIC_INTERFACE
async function runTimeScenario(driver, cfg, scenario) {
  /**
   * Time scenarios:
   * - Navigate to Time module and assert page loads
   * - Sanity check hoursWorked for expanded boundary/negative cases
   */
  await performLogin(driver, {
    baseUrl: cfg.baseUrl,
    username: cfg.adminUsername,
    password: cfg.adminPassword,
  });

  const timeMenu = await waitClickable(driver, menu.time);
  await timeMenu.click();
  await waitUrlContains(driver, '/time');
  await waitVisible(driver, shell.sidePanel);
  await waitVisible(driver, shell.topBarHeader);

  const t = scenario.data || {};
  if (typeof t.hoursWorked === 'number') {
    // Data-level validation (UI should also validate)
    if (t.hoursWorked < 0 && t.expectedResult !== 'validation_error') {
      // In our generator negative hours are expected to validation_error; accept if declared so.
      throw new Error('Negative hoursWorked should expect validation_error');
    }
  }
}

module.exports = {runTimeScenario};
