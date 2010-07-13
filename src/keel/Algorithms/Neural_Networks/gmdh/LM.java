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

package keel.Algorithms.Neural_Networks.gmdh;

/**
 * <p>
 * Class representing the Levenberg Marquard method
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */
public class LM {
  /**
   * <p>
   * Empty constructor
   * </p>
   */
  public LM() {
  }

  // Levenberg - Marquardt method
  // Return true in case of singular matrix

  /**
   * <p>
   * Levenberg - Marquardt method
   * </p>
   * @param x Input data
   * @param y Expected output
   * @param sig Consideration of each parameter
   * @param ndata Number of training patterns
   * @param a Auxiliar variable in each iteration
   * @param ia Auxiliar variable in each iteration
   * @param ma Auxiliar variable in each iteration
   * @param covar Auxiliar variable in each iteration
   * @param alpha Auxiliar variable in each iteration
   * @param chisq Current error
   * @param alamda Auxiliar variable in each iteration
   * @param mfit Auxiliar variable in each iteration
   * @param ochisq Previous error
   * @param atry Auxiliar variable in each iteration
   * @param beta Auxiliar variable in each iteration
   * @param da Auxiliar variable in each iteration
   * @param oneda Auxiliar variable in each iteration
   * @param global Global parameters of the algorithm
   */
  public static boolean mrqmin(
      double x[][],
      double y[],
      double sig[],
      int ndata,
      double a[],
      int ia[],
      int ma,
      double covar[][],
      double alpha[][],
      double chisq[],
      double alamda[],
      int mfit,
      double ochisq[],
      double atry[],
      double beta[],
      double da[],
      double oneda[][],
      SetupParameters global) {

    int j, k, l;

    if (alamda[0] < 0.0) {
      alamda[0] = 0.001;
      mrqcof(x, y, sig, ndata, a, ia, ma, alpha, beta, chisq);
      ochisq[0] = chisq[0];
      for (j = 0; j < ma; j++) {
        atry[j] = a[j];
      }
    }
    for (j = 0; j < mfit; j++) {
      for (k = 0; k < mfit; k++) {
        covar[j][k] = alpha[j][k];
      }
      covar[j][j] = alpha[j][j] * (1.0 + alamda[0]);
      oneda[j][0] = beta[j];
    }
    if (!gaussj(covar, mfit, oneda, 1)) {
      System.out.println("Singular matrix");
      alamda[0] = -1.0;
      ochisq[0] = chisq[0] = 1.0e20;

      // New random values for a
      for (int i = 0; i < ma; i++) {
        if (ia[i] == 1) {
          a[i] = Genesis.frandom(-global.aRange, global.aRange);
        }
      }
      return true;
    }

    for (j = 0; j < mfit; j++) {
      da[j] = oneda[j][0];
    }
    if (alamda[0] == 0.0) {
      covsrt(covar, ma, ia, mfit);

      covsrt(alpha, ma, ia, mfit);
      return false;
    }
    for (j = -1, l = 0; l < ma; l++) {
      if (ia[l] != 0) {
        atry[l] = a[l] + da[++j];

      }
    }

    mrqcof(x, y, sig, ndata, atry, ia, ma, covar, da, chisq);

    if (chisq[0] < ochisq[0]) {
      alamda[0] *= 0.1;
      ochisq[0] = chisq[0];
      for (j = 0; j < mfit; j++) {
        for (k = 0; k < mfit; k++) {
          alpha[j][k] = covar[j][k];
        }
        beta[j] = da[j];
      }
      for (l = 0; l < ma; l++) {
        a[l] = atry[l];
      }
    }
    else {
      alamda[0] *= 10.0;
      chisq[0] = ochisq[0];
    }

    return false;
  }

  /**
   * <p>
   * Used by mrqmin to evaluate the linearized fitting matrix alpha,
   * and vector beta as in (15.5 .8), and calculates chi2.
   * </p>
   * @param x Input data
   * @param y Expected output
   * @param sig Consideration of each parameter
   * @param ndata Number of training patterns
   * @param a Auxiliar variable in each iteration
   * @param ia Auxiliar variable in each iteration
   * @param ma Auxiliar variable in each iteration
   * @param alpha Auxiliar variable in each iteration
   * @param beta Auxiliar variable in each iteration
   * @param chisq Current error
   */
  private static void mrqcof(
      double x[][],
      double y[],
      double sig[],
      int ndata,
      double a[],
      int ia[],
      int ma,
      double alpha[][],
      double beta[],
      double chisq[])
  {
    int i, j, k, l, m, mfit = 0;
    double ymod[], wt, sig2i, dy, dyda[];

    dyda = new double[ma];
    ymod = new double[1];
    for (j = 0; j < ma; j++) {
      if (ia[j] != 0) {
        mfit++;
      }
    }
    for (j = 0; j < mfit; j++) {
      for (k = 0; k <= j; k++) {
        alpha[j][k] = 0.0;
      }
      beta[j] = 0.0;
    }
    chisq[0] = 0.0;
    for (i = 0; i < ndata; i++) {
      Polynomial(x[i], a, ymod, dyda, ma);

      sig2i = 1.0 / (sig[i] * sig[i]);
      dy = y[i] - ymod[0];
      for (j = -1, l = 0; l < ma; l++) {
        if (ia[l] != 0) {
          wt = dyda[l] * sig2i;
          for (j++, k = -1, m = 0; m <= l; m++) {
            if (ia[m] != 0) {
              alpha[j][++k] += wt * dyda[m];
            }
          }
          beta[j] += dy * wt;
        }
      }
      chisq[0] += dy * dy * sig2i;
    }
    for (j = 1; j < mfit; j++) {
      for (k = 0; k < j; k++) {
        alpha[k][j] = alpha[j][k];
      }
    }
  }

