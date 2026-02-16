package org.orangehrm.api.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.orangehrm.allure.AllureMetadata;
import org.orangehrm.api.client.OrangeHrmApiClient;
import org.orangehrm.api.config.OrangeHrmApiConfig;

/**
 * Base class for API tests.
 *
 * IMPORTANT:
 * - In discovery-only mode (default), tests must not perform HTTP calls.
 * - Tests should still execute (not skipped); they should take a discovery-safe path.
 */
public abstract class BaseApiTest {

    protected OrangeHrmApiConfig config;
    protected OrangeHrmApiClient client;

    @BeforeEach
    void setUp() {
        // Require secrets only if we will actually execute HTTP calls.
        // Default ORANGEHRM_API_DISCOVERY_ONLY=true => requireSecrets=false
        this.config = OrangeHrmApiConfig.fromEnv(false);
        this.client = new OrangeHrmApiClient(config);

        // Allure metadata for aggregation. For API tests we still publish suite/language/framework.
        // We reuse UI config shape only for environment generation; browser will be "unknown".
        String resultsDir = System.getProperty("allure.results.directory", "./allure-results");
        AllureMetadata.applyCommonLabelsAndEnvironment("java-junit-api", resultsDir, null);
    }

    protected void guardDiscoveryOnly() {
        if (config.isDiscoveryOnly()) {
            return;
        }

        // In real mode ensure required env vars exist.
        this.config = OrangeHrmApiConfig.fromEnv(true);
        this.client = new OrangeHrmApiClient(config);

        Assertions.assertNotNull(config.getBaseUrl(), "ORANGEHRM_API_BASE_URL must be set when discovery-only is false.");
    }
}
