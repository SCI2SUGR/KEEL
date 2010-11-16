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

package keel.Algorithms.RE_SL_Methods.P_FCS1;

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
    ArrayList<Individual> Descen;
    ArrayList<Individual> Hijos;
    long semilla;
    int tamPoblacion, n_rulesIndividual, max_n_rulesIndividual, Gen,
            numGeneraciones;
    double probCrossover, probMut;

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
        n_rulesIndividual = Integer.parseInt(parameters.getParameter(2));
        numGeneraciones = Integer.parseInt(parameters.getParameter(3));
        probCrossover = Double.parseDouble(parameters.getParameter(4));
        probMut = Double.parseDouble(parameters.getParameter(5));

        entradas = train.getnInputs();

        Poblacion = new ArrayList<Individual>(tamPoblacion);
        for (int i = 0; i < tamPoblacion; i++) {
            Individual indi = new Individual(n_rulesIndividual, entradas + 1);
            Poblacion.add(indi);
        }

        Poblacion2 = new ArrayList<Individual>(tamPoblacion);

        Descen = new ArrayList<Individual>(2);
        for (int i = 0; i < 2; i++) {
            Individual indi = new Individual(n_rulesIndividual, entradas + 1);
            Poblacion.add(indi);
        }

        Hijos = new ArrayList<Individual>(tamPoblacion);
    }

    /**
     * <p>
     * It launches the algorithm
     * </p>
     */
    public void execute() {
        int i, j, num;
        double fitness;

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

            /* evaluatePopulationtion of the initial population */
            System.out.println("evaluatePopulationting the initial population.");
            evaluatePopulation();

            /* Evolutionary process */
            System.out.println("Starting the evolutionary process.");
            do {
                /* Reproduction stage */
                Reproduction();

                /* we increment the counter */
                Gen++;
                System.out.println("Iteration: " + Gen + ". Best fitness: " +
                                   (1.0 / Poblacion.get(0).fitness));
            } while (Gen <= numGeneraciones);

            String salida = new String("");
            salida += Print_Population();

            salida += "MSE Training:\t" + (1.0 / Poblacion.get(0).fitness) +
                    "%\n";
            salida += "MSE Test:\t\t" + (1.0 / eval(test, Poblacion.get(0))) +
                    "%\n\n";

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
        int i, j, k;

        for (i = 0; i < tamPoblacion; i++) {
            for (j = 0; j < n_rulesIndividual; j++) {
                for (k = 0; k <= entradas; k++) {
                    Poblacion.get(i).RuleBase[j].memfunctions[k].center =
                            Randomize.RanddoubleClosed(train.getMin(k),
                            train.getMax(k));
                    Poblacion.get(i).RuleBase[j].memfunctions[k].width =
                            Randomize.RanddoubleClosed(0.0,
                            ((2.0 * (train.getMax(k) - train.getMin(k))) /
                             (Math.sqrt(n_rulesIndividual))));
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
        int i, j;

        for (i = 0; i < tamPoblacion; i++) {
            /* if the chromosome aren't evaluated, it's evaluate */
            if (Poblacion.get(i).n_e == 1) {
//                System.out.println("evaluatePopulationndo el individuo " + (i+1));
                Poblacion.get(i).fitness = eval(train, Poblacion.get(i));
                Poblacion.get(i).n_e = 0;
            }
        }
    }


    /**
     * <p>
     * It evaluates an individual
     * </p>
     * @param dataset myDataset The set of examples
     * @param indi Invidual The individual being evaluated
     * @return double The fitness of the individual
     */
    public double eval(myDataset dataset, Individual indi) {
        int i;
        double result, suma, fuerza;

        suma = 0.0;
        for (i = 0; i < dataset.getnData(); i++) {
            fuerza = Output_fuzzy_system(dataset, indi, dataset.getExample(i));
            suma += Math.pow(dataset.getOutputAsReal(i) - fuerza, 2.0);
        }

        result = suma / dataset.getnData();

        /* We want to have a maximization problem so, we invert the error */
        if (result != 0.0) {
            result = 1.0 / result;
        } else {
            result = 0.0;
        }

        return (result);
    }


    /**
     * <p>
     * It calculate the output of the fuzzy system encoded in the individual for a given example
     * </p>
     * @param dataset myDataset The set of examples
     * @param indi Individual The individual enconding several fuzzy rules
     * @param ejemplo double [] A given example
     * @return double The output value obtained as output of the fuzzy system for a given example
     */
    double Output_fuzzy_system(myDataset dataset, Individual indi,
                                 double[] ejemplo) {
        int i;
        double result, suma1, suma2, omega, y;

        suma1 = suma2 = 0.0;
        for (i = 0; i < indi.num_reglas; i++) {
            omega = Matching_degree(indi.RuleBase[i], ejemplo);
            y = Output_Value(dataset, indi.RuleBase[i], omega);
            suma1 += (omega * y);
            suma2 += omega;
        }

        if (suma2 != 0.0) {
            result = suma1 / suma2;
        } else {
//                result = 0.0;
            result = ((dataset.getMax(entradas) - dataset.getMin(entradas)) /
                      2.0);
        }

        return (result);
    }

    /**
     * <p>
     * It calculate the matching degree between the antecedent of the rule and a given example
     * </p>
     * @param reg Rule The rule containing several gaussian fuzzy sets
     * @param ejemplo double [] A given example
     * @return double The matching degree between the example and the antecedent of the rule
     */
    double Matching_degree(Rule reg, double[] ejemplo) {
        int i;
        double result, suma, numerador, denominador;

        suma = 0.0;
        for (i = 0; i < entradas; i++) {
            numerador = Math.pow((ejemplo[i] - reg.memfunctions[i].center), 2.0);
            denominador = Math.pow(reg.memfunctions[i].width, 2.0);
            suma += (numerador / denominador);
        }

        suma *= -1.0;
        result = Math.exp(suma);

        return (result);
    }

    /**
     * <p>
     * Defuzzification value for the rule 
     * </p>
     * @param dataset myDataset The set of examples     
     * @param reg Rule The rule containing several gaussian fuzzy sets
     * @param grado_entrada double The matching degree between a given example and the antecedent of the rule
     * @return double The defuzzification value for the rule
     */
    double Output_Value(myDataset dataset, Rule reg, double grado_entrada) {
        int i;
        double result, ancho_intervalo, suma1, suma2, y, grado, inter;
        double numerador, denominador, suma;

        ancho_intervalo = dataset.getMax(entradas) - dataset.getMin(entradas);
        inter = (ancho_intervalo / 20.0);

        /* Defuzzification */
        y = dataset.getMin(entradas) + inter;
        suma1 = suma2 = 0.0;
        for (i = 0; i < 19; i++) {
            numerador = Math.pow((y - reg.memfunctions[entradas].center), 2.0);
            denominador = Math.pow(reg.memfunctions[entradas].width, 2.0);
            suma = (numerador / denominador);
            suma *= -1.0;
            grado = Math.exp(suma);

            if (grado > grado_entrada) {
                grado = grado_entrada;
            }

            suma1 += y * grado;
            suma2 += grado;
            y += inter;
        }

        /* Now, we calculate the value for the output */
        if (suma2 != 0.0) {
            result = suma1 / suma2;
        } else {
            result = ((dataset.getMax(entradas) - dataset.getMin(entradas)) /
                      2.0);
        }

        return (result);
    }


    /**
     * <p>
     * It performs the reproduction stage
     * </p>
     */
    void Reproduction() {
        int i;
        int[] padres = new int[(tamPoblacion / 10)];

        /* First, the individual in the population are ordered according to their fitness */
        Collections.sort(Poblacion);

        Poblacion2.clear();
        Hijos.clear();

        /* The worst 10% of the individual in the population is replaced by the new offspring */
        for (i = 0; i < (tamPoblacion - (tamPoblacion / 10)); i++) {
            Individual indi = new Individual(Poblacion.get(i));
            Poblacion2.add(indi);
        }

        /* Parents are selected */
        padres = Selection();

        for (i = 0; i < (tamPoblacion / 10); i += 2) {
            /* Crossover operator */
            Crossover(padres[i], padres[i + 1]);

            /* Mutation operator */
            Mutation();

            /* Now, we add the 2 new individuals to the population of children */
            Hijos.add(Descen.get(0));
            Hijos.add(Descen.get(1));
        }

        /* Create the population for the next generation */
        Poblacion.clear();
        for (i = 0; i < Poblacion2.size(); i++) {
            Individual indi = new Individual(Poblacion2.get(i));
            Poblacion.add(indi);
        }
        for (i = 0; i < Hijos.size(); i++) {
            Individual indi = new Individual(Hijos.get(i));
            Poblacion.add(indi);
        }

        /* evaluatePopulationtion of new children */
        evaluatePopulation();

        /* The population is ordered again according the fitness of its individual */
        Collections.sort(Poblacion);
    }


    /**
     * <p>
     * It selects the parents that will participate in the evolutionary process (by rank based roulette wheel selection).
     * </p>
     * @param int[] The positions in the population for the selected parents
     */
    int[] Selection() {
        int i, j, k;
        double rank_min, rank_max, u;
        int[] parents;
        double[] sample;

        rank_min = 0.75;
        rank_max = 2.0 - rank_min;

        sample = new double[tamPoblacion];
        parents = new int[(tamPoblacion / 10)];

        for(i = 0; i < tamPoblacion; i++){
                if(i != 0){
                        sample[i] = sample[i-1] + (rank_max - (rank_max - rank_min) * i / (double)(tamPoblacion-1)) / (double)tamPoblacion;
                        }
                else{
                        sample[i] = (rank_max - (rank_max - rank_min) * i / (double)(tamPoblacion-1)) / (double)tamPoblacion;
                        }
                }

        /* select the parent for creating children */
        for(k = 0; k < (tamPoblacion / 10); k++){
            u = Randomize.Rand();
            i = 0;
            for (j = 0; j < tamPoblacion; j++) {
                for (; sample[i] < u; i++);
                parents[k] = i;
            }
        }

        return (parents);
    }


    /**
     * <p>
     * It applies a Ordered Two-point crossover genetic operator between individual in position "madre" and "padre" in the population.
     * The new generated children (2 descendants) are added in a population of descendants
     * </p>
     * @param madre int Parent number 1 is in position "madre" in the population
     * @param padre int Parent number 2 is in position "padre" in the population
     */    
    void Crossover(int madre, int padre) {
        int i, k, num1, num2, ind1, ind2;
        Individual Hijo1;
        Individual Hijo2;
        double u, R1, R2;
        int[] madre_reglas;
        int[] padre_reglas;
        double[] C1;
        double[] C2;
        boolean verifica;

        madre_reglas = new int[Poblacion.get(madre).num_reglas];
        for (i = 0; i < Poblacion.get(madre).num_reglas; i++) {
            madre_reglas[i] = -1;
        }
        padre_reglas = new int[Poblacion.get(padre).num_reglas];
        for (i = 0; i < Poblacion.get(padre).num_reglas; i++) {
            padre_reglas[i] = -1;
        }

        C1 = new double[entradas];
        C2 = new double[entradas];

        Descen.clear();

        u = Randomize.RandClosed();
        if (u < probCrossover) {
            /* We calculate the two crosspoints vectors C1 and C2 */
            R1 = Randomize.RandClosed();
            R2 = Randomize.RandClosed();
            for (i = 0; i < entradas; i++) {
                C1[i] = train.getMin(i) +
                        (train.getMax(i) - train.getMin(i)) *
                        (Math.pow(R1, 1.0 / entradas));
                C2[i] = C1[i] +
                        (train.getMax(i) - train.getMin(i)) *
                        (Math.pow(R2, 1.0 / entradas));
            }

            num1 = num2 = 0;
            /* According to the crossover operator, we look which rules from the mother are going to be part of
               each one of the two children */
            for (k = 0; k < Poblacion.get(madre).num_reglas; k++) {
                verifica = true;
                for (i = 0; (i < entradas) && (verifica == true); i++) {
                    verifica = false;
                    if ((Poblacion.get(madre).RuleBase[k].memfunctions[i].
                         center > C1[i]) &&
                        (Poblacion.get(madre).RuleBase[k].memfunctions[i].
                         center < C2[i])) {
                        verifica = true;
                    }
                    if ((Poblacion.get(madre).RuleBase[k].memfunctions[i].
                         center + train.getMax(i) - train.getMin(i)) < C2[i]) {
                        verifica = true;
                    }
                }

                if (verifica == true) {
                    madre_reglas[k] = 1;
                    num1++;
                } else {
                    madre_reglas[k] = 2;
                    num2++;
                }
            }

            /* According to the crossover operator, we look which rules from the father are going to be part of
               each one of the two children */
            for (k = 0; k < Poblacion.get(padre).num_reglas; k++) {
                verifica = true;
                for (i = 0; (i < entradas) && (verifica == true); i++) {
                    verifica = false;
                    if ((Poblacion.get(padre).RuleBase[k].memfunctions[i].
                         center > C1[i]) &&
                        (Poblacion.get(padre).RuleBase[k].memfunctions[i].
                         center < C2[i])) {
                        verifica = true;
                    }
                    if ((Poblacion.get(padre).RuleBase[k].memfunctions[i].
                         center + train.getMax(i) - train.getMin(i)) < C2[i]) {
                        verifica = true;
                    }
                }

                if (verifica == true) {
                    padre_reglas[k] = 2;
                    num2++;
                } else {
                    padre_reglas[k] = 1;
                    num1++;
                }
            }

            if ((num1 != 0) && (num2 != 0)) {
                /* We created 2 new children */
                Hijo1 = new Individual(num1, entradas + 1);
                Hijo2 = new Individual(num2, entradas + 1);

                ind1 = ind2 = 0;
                for (k = 0; k < Poblacion.get(madre).num_reglas; k++) {
                    if (madre_reglas[k] == 1) {
                        for (i = 0; i <= entradas; i++) {
                            Hijo1.RuleBase[ind1].memfunctions[i].center =
                                    Poblacion.get(madre).RuleBase[k].
                                    memfunctions[i].center;
                            Hijo1.RuleBase[ind1].memfunctions[i].width =
                                    Poblacion.get(madre).RuleBase[k].
                                    memfunctions[i].width;
                        }
                        ind1++;
                    } else {
                        for (i = 0; i <= entradas; i++) {
                            Hijo2.RuleBase[ind2].memfunctions[i].center =
                                    Poblacion.get(madre).RuleBase[k].
                                    memfunctions[i].center;
                            Hijo2.RuleBase[ind2].memfunctions[i].width =
                                    Poblacion.get(madre).RuleBase[k].
                                    memfunctions[i].width;
                        }
                        ind2++;
                    }
                }

                for (k = 0; k < Poblacion.get(padre).num_reglas; k++) {
                    if (padre_reglas[k] == 1) {
                        for (i = 0; i <= entradas; i++) {
                            Hijo1.RuleBase[ind1].memfunctions[i].center =
                                    Poblacion.get(padre).RuleBase[k].
                                    memfunctions[i].center;
                            Hijo1.RuleBase[ind1].memfunctions[i].width =
                                    Poblacion.get(padre).RuleBase[k].
                                    memfunctions[i].width;
                        }
                        ind1++;
                    } else {
                        for (i = 0; i <= entradas; i++) {
                            Hijo2.RuleBase[ind2].memfunctions[i].center =
                                    Poblacion.get(padre).RuleBase[k].
                                    memfunctions[i].center;
                            Hijo2.RuleBase[ind2].memfunctions[i].width =
                                    Poblacion.get(padre).RuleBase[k].
                                    memfunctions[i].width;
                        }
                        ind2++;
                    }
                }

                /* The 2 new created children are added for appling the mutation operator */
                Descen.add(Hijo1);
                Descen.add(Hijo2);
            } else {
                /* The 2 parents are copied for appling the mutation operator */
                Individual indi = new Individual(Poblacion.get(madre));
                Descen.add(indi);
                Individual indi2 = new Individual(Poblacion.get(padre));
                Descen.add(indi2);
            }
        } else {
            /* The 2 parents are copied for appling the mutation operator */
            Individual indi = new Individual(Poblacion.get(madre));
            Descen.add(indi);
            Individual indi2 = new Individual(Poblacion.get(padre));
            Descen.add(indi2);
        }

    }


    /**
     * <p>
     * It applies mutation genetic operator to the descendants previosly created by crossover operator
     * This operator modifies the center or the width of a selected gaussian fuzzy set in the desdecendat
     * </p>
     */
    void Mutation() {
        int reg, entrada;
        double u, factor;

        /* First child */
        reg = Randomize.RandintClosed(0, Descen.get(0).num_reglas-1);
        entrada = Randomize.RandintClosed(0, entradas);
        factor = Randomize.RanddoubleClosed(0.9, 1.1);

        u = Randomize.RandClosed();
        if (u < probMut) {
            u = Randomize.RandClosed();
            /* We mutate the center of input "entrada" in the rule "reg" of the first child */
            if (u < 0.5) {
                Descen.get(0).RuleBase[reg].memfunctions[entrada].center *=
                        factor;
            }
            /* We mutate the width of input "entrada" in the rule "reg" of the first child */
            else {
                Descen.get(0).RuleBase[reg].memfunctions[entrada].width *=
                        factor;
            }
        }

        /* Second child */
        reg = Randomize.RandintClosed(0, Descen.get(1).num_reglas-1);
        entrada = Randomize.RandintClosed(0, entradas);
        factor = Randomize.RanddoubleClosed(0.9, 1.1);

        u = Randomize.RandClosed();
        if (u < probMut) {
            u = Randomize.RandClosed();
            /* We mutate the center of input "entrada" in the rule "reg" of the first child */
            if (u < 0.5) {
                Descen.get(1).RuleBase[reg].memfunctions[entrada].center *=
                        factor;
            }
            /* We mutate the width of input "entrada" in the rule "reg" of the first child */
            else {
                Descen.get(1).RuleBase[reg].memfunctions[entrada].width *=
                        factor;
            }
        }

    }


    /**
     * <p>
     * It prints the current population as a String
     * </p>
     * @return String The current population as a String
     */
    public String Print_Population() {
        int i, j;
        String output = new String("");

        output += "Rule Base with " + Poblacion.get(0).num_reglas +
                " rules\n\n";

        for (i = 0; i < Poblacion.get(0).num_reglas; i++) {
            output += "Rule " + (i + 1) + ": IF ";
            for (j = 0; j < entradas; j++) {
                output += "X(" + (j + 1) + ") is Gaussian(" +
                        Poblacion.get(0).RuleBase[i].memfunctions[j].center +
                        ", " +
                        Poblacion.get(0).RuleBase[i].memfunctions[j].width +
                        ")";
                if (j != (entradas - 1)) {
                    output += " and ";
                }
            }

            output += " THEN Y is Gaussian(" +
                    Poblacion.get(0).RuleBase[i].memfunctions[entradas].center +
                    ", " +
                    Poblacion.get(0).RuleBase[i].memfunctions[entradas].width +
                    ")\n\n"; ;
        }

        return (output);
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

        for (i = 0; i < dataset.getnData(); i++) {
            fuerza = Output_fuzzy_system(dataset, Poblacion.get(0),
                                           dataset.getExample(i));
            output += (dataset.getOutputAsReal(i) + " " + fuerza + " " + "\n");
        }

        Files.writeFile(filename, output);
    }

}

