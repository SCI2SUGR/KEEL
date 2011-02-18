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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import keel.Dataset.Attributes;

import org.core.Randomize;

/**
 * <p>
 * Class for generating the individuals
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @author Modified by Victoria Lopez Morales (University of Granada) 23/05/2010 
 * @version 0.1
 * @since JDK1.5
 */
public class MLPerceptronBackpropCS {

    /**
     * <p>
     * Empty constructor
     * </p>
     */
    public MLPerceptronBackpropCS() {
    }

    /**
     * <p>
     * Main function
     * </p>
     * @param args Arguments to main method
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) {
        if (args.length <= 0) {
            System.err.println("No parameters file");
            System.exit(1);
        }
//System.out.println("");
//System.out.println("------------------>>>>>>>>>>>>>>>>>>>>  1");

        Parameters global = new Parameters();
        global.LoadParameters(args[0]);
//System.out.println("");
//System.out.println("------------------>>>>>>>>>>>>>>>>>>>>  2");
        OpenDataset train = new OpenDataset();
        OpenDataset test = null;
        OpenDataset validation = null;

        boolean isTrain;
        train.processClassifierDataset(global.train_file, true);

        global.n_test_patterns = 0;
        global.n_train_patterns = train.getndatos();

//System.out.println("");
//System.out.println("------------------>>>>>>>>>>>>>>>>>>>>  test_data="+global.test_data);
        if (global.test_data) {
            test = new OpenDataset();
            test.processClassifierDataset(global.test_file, false);
            global.n_test_patterns = test.getndatos();
//System.out.println("");
//System.out.println("------------------>>>>>>>>>>>>>>>>>>>>  n_test_patterns:"+global.n_test_patterns);
        }
        global.n_val_patterns = 0;
//System.out.println("");
//System.out.println("------------------>>>>>>>>>>>>>>>>>>>>  global.val_data="+global.val_data);
        if (global.val_data) {
            validation = new OpenDataset();
            validation.processClassifierDataset(global.val_file, false);
            global.n_val_patterns = validation.getndatos();
//System.out.println("");
//System.out.println("------------------>>>>>>>>>>>>>>>>>>>>  n_val_patterns:"+global.n_val_patterns);
        }

        // Assign data and parameters to internal variables
        // Number of inputs

        global.Ninputs = 0;

//System.out.println("");
//System.out.println("------------------>>>>>>>>>>>>>>>>>>>>  getnentradas:"+train.getnentradas());
        for (int i = 0; i < train.getnentradas(); i++) {
            if (train.getTiposAt(i) == 0) {
                Vector in_values = train.getRangosVar(i);
                global.Ninputs += in_values.size();
            } else {
                global.Ninputs++;
            }
        }
//System.out.println("");
//System.out.println("------------------>>>>>>>>>>>>>>>>>>>>  Ninputs:"+global.Ninputs);

        // Number of outputs
        if (train.getTiposAt(train.getnentradas()) != 0) {
//System.out.println("");
//System.out.println("------------------>>>>>>>>>>>>>>>>>>>>  no es cero");
            global.Noutputs = train.getnsalidas();
        } else {
            Vector out_values = train.getRangosVar(train.getnentradas());

            global.Noutputs = out_values.size();
        }
//System.out.println("");
//System.out.println("------------------>>>>>>>>>>>>>>>>>>>>  Noutputs:"+global.Noutputs);

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
        /* TODO Print of weight matrix
             System.out.println (global.Ninputs +" "+ global.Noutputs);
             //FileWriter writer = new FileWriter("./matriz.txt");
             for (int f=0; f< global.n_train_patterns; f++)
             {
         for (int c=global.Ninputs; c<global.Ninputs+global.Noutputs; c++)
         {
          Double aux = new Double ( data.train[f][c] );
		  System.out.print(aux.toString()+" ");
          //writer.write( aux.toString()+" " );
         }
         System.out.println("");
		 //writer.write("\n");
             }
         //    writer.close();
         /************************************** */

