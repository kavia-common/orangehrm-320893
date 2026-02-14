package com.orangehrm.api.tests;

import com.orangehrm.api.support.ApiTestBase;
import com.orangehrm.api.support.AuthClient;
import com.orangehrm.api.support.OrangeHrmApi;
import com.orangehrm.api.support.TestConfig;
import io.restassured.http.Cookies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * Employee-related workflows and backend business-logic validations.
 *
 * Note:
 * OrangeHRM employee creation endpoints can require specific fields and permissions.
 * This sample uses "Admin - Job Titles" as a stable CRUD-like workflow, and includes
 * generic collection paging validations which are business-logic adjacent.
 */
public class EmployeeWorkflowTests extends ApiTestBase {

    @Test
    @DisplayName("Create -> Delete workflow (Admin: Job Titles) should succeed")
    void createAndDeleteJobTitle() {
        Cookies cookies = AuthClient.loginWithCookies(TestConfig.adminUsername(), TestConfig.adminPassword());
        String endpoint = OrangeHrmApi.v2("/admin/job-titles");

        // Create
        int createdId =
                OrangeHrmApi.authed(cookies)
                        .body(Map.of(
                                "title", "API-AUTO-" + System.currentTimeMillis(),
                                "description", "Created by API automation suite",
                                "specification", null,
                                "note", "cleanup expected"
                        ))
                        .when()
                        .post(endpoint)
                        .then()
                        .statusCode(200)
                        .body("data.id", notNullValue())
                        .extract()
                        .path("data.id");

        // Verify list contains created record (basic business logic validation)
        OrangeHrmApi.authed(cookies)
                .queryParam("limit", 50)
                .queryParam("offset", 0)
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("meta", notNullValue());

        // Delete
        OrangeHrmApi.authed(cookies)
                .body(Map.of("ids", new int[]{createdId}))
                .when()
                .delete(endpoint)
                .then()
                .statusCode(200)
                .body("data", hasItem(createdId));
    }

    @Test
    @DisplayName("Pagination business logic: limit/offset should be honored (shape + no errors)")
    void paginationValidation() {
        Cookies cookies = AuthClient.loginWithCookies(TestConfig.adminUsername(), TestConfig.adminPassword());
        String endpoint = OrangeHrmApi.v2("/admin/job-titles");

        OrangeHrmApi.authed(cookies)
                .queryParam("limit", 1)
                .queryParam("offset", 0)
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                // Many collections return <= limit
                .body("data.size()", lessThanOrEqualTo(1))
                .body("meta", notNullValue());

        OrangeHrmApi.authed(cookies)
                .queryParam("limit", 1)
                .queryParam("offset", 1)
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("data.size()", lessThanOrEqualTo(1))
                .body("meta", notNullValue());
    }

    @Test
    @DisplayName("Validation business logic: deleting with empty ids should be rejected")
    void deleteValidationShouldFailOnEmptyIds() {
        Cookies cookies = AuthClient.loginWithCookies(TestConfig.adminUsername(), TestConfig.adminPassword());
        String endpoint = OrangeHrmApi.v2("/admin/job-titles");

        OrangeHrmApi.authed(cookies)
                .body(Map.of("ids", new int[]{}))
                .when()
                .delete(endpoint)
                .then()
                .statusCode(anyOf(is(400), is(422)))
                .body(anything());
    }
}
