package org.orangehrm.selenium.driver;

import org.orangehrm.selenium.config.OrangeHrmConfig;
import org.orangehrm.selenium.config.RunMode;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;

/**
 * WebDriver factory.
 *
 * Default behavior is discovery-only to prevent any real browser launch during CI validation.
 */
public final class WebDriverFactory {
    private WebDriverFactory() {}

    // PUBLIC_INTERFACE
    public static WebDriver create(OrangeHrmConfig config) {
        /**
         * Create WebDriver.
         *
         * In discovery-only mode, returns a DiscoveryOnlyWebDriver.
         * In real mode, attempts to create a local Chrome/Firefox driver (requires driver binaries on PATH).
         *
         * @param config test configuration
         * @return WebDriver instance
         */
        if (RunMode.isDiscoveryOnly()) {
            return new DiscoveryOnlyWebDriver();
        }

        String browser = (config.getBrowser() == null ? "chrome" : config.getBrowser()).toLowerCase();
        boolean headless = config.isHeadless();

        try {
            if ("chrome".equals(browser)) {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    // Mirrors Node suite preference: '--headless=new' where available
                    options.addArguments("--headless=new");
                }
                options.addArguments(
                        "--no-sandbox",
                        "--disable-dev-shm-usage",
                        "--disable-gpu",
                        "--window-size=1440,900"
                );
                return new org.openqa.selenium.chrome.ChromeDriver(options);
            }

            if ("firefox".equals(browser)) {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                return new org.openqa.selenium.firefox.FirefoxDriver(options);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create real WebDriver. Ensure drivers are available on PATH.", e);
        }

        throw new IllegalArgumentException("Unsupported BROWSER=\"" + browser + "\". Use \"chrome\" or \"firefox\".");
    }
}
