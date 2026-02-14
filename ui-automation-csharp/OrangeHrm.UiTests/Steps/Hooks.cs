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
            // If the scenario failed, attach a screenshot to Allure.
            if (_scenarioContext.TestError is not null)
            {
                var driver = DriverFactory.Driver;
                if (driver is ITakesScreenshot takesScreenshot)
                {
                    var screenshot = takesScreenshot.GetScreenshot();
                    AllureApi.AddAttachment("failure-screenshot", "image/png", screenshot.AsByteArray);
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
}
