#!/usr/bin/env bash
set -euo pipefail

# Generate Allure HTML report for the C# NUnit skeleton.
# Expects allure-results in ./allure-results.

export PATH="/home/kavia/.local/bin:${PATH}"

allure generate "./allure-results" -o "./allure-report" --clean

echo "Allure report generated at: $(pwd)/allure-report/index.html"
