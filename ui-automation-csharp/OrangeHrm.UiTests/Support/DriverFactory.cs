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
                new DriverManager().SetUpDriver(new ChromeConfig());
                var chromeOptions = new ChromeOptions();
                chromeOptions.AddArgument("--remote-allow-origins=*");
                if (headless) chromeOptions.AddArgument("--headless=new");
                _driver = new ChromeDriver(chromeOptions);
                break;

            default:
                throw new ArgumentException($"Unsupported browser: {browser} (use chrome|firefox|edge)");
        }

        _driver.Manage().Timeouts().ImplicitWait = TimeSpan.Zero;
        _driver.Manage().Timeouts().PageLoad = TimeSpan.FromSeconds(45);
        _driver.Manage().Window.Maximize();
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
