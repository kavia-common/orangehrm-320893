using System;

namespace OrangeHrm.SeleniumNUnit.Config
{
    /// <summary>
    /// Configuration for API automation tests.
    ///
    /// Defaults to discovery-only mode (CI safe), which must not perform outbound HTTP calls.
    /// </summary>
    public sealed class OrangeHrmApiSettings
    {
        public bool DiscoveryOnly { get; }
        public string? ApiBaseUrl { get; }
        public string? AdminUsername { get; }
        public string? AdminPassword { get; }

        private OrangeHrmApiSettings(bool discoveryOnly, string? apiBaseUrl, string? adminUsername, string? adminPassword)
        {
            DiscoveryOnly = discoveryOnly;
            ApiBaseUrl = apiBaseUrl;
            AdminUsername = adminUsername;
            AdminPassword = adminPassword;
        }

        // PUBLIC_INTERFACE
        public static OrangeHrmApiSettings Load()
        {
            /// <summary>
            /// Load API settings from environment variables.
            ///
            /// Env vars:
            /// - ORANGEHRM_API_DISCOVERY_ONLY (default true)
            /// - ORANGEHRM_API_BASE_URL (example: http://localhost/web/index.php)
            /// - ORANGEHRM_ADMIN_USERNAME
            /// - ORANGEHRM_ADMIN_PASSWORD
            /// </summary>
            var discoveryOnly = ReadBoolEnv("ORANGEHRM_API_DISCOVERY_ONLY", defaultValue: true);

            var apiBaseUrl = Environment.GetEnvironmentVariable("ORANGEHRM_API_BASE_URL");
            var adminUsername = Environment.GetEnvironmentVariable("ORANGEHRM_ADMIN_USERNAME");
            var adminPassword = Environment.GetEnvironmentVariable("ORANGEHRM_ADMIN_PASSWORD");

            return new OrangeHrmApiSettings(
                discoveryOnly: discoveryOnly,
                apiBaseUrl: apiBaseUrl,
                adminUsername: adminUsername,
                adminPassword: adminPassword
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
