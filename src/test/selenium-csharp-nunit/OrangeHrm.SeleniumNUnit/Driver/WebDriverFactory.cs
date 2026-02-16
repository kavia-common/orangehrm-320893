using System;
using OrangeHrm.SeleniumNUnit.Config;
using OpenQA.Selenium;
using OpenQA.Selenium.Chrome;
using OpenQA.Selenium.Edge;
using OpenQA.Selenium.Firefox;
using OpenQA.Selenium.Remote;

namespace OrangeHrm.SeleniumNUnit.Driver
{
    /// <summary>
    /// WebDriver factory. This is intentionally conservative:
    /// - In discovery-only mode it returns null and does not touch real browser drivers.
    /// - In real mode it creates either:
    ///   - RemoteWebDriver (if SELENIUM_REMOTE_URL is provided)
    ///   - Local Chrome/Firefox/Edge drivers (drivers must already be on PATH)
    /// </summary>
    public static class WebDriverFactory
    {
        // PUBLIC_INTERFACE
        /// <summary>
        /// Creates an IWebDriver if discovery-only is disabled; otherwise returns null.
        /// </summary>
        public static IWebDriver? CreateIfEnabled(OrangeHrmSettings settings)
        {
            if (settings.DiscoveryOnly)
            {
                return null;
            }

            var browser = (settings.Browser ?? "chrome").Trim().ToLowerInvariant();

            if (!string.IsNullOrWhiteSpace(settings.SeleniumRemoteUrl))
            {
                return CreateRemote(settings.SeleniumRemoteUrl!, browser, settings.Headless);
            }

            return browser switch
            {
                "firefox" => CreateFirefox(settings.Headless),
                "edge" => CreateEdge(settings.Headless),
                _ => CreateChrome(settings.Headless)
            };
        }

        private static IWebDriver CreateRemote(string remoteUrl, string browser, bool headless)
        {
            try
            {
                ICapabilities capabilities = browser switch
                {
                    "firefox" => BuildFirefoxOptions(headless).ToCapabilities(),
                    "edge" => BuildEdgeOptions(headless).ToCapabilities(),
                    _ => BuildChromeOptions(headless).ToCapabilities()
                };

                return new RemoteWebDriver(new Uri(remoteUrl), capabilities);
            }
            catch (Exception e)
            {
                throw new InvalidOperationException($"Failed to create RemoteWebDriver for SELENIUM_REMOTE_URL={remoteUrl}", e);
            }
        }

        private static ChromeOptions BuildChromeOptions(bool headless)
        {
            var options = new ChromeOptions();
            if (headless)
            {
                options.AddArgument("--headless=new");
            }

            options.AddArgument("--window-size=1280,800");
            options.AddArgument("--no-sandbox");
            options.AddArgument("--disable-dev-shm-usage");
            return options;
        }

        private static EdgeOptions BuildEdgeOptions(bool headless)
        {
            var options = new EdgeOptions();
            if (headless)
            {
                options.AddArgument("--headless=new");
            }

            options.AddArgument("--window-size=1280,800");
            options.AddArgument("--no-sandbox");
            options.AddArgument("--disable-dev-shm-usage");
            return options;
        }

        private static FirefoxOptions BuildFirefoxOptions(bool headless)
        {
            var options = new FirefoxOptions();
            if (headless)
            {
                options.AddArgument("-headless");
            }

            return options;
        }

        private static IWebDriver CreateChrome(bool headless)
        {
            return new ChromeDriver(BuildChromeOptions(headless));
        }

        private static IWebDriver CreateEdge(bool headless)
        {
            return new EdgeDriver(BuildEdgeOptions(headless));
        }

        private static IWebDriver CreateFirefox(bool headless)
        {
            return new FirefoxDriver(BuildFirefoxOptions(headless));
        }
    }
}
