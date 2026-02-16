'use strict';

const {Key} = require('selenium-webdriver');
const {openLogin} = require('../lib/flows/auth');
const {login, shell} = require('../lib/locators');
const {waitVisible, waitUrlContains} = require('../lib/waits');

// PUBLIC_INTERFACE
async function runLoginScenario(driver, cfg, scenario) {
  /**
   * Execute a login scenario.
   *
   * Validations:
   * - success: dashboard URL and shell visible
   * - error: invalid credentials alert visible
   * - validation_error: required field validations visible
   */
  const t = scenario.data || {};
  await openLogin(driver, cfg.baseUrl);

  const u = await waitVisible(driver, login.username);
  await u.clear();
  await u.sendKeys(String(t.username ?? ''));

  const p = await waitVisible(driver, login.password);
  await p.clear();
  await p.sendKeys(String(t.password ?? ''), Key.ENTER);

  if (t.expectedResult === 'success') {
    await waitUrlContains(driver, '/dashboard');
    await waitVisible(driver, shell.sidePanel);
    return;
  }

  if (t.expectedResult === 'validation_error') {
    // Either required message or input error message
    await waitVisible(driver, login.requiredField);
    await waitUrlContains(driver, '/auth/login');
    return;
  }

  // Default to invalid credentials style error
  await waitUrlContains(driver, '/auth/login');
  await waitVisible(driver, login.invalidCredentials);
}

module.exports = {runLoginScenario};
