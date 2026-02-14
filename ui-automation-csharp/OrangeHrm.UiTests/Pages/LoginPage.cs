using OpenQA.Selenium;
using OrangeHrm.UiTests.Support;

namespace OrangeHrm.UiTests.Pages;

public class LoginPage : BasePage
{
    private readonly By _username = By.Name("username");
    private readonly By _password = By.Name("password");
    private readonly By _loginButton = By.CssSelector("button[type='submit']");
    private readonly By _invalidCreds = By.XPath("//*[contains(@class,'oxd-alert-content-text') and contains(.,'Invalid credentials')]");
    private readonly By _required = By.XPath("//*[contains(@class,'oxd-input-group__message') and normalize-space(.)='Required']");

    public void Open()
    {
        Driver.Navigate().GoToUrl($"{TestConfig.BaseUrl}/web/index.php/auth/login");
    }

    public void Login(string username, string password)
    {
        Type(_username, username);
        Type(_password, password);
        Click(_loginButton);
    }

    public bool IsInvalidCredsShown() => IsPresent(_invalidCreds);

    public bool IsRequiredShown() => IsPresent(_required);
}
