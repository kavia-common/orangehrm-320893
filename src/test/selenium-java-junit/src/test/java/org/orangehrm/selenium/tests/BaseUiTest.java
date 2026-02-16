package org.orangehrm.selenium.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.orangehrm.selenium.config.OrangeHrmConfig;
import org.orangehrm.selenium.config.RunMode;
import org.orangehrm.selenium.driver.WebDriverFactory;
import org.openqa.selenium.WebDriver;

/**
 * Base class for UI tests.
 *
 * By default, tests should be discoverable and runnable without launching a browser.
 */
public abstract class BaseUiTest {

    protected WebDriver driver;
    protected OrangeHrmConfig config;

    @BeforeEach
    void setUp() {
        // In discovery-only mode, do not require env secrets.
        boolean requireSecrets = !RunMode.isDiscoveryOnly();
        this.config = OrangeHrmConfig.fromEnv(requireSecrets);
        this.driver = WebDriverFactory.create(this.config);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            // In discovery-only mode this is a no-op; in real mode quits the browser.
            driver.quit();
        }
    }

    protected void assumeRealMode() {
        // If discovery-only: skip test body (still discovered).
        Assumptions.assumeFalse(RunMode.isDiscoveryOnly(), "Discovery-only mode enabled; skipping real UI execution.");
    }
}
