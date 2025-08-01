# Shamir's Secret Sharing - Vandermonde Matrix Implementation

## Overview

This Java program implements Shamir's Secret Sharing reconstruction using the **Vandermonde Matrix Method** to find the polynomial's constant term (secret).

## Algorithm

The Vandermonde matrix approach solves the linear system:

```
| 1  x1  x1²  ...  x1^(k-1) |   | a0 |   | y1 |
| 1  x2  x2²  ...  x2^(k-1) | × | a1 | = | y2 |
| ...                       |   | .. |   | .. |
| 1  xk  xk²  ...  xk^(k-1) |   |ak-1|   | yk |
```

Where:
- `(xi, yi)` are the decoded coordinate points
- `ai` are the polynomial coefficients
- `a0` is the secret (constant term)

## Files

- `ShamirSecretSolver.java` - Main implementation using Vandermonde matrix
- `testcase1.json` - Test case 1 (4 points, k=3)
- `testcase2.json` - Test case 2 (10 points, k=7)
- `README.md` - This documentation

## Usage

### Compilation
```bash
javac ShamirSecretSolver.java
```

### Execution
```bash
java ShamirSecretSolver
```

## Results

### Test Case 1
- **Secret**: 3
- **Polynomial**: f(x) = 3 + 0x + 1x² = 3 + x²
- **Verification**: Points (1,4), (2,7), (3,12), (6,39) all satisfy f(x) = 3 + x²

### Test Case 2
- **Secret**: 79836264049851
- **Polynomial**: 6th degree polynomial with coefficients [79836264049851, 92534348706405, 234176747398429, 147160079768248, 105860038268942, 129715447661077, 205802168748539]
- **All calculations**: Performed with high precision using BigInteger and BigDecimal

## Implementation Features

- **Pure Java**: No external dependencies required
- **High Precision**: Uses BigInteger for exact arithmetic on large numbers
- **Gaussian Elimination**: Implements forward elimination with partial pivoting and back substitution
- **JSON Parsing**: Manual regex-based parsing for simplicity
- **Base Conversion**: Handles multiple number bases (2, 3, 4, 6, 7, 8, 10, 12, 15, 16)

## Output Example

```
=== Shamir's Secret Sharing Solver ===
Using Vandermonde Matrix Method

Processing: testcase1.json
n (total points): 4
k (minimum required): 3

Decoded points (x, y):
(1, 4)
(2, 7)
(3, 12)
(6, 39)
Solving using Vandermonde Matrix Method with 3 points...
Vandermonde matrix solved successfully!

*** SECRET FOUND: 3 ***
Full polynomial coefficients: [3, 0, 1]
===================================================

Processing: testcase2.json
n (total points): 10
k (minimum required): 7

Decoded points (x, y):
(1, 995085094601491)
(2, 21394886326566393)
(3, 196563650089608567)
(4, 1016509518118225951)
(5, 3711974121218449851)
(6, 10788619898233492461)
(7, 26709394976508342463)
(8, 58725075613853308713)
(9, 117852986202006511971)
(10, 220003896831595324801)
Solving using Vandermonde Matrix Method with 7 points...
Vandermonde matrix solved successfully!

*** SECRET FOUND: 79836264049851 ***
Full polynomial coefficients: [79836264049851, 92534348706405, 234176747398429, 147160079768248, 105860038268942, 129715447661077, 205802168748539]
===================================================
```

## Mathematical Background

Shamir's Secret Sharing is based on polynomial interpolation. Given k points, we can uniquely determine a polynomial of degree k-1. The secret is stored as the constant term (y-intercept) of this polynomial.

The Vandermonde matrix method provides a direct linear algebra approach to find all polynomial coefficients by solving the system of linear equations formed by the known points.

## Repository

**GitHub Repository**: https://github.com/Abhiiesante/Catalog-Assignment
