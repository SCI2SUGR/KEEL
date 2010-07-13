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

package keel.Algorithms.Genetic_Rule_Learning.LogenPro;

/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada) 01/01/2007
 * @author Modified by Francisco José Berlanga (University of Jaén) 09/12/2008 
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
    String outputTr, outputTst, output;
    double classProb[];
    double attrProb[][][]; //atribute value, atribute position, class
    int nClasses;
    ArrayList<Individual> poblacion;
    long semilla;
    int tamPoblacion, numGeneraciones;
    double min_support;
    double w1, w2;
    double probCross, probMut, probDrop;
    boolean[] tokensGlobal;

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
        output = parameters.getOutputFile(0);

        //Now we parse the parameters, for example:
        semilla = Long.parseLong(parameters.getParameter(0));
        //...
        tamPoblacion = Integer.parseInt(parameters.getParameter(1));
        numGeneraciones = Integer.parseInt(parameters.getParameter(2));
        probCross = Double.parseDouble(parameters.getParameter(3));
        probMut = Double.parseDouble(parameters.getParameter(4));
        probDrop = Double.parseDouble(parameters.getParameter(5));
        min_support = Double.parseDouble(parameters.getParameter(6));
        w1 = Double.parseDouble(parameters.getParameter(7));
        w2 = Double.parseDouble(parameters.getParameter(8));

        tokensGlobal = new boolean[train.getnData()];

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
            //We do here the algorithm's operations
            Randomize.setSeed(semilla);

            System.out.println("Initializing population");
            initializePopulation();
            System.out.println("Evaluating population");
            evaluatePopulation();

            Collections.sort(poblacion);
            /*for (int j = 0; j < poblacion.size(); j++) {
                System.out.println("Rule[" + (j + 1) + "]: " +
                                   poblacion.get(j).printIndividual());
            }
            System.out.println("");*/

            for (int i = 0; i < numGeneraciones; i++) {
                System.out.println("Generation " + (i + 1));
                while (poblacion.size() < 2 * tamPoblacion) {
                    int uno = selection( -1);
                    double aleatorio = Randomize.Rand();
                    if (aleatorio <= probCross) {
                        int dos = selection(uno);
                        //System.out.println("Applying Crossover");
                        crossover(uno, dos);
                    } else if (aleatorio <= (probCross + probMut)) {
                        //System.out.println("Applying Mutation");
                        mutation(uno);
                    } else {
                        //System.out.println("Applying Dropping");
                        dropping(uno);
                    }
                }
                //System.out.println("Evaluating Population");
                evaluatePopulation();

                /*for (int j = 0; j < poblacion.size(); j++) {
                    System.out.println("Rule[" + (j + 1) + "]: " +
                                       poblacion.get(j).printIndividual());
                }
                System.out.println("");
*/
                //System.out.println("Token Competition");
                tokenCompetition();

               /* for (int j = 0; j < poblacion.size(); j++) {
                    System.out.println("Rule[" + (j + 1) + "]: " +
                                       poblacion.get(j).printIndividual());
                }
                System.out.println("");
*/
                //System.out.println("Replacing Redundant Rules");
                replaceRedundantRules();

                //System.out.println("Evaluating Population (again)");
                evaluatePopulation(); //por si generamos alguno nuevo
/*
                for (int j = 0; j < poblacion.size(); j++) {
                    System.out.println("Rule[" + (j + 1) + "]: " +
                                       poblacion.get(j).printIndividual());
                }
                System.out.println("");
*/
                Collections.sort(poblacion);
                //System.out.println("Cutting Population");
                cutPopulation();
                /*for (int j = 0; j < poblacion.size(); j++) {
                    System.out.println("Rule[" + (j + 1) + "]: " +
                                       poblacion.get(j).printIndividual());
                }
                System.out.println("");
*/

            }
            Collections.sort(poblacion);

            //nClasses = train.getnOutputs();

            //Finally we should fill the training and test output files
            double accTr = doOutput(this.val, this.outputTr);
            double accTst = doOutput(this.test, this.outputTst);
            String cadena = new String("");
            System.out.println("Training Accuracy: "+accTr);
            System.out.println("Test Accuracy: "+accTst);
            cadena += "@Training Accuracy: "+accTr+"\nTest Accuracy: "+accTst+"\nRule Base:\n";
            for (int i = 0;
                         (i < poblacion.size()) &&
                         (poblacion.get(i).getFitness() > 0); i++) {
                System.out.println((i + 1) + ":" +
                                   poblacion.get(i).printIndividual());
                cadena += (i + 1) + ":" +
                                   poblacion.get(i).printIndividual()+"\n";
            }
            System.out.println("Algorithm Finished");
            Files.writeFile(output,cadena);
        }
    }

    /**
     * <p>
     * It generates the output file from a given dataset and stores it in a file
     * </p>
     * @param dataset myDataset input dataset
     * @param filename String the name of the file
     * @return The accuracy
     */
    private double doOutput(myDataset dataset, String filename) {
        String output = new String("");
        int aciertos = 0;
        output = dataset.copyHeader(); //we insert the header in the output file
        //We write the output for each example
        for (int i = 0; i < dataset.getnData(); i++) {
            //for classification:
            output += dataset.getOutputAsString(i) + " " +
                    this.classificationOutput(dataset.getExample(i),dataset.getMissing(i)) + "\n";
            if (dataset.getOutputAsString(i).compareTo(this.classificationOutput(dataset.getExample(i),dataset.getMissing(i))) == 0){
                aciertos++;
            }
        }
        Files.writeFile(filename, output);
        return 1.0*aciertos/dataset.getnData();
    }

    /**
     * <p>
     * It returns the algorithm classification output given an input example
     * </p>
     * @param example double[] The input example
     * @param missing boolean [] A boolean array that stores the value "true" if any value of the example is missing
     * @return String the output generated by the algorithm
     */
    private String classificationOutput(double[] example, boolean [] missing) {
        String output = new String("?");
        boolean salir = false;
        /**
          Here we should include the algorithm directives to generate the
          classification output from the input example
         */

        for (int i = 0; (i < poblacion.size()) && (!salir); i++) {
            if (poblacion.get(i).matching(example, missing)) {
                output = poblacion.get(i).getClase();
                salir = true;
            }
        }
        return output;
    }

    /**
     * <p>
     * It initializes each individual in the population
     * </p>
     */
    private void initializePopulation() {
        poblacion = new ArrayList<Individual>(tamPoblacion);

        for (int i = 0; i < tamPoblacion; i++) {
            Individual indi = new Individual(train, min_support, w1, w2);
            poblacion.add(indi);
        }
    }

    /**
     * <p>
     * It evaluates each individual in the population
     * </p>
     */
    private void evaluatePopulation() {
        for (int i = 0; i < poblacion.size(); i++) {
            if (poblacion.get(i).non_evaluated()) {
                poblacion.get(i).evaluate(train);
            }
        }
    }

    /**
     * <p>
     * It selects an individual in the population (by rank based roulette wheel selection). This individual
     * can not be the one in position "positionNo"
     * </p>
     * @param posicionNo int Invididual in position "positionNo" is not allowed to be selected
     */
    private int selection(int posicionNo) {
        int i = 0;
        int posicion = 0;
        double rank_min = 0.75;
        double rank_max = 2.0 - rank_min;
        double[] Ruleta = new double[tamPoblacion];

        /* Ordenamos la poblacion de mayor a menor rango */
        Collections.sort(poblacion);

        /* Calculamos la probabilidad de selection de cada individuo mediante
         el ranking lineal en funcion de su posicion en el orden y construimos
           la ruleta */
        for (i = 0; i < tamPoblacion; i++) {
            if (i != 0) {
                Ruleta[i] = Ruleta[i - 1] +
                            (rank_max -
                             (rank_max - rank_min) * i /
                             (double) (tamPoblacion - 1)) /
                            (double) tamPoblacion;
            } else {
                Ruleta[i] = (rank_max -
                             (rank_max - rank_min) * i /
                             (double) (tamPoblacion - 1)) /
                            (float) tamPoblacion;
            }
        }

        boolean salir = false;

        while (!salir) {
            double u = Randomize.Rand();
            posicion = 0;
            for (i = 0; i < tamPoblacion; i++) {
                for (; Ruleta[posicion] < u; posicion++) {
                    ;
                }
            }
            if (posicion != posicionNo) {
                salir = true;
            }
        }

        return posicion;
    }

    /**
     * <p>
     * It applies crossover genetic operator between individual in position "pos1" and "pos2" in the population.
     * The new generated child is added at the end of the population
     * </p>
     * @param pos1 int Parent number 1 is in position "pos1" in the population
     * @param pos2 int Parent number 2 is in position "pos2" in the population
     */
    private void crossover(int pos1, int pos2) {
        Individual padre = poblacion.get(pos1);
        Individual madre = poblacion.get(pos2);
        Individual hijo = new Individual(padre);

        //System.out.println("Cruce (" + pos1 + "," + pos2 + ") Hijo (antes): " +
        //                   hijo.printIndividual());

        int atributo;
        boolean salir = false;
        do {
            atributo = Randomize.RandintClosed(0, train.getnInputs() + 1);
            if (atributo == train.getnInputs()) {
                salir = true;
            }
        } while (!salir && madre.isAny(atributo));

        if (!salir) {
            hijo.setCondition(atributo, madre.getCondition(atributo));
        } else {
            hijo.clase = madre.clase;
        }

       // int atributo;
       // atributo = Randomize.RandintClosed(0, train.getnInputs());
       //  hijo.setCondition(atributo, madre.getCondition(atributo));

        //System.out.println("Cruce (" + pos1 + "," + pos2 + ") Hijo (después): " +
        //                   hijo.printIndividual());
        poblacion.add(hijo);
    }

    /**
     * <p>
     * It applies mutation genetic operator to the individual in position "pos" in the population.
     * The new generated child is added at the end of the population
     * </p>
     * @param pos int Parent is in position "pos" in the population
     */
    private void mutation(int pos) {
        Individual padre = poblacion.get(pos);
        Individual hijo = new Individual(padre);
        //System.out.println("Mutación (" + pos + ") Hijo (antes): " +
        //                   hijo.printIndividual());
        int atributo = Randomize.RandintClosed(0, train.getnInputs() + 1);
        //System.out.println("Tiene "+train.getnInputs()+ " entradas ("+atributo+")");
        if (atributo == train.getnInputs()) {
            hijo.assignNewClass(train);
        } else {
            hijo.assignConditionNoAny(atributo, train);
        }
        //System.out.println("Mutación (" + pos + ") Hijo (después): " +
        //                   hijo.printIndividual());
        poblacion.add(hijo);
        //padre.printIndividual();
        //hijo.printIndividual();
    }
    
    
    /**
     * <p>
     * It applies dropping condition genetic operator to the individual in position "pos" in the population.
     * The new generated child is added at the end of the population
     * </p>
     * @param pos int Parent is in position "pos" in the population
     */
    private void dropping(int pos) {
        int atributo = 0;
        Individual padre = poblacion.get(pos);
        if (padre.applicableDropping(train.getnInputs())) {
        //if (padre.applicableDropping(0)) {
            Individual hijo = new Individual(padre);
            //System.out.println("Dropping (" + pos + ") Hijo (antes): " +
            //                   hijo.printIndividual());
            do {
                atributo = Randomize.RandintClosed(0, train.getnInputs());
            } while (hijo.isAny(atributo));
            hijo.setAny(atributo, train.nameVar(atributo));
            //System.out.println("Dropping (" + pos + ") Hijo (después): " +
            //                   hijo.printIndividual());
            poblacion.add(hijo);
        }
    }

    /**
     * <p>
     * It applies Token Competition diversity mechanism to the population
     * </p>
     */
    private void tokenCompetition() {
        Collections.sort(poblacion);

        for (int i = 0; i < train.getnData(); i++) {
            tokensGlobal[i] = false;
        }
        for (int i = 0; i < poblacion.size(); i++) {
            int count = 0;
            Individual ind = poblacion.get(i);
            if (ind.ideal() == 0) {
                ind.setFitness(0.0);
            } else {
                for (int j = 0; j < train.getnData(); j++) {
                    if ((ind.isCovered(j)) && (!tokensGlobal[j])) {
                        tokensGlobal[j] = true;
                        count++;
                    }
                }
                ind.setFitness(ind.getFitness() * (1.0 * count / ind.ideal()));
            }
        }
    }

    /**
     * <p>
     * It eliminates redundant rules (rules with their fitness equal to zero after Token Competition)
     * If some of the training examples remain with their tokens free, a new rule (containing of the the variables)
     * is generated for cover them
     * </p>
     */
    private void replaceRedundantRules() {
        int i;
        ArrayList libres = new ArrayList();
        for (int j = 0; j < tokensGlobal.length; j++) {
            if (!tokensGlobal[j]) {
                libres.add(new Integer(j));
            }
        }
        for (i = 0;
                 (i < poblacion.size()) && (poblacion.get(i).getFitness() > 0);
                 i++) {
            ;
        }
        for (int j = 0; (j < libres.size()) && (i + j < tamPoblacion); j++) {
            poblacion.get(i +
                          j).replace(train.getExample(((Integer) libres.get(
                                  j)).
                    intValue()), train);
        }
    }

    /**
     * <p>
     * It sets the population size to its half
     * </p>
     */
    private void cutPopulation() {
        for (int i = (tamPoblacion * 2) - 1; i >= tamPoblacion; i--) {
            poblacion.remove(i);
        }
    }
}

