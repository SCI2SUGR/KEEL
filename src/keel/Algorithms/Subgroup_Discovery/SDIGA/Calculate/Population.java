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
 * @author Written by Pedro González (University of Jaen) 15/02/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDIGA.Calculate;

import org.core.Files;
import java.text.DecimalFormat;

public class Population {

    /**
     * <p>
     * Population of candidate rules
     * </p>
     */

      private Individual indivi [];    // Population individuals
      private int num_indiv;           // Number of individuals in the population
      private int Best_guy;            // Position of the better individual
      private float Best_current_perf; // Fitness of the better individual
      
      /**
       * <p>
       * Creates a new instance of Population
       * </p>
       * @param numind  Number of individuals in the population
       * @param numgen  Number of genes for the individuals
       * @param TVar    Contents the type of the variable, and the number of labels.
       */
      public Population(int numind, int numgen, TypeVar TVar[]) {
          indivi = new Individual[numind];
            num_indiv = numind;
            for(int i=0; i<numind; i++)
                indivi[i] = new Individual(numgen, TVar);
      }
      
      /**
       * <p>
       * Population initialisation (calling individual inicialisation)
       * </p>
       */
      public void initPopEmp () {
          for(int i=0; i<num_indiv; i++)
              indivi[i].InitIndEmp();  // Does not need the attributes of the variables
      }

      /**
       * <p>
       * Individual of the Population initialisation
       * </p>
       * @param pos     Position of the individual to initialise
       */
      public void initIndEmp (int pos) {
          indivi[pos].InitIndEmp();  // Does not need the attributes of the variables
      }
      
      
      
      /**
       * <p>
       * Gets the position of the best individual of the population
       * </p>
       * @return     The position of the best individual
       */
      public int getPopBestGuy() {
          return Best_guy;
      }
      
      /**
       * <p>
       * Sets the position of the best individual of the population
       * </p>
       * @param value       The position of the best individual
       */
      public void setPopBestGuy(int value) {
          Best_guy = value;
      }
      
      /**
       * <p>
       * Gets the fitness of the best individual of the population
       * </p>
       * @return        The value of fitness of the best individual
       */
      public float getPobBestPerf() {
          return Best_current_perf;
      }

