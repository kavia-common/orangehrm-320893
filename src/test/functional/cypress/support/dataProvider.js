/**
 * Data-driven testing support for Cypress specs.
 *
 * This module intentionally supports JSON first, but is designed so CSV/Excel providers
 * can be added later with the same interface (IDataProvider).
 */

/**
 * @typedef {Object} DataProviderOptions
 * @property {string} [schemaPath] - Optional schema path (for validation).
 */

/**
 * @interface IDataProvider
 * @property {(dataPath: string, options?: DataProviderOptions) => Cypress.Chainable<any>} load
 *   Loads raw dataset from a path (typically under cypress/fixtures).
 * @property {(dataset: any, schemaPath: string) => Cypress.Chainable<{valid: boolean, errors: any[]}>} validate
 *   Validates dataset against a JSON schema.
 * @property {(dataset: any, collectionKey?: string) => any[]} cases
 *   Returns an array of test cases from dataset. If collectionKey is omitted, attempts auto-detect.
 */

/**
 * Small helper to make errors readable in Cypress runner output.
 * @param {any[]} errors
 * @returns {string}
 */
function formatAjvErrors(errors) {
  if (!errors || errors.length === 0) return 'No validation errors';
  return errors
    .map((e) => {
      const instancePath = e.instancePath || '(root)';
      const message = e.message || 'invalid';
      return `${instancePath} ${message}`;
    })
    .join('\n');
}

/**
 * JSON-backed data provider.
 * Uses Cypress fixture loading and validates using AJV (json-schema).
 */
class JsonDataProvider {
  /**
   * @param {import('ajv').default} ajv
   */
  constructor(ajv) {
    this.ajv = ajv;
  }

  load(dataPath) {
    // We intentionally use cy.fixture so the path resolves relative to cypress/fixtures.
    return cy.fixture(dataPath);
  }

  validate(dataset, schemaPath) {
    return cy.fixture(schemaPath).then((schema) => {
      // Use addSchema + getSchema so schema can reference itself or shared defs later.
      // We key it by schemaPath to avoid collisions.
      const schemaId = `schema:${schemaPath}`;
      try {
        this.ajv.removeSchema(schemaId);
      } catch (_) {
        // ignore - removeSchema throws if not found in some ajv versions
      }
      this.ajv.addSchema(schema, schemaId);
      const validate = this.ajv.getSchema(schemaId);
      if (!validate) {
        return {valid: false, errors: [{message: `Unable to compile schema: ${schemaPath}`}]};
      }

      const valid = validate(dataset);
      return {valid: !!valid, errors: validate.errors || []};
    });
  }

  cases(dataset, collectionKey) {
    if (collectionKey) {
      const value = dataset?.[collectionKey];
      if (!Array.isArray(value)) {
        throw new Error(
          `Expected dataset["${collectionKey}"] to be an array, got ${typeof value}`,
        );
      }
      return value;
    }

    // Auto-detect: pick the first array property on the root object.
    if (dataset && typeof dataset === 'object') {
      const firstArrayKey = Object.keys(dataset).find((k) => Array.isArray(dataset[k]));
      if (firstArrayKey) return dataset[firstArrayKey];
    }
    throw new Error(
      'Unable to infer test case collection key from dataset. Pass collectionKey explicitly.',
    );
  }
}

/**
 * Provider registry so we can support csv/excel later without changing tests:
 * - Add a new provider implementation
 * - Register under a new type
 * - cy.ddtLoad({type: 'csv', ...}) works
 */
const providerRegistry = {};

/**
 * Registers an IDataProvider by type.
 * @param {string} type
 * @param {IDataProvider} provider
 */
function registerProvider(type, provider) {
  providerRegistry[type] = provider;
}

/**
 * Gets an IDataProvider by type.
 * @param {string} type
 * @returns {IDataProvider}
 */
function getProvider(type) {
  const provider = providerRegistry[type];
  if (!provider) {
    throw new Error(
      `No data provider registered for type "${type}". Registered: ${Object.keys(
        providerRegistry,
      ).join(', ')}`,
    );
  }
  return provider;
}

/**
 * Default provider setup (JSON).
 * AJV is required as a devDependency in src/test/functional/package.json.
 */
function initDefaultProviders() {
  // Lazy require so Cypress bundler only includes when used.
  // eslint-disable-next-line global-require
  const Ajv = require('ajv');
  const ajv = new Ajv({
    allErrors: true,
    strict: false, // keep schemas flexible for now
  });

  registerProvider('json', new JsonDataProvider(ajv));
}

/**
 * PUBLIC_INTERFACE
 * Loads and validates a data-driven test dataset.
 *
 * @param {Object} params
 * @param {'json'|'csv'|'excel'} [params.type='json'] - Provider type.
 * @param {string} params.dataPath - Path under cypress/fixtures.
 * @param {string} [params.schemaPath] - Schema path under cypress/fixtures.
 * @param {string} [params.collectionKey] - Key that holds test case array.
 * @param {boolean} [params.failOnSchemaError=true] - If true, fails the test on schema mismatch.
 * @returns {Cypress.Chainable<{dataset: any, cases: any[]}>}
 */
export function ddtLoad({
  type = 'json',
  dataPath,
  schemaPath,
  collectionKey,
  failOnSchemaError = true,
}) {
  if (Object.keys(providerRegistry).length === 0) {
    initDefaultProviders();
  }
  const provider = getProvider(type);

  return provider.load(dataPath).then((dataset) => {
    if (schemaPath) {
      return provider.validate(dataset, schemaPath).then(({valid, errors}) => {
        if (!valid && failOnSchemaError) {
          throw new Error(
            `DDT schema validation failed for "${dataPath}" using schema "${schemaPath}":\n${formatAjvErrors(
              errors,
            )}`,
          );
        }
        return {dataset, cases: provider.cases(dataset, collectionKey)};
      });
    }
    return {dataset, cases: provider.cases(dataset, collectionKey)};
  });
}
