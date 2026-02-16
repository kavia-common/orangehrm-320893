using System;
using System.Collections.Generic;
using NUnit.Framework;
using Reqnroll;

namespace OrangeHrm.SeleniumNUnit.Bindings
{
    [Binding]
    public sealed class ModuleSteps
    {
        private readonly ScenarioContext _scenarioContext;

        public ModuleSteps(ScenarioContext scenarioContext)
        {
            _scenarioContext = scenarioContext;
        }

        private void RequireAuthenticated()
        {
            if (!_scenarioContext.ContainsKey("AuthenticatedUser") ||
                string.IsNullOrWhiteSpace(_scenarioContext["AuthenticatedUser"]?.ToString()))
            {
                throw new InvalidOperationException("Scenario requires authentication; call 'I am authenticated as ...' first.");
            }
        }

        // -------------------------
        // Shared/auth
        // -------------------------

        [Given("I am authenticated as {string}")]
        public void GivenIAmAuthenticatedAs(string username)
        {
            // Discovery/dry-run safe: no real authentication.
            Assert.That(username, Is.Not.Empty);
            _scenarioContext["AuthenticatedUser"] = username;
        }

        // -------------------------
        // PIM
        // -------------------------

        [When("I create a PIM employee with first name {string} and last name {string}")]
        public void WhenICreateAPimEmployeeWithFirstNameAndLastName(string firstName, string lastName)
        {
            RequireAuthenticated();

            Assert.That(firstName, Is.Not.Empty);
            Assert.That(lastName, Is.Not.Empty);

            var employee = new Dictionary<string, object?>
            {
                ["firstName"] = firstName,
                ["lastName"] = lastName,
                ["fullName"] = $"{firstName} {lastName}"
            };

            _scenarioContext["PimEmployee"] = employee;
        }

        [Then("the employee record should be created in scenario context")]
        public void ThenTheEmployeeRecordShouldBeCreatedInScenarioContext()
        {
            Assert.That(_scenarioContext.ContainsKey("PimEmployee"));
        }

        [Then("the employee full name should be {string}")]
        public void ThenTheEmployeeFullNameShouldBe(string expectedFullName)
        {
            var employee = _scenarioContext.Get<Dictionary<string, object?>>("PimEmployee");
            Assert.That(employee["fullName"]?.ToString(), Is.EqualTo(expectedFullName));
        }

        [Given("a PIM employee exists with employee id {string}")]
        public void GivenAPimEmployeeExistsWithEmployeeId(string employeeId)
        {
            Assert.That(employeeId, Is.Not.Empty);
            _scenarioContext["SeedEmployeeId"] = employeeId;
        }

        [When("I search PIM employees by employee id {string}")]
        public void WhenISearchPimEmployeesByEmployeeId(string employeeId)
        {
            RequireAuthenticated();

            Assert.That(employeeId, Is.Not.Empty);
            _scenarioContext["PimSearchResults"] = new List<string> { employeeId };
        }

        [Then("I should see search results containing employee id {string}")]
        public void ThenIShouldSeeSearchResultsContainingEmployeeId(string employeeId)
        {
            var results = _scenarioContext.Get<List<string>>("PimSearchResults");
            Assert.That(results, Does.Contain(employeeId));
        }

        // -------------------------
        // Leave
        // -------------------------

        [When("I apply for leave of type {string} from {string} to {string}")]
        public void WhenIApplyForLeaveOfTypeFromTo(string leaveType, string fromDate, string toDate)
        {
            RequireAuthenticated();

            Assert.That(leaveType, Is.Not.Empty);
            Assert.That(fromDate, Is.Not.Empty);
            Assert.That(toDate, Is.Not.Empty);

            var leaveRequest = new Dictionary<string, object?>
            {
                ["type"] = leaveType,
                ["from"] = fromDate,
                ["to"] = toDate,
                ["status"] = "Pending Approval"
            };

            _scenarioContext["LeaveRequest"] = leaveRequest;
        }

        [Then("a leave request should be created in scenario context")]
        public void ThenALeaveRequestShouldBeCreatedInScenarioContext()
        {
            Assert.That(_scenarioContext.ContainsKey("LeaveRequest"));
        }

        [Then("the leave request status should be {string}")]
        public void ThenTheLeaveRequestStatusShouldBe(string expectedStatus)
        {
            var leaveRequest = _scenarioContext.Get<Dictionary<string, object?>>("LeaveRequest");
            Assert.That(leaveRequest["status"]?.ToString(), Is.EqualTo(expectedStatus));
        }

        [Given("a leave request exists with reference {string} and status {string}")]
        public void GivenALeaveRequestExistsWithReferenceAndStatus(string reference, string status)
        {
            Assert.That(reference, Is.Not.Empty);
            Assert.That(status, Is.Not.Empty);

            var leaveRequest = new Dictionary<string, object?>
            {
                ["reference"] = reference,
                ["status"] = status
            };

            _scenarioContext["LeaveRequest"] = leaveRequest;
        }

