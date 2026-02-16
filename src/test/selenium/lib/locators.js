'use strict';

const {By} = require('selenium-webdriver');

/**
 * Locators use OrangeHRM common attributes/classes.
 * Adjust here if UI changes.
 */
const login = {
  username: By.name('username'),
  password: By.name('password'),
  submit: By.css('button[type="submit"]'),
  invalidCredentials: By.xpath("//*[contains(@class,'oxd-alert-content-text') or contains(.,'Invalid credentials')]"),
  requiredField: By.xpath("//*[contains(@class,'oxd-input-field-error-message') and contains(.,'Required')]"),
};

const shell = {
  sidePanel: By.css('aside.oxd-sidepanel'),
  topBarHeader: By.css('header.oxd-topbar'),
  userDropdown: By.css('.oxd-userdropdown'),
  logoutLink: By.xpath("//a[contains(@href,'logout') or contains(.,'Logout')]"),
  dashboardHeader: By.xpath("//*[contains(@class,'oxd-topbar-header-breadcrumb') and contains(.,'Dashboard')]"),
};

const menu = {
  dashboard: By.xpath("//span[normalize-space()='Dashboard']"),
  admin: By.xpath("//span[normalize-space()='Admin']"),
  pim: By.xpath("//span[normalize-space()='PIM']"),
  leave: By.xpath("//span[normalize-space()='Leave']"),
  time: By.xpath("//span[normalize-space()='Time']"),
  recruitment: By.xpath("//span[normalize-space()='Recruitment']"),
};

module.exports = {login, shell, menu};
