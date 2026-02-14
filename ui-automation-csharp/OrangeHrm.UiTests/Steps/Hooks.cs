using System;
using Allure.Commons;
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

                    // Allure.Commons 3.x no longer exposes the `AllureApi` helper used in older examples.
                    // Also, `AllureLifecycle.Instance` can throw if an Allure config is not present.
                    //
                    // We therefore:
                    // 1) try to use the currently active lifecycle if the adapter initialized it
                    // 2) if that’s unavailable, silently skip the attachment (do not fail teardown)
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
            // Prefer a lifecycle that is already initialized by the Allure adapters.
            // If none is available, do not attempt to create one (may require config).
            var lifecycle = AllureLifecycle.Current;
            if (lifecycle is null)
            {
                return;
            }

            lifecycle.AddAttachment(name, type, content, fileExtension);
        }
        catch
        {
            // Ignore attachment failures.
        }
    }
}
