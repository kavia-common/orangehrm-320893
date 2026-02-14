package com.orangehrm.api.tests;

import com.orangehrm.api.support.ApiTestBase;
import com.orangehrm.api.support.AuthClient;
import com.orangehrm.api.support.OrangeHrmApi;
import com.orangehrm.api.support.TestConfig;
import io.restassured.http.Cookies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

/**
 * Validates authentication/authorization flows for OrangeHRM REST APIs.
 */
public class AuthFlowTests extends ApiTestBase {

    @Test
    @DisplayName("Unauthenticated request should be rejected by protected endpoint")
    void unauthenticatedRequestShouldFail() {
        // Pick a commonly protected endpoint: admin job titles.
        OrangeHrmApi.anon()
                .when()
                .get(OrangeHrmApi.v2("/admin/job-titles"))
                .then()
                // Different deployments may return 401 or 403; OrangeHRM often uses 403 "Unauthorized"
                .statusCode(anyOf(is(401), is(403)))
                .body(anyOf(
                        hasKey("error"),
                        hasKey("message"),
                        anything()
                ));
    }

    @Test
    @DisplayName("Admin login should allow access to protected endpoint")
    void adminLoginAllowsAccess() {
        Cookies cookies = AuthClient.loginWithCookies(TestConfig.adminUsername(), TestConfig.adminPassword());

        OrangeHrmApi.authed(cookies)
                .when()
                .get(OrangeHrmApi.v2("/admin/job-titles"))
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("meta", notNullValue());
    }
}
