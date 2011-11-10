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

package keel.Algorithms.Neural_Networks.ensemble;

import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import org.core.Randomize;

import keel.Algorithms.Neural_Networks.net.Data;
import keel.Algorithms.Neural_Networks.net.OpenDataset;

/**
 * <p>
 * Class that creates individuals
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */
public class Genesis {

    /**
     * <p>
     * Empty constructor
     * </p>
     * 
     */
    public Genesis() {
    }

    /**
     * <p>
     * Main function
     * </p>
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) {

        if (args.length <= 0) {
            System.err.println("No parameters file");
            System.exit(1);
        }

        EnsembleParameters global = new EnsembleParameters();
        global.LoadParameters(args[0]);

        OpenDataset train = new OpenDataset();
        train.processClassifierDataset(global.train_file, true);
        OpenDataset test = null;
        OpenDataset validation = null;

        global.n_train_patterns = train.getndatos();
        global.n_test_patterns = 0;
        if (global.test_data) {
            test = new OpenDataset();
            test.processClassifierDataset(global.test_file, false);
            global.n_test_patterns = test.getndatos();
        }
        global.n_val_patterns = 0;
        if (global.val_data) {
            validation = new OpenDataset();
            validation.processClassifierDataset(global.val_file, false);
            global.n_val_patterns = validation.getndatos();
        }

        // Assign data and parameters to internal variables
        // Number of inputs
        global.Ninputs = 0;
        for (int i = 0; i < train.getnentradas(); i++) {
            if (train.getTiposAt(i) == 0) {
                Vector in_values = train.getRangosVar(i);
                global.Ninputs += in_values.size();
            } else {
                global.Ninputs++;
            }
        }

        // Number of outputs
        if (train.getnsalidas() != 1 ||
            global.problem.compareToIgnoreCase("Regression") == 0) {
            global.Noutputs = train.getnsalidas();
        } else {
            Vector out_values = train.getRangeOutput(); //train.getRangosVar(train.getnentradas());

            global.Noutputs = out_values.size();

        }

        global.n_train_patterns = train.getndatos();
        Data data = new Data(global.Ninputs + global.Noutputs,
                             global.n_train_patterns,
                             global.n_test_patterns, global.n_val_patterns);
        global.Nhidden[global.Nhidden_layers] = global.Noutputs;

        keel.Algorithms.Neural_Networks.net.Genesis.DatasetToArray(data.train,
                train);
        if (global.test_data) {
            keel.Algorithms.Neural_Networks.net.Genesis.DatasetToArray(data.
                    test, test);
        }
        if (global.val_data) {
            keel.Algorithms.Neural_Networks.net.Genesis.DatasetToArray(data.
                    validation, validation);
        }
        if (global.tipify_inputs == true) {
            data.TipifyInputData(global);

        }
        /* TODO Print of the weight matrix
             System.out.println (global.Ninputs +" "+ global.Noutputs);
             FileWriter writer = new FileWriter("./matriz.txt");
             for (int f=0; f< global.n_train_patterns; f++)
             {
         for (int c=global.Ninputs; c<global.Ninputs+global.Noutputs; c++)
         {
          Double aux = new Double ( data.train[f][c] );
          writer.write( aux.toString()+" " );
         }
         writer.write("\n");
             }
             writer.close();
         ************************************** */

        if (global.transfer[global.Nhidden_layers].compareToIgnoreCase("Htan") ==
            0 &&
            global.problem.compareToIgnoreCase("Classification") == 0) {
            for (int i = 0; i < global.n_train_patterns; i++) {
                for (int j = 0; j < global.Noutputs; j++) {
                    if (data.train[i][j + global.Ninputs] == 0) {
                        data.train[i][j + global.Ninputs] = -1.0;

                    }
                }
            }
            if (global.test_data) {
                for (int i = 0; i < global.n_test_patterns; i++) {
                    for (int j = 0; j < global.Noutputs; j++) {
                        if (data.test[i][j + global.Ninputs] == 0) {
                            data.test[i][j + global.Ninputs] = -1.0;

                        }
                    }
                }
            }
            if (global.val_data) {
                for (int i = 0; i < global.n_val_patterns; i++) {
                    for (int j = 0; j < global.Noutputs; j++) {
                        if (data.validation[i][j + global.Ninputs] == 0) {
                            data.validation[i][j + global.Ninputs] = -1.0;

                        }
                    }
                }
            }

        }
        if (global.problem.compareToIgnoreCase("Regression") == 0) {
            // Scale outputs
            double ubound = 1.0, lbound;

            if (global.transfer[global.Nhidden_layers].compareToIgnoreCase(
                    "Log") == 0) {
                lbound = 0.0;
            } else {
                lbound = -1.0;
            }
            data.ScaleOutputData(global, lbound, ubound);
        }

        Ensemble ensemble = new Ensemble(global);

        ensemble.TrainEnsemble(global, data);

        if (global.sampling.compareToIgnoreCase("Ada") == 0) {
            ensemble.GetAdaWeights();
        } else if (global.ensemble_method.compareToIgnoreCase("GEM") == 0) {
            // Generalized ensemble method
            if (global.val_data == true) {
                ensemble.GetGEMWeights(global, data.validation,
                                       global.n_val_patterns);
            } else {
                ensemble.GetGEMWeights(global, data.train,
                                       global.n_train_patterns);
            }
        }

        if (global.save) {
            ensemble.SaveEnsemble("ensemble");
        }

        if (global.verbose) {
            double res = ensemble.TestEnsembleInClassification(global,
                    data.train,
                    global.n_train_patterns);
            System.out.println("Final ensemble training accuracy: " +
                               100.0 * res);
            if (global.val_data == true) {
                res = ensemble.TestEnsembleInClassification(global,
                        data.validation,
                        global.n_val_patterns);
                System.out.println("Final ensemble validation accuracy: " +
                                   100.0 * res);
            }
            if (global.test_data == true) {
                res = ensemble.TestEnsembleInClassification(global, data.test,
                        global.n_test_patterns);
                System.out.println("Final ensemble test accuracy: " +
                                   100.0 * res);
            }
        }
        ensemble.SaveOutputFile(global.train_output, data.validation,
                                global.n_val_patterns, global.problem, data.a, data.b);
        ensemble.SaveOutputFile(global.test_output, data.test,
                                global.n_test_patterns, global.problem, data.a, data.b);
        /*
         ensemble.SaveOutputFile(global.train_output, data.train,
                                global.n_train_patterns, global.problem);
                 if (global.test_data) {
            ensemble.SaveOutputFile(global.test_output, data.test,
                                    global.n_test_patterns, global.problem);

                 }
         */

    }

    /**
     * <p>
     * Generate random double between min and max
     * </p>
     * @param min Min value
     * @param max Max value
     * @return random float number
     */
    public static double frandom(double min, double max) {
        return Randomize.Randdouble(min, max);
    }

    /**
     * <p>
     * Generate random int between min and max using r seed
     * </p>
     * @param min Min value
     * @param max Max value
     * @return random integer number
     */
    public static int irandom(double min, double max) {
        return (int) Randomize.Randdouble(min, max);
    }


}

