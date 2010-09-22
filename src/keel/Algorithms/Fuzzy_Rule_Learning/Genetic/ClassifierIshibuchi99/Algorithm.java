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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierIshibuchi99;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @author Modified by Alberto Fernández (University of Jaén) 22/09/2010
 * @version 1.1
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
    String outputTr, outputTst, outputBD, outputBR;
    double classProb[];
    double attrProb[][][]; //atribute value, atribute position, class
    int nClasses, entradas;
    ArrayList<Individual> Poblacion;
    ArrayList<Individual> Poblacion2;
    ArrayList<Individual> Hijos;
    ArrayList<Individual> MejorPoblacion;
    double[] Particiones_difusas;
    long semilla;
    int tamPoblacion, nRep, num_evaluaciones, numEtiquetas, n_eval;
    int son, mum, dad, total_reglas, Num_var, Num_cond;
    double probCross, probMut, probDontCare, fitness, fitness_mejor_pob,
    porcen1, porcen2;

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
        outputBD = parameters.getOutputFile(0);
        outputBR = parameters.getOutputFile(1);

        //Now we parse the parameters, for example:
        semilla = Long.parseLong(parameters.getParameter(0));
        //...
        numEtiquetas = Integer.parseInt(parameters.getParameter(1));
        tamPoblacion = Integer.parseInt(parameters.getParameter(2));
        num_evaluaciones = Integer.parseInt(parameters.getParameter(3));
        nRep = Integer.parseInt(parameters.getParameter(4));
        probCross = Double.parseDouble(parameters.getParameter(5));
        probMut = Double.parseDouble(parameters.getParameter(6));
        probDontCare = Double.parseDouble(parameters.getParameter(7));

        entradas = train.getnInputs();
        nClasses = train.getnClasses();
        Particiones_difusas = new double[3 * numEtiquetas * entradas];

        Poblacion = new ArrayList<Individual>(tamPoblacion);
        Poblacion.clear();
        for (int i = 0; i < tamPoblacion; i++) {
            Individual indi = new Individual(entradas);
            Poblacion.add(indi);
        }

        Poblacion2 = new ArrayList<Individual>(tamPoblacion);
        Poblacion2.clear();

        MejorPoblacion = new ArrayList<Individual>(tamPoblacion);
//        MejorPoblacion.clear();
//        for (int i = 0; i < tamPoblacion; i++) {
//            Individual indi = new Individual(entradas);
//            MejorPoblacion.add(indi);
//        }

        Hijos = new ArrayList<Individual>(nRep + 1);
