using System;
using OpenQA.Selenium;
using OpenQA.Selenium.Chrome;
using OpenQA.Selenium.Edge;
using OpenQA.Selenium.Firefox;
using WebDriverManager;
using WebDriverManager.DriverConfigs.Impl;

namespace OrangeHrm.UiTests.Support;

public static class DriverFactory
{
    [ThreadStatic] private static IWebDriver? _driver;

    // PUBLIC_INTERFACE
    public static IWebDriver Driver => _driver ?? throw new InvalidOperationException("Driver not initialized.");

    // PUBLIC_INTERFACE
    public static void CreateDriver()
    {
        if (_driver != null) return;

        var browser = TestConfig.Browser;
        var headless = TestConfig.Headless;

        switch (browser)
        {
            case "firefox":
                new DriverManager().SetUpDriver(new FirefoxConfig());
                var ffOptions = new FirefoxOptions();
                if (headless) ffOptions.AddArgument("-headless");
                _driver = new FirefoxDriver(ffOptions);
                break;

            case "edge":
                new DriverManager().SetUpDriver(new EdgeConfig());
                var edgeOptions = new EdgeOptions();
                if (headless) edgeOptions.AddArgument("--headless=new");
                _driver = new EdgeDriver(edgeOptions);
                break;

            case "chrome":
                // IMPORTANT:
                // Do not use WebDriverManager for Chrome here. In container images the installed
                // Chrome version can be far ahead of the pinned driver versions, which causes
                // immediate startup crashes (DevToolsActivePort errors).
                //
                // Selenium 4.6+ includes Selenium Manager, which will resolve/download a matching
                // driver automatically when using the plain ChromeDriver constructor.
                var chromeOptions = new ChromeOptions();

                // Container-safe / CI-safe flags.
                chromeOptions.AddArgument("--remote-allow-origins=*");
                chromeOptions.AddArgument("--no-sandbox");
                chromeOptions.AddArgument("--disable-dev-shm-usage");
                chromeOptions.AddArgument("--disable-gpu");
                chromeOptions.AddArgument("--window-size=1920,1080");

                // Optional explicit Chrome binary path (useful in some containers).
                if (!string.IsNullOrWhiteSpace(TestConfig.ChromeBinary))
                {
                    chromeOptions.BinaryLocation = TestConfig.ChromeBinary;
                    Console.WriteLine($"[DriverFactory] Using Chrome binary: {TestConfig.ChromeBinary}");
                }

                // If no display is available, force headless regardless of config.
                var shouldHeadless = headless || string.IsNullOrWhiteSpace(Environment.GetEnvironmentVariable("DISPLAY"));
                if (shouldHeadless) chromeOptions.AddArgument("--headless=new");

                _driver = new ChromeDriver(chromeOptions);
                break;

            default:
                throw new ArgumentException($"Unsupported browser: {browser} (use chrome|firefox|edge)");
        }

        _driver.Manage().Timeouts().ImplicitWait = TimeSpan.Zero;
        _driver.Manage().Timeouts().PageLoad = TimeSpan.FromSeconds(45);

        // Maximize is unreliable in headless/container runs; prefer explicit window-size.
        if (!headless)
        {
            _driver.Manage().Window.Maximize();
        }
    }

    // PUBLIC_INTERFACE
    public static void QuitDriver()
    {
        try
        {
            _driver?.Quit();
        }
        finally
        {
            _driver = null;
        }
    }
}
