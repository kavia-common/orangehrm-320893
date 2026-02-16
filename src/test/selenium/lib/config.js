'use strict';

// PUBLIC_INTERFACE
function loadConfig() {
  /**
   * Load Selenium test configuration from environment variables.
   * @returns {{
   *   baseUrl: string,
   *   adminUsername: string,
   *   adminPassword: string,
   *   browser: string,
   *   headless: boolean,
   *   remoteUrl: (string|undefined),
   *   parallelWorkers: number
   * }}
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

  const remoteUrl = process.env.SELENIUM_REMOTE_URL || undefined;

  const parallelWorkersRaw = String(process.env.PARALLEL_WORKERS || '').trim();
  const parallelWorkers = parallelWorkersRaw ? Math.max(1, parseInt(parallelWorkersRaw, 10) || 1) : 4;

  return {baseUrl, adminUsername, adminPassword, browser, headless, remoteUrl, parallelWorkers};
}

module.exports = {loadConfig};
