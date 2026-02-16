package org.orangehrm.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.*;

/**
 * Discovery-safe step definitions for non-login modules (PIM/Leave/Recruitment/Time/Admin/Dashboard).
 *
 * IMPORTANT:
 * - This class must remain no-browser/no-network by default.
 * - It is used primarily for Cucumber dry-run binding validation and lightweight intent execution.
 */
public class ModuleSteps {

    private String authenticatedUser;

    private final Map<String, Object> scenarioContext = new HashMap<>();

    // -------------------------
    // Shared/auth steps
    // -------------------------

    // PUBLIC_INTERFACE
    @Given("I am authenticated as {string}")
    public void i_am_authenticated_as(String username) {
        /**
         * Stores authenticated username in memory.
         * No real authentication is performed in this suite.
         */
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must be provided.");
        }
        this.authenticatedUser = username;
        scenarioContext.put("AuthenticatedUser", username);
    }

    // -------------------------
    // PIM
    // -------------------------

    // PUBLIC_INTERFACE
    @When("I create a PIM employee with first name {string} and last name {string}")
    public void i_create_a_pim_employee(String firstName, String lastName) {
        /** Creates an employee object in scenario context (no persistence). */
        requireAuthenticated();

        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must be provided.");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must be provided.");
        }

        Map<String, String> employee = new HashMap<>();
        employee.put("firstName", firstName);
        employee.put("lastName", lastName);
        employee.put("fullName", firstName + " " + lastName);

        scenarioContext.put("PimEmployee", employee);
    }

    // PUBLIC_INTERFACE
    @Then("the employee record should be created in scenario context")
    public void employee_record_should_be_created_in_scenario_context() {
        /** Validates employee object exists in context. */
        Object employee = scenarioContext.get("PimEmployee");
        if (!(employee instanceof Map)) {
            throw new AssertionError("Expected PimEmployee map in scenario context.");
        }
    }

    // PUBLIC_INTERFACE
    @Then("the employee full name should be {string}")
    public void employee_full_name_should_be(String expectedFullName) {
        /** Validates full name composition. */
        @SuppressWarnings("unchecked")
        Map<String, String> employee = (Map<String, String>) scenarioContext.get("PimEmployee");
        if (employee == null) {
            throw new AssertionError("PimEmployee must exist before asserting full name.");
        }
        String actual = employee.get("fullName");
        if (!Objects.equals(expectedFullName, actual)) {
            throw new AssertionError("Expected full name '" + expectedFullName + "' but got '" + actual + "'");
        }
    }

    // PUBLIC_INTERFACE
    @Given("a PIM employee exists with employee id {string}")
    public void a_pim_employee_exists_with_employee_id(String employeeId) {
        /** Seeds a minimal employee identifier for search scenarios. */
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("Employee id must be provided.");
        }
        scenarioContext.put("SeedEmployeeId", employeeId);
    }

    // PUBLIC_INTERFACE
    @When("I search PIM employees by employee id {string}")
    public void i_search_pim_employees_by_employee_id(String employeeId) {
        /** Creates deterministic search results in context. */
        requireAuthenticated();

        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("Employee id must be provided.");
        }

        List<String> results = new ArrayList<>();
        // Deterministic "result": include whatever was searched.
        results.add(employeeId);
        scenarioContext.put("PimSearchResults", results);
    }

    // PUBLIC_INTERFACE
    @Then("I should see search results containing employee id {string}")
    public void i_should_see_search_results_containing_employee_id(String employeeId) {
        /** Validates deterministic search results. */
        @SuppressWarnings("unchecked")
        List<String> results = (List<String>) scenarioContext.get("PimSearchResults");
        if (results == null || !results.contains(employeeId)) {
            throw new AssertionError("Expected search results to contain employee id " + employeeId);
        }
    }

    // -------------------------
    // Leave
    // -------------------------

    // PUBLIC_INTERFACE
    @When("I apply for leave of type {string} from {string} to {string}")
    public void i_apply_for_leave(String leaveType, String fromDate, String toDate) {
        /** Creates a leave request object in scenario context (no API/UI calls). */
        requireAuthenticated();

        if (leaveType == null || leaveType.isBlank()) {
            throw new IllegalArgumentException("Leave type must be provided.");
        }
        if (fromDate == null || fromDate.isBlank() || toDate == null || toDate.isBlank()) {
            throw new IllegalArgumentException("From/to dates must be provided.");
        }

        Map<String, String> leaveRequest = new HashMap<>();
        leaveRequest.put("type", leaveType);
        leaveRequest.put("from", fromDate);
        leaveRequest.put("to", toDate);
        leaveRequest.put("status", "Pending Approval");

        scenarioContext.put("LeaveRequest", leaveRequest);
    }

    // PUBLIC_INTERFACE
    @Then("a leave request should be created in scenario context")
    public void leave_request_should_be_created_in_scenario_context() {
        /** Validates leave request exists in context. */
        Object lr = scenarioContext.get("LeaveRequest");
        if (!(lr instanceof Map)) {
            throw new AssertionError("Expected LeaveRequest map in scenario context.");
        }
    }

    // PUBLIC_INTERFACE
    @Then("the leave request status should be {string}")
    public void the_leave_request_status_should_be(String expectedStatus) {
        /** Validates leave request status value. */
        @SuppressWarnings("unchecked")
        Map<String, String> lr = (Map<String, String>) scenarioContext.get("LeaveRequest");
        if (lr == null) {
            throw new AssertionError("LeaveRequest must exist before asserting status.");
        }
        String actual = lr.get("status");
        if (!Objects.equals(expectedStatus, actual)) {
            throw new AssertionError("Expected leave request status '" + expectedStatus + "' but got '" + actual + "'");
        }
    }

    // PUBLIC_INTERFACE
    @Given("a leave request exists with reference {string} and status {string}")
    public void a_leave_request_exists(String reference, String status) {
        /** Seeds a leave request record keyed by reference. */
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException("Leave request reference must be provided.");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Leave request status must be provided.");
        }
        Map<String, String> lr = new HashMap<>();
        lr.put("reference", reference);
        lr.put("status", status);
        scenarioContext.put("LeaveRequest", lr);
    }

    // PUBLIC_INTERFACE
    @When("I approve leave request with reference {string}")
    public void i_approve_leave_request(String reference) {
        /** Marks existing leave request as approved. */
        requireAuthenticated();

        @SuppressWarnings("unchecked")
        Map<String, String> lr = (Map<String, String>) scenarioContext.get("LeaveRequest");
        if (lr == null) {
            throw new AssertionError("LeaveRequest must exist before approval.");
        }
        if (!Objects.equals(reference, lr.get("reference"))) {
            throw new AssertionError("Reference mismatch: expected " + reference + " but got " + lr.get("reference"));
        }
        lr.put("status", "Approved");
    }

    // -------------------------
    // Recruitment
    // -------------------------

    // PUBLIC_INTERFACE
    @When("I add a recruitment candidate {string} {string} for vacancy {string}")
    public void i_add_a_recruitment_candidate(String firstName, String lastName, String vacancy) {
        /** Creates a candidate object in scenario context. */
        requireAuthenticated();

        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Candidate first/last name must be provided.");
        }
        if (vacancy == null || vacancy.isBlank()) {
            throw new IllegalArgumentException("Vacancy must be provided.");
        }

        Map<String, String> candidate = new HashMap<>();
        candidate.put("firstName", firstName);
        candidate.put("lastName", lastName);
        candidate.put("vacancy", vacancy);
        candidate.put("status", "Application Initiated");

        scenarioContext.put("Candidate", candidate);
    }

    // PUBLIC_INTERFACE
    @Then("the candidate should be created in scenario context")
    public void the_candidate_should_be_created_in_scenario_context() {
        /** Validates candidate exists in context. */
        Object cand = scenarioContext.get("Candidate");
        if (!(cand instanceof Map)) {
            throw new AssertionError("Expected Candidate map in scenario context.");
        }
    }

    // PUBLIC_INTERFACE
    @Then("the candidate status should be {string}")
    public void the_candidate_status_should_be(String expectedStatus) {
        /** Validates candidate status. */
        @SuppressWarnings("unchecked")
        Map<String, String> cand = (Map<String, String>) scenarioContext.get("Candidate");
        if (cand == null) {
            throw new AssertionError("Candidate must exist before asserting status.");
        }
        String actual = cand.get("status");
        if (!Objects.equals(expectedStatus, actual)) {
            throw new AssertionError("Expected candidate status '" + expectedStatus + "' but got '" + actual + "'");
        }
    }

    // PUBLIC_INTERFACE
    @Given("a recruitment candidate exists with reference {string} and status {string}")
    public void a_recruitment_candidate_exists(String reference, String status) {
        /** Seeds a candidate record keyed by reference. */
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException("Candidate reference must be provided.");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Candidate status must be provided.");
        }
        Map<String, String> cand = new HashMap<>();
        cand.put("reference", reference);
        cand.put("status", status);
        scenarioContext.put("Candidate", cand);
    }

    // PUBLIC_INTERFACE
    @When("I shortlist recruitment candidate with reference {string}")
    public void i_shortlist_recruitment_candidate(String reference) {
        /** Marks candidate as shortlisted. */
        requireAuthenticated();

        @SuppressWarnings("unchecked")
        Map<String, String> cand = (Map<String, String>) scenarioContext.get("Candidate");
        if (cand == null) {
            throw new AssertionError("Candidate must exist before shortlisting.");
        }
        if (!Objects.equals(reference, cand.get("reference"))) {
            throw new AssertionError("Reference mismatch: expected " + reference + " but got " + cand.get("reference"));
        }
        cand.put("status", "Shortlisted");
    }

    // -------------------------
    // Time
    // -------------------------

    // PUBLIC_INTERFACE
    @When("I create a timesheet entry for project {string} activity {string} on {string} with hours {int}")
    public void i_create_a_timesheet_entry(String project, String activity, String date, int hours) {
        /** Creates a timesheet entry in scenario context. */
        requireAuthenticated();

        if (project == null || project.isBlank()) {
            throw new IllegalArgumentException("Project must be provided.");
        }
        if (activity == null || activity.isBlank()) {
            throw new IllegalArgumentException("Activity must be provided.");
        }
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Date must be provided.");
        }
        if (hours < 0) {
            throw new IllegalArgumentException("Hours must be non-negative.");
        }

        Map<String, Object> entry = new HashMap<>();
        entry.put("project", project);
        entry.put("activity", activity);
        entry.put("date", date);
        entry.put("hours", hours);

        scenarioContext.put("TimesheetEntry", entry);
    }

    // PUBLIC_INTERFACE
    @Then("the timesheet entry should be created in scenario context")
    public void timesheet_entry_should_be_created_in_scenario_context() {
        /** Validates timesheet entry exists. */
        Object entry = scenarioContext.get("TimesheetEntry");
        if (!(entry instanceof Map)) {
            throw new AssertionError("Expected TimesheetEntry map in scenario context.");
        }
    }

    // PUBLIC_INTERFACE
    @Then("the timesheet entry total hours should be {int}")
    public void the_timesheet_entry_total_hours_should_be(int expectedHours) {
        /** Validates hours in entry. */
        @SuppressWarnings("unchecked")
        Map<String, Object> entry = (Map<String, Object>) scenarioContext.get("TimesheetEntry");
        if (entry == null) {
            throw new AssertionError("TimesheetEntry must exist before asserting hours.");
        }
        Object hoursObj = entry.get("hours");
        if (!(hoursObj instanceof Integer)) {
            throw new AssertionError("Expected integer hours in TimesheetEntry.");
        }
        int actual = (Integer) hoursObj;
        if (actual != expectedHours) {
            throw new AssertionError("Expected hours " + expectedHours + " but got " + actual);
        }
    }

    // PUBLIC_INTERFACE
    @Given("a timesheet exists with reference {string} and status {string}")
    public void a_timesheet_exists_with_reference_and_status(String reference, String status) {
        /** Seeds a timesheet record. */
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException("Timesheet reference must be provided.");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Timesheet status must be provided.");
        }
        Map<String, String> ts = new HashMap<>();
        ts.put("reference", reference);
        ts.put("status", status);
        scenarioContext.put("Timesheet", ts);
    }

    // PUBLIC_INTERFACE
    @When("I approve timesheet with reference {string}")
    public void i_approve_timesheet_with_reference(String reference) {
        /** Marks timesheet approved. */
        requireAuthenticated();

        @SuppressWarnings("unchecked")
        Map<String, String> ts = (Map<String, String>) scenarioContext.get("Timesheet");
        if (ts == null) {
            throw new AssertionError("Timesheet must exist before approval.");
        }
        if (!Objects.equals(reference, ts.get("reference"))) {
            throw new AssertionError("Reference mismatch: expected " + reference + " but got " + ts.get("reference"));
        }
        ts.put("status", "Approved");
    }

    // PUBLIC_INTERFACE
    @Then("the timesheet status should be {string}")
    public void the_timesheet_status_should_be(String expectedStatus) {
        /** Validates timesheet status. */
        @SuppressWarnings("unchecked")
        Map<String, String> ts = (Map<String, String>) scenarioContext.get("Timesheet");
        if (ts == null) {
            throw new AssertionError("Timesheet must exist before asserting status.");
        }
        String actual = ts.get("status");
        if (!Objects.equals(expectedStatus, actual)) {
            throw new AssertionError("Expected timesheet status '" + expectedStatus + "' but got '" + actual + "'");
        }
    }

    // -------------------------
    // Admin
    // -------------------------

    // PUBLIC_INTERFACE
    @When("I create an admin system user with username {string} and role {string}")
    public void i_create_an_admin_system_user(String username, String role) {
        /** Creates a system user record in scenario context. */
        requireAuthenticated();

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("System username must be provided.");
        }
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role must be provided.");
        }

        Map<String, String> user = new HashMap<>();
        user.put("username", username);
        user.put("role", role);
        user.put("status", "Enabled");
        scenarioContext.put("SystemUser", user);
    }

    // PUBLIC_INTERFACE
    @Then("the system user should be created in scenario context")
    public void the_system_user_should_be_created_in_scenario_context() {
        /** Validates system user exists. */
        Object user = scenarioContext.get("SystemUser");
        if (!(user instanceof Map)) {
            throw new AssertionError("Expected SystemUser map in scenario context.");
        }
    }

    // PUBLIC_INTERFACE
    @Then("the system user role should be {string}")
    public void the_system_user_role_should_be(String expectedRole) {
        /** Validates system user role. */
        @SuppressWarnings("unchecked")
        Map<String, String> user = (Map<String, String>) scenarioContext.get("SystemUser");
        if (user == null) {
            throw new AssertionError("SystemUser must exist before asserting role.");
        }
        String actual = user.get("role");
        if (!Objects.equals(expectedRole, actual)) {
            throw new AssertionError("Expected role '" + expectedRole + "' but got '" + actual + "'");
        }
    }

    // PUBLIC_INTERFACE
    @Given("a system user exists with username {string} and status {string}")
    public void a_system_user_exists_with_username_and_status(String username, String status) {
        /** Seeds a system user record. */
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("System username must be provided.");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status must be provided.");
        }
        Map<String, String> user = new HashMap<>();
        user.put("username", username);
        user.put("role", "ESS");
        user.put("status", status);
        scenarioContext.put("SystemUser", user);
    }

    // PUBLIC_INTERFACE
    @When("I disable the system user {string}")
    public void i_disable_the_system_user(String username) {
        /** Marks system user disabled. */
        requireAuthenticated();

        @SuppressWarnings("unchecked")
        Map<String, String> user = (Map<String, String>) scenarioContext.get("SystemUser");
        if (user == null) {
            throw new AssertionError("SystemUser must exist before disabling.");
        }
        if (!Objects.equals(username, user.get("username"))) {
            throw new AssertionError("Username mismatch: expected " + username + " but got " + user.get("username"));
        }
        user.put("status", "Disabled");
    }

    // PUBLIC_INTERFACE
    @Then("the system user status should be {string}")
    public void the_system_user_status_should_be(String expectedStatus) {
        /** Validates system user status. */
        @SuppressWarnings("unchecked")
        Map<String, String> user = (Map<String, String>) scenarioContext.get("SystemUser");
        if (user == null) {
            throw new AssertionError("SystemUser must exist before asserting status.");
        }
        String actual = user.get("status");
        if (!Objects.equals(expectedStatus, actual)) {
            throw new AssertionError("Expected status '" + expectedStatus + "' but got '" + actual + "'");
        }
    }

    // -------------------------
    // Dashboard
    // -------------------------

    // PUBLIC_INTERFACE
    @When("I load the dashboard configuration")
    public void i_load_the_dashboard_configuration() {
        /** Seeds widget list as if loaded from the system. */
        requireAuthenticated();

        List<String> widgets = new ArrayList<>();
        widgets.add("Quick Launch");
        widgets.add("My Actions");
        scenarioContext.put("DashboardWidgets", widgets);

        // Maintain a derived "positions" map to support move/assert steps.
        Map<String, Integer> positions = new HashMap<>();
        for (int i = 0; i < widgets.size(); i++) {
            positions.put(widgets.get(i), i + 1);
        }
        scenarioContext.put("DashboardWidgetPositions", positions);
    }

    // PUBLIC_INTERFACE
    @Then("I should have at least {int} dashboard widget in scenario context")
    public void i_should_have_at_least_dashboard_widget_in_scenario_context(int minWidgets) {
        /** Validates widget list count. */
        @SuppressWarnings("unchecked")
        List<String> widgets = (List<String>) scenarioContext.get("DashboardWidgets");
        if (widgets == null) {
            throw new AssertionError("DashboardWidgets must exist after loading dashboard configuration.");
        }
        if (widgets.size() < minWidgets) {
            throw new AssertionError("Expected at least " + minWidgets + " widgets but got " + widgets.size());
        }
    }

    // PUBLIC_INTERFACE
    @Given("dashboard widgets exist in scenario context")
    public void dashboard_widgets_exist_in_scenario_context() {
        /** Ensures widgets are present; if missing, seed defaults. */
        @SuppressWarnings("unchecked")
        List<String> widgets = (List<String>) scenarioContext.get("DashboardWidgets");
        if (widgets == null) {
            widgets = new ArrayList<>(List.of("Quick Launch", "My Actions"));
            scenarioContext.put("DashboardWidgets", widgets);
        }
        @SuppressWarnings("unchecked")
        Map<String, Integer> positions = (Map<String, Integer>) scenarioContext.get("DashboardWidgetPositions");
        if (positions == null) {
            positions = new HashMap<>();
            for (int i = 0; i < widgets.size(); i++) {
                positions.put(widgets.get(i), i + 1);
            }
            scenarioContext.put("DashboardWidgetPositions", positions);
        }
    }

    // PUBLIC_INTERFACE
    @When("I move dashboard widget {string} to position {int}")
    public void i_move_dashboard_widget_to_position(String widgetName, int position) {
        /** Updates widget position in context (no UI operation). */
        requireAuthenticated();

        if (widgetName == null || widgetName.isBlank()) {
            throw new IllegalArgumentException("Widget name must be provided.");
        }
        if (position < 1) {
            throw new IllegalArgumentException("Position must be >= 1.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Integer> positions = (Map<String, Integer>) scenarioContext.get("DashboardWidgetPositions");
        if (positions == null) {
            throw new AssertionError("DashboardWidgetPositions must exist before moving widgets.");
        }
        positions.put(widgetName, position);
    }

    // PUBLIC_INTERFACE
    @Then("the dashboard widget {string} should be at position {int}")
    public void the_dashboard_widget_should_be_at_position(String widgetName, int expectedPosition) {
        /** Validates position mapping. */
        @SuppressWarnings("unchecked")
        Map<String, Integer> positions = (Map<String, Integer>) scenarioContext.get("DashboardWidgetPositions");
        if (positions == null) {
            throw new AssertionError("DashboardWidgetPositions must exist before asserting widget position.");
        }
        Integer actual = positions.get(widgetName);
        if (actual == null) {
            throw new AssertionError("Widget '" + widgetName + "' was not found in positions map.");
        }
        if (actual != expectedPosition) {
            throw new AssertionError("Expected widget '" + widgetName + "' at position " + expectedPosition + " but got " + actual);
        }
    }

    private void requireAuthenticated() {
        if (authenticatedUser == null || authenticatedUser.isBlank()) {
            throw new IllegalStateException("Scenario requires authentication; call 'I am authenticated as ...' first.");
        }
    }
}
