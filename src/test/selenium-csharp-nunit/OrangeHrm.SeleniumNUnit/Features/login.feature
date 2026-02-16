Feature: Login

  Scenario: Successful login shows dashboard
    Given OrangeHRM is available at "http://localhost/orangehrm/web/index.php"
    When I login as "Admin" with password "admin123"
    Then I should see the dashboard
