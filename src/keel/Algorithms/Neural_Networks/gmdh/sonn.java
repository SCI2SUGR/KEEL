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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import keel.Dataset.Attributes;

/**
 * <p>
 * Class for the algorithm sonn
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */

public class sonn {

  double T;
  node nodes[];
  int n_nodes;
  int output; // Output node. It is the node with the smallest SEC

  /**
   * <p>
   * Constructor
   * </p>
   * @param global Global parameters
   * @param data Input data
   */
  public sonn(SetupParameters global, Data data) {
    double old_sec, new_sec;
    nodes = new node[global.max_nodes];

    // Initialize T and S
    n_nodes = 0;
    T = global.To;

    // Generate input nodes
    for (int i = 0; i < global.Ninputs; i++) {
      NewBasicNode();

    }
    do {
      int new_nodes = 0;

      do {
        old_sec = StateEnergy(global);

        // Sj = generate (Si)
        NewRandomNode(global, data);
        new_sec = StateEnergy(global);

        if (Accept(global, new_sec, old_sec) == false) {
          // Delete last node
          n_nodes--;
        }
        else {
          new_nodes++;
          nodes[nodes[n_nodes - 1].terminal[0]].front_node = false;
          nodes[nodes[n_nodes - 1].terminal[1]].front_node = false;
        }
      }
      while (new_nodes <= global.omega && n_nodes < global.max_nodes);
      // Number of new nodes exceds limit

      T *= global.alpha; // decrease temperature T

    }
    while (T > global.Tend && n_nodes < global.max_nodes); // Temperature is below Tend

  }

  /**
   * <p>
   * Adds a new node
   * </p>
   */
  public void NewBasicNode() {
    node new_node = new node();

    new_node.basic_node = true;

    new_node.terminal[0] = n_nodes;
    new_node.SEC = 1.0e20;
    new_node.k = 1.0;
    nodes[n_nodes] = new_node;
    n_nodes++;
  }

  /**
   * Acept or not a new SEC
   * @param global Global parameters
   * @param sec_new New Sec value
   * @param sec_old Old Sec value
   * @return boolean indicating if the sec has been accepted
   */
  private boolean Accept(Parameters global, double sec_new, double sec_old) {

    if (sec_new < sec_old) {
      return true;
    }
    else {
      double p = Math.exp( - (sec_new - sec_old) / T);

      if (Genesis.frandom(0.0, 1.0) < p) {
        return true;
      }
      else {
        return false;
      }
    }
  }

  /**
   * <p>
   * Returns the minimum SEC of all the front nodes of the state
   * </p>
   * @param global Global parameters
   */
  private double StateEnergy(SetupParameters global) {
    double min_sec = 1.0e20;

    for (int i = global.Ninputs; i < n_nodes; i++) {
      if (nodes[i].SEC < min_sec && nodes[i].front_node) {
        min_sec = nodes[i].SEC;
        output = i;
      }
    }

    return min_sec;
  }

