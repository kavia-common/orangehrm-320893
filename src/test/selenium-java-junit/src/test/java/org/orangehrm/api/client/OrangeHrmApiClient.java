package org.orangehrm.api.client;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.orangehrm.api.config.OrangeHrmApiConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Small API client wrapper for OrangeHRM API automation tests.
 *
 * Auth strategy:
 * - Uses the functional testing helper endpoint:
 *     POST /functional-testing/auth/validate
 *   which sets authentication state in the session.
 * - Subsequent API calls are executed with the session cookies returned by the validate call.
 */
public final class OrangeHrmApiClient {

    private final OrangeHrmApiConfig config;
    private Map<String, String> sessionCookies = new HashMap<>();

    public OrangeHrmApiClient(OrangeHrmApiConfig config) {
        this.config = config;
    }

    // PUBLIC_INTERFACE
    public void authenticateAsAdmin() {
        /**
         * Authenticate using functional-testing helper endpoint, capturing cookies for later requests.
         *
         * This mirrors Cypress command:
         *   POST /functional-testing/auth/validate { username, password }
         */
        Map<String, Object> body = new HashMap<>();
        body.put("username", config.getAdminUsername());
        body.put("password", config.getAdminPassword());

        var response =
                RestAssured.given()
                        .spec(baseSpec())
                        .contentType(ContentType.JSON)
                        .body(body)
                        .when()
                        .post("/functional-testing/auth/validate")
                        .then()
                        .extract()
                        .response();

        // Capture cookies regardless of success; tests will assert success.
        this.sessionCookies = response.getCookies();
    }

    // PUBLIC_INTERFACE
    public RequestSpecification baseSpec() {
        /** @return base request spec with baseUri set to ORANGEHRM_API_BASE_URL */
        return new RequestSpecBuilder()
                .setBaseUri(config.getBaseUrl())
                .setAccept(ContentType.JSON)
                .build();
    }

    // PUBLIC_INTERFACE
    public RequestSpecification authedSpec() {
        /**
         * @return request spec with session cookies applied.
         *         Call authenticateAsAdmin() first.
         */
        return new RequestSpecBuilder()
                .setBaseUri(config.getBaseUrl())
                .setAccept(ContentType.JSON)
                .addCookies(sessionCookies)
                .build();
    }
}
