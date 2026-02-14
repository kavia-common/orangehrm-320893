/**
 * Data-driven API test example using external JSON fixtures.
 *
 * Note: The uploaded apiData.json uses example endpoints like "/api/login" and "/api/employee"
 * which may not exist in this OrangeHRM test environment. This spec demonstrates the
 * data-driven mechanism and runs requests against the provided endpoint paths.
 */

describe('API (Data-Driven) - Requests from fixtures/apiData.json', function () {
  beforeEach(function () {
    cy.task('db:reset');
  });

  it('runs api cases from fixtures/apiData.json', function () {
    cy.ddtLoad({
      type: 'json',
      dataPath: 'apiData.json',
      schemaPath: 'test-data.schema.json',
      collectionKey: 'apiTests',
    }).then(({cases}) => {
      cases.forEach((tc) => {
        cy.log(`[${tc.testCaseId}] ${tc.method} ${tc.endpoint}`);

        const method = (tc.method || 'GET').toUpperCase();

        cy.request({
          method,
          url: tc.endpoint,
          failOnStatusCode: false, // keep it robust across environments
          body: tc.payload,
        }).then((response) => {
          // If expectedStatusCode is provided, assert it; otherwise just log.
          if (typeof tc.expectedStatusCode === 'number') {
            expect(response.status).to.eq(tc.expectedStatusCode);
          }
        });
      });
    });
  });
});