      /**
       * <p>
       * Evaluates the population
       * </p>
       * @param nameFichQua       Output quality file
       */
      public void CalcPob (String nameFichQua) {
          Result res;
          int i; 
          String contents;
          float sumComp=0;
          float sumFSup=0;
          float sumCSup=0;
          float sumFConf=0;
          float sumCConf=0;
          float sumCov=0;
          float sumSign=0;
          float sumUnus=0;
          float sumNVar=0;
          float sumAccu=0;

          float medComp=0;
          float medFSup=0;
          float medCSup=0;
          float medFConf=0;
          float medCConf=0;
          float medCov=0;
          float medSign=0;
          float medUnus=0;
          float medNVar=0;
          float medAccu=0;

          DecimalFormat sixDecimals = new DecimalFormat("0.000000");
          DecimalFormat threeInts   = new DecimalFormat("000");

          contents = "Number \tClass \tSize \tNVar    \tCoverage \tSignificance \tUnusualness " +
                     "\tAccuracy \tSupport  \tCSupport  \tFSupport \tFConfidence \tCConfidence\n";

          int claseReglaAnterior = -1;
          
          for (i=0; i<StCalculate.NumReglasGeneradas; i++) {

              System.out.println ("\tProcessing rule #" + i);
              
              // If the class is not the same of the new rule, we put to 0 the count of covered examples
              if (indivi[i].getNumClass()!=claseReglaAnterior)
                  StCalculate.total_ej_cubiertos = 0;
              
              res = indivi[i].CalcInd (StCalculate.GI, indivi[i].getTotalClass());

              contents+= "" + threeInts.format(i) + "   ";
              contents+= "\t" + threeInts.format(indivi[i].getNumClass());
              contents+= "\t-";
              contents+= "\t" + sixDecimals.format(indivi[i].getNumVar());
              contents+= "\t" + sixDecimals.format(res.cov);
              contents+= "\t" + sixDecimals.format(res.sign);
              contents+= "\t" + sixDecimals.format(res.unus);
              contents+= "\t" + sixDecimals.format(res.accu);
              contents+= "\t" + sixDecimals.format(res.comp);
              contents+= "\t" + sixDecimals.format(res.csup);
              contents+= "\t" + sixDecimals.format(res.fsup);
              contents+= "\t" + sixDecimals.format(res.fconf);
              contents+= "\t" + sixDecimals.format(res.cconf);
              contents+= "\n";

              // Calculate the quality measures
              sumComp = sumComp + res.comp;
              sumFSup = sumFSup + res.fsup;
              sumCSup = sumCSup + res.csup;
              sumFConf = sumFConf + res.fconf;
              sumCConf = sumCConf + res.cconf;
              sumCov = sumCov + res.cov;
              sumSign = sumSign + res.sign;
              sumUnus = sumUnus + res.unus;
              sumNVar = sumNVar + indivi[i].getNumVar();
              sumAccu = sumAccu + res.accu;
              
              // Updates the value of the class of the last rule generated
              claseReglaAnterior = indivi[i].getNumClass();
             
              
          } 

         int numeroFCubiertos =0;
         int numeroCCubiertos =0;
         for (int w=0; w<StCalculate.n_eje; w++) {
             if (StCalculate.tabla[w].fcubierto) numeroFCubiertos++;
             if (StCalculate.tabla[w].ccubierto) numeroCCubiertos++;
         }

         medComp = sumComp/StCalculate.NumReglasGeneradas;
         medCSup = (float)numeroCCubiertos/StCalculate.n_eje;
         medFSup = sumFSup/StCalculate.NumReglasGeneradas;
         medCConf = sumCConf/StCalculate.NumReglasGeneradas;
         medFConf = sumFConf/StCalculate.NumReglasGeneradas;
         medCov = sumCov/StCalculate.NumReglasGeneradas;
         medSign = sumSign/StCalculate.NumReglasGeneradas;
         medUnus = sumUnus/StCalculate.NumReglasGeneradas;
         medNVar = sumNVar/StCalculate.NumReglasGeneradas;
         medAccu = sumAccu/StCalculate.NumReglasGeneradas;

         contents+= "-    \t- ";
         contents+= "\t" + threeInts.format(StCalculate.NumReglasGeneradas);
         contents+= "\t" + sixDecimals.format(medNVar);
         contents+= "\t" + sixDecimals.format(medCov);
         contents+= "\t" + sixDecimals.format(medSign);
         contents+= "\t" + sixDecimals.format(medUnus);
         contents+= "\t" + sixDecimals.format(medAccu);
         contents+= "\t" + sixDecimals.format(medComp);
         contents+= "\t" + sixDecimals.format(medCSup);
         contents+= "\t" + sixDecimals.format(medFSup);
         contents+= "\t" + sixDecimals.format (medFConf);
         contents+= "\t" + sixDecimals.format (medCConf);
         contents+= "\n";
         
         Files.writeFile(nameFichQua, contents);
      }
      