        [When("I approve leave request with reference {string}")]
        public void WhenIApproveLeaveRequestWithReference(string reference)
        {
            RequireAuthenticated();

            var leaveRequest = _scenarioContext.Get<Dictionary<string, object?>>("LeaveRequest");
            Assert.That(leaveRequest["reference"]?.ToString(), Is.EqualTo(reference));

            leaveRequest["status"] = "Approved";
        }

        // -------------------------
        // Recruitment
        // -------------------------

        [When("I add a recruitment candidate {string} {string} for vacancy {string}")]
        public void WhenIAddARecruitmentCandidateForVacancy(string firstName, string lastName, string vacancy)
        {
            RequireAuthenticated();

            Assert.That(firstName, Is.Not.Empty);
            Assert.That(lastName, Is.Not.Empty);
            Assert.That(vacancy, Is.Not.Empty);

            var candidate = new Dictionary<string, object?>
            {
                ["firstName"] = firstName,
                ["lastName"] = lastName,
                ["vacancy"] = vacancy,
                ["status"] = "Application Initiated"
            };

            _scenarioContext["Candidate"] = candidate;
        }

        [Then("the candidate should be created in scenario context")]
        public void ThenTheCandidateShouldBeCreatedInScenarioContext()
        {
            Assert.That(_scenarioContext.ContainsKey("Candidate"));
        }

        [Then("the candidate status should be {string}")]
        public void ThenTheCandidateStatusShouldBe(string expectedStatus)
        {
            var candidate = _scenarioContext.Get<Dictionary<string, object?>>("Candidate");
            Assert.That(candidate["status"]?.ToString(), Is.EqualTo(expectedStatus));
        }

        [Given("a recruitment candidate exists with reference {string} and status {string}")]
        public void GivenARecruitmentCandidateExistsWithReferenceAndStatus(string reference, string status)
        {
            Assert.That(reference, Is.Not.Empty);
            Assert.That(status, Is.Not.Empty);

            var candidate = new Dictionary<string, object?>
            {
                ["reference"] = reference,
                ["status"] = status
            };

            _scenarioContext["Candidate"] = candidate;
        }

        [When("I shortlist recruitment candidate with reference {string}")]
        public void WhenIShortlistRecruitmentCandidateWithReference(string reference)
        {
            RequireAuthenticated();

            var candidate = _scenarioContext.Get<Dictionary<string, object?>>("Candidate");
            Assert.That(candidate["reference"]?.ToString(), Is.EqualTo(reference));

            candidate["status"] = "Shortlisted";
        }

        // -------------------------
        // Time
        // -------------------------

        [When("I create a timesheet entry for project {string} activity {string} on {string} with hours {int}")]
        public void WhenICreateATimesheetEntry(string project, string activity, string date, int hours)
        {
            RequireAuthenticated();

            Assert.That(project, Is.Not.Empty);
            Assert.That(activity, Is.Not.Empty);
            Assert.That(date, Is.Not.Empty);
            Assert.That(hours, Is.GreaterThanOrEqualTo(0));

            var entry = new Dictionary<string, object?>
            {
                ["project"] = project,
                ["activity"] = activity,
                ["date"] = date,
                ["hours"] = hours
            };

            _scenarioContext["TimesheetEntry"] = entry;
        }

        [Then("the timesheet entry should be created in scenario context")]
        public void ThenTheTimesheetEntryShouldBeCreatedInScenarioContext()
        {
            Assert.That(_scenarioContext.ContainsKey("TimesheetEntry"));
        }

        [Then("the timesheet entry total hours should be {int}")]
        public void ThenTheTimesheetEntryTotalHoursShouldBe(int expectedHours)
        {
            var entry = _scenarioContext.Get<Dictionary<string, object?>>("TimesheetEntry");
            Assert.That(Convert.ToInt32(entry["hours"]), Is.EqualTo(expectedHours));
        }

        [Given("a timesheet exists with reference {string} and status {string}")]
        public void GivenATimesheetExistsWithReferenceAndStatus(string reference, string status)
        {
            Assert.That(reference, Is.Not.Empty);
            Assert.That(status, Is.Not.Empty);

            var ts = new Dictionary<string, object?>
            {
                ["reference"] = reference,
                ["status"] = status
            };

            _scenarioContext["Timesheet"] = ts;
        }

        [When("I approve timesheet with reference {string}")]
        public void WhenIApproveTimesheetWithReference(string reference)
        {
            RequireAuthenticated();

            var ts = _scenarioContext.Get<Dictionary<string, object?>>("Timesheet");
            Assert.That(ts["reference"]?.ToString(), Is.EqualTo(reference));

            ts["status"] = "Approved";
        }

        [Then("the timesheet status should be {string}")]
        public void ThenTheTimesheetStatusShouldBe(string expectedStatus)
        {
            var ts = _scenarioContext.Get<Dictionary<string, object?>>("Timesheet");
            Assert.That(ts["status"]?.ToString(), Is.EqualTo(expectedStatus));
        }

