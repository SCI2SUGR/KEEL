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

import org.core.*;
import java.util.*;

public class Genetic {
    /**
     * <p>
     * Methods to define the genetic algorithm and to apply operators and reproduction schema
     * </p>
     */

    private Population poblac;     // Main Population
    
    private Population offspring;  // Offspring population
    private Population union;      // Main+Offspring populations
    
    private int num_objetivos;    // Number of objective of the algorithm
    private String[] n_objetivos; // Name of the number of objective
    private int long_poblacion;   // Number of individuals of the population
    private int n_eval;           // Number of evaluations per ejecution
    private float prob_cruce;     // Cross probability
    private float prob_mutacion;  // Mutation probability
    private int Gen;		  // Number of generations performed by the GA
    private int Trials;		  // Number of evaluated chromosomes

    private String RulesRep = "CAN";
    private String StrictDominance = "no";

    private String ReInitCob = "no";    // Re-initialization based on coverage for the diversity in the model
    private float porcCob = 1;          // Biased initialization for individuals in ReInitCob
    private float minCnf = 0;
    private String diversity;

    private int long_lambda;            // Utility param
    private double lambda[][];          // Utility param


    /**
     * <p>
     * Sets the number of objectives
     * </p>
     * @param nobj          Number of objectives
     */
    public void setNumObjectives(int nobj){
        num_objetivos = nobj;
    }


    /**
     * <p>
     * Gets the number of objectives
     * </p>
     * @return          Number of objectives
     */
    public int getNumObjectives(){
        return num_objetivos;
    }

    /**
     * <p>
     * Sets the name of an objective
     * </p>
     * @param pos           Position of the objective
     * @param value         Name of the objective
     */
    public void setNObjectives(int pos, String value){
        n_objetivos[pos] = value;
    }

    /**
     * <p>
     * Initialises the structure for the name of the objectives
     * </p>
     */

    public void iniNObjectives(){
        n_objetivos = new String[num_objetivos];
    }

    /**
     * <p>
     * Gets the name of the objective
     * </p>
     * @param pos           Position of the objective
     * @return              Name of the objective
     */
    public String getNObjectives(int pos){
        return n_objetivos[pos];
    }

    /**
     * <p>
     * Sets the lenght of the population
     * </p>
     * @param value             Lenght of the population
     */
    public void setLengthPopulation (int value){
        long_poblacion = value;
    }

    /**
     * <p>
     * Gets the lenght of the population
     * </p>
     * @return                  Lenght of the population
     */
    public int getLengthPopulation (){
        return long_poblacion;
    }

    /**
     * <p>
     * Sets the number of evaluations of the algorithm
     * </p>
     * @param value             Number of evaluations
     */
    public void setNEval (int value){
        n_eval = value;
    }


    /**
     * <p>
     * Gets the number of evalutions of the algorithms
     * </p>
     * @return                  Number of evaluations
     */
    public int getNEval (){
        return n_eval;
    }

    /**
     * <p>
     * Sets the cross probability in the algorithm
     * </p>
     * @param value             Cross probability
     */
    public void setProbCross (float value){
        prob_cruce = value;
    }


    /**
     * <p>
     * Gets the cross probability
     * </p>
     * @return          Cross probability
     */
    public float getProbCross (){
        return prob_cruce;
    }

    /**
     * <p>
     * Sets the mutation probability
     * </p>
     * @param value             Mutation probability
     */
    public void setProbMutation (float value){
        prob_mutacion = value;
    }


    /**
     * <p>
     * Gets the mutation probability
     * </p>
     * @return                  Mutation probability
     */
    public float getProbMutation (){
        return prob_mutacion;
    }

    /**
     * <p>
     * Sets the value of a gene
     * </p>
     * @param value             Value of the gene
     */
    public void setGen(int value){
        Gen = value;
    }


    /**
     * <p>
     * Gets the value of a gene
     * </p>
     * @return                  Value of the gene
     */
    public int getGen(){
        return Gen;
    }

    /**
     * <p>
     * Sets the number of trials in the algorithm
     * </p>
     * @param value             Number of trials
     */
    public void setTrials(int value){
        Trials = value;
    }


    /**
     * <p>
     * Gets the number of trials in the algorithm
     * </p>
     * @return                  Number of trials
     */
    public int getTrials(){
        return Trials;
    }

    /**
     * <p>
     * Gets the type of diversity of the algorithm
     * </p>
     * @return              Type of diversity
     */
    public String getDiversity(){
        return diversity;
    }

    /**
     * <p>
     * Sets the type of diversity of the algorithm
     * </p>
     * @param value         Type of diversity
     */

    public void setDiversity(String value){
        diversity = value;
    }

