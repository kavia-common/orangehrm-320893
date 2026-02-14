using System.Net;
using System.Threading.Tasks;
using NUnit.Framework;
using RestSharp;
using OrangeHrm.ApiTests.Support;

namespace OrangeHrm.ApiTests.Tests;

[TestFixture]
public class AuthFlowTests
{
    [Test]
    public async Task Unauthenticated_Request_Should_Be_Rejected()
    {
        var client = new OrangeHrmClient();

        var req = new RestRequest(OrangeHrmClient.V2("/admin/job-titles"), Method.Get);
        var res = await client.ExecuteAnonAsync(req);

        Assert.That(res.StatusCode, Is.AnyOf(HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden));
    }

    [Test]
    public async Task Admin_Login_Should_Allow_Access()
    {
        var client = new OrangeHrmClient();
        await client.LoginWithCookiesAsync(TestConfig.AdminUsername, TestConfig.AdminPassword);

        var req = new RestRequest(OrangeHrmClient.V2("/admin/job-titles"), Method.Get);
        var res = await client.ExecuteAuthedAsync(req);

        Assert.That(res.StatusCode, Is.EqualTo(HttpStatusCode.OK));
        Assert.That(res.Content, Is.Not.Null.And.Not.Empty);

        using var json = OrangeHrmClient.ParseJson(res.Content);
        Assert.That(json.RootElement.TryGetProperty("data", out _), Is.True);
        Assert.That(json.RootElement.TryGetProperty("meta", out _), Is.True);
    }
}
