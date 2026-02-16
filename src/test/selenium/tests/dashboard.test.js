'use strict';

const {By} = require('selenium-webdriver');
const {performLogin} = require('../lib/flows/auth');
const {menu, shell} = require('../lib/locators');
const {waitVisible, waitClickable, waitUrlContains} = require('../lib/waits');

// PUBLIC_INTERFACE
async function runDashboardScenario(driver, cfg, scenario) {
  /**
   * Dashboard scenarios:
   * - Ensure Dashboard page loads after login and common widgets behave
   * - Widget presence check based on widgetName
   * - Generic navigation/visibility checks for expanded scenarios
   */
  // Ensure we're logged in
  await performLogin(driver, {
    baseUrl: cfg.baseUrl,
    username: cfg.adminUsername,
    password: cfg.adminPassword,
  });

  // Navigate to Dashboard explicitly
  const dashboardMenu = await waitClickable(driver, menu.dashboard);
  await dashboardMenu.click();
  await waitUrlContains(driver, '/dashboard');
  await waitVisible(driver, shell.sidePanel);

  const t = scenario.data || {};
  const widgetName = String(t.widgetName ?? '');

  if (widgetName) {
    const widgetHeader = By.xpath(`//*[contains(@class,'oxd-widget')]//*[normalize-space()=${JSON.stringify(widgetName)}]`);
    const found = await driver.findElements(widgetHeader);
    const visible = found.length > 0;

    if (t.expectedVisible === true && !visible) {
      throw new Error(`Expected widget "${widgetName}" to be visible, but it was not found.`);
    }
    if (t.expectedVisible === false && visible) {
      throw new Error(`Expected widget "${widgetName}" to be NOT visible, but it was found.`);
    }
  } else {
    // For boundary case (empty widget name), just ensure dashboard shell exists.
    await waitVisible(driver, shell.topBarHeader);
  }
}

module.exports = {runDashboardScenario};
