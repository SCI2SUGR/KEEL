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
 * <p>
 * @author Writed by Pedro González (University of Jaen) 15/02/2004
 * @author Modified by Pedro González (University of Jaen) 4/08/2007
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 20/04/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDIGA.SDIGA;

import org.core.*;

public class Population {
    /**
     * <p>
     * Population of candidate rules
     * </p>
     */

    private Individual indivi [];    // Population individuals
    private int num_indiv;           // Number of individuals in the population
    private int Best_guy;            // Position of the better individual

      
      /**
       * <p>
       * Creates a population of Individual
       * </p>
       * @param numind          Number of individuals
       * @param numgen          Number of variables
       * @param RulesRep        Rules representation
       * @param Variables       Variables structure
       */
      public Population(int numind, int numgen, String RulesRep, TableVar Variables) {
          indivi = new Individual[numind];
            num_indiv = numind;
            for(int i=0; i<numind; i++){
                if(RulesRep.compareTo("CAN")==0){
                    indivi[i] = new IndCAN(numgen);
                } else {
                    indivi[i] = new IndDNF(numgen,Variables);
                        
                }
            }
      }


      /**
       * <p>
       * Biased random population initialization
       * </p>
       * @param Variables       Variables structure
       */
      public void RndInitPop (TableVar Variables) {
          for(int i=0; i<num_indiv; i++)
              indivi[i].RndInitInd(Variables);
      }
        

      /**
       * <p>
       * Gets the position of the better individual of the population
       * </p>
       * @return                Position of the best individual
       */
      public int getBestGuy() {
          return Best_guy;
      }
      

      /**
       * <p>
       * Evaluates the population
       * </p>
       * @param AG              Genetic algorithm
       * @param Variables       Variables structure
       * @param Examples        Examples structure
       * @param marcar          Indicates to mark the covered examples
       * @return                Number of evalutions
       */
      public int evalPop (Genetic AG, TableVar Variables, TableDat Examples, boolean marcar) {

          double performance = 0;             // Fitness of the individual evaluated
          double Best_current_perf = 0;       // Better fitness of the invididuals evaluated
          int numero_eval=0;                  // Number of individuals evaluated
          
          Best_guy = 0;   // At the beginning, set to 0 the Best_guy

          // For each individual in the population
           for (int i=0; i<num_indiv; i++) {
              if (!getIndivEvaluated(i)) {
                  // Only nonevaluated individuals are evaluated
                  indivi[i].evalInd (AG, Variables, Examples, marcar);
                  numero_eval++;              
                  // Increments the number of individuals evaluated
              }
              performance = getIndivFitness(i);
              
              // Computes position of the better individual
              if (i==0) // For the first individual
                  Best_current_perf = performance;
              else
                  if (Utils.BETTER (performance, Best_current_perf)) {
                      Best_current_perf = performance;
                      Best_guy = i;   
                  }
          } // End for each individual
          return numero_eval;
      }


      /**
       * <p>
       * Evaluates an individual of the population
       * </p>
       * @param pos             Position of the individual
       * @param Variables       Variables structure
       * @param Examples        Examples structure
       * @param marcar          Indicates to mark the covered examples
       */
      public void evalIndiv (int pos, Genetic AG, TableVar Variables, TableDat Examples, boolean marcar) {
          indivi[pos].evalInd (AG, Variables, Examples, marcar);
      }
      
      
      /**
       * <p>
       * Get the measures of a single rule
       * </p>
       * @param pos             Position of the individual
       * @param nFile           File to write the measures
       */
      public QualityMeasures getMedidas (int pos, String nFile) {
          if (!getIndivEvaluated(pos)) {
              String contents = "\n\nWarning: The individual " + pos + " is not evaluated and the quality measure values are ficticious\n";
              System.out.println (contents);
              Files.addToFile(nFile, contents);
          }
          return (indivi[pos].getMedidas());
      }

      
      /**
       * <p>
       * Returns the indicated gene of the CromCAN
       * </p>
       * @param num_indiv               Number of individual
       * @param pos                     Position of the gene
       * @return                        Value of the gene
       */
      public int getCromElem (int num_indiv, int pos) {
          return indivi[num_indiv].getCromElem (pos);
      }

     /**
       * <p>
       * Sets the value of the indicated gene of the CromCAN
       * </p>
       * @param num_indiv               Number of individual
       * @param pos                     Position of the gene
       * @param val                     Value of the gene
       */
      public void setCromElem (int num_indiv, int pos, int val) {
          indivi[num_indiv].setCromElem (pos, val);
      }

      /**
       * <p>
       * Returns the indicated gene of the CromCAN
       * </p>
       * @param num_indiv               Number of individual
       * @param var                     Position of the variable
       * @param pos                     Position of the gene
       * @return                        Value of the gene
       */
      public int getCromElemGene (int num_indiv, int var, int pos) {
          return indivi[num_indiv].getCromElemGene(var, pos);
      }

     /**
      * <p>
      * Sets the value of the indicated gene of the CromCAN
      * </p>
      * @param num_indiv               Number of individual
      *  @param var                     Position of the variable
      * @param pos                     Position of the gene
      * @param val                     Value of the gene
      */
      public void setCromElemGene (int num_indiv, int var, int pos, int val) {
          indivi[num_indiv].setCromElemGene(var, pos, val);
      }

      /**
       * <p>
       * Returns de hole cromosoma of the selected individual
       * </p>
       * @param num_indiv               Number of individual
       * @return                        Canonical chromosome of the individual
       */
      public CromCAN getIndivCromCAN (int num_indiv) {
          return indivi[num_indiv].getIndivCromCAN();
      }

      /**
       * <p>
       * Returns de hole cromosoma of the selected individual
       * </p>
       * @param num_indiv               Number of individual
       * @return                        DNF chromosome of the individual
       */
      public CromDNF getIndivCromDNF (int num_indiv) {
          return indivi[num_indiv].getIndivCromDNF();
      }


      /**
       * <p>
       * Returns fitness of the indicated inidividual of the population
       * </p>
       * @param num_indiv               Number of individual
       * @return                        Value of the fitness
       */
      public float getIndivFitness (int num_indiv) {
          return indivi[num_indiv].getIndivFitness ();
      }

      /**
       * <p>
       * Sets the value of fitnes for the the indicated individual
       * </p>
       * @param num_indiv               Number of individual
       * @param val                     Value of the fitness
       */
      public void setIndivFitness (int num_indiv, float val) {
          indivi[num_indiv].setIndivFitness (val);
      }

      
      /**
       * <p>
       * Returns if the individual of the population has been evaluated
       * </p>
       * @param num_indiv               Number of individual
       * @return                        Value of the state of the individual
       */
      public boolean getIndivEvaluated (int num_indiv) {
          return indivi[num_indiv].getIndivEvaluated ();
      }

      /**
       * <p>
       * Sets the state evaluated of the individual of the population
       * </p>
       * @param num_indiv               Number of individual
       * @param val                     Value of the state of the individual
       */
      public void setIndivEvaluated (int num_indiv, boolean val) {
          indivi[num_indiv].setIndivEvaluated (val);
      }

      
      /**
       * <p>
       * Prints population individuals
       * </p>
       * @param nFile                   File to write the Population
       */
      public void print(String nFile) {

          Files.addToFile(nFile, "Population\n");
          for(int i=0; i<num_indiv; i++) {
              Files.addToFile(nFile, "Individuo " + i + ": ");
              indivi[i].Print(nFile);
          }
          Files.addToFile(nFile, "\n");
      }
  
}
