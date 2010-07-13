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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierShi_Eberhart_Chen;

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
    String outputTr, outputTst, outputBDyBR;
    double classProb[];
    double attrProb[][][]; //atribute value, atribute position, class
    int nClasses, entradas;
    ArrayList<Individual> New;
    ArrayList<Individual> Old;
    ArrayList<Individual> temp;
    long semilla;
    int long_cromosoma, tamPoblacion, numGeneraciones, numEtiquetas, Gen, max_n_reglas;
    int Best_guy, Mu_next;
    double probCross, probMut, Best_current_perf;

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
            train.readClassificationSet(parameters.getTrainingInputFile(), true);
            System.out.println("\nReading the validation set: " +
                               parameters.getValidationInputFile());
            val.readClassificationSet(parameters.getValidationInputFile(), false);
            System.out.println("\nReading the test set: " +
                               parameters.getTestInputFile());
            test.readClassificationSet(parameters.getTestInputFile(), false);
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
        outputBDyBR = parameters.getOutputFile(0);

        //Now we parse the parameters, for example:
        semilla = Long.parseLong(parameters.getParameter(0));
        //...
        numEtiquetas = Integer.parseInt(parameters.getParameter(1));
        tamPoblacion = Integer.parseInt(parameters.getParameter(2));
        numGeneraciones = Integer.parseInt(parameters.getParameter(3));
        max_n_reglas = Integer.parseInt(parameters.getParameter(4));
        probCross = Double.parseDouble(parameters.getParameter(5));
        probMut = Double.parseDouble(parameters.getParameter(6));

        entradas = train.getnInputs();
        nClasses = train.getnClasses();

        long_cromosoma = 1 + ((entradas + 1) * (numEtiquetas * (2 + 1))) + ((entradas + 1) * max_n_reglas);

        New = new ArrayList<Individual>(tamPoblacion);
        for (int i = 0; i < tamPoblacion; i++) {
            Individual indi = new Individual(max_n_reglas, long_cromosoma);
            New.add(indi);
        }

        Old = new ArrayList<Individual>(tamPoblacion);
        for (int i = 0; i < tamPoblacion; i++) {
            Individual indi = new Individual(max_n_reglas, long_cromosoma);
            Old.add(indi);
        }
    }

    /**
     * It launches the algorithm
     */
    public void execute() {
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found, either the data-set have numerical values or missing values.");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } else {
            //We do here the algorithm's operations
            Randomize.setSeed(semilla);

            if (probMut < 1.0) {
                    Mu_next = (int) (Math.log(Randomize.Rand()) / Math.log(1.0 - probMut));
                    Mu_next++;
            }
            else  Mu_next = 1;

            /* Generation of the initial population */
            System.out.println("Creating the initial population.");
            initializePopulation();
            Gen = 0;

            /* evaluatePopulationtion of the initial population */
            System.out.println("evaluatePopulationting the initial population.");
            evaluatePopulation();
//            Gen++;

            /* Main of the genetic algorithm */
            System.out.println("Starting the evolutionary process.");
            do {
                    /* Interchange of the new and old population */
                    Exchange();
//                    System.out.println("Exchanging Old and New populations.");

                    /* Selection by means of Baker */
                    Seleccion();
//                    System.out.println("Selecting parents.");

                    /* Crossover */
                    Crossover();
//                    System.out.println("Crossover.");

                    /* Mutation */
                    Mutation();
//                    System.out.println("Mutation.");

                    /* Elitist selection */
                    ElitistSelection ();
//                    System.out.println("Elitism.");

                    /* evaluatePopulationtion of the current population */
                    evaluatePopulation();
//                    System.out.println("evaluatePopulationting the new population of children.");


                    /* we increment the counter */
                    Gen++;
                    System.out.println("Iteration: " + Gen + ". Best fitness: " + New.get(Best_guy).fitness);
//                    System.out.println("Iteration: " + Gen + ". Best fitness: " + New.get(Best_guy).fitness + ". Num feasibles: " + New.get(Best_guy).num_feasibles);
            } while (Gen <= numGeneraciones);



            String salida = new String("");
            salida += Print_Population(New.get(Best_guy));

            salida += "Training Accuracy:\t" + New.get(Best_guy).fitness + "%\n";
            salida += "Test Accuracy:\t\t" + eval_test(New.get(Best_guy)) + "%\n\n";

            Files.writeFile(outputBDyBR, salida);

            doOutput(this.val, New.get(Best_guy), this.outputTr);
            doOutput(this.test, New.get(Best_guy), this.outputTst);

            System.out.println("Algorithm Finished.");
        }
    }


    /**
     * <p>
     * It initializes each individual in the population
     * </p>
     */
    void initializePopulation() {
        int i, j, k, l, label;

        for (j = 0; j < tamPoblacion; j++) {
            /* The number of active (feasible) rules in the chromosome */
            New.get(j).cromosoma[0] = Randomize.RandintClosed(1, max_n_reglas+1);

            /* The DB */
            for (i = 0; i < entradas+1; i++) {
                for (k = 0; k < numEtiquetas; k++) {
                    for (l = 0; l < 3; l++) {
                        /* Start and end point of the fuzzy set */
                        if(l != 2){
                            New.get(j).cromosoma[1 + ((i * numEtiquetas * 3) + (k * 3) + l)] = Randomize.RandintClosed(0, 10+1);;
                        }
                        /* Type of membership function */
                        else{
                            New.get(j).cromosoma[1 + ((i * numEtiquetas * 3) + (k * 3) + l)] = Randomize.RandintClosed(1, 6+1);;
                        }
                    }
                }
            }

            /* The RB */
            k = 1 + ((entradas+1) * numEtiquetas * 3);

            for (i = 0; i < max_n_reglas; i++) {
                for (l = 0; l < entradas+1; l++) {
                    label = Randomize.RandintClosed((-1 * numEtiquetas), numEtiquetas+1);
                    New.get(j).cromosoma[k + ((i * (entradas+1)) + l)] = label;
                }
            }
        }
    }


    /**
     * <p>
     * It evaluates each individual in the population
     * </p>
     */
    void evaluatePopulation() {
            double performance;
            int i, j;

            for (i=0; i<tamPoblacion; i++) {
                    /* if the chromosome aren't evaluated, it's evaluate */
                    if (New.get(i).n_e == 1) {
//                            System.out.println("evaluatePopulationting individual " + (i+1) + " of the New population.");
                            New.get(i).fitness = eval(New.get(i));
                            performance = New.get(i).fitness;
                            New.get(i).n_e = 0;
                    }
                    else  performance = New.get(i).fitness;

                    /* we calculate the position of the best individual */
                    if (i == 0) {
                            Best_current_perf = performance;
                            Best_guy = 0;
                    }
                    else if (performance > Best_current_perf) {
                            Best_current_perf = performance;
                            Best_guy = i;
                    }
            }
    }


    /**
     * <p>
     * It exchange the old and the new population
     * </p>
     */
    public void Exchange () {
            temp=Old;
            Old=New;
            New=temp;
        }


    /**
     * <p>
     * It selects two parents to participate in the evolutionary process (by rank based roulette wheel selection).
     * </p>
     */
        void Seleccion(){
            int [] sample;
                double expected, factor, perf, ptr, sum, rank_max, rank_min;
                int i, j, k, best, temp;

                sample = new int[tamPoblacion];

                rank_min = 0.75;

                /* we assign the ranking to each element:
                     The best: ranking = long_poblacion-1
                         The worse: ranking = 0 */
                for (i=0; i<tamPoblacion; i++)  Old.get(i).n_e = 0;

                /* we look for the best ordered non element */
                for (i=0; i<tamPoblacion-1; i++) {
                        best = -1;
                        perf = 0.0;
                        for (j=0; j<tamPoblacion; j++) {
                                if ((Old.get(j).n_e==0) && (best==-1 || Old.get(j).fitness > perf)) {
                                        perf = Old.get(j).fitness;
                                        best = j;
                                }
                        }

                        /* we assign the ranking */
                        Old.get(best).n_e = tamPoblacion - 1 - i;
                }

                /* we normalize the ranking */
                rank_max = 2.0 - rank_min;
                factor = (rank_max-rank_min)/(double)(tamPoblacion-1);

                /* we assign the number of replicas of each chormosome according to the select probability */
                k = 0;
                ptr = Randomize.Rand ();
                for (sum = i = 0; i<tamPoblacion; i++) {
                        expected = rank_min + Old.get(i).n_e * factor;
                        for (sum += expected; sum>=ptr; ptr++)  sample[k++] = i;
                }

                /* we complete the population if necessary */
                if (k != tamPoblacion) {
                        for (;k<tamPoblacion; k++)  sample[k]=Randomize.RandintClosed (0,tamPoblacion);
                }

                /* we shuffle the selected chromosomes */
                for (i=0; i<tamPoblacion; i++) {
                        j = Randomize.RandintClosed (i, tamPoblacion);
                        temp = sample[j];
                        sample[j] = sample[i];
                        sample[i] = temp;
                }

                /* we create the new population */
                for (i=0; i<tamPoblacion; i++) {
                        k = sample[i];
                        for (j=0; j<long_cromosoma; j++)  New.get(i).cromosoma[j] = Old.get(k).cromosoma[j];

                        for (j=0; j<max_n_reglas; j++)  New.get(i).feasibles[j] = Old.get(k).feasibles[j];
                        New.get(i).num_feasibles = Old.get(k).num_feasibles;

                        New.get(i).fitness = Old.get(k).fitness;
                        New.get(i).n_e = 0;
                }
        }


    /**
     * <p>
     * It applies a multipoint crossover genetic operator
     * </p>
     */  
        void Crossover() {
                int mom, dad, xpoint1, xpoint2, i, j, temp, last;

                last = (int) ((probCross * tamPoblacion) - 0.5);

                for (mom=0; mom<last; mom+=2) {
                        dad = mom+1;

                        /* we generate the 2 crossing points */
                        xpoint1 = Randomize.RandintClosed (0,long_cromosoma);

                        if (xpoint1 != long_cromosoma-1)
                                xpoint2 = Randomize.RandintClosed (xpoint1 + 1, long_cromosoma);

                        else  xpoint2 = long_cromosoma - 1;

                        /* we cross the individuals between xpoint1 and xpoint2 */
                        for (i=xpoint1; i<=xpoint2; i++) {
                                temp = New.get(mom).cromosoma[i];
                                New.get(mom).cromosoma[i] = New.get(dad).cromosoma[i];
                                New.get(dad).cromosoma[i] = temp;
                        }

                        New.get(mom).n_e = 1;
                        New.get(dad).n_e = 1;
                }
        }


    /**
     * <p>
     * It applies a uniform mutation genetic operator 
     * </p>
     */        
        void Mutation() {
                int posiciones, i, j;
                double m;

                posiciones = long_cromosoma * tamPoblacion;

                if (probMut>0) {
                        while (Mu_next<posiciones) {
                                /* we determinate the chromosome and the gene */
                                i = Mu_next / long_cromosoma;
                                j = Mu_next % long_cromosoma;

                                /* we mutate the gene */

                                /* If the gene is the number of active rules */
                                if(j == 0){
                                    m = Randomize.Rand();
                                    /* Increment */
                                    if(m < 0.5){
                                       if(New.get(i).cromosoma[j] != max_n_reglas){
                                           New.get(i).cromosoma[j]++;
                                       }
                                       else{
                                           New.get(i).cromosoma[j]--;
                                       }
                                    }
                                    /* Decrement */
                                    else{
                                        if(New.get(i).cromosoma[j] != 1){
                                            New.get(i).cromosoma[j]--;
                                        }
                                        else{
                                            New.get(i).cromosoma[j]++;
                                        }
                                    }
                                }
                                else{
                                    /* If the gene is part of the DB */
                                    if((j >= 1) && (j < (1 + ((entradas+1) * numEtiquetas * 3)))){
                                        /* If it is a type of membership function */
                                        if((j % 3) == 0){
                                            m = Randomize.Rand();
                                            /* Increment */
                                            if (m < 0.5) {
                                                if (New.get(i).cromosoma[j] != 6) {
                                                    New.get(i).cromosoma[j]++;
                                                } else {
                                                    New.get(i).cromosoma[j]--;
                                                }
                                            }
                                            /* Decrement */
                                            else {
                                                if (New.get(i).cromosoma[j] != 1) {
                                                    New.get(i).cromosoma[j]--;
                                                } else {
                                                    New.get(i).cromosoma[j]++;
                                                }
                                            }
                                        }
                                        /* If it is a start or a end point of a fuzzy sets */
                                        else{
                                            m = Randomize.Rand();
                                            /* Increment */
                                            if (m < 0.5) {
                                                if (New.get(i).cromosoma[j] !=
                                                    10) {
                                                    New.get(i).cromosoma[j]++;
                                                } else {
                                                    New.get(i).cromosoma[j]--;
                                                }
                                            }
                                            /* Decrement */
                                            else {
                                                if (New.get(i).cromosoma[j] != 0) {
                                                    New.get(i).cromosoma[j]--;
                                                } else {
                                                    New.get(i).cromosoma[j]++;
                                                }
                                            }
                                        }
                                    }
                                    else{
                                        /* If the gene is part of the RB */
                                        m = Randomize.Rand();
                                        /* Increment */
                                        if (m < 0.5) {
                                            if (New.get(i).cromosoma[j] !=
                                                numEtiquetas) {
                                                New.get(i).cromosoma[j]++;
                                            } else {
                                                New.get(i).cromosoma[j]--;
                                            }
                                        }
                                        /* Decrement */
                                        else {
                                            if (New.get(i).cromosoma[j] != (-1 * numEtiquetas)) {
                                                New.get(i).cromosoma[j]--;
                                            } else {
                                                New.get(i).cromosoma[j]++;
                                            }
                                        }
                                    }
                                }

                                New.get(i).n_e=1;

                                /* we calculate the next position */
                                if (probMut<1) {
                                        m = Randomize.Rand();
                                        Mu_next += (int) (Math.log(m) / Math.log(1.0 - probMut)) + 1;
                                }
                                else  Mu_next += 1;
                        }
                }

                Mu_next -= posiciones;
        }


    /**
     * <p>
     * It applies a elitist selection
     * </p>
     */        
        void ElitistSelection () {
                 int i, k, found;

                 /* if the best individual of the old population aren't in the new population, we remplace the last individual for this */
                 for (i=0, found=0; i<tamPoblacion && (found==0); i++)
                        for (k=0, found=1; (k<long_cromosoma) && (found==1); k++)
                                if (New.get(i).cromosoma[k] != Old.get(Best_guy).cromosoma[k])  found = 0;

                 if (found == 0) {
                        for (k=0; k<long_cromosoma; k++)
                            New.get(tamPoblacion-1).cromosoma[k] = Old.get(Best_guy).cromosoma[k];

                        for (k=0; k<max_n_reglas; k++)
                            New.get(tamPoblacion-1).feasibles[k] = Old.get(Best_guy).feasibles[k];

                        New.get(tamPoblacion-1).num_feasibles = Old.get(Best_guy).num_feasibles;

                        New.get(tamPoblacion-1).fitness = Old.get(Best_guy).fitness;
                        New.get(tamPoblacion-1).n_e = 0;
                }
        }


    /**
     * <p>
     * It returns the best solution 
     * </p>
     * @return int [] An integer array containing the best solution
     */        
        public int [] solucion () {
                return (New.get(Best_guy).cromosoma);
        }


    /**
     * <p>
     * It evaluate the RB encoded in the individual "indiv"
     * </p>
     * @return double The correct percentage accuracy in training examples
     */  
        public double eval(Individual indiv){
            int i, j, k, l, m, clase, n_pos;
            double grado, y, suma1, suma2, valor_salida, rango, result;
            boolean salir, al_menos_una;
            double [] grado_salida;

            grado_salida = new double [2 * numEtiquetas];

            /* "k" keeps the begining of the RB */
            k = 1 + ((entradas+1) * numEtiquetas * 3);

            /* For each active rule in the chromosome we check if it is feasible */
            indiv.num_feasibles = 0;
            for(i = 0; i < indiv.cromosoma[0]; i++){
                indiv.feasibles[i] = false;

                for(j = 0; j < (entradas+1); j++){
                    if(j < entradas){
                        if(indiv.cromosoma[k + ((i * (entradas+1)) + j)] != 0){
                            indiv.feasibles[i] = true;
                        }
                    }
                    else{
                        if(indiv.cromosoma[k + ((i * (entradas+1)) + j)] == 0){
                            indiv.feasibles[i] = false;
                        }
                    }
                }

                if(indiv.feasibles[i] == true){
                    indiv.num_feasibles++;
                }
            }


            /* We calculate the matching degree among each example and each feasible rule */
            if(indiv.num_feasibles != 0){
                n_pos = 0;
                for (i = 0; i < train.getnData(); i++){
//                    System.out.println("\tevaluatePopulationting example " + (i+1));

                    for(j = 0; j < (2 * numEtiquetas); j++){
                        grado_salida[j] = 0.0;
                    }

                    al_menos_una = false;
                    for(j = 0; j < indiv.cromosoma[0]; j++){
                        if(indiv.feasibles[j] == true){
                            grado = Matching_degree(indiv.cromosoma, j, train.getExample(i));

                            /* We look for the fuzzy set in the output variable */
                            m = indiv.cromosoma[k + ((j * (entradas+1)) + entradas)];
                            if(m < 0){
                                l = Math.abs(m) + numEtiquetas - 1;
                            }
                            else{
                                l = m - 1;
                            }

                            /* Upgrade the membership value of the output fuzzy set */
                            if(grado > grado_salida[l]){
                                al_menos_una = true;
                                grado_salida[l] = grado;
                            }
                        }
                    }

//                    System.out.println("\tLlego a defuzificar.");
                    if(al_menos_una == true){
                        /* Defuzzification */
                        y = 0.05;
                        suma1 = suma2 = 0.0;
                        for(j = 0; j < 19; j++){
                            grado = Output_Matching_degree(indiv.cromosoma, grado_salida, y);
                            suma1 += y * grado;
                            suma2 += grado;
                            y += 1.0 / 20.0;
                        }

                        /* Now, we calculate the class of the example */
                        valor_salida = suma1 / suma2;
                        rango = 1.0 / nClasses;
                        suma1 = 0.0;
                        suma2 = rango;
                        clase = 0;
                        salir = false;
                        do{
                            if((valor_salida >= suma1) && (valor_salida < suma2)){
                                salir = true;
                            }
                            else{
                                clase++;
                                suma1 = suma2;
                                suma2 += rango;
                            }
                        }
                        while(salir == false);


                        /* Check if the asigned class is equal to the one in the example it is a
                        sucess. Otherwise, it is an error */
                        if (clase == train.getOutputAsInteger(i)) {
                            n_pos++;
                        }
                    }
                }

                result = ((double) n_pos / train.getnData()) * 100.0;
            }
            else{
                result = 0.0001;
            }

            return(result);
        }


    /**
     * <p>
     * It calculate the matching degree between the antecedent of the rule "regla" and a given example "ejemplo"
     * </p>
     * @param cromosoma int [] The antecedent of the rules in the RB
     * @param regla int The number of the rule in the RB
     * @param ejemplo double [] A given example
     * @return double The matching degree between the example and the antecedent of the rule "regla"
     */
        double Matching_degree(int [] cromosoma, int regla, double[] ejemplo) {
            int k, variable, etiqueta, x1_int, x2_int, tipo;
            double result, valor_ejemplo, grado, x1, x2, step;
            boolean neg;

            /* "k" keeps the begining of the RB */
            k = 1 + ((entradas+1) * numEtiquetas * 3);


            result = 1.0;
            for (variable = 0; variable < entradas; variable++) {
                neg = false;
                grado = 0.0;

                /* We obtain the label asociated to the variable */
                etiqueta = cromosoma[k + ((regla * (entradas+1)) + variable)];
                /* Only if the variable is taken into account */
                if(etiqueta != 0){
                    if(etiqueta < 0){
                        neg = true;
                        etiqueta = Math.abs(etiqueta);
                    }

                    /* We obtain the value of the example with the variable */
                    valor_ejemplo = ejemplo[variable];

                    /* Now, we calculate the x1 and x2 value using the integer values in the DB */
                    x1_int = cromosoma[1 + ((variable * numEtiquetas * 3) + ((etiqueta - 1) * 3))];
                    x2_int = cromosoma[1 + ((variable * numEtiquetas * 3) + ((etiqueta - 1) * 3) + 1)];
                    tipo = cromosoma[1 + ((variable * numEtiquetas * 3) + ((etiqueta - 1) * 3) + 2)];

                    step = (train.getMax(variable) - train.getMin(variable)) / (numEtiquetas + 1);

                    x1 = (etiqueta * step) - ((step * (10 + x1_int)) / (2 * 10) ) + train.getMin(variable);
                    x2 = (etiqueta * step) + ((step * (10 + x2_int)) / (2 * 10) ) + train.getMin(variable);

                    grado = Membership_degree(x1, x2, tipo, neg, valor_ejemplo);

                    if(grado < result){
                            result = grado;
                            }

                    if (result == 0.0) {
                        variable = entradas;
                    }
                }
            }

            return (result);
        }


    /**
     * <p>
     * It calculate the membership degree between the value of a example in a certain  variable and the antecedent of the rule in this variable
     * </p>
     * @param x1 double The lower value for the fuzzy set
     * @param x2 double The upper value for the fuzzy set
     * @param tipo int The type of fuzzy set: Left triangle, Right triangle, ...
     * @param neg boolean TRUE if is the fuzzy set is in the form "not ..."
     * @param x double The value for the given example
     * @return double The membership degree between the value of the example in the variable and the antecedent of the rule in this variable
     */
        double Membership_degree(double x1, double x2, int tipo, boolean neg, double x){
            double result, y;

            result = -1;

            switch(tipo){
            /* Left triangle */
            case 1:
                if(x < x1){
                    result = 1.0;
                }
                else{
                    if(x > x2){
                        result = 0.0;
                    }
                    else{
                        result = (x2 - x) / (x2 - x1);
                    }
                }
                break;
            /* Right triangle */
            case 2:
                if(x < x1){
                    result = 0.0;
                }
                else{
                    if(x > x2){
                        result = 1.0;
                    }
                    else{
                        result = (x - x1) / (x2 - x1);
                    }
                }
                break;
            /* Triangle */
            case 3:
                if(x < x1){
                    result = 0.0;
                }
                else{
                    if(x > x2){
                        result = 0.0;
                    }
                    else{
                        if(x <= ((x2 + x1) / 2)){
                            result = 2 * ((x - x1) / (x2 - x1));
                        }
                        else{
                            result = 2 * ((x2 - x) / (x2 - x1));
                        }
                    }
                }
                break;
            /* Gaussian */
            case 4:
                y  = ((8 * (x - x1)) / (x2 - x1)) - 4;
                result = Math.exp(-0.5 * (y * y));
                break;
            /* Sigmoid */
            case 5:
                y  = (12 * (x - x1)) / (x2 - x1);
                result = 1 / (1 + Math.exp(-y + 6));
                break;
            /* Reverse sigmoid */
            case 6:
                y  = (12 * (x - x1)) / (x2 - x1);
                result = 1 / (1 + Math.exp(-y + 6));
                result = 1 - result;
                break;
            }


            if(neg == true){
                result = 1.0 - result;
            }

            return (result);
        }


    /**
     * <p>
     * It calculate the membership degree for the consequent of the rule, and update the matching degree of each rule
     * if it is greater
     * </p>
     * @param cromosoma int [] The consequent of the rules in the RB
     * @param grado_salida double [] The matching degrees for the antecedents of the rules in the RB
     * @param ejemplo double [] A given example
     * @return double The matching degree between the example and the antecedent and consequent
     */
        double Output_Matching_degree(int [] cromosoma, double [] grado_salida, double valor_ejemplo){
            int i, l, variable, etiqueta, x1_int, x2_int, tipo;
            double result, grado, x1, x2, step;
            boolean neg;

            result = 0.0;
            variable = entradas;

            for(i = (-1 * numEtiquetas); i <= numEtiquetas; i++){
                /* All the label except the null one */
                if(i != 0){
                    if(i < 0){
                        neg = true;
                        l = Math.abs(i) + numEtiquetas - 1;
                        etiqueta = Math.abs(i);
                    }
                    else{
                        neg = false;
                        l = i - 1;
                        etiqueta = i;
                    }

                    /* Now, we calculate the x1 and x2 value using the integer values in the DB */
                    x1_int = cromosoma[1 + ((variable * numEtiquetas * 3) + ((etiqueta - 1) * 3))];
                    x2_int = cromosoma[1 + ((variable * numEtiquetas * 3) + ((etiqueta - 1) * 3) + 1)];
                    tipo = cromosoma[1 + ((variable * numEtiquetas * 3) + ((etiqueta - 1) * 3) + 2)];

                    step = (1.0 - 0.0) / (numEtiquetas + 1);

                    x1 = (etiqueta * step) - ((step * (10 + x1_int)) / (2 * 10) ) + 0.0;
                    x2 = (etiqueta * step) + ((step * (10 + x2_int)) / (2 * 10) ) + 0.0;

                    grado = Membership_degree(x1, x2, tipo, neg, valor_ejemplo);

                    /* The maximum value of the matching degree of the output is given by the matching degree
                       of the antecedent with this fuzzy set */
                    if(grado > grado_salida[l]){
                        grado = grado_salida[l];
                    }

                    /* We keep the greatest matching degree among all the fuzzy sets */
                    if(grado > result){
                            result = grado;
                            }
                    }
                }

        return (result);
        }


    /**
     * <p>
     * It evaluate the RB encoded in the individual "indiv"
     * </p>
     * @return double The correct percentage accuracy in test examples
     */  
        public double eval_test(Individual indiv){
            int i, j, k, l, m, clase, n_pos;
            double grado, y, suma1, suma2, valor_salida, rango, result;
            boolean salir, al_menos_una;
            double [] grado_salida;

            grado_salida = new double [2 * numEtiquetas];

            /* "k" keeps the begining of the RB */
            k = 1 + ((entradas+1) * numEtiquetas * 3);

            /* We calculate the matching degree among each example and each feasible rule */
            if(indiv.num_feasibles != 0){
                n_pos = 0;
                for (i = 0; i < test.getnData(); i++){
//                    System.out.println("\tevaluatePopulationting example " + (i+1));

                    for(j = 0; j < (2 * numEtiquetas); j++){
                        grado_salida[j] = 0.0;
                    }

                    al_menos_una = false;
                    for(j = 0; j < indiv.cromosoma[0]; j++){
                        if(indiv.feasibles[j] == true){
                            grado = Matching_degree(indiv.cromosoma, j, test.getExample(i));

                            /* We look for the fuzzy set in the output variable */
                            m = indiv.cromosoma[k + ((j * (entradas+1)) + entradas)];
                            if(m < 0){
                                l = Math.abs(m) + numEtiquetas - 1;
                            }
                            else{
                                l = m - 1;
                            }

                            /* Upgrade the membership value of the output fuzzy set */
                            if(grado > grado_salida[l]){
                                al_menos_una = true;
                                grado_salida[l] = grado;
                            }
                        }
                    }

//                    System.out.println("\tLlego a defuzificar.");
                    if(al_menos_una == true){
                        /* Defuzzification */
                        y = 0.05;
                        suma1 = suma2 = 0.0;
                        for(j = 0; j < 19; j++){
                            grado = Output_Matching_degree(indiv.cromosoma, grado_salida, y);
                            suma1 += y * grado;
                            suma2 += grado;
                            y += 1.0 / 20.0;
                        }

                        /* Now, we calculate the class of the example */
                        valor_salida = suma1 / suma2;
                        rango = 1.0 / nClasses;
                        suma1 = 0.0;
                        suma2 = rango;
                        clase = 0;
                        salir = false;
                        do{
                            if((valor_salida >= suma1) && (valor_salida < suma2)){
                                salir = true;
                            }
                            else{
                                clase++;
                                suma1 = suma2;
                                suma2 += rango;
                            }
                        }
                        while(salir == false);


                        /* Check if the asigned class is equal to the one in the example it is a
                        sucess. Otherwise, it is an error */
                        if (clase == test.getOutputAsInteger(i)) {
                            n_pos++;
                        }
                    }
                }

                result = ((double) n_pos / test.getnData()) * 100.0;
            }
            else{
                result = 0.0001;
            }

            return(result);
        }


    /**
     * <p>
     * It prints the current RB encoded in the individual "indiv" as a String
     * </p>
     * @param indiv Individual The individual enconding the RB
     * @return String RB encoded in the individual "indiv" as a String
     */
        public String Print_Population(Individual indiv){
            int i, j, k, x1_int, x2_int;
            double x1, x2, step;
            String output = new String("");

            output += indiv.num_feasibles + "\n";
            output += entradas + "\t 1\n";

            for(i = 0; i < (entradas+1); i++){
                /* The DB */
                if(i < entradas){
                    output += "\ninput_" + (i + 1) + "\t\t" + numEtiquetas + "\t" + train.getMin(0) + "\t" + train.getMax(i) + "\n\n";
                }
                else{
                    output += "\noutput\t" + numEtiquetas + "\t 0 \t 1 \n\n";
                }

                for(j = 0; j < numEtiquetas; j++){
                    output += "\t";

                    switch(indiv.cromosoma[1 + ((i * numEtiquetas * 3) + (j * 3) + 2)]){
                    case 1:
                        output += "leftTriangle\t";
                        break;
                    case 2:
                        output += "rightTriangle\t";
                        break;
                    case 3:
                        output += "Triangle\t";
                        break;
                    case 4:
                        output += "Gaussian\t";
                        break;
                    case 5:
                        output += "Sigmoid\t\t";
                        break;
                    case 6:
                        output += "reverseSigmoid\t";
                        break;
                    }


                    x1_int = indiv.cromosoma[1 + ((i * numEtiquetas * 3) + (j * 3) + 0)];
                    x2_int = indiv.cromosoma[1 + ((i * numEtiquetas * 3) + (j * 3) + 1)];

                    if(i < entradas){
                        step = (train.getMax(i) - train.getMin(i)) / (numEtiquetas + 1);

                        x1 = ((j + 1) * step) - ((step * (10 + x1_int)) / (2 * 10) ) + train.getMin(i);
                        x2 = ((j + 1) * step) + ((step * (10 + x2_int)) / (2 * 10) ) + train.getMin(i);
                    }
                    else{
                        step = (1.0 - 0.0) / (numEtiquetas + 1);

                        x1 = ((j + 1) * step) - ((step * (10 + x1_int)) / (2 * 10) ) + 0.0;
                        x2 = ((j + 1) * step) + ((step * (10 + x2_int)) / (2 * 10) ) + 0.0;
                    }

                    output += x1 + "\t" + x2 + "\n";
                }
            }

            output += "\n\n";

            k = 1 + ((entradas+1) * numEtiquetas * 3);

            for(i = 0; i < indiv.cromosoma[0]; i++){
                /* The RB */
                if(indiv.feasibles[i] == true){
                    for(j = 0; j < (entradas+1); j++){
                        output += indiv.cromosoma[k + ((i * (entradas+1)) + j)];
                        if(j != entradas){
                            output += "\t";
                        }
                        else{
                            output += "\n";
                        }
                    }
                }
            }

            output += "\n\n";

            return(output);
        }


    /**
     * <p>
     * It generates the output file from a given dataset and stores it in a file
     * </p>
     * @param dataset myDataset input dataset
     * @param indiv Individual The individual enconding the RB     
     * @param filename String the name of the file
     */
        private void doOutput(myDataset dataset, Individual indiv, String filename) {
            int i, j, k, l, m, clase;
            double grado, y, suma1, suma2, valor_salida, rango;
            boolean salir, al_menos_una;
            double[] grado_salida;
            String output = new String("");
            int aciertos = 0;

            output = dataset.copyHeader(); //we insert the header in the output file

            grado_salida = new double[2 * numEtiquetas];

            /* "k" keeps the begining of the RB */
            k = 1 + ((entradas + 1) * numEtiquetas * 3);

            /* We calculate the matching degree among each example and each feasible rule */
            if (indiv.num_feasibles != 0) {
                for (i = 0; i < dataset.getnData(); i++) {
        //                    System.out.println("\tevaluatePopulationting example " + (i+1));

                    for (j = 0; j < (2 * numEtiquetas); j++) {
                        grado_salida[j] = 0.0;
                    }

                    al_menos_una = false;
                    for (j = 0; j < indiv.cromosoma[0]; j++) {
                        if (indiv.feasibles[j] == true) {
                            grado = Matching_degree(indiv.cromosoma, j,
                                                         dataset.getExample(i));

                            /* We look for the fuzzy set in the output variable */
                            m = indiv.cromosoma[k + ((j * (entradas + 1)) + entradas)];
                            if (m < 0) {
                                l = Math.abs(m) + numEtiquetas - 1;
                            } else {
                                l = m - 1;
                            }

                            /* Upgrade the membership value of the output fuzzy set */
                            if (grado > grado_salida[l]) {
                                al_menos_una = true;
                                grado_salida[l] = grado;
                            }
                        }
                    }

        //                    System.out.println("\tLlego a defuzificar.");
                    if (al_menos_una == true) {
                        /* Defuzzification */
                        y = 0.05;
                        suma1 = suma2 = 0.0;
                        for (j = 0; j < 19; j++) {
                            grado = Output_Matching_degree(indiv.cromosoma,
                                    grado_salida, y);
                            suma1 += y * grado;
                            suma2 += grado;
                            y += 1.0 / 20.0;
                        }

                        /* Now, we calculate the class of the example */
                        valor_salida = suma1 / suma2;
                        rango = 1.0 / nClasses;
                        suma1 = 0.0;
                        suma2 = rango;
                        clase = 0;
                        salir = false;
                        do {
                            if ((valor_salida >= suma1) && (valor_salida < suma2)) {
                                salir = true;
                            } else {
                                clase++;
                                suma1 = suma2;
                                suma2 += rango;
                            }
                        } while (salir == false);

                        output += dataset.getOutputAsString(i) + " " +
                                dataset.getOutputValue(clase) + "\n";
                        if (clase == dataset.getOutputAsInteger(i)) {
                            aciertos++;
                        }
                    }
                }

                System.out.println("" + 1.0 * aciertos / dataset.getnData());
                Files.writeFile(filename, output);
            }
        }

}

