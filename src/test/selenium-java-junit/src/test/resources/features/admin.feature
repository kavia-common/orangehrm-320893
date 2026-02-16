Feature: Admin

  @admin @discoverySafe
  Scenario: Admin can create a system user (intent only)
    Given OrangeHRM is available at "http://localhost/orangehrm/web/index.php"
    And I am authenticated as "Admin"
    When I create an admin system user with username "jdoe" and role "ESS"
    Then the system user should be created in scenario context
    And the system user role should be "ESS"

  @admin @discoverySafe
  Scenario: Admin can disable a system user (intent only)
    Given a system user exists with username "jdoe" and status "Enabled"
    When I disable the system user "jdoe"
    Then the system user status should be "Disabled"
