using System;
using System.Net;
using System.Text.Json;
using System.Threading.Tasks;
using NUnit.Framework;
using RestSharp;
using OrangeHrm.ApiTests.Support;

namespace OrangeHrm.ApiTests.Tests;

[TestFixture]
public class EmployeeWorkflowTests
{
    [Test]
    public async Task Create_And_Delete_JobTitle_Workflow_Should_Succeed()
    {
        var client = new OrangeHrmClient();
        await client.LoginWithCookiesAsync(TestConfig.AdminUsername, TestConfig.AdminPassword);

        var endpoint = OrangeHrmClient.V2("/admin/job-titles");

        // Create
        var createReq = new RestRequest(endpoint, Method.Post);
        createReq.AddJsonBody(new
        {
            title = $"API-AUTO-{DateTimeOffset.UtcNow.ToUnixTimeMilliseconds()}",
            description = "Created by API automation suite",
            specification = (string?)null,
            note = "cleanup expected"
        });

        var createRes = await client.ExecuteAuthedAsync(createReq);
        Assert.That(createRes.StatusCode, Is.EqualTo(HttpStatusCode.OK));
        Assert.That(createRes.Content, Is.Not.Null.And.Not.Empty);

        using var createJson = OrangeHrmClient.ParseJson(createRes.Content);
        var id = createJson.RootElement.GetProperty("data").GetProperty("id").GetInt32();
        Assert.That(id, Is.GreaterThan(0));

        // Delete
        var deleteReq = new RestRequest(endpoint, Method.Delete);
        deleteReq.AddJsonBody(new { ids = new[] { id } });

        var deleteRes = await client.ExecuteAuthedAsync(deleteReq);
        Assert.That(deleteRes.StatusCode, Is.EqualTo(HttpStatusCode.OK));

        using var deleteJson = OrangeHrmClient.ParseJson(deleteRes.Content);
        var dataArray = deleteJson.RootElement.GetProperty("data");
        Assert.That(dataArray.ValueKind, Is.EqualTo(JsonValueKind.Array));
    }

    [Test]
    public async Task Pagination_Should_Return_At_Most_Limit_Items()
    {
        var client = new OrangeHrmClient();
        await client.LoginWithCookiesAsync(TestConfig.AdminUsername, TestConfig.AdminPassword);

        var endpoint = OrangeHrmClient.V2("/admin/job-titles");

        var req = new RestRequest(endpoint, Method.Get);
        req.AddQueryParameter("limit", "1");
        req.AddQueryParameter("offset", "0");

        var res = await client.ExecuteAuthedAsync(req);
        Assert.That(res.StatusCode, Is.EqualTo(HttpStatusCode.OK));

        using var json = OrangeHrmClient.ParseJson(res.Content);
        var data = json.RootElement.GetProperty("data");
        Assert.That(data.ValueKind, Is.EqualTo(JsonValueKind.Array));
        Assert.That(data.GetArrayLength(), Is.LessThanOrEqualTo(1));
    }

    [Test]
    public async Task Delete_With_Empty_Ids_Should_Be_Rejected()
    {
        var client = new OrangeHrmClient();
        await client.LoginWithCookiesAsync(TestConfig.AdminUsername, TestConfig.AdminPassword);

        var endpoint = OrangeHrmClient.V2("/admin/job-titles");

        var req = new RestRequest(endpoint, Method.Delete);
        req.AddJsonBody(new { ids = Array.Empty<int>() });

        var res = await client.ExecuteAuthedAsync(req);

        Assert.That(res.StatusCode, Is.AnyOf(HttpStatusCode.BadRequest, (HttpStatusCode)422));
    }
}
