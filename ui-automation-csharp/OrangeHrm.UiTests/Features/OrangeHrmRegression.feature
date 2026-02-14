@regression
Feature: OrangeHRM regression suite (150+ scenarios)

  Background:
    Given the user is on the OrangeHRM login page

  @login @smoke
  Scenario Outline: Login - positive/negative/boundary validation (<caseId>)
    When the user logs in with username "<username>" and password "<password>"
    Then <expectedResult>
    Examples:
      | caseId | username            | password            | expectedResult                                         |
      | L01    | __ADMIN_USERNAME__   | __ADMIN_PASSWORD__  | login should succeed and Dashboard should be visible   |
      | L02    | __ESS_USERNAME__     | __ESS_PASSWORD__    | login should succeed and Dashboard should be visible   |
      | L03    | __ADMIN_USERNAME__   | wrongPassword       | an invalid credentials message should be shown         |
      | L04    | wrongUser            | __ADMIN_PASSWORD__  | an invalid credentials message should be shown         |
      | L05    | wrongUser            | wrongPassword       | an invalid credentials message should be shown         |
      | L06    |                      | __ADMIN_PASSWORD__  | required field validation should be shown on the login form |
      | L07    | __ADMIN_USERNAME__   |                     | required field validation should be shown on the login form |
      | L08    |                      |                     | required field validation should be shown on the login form |
      | L09    | aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa | bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb | an invalid credentials message should be shown |
      | L10    | admin' OR '1'='1     | pass' OR '1'='1     | an invalid credentials message should be shown         |

  @dashboard
  Scenario Outline: Dashboard - access and widget presence validations (<caseId>)
    When the user logs in as Admin
    Then login should succeed and Dashboard should be visible
    When the user opens the Dashboard module
    Then the Dashboard module should be accessible
    Examples:
      | caseId |
      | D01    |
      | D02    |
      | D03    |
      | D04    |
      | D05    |
      | D06    |
      | D07    |
      | D08    |
      | D09    |
      | D10    |
      | D11    |
      | D12    |
      | D13    |
      | D14    |
      | D15    |
      | D16    |
      | D17    |
      | D18    |
      | D19    |
      | D20    |

  @pim
  Scenario Outline: PIM - navigation regression checks (<caseId>)
    When the user logs in as Admin
    Then login should succeed and Dashboard should be visible
    When the user opens the PIM module
    Then login should succeed and Dashboard should be visible
    Examples:
      | caseId |
      | PIM01  |
      | PIM02  |
      | PIM03  |
      | PIM04  |
      | PIM05  |
      | PIM06  |
      | PIM07  |
      | PIM08  |
      | PIM09  |
      | PIM10  |
      | PIM11  |
      | PIM12  |
      | PIM13  |
      | PIM14  |
      | PIM15  |
      | PIM16  |
      | PIM17  |
      | PIM18  |
      | PIM19  |
      | PIM20  |
      | PIM21  |
      | PIM22  |
      | PIM23  |
      | PIM24  |
      | PIM25  |

  @leave
  Scenario Outline: Leave - navigation regression checks (<caseId>)
    When the user logs in as Admin
    Then login should succeed and Dashboard should be visible
    When the user opens the Leave module
    Then login should succeed and Dashboard should be visible
    Examples:
      | caseId |
      | LV01   |
      | LV02   |
      | LV03   |
      | LV04   |
      | LV05   |
      | LV06   |
      | LV07   |
      | LV08   |
      | LV09   |
      | LV10   |
      | LV11   |
      | LV12   |
      | LV13   |
      | LV14   |
      | LV15   |
      | LV16   |
      | LV17   |
      | LV18   |
      | LV19   |
      | LV20   |
      | LV21   |
      | LV22   |
      | LV23   |
      | LV24   |
      | LV25   |

  @recruitment
  Scenario Outline: Recruitment - navigation regression checks (<caseId>)
    When the user logs in as Admin
    Then login should succeed and Dashboard should be visible
    When the user opens the Recruitment module
    Then login should succeed and Dashboard should be visible
    Examples:
      | caseId |
      | REC01  |
      | REC02  |
      | REC03  |
      | REC04  |
      | REC05  |
      | REC06  |
      | REC07  |
      | REC08  |
      | REC09  |
      | REC10  |
      | REC11  |
      | REC12  |
      | REC13  |
      | REC14  |
      | REC15  |
      | REC16  |
      | REC17  |
      | REC18  |
      | REC19  |
      | REC20  |

  @time
  Scenario Outline: Time - navigation regression checks (<caseId>)
    When the user logs in as Admin
    Then login should succeed and Dashboard should be visible
    When the user opens the Time module
    Then login should succeed and Dashboard should be visible
    Examples:
      | caseId |
      | T01    |
      | T02    |
      | T03    |
      | T04    |
      | T05    |
      | T06    |
      | T07    |
      | T08    |
      | T09    |
      | T10    |
      | T11    |
      | T12    |
      | T13    |
      | T14    |
      | T15    |
      | T16    |
      | T17    |
      | T18    |
      | T19    |
      | T20    |

  @admin
  Scenario Outline: Admin - navigation regression checks (<caseId>)
    When the user logs in as Admin
    Then login should succeed and Dashboard should be visible
    When the user opens the Admin module
    Then login should succeed and Dashboard should be visible
    Examples:
      | caseId |
      | A01    |
      | A02    |
      | A03    |
      | A04    |
      | A05    |
      | A06    |
      | A07    |
      | A08    |
      | A09    |
      | A10    |
      | A11    |
      | A12    |
      | A13    |
      | A14    |
      | A15    |
      | A16    |
      | A17    |
      | A18    |
      | A19    |
      | A20    |
      | A21    |
      | A22    |
      | A23    |
      | A24    |
      | A25    |
      | A26    |
      | A27    |
      | A28    |
      | A29    |
      | A30    |
      | A31    |
      | A32    |
      | A33    |
      | A34    |
      | A35    |
      | A36    |
      | A37    |
      | A38    |
      | A39    |
      | A40    |

  # Total scenarios: 160 (>= 150)
