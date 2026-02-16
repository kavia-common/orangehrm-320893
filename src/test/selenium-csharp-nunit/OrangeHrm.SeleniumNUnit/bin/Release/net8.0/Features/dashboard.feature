Feature: Dashboard

  @dashboard @discoverySafe
  Scenario: Logged-in user has dashboard widgets configured (intent only)
    Given OrangeHRM is available at "http://localhost/orangehrm/web/index.php"
    And I am authenticated as "Admin"
    When I load the dashboard configuration
    Then I should have at least 1 dashboard widget in scenario context

  @dashboard @discoverySafe
  Scenario: User can personalize dashboard layout (intent only)
    Given dashboard widgets exist in scenario context
    When I move dashboard widget "Quick Launch" to position 1
    Then the dashboard widget "Quick Launch" should be at position 1
