# OrangeHRM UI Automation (C# + Reqnroll/SpecFlow)

Selenium WebDriver UI automation for OrangeHRM demo: https://opensource-demo.orangehrmlive.com  
- Framework: .NET 8 + Reqnroll (SpecFlow-compatible) + NUnit  
- Execution: local Chrome / Firefox / Edge (no Grid)  
- Configuration: `runsettings.local.runsettings` + optional `.env` (not committed)

## Prerequisites
- .NET SDK 8+
- Chrome/Firefox/Edge installed

## Configuration
Edit `OrangeHrm.UiTests/runsettings.local.runsettings` and set:
- ADMIN/ESS credentials (placeholders are provided)
- BROWSER: `chrome|firefox|edge`
- HEADLESS: `true|false`

## Run (demo)
From `ui-automation-csharp/` (single stable demo run; only passing demo scenarios are included in this project):

```bash
dotnet test OrangeHrm.UiTests/OrangeHrm.UiTests.csproj --settings OrangeHrm.UiTests/runsettings.demo.runsettings
```

Notes:
- Demo credentials are provided via the runsettings file (Admin / admin123).
- Parallel execution is disabled for stability (NUnit assembly attributes + MaxCpuCount=1 in demo runsettings).

## Run (local)
```bash
dotnet test OrangeHrm.UiTests/OrangeHrm.UiTests.csproj --settings OrangeHrm.UiTests/runsettings.local.runsettings
```

Change browser:
- update `BROWSER` parameter in the runsettings, or
- set an environment variable `BROWSER` (takes precedence in this project)

## Allure reporting (disabled for demo)
Allure integration remains disabled for demo runs:
- No Allure hooks are enabled in code
- No Allure HTML generation step is expected (console logs + raw test results only)

## Notes
Demo environment can be unstable; tests prioritize navigation and critical validations.
Includes 150+ regression scenarios via scenario outlines + example matrices.
