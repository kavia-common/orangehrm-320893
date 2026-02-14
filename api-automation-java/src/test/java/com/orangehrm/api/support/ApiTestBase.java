package com.orangehrm.api.support;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.config.RestAssuredConfig.config;

/**
 * Base class for REST Assured test setup.
 */
public abstract class ApiTestBase {

    @BeforeAll
    static void configureRestAssured() {
        RestAssured.baseURI = TestConfig.baseUrl();

        // Keep this conservative: we don't want to dump credentials; logging is enabled only on validation failure.
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);

        RestAssured.config = config()
                .logConfig(LogConfig.logConfig().enablePrettyPrinting(true))
                .httpClient(HttpClientConfig.httpClientConfig()
                        // Avoid "NoHttpResponseException" flakiness by retrying idempotent requests if needed.
                        .setParam("http.connection.timeout", 15000)
                        .setParam("http.socket.timeout", 20000));
    }
}
