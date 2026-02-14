package com.orangehrm.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * PUBLIC_INTERFACE
 * JUnit Platform entrypoint for executing Cucumber features.
 *
 * Demo mode:
 *  - Executes ONLY the demo login scenario.
 *  - Outputs console logs + raw JSON results (no Allure, no HTML report).
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/demo")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.orangehrm.steps")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty, summary, json:target/cucumber-report.json"
)
public class RunCucumberTest {
}
