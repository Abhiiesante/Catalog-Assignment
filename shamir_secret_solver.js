const fs = require('fs');

/**
 * Convert a number from any base to decimal
 * @param {string} value - The value to convert
 * @param {number} base - The base of the input value
 * @returns {bigint} - The decimal value as BigInt for large numbers
 */
function convertToDecimal(value, base) {
    return BigInt(parseInt(value, base));
}

/**
 * Parse the JSON input and extract roots
 * @param {object} data - The JSON data
 * @returns {Array} - Array of [x, y] pairs
 */
function parseInput(data) {
    const roots = [];
    const n = data.keys.n;
    
    // Extract all roots (x, y pairs)
    for (let key in data) {
        if (key !== 'keys' && !isNaN(key)) {
            const x = BigInt(key);
            const base = parseInt(data[key].base);
            const encodedValue = data[key].value;
            const y = convertToDecimal(encodedValue, base);
            roots.push([x, y]);
        }
    }
    
    return roots;
}

/**
 * Calculate the secret using Lagrange interpolation
 * We need to find f(0) where f(x) is the polynomial
 * @param {Array} points - Array of [x, y] pairs
 * @param {number} k - Minimum number of points needed
 * @returns {bigint} - The secret (constant term)
 */
function findSecret(points, k) {
    // Take only k points (minimum required)
    const selectedPoints = points.slice(0, k);
    
    let secret = BigInt(0);
    
    // Lagrange interpolation to find f(0)
    for (let i = 0; i < selectedPoints.length; i++) {
        const [xi, yi] = selectedPoints[i];
        
        // Calculate Lagrange basis polynomial Li(0)
        let numerator = BigInt(1);
        let denominator = BigInt(1);
        
        for (let j = 0; j < selectedPoints.length; j++) {
            if (i !== j) {
                const [xj, ] = selectedPoints[j];
                numerator *= (BigInt(0) - xj);  // (0 - xj)
                denominator *= (xi - xj);       // (xi - xj)
            }
        }
        
        // Add yi * Li(0) to the result
        secret += yi * numerator / denominator;
    }
    
    return secret;
}

/**
 * Main function to solve the secret sharing problem
 * @param {string} filename - Path to the JSON file
 */
function solveShamirSecret(filename) {
    try {
        // Read and parse the JSON file
        const data = JSON.parse(fs.readFileSync(filename, 'utf8'));
        
        console.log(`\n=== Processing ${filename} ===`);
        console.log(`n (total roots): ${data.keys.n}`);
        console.log(`k (minimum required): ${data.keys.k}`);
        
        // Parse input and get roots
        const roots = parseInput(data);
        
        console.log('\nDecoded roots (x, y):');
        roots.forEach(([x, y]) => {
            console.log(`(${x}, ${y})`);
        });
        
        // Find the secret
        const secret = findSecret(roots, data.keys.k);
        
        console.log(`\nSecret (constant term): ${secret}`);
        console.log(`Secret (as regular number): ${Number(secret)}`);
        
        return secret;
        
    } catch (error) {
        console.error(`Error processing ${filename}:`, error.message);
        return null;
    }
}

/**
 * Verify the solution by checking if the polynomial passes through all points
 * @param {Array} coefficients - Polynomial coefficients [a0, a1, a2, ...]
 * @param {Array} points - Points to verify
 */
function verifyPolynomial(coefficients, points) {
    console.log('\n=== Verification ===');
    
    points.forEach(([x, expectedY]) => {
        let calculatedY = BigInt(0);
        let xPower = BigInt(1);
        
        // Calculate f(x) = a0 + a1*x + a2*x^2 + ...
        for (let i = 0; i < coefficients.length; i++) {
            calculatedY += coefficients[i] * xPower;
            xPower *= x;
        }
        
        const matches = calculatedY === expectedY;
        console.log(`f(${x}) = ${calculatedY}, expected: ${expectedY}, match: ${matches}`);
    });
}

// Main execution
function main() {
    console.log('Shamir\'s Secret Sharing Solver');
    console.log('================================');
    
    // Solve both test cases
    const secret1 = solveShamirSecret('testcase1.json');
    const secret2 = solveShamirSecret('testcase2.json');
    
    console.log('\n=== Summary ===');
    console.log(`Test case 1 secret: ${secret1}`);
    console.log(`Test case 2 secret: ${secret2}`);
}

// Run the program
if (require.main === module) {
    main();
}

module.exports = {
    convertToDecimal,
    parseInput,
    findSecret,
    solveShamirSecret
};
