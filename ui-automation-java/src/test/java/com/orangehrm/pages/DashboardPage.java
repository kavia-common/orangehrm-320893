package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

/**
 * Dashboard page object.
 */
public class DashboardPage extends BasePage {

    private final By header = By.xpath("//h6[normalize-space(.)='Dashboard']");

    public boolean isLoaded() {
        try {
            // Use an explicit wait to avoid flaky post-login timing issues.
            visible(header);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
