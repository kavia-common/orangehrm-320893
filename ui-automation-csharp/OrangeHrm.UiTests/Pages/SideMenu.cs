using OpenQA.Selenium;

namespace OrangeHrm.UiTests.Pages;

public class SideMenu : BasePage
{
    private By MenuItem(string name) =>
        By.XPath($"//span[contains(@class,'oxd-main-menu-item--name') and normalize-space(.)='{name}']/ancestor::a");

    public void OpenDashboard() => Click(MenuItem("Dashboard"));
    public void OpenPim() => Click(MenuItem("PIM"));
    public void OpenLeave() => Click(MenuItem("Leave"));
    public void OpenRecruitment() => Click(MenuItem("Recruitment"));
    public void OpenTime() => Click(MenuItem("Time"));
    public void OpenAdmin() => Click(MenuItem("Admin"));
}
