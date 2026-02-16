package org.orangehrm.api.config;

/**
 * Configuration for API automation tests.
 *
 * Defaults to discovery-only behavior (no outbound HTTP calls) unless explicitly enabled.
 *
 * Env vars used:
 * - ORANGEHRM_API_BASE_URL (example: http://localhost/web/index.php)
 * - ORANGEHRM_ADMIN_USERNAME
 * - ORANGEHRM_ADMIN_PASSWORD
 * - ORANGEHRM_API_DISCOVERY_ONLY (true/false, default true)
 */
public final class OrangeHrmApiConfig {

    private final boolean discoveryOnly;
    private final String baseUrl;
    private final String adminUsername;
    private final String adminPassword;

    private OrangeHrmApiConfig(boolean discoveryOnly, String baseUrl, String adminUsername, String adminPassword) {
        this.discoveryOnly = discoveryOnly;
        this.baseUrl = baseUrl;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    // PUBLIC_INTERFACE
    public static OrangeHrmApiConfig fromEnv(boolean requireSecrets) {
        /**
         * Load API configuration from environment variables.
         *
         * @param requireSecrets if true, throws if required env vars are missing
         * @return config instance
         */
        boolean discoveryOnly = parseBoolean(getenvOrDefault("ORANGEHRM_API_DISCOVERY_ONLY", "true"));

        String baseUrl = getenv("ORANGEHRM_API_BASE_URL");
        String adminUsername = getenv("ORANGEHRM_ADMIN_USERNAME");
        String adminPassword = getenv("ORANGEHRM_ADMIN_PASSWORD");

        // If not requiring secrets (e.g., discovery-only), allow null/empty values.
        if (requireSecrets) {
            requireNonBlank(baseUrl, "ORANGEHRM_API_BASE_URL");
            requireNonBlank(adminUsername, "ORANGEHRM_ADMIN_USERNAME");
            requireNonBlank(adminPassword, "ORANGEHRM_ADMIN_PASSWORD");
        }

        return new OrangeHrmApiConfig(discoveryOnly, baseUrl, adminUsername, adminPassword);
    }

    // PUBLIC_INTERFACE
    public boolean isDiscoveryOnly() {
        /** @return true if discovery-only mode is enabled (default) */
        return discoveryOnly;
    }

    // PUBLIC_INTERFACE
    public String getBaseUrl() {
        /** @return base URL like http://host/web/index.php */
        return baseUrl;
    }

    // PUBLIC_INTERFACE
    public String getAdminUsername() {
        /** @return admin username */
        return adminUsername;
    }

    // PUBLIC_INTERFACE
    public String getAdminPassword() {
        /** @return admin password */
        return adminPassword;
    }

    private static String getenv(String name) {
        return System.getenv(name);
    }

    private static String getenvOrDefault(String name, String defaultValue) {
        String v = getenv(name);
        return v == null ? defaultValue : v;
    }

    private static boolean parseBoolean(String v) {
        String s = (v == null ? "" : v).trim().toLowerCase();
        return "true".equals(s) || "1".equals(s) || "yes".equals(s) || "y".equals(s);
    }

    private static void requireNonBlank(String value, String envName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing env " + envName);
        }
    }
}
