using System;
using NUnit.Framework;
using OrangeHrm.SeleniumNUnit.Config;
using RestSharp;

namespace OrangeHrm.SeleniumNUnit.Api
{
    /// <summary>
    /// Minimal API client wrapper for OrangeHRM.
    ///
    /// Auth strategy:
    /// - POST /functional-testing/auth/validate with { username, password }
    /// - Use the resulting cookie container for subsequent /api/v2 requests.
    /// </summary>
    public sealed class OrangeHrmApiClient
    {
        private readonly OrangeHrmApiSettings _settings;
        private readonly CookieContainer _cookies = new CookieContainer();

        public OrangeHrmApiClient(OrangeHrmApiSettings settings)
        {
            _settings = settings;
        }

        // PUBLIC_INTERFACE
        public RestClient CreateClient()
        {
            /// <summary>
            /// Create a RestSharp client configured with a shared CookieContainer.
            /// </summary>
            Assert.That(_settings.ApiBaseUrl, Is.Not.Null.And.Not.Empty, "ORANGEHRM_API_BASE_URL must be set.");

            var options = new RestClientOptions(_settings.ApiBaseUrl!)
            {
                CookieContainer = _cookies,
                ThrowOnAnyError = false
            };
            return new RestClient(options);
        }

        // PUBLIC_INTERFACE
        public void AuthenticateAsAdmin()
        {
            /// <summary>
            /// Authenticate via functional-testing helper endpoint and store cookies.
            /// </summary>
            Assert.That(_settings.AdminUsername, Is.Not.Null.And.Not.Empty, "ORANGEHRM_ADMIN_USERNAME must be set.");
            Assert.That(_settings.AdminPassword, Is.Not.Null.And.Not.Empty, "ORANGEHRM_ADMIN_PASSWORD must be set.");

            var client = CreateClient();

            var req = new RestRequest("/functional-testing/auth/validate", Method.Post);
            req.AddJsonBody(new
            {
                username = _settings.AdminUsername,
                password = _settings.AdminPassword
            });

            var resp = client.Execute(req);
            Assert.That(resp.StatusCode, Is.EqualTo(System.Net.HttpStatusCode.OK), "Expected 200 from auth validate helper.");
            Assert.That(resp.Content, Does.Contain("success"), "Expected response to include success field.");
        }

        // PUBLIC_INTERFACE
        public RestResponse ExecuteAuthed(RestRequest request)
        {
            /// <summary>
            /// Execute request using the authenticated cookie container.
            /// Caller must have called AuthenticateAsAdmin().
            /// </summary>
            var client = CreateClient();
            return client.Execute(request);
        }
    }
}
