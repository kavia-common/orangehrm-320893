package com.orangehrm.steps;

import com.orangehrm.support.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;

/**
 * Cucumber hooks to manage WebDriver lifecycle.
 */
public class Hooks {

    @Before
    public void beforeScenario() {
        DriverFactory.createDriver();
    }

    @After
    public void afterScenario() {
        DriverFactory.quitDriver();
    }
}
