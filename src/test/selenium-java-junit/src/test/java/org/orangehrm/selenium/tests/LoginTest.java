package org.orangehrm.selenium.tests;

import org.junit.jupiter.api.Test;
import org.orangehrm.selenium.pages.LoginPage;

/**
 * Example login test.
 *
 * - In discovery-only mode: test is discovered but skipped before doing browser actions.
 * - In real mode: uses env vars and attempts to execute login flow.
 */
public final class LoginTest extends BaseUiTest {

    @Test
    void adminCanLogin_exampleSkeleton() {
        assumeRealMode();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(config.getBaseUrl());
        loginPage.login(config.getAdminUsername(), config.getAdminPassword());

        // Skeleton: Add a real post-login assertion once selectors and URLs are confirmed.
        // e.g., assertTrue(driver.getCurrentUrl().contains("/dashboard"));
    }
}
