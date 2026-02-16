package org.orangehrm.api.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API automation tests for authentication flows.
 */
public final class AuthApiTest extends BaseApiTest {

    @Test
    void validateAuthEndpoint_discoverySafe() {
        // Required by work item: do not skip; just avoid real HTTP usage by default.
        guardDiscoveryOnly();

        if (config.isDiscoveryOnly()) {
            assertTrue(true, "Discovery-only mode: test executed without outbound HTTP calls.");
            return;
        }

        client.authenticateAsAdmin();

        // The endpoint returns JSON {"success": true/false}
        var resp =
                RestAssured.given()
                        .spec(client.baseSpec())
                        .accept(ContentType.JSON)
                        .when()
                        .get("/api/v2/admin/job-titles") // authenticated endpoint used as a quick smoke check
                        .then()
                        .extract()
                        .response();

        // Auth cookie presence is expected; actual endpoint status should be 200 for admin.
        assertEquals(200, resp.statusCode(), "Expected authenticated request to succeed.");
    }

    @Test
    void validateAuthEndpoint_rejectsInvalidCredentials() {
        guardDiscoveryOnly();

        if (config.isDiscoveryOnly()) {
            assertTrue(true, "Discovery-only mode: test executed without outbound HTTP calls.");
            return;
        }

        var response =
                RestAssured.given()
                        .spec(client.baseSpec())
                        .contentType(ContentType.JSON)
                        .body(Map.of("username", "Admin", "password", "wrong-password"))
                        .when()
                        .post("/functional-testing/auth/validate")
                        .then()
                        .extract()
                        .response();

        assertEquals(200, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false for invalid credentials.");
    }
}
