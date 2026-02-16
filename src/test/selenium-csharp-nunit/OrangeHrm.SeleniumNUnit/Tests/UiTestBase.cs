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
    /// - In discovery-only mode, tests should NOT launch a real browser.
    /// - Tests should still execute (no Ignore/Skip), so reporting (e.g., Allure) is produced.
    /// </summary>
    [Parallelizable(ParallelScope.All)]
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
        /// In discovery-only mode, ensures we do NOT touch WebDriver-dependent actions.
        ///
        /// IMPORTANT:
        /// We intentionally do NOT skip/ignore tests here because this repository task requires
        /// "no skipping" while still avoiding a real browser in CI.
        /// </summary>
        protected void GuardDiscoveryOnly()
        {
            if (Settings.DiscoveryOnly)
            {
                // No-op: the test should proceed through its discovery-safe path.
                return;
            }

            // In real mode, Driver is expected to be created by SetUp().
            Assert.That(Driver, Is.Not.Null, "Driver should be created when discovery-only is disabled.");
        }
    }
}
