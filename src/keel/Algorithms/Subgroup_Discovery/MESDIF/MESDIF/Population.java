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
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 30/06/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.MESDIF.MESDIF;

import org.core.*;

public class Population {
    /**
     * <p>
     * Population of candidate rules
     * </p>
     */

    private Individual indivi [];    // Population individuals
    private int num_indiv;           // Max number of individuals in the population
    public int num_used;             // Number or individuals really used

    private int strength[];          // To store the strength of each individual in the pop
    private int raw[];               // To store the raw fitness of each individual
    private float density[];	     // To store the density of each individual


      
      /**
       * <p>
       * Creates a population of Individual
       * </p>
       * @param AG                  Instance of the genetic algorithm
       * @param numind          Number of individuals
       * @param numgen          Number of variables
       * @param RulesRep        Rules representation
       * @param Variables       Variables structure
       * @param numExamples     Number of examples of the dataset
       * @param numObjectives   Number of objectives used
       */
      public Population(Genetic AG, int numind, int numgen, String RulesRep, TableVar Variables, int numExamples, int numObjectives) {
          indivi = new Individual[numind];
            num_indiv = numind;
            num_used =0;

            for(int i=0; i<numind; i++){
                if(RulesRep.compareTo("CAN")==0)
                    indivi[i] = new IndCAN(AG, numgen, numExamples,numObjectives);
                else
                    indivi[i] = new IndDNF(AG, numgen,Variables, numExamples, numObjectives);
            }
            strength = new int[numind];
            raw = new int[numind];
            density = new float[numind];
      }


      /**
       * <p>
       * Random population initialization
       * </p>
       * @param Variables       Variables structure
       */
      public void RndInitPop (TableVar Variables) {
          for(int i=0; i<num_indiv; i++)
              indivi[i].InitIndRnd(Variables);
      }


      /**
       * <p>
       * Biased random population initialization
       * </p>
       * @param Variables       Variables structure
       * @param porcVar         Percentage of max number of variables in the individual
       * @param porcPob          Percentage of max number of individuals to apply this initialization
       */
      public void BsdInitPop (TableVar Variables, float porcVar, float porcPob) {
          int i, j;
          float parteSesg = porcPob * num_indiv;

          /* Biased part */
          for(i=0; i<parteSesg; i++)
              indivi[i].InitIndBsd(Variables, porcVar);

          /* Random part */
          for(j=i; j<num_indiv; j++)
              indivi[j].InitIndRnd(Variables);  /* Needs the attributes of the variables */
      }



      /**
       * <p>
       * Evaluates the non-evaluates individuals of the population
       * </p>
       * Evaluates individuals from 0 to this.num_used (not all the individuals)
       * and computes "original support"
       *
       * @param number          Evaluate first "number" individuals
       * @param Variables       Variables structure
       * @param Examples        Examples structure
       * @return                Number of evalutions
       */
      public int evalPop (Genetic AG, int number, TableVar Variables, TableDat Examples) {
          int numero_eval=0;                  // Number of individuals evaluated
          
          // For each individual in the population
          for (int i=0; i<number; i++) {
              if (!getIndivEvaluated(i)) {     /* Not evaluated */
                  /* Individual evaluation to set the quality measures (except of fitness) */
                  // Only nonevaluated individuals are evaluated
                  indivi[i].evalInd (AG, Variables, Examples);
                  setIndivEvaluated(i,true);   /* Now it is evaluated */
                  numero_eval++;  // Increments the number of individuals evaluated
              }
          } // End for each individual
          
          
          // Compute the original support (according to the examples cobered by each rule)

          // First: conunt how many rules cover each example
          int[] reglascubren = new int[Examples.getNEx()];
          for (int aa=0; aa<Examples.getNEx(); aa++)
              reglascubren[aa]=0;
          for (int aa=0; aa<number; aa++) // For each rule
              for (int bb=0; bb<Examples.getNEx(); bb++) // For each example
                  if (indivi[aa].getIndivCubre(bb))
                  {
                      reglascubren[bb]++;
                  }

          // Second: Compute the value of original support for each rule
          for (int i=0; i<number; i++) {
              float valor=0;
              for (int j=0; j<Examples.getNEx(); j++) {
                  if (indivi[i].getIndivCubre(j)==true)
                      valor += 1.0/reglascubren[j];
              }
              // Store the value
              indivi[i].setIndivOSup (valor);

          }

          return numero_eval;
      }


