using System;
using System.IO;
using Allure.Net.Commons;
using NUnit.Framework;

namespace OrangeHrm.SeleniumNUnit.Tests
{
    /// <summary>
    /// Global Allure setup.
    ///
    /// This project is a CI-safe skeleton that defaults to discovery-only mode.
    /// To satisfy reporting requirements, we ensure the Allure results directory exists,
    /// and we also provide a minimal JSON result writer as a fallback when the adapter
    /// does not emit results.
    /// </summary>
    [SetUpFixture]
    public sealed class AllureSetup
    {
        private static string ResolveResultsDir()
        {
            // Prefer official env var, then our older param name, then default to repo-local path.
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

            return "../allure-results";
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

            // Clean whatever directory Allure is configured to use (now driven by env var).
            // If the adapter isn't active, this is harmless.
            AllureLifecycle.Instance.CleanupResultDirectory();
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
