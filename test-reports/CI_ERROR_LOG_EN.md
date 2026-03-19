# CI Assignment Error Log (English)

> Record date: 2026-02-18  
> Goal: capture each build/test error step-by-step for assignment evidence (process + issues + handling).

## Logging Rules
- Add one entry for every key step (setup, build, test, push, CI trigger).
- Even when a step succeeds, record it as “Success (no error)” for traceability.
- For failures, always capture: command, exit code, error summary, initial analysis, and next action.

## Error Timeline (Chronological)

### Step 1: Run test via VS Code test tool
- **Action**: run `ProductPricePartitionTest.java`
- **Result**: no actual Java test execution stats (`passed=0 failed=0`)
- **Issue/Observation**: tool did not return real JUnit execution output
- **Initial Analysis**: this tool path did not properly execute Maven/JUnit tests in current project setup
- **Next Action**: switch to Maven CLI (same execution path used by CI)

### Step 2: Run targeted test in sm-core
- **Command**: `./mvnw -pl sm-core -Dtest=ProductPricePartitionTest test`
- **Exit code**: `1`
- **Result**: `BUILD FAILURE`
- **Key error summary**:
  - `Tests run: 21, Failures: 5, Errors: 0, Skipped: 0`
  - Failing tests include:
    - `testPartition5_InvalidNegativePrice`
    - `testPartition5_BoundaryNegative_JustBelowZero`
    - `testPartition6_InvalidDecimalPrecision_ThreeDecimals`
    - `testPartition6_InvalidDecimalPrecision_ExtremeDecimals`
    - `testPartition7_InvalidNullPrice_HandlesGracefully`
- **Additional log signal**: `NullPointerException` from `CacheManagerImpl` appears during startup logs
- **Initial Analysis**: this class currently contains expected failing scenarios (defect-revealing tests), so it is not suitable as a default “must-pass CI gate” test target
- **Next Action**: keep this class as defect evidence; choose a stable CI test subset or fix business logic first

### Step 3: Run targeted test in sm-shop
- **Command**: `./mvnw -pl sm-shop -Dtest=GeneratePasswordTest test`
- **Exit code**: `1`
- **Result**: `BUILD FAILURE`
- **Key error summary**:
  - `Could not resolve dependencies`
  - `Failed to collect dependencies at com.shopizer:sm-core:jar:3.2.5`
  - `Could not transfer artifact ... from/to spring-releases ... : Not authorized`
- **Initial Analysis**:
  - Maven attempted to fetch `com.shopizer:sm-core:3.2.5` from remote `spring-releases`, which is unauthorized;
  - local reactor artifacts were not used/pre-installed for dependency resolution.
- **Next Action (recommended)**:
  - pre-install reactor modules from repo root: `./mvnw -DskipTests install`, then run target tests;
  - or build dependency modules automatically with `-am`: `./mvnw -pl sm-shop -am test`.

### Step 4: Validate sm-core-model module build
- **Command**: `./mvnw -pl sm-core-model test`
- **Exit code**: `0`
- **Result**: `BUILD SUCCESS`
- **Execution summary**: `No tests to run.`
- **Note**: useful as build-proof, but not enough alone to prove test-case execution.

### Step 5: Validate executable tests in sm-core (DataUtils)
- **Command**: `./mvnw -pl sm-core -Dtest=DataUtilsTest test`
- **Exit code**: `0`
- **Result**: `BUILD SUCCESS`
- **Execution summary**: `Tests run: 9, Failures: 0, Errors: 0, Skipped: 0`
- **Additional signal**: JaCoCo instrumentation warnings appear for high JDK class version (`Unsupported class file major version 69`), but build still succeeds.
- **Conclusion**: this is a stable baseline command for CI that actually executes tests.

### Step 6: Validate another test target (ShippingMethodDecisionTest)
- **Command**: `./mvnw -pl sm-core -Dtest=ShippingMethodDecisionTest test`
- **Exit code**: `0`
- **Result**: `BUILD SUCCESS`
- **Execution summary**: `Tests run: 1, Failures: 0, Errors: 0, Skipped: 1`
- **Note**: this test is skipped, so it is not ideal as the main evidence of executed test cases.

### Step 7: Reproduce CI command locally (first attempt failed)
- **Command**: `./mvnw -B -ntp -pl sm-core -Dtest=DataUtilsTest test`
- **Exit code**: `1`
- **Result**: `BUILD FAILURE`
- **Key error summary**: `Unable to parse command line options: Unrecognized option: -ntp`
- **Initial Analysis**: project Maven Wrapper is 3.5.2, which does not support `-ntp`.
- **Next Action**: remove `-ntp` from workflow command.

### Step 8: Re-test after command fix
- **Command**: `./mvnw -B -pl sm-core -Dtest=DataUtilsTest test`
- **Exit code**: `0`
- **Result**: `BUILD SUCCESS`
- **Execution summary**: `Tests run: 9, Failures: 0, Errors: 0, Skipped: 0`
- **Conclusion**: fixed command is ready for GitHub Actions.

### Step 9: Add CI configuration file
- **File**: `.github/workflows/ci.yml`
- **Core flow**: `checkout` + `setup-java(17)` + `./mvnw -B -pl sm-core -Dtest=DataUtilsTest test`
- **Status**: created and ready to run after push.

