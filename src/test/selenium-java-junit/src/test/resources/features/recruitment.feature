Feature: Recruitment

  @recruitment @discoverySafe
  Scenario: Recruiter can add a candidate (intent only)
    Given OrangeHRM is available at "http://localhost/orangehrm/web/index.php"
    And I am authenticated as "Admin"
    When I add a recruitment candidate "Sam" "Taylor" for vacancy "QA Engineer"
    Then the candidate should be created in scenario context
    And the candidate status should be "Application Initiated"

  @recruitment @discoverySafe
  Scenario: Recruiter can shortlist a candidate (intent only)
    Given a recruitment candidate exists with reference "C-100" and status "Application Initiated"
    When I shortlist recruitment candidate with reference "C-100"
    Then the candidate status should be "Shortlisted"
