'use strict';

const {Builder} = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const firefox = require('selenium-webdriver/firefox');
const edge = require('selenium-webdriver/edge');

/**
 * Build browser-specific options for local or remote sessions.
 * Selenium Grid will ignore some options depending on browser/container.
 */
function buildOptions(browser, headless) {
  const b = (browser || 'chrome').toLowerCase();

  if (b === 'chrome') {
    const opts = new chrome.Options();
    if (headless) {
      opts.addArguments('--headless=new');
    }
    opts.addArguments('--no-sandbox', '--disable-dev-shm-usage', '--disable-gpu', '--window-size=1440,900');
    return opts;
  }

  if (b === 'firefox') {
    const opts = new firefox.Options();
    if (headless) {
      opts.addArguments('-headless');
    }
    return opts;
  }

  if (b === 'edge') {
    const opts = new edge.Options();
    if (headless) {
      // Edge in Grid typically supports Chromium headless flags.
      opts.addArguments('--headless=new');
    }
    opts.addArguments('--no-sandbox', '--disable-dev-shm-usage', '--disable-gpu', '--window-size=1440,900');
    return opts;
  }

  throw new Error(`Unsupported BROWSER="${browser}". Use "chrome", "firefox", or "edge".`);
}

// PUBLIC_INTERFACE
async function buildDriver({browser, headless, remoteUrl}) {
  /**
   * Create a Selenium WebDriver instance.
   *
   * Behavior:
   * - If remoteUrl is provided (or env SELENIUM_REMOTE_URL is set), creates a RemoteWebDriver session on Selenium Grid.
   * - Otherwise creates a local browser driver (requires local driver binaries).
   *
   * @param {{browser: string, headless: boolean, remoteUrl?: string}} options
   * @returns {Promise<import('selenium-webdriver').WebDriver>}
   */
  const b = (browser || 'chrome').toLowerCase();
  const gridUrl = remoteUrl || process.env.SELENIUM_REMOTE_URL;

  if (gridUrl) {
    // Remote session on Selenium Grid
    const builder = new Builder().forBrowser(b).usingServer(gridUrl);

    if (b === 'chrome') {
      return await builder.setChromeOptions(buildOptions(b, headless)).build();
    }
    if (b === 'firefox') {
      return await builder.setFirefoxOptions(buildOptions(b, headless)).build();
    }
    if (b === 'edge') {
      return await builder.setEdgeOptions(buildOptions(b, headless)).build();
    }

    // Should not reach due to validation in buildOptions, but keep safe:
    throw new Error(`Unsupported BROWSER="${browser}". Use "chrome", "firefox", or "edge".`);
  }

  // Local drivers (legacy behavior)
  if (b === 'chrome') {
    return await new Builder().forBrowser('chrome').setChromeOptions(buildOptions(b, headless)).build();
  }

  if (b === 'firefox') {
    return await new Builder().forBrowser('firefox').setFirefoxOptions(buildOptions(b, headless)).build();
  }

  if (b === 'edge') {
    return await new Builder().forBrowser('MicrosoftEdge').setEdgeOptions(buildOptions(b, headless)).build();
  }

  throw new Error(`Unsupported BROWSER="${browser}". Use "chrome", "firefox", or "edge".`);
}

module.exports = {buildDriver};
