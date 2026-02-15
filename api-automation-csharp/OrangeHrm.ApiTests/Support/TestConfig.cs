using System;
using System.IO;
using DotNetEnv;

namespace OrangeHrm.ApiTests.Support;

public static class TestConfig
{
    static TestConfig()
    {
        // Load .env if present (optional). Do not commit secrets.
        //
        // DotNetEnv 3.0.0 does not support `ignoreIfMissing`, so we implement the
        // optional behavior ourselves by checking file existence first.
        //
        // NOTE: DotNetEnv loads from current working directory; tests typically run with project output dir.
        // This still enables local runs with a copied .env.example/.env.
        var envPath = Path.Combine(Directory.GetCurrentDirectory(), ".env");
        if (File.Exists(envPath))
        {
            Env.Load(envPath);
        }
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
    public static bool MockDemoMode
    {
        get
        {
            // Enabled via env var/flag to mirror the Java suite’s behavior.
            // Accepts: "1", "true", "yes", "on" (case-insensitive).
            var raw = Environment.GetEnvironmentVariable("MOCK_DEMO_MODE");
            if (string.IsNullOrWhiteSpace(raw)) return false;

            var v = raw.Trim().ToLowerInvariant();
            return v is "1" or "true" or "yes" or "on";
        }
    }

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
