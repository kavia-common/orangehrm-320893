using System;

namespace OrangeHrm.SeleniumNUnit.Config
{
    /// <summary>
    /// Central configuration loader for the OrangeHRM Selenium NUnit skeleton.
    ///
    /// IMPORTANT:
    /// - Defaults to discovery-only mode (CI safe).
    /// - Only reads real browser settings when discovery-only is disabled.
    /// </summary>
    public sealed class OrangeHrmSettings
    {
        /// <summary>
        /// If true, tests must not attempt to create WebDriver / launch browsers.
        /// </summary>
        public bool DiscoveryOnly { get; }

        /// <summary>
        /// OrangeHRM base URL (only required in real browser mode).
        /// </summary>
        public string? BaseUrl { get; }

        /// <summary>
        /// Admin username (only required in real browser mode).
        /// </summary>
        public string? AdminUsername { get; }

        /// <summary>
        /// Admin password (only required in real browser mode).
        /// </summary>
        public string? AdminPassword { get; }

        /// <summary>
        /// Browser name ("chrome", "firefox", or "edge"). Default: "chrome".
        /// </summary>
        public string Browser { get; }

        /// <summary>
        /// Run headless if true. Default: false.
        /// </summary>
        public bool Headless { get; }

        /// <summary>
        /// Optional Selenium Grid RemoteWebDriver URL (e.g. http://localhost:4444/wd/hub).
        /// If set, the driver factory will create remote sessions instead of local drivers.
        /// </summary>
        public string? SeleniumRemoteUrl { get; }

        private OrangeHrmSettings(
            bool discoveryOnly,
            string? baseUrl,
            string? adminUsername,
            string? adminPassword,
            string browser,
            bool headless,
            string? seleniumRemoteUrl)
        {
            DiscoveryOnly = discoveryOnly;
            BaseUrl = baseUrl;
            AdminUsername = adminUsername;
            AdminPassword = adminPassword;
            Browser = browser;
            Headless = headless;
            SeleniumRemoteUrl = seleniumRemoteUrl;
        }

        // PUBLIC_INTERFACE
        /// <summary>
        /// Load settings from environment variables and MSBuild property.
        ///
        /// Discovery-only:
        /// - Default true via MSBuild property OrangeHrmDiscoveryOnly (see csproj).
        /// - Can also be overridden by env var ORANGEHRM_DISCOVERY_ONLY for convenience.
        /// </summary>
        public static OrangeHrmSettings Load()
        {
            // Primary mechanism: MSBuild property injected at build time.
            // In test assemblies, MSBuild properties are not directly accessible as constants,
            // but we can pass it via environment variable in CI if needed.
            // Here we support both:
            //  - ORANGEHRM_DISCOVERY_ONLY env var
            //  - Fallback to "true" to remain safe by default
            var discoveryOnly = ReadBoolEnv("ORANGEHRM_DISCOVERY_ONLY", defaultValue: true);

            var browser = (Environment.GetEnvironmentVariable("BROWSER") ?? "chrome").Trim().ToLowerInvariant();
            var headless = ReadBoolEnv("HEADLESS", defaultValue: false);

            var baseUrl = Environment.GetEnvironmentVariable("ORANGEHRM_BASE_URL");
            var adminUsername = Environment.GetEnvironmentVariable("ORANGEHRM_ADMIN_USERNAME");
            var adminPassword = Environment.GetEnvironmentVariable("ORANGEHRM_ADMIN_PASSWORD");

            var seleniumRemoteUrl = Environment.GetEnvironmentVariable("SELENIUM_REMOTE_URL");

            return new OrangeHrmSettings(
                discoveryOnly: discoveryOnly,
                baseUrl: baseUrl,
                adminUsername: adminUsername,
                adminPassword: adminPassword,
                browser: string.IsNullOrWhiteSpace(browser) ? "chrome" : browser,
                headless: headless,
                seleniumRemoteUrl: seleniumRemoteUrl
            );
        }

        private static bool ReadBoolEnv(string key, bool defaultValue)
        {
            var raw = Environment.GetEnvironmentVariable(key);
            if (string.IsNullOrWhiteSpace(raw))
            {
                return defaultValue;
            }

            raw = raw.Trim().ToLowerInvariant();

            return raw switch
            {
                "1" => true,
                "true" => true,
                "yes" => true,
                "y" => true,

                "0" => false,
                "false" => false,
                "no" => false,
                "n" => false,

                _ => defaultValue
            };
        }
    }
}
