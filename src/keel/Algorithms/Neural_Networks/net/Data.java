/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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

package keel.Algorithms.Neural_Networks.net;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * <p>
 * Class representing the data
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */
public class Data {

    /** Training data */
    public double train[][];

    /** Validation data */
    public double validation[][];

    /** Testing data */
    public double test[][];

    /** Scaling parameters */
    public double [] a = null;
    public double [] b = null;

    /**
     * <p>
     * Constructor
     * </p>
     * @param n_variables Number of variables
     * @param n_train Number of train variables
     * @param n_test Number of test variables
     * @param n_val Number of validation variables
     */
    public Data(int n_variables, int n_train, int n_test, int n_val) {
        train = new double[n_train][n_variables];
        if (n_test != 0) {
            test = new double[n_test][n_variables];
        }

        if (n_val != 0) {
            validation = new double[n_val][n_variables];
        }
    }

    /**
     * <p>
     * Constructor that receives the parameters (NOT USED)
     * </p>
     * @param global Global Definition parameters
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Data(Parameters global) throws FileNotFoundException, IOException {
        String line;
        int pos1, pos2;

        try {
            // Training data
            FileInputStream file = new FileInputStream(global.train_file);
            BufferedReader f = new BufferedReader(new InputStreamReader(file));

            // Number of patterns
            line = f.readLine();
            global.n_train_patterns = Integer.parseInt(line);

            // Number of inputs
            line = f.readLine();
            global.Ninputs = Integer.parseInt(line);

            // Number of outputs
            line = f.readLine();
            global.Noutputs = Integer.parseInt(line);

            // Read data
            train = new double[global.n_train_patterns][global.Ninputs +
                    global.Noutputs];

            for (int i = 0; i < global.n_train_patterns; i++) {
                line = f.readLine();
                pos1 = 0;
                for (int j = 0; j < global.Ninputs + global.Noutputs - 1; j++) {
                    pos2 = line.indexOf(" ", pos1);
                    train[i][j] = Double.parseDouble(line.substring(pos1, pos2));
                    pos1 = pos2 + 1;
                }
                train[i][global.Ninputs + global.Noutputs -
                        1] = Double.parseDouble(line.substring(pos1));
            }

            file.close();
        } catch (FileNotFoundException e) {
            System.err.println("Training file does not exist");
            System.exit( -1);
        }

        if (global.test_data) {
            try {
                // Training data
                FileInputStream file = new FileInputStream(global.test_file);
                BufferedReader f = new BufferedReader(new InputStreamReader(
                        file));

                // Number of patterns
                line = f.readLine();
                global.n_test_patterns = Integer.parseInt(line);

                // Number of inputs
                line = f.readLine();
                global.Ninputs = Integer.parseInt(line);

                // Number of outputs
                line = f.readLine();
                global.Noutputs = Integer.parseInt(line);

                // Read data
                test = new double[global.n_test_patterns][global.Ninputs +
                       global.Noutputs];

                for (int i = 0; i < global.n_test_patterns; i++) {
                    line = f.readLine();
                    pos1 = 0;
                    for (int j = 0; j < global.Ninputs + global.Noutputs - 1; j++) {
                        pos2 = line.indexOf(" ", pos1);
                        test[i][j] = Double.parseDouble(line.substring(pos1,
                                pos2));
                        pos1 = pos2 + 1;
                    }
                    test[i][global.Ninputs + global.Noutputs -
                            1] = Double.parseDouble(line.substring(pos1));
                }

                file.close();
            } catch (FileNotFoundException f) {
                System.err.println("Testing file does not exist");
                System.exit( -1);
            }
        }

        if (global.val_data) {
            try {
                // Training data
                FileInputStream file = new FileInputStream(global.val_file);
                BufferedReader f = new BufferedReader(new InputStreamReader(
                        file));

                // Number of patterns
                line = f.readLine();
                global.n_val_patterns = Integer.parseInt(line);

                // Number of inputs
                line = f.readLine();
                global.Ninputs = Integer.parseInt(line);

                // Number of outputs
                line = f.readLine();
                global.Noutputs = Integer.parseInt(line);
                global.Nhidden[global.Nhidden_layers] = global.Noutputs;

                // Read data
                validation = new double[global.n_val_patterns][global.Ninputs +
                             global.Noutputs];

                for (int i = 0; i < global.n_val_patterns; i++) {
                    line = f.readLine();
                    pos1 = 0;
                    for (int j = 0; j < global.Ninputs + global.Noutputs - 1; j++) {
                        pos2 = line.indexOf(" ", pos1);
                        validation[i][j] = Double.parseDouble(line.substring(
                                pos1, pos2));
                        pos1 = pos2 + 1;
                    }
                    validation[i][global.Ninputs + global.Noutputs -
                            1] = Double.parseDouble(line.substring(pos1));
                }

                file.close();
            } catch (FileNotFoundException e) {
                System.err.println("Validation file does not exist");
                System.exit( -1);
            }
        }

    }

    /**
     * <p>
     * Tipify all data inputs
     * </p>
     * @param global Global Definition parameters
     */
    public void TipifyInputData(Parameters global) {
        double mean, sigma, sq_sum; /* Tipify inut data. */

        /* Scale input. */
        for (int i = 0; i < global.Ninputs; i++) {
            /* Get the mean and variance. */
            mean = sigma = sq_sum = 0.;

            for (int j = 0; j < global.n_train_patterns; j++) {
                mean += train[j][i];
                sq_sum += train[j][i] * train[j][i];
            }

            mean /= global.n_train_patterns;
            sigma = Math.sqrt(sq_sum / global.n_train_patterns - mean * mean);

            /* Tipify: z = (x - mean)/std. dev. */
            /* If std. dev. is 0 do nothing. */
            if (sigma > 0.000001) {
                for (int j = 0; j < global.n_train_patterns; j++) {
                    train[j][i] = (train[j][i] - mean) / sigma;
                }
                if (global.test_data == true) {
                    for (int j = 0; j < global.n_test_patterns; j++) {
                        test[j][i] = (test[j][i] - mean) / sigma;
                    }
                }
                if (global.val_data == true) {
                    for (int j = 0; j < global.n_val_patterns; j++) {
                        validation[j][i] = (validation[j][i] - mean) / sigma;
                    }
                }
            }
        }

    }

