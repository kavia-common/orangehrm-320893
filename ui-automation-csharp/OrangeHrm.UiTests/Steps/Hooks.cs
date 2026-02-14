using OrangeHrm.UiTests.Support;
using Reqnroll;

namespace OrangeHrm.UiTests.Steps;

[Binding]
public sealed class Hooks
{
    [BeforeScenario]
    public void BeforeScenario()
    {
        DriverFactory.CreateDriver();
    }

    [AfterScenario]
    public void AfterScenario()
    {
        DriverFactory.QuitDriver();
    }
}