        if (global.tipify_inputs == true) {
            data.TipifyInputData(global);

        }
/*		System.out.println ("------------------" + global.Ninputs +" "+ global.Noutputs);
        System.out.println ("------------------"+global.Ninputs +" "+ global.Noutputs);
        for (int f=0; f< global.n_train_patterns; f++)
             {
             for (int c=global.Ninputs; c<global.Ninputs+global.Noutputs; c++)
				 {
				  Double aux = new Double ( data.train[f][c] );
				  System.out.print(aux.toString()+" ");
				 }
		     System.out.println("");
    		 }
*/

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
/*		System.out.println("------------------ "+global.problem+" ----- "+global.Nhidden_layers+" ----- " +global.transfer[global.Nhidden_layers]);
		System.out.println ("------------------" + global.Ninputs +" "+ global.Noutputs);
        System.out.println ("------------------"+global.Ninputs +" "+ global.Noutputs);
        for (int f=0; f< global.n_train_patterns; f++)
             {
             for (int c=global.Ninputs; c<global.Ninputs+global.Noutputs; c++)
				 {
				  Double aux = new Double ( data.train[f][c] );
				  System.out.print(aux.toString()+" ");
				 }
		     System.out.println("");
    		 }
		global.verbose=true;
		System.out.println("------------global.crossValidation ------------"+global.cross_validation);
*/

        Network neural = new Network(global);

        if (global.verbose) {
            neural.PrintWeights();

        }
        if (global.cross_validation) {
            neural.TrainNetworkWithCrossvalidation(global, data);
        } else {
            neural.TrainNetwork(global, data.train, global.n_train_patterns);

        }
        if (global.save) {
            neural.SaveNetwork("network", false);
        }
        
        int positive_class;
        double positive_cost, negative_cost;
        
        positive_class = positive_class(train);
        positive_cost = positive_cost(train);
        negative_cost = negative_cost(train);
        
