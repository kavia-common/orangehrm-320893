using NUnit.Framework;
using OrangeHrm.SeleniumNUnit.Api;
using RestSharp;

namespace OrangeHrm.SeleniumNUnit.Tests
{
    public sealed class ApiAuthTests : ApiTestBase
    {
        [Test]
        public void CanAuthenticateUsingFunctionalTestingValidateEndpoint()
        {
            GuardDiscoveryOnly();

            if (ApiSettings.DiscoveryOnly)
            {
                Assert.Pass("Discovery-only mode: test executed without outbound HTTP calls.");
                return;
            }

            ApiClient.AuthenticateAsAdmin();

            // Smoke check an authenticated endpoint
            var req = new RestRequest("/api/v2/admin/job-titles", Method.Get);
            var resp = ApiClient.ExecuteAuthed(req);

            Assert.That((int)resp.StatusCode, Is.EqualTo(200));
        }

        [Test]
        public void InvalidCredentials_ReturnSuccessFalse()
        {
            GuardDiscoveryOnly();

            if (ApiSettings.DiscoveryOnly)
            {
                Assert.Pass("Discovery-only mode: test executed without outbound HTTP calls.");
                return;
            }

            var client = ApiClient.CreateClient();
            var req = new RestRequest("/functional-testing/auth/validate", Method.Post);
            req.AddJsonBody(new { username = "Admin", password = "wrong-password" });

            var resp = client.Execute(req);

            Assert.That((int)resp.StatusCode, Is.EqualTo(200));
            Assert.That(resp.Content, Does.Contain("\"success\":false"));
        }
    }
}
