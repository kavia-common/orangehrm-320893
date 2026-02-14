using System;
using OpenQA.Selenium;
using OpenQA.Selenium.Support.UI;
using SeleniumExtras.WaitHelpers;
using OrangeHrm.UiTests.Support;

namespace OrangeHrm.UiTests.Pages;

public abstract class BasePage
{
    protected IWebDriver Driver => DriverFactory.Driver;

    protected WebDriverWait Wait(int seconds = 20) => new(Driver, TimeSpan.FromSeconds(seconds));

    protected IWebElement Visible(By locator) => Wait().Until(ExpectedConditions.ElementIsVisible(locator));

    protected IWebElement Clickable(By locator) => Wait().Until(ExpectedConditions.ElementToBeClickable(locator));

    protected void Click(By locator) => Clickable(locator).Click();

    protected void Type(By locator, string value)
    {
        var el = Visible(locator);
        el.Clear();
        el.SendKeys(value);
    }

    protected bool IsPresent(By locator)
    {
        try
        {
            Driver.FindElement(locator);
            return true;
        }
        catch (NoSuchElementException)
        {
            return false;
        }
    }
}
