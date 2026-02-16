using System;
using OrangeHrm.SeleniumNUnit.Config;
using OpenQA.Selenium;
using OpenQA.Selenium.Chrome;
using OpenQA.Selenium.Firefox;

namespace OrangeHrm.SeleniumNUnit.Driver
{
    /// <summary>
    /// WebDriver factory. This is intentionally conservative:
    /// - In discovery-only mode it returns null and does not touch real browser drivers.
    /// - In real mode it creates a local Chrome/Firefox driver (drivers must already be on PATH).
    /// </summary>
    public static class WebDriverFactory
    {
        // PUBLIC_INTERFACE
        /// <summary>
        /// Creates an IWebDriver if discovery-only is disabled; otherwise returns null.
        ///
        /// In real mode, this expects a driver binary on PATH:
        /// - chromedriver (for Chrome)
        /// - geckodriver (for Firefox)
        /// </summary>
        public static IWebDriver? CreateIfEnabled(OrangeHrmSettings settings)
        {
            if (settings.DiscoveryOnly)
            {
                return null;
            }

            return settings.Browser switch
            {
                "firefox" => CreateFirefox(settings.Headless),
                _ => CreateChrome(settings.Headless)
            };
        }

        private static IWebDriver CreateChrome(bool headless)
        {
            var options = new ChromeOptions();

            if (headless)
            {
                // Use modern headless mode when available
                options.AddArgument("--headless=new");
            }

            options.AddArgument("--window-size=1280,800");
            options.AddArgument("--no-sandbox");
            options.AddArgument("--disable-dev-shm-usage");

            return new ChromeDriver(options);
        }

        private static IWebDriver CreateFirefox(bool headless)
        {
            var options = new FirefoxOptions();
            if (headless)
            {
                options.AddArgument("-headless");
            }

            return new FirefoxDriver(options);
        }
    }
}
