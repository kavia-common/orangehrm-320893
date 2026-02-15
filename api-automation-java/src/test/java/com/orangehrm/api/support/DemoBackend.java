package com.orangehrm.api.support;

import io.restassured.http.Cookies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demo-only in-memory backend simulation used when MOCK_MODE is enabled.
 *
 * Intentionally minimal: it only simulates the specific flows used by the suite:
 * - cookie-based login (returns non-empty cookies)
 * - optional bearer token generation (returns a stable token)
 * - job-title CRUD-like workflow with deterministic 200 OK responses
 * - validation error responses for known negative cases
 */
final class DemoBackend {

    private static final AtomicInteger ID_SEQ = new AtomicInteger(1000);

    // In-memory "job titles" list. Each element: {id, title, description, note}
    private static final List<Map<String, Object>> JOB_TITLES = Collections.synchronizedList(new ArrayList<>());

    static {
        // Seed with a couple entries so pagination tests are meaningful.
        JOB_TITLES.add(new LinkedHashMap<>(Map.of(
                "id", 1,
                "title", "Software Engineer",
                "description", "Seeded demo record",
                "note", ""
        )));
        JOB_TITLES.add(new LinkedHashMap<>(Map.of(
                "id", 2,
                "title", "QA Engineer",
                "description", "Seeded demo record",
                "note", ""
        )));
    }

    private DemoBackend() {
        // utility
    }

    static Cookies demoCookiesFor(String username) {
        // RestAssured Cookies must be non-empty because existing code validates that.
        // Use stable name/value so logs remain deterministic.
        return new Cookies(
                new io.restassured.http.Cookie.Builder(
                        "orangehrm_session",
                        "demo-session-" + (username == null ? "user" : username)
                )
                        .setSecured(false)
                        .setHttpOnly(false)
                        .build()
        );
    }

    static String demoBearerTokenFor(String username) {
        return "demo-token-" + (username == null ? "user" : username);
    }

    static Map<String, Object> listJobTitles(Integer limit, Integer offset) {
        int safeLimit = limit == null ? 50 : Math.max(0, limit);
        int safeOffset = offset == null ? 0 : Math.max(0, offset);

        List<Map<String, Object>> snapshot;
        synchronized (JOB_TITLES) {
            snapshot = new ArrayList<>(JOB_TITLES);
        }

        int from = Math.min(safeOffset, snapshot.size());
        int to = Math.min(from + safeLimit, snapshot.size());

        List<Map<String, Object>> page = snapshot.subList(from, to);

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("total", snapshot.size());
        meta.put("limit", safeLimit);
        meta.put("offset", safeOffset);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("data", page);
        body.put("meta", meta);
        return body;
    }

    static Map<String, Object> createJobTitle(Map<String, Object> payload) {
        int id = ID_SEQ.incrementAndGet();

        String title = payload == null ? null : asString(payload.get("title"));
        String description = payload == null ? null : asString(payload.get("description"));
        String note = payload == null ? null : asString(payload.get("note"));

        Map<String, Object> rec = new LinkedHashMap<>();
        rec.put("id", id);
        rec.put("title", title == null ? ("Untitled-" + id) : title);
        rec.put("description", description == null ? "" : description);
        rec.put("note", note == null ? "" : note);

        JOB_TITLES.add(rec);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("data", rec);
        body.put("meta", new LinkedHashMap<>());
        return body;
    }

    static Map<String, Object> deleteJobTitles(int[] ids) {
        if (ids == null || ids.length == 0) {
            // Simulate a typical API validation error shape (suite only asserts status code and "anything()").
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("error", "Validation error");
            err.put("message", "ids must not be empty");
            return err;
        }

        List<Integer> deleted = new ArrayList<>();
        synchronized (JOB_TITLES) {
            for (int id : ids) {
                boolean removed = JOB_TITLES.removeIf(r -> asInt(r.get("id")) == id);
                if (removed) {
                    deleted.add(id);
                }
            }
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("data", deleted);
        body.put("meta", new LinkedHashMap<>());
        return body;
    }

    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    private static int asInt(Object v) {
        if (v instanceof Number n) {
            return n.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(v));
        } catch (Exception e) {
            return 0;
        }
    }
}