//        Hijos.clear();
//        for (int i = 0; i < (nRep + 1); i++) {
//            Individual indi = new Individual(entradas);
//            Hijos.add(indi);
//        }
    }

    /**
     * <p>
     * It launches the algorithm
     * </p>
     */
    public void execute() {
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found, either the data-set have numerical values or missing values.");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } else {
            total_reglas = tamPoblacion;

            //We do here the algorithm's operations
            Randomize.setSeed(semilla);

            Initial_fuzzy_partition();

            System.out.println("Creating the initial population.");
            initializePopulation();
            evaluatePopulation();

            fitness_mejor_pob = Training_accuracy();
            MejorPoblacion.clear();
            Poblacion2.clear();
            for (int i = 0; i < tamPoblacion; i++) {
                Individual indi = new Individual(Poblacion.get(i));
                MejorPoblacion.add(i, indi);

                Individual indi2 = new Individual(Poblacion.get(i));
                Poblacion2.add(i, indi2);
            }

            n_eval = 0;
            System.out.println("Starting the evolutionary process.");
            while (n_eval < num_evaluaciones) {
                Poblacion.clear();
                for (int i = 0; i < tamPoblacion; i++) {
                    Individual indi = new Individual(Poblacion2.get(i));
                    Poblacion.add(i, indi);
                }

                Hijos.clear();
                son = 0;
                while (son < nRep) {
                    Selection();

                    Crossover(Poblacion.get(mum), Poblacion.get(dad));

                    Mutation();
                    Class_and_certainty_degree(Hijos.get(son));

                    son++;

                    Mutation();
                    Class_and_certainty_degree(Hijos.get(son));

                    son++;
                }

                Replace_rules();

                evaluatePopulation();

                fitness = Training_accuracy();
                if (fitness > fitness_mejor_pob) {
                    fitness_mejor_pob = fitness;

                    MejorPoblacion.clear();
                    for (int i = 0; i < tamPoblacion; i++) {
                        Individual indi = new Individual(Poblacion.get(i));
                        MejorPoblacion.add(i, indi);
                    }
                }


                Poblacion2.clear();
                for (int i = 0; i < tamPoblacion; i++) {
                    Individual indi = new Individual(Poblacion.get(i));
                    Poblacion2.add(i, indi);
                }
            }

            Poblacion.clear();
            for (int i = 0; i < tamPoblacion; i++) {
                Individual indi = new Individual(MejorPoblacion.get(i));
                Poblacion.add(i, indi);
            }

            Delete_rules_fitness_zero();

            Delete_rules_certainty_degree_zero();

            Num_var = Count_variables();
            Num_cond = Count_conditions();

            porcen1 = Training_accuracy();
            porcen2 = Test_accuracy();

            String salida0 = new String("");
            salida0 += Print_Partitions();

            Files.writeFile(outputBD, salida0);

            String salida = new String("");
            salida += Print_Population();

            salida += "Training Accuracy:\t" + porcen1 + "%\n";
            salida += "Test Accuracy:\t\t" + porcen2 + "%\n\n";

            Files.writeFile(outputBR, salida);

            doOutput(this.val, this.outputTr);
            doOutput(this.test, this.outputTst);

            System.out.println("Algorithm Finished.");
        }
    }


    /**
     * <p>
     * It creates the initial fuzzy partition (using triangular fuzzy sets)
     * </p>
     */
    void Initial_fuzzy_partition() {
        int i, j, k;
        double dist;

        k = 0;
        for (i = 0; i < entradas; i++) {
            dist = train.getMax(i) - train.getMin(i);
            dist /= (numEtiquetas - 1);

            Particiones_difusas[k] = train.getMin(i) - dist;
            Particiones_difusas[k + 1] = train.getMin(i);
            Particiones_difusas[k + 2] = Particiones_difusas[k + 1] + dist;
            k += 3;
            for (j = 0; j < (numEtiquetas - 1); j++) {
                Particiones_difusas[k] = Particiones_difusas[k - 2];
                Particiones_difusas[k + 1] = Particiones_difusas[k - 1];
                Particiones_difusas[k +
                        2] = Particiones_difusas[k - 1] + dist;
                k += 3;
            }
        }
    }

    /**
     * <p>
     * It initializes each individual in the population
     * </p>
     */
    void initializePopulation() {
        int i, j;

        for (j = 0; j < tamPoblacion; j++) {
            for (i = 0; i < entradas; i++) {
                Poblacion.get(j).Arbol[i] = Random_Label();
            }
            Class_and_certainty_degree(Poblacion.get(j));
        }
    }


    /**
     * <p>
     * It randomly obtains a label number for a given variable or the Don't Care label
     * </p>
     * @return The label number or -1 if the Don't Care label is selected
     */
    int Random_Label() {
        int result;
        double u;

        u = Randomize.Rand();
        if (u < probDontCare) {
            result = -1;
        } else {
            result = Randomize.RandintClosed(0, numEtiquetas-1);
        }

        return (result);
    }

    /**
     * <p>
     * It heuristically calculate the best class for the individual, and also calculate its certainty degree
     * </p>
     */
    void Class_and_certainty_degree(Individual indiv) {
        int i, j, clase;
        double max, max2, grado, grado3, grado4;
        double[] grado2;

        grado2 = new double[nClasses];

        for (j = 0; j < nClasses; j++) {
            grado2[j] = 0.0;
        }

        for (i = 0; i < train.getnData(); i++) {
            grado = Product_Matching_degree(indiv, train.getExample(i));
            clase = train.getOutputAsInteger(i);

            grado2[clase] += grado;
        }

        max = 0.0;
        max2 = 0.0;
        for (j = 0; j < nClasses; j++) {
            if (grado2[j] > max) {
                max2 = max;
                max = grado2[j];
                indiv.clase = j;
            }
        }

        if ((max == max2) || (max == 0.0)) {
            indiv.clase = -1;
        }

        if (indiv.clase != -1) {
            grado = grado3 = grado4 = 0.0;
            for (j = 0; j < nClasses; j++) {
                if (j != indiv.clase) {
                    grado += grado2[j];
                } else {
                    grado4 = grado2[j];
                }

                grado3 += grado2[j];
            }
            grado /= (nClasses - 1);

            if (grado3 > 0.0) {
                indiv.grado_certeza = ((grado4 - grado) / grado3);
            } else {
                indiv.grado_certeza = 0.0;
            }
        } else {
            indiv.grado_certeza = 0.0;
        }
    }


    /**
     * <p>
     * It calculate the matching degree between the antecedent of the rule and a given example (using the product t-norm)
     * </p>
     * @param indiv Individual The individual representing a fuzzy rule
     * @param ejemplo double [] A given example
     * @return double The matching degree between the example and the antecedent of the rule
     */
    double Product_Matching_degree(Individual indiv, double[] ejemplo) {
        int variable, etiqueta, pos;
        double result, valor_ejemplo, grado, x0, x1, x2;

        result = 1.0;
        for (variable = 0; variable < entradas; variable++) {
            grado = 0.0;

            etiqueta = indiv.Arbol[variable];
            valor_ejemplo = ejemplo[variable];

            if (etiqueta != -1) {
                pos = (variable * (numEtiquetas * 3));
                pos += (etiqueta * 3);

                x0 = Particiones_difusas[pos];
                x1 = Particiones_difusas[pos + 1];
                x2 = Particiones_difusas[pos + 2];
                if ((valor_ejemplo > x0) && (valor_ejemplo < x2)) {
                    if (valor_ejemplo < x1) {
                        grado = ((valor_ejemplo - x0) / (x1 - x0));
                    } else {
                        if (valor_ejemplo > x1) {
                            grado = (1 - ((valor_ejemplo - x1) / (x2 - x1)));
                        } else {
                            grado = 1.0;
                        }
                    }
                } else {
                    grado = 0.0;
                }
            } else {
                grado = 1.0;
            }

            result *= grado;

            if (result == 0.0) {
                variable = entradas;
            }
        }

        return (result);
    }


    /**
     * <p>
     * It evaluates each individual in the population
     * </p>
     */
    void evaluatePopulation() {
        int i, j, mejor, clase;
        double max, grado;

        for (j = 0; j < tamPoblacion; j++) {
            Poblacion.get(j).fitness = 0;
        }

        for (i = 0; i < train.getnData(); i++) {
            max = 0.0;
            mejor = -1;
            clase = -1;
            for (j = 0; j < tamPoblacion; j++) {
                grado = Product_Matching_degree(Poblacion.get(j),
                        train.getExample(i));
                grado *= Poblacion.get(j).grado_certeza;

                if (grado > max) {
                    mejor = j;
                    clase = Poblacion.get(j).clase;
                    max = grado;
                }
            }

            if (clase != -1) {
                if (clase == train.getOutputAsInteger(i)) {
                    Poblacion.get(mejor).fitness++;
                }
            }
        }

        n_eval += tamPoblacion;
    }

    /**
     * <p>
     * It calculate the correct percentage accuracy in training examples
     * </p>
     */
    double Training_accuracy() {
        int i, j, k, n_pos, num_max, clases_distintas, clase;
        double porcen, max, grado;
        int[] pos_max;

        pos_max = new int[tamPoblacion];

        n_pos = 0;
        for (i = 0; i < train.getnData(); i++) {
            max = -1.0;
            num_max = 0;
            for (j = 0; j < total_reglas; j++) {
                grado = Product_Matching_degree(Poblacion.get(j),
                        train.getExample(i));
                grado *= Poblacion.get(j).grado_certeza;

                if (grado >= max) {
                    if (grado > max) {
                        num_max = 0;
                    }
                    pos_max[num_max] = j;
                    num_max++;
                    max = grado;
                }
            }

            if(max > 0.0){
                clases_distintas = 0;
                clase = Poblacion.get(pos_max[0]).clase;
                for (j = 1; j < num_max; j++) {
                    if (clase != Poblacion.get(pos_max[j]).clase) {
                        clases_distintas = 1;
                        j = num_max;
                    }
                }

                if (clases_distintas == 0) {
                    if (clase == train.getOutputAsInteger(i)) {
                        n_pos++;
                    }
                }
            }
        }

        porcen = ((double) n_pos / train.getnData()) * 100.0;

        return (porcen);
    }

    /**
     * <p>
     * It calculate the correct percentage accuracy in test examples
     * </p>
     */
    double Test_accuracy() {
        int i, j, k, n_pos, num_max, clases_distintas, clase;
        double porcen, max, grado;
        int[] pos_max;

        pos_max = new int[tamPoblacion];

        n_pos = 0;
        for (i = 0; i < test.getnData(); i++) {
            max = -1.0;
            num_max = 0;
            for (j = 0; j < total_reglas; j++) {
                grado = Product_Matching_degree(Poblacion.get(j),
                        test.getExample(i));
                grado *= Poblacion.get(j).grado_certeza;

                if (grado >= max) {
                    if (grado > max) {
                        num_max = 0;
                    }
                    pos_max[num_max] = j;
                    num_max++;
                    max = grado;
                }
            }

            if(max > 0.0){
                clases_distintas = 0;
                clase = Poblacion.get(pos_max[0]).clase;
                for (j = 1; j < num_max; j++) {
                    if (clase != Poblacion.get(pos_max[j]).clase) {
                        clases_distintas = 1;
                        j = num_max;
                    }
                }

                if (clases_distintas == 0) {
                    if (clase == test.getOutputAsInteger(i)) {
                        n_pos++;
                    }
                }
            }
        }

        porcen = ((double) n_pos / test.getnData()) * 100.0;

        return (porcen);
    }


    /**
     * <p>
     * It selects two parents to participate in the evolutionary process (by rank based roulette wheel selection).
     * </p>
     */
    void Selection() {
        int i, j, k;
        double sum_fitness, peor_fitness, u;
        double[] Ruleta;

        Ruleta = new double[tamPoblacion];

        Collections.sort(Poblacion);

        peor_fitness = Poblacion.get(tamPoblacion - 1).fitness;
        sum_fitness = 0.0;
        for (i = 0; i < tamPoblacion; i++) {
            sum_fitness += (Poblacion.get(i).fitness - peor_fitness);
        }

        if (sum_fitness > 0.0) {
            for (i = 0; i < tamPoblacion; i++) {
                if (i == 0) {
                    Ruleta[i] = ((Poblacion.get(i).fitness - peor_fitness) /
                                 sum_fitness);
                } else {
                    Ruleta[i] = Ruleta[i - 1] +
                                ((Poblacion.get(i).fitness - peor_fitness) /
                                 sum_fitness);
                }
            }

            for (k = 0; k < 2; k++) {
                u = Randomize.RanddoubleClosed(0.0, Ruleta[tamPoblacion - 1]);
                i = 0;
                for (j = 0; j < tamPoblacion; j++) {
                    for (; Ruleta[i] < u; i++) {
                        ;
                    }
                    if (k == 0) {
                        mum = i;
                    } else {
                        dad = i;
                    }
                }
            }
        } else {
            mum = Randomize.RandintClosed(0, tamPoblacion-1);
            do {
                dad = Randomize.RandintClosed(0, tamPoblacion-1);
            } while (mum == dad);
        }
    }


    /**
     * <p>
     * It applies a crossover genetic operator between individual in position "madre" and "padre" in the population.
     * The new generated children (2 descendants) are added in a population of descendants
     * </p>
     * @param madre int Parent number 1 is in position "madre" in the population
     * @param padre int Parent number 2 is in position "padre" in the population
     */   
    void Crossover(Individual Madre, Individual Padre) {
        int i;
        Individual Hijo1 = new Individual(entradas);
        Individual Hijo2 = new Individual(entradas);

        if (Randomize.Rand() < probCross) {
            for (i = 0; i < entradas; i++) {
                if (Randomize.Rand() < 0.5) {
                    Hijo1.Arbol[i] = Madre.Arbol[i];
                    Hijo2.Arbol[i] = Padre.Arbol[i];
                } else {
                    Hijo2.Arbol[i] = Madre.Arbol[i];
                    Hijo1.Arbol[i] = Padre.Arbol[i];
                }
            }
        } else {
            for (i = 0; i < entradas; i++) {
                Hijo1.Arbol[i] = Madre.Arbol[i];
                Hijo2.Arbol[i] = Padre.Arbol[i];
            }
        }

        Hijos.add(son, Hijo1);
        Hijos.add(son + 1, Hijo2);
    }


    /**
     * <p>
     * It applies mutation genetic operator 
     * </p>
     */
    void Mutation() {
        int i, valor_old, valor_new;
        Individual Hijo = new Individual(entradas);

        for (i = 0; i < entradas; i++) {
            if (Randomize.Rand() < probMut) {
                valor_old = Hijos.get(son).Arbol[i];
                do {
                    valor_new = Random_Label();
                } while (valor_old == valor_new);

                Hijo.Arbol[i] = valor_new;
            } else {
                Hijo.Arbol[i] = Hijos.get(son).Arbol[i];
            }
        }

        Hijos.add(son, Hijo);
    }


    /**
     * <p>
     * It replaces the nRep (a prefixed number of rules) worst individual in the population by the new generated descendants
     * </p>
     */
    void Replace_rules() {
        int i, j, tam, pos;

        if (nRep < tamPoblacion) {
            tam = nRep;
            pos = tamPoblacion - nRep;
        } else {
            tam = tamPoblacion;
            pos = 0;
        }

        for (i = pos, j = 0; i < (pos + tam); i++, j++) {
            Poblacion.add(i, Hijos.get(j));
        }
    }


    /**
     * <p>
     * It deletes those rules in the population which their fitness equal to zero
     * </p>
     */
    void Delete_rules_fitness_zero() {
        int i, pos;

        Collections.sort(Poblacion);

        pos = total_reglas;
        if (Poblacion.get(0).fitness != 0) {
            for (i = tamPoblacion - 1; i >= 0; i--) {
                if (Poblacion.get(i).fitness != 0) {
                    pos = i + 1;
                    i = -1;
                }
            }
        } else {
            pos = 0;
        }

        total_reglas = pos;
    }


    /**
     * <p>
     * It deletes those rules in the population which their certainty degree equal to zero
     * </p>
     */
    void Delete_rules_certainty_degree_zero() {
        int i;

        Poblacion2.clear();
        for (i = 0; i < total_reglas; i++) {
            if(Poblacion.get(i).grado_certeza != 0.0){
                Individual indi = new Individual(Poblacion.get(i));
                Poblacion2.add(i, indi);
            }
        }

        Poblacion.clear();
        for (i = 0; i < Poblacion2.size(); i++) {
            Individual indi = new Individual(Poblacion2.get(i));
            Poblacion.add(i, indi);
        }

        total_reglas = Poblacion.size();
    }


    /**
     * <p>
     * It counts the number of variables in all the rule set
     * </p>
     * @return int The number of variables used in all the rules of the learned rule set
     */
    int Count_variables() {
        int i, j, num;

        num = 0;
        for (i = 0; i < total_reglas; i++) {
            for (j = 0; j < entradas; j++) {
                if (Poblacion.get(i).Arbol[j] != -1) {
                    num++;
                }
            }
        }

        return (num);
    }


    /**
     * <p>
     * It counts the number of conditions (labels) in all the rule set
     * </p>
     * @return int The number of conditions (labels) used in all the rules of the learned rule set
     */
    int Count_conditions() {
        int i, j, num;

        num = 0;
        for (i = 0; i < total_reglas; i++) {
            for (j = 0; j < entradas; j++) {
                if (Poblacion.get(i).Arbol[j] != -1) {
                    num++;
                }
            }
        }

        return (num);
    }


    /**
     * <p>
     * It prints the current population as a String
     * </p>
     * @return String The current population as a String
     */
    String Print_Population() {
        int i, j, antes;
        String output = new String("");

        Collections.sort(Poblacion);

        output += "\nRule base with " + total_reglas + " rules, " + Num_var +
                " variables and " + Num_cond + " labels.\n";

        output += "-----------------------------------------------------------------------------------\n\n";

        for (i = 0; i < total_reglas; i++) {
            antes = 0;
            output += "Rule " + (i + 1) + ":\n\n";
            output += "If ";
            for (j = 0; j < entradas; j++) {
                if (Poblacion.get(i).Arbol[j] != -1) {
                    if (antes == 1) {
                        output += "and ";
                    }
                    antes = 1;

                    output += "\"X" + (j + 1) + " is ";
                    output += "Label " + (Poblacion.get(i).Arbol[j] + 1) +
                            "\" ";
                }
            }

            if (Poblacion.get(i).clase != -1) {
                output += "then Class is " + train.getOutputValue(Poblacion.get(i).clase) + " ";
            } else {
                output += "then Class is \"Empty\" ";
            }
            output += "with certainty degree " + Poblacion.get(i).grado_certeza +
                    "\n\n";
            output += "Fitness: " + Poblacion.get(i).fitness + "\n";
            output += "\n\n";
        }

        output += "\n";

        return (output);
    }


    /**
     * <p>
     * It prints the fuzzy partition as a String
     * </p>
     * @return String The fuzzy partition as a String
     */
    String Print_Partitions() {
        int i, j, k;
        String output = new String("");

        output += "Fuzzy Partition:\n";
        output += "----------------\n";

        k = 0;
        for (i = 0; i < entradas; i++) {
            output += "\nVariable X" + (i + 1) + ":\n\n";

            for (j = 0; j < numEtiquetas; j++) {
                output += "\tLabel" + (j + 1) + ":\t (" + Particiones_difusas[k] +
                        ", " + Particiones_difusas[k + 1] + ", " +
                        Particiones_difusas[k + 2] + ")\n";
                k += 3;
            }
        }

        output += "\n----------------------------------------------\n\n\n\n";

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
        int i, j, k, n_pos, num_max, clases_distintas, clase;
        double porcen, max, grado;
        int[] pos_max;
        String output = new String("");
        int aciertos = 0;

        pos_max = new int[tamPoblacion];

        output = dataset.copyHeader(); //we insert the header in the output file

        //We write the output for each example
        for (i = 0; i < dataset.getnData(); i++) {
            max = -1.0;
            num_max = 0;
            for (j = 0; j < total_reglas; j++) {
                grado = Product_Matching_degree(Poblacion.get(j),
                        dataset.getExample(i));
                grado *= Poblacion.get(j).grado_certeza;

                if (grado >= max) {
                    if (grado > max) {
                        num_max = 0;
                    }
                    pos_max[num_max] = j;
                    num_max++;
                    max = grado;
                }
            }

            if(max > 0.0){
                clases_distintas = 0;
                clase = Poblacion.get(pos_max[0]).clase;
                for (j = 1; j < num_max; j++) {
                    if (clase != Poblacion.get(pos_max[j]).clase) {
                        clases_distintas = 1;
                        j = num_max;
                    }
                }

                if (clases_distintas == 0) {
                    output += dataset.getOutputAsString(i) + " " + dataset.getOutputValue(clase) + "\n";
                    if (clase == dataset.getOutputAsInteger(i)) {
                        aciertos++;
                    }
                }
                else{
                    output += dataset.getOutputAsString(i) + " ?\n";
                }
            }
            else{
                output += dataset.getOutputAsString(i) + " ?\n";
            }
        }
        System.out.println("" + 1.0 * aciertos / dataset.getnData());
        Files.writeFile(filename, output);
    }
}
