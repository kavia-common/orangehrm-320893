using NUnit.Framework;
using OrangeHrm.UiTests.Pages;
using OrangeHrm.UiTests.Support;
using Reqnroll;

namespace OrangeHrm.UiTests.Steps;

[Binding]
public sealed class CommonSteps
{
    private readonly LoginPage _login = new();
    private readonly SideMenu _menu = new();
    private readonly DashboardPage _dashboard = new();

    [Given("the user is on the OrangeHRM login page")]
    public void GivenUserIsOnLoginPage()
    {
        _login.Open();
    }

    [When("the user logs in as Admin")]
    public void WhenUserLogsInAsAdmin()
    {
        _login.Login(TestConfig.AdminUsername, TestConfig.AdminPassword);
    }

    [When("the user logs in as ESS")]
    public void WhenUserLogsInAsEss()
    {
        _login.Login(TestConfig.EssUsername, TestConfig.EssPassword);
    }

    [When("the user logs in with username {string} and password {string}")]
    public void WhenUserLogsInWith(string username, string password)
    {
        _login.Login(username, password);
    }

    [Then("login should succeed and Dashboard should be visible")]
    public void ThenDashboardShouldBeVisible()
    {
        Assert.That(_dashboard.IsLoaded(), Is.True, "Expected Dashboard to load.");
    }

    [Then("an invalid credentials message should be shown")]
    public void ThenInvalidCredentials()
    {
        Assert.That(_login.IsInvalidCredsShown(), Is.True, "Expected invalid credentials message.");
    }

    [Then("required field validation should be shown on the login form")]
    public void ThenRequiredValidation()
    {
        Assert.That(_login.IsRequiredShown(), Is.True, "Expected required field validation.");
    }

    [When("the user opens the Dashboard module")]
    public void WhenOpenDashboard() => _menu.OpenDashboard();

    [When("the user opens the PIM module")]
    public void WhenOpenPim() => _menu.OpenPim();

    [When("the user opens the Leave module")]
    public void WhenOpenLeave() => _menu.OpenLeave();

    [When("the user opens the Recruitment module")]
    public void WhenOpenRecruitment() => _menu.OpenRecruitment();

    [When("the user opens the Time module")]
    public void WhenOpenTime() => _menu.OpenTime();

    [When("the user opens the Admin module")]
    public void WhenOpenAdmin() => _menu.OpenAdmin();

    [Then("the Dashboard module should be accessible")]
    public void ThenDashboardModuleAccessible()
    {
        Assert.That(_dashboard.IsLoaded(), Is.True, "Expected Dashboard header.");
    }
}
