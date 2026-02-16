Feature: PIM (Personal Information Management)

  @pim @discoverySafe
  Scenario: Admin can add a new employee record (intent only)
    Given OrangeHRM is available at "http://localhost/orangehrm/web/index.php"
    And I am authenticated as "Admin"
    When I create a PIM employee with first name "Jane" and last name "Doe"
    Then the employee record should be created in scenario context
    And the employee full name should be "Jane Doe"

  @pim @discoverySafe
  Scenario: User can search for an employee (intent only)
    Given OrangeHRM is available at "http://localhost/orangehrm/web/index.php"
    And I am authenticated as "Admin"
    And a PIM employee exists with employee id "E123"
    When I search PIM employees by employee id "E123"
    Then I should see search results containing employee id "E123"