  /**
   * <p>
   * Creates a new random node
   * </p>
   * @param global Global parameters
   * @param data Input data
   */
  public void NewRandomNode(SetupParameters global, Data data) {
    node next, best;
    double sec, covar[][], alpha[][], chisq[], alamda[], x[][], y[],
        ochisq[], atry[], beta[], da[], oneda[][], sd[], min, old_chisq;
    int mfit, sw[];
    boolean sing;

    // Create new node
    next = new node();
    best = new node();
    next.front_node = true;

    ochisq = new double[1];
    chisq = new double[1];
    alamda = new double[1];
    best = new node();
    sw = new int[node.TERMS];
    covar = new double[node.TERMS][node.TERMS];
    alpha = new double[node.TERMS][node.TERMS];
    x = new double[global.n_train_patterns][2];
    y = new double[global.n_train_patterns];
    atry = new double[node.TERMS];
    beta = new double[node.TERMS];
    da = new double[node.TERMS];
    oneda = new double[node.TERMS][1];
    sd = new double[global.n_train_patterns];
    for (int i = 0; i < global.n_train_patterns; i++) {
      sd[i] = 1.0;
    }

    /////// Creation of inputs

    // Test if inputs are constant
    double variance[] = new double[2];
    double mean[] = new double[2];
    double sosq[] = new double[2];

    do {
      mean[0] = mean[1] = sosq[0] = sosq[1] = 0.0;

      // Randomly select two terminals
      int one = Genesis.irandom( 0, n_nodes);
      int two = Genesis.irandom( 0, n_nodes);
      // If the two terminals form another front node destroy the old node
      // Not implemented yet

      next.terminal[0] = one;
      next.terminal[1] = two;

      // Set desired output and x1 and x2 inputs
      for (int i = 0; i < global.n_train_patterns; i++) {
        y[i] = data.train[i][global.Ninputs];

        x[i][0] = nodes[one].NodeOutput(data.train[i], nodes);
        x[i][1] = nodes[two].NodeOutput(data.train[i], nodes);

        mean[0] += x[i][0];
        mean[1] += x[i][1];
        sosq[0] += x[i][0] * x[i][0];
        sosq[1] += x[i][1] * x[i][1];
      }

      mean[0] /= global.n_train_patterns;
      mean[1] /= global.n_train_patterns;

      variance[0] = sosq[0] / global.n_train_patterns -
          mean[0] * mean[0];
      variance[1] = sosq[1] / global.n_train_patterns -
          mean[1] * mean[1];

    }
    while (variance[0] < 0.00001 || variance[1] < 0.00001);

    // For each prototype surface in F
    min = 1e20;
    for (int i = 1; i <= 4; i++) {
      // next.type = i;
      for (int j = 0; j < node.TERMS; j++) {
        next.a[j] = Genesis.frandom(-global.aRange, global.aRange);
      }
      // Fit the surface
      switch (i) {
        case 1:

          // Surfaces y = a0 + a1x1 + a2x2
          sw[0] = sw[1] = sw[2] = 1;
          sw[3] = sw[4] = sw[5] = 0;
          next.a[3] = next.a[4] = next.a[5] = 0.0;
          mfit = 3;
          break;

        case 2:

          // Surface y = a0 + a1x1 + a2x2 + a3x1x2
          sw[0] = sw[1] = sw[2] = sw[3] = 1;
          sw[4] = sw[5] = 0;
          next.a[4] = next.a[5] = 0.0;
          mfit = 4;
          break;

        case 3:

          // Surface y = a0 + a1x1 + a2x1**2
          sw[0] = sw[1] = sw[4] = 1;
          sw[2] = sw[3] = sw[5] = 0;
          next.a[2] = next.a[3] = next.a[5] = 0.0;
          mfit = 3;
          break;

        case 4:

          // Surface y = a0 + a1x1 + a2x2 + a3x1x2 + a4x1**2 + a5x2**2
          sw[0] = sw[1] = sw[2] = sw[3] = sw[4] = sw[5] = 1;
          mfit = 6;
          break;

        default:
          mfit = 0;
          break;
      }

      // Levenberg - Marquardt algorithm
      alamda[0] = -1.0;
      ochisq[0] = chisq[0] = 1.0e20;

      int ileven = 10; // Maximum number of Levenberg iterations. 
  
      do { // Repeat till convergence
        old_chisq = chisq[0];
        sing = LM.mrqmin(x, y, sd, global.n_train_patterns, next.a, sw,
                         node.TERMS,
                         covar,
                         alpha, chisq, alamda, mfit, ochisq, atry, beta, da,
                         oneda, global);
        if (sing)
        	ileven--;
      }
      while ( (old_chisq - chisq[0]) > global.LM_convergence && ileven > 0);

      /*alamda[0] = 0.0;
       LM.mrqmin(x, y, sd, global.n_train_patterns, next.a, sw, node.TERMS,
                covar,
                alpha, chisq, alamda, mfit, ochisq, atry, beta, da, oneda);*/

      sec = next.StructureEstimationCriterion(nodes, data, global);

      // Choose the surface with smallest SEC
      if (min > sec) {
        min = sec;
        next.CopyTo(best);
      }
    }

    /* Test if all the coefficients are 0
         boolean all_0 = true;
         for (int i = 0; i < node.TERMS; i++) {
      if (best.a[i] != 0.0) {
        all_0 = false;
      }
         }*/

    // Construct the node using the prototype surface chosen if the SEC of the node
    // is smaller than the SEC of parents
    if (best.SEC < nodes[best.terminal[0]].SEC &&
        best.SEC < nodes[best.terminal[1]].SEC /*&& !all_0 &&
                        best.SEC < 1e20*/) {
      // Add new node
      nodes[n_nodes] = best;
      n_nodes++;

      if (global.verbose) {
        System.err.println("Added node " + n_nodes);
      }
    }
  }

