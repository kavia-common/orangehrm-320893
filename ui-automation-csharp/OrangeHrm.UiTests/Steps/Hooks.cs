using System;
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
        Console.WriteLine($"[C#][Hooks] Starting scenario: {_scenarioContext.ScenarioInfo.Title}");
        DriverFactory.CreateDriver();
    }

    [AfterScenario]
    public void AfterScenario()
    {
        try
        {
            if (_scenarioContext.TestError is not null)
            {
                Console.WriteLine($"[C#][Hooks] Scenario failed: {_scenarioContext.ScenarioInfo.Title}");
                Console.WriteLine($"[C#][Hooks] Error: {_scenarioContext.TestError.GetType().Name}: {_scenarioContext.TestError.Message}");
            }
            else
            {
                Console.WriteLine($"[C#][Hooks] Scenario passed: {_scenarioContext.ScenarioInfo.Title}");
            }
        }
        finally
        {
            DriverFactory.QuitDriver();
        }
    }
}
