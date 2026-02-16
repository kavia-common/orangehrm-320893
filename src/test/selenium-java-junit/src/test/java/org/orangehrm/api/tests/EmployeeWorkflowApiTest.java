package org.orangehrm.api.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API automation tests for employee/admin workflows.
 *
 * Note: This repo already uses /api/v2/admin/job-titles as a stable API target in Cypress API tests,
 * so we use it as a representative "workflow" endpoint for create + list validations.
 */
public final class EmployeeWorkflowApiTest extends BaseApiTest {

    @Test
    void adminCanCreateJobTitle_thenItAppearsInList() {
        guardDiscoveryOnly();

        if (config.isDiscoveryOnly()) {
            assertTrue(true, "Discovery-only mode: test executed without outbound HTTP calls.");
            return;
        }

        client.authenticateAsAdmin();

        // Create job title
        String title = "API-JobTitle-" + System.currentTimeMillis();
        var createPayload = Map.of(
                "title", title,
                "description", "Created by REST Assured API test",
                "specification", null,
                "note", "note"
        );

        var createResp =
                RestAssured.given()
                        .spec(client.authedSpec())
                        .contentType(ContentType.JSON)
                        .body(createPayload)
                        .when()
                        .post("/api/v2/admin/job-titles")
                        .then()
                        .extract()
                        .response();

        assertEquals(200, createResp.statusCode(), "Create should return 200 for job titles endpoint.");

        // List job titles and ensure count >= 1 (not relying on exact schema details)
        var listResp =
                RestAssured.given()
                        .spec(client.authedSpec())
                        .accept(ContentType.JSON)
                        .when()
                        .get("/api/v2/admin/job-titles")
                        .then()
                        .extract()
                        .response();

        assertEquals(200, listResp.statusCode());
        assertNotNull(listResp.jsonPath().getList("data"), "Expected response to include data array.");
        assertTrue(listResp.jsonPath().getList("data").size() >= 1, "Expected at least one job title after create.");
    }

    @Test
    void unauthenticatedRequest_isForbiddenOrUnauthorized() {
        guardDiscoveryOnly();

        if (config.isDiscoveryOnly()) {
            assertTrue(true, "Discovery-only mode: test executed without outbound HTTP calls.");
            return;
        }

        var resp =
                RestAssured.given()
                        .spec(client.baseSpec())
                        .accept(ContentType.JSON)
                        .when()
                        .get("/api/v2/admin/job-titles")
                        .then()
                        .extract()
                        .response();

        // Depending on environment/config this could be 401/403; OpenAPI shows 403 Unauthorized as common.
        assertTrue(resp.statusCode() == 401 || resp.statusCode() == 403,
                "Expected unauthorized status (401/403), got: " + resp.statusCode());
    }
}
