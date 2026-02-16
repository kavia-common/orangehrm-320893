using NUnit.Framework;
using OrangeHrm.SeleniumNUnit.Api;
using OrangeHrm.SeleniumNUnit.Config;

namespace OrangeHrm.SeleniumNUnit.Tests
{
    /// <summary>
    /// Base class for API tests.
    ///
    /// IMPORTANT:
    /// - In discovery-only mode (default), tests must not perform outbound HTTP calls.
    /// - Tests should still run (not skipped); they should take a discovery-safe path.
    /// </summary>
    public abstract class ApiTestBase
    {
        protected OrangeHrmApiSettings ApiSettings { get; private set; } = null!;
        protected OrangeHrmApiClient ApiClient { get; private set; } = null!;

        [SetUp]
        public void SetUp()
        {
            ApiSettings = OrangeHrmApiSettings.Load();
            ApiClient = new OrangeHrmApiClient(ApiSettings);
        }

        protected void GuardDiscoveryOnly()
        {
            if (ApiSettings.DiscoveryOnly)
            {
                return;
            }

            Assert.That(ApiSettings.ApiBaseUrl, Is.Not.Null.And.Not.Empty, "ORANGEHRM_API_BASE_URL must be set.");
            Assert.That(ApiSettings.AdminUsername, Is.Not.Null.And.Not.Empty, "ORANGEHRM_ADMIN_USERNAME must be set.");
            Assert.That(ApiSettings.AdminPassword, Is.Not.Null.And.Not.Empty, "ORANGEHRM_ADMIN_PASSWORD must be set.");
        }
    }
}
