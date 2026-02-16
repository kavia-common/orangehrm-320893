/**
 * Data-driven testing utilities for Cypress E2E tests.
 *
 * Centralizes:
 *  - loading external JSON fixture data
 *  - validating fixture data against JSON Schemas
 *
 * This keeps test specs clean and ensures fixture contracts are enforced.
 */

import Ajv from 'ajv';

/**
 * Single Ajv instance for the Cypress bundle.
 * allErrors helps produce a useful error message when validation fails.
 */
const ajv = new Ajv({allErrors: true, strict: false});

/**
 * Loads a fixture JSON file and validates it against a schema JSON file.
 *
 * Note: this uses cy.fixture() so the file must live under cypress/fixtures.
 *
 * PUBLIC_INTERFACE
 * @param {string} dataFixturePath e.g. "loginData"
 * @param {string} schemaFixturePath e.g. "schemas/loginData.schema.json"
 * @returns {Cypress.Chainable<any>} validated fixture data
 */
export function loadAndValidateFixture(dataFixturePath, schemaFixturePath) {
  /** This function is a public function. */
  return cy.fixture(schemaFixturePath).then((schema) => {
    const validate = ajv.compile(schema);

    return cy.fixture(dataFixturePath).then((data) => {
      const valid = validate(data);
      if (!valid) {
        // Throwing here fails the test early with an actionable error.
        throw new Error(
          `Fixture validation failed.\n` +
            `data="${dataFixturePath}" schema="${schemaFixturePath}"\n` +
            `errors=${JSON.stringify(validate.errors, null, 2)}`,
        );
      }
      return data;
    });
  });
}

/**
 * Helper to return a specific list under a root key (e.g. "loginTests").
 *
 * PUBLIC_INTERFACE
 * @param {any} root validated fixture root object
 * @param {string} key array key
 * @returns {any[]} list
 */
export function getTestCases(root, key) {
  /** This is a public function. */
  const list = root?.[key];
  if (!Array.isArray(list)) {
    throw new Error(`Expected fixture key "${key}" to be an array.`);
  }
  return list;
}
