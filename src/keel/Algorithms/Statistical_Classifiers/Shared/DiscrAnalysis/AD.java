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

/** Linear and Cuadratic discriminant analysis,
 with a normal distribution of the examples for each class
 **/

package keel.Algorithms.Statistical_Classifiers.Shared.DiscrAnalysis;

import keel.Algorithms.Statistical_Classifiers.Shared.MatrixCalcs.*;
import keel.Algorithms.Statistical_Classifiers.Shared.*;

import java.io.*;
import java.util.Vector;


public class AD {
    double COVAR[][][];
    double MEDIA[][][];
    double ejemplos[][];
    double deseado[][];
    int nentradas;
    int nsalidas;
    int nelem;
    int nejemplos[];


    public AD(double[][] vejemplos, double[][] vdeseado) {
        ejemplos = vejemplos;
        deseado = vdeseado;
        nentradas = ejemplos[0].length;
        nsalidas = deseado[0].length;
        nelem = ejemplos.length;
        COVAR = new double[nsalidas][nentradas][nentradas];
        MEDIA = new double[nsalidas][nentradas][1];
        nejemplos = new int[nsalidas];
        for (int i = 0; i < nelem; i++) {
            for (int s = 0; s < nsalidas; s++) {
                if (deseado[i][s] != 0) {
                    nejemplos[s]++;
                }
            }
        }
    }

    public void computeParameter(boolean lineal) throws ErrorDimension, ErrorSingular {

        // If 'lineal' is true, Every covariance matrix are equal
        for (int i = 0; i < nelem; i++) {
            for (int s = 0; s < nsalidas; s++) {
                if (deseado[i][s] != 0) {
                    MEDIA[s] = MatrixCalcs.matsum(MEDIA[s], MatrixCalcs.columna(ejemplos[i]));
                }
            }
        }
        for (int s = 0; s < nsalidas; s++) {
            MEDIA[s] = MatrixCalcs.matmul(MEDIA[s], 1.0f / nejemplos[s]);
        }

        double tmp[][];
        if (lineal) {
            // Every calculus are made over COVAR[0]
            for (int i = 0; i < nelem; i++) {
                for (int s = 0; s < nsalidas; s++) {
                    if (deseado[i][s] != 0) {
                        tmp = MatrixCalcs.matsum(
                                MatrixCalcs.columna(ejemplos[i]),
                                MatrixCalcs.matmul(MEDIA[s], -1.0f));
                        tmp = MatrixCalcs.matmul(tmp, MatrixCalcs.tr(tmp));
                        COVAR[0] = MatrixCalcs.matsum(COVAR[0], tmp);
                    }
                }
            }
            // Covariance matrix is inverted
            COVAR[0] = MatrixCalcs.matmul(COVAR[0], 1.0f / nelem);
            COVAR[0] = MatrixCalcs.inv(COVAR[0]);

            // Results are copied to every  matrix
            for (int s = 1; s < nsalidas; s++) {
                for (int i = 0; i < COVAR[s].length; i++) {
                    for (int j = 0; j < COVAR[s][i].length; j++) {
                        COVAR[s][i][j] = COVAR[0][i][j];
                    }
                }
            }

        } else {

            // Covariance matrix are estimated separately
            for (int i = 0; i < nelem; i++) {
                for (int s = 0; s < nsalidas; s++) {
                    if (deseado[i][s] != 0) {
                        tmp = MatrixCalcs.matsum(
                                MatrixCalcs.columna(ejemplos[i]),
                                MatrixCalcs.matmul(MEDIA[s], -1.0f));
                        tmp = MatrixCalcs.matmul(tmp, MatrixCalcs.tr(tmp));
                        COVAR[s] = MatrixCalcs.matsum(COVAR[s], tmp);
                    }
                }
            }

            // Covariace matrixes are inverted
            for (int s = 0; s < nsalidas; s++) {
                COVAR[s] = MatrixCalcs.matmul(COVAR[s], 1.0f / nejemplos[s]);
                COVAR[s] = MatrixCalcs.inv(COVAR[s]);
            }
        }

    }

    public String AString(double[] s) {
        String result = "[";
        for (int i = 0; i < s.length; i++) {
            result = result + s[i] + " ";
        }
        return result + "]";
    }

    public String AString(double[][] s) {
        String result = "[";
        for (int i = 0; i < s.length; i++) {
            result = result + AString(s[i]) + " ";
        }
        return result + "]";
    }


    public double[] distances(double[] x) throws ErrorDimension, ErrorSingular {
        // Distance from each example to each prototype is calculated
        double d[] = new double[nsalidas];
        double g[][];

        double[][] cx = MatrixCalcs.columna(x);

        for (int s = 0; s < nsalidas; s++) {

            // Cuadratic term
            g = MatrixCalcs.matmul(
                    MatrixCalcs.tr(cx),
                    MatrixCalcs.matmul(COVAR[s], cx));
            g = MatrixCalcs.matmul(g, -0.5f);

            // Linear term
            double[][] w = MatrixCalcs.tr(
                    MatrixCalcs.matmul(COVAR[s], MEDIA[s]));
            g = MatrixCalcs.matsum(g, MatrixCalcs.matmul(w, cx));

            // Constant term
            double[][] C1 = MatrixCalcs.matmul(
                    MatrixCalcs.tr(MEDIA[s]),
                    MatrixCalcs.matmul(COVAR[s], MEDIA[s]));
            C1 = MatrixCalcs.matmul(C1, -0.5f);

            double C2 = 0.5f * (double) Math.log(
                    MatrixCalcs.determinante(COVAR[s]));

            double C3 = (double) Math.log(nejemplos[s] / (double) nelem);

            d[s] = g[0][0] + C1[0][0] + C2 + C3;
        }
        return d;
    }

    public void AlmacenaParametros(double[] pesos) {
        int p = 0;
        for (int i = 0; i < COVAR.length; i++) {
            for (int j = 0; j < COVAR[i].length; j++) {
                for (int k = 0; k < COVAR[i][j].length; k++) {
                    pesos[p++] = COVAR[i][j][k];
                }
            }
        }
        for (int i = 0; i < MEDIA.length; i++) {
            for (int j = 0; j < MEDIA[i].length; j++) {
                for (int k = 0; k < MEDIA[i][j].length; k++) {
                    pesos[p++] = MEDIA[i][j][k];
                }
            }
        }
        for (int i = 0; i < nejemplos.length; i++) {
            pesos[p++] = nejemplos[i];
        }
    }

    public void RecuperaParametros(double[] pesos) {
        int p = 0;
        for (int i = 0; i < COVAR.length; i++) {
            for (int j = 0; j < COVAR[i].length; j++) {
                for (int k = 0; k < COVAR[i][j].length; k++) {
                    COVAR[i][j][k] = pesos[p++];
                }
            }
        }
        for (int i = 0; i < MEDIA.length; i++) {
            for (int j = 0; j < MEDIA[i].length; j++) {
                for (int k = 0; k < MEDIA[i][j].length; k++) {
                    MEDIA[i][j][k] = pesos[p++];
                }
            }
        }
        for (int i = 0; i < nejemplos.length; i++) {
            nejemplos[i] = (int) pesos[p++];
        }
    }

    public static int argmax(double[] x) {
        double max = x[0];
        int imax = 0;
        for (int i = 1; i < x.length; i++) {
            if (x[i] > max) {
                max = x[i];
                imax = i;
            }
        }
        return imax;
    }


}

