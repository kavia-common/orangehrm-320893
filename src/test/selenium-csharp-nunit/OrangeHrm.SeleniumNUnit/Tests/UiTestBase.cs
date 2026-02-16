using NUnit.Framework;
using OrangeHrm.SeleniumNUnit.Config;
using OrangeHrm.SeleniumNUnit.Driver;
using OpenQA.Selenium;

namespace OrangeHrm.SeleniumNUnit.Tests
{
    /// <summary>
    /// Base class for UI tests.
    ///
    /// Key behavior:
    /// - If discovery-only is enabled, tests should call SkipIfDiscoveryOnly() early.
    /// - WebDriver is only created when discovery-only is disabled.
    /// </summary>
    public abstract class UiTestBase
    {
        protected OrangeHrmSettings Settings { get; private set; } = null!;
        protected IWebDriver? Driver { get; private set; }

        [SetUp]
        public void SetUp()
        {
            Settings = OrangeHrmSettings.Load();
            Driver = WebDriverFactory.CreateIfEnabled(Settings);
        }

        [TearDown]
        public void TearDown()
        {
            try
            {
                Driver?.Quit();
                Driver?.Dispose();
            }
            finally
            {
                Driver = null;
            }
        }

        /// <summary>
        /// Skip the current test when discovery-only is enabled.
        /// </summary>
        protected void SkipIfDiscoveryOnly()
        {
            if (Settings.DiscoveryOnly)
            {
                Assert.Ignore("Discovery-only mode enabled (no real browser launch).");
            }
        }
    }
}
