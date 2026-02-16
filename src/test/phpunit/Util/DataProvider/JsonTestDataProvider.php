<?php

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

namespace OrangeHRM\Tests\Util\DataProvider;

use InvalidArgumentException;
use RuntimeException;

/**
 * Simple JSON-based data provider for PHPUnit tests.
 *
 * Notes:
 * - OrangeHRM has an established YAML-based fixture/testcase pattern; this utility adds
 *   a parallel mechanism for test data sourced from external JSON files.
 * - Full JSON Schema validation is intentionally not introduced on the PHP side to avoid
 *   adding heavy dependencies; instead we validate a minimal contract for safety.
 */
class JsonTestDataProvider
{
    /**
     * Loads and decodes a JSON file into an associative array.
     *
     * PUBLIC_INTERFACE
     * @param string $absolutePath Absolute path to JSON file
     * @return array<string, mixed>
     */
    public static function loadJson(string $absolutePath): array
    {
        /** This is a public function. */
        if (!is_file($absolutePath)) {
            throw new InvalidArgumentException("JSON file not found: {$absolutePath}");
        }
        $raw = file_get_contents($absolutePath);
        if ($raw === false) {
            throw new RuntimeException("Unable to read JSON file: {$absolutePath}");
        }

        $data = json_decode($raw, true);
        if (!is_array($data)) {
            $err = function_exists('json_last_error_msg') ? json_last_error_msg() : 'unknown';
            throw new RuntimeException("Invalid JSON in {$absolutePath}: {$err}");
        }
        return $data;
    }

    /**
     * Extracts an array of test cases from a decoded JSON root and validates
     * it is a list of objects each containing at least a testCaseId.
     *
     * PUBLIC_INTERFACE
     * @param array<string, mixed> $root
     * @param string $key
     * @return array<int, array<string, mixed>>
     */
    public static function getCases(array $root, string $key): array
    {
        /** This is a public function. */
        if (!isset($root[$key]) || !is_array($root[$key])) {
            throw new InvalidArgumentException("Expected JSON root key `{$key}` to be an array.");
        }

        $cases = $root[$key];
        foreach ($cases as $idx => $case) {
            if (!is_array($case)) {
                throw new InvalidArgumentException("Test case at index {$idx} is not an object.");
            }
            if (!isset($case['testCaseId']) || !is_string($case['testCaseId']) || $case['testCaseId'] === '') {
                throw new InvalidArgumentException("Test case at index {$idx} missing non-empty `testCaseId`.");
            }
        }
        return $cases;
    }
}
