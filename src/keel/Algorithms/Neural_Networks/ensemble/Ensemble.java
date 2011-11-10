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

import keel.Algorithms.Neural_Networks.net.*;
import keel.Dataset.Attributes;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;


/**
 * <p>
 * Class representing an ensemble
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */
public class Ensemble {

    EnsembleNetwork nets[];
    public double weights[][], betta[], cache[][][][];
    int Nnetworks, Ninputs, Noutputs;
    private final double TH_COS = 0.99619;
    public final int TRAIN = 0, TEST = 1, VAL = 2;

    /**
     * <p>
     * Constructor
     * </p>
     * @param global Global definition parameters
     */
    public Ensemble(EnsembleParameters global) {

        Nnetworks = global.n_networks;
        Ninputs = global.Ninputs;
        Noutputs = global.Noutputs;
        nets = new EnsembleNetwork[Nnetworks];
        weights = new double[Noutputs][Nnetworks];
        betta = new double[Nnetworks];
        for (int i = 0; i < Nnetworks; i++) {
            nets[i] = new EnsembleNetwork(global);

        }
        for (int i = 0; i < Noutputs; i++) {
            for (int j = 0; j < Nnetworks; j++) {
                weights[i][j] = 1.0 / Nnetworks;

            }
        }
    }

    /**
     * <p>
     * Train Ensemble
     * </p>
     * @param global Global definition parameters
     * @param data  Input data
     */
    public void TrainEnsemble(EnsembleParameters global, Data data) {

        // Test type of sampling
        if (global.sampling.compareToIgnoreCase("None") == 0) {
            TrainEnsembleNoSampling(global, data);
        } else if (global.sampling.compareToIgnoreCase("Bagging") == 0) {
            TrainEnsembleBagging(global, data);
        } else if (global.sampling.compareToIgnoreCase("Arcing") == 0) {
            TrainEnsembleArcing(global, data);
        } else if (global.sampling.compareToIgnoreCase("Ada") == 0) {
            TrainEnsembleAda(global, data);
        } else {
            System.err.println("Invalid sampling method");
            System.exit(1);
        }
    }

    /**
     * <p>
     * Train ensemble without sampling
     * </p>
     * @param global Global definition parameters
     * @param data Input data
     */
    public void TrainEnsembleNoSampling(EnsembleParameters global, Data data) {

        for (int i = 0; i < Nnetworks; i++) {
            // Train each network
            if (global.cross_validation) {
                nets[i].TrainNetworkWithCrossvalidation(global, data);
            } else {
                nets[i].TrainNetwork(global, data.train,
                                     global.n_train_patterns);
            }
        }
    }

    /**
     * <p>
     * Train ensemble using Bagging
     * </p>
     * @param global Global definition parameters
     * @param data Input data
     */
    public void TrainEnsembleBagging(EnsembleParameters global, Data data) {

        for (int i = 0; i < Nnetworks; i++) {
            // Get bagging sample
            nets[i].sample.GetBaggingSample();

            // Train each network
            if (global.cross_validation) {
                nets[i].TrainNetworkWithCrossvalidation(global, data);
            } else {
                nets[i].TrainNetwork(global, data.train,
                                     global.n_train_patterns);
            }
        }

    }

    /**
     * <p>
     * Train ensemble using Arcing
     * </p>
     * @param global Global definition parameters
     * @param data Input data.
     */
    public void TrainEnsembleArcing(EnsembleParameters global, Data data) {

        // Get initial equal sample
        nets[0].sample.GetEqualSample();

        // Train networks
        for (int i = 0; i < Nnetworks; i++) {
            if (global.cross_validation) {
                nets[i].TrainNetworkWithCrossvalidation(global, data);
            } else {
                nets[i].TrainNetwork(global, data.train,
                                     global.n_train_patterns);

                // Get new sampling
            }
            nets[i +
                    1].sample.GetArcingSample(this, data.train,
                                              global.n_train_patterns,
                                              i + 1);

        }

    }

