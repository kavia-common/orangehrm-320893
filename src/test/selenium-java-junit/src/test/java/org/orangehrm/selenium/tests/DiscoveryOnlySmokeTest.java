package org.orangehrm.selenium.tests;

import org.junit.jupiter.api.Test;
import org.orangehrm.selenium.config.RunMode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke test to confirm the suite is discoverable and runnable without a real browser.
 */
public final class DiscoveryOnlySmokeTest {

    @Test
    void discoveryOnlyDefaultIsTrueUnlessOverridden() {
        // This assertion documents the intended CI-safe behavior.
        assertTrue(RunMode.isDiscoveryOnly(), "Expected discovery-only mode to be enabled by default.");
    }
}
