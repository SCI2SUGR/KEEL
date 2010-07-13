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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Thrift;

/**
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */

import java.io.IOException;
import org.core.*;

public class Thrift {

    myDataset train, val, test;
    String outputTr, outputTst, outputEvo, outputTh;
    long seed;
    int populationSize, nEvaluations, nLabels;
    double crossProb, mutProb;

    int n_genes;

    BaseD baseDatos;
    BaseR baseReglas;

    //int nClasses;

    //We may declare here the algorithm's parameters

    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public Thrift() {
    }

    /**
     * It reads the data from the input files (training, validation and test) and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public Thrift(parseParameters parameters) {

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
        outputTh = parameters.getOutputFile(1);

        //Now we parse the parameters, for example:
        seed = Long.parseLong(parameters.getParameter(0));

        nLabels = Integer.parseInt(parameters.getParameter(1));

        //nGenerations = Integer.parseInt(parameters.getParameter(2));
        populationSize = Integer.parseInt(parameters.getParameter(2));
        nEvaluations = Integer.parseInt(parameters.getParameter(3));

        this.crossProb = Double.parseDouble(parameters.getParameter(4));
        this.mutProb = Double.parseDouble(parameters.getParameter(5));

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

            n_genes = 1;
            for (int i = 0; i < train.getnInputs(); i++) {
                n_genes *= nLabels;
            }

            this.crossProb = (crossProb * this.populationSize) - 0.5;
            this.mutProb = (mutProb / n_genes);

            baseDatos = new BaseD(nLabels, train.getnVars(),
                                  train.devuelveRangos());
            baseDatos.Semantica();

            baseReglas = new BaseR(n_genes, baseDatos, train);

            GA genetico = new GA(train, test, baseDatos, baseReglas,
                                 populationSize, nEvaluations,
                                 n_genes,
                                 crossProb, mutProb, outputEvo);

            Individuo solucion = genetico.lanzar();
            this.generaSalida(solucion);

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
        return baseReglas.FLC(example, baseReglas.n_reglas);
    }

    private void generaSalida(Individuo solucion) {
        int n_reg = baseReglas.decodifica(solucion.getGene());

        /* Apertura del fichero de resultados que almacena el conjunto de reglas
           finalmente aprendido */
        String salida = new String("");
        salida += "Numero de reglas: " + n_reg + "\n\n";
        salida += baseReglas.BRtoString();

        /* Calculo del Error de la Base de Conocimiento aprendida */
        double ec_tra = GA.Error(val, n_reg);
        double ec_tst = GA.Error(test, n_reg);
        int Trials_mejor = GA.dameTrials();

        salida += "\nECMtra: " + ec_tra + ",  ECMtst: " + ec_tst + ",  EMS: " +
                Trials_mejor + "\n";
        //salida += "\n----------------- Parametros de Entrada aceptados ------------------\n\n";
        //fprintf(fp, FORMATO_SAL, VAR_SAL);
        for (int i = 0; i < train.getnInputs(); i++) {
            salida += "Variable de entrada = " + (i + 1) + "\n";
            salida += "Numero de etiquetas = " + baseDatos.getnLabels(i) + "\n";
            salida += "Universo de discurso = [" + baseDatos.getExtremoInf(i) +
                    "," + baseDatos.getExtremoSup(i) + "]\n\n";
        }
        salida += "Variable de salida = 1\n";
        salida += "Numero de etiquetas = " +
                baseDatos.getnLabels(baseDatos.n_var_estado - 1) + "\n";
        salida += "Universo de discurso = [" +
                baseDatos.getExtremoInf(baseDatos.n_var_estado - 1) + "," +
                baseDatos.getExtremoSup(baseDatos.n_var_estado - 1) + "]\n\n";

        /* Se almacena la Base de Datos en el fichero de informe */
        salida += "\n\nBase de Datos inicial: \n";
        salida += baseDatos.printString();
        salida +=
                "\n--------------------------------------------------------------------\n\n";
        Fichero.escribeFichero(outputTh, salida);

        System.out.println("ECMtra: " + ec_tra + ", ECMtst: " + ec_tst +
                           ", #R: " + n_reg + ", EMS: " + Trials_mejor);

    }

}