    /**
     * <p>
     * Gets if the algorithm uses re-initialisation based on coverage
     * </p>
     * @return              The uses of re-initialisation based on coverage
     */
    public String getReInitCob(){
        return ReInitCob;
    }


    /**
     * <p>
     * Sets the value of re-initialisation based on coverage
     * </p>
     * @param value         Value of the re-inisitalisation based on coverage
     */
    public void setReInitCob(String value){
        ReInitCob = value;
    }


    /**
     * <p>
     * Gets the percentage of biased initialisation in the re-initialisation based on coverage
     * </p>
     * @return              Percentage of biases
     */
    public float getPorcCob(){
        return porcCob;
    }


    /**
     * <p>
     * Sets the percentage of biased initialisation in the re-initialisation based on coverage
     * </p>
     * @param value         Value of the percentage
     */
    public void setPorcCob(float value){
        porcCob = value;
    }


    /**
     * <p>
     * Gets the minimum confidence
     * </p>
     * @return              Minimum confidence
     */
    public float getMinCnf(){
        return minCnf;
    }


    /**
     * <p>
     * Sets the minimum confidence
     * </p>
     * @param value             Minimum confidence
     */
    public void setMinCnf(float value){
        minCnf = value;
    }


    /**
     * <p>
     * Gets the rules representation of the algorithm
     * </p>
     * @return                  Representation of the rules
     */
    public String getRulesRep(){
        return RulesRep;
    }


    /**
     * <p>
     * Sets the rules representation of the algorithm
     * </p>
     * @param value             Representation of the rule
     */
    public void setRulesRep(String value){
        RulesRep = value;
    }

    /**
     * <p>
     * Gets if the algorithm considers strict dominance
     * </p>
     * @return                  The value of strict dominance
     */
    public String getStrictDominance(){
        return StrictDominance;
    }


    /**
     * <p>
     * Sets if the algorithm considers strict dominance
     * </p>
     * @param value             The value of strict dominance
     */
    public void setStrictDominance(String value){
        StrictDominance = value;
    }

    /**
     * <p>
     * Joins two populations
     * </p>
     * @param neje              Number of examples
     */
    public void JoinTemp(int neje) {
        int i,j,k;

        for (i=0; i<long_poblacion; i++){
            union.CopyIndiv(i, neje, num_objetivos, poblac.getIndiv(i));
        }
        j = 0;
        for (i=long_poblacion; i<(long_poblacion*2); i++){
            union.CopyIndiv(i, neje, num_objetivos, offspring.getIndiv(j));
            j++;
        }

    }
    
    
    /**
     * <p>
     * Applies the selection schema of the genetic algorithm.
     * Binary tournament selection from elite to inter
     * </p>
     * @return              Position of the individual selected
     */
    public int Select() {
        int winner;

        int opponent1 = Randomize.Randint(0,long_poblacion-1);
        int opponent2 = opponent1;
        while ((opponent2 == opponent1) && (poblac.getNumIndiv()>1)){
            opponent2 = Randomize.Randint(0,long_poblacion-1);
        }

        winner = opponent1;

        if (poblac.getIndiv(opponent2).getRank() < poblac.getIndiv(opponent1).getRank())
            winner = opponent2;
        else if (poblac.getIndiv(opponent2).getRank() > poblac.getIndiv(opponent1).getRank())
            winner = opponent1;
        else {
            if (poblac.getIndiv(opponent2).getCrowdingDistance() > poblac.getIndiv(opponent1).getCrowdingDistance())
                winner = opponent2;
            else if (poblac.getIndiv(opponent2).getCrowdingDistance() <= poblac.getIndiv(opponent1).getCrowdingDistance())
                winner = opponent1;
        }
        return winner;
      
    }
   

