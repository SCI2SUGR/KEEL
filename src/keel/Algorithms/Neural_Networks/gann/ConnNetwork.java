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

/**
 * <p>
 * This is a Connectionist Network
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */

public class ConnNetwork extends Network {
	
  // Matrix containing the connections of the neural net
  public boolean conns[][][];
  
  /**
   * <p>
   * Constructor that receives the parameters of the algorithm
   * </p>
   * @param global Parameters of the algorithm
   */
  public ConnNetwork(Parameters global) {

    double range;

    transfer = new String[global.Nhidden_layers + 1];
    for (int i = 0; i < global.Nhidden_layers + 1; i++) {
      transfer[i] = global.transfer[i];
    }
    Ninputs = global.Ninputs;
    Noutputs = global.Noutputs;
    Nlayers = global.Nhidden_layers + 2;
    Nhidden = new int[Nlayers];
    w = new double[Nlayers - 1][][];
    conns = new boolean[Nlayers - 1][][];
    delta = new double[Nlayers][];
    activation = new double[Nlayers][];
    momentum = new double[Nlayers - 1][][];
    Nhidden[0] = Ninputs;
    delta[0] = new double[Nhidden[0]];
    activation[0] = new double[Nhidden[0]];
    for (int i = 1; i < Nlayers; i++) {
      Nhidden[i] = global.Nhidden[i - 1];
      w[i - 1] = new double[Nhidden[i]][Nhidden[i - 1]];
      conns[i-1] = new boolean[Nhidden[i]][Nhidden[i - 1]];
      momentum[i - 1] = new double[Nhidden[i]][Nhidden[i - 1]];
      delta[i] = new double[Nhidden[i]];
      activation[i] = new double[Nhidden[i]];
    }
    Nhidden[Nlayers - 1] = Noutputs;

    /* Initialize network weights
    for (int k = 0; k < Nlayers - 1; k++) {
      range = Math.sqrt(3.0) / Nhidden[k];
      for (int i = 0; i < Nhidden[k + 1]; i++) {
        for (int j = 0; j < Nhidden[k]; j++) {
          w[k][i][j] = Genesis.frandom(global.random, -range, range);

        }
      }
    }*/

  }

  /**
   * <p>
   * Method that implements the backpropagation algorithm
   * </p>
   * @param global Parameters of the algorithm
   * @param cycles Number of cycles
   * @param data Data matrix file
   * @param npatterns Number of patterns in data
   */
  public void BackPropagation(Parameters global, int cycles, double data[][],
                               int npatterns) {
    int pattern;
    double change;

    double[] error = new double[Noutputs];

    // Momentum set to 0
    for (int k = 0; k < Nlayers - 1; k++) {
      for (int i = 0; i < Nhidden[k + 1]; i++) {
        for (int j = 0; j < Nhidden[k]; j++) {
          momentum[k][i][j] = 0.0;

        }
      }
    }
    for (int iter = 0; iter < cycles; iter++) {
      // Choose a random pattern
      pattern = Genesis.irandom(0, npatterns);
      // Forward pass
      GenerateOutput(data[pattern]);

      // Obtain error for output nodes
      for (int i = 0; i < Noutputs; i++) {
        error[i] = data[pattern][Ninputs + i] - activation[Nlayers - 1][i];
      }

      // Compute deltas for output
      for (int i = 0; i < Noutputs; i++) {
        if (transfer[Nlayers - 2].compareToIgnoreCase("Log") == 0) {
          delta[Nlayers - 1][i] = error[i] * b_log * activation[Nlayers -
              1][i] * (1.0 - activation[Nlayers - 1][i] / a);
        }
        else if (transfer[Nlayers - 2].compareToIgnoreCase("Htan") == 0) {
          delta[Nlayers -
              1][i] = error[i] * (b_htan / a) * (a - activation[Nlayers - 1][i]) *
              (a + activation[Nlayers - 1][i]);
        }
        else {
          delta[Nlayers - 1][i] = error[i];

        }
      }

      // Compute deltas for hidden nodes
      for (int k = Nlayers - 2; k > 0; k--) {
        for (int i = 0; i < Nhidden[k]; i++) {
          delta[k][i] = 0.0;
          for (int j = 0; j < Nhidden[k + 1]; j++) {
            delta[k][i] += delta[k + 1][j] * w[k][j][i];
          }
          if (transfer[k - 1].compareToIgnoreCase("Log") == 0) {
            delta[k][i] *= b_log * activation[k][i] *
                (1.0 - activation[k][i] / a);
          }
          else if (transfer[k - 1].compareToIgnoreCase("Htan") == 0) {
            delta[k][i] *= (b_htan / a) * (a - activation[k][i]) *
                (a + activation[k][i]);
          }
        }
      }

      // Update weights
      for (int k = Nlayers - 2; k >= 0; k--) {
        for (int i = 0; i < Nhidden[k + 1]; i++) {
          for (int j = 0; j < Nhidden[k]; j++) {
            if (conns[k][i][j]) {
              change = global.eta * delta[k + 1][i] * activation[k][j] +
                  global.alpha * momentum[k][i][j] -
                  global.lambda * w[k][i][j];
              w[k][i][j] += change;
              momentum[k][i][j] = change;
            }
          }
        }
      }
    }
  }
}

