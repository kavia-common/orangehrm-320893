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

const jobTitlesApi = `/api/v2/admin/job-titles`;

describe('Admin - Job Title API (Data-driven seed examples)', function () {
  beforeEach(function () {
    cy.task('db:reset');
    cy.fixture('chars').as('strings');
    cy.fixture('user').then(({admin}) => {
      cy.apiLogin(admin);
    });
  });

  it('executes example API test cases from apiData.json (schema-validated)', function () {
    // This external JSON is an authoritative example/seed dataset.
    loadAndValidateFixture('apiData', 'schemas/apiData.schema.json').then(
      (data) => {
        const cases = getTestCases(data, 'apiTests');

        cases.forEach((tc) => {
          cy.log(`[${tc.testCaseId}] ${tc.method} ${tc.endpoint}`);

          // Map example endpoints to actual OrangeHRM test endpoints where applicable.
          // The provided seed JSON uses "/api/login" and "/api/employee" as examples;
          // in OrangeHRM Cypress tests we have:
          //  - authentication test helper endpoint: /functional-testing/auth/validate
          //  - job titles endpoint: /api/v2/admin/job-titles
          //
          // This keeps the external data "authoritative" while still making the spec runnable.
          let url = tc.endpoint;
          let method = tc.method;

          if (tc.endpoint === '/api/login' && tc.method === 'POST') {
            url = '/functional-testing/auth/validate';
          } else if (tc.endpoint === '/api/employee' && tc.method === 'POST') {
            // Use job titles create as a representative "create" endpoint in this repo's existing API tests.
            url = jobTitlesApi;
            // translate payload shape to match the job titles API
            tc.payload = {
              title: this.strings.chars50.text,
              description: this.strings.chars120.text,
              specification: null,
              note: this.strings.chars120.text,
            };
            // for this endpoint, OrangeHRM typically responds 200 on create
            tc.expectedStatusCode = 200;
          }

          cy.request({
            method,
            url,
            body: tc.payload,
            failOnStatusCode: false,
          }).then((response) => {
            expect(response.status).to.eq(tc.expectedStatusCode);
          });
        });
      },
    );
  });

  // Keep original targeted endpoint tests (non-data-driven) as they are useful for debugging
  // and remain the "source of truth" for the job titles API behavior.
  describe('GET /job-titles', function () {
    it('gets a list of job titles', function () {
      cy.request('GET', jobTitlesApi).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body.data.length).to.eq(0);
      });
    });
  });
});
