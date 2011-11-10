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
 * @author Written by Cristóbal J. Carmona (University of Jaen) 11/08/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.NMEEFSD.NMEEFSD;

import java.util.Vector;

public class Population {
    /**
     * <p>
     * Population of candidate rules
     * </p>
     */

      private Individual indivi [];     // Population individuals
      private int num_indiv;           // Max number of individuals in the population
      public int num_used;             // Number or individuals really used
      public boolean ej_cubiertos[];   // Covered examples of the population
      public int ult_cambio_eval;      // Last change in the population
      
      /**
       * <p>
       * Creates a population of Individual
       * </p>
       * @param numind          Number of individuals
       * @param numgen          Number of variables
       * @param nobj            Number of objectives
       * @param neje            Number of examples
       * @param RulRep          Rules representation
       * @param Variables       Variables structure
       */
      public Population(int numind, int numgen, int nobj, int neje, String RulRep, TableVar Variables) {

          indivi = new Individual[numind];
          num_indiv = numind;
          num_used = 0;
          for(int i=0; i<numind; i++){
              if(RulRep.compareTo("CAN")==0){
                indivi[i] = new IndCAN(numgen, neje, nobj);
              } else {
                indivi[i] = new IndDNF(numgen, neje, nobj, Variables);
              }
          }

          ej_cubiertos = new boolean[neje];
          ult_cambio_eval = 0;
          
      }

      
      /**
       * <p>
       * Biased random population initialization
       * </p>
       * @param Variables       Variables structure
       * @param porcVar         Percentage of variables to form the rules
       * @param porcPob         Percentage of population with biased initialisation
       * @param neje            Number of examples
       * @param nFile           File to write the population
       */
      public void BsdInitPob (TableVar Variables, float porcVar, float porcPob, int neje, String nFile) {
          String contents;
          float parteSesg = porcPob * num_indiv;
          int i,j;

          for(i=0; i<parteSesg; i++) {
              contents = "Individuo(s) " + i + ": ";
              indivi[i].BsdInitInd(Variables, porcVar, neje, nFile);
          }
          for(j=i; j<num_indiv; j++) {
              indivi[j].RndInitInd(Variables, neje, nFile);
          }

          num_used = num_indiv;
          for(i=0; i<neje; i++)
            ej_cubiertos[i] = false;
      }
        
      
     /**
      * <p>
      * Evaluates non-evaluated individuals
      * </p>
      * @param AG                   Genetic algorithm
      * @param Variables            Variables structure
      * @param Examples             Examples structure
      * @return                     Number of evaluations performed
      */
      public int evalPop (Genetic AG, TableVar Variables, TableDat Examples) {

          int trials = 0;

          for (int i=0; i<AG.getLengthPopulation(); i++) {
              if (!getIndivEvaluated(i)) {     // Not evaluated
                  indivi[i].evalInd (AG, Variables, Examples);
                  setIndivEvaluated(i,true);   // Now it is evaluated
                  indivi[i].setNEval(AG.getTrials());
                  trials++;
              }
          }
          return trials;
      }

      
      /**
       * <p>
       * Returns the indicated individual of the population
       * </p>
       * @param pos             Position of the individual
       * @return                Individual
       */
      public Individual getIndiv (int pos) {
          return indivi[pos];
      }
      
      /**
       * <p>
       * Return the number of individuals of the population
       * </p>
       * @return                Number of individuals of the population
       */
      public int getNumIndiv(){
        return num_indiv;
      }

      /**
       * <p>
       * Copy the individual in the Individual otro
       * </p>
       * @param pos             Position of the individual to copy
       * @param neje            Number of examples
       * @param nobj            Number of objectives
       * @param a               Individual to copy
       */
      public void CopyIndiv (int pos, int neje, int nobj, Individual a) {
          indivi[pos].copyIndiv(a, neje, nobj);
      }


      /**
       * <p>
       * Returns the indicated gene of the Chromosome
       * </p>
       * @param num_indiv               Position of the individual
       * @param pos                     Position of the variable
       * @param elem                    Position of the gene of the variable
       * @param RulRep                Rules representation
       * @return                        Gene of the chromosome
       */
      public int getCromElem (int num_indiv, int pos, int elem, String RulRep) {

          if(RulRep.compareTo("CAN")==0){
               return indivi[num_indiv].getCromElem(pos);
          } else {
               if(indivi[num_indiv].getCromGeneElem(pos, elem)==true)
                   return 1;
               else return 0;
          }
          
      }


