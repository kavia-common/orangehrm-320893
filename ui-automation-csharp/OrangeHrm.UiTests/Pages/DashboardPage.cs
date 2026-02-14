using OpenQA.Selenium;
using OpenQA.Selenium.Support.UI;

namespace OrangeHrm.UiTests.Pages;

public class DashboardPage : BasePage
{
    private readonly By _header = By.XPath("//h6[normalize-space(.)='Dashboard']");

    public bool IsLoaded()
    {
        try
        {
            _ = Visible(_header);
            return true;
        }
        catch (WebDriverTimeoutException)
        {
            return false;
        }
    }
}
