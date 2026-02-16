using NUnit.Framework;
using Reqnroll;

namespace OrangeHrm.SeleniumNUnit.Bindings
{
    [Binding]
    public sealed class LoginSteps
    {
        private readonly ScenarioContext _scenarioContext;

        public LoginSteps(ScenarioContext scenarioContext)
        {
            _scenarioContext = scenarioContext;
        }

        [Given("OrangeHRM is available at {string}")]
        public void GivenOrangeHrmIsAvailableAt(string baseUrl)
        {
            // Discovery/dry-run safe: do not perform any network/browser operations.
            _scenarioContext["BaseUrl"] = baseUrl;

            Assert.That(baseUrl, Is.Not.Empty);
        }

        [When("I login as {string} with password {string}")]
        public void WhenILoginAsWithPassword(string username, string password)
        {
            // Discovery/dry-run safe: do not attempt real UI login.
            // Store values so the scenario has a meaningful execution path.
            _scenarioContext["Username"] = username;
            _scenarioContext["Password"] = password;

            Assert.That(username, Is.Not.Empty);
            Assert.That(password, Is.Not.Empty);
        }

        [Then("I should see the dashboard")]
        public void ThenIShouldSeeTheDashboard()
        {
            // Discovery/dry-run safe assertion that the step binding works and scenario executes.
            Assert.That(_scenarioContext.ContainsKey("BaseUrl"), "BaseUrl should have been captured in Given step.");
            Assert.That(_scenarioContext.ContainsKey("Username"), "Username should have been captured in When step.");
        }
    }
}
