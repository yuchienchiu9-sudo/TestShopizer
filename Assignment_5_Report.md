# Assignment 5: Testable Design and Mocking

> **Combined Document**: Merged from `Assignment_5_Report.docx` (structured report) and `Part5.docx` (detailed draft notes).

---

## Part 1: Testable Design (20 points)

### 1.1 Aspects and Goals of Testable Design

A testable design is one that allows individual components to be verified in isolation from their environment. The primary goals include:

- **Isolation (Decoupling):** Components should not depend on concrete implementations of external systems (databases, file systems, network). Instead, they should depend on abstractions (interfaces).
- **Controllability:** The tester should be able to force the software into specific states or paths (e.g., simulating a network timeout or a specific database return value).
- **Determinism:** A testable design ensures that given the same input and environment state, the component produces the same output every time.
- **Observability:** The internal state or outputs of a component should be easily accessible for verification.

---

### 1.2 Stubbing Implementation

**Existing Use of Stubbing:** In the refactored `IntegrationModulesLoader`, we introduced the `IntegrationModuleSource` interface.

- **Usage:** The system now uses this interface to load raw data instead of directly accessing the classpath resources.
- **Rationale:** This is used because the original implementation was tightly coupled to the Java `ClassLoader` and resource files. By stubbing this source, we can return pre-defined JSON strings in memory, making the tests fast and independent of the physical disk.

<!-- IMAGE PLACEHOLDER 1 -->
> 📸 **Screenshot needed:** IntegrationModuleSource interface and its default implementation in `IntegrationModulesLoader.java`.

**New Stub Implementation:** I implemented `StubIntegrationModuleSource` inside `IntegrationModulesLoaderTest.java`.

- **Technique:** I created a static inner class that implements the `IntegrationModuleSource` interface.
- **Test Case:** In `testLoadModulesWithValidJson`, this stub is injected into the loader. It returns a hardcoded JSON string, allowing us to verify the parser's logic without ever reading a real file.

<!-- IMAGE PLACEHOLDER 2 -->
> 📸 **Screenshot needed:** `StubIntegrationModuleSource` class and the test case using it.

---

### 1.3 Analysis of Bad Testable Design

#### Documented Code (Original Design)

The original `IntegrationModulesLoader.loadIntegrationModules` method contained the following logic:

```java
InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFilePath);
Map[] objects = mapper.readValue(in, Map[].class);
```

#### The Problem

This design makes testing difficult because:

1. **File System Dependency:** You cannot test how the code handles different JSON structures without creating multiple physical files in the `src/main/resources` folder.
2. **Error Handling:** It is nearly impossible to simulate an `InputStream` failure or a specific I/O error without complex environment manipulation.
3. **Side Effects:** Tests might accidentally overwrite or depend on shared resource files, leading to flaky tests.

#### Advice for Fix

Replace the hardcoded instantiation/access with Dependency Injection (DI). Define an interface for the resource loading behavior and pass that interface into the constructor.

#### Implementation of New Design

I updated `IntegrationModulesLoader` to accept two interfaces:
- `IntegrationModuleSource` (for getting the string)
- `IntegrationModuleParser` (for converting data to objects)

<!-- IMAGE PLACEHOLDER 3 -->
> 📸 **Screenshot needed:** Refactored `IntegrationModulesLoader` constructor showing Dependency Injection.

---

#### Extended Analysis (from Draft Notes — Part5.docx)

##### 1. Problematic Design in Original Implementation

In the original implementation of `ShoppingCartServiceImpl.saveOrUpdate()`, the system directly retrieves user context using a static global call:

