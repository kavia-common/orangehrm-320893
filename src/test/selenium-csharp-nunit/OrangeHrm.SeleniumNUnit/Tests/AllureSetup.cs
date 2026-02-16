using Allure.Net.Commons;
using NUnit.Framework;

namespace OrangeHrm.SeleniumNUnit.Tests
{
    /// <summary>
    /// Global one-time setup for Allure reporting.
    /// Ensures that allure-results files are emitted during NUnit runs.
    /// </summary>
    [SetUpFixture]
    public sealed class AllureSetup
    {
        // PUBLIC_INTERFACE
        [OneTimeSetUp]
        public void GlobalSetup()
        {
            /**
             * Configure Allure results directory.
             *
             * NOTE:
             * In Allure.Net.Commons 2.12.x, AllureLifecycle.ResultsDirectory is read-only,
             * so we configure the output path via the documented env var:
             *   ALLURE_RESULTS_DIRECTORY
             *
             * This is compatible with `dotnet test` and the Allure NUnit adapter.
             */
            var resultsDir = TestContext.Parameters.Get("allureResultsDir", "../allure-results");
            System.Environment.SetEnvironmentVariable("ALLURE_RESULTS_DIRECTORY", resultsDir);

            // Clean whatever directory Allure is configured to use (now driven by env var).
            AllureLifecycle.Instance.CleanupResultDirectory();
        }
    }
}
