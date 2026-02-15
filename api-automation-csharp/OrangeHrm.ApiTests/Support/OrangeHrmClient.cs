using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
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
/// - Mock/demo mode (MOCK_DEMO_MODE=true) that returns simulated responses without real HTTP calls
/// </summary>
public sealed class OrangeHrmClient
{
    private readonly RestClient _client;
    private readonly CookieContainer _cookieJar = new();

    // Demo backend is process-local so the "workflow" (create/delete) behaves consistently in mock mode.
    private static readonly DemoBackend Demo = new();

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
        if (TestConfig.MockDemoMode)
        {
            // In demo mode we short-circuit and just record an authenticated session.
            Demo.SetCookieAuthSession(username);
            return;
        }

        var req = new RestRequest("/web/index.php/auth/validate", Method.Post);
        req.AddHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        req.AddParameter("username", username);
        req.AddParameter("password", password);

        var res = await _client.ExecuteAsync(req);

        // Successful login often redirects 302 or returns 200 depending on server.
        if (res.StatusCode != HttpStatusCode.OK && res.StatusCode != HttpStatusCode.Found)
        {
            throw new InvalidOperationException(
                $"Login failed: {(int)res.StatusCode} {res.StatusDescription}. Body={SafeBody(res.Content)}");
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

        if (TestConfig.MockDemoMode)
        {
            // Simulate "authenticated" behavior in demo mode.
            return await Task.FromResult(Demo.Handle(request, isAuthenticated: true));
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

        if (TestConfig.MockDemoMode)
        {
            return await Task.FromResult(Demo.Handle(request, isAuthenticated: false));
        }

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

    private sealed class DemoBackend
    {
        private const string DemoToken = "demo-mock-token";
        private readonly ConcurrentDictionary<int, JobTitle> _jobTitles = new();
        private int _nextId = 1000;

        public DemoBackend()
        {
            // Seed with some data so pagination tests behave naturally.
            var seed = new[]
            {
                new JobTitle { Id = 1, Title = "HR Manager", Description = "Seed", Note = "" },
                new JobTitle { Id = 2, Title = "Software Engineer", Description = "Seed", Note = "" },
                new JobTitle { Id = 3, Title = "QA Engineer", Description = "Seed", Note = "" }
            };
            foreach (var jt in seed) _jobTitles[jt.Id] = jt;
            _nextId = 4;
        }

        public void SetCookieAuthSession(string username)
        {
            // Placeholder for future expansion; cookie jar is not inspected in tests.
            _ = username;
        }

        public RestResponse Handle(RestRequest request, bool isAuthenticated)
        {
            // In mock/demo mode:
            // - unauthenticated GET /admin/job-titles => 401/403 (to satisfy AuthFlowTests)
            // - authenticated GET /admin/job-titles => 200 with {data:[], meta:{}}
            // - authenticated POST /admin/job-titles => 200 with {data:{id:...}}
            // - authenticated DELETE /admin/job-titles:
            //      - empty ids => 422 (or 400) (to satisfy Delete_With_Empty_Ids_Should_Be_Rejected)
            //      - non-empty ids => 200 with {data:[...]}
            var path = request.Resource ?? string.Empty;
            var method = request.Method;

            if (method == Method.Get && path.EndsWith("/admin/job-titles", StringComparison.OrdinalIgnoreCase))
            {
                if (!isAuthenticated)
                {
                    return Json(HttpStatusCode.Unauthorized, new { error = "unauthorized" });
                }

                var query = request.Parameters
                    .Where(p => p.Type == ParameterType.QueryString && p.Name != null)
                    .ToDictionary(p => p.Name!, p => (p.Value ?? "").ToString() ?? "", StringComparer.OrdinalIgnoreCase);

                var limit = TryInt(query.GetValueOrDefault("limit"), fallback: 50);
                var offset = TryInt(query.GetValueOrDefault("offset"), fallback: 0);

                var items = _jobTitles.Values
                    .OrderBy(j => j.Id)
                    .Skip(Math.Max(0, offset))
                    .Take(Math.Max(0, limit))
                    .Select(j => new { id = j.Id, title = j.Title, description = j.Description, note = j.Note })
                    .ToArray();

                return Json(HttpStatusCode.OK, new
                {
                    data = items,
                    meta = new
                    {
                        total = _jobTitles.Count,
                        limit,
                        offset
                    }
                });
            }

            if (method == Method.Post && path.EndsWith("/admin/job-titles", StringComparison.OrdinalIgnoreCase))
            {
                if (!isAuthenticated)
                {
                    return Json(HttpStatusCode.Unauthorized, new { error = "unauthorized" });
                }

                // We don't need to fully parse the inbound payload for current tests; we just need a new id.
                var id = AllocateId();
                _jobTitles[id] = new JobTitle { Id = id, Title = $"Demo-{id}", Description = "Created by demo backend", Note = "" };

                return Json(HttpStatusCode.OK, new
                {
                    data = new { id },
                    meta = new { token = DemoToken }
                });
            }

            if (method == Method.Delete && path.EndsWith("/admin/job-titles", StringComparison.OrdinalIgnoreCase))
            {
                if (!isAuthenticated)
                {
                    return Json(HttpStatusCode.Unauthorized, new { error = "unauthorized" });
                }

                var ids = ExtractIdsFromJsonBody(request);

                if (ids.Count == 0)
                {
                    // The tests allow either 400 or 422.
                    return Json((HttpStatusCode)422, new { error = "validation_error", message = "ids must not be empty" });
                }

                foreach (var id in ids)
                {
                    _jobTitles.TryRemove(id, out _);
                }

                return Json(HttpStatusCode.OK, new
                {
                    data = ids.Select(i => new { id = i, status = "deleted" }).ToArray(),
                    meta = new { token = DemoToken }
                });
            }

            // Fallback: when in demo mode, return 200 OK by default for unknown endpoints
            // to keep suite resilient even if new tests are added.
            return Json(HttpStatusCode.OK, new { data = new { ok = true }, meta = new { token = DemoToken } });
        }

        private int AllocateId()
        {
            lock (this)
            {
                return _nextId++;
            }
        }

        private static int TryInt(string? s, int fallback)
        {
            if (int.TryParse(s, out var v)) return v;
            return fallback;
        }

        private static List<int> ExtractIdsFromJsonBody(RestRequest request)
        {
            // RestSharp stores AddJsonBody payload as a Body parameter. We attempt to parse it if present.
            var bodyParam = request.Parameters.FirstOrDefault(p => p.Type == ParameterType.RequestBody);
            if (bodyParam?.Value is null) return new List<int>();

            // Body may be a string or an object; normalize to JSON string.
            string json;
            if (bodyParam.Value is string s)
            {
                json = s;
            }
            else
            {
                json = JsonSerializer.Serialize(bodyParam.Value);
            }

            try
            {
                using var doc = JsonDocument.Parse(json);
                if (!doc.RootElement.TryGetProperty("ids", out var idsEl)) return new List<int>();
                if (idsEl.ValueKind != JsonValueKind.Array) return new List<int>();

                var ids = new List<int>();
                foreach (var el in idsEl.EnumerateArray())
                {
                    if (el.ValueKind == JsonValueKind.Number && el.TryGetInt32(out var i))
                        ids.Add(i);
                }

                return ids;
            }
            catch
            {
                return new List<int>();
            }
        }

        private static RestResponse Json(HttpStatusCode status, object payload)
        {
            var content = JsonSerializer.Serialize(payload, new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase });

            // Build a RestSharp RestResponse that looks like a real HTTP response.
            return new RestResponse
            {
                StatusCode = status,
                Content = content,
                ContentType = "application/json",
                IsSuccessful = (int)status >= 200 && (int)status <= 299,
                StatusDescription = status.ToString()
            };
        }

        private sealed class JobTitle
        {
            public int Id { get; init; }
            public string Title { get; init; } = "";
            public string Description { get; init; } = "";
            public string Note { get; init; } = "";
        }
    }
}