### Step 10: First GitHub Actions web run failed (dependency resolution)
- **Symptom**: CI log shows `Could not resolve dependencies ... com.shopizer:sm-core-model ... from/to spring-releases ... Not authorized`
- **Root cause**: workflow built only `sm-core` and did not build required local dependency modules in the same reactor; Maven then tried remote resolution for `sm-core-model` and hit authorization failure.
- **Fix**: update command to `./mvnw -B -pl sm-core -am -Dtest=DataUtilsTest test` so Maven also builds required modules (`-am`).
- **Submission note**: commit this fix and re-run Actions, then capture screenshots for both “failed run” and “fixed successful run”.

### Step 11: Second GitHub Actions failure (No tests were executed)
- **Symptom**: CI failed with `Failed to execute goal ... maven-surefire-plugin ... on project sm-core-model: No tests were executed!`
- **Root cause**: after adding `-am`, Maven enters dependency modules such as `sm-core-model`; with `-Dtest=DataUtilsTest`, only `sm-core` has matching tests, while other modules have no matching tests and surefire fails by default.
- **Fix**: update command to `./mvnw -B -pl sm-core -am -Dtest=DataUtilsTest -DfailIfNoTests=false test`.
- **Local validation**: Reactor Summary shows `shopizer / sm-core-model / sm-core-modules / sm-core` all `SUCCESS`, and `DataUtilsTest` reports `Tests run: 9, Failures: 0, Errors: 0, Skipped: 0`.

---

## Assignment-ready Issue Description Template
- **Issue**: command/step name
- **Symptoms**: key error + screenshot
- **Root-cause hypothesis**: dependency/environment/test data/code defect
- **Resolution attempts**: what was tried and why
- **Outcome**: resolved / unresolved (with next plan)

## Screenshots & Evidence

### Figure 1: Successful CI Run Overview
**Caption**: GitHub Actions CI run final status showing "Success" with execution duration and latest commit information.

**Description**: This screenshot demonstrates that the Shopizer project successfully passed the CI pipeline. The run was triggered by commit "Add comment to workflow for clarity" and completed in approximately 3–5 minutes. The green checkmark indicates all jobs executed without errors.

**Location**: GitHub repository → Actions tab → Latest run (with green checkmark) → Click on it → Scroll to top

**Insertion Location**: Place after Step 11 in the error timeline.

---

### Figure 2: Build Job Execution Steps
**Caption**: Detailed step list of the `build-test` job showing all executed stages: Checkout, Install JDK 17, Maven executable permission, and test execution.

**Description**: This figure shows the complete workflow execution stages. Each step (green checkmark) indicates successful completion. Key steps include:
- Checkout source code
- Set up JDK 17
- Make Maven wrapper executable
- Build and run targeted tests (sm-core)

**Location**: Within the same run page → Scroll down to "Jobs" section → Click `build-test` → View "Run steps" at left panel

**Insertion Location**: After Figure 1.

---

### Figure 3: Test Execution Log (Success)
**Caption**: Maven test execution output showing successful test results: "Tests run: 9, Failures: 0, Errors: 0, Skipped: 0" and "BUILD SUCCESS".

**Description**: This is the critical evidence that tests were actually executed and passed. The log shows:
- `[INFO] Running com.salesmanager.test.business.utils.DataUtilsTest`
- `[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0`
- `[INFO] BUILD SUCCESS` with execution time
- Reactor Summary confirming all modules passed

**Location**: Within the run page → Click `build-test` job → Scroll to bottom section or search for "Tests run:" in the logs

**Insertion Location**: After Figure 2 (this is the most important proof).

---

### Figure 4: Failed Run - Dependency Resolution Error
**Caption**: First CI run failure showing "No tests were executed!" error on project `sm-core-model` before applying the fix.

**Description**: This screenshot captures the initial failure state before the `-DfailIfNoTests=false` parameter was added. The error message reads:
- `Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin ... on project sm-core-model: No tests were executed!`
- `(Set -DfailIfNoTests=false to ignore this error.)`

This demonstrates the problem-solving process and the iterative nature of CI configuration.

**Location**: GitHub repository → Actions tab → Find the run labeled "Fix CI no-tests failure..." (red X mark) → View its error logs

**Insertion Location**: In a new subsection titled "Problem & Resolution Timeline" after Figure 3.

---

### Figure 5: Successful Re-run After Fix
**Caption**: Latest successful CI run (green checkmark) using the fixed workflow with `-DfailIfNoTests=false` parameter.

**Description**: This screenshot shows the same test suite passing cleanly after the configuration fix. Compare this with Figure 4 to demonstrate problem-solving effectiveness. The red-to-green transition proves that:
- The root cause was correctly identified (modules without tests causing surefire failure)
- The fix (adding `-DfailIfNoTests=false`) was effective
- CI pipeline now runs reliably on every push

**Location**: GitHub repository → Actions tab → The latest run with green checkmark → Same as Figure 1 but focus on the date/commit message to confirm it's the fix commit

**Insertion Location**: In the "Problem & Resolution Timeline" subsection alongside Figure 4.
