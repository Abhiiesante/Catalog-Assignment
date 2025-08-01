import java.io.*;
import java.math.*;
import java.util.*;
import java.util.regex.*;

/**
 * Enhanced Shamir's Secret Sharing Solver
 *
 * This version logs which points fail validation when reconstructing the
 * polynomial,
 * so you can pinpoint inconsistent inputs (outliers) in a test case.
 */
public class ShamirSecretSolver {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Shamir's Secret Sharing Solver ===");
        System.out.println("Using Vandermonde Matrix Method with Validation Logging\n");
        // Process both test cases
        processTestCase("testcase1.json");
        processTestCase("testcase2.json");
    }

    public static void processTestCase(String filename) throws Exception {
        System.out.println("Processing: " + filename);
        TestCaseData data = parseJsonFile(filename);
        System.out.println("n (total points): " + data.n);
        System.out.println("k (minimum required): " + data.k + "\n");
        System.out.println("Decoded points (x, y):");
        for (Point p : data.points) {
            System.out.println("(" + p.x + ", " + p.y + ")");
        }

        BigInteger[] secretCoeffs = null;
        List<Point> pts = data.points;
        int n = data.n, k = data.k;
        int[] idx = new int[k];
        for (int i = 0; i < k; i++)
            idx[i] = i;

        Map<Point, Integer> mismatchCounts = new HashMap<>();

        outer: while (true) {
            // subset arrays
            BigInteger[] x = new BigInteger[k];
            BigInteger[] y = new BigInteger[k];
            for (int i = 0; i < k; i++) {
                x[i] = pts.get(idx[i]).x;
                y[i] = pts.get(idx[i]).y;
            }
            // Solve Vandermonde for this subset
            BigInteger[] coeffs = solveVandermonde(x, y);
            // Validate all points & log mismatches
            List<Point> mismatches = validateAndLog(coeffs, pts);
            if (mismatches.isEmpty()) {
                secretCoeffs = coeffs;
                break outer;
            } else {
                for (Point m : mismatches) {
                    mismatchCounts.put(m, mismatchCounts.getOrDefault(m, 0) + 1);
                }
            }

            // next combination
            int pos = k - 1;
            while (pos >= 0 && idx[pos] == n - k + pos)
                pos--;
            if (pos < 0)
                break;
            idx[pos]++;
            for (int j = pos + 1; j < k; j++)
                idx[j] = idx[j - 1] + 1;
        }

        if (secretCoeffs == null) {
            System.err.println("No valid polynomial found for " + filename + "!\n");
            if (!mismatchCounts.isEmpty()) {
                System.out.println("=== POTENTIAL INCORRECT POINTS ===");
                mismatchCounts.entrySet().stream()
                        .sorted((a, b) -> b.getValue() - a.getValue())
                        .forEach(e -> System.out
                                .println(e.getKey().x + ":" + e.getKey().y + " failed " + e.getValue() + " times"));
                System.out.println("===================================\n");
            }
        } else {
            System.out.println("\n*** SECRET FOUND: " + secretCoeffs[0] + " ***");
            System.out.println("Full polynomial coefficients (a0 ... a(k-1)): " + Arrays.toString(secretCoeffs));
            System.out.println("=".repeat(60) + "\n");
        }
    }

    /**
     * Vandermonde solver
     */
    public static BigInteger[] solveVandermonde(BigInteger[] x, BigInteger[] y) {
        int m = x.length;
        BigDecimal[][] V = new BigDecimal[m][m + 1];
        for (int i = 0; i < m; i++) {
            BigDecimal pow = BigDecimal.ONE;
            for (int j = 0; j < m; j++) {
                V[i][j] = pow;
                pow = pow.multiply(new BigDecimal(x[i]));
            }
            V[i][m] = new BigDecimal(y[i]);
        }
        for (int i = 0; i < m; i++) {
            int maxR = i;
            for (int r = i + 1; r < m; r++) {
                if (V[r][i].abs().compareTo(V[maxR][i].abs()) > 0)
                    maxR = r;
            }
            BigDecimal[] tmp = V[i];
            V[i] = V[maxR];
            V[maxR] = tmp;
            BigDecimal piv = V[i][i];
            if (piv.compareTo(BigDecimal.ZERO) == 0)
                throw new RuntimeException("Singular matrix");
            for (int c = i; c <= m; c++)
                V[i][c] = V[i][c].divide(piv, MathContext.DECIMAL128);
            for (int r = i + 1; r < m; r++) {
                BigDecimal factor = V[r][i];
                for (int c = i; c <= m; c++)
                    V[r][c] = V[r][c].subtract(factor.multiply(V[i][c]));
            }
        }
        BigInteger[] a = new BigInteger[m];
        for (int i = m - 1; i >= 0; i--) {
            BigDecimal sum = V[i][m];
            for (int j = i + 1; j < m; j++)
                sum = sum.subtract(V[i][j].multiply(new BigDecimal(a[j])));
            a[i] = sum.toBigInteger();
        }
        return a;
    }

    /**
     * Validate polynomial against all points and log mismatches.
     */
    private static List<Point> validateAndLog(BigInteger[] coeffs, List<Point> pts) {
        List<Point> mismatches = new ArrayList<>();
        for (Point p : pts) {
            BigInteger xi = p.x, acc = BigInteger.ZERO, pow = BigInteger.ONE;
            for (BigInteger c : coeffs) {
                acc = acc.add(c.multiply(pow));
                pow = pow.multiply(xi);
            }
            if (!acc.equals(p.y)) {
                System.out.println("Mismatch: x=" + xi + " expected=" + p.y + " got=" + acc);
                mismatches.add(p);
            }
        }
        return mismatches;
    }

    /**
     * Parse JSON file manually with regex
     */
    public static TestCaseData parseJsonFile(String filename) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String l;
            while ((l = br.readLine()) != null)
                sb.append(l);
        }
        String json = sb.toString();
        Pattern np = Pattern.compile("\\\"n\\\"\\s*:\\s*(\\d+)");
        Pattern kp = Pattern.compile("\\\"k\\\"\\s*:\\s*(\\d+)");
        Matcher nm = np.matcher(json), km = kp.matcher(json);
        int nn = 0, kk = 0;
        if (nm.find())
            nn = Integer.parseInt(nm.group(1));
        if (km.find())
            kk = Integer.parseInt(km.group(1));
        List<Point> list = new ArrayList<>();
        Pattern pp = Pattern.compile(
                "\\\"(\\\\d+)\\\"\\s*:\\s*\\{[^}]*\\\"base\\\"\\s*:\\s*\\\"(\\\\d+)\\\"[^}]*\\\"value\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"[^}]*\\}");
        Matcher pm = pp.matcher(json);
        while (pm.find()) {
            BigInteger xi = new BigInteger(pm.group(1));
            BigInteger yi = new BigInteger(pm.group(3), Integer.parseInt(pm.group(2)));
            list.add(new Point(xi, yi));
        }
        return new TestCaseData(nn, kk, list);
    }

    static class Point {
        BigInteger x, y;

        Point(BigInteger a, BigInteger b) {
            x = a;
            y = b;
        }
    }

    static class TestCaseData {
        int n, k;
        List<Point> points;

        TestCaseData(int n, int k, List<Point> p) {
            this.n = n;
            this.k = k;
            this.points = p;
        }
    }
}
