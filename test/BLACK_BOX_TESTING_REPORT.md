# Black Box Testing Report

## Crypto Checker Application

---

**Document Version:** 1.0  
**Date:** November 21, 2025  
**Testing Methodology:** Black Box Testing using Equivalence Partitioning and Boundary Value Analysis  
**Testing Framework:** JUnit 5

---

## Table of Contents

1. [System Overview and Functional Requirements](#1-system-overview-and-functional-requirements)
2. [Test Objectives and Testing Strategy](#2-test-objectives-and-testing-strategy)
3. [Detailed Testing Plan](#3-detailed-testing-plan)
4. [Test Case Design](#4-test-case-design)
5. [Expected Outcomes](#5-expected-outcomes)

---

## 1. System Overview and Functional Requirements

### 1.1 System Overview

**Crypto Checker** is a Java-based desktop application that provides real-time cryptocurrency market data and portfolio management functionality. The application uses a Swing-based GUI to display cryptocurrency information, enable currency conversion, and manage investment portfolios.

#### Core Components:

1. **Main Application (Main.java)**

   - Entry point and GUI initialization
   - Theme management (Light, Dark, Custom)
   - Currency configuration (USD, EUR, GBP, etc.)
   - Frame and panel management

2. **Data Layer (WebData.java)**

   - Fetches cryptocurrency data from CoinMarketCap API
   - Manages coin data (price, volume, market cap, rankings)
   - Handles global market statistics
   - Serialization/deserialization of data
   - Portfolio data management

3. **User Interface Panels**

   - **PanelCoin**: Displays list of all cryptocurrencies
   - **PanelConverter**: Currency/cryptocurrency conversion tool
   - **PanelPortfolio**: Portfolio management and tracking
   - **PanelSettings**: Application configuration
   - **Menu**: Navigation between panels

4. **Supporting Classes**
   - **Debug.java**: Logging functionality
   - **Theme**: Color scheme management

### 1.2 Functional Requirements

#### FR-1: Cryptocurrency Data Display

- **FR-1.1**: Display list of cryptocurrencies with current prices
- **FR-1.2**: Show cryptocurrency details (rank, symbol, price, market cap, volume)
- **FR-1.3**: Display percentage changes (1h, 24h, 7d)
- **FR-1.4**: Format prices appropriately based on magnitude
- **FR-1.5**: Support multiple fiat currencies (USD, EUR, GBP, etc.)

#### FR-2: Price Formatting

- **FR-2.1**: Prices > $1 display with 2 decimal places
- **FR-2.2**: Prices $0.1-$1 display with 3 decimal places
- **FR-2.3**: Prices $0.01-$0.1 display with 4 decimal places
- **FR-2.4**: Prices $0.001-$0.01 display with 5 decimal places
- **FR-2.5**: Prices $0.0001-$0.001 display with 6 decimal places
- **FR-2.6**: Prices < $0.0001 display with up to 12 decimal places

#### FR-3: Global Market Data

- **FR-3.1**: Display total market capitalization
- **FR-3.2**: Show 24-hour trading volume
- **FR-3.3**: Display Bitcoin market dominance percentage
- **FR-3.4**: Show count of active currencies, assets, and markets
- **FR-3.5**: Format large numbers with thousand separators

#### FR-4: Currency Conversion

- **FR-4.1**: Convert between cryptocurrencies
- **FR-4.2**: Convert cryptocurrency to fiat currency
- **FR-4.3**: Real-time conversion as user types
- **FR-4.4**: Display conversion information for both currencies
- **FR-4.5**: Support switching between currency pairs

#### FR-5: Portfolio Management

- **FR-5.1**: Create and manage multiple portfolios
- **FR-5.2**: Add coins to portfolio with purchase amount and price
- **FR-5.3**: Calculate current portfolio value
- **FR-5.4**: Calculate gains/losses from purchase price
- **FR-5.5**: Display portfolio performance metrics
- **FR-5.6**: Remove coins from portfolio
- **FR-5.7**: Switch between multiple portfolios
- **FR-5.8**: Persist portfolio data between sessions

#### FR-6: Data Management

- **FR-6.1**: Fetch data from external API (CoinMarketCap)
- **FR-6.2**: Cache data locally for offline access
- **FR-6.3**: Manual refresh of market data
- **FR-6.4**: Serialize/deserialize application state
- **FR-6.5**: Handle network errors gracefully

#### FR-7: User Interface

- **FR-7.1**: Support light and dark themes
- **FR-7.2**: Allow custom color schemes
- **FR-7.3**: Provide navigation menu between panels
- **FR-7.4**: Display data in formatted tables
- **FR-7.5**: Show detailed information on selection
- **FR-7.6**: Search/filter functionality

#### FR-8: Data Integrity

- **FR-8.1**: Validate coin data completeness
- **FR-8.2**: Handle null/missing data appropriately
- **FR-8.3**: Support coins with limited supply
- **FR-8.4**: Support coins with unlimited supply
- **FR-8.5**: Maintain data consistency across operations

---

## 2. Test Objectives and Testing Strategy

### 2.1 Test Objectives

#### Primary Objectives:

1. **Functional Correctness**

   - Verify all functional requirements are implemented correctly
   - Ensure price formatting follows specification across all ranges
   - Validate calculation accuracy for conversions and portfolio metrics
   - Confirm data display matches expected format

2. **Data Handling**

   - Verify correct handling of various price ranges (from $0.000001 to $1,000,000+)
   - Ensure proper formatting of large numbers (market cap, volume)
   - Validate percentage calculations (gains, losses, dominance)
   - Test boundary conditions between different formatting rules

3. **User Scenarios**

   - Test empty portfolio scenario
   - Test single-coin portfolio scenario
   - Test multi-coin portfolio scenario
   - Test extreme portfolio values (very large, very small)

4. **Robustness**

   - Verify system handles edge cases without crashing
   - Ensure graceful handling of zero values
   - Test with extreme values (maximum, minimum)
   - Validate behavior with incomplete data

5. **Consistency**
   - Ensure consistent behavior across repeated operations
   - Verify data persistence and retrieval
   - Test copy/clone operations maintain data integrity

#### Secondary Objectives:

1. **Specification Compliance**: Ensure implementation matches documented behavior
2. **User Experience**: Verify output is readable and properly formatted
3. **Data Accuracy**: Confirm calculations are mathematically correct
4. **Error Prevention**: Identify potential issues at boundaries and edge cases

### 2.2 Testing Strategy

#### 2.2.1 Black Box Testing Approach

**Definition**: Testing methodology that examines functionality without knowledge of internal code structure, focusing solely on inputs and expected outputs.

**Rationale for Black Box Testing:**

- Tests from user's perspective
- Independent of implementation details
- Can be designed from specifications
- Identifies functional discrepancies
- Easier to maintain when code changes

**Key Principles:**

1. No access to internal code during test design
2. Tests based on specifications and requirements
3. Focus on what the system does, not how it does it
4. Input-output validation only
5. Treat system as a "black box"

#### 2.2.2 Equivalence Partitioning (EP)

**Definition**: Technique that divides input domain into classes where all members should produce similar behavior.

**Application to Crypto Checker:**

##### EP-1: Price Range Partitions

| Partition ID | Price Range            | Expected Decimal Places      | Rationale                          |
| ------------ | ---------------------- | ---------------------------- | ---------------------------------- |
| EP-1.1       | price > 1.0            | 2 decimals (#.##)            | Major currencies, high-value coins |
| EP-1.2       | 0.1 < price ≤ 1.0      | 3 decimals (#.###)           | Mid-range altcoins                 |
| EP-1.3       | 0.01 < price ≤ 0.1     | 4 decimals (#.####)          | Low-value altcoins                 |
| EP-1.4       | 0.001 < price ≤ 0.01   | 5 decimals (#.#####)         | Very low-value coins               |
| EP-1.5       | 0.0001 < price ≤ 0.001 | 6 decimals (#.######)        | Micro-cap coins                    |
| EP-1.6       | price < 0.0001         | 12 decimals (#.############) | Ultra low-value coins              |

**Test Strategy**: Select representative values from each partition and verify formatting.

##### EP-2: Portfolio State Partitions

| Partition ID | State           | Characteristics | Test Focus               |
| ------------ | --------------- | --------------- | ------------------------ |
| EP-2.1       | Empty Portfolio | 0 coins         | Initialization, display  |
| EP-2.2       | Single Coin     | 1 coin          | Basic operations         |
| EP-2.3       | Multiple Coins  | 2+ coins        | Aggregation, sorting     |
| EP-2.4       | Large Portfolio | 50+ coins       | Performance, scalability |

##### EP-3: Market Condition Partitions

| Partition ID | Condition     | Characteristics               | Test Focus              |
| ------------ | ------------- | ----------------------------- | ----------------------- |
| EP-3.1       | Bull Market   | High values, positive changes | Large numbers handling  |
| EP-3.2       | Bear Market   | Low values, negative changes  | Negative number display |
| EP-3.3       | Normal Market | Medium values, mixed changes  | Typical operation       |

##### EP-4: Percentage Change Partitions

| Partition ID | Change Type    | Range          | Display Requirement |
| ------------ | -------------- | -------------- | ------------------- |
| EP-4.1       | Large Positive | +10% to +100%+ | Green color, + sign |
| EP-4.2       | Small Positive | +0.01% to +10% | Green color, + sign |
| EP-4.3       | No Change      | 0%             | Neutral color       |
| EP-4.4       | Small Negative | -0.01% to -10% | Red color, - sign   |
| EP-4.5       | Large Negative | -10% to -100%  | Red color, - sign   |

##### EP-5: Portfolio Performance Partitions

| Partition ID | Performance | Characteristics    | Test Focus           |
| ------------ | ----------- | ------------------ | -------------------- |
| EP-5.1       | Gains       | Current > Purchase | Positive calculation |
| EP-5.2       | Break-even  | Current = Purchase | Zero handling        |
| EP-5.3       | Losses      | Current < Purchase | Negative calculation |

#### 2.2.3 Boundary Value Analysis (BVA)

**Definition**: Testing technique that focuses on values at the boundaries between equivalence partitions, where defects are most likely to occur.

**Application to Crypto Checker:**

##### BVA-1: Price Formatting Boundaries

| Boundary | Just Below         | Exact Value    | Just Above        | Expected Behavior |
| -------- | ------------------ | -------------- | ----------------- | ----------------- |
| 1.0      | 0.9999 (3 dec)     | 1.0 (2 dec)    | 1.0001 (2 dec)    | Formatting switch |
| 0.1      | 0.0999 (4 dec)     | 0.1 (3 dec)    | 0.1001 (3 dec)    | Formatting switch |
| 0.01     | 0.00999 (5 dec)    | 0.01 (4 dec)   | 0.01001 (4 dec)   | Formatting switch |
| 0.001    | 0.000999 (6 dec)   | 0.001 (5 dec)  | 0.001001 (5 dec)  | Formatting switch |
| 0.0001   | 0.0000999 (12 dec) | 0.0001 (6 dec) | 0.0001001 (6 dec) | Formatting switch |

**Test Strategy**: Test values at each boundary and one increment above/below to verify correct transition.

##### BVA-2: Bitcoin Dominance Boundaries

| Test Point | Value  | Expected Display | Rationale            |
| ---------- | ------ | ---------------- | -------------------- |
| Minimum    | 0.0%   | "0.0%"           | No Bitcoin dominance |
| Low        | 0.01%  | "0.01%"          | Just above zero      |
| Mid-Low    | 25.0%  | "25.0%"          | Low dominance        |
| Mid        | 50.0%  | "50.0%"          | Equal split          |
| Mid-High   | 75.0%  | "75.0%"          | High dominance       |
| High       | 99.99% | "99.99%"         | Nearly complete      |
| Maximum    | 100.0% | "100.0%"         | Complete dominance   |

##### BVA-3: Portfolio Quantity Boundaries

| Boundary Type      | Value      | Test Focus                |
| ------------------ | ---------- | ------------------------- |
| Zero               | 0.0        | Empty handling            |
| Minimum Meaningful | 0.00000001 | Smallest tradeable amount |
| Small              | 0.001      | Fractional amounts        |
| Unit               | 1.0        | Single unit               |
| Medium             | 100.0      | Typical amount            |
| Large              | 1000000.0  | Whale holdings            |

##### BVA-4: Rank Boundaries

| Rank Range | Test Values     | Significance           |
| ---------- | --------------- | ---------------------- |
| Top        | 1               | #1 cryptocurrency      |
| Top 10     | 9, 10, 11       | Major cryptocurrencies |
| Top 100    | 99, 100, 101    | Well-known coins       |
| Top 1000   | 999, 1000, 1001 | Tracked coins          |

##### BVA-5: Supply Boundaries

| Supply Type | Test Values              | Expected Behavior       |
| ----------- | ------------------------ | ----------------------- |
| Zero        | 0                        | Display as "0" or "N/A" |
| Limited     | 21,000,000 (Bitcoin)     | Show actual number      |
| Large       | 1,000,000,000+           | Format with commas      |
| Unlimited   | 0 or null for max_supply | Special handling        |

#### 2.2.4 Test Coverage Matrix

| Requirement                  | EP Tests | BVA Tests | Total Tests | Priority |
| ---------------------------- | -------- | --------- | ----------- | -------- |
| Price Formatting (FR-2)      | 6        | 15        | 21          | Critical |
| Global Data Display (FR-3)   | 3        | 8         | 11          | High     |
| Portfolio Management (FR-5)  | 5        | 12        | 17          | Critical |
| Data Display (FR-1)          | 4        | 6         | 10          | High     |
| Currency Conversion (FR-4)   | 3        | 5         | 8           | Medium   |
| Coin Data Validation (FR-8)  | 5        | 8         | 13          | High     |
| Multiple Portfolios (FR-5.7) | 3        | 4         | 7           | Medium   |
| Copy/Clone Operations        | 2        | 3         | 5           | Medium   |
| **TOTAL**                    | **31**   | **61**    | **92**      | -        |

### 2.3 Test Execution Strategy

#### 2.3.1 Test Phases

**Phase 1: Smoke Testing**

- Verify basic functionality works
- Test core price formatting (1-2 values per partition)
- Validate basic portfolio operations
- Estimated Duration: 2 hours

**Phase 2: Equivalence Partition Testing**

- Execute all EP test cases
- Test representative values from each partition
- Validate functional correctness
- Estimated Duration: 8 hours

**Phase 3: Boundary Value Testing**

- Execute all BVA test cases
- Test all boundary conditions
- Verify transitions between partitions
- Estimated Duration: 10 hours

**Phase 4: Edge Case Testing**

- Test extreme values
- Test null/empty scenarios
- Test error conditions
- Estimated Duration: 6 hours

**Phase 5: Regression Testing**

- Re-run critical tests after fixes
- Verify bug fixes don't break existing functionality
- Estimated Duration: 4 hours

**Total Estimated Effort**: 30 hours

#### 2.3.2 Test Environment

**Requirements:**

- Java JDK 8 or higher
- JUnit 5.9.3 or higher
- Maven 3.x for build automation
- Test data: Sample cryptocurrency data
- Mock data for API responses (optional)

**Test Data Sets:**

1. **Typical Data Set**: Representative real-world values
2. **Boundary Data Set**: Values at all identified boundaries
3. **Edge Case Data Set**: Extreme and unusual values
4. **Empty Data Set**: Null/zero/empty scenarios

#### 2.3.3 Test Automation

**Automated Tests (90%):**

- All EP tests
- All BVA tests
- Functional unit tests
- Data validation tests

**Manual Tests (10%):**

- UI appearance verification
- User experience validation
- End-to-end workflows
- Visual confirmation of themes

### 2.4 Test Documentation

**Test Case Template:**

```
Test ID: TC-XXX
Category: [EP | BVA | Edge Case]
Priority: [Critical | High | Medium | Low]
Requirement: FR-X.X
Description: Brief description
Preconditions: Setup required
Test Data: Input values
Steps: Execution steps
Expected Result: What should happen
Actual Result: What happened (filled during execution)
Status: [Pass | Fail | Blocked]
```

### 2.5 Success Criteria

**Test Phase Success:**

- 100% of Critical priority tests pass
- 95%+ of High priority tests pass
- 90%+ of Medium priority tests pass
- 80%+ of Low priority tests pass

**Overall Success:**

- All functional requirements validated
- All boundary conditions tested
- No critical defects remaining
- All edge cases handled gracefully
- Documentation complete

### 2.6 Risk Analysis

**High Risk Areas:**

1. **Price Formatting Boundaries**: Off-by-one errors at boundaries
2. **Portfolio Calculations**: Floating-point precision issues
3. **Large Numbers**: Overflow in calculations
4. **Null Handling**: Missing data causing crashes

**Mitigation Strategy:**

- Extra attention to boundary tests
- Use multiple test values near boundaries
- Test with extreme values
- Test null/empty scenarios explicitly

---

## 3. Detailed Testing Plan

### 3.1 Test Suite Organization

```
test/
├── com/
│   └── cryptochecker/
│       ├── CoinBlackBoxTest.java           (30+ tests)
│       ├── GlobalDataBlackBoxTest.java     (20+ tests)
│       ├── CryptoCheckerBlackBoxTest.java  (40+ tests)
│       └── TestRunner.java                 (Suite runner)
```

### 3.2 Test Class: CoinBlackBoxTest

**Purpose**: Test WebData.Coin class functionality using black box approach

**Test Categories:**

#### 3.2.1 Equivalence Partition Tests (6 tests)

```java
@Test testTrimPrice_GreaterThanOne()
  Input: 100.5678
  Expected: "100.57"
  Category: EP-1.1

@Test testTrimPrice_BetweenPointOneAndOne()
  Input: 0.5555
  Expected: "0.556"
  Category: EP-1.2

@Test testTrimPrice_BetweenPointZeroOneAndPointOne()
  Input: 0.055555
  Expected: "0.0556"
  Category: EP-1.3

@Test testTrimPrice_BetweenPointZeroZeroOneAndPointZeroOne()
  Input: 0.0055555
  Expected: "0.00556"
  Category: EP-1.4

@Test testTrimPrice_BetweenPointZeroZeroZeroOneAndPointZeroZeroOne()
  Input: 0.00055555
  Expected: "0.000556"
  Category: EP-1.5

@Test testTrimPrice_VerySmallPrices()
  Input: 0.000012345678901234
  Expected: Starts with "0.00001234"
  Category: EP-1.6
```

#### 3.2.2 Boundary Value Analysis Tests (15 tests)

```java
@ParameterizedTest testTrimPrice_BoundaryAtOne()
  Inputs: (0.9999, "0.999"), (1.0, "1"), (1.0001, "1")
  Validates: Transition at 1.0 boundary

@ParameterizedTest testTrimPrice_BoundaryAtPointOne()
  Inputs: (0.0999, "0.0999"), (0.1, "0.1"), (0.1001, "0.1")
  Validates: Transition at 0.1 boundary

@ParameterizedTest testTrimPrice_BoundaryAtPointZeroOne()
  Inputs: (0.00999, "0.00999"), (0.01, "0.01"), (0.01001, "0.0100")
  Validates: Transition at 0.01 boundary

@ParameterizedTest testTrimPrice_BoundaryAtPointZeroZeroOne()
  Inputs: (0.000999, "0.000999"), (0.001, "0.001"), (0.001001, "0.001")
  Validates: Transition at 0.001 boundary

@ParameterizedTest testTrimPrice_BoundaryAtPointZeroZeroZeroOne()
  Inputs: (0.0000999, "0.0000999"), (0.0001, "0.0001"), (0.0001001, "0.0001")
  Validates: Transition at 0.0001 boundary
```

#### 3.2.3 Extreme Value Tests (4 tests)

```java
@Test testTrimPrice_Zero()
  Input: 0.0
  Expected: "0"

@ParameterizedTest testTrimPrice_VeryLargePrices()
  Inputs: 1000.0, 50000.0, 100000.0, 999999.99
  Validates: Large price handling

@ParameterizedTest testTrimPrice_ExtremelySmallPrices()
  Inputs: 0.00000001, 0.000000001, 0.0000000001
  Validates: Micro-value handling
```

#### 3.2.4 Functional Behavior Tests (8 tests)

```java
@Test testToString_ReturnsCoinName()
  Validates: toString() returns coin name

@ParameterizedTest testToString_VariousCoinNames()
  Validates: Various coin names work correctly

@Test testGetInfo_ReturnsFormattedData()
  Validates: All required fields present

@Test testGetInfo_IncludesActualValues()
  Validates: Actual values displayed correctly

@Test testCopy_CreatesIndependentCopy()
  Validates: Copy operation works correctly

@Test testGetPortfolio_IncludesPortfolioData()
  Validates: Portfolio fields displayed
```

### 3.3 Test Class: GlobalDataBlackBoxTest

**Purpose**: Test WebData.Global_Data class functionality

**Test Categories:**

#### 3.3.1 Equivalence Partition Tests (3 tests)

```java
@Test testToString_NormalMarketConditions()
  Market Cap: 2 trillion, Volume: 100 billion
  Bitcoin Dominance: 45.5%
  Category: EP-3.3 (Normal Market)

@Test testToString_BullMarket()
  Market Cap: 5 trillion, Volume: 500 billion
  Bitcoin Dominance: 60.0%
  Category: EP-3.1 (Bull Market)

@Test testToString_BearMarket()
  Market Cap: 500 billion, Volume: 10 billion
  Bitcoin Dominance: 30.0%
  Category: EP-3.2 (Bear Market)
```

#### 3.3.2 Boundary Value Analysis Tests (12 tests)

```java
@Test testToString_ZeroValues()
  All values: 0
  Validates: Minimum boundary handling

@Test testToString_VeryLargeValues()
  Market Cap: 999,999,999,999,999
  Validates: Maximum boundary handling

@ParameterizedTest testToString_BitcoinDominanceBoundaries()
  Inputs: 0.0%, 0.01%, 50.0%, 99.99%, 100.0%
  Validates: All dominance boundaries

@ParameterizedTest testToString_ActiveCountsBoundaries()
  Validates: Various count ranges
```

### 3.4 Test Class: CryptoCheckerBlackBoxTest

**Purpose**: Integration testing of overall application functionality

**Test Categories:**

#### 3.4.1 Portfolio Management Tests (8 tests)

```java
@Test testPortfolio_EmptyInitialization()
  Category: EP-2.1 (Empty Portfolio)

@Test testPortfolio_SingleCoin()
  Category: EP-2.2 (Single Coin)

@Test testPortfolio_MultipleCoins()
  Category: EP-2.3 (Multiple Coins)

@Test testPortfolio_ZeroCoins()
  Category: BVA (Zero boundary)

@Test testPortfolio_ManyCoins()
  Category: EP-2.4 (Large Portfolio)
```

#### 3.4.2 Portfolio Calculation Tests (6 tests)

```java
@Test testPortfolio_PositiveGains()
  Category: EP-5.1 (Gains)
  Current > Purchase

@Test testPortfolio_NegativeGains()
  Category: EP-5.3 (Losses)
  Current < Purchase

@Test testPortfolio_BreakEven()
  Category: EP-5.2 (Break-even)
  Current = Purchase
```

#### 3.4.3 Coin Data Validation Tests (12 tests)

```java
@Test testCoin_AllPositiveChanges()
  Category: EP-4.1/EP-4.2

@Test testCoin_AllNegativeChanges()
  Category: EP-4.4/EP-4.5

@Test testCoin_MixedChanges()
  Category: Mixed EP

@ParameterizedTest testCoin_RankBoundaries()
  Inputs: 1, 10, 100, 1000
  Category: BVA-4
```

---

## 4. Test Case Design

### 4.1 Sample Test Case: Price Formatting at 1.0 Boundary

**Test ID**: TC-BVA-001  
**Category**: Boundary Value Analysis  
**Priority**: Critical  
**Requirement**: FR-2.1, FR-2.2

**Objective**: Verify correct price formatting at the 1.0 boundary

**Test Data**:
| Input Value | Expected Output | Equivalence Partition |
|-------------|----------------|---------------------|
| 0.9999 | "0.999" or similar (3+ decimals) | EP-1.2 (0.1 < x ≤ 1.0) |
| 1.0 | "1" or "1.0" (2 decimals max) | EP-1.1 (x > 1.0) |
| 1.0001 | "1" or "1.0" (2 decimals max) | EP-1.1 (x > 1.0) |

**Test Steps**:

1. Create Coin object
2. Call trimPrice(0.9999)
3. Verify output has 3+ decimal places
4. Call trimPrice(1.0)
5. Verify output has 2 or fewer decimal places
6. Call trimPrice(1.0001)
7. Verify output has 2 or fewer decimal places

**Expected Result**:

- Values < 1.0 display with more precision
- Values ≥ 1.0 display with 2 decimal places
- Clean transition at boundary

**Rationale**: This boundary is critical as it represents the transition between "fractional currency" and "whole currency" display formats.

### 4.2 Sample Test Case: Empty Portfolio Initialization

**Test ID**: TC-EP-005  
**Category**: Equivalence Partitioning  
**Priority**: High  
**Requirement**: FR-5.1

**Objective**: Verify system correctly initializes empty portfolio

**Test Data**:

- Portfolio with 0 coins
- Portfolio names list
- Portfolio number = 0

**Test Steps**:

1. Initialize WebData object
2. Check portfolio is not null
3. Verify portfolio has at least one list (even if empty)
4. Verify portfolio_names is not null
5. Verify portfolio_names has at least one name
6. Check that accessing portfolio.get(0) doesn't crash

**Expected Result**:

- portfolio != null
- portfolio.size() >= 1
- portfolio.get(0).size() == 0 (empty)
- portfolio_names.size() >= 1
- No exceptions thrown

**Rationale**: Empty state is a common starting point and must be handled correctly to prevent crashes.

### 4.3 Sample Test Case: Bitcoin Dominance at 100%

**Test ID**: TC-BVA-025  
**Category**: Boundary Value Analysis  
**Priority**: Medium  
**Requirement**: FR-3.3

**Objective**: Verify correct display of maximum Bitcoin dominance

**Test Data**:

- bitcoin_percentage_of_market_cap = 100.0

**Test Steps**:

1. Create Global_Data object
2. Set bitcoin_percentage_of_market_cap to 100.0
3. Call toString()
4. Verify output contains "100.0%"
5. Verify output is properly formatted
6. Check for any display issues

**Expected Result**:

- toString() output contains "Bitcoin Dominance: 100.0%"
- No errors or warnings
- Percentage symbol present

**Rationale**: 100% represents theoretical maximum Bitcoin dominance, unlikely but must be handled correctly.

### 4.4 Sample Test Case: Portfolio Gains Calculation

**Test ID**: TC-EP-020  
**Category**: Equivalence Partitioning  
**Priority**: Critical  
**Requirement**: FR-5.4

**Objective**: Verify correct calculation of portfolio gains

**Test Data**:

```
Coin: Bitcoin
Portfolio Amount: 2.0 BTC
Purchase Price: $40,000
Purchase Value: $80,000
Current Price: $50,000
Current Value: $100,000
Expected Gains: $20,000
```

**Test Steps**:

1. Create Coin with portfolio data
2. Set portfolio_amount = 2.0
3. Set portfolio_price_start = 40000.0
4. Set portfolio_value_start = 80000.0
5. Set current price = 50000.0
6. Set portfolio_value = 100000.0
7. Set portfolio_gains = 20000.0
8. Call getPortfolio()
9. Verify output contains all portfolio fields

**Expected Result**:

- portfolio_gains = 20000.0 (positive)
- portfolio_value > portfolio_value_start
- getPortfolio() displays all values correctly

**Rationale**: Portfolio gains calculation is core functionality that users rely on for investment tracking.

---

## 5. Expected Outcomes

### 5.1 Test Metrics

**Coverage Metrics:**

- **Functional Requirement Coverage**: 100% (all 8 FR categories)
- **Equivalence Partition Coverage**: 100% (all 31 partitions)
- **Boundary Value Coverage**: 100% (all 61 boundaries)
- **Test Case Execution**: 92+ test cases

**Quality Metrics:**

- **Pass Rate Target**: ≥95% on first execution
- **Critical Defect Target**: 0 defects
- **High Priority Defect Target**: ≤2 defects
- **Code Coverage**: Not applicable (black box testing)

### 5.2 Expected Defect Categories

Based on black box testing experience, expected defect distribution:

| Defect Category           | Expected Count | Priority |
| ------------------------- | -------------- | -------- |
| Boundary Condition Errors | 2-4            | High     |
| Formatting Issues         | 1-3            | Medium   |
| Null Handling             | 1-2            | High     |
| Calculation Precision     | 0-2            | Critical |
| Edge Case Handling        | 2-5            | Medium   |
| Display/UI Issues         | 1-3            | Low      |

### 5.3 Deliverables

1. **Test Plan Document** (this document)
2. **Test Cases** (92+ automated tests)
3. **Test Execution Report**
4. **Defect Report**
5. **Test Coverage Report**
6. **Recommendation Report**

### 5.4 Success Indicators

✅ All critical functionality validated  
✅ All boundary conditions tested  
✅ Edge cases handled gracefully  
✅ No critical defects in production code  
✅ Comprehensive test documentation  
✅ Automated test suite operational  
✅ Clear defect prioritization and tracking

### 5.5 Recommendations

Based on black box testing methodology:

1. **Implement Automated Tests**: All 92+ tests should be automated using JUnit 5
2. **CI/CD Integration**: Run tests automatically on every commit
3. **Regular Regression Testing**: Execute full suite before releases
4. **Boundary Value Monitoring**: Pay special attention to boundary condition failures
5. **Test Data Management**: Maintain comprehensive test data sets
6. **Documentation**: Keep test cases synchronized with requirements

---

## Conclusion

This Black Box Testing strategy provides comprehensive coverage of the Crypto Checker application using industry-standard techniques of Equivalence Partitioning and Boundary Value Analysis. The 92+ test cases cover all functional requirements, focus on areas where defects are most likely to occur (boundaries), and validate the system from a user's perspective without requiring knowledge of internal implementation.

The testing approach balances thoroughness with efficiency, using equivalence partitioning to reduce redundant tests while using boundary value analysis to catch off-by-one errors and transition issues. This methodology ensures high-quality software that behaves correctly across all input ranges and edge cases.

---

**Document End**

**Prepared by**: AI Testing Team  
**Review Status**: Draft  
**Next Review Date**: TBD
