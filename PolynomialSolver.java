// File: PolynomialSolver.java
// Requires: com.google.code.gson:gson:2.8.9 (or later)

import com.google.gson.*;
import java.io.FileReader;
import java.math.*;
import java.util.*;

public class PolynomialSolver {

    public static void main(String[] args) throws Exception {
        // Process both test cases
        System.out.println("=== Polynomial Solver - Multiple Methods ===\n");
        
        processTestCase("testcase1.json");
        processTestCase("testcase2.json");
    }
    
    public static void processTestCase(String filename) throws Exception {
        System.out.println("Processing: " + filename);
        
        // 1. Read & parse JSON input
        JsonObject json = JsonParser.parseReader(new FileReader(filename)).getAsJsonObject();
        int n = json.getAsJsonObject("keys").get("n").getAsInt();
        int k = json.getAsJsonObject("keys").get("k").getAsInt();  // m + 1

        System.out.println("n (total roots): " + n);
        System.out.println("k (minimum required): " + k);

        // 2. Decode roots
        List<BigInteger> X = new ArrayList<>();
        List<BigInteger> Y = new ArrayList<>();
        for (Map.Entry<String, JsonElement> e : json.entrySet()) {
            if (e.getKey().equals("keys")) continue;
            int xi = Integer.parseInt(e.getKey());
            JsonObject pair = e.getValue().getAsJsonObject();
            int base = pair.get("base").getAsInt();
            String val = pair.get("value").getAsString();
            BigInteger yi = new BigInteger(val, base);
            X.add(BigInteger.valueOf(xi));
            Y.add(yi);
            System.out.println("Decoded point: (" + xi + ", " + yi + ")");
        }

        // Only take first k points
        BigInteger[] x = X.subList(0, k).toArray(new BigInteger[0]);
        BigInteger[] y = Y.subList(0, k).toArray(new BigInteger[0]);

        // 3. Solve via each method
        System.out.println("\nSolving polynomial...");
        
        BigInteger[] coeffsNewton  = newtonDividedDiff(x, y);
        BigInteger[] coeffsLagrange = lagrangeInterpolation(x, y);
        BigInteger[] coeffsVandermonde = solveVandermonde(x, y);
        
        // For least squares, convert to BigDecimal
        BigDecimal[] xDecimal = Arrays.stream(x).map(BigDecimal::new).toArray(BigDecimal[]::new);
        BigDecimal[] yDecimal = Arrays.stream(y).map(BigDecimal::new).toArray(BigDecimal[]::new);
        double[] coeffsLS = leastSquares(xDecimal, yDecimal, k - 1);

        // 4. Output results
        System.out.println("\n--- Results ---");
        System.out.println("Newton's Divided Differences Secret (a0): " + coeffsNewton[0]);
        System.out.println("Lagrange Interpolation Secret (a0):      " + coeffsLagrange[0]);
        System.out.println("Vandermonde Solution Secret (a0):        " + coeffsVandermonde[0]);
        System.out.println("Least Squares Secret (a0):               " + Math.round(coeffsLS[0]));
        
        System.out.println("\nFull coefficient arrays:");
        System.out.println("Newton coeffs:      " + Arrays.toString(coeffsNewton));
        System.out.println("Lagrange coeffs:    " + Arrays.toString(coeffsLagrange));
        System.out.println("Vandermonde coeffs: " + Arrays.toString(coeffsVandermonde));
        System.out.println("Least Squares coeffs: " + Arrays.toString(coeffsLS));
        
        System.out.println("\n" + "=".repeat(60) + "\n");
    }

    // ==============================
    // 1) Newton's Divided Differences
    // ==============================
    public static BigInteger[] newtonDividedDiff(BigInteger[] x, BigInteger[] y) {
        int n = x.length;
        BigInteger[][] dd = new BigInteger[n][n];
        
        // Initialize first column with y values
        for (int i = 0; i < n; i++) {
            dd[i][0] = y[i];
        }
        
        // Compute divided differences
        for (int j = 1; j < n; j++) {
            for (int i = 0; i + j < n; i++) {
                BigInteger num = dd[i+1][j-1].subtract(dd[i][j-1]);
                BigInteger den = x[i+j].subtract(x[i]);
                dd[i][j] = num.divide(den);
            }
        }
        
        // Convert Newton form to standard polynomial coefficients
        BigInteger[] a = new BigInteger[n];
        Arrays.fill(a, BigInteger.ZERO);
        a[0] = dd[0][0];
        
        BigInteger[] poly = new BigInteger[n];
        Arrays.fill(poly, BigInteger.ZERO);
        poly[0] = BigInteger.ONE;
        
        for (int order = 1; order < n; order++) {
            // Multiply current poly by (X - x[order-1])
            BigInteger[] next = new BigInteger[n];
            Arrays.fill(next, BigInteger.ZERO);
            
            for (int i = 0; i <= order; i++) {
                if (i > 0) {
                    next[i] = next[i].add(poly[i-1]);
                }
                if (i < order) {
                    next[i] = next[i].subtract(x[order-1].multiply(poly[i]));
                }
            }
            poly = next;
            
            // Accumulate coefficients
            for (int i = 0; i < n; i++) {
                a[i] = a[i].add(poly[i].multiply(dd[0][order]));
            }
        }
        return a;
    }

