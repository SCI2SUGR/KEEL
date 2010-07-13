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

package keel.Algorithms.Neural_Networks.gann;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

/**
 * <p>
 * Implementation of the Genetic Algorithm Neural Networks
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */

public class Gann {

  /**
   * <p>
   * Empty (default) constructor
   * </p>
   */
  public Gann() {
  }
  
  /**
   * <p>
   * Main method
   * </p>
   * @param args Parameters file
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void main(String[] args) throws FileNotFoundException,
      IOException {
    if (args.length <= 0) {
      System.err.println("No parameters file");
      System.exit(1);
    }

    SetupParameters global = new SetupParameters();
    global.LoadParameters(args[0]);

    OpenDataset train = new OpenDataset();
    train.processClassifierDataset(global.train_file,true);
    OpenDataset test = null;
    OpenDataset validation = null;
    
    global.n_train_patterns = train.getndatos();
    global.n_test_patterns = 0;
    if (global.test_data) {
      test = new OpenDataset();
      test.processClassifierDataset(global.test_file,false);
      global.n_test_patterns = test.getndatos();
    }
    global.n_val_patterns = 0;
    if (global.val_data) {
      validation = new OpenDataset();
      validation.processClassifierDataset(global.val_file,false);
      global.n_val_patterns = validation.getndatos();
    }
   
    
    // Assign data and parameters to internal variables
    // Number of inputs
    global.Ninputs = 0;
    for (int i = 0; i < train.getnentradas(); i++) {
      if (train.getTiposAt(i) == 0) {
        Vector in_values = train.getRangosVar(i);
        global.Ninputs += in_values.size();
      }
      else {
        global.Ninputs++;
      }
    }

    // Number of outputs
    if (train.getTiposAt(train.getnentradas())!= 0) {
      global.Noutputs = train.getnsalidas();
    }
    else {
      Vector out_values = train.getRangosVar(train.getnentradas());

      global.Noutputs = out_values.size();
    }

    global.n_train_patterns = train.getndatos();
    Data data = new Data(global.Ninputs + global.Noutputs,
                         global.n_train_patterns,
                         global.n_test_patterns, global.n_val_patterns);
    global.Nhidden[global.Nhidden_layers] = global.Noutputs;

    Genesis.DatasetToArray(data.train, train);
    if (global.test_data) {
    	Genesis.DatasetToArray(data.test, test);
    }
    if (global.val_data) {
    	Genesis.DatasetToArray(data.validation, validation);

    }

    if (global.tipify_inputs == true) {
      double mean, sigma, sq_sum; /* Tipify input data. */

      /* Scale input. */
      for (int i = 0; i < global.Ninputs; i++) {
        /* Get the mean and variance. */
        mean = sigma = sq_sum = 0.;

        for (int j = 0; j < global.n_train_patterns; j++) {
          mean += data.train[j][i];
          sq_sum += data.train[j][i] * data.train[j][i];
        }

        mean /= global.n_train_patterns;
        sigma = Math.sqrt(sq_sum / global.n_train_patterns - mean * mean);

        /* Tipify: z = (x - mean)/std. dev. */
        /* If std. dev. is 0 do nothing. */
        if (sigma > 0.000001) {
          for (int j = 0; j < global.n_train_patterns; j++) {
            data.train[j][i] = (data.train[j][i] - mean) / sigma;
          }

          for (int j = 0; j < global.n_test_patterns; j++) {
            data.test[j][i] = (data.test[j][i] - mean) / sigma;
          }

        }
      }

    }

    if (global.problem.compareToIgnoreCase("Classification") == 0) {
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

    Population pop = new Population(global);

    Individual best = new Individual (10);

    best = pop.EvolvePopulation (global, data);

    ConnNetwork neural = new ConnNetwork (global);

    best.PhenotypeToNetwork(neural);



     if (global.save) {
       neural.SaveNetwork("network", false);

     }

     // neural.PrintWeights();

     double res = neural.TestNetworkInClassification(global, data.train,
                                                     global.n_train_patterns);
     System.out.println("Final network training accuracy: " + 100.0 * res);

     if (global.val_data == true) {
       res = neural.TestNetworkInClassification(global, data.validation,
                                                global.n_val_patterns);
       System.out.println("Final network validation accuracy: " +
                          100.0 * res);
     }
     if (global.test_data == true) {
       res = neural.TestNetworkInClassification(global, data.test,
                                                global.n_test_patterns);
       System.out.println("Final network test accuracy: " +
                          100.0 * res);

     }

     neural.SaveOutputFile(global.train_output, data.train,
                           global.n_train_patterns, global.problem);

     if (global.test_data) {
       neural.SaveOutputFile(global.test_output, data.test,
                             global.n_test_patterns, global.problem);
     }

  }

}