    /**
     * <p>
     * Cross operator for the genetic algorithm
     * </p>
     * @param Variables         Variables structure
     * @param dad               Position of the daddy
     * @param mom               Position of the mummy
     * @param contador          Position to insert the son
     * @param neje              Number of examples
    */
    public void CrossMultipoint (TableVar Variables, int dad, int mom, int contador, int neje) {
        
        int i, xpoint1, xpoint2;
        double cruce;

        // Copy the individuals to cross
        for (i=0; i<Variables.getNVars(); i++) {
            if(RulesRep.compareTo("CAN")==0){
                offspring.setCromElem ((contador*2), i, 0, poblac.getCromElem(mom,i,0,RulesRep), RulesRep);
                offspring.setCromElem ((contador*2)+1, i, 0, poblac.getCromElem(dad,i,0,RulesRep), RulesRep);
            } else {
                int number = offspring.getIndivCromDNF(contador*2).getCromGeneLenght(i);
                for(int ii=0; ii<=number; ii++){
                    offspring.setCromElem((contador*2), i, ii, poblac.getCromElem(mom, i, ii, RulesRep), RulesRep);
                    offspring.setCromElem((contador*2)+1, i, ii, poblac.getCromElem(dad, i, ii, RulesRep), RulesRep);
                }
            }
        }

        cruce = Randomize.Randdouble(0.0,1.0);
        
        if (cruce <= getProbCross()){
            // Generation of the two point of cross
            xpoint1 = Randomize.Randint (0,(Variables.getNVars()-1));
            if (xpoint1!=Variables.getNVars()-1)
                xpoint2 = Randomize.Randint ((xpoint1+1),(Variables.getNVars()-1));
            else
                xpoint2 = Variables.getNVars()-1;

            // Cross the parts between both points
            for (i=xpoint1;i<=xpoint2;i++) {
                if(RulesRep.compareTo("CAN")==0){
                    offspring.setCromElem ((contador*2), i, 0, poblac.getCromElem(dad,i,0,RulesRep), RulesRep);
                    offspring.setCromElem ((contador*2)+1, i, 0, poblac.getCromElem(mom,i,0,RulesRep), RulesRep);
                } else {
                    int number = offspring.getIndivCromDNF(contador*2).getCromGeneLenght(i);
                    for(int ii=0; ii<=number; ii++){
                        offspring.setCromElem((contador*2), i, ii, poblac.getCromElem(dad, i, ii, RulesRep), RulesRep);
                        offspring.setCromElem((contador*2)+1, i, ii, poblac.getCromElem(mom, i, ii, RulesRep), RulesRep);
                    }
                    int aux1 = 0;
                    int aux2 = 0;
                    for(int ii=0; ii<number; ii++){
                        if(offspring.getCromElem((contador*2), i, ii, RulesRep)==1) aux1++;
                        if(offspring.getCromElem((contador*2)+1, i, ii, RulesRep)==1) aux2++;
                    }
                    if((aux1==number)||(aux1==0)) offspring.setCromElem((contador*2), i, number, 0, RulesRep);
                    else offspring.setCromElem((contador*2), i, number, 1, RulesRep);
                    if((aux2==number)||(aux2==0)) offspring.setCromElem((contador*2)+1, i, number, 0, RulesRep);
                    else offspring.setCromElem((contador*2)+1, i, number, 1, RulesRep);
                }
            }
        } else {
            offspring.CopyIndiv(contador*2, neje, num_objetivos, poblac.getIndiv(dad));
            offspring.CopyIndiv((contador*2)+1, neje, num_objetivos, poblac.getIndiv(mom));
        }
        
    }

    
    /**
     * <p>
     * Mutates an individual
     * </p>
     * @param Variables             Variables structure
     * @param pos                   Position of the individual to mutate
     */
    public void Mutation (TableVar Variables, int pos) {
        
        double mutar;
        int posiciones, eliminar;
        
        posiciones = Variables.getNVars();
        
        if (getProbMutation() > 0) {
            for(int i=0; i<posiciones; i++){
                mutar = Randomize.Randdouble(0.00,1.00);
                if(mutar <= getProbMutation()){
                    eliminar = Randomize.Randint (0,10);
                    if (eliminar <=5){
                        if (!Variables.getContinuous(i))
                            if(RulesRep.compareTo("CAN")==0){
                                offspring.setCromElem (pos, i, 0, (int)Variables.getMax(i)+1, RulesRep);
                            } else {
                                int number = Variables.getNLabelVar(i);
                                for(int l=0; l<=number; l++){
                                    offspring.setCromElem(pos, i, l, 0, RulesRep);
                                }
                            }
                        else
                            if(RulesRep.compareTo("CAN")==0){
                                offspring.setCromElem(pos, i, 0, Variables.getNLabelVar(i), RulesRep);
                            } else {
                                int number = Variables.getNLabelVar(i);
                                for(int l=0; l<=number; l++){
                                    offspring.setCromElem(pos, i, l, 0, RulesRep);
                                }
                            }
                    } else {
                        if (!Variables.getContinuous(i))
                            if(RulesRep.compareTo("CAN")==0){
                                offspring.setCromElem(pos, i, 0, Randomize.Randint(0, (int)Variables.getMax(i)), RulesRep);
                            } else {
                                int number = Variables.getNLabelVar(i);
                                int cambio = Randomize.Randint (0,number-1);
                                if(offspring.getCromElem(pos, i, cambio, RulesRep)==0){
                                    offspring.setCromElem(pos, i, cambio, 1, RulesRep);
                                    int aux1 = 0;
                                    for(int ii=0; ii<number; ii++){
                                        if(offspring.getCromElem(pos, i, ii, RulesRep)==1) aux1++;
                                    }
                                    if((aux1==number)||(aux1==0)) offspring.setCromElem(pos, i, number, 0, RulesRep);
                                    else offspring.setCromElem(pos, i, number, 1, RulesRep);
                                } else {
                                    for(int k=0; k<=number; k++)
                                        offspring.setCromElem(pos, i, k, 0, RulesRep);
                                }
                            }
                        else
                            if(RulesRep.compareTo("CAN")==0){
                                offspring.setCromElem(pos, i, 0, Randomize.Randint(0,Variables.getNLabelVar(i)-1), RulesRep);
                            } else {
                                int number = Variables.getNLabelVar(i);
                                int cambio = Randomize.Randint (0,number-1);
                                if(offspring.getCromElem(pos, i, cambio, RulesRep)==0){
                                    offspring.setCromElem(pos, i, cambio, 1, RulesRep);
                                    int aux1 = 0;
                                    for(int ii=0; ii<number; ii++){
                                        if(offspring.getCromElem(pos, i, ii, RulesRep)==1) aux1++;
                                    }
                                    if((aux1==number)||(aux1==0)) offspring.setCromElem(pos, i, number, 0, RulesRep);
                                    else offspring.setCromElem(pos, i, number, 1, RulesRep);
                                } else {
                                    for(int k=0; k<=number; k++)
                                        offspring.setCromElem(pos, i, k, 0, RulesRep);
                                }
                            }
                    }

                    // Marks the chromosome as not evaluated
                    offspring.setIndivEvaluated(pos,false);
                }
            }
        }
            
    }

    
    /**
     * <p>
     * Composes the genetic algorithm applying the operators
     * </p>
     * @param Variables         Variables structure
     * @param Examples          Examples structure
     * @param nFile             Fichero to write the process
     * @return                  Final Pareto population
     */
    public Population GeneticAlgorithm (TableVar Variables, TableDat Examples, String nFile) {

        String contents;
        float porcVar = (float) 0.25;
        float porcPob = (float) 0.75;
        
        poblac = new Population(long_poblacion, Variables.getNVars(), num_objetivos, Examples.getNEx(), RulesRep, Variables);
        poblac.BsdInitPob(Variables, porcVar, porcPob, Examples.getNEx(), nFile);
        
        Trials = 0;
        Gen = 0;

        if(getDiversity().compareTo("Utility")==0){
            long_lambda = long_poblacion;
            lambda = new double[long_lambda][num_objetivos];
            lambda[0][0] = 0.0;
            lambda[0][1] = 1.0;
            for(int i=1; i<long_lambda; i++){
                lambda[i][0] = lambda[i-1][0]+(1.0 / (long_lambda-1));
                lambda[i][1] = 1.0 - lambda[i][0];
            }
        }

        //Evaluates the population
        Trials += poblac.evalPop(this, Variables, Examples);
          
        do { // GA General cycle

            Gen++;
            // Creates offspring and union
            offspring = new Population(long_poblacion, Variables.getNVars(), num_objetivos, Examples.getNEx(), RulesRep, Variables);
            union = new Population(2*long_poblacion, Variables.getNVars(), num_objetivos, Examples.getNEx(), RulesRep, Variables);

            for(int conta=0; conta < long_poblacion/2; conta++){

                // Select the daddy and mummy
                int dad = Select();
                int mum = Select();
                while ((dad == mum) && (poblac.getNumIndiv()>1)){
                    mum = Select();
                }            
                
                // Crosses
                CrossMultipoint(Variables, dad, mum, conta, Examples.getNEx());
                // Mutates
                Mutation(Variables, (conta*2));
                Mutation(Variables, (conta*2)+1);
            }
            
            if(long_poblacion%2 == 1){
                int dad = Select();
                offspring.CopyIndiv(long_poblacion-1, Examples.getNEx(), num_objetivos, poblac.getIndiv(dad));
            }
            
            // Evaluates the offspring
            Trials += offspring.evalPop(this, Variables, Examples);
            
            // Join population and offspring in union population
            JoinTemp (Examples.getNEx());
            
            // Makes the ranking of union
            Ranking ranking = new Ranking(union, Variables, num_objetivos, Examples.getNEx(), RulesRep, StrictDominance);
 
            int remain = poblac.getNumIndiv();
            int index  = 0;

            // Obtains the Pareto front
            Population front = ranking.getSubfront(index);

            int contador=0;

            while ((remain > 0) && (remain >= front.getNumIndiv())){

                // Calculates the diversity function
                if((diversity.compareTo("KNEE")==0)&&(num_objetivos==2)){
                    CalculateKnee(front, num_objetivos);
                } else if((diversity.compareTo("UTILITY")==0)&&(num_objetivos==2)) {
                    CalculateUtility(front, num_objetivos);
                } else {
                    CalculateDistanceCrowding(front, num_objetivos);
                }

                // Add the individuals of this front
                for (int k = 0; k < front.getNumIndiv(); k++ ) {
                  poblac.CopyIndiv(contador,Examples.getNEx(),num_objetivos, front.getIndiv(k));
                  contador++;
                }

                //Decrement remain
                remain = remain - front.getNumIndiv();
                //Obtain the next front
                index++;
                if (remain > 0) {
                    if(ranking.getNumberOfSubfronts()==index){
                        front = new Population(remain, Variables.getNVars(), num_objetivos, Examples.getNEx(), RulesRep, Variables);
                        front = ReInitCoverage(front, Variables, Examples, nFile);
                        remain = 0;
                    } else {
                        front = ranking.getSubfront(index);
                    }
                } // if
           } // while
          // remain is less than front(index).size, insert only the best one
          if (remain > 0) {  // front contains individuals to insert                        

            // Assign diversity function to individuals
            if((diversity.compareTo("KNEE")==0)&&(num_objetivos==2)){
                CalculateKnee(front, num_objetivos);
            } else if((diversity.compareTo("UTILITY")==0)&&(num_objetivos==2)) {
                CalculateUtility(front, num_objetivos);
            } else {
                CalculateDistanceCrowding(front, num_objetivos);
            }

            // Sort population with the diversity function
            double[] ordenado = new double[front.getNumIndiv()];
            int izq = 0;
            int der = front.getNumIndiv()-1;
            int indices[] = new int[front.getNumIndiv()];
            for(int i=0; i<front.getNumIndiv(); i++){
                indices[i] = i;
                ordenado[i] = front.getIndiv(i).getCrowdingDistance();
            }
            Utils.OrCrecIndex(ordenado, izq, der, indices);
            int i = front.getNumIndiv()-1;

            for (int k = remain-1; k >= 0; k--) {
                
                poblac.CopyIndiv(contador, Examples.getNEx(), num_objetivos, front.getIndiv(indices[i]));
                i--;
                contador++;

            } // for

            // Re-initialisation based on coverage
            if(getReInitCob().compareTo("yes")==0){
                poblac = ReInitCoverage(poblac, Variables, Examples, nFile);
            }
            
            remain = 0; 
          } // if        
            
        } while (Trials <= n_eval);

        Ranking ranking = new Ranking(poblac,Variables,num_objetivos, Examples.getNEx(), RulesRep, StrictDominance);

        contents = "\nGenetic Algorithm execution finished\n";
        contents+= "\tNumber of Generations = " + Gen + "\n";
        contents+= "\tNumber of Evaluations = " + Trials + "\n";
        Files.addToFile(nFile, contents);
        
        return ranking.getSubfront(0);
        
    }

