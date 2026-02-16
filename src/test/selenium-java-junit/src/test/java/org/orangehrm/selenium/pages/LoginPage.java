package org.orangehrm.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Minimal Login page object.
 *
 * NOTE: This is a skeleton; selectors may need adjustment to match OrangeHRM UI version.
 */
public final class LoginPage {
    private final WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // PUBLIC_INTERFACE
    public void open(String baseUrl) {
        /**
         * Navigate to the login page.
         * @param baseUrl base URL (e.g., http://host/orangehrm/web/index.php)
         */
        driver.get(baseUrl);
    }

    // PUBLIC_INTERFACE
    public void login(String username, String password) {
        /**
         * Perform login. Selectors are placeholders and must be updated if the DOM differs.
         * @param username admin username
         * @param password admin password
         */
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }
}
