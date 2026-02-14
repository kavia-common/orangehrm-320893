package com.orangehrm.pages;

import org.openqa.selenium.By;

/**
 * Left side menu navigation for OrangeHRM.
 */
public class SideMenu extends BasePage {

    private By menuItem(String name) {
        return By.xpath("//span[contains(@class,'oxd-main-menu-item--name') and normalize-space(.)='" + name + "']/ancestor::a");
    }

    public void openDashboard() {
        safeClick(menuItem("Dashboard"));
    }

    public void openPim() {
        safeClick(menuItem("PIM"));
    }

    public void openLeave() {
        safeClick(menuItem("Leave"));
    }

    public void openRecruitment() {
        safeClick(menuItem("Recruitment"));
    }

    public void openTime() {
        safeClick(menuItem("Time"));
    }

    public void openAdmin() {
        safeClick(menuItem("Admin"));
    }
}
