using System;
using System.Collections.Generic;
using System.Net;
using System.Text.Json;
using System.Threading.Tasks;
using RestSharp;

namespace OrangeHrm.ApiTests.Support;

/// <summary>
/// PUBLIC_INTERFACE
/// Minimal HTTP client wrapper for OrangeHRM API automation tests.
///
/// Supports:
/// - Cookie-based session auth via /web/index.php/auth/validate
/// - Optional bearer token auth if API_BEARER_TOKEN is provided
/// </summary>
public sealed class OrangeHrmClient
{
    private readonly RestClient _client;
    private readonly CookieContainer _cookieJar = new();

    public OrangeHrmClient()
    {
        var options = new RestClientOptions(TestConfig.BaseUrl)
        {
            CookieContainer = _cookieJar,
            ThrowOnAnyError = false,
            MaxTimeout = 20000
        };
        _client = new RestClient(options);
    }

    /// <summary>
    /// PUBLIC_INTERFACE
    /// Attempt cookie-based login using the OrangeHRM web auth endpoint.
    /// </summary>
    public async Task LoginWithCookiesAsync(string username, string password)
    {
        var req = new RestRequest("/web/index.php/auth/validate", Method.Post);
        req.AddHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        req.AddParameter("username", username);
        req.AddParameter("password", password);

        var res = await _client.ExecuteAsync(req);

        // Successful login often redirects 302 or returns 200 depending on server.
        if (res.StatusCode != HttpStatusCode.OK && res.StatusCode != HttpStatusCode.Found)
        {
            throw new InvalidOperationException($"Login failed: {(int)res.StatusCode} {res.StatusDescription}. Body={SafeBody(res.Content)}");
        }
    }

    /// <summary>
    /// PUBLIC_INTERFACE
    /// Execute an authenticated request against API v2.
    /// </summary>
    public async Task<RestResponse> ExecuteAuthedAsync(RestRequest request)
    {
        request.AddHeader("Accept", "application/json");

        // Prefer bearer token if provided.
        if (!string.IsNullOrWhiteSpace(TestConfig.ApiBearerToken))
        {
            request.AddOrUpdateHeader("Authorization", $"Bearer {TestConfig.ApiBearerToken}");
        }

        return await _client.ExecuteAsync(request);
    }

    /// <summary>
    /// PUBLIC_INTERFACE
    /// Execute an anonymous request against API v2.
    /// </summary>
    public async Task<RestResponse> ExecuteAnonAsync(RestRequest request)
    {
        request.AddHeader("Accept", "application/json");
        return await _client.ExecuteAsync(request);
    }

    /// <summary>
    /// PUBLIC_INTERFACE
    /// Build a full API v2 path from a resource path such as "/admin/job-titles".
    /// </summary>
    public static string V2(string resourcePath)
    {
        if (!resourcePath.StartsWith("/")) resourcePath = "/" + resourcePath;
        return $"{TestConfig.ApiBasePath}{resourcePath}";
    }

    /// <summary>
    /// PUBLIC_INTERFACE
    /// Parse JSON response to JsonDocument (for flexible assertions).
    /// </summary>
    public static JsonDocument ParseJson(string? json)
    {
        if (string.IsNullOrWhiteSpace(json))
            throw new ArgumentException("Response body is empty; cannot parse JSON.");

        return JsonDocument.Parse(json);
    }

    private static string SafeBody(string? body)
    {
        if (string.IsNullOrEmpty(body)) return "<empty>";
        return body.Length > 500 ? body[..500] + "..." : body;
    }
}