    /**
     * <p>
     * Train ensemble using Ada
     * </p>
     * @param global Global definition parameters
     * @param data Input data
     */
    public void TrainEnsembleAda(EnsembleParameters global, Data data) {

        // Get initial equal sample
        nets[0].sample.GetEqualSample();

        // Train networks
        for (int i = 0; i < Nnetworks; i++) {
            if (global.cross_validation) {
                nets[i].TrainNetworkWithCrossvalidation(global, data);
            } else {
                nets[i].TrainNetwork(global, data.train,
                                     global.n_train_patterns);

                // Get new sampling
            }
            nets[i +
                    1].sample.GetAdaSample(this, data.train,
                                           global.n_train_patterns,
                                           i);

        }
    }

    /**
     * <p>
     * Output of every network
     * </p>
     * @param inputs Input data
     * @param outputs Output data
     */
    public void EnsembleOutput(double inputs[], double outputs[]) {

        double out[] = new double[Noutputs];
        for (int i = 0; i < Noutputs; i++) {
            outputs[i] = 0.0;

            // Output of every network
        }
        for (int i = 0; i < Nnetworks; i++) {
            nets[i].GenerateOutput(inputs, out);
            for (int j = 0; j < Noutputs; j++) {
                outputs[j] += weights[j][i] * out[j];
            }
        }

    }

    /**
     * <p>
     * Test ensemble in classification
     * </p>
     * @param global Global definition parameters
     * @param data Input data
     * @param npatterns No of patterns
     * @return Correct classified per cent
     */
    public double TestEnsembleInClassification(EnsembleParameters global,
                                               double data[][],
                                               int npatterns) {
        double ok = 0.0, fitness;
        int max_index = 0;

        double[] outputs = new double[Noutputs];

        for (int i = 0; i < npatterns; i++) {
            if (global.combination.compareToIgnoreCase("WeightedSum") == 0) {
                // Obtain network output
                EnsembleOutput(data[i], outputs);

                max_index = 0;
                for (int j = 1; j < global.Noutputs; j++) {
                    if (outputs[max_index] < outputs[j]) {
                        max_index = j;
                    }
                }
            } else {
                double[] votes = new double[Noutputs];

                for (int j = 0; j < Nnetworks; j++) {
                    votes[nets[j].NetGetClassOfPattern(data[i])] += weights[0][
                            j];
                }

                // Get majority of votes
                max_index = 0;
                for (int j = 0; j < Noutputs; j++) {
                    if (votes[j] > votes[max_index]) {
                        max_index = j;

                    }
                }
            }

            // Obtain class
            int Class = 0;
            for (int j = 1; j < Noutputs; j++) {
                if (data[i][Class + Ninputs] < data[i][j + Ninputs]) {
                    Class = j;
                }
            }
            // Test if correctly classified
            if (Class == max_index) {
                ok++;

            }
        }

        fitness = ok / npatterns;

        return fitness;

    }

    /**
     * <p>
     * Test ensemble in regression
     * </p>
     * @param global Global definition parameters
     * @param data Input data
     * @param npatterns No of patterns
     * @return Test ensemble in regression
     */
    public double TestEnsembleInRegression(EnsembleParameters global,
                                           double data[][],
                                           int npatterns) {
        double fitness, RMS = 0.0, error;

        double[] outputs = new double[Noutputs];

        for (int i = 0; i < npatterns; i++) {
            // Obtain network output
            EnsembleOutput(data[i], outputs);

            // Obtain RMS error
            error = 0.0;
            for (int j = 0; j < Noutputs; j++) {
                error += Math.pow(outputs[j] - data[i][Ninputs + j], 2.0);

            }
            RMS += Math.sqrt(error);

        }

        fitness = RMS / (npatterns * global.Noutputs);

        return fitness;
    }

