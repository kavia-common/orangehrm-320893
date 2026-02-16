Feature: Leave

  @leave @discoverySafe
  Scenario: Employee can apply for leave (intent only)
    Given OrangeHRM is available at "http://localhost/orangehrm/web/index.php"
    And I am authenticated as "Admin"
    When I apply for leave of type "Annual" from "2025-01-10" to "2025-01-12"
    Then a leave request should be created in scenario context
    And the leave request status should be "Pending Approval"

  @leave @discoverySafe
  Scenario: Manager can approve a leave request (intent only)
    Given a leave request exists with reference "LR-001" and status "Pending Approval"
    When I approve leave request with reference "LR-001"
    Then the leave request status should be "Approved"
