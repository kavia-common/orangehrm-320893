package com.orangehrm.steps;

import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.SideMenu;
import com.orangehrm.support.TestConfig;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for common actions across modules.
 */
public class CommonSteps {

    private final LoginPage loginPage = new LoginPage();
    private final SideMenu sideMenu = new SideMenu();
    private final DashboardPage dashboardPage = new DashboardPage();

    @Given("the user is on the OrangeHRM login page")
    public void openLogin() {
        loginPage.open();
    }

    @When("the user logs in as Admin")
    public void loginAsAdmin() {
        loginPage.login(TestConfig.adminUsername(), TestConfig.adminPassword());
    }

    @When("the user logs in as ESS")
    public void loginAsEss() {
        loginPage.login(TestConfig.essUsername(), TestConfig.essPassword());
    }

    @When("the user logs in with username {string} and password {string}")
    public void loginWith(String username, String password) {
        loginPage.login(username, password);
    }

    @Then("login should succeed and Dashboard should be visible")
    public void dashboardVisible() {
        assertTrue(dashboardPage.isLoaded(), "Expected Dashboard to be visible after login");
    }

    @Then("an invalid credentials message should be shown")
    public void invalidCreds() {
        assertTrue(loginPage.isInvalidCredentialsShown(), "Expected Invalid credentials message");
    }

    @Then("required field validation should be shown on the login form")
    public void requiredValidation() {
        assertTrue(loginPage.isRequiredShown(), "Expected Required validation");
    }

    @When("the user opens the Dashboard module")
    public void openDashboard() {
        sideMenu.openDashboard();
    }

    @When("the user opens the PIM module")
    public void openPim() {
        sideMenu.openPim();
    }

    @When("the user opens the Leave module")
    public void openLeave() {
        sideMenu.openLeave();
    }

    @When("the user opens the Recruitment module")
    public void openRecruitment() {
        sideMenu.openRecruitment();
    }

    @When("the user opens the Time module")
    public void openTime() {
        sideMenu.openTime();
    }

    @When("the user opens the Admin module")
    public void openAdmin() {
        sideMenu.openAdmin();
    }

    @Then("the Dashboard module should be accessible")
    public void assertDashboardAccessible() {
        assertTrue(dashboardPage.isLoaded(), "Expected Dashboard header");
    }
}
