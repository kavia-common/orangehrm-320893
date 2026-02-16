'use strict';

const {Builder} = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const firefox = require('selenium-webdriver/firefox');

// PUBLIC_INTERFACE
async function buildDriver({browser, headless}) {
  /**
   * Create a Selenium WebDriver instance with headless option.
   * @param {{browser: string, headless: boolean}} options
   * @returns {Promise<import('selenium-webdriver').WebDriver>}
   */
  const b = (browser || 'chrome').toLowerCase();

  if (b === 'chrome') {
    const opts = new chrome.Options();
    if (headless) {
      // Use new headless mode where available
      opts.addArguments('--headless=new');
    }
    opts.addArguments(
      '--no-sandbox',
      '--disable-dev-shm-usage',
      '--disable-gpu',
      '--window-size=1440,900',
    );
    return await new Builder().forBrowser('chrome').setChromeOptions(opts).build();
  }

  if (b === 'firefox') {
    const opts = new firefox.Options();
    if (headless) {
      opts.addArguments('-headless');
    }
    return await new Builder().forBrowser('firefox').setFirefoxOptions(opts).build();
  }

  throw new Error(`Unsupported BROWSER="${browser}". Use "chrome" or "firefox".`);
}

module.exports = {buildDriver};
