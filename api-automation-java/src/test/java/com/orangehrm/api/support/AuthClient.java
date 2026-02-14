package com.orangehrm.api.support;

import io.restassured.http.Cookies;
import io.restassured.response.Response;

import java.util.Optional;

import static io.restassured.RestAssured.given;

/**
 * PUBLIC_INTERFACE
 * Authentication utilities for API tests.
 *
 * OrangeHRM deployments may authenticate API requests using:
 * - Session cookies (web login), or
 * - Bearer tokens (OAuth2).
 *
 * This helper provides cookie-based login and a hook for bearer token usage.
 */
public final class AuthClient {

    private AuthClient() {
        // utility
    }

    /**
     * PUBLIC_INTERFACE
     * Perform a cookie-based login using the OrangeHRM web login endpoint.
     *
     * This approach is compatible with endpoints protected by the same session as the UI.
     *
     * @param username username
     * @param password password
     * @return session cookies to attach to subsequent API calls
     */
    public static Cookies loginWithCookies(String username, String password) {
        // Many OrangeHRM deployments accept form login at /web/index.php/auth/validate
        // The base URL in TestConfig points to host root; we include the /web/index.php prefix explicitly.
        Response res = given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("username", username)
                .formParam("password", password)
                .post("/web/index.php/auth/validate");

        // Successful login often redirects (302) to dashboard; accept 200 or 302.
        int status = res.getStatusCode();
        if (status != 200 && status != 302) {
            throw new IllegalStateException("Login failed. Status=" + status + ", body=" + safeBody(res));
        }

        Cookies cookies = res.getDetailedCookies();
        if (cookies == null || cookies.asList().isEmpty()) {
            throw new IllegalStateException("Login did not return any cookies; cannot authenticate subsequent API calls.");
        }
        return cookies;
    }

    /**
     * PUBLIC_INTERFACE
     * Optional bearer token accessor.
     *
     * If your environment uses OAuth2, set an env var (e.g., API_BEARER_TOKEN) and update this method accordingly.
     * Not used by default sample tests.
     *
     * @return bearer token if provided; empty otherwise
     */
    public static Optional<String> bearerToken() {
        String token = System.getProperty("API_BEARER_TOKEN");
        if (token == null || token.isBlank()) {
            token = System.getenv("API_BEARER_TOKEN");
        }
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(token.trim());
    }

    private static String safeBody(Response res) {
        try {
            String b = res.getBody() != null ? res.getBody().asString() : "";
            return b.length() > 500 ? b.substring(0, 500) + "..." : b;
        } catch (Exception e) {
            return "<unavailable>";
        }
    }
}
