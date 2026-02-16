using System;
using OpenQA.Selenium;
using OpenQA.Selenium.Support.UI;

namespace OrangeHrm.SeleniumNUnit.Pages
{
    /// <summary>
    /// Minimal page object for OrangeHRM login page.
    /// This skeleton intentionally keeps selectors minimal and stable.
    /// </summary>
    public sealed class LoginPage
    {
        private readonly IWebDriver _driver;

        public LoginPage(IWebDriver driver)
        {
            _driver = driver;
        }

        // PUBLIC_INTERFACE
        /// <summary>
        /// Navigate to the login page and wait for a key element to be present.
        /// </summary>
        public void Open(string baseUrl)
        {
            if (string.IsNullOrWhiteSpace(baseUrl))
            {
                throw new ArgumentException("Base URL must be provided in real browser mode.", nameof(baseUrl));
            }

            _driver.Navigate().GoToUrl(baseUrl);

            // Wait for login form presence (OrangeHRM typically uses input[name="username"]).
            var wait = new WebDriverWait(_driver, TimeSpan.FromSeconds(10));
            wait.Until(d => d.FindElements(By.Name("username")).Count > 0);
        }
    }
}
