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
 *
 * Demo stabilization:
 *  - Provides stable default credentials for the public OrangeHRM demo site so a single run
 *    (with no local .env setup) executes only passing demo scenarios once.
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
    public static String chromeBinary() {
        /*
         * Optional: explicit Chrome/Chromium binary path (useful in containers).
         * Precedence is still handled by get():
         *  - System property
         *  - .env
         *  - OS environment variables
         */
        String val = get("CHROME_BINARY", "");
        if (val == null || val.isBlank()) {
            // Common alternative name used by some environments/images.
            val = get("CHROME_BIN", "");
        }
        if (val == null || val.isBlank()) {
            // Convenience JVM property name.
            val = get("chromeBinary", "");
        }
        return val == null ? "" : val.trim();
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
        /*
         * ESS credentials are not required for the demo run.
         * Default to the demo Admin account so optional login validations won't fail
         * when users run additional tags locally without configuring an ESS user.
         */
        return get("ESS_USERNAME", "Admin");
    }

    // PUBLIC_INTERFACE
    public static String essPassword() {
        return get("ESS_PASSWORD", "admin123");
    }
}
