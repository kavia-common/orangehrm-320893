Java Cucumber dry-run logs (JUnit Platform Suite)

This folder is used to store Maven/Surefire output from the Java Cucumber dry-run execution.

Expected command (run from `src/test/selenium-java-junit/`):

- `mvn test -Dtest=org.orangehrm.bdd.CucumberTest`

Notes:
- The suite runner is `org.orangehrm.bdd.CucumberTest` (JUnit 5 `@Suite` + Cucumber engine).
- Dry-run is enabled by default via `cucumber.execution.dry-run=true` (see pom.xml).

After the fix that adds `org.junit.platform:junit-platform-suite-engine`, Surefire should discover and execute the suite and Cucumber should print:
- scenario counts / step counts summary
- undefined-step snippets (if any)
to the Maven logs.