      /**
       * <p>
       * Evaluates the population to obtain the output files of training and test for classification
       * </p>
       * @param nameFileOutputTra       Output quality file
       */
      public void CalcPobOutput (String nameFileOutputTra) {

        float pertenencia, pert;
        Chromosome chrome;
        float disparoFuzzy = 1;
        float success = 0;
        float error = 0;
        int num_var_no_interv = 0;
        float tp = 0;
        float fp = 0;
        float[] compatibility;
        float[] normsum;
        String contents;

        compatibility = new float[StCalculate.NumReglasGeneradas];
        normsum = new float[StCalculate.n_clases];
        int minclass = 0;
        if(Utils.ExamplesClass(0)>Utils.ExamplesClass(1))
            minclass = 1;

        for(int i=0; i<StCalculate.n_eje; i++){
            for (int j=0; j<StCalculate.NumReglasGeneradas; j++){
                disparoFuzzy = 1;
                chrome = indivi[j].getIndivCrom();
                for (int k=0; k<StCalculate.num_vars; k++){

                    if (!StCalculate.var[k].continua) {  /* Discrete Variable */
                        if (chrome.getCromElem(k,StCalculate.var[k].n_etiq)==1){
                            // Variable j takes part in the rule
                            if ((chrome.getCromElem(k,(int)StCalculate.tabla[i].ejemplo[k])==0) && (!Calculate.getLost(i,k))) {
                                disparoFuzzy = 0;
                            }
                        }
                        else
                            num_var_no_interv++;  // Variable does not take part
                    }
                    else {
                        // Continuous variable
                        if (chrome.getCromElem(k, StCalculate.var[k].n_etiq)==1){
                            // Variable takes part in the rule
                            // Fuzzy computation
                            if (!Calculate.getLost(i,k)) {
                                pertenencia = 0;
                                for (int l=0; l<StCalculate.var[k].n_etiq; l++) {
                                    if (chrome.getCromElem(k,l)==1)
                                        pert = StCalculate.BaseDatos[k][l].Fuzzy(StCalculate.tabla[i].ejemplo[k]);
                                    else pert = 0;
                                    pertenencia = Utils.Maximum (pertenencia, pert);
                                }
                                disparoFuzzy = Utils.Minimum (disparoFuzzy, pertenencia);
                            }
                        }
                        else
                            num_var_no_interv++;  // Variable does not take part
                    }
                }
                // Update globals counters
                compatibility[j] = disparoFuzzy;
            }

            for(int j=0; j<StCalculate.n_clases; j++)
                normsum[j]=0;

            for(int j=0; j<StCalculate.NumReglasGeneradas; j++){
                normsum[indivi[j].getNumClass()] += compatibility[j];
            }

            float maximum = 0;
            int pos = -1;
            for(int j=0; j<StCalculate.n_clases; j++){
                if(normsum[j]>=maximum){
                    maximum = normsum[j];
                    pos = j;
                }
            }

            contents = StCalculate.name_class[StCalculate.tabla[i].clase]+" "+StCalculate.name_class[pos]+"\n";
            Files.addToFile(nameFileOutputTra, contents);
        }

      }

      
      /**
       * <p>
       * Returns the number of variables of the indicated individual (including the consequent)
       * </p>
       * @param num_indiv       Position of the individual
       * @return                Number of variables of the individual
       */
      public int getIndivNvar (int num_indiv) {
          return indivi[num_indiv].getNumVar();
      }

     /**
      * <p>
      * Sets the number of variables of the indicated individual
      * </p>
      * @param num_indiv        Position of the individual
      * @param val              Number of variables
      */
      public void setIndivNvar (int num_indiv, int val) {
          indivi[num_indiv].setNumVar (val);
      }
      
           
      
      /**
       * <p>
       * Returns the indicated gene of the Chromosome
       * </p>
       * @param num_indiv       Position of the individual
       * @param pos             Position of the variable of the individual
       * @param elem            Position of the gen of the variable
       * @return                The gene indicated
       */
      public int getCromElem (int num_indiv, int pos, int elem) {
          return indivi[num_indiv].getCromElem (pos, elem);
      }

      /**
       * <p>
       * Sets the value of the indicated gene of the Chromosome
       * @param num_indiv       Position of the individual
       * @param pos             Position of the variable of the individual
       * @param elem            Position of the gen of the variable
       * @param val             Value of the gene indicated
       * </p>
       */
      public void setCromElem (int num_indiv, int pos, int elem, int val) {
          indivi[num_indiv].setCromElem (pos, elem, val);
      }
      
      /**
       * <p>
       * Returns fitness of the indicated inidividual of the population
       * </p>
       * @param num_indiv      Position of the individual
       * @return                Fitness of the individual
       */
      public float getIndivPerf (int num_indiv) {
          return indivi[num_indiv].getIndivPerf ();
      }