      /**
       * <p>
       * Evaluates an individual of the population
       * </p>
       * @param pos             Position of the individual
       * @param Variables       Variables structure
       * @param Examples        Examples structure
       */
      public void evalIndiv (int pos, Genetic AG, TableVar Variables, TableDat Examples) {
          indivi[pos].evalInd (AG, Variables, Examples);
      }
      
      
      /**
       * <p>
       * Get the measures of a single rule
       * </p>
       * @param AG                  Instance of the genetic algorithm
       * @param pos             Position of the individual
       * @param nFile           File to write the measures
       */
      public QualityMeasures getMedidas (Genetic AG, int pos, String nFile) {
          if (!getIndivEvaluated(pos)) {
              String contents = "\n\nWarning: The individual " + pos + " is not evaluated and the quality measure values are ficticious\n";
              System.out.println (contents);
              Files.addToFile(nFile, contents);
          }
          return (indivi[pos].getMedidas(AG));
      }


      /**
       * Returns the indicated individual of the population
       *
       * @param pos         Number of the individual
       * @return Individuo  Individual indicated by pos
       */
      public Individual getIndiv (int pos) {
          return indivi[pos];
      }

      /**
       * Copy the individual otro into the individual pos of this population
       *
       * @param pos         Number of the individual of "indivi" where to copy individual "otro"
       * @param otro        Individual to be copied
       */
      public void copyIndiv (int pos, Individual otro) {
          indivi[pos].copyIndiv(otro);
      }


