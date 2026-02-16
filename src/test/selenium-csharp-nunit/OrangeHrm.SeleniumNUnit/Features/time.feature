Feature: Time

  @time @discoverySafe
  Scenario: Employee can submit a timesheet entry (intent only)
    Given OrangeHRM is available at "http://localhost/orangehrm/web/index.php"
    And I am authenticated as "Admin"
    When I create a timesheet entry for project "Internal" activity "Development" on "2025-02-01" with hours 8
    Then the timesheet entry should be created in scenario context
    And the timesheet entry total hours should be 8

  @time @discoverySafe
  Scenario: Supervisor can approve a timesheet (intent only)
    Given a timesheet exists with reference "TS-001" and status "Submitted"
    When I approve timesheet with reference "TS-001"
    Then the timesheet status should be "Approved"
