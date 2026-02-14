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

## Run
From `ui-automation-csharp/`:

```bash
dotnet test OrangeHrm.UiTests.sln --settings OrangeHrm.UiTests/runsettings.local.runsettings
```

Change browser:
- update `BROWSER` parameter in the runsettings, or
- set an environment variable `BROWSER` (takes precedence in this project)

## Notes
Demo environment can be unstable; tests prioritize navigation and critical validations.
Includes 150+ regression scenarios via scenario outlines + example matrices.
