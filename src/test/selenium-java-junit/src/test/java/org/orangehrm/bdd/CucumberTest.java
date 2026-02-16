package org.orangehrm.bdd;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit Platform suite entry point for running Cucumber features.
 *
 * Notes:
 * - This suite is intended to support "dry-run" validation in CI (validate step bindings without executing steps).
 * - Dry-run is controlled via Maven/Surefire system property 'cucumber.dryRun' (default true in pom.xml).
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.orangehrm.bdd.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty, summary")
public class CucumberTest {
}
