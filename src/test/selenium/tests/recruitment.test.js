'use strict';

const {performLogin} = require('../lib/flows/auth');
const {menu, shell} = require('../lib/locators');
const {waitVisible, waitClickable, waitUrlContains} = require('../lib/waits');

function looksLikeEmail(s) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(s || ''));
}

// PUBLIC_INTERFACE
async function runRecruitmentScenario(driver, cfg, scenario) {
  /**
   * Recruitment scenarios:
   * - Navigate to Recruitment module and assert page loads
   * - Basic email sanity checks (expanded negative case)
   */
  await performLogin(driver, {
    baseUrl: cfg.baseUrl,
    username: cfg.adminUsername,
    password: cfg.adminPassword,
  });

  const recMenu = await waitClickable(driver, menu.recruitment);
  await recMenu.click();
  await waitUrlContains(driver, '/recruitment');
  await waitVisible(driver, shell.sidePanel);
  await waitVisible(driver, shell.topBarHeader);

  const t = scenario.data || {};
  if (scenario.id.includes('NEG_EMAIL_INVALID')) {
    if (looksLikeEmail(t.email)) {
      throw new Error('Expected invalid email data for NEG_EMAIL_INVALID scenario');
    }
  }
}

module.exports = {runRecruitmentScenario};
