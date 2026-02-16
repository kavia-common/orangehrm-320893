package org.orangehrm.selenium.config;

/**
 * Determines whether tests should run in discovery-only mode.
 *
 * Default is discovery-only to avoid launching browsers in CI during validation.
 */
public final class RunMode {
    private RunMode() {}

    // PUBLIC_INTERFACE
    public static boolean isDiscoveryOnly() {
        /**
         * @return true if discovery-only mode is enabled (default true)
         */
        String prop = System.getProperty("orangehrm.discoveryOnly");
        if (prop == null) {
            // Default to discovery-only for safety.
            return true;
        }
        String s = prop.trim().toLowerCase();
        return "true".equals(s) || "1".equals(s) || "yes".equals(s) || "y".equals(s);
    }
}
