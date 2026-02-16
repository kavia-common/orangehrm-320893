package org.orangehrm.selenium.config;

/**
 * OrangeHRM UI test configuration loaded from environment variables.
 *
 * Aligns with Node.js Selenium suite env vars found in: src/test/selenium/lib/config.js
 */
public final class OrangeHrmConfig {
    private final String baseUrl;
    private final String adminUsername;
    private final String adminPassword;
    private final String browser;
    private final boolean headless;

    private OrangeHrmConfig(String baseUrl, String adminUsername, String adminPassword, String browser, boolean headless) {
        this.baseUrl = baseUrl;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.browser = browser;
        this.headless = headless;
    }

    // PUBLIC_INTERFACE
    public static OrangeHrmConfig fromEnv(boolean requireSecrets) {
        /**
         * Load configuration from environment variables.
         *
         * If requireSecrets is false (used for discovery-only), missing variables do not throw.
         *
         * @param requireSecrets if true, throws if required env vars are missing
         * @return OrangeHrmConfig instance
         */
        String baseUrl = getenv("ORANGEHRM_BASE_URL");
        String adminUsername = getenv("ORANGEHRM_ADMIN_USERNAME");
        String adminPassword = getenv("ORANGEHRM_ADMIN_PASSWORD");

        if (requireSecrets) {
            requireNonBlank(baseUrl, "ORANGEHRM_BASE_URL");
            requireNonBlank(adminUsername, "ORANGEHRM_ADMIN_USERNAME");
            requireNonBlank(adminPassword, "ORANGEHRM_ADMIN_PASSWORD");
        }

        String browser = getenvOrDefault("BROWSER", "chrome").toLowerCase();
        boolean headless = parseBoolean(getenvOrDefault("HEADLESS", ""));

        return new OrangeHrmConfig(baseUrl, adminUsername, adminPassword, browser, headless);
    }

    // PUBLIC_INTERFACE
    public String getBaseUrl() {
        /** @return OrangeHRM base URL */
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

    // PUBLIC_INTERFACE
    public String getBrowser() {
        /** @return browser name (chrome|firefox) */
        return browser;
    }

    // PUBLIC_INTERFACE
    public boolean isHeadless() {
        /** @return whether headless is enabled */
        return headless;
    }

    private static String getenv(String name) {
        return System.getenv(name);
    }

    private static String getenvOrDefault(String name, String defaultValue) {
        String v = getenv(name);
        if (v == null) return defaultValue;
        return v;
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
