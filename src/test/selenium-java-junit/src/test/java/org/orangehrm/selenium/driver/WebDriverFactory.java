package org.orangehrm.selenium.driver;

import org.orangehrm.selenium.config.OrangeHrmConfig;
import org.orangehrm.selenium.config.RunMode;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;

/**
 * WebDriver factory.
 *
 * Default behavior is discovery-only to prevent any real browser launch during CI validation.
 *
 * Real execution modes:
 * - Local: uses browser-specific local drivers (requires driver binaries on PATH)
 * - Remote/Grid: if env SELENIUM_REMOTE_URL is set, creates a RemoteWebDriver session on Selenium Grid
 */
public final class WebDriverFactory {
    private WebDriverFactory() {}

    // PUBLIC_INTERFACE
    public static WebDriver create(OrangeHrmConfig config) {
        /**
         * Create WebDriver.
         *
         * In discovery-only mode, returns a DiscoveryOnlyWebDriver.
         * In real mode:
         *  - if SELENIUM_REMOTE_URL is set: uses Selenium Grid (RemoteWebDriver)
         *  - else: attempts to create a local Chrome/Firefox/Edge driver (requires driver binaries on PATH)
         *
         * @param config test configuration
         * @return WebDriver instance
         */
        if (RunMode.isDiscoveryOnly()) {
            return new DiscoveryOnlyWebDriver();
        }

        String browser = (config.getBrowser() == null ? "chrome" : config.getBrowser()).toLowerCase();
        boolean headless = config.isHeadless();

        String remoteUrl = System.getenv("SELENIUM_REMOTE_URL");
        if (remoteUrl != null && !remoteUrl.trim().isEmpty()) {
            return createRemote(remoteUrl.trim(), browser, headless);
        }

        return createLocal(browser, headless);
    }

    private static WebDriver createRemote(String remoteUrl, String browser, boolean headless) {
        try {
            MutableCapabilities capabilities = buildCapabilities(browser, headless);
            return new RemoteWebDriver(URI.create(remoteUrl).toURL(), capabilities);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create RemoteWebDriver for SELENIUM_REMOTE_URL=" + remoteUrl, e);
        }
    }

    private static WebDriver createLocal(String browser, boolean headless) {
        try {
            if ("chrome".equals(browser)) {
                ChromeOptions options = (ChromeOptions) buildCapabilities(browser, headless);
                return new org.openqa.selenium.chrome.ChromeDriver(options);
            }

            if ("firefox".equals(browser)) {
                FirefoxOptions options = (FirefoxOptions) buildCapabilities(browser, headless);
                return new org.openqa.selenium.firefox.FirefoxDriver(options);
            }

            if ("edge".equals(browser)) {
                EdgeOptions options = (EdgeOptions) buildCapabilities(browser, headless);
                return new org.openqa.selenium.edge.EdgeDriver(options);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create real WebDriver. Ensure drivers are available on PATH.", e);
        }

        throw new IllegalArgumentException(
                "Unsupported BROWSER=\"" + browser + "\". Use \"chrome\", \"firefox\" or \"edge\"."
        );
    }

    private static MutableCapabilities buildCapabilities(String browser, boolean headless) {
        if ("chrome".equals(browser)) {
            ChromeOptions options = new ChromeOptions();
            if (headless) {
                options.addArguments("--headless=new");
            }
            options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu", "--window-size=1440,900");
            return options;
        }

        if ("firefox".equals(browser)) {
            FirefoxOptions options = new FirefoxOptions();
            if (headless) {
                options.addArguments("-headless");
            }
            return options;
        }

        if ("edge".equals(browser)) {
            EdgeOptions options = new EdgeOptions();
            if (headless) {
                options.addArguments("--headless=new");
            }
            options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu", "--window-size=1440,900");
            return options;
        }

        throw new IllegalArgumentException(
                "Unsupported BROWSER=\"" + browser + "\". Use \"chrome\", \"firefox\" or \"edge\"."
        );
    }
}
