using System;
using NUnit.Framework;
using RestSharp;

namespace OrangeHrm.SeleniumNUnit.Tests
{
    public sealed class ApiEmployeeWorkflowTests : ApiTestBase
    {
        [Test]
        public void AdminCanCreateJobTitle_ThenListReturnsData()
        {
            GuardDiscoveryOnly();

            if (ApiSettings.DiscoveryOnly)
            {
                Assert.Pass("Discovery-only mode: test executed without outbound HTTP calls.");
                return;
            }

            ApiClient.AuthenticateAsAdmin();

            var title = "API-JobTitle-" + DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();

            var createReq = new RestRequest("/api/v2/admin/job-titles", Method.Post);
            createReq.AddJsonBody(new
            {
                title = title,
                description = "Created by RestSharp API test",
                specification = (string?)null,
                note = "note"
            });

            var createResp = ApiClient.ExecuteAuthed(createReq);
            Assert.That((int)createResp.StatusCode, Is.EqualTo(200), $"Create response: {createResp.Content}");

            var listReq = new RestRequest("/api/v2/admin/job-titles", Method.Get);
            var listResp = ApiClient.ExecuteAuthed(listReq);

            Assert.That((int)listResp.StatusCode, Is.EqualTo(200));
            Assert.That(listResp.Content, Does.Contain("\"data\""), "Expected data field in list response.");
        }

        [Test]
        public void UnauthenticatedRequest_IsForbiddenOrUnauthorized()
        {
            GuardDiscoveryOnly();

            if (ApiSettings.DiscoveryOnly)
            {
                Assert.Pass("Discovery-only mode: test executed without outbound HTTP calls.");
                return;
            }

            var client = ApiClient.CreateClient();

            var req = new RestRequest("/api/v2/admin/job-titles", Method.Get);
            var resp = client.Execute(req);

            // Environment dependent: could be 401 or 403.
            Assert.That((int)resp.StatusCode, Is.AnyOf(401, 403), $"Status: {(int)resp.StatusCode}, body: {resp.Content}");
        }
    }
}