  /**
   * <p>
   * Saves the network to a file, including the seed
   * </p>
   * @param file_name The name of the file
   * @param seed Random seed
   * @param append Boolean for appending or replacing the file
   * @throws IOException
   */
  public void SaveNetwork(String file_name, long seed, boolean append) throws IOException {
    String line;

    try {
      // Result file
      FileOutputStream file = new FileOutputStream(file_name, append);
      BufferedWriter f = new BufferedWriter(new OutputStreamWriter(file));

      f.write("Random seed: " + seed);
      f.newLine();
      
      
      // For all nodes
      for (int i = 0; i < n_nodes; i++) {

        if (nodes[i].basic_node) {
          f.write("y(" + Integer.toString(i) + ") = x(" + Integer.toString(i) +
                  ")");

        }
        else {
          f.write("y(" + Integer.toString(i) + ") = ");
          if (nodes[i].a[0] != 0.0) {
            f.write(new PrintfFormat("%6.4g ").sprintf(nodes[i].a[0]));
          }
          if (nodes[i].a[1] != 0.0) {
            f.write(new PrintfFormat("%+6.4g").sprintf(nodes[i].a[1]));
            f.write(" y(" +
                    Integer.toString(nodes[i].terminal[0]) + ") ");

          }
          if (nodes[i].a[2] != 0.0) {
            f.write(new PrintfFormat("%+6.4g").sprintf(nodes[i].a[2]));
            f.write(" y(" +
                    Integer.toString(nodes[i].terminal[1]) + ") ");

          }
          if (nodes[i].a[3] != 0.0) {
            f.write(new PrintfFormat("%+6.4g").sprintf(nodes[i].a[3]));
            f.write(" y(" +
                    Integer.toString(nodes[i].terminal[0]) + ") y(" +
                    Integer.toString(nodes[i].terminal[1]) + ") ");

          }
          if (nodes[i].a[4] != 0.0) {
            f.write(new PrintfFormat("%+6.4g").sprintf(nodes[i].a[4]));
            f.write(" y(" +
                    Integer.toString(nodes[i].terminal[0]) + ")^2");

          }
          if (nodes[i].a[5] != 0.0) {
            f.write(new PrintfFormat("%+6.4g").sprintf(nodes[i].a[5]));
            f.write(" y(" +
                    Integer.toString(nodes[i].terminal[1]) + ")^2");

          }
        }

        f.newLine();

      }

      f.write("Output node: y(" + output + ")\n");

      f.close();
      file.close();
    }
    catch (FileNotFoundException e) {
      System.err.println("Cannot created output file");
    }

  }

  /**
   * <p>
   * Obtains the fitness of the sonn
   * </p>
   * @param input Input value
   * @return fitness value
   */
  public double GenerateOutput(double input[]) {
    return nodes[output].NodeOutput(input, nodes);
  }

  public double TestSONNInRegression(SetupParameters global, double data[][],
                                     int npatterns) {
    double fitness, RMS = 0.0, error, out;

    for (int i = 0; i < npatterns; i++) {
      // Obtain network output
      out = GenerateOutput(data[i]);

      // Obtain RMS error
      error = Math.pow(out - data[i][global.Ninputs], 2.0);
      RMS += Math.sqrt(error);

    }

    fitness = RMS / (npatterns * global.Noutputs);

    return fitness;
  }

  /**
   * <p>
   * Obtains fitness for a classification problem
   * </p>
   * @param global Global parameters 
   * @param data Input data
   * @param npatterns Number of patterns
   * @return fitness
   */
  public double TestSONNInClassification(SetupParameters global, double data[][],
                                         int npatterns) {
    double ok = 0.0;
    double fitness, out;

    for (int i = 0; i < npatterns; i++) {
      // Obtain network output
      out = GenerateOutput(data[i]);

      if ( (data[i][global.Ninputs] == 0 && out < 0.5) ||
          (data[i][global.Ninputs] == 1 && out > 0.5)) {
        ok++;
      }
    }

    fitness = ok / npatterns;

    return fitness;
  }

  /**
   * <p>
   * Saves the output to a file
   * </p<
   * @param file_name Name of the file
   * @param data Input data
   * @param n Number of patterns
   * @param global Global parameters
   * @throws IOException
   */
  public void SaveOutputFile(String file_name, double data[][], int n,
                             SetupParameters global) throws
      IOException {
    String line;
    double out;

    try {
      // Result file
      FileOutputStream file = new FileOutputStream(file_name);
      BufferedWriter f = new BufferedWriter(new OutputStreamWriter(file));
      
      // File header
      f.write("@relation "+Attributes.getRelationName()+"\n");
      f.write(Attributes.getInputAttributesHeader());
      f.write(Attributes.getOutputAttributesHeader());
      f.write(Attributes.getInputHeader()+"\n");
      f.write(Attributes.getOutputHeader()+"\n");
      f.write("@data\n");
      
      // For all patterns
      for (int i = 0; i < n; i++) {

        // Classification
        if (global.problem.compareToIgnoreCase("Classification") == 0) {
          // Obtain network output
          out = GenerateOutput(data[i]);
          
          /* Original output using numbers (Deprecated)
           f.write(Integer.toString( (int) data[i][global.Ninputs]) + " ");
           if (out < 0.5) {
              f.write(Integer.toString(0));
            }
            else {
              f.write(Integer.toString(1));
            }
          */
          
          // Current output using nominal values.
          f.write(Attributes.getOutputAttributes()[0].getNominalValue((int) data[i][global.Ninputs]) + " ");
          if (out < 0.5) {
            f.write(Attributes.getOutputAttributes()[0].getNominalValue(0));
          }
          else {
            f.write(Attributes.getOutputAttributes()[0].getNominalValue(1));

          }
        }
        // Regression
        else {
          f.write(Double.toString(data[i][global.Ninputs]) + " ");

          out = GenerateOutput(data[i]);
          f.write(Double.toString(out));

        }

        f.newLine();
      }

      f.close();
      file.close();
    }
    catch (FileNotFoundException e) {
      System.err.println("Cannot created output file");
    }

  }

}

