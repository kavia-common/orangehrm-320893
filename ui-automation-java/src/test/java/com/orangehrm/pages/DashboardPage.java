package com.orangehrm.pages;

import org.openqa.selenium.By;

/**
 * Dashboard page object.
 */
public class DashboardPage extends BasePage {

    private final By header = By.xpath("//h6[normalize-space(.)='Dashboard']");

    public boolean isLoaded() {
        return isPresent(header);
    }
}
