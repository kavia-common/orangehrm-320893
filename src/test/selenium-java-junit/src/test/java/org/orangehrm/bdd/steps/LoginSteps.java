package org.orangehrm.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for login-related BDD scenarios.
 *
 * IMPORTANT:
 * - Cucumber dry-run should validate these bindings without executing the step bodies.
 * - If dry-run is disabled, these steps still do not launch a browser; the Selenium UI suite lives separately
 *   under org.orangehrm.selenium.* and is controlled via orangehrm.discoveryOnly.
 */
public class LoginSteps {

    private String baseUrl;
    private String username;

    // PUBLIC_INTERFACE
    @Given("OrangeHRM is available at {string}")
    public void orangehrm_is_available_at(String baseUrl) {
        /**
         * Stores the base URL for the scenario context.
         * In dry-run mode, this method is not executed.
         */
        this.baseUrl = baseUrl;
    }

    // PUBLIC_INTERFACE
    @When("I login as {string} with password {string}")
    public void i_login_as_with_password(String username, String password) {
        /**
         * Represents a login action.
         * This suite intentionally avoids launching a browser; real UI tests should use org.orangehrm.selenium.*.
         */
        this.username = username;

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("Base URL must be set before login.");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must be provided.");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password must be provided.");
        }
    }

    // PUBLIC_INTERFACE
    @Then("I should see the dashboard")
    public void i_should_see_the_dashboard() {
        /**
         * Placeholder assertion for demonstration.
         * In real UI mode, assertions belong in the Selenium test suite.
         */
        if (username == null || username.isBlank()) {
            throw new AssertionError("Expected a logged-in user in scenario context.");
        }
    }
}
