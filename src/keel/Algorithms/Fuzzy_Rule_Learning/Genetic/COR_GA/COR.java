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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.COR_GA;

/**
 * <p>Title: COR-GA</p>
 *
 * <p>Description: It contains the implementation of the COR-GA algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */

import java.io.IOException;
import org.core.*;

public class COR {

    myDataset train, val, test;
    String outputTr, outputTst, outputEvo, outputCor;
    long seed;
    boolean deleteRules, learnWeights;
    int weightCrossType, populationSize, nGenerations, nLabels;
    int agrupa_ejem, consec_candid;
    double crossProb, mutProb, a_param;

    int n_max_reglas;

    BaseD baseDatos;
    BaseR baseReglas;

    //int nClasses;

    //We may declare here the algorithm's parameters

    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public COR() {
    }

    /**
     * It reads the data from the input files (training, validation and test) and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public COR(parseParameters parameters) {

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
        somethingWrong = somethingWrong || train.hasMissingAttributes();

        outputTr = parameters.getTrainingOutputFile();
        outputTst = parameters.getTestOutputFile();
        outputEvo = parameters.getOutputFile(0);
        outputCor = parameters.getOutputFile(1);

        //Now we parse the parameters, for example:
        seed = Long.parseLong(parameters.getParameter(0));

        agrupa_ejem = 1;
        if (parameters.getParameter(1).compareToIgnoreCase("CH") == 0) {
            agrupa_ejem = 2;
        }

        consec_candid = 1;
        if (parameters.getParameter(2).compareToIgnoreCase("CH") == 0) {
            consec_candid = 2;
        }

        deleteRules = true;
        if (parameters.getParameter(3).compareToIgnoreCase("NO") == 0) {
            deleteRules = false;
        }

        learnWeights = true;
        if (parameters.getParameter(4).compareToIgnoreCase("NO") == 0) {
            learnWeights = false;
        }

        nLabels = Integer.parseInt(parameters.getParameter(5));

        this.weightCrossType = 1; //MMA
        if (parameters.getParameter(6).compareToIgnoreCase("2pConj") == 0) {
            weightCrossType = 1;
        } else if (parameters.getParameter(6).compareToIgnoreCase("2pDisj") ==
                   0) {
            weightCrossType = 2;
        }

        populationSize = Integer.parseInt(parameters.getParameter(7));
        nGenerations = Integer.parseInt(parameters.getParameter(8));
        this.crossProb = Double.parseDouble(parameters.getParameter(9));
        this.mutProb = Double.parseDouble(parameters.getParameter(10));
        this.a_param = Double.parseDouble(parameters.getParameter(11));

    }

    /**
     * It launches the algorithm
     */
    public void execute() {
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found, either the data-set has missing values.");
            System.err.println("Please remove the examples with missing data or apply a MV preprocessing.");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } else {
            //We do here the algorithm's operations

            Randomize.setSeed(seed);

            n_max_reglas = 1;
            for (int i = 0; i < train.getnVars(); i++) {
                n_max_reglas *= nLabels;
            }

            baseDatos = new BaseD(nLabels, train.getnVars(),
                                  train.devuelveRangos());
            baseDatos.Semantica();

            baseReglas = new BaseR(n_max_reglas, baseDatos, train, learnWeights,
                                   deleteRules);

            Espacio subespacio = new Espacio(agrupa_ejem, consec_candid, train,
                                             baseDatos);
            /* We obtaine the combination of the antecedents in where there are examples */
            subespacio.generate();
            /* Now "subspace" contains the "n_reglas" obtained subspaces */

            proceso(subespacio);

            //Finally we should fill the training and test output files
            doOutput(this.val, this.outputTr);
            doOutput(this.test, this.outputTst);

            System.out.println("Algorithm Finished");
        }
    }

    /**
     * It generates the output file from a given dataset and stores it in a file
     * @param dataset myDataset input dataset
     * @param filename String the name of the file
     */
    private void doOutput(myDataset dataset, String filename) {
        String output = new String("");
        output = dataset.copyHeader(); //we insert the header in the output file
        //We write the output for each example
        for (int i = 0; i < dataset.getnData(); i++) {
            //for classification:
            output += dataset.getOutputAsReal(i) + " " +
                    this.classificationOutput(dataset.getExample(i)) + "\n";
        }
        Fichero.escribeFichero(filename, output);
    }

    /**
     * It returns the algorithm classification output given an input example
     * @param example double[] The input example
     * @return double the output generated by the algorithm
     */
    private double classificationOutput(double[] example) {
        /**
          Here we should include the algorithm directives to generate the
          classification output from the input example
         */
        return baseReglas.FLC(example,baseReglas.n_reglas);
    }

    private void proceso(Espacio subEspacio) {
        double tmp, tmp_r;

        /* For each subspace we compute the possible consequents from each one of the
        positive examples */
        subEspacio.calculaConsecuentes();

        tmp = tmp_r = 1.0;
        //System.err.println("tamaño -> "+subEspacio.size());
        for (int i = 0; i < subEspacio.size(); i++) {
            tmp *= subEspacio.numConsecuentes(i);
            tmp_r *= subEspacio.numConsecuentes(i) + 1;
        }

        System.out.println("Search Space: COR - " + tmp + ", COR-R - " +
                           tmp_r + "\n");

        if (this.deleteRules) {
            subEspacio.incluirBorrado();
        }

        GA genetico = new GA(train, test, baseDatos, baseReglas, subEspacio,
                             learnWeights, weightCrossType, populationSize,
                             nGenerations, crossProb, mutProb, a_param, outputEvo);
        Individuo solucion = genetico.lanzar();
        int n_reg = 0;
        if (this.learnWeights) {
            n_reg = baseReglas.obtener_BRP(solucion.getGene(), solucion.getPeso(),
                                           subEspacio);
        } else {
            n_reg = baseReglas.obtener_BR(solucion.getGene(), subEspacio);
        }

        /* We open the results file in which we will store the learned rule set */
        String salida = new String("");
        salida += "Number of rules: " + n_reg + "\n\n";
        salida += baseReglas.BRtoString();

        /* Computation of the MSE of the learned Knowledge Base */
        double ec_tra = GA.Error(val, n_reg);
        double ec_tst = GA.Error(test, n_reg);
        int Trials_mejor = GA.dameTrials();

        salida += "\nMSEtra: " + ec_tra + ",  MSEtst: " + ec_tst + ",  EBS: " +
                Trials_mejor + "\n";
        //salida += "\n----------------- Parametros de Entrada aceptados ------------------\n\n";
        //fprintf(fp, FORMATO_SAL, VAR_SAL);
        for (int i = 0; i < train.getnInputs(); i++) {
            salida += "Input Variable = " + (i + 1) + "\n";
            salida += "Number of labels = " + baseDatos.getnLabels(i) + "\n";
            salida += "Discurse universe = [" + baseDatos.getExtremoInf(i) +
                    "," + baseDatos.getExtremoSup(i) + "]\n\n";
        }
        salida += "Output variable = 1\n";
        salida += "Number of labels = " +
                baseDatos.getnLabels(baseDatos.n_var_estado - 1) + "\n";
        salida += "Discurse Universe = [" +
                baseDatos.getExtremoInf(baseDatos.n_var_estado - 1) + "," +
                baseDatos.getExtremoSup(baseDatos.n_var_estado - 1) + "]\n\n";

        /* Se almacena la Base de Datos en el fichero de informe */
        salida += "\n\nInitial Data Base: \n";
        salida += baseDatos.printString();
        salida +=
                "\n--------------------------------------------------------------------\n\n";
        Fichero.escribeFichero(outputCor, salida);

        System.out.println("MSEtra: " + ec_tra + ", MSEtst: " + ec_tst +
                           ", #R: " + n_reg + ", EBS: " + Trials_mejor);

    }

}

