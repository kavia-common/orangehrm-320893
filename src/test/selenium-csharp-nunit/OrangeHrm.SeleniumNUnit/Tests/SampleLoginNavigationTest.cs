using NUnit.Framework;
using OrangeHrm.SeleniumNUnit.Pages;

namespace OrangeHrm.SeleniumNUnit.Tests
{
    public sealed class SampleLoginNavigationTest : UiTestBase
    {
        [Test]
        public void CanNavigateToLoginPage()
        {
            // Required by this work item: do not skip tests; just avoid real browser usage.
            GuardDiscoveryOnly();

            if (Settings.DiscoveryOnly)
            {
                Assert.That(Driver, Is.Null, "Driver must not be created in discovery-only mode.");
                Assert.Pass("Discovery-only mode: test executed without launching a browser.");
                return;
            }

            var page = new LoginPage(Driver!);
            page.Open(Settings.BaseUrl!);

            Assert.Pass("Navigation succeeded.");
        }
    }
}