        if (global.verbose) {
            neural.PrintWeights();

            double res = neural.TestNetworkInClassification(global, data.train,
                    global.n_train_patterns);
            System.out.println("Final network training accuracy: " +
                               100.0 * res);
            if (global.val_data == true) {
                res = neural.TestNetworkInClassification(global,
                        data.validation,
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
        
        neural.SaveOutputFile(global.train_output, data.train, //data.validation,
                              global.n_train_patterns, global.problem, positive_class, positive_cost, negative_cost); // global.n_val_patterns, global.problem);
        if (global.test_data) {
			neural.SaveOutputFile(global.test_output, data.test,
                                  global.n_test_patterns, global.problem, positive_class, positive_cost, negative_cost);
        }
        if (global.val_data) {
			neural.SaveOutputFile(global.val_output, data.validation,
                                  global.n_val_patterns, global.problem, positive_class, positive_cost, negative_cost);
        }

    }

    /**
     * <p>
     * Transforms the dataset into a double matrix
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
                        if (line.substring(pos1,
                            pos2).compareToIgnoreCase(cats[k]) == 0) {
                            array[i][offset + k] = 1.0;
                        } else {
                            array[i][offset + k] = 0.0;
                        }

                    }

                    offset += values.size();
                } else {
                    try {
                        array[i][offset] = Double.parseDouble(line.substring(
                                pos1, pos2));
                    } catch (java.lang.NumberFormatException
                             NumberFormatException) {
                        array[i][offset] = 0.0;
                    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        System.exit( -1);
                    }
                    offset++;
                }
                pos1 = pos2 + 1;
            }

            // Take the output classes without spaces and convert them to binary outputs.
            pos1 = line.indexOf(",", pos2);
            //pos2 = line.indexOf("]" pos1);

            String category = line.substring(pos1 + 1, line.length());

            if (dataset.getTiposAt(dataset.getnentradas()) != 0) {
                pos1 = 0;
                for (int k = 0; k < dataset.getnsalidas() - 1; k++) {
                    pos2 = category.indexOf(",", pos1);
                    array[i][offset +
                            k] = Double.parseDouble(category.
                            substring(pos1, pos2));
                    pos1 = pos2 + 1;
                }
                try {
                    array[i][offset + dataset.getnsalidas() -
                            1] = Double.parseDouble(category.substring(pos1));
                } catch (java.lang.NumberFormatException e) {
                    e.printStackTrace();
                    System.exit( -1);
                }
            } else {

                Vector out_values = dataset.getRangosVar(dataset.getnentradas());
                String cats[] = new String[out_values.size()];

                for (int k = 0; k < out_values.size(); k++) {
                    cats[k] = out_values.elementAt(k).toString();
                }

                for (int j = 0; j < out_values.size(); j++) {
                    if (category.compareToIgnoreCase(cats[j]) == 0) {
                        array[i][offset + j] = 1.0;
                    } else {
                        array[i][offset + j] = 0.0;
                    }
                }

            }
        }

    }

    /**
     * <p>
     * Generates a random number between min and max
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
     * Generates a random integer number between min and max
     * </p>
     * @param min Min value
     * @param max Max value
     * @return random integer number
     */
    public static int irandom(double min, double max) {
        return (int) Randomize.Randdouble(min, max);
    }
    
    /**
     * <p>
     * Computes the positive_class of the dataset (in an imbalanced classification problem) according to the frequency of patterns
     * </p>
     * 
     * @param	data Dataset used to compute the positive class
     * @return integer that represents the positive class
     */
    private static int positive_class (OpenDataset data) {
    	int n_classes = data.getRangosVar(data.getnentradas()).size();
    	int freqClasses[] = new int[n_classes];
    	double minimum = 0;
        int minIndex = 0;
                
    	// Compute the class frequency
        for (int i = 0; i < data.getndatos(); i++) {
        	freqClasses[data.getClassAt(i)]++;
        }
        
        // Obtain the positive class
        minIndex = 0;
        minimum = freqClasses[0];
        for (int i = 1; i < freqClasses.length; i++) {
            if (freqClasses[i] < minimum) {
                minIndex = i;
                minimum = freqClasses[i];
            }
        }

        return minIndex;   
    }
    
    /**
     * <p>
     * Computes the cost of misclassifying a positive instance in an imbalanced classification problem according to the frequency of patterns
     * </p>
     * 
     * @param	data Dataset used to compute the cost of misclassifying a positive instance
     * @return cost of misclassifying a positive instance
     */
    private static double positive_cost(OpenDataset data) {
    	int n_classes = data.getRangosVar(data.getnentradas()).size();
    	int freqClasses[] = new int[n_classes];
    	double minimum = 0;
        int minIndex = 0;
        int positive_class;
        int freq_pos = 0;
        int freq_neg = 0;
        double positive_cost;
                
    	// Compute the class frequency
        for (int i = 0; i < data.getndatos(); i++) {
        	freqClasses[data.getClassAt(i)]++;
        }
        
        // Obtain the positive class
        minIndex = 0;
        minimum = freqClasses[0];
        for (int i = 1; i < freqClasses.length; i++) {
            if (freqClasses[i] < minimum) {
                minIndex = i;
                minimum = freqClasses[i];
            }
        }

        positive_class = minIndex;
        
        // Compute the N+ and N-
        for (int i = 0; i < freqClasses.length; i++) {
            if (i == positive_class) {
                freq_pos += freqClasses[i];
            }
            else {
            	freq_neg += freqClasses[i];
            }
        }
        positive_cost = (double)freq_neg/(double)freq_pos;
        
        return positive_cost;
    }
    
    /**
     * <p>
     * Computes the cost of misclassifying a negative instance in an imbalanced classification problem according to the frequency of patterns
     * </p>
     * 
     * @param	data Dataset used to compute the cost of misclassifying a negative instance
     * @return cost of misclassifying a negative instance
     */    
    private static double negative_cost(OpenDataset data) {
    	return 1.0;
    }

}

