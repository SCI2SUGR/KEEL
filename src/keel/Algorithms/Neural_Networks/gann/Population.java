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
 * Class Population, which represents a population of individuals
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 9/02/2007
 * @version 0.1
 * @since JDK1.5
 */

public class Population {

  int n_indiv;
  Individual indiv[];
  double fitness[];

  /**
   * <p>
   * Constructor
   * </p>
   * @param global
   */
  public Population(SetupParameters global) {

    n_indiv = global.n_indiv;
    indiv = new Individual[n_indiv];
    fitness = new double[n_indiv];
  }


  
  
  
  /**
   * <p>
   * Method that evolves the population until the maximum number of generations is reached. It initializes the first population, and in
   * each iteration, saves the best individual by replicating it, makes the crossover, makes the mutation, copies the new population
   * into the old one, and evaluates fitness.
   * </p>
   * @param global Global Definition parameters
   * @param data Input data
   * @return Best individual obtained
   */
  public Individual EvolvePopulation(SetupParameters global, Data data) {
    boolean finish = false;
    int being, next, father, mother;

    Population new_pop;

    new_pop = new Population(global);
    // Get size of phenotype
    int size = global.Ninputs * global.Nhidden[0];
    for (int i = 1; i < global.Nhidden_layers; i++) {
      size += global.Nhidden[i] * global.Nhidden[i - 1];
    }
    size += global.Noutputs * global.Nhidden[global.Nhidden_layers - 1];

    for (int i = 0; i < n_indiv; i++) {
      new_pop.indiv[i] = new Individual (size);
    }

    // Initialize population
    for (int i = 0; i < n_indiv; i++) {
      indiv[i] = new Individual(global);
    }

    // Evaluate fitness
    for (int i = 0; i < n_indiv; i++) {
      fitness[i] = indiv[i].EvaluateIndividual(global, data);
    }

    int g = 1;
    while (!finish) {

      // Elitist replication
      for (being = 0; being < n_indiv * global.elite; being++) {
        next = Selector.Ordered(fitness, being, n_indiv);
        indiv[next].CopyIndividualTo(new_pop.indiv[being]);
      }

      // Crossover
      for (; being < n_indiv; being++) {
        father = Selector.Roulette(fitness, n_indiv, global);
        mother = Selector.Roulette(fitness, n_indiv, global);

        new_pop.indiv[being].TwoPointsCrossover(global, indiv[father], indiv[mother], data);
      }

      // Structural mutation
      for (int i = 0; i < n_indiv; i++) {
        if (Rand.frandom( 0, 1) < global.p_struct) {
          new_pop.indiv[i].StructuralMutation(global);
        }
      }

      // Parametric mutation
      for (int i = 0; i < n_indiv; i++) {
        if (Rand.frandom( 0, 1) < global.p_param) {
          new_pop.indiv[i].ParametricMutation(global);
        }
      }


      // BP mutation
      for (int i = 0; i < n_indiv; i++) {
        if (Rand.frandom( 0, 1) < global.p_bp) {
          new_pop.indiv[i].BPMutation(global, data.train, global.n_train_patterns);
        }
      }


      // Copy new population onto old one
      for (int i = 0; i < n_indiv; i++) {
        new_pop.indiv[i].CopyIndividualTo(indiv[i]);
      }

      // Evaluate fitness
      for (int i = 0; i < n_indiv; i++) {
        fitness[i] = indiv[i].EvaluateIndividual(global, data);
      }

      // Print best individual so far
      System.out.print("Best individual on generation " + g + ": ");
      System.out.println(fitness[Selector.Ordered(fitness, 0, n_indiv)]);

      // Evaluate stop criterion
      g++;
      if (g > global.max_generations) {
        finish = true;
      }
    }

    // Select best individual and return it
    next = Selector.Ordered(fitness, 0, n_indiv);
    return indiv[next];

  }

}

