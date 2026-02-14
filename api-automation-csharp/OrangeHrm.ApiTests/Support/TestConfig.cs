using System;
using DotNetEnv;

namespace OrangeHrm.ApiTests.Support;

public static class TestConfig
{
    static TestConfig()
    {
        // Load .env if present (optional). Do not commit secrets.
        // NOTE: DotNetEnv loads from current working directory; tests typically run with project output dir.
        // We still call it so local runs with copied .env.example/.env work.
        Env.Load(ignoreIfMissing: true);
    }

    private static string Get(string key, string defaultValue)
    {
        var env = Environment.GetEnvironmentVariable(key);
        if (!string.IsNullOrWhiteSpace(env)) return env.Trim();

        return defaultValue;
    }

    // PUBLIC_INTERFACE
    public static string BaseUrl => Get("BASE_URL", "https://opensource-demo.orangehrmlive.com");

    // PUBLIC_INTERFACE
    public static string ApiBasePath => Get("API_BASE_PATH", "/api/v2");

    // PUBLIC_INTERFACE
    public static string AdminUsername => Get("ADMIN_USERNAME", "__ADMIN_USERNAME__");

    // PUBLIC_INTERFACE
    public static string AdminPassword => Get("ADMIN_PASSWORD", "__ADMIN_PASSWORD__");

    // PUBLIC_INTERFACE
    public static string EssUsername => Get("ESS_USERNAME", "__ESS_USERNAME__");

    // PUBLIC_INTERFACE
    public static string EssPassword => Get("ESS_PASSWORD", "__ESS_PASSWORD__");

    // PUBLIC_INTERFACE
    public static string? ApiBearerToken
    {
        get
        {
            var token = Environment.GetEnvironmentVariable("API_BEARER_TOKEN");
            return string.IsNullOrWhiteSpace(token) ? null : token.Trim();
        }
    }
}
