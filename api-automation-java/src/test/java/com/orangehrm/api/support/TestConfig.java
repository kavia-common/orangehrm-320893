package com.orangehrm.api.support;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * PUBLIC_INTERFACE
 * Centralized configuration utility for API tests.
 *
 * Reads configuration in this order:
 *  1) JVM system properties (e.g., -DBASE_URL=http://localhost:8080)
 *  2) .env file (loaded from api-automation-java/)
 *  3) OS environment variables
 *  4) defaults / placeholders
 *
 * Demo stabilization:
 *  - Provides stable defaults for the public OrangeHRM demo site so a single demo
 *    run executes expected-to-pass scenarios without a prior configuration step.
 */
public final class TestConfig {
    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private TestConfig() {
        // utility
    }

    private static String get(String key, String defaultValue) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys.trim();
        }
        String env = DOTENV.get(key);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        String os = System.getenv(key);
        if (os != null && !os.isBlank()) {
            return os.trim();
        }
        return defaultValue;
    }

    // PUBLIC_INTERFACE
    public static String baseUrl() {
        return get("BASE_URL", "https://opensource-demo.orangehrmlive.com");
    }

    // PUBLIC_INTERFACE
    public static String apiBasePath() {
        return get("API_BASE_PATH", "/api/v2");
    }

    // PUBLIC_INTERFACE
    public static String adminUsername() {
        // Public OrangeHRM demo default credentials
        return get("ADMIN_USERNAME", "Admin");
    }

    // PUBLIC_INTERFACE
    public static String adminPassword() {
        // Public OrangeHRM demo default credentials
        return get("ADMIN_PASSWORD", "admin123");
    }

    // PUBLIC_INTERFACE
    public static String essUsername() {
        // Not required for demo; default to Admin to avoid accidental misconfig failures.
        return get("ESS_USERNAME", "Admin");
    }

    // PUBLIC_INTERFACE
    public static String essPassword() {
        return get("ESS_PASSWORD", "admin123");
    }
}