    /**
     * <p>
     * Function of the re-initialisation based on coverage
     * </p>
     * @param poblac            The actual population
     * @param Variables         Variables structure
     * @param Examples          Examples structure
     * @param nFile             Fichero to write the process
     * @return                  The new population for the next generation
     */
    private Population ReInitCoverage(Population poblac, TableVar Variables, TableDat Examples, String nFile){

        poblac.examplesCoverPopulation(Examples.getNEx(),Trials);

        // Checks the difference between the last and actual evaluations
        double porc_cambio = (n_eval*5)/100;
        if((Trials - poblac.getLastChangeEval()) >= porc_cambio){
            Vector marcas;
            if(RulesRep.compareTo("CAN")==0){
                marcas = RemoveRepeatedCAN(poblac);
            } else {
                marcas = RemoveRepeatedDNF(poblac,Variables);
            }
            // Generates new individuals
            for(int conta=0; conta < poblac.getNumIndiv(); conta++){
                if((Integer) marcas.get(conta) == 1){
                    Individual indi = null;
                    if(RulesRep.compareTo("CAN")==0){
                        indi = new IndCAN(Variables.getNVars(),Examples.getNEx(),num_objetivos);
                    } else {
                        indi = new IndDNF(Variables.getNVars(),Examples.getNEx(),num_objetivos,Variables);
                    }
                    indi.CobInitInd(poblac,Variables,Examples,porcCob,num_objetivos,nFile);
                    indi.evalInd(this, Variables, Examples);
                    indi.setIndivEvaluated(true);
                    indi.setNEval(Trials);
                    Trials++;
                    // Copy the individual in the population
                    poblac.CopyIndiv(conta, Examples.getNEx(), num_objetivos, indi);
                    for(int j=0; j<Examples.getNEx(); j++){
                        if((poblac.getIndiv(conta).getIndivCovered(j)==true)&&(poblac.ej_cubiertos[j]==false)){
                            poblac.ej_cubiertos[j]=true;
                            poblac.ult_cambio_eval = Trials;
                        }
                    }
                }
            }
        }
        return poblac;

    }

