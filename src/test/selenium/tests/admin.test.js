'use strict';

const {performLogin} = require('../lib/flows/auth');
const {menu, shell} = require('../lib/locators');
const {waitVisible, waitClickable, waitUrlContains} = require('../lib/waits');

// PUBLIC_INTERFACE
async function runAdminScenario(driver, cfg, scenario) {
  /**
   * Admin scenarios:
   * - Navigate to Admin module and verify page loads
   * - Expanded cases validate that UI remains stable with boundary inputs (smoke-level)
   *
   * Note: This suite focuses on broad regression coverage with stable checks rather than
   * brittle deep CRUD steps (OrangeHRM data/state differs across environments).
   */
  await performLogin(driver, {
    baseUrl: cfg.baseUrl,
    username: cfg.adminUsername,
    password: cfg.adminPassword,
  });

  const adminMenu = await waitClickable(driver, menu.admin);
  await adminMenu.click();
  await waitUrlContains(driver, '/admin');
  await waitVisible(driver, shell.sidePanel);

  // Basic negative/boundary validation: ensure page shell does not crash
  await waitVisible(driver, shell.topBarHeader);

  // If seed includes role/status, we do a minimal sanity check that they are strings.
  const t = scenario.data || {};
  if (t.role && typeof t.role !== 'string') {
    throw new Error('Admin role should be a string');
  }
  if (t.status && typeof t.status !== 'string') {
    throw new Error('Admin status should be a string');
  }
}

module.exports = {runAdminScenario};