    /**
     * <p>
     * Save ensemble at file_name
     * </p>
     * @param file_name File name
     */
    public void SaveEnsemble(String file_name) {
        String name;

        // Save networks
        for (int i = 0; i < Nnetworks; i++) {
            name = file_name + "_net_" + i;
            nets[i].SaveNetwork(name, false);
        }

        // Save weigths
        try {
            FileOutputStream file = new FileOutputStream(file_name, false);
            DataOutputStream dataOut = new DataOutputStream(file);

            // Save weights
            for (int i = 0; i < Noutputs; i++) {
                for (int j = 0; j < Nnetworks; j++) {
                    dataOut.writeDouble(weights[i][j]);
                }
            }

            dataOut.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Unable to open ensemble files");
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("IO exception");
            System.exit(1);
        }

    }

    /**
     * <p>
     * Load ensemble from file_name
     * </p>
     * @param file_name File name
     */
    public void LoadEnsemble(String file_name) {
        String name;

        // Load networks
        for (int i = 0; i < Nnetworks; i++) {
            name = file_name + "_net_" + i;
            nets[i].LoadNetwork(name);
        }

        // Load weigths
        try {
            FileInputStream file = new FileInputStream(file_name);
            DataInputStream dataIn = new DataInputStream(file);

            // Load weights
            for (int i = 0; i < Noutputs; i++) {
                for (int j = 0; j < Nnetworks; j++) {
                    weights[i][j] = dataIn.readDouble();
                }
            }

            dataIn.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Unable to open ensemble files");
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("IO exception");
            System.exit(1);
        }

    }