    /**
     * <p>
     * Tipify all data outputs
     * </p>
     * @param global Global Definition parameters
     * @param lbound lower bound
     * @param ubound upper bound
     */
    public void ScaleOutputData(Parameters global, double lbound, double ubound) {
        double max_val, min_val;
        /* Make the linear transformation x' = ax
         * + b, where a=(MAX -
         * MIN)/(max_val-min_val)
         * b=(MIN\u00B7max_val-MAX\u00B7min_val)/(max_val-min_val).
         */

        a = new double[global.Noutputs];
        b = new double[global.Noutputs];

        // Scale output.
        for (int i = global.Ninputs; i < global.Ninputs + global.Noutputs; i++) {
            int ii = i-global.Ninputs;
            /*
             * Get the max and min values of the column.
             */
            min_val = max_val = train[0][i];
            for (int j = 0; j < global.n_train_patterns; j++) {
                if (max_val < train[j][i]) {
                    max_val = train[j][i];
                } else {
                    if (min_val > train[j][i]) {
                        min_val = train[j][i];
                    }
                }
            }

            // Calculate a and b coefficients.
            // If constant do nothing.
            if (max_val != min_val) {
                a[ii] = (ubound - lbound) / (max_val - min_val);
                b[ii] = (lbound * max_val - ubound * min_val) / (max_val - min_val);

                /*
                 * Scale column.
                 */
                for (int j = 0; j < global.n_train_patterns; j++) {
                    train[j][i] = a[ii] * train[j][i] + b[ii];
                }
                if (global.test_data) {
                    for (int j = 0; j < global.n_test_patterns; j++) {
                        test[j][i] = a[ii] * test[j][i] + b[ii];
                    }
                }
                if (global.val_data) {
                    for (int j = 0; j < global.n_val_patterns; j++) {
                        validation[j][i] = a[ii] * validation[j][i] + b[ii];
                    }
                }
            }
        }
    }

}

