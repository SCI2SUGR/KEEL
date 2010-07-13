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
 * Class which represents an individual
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */

public class Individual {
	
  int size;
  boolean connect[];
  double w[];
  
  /**
   * <p>
   * Constructor that sets individual's size
   * </p>
   * @param s size of the vectors representing the individuals
   */
  public Individual(int s) {
    size = s;
    connect = new boolean[s];
    w = new double[s];
  }

  /**
   * <p>
   * Constructor that initializes all the attributes of an individual instance,
   * given the SetupParameters 'global' object.
   * </p>
   * @param global SetupParameters of the algorithm
   */
  public Individual(SetupParameters global) {

    // Initialize
    // Get size of phenotype
    size = global.Ninputs * global.Nhidden[0];
    for (int i = 1; i < global.Nhidden_layers; i++) {
      size += global.Nhidden[i] * global.Nhidden[i - 1];
    }
    size += global.Noutputs * global.Nhidden[global.Nhidden_layers - 1];

    connect = new boolean[size];
    w = new double[size];

    // Initialize weight and connectivity
    for (int i = 0; i < size; i++) {
      if (Rand.frandom( 0.0, 1.0) < global.connectivity) {
        connect[i] = true;
        w[i] = Rand.frandom( -global.w_range, global.w_range);
      }
      else {
        connect[i] = false;
        w[i] = 0.0;
      }
    }

  }
  
  
  /**
   * <p> 
   * Two points crossover
   * Parents are not modified
   * Returns the best of the two offspring
   * </p>
   * @param global Parameters of the algorithm
   * @param father The father
   * @param mother The mother
   * @param data Data structures previously taken from the data file
   */
  void TwoPointsCrossover(SetupParameters global, Individual father,
                          Individual mother, Data data) {
    int one, two;
    Individual son, daughter;
    double son_fitness, daughter_fitness;

    son = new Individual(size);
    daughter = new Individual(size);

    // Select crossover points.
    one = Rand.irandom( 0, size);
    two = Rand.irandom( one, size);

    // Perform crossover.
    for (int i = 0; i < one; i++) {
      son.connect[i] = father.connect[i];
      son.w[i] = father.w[i];
      daughter.connect[i] = mother.connect[i];
      daughter.w[i] = mother.w[i];
    }

    for (int i = one; i < two; i++) {
      son.connect[i] = mother.connect[i];
      son.w[i] = mother.w[i];
      daughter.connect[i] = father.connect[i];
      daughter.w[i] = father.w[i];
    }

    for (int i = two; i < size; i++) {
      son.connect[i] = father.connect[i];
      son.w[i] = father.w[i];
      daughter.connect[i] = mother.connect[i];
      daughter.w[i] = mother.w[i];
    }

    son_fitness = son.EvaluateIndividual(global, data);
    daughter_fitness = daughter.EvaluateIndividual(global, data);

    if (son_fitness > daughter_fitness) {
      son.CopyIndividualTo(this);
    }
    else {
      daughter.CopyIndividualTo(this);
    }

  }
  
  /**
   * <p>
   * Method that implements the parametric mutation
   * </p>
   * @param global Parameters of the algorithm
   */
  void ParametricMutation(SetupParameters global) {

    for (int i = 0; i < size; i++) {
      if (connect[i]) {
        w[i] += Rand.Normal(0.0, 1.0);
      }
    }
  }

  /**
   * <p>
   * Method that implements the BP mutation operator
   * </p>
   * @param global Parameters of the algorithm
   * @param data Data matrix file
   * @param n Number of patterns in data
   */
  void BPMutation(SetupParameters global, double data[][], int n) {

	    ConnNetwork net = new ConnNetwork (global);

	    PhenotypeToNetwork (net);

	    net.BackPropagation(global, global.cycles, data, n);

	    NetworkToGenotype (net);

	  }
  
  /**
   * <p>
   * Method that implements the structural mutation
   * </p>
   * 
   * @param global SetupParameters of the algorithm
   */
  void StructuralMutation(SetupParameters global) {

    int i = Rand.irandom( 0, size);

    connect[i] = !connect[i];

    if (connect[i]) {
      w[i] = Rand.frandom(-global.w_range, global.w_range);
    }

  }

  /**
   * <p>
   * Copy method
   * </p>
   * @param dest Individual which receives the copy
   */
  void CopyIndividualTo(Individual dest) {

    for (int i = 0; i < size; i++) {
      dest.connect[i] = connect[i];
      dest.w[i] = w[i];
    }
  }

  /**
   * <p>
   * Method that evaluates the Individual
   * </p>
   * @param global SetupParameters of the algorithm
   * @param data Training data
   * @return fitness of the individual
   */
  double EvaluateIndividual(SetupParameters global, Data data) {
    double fitness = 0.0;

    ConnNetwork net = new ConnNetwork(global);
    PhenotypeToNetwork(net);

    if (global.problem.compareToIgnoreCase("Classification") == 0) {
      fitness = net.TestNetworkInClassification(global, data.train,global.n_train_patterns);
    }
    else if (global.problem.compareToIgnoreCase("Regression") == 0) {
      fitness = net.TestNetworkInRegression(global, data.train,global.n_train_patterns);
    }

    return fitness;
  }

  /**
   * <p>
   * Method that implements the network to genotype conversion
   * </p>
   * @param net Connectionist network
   */
  void NetworkToGenotype(ConnNetwork net) {
    int i, j, k, l;

    l = 0;
    for (i = 0; i < net.Nlayers-1; i++) {
      for (j = 0; j < net.Nhidden[i+1]; j++) {
        for (k = 0; k < net.Nhidden[i]; k++) {
          if (net.conns[i][j][k]) {
            w[l] = net.w[i][j][k];
            connect[l] = true;
          }
          else {
            w[l] = 0.0;
            connect[l] = false;
          }
          l++;
        }

      }
    }

  }

  /**
   * <p>
   * Method that implements the phenotype to network conversion
   * </p>
   * @param net Connectionist network
   */
  void PhenotypeToNetwork(ConnNetwork net) {

    int i, j, k, l;

    l = 0;
    for (i = 0; i < net.Nlayers-1; i++) {
      for (j = 0; j < net.Nhidden[i+1]; j++) {
        for (k = 0; k < net.Nhidden[i]; k++) {
          if (connect[l]) {
            net.w[i][j][k] = w[l];
            net.conns[i][j][k] = true;
          }
          else {
            net.w[i][j][k] = 0.0;
            net.conns[i][j][k] = false;
          }
          l++;
        }

      }
    }

  }

}