  /**
   * <p>
   * Polynomial method
   * </p>
   * @param inputs Current inputs
   * @param a Current value of the parameters
   * @param y Expected outputs
   * @param dev Deviation of the parameters
   * @param terms Number of terms
   */
  private static void Polynomial(double inputs[], double a[], double y[],
                                 double dev[],
                                 int terms) {

    y[0] = a[0] + a[1] * inputs[0] + a[2] * inputs[1] +
        a[3] * inputs[0] * inputs[1] +
        a[4] * inputs[0] * inputs[0] + a[5] * inputs[1] * inputs[1];

    // Differential of y with regard to a[i]
    dev[0] = 1.0;
    dev[1] = inputs[0];
    dev[2] = inputs[1];
    dev[3] = inputs[0] * inputs[1];
    dev[4] = inputs[0] * inputs[0];
    dev[5] = inputs[1] * inputs[1];
  }

  /**
   * <p>
   * Gauss-Jordan method for solution of linear equations
   * </p>
   * @param a Left hand of the equation system
   * @param n Number of elements of the matrix
   * @param b Right hand of the equation system
   * @param m Number of elements of the matrix
   * @return
   */
  private static boolean gaussj(
      double a[][],
      int n,
      double b[][],
      int m) {
    int indxc[], indxr[], ipiv[];
    int i, icol = 0, irow = 0, j, k, l, ll;
    double big, dum, pivinv, temp;

    indxc = new int[n];
    indxr = new int[n];
    ipiv = new int[n];

    for (j = 0; j < n; j++) {
      ipiv[j] = 0;
    }
    for (i = 0; i < n; i++) {
      big = 0.0;

      for (j = 0; j < n; j++) {
        if (ipiv[j] != 1) {
          for (k = 0; k < n; k++) {
            if (ipiv[k] == 0) {
              if (Math.abs(a[j][k]) >= big) {
                big = Math.abs(a[j][k]);
                irow = j;
                icol = k;
              }
            }
          }
        }
      }
      ++ (ipiv[icol]);
      if (irow != icol) {
        for (l = 0; l < n; l++) {
          temp = a[irow][l];
          a[irow][l] = a[icol][l];
          a[icol][l] = temp;
        }
        for (l = 0; l < m; l++) {
          temp = a[irow][l];
          a[irow][l] = a[icol][l];
          a[icol][l] = temp;
        }
      }
      indxr[i] = irow;
      indxc[i] = icol;
      if (a[icol][icol] == 0.0) {
        return false;
      }
      pivinv = 1.0 / a[icol][icol];
      a[icol][icol] = 1.0;
      for (l = 0; l < n; l++) {
        a[icol][l] *= pivinv;
      }
      for (l = 0; l < m; l++) {
        b[icol][l] *= pivinv;
      }
      for (ll = 0; ll < n; ll++) {
        if (ll != icol) {
          dum = a[ll][icol];
          a[ll][icol] = 0.0;
          for (l = 0; l < n; l++) {
            a[ll][l] -= a[icol][l] * dum;
          }
          for (l = 0; l < m; l++) {
            b[ll][l] -= b[icol][l] * dum;
          }
        }
      }
    }
    for (l = n - 1; l >= 0; l--) {
      if (indxr[l] != indxc[l]) {
        for (k = 0; k < n; k++) {
          temp = a[k][indxr[l]];
          a[k][indxr[l]] = a[k][indxc[l]];
          a[k][indxc[l]] = temp;
        }
      }
    }

    return true;
  }

  /**
   * <p>
   * Obtain covariance
   * </p>
   * @param covar Current covariance matrix
   * @param ma Number of parameters to optimize
   * @param ia Current ia vector
   * @param mfit Number of parameters fit
   */
  private static void covsrt(double covar[][], int ma, int ia[], int mfit) {
    int i, j, k;
    double temp;

    for (i = mfit; i < ma; i++) {
      for (j = 0; j <= i; j++) {
        covar[i][j] = covar[j][i] = 0;

      }
    }
    k = mfit-1;
    for (j = ma - 1; j >= 0; j--) {
      if (ia[j] != 0) {
        for (i = 0; i < ma; i++) {
          temp = covar[i][k];
          covar[i][k] = covar[i][j];
          covar[i][j] = temp;
        }
        for (i = 0; i < ma; i++) {
          temp = covar[k][i];
          covar[k][i] = covar[j][i];
          covar[j][i] = temp;
        }
        k--;

      }
    }

  }

}

