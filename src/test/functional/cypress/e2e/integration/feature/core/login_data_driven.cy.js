/**
 * Data-driven version of the login tests using external JSON fixtures.
 *
 * Demonstrates:
 * - external JSON file loading
 * - schema validation
 * - injecting multiple cases into a single Cypress spec
 */

describe('Core - Login Page (Data-Driven)', function () {
  before(function () {
    cy.task('db:reset');
    cy.intercept('POST', '**/auth/validate').as('postLogin');
  });

  it('runs login cases from fixtures/loginData.json', function () {
    cy.ddtLoad({
      type: 'json',
      dataPath: 'loginData.json',
      schemaPath: 'test-data.schema.json',
      collectionKey: 'loginTests',
    }).then(({cases}) => {
      cases.forEach((tc) => {
        cy.log(`[${tc.testCaseId}] ${tc.description || ''}`);

        cy.visit('/auth/login');

        // Always attempt to submit; assertions depend on expectedResult
        cy.getOXD('form').within(() => {
          cy.getOXDInput('Username').type(tc.username);
          cy.getOXDInput('Password').type(tc.password);
          cy.getOXD('button').contains('Login').click();
        });

        if (tc.expectedResult === 'success') {
          cy.wait('@postLogin')
            .its('response.headers')
            .should('have.property', 'location')
            .and('match', /dashboard\/index/);
        } else if (tc.expectedResult === 'validation_error') {
          cy.getOXDInput('Username').isInvalid('Required');
          cy.getOXDInput('Password').isInvalid('Required');
        } else if (tc.expectedResult === 'error') {
          // Keep the assertion generic (copy may vary); ensure we did not redirect.
          cy.url().should('include', '/auth/login');
        } else {
          throw new Error(
            `Unknown expectedResult "${tc.expectedResult}" for testCaseId=${tc.testCaseId}`,
          );
        }
      });
    });
  });
});
