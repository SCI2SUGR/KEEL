/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

package keel.Algorithms.ImbalancedClassification.CSMethods.MLPerceptronBackpropCS;

/**
 * <p>
 * Class with matrix operations
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */
public class Matrix {

    /**
     * <p>
     * Inversion method
     * </p>
     * @param matrix Input matrix
     * @param inverted Inverted output matrix
     * @param n Matrix order (Number of rows and columns)
     * @return integer error code (1 if everything is OK and 0 otherwise)
     */
    public static int InvertMatrix(double matrix[][], double inverted[][],
                                   int n) {

        int i, j;
        double temp;

        // Decompose matrix into L and U triangular matrices
        double[] scales = new double[n];
        double[][] lu = new double[n][n];
        int[] ps = new int[n];
        if (Matrix.lu_decompose(matrix, n, scales, lu, ps) == 0) {
            return 0;
        }

        // Invert matrix by solving n simultaneous equations n times
        double[] b = new double[n];
        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                b[j] = 0.0;
            }
            b[i] = 1.0;
            Matrix.lu_solve(inverted[i], b, n, lu, ps); // Into a row of Ainv: fix later
        }

        // Transpose matrix
        for (i = 0; i < n; i++) {
            for (j = 0; j < i; j++) {
                temp = inverted[i][j];
                inverted[i][j] = inverted[j][i];
                inverted[j][i] = temp;
            }
        }

        return 1;

    }

    /**
     * <p>
     * Lu solution of a matrix
     * </p>
     * @param x vector of inputs
     * @param b vector of coefficients
     * @param n number of elements
     * @param lu matrix with lu decomposition
     * @param ps indexes
     */
    public static void lu_solve(double x[], double b[], int n, double lu[][],
                                int ps[]) {
        int i, j;
        double dot;

        // Vector reduction using U triangular matrix
        for (i = 0; i < n; i++) {
            dot = 0.0;
            for (j = 0; j < i; j++) {
                dot += lu[ps[i]][j] * x[j];
            }
            x[i] = b[ps[i]] - dot;
        }

        /* Back substitution, in L triangular matrix */
        for (i = n - 1; i >= 0; i--) {
            dot = 0.0;
            for (j = i + 1; j < n; j++) {
                dot += lu[ps[i]][j] * x[j];
            }
            x[i] = (x[i] - dot) / lu[ps[i]][i];
        }

    }

    /**
     * <p>
     * Lu Descomposition of a matrix
     * </p>
     * @param a matrix of coefficients
     * @param n number of elements
     * @param scales vector with scales of the elements
     * @param lu matrix with the lu decomposition
     * @param ps indexes
     * @return integer error code (1 if everything is OK and 0 otherwise)
     */
    public static int lu_decompose(double a[][], int n, double scales[],
                                   double lu[][], int ps[]) {
        int i, j, k;
        int pivotindex = 0;
        double pivot, biggest, mult, tempf;

        for (i = 0; i < n; i++) { // For each row
            // Find the largest element in each row for row equilibration
            biggest = 0.0;
            for (j = 0; j < n; j++) {
                if (biggest < (tempf = Math.abs(lu[i][j] = a[i][j]))) {
                    biggest = tempf;
                }
            }
            if (biggest != 0.0) {
                scales[i] = 1.0 / biggest;
            } else {
                scales[i] = 0.0;
                return 0; // Zero row: singular matrix
            }
            ps[i] = i; // Initialize pivot sequence
        }

        for (k = 0; k < n - 1; k++) { // For each column
            // Find the largest element in each column to pivot around
            biggest = 0.0;
            for (i = k; i < n; i++) {
                if (biggest < (tempf = Math.abs(lu[ps[i]][k]) * scales[ps[i]])) {
                    biggest = tempf;
                    pivotindex = i;
                }
            }
            if (biggest == 0.0) {
                return 0; // Zero column: singular matrix
            }
            if (pivotindex != k) { // Update pivot sequence
                j = ps[k];
                ps[k] = ps[pivotindex];
                ps[pivotindex] = j;
            }

            // Pivot, eliminating an extra variable  each time
            pivot = lu[ps[k]][k];
            for (i = k + 1; i < n; i++) {
                lu[ps[i]][k] = mult = lu[ps[i]][k] / pivot;
                if (mult != 0.0) {
                    for (j = k + 1; j < n; j++) {
                        lu[ps[i]][j] -= mult * lu[ps[k]][j];
                    }
                }
            }
        }

        if (lu[ps[n - 1]][n - 1] == 0.0) {
            return 0; // Singular matrix
        }
        return 1;

    }

}