    /**
     * <p>
     * Calculate weights using GEM method
     * </p>
     * @param global Global definition parameters
     * @param data Input data
     * @param n matrix order (no of rows and colms in data matrix)
     */
    public void GetGEMWeights(EnsembleParameters global, double data[][], int n) {
        int xx, yy, offset, cols;
        double scalar;

        double[] module = new double[Nnetworks];
        boolean[] collinear = new boolean[Nnetworks];
        double[][] omega = new double[Nnetworks][Nnetworks];
        double[][] inverse = new double[Nnetworks][Nnetworks];
        double[][] toinvert = new double[Nnetworks][Nnetworks];
        double[] output_i = new double[Noutputs];
        double[] output_j = new double[Noutputs];

        // Weight for every output.
        for (int out = 0; out < Noutputs; out++) {
            // Obtain omega matrix.
            for (int i = 0; i < Nnetworks; i++) {
                for (int j = 0; j <= i; j++) {
                    omega[i][j] = 0.0;
                    for (int k = 0; k < n; k++) {
                        nets[i].GenerateOutput(data[k], output_i);
                        nets[j].GenerateOutput(data[k], output_j);
                        omega[i][j] += (data[k][Ninputs + out] - output_i[out]) *
                                (data[k][Ninputs + out] - output_j[out]);
                    }
                    omega[j][i] = (omega[i][j] /= n);
                }
            }

            // Search collinearity.
            for (int i = 0; i < Nnetworks; i++) {
                module[i] = 0.0;
                for (int k = 0; k < Nnetworks; k++) {
                    module[i] += omega[i][k] * omega[i][k];
                }
                module[i] = Math.sqrt(module[i]);
                collinear[i] = false;
            }

            for (int i = 0; i < Nnetworks - 1; i++) {
                for (int j = i + 1; j < Nnetworks && !collinear[i]; j++) {
                    scalar = 0.0;
                    for (int k = 0; k < Nnetworks; k++) {
                        scalar += omega[i][k] * omega[j][k];

                    }
                    if (scalar / (module[i] * module[j]) > TH_COS) {
                        collinear[i] = true;
                    }
                }
            }

            // Create non-singular matrixx.
            for (int i = xx = 0; i < Nnetworks; i++) {
                if (!collinear[i]) {
                    // Copyy row.
                    for (int j = yy = 0; j < Nnetworks; j++) {
                        if (!collinear[j]) {
                            toinvert[xx][yy] = omega[i][j];
                            yy++;
                        }
                    }
                    xx++;
                }
            }

            cols = xx;

            //Invert omega matrix.
            Matrix.InvertMatrix(toinvert, inverse, cols);

            // Obtain sum of matrix elements (1 Omega-1 1).
            double sum = 0.0;
            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < cols; j++) {
                    sum += inverse[i][j];

                    // Obtain weights.
                }
            }
            for (int i = offset = 0; i < Nnetworks; i++) {
                weights[out][i] = 0.0;
                if (!collinear[i]) {
                    for (int j = 0; j < cols; j++) {
                        weights[out][i] += inverse[i - offset][j];
                    }
                    weights[out][i] /= sum;
                } else {
                    offset++;
                }
            }
        }
    }

    /**
     * <p>
     * Calculate weights using Ada method
     * </p>
     */
    public void GetAdaWeights() {

        for (int i = 0; i < Noutputs; i++) {
            for (int j = 0; j < Nnetworks; j++) {
                weights[i][j] = Math.log(1.0 / betta[j]);
            }
        }
    }

    /**
     * <p>
     * Save data in output file
     * </p>
     * @param file_name File name
     * @param data Data to be saved
     * @param n No of patterns
     * @param problem Type of problem (CLASSIFICATION | REGRESSION)
     * @throws IOException
     */
    public void SaveOutputFile(String file_name, double data[][], int n,
                               String problem, double[] a, double[] b) {
        String line;
        double outputs[] = new double[Noutputs];

        try {
            // Result file
            FileOutputStream file = new FileOutputStream(file_name);
            BufferedWriter f = new BufferedWriter(new OutputStreamWriter(file));

            // File header
            f.write("@relation " + Attributes.getRelationName() + "\n");
            f.write(Attributes.getInputAttributesHeader());
            f.write(Attributes.getOutputAttributesHeader());
            f.write(Attributes.getInputHeader() + "\n");
            f.write(Attributes.getOutputHeader() + "\n");
            f.write("@data\n");

            // For all patterns
            for (int i = 0; i < n; i++) {

                // Classification
                if (problem.compareToIgnoreCase("Classification") == 0) {
                    // Obtain class
                    int Class = 0;
                    for (int j = 1; j < Noutputs; j++) {
                        if (data[i][Class + Ninputs] < data[i][j + Ninputs]) {
                            Class = j;

                        }
                    }
                    /*
                     f.write(Integer.toString(Class) + " ");
                     f.write(Integer.toString(EnsembleGetClassOfPattern(data[i])));
                                         f.newLine();
                     */
                    f.write(Attributes.getOutputAttributes()[0].getNominalValue(
                            Class) + " ");
                    f.write(Attributes.getOutputAttributes()[0].getNominalValue(
                            EnsembleGetClassOfPattern(data[i])));
                    f.newLine();
                    f.flush();

                }
                // Regression
                else {
                    if(a!=null && b!=null){
                        for (int j = 0; j < Noutputs; j++) {
                            f.write(Double.toString((data[i][Ninputs + j] - b[j])/ a[j]) + " ");
                        }
                        EnsembleOutput(data[i], outputs);
                        for (int j = 0; j < Noutputs; j++) {
                            f.write(Double.toString((outputs[j] - b[j])/a[j]) + " ");
                        }
                        f.newLine();
                    }
                    else{
                        for (int j = 0; j < Noutputs; j++) {
                            f.write(Double.toString(data[i][Ninputs + j]) + " ");
                        }
                        EnsembleOutput(data[i], outputs);
                        for (int j = 0; j < Noutputs; j++) {
                            f.write(Double.toString(outputs[j]) + " ");
                        }
                        f.newLine();
                    }
                }
            }
            f.close();
            file.close();
        } catch (FileNotFoundException e) {
            System.err.println("Training file does not exist");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit( -1);
        }

    }

    /**
     * <p>
     * Return output class of pattern
     * </p>
     * @param pattern Pattern
     * @return Pattern class
     */
    private int EnsembleGetClassOfPattern(double pattern[]) {
        double outputs[] = new double[Noutputs];

        // Obtain output
        EnsembleOutput(pattern, outputs);

        // Classify pattern
        int max_index = 0;
        for (int j = 1; j < Noutputs; j++) {
            if (outputs[max_index] < outputs[j]) {
                max_index = j;

            }
        }
        return max_index;
    }

    /* TODO this function will be removed
       public double EvaluateWeights(double w[], EnsembleParameters global, double data[][],
                                  int npatterns) {

      int k = 0;
      for (int i = 0; i < Noutputs; i++) {
        for (int j = 0; j < Nnetworks; j++) {
          weights[i][j] = w[k];
          k++;
        }
      }

      if (global.problem.compareToIgnoreCase("Classification") == 0) {
        return (TestEnsembleInClassification(global, data, npatterns));
      }
      else {
        return (TestEnsembleInRegression(global, data, npatterns));
      }

       }
     */
    /**
     * <p>
     * Update cache data
     * </p>
     * @param global Global definition parameters
     * @param data Input data
     */
    public void UpdateCache(EnsembleParameters global, Data data) {

        cache = new double[3][Nnetworks][global.n_train_patterns][Noutputs];

        double out[] = new double[Noutputs];

        // Output of every network for TRAIN patterns
        for (int p = 0; p < global.n_train_patterns; p++) {
            for (int i = 0; i < Nnetworks; i++) {
                nets[i].GenerateOutput(data.train[p], out);
                for (int j = 0; j < Noutputs; j++) {
                    cache[TRAIN][i][p][j] = out[j];
                }
            }
        }

        // Output of every network for TEST patterns
        if (global.test_data) {
            for (int p = 0; p < global.n_test_patterns; p++) {
                for (int i = 0; i < Nnetworks; i++) {
                    nets[i].GenerateOutput(data.test[p], out);
                    for (int j = 0; j < Noutputs; j++) {
                        cache[TEST][i][p][j] = out[j];
                    }
                }
            }
        }

        // Output of every network for TEST patterns
        if (global.val_data) {
            for (int p = 0; p < global.n_val_patterns; p++) {
                for (int i = 0; i < Nnetworks; i++) {
                    nets[i].GenerateOutput(data.validation[p], out);
                    for (int j = 0; j < Noutputs; j++) {
                        cache[VAL][i][p][j] = out[j];
                    }
                }
            }
        }

    }

    /* TODO This function will be removed
     public double EvaluateWeightsWithCache(double w[], EnsembleParameters global,
                                           double[][] data, int npatterns,
                                           int data_file) {

      double[] outputs = new double[global.Noutputs];
      double ok = 0.0, fitness;
      int max_index = 0;

      for (int p = 0; p < npatterns; p++) {
        if (global.combination.compareToIgnoreCase("WeightedSum") == 0) {
          // Use cached output
          for (int j = 0; j < global.Noutputs; j++) {
            outputs[j] = 0.0;

          }
          int k = 0;
          for (int i = 0; i < Noutputs; i++) {
            for (int j = 0; j < Nnetworks; j++) {
              outputs[i] += w[k] * cache[data_file][j][p][i];
              k++;
            }
          }

          max_index = 0;
          for (int j = 1; j < global.Noutputs; j++) {
            if (outputs[max_index] < outputs[j]) {
              max_index = j;

            }
          }
        }
        else {
          double[] votes = new double[Noutputs];
          int max_of_net;

          for (int j = 0; j < Nnetworks; j++) {
            max_of_net = 0;
            for (int k = 1; k < Noutputs; k++) {
     if (cache[data_file][j][p][k] > cache[data_file][j][p][max_of_net])
                max_of_net = k;
            }
            votes[max_of_net] += weights[0][j];
          }

          // Get majority of votes
          max_index = 0;
          for (int j = 0; j < Noutputs; j++) {
            if (votes[j] > votes[max_index]) {
              max_index = j;

            }
          }
        }

        // Obtain class
        int Class = 0;
        for (int j = 1; j < Noutputs; j++) {
          if (data[p][Class + Ninputs] < data[p][j + Ninputs]) {
            Class = j;

          }
        }
        // Test if correctly classified
        if (Class == max_index) {
          ok++;

        }
      }

      fitness = ok / npatterns;

      return fitness;
       }
     */
}

