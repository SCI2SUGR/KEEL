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
 * Class that represents a node of a Neural Network
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */

public class node {

  /** Node type */
  boolean basic_node, front_node;

  /** Parameters of the node */
  double a[], SEC, k;

  /** Terminals of the node */
  int terminal[];

  /** Number of terms */
  public static final int TERMS = 6;

  /** Absolute minimum value */
  public static final double NEARZERO = 0.0000000001;

  // int type; // 1) a0 + a1x1 + a2x2
  // 2) a0 + a1x1 + a2x2 + a3x1x2
  // 3) a0 + a1x1 + a2x1**2
  // 4) a0 + a1x1 + a2x2 + a3x1x2 + a4x1**2 + a5x2**2

  /**
   * <p>
   * Constructor
   * </p>
   */
  public node() {

    a = new double[TERMS];
    terminal = new int[2];
    basic_node = front_node = false;
  }

  /**
   * <p>
   * Copy method
   * </p>
   * @param dest node where this node is going to copied
   */
  public void CopyTo(node dest) {

    dest.basic_node = basic_node;
    dest.front_node = front_node;
    dest.k = k;
    dest.SEC = SEC;
    dest.terminal[0] = terminal[0];
    dest.terminal[1] = terminal[1];
    for (int i = 0; i < TERMS; i++) {
      dest.a[i] = a[i];
    }
  }

  /**
   * <p>
   * Calculates the Structure Estimation Criterion
   * </p>
   * @param nodes Vector of nodes
   * @param data Training data
   * @param global Global parameters of the algorithm
   * @return Structure estimation value
   */
  public double StructureEstimationCriterion(node nodes[], Data data,
                                             SetupParameters global) {
    double y;

    // MDL = 0.5NlogS**2 + 0.5klogN

    k = nodes[terminal[0]].k + nodes[terminal[1]].k;

    for (int i = 0; i < TERMS; i++) {
      if (a[i] != 0.0) {
        k++;

        // Obtain mean square error (S**2)
      }
    }
    double mse = 0.0;
    for (int i = 0; i < global.n_train_patterns; i++) {
      y = NodeOutput(data.train[i], nodes);

      if (global.error.compareToIgnoreCase("mse") == 0) {
        mse += (y - data.train[i][global.Ninputs]) *
            (y - data.train[i][global.Ninputs]);
      }
      else {

        if ( (data.train[i][global.Ninputs] == 0 && y > 0.5) ||
            (data.train[i][global.Ninputs] == 1 && y < 0.5)) {
          mse += 1.0;
        }
      }

    }

    mse /= global.n_train_patterns;

    if (mse < NEARZERO) {
      mse = NEARZERO;
    }

    SEC = global.w_mse * (0.5 * global.n_train_patterns * math.log10(mse)) +
        global.w_k * (0.5 * k * math.log10( (double)
                                           global.n_train_patterns));
    return SEC;
  }

  /**
   * <p>
   * Obtains the node output
   * </p>
   * @param input Vector of inputs
   * @param nodes Vector of nodes
   * @return node output
   */
  public double NodeOutput(double input[], node nodes[]) {
    double sum, x1, x2;

    // Input node
    if (basic_node) {
      return (input[terminal[0]]);
    }
    else {

      x1 = nodes[terminal[0]].NodeOutput(input, nodes);
      x2 = nodes[terminal[1]].NodeOutput(input, nodes);

      sum = a[0] + a[1] * x1 + a[2] * x2 + a[3] * x1 * x2 + a[4] * x1 * x1 +
          a[5] * x2 * x2;

      return sum;
    }
  }
}

