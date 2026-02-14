using System;
using System.IO;
using DotNetEnv;
using NUnit.Framework;

namespace OrangeHrm.UiTests.Support;

public static class TestConfig
{
    static TestConfig()
    {
        // Load .env if present (optional). Do not commit secrets.
        //
        // DotNetEnv 3.0.0 does not support `ignoreIfMissing`, so we implement the
        // optional behavior ourselves by checking file existence first.
        var envPath = Path.Combine(Directory.GetCurrentDirectory(), ".env");
        if (File.Exists(envPath))
        {
            Env.Load(envPath);
        }
    }

    private static string Get(string key, string defaultValue)
    {
        // 1) environment variable
        var env = Environment.GetEnvironmentVariable(key);
        if (!string.IsNullOrWhiteSpace(env)) return env.Trim();

        // 2) runsettings parameter (NUnit will expose via TestContext.Parameters)
        var param = TestContext.Parameters.Get(key);
        if (!string.IsNullOrWhiteSpace(param)) return param.Trim();

        return defaultValue;
    }

    // PUBLIC_INTERFACE
    public static string BaseUrl => Get("BASE_URL", "https://opensource-demo.orangehrmlive.com");

    // PUBLIC_INTERFACE
    public static string Browser => Get("BROWSER", "chrome").ToLowerInvariant();

    // PUBLIC_INTERFACE
    public static bool Headless => bool.TryParse(Get("HEADLESS", "false"), out var v) && v;

    // PUBLIC_INTERFACE
    public static string ChromeBinary => Get("CHROME_BINARY", Get("CHROME_BIN", ""));

    // PUBLIC_INTERFACE
    public static string AdminUsername => Get("ADMIN_USERNAME", "__ADMIN_USERNAME__");

    // PUBLIC_INTERFACE
    public static string AdminPassword => Get("ADMIN_PASSWORD", "__ADMIN_PASSWORD__");

    // PUBLIC_INTERFACE
    public static string EssUsername => Get("ESS_USERNAME", "__ESS_USERNAME__");

    // PUBLIC_INTERFACE
    public static string EssPassword => Get("ESS_PASSWORD", "__ESS_PASSWORD__");
}
