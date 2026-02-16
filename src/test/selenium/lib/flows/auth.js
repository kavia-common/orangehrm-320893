'use strict';

const {Key} = require('selenium-webdriver');
const {login, shell} = require('../locators');
const {waitVisible, waitClickable, waitUrlContains} = require('../waits');

// PUBLIC_INTERFACE
async function openLogin(driver, baseUrl) {
  /**
   * Navigate to login page.
   */
  await driver.get(baseUrl);
  await waitVisible(driver, login.username);
}

// PUBLIC_INTERFACE
async function performLogin(driver, {baseUrl, username, password}) {
  /**
   * Perform login and wait for dashboard shell.
   */
  await openLogin(driver, baseUrl);

  const u = await waitVisible(driver, login.username);
  await u.clear();
  await u.sendKeys(username);

  const p = await waitVisible(driver, login.password);
  await p.clear();
  await p.sendKeys(password, Key.ENTER);

  // Successful login usually lands on /dashboard
  await waitUrlContains(driver, '/dashboard');
  await waitVisible(driver, shell.sidePanel);
}

// PUBLIC_INTERFACE
async function performLogout(driver) {
  /**
   * Logout from any page using user dropdown.
   */
  const dropdown = await waitClickable(driver, shell.userDropdown);
  await dropdown.click();

  const logout = await waitClickable(driver, shell.logoutLink);
  await logout.click();

  // Back to auth/login
  await waitUrlContains(driver, '/auth/login');
  await waitVisible(driver, login.username);
}

module.exports = {openLogin, performLogin, performLogout};
