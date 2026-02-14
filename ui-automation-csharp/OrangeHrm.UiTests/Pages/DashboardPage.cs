using OpenQA.Selenium;

namespace OrangeHrm.UiTests.Pages;

public class DashboardPage : BasePage
{
    private readonly By _header = By.XPath("//h6[normalize-space(.)='Dashboard']");
    public bool IsLoaded() => IsPresent(_header);
}