      /**
       * <p>
       * Sets the value of fitnes for the the indicated individual
       * </p>
       * @param num_indiv       Position of the individual
       * @param val             Fitness of the individual
       */
      public void setIndivPerf (int num_indiv, float val) {
          indivi[num_indiv].setIndivPerf (val);
      }
      
      /**
       * <p>
       * Returns if the individual of the population has been evaluated
       * </p>
       * @param num_indiv       Position of the individual
       * @return                State of the individual
       */
      public boolean getIndivEvaluated (int num_indiv) {
          return indivi[num_indiv].getIndivEvaluated ();
      }

      /**
       * <p>
       * Sets the value for de evaluated attribute of the individual
       * </p>
       * @param num_indiv       Position of the individual
       * @param val             Value of the state of the individual
       */
      public void setIndivEvaluated (int num_indiv, boolean val) {
          indivi[num_indiv].setIndivEvaluated (val);
      }

      /**
       * <p>
       * Returns the class number of the individual of the population
       * </p>
       * @param num_indiv       Position of the individual
       * @return                Number of class of the individual
       */
      public int getIndivNumClass (int num_indiv) {
          return indivi[num_indiv].getNumClass ();
      }

      /**
       * <p>
       * Sets the value for the number of the class of the individual
       * </p>
       * @param num_indiv       Position of the individual
       * @param val             Number of the class of the individual
       */
      public void setIndivNumClass (int num_indiv, int val) {
          indivi[num_indiv].setNumClass (val);
      }

      /**
       * <p>
       * Returns the class name of the individual of the population
       * </p>
       * @param num_indiv       Position of the individual
       * @return                The name of the class
       */
      public String getIndivNameClass (int num_indiv) {
          return indivi[num_indiv].getNameClass ();
      }

      /**
       * <p>
       * Sets the class name of the class of the individual
       * </p>
       * @param num_indiv       Position of the individual
       * @param val             Name of the class
       */
      public void setIndivNameClass (int num_indiv, String val) {
          indivi[num_indiv].setNameClass (val);
      }
      
      /**
       * <p>
       * Returns the number of examples of the DB belonging to the class of the individual
       * </p>
       * @param num_indiv       Position of the individual
       * @return                Number of total examples
       */
      public int getIndivTotalClass (int num_indiv) {
          return indivi[num_indiv].getTotalClass ();
      }

      /**
       * <p>
       * Sets the number of examples of the DB belonging to the class of the individual
       * </p>
       * @param num_indiv       Position of the individual
       * @param val             Number of total examples
       */
      public void setIndivTotalClass (int num_indiv, int val) {
          indivi[num_indiv].setTotalClass (val);
      }

            
      /**
       * <p>
       * Returns de hole cromosoma of the selected individual
       * </p>
       * @param num_indiv       Position of the individual
       * @return                The Chromosome of the individual
       */
      public Chromosome getIndivCrom (int num_indiv) {
          return indivi[num_indiv].getIndivCrom();
      }

      
    /**
     * <p>
     * Method to Compare two individuals of the population
     * </p>
     * @param primero       Position of an individual
     * @param segundo       Position of an individual
     * @return              The result of the comparison
     */
    public boolean Compare (int primero, int segundo) {
        if (indivi[primero].getNumClass() != indivi[segundo].getNumClass())
            return false;
        else {
            for (int i=0; i<indivi[primero].getIndivSize(); i++) {
                if (!indivi[primero].EqualTo(indivi[segundo]))
                    return false;
            }
        }
        return true;
    }
     
      /**
       * <p>
       * Prints population individuals
       * </p>
       * @param nFile       File to write the population
       */
      public void Print(String nFile) {
          Files.addToFile(nFile, "Population\n");
          for(int i=0; i<num_indiv; i++) {
              Files.addToFile(nFile, "Individuo " + i + ": ");
              indivi[i].Print(nFile);
          }
          Files.addToFile(nFile, "\n");
      }
  
}
