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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import org.core.Randomize;

/**
 * <p>
 * Class Genesis
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
   */
  public Genesis() {
  }

  /**
   * <p>
   * Main function
   * </p>
   * @param args to the main method
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void main(String[] args) {
    if (args.length <= 0) {
      System.err.println("No parameters file");
      System.exit(1);
    }

    SetupParameters global = new SetupParameters();
    global.LoadParameters(args[0]);
    
    OpenDataset train = new OpenDataset();
    OpenDataset test = null;
    OpenDataset validation = null;
    
    boolean isTrain;
    train.processClassifierDataset(global.train_file, true);
    
    global.n_test_patterns = 0;
    global.n_train_patterns = train.getndatos();
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
      }
      else {
        global.Ninputs++;
      }
    }
    
    // Number of outputs
    if (train.getTiposAt(train.getnentradas()) != 0) {
      global.Noutputs = train.getnsalidas();
    }
    else {
      Vector out_values = train.getRangosVar(train.getnentradas());

      global.Noutputs = out_values.size();
    }
    
    Data data = new Data(global.Ninputs + global.Noutputs,
                         global.n_train_patterns,
                         global.n_test_patterns, global.n_val_patterns);
    global.Nhidden[global.Nhidden_layers] = global.Noutputs;

    DatasetToArray(data.train, train);
    if (global.test_data) {
      DatasetToArray(data.test, test);
    }
    if (global.val_data) {
      DatasetToArray(data.validation, validation);

    }
       
    if (global.tipify_inputs == true) {
      data.TipifyInputData(global);

    }
    if (global.transfer[global.Nhidden_layers].compareToIgnoreCase("Htan") == 0 &&
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

      if (global.transfer[global.Nhidden_layers].compareToIgnoreCase("Log") == 0) {
        lbound = 0.0;
      }
      else {
        lbound = -1.0;
      }
      data.ScaleOutputData(global, lbound, ubound);
    }

    Network neural = new Network(global);

    if (global.verbose) {
      neural.PrintWeights();

    }
    if (global.cross_validation) {
      neural.TrainNetworkWithCrossvalidation(global, data);
    }
    else {
      neural.TrainNetwork(global, data.train, global.n_train_patterns);

    }
    if (global.save) {
      neural.SaveNetwork("network", false);

    }
    if (global.verbose) {
      neural.PrintWeights();

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
    }

    neural.SaveOutputFile(global.train_output, data.train,
                          global.n_train_patterns, global.problem);
    if (global.test_data == true) {
      neural.SaveOutputFile(global.test_output, data.test,
                            global.n_test_patterns, global.problem);
    }

  }

  /**
   * <p>
   * Transform dataset into a double matrix
   * </p>
   * @param array Output matrix
   * @param dataset Input dataset
   */
  public static void DatasetToArray(double array[][], OpenDataset dataset) {

    String line;
    int pos1, pos2 = 0, group;
    // For all the patterns
    for (int i = 0; i < dataset.getndatos(); i++) {
      line = dataset.getDatosAt(i);
      pos1 = 1;
      int offset = 0;
      for (int j = 0; j < dataset.getnentradas(); j++) {
        pos2 = line.indexOf(",", pos1);

        if (dataset.getTiposAt(j) == 0) {
          Vector values = dataset.getRangosVar(j);
          String cats[] = new String[values.size()];

          for (int k = 0; k < values.size(); k++) {
            cats[k] = values.elementAt(k).toString();
          }

          for (int k = 0; k < values.size(); k++) {
            if (line.substring(pos1, pos2).compareToIgnoreCase(cats[k]) == 0) {
              array[i][offset + k] = 1.0;
            }
            else {
              array[i][offset + k] = 0.0;
            }

          }

          offset += values.size();
        }
        else {
          try {
            array[i][offset] = Double.parseDouble(line.substring(pos1, pos2));
          }
          catch (java.lang.NumberFormatException NumberFormatException) {
            array[i][offset] = 0.0;
          }
          catch (java.lang.ArrayIndexOutOfBoundsException e) {
          	e.printStackTrace();
          	System.exit(-1);
          }
          offset++;
        }
        pos1 = pos2 + 1;
      }
      
      // Take the output classes without spaces and convert them to binary outputs. 
      pos1 = line.indexOf(",", pos2);
      //pos2 = line.indexOf("]" pos1);
      
      String category = line.substring(pos1+1,line.length());
      
      if (dataset.getTiposAt(dataset.getnentradas()) != 0) {
        pos1 = 0;
        for (int k = 0; k < dataset.getnsalidas()-1; k++) {
          pos2 = category.indexOf(",", pos1);
          array[i][offset+k] = Double.parseDouble(category.substring(pos1,pos2));
          pos1 = pos2+1;
        }
        try {
        array[i][offset+dataset.getnsalidas()-1] = Double.parseDouble(category.substring(pos1));
        }
        catch(java.lang.NumberFormatException e) {
        	e.printStackTrace();
        	System.exit(-1);
        }
      }
      else {

        Vector out_values = dataset.getRangosVar(dataset.getnentradas());
        String cats[] = new String[out_values.size()];

        for (int k = 0; k < out_values.size(); k++) {
          cats[k] = out_values.elementAt(k).toString();
        }

        for (int j = 0; j < out_values.size(); j++) {
          if (category.compareToIgnoreCase(cats[j]) == 0) {
            array[i][offset + j] = 1.0;
          }
          else {
            array[i][offset + j] = 0.0;
          }
        }

      }
    }

  }
  
  /**
   * <p>
   * Generate random number between min and max 
   * </p>
   * @param min Min value
   * @param max Max value
   * @return random float number
   */
  public static double frandom( double min, double max) {
	  return Randomize.Randdouble(min, max);
  }

  /**
   * <p>
   * Generate random integer number between min and max 
   * </p>
   * @param min Min value
   * @param max Max value
   * @return random integer number
   */
  public static int irandom( double min, double max) {
    return (int) Randomize.Randdouble(min, max);
  }

}