      /**
       * <p>
       * Sets the value of the indicated gene of the Chromosome
       * </p>
       * @param num_indiv               Position of the individual
       * @param pos                     Position of the variable
       * @param elem                    Position of the gene of the variable
       * @param val                     Value for the gene
       * @param RulRep                Rules representation
       */
      public void setCromElem (int num_indiv, int pos, int elem, int val, String RulRep) {

          if(RulRep.compareTo("CAN")==0){
               indivi[num_indiv].setCromElem(pos, val);
          } else {
              if(val==0)
                indivi[num_indiv].setCromGeneElem(pos, elem, false);
              else indivi[num_indiv].setCromGeneElem(pos, elem, true);
          }
          
      }

      
      /**
       * <p>
       * Returns if the individual of the population has been evaluated
       * </p>
       * @param num_indiv               Position of the individual
       */
      public boolean getIndivEvaluated (int num_indiv) {
          return indivi[num_indiv].getIndivEvaluated ();
      }


      /**
       * <p>
       * Sets the value for de evaluated attribute of the individual
       * </p>
       * @param num_indiv           Position of the individual
       * @param val                 Value of the individual
       */
      public void setIndivEvaluated (int num_indiv, boolean val) {
          indivi[num_indiv].setIndivEvaluated (val);
      }
      

      /**
       * <p>
       * Returns de hole cromosoma of the selected individual
       * </p>
       * @param num_indiv           Position of the individual
       * @return                    Canonical chromosome
       */
      public CromCAN getIndivCromCAN (int num_indiv) {
          return indivi[num_indiv].getIndivCromCAN();
      }

      /**
       * <p>
       * Returns de hole cromosoma of the selected individual
       * </p>
       * @param num_indiv           Position of the individual
       * @return                    DNF chromosome
       */
      public CromDNF getIndivCromDNF (int num_indiv) {
          return indivi[num_indiv].getIndivCromDNF();
      }
      
      /*
       * <p>
       * Return the number of the evaluation with the last change
       * </p>
       * @return                    Number of the last evaluation
       */
      public int getLastChangeEval(){
          return ult_cambio_eval;       
      }
      

      /**
       * <p>
       * This function marks the examples covered by the actual population.
       * </p>
       * @param neje            Number of examples
       * @param trials          Number of trials performed
       */
      public void examplesCoverPopulation (int neje, int trials){
      
          //Copies the actual examples structure
          boolean cubiertos_antes[] = new boolean[neje];
          
          for(int i=0; i<neje; i++){
            cubiertos_antes[i] = ej_cubiertos[i];
            //Initialises the new structure
            ej_cubiertos[i] = false;
          }
          
          // Checks the examples covered by the actual population
          for (int a = 0; a < getNumIndiv(); a++) { // for each rule
              if (indivi[a].getRank()==0){
                  for (int b = 0; b < neje; b++) {  // for each example
                      //Checks if the individual 'a' covers all the examples of 'b'
                      if (indivi[a].getIndivCovered(b)){
                          ej_cubiertos[b] = true;
                      }
                  }
              }
          }
          
          
          //Comparisons both structures
          boolean centi = false;
          int i = 0;
          while((!centi)&&(i<neje)){
              //If both have different values
              if(cubiertos_antes[i] != ej_cubiertos[i]){
                  //If the last value was false
                  if(cubiertos_antes[i] == false){
                      //There is change
                      ult_cambio_eval = trials;
                      centi = true;
                  }
              }
              i++;          
          }
      }
      
      /**
       * <p>
       * Prints population individuals
       * </p>
       * @param nFile           File to write the population
       * @param v               Vector which indicates if the individual if repeated
       */
      public void Print(String nFile, Vector v) {

          int marca;

          for(int i=0; i<num_indiv; i++) {
              marca = (Integer) v.get(i);
              if(marca!=1)
                indivi[i].Print(nFile);
          }
      }
      
}


