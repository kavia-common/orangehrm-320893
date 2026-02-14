package com.orangehrm.api.support;

import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * PUBLIC_INTERFACE
 * Small API wrapper to keep tests readable and consistent.
 */
public final class OrangeHrmApi {

    private OrangeHrmApi() {
        // utility
    }

    /**
     * PUBLIC_INTERFACE
     * Build a REST Assured request spec with authentication applied.
     *
     * Prefers bearer token if provided; otherwise uses cookies.
     *
     * @param cookies session cookies (may be null if bearer token is used)
     * @return request specification
     */
    public static RequestSpecification authed(Cookies cookies) {
        RequestSpecification req = given()
                .accept("application/json")
                .contentType("application/json");

        var bearerOpt = AuthClient.bearerToken();
        if (bearerOpt.isPresent()) {
            return req.auth().oauth2(bearerOpt.get());
        }

        if (cookies == null) {
            throw new IllegalArgumentException("Cookies are required when bearer token is not configured.");
        }
        return req.cookies(cookies);
    }

    /**
     * PUBLIC_INTERFACE
     * Unauthenticated request for negative tests.
     */
    public static RequestSpecification anon() {
        return given()
                .accept("application/json")
                .contentType("application/json");
    }

    /**
     * PUBLIC_INTERFACE
     * Helper to build an API v2 URL from relative resource path (e.g., "/admin/job-titles").
     */
    public static String v2(String resourcePath) {
        String basePath = TestConfig.apiBasePath();
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
        }
        return basePath + resourcePath;
    }
}
