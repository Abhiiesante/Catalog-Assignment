# Shamir's Secret Sharing - Assignment Solution Summary

## 🎯 Solution Overview

This repository contains a complete implementation of Shamir's Secret Sharing solver that successfully processes both test cases and finds the polynomial's constant term (secret).

## 📊 Results

### Test Case 1
- **Input**: 4 roots, need minimum 3 (k=3)
- **Secret Found**: **3**
- **Polynomial**: f(x) = 3 + x²
- **Verification**: All points (1,4), (2,7), (3,12), (6,39) satisfy f(x) = 3 + x²

### Test Case 2  
- **Input**: 10 roots, need minimum 7 (k=7)
- **Secret Found**: **79836264049851**
- **Polynomial**: 6th degree with 7 coefficients
- **Verification**: All algorithms agree on the secret value

## 🛠 Implementation Details

### JavaScript Implementation (Node.js)
- **File**: `shamir_secret_solver.js`
- **Algorithm**: Lagrange Interpolation with BigInt for large numbers
- **Features**: JSON parsing, base conversion, precise calculation
- **Run**: `node shamir_secret_solver.js`

### Java Implementation (Advanced)
- **File**: `PolynomialSolver.java`
- **Algorithms**: 4 different mathematical approaches:
  1. Newton's Divided Differences
  2. Lagrange Interpolation  
  3. Vandermonde Matrix Method
  4. Least Squares Regression
- **Features**: Multiple algorithm comparison, high precision with BigInteger
- **Run**: `./run.sh` or `java -cp .:gson-2.10.1.jar PolynomialSolver`

## 📁 Repository Structure

```
Catalog-Assignment/
├── shamir_secret_solver.js    # JavaScript implementation
├── PolynomialSolver.java      # Java implementation (4 algorithms)
├── testcase1.json            # Test case 1 data
├── testcase2.json            # Test case 2 data
├── test.js                   # Verification tests
├── build.sh                  # Java build script
├── run.sh                    # Java run script
├── gson-2.10.1.jar          # JSON parsing library
├── package.json             # Node.js configuration
└── README.md                # Detailed documentation
```

## 🔬 Algorithm Verification

All implementations use mathematically sound approaches:

1. **Base Conversion**: Correctly converts encoded Y values from various bases (2, 3, 4, 6, 7, 8, 10, 12, 15, 16)
2. **Lagrange Interpolation**: Uses the formula f(0) = Σ(yi * Li(0)) where Li(0) is the Lagrange basis polynomial
3. **Precision Handling**: Uses BigInt/BigInteger to handle very large numbers without precision loss
4. **Cross-Verification**: Multiple algorithms produce identical results, confirming correctness

## 🚀 Quick Start

```bash
# Clone the repository
git clone https://github.com/Abhiiesante/Catalog-Assignment.git
cd Catalog-Assignment

# Run JavaScript version
node shamir_secret_solver.js

# Run Java version  
./run.sh
```

## 📈 Output Sample

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

## ✅ Assignment Checkpoints Completed

1. ✅ **Read Test Case from JSON**: Both test cases parsed correctly
2. ✅ **Decode Y Values**: All bases (2,3,4,6,7,8,10,12,15,16) handled correctly  
3. ✅ **Find Secret (C)**: Constant term calculated using Lagrange interpolation
4. ✅ **Multiple Algorithms**: Java version implements 4 different approaches
5. ✅ **Verification**: Manual verification confirms polynomial f(x) = 3 + x² for test case 1

## 🔗 Repository Link

**GitHub Repository**: https://github.com/Abhiiesante/Catalog-Assignment

The complete solution is ready for submission and demonstrates both theoretical understanding and practical implementation of Shamir's Secret Sharing scheme.
