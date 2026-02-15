package com.orangehrm.api.support;

/**
 * PUBLIC_INTERFACE
 * Central toggle for API test "mock/demo mode".
 *
 * When enabled, the automation suite must not make any real HTTP calls and instead return
 * deterministic, mocked responses that simulate a successful OrangeHRM backend.
 *
 * Enable via:
 *  - JVM system property: -DMOCK_MODE=true
 *  - Environment variable: MOCK_MODE=true
 */
public final class MockMode {

    private MockMode() {
        // utility
    }

    // PUBLIC_INTERFACE
    public static boolean enabled() {
        /** This is a public function. */
        String sys = System.getProperty("MOCK_MODE");
        if (sys != null && !sys.isBlank()) {
            return parseBoolean(sys);
        }
        String env = System.getenv("MOCK_MODE");
        if (env != null && !env.isBlank()) {
            return parseBoolean(env);
        }
        return false;
    }

    private static boolean parseBoolean(String v) {
        String t = v.trim().toLowerCase();
        return t.equals("1") || t.equals("true") || t.equals("yes") || t.equals("y") || t.equals("on");
    }
}
