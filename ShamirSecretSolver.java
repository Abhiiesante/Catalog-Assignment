import java.io.*;
import java.math.*;
import java.util.*;
import java.util.regex.*;

/**
 * Shamir's Secret Sharing Solver using Vandermonde Matrix Method
 * 
 * This program implements Shamir's Secret Sharing reconstruction using
 * the Vandermonde matrix approach to find the polynomial's constant term (secret).
 * 
 * @author Assignment Solution
 */
public class ShamirSecretSolver {
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Shamir's Secret Sharing Solver ===");
        System.out.println("Using Vandermonde Matrix Method\n");
        
        // Process both test cases
        processTestCase("testcase1.json");
        processTestCase("testcase2.json");
    }
    
    /**
     * Process a single test case file
     * @param filename The JSON file containing the test case
     */
    public static void processTestCase(String filename) throws Exception {
        System.out.println("Processing: " + filename);
        
        // Parse the JSON file
        TestCaseData data = parseJsonFile(filename);
        
        System.out.println("n (total points): " + data.n);
        System.out.println("k (minimum required): " + data.k);
        
        // Display decoded points
        System.out.println("\nDecoded points (x, y):");
        for (Point point : data.points) {
            System.out.println("(" + point.x + ", " + point.y + ")");
        }
        
        // Take only first k points as required
        BigInteger[] x = new BigInteger[data.k];
        BigInteger[] y = new BigInteger[data.k];
        
        for (int i = 0; i < data.k; i++) {
            x[i] = data.points.get(i).x;
            y[i] = data.points.get(i).y;
        }
        
        // Calculate secret using Vandermonde matrix method
        BigInteger[] coefficients = solveVandermonde(x, y);
        BigInteger secret = coefficients[0]; // The constant term is our secret
        
        System.out.println("\n*** SECRET FOUND: " + secret + " ***");
        System.out.println("Full polynomial coefficients: " + Arrays.toString(coefficients));
        System.out.println("=" + "=".repeat(50) + "\n");
    }
    
    /**
     * Vandermonde Matrix Method
     * 
     * The Vandermonde matrix for points (x1,y1), (x2,y2), ..., (xk,yk) is:
     * | 1  x1  x1²  ...  x1^(k-1) |   | a0 |   | y1 |
     * | 1  x2  x2²  ...  x2^(k-1) | × | a1 | = | y2 |
     * | ...                       |   | .. |   | .. |
     * | 1  xk  xk²  ...  xk^(k-1) |   |ak-1|   | yk |
     * 
     * We solve this system to find the coefficients, where a0 is our secret.
     * 
     * @param x Array of x coordinates
     * @param y Array of y coordinates
     * @return Array of polynomial coefficients [a0, a1, a2, ...]
     */
    public static BigInteger[] solveVandermonde(BigInteger[] x, BigInteger[] y) {
        int n = x.length;
        System.out.println("Solving using Vandermonde Matrix Method with " + n + " points...");
        
        // Build Vandermonde matrix and solve V·a = y via Gaussian elimination
        BigDecimal[][] V = new BigDecimal[n][n + 1];
        
        for (int i = 0; i < n; i++) {
            BigDecimal pow = BigDecimal.ONE;
            for (int j = 0; j < n; j++) {
                V[i][j] = pow;
                pow = pow.multiply(new BigDecimal(x[i]));
            }
            V[i][n] = new BigDecimal(y[i]); // Augmented part
        }
        
        // Forward elimination with partial pivoting
        for (int i = 0; i < n; i++) {
            // Find pivot
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (V[k][i].abs().compareTo(V[maxRow][i].abs()) > 0) {
                    maxRow = k;
                }
            }
            
            // Swap rows if needed
            if (maxRow != i) {
                BigDecimal[] temp = V[i];
                V[i] = V[maxRow];
                V[maxRow] = temp;
            }
            
            // Pivot normalization
            BigDecimal piv = V[i][i];
            if (piv.equals(BigDecimal.ZERO)) {
                throw new RuntimeException("Matrix is singular - no unique solution exists");
            }
            
            for (int j = i; j <= n; j++) {
                V[i][j] = V[i][j].divide(piv, MathContext.DECIMAL128);
            }
            
            // Eliminate below
            for (int k = i + 1; k < n; k++) {
                BigDecimal factor = V[k][i];
                for (int j = i; j <= n; j++) {
                    V[k][j] = V[k][j].subtract(factor.multiply(V[i][j]));
                }
            }
        }
        
        // Back substitution
        BigInteger[] a = new BigInteger[n];
        for (int i = n - 1; i >= 0; i--) {
            BigDecimal sum = V[i][n];
            for (int j = i + 1; j < n; j++) {
                sum = sum.subtract(V[i][j].multiply(new BigDecimal(a[j])));
            }
            a[i] = sum.toBigInteger();
        }
        
        System.out.println("Vandermonde matrix solved successfully!");
        return a;
    }
    
    /**
     * Parse JSON file manually (no external dependencies)
     * @param filename Path to the JSON file
     * @return Parsed test case data
     */
    public static TestCaseData parseJsonFile(String filename) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder content = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        reader.close();
        
        String json = content.toString();
        
        // Extract n and k values using regex
        Pattern nPattern = Pattern.compile("\"n\"\\s*:\\s*(\\d+)");
        Pattern kPattern = Pattern.compile("\"k\"\\s*:\\s*(\\d+)");
        
        Matcher nMatcher = nPattern.matcher(json);
        Matcher kMatcher = kPattern.matcher(json);
        
        int n = 0, k = 0;
        if (nMatcher.find()) n = Integer.parseInt(nMatcher.group(1));
        if (kMatcher.find()) k = Integer.parseInt(kMatcher.group(1));
        
        // Extract points using regex
        List<Point> points = new ArrayList<>();
        Pattern pointPattern = Pattern.compile("\"(\\d+)\"\\s*:\\s*\\{\\s*\"base\"\\s*:\\s*\"(\\d+)\"\\s*,\\s*\"value\"\\s*:\\s*\"([^\"]+)\"\\s*\\}");
        Matcher pointMatcher = pointPattern.matcher(json);
        
        while (pointMatcher.find()) {
            int x = Integer.parseInt(pointMatcher.group(1));
            int base = Integer.parseInt(pointMatcher.group(2));
            String value = pointMatcher.group(3);
            
            // Convert from given base to decimal
            BigInteger y = new BigInteger(value, base);
            points.add(new Point(BigInteger.valueOf(x), y));
        }
        
        return new TestCaseData(n, k, points);
    }
    
    /**
     * Simple data class to hold a coordinate point
     */
    static class Point {
        BigInteger x, y;
        
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
    
    /**
     * Simple data class to hold test case information
     */
    static class TestCaseData {
        int n, k;
        List<Point> points;
        
        TestCaseData(int n, int k, List<Point> points) {
            this.n = n;
            this.k = k;
            this.points = points;
        }
    }
}