        // -------------------------
        // Admin
        // -------------------------

        [When("I create an admin system user with username {string} and role {string}")]
        public void WhenICreateAnAdminSystemUserWithUsernameAndRole(string username, string role)
        {
            RequireAuthenticated();

            Assert.That(username, Is.Not.Empty);
            Assert.That(role, Is.Not.Empty);

            var user = new Dictionary<string, object?>
            {
                ["username"] = username,
                ["role"] = role,
                ["status"] = "Enabled"
            };

            _scenarioContext["SystemUser"] = user;
        }

        [Then("the system user should be created in scenario context")]
        public void ThenTheSystemUserShouldBeCreatedInScenarioContext()
        {
            Assert.That(_scenarioContext.ContainsKey("SystemUser"));
        }

        [Then("the system user role should be {string}")]
        public void ThenTheSystemUserRoleShouldBe(string expectedRole)
        {
            var user = _scenarioContext.Get<Dictionary<string, object?>>("SystemUser");
            Assert.That(user["role"]?.ToString(), Is.EqualTo(expectedRole));
        }

        [Given("a system user exists with username {string} and status {string}")]
        public void GivenASystemUserExistsWithUsernameAndStatus(string username, string status)
        {
            Assert.That(username, Is.Not.Empty);
            Assert.That(status, Is.Not.Empty);

            var user = new Dictionary<string, object?>
            {
                ["username"] = username,
                ["role"] = "ESS",
                ["status"] = status
            };

            _scenarioContext["SystemUser"] = user;
        }

        [When("I disable the system user {string}")]
        public void WhenIDisableTheSystemUser(string username)
        {
            RequireAuthenticated();

            var user = _scenarioContext.Get<Dictionary<string, object?>>("SystemUser");
            Assert.That(user["username"]?.ToString(), Is.EqualTo(username));

            user["status"] = "Disabled";
        }

        [Then("the system user status should be {string}")]
        public void ThenTheSystemUserStatusShouldBe(string expectedStatus)
        {
            var user = _scenarioContext.Get<Dictionary<string, object?>>("SystemUser");
            Assert.That(user["status"]?.ToString(), Is.EqualTo(expectedStatus));
        }

        // -------------------------
        // Dashboard
        // -------------------------

        [When("I load the dashboard configuration")]
        public void WhenILoadTheDashboardConfiguration()
        {
            RequireAuthenticated();

            var widgets = new List<string> { "Quick Launch", "My Actions" };
            _scenarioContext["DashboardWidgets"] = widgets;

            var positions = new Dictionary<string, int>();
            for (var i = 0; i < widgets.Count; i++)
            {
                positions[widgets[i]] = i + 1;
            }
            _scenarioContext["DashboardWidgetPositions"] = positions;
        }

        [Then("I should have at least {int} dashboard widget in scenario context")]
        public void ThenIShouldHaveAtLeastDashboardWidgetInScenarioContext(int minWidgets)
        {
            var widgets = _scenarioContext.Get<List<string>>("DashboardWidgets");
            Assert.That(widgets.Count, Is.GreaterThanOrEqualTo(minWidgets));
        }

        [Given("dashboard widgets exist in scenario context")]
        public void GivenDashboardWidgetsExistInScenarioContext()
        {
            if (!_scenarioContext.ContainsKey("DashboardWidgets"))
            {
                _scenarioContext["DashboardWidgets"] = new List<string> { "Quick Launch", "My Actions" };
            }

            if (!_scenarioContext.ContainsKey("DashboardWidgetPositions"))
            {
                var widgets = _scenarioContext.Get<List<string>>("DashboardWidgets");
                var positions = new Dictionary<string, int>();
                for (var i = 0; i < widgets.Count; i++)
                {
                    positions[widgets[i]] = i + 1;
                }
                _scenarioContext["DashboardWidgetPositions"] = positions;
            }
        }

        [When("I move dashboard widget {string} to position {int}")]
        public void WhenIMoveDashboardWidgetToPosition(string widgetName, int position)
        {
            RequireAuthenticated();

            Assert.That(widgetName, Is.Not.Empty);
            Assert.That(position, Is.GreaterThanOrEqualTo(1));

            var positions = _scenarioContext.Get<Dictionary<string, int>>("DashboardWidgetPositions");
            positions[widgetName] = position;
        }

        [Then("the dashboard widget {string} should be at position {int}")]
        public void ThenTheDashboardWidgetShouldBeAtPosition(string widgetName, int expectedPosition)
        {
            var positions = _scenarioContext.Get<Dictionary<string, int>>("DashboardWidgetPositions");
            Assert.That(positions.ContainsKey(widgetName), Is.True, $"Widget '{widgetName}' not found in position map.");
            Assert.That(positions[widgetName], Is.EqualTo(expectedPosition));
        }
    }
}
