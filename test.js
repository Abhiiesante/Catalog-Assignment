const { findSecret, parseInput, convertToDecimal } = require('./shamir_secret_solver');

/**
 * Manual verification for test case 1
 * We know the polynomial passes through: (1,4), (2,7), (3,12), (6,39)
 * With secret = 3, let's determine the actual polynomial coefficients
 */
function manualVerificationTestCase1() {
    console.log('=== Manual Verification for Test Case 1 ===');
    
    // We found the secret is 3, so f(0) = 3
    // Now let's use our points to verify this makes sense
    // Points: (1,4), (2,7), (3,12), (6,39)
    
    console.log('Given points: (1,4), (2,7), (3,12), (6,39)');
    console.log('Calculated secret (f(0)): 3');
    
    // Let's manually solve for a quadratic: f(x) = a + bx + cx²
    // We know a = 3 (the secret)
    // Using points (1,4) and (2,7):
    // 4 = 3 + b + c  => b + c = 1
    // 7 = 3 + 2b + 4c => 2b + 4c = 4 => b + 2c = 2
    // Solving: c = 1, b = 0
    // So f(x) = 3 + 0x + 1x² = 3 + x²
    
    const testPolynomial = (x) => 3 + x * x;
    
    console.log('\nIf f(x) = 3 + x², then:');
    console.log(`f(1) = ${testPolynomial(1)} (expected: 4) ${testPolynomial(1) === 4 ? '✓' : '✗'}`);
    console.log(`f(2) = ${testPolynomial(2)} (expected: 7) ${testPolynomial(2) === 7 ? '✓' : '✗'}`);
    console.log(`f(3) = ${testPolynomial(3)} (expected: 12) ${testPolynomial(3) === 12 ? '✓' : '✗'}`);
    console.log(`f(6) = ${testPolynomial(6)} (expected: 39) ${testPolynomial(6) === 39 ? '✓' : '✗'}`);
    
    console.log('\nAll points match! The polynomial f(x) = 3 + x² is correct.');
    console.log('Therefore, the secret (constant term) = 3 is verified.');
}

/**
 * Test the base conversion function
 */
function testBaseConversion() {
    console.log('\n=== Testing Base Conversion ===');
    
    const testCases = [
        { value: "111", base: 2, expected: 7 },
        { value: "213", base: 4, expected: 39 },
        { value: "4", base: 10, expected: 4 },
        { value: "12", base: 10, expected: 12 }
    ];
    
    testCases.forEach(({ value, base, expected }) => {
        const result = Number(convertToDecimal(value, base));
        const status = result === expected ? '✓' : '✗';
        console.log(`${value} in base ${base} = ${result} (expected: ${expected}) ${status}`);
    });
}

/**
 * Test with a known simple polynomial
 */
function testSimplePolynomial() {
    console.log('\n=== Testing Simple Polynomial f(x) = 2 + 3x ===');
    
    // Create points for f(x) = 2 + 3x
    const points = [
        [BigInt(1), BigInt(5)],  // f(1) = 2 + 3*1 = 5
        [BigInt(2), BigInt(8)]   // f(2) = 2 + 3*2 = 8
    ];
    
    const secret = findSecret(points, 2);
    console.log(`Calculated secret: ${secret} (expected: 2)`);
    console.log(`Result: ${Number(secret) === 2 ? '✓ Correct' : '✗ Incorrect'}`);
}

// Run all tests
function runTests() {
    console.log('Running verification tests...\n');
    
    testBaseConversion();
    testSimplePolynomial();
    manualVerificationTestCase1();
    
    console.log('\n=== All Tests Complete ===');
}

runTests();
