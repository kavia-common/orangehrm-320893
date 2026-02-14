package com.orangehrm.pages;

import com.orangehrm.support.DriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base page object with wait helpers.
 */
public abstract class BasePage {

    protected WebDriver driver() {
        return DriverFactory.getDriver();
    }

    protected WebDriverWait wait10() {
        return new WebDriverWait(driver(), Duration.ofSeconds(10));
    }

    protected WebDriverWait wait20() {
        return new WebDriverWait(driver(), Duration.ofSeconds(20));
    }

    protected WebElement visible(By locator) {
        return wait20().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement clickable(By locator) {
        return wait20().until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        clickable(locator).click();
    }

    protected void type(By locator, String value) {
        WebElement el = visible(locator);
        el.clear();
        el.sendKeys(value);
    }

    protected boolean isPresent(By locator) {
        try {
            driver().findElement(locator);
            return true;
        } catch (NoSuchElementException ignored) {
            return false;
        }
    }

    protected void safeClick(By locator) {
        try {
            click(locator);
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver()).executeScript("arguments[0].click();", driver().findElement(locator));
        }
    }
}
