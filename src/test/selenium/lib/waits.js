'use strict';

const {until} = require('selenium-webdriver');

const DEFAULT_TIMEOUT_MS = 15000;

// PUBLIC_INTERFACE
async function waitVisible(driver, locator, timeoutMs = DEFAULT_TIMEOUT_MS) {
  /**
   * Wait until element is located and visible.
   */
  const el = await driver.wait(until.elementLocated(locator), timeoutMs);
  await driver.wait(until.elementIsVisible(el), timeoutMs);
  return el;
}

// PUBLIC_INTERFACE
async function waitClickable(driver, locator, timeoutMs = DEFAULT_TIMEOUT_MS) {
  /**
   * Wait until element is located, visible and enabled.
   */
  const el = await waitVisible(driver, locator, timeoutMs);
  await driver.wait(until.elementIsEnabled(el), timeoutMs);
  return el;
}

// PUBLIC_INTERFACE
async function waitUrlContains(driver, fragment, timeoutMs = DEFAULT_TIMEOUT_MS) {
  /**
   * Wait until current URL contains a fragment.
   */
  await driver.wait(async () => (await driver.getCurrentUrl()).includes(fragment), timeoutMs);
}

module.exports = {waitVisible, waitClickable, waitUrlContains, DEFAULT_TIMEOUT_MS};
