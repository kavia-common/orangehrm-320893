package com.orangehrm.api.support;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RestAssured filter that intercepts requests in MOCK_MODE and returns deterministic fake responses.
 *
 * This ensures the suite makes *zero* outbound HTTP calls in demo mode.
 */
final class DemoModeFilter implements Filter {

    static final DemoModeFilter INSTANCE = new DemoModeFilter();

    private DemoModeFilter() {
        // singleton
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        if (!MockMode.enabled()) {
            return ctx.next(requestSpec, responseSpec);
        }

        String method = requestSpec.getMethod();
        String uri = requestSpec.getURI();
        String path = safePath(uri);

        // Simulate auth/validate endpoint used by AuthClient.loginWithCookies
        if ("POST".equalsIgnoreCase(method) && path.endsWith("/web/index.php/auth/validate")) {
            return json(200, Map.of(
                    "message", "demo-login-ok"
            ));
        }

        // Simulate API v2 job titles endpoints used by current tests
        String v2Prefix = TestConfig.apiBasePath();
        if (path.startsWith(v2Prefix + "/admin/job-titles")) {
            boolean hasAuth =
                    (requestSpec.getHeaders() != null && requestSpec.getHeaders().hasHeaderWithName("Authorization"))
                            || (requestSpec.getCookies() != null && !requestSpec.getCookies().asList().isEmpty());

            // In demo mode we enforce "protected endpoint" behavior for anon() calls.
            if (!hasAuth) {
                return json(401, Map.of(
                        "error", "Unauthorized",
                        "message", "Missing auth"
                ));
            }

            if ("GET".equalsIgnoreCase(method)) {
                Integer limit = requestSpec.getQueryParams() != null ? tryInt(requestSpec.getQueryParams().get("limit")) : null;
                Integer offset = requestSpec.getQueryParams() != null ? tryInt(requestSpec.getQueryParams().get("offset")) : null;
                return json(200, DemoBackend.listJobTitles(limit, offset));
            }
            if ("POST".equalsIgnoreCase(method)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> payload = requestSpec.getBody() instanceof Map<?, ?>
                        ? (Map<String, Object>) requestSpec.getBody()
                        : null;
                return json(200, DemoBackend.createJobTitle(payload));
            }
            if ("DELETE".equalsIgnoreCase(method)) {
                int[] ids = extractIdsFromDeleteBody(requestSpec.getBody());
                Map<String, Object> body = DemoBackend.deleteJobTitles(ids);

                // When ids empty -> tests accept 400 or 422
                if (ids == null || ids.length == 0) {
                    return json(400, body);
                }
                return json(200, body);
            }

            return json(405, Map.of(
                    "error", "Method Not Allowed",
                    "message", "Unsupported method in demo backend",
                    "method", method
            ));
        }

        // Default: return 200 OK with empty-ish structure so unknown calls don't fail unexpectedly
        Map<String, Object> defaultBody = new LinkedHashMap<>();
        defaultBody.put("data", new LinkedHashMap<>());
        defaultBody.put("meta", new LinkedHashMap<>());
        defaultBody.put("message", "demo-mode-default-response");
        defaultBody.put("path", path);
        defaultBody.put("method", String.valueOf(method));
        return json(200, defaultBody);
    }

    private static String safePath(String uri) {
        try {
            URI u = URI.create(uri);
            String p = u.getPath();
            return p == null ? "" : p;
        } catch (Exception e) {
            // fallback: strip host if present
            int idx = uri.indexOf("://");
            if (idx >= 0) {
                int slash = uri.indexOf('/', idx + 3);
                return slash >= 0 ? uri.substring(slash) : "/";
            }
            return uri;
        }
    }

    private static Integer tryInt(Object v) {
        if (v == null) return null;
        try {
            return Integer.parseInt(String.valueOf(v));
        } catch (Exception e) {
            return null;
        }
    }

    private static int[] extractIdsFromDeleteBody(Object body) {
        if (!(body instanceof Map<?, ?> m)) {
            return null;
        }
        Object idsObj = m.get("ids");
        if (idsObj instanceof int[] arr) {
            return arr;
        }
        if (idsObj instanceof Integer[] arr) {
            int[] out = new int[arr.length];
            for (int i = 0; i < arr.length; i++) out[i] = arr[i] == null ? 0 : arr[i];
            return out;
        }
        if (idsObj instanceof Iterable<?> it) {
            // handle List<Integer>
            java.util.ArrayList<Integer> tmp = new java.util.ArrayList<>();
            for (Object o : it) {
                Integer iv = tryInt(o);
                if (iv != null) tmp.add(iv);
            }
            int[] out = new int[tmp.size()];
            for (int i = 0; i < tmp.size(); i++) out[i] = tmp.get(i);
            return out;
        }
        return null;
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static Response json(int statusCode, Map<String, Object> body) {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(body);

            RestAssuredResponseImpl resp = new RestAssuredResponseImpl();
            resp.setStatusCode(statusCode);

            // Important: RestAssured parses response bodies via a content-type header driven parser registry.
            // If we only set contentType but not headers, ContentParser may fail with getParser() NPE.
            resp.setHeaders(new io.restassured.http.Headers(
                    new io.restassured.http.Header("Content-Type", "application/json; charset=UTF-8")
            ));

            // RestAssured 5.4.x uses setContent(byte[]) for internal response impls.
            resp.setContent(json.getBytes(StandardCharsets.UTF_8));
            return resp;
        } catch (Exception e) {
            // ultra-safe fallback
            RestAssuredResponseImpl resp = new RestAssuredResponseImpl();
            resp.setStatusCode(statusCode);
            resp.setHeaders(new io.restassured.http.Headers(
                    new io.restassured.http.Header("Content-Type", "application/json; charset=UTF-8")
            ));
            resp.setContent(("{\"message\":\"demo-mode-json-serialization-failed\"}").getBytes(StandardCharsets.UTF_8));
            return resp;
        }
    }
}