> **Example of problematic design:** [`ShoppingCartServiceImpl.java` L104–113](https://github.com/KrimsonSun/shopizerForTest/blob/3.2.7/sm-core/src/main/java/com/salesmanager/core/business/services/shoppingcart/ShoppingCartServiceImpl.java#L104-L113) — `saveOrUpdate()` directly calls `UserContext.getCurrentInstance()`

**Why This Is Bad Design for Testing:**

This design tightly couples business logic to a static global dependency. `UserContext.getCurrentInstance()`:

- Is a static method
- Returns a global object
- Belongs to a `final` class
- Cannot be easily mocked with standard Mockito

**What Testing Becomes Difficult or Impossible?**

- Cannot control what `UserContext.getCurrentInstance()` returns
- Cannot simulate "no user context" reliably
- Cannot simulate different IP addresses
- Cannot verify interaction behavior
- Cannot isolate cart logic from global application state

**This violates:**
- Dependency Inversion Principle
- Single Responsibility Principle
- Unit Test Isolation principles

The `saveOrUpdate()` method becomes dependent on global state, making proper unit testing difficult.

##### 2. Recommended Fix

To improve testability, we refactored the design to introduce an abstraction layer. Instead of calling `UserContext.getCurrentInstance()` directly, we created an interface: [`ShoppingCartPricingServiceV2.java`](https://github.com/KrimsonSun/shopizerForTest/blob/3.2.7/sm-core/src/main/java/com/salesmanager/core/business/services/shoppingcart/ShoppingCartPricingServiceV2.java)

##### 3. Refactored Version (`ShoppingCartUserContextV2`)

We implemented a new version that depends on the abstraction: [`ShoppingCartPricingServiceV2.java`](https://github.com/KrimsonSun/shopizerForTest/blob/3.2.7/sm-core/src/main/java/com/salesmanager/core/business/services/shoppingcart/ShoppingCartPricingServiceV2.java)

Now the business logic no longer depends on static global state.

##### 4. Why This Fix Improves Testability

Now:
- We can inject a mock implementation
- We can simulate different IP addresses
- We can simulate null context
- No static calls exist inside business logic
- Behavior can be verified independently

This follows:
- Dependency Injection
- SOLID principles
- Clean Architecture principles
- Testable design practices

##### 5. New Test Case for More Testable Code

We implemented a unit test:

```java
public void attachIpAddress_whenContextExists_setsIpAddress()
```
[`CartMockTest.java` — `testTotalCalculationWithMockedPrice()`](https://github.com/KrimsonSun/shopizerForTest/blob/3.2.7/sm-core/src/test/java/com/salesmanager/test/shoppingcart/CartMockTest.java#L63-L84)

**What This Test Achieves:**
- Confirms IP is attached when context exists
- Simulates absence of context
- Does not depend on global application state
- Fully unit-testable

This functionality was previously difficult to test due to static global dependency.

---

## Part 2: Mocking (20 points)

### 2.1 Mocking and its Utility

Mocking involves creating objects that simulate the behavior of real, complex objects. Its primary utilities are:

- **Verification of Interactions:** Unlike stubs (which just provide data), mocks allow you to verify if a method was called, with what arguments, and how many times.
- **Avoiding Side Effects:** Mocking an `EmailService` ensures you don't actually send 1,000 emails during a test run.
- **Simulating External Systems:** Mocks can easily simulate external API failures or specific sequence of calls that are hard to trigger with real systems.

---

#### Extended Theory (from Draft Notes — Part5.docx)

**What is Mocking?**

Mocking is a unit testing technique in which real dependencies are replaced with controlled test doubles (mock objects). Mocking allows the tester to isolate the system under test (SUT) from its external dependencies.

Mocks provide:
- **Controllability** – the ability to define exactly what a dependency should return.
- **Behavior Verification** – the ability to verify whether a dependency method was invoked, how many times it was called, and with what arguments.
- **Isolation** – preventing external systems (e.g., APIs, databases, or complex services) from affecting test results.

In this project, **Mockito** was used to create mock objects and verify behavior.

---

### 2.2 Feature for Mocking: Email Notification

The `EmailService` is a perfect candidate for mocking. In most enterprise applications, sending an email involves an SMTP server and network connectivity.

- **Behavior Checking:** Without mocking, we could only check if the code runs without crashing. With Mockito mocking, we can check if the correct recipient was set and if the Email Template Tokens (like order ID and amount) were populated correctly.

<!-- IMAGE PLACEHOLDER 4 -->
> 📸 **Screenshot needed:** `EmailService` interface definition.

---

#### Extended Feature Analysis (from Draft Notes — Part5.docx)

**Feature Suitable for Mocking: Price Calculation Logic**

The feature selected for mocking is the price calculation logic used by the shopping cart.

In a real system, the `PricingService` may depend on complex business rules, tax engines, or external configurations. Testing such dependencies directly would make the unit test unstable and environment-dependent.

To isolate the cart's behavior, a new testable service (`ShoppingCartPricingServiceV2`) was introduced. This class depends on `PricingService` via dependency injection. Instead of directly instantiating the real `PricingService`, a mock object is injected into the V2 service.

---

### 2.3 Mockito Test Case

I implemented `EmailServiceMockTest.java` using Mockito.

- **Key Technique:** Used `@Mock` for `EmailService` and `ArgumentCaptor` to inspect the `Email` object passed to the service.
- **Verification:** The test `testOrderConfirmationEmailContentIsCorrect` captures the `Email` object and asserts that the subject and template tokens match the test data.

<!-- IMAGE PLACEHOLDER 5 -->
> 📸 **Screenshot needed:** JUnit test using `ArgumentCaptor` and `verify(mockEmailService).sendHtmlEmail(...)`.

---

#### Extended Mocking Implementation (from Draft Notes — Part5.docx)

**Mocking Implementation for Cart Pricing:**

In the test:
1. A mock `PricingService` is created.
2. The behavior of `calculatePriceQuantity()` is stubbed to always return `100.00`.
3. The mock is injected into `ShoppingCartPricingServiceV2`.
4. The system under test calls `computeLineTotal()`.
5. The test verifies:
   - The returned value matches the mocked value.
   - The `calculatePriceQuantity()` method was invoked exactly once with the correct arguments.

[`CartMockTest.java`](https://github.com/KrimsonSun/shopizerForTest/blob/3.2.7/sm-core/src/test/java/com/salesmanager/test/shoppingcart/CartMockTest.java)

**Why This Is Correct Mocking (System-Level Behavior):**

Originally, the test directly invoked the mock object itself. That approach only demonstrated Mockito usage but did not test system behavior.

After refactoring, the mock is injected into the system under test. The test now verifies that:
- The shopping cart logic actually uses the pricing service.
- The correct parameters are passed.
- The pricing service is invoked exactly once.
- The cart uses the returned value properly.

This change transforms the test from **"testing a mock object"** into **"testing the system's interaction with its dependency."**

> 📸 **Test Evidence Screenshot needed:** `CartMockTest.testTotalCalculationWithMockedPrice` passed.

---

## Conclusion

By refactoring the code to use interfaces and dependency injection, we transformed a legacy "untestable" component into a modular service. Using Mockito allowed us to verify business logic (like email generation) without the overhead or risk of sending real emails.

The combined approach of:
- **Stubbing** (for data isolation, e.g., `IntegrationModuleSource`)
- **Mocking** (for behavior verification, e.g., `EmailService`, `PricingService`)

...ensures our tests are fast, deterministic, isolated, and maintainable.

---

## 📸 Screenshots Required

Below is a summary of all screenshots you need to capture and insert into this report:

| # | Location to Go | What to Capture |
|---|---|---|
| 1 | `sm-core/src/main/java/.../IntegrationModulesLoader.java` | `IntegrationModuleSource` interface and its default implementation |
| 2 | `sm-core/src/test/java/.../IntegrationModulesLoaderTest.java` | `StubIntegrationModuleSource` class and the test case using it |
| 3 | `sm-core/src/main/java/.../IntegrationModulesLoader.java` | Refactored constructor showing Dependency Injection |
| 4 | `EmailService` interface file | `EmailService` interface definition |
| 5 | `sm-core/src/test/java/.../EmailServiceMockTest.java` | JUnit test using `ArgumentCaptor` and `verify(mockEmailService).sendHtmlEmail(...)` |
| 6 | IntelliJ IDEA test runner | `CartMockTest.testTotalCalculationWithMockedPrice` passed (green checkmark) |
