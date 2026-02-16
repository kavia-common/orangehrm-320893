'use strict';

// PUBLIC_INTERFACE
function loadConfig() {
  /**
   * Load Selenium test configuration from environment variables.
   * @returns {{baseUrl: string, adminUsername: string, adminPassword: string, browser: string, headless: boolean}}
   */
  const baseUrl = process.env.ORANGEHRM_BASE_URL;
  const adminUsername = process.env.ORANGEHRM_ADMIN_USERNAME;
  const adminPassword = process.env.ORANGEHRM_ADMIN_PASSWORD;

  if (!baseUrl) {
    throw new Error('Missing env ORANGEHRM_BASE_URL');
  }
  if (!adminUsername) {
    throw new Error('Missing env ORANGEHRM_ADMIN_USERNAME');
  }
  if (!adminPassword) {
    throw new Error('Missing env ORANGEHRM_ADMIN_PASSWORD');
  }

  const browser = (process.env.BROWSER || 'chrome').toLowerCase();
  const headless =
    String(process.env.HEADLESS || '').toLowerCase() === 'true' ||
    String(process.env.HEADLESS || '').toLowerCase() === '1';

  return {baseUrl, adminUsername, adminPassword, browser, headless};
}

module.exports = {loadConfig};