    /**
     * <p>
     * Calculates the crowding distance
     * </p>
     * @param population                The actual population
     * @param nobj             The number of objectives
     */
    private void CalculateDistanceCrowding(Population pop, int nobj){

        int size = pop.getNumIndiv();

        if (size == 0)
          return;

        if (size == 1) {
            pop.getIndiv(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
          return;
        } // if

        if (size == 2) {
          pop.getIndiv(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
          pop.getIndiv(1).setCrowdingDistance(Double.POSITIVE_INFINITY);
          return;
        } // if       

        for (int i = 0; i < size; i++)
          pop.getIndiv(i).setCrowdingDistance(0.0);

        double objetiveMaxn;
        double objetiveMinn;
        double distance;

        int ini,fin;
 
        for (int i = 0; i<nobj; i++) {
          double[] ordenado = new double[pop.getNumIndiv()];
          int izq = 0;
          int der = pop.getNumIndiv()-1;
          int indices[] = new int[pop.getNumIndiv()];
          QualityMeasures medidas = new QualityMeasures(nobj);
          for(int j=0; j<pop.getNumIndiv(); j++){
              indices[j] = j;
              medidas = pop.getIndiv(j).getMeasures();
              ordenado[j] = medidas.getObjectiveValue(i);
          }
          Utils.OrCrecIndex(ordenado, izq, der, indices);

          ini = indices[0];
          fin = indices[pop.getNumIndiv()-1];

          medidas = pop.getIndiv(ini).getMeasures();
          objetiveMinn = medidas.getObjectiveValue(i);
          medidas = pop.getIndiv(fin).getMeasures();
          objetiveMaxn = medidas.getObjectiveValue(i);

          //Set de crowding distance            
          pop.getIndiv(ini).setCrowdingDistance(Double.POSITIVE_INFINITY);
          pop.getIndiv(fin).setCrowdingDistance(Double.POSITIVE_INFINITY);

          double a,b;

          for (int j = 1; j < size-1; j++) {
            medidas = pop.getIndiv(indices[j+1]).getMeasures();
            a = medidas.getObjectiveValue(i);
            medidas = pop.getIndiv(indices[j-1]).getMeasures();
            b = medidas.getObjectiveValue(i);
            distance = a-b;
            if(distance!=0){
                distance = distance / (objetiveMaxn - objetiveMinn);        
            }
            distance += pop.getIndiv(indices[j]).getCrowdingDistance();
            pop.getIndiv(indices[j]).setCrowdingDistance(distance);
          } // for
        } // for        
    }

    /**
     * <p>
     * Calculates the knee value. This function is only valid for two objectives
     * </p>
     * @param population                The actual population
     * @param nobj             The number of objectives
     */
    private void CalculateKnee(Population pop, int nobj){

    int i, j;
    int izq, der;
    double a, b, c;
    double pi2 = 1.5707963267948966;

        int size = pop.getNumIndiv();

        if (size == 0)
          return;

        if (size == 1) {
            pop.getIndiv(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
          return;
        } // if

        if (size == 2) {
          pop.getIndiv(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
          pop.getIndiv(1).setCrowdingDistance(Double.POSITIVE_INFINITY);
          return;
        } // if

        for (i = 0; i < size; i++)
          pop.getIndiv(i).setCrowdingDistance(0.0);

      double[] ordenado = new double[size];
      double[] ordenado2 = new double[size];
      int indices[] = new int[size];
      int indices2[] = new int[size];

      i=0;

      izq = 0;
      der = size-1;
      QualityMeasures medidas = new QualityMeasures(nobj);
      for(j=0; j<size; j++){
          indices[j] = j;
          medidas = pop.getIndiv(j).getMeasures();
          ordenado[j] = medidas.getObjectiveValue(0);
      }
      i=1;
      izq = 0;
      der = size-1;
      for(j=0; j<size; j++){
          indices2[j] = j;
          medidas = pop.getIndiv(j).getMeasures();
          ordenado2[j] = medidas.getObjectiveValue(1);
      }
      Utils.OrCrecIndex(ordenado, izq, der, indices);
      Utils.OrCrecIndex(ordenado2, izq, der, indices2);

      for(j=0; j<pop.getNumIndiv(); j++){
          for (izq=j-1;
               izq>=0 &&
               pop.getIndiv(indices2[izq]).getMeasures().getObjectiveValue(1) == pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(1) &&
      	       pop.getIndiv(indices2[izq]).getMeasures().getObjectiveValue(0) == pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(0); izq--);
          for (der=j;
               der < size &&
               pop.getIndiv(indices2[der]).getMeasures().getObjectiveValue(1) == pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(1) &&
               pop.getIndiv(indices2[der]).getMeasures().getObjectiveValue(0) == pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(0); der++);

          pop.getIndiv(indices2[j]).setCrowdingDistance(pi2);

          if (izq < 0){
              double valor = pop.getIndiv(indices2[j]).getCrowdingDistance();
              pop.getIndiv(indices2[j]).setCrowdingDistance(valor+pi2);
          } else {
              b = (pop.getIndiv(indices2[izq]).getMeasures().getObjectiveValue(0)-pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(0))/
                  (pop.getIndiv(indices[pop.getNumIndiv()-1]).getMeasures().getObjectiveValue(0)-pop.getIndiv(indices[0]).getMeasures().getObjectiveValue(0));
              c = (pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(1)-pop.getIndiv(indices2[izq]).getMeasures().getObjectiveValue(1))/
                  (pop.getIndiv(indices2[pop.getNumIndiv()-1]).getMeasures().getObjectiveValue(1)-pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(0)*1.0);
              a = Math.sqrt(b*b + c*c);
              double valor = pop.getIndiv(indices2[j]).getCrowdingDistance();
              pop.getIndiv(indices2[j]).setCrowdingDistance(valor+Math.asin(b/a));
          }

          if(der >= pop.getNumIndiv()){
              double valor = pop.getIndiv(indices2[j]).getCrowdingDistance();
              pop.getIndiv(indices2[j]).setCrowdingDistance(valor+pi2);
          } else {
              b= (pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(0)-pop.getIndiv(indices2[der]).getMeasures().getObjectiveValue(0))/
                 (pop.getIndiv(indices[pop.getNumIndiv()-1]).getMeasures().getObjectiveValue(0)-pop.getIndiv(indices[0]).getMeasures().getObjectiveValue(0));
              c= (pop.getIndiv(indices2[der]).getMeasures().getObjectiveValue(0)-pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(1))/
                 (pop.getIndiv(indices2[pop.getNumIndiv()-1]).getMeasures().getObjectiveValue(1)-pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(1)*1.0);
              a = Math.sqrt(b*b + c*c);
              double valor = pop.getIndiv(indices2[j]).getCrowdingDistance();
              pop.getIndiv(indices2[j]).setCrowdingDistance(valor+Math.asin(c/a));
          }
      }


    }

    /**
     * <p>
     * Calculates the utility value. This function is only valid for two objectives
     * </p>
     * @param population                The actual population
     * @param nobj             The number of objectives
     */
    private void CalculateUtility(Population pop, int nobj){

        int size = pop.getNumIndiv();

        if (size == 0)
          return;

        if (size == 1) {
            pop.getIndiv(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
          return;
        } // if

        if (size == 2) {
          pop.getIndiv(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
          pop.getIndiv(1).setCrowdingDistance(Double.POSITIVE_INFINITY);
          return;
        } // if

        for (int i = 0; i < size; i++)
          pop.getIndiv(i).setCrowdingDistance(0.0);

        double[] ordenado = new double[size];
        double[] ordenado2 = new double[size];
        int indices[] = new int[size];
        int indices2[] = new int[size];


        int izq = 0;
        int der = size-1;
        QualityMeasures medidas = new QualityMeasures(nobj);
        for(int j=0; j<size; j++){
            indices[j] = j;
            medidas = pop.getIndiv(j).getMeasures();
            ordenado[j] = medidas.getObjectiveValue(0);
        }

        izq = 0;
        der = size-1;
        for(int j=0; j<size; j++){
            indices2[j] = j;
            medidas = pop.getIndiv(j).getMeasures();
            ordenado2[j] = medidas.getObjectiveValue(1);
        }
        Utils.OrCrecIndex(ordenado, izq, der, indices);
        Utils.OrCrecIndex(ordenado2, izq, der, indices2);

        if(pop.getIndiv(indices2[size-1]).getMeasures().getObjectiveValue(1)!=pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(1)){

            for(int i=0; i<long_lambda; i++){
                double min=0;
                for(int k=0; k<nobj; k++){
                    if(k==0){
                        min += lambda[i][k] * ((pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(k)
                            -pop.getIndiv(indices[0]).getMeasures().getObjectiveValue(k))
                            /(pop.getIndiv(indices[size-1]).getMeasures().getObjectiveValue(k)
                            -pop.getIndiv(indices[0]).getMeasures().getObjectiveValue(k)));
                    } else {
                        min += lambda[i][k] * ((pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(k)
                            -pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(k))
                            /(pop.getIndiv(indices2[size-1]).getMeasures().getObjectiveValue(k)
                            -pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(k)));
                    }
                }
                int posmin = 0;
                double second = Double.POSITIVE_INFINITY;
                int possecond = -1;
                for(int j=1; j<size; j++){
                    double temp = 0.0;
                    for(int k=0; k<nobj; k++){
                        if(k==0){
                            temp += lambda[i][k] * ((pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(k)
                                    -pop.getIndiv(indices[0]).getMeasures().getObjectiveValue(k))
                                    /(pop.getIndiv(indices[size-1]).getMeasures().getObjectiveValue(k)
                                    -pop.getIndiv(indices[0]).getMeasures().getObjectiveValue(k)));
                        } else {
                            temp += lambda[i][k] * ((pop.getIndiv(indices2[j]).getMeasures().getObjectiveValue(k)
                                    -pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(k))
                                    /(pop.getIndiv(indices2[size-1]).getMeasures().getObjectiveValue(k)
                                    -pop.getIndiv(indices2[0]).getMeasures().getObjectiveValue(k)));
                        }
                    }
                    if (temp < min) {
                        second = min;
                        possecond = posmin;
                        min = temp;
                        posmin = j;
                    } else {
                        if (temp < second) {
                            second = temp;
                            possecond = j;
                        }
                    }
                }
                double crowding = pop.getIndiv(indices2[posmin]).getCrowdingDistance();
                pop.getIndiv(indices2[posmin]).setCrowdingDistance(crowding + (second-min));
            }
        }
    }

    /**
     * <p>
     * Eliminates the repeated individuals for canonical representation
     * </p>
     * @param original              A population
     * @return                      A vector which marks the inviduals repeated
     */
    public Vector RemoveRepeatedCAN(Population original){
    
        Vector marcar = new Vector();
        for(int i=0; i<original.getNumIndiv(); i++){
            marcar.add(i,0);
        }

        int repes = 0;
        int tama_cromo;
        
        for(int i=0; i<original.getNumIndiv(); i++){
            Individual ini = original.getIndiv(i);
            CromCAN cini = ini.getIndivCromCAN();
            tama_cromo = cini.getCromLength();
            for(int j=i+1; j<original.getNumIndiv(); j++){
                int marca = (Integer) marcar.get(j);
                int cuenta_iguales = 0;
                Individual fin = original.getIndiv(j);
                CromCAN cfin = fin.getIndivCromCAN();
                if(marca==0){
                    for(int k=0; k<tama_cromo; k++){
                        if(cini.getCromElem(k) == cfin.getCromElem(k)){
                            cuenta_iguales++;
                        }
                    }
                }
                if(((cuenta_iguales == tama_cromo)&&(i<j))||(fin.getRank()!=0)){
                    marcar.set(j,1);
                    repes++;
                }
            }
            
        }
        return marcar;
        
    }
    
    /**
     * <p>
     * Eliminates the repeated individuals for DNF representation
     * </p>
     * @param original              A population
     * @return                      A vector which marks the inviduals repeated
     */
    public Vector RemoveRepeatedDNF(Population original, TableVar Variables){

        Vector marcar = new Vector();
        for(int i=0; i<original.getNumIndiv(); i++){
            marcar.add(i,0);
        }

        int repes = 0;
        int tama_cromo;

        for(int i=0; i<original.getNumIndiv(); i++){
            Individual ini = original.getIndiv(i);
            CromDNF cini = ini.getIndivCromDNF();
            tama_cromo = cini.getCromLenght();
            for(int j=i+1; j<original.getNumIndiv(); j++){
                int marca = (Integer) marcar.get(j);
                int cuenta_iguales = 0;
                Individual fin = original.getIndiv(j);
                CromDNF cfin = fin.getIndivCromDNF();
                if(marca==0){
                    for(int k=0; k<tama_cromo; k++){
                        boolean genes = true;
                        int number = cini.getCromGeneLenght(k);
                        if((cini.getCromGeneElem(k,number)==true)&&(cfin.getCromGeneElem(k,number)==true)){
                            for(int l=0; l<number; l++){
                                if(cini.getCromGeneElem(k, l) != cfin.getCromGeneElem(k, l)){
                                    genes = false;
                                }
                            }
                        }
                        if(genes) cuenta_iguales++;
                    }
                }
                if(((cuenta_iguales == tama_cromo)&&(i<j))||(fin.getRank()!=0)){
                    marcar.set(j,1);
                    repes++;
                }
            }

        }
        return marcar;


    }

}
