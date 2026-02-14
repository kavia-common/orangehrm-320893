package com.orangehrm.pages;

import com.orangehrm.support.TestConfig;
import org.openqa.selenium.By;

/**
 * Page object for OrangeHRM login page.
 */
public class LoginPage extends BasePage {

    private final By username = By.name("username");
    private final By password = By.name("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By invalidCredentialsToast = By.xpath("//*[contains(@class,'oxd-alert-content-text') and contains(.,'Invalid credentials')]");
    private final By requiredMessage = By.xpath("//*[contains(@class,'oxd-input-group__message') and normalize-space(.)='Required']");

    public void open() {
        driver().get(TestConfig.baseUrl() + "/web/index.php/auth/login");
    }

    public void login(String user, String pass) {
        type(username, user);
        type(password, pass);
        click(loginButton);
    }

    public boolean isInvalidCredentialsShown() {
        return isPresent(invalidCredentialsToast);
    }

    public boolean isRequiredShown() {
        return isPresent(requiredMessage);
    }
}
