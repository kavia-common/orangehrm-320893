package com.orangehrm.steps;

import com.orangehrm.support.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Cucumber hooks to manage WebDriver lifecycle.
 *
 * Demo stabilization:
 *  - No Allure integration (console logs + raw results only).
 */
public class Hooks {

    @Before
    public void beforeScenario(Scenario scenario) {
        System.out.println("[Java][Hooks] Starting scenario: " + scenario.getName());
        DriverFactory.createDriver();
    }

    @After
    public void afterScenario(Scenario scenario) {
        try {
            System.out.println("[Java][Hooks] Finished scenario: " + scenario.getName() + " | status=" + scenario.getStatus());
        } finally {
            DriverFactory.quitDriver();
        }
    }
}
