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

package keel.Algorithms.RE_SL_Methods.SEFC;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
import java.io.IOException;
import java.util.*;
import org.core.*;

public class Algorithm {
/**	
 * <p>
 * It contains the implementation of the algorithm
 * </p>
 */
 
    myDataset train, val, test;
    String outputTr, outputTst, outputBC;
    double classProb[];
    double attrProb[][][]; //atribute value, atribute position, class
    int entradas;
    ArrayList<Individual> Poblacion;
    ArrayList<Individual> Poblacion2;
    ArrayList<Individual> Hijos;
    ArrayList<Individual> SistemaDifuso;
    ArrayList<Individual> BestSistemaDifuso;
    long semilla;
    int tamPoblacion, Nf, Nr, Gen, GenSincambio, numGeneraciones, K, numGenMigration;
    double probMut, Bestperformance;
    int [] vectorNr;

    //We may declare here the algorithm's parameters

    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * <p>
     * Default constructor
     * </p>
     */
    public Algorithm() {
    }

    /**
     * <p>
     * It reads the data from the input files (training, validation and test) and parse all the parameters
     * from the parameters array.
     * </p>
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public Algorithm(parseParameters parameters) {

        train = new myDataset();
        val = new myDataset();
        test = new myDataset();
        try {
            System.out.println("\nReading the training set: " +
                               parameters.getTrainingInputFile());
            train.readRegressionSet(parameters.getTrainingInputFile(), true);
            System.out.println("\nReading the validation set: " +
                               parameters.getValidationInputFile());
            val.readRegressionSet(parameters.getValidationInputFile(), false);
            System.out.println("\nReading the test set: " +
                               parameters.getTestInputFile());
            test.readRegressionSet(parameters.getTestInputFile(), false);
        } catch (IOException e) {
            System.err.println(
                    "There was a problem while reading the input data-sets: " +
                    e);
            somethingWrong = true;
        }

        //We may check if there are some numerical attributes, because our algorithm may not handle them:
        //somethingWrong = somethingWrong || train.hasNumericalAttributes();
        //somethingWrong = somethingWrong || train.hasMissingAttributes();

        outputTr = parameters.getTrainingOutputFile();
        outputTst = parameters.getTestOutputFile();
        outputBC = parameters.getOutputFile(0);

        //Now we parse the parameters, for example:
        semilla = Long.parseLong(parameters.getParameter(0));
        //...
        tamPoblacion = Integer.parseInt(parameters.getParameter(1));
        numGeneraciones = Integer.parseInt(parameters.getParameter(2));
        numGenMigration = Integer.parseInt(parameters.getParameter(3));
        Nr = Integer.parseInt(parameters.getParameter(4));
        Nf = Integer.parseInt(parameters.getParameter(5));
        K = Integer.parseInt(parameters.getParameter(6));
        probMut = Double.parseDouble(parameters.getParameter(7));

        entradas = train.getnInputs();

        Poblacion = new ArrayList<Individual>(tamPoblacion);
        for (int i = 0; i < tamPoblacion; i++) {
            Individual indi = new Individual(entradas);
            Poblacion.add(indi);
        }

        Poblacion2 = new ArrayList<Individual>(tamPoblacion);
        Hijos = new ArrayList<Individual>(tamPoblacion / 2);
        SistemaDifuso = new ArrayList<Individual>(Nr);
        BestSistemaDifuso = new ArrayList<Individual>(Nr);

        vectorNr = new int[Nr];
    }

    /**
     * <p>
     * It launches the algorithm
     * </p>
     */
    public void execute() {
        int i, j, num;
        double fitness, fitness2;

        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found, either the data-set have numerical values or missing values.");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } else {
            //We do here the algorithm's operations
            Randomize.setSeed(semilla);

            /* Generation of the initial population */
            System.out.println("Creating the initial population.");
            initializePopulation();
            Gen = 0;
            GenSincambio = 0;
            Bestperformance = -1.0;

            /* Main of the genetic algorithm */
            System.out.println("Starting the evolutionary process.");
            do {
                /* First, all rules' fitness is set to 0 */
                for(i = 0; i < tamPoblacion; i++){
                    Poblacion.get(i).fitness = 0.0;
                    Poblacion.get(i).n_SistemasDifusos = 0;
                }

                /* Then Nf fuzzy system are created */
                for(i = 0; i < Nf; i++){
                    /* A fuzzy system containing Nr rules from the population is created */
                    Create_fuzzy_system();

                    /* The fitness asociated to this fuzzy system is calculated */
                    fitness = Evaluate_fuzzy_system();

                    /* The fitness value is accumulated among the rules in the fuzzy system */
                    Accumulate_fitness_fuzzy_system(fitness);

                    /* If the fitness of the current fuzzy system outperforms the best evolved one,
                       we update this last one */
                    if(fitness > Bestperformance){
                        Bestperformance = fitness;
                        GenSincambio = -1;

                        BestSistemaDifuso.clear();
                        for(j = 0; j < Nr; j++){
                            Individual indi = new Individual(Poblacion.get(vectorNr[j]));
                            BestSistemaDifuso.add(indi);
                        }
                    }
                }

                    /* The accumulated fitness value of each individual in the population is divided
                       by the number of times it has been selected */
                    for (i = 0; i < tamPoblacion; i++) {
                        if (Poblacion.get(i).n_SistemasDifusos != 0) {
                            Poblacion.get(i).fitness /= Poblacion.get(i).n_SistemasDifusos;
                        } else {
                            Poblacion.get(i).fitness = 0.0;
                        }

                        /* Now we count the number of parameter used in the consequent, in order to
                           give a better fitness to those rules with a lower number of parameters */
                        num = 0;
                        for(j = 0; j < entradas; j++){
                            if(Poblacion.get(i).consecuente[j] != 0.0){
                                num++;
                            }
                        }
                        if(Poblacion.get(i).consecuente[entradas] != 0.0){
                            num++;
                        }

                        Poblacion.get(i).fitness /= (K + num);
                    }

                    /* we increment the counter of the number of generations */
                    Gen++;
                    GenSincambio++;

                    if(GenSincambio == numGenMigration){
                        /* Migration stage: half of the population (the worst one) is radomly generated again
                           to increase the searching ability of the genetic process */
                        System.out.println("Migrating half of the population in order to restart the evolutionary process.");
                        Migration();
                        GenSincambio = 0;
                    }
                    else{
                        /* Reproduction stage (includes crossover) */
                        Reproduction();

                        /* Mutation */
                        Mutation();
                    }
                System.out.println("Iteration: " + Gen + ". Best fitness: " + (1.0 / Bestperformance));
            } while (Gen <= numGeneraciones);

            String salida = new String("");
            salida += Print_Population();

            SistemaDifuso.clear();
            for(i = 0; i < Nr; i++){
                Individual indi = new Individual(BestSistemaDifuso.get(i));
                SistemaDifuso.add(indi);
            }

            salida += "MSE Training:\t" + (1.0 / Bestperformance) + "%\n";
            salida += "MSE Test:\t\t" + Evaluate_best_fuzzy_system_in_test() + "%\n\n";

            Files.writeFile(outputBC, salida);

            doOutput(this.val, this.outputTr);
            doOutput(this.test, this.outputTst);

            System.out.println("Algorithm Finished.");
        }
    }


    /**
     * <p>
     * It initializes each individual in the population
     * </p>
     */
    void initializePopulation() {
        int i, j, num;
        double u;

        for (j = 0; j < tamPoblacion; j++) {
            /* First, the antecedent */
            for (i = 0; i < entradas; i++) {
                Poblacion.get(j).antecedente[i].m = Randomize.RanddoubleClosed(train.getMin(i), train.getMax(i));
                Poblacion.get(j).antecedente[i].sigma = Randomize.RandintClosed(1, 4);
            }

            /* Secondly, the consequent */
            do{
                num = 0;

                for (i = 0; i < entradas; i++) {
                    u = Randomize.RandClosed();
                    /* The term is used in the consequent */
                    if (u < 0.5) {
                        Poblacion.get(j).consecuente[i] = Randomize.RanddoubleClosed(-1.0, 1.0);
//                        Poblacion.get(j).consecuente[i] = Randomize.RanddoubleClosed(train.getMin(i) - 45.0, train.getMax(i) + 45.0);
//                        Poblacion.get(j).consecuente[i] = Randomize.RanddoubleClosed(-45.0, 45.0);
                        if(Poblacion.get(j).consecuente[i] != 0.0){
                            num++;
                        }
                    }
                    /* The term is NOT used in the consequent */
                    else {
                        Poblacion.get(j).consecuente[i] = 0.0;
                    }
                }

                u = Randomize.RandClosed();
                /* The term is used in the consequent */
                if (u < 0.5) {
//                    Poblacion.get(j).consecuente[entradas] = Randomize.RanddoubleClosed( - 45.0, 45.0);
                    Poblacion.get(j).consecuente[entradas] = Randomize.RanddoubleClosed(-1.0 * ((train.getMax(entradas) - train.getMin(entradas)) / 2.0), ((train.getMax(entradas) - train.getMin(entradas)) / 2.0));
                    if(Poblacion.get(j).consecuente[entradas] != 0.0){
                        num++;
                    }
                }
                /* The term is NOT used in the consequent */
                else {
                    Poblacion.get(j).consecuente[entradas] = 0.0;
                }
            } while (num == 0);
        }
    }


    /**
     * <p>
     * It creates a fuzzy system containing Nr rules from the population
     * </p>
     */    
    void Create_fuzzy_system(){
        int i, pos, tam;
        int [] vector = new int[tamPoblacion];

        for (i = 0; i < tamPoblacion; i++){
            vector[i] = i;
        }
        tam = tamPoblacion;

        SistemaDifuso.clear();
        for (i = 0; i < Nr; i++){
            pos = Randomize.RandintClosed(0, tam-1);
            Poblacion.get(vector[pos]).n_SistemasDifusos++;
            Individual indi = new Individual(Poblacion.get(vector[pos]));
            SistemaDifuso.add(indi);
            vectorNr[i] = vector[pos];
            vector[pos] = vector[tam-1];
            tam--;
        }
    }


    /**
     * <p>
     * It Evaluates the performance of the fuzzy system. The Mean Square Error (MSE) by training is used
     * </p>
     */       
    public double Evaluate_fuzzy_system(){
        int i;
        double result, suma, fuerza;

        suma = 0.0;
        for (i = 0; i < train.getnData(); i++){
            fuerza = Output_fuzzy_system(train.getExample(i));
            suma += Math.pow(train.getOutputAsReal(i) - fuerza, 2.0);
        }

        result = suma / train.getnData();

        /* We want to have a maximization problem so, we invert the error */
        if(result != 0.0){
            result = 1.0 / result;
        }

        return(result);
    }


    /**
     * <p>
     * It accumulates de fitness value (obtained by the evaluation function) among all the rules forming the fuzzy system
     * </p>
     * @param valor double The fitness value obtained by the evaluation function
     */    
    void Accumulate_fitness_fuzzy_system(double valor){
        int i;

        for(i = 0; i < Nr; i++){
            Poblacion.get(vectorNr[i]).fitness += (valor / Nr);
        }
    }


    /**
     * <p>
     * It applies the migration stage
     * </p>
     */      
    void Migration() {
        int i, j, num;
        double u;

        /* First, the individual in the population are ordered according to their fitness */
        Collections.sort(Poblacion);

        /* The second half (the worst one) is radomly initialized again */
        for (j = (tamPoblacion / 2); j < tamPoblacion; j++) {
            /* First, the antecedent */
            for (i = 0; i < entradas; i++) {
                Poblacion.get(j).antecedente[i].m = Randomize.RanddoubleClosed(train.getMin(i), train.getMax(i));
                Poblacion.get(j).antecedente[i].sigma = Randomize.RandintClosed(1, 4);
            }

            /* Secondly, the consequent */
            do{
                num = 0;

                for (i = 0; i < entradas; i++) {
                    u = Randomize.RandClosed();
                    /* The term is used in the consequent */
                    if(u < 0.5){
                        Poblacion.get(j).consecuente[i] = Randomize.RanddoubleClosed(-1.0, 1.0);
//                    Poblacion.get(j).consecuente[entradas] = Randomize.RanddoubleClosed(-45.0, 45.0);
                        if(Poblacion.get(j).consecuente[i] != 0.0){
                            num++;
                        }
                    }
                    /* The term is NOT used in the consequent */
                    else{
                        Poblacion.get(j).consecuente[i] = 0.0;
                    }
                }

                u = Randomize.RandClosed();
                /* The term is used in the consequent */
                if(u < 0.5){
                      Poblacion.get(j).consecuente[entradas] = Randomize.RanddoubleClosed(-1.0 * ((train.getMax(entradas) - train.getMin(entradas)) / 2.0), ((train.getMax(entradas) - train.getMin(entradas)) / 2.0));
//                    Poblacion.get(j).consecuente[entradas] = Randomize.RanddoubleClosed(-45.0, 45.0);
//                Poblacion.get(j).consecuente[entradas] = Randomize.RanddoubleClosed(train.getMin(entradas), train.getMax(entradas));
                    if(Poblacion.get(j).consecuente[entradas] != 0.0){
                        num++;
                    }
                }
                /* The term is NOT used in the consequent */
                else{
                    Poblacion.get(j).consecuente[entradas] = 0.0;
                }
            } while(num == 0);
        }
    }


    /**
     * <p>
     * It applies the reproduction stage
     * </p>
     */     
        void Reproduction(){
            int i, madre, padre;

            /* First, the individual in the population are ordered according to their fitness */
            Collections.sort(Poblacion);

            /* Create the new population */
            Poblacion2.clear();
            Hijos.clear();

            /* Top-half best-performing individuals will advance to the next generation */
            for(i = 0; i < (tamPoblacion / 2); i++){
                Individual indi = new Individual(Poblacion.get(i));
                Poblacion2.add(indi);
            }

            /* The remaining half is generated by performing crossover operations on individuals
               in the top half */
            while(Poblacion.size() != (Poblacion2.size() + Hijos.size())){
                /* 2 parents are selected */
                madre = Selection();
                do{
                    padre = Selection();
                } while (madre == padre);

                /* 2 children are created by crossover operator */
                Crossover(madre, padre);
            }

            /* Create the population for the next generation */
            Poblacion.clear();
            for(i = 0; i < Poblacion2.size(); i++){
                Individual indi = new Individual(Poblacion2.get(i));
                Poblacion.add(indi);
            }
            for(i = 0; i < Hijos.size(); i++){
                Individual indi = new Individual(Hijos.get(i));
                Poblacion.add(indi);
            }
        }


    /**
     * <p>
     * It selects one parent to participate in the evolutionary process (by binary tournament selection).
     * </p>
     * @param int The position in the population for the selected parent
     */
        int Selection() {
            int result;
            int[] indiv = new int[2];

            indiv[0] = Randomize.RandintClosed(0, ((tamPoblacion / 2)-2));
            do{
                indiv[1] = Randomize.RandintClosed(0, ((tamPoblacion / 2) - 2));
            } while (indiv[0] == indiv[1]);

            if(Poblacion2.get(indiv[0]).fitness > Poblacion2.get(indiv[1]).fitness){
                result = indiv[0];
            }
            else{
                result = indiv[1];
            }

            return(result);
        }


    /**
     * <p>
     * It applies a One-point Crossover genetic operator between individual in position "madre" and "padre" in the population.
     * The new generated children (2 descendants) are added in a population of descendants
     * </p>
     * @param madre int Parent number 1 is in position "madre" in the population
     * @param padre int Parent number 2 is in position "padre" in the population
     */           
        void Crossover(int madre, int padre) {
                int i, xpoint, num1, num2;
                Individual Hijo1 = new Individual(entradas);
                Individual Hijo2 = new Individual(entradas);
                boolean salir;

                do{
                    salir = true;

                    /* We choose the crossover site */
                    xpoint = Randomize.RandintClosed(1, ((2 * entradas) + entradas -1));

                    /* The crossover point is in the antededent part */
                    if(xpoint < (2 * entradas)){
                        /* The crossover point does not part a fuzzy set */
                        if((xpoint % 2) == 0){
                            for(i = 0; i < (xpoint / 2); i++){
                                Hijo1.antecedente[i].m = Poblacion2.get(madre).antecedente[i].m;
                                Hijo1.antecedente[i].sigma = Poblacion2.get(madre).antecedente[i].sigma;
                                Hijo2.antecedente[i].m = Poblacion2.get(padre).antecedente[i].m;
                                Hijo2.antecedente[i].sigma = Poblacion2.get(padre).antecedente[i].sigma;
                            }
                            for(i = (xpoint / 2); i < entradas; i++){
                                Hijo1.antecedente[i].m = Poblacion2.get(padre).antecedente[i].m;
                                Hijo1.antecedente[i].sigma = Poblacion2.get(padre).antecedente[i].sigma;
                                Hijo2.antecedente[i].m = Poblacion2.get(madre).antecedente[i].m;
                                Hijo2.antecedente[i].sigma = Poblacion2.get(madre).antecedente[i].sigma;
                            }
                        }
                        /* The crossover point part a fuzzy set */
                        else{
                            for(i = 0; i < ((xpoint - 1) / 2); i++){
                                Hijo1.antecedente[i].m = Poblacion2.get(madre).antecedente[i].m;
                                Hijo1.antecedente[i].sigma = Poblacion2.get(madre).antecedente[i].sigma;
                                Hijo2.antecedente[i].m = Poblacion2.get(padre).antecedente[i].m;
                                Hijo2.antecedente[i].sigma = Poblacion2.get(padre).antecedente[i].sigma;
                            }

                            Hijo1.antecedente[((xpoint - 1) / 2)].m = Poblacion2.get(madre).antecedente[((xpoint - 1) / 2)].m;
                            Hijo1.antecedente[((xpoint - 1) / 2)].sigma = Poblacion2.get(padre).antecedente[((xpoint - 1) / 2)].sigma;
                            Hijo2.antecedente[((xpoint - 1) / 2)].m = Poblacion2.get(padre).antecedente[((xpoint - 1) / 2)].m;
                            Hijo2.antecedente[((xpoint - 1) / 2)].sigma = Poblacion2.get(madre).antecedente[((xpoint - 1) / 2)].sigma;

                            for(i = ((xpoint + 1) / 2); i < entradas; i++){
                                Hijo1.antecedente[i].m = Poblacion2.get(padre).antecedente[i].m;
                                Hijo1.antecedente[i].sigma = Poblacion2.get(padre).antecedente[i].sigma;
                                Hijo2.antecedente[i].m = Poblacion2.get(madre).antecedente[i].m;
                                Hijo2.antecedente[i].sigma = Poblacion2.get(madre).antecedente[i].sigma;
                            }
                        }

                        for(i = 0; i < (entradas + 1); i++){
                            Hijo1.consecuente[i] = Poblacion2.get(padre).consecuente[i];
                            Hijo2.consecuente[i] = Poblacion2.get(madre).consecuente[i];
                        }
                    }
                    /* The crossover point is in the consequent part */
                    else{
                        for(i = 0; i < entradas; i++){
                            Hijo1.antecedente[i].m = Poblacion2.get(madre).antecedente[i].m;
                            Hijo1.antecedente[i].sigma = Poblacion2.get(madre).antecedente[i].sigma;
                            Hijo2.antecedente[i].m = Poblacion2.get(padre).antecedente[i].m;
                            Hijo2.antecedente[i].sigma = Poblacion2.get(padre).antecedente[i].sigma;
                        }

                        xpoint -= (2 * entradas);
                        for(i = 0; i < xpoint; i++){
                            Hijo1.consecuente[i] = Poblacion2.get(madre).consecuente[i];
                            Hijo2.consecuente[i] = Poblacion2.get(padre).consecuente[i];
                        }
                        for(i = xpoint; i < entradas; i++){
                            Hijo1.consecuente[i] = Poblacion2.get(padre).consecuente[i];
                            Hijo2.consecuente[i] = Poblacion2.get(madre).consecuente[i];
                        }
                    }

                    num1 = num2 = 0;
                    for(i = 0; i <= entradas; i++){
                        if(Hijo1.consecuente[i] != 0){
                            num1++;
                        }
                        if(Hijo2.consecuente[i] != 0){
                            num2++;
                        }

                    }
                    if((num1 == 0) || (num2 == 0)){
                        salir = false;
                    }
                } while(salir == false);

                /* Add the new 2 children to the offspring population */
                Hijos.add(Hijo1);
                Hijos.add(Hijo2);
        }


    /**
     * <p>
     * It applies mutation genetic operator 
     * </p>
     */
        void Mutation() {
            int i, j, aux1, num;
            double u, u2;

            for (j = 0; j < tamPoblacion; j++){
                /* First, the antecedent */
                for (i = 0; i < entradas; i++){
                    u = Randomize.RandClosed();
                    if (u < probMut){
                        Poblacion.get(j).antecedente[i].m = Randomize.RanddoubleClosed(train.getMin(i), train.getMax(i));
                    }

                    u = Randomize.RandClosed();
                    if (u < probMut){
                        aux1 = Poblacion.get(j).antecedente[i].sigma;
                        do{
                            Poblacion.get(j).antecedente[i].sigma = Randomize.RandintClosed(1, 4);
                        } while (aux1 == Poblacion.get(j).antecedente[i].sigma);
                    }
                }

                /* Secondly, the consequent */
                num = 0;
                for (i = 0; i <= entradas; i++){
                    if(Poblacion.get(j).consecuente[i] != 0.0){
                        num++;
                    }
                }

                for (i = 0; i < entradas; i++){
                    u = Randomize.RandClosed();
                    if (u < probMut){
                        u2 = Randomize.RandClosed();
                        if (u2 < 0.5){
                            Poblacion.get(j).consecuente[i] = Randomize.RanddoubleClosed(-1.0, 1.0);
                        }
                        /* The term is NOT used in the consequent */
                        else {
                            if (num != 1){
                                Poblacion.get(j).consecuente[i] = 0.0;
                                num--;
                            }
                        }
                    }
                }

                u = Randomize.RandClosed();
                if (u < probMut){
                    u2 = Randomize.RandClosed();
                    if (u2 < 0.5){
                        Poblacion.get(j).consecuente[entradas] = Randomize.RanddoubleClosed(-1.0 * ((train.getMax(entradas) - train.getMin(entradas)) / 2.0), ((train.getMax(entradas) - train.getMin(entradas)) / 2.0));
//                        Poblacion.get(j).consecuente[entradas] = Randomize.RanddoubleClosed(-45.0, 45.0);
                    }
                    /* The term is NOT used in the consequent */
                    else {
                        if (num != 1){
                            Poblacion.get(j).consecuente[entradas] = 0.0;
                            num--;
                        }
                    }
                }
            }
        }


    /**
     * <p>
     * It calculate the matching degree between the antecedent of the rule and a given example
     * </p>
     * @param indiv Individual The individual representing a fuzzy rule
     * @param ejemplo double [] A given example
     * @return double The matching degree between the example and the antecedent of the rule
     */
        double Matching_degree(Individual indiv, double[] ejemplo) {
            int i, sig;
            double result, suma, numerador, denominador, sigma, ancho_intervalo;

            suma = 0.0;
            for (i = 0; i < entradas; i++){
                ancho_intervalo = train.getMax(i) - train.getMin(i);

                sigma = -1.0;
                sig = indiv.antecedente[i].sigma;
                switch(sig){
                    case 1: sigma = 0.3;
                            break;
                        case 2: sigma = 0.4;
                                break;
                            case 3: sigma = 0.5;
                                    break;
                                case 4: sigma = 0.6;
                                        break;
                }

                sigma *= ancho_intervalo;

                numerador = Math.pow((ejemplo[i] - indiv.antecedente[i].m), 2.0);
                denominador = Math.pow(sigma, 2.0);
                suma += (numerador / denominador);
                }

            suma *= -1.0;
            result = Math.exp(suma);

            return (result);
        }

    /**
     * <p>
     * Returns the output value for the individual according to the example 
     * </p>
     * @param indiv Individual The individual enconding a fuzzy rule
     * @param ejemplo double [] A given example     
     * @return double The output value for the rule
     */
        double Output_value(Individual indiv, double[] ejemplo){
            int i;
            double suma;

            suma = 0.0;
            for(i = 0; i < entradas; i++){
                suma += (indiv.consecuente[i] * ejemplo[i]);
                }
            suma += indiv.consecuente[entradas]; // w0

            return (suma);
        }


    /**
     * <p>
     * It calculate the output of the fuzzy system for a given example
     * </p>
     * @param ejemplo double [] A given example
     * @return double The output value obtained as output of the fuzzy system for a given example
     */
        double Output_fuzzy_system(double[] ejemplo){
            int i;
            double result, suma1, suma2, omega, y;

            suma1 = suma2 = 0.0;
            for(i = 0; i < Nr; i++){
                omega = Matching_degree(SistemaDifuso.get(i), ejemplo);
                y = Output_value(SistemaDifuso.get(i), ejemplo);
                suma1 += (omega * y);
                suma2 += omega;
            }

            if(suma2 != 0.0){
                result = suma1 / suma2;
            }
            else{
//                result = 0.0;
                result = ((train.getMax(entradas) - train.getMin(entradas)) / 2.0);
            }

            return (result);
        }


    /**
     * <p>
     * It Evaluates the performance of the best evolved fuzzy system on test data. The Mean Square Error (MSE) is used
     * </p>
     * @return double The MSE error in test data
     */           
        public double Evaluate_best_fuzzy_system_in_test(){
            int i;
            double result, suma, fuerza;

            SistemaDifuso.clear();
            for(i = 0; i < Nr; i++){
                Individual indi = new Individual(BestSistemaDifuso.get(i));
                SistemaDifuso.add(indi);
            }

            suma = 0.0;
            for (i = 0; i < test.getnData(); i++){
                fuerza = Output_fuzzy_system(test.getExample(i));
                suma += Math.pow(test.getOutputAsReal(i) - fuerza, 2.0);
            }

            result = suma / test.getnData();

            return(result);
        }


    /**
     * <p>
     * It prints the current population as a String
     * </p>
     * @return String The current population as a String
     */
        public String Print_Population(){
            int i, j, sig;
            double sigma, ancho_intervalo;
            boolean anterior_nulo;
            String output = new String("");

            output += "Rule Base with " + Nr + " rules\n\n";

            for(i = 0; i < Nr; i++){
                output += "Rule " + (i+1) + ": IF ";
                for(j = 0; j < entradas; j++){
                    ancho_intervalo = train.getMax(j) - train.getMin(j);

                    sigma = -1.0;
                    sig = BestSistemaDifuso.get(i).antecedente[j].sigma;
                    switch(sig){
                        case 1: sigma = 0.3;
                                break;
                            case 2: sigma = 0.4;
                                    break;
                                case 3: sigma = 0.5;
                                        break;
                                    case 4: sigma = 0.6;
                                            break;
                    }

                    sigma *= ancho_intervalo;

                    output += "X(" + (j+1) + ") is Gaussian(" + BestSistemaDifuso.get(i).antecedente[j].m + ", " + sigma + ")";
                    if(j != (entradas - 1)){
                        output += " and ";
                    }
                }

                output += " THEN Y = ";

                anterior_nulo = true;
                if(BestSistemaDifuso.get(i).consecuente[entradas] != 0.0){
                    anterior_nulo = false;
                    output += "(" + BestSistemaDifuso.get(i).consecuente[entradas] + ")";
                }

                for(j = 0; j < entradas; j++){
                    if(BestSistemaDifuso.get(i).consecuente[j] != 0.0){
                        if(anterior_nulo == false){
                            output += " + ";
                        }

                        anterior_nulo = false;
                        output += "(" + BestSistemaDifuso.get(i).consecuente[j] + " * X(" + (j+1) + "))";
                    }
                }

                output += "\n\n";
            }

            return(output);
        }

    /**
     * <p>
     * It generates the output file from a given dataset and stores it in a file
     * </p>
     * @param dataset myDataset input dataset
     * @param filename String the name of the file
     */
        private void doOutput(myDataset dataset, String filename) {
            int i;
            double fuerza;
            String output = new String("");

            output = dataset.copyHeader(); //we insert the header in the output file

            for (i = 0; i < dataset.getnData(); i++){
                fuerza = Output_fuzzy_system(dataset.getExample(i));
                output += (dataset.getOutputAsReal(i) + " " + fuerza + " " + "\n");
            }

            Files.writeFile(filename, output);
        }

}

