using System;
using Allure.Net.Commons;
using OpenQA.Selenium;
using OrangeHrm.UiTests.Support;
using Reqnroll;

namespace OrangeHrm.UiTests.Steps;

[Binding]
public sealed class Hooks
{
    private readonly ScenarioContext _scenarioContext;

    public Hooks(ScenarioContext scenarioContext)
    {
        _scenarioContext = scenarioContext;
    }

    [BeforeScenario]
    public void BeforeScenario()
    {
        DriverFactory.CreateDriver();
    }

    [AfterScenario]
    public void AfterScenario()
    {
        try
        {
            // If the scenario failed, attach a screenshot to Allure (best-effort).
            if (_scenarioContext.TestError is not null)
            {
                var driver = DriverFactory.Driver;
                if (driver is ITakesScreenshot takesScreenshot)
                {
                    var screenshot = takesScreenshot.GetScreenshot();

                    TryAddAllureAttachment(
                        name: "failure-screenshot",
                        type: "image/png",
                        content: screenshot.AsByteArray,
                        fileExtension: "png"
                    );
                }
            }
        }
        catch
        {
            // Best-effort attachment; do not mask the original failure / teardown.
        }
        finally
        {
            DriverFactory.QuitDriver();
        }
    }

    private static void TryAddAllureAttachment(string name, string type, byte[] content, string fileExtension)
    {
        try
        {
            // Allure.Net.Commons (2.14.x line) provides the AllureApi helper used by the adapters.
            // This writes into the raw results directory configured by allureConfig.json.
            AllureApi.AddAttachment(name, type, content, fileExtension);
        }
        catch
        {
            // Ignore attachment failures.
        }
    }
}
