using NUnit.Framework;
using OrangeHrm.SeleniumNUnit.Pages;

namespace OrangeHrm.SeleniumNUnit.Tests
{
    public sealed class SampleLoginNavigationTest : UiTestBase
    {
        [Test]
        public void CanNavigateToLoginPage_WhenRealBrowserModeEnabled()
        {
            // CI-safe default: discovery-only -> test is ignored, not executed with a browser.
            SkipIfDiscoveryOnly();

            Assert.That(Driver, Is.Not.Null, "Driver should be created when discovery-only is disabled.");

            var page = new LoginPage(Driver!);
            page.Open(Settings.BaseUrl!);

            Assert.Pass("Navigation succeeded.");
        }
    }
}
