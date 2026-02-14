@demo
Feature: Demo - Login (headless)

  Scenario: Admin can log in successfully
    Given the user is on the OrangeHRM login page
    When the user logs in as Admin
    Then login should succeed and Dashboard should be visible
