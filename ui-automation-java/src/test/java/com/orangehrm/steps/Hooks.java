package com.orangehrm.steps;

import com.orangehrm.support.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

/**
 * Cucumber hooks to manage WebDriver lifecycle.
 */
public class Hooks {

    @Before
    public void beforeScenario() {
        DriverFactory.createDriver();
    }

    @After
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                WebDriver driver = DriverFactory.getDriver();
                if (driver instanceof TakesScreenshot ts) {
                    byte[] png = ts.getScreenshotAs(OutputType.BYTES);

                    // Attach to Cucumber report output
                    scenario.attach(png, "image/png", "failure-screenshot");

                    // Attach to Allure results (visible in Allure report)
                    Allure.addAttachment("failure-screenshot", "image/png", new ByteArrayInputStream(png), ".png");
                }
            }
        } finally {
            DriverFactory.quitDriver();
        }
    }
}
