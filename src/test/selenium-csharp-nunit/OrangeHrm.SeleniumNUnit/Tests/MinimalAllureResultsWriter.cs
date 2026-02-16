using System;
using System.Collections.Generic;
using System.IO;
using System.Text.Json;
using NUnit.Framework;
using NUnit.Framework.Interfaces;

namespace OrangeHrm.SeleniumNUnit.Tests
{
    /// <summary>
    /// Minimal Allure results writer for environments where the Allure NUnit adapter
    /// is not emitting output (or where tests are designed to avoid real browsers).
    ///
    /// This creates a subset of Allure v2 result JSON files that is sufficient for
    /// `allure generate` to produce a report.
    /// </summary>
    internal static class MinimalAllureResultsWriter
    {
        /// <summary>
        /// PUBLIC_INTERFACE
        /// Write an Allure `*-result.json` file for the current test.
        /// </summary>
        public static void WriteResultForCurrentTest(string resultsDir)
        {
            Directory.CreateDirectory(resultsDir);

            var test = TestContext.CurrentContext.Test;
            var outcome = TestContext.CurrentContext.Result.Outcome.Status;

            var status = outcome switch
            {
                TestStatus.Passed => "passed",
                TestStatus.Failed => "failed",
                TestStatus.Skipped => "skipped",
                TestStatus.Inconclusive => "skipped",
                _ => "broken"
            };

            // Allure expects times in ms since epoch.
            var start = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
            var stop = start;

            var result = new Dictionary<string, object?>
            {
                ["uuid"] = Guid.NewGuid().ToString(),
                ["name"] = test.Name,
                ["fullName"] = test.FullName,
                ["status"] = status,
                ["stage"] = "finished",
                ["start"] = start,
                ["stop"] = stop,
                ["labels"] = new[]
                {
                    new Dictionary<string, string> { ["name"] = "framework", ["value"] = "nunit" },
                    new Dictionary<string, string> { ["name"] = "language", ["value"] = "csharp" }
                }
            };

            var json = JsonSerializer.Serialize(result, new JsonSerializerOptions { WriteIndented = false });
            var fileName = Path.Combine(resultsDir, $"{Guid.NewGuid()}-result.json");
            File.WriteAllText(fileName, json);
        }
    }
}
