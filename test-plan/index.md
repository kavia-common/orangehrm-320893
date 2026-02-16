# OrangeHRM Master Test Plan

## Overview

This document defines the repository’s overall testing strategy and coverage goals for the OrangeHRM-based application in this workspace. It focuses on functional testing across core HR modules and describes how UI and API automation are organized, how data-driven testing is implemented, and what “good coverage” means for this codebase.

This plan is intentionally written as a master plan. Detailed, executable test cases are expected to live in the automation projects and test suites referenced in this repository.

## Objectives

The goals of this test plan are to ensure that:

The most business-critical user journeys across the OrangeHRM modules are verified regularly via automated UI and API tests. The test suites provide confidence in core functionality such as authentication, employee data management, leave workflows, recruitment flows, time tracking, administrative configuration, and dashboard visibility. The test strategy provides a repeatable approach for regression, smoke, and data-driven test execution. Test data and results artifacts are discoverable and traceable.

## In-Scope Modules

This master plan covers the following functional modules at a minimum:

### Login (Authentication)

Authentication is considered a foundational dependency for all other modules. The plan includes login success and failure cases, session behavior, navigation to landing pages, and basic access checks for role-based areas.

### PIM (Personnel Information Management)

The plan includes employee record lifecycle operations and core employee profile features. Coverage prioritizes add/edit/search/view flows and key validations for employee profile data.

### Leave

The plan includes applying for leave, leave period configuration dependencies, entitlement and balance visibility (where applicable), and request status workflows such as “Pending Approval” and related outcomes.

### Recruitment

The plan includes candidate and vacancy flows, candidate actions, and the main recruitment lifecycle that is typically exercised in both UI and API layers depending on availability.

### Time

The plan includes time module workflows such as timesheet interactions and basic time entry or submission flows, emphasizing data integrity and predictable results.

### Admin

The plan includes administrative configuration workflows, emphasizing entities commonly managed through Admin screens and APIs such as job titles and job categories, plus access/permission boundaries as applicable.

### Dashboard

The plan includes basic verification that the dashboard is accessible after login and that key widgets and navigation links render correctly for intended roles.

## Test Types and Levels

### UI Automation Testing

UI automation validates end-to-end workflows from the user’s perspective, including navigation, form input, validation messages, and key page state changes. In this repository, UI automation is implemented in both Java and C# stacks.

The UI automation projects are:

The Java UI automation suite under `ui-automation-java/`, implemented with Selenium WebDriver, Cucumber, and JUnit Platform.

The C# UI automation suite under `ui-automation-csharp/`, implemented with Selenium WebDriver, Reqnroll/SpecFlow-compatible BDD style, and NUnit.

UI automation is best suited for validating multi-step journeys such as login and module navigation, and for verifying user-visible validations and page behaviors.

### API Automation Testing

API automation validates backend contracts and business workflows at the API layer. It is used to verify authorization boundaries, CRUD flows for module entities, pagination and filtering behaviors, and error handling.

The API automation projects are:

The Java API automation suite under `api-automation-java/`, implemented with REST Assured and JUnit 5.

The C# API automation suite under `api-automation-csharp/`, implemented with RestSharp and NUnit.

Additionally, this repository includes Cypress functional tests that demonstrate both UI and API data-driven approaches under `src/test/functional/cypress/`.

### Data-Driven Functional Testing (Cypress)

This repository includes a data-driven test mechanism for Cypress specs. The data-driven loader supports JSON first and is implemented in `src/test/functional/cypress/support/dataProvider.js`. Tests can load JSON fixtures from `src/test/functional/cypress/fixtures/`, optionally validate them using JSON Schema, and then iterate over each test case entry.

The data-driven mechanism is demonstrated by example Cypress specs, including:

`src/test/functional/cypress/e2e/integration/feature/core/login_data_driven.cy.js` for data-driven UI login cases.

`src/test/functional/cypress/e2e/integration/api/admin/jobTitles_data_driven_API.cy.js` for data-driven API request execution.

## Data-Driven Approach

### Design

The data-driven approach is intended to separate test logic from test data. A Cypress spec can load a dataset file (for example `loginData.json`), validate it against a JSON schema (for example `test-data.schema.json`), and then run the same test logic for each test case row in the dataset.

This approach enables:

Easier addition of new test cases without editing the test spec logic.

Consistency of required fields and structure through schema validation.

Traceability through unique `testCaseId` values.

### Current JSON Schema

The JSON schema used by the Cypress data-driven loader is:

`src/test/functional/cypress/fixtures/test-data.schema.json`