      /**
       * Eliminates de duplicated individuals of the population
       *     from the first individual to number max (not to the total number of individuals)
       * @return pos: Number of individuals left in the population
       */
      public int delDup (int max) {
          boolean rep;
          int pos;
          // first individual (0) is never duplicated so far
          pos=1;
          for(int i=1; i<max; i++) {
              rep = false;
              for (int j=0; j<pos; j++) {
                  if (indivi[i].equalTo(indivi[j])) {
                      rep = true;
                      break;
                  }
              }
              if (!rep) {
                  indivi[pos].copyIndiv(indivi[i]);
                  pos++;
              }
          }
          return pos;
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
       * Gets if the individual in the position is or not dominated
       * </p>
       * @param num_indiv               Number of individual
       * @return                   The individual is dominated or not
       */
      public boolean getIndivDom (int num_indiv) {
          return indivi[num_indiv].getIndivDom ();
      }

      /**
       * <p>
       * Sets the individual to dominated or not dominated
       * </p>
       * @param num_indiv               Number of individual
       * @param dominated               Dominated or not
       */
      public void setIndivDom (int num_indiv, boolean dominated) {
          indivi[num_indiv].setIndivDom (dominated);
      }


      /**
       * <p>
       * Gets the strenght of the individual indicated
       * </p>
       * @param pos                Number of individual
       * @return                   Individual Strength
       */
      public int getIndivStrength (int pos) {
          return strength[pos];
      }

      /**
       * <p>
       * Sets the individual strength
       * </p>
       * @param pos               Number of the individual
       * @param value              Stregth value
       */
      public void setIndivStrength (int pos, int value) {
          strength[pos] = value;
      }


      /**
       * <p>
       * Gets the raw fitness of the individual indicated
       * </p>
       * @param pos                Number of individual
       * @return                   Individual Raw fitness
       */
      public int getIndivRawFit (int pos) {
          return raw[pos];
      }

      /**
       * <p>
       * Sets the individual raw fitness
       * </p>
       * @param pos               Number of the individual
       * @param value              Raw fitness value
       */
      public void setIndivRawFit (int pos, int value) {
          raw[pos] = value;
      }


      /**
       * <p>
       * Gets the density of the individual indicated
       * </p>
       * @param pos                Number of individual
       * @return                   Individual density
       */
      public float getIndivDensity (int pos) {
          return density[pos];
      }

      /**
       * <p>
       * Sets the individual density
       * </p>
       * @param pos               Number of the individual
       * @param value              Density value
       */
      public void setIndivDensity (int pos, float value) {
          density[pos] = value;
      }



    /**
     * <p>
     * Computes the fitness of the individuals in the population, until number max
     * <p>
     *    First we compute strength, then rawFitness, distances, density and finally fitness
     * All the individuals are suposed to have been evaluated
     *    We don't compute for all the population but the number inicated as a parameter
     *
     * @param max         number of individuals in the population to be computed
     * @param num_obj     number of objectives
     **/
    public void CalcFitness (int max, int num_obj) {
        int i,j, rawFit, num_domina, num_dominado, kTH;
        float distances[][] = new float[max][max];
        float kthDistance, vol, sumKthDistance;
        float MAXDOUBLE = 99999999;

        sumKthDistance = 0;

        // Calculate strength of all the individuals in population and elite
        for (i=0; i<max; i++) {
            this.setIndivDom (i, false);
            num_domina=num_dominado=0;
            for (j=0; j<max; j++) {
		if (this.getIndiv(i).dominate(this.getIndiv(j)))
		    num_domina++;
		if (this.getIndiv(i).dominated(this.getIndiv(j)))
		    num_dominado++;
            }
            // Compute whether the individual is dominated saving its strength
            if (num_dominado>0)
                this.setIndivDom (i, true);
            this.setIndivStrength (i,num_domina);
        }

        // For each element, compute rawfit, density and fitness
        for (i=0; i<max; i++) {
            rawFit=0;
            kthDistance = 0;

            for (j=0; j<max; j++) {

                // Rawfitness
                if (this.getIndiv(j).dominate(this.getIndiv(i)))
                    rawFit+= this.getIndivStrength(j);

                // Compute distances
                if (i==j)
                    distances[i][j]=0;
                else if (j >i) {
                    distances[i][j] = this.getIndiv(i).calcDist(this.getIndiv(j));
                    distances[j][i] = distances[i][j];
                }
            }
            
            // Store rawFitness
            this.setIndivRawFit (i,rawFit);

            // Compute density

            // compute k value
            kTH = (int)Math.sqrt(max-1);
            vol = volSphere(num_obj);

            // calc k-th nearest neighbor distance
            double dist = 0;
            int index = -1;

            for(int k=0;k<max;k++) {
                dist = MAXDOUBLE;
                for(int l=0;l<max;l++) {
                    if (distances[i][l]<dist) {
                        index = l; //index of current jth NN
                        dist = distances[i][l];
                        if ( dist == 0 ) { break; }
                    }
                }
                distances[i][index] = distances[i][k];
                if ( dist > 0 && k >= kTH ) { break; }
            }

            if (dist==0)  // exception: only possible when all are equal
                dist = 1;

            kthDistance = 1 / (float)Math.pow(dist,num_obj) * kTH / max / vol;
            sumKthDistance += kthDistance;

            // Set SPEA2 k-th NN distance value for each individual
            this.setIndivDensity (i,kthDistance);

        }

        // Normalize the kthDistance value
        for(i=0; i<max;i++) {
            this.setIndivDensity (i, this.getIndivDensity(i)/sumKthDistance);
            // Set SPEA2 fitness value for each individual
            this.setIndivFitness (i, this.getIndivRawFit (i) + this.getIndivDensity (i));

        }

    }


    /**
     * <p>
     * VolSphere
     * <p>
     * volSphere
     * volSphere calculates the area of a sphere in n dimensions for use in the
     *        SPEA2 density calculation
     *
     * @param dimensions        Number of objectives
     * @return                  Vol
     **/
    public float volSphere(int dimensions) {
        double PI = 3.14159;
        float vol = 1;
        if ( dimensions % 2 == 0 ) {
            for ( int i=1; i<=dimensions/2; i++ )
                vol *= i;
            vol = (float)Math.pow(PI,dimensions/2)/vol;
        }
        else {
            for ( int i=(dimensions-1)/2+1; i<=dimensions; i++ )
                vol *= i;
            vol = (float)Math.pow(2,dimensions) * (float)Math.pow(PI,(dimensions-1)/2) * vol;
        }
        return vol;
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
