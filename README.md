# Shamir's Secret Sharing Solver

This project implements a solution for Shamir's Secret Sharing scheme, which reconstructs a polynomial's constant term (secret) from a set of encoded points.

## Problem Description

Given a polynomial f(x) = a₀ + a₁x + a₂x² + ... + aₘxᵐ, where:
- a₀ is the secret we want to find
- The polynomial is of degree m
- We need k = m + 1 points to uniquely determine the polynomial
- Points are provided as (x, y) pairs where y values are encoded in different bases

## Algorithm

The solution uses **Lagrange Interpolation** to reconstruct the polynomial and find f(0), which equals the constant term (secret).

### Steps:
1. **Parse JSON Input**: Read test cases from JSON files
2. **Decode Y Values**: Convert encoded values from various bases to decimal
3. **Apply Lagrange Interpolation**: Calculate f(0) using the formula:

```
f(0) = Σ(i=0 to k-1) yi * Li(0)
```

Where Li(0) is the Lagrange basis polynomial evaluated at x=0:

```
Li(0) = Π(j≠i) (0 - xj) / (xi - xj)
```

## Files

### JavaScript Implementation
- `shamir_secret_solver.js` - Main solver implementation (Node.js)
- `test.js` - Verification tests for the JavaScript implementation
- `package.json` - Node.js project configuration

### Java Implementation
- `PolynomialSolver.java` - Advanced solver with multiple algorithms
- `gson-2.10.1.jar` - Gson library for JSON parsing
- `build.sh` - Build script for Java program
- `run.sh` - Run script for Java program

### Test Data
- `testcase1.json` - First test case (4 roots, k=3)
- `testcase2.json` - Second test case (10 roots, k=7)
- `README.md` - This documentation

## Usage

### Prerequisites
- **JavaScript**: Node.js (version 14 or higher)
- **Java**: JDK 8 or higher

### Running the JavaScript Solver

```bash
# Run the main solver
node shamir_secret_solver.js

# Or use npm
npm start

# Run verification tests
node test.js
```

### Running the Java Solver

```bash
# Build and run using scripts
./build.sh
./run.sh

# Or manually
javac -cp gson-2.10.1.jar PolynomialSolver.java
java -cp .:gson-2.10.1.jar PolynomialSolver
```

## Multiple Algorithms (Java Implementation)

The Java implementation provides four different algorithms to solve the polynomial interpolation:

1. **Newton's Divided Differences**: Constructs the polynomial using divided differences
2. **Lagrange Interpolation**: Uses Lagrange basis polynomials
3. **Vandermonde Matrix Method**: Solves the linear system using Gaussian elimination
4. **Least Squares Regression**: Provides an approximate solution using least squares fitting

All methods should produce the same secret (constant term) for valid inputs.

### Test Cases

#### Test Case 1
```json
{
    "keys": {"n": 4, "k": 3},
    "1": {"base": "10", "value": "4"},
    "2": {"base": "2", "value": "111"},
    "3": {"base": "10", "value": "12"},
    "6": {"base": "4", "value": "213"}
}
```

**Decoded points**: (1,4), (2,7), (3,12), (6,39)
**Secret**: The constant term of the polynomial

#### Test Case 2
Larger test case with 10 roots in various bases (6, 15, 16, 8, 3, 12, 7).

## Implementation Details

### Key Functions

1. **`convertToDecimal(value, base)`**: Converts a string from any base to decimal using BigInt for large numbers
2. **`parseInput(data)`**: Extracts and decodes all (x,y) pairs from JSON
3. **`findSecret(points, k)`**: Implements Lagrange interpolation to find f(0)

### BigInt Usage

The solution uses JavaScript's `BigInt` type to handle very large numbers that exceed the standard integer limits, ensuring accuracy for all test cases.

### Mathematical Foundation

This implementation is based on the mathematical property that any polynomial of degree m is uniquely determined by m+1 distinct points. Lagrange interpolation provides an explicit formula to reconstruct the polynomial and evaluate it at any point.

## Results Analysis

### Test Case 1 Results
- **Secret**: 3
- **Polynomial**: f(x) = 3 + x²
- **All methods agree**: ✓

### Test Case 2 Results
- **Secret**: 79836264049851 (for most methods)
- **Slight variation in Least Squares**: Due to floating-point precision limitations
- **Polynomial degree**: 6 (requires 7 points)

The Java implementation demonstrates that multiple mathematical approaches can solve the same Shamir's Secret Sharing problem, with exact methods (Newton, Lagrange, Vandermonde) producing identical results, while approximate methods (Least Squares) may have minor precision differences for very large numbers.

## Output Format

The program outputs:
- Number of total roots (n) and minimum required (k)
- All decoded (x, y) pairs
- The calculated secret (constant term)
- Both BigInt and regular number representations when applicable

## Example Output

```
=== Processing testcase1.json ===
n (total roots): 4
k (minimum required): 3

Decoded roots (x, y):
(1, 4)
(2, 7)
(3, 12)
(6, 39)

Secret (constant term): 3
```