The schema supports collections such as `loginTests`, `apiTests`, `timeTests`, `leaveTests`, and other module keys. Each entry requires at least a non-empty `testCaseId`. Additional properties are permitted to keep datasets flexible.

### JSON Test Data Files (Linked)

The following JSON test data fixtures currently exist in this repository and are suitable to be referenced as examples for data-driven test expansion:

`src/test/functional/cypress/fixtures/loginData.json` provides login test cases (success, invalid password, empty credentials).

`src/test/functional/cypress/fixtures/apiData.json` provides example API test cases with `endpoint`, `method`, `payload`, and `expectedStatusCode`. The corresponding Cypress spec notes that these endpoints are illustrative and may not exist in a given OrangeHRM environment.

`src/test/functional/cypress/fixtures/leaveData.json` provides an example leave dataset with expected status values.

`src/test/functional/cypress/fixtures/timeData.json` provides an example time dataset with timesheet submission attributes.

If additional JSON datasets are uploaded later, they should be stored alongside these fixtures (or referenced by the same directory convention) so the data-driven loader can discover them consistently.

## Coverage Goals

### Functional Coverage Goals

Coverage targets are defined as a combination of “module-level breadth” and “workflow depth”.

Module-level breadth means that each in-scope module has at least one smoke test and a small set of regression scenarios covering the most common paths.

Workflow depth means that for each module, at least one key workflow is exercised end-to-end, including the primary success path and representative negative/validation scenarios.

For the in-scope modules in this plan, the coverage goals are:

Login: Cover success, invalid credentials, empty credential validation, and post-login landing path behavior.

PIM: Cover employee search/list and at least one representative create or update workflow (depending on environment constraints).

Leave: Cover leave apply flow, leave type selection, date range selection, and verification of an expected status or outcome.

Recruitment: Cover candidate or vacancy flows (view/list and at least one create/update or action flow).

Time: Cover timesheet or time entry submission and verification of outcome state.

Admin: Cover at least one Admin-managed entity end-to-end via API tests (for example job titles) plus representative UI management flows when stable.

Dashboard: Cover that the dashboard is accessible after login and that navigation to major modules is visible for the intended role.

### API Contract Coverage Goals

API coverage is considered sufficient when:

A set of core endpoints per module are verified for authentication requirements (unauthenticated requests fail appropriately).

At least one happy-path CRUD flow is executed for representative entities where the environment supports mutation.

Responses are validated at least at the level of status code, basic required fields, and error contract shape for negative cases.

### Non-Goals

This master plan does not attempt to define complete UI pixel-perfect testing, cross-browser exhaustive matrices, or performance testing. Those can be added as separate plans when needed.

## Test Execution Strategy

### Recommended Suites

This plan assumes three primary suites:

A Smoke suite that runs quickly and validates that the system is usable, login works, and a small set of critical module checks pass.

A Regression suite that runs a larger matrix of module scenarios.

A Data-driven suite that validates that datasets and schema-driven iteration behave correctly, and that key datasets execute successfully.

### Automation Project Entry Points

For details on how to run the automation suites, refer to the existing automation module READMEs:

`ui-automation-java/README.md`

`ui-automation-csharp/README.md`

`api-automation-java/README.md`

`api-automation-csharp/README.md`

## Test Artifacts and Reporting

This repository contains a `test-artifacts/` directory which includes example execution outputs such as raw reports and Allure result artifacts. These artifacts are useful for troubleshooting and for verifying that reporting integrations are producing expected outputs, but they are not a substitute for baseline pass/fail gating in CI.

## Maintenance and Evolution

This master test plan should be updated when:

A new module becomes in-scope or a module is de-scoped.

The automation architecture changes (for example, Cypress becomes the primary runner, or API auth mechanisms change).

New standard datasets are added (new JSON fixtures, schemas, or data providers).

Coverage goals evolve to include additional roles, permissions, or advanced workflows.

## References

UI automation (Java): `ui-automation-java/README.md`

UI automation (C#): `ui-automation-csharp/README.md`

API automation (Java): `api-automation-java/README.md`

API automation (C#): `api-automation-csharp/README.md`

Cypress data provider: `src/test/functional/cypress/support/dataProvider.js`

Cypress JSON schema: `src/test/functional/cypress/fixtures/test-data.schema.json`

Cypress test data fixtures:
`src/test/functional/cypress/fixtures/loginData.json`
`src/test/functional/cypress/fixtures/apiData.json`
`src/test/functional/cypress/fixtures/leaveData.json`
`src/test/functional/cypress/fixtures/timeData.json`
