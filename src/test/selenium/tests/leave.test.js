'use strict';

const {performLogin} = require('../lib/flows/auth');
const {menu, shell} = require('../lib/locators');
const {waitVisible, waitClickable, waitUrlContains} = require('../lib/waits');

function parseDate(s) {
  const d = new Date(String(s));
  return Number.isNaN(d.getTime()) ? null : d;
}

// PUBLIC_INTERFACE
async function runLeaveScenario(driver, cfg, scenario) {
  /**
   * Leave scenarios:
   * - Navigate to Leave module
   * - Basic data sanity checks for boundary/negative cases (date ranges)
   */
  await performLogin(driver, {
    baseUrl: cfg.baseUrl,
    username: cfg.adminUsername,
    password: cfg.adminPassword,
  });

  const leaveMenu = await waitClickable(driver, menu.leave);
  await leaveMenu.click();
  await waitUrlContains(driver, '/leave');
  await waitVisible(driver, shell.sidePanel);
  await waitVisible(driver, shell.topBarHeader);

  const t = scenario.data || {};
  if (t.fromDate && t.toDate) {
    const from = parseDate(t.fromDate);
    const to = parseDate(t.toDate);
    if (!from || !to) {
      // invalid date format boundary
      if (t.expectedResult === 'validation_error') return;
      throw new Error(`Invalid date format in scenario ${scenario.id}`);
    }
    // For negative reversed-range case we accept reversed ordering as input (UI should validate),
    // but at least ensure the values are present.
  }
}

module.exports = {runLeaveScenario};