    // ========================
    // 2) Lagrange Interpolation
    // ========================
    public static BigInteger[] lagrangeInterpolation(BigInteger[] x, BigInteger[] y) {
        int n = x.length;
        BigInteger[] coeffs = new BigInteger[n];
        Arrays.fill(coeffs, BigInteger.ZERO);
        
        for (int i = 0; i < n; i++) {
            // Compute L_i(X) = ∏_{j≠i} (X - x_j) / (x_i - x_j)
            BigInteger[] Li = new BigInteger[n];
            Arrays.fill(Li, BigInteger.ZERO);
            Li[0] = BigInteger.ONE;
            BigInteger denom = BigInteger.ONE;
            
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    // Multiply Li by (X - x[j])
                    BigInteger[] tmp = new BigInteger[n];
                    Arrays.fill(tmp, BigInteger.ZERO);
                    
                    for (int k = 0; k < n; k++) {
                        if (!Li[k].equals(BigInteger.ZERO)) {
                            // Constant term: -x[j] * Li[k]
                            tmp[k] = tmp[k].add(Li[k].multiply(x[j].negate()));
                            // X term: Li[k]
                            if (k + 1 < n) {
                                tmp[k+1] = tmp[k+1].add(Li[k]);
                            }
                        }
                    }
                    Li = tmp;
                    denom = denom.multiply(x[i].subtract(x[j]));
                }
            }
            
            // Add y[i]/denom * Li to final coefficients
            for (int k = 0; k < n; k++) {
                if (!Li[k].equals(BigInteger.ZERO)) {
                    coeffs[k] = coeffs[k].add(Li[k].multiply(y[i]).divide(denom));
                }
            }
        }
        return coeffs;
    }

    // ======================================
    // 3) Vandermonde Matrix Inversion Method
    // ======================================
    public static BigInteger[] solveVandermonde(BigInteger[] x, BigInteger[] y) {
        int n = x.length;
        
        // Build Vandermonde matrix and augment with y vector
        BigDecimal[][] V = new BigDecimal[n][n+1];
        for (int i = 0; i < n; i++) {
            BigDecimal pow = BigDecimal.ONE;
            for (int j = 0; j < n; j++) {
                V[i][j] = pow;
                pow = pow.multiply(new BigDecimal(x[i]));
            }
            V[i][n] = new BigDecimal(y[i]);
        }
        
        // Gaussian elimination with partial pivoting
        for (int i = 0; i < n; i++) {
            // Find pivot
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (V[k][i].abs().compareTo(V[maxRow][i].abs()) > 0) {
                    maxRow = k;
                }
            }
            
            // Swap rows
            BigDecimal[] temp = V[i];
            V[i] = V[maxRow];
            V[maxRow] = temp;
            
            // Make diagonal element 1
            BigDecimal pivot = V[i][i];
            for (int j = i; j <= n; j++) {
                V[i][j] = V[i][j].divide(pivot, MathContext.DECIMAL128);
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
        return a;
    }

    // =================================
    // 4) Least Squares Regression (degree d)
    // =================================
    public static double[] leastSquares(BigDecimal[] x, BigDecimal[] y, int d) {
        int n = x.length;
        
        // Build normal equations: (X^T X) c = X^T y
        double[][] XT_X = new double[d+1][d+1];
        double[] XT_y = new double[d+1];
        
        for (int i = 0; i < n; i++) {
            double xi = x[i].doubleValue();
            double yi = y[i].doubleValue();
            double pow_i = 1;
            
            for (int p = 0; p <= d; p++) {
                double pow_j = 1;
                for (int q = 0; q <= d; q++) {
                    XT_X[p][q] += pow_i * pow_j;
                    pow_j *= xi;
                }
                XT_y[p] += pow_i * yi;
                pow_i *= xi;
            }
        }
        
        // Solve by Gaussian elimination
        for (int i = 0; i <= d; i++) {
            double pivot = XT_X[i][i];
            for (int j = i; j <= d; j++) {
                XT_X[i][j] /= pivot;
            }
            XT_y[i] /= pivot;
            
            for (int k = i + 1; k <= d; k++) {
                double factor = XT_X[k][i];
                for (int j = i; j <= d; j++) {
                    XT_X[k][j] -= factor * XT_X[i][j];
                }
                XT_y[k] -= factor * XT_y[i];
            }
        }
        
        // Back substitution
        double[] c = new double[d+1];
        for (int i = d; i >= 0; i--) {
            double sum = XT_y[i];
            for (int j = i + 1; j <= d; j++) {
                sum -= XT_X[i][j] * c[j];
            }
            c[i] = sum;
        }
        return c;
    }
}
