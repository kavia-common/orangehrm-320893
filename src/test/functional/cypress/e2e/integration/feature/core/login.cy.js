/**
 * OrangeHRM is a comprehensive Human Resource Management (HRM) System that captures
 * all the essential functionalities required for any enterprise.
 * Copyright (C) 2006 OrangeHRM Inc., http://www.orangehrm.com
 *
 * OrangeHRM is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * OrangeHRM is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrangeHRM.
 * If not, see <https://www.gnu.org/licenses/>.
 */

import {
  getTestCases,
  loadAndValidateFixture,
} from '../../../support/testDataProvider';

describe('Core - Login Page (Data-driven)', function () {
  before(function () {
    cy.task('db:reset');
    cy.intercept('POST', '**/auth/validate').as('postLogin');

    // Load + validate external test data (authoritative JSON fixture)
    loadAndValidateFixture('loginData', 'schemas/loginData.schema.json').then(
      (data) => {
        this.loginData = data;
      },
    );
  });

  it('executes login scenarios from loginData.json', function () {
    const cases = getTestCases(this.loginData, 'loginTests');

    // Keep it simple: execute each case sequentially in one test to avoid
    // Cypress "dynamic test generation" limitations.
    cases.forEach((tc) => {
      cy.log(`[${tc.testCaseId}] ${tc.description}`);

      cy.visit('/auth/login');

      if (tc.username !== '') {
        cy.getOXDInput('Username').type(tc.username);
      }
      if (tc.password !== '') {
        cy.getOXDInput('Password').type(tc.password);
      }

      cy.getOXD('button').contains('Login').click();

      if (tc.expectedResult === 'success') {
        cy.wait('@postLogin')
          .its('response.headers')
          .should('have.property', 'location')
          .and('match', /dashboard\/index/);
      } else if (tc.expectedResult === 'validation_error') {
        cy.getOXDInput('Username').isInvalid('Required');
        cy.getOXDInput('Password').isInvalid('Required');
      } else if (tc.expectedResult === 'error') {
        // Keep assertion generic (app may show toast/inline error depending on version).
        // We at least assert we are still on the login screen (no redirect).
        cy.location('pathname').should('match', /\/auth\/login/);
      } else {
        throw new Error(`Unhandled expectedResult: ${tc.expectedResult}`);
      }
    });
  });
});
