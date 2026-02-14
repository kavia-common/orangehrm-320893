package com.orangehrm.support;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * PUBLIC_INTERFACE
 * Centralized configuration utility.
 *
 * Reads configuration in this order:
 *  1) JVM system properties (e.g., -Dbrowser=firefox)
 *  2) .env file (loaded from project root)
 *  3) .env.example placeholders / defaults
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
    public static String browser() {
        return get("browser", get("BROWSER", "chrome"));
    }

    // PUBLIC_INTERFACE
    public static boolean headless() {
        return Boolean.parseBoolean(get("HEADLESS", "false"));
    }

    // PUBLIC_INTERFACE
    public static String adminUsername() {
        return get("ADMIN_USERNAME", "__ADMIN_USERNAME__");
    }

    // PUBLIC_INTERFACE
    public static String adminPassword() {
        return get("ADMIN_PASSWORD", "__ADMIN_PASSWORD__");
    }

    // PUBLIC_INTERFACE
    public static String essUsername() {
        return get("ESS_USERNAME", "__ESS_USERNAME__");
    }

    // PUBLIC_INTERFACE
    public static String essPassword() {
        return get("ESS_PASSWORD", "__ESS_PASSWORD__");
    }
}
