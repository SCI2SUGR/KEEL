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

/**
 *
 * File: CHC.java
 *
 * A CHC implementation for EFKNNIVFS.
 *
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011
 * @version 1.0
 * @since JDK1.5
 *
 */

package keel.Algorithms.Fuzzy_Instance_Based_Learning.EF_KNN_IVFS;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * CHC algorithm implementation (static)
 */
class CHC {

    private static List<Chromosome> population;
    private static int threshold;
    private static int current_threshold;
    private static int population_size;

    /**
     * Initialize the algorithm structures
     * @param pop_size Size of the population
     */
    public static void initialize(int pop_size){

        population = new ArrayList<>();
        for(int i=0; i<pop_size; i++){
            Chromosome chromosome = new Chromosome();
            chromosome.setFitness(Fitness.evaluate(chromosome));
            population.add(chromosome);
        }
        population_size = pop_size;
        threshold = pop_size / 4;
        current_threshold = threshold;

    }

    /**
     * Run a generation of CHC
     * @return The current number of evaluations used
     */
    public static int doGeneration(){

        Collections.shuffle(population);

        //cross dissimilar pairs
        List<Chromosome> offspring = new ArrayList<>();
        for(int i=0; i<population_size; i+=2){
            Chromosome first = new Chromosome(population.get(i));
            Chromosome second = new Chromosome(population.get(i+1));

            if (first.distance(second) >= current_threshold){
                first.crossover(second);
                first.setFitness(Fitness.evaluate(first));
                second.setFitness(Fitness.evaluate(second));
                offspring.add(first);
                offspring.add(second);
            }
        }

        //merge old and new populations

        Collections.sort(population);
        double worstFitness = population.get(population.size()-1).getFitness();

        population.addAll(offspring);
        population = new ArrayList<>(population.subList(0,population_size));

        Collections.sort(population);
        boolean stagnation = worstFitness == population.get(population.size()-1).getFitness();

        //reinitialization in case of stagnation
        if(stagnation){
            current_threshold --;

            if(current_threshold == 0){

                // reset the population:

                // 1) all members are copies of the best chromosome found so far

                for(int i = 1; i<population_size; i++){
                    population.set(i,new Chromosome(population.get(0)));
                }

                // 2) mutate all copies (not the original)
                for(int i = 1; i<population_size; i++){
                    population.get(i).mutate();
                }

                // 3) evaluate & sort the population
                for(int i = 1; i<population_size; i++){
                    population.get(i).setFitness(Fitness.evaluate(population.get(i)));
                }
                Collections.sort(population);

                current_threshold = threshold;
            }
        }

        return Fitness.getEvaluations();
    }

    /**
     * Get the current best solution found by CHC
     * @return The best chromosome of the population
     */
    public static Chromosome getSolution(){
        return population.get(0);
    }

}
