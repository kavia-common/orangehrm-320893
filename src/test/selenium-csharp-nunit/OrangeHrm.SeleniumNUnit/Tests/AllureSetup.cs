using System;
using System.Collections.Generic;
using System.IO;
using Allure.Net.Commons;
using NUnit.Framework;
using OrangeHrm.SeleniumNUnit.Config;

namespace OrangeHrm.SeleniumNUnit.Tests
{
    /// <summary>
    /// Global Allure setup.
    ///
    /// This project is a CI-safe skeleton that defaults to discovery-only mode.
    ///
    /// Responsibilities:
    /// - Ensure Allure results directory exists.
    /// - Point Allure.Net.Commons at that directory.
    /// - Emit environment.properties so downstream aggregation can read suite/browser/discovery flags.
    /// - Provide a minimal JSON result writer fallback as a safety net.
    /// </summary>
    [SetUpFixture]
    public sealed class AllureSetup
    {
        private static string ResolveResultsDir()
        {
            // Prefer official env var, then our older param name, then default to module-local path.
            var env = Environment.GetEnvironmentVariable("ALLURE_RESULTS_DIRECTORY");
            if (!string.IsNullOrWhiteSpace(env))
            {
                return env.Trim();
            }

            var param = TestContext.Parameters.Get("allureResultsDir", null);
            if (!string.IsNullOrWhiteSpace(param))
            {
                return param.Trim();
            }

            // Standard module-level directory: src/test/selenium-csharp-nunit/allure-results
            return "../allure-results";
        }

        private static void WriteEnvironmentProperties(string resultsDir)
        {
            // Allure standard file name; used by Allure report UI and also by our centralized dashboard.
            var path = Path.Combine(resultsDir, "environment.properties");

            // Avoid secrets: only publish non-sensitive metadata.
            var ui = OrangeHrmSettings.Load();
            var api = OrangeHrmApiSettings.Load();

            var lines = new List<string>
            {
                "suite=csharp-nunit",
                "language=csharp",
                "framework=nunit",
                $"browser={ui.Browser}",
                $"headless={ui.Headless}",
                $"ui.discoveryOnly={ui.DiscoveryOnly}",
                $"api.discoveryOnly={api.DiscoveryOnly}"
            };

            File.WriteAllLines(path, lines);
        }

        // PUBLIC_INTERFACE
        [OneTimeSetUp]
        public void GlobalSetup()
        {
            var resultsDir = ResolveResultsDir();

            // Ensure directory exists even if the adapter doesn't create it.
            Directory.CreateDirectory(resultsDir);

            // Configure Allure.Net.Commons to use this directory via env var.
            Environment.SetEnvironmentVariable("ALLURE_RESULTS_DIRECTORY", resultsDir);

            // Emit environment metadata early so it exists even if the run fails.
            WriteEnvironmentProperties(resultsDir);

            // Clean whatever directory Allure is configured to use (now driven by env var).
            // If the adapter isn't active, this is harmless.
            AllureLifecycle.Instance.CleanupResultDirectory();

            // Re-create after cleanup so environment.properties is present in final output.
            Directory.CreateDirectory(resultsDir);
            WriteEnvironmentProperties(resultsDir);
        }

        // PUBLIC_INTERFACE
        [OneTimeTearDown]
        public void GlobalTearDown()
        {
            // SetUpFixture supports ONLY one-time teardown.
            //
            // We still want to guarantee at least some Allure output exists even if the adapter
            // doesn't emit results (e.g., in no-browser/demo mode). We therefore write a minimal
            // result in the one-time teardown as a fallback safety net.
            var resultsDir = ResolveResultsDir();
            try
            {
                MinimalAllureResultsWriter.WriteResultForCurrentTest(resultsDir);
            }
            catch
            {
                // Never fail the test run due to reporting.
            }
        }
    }
}
