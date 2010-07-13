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

package keel.Algorithms.Preprocess.Feature_Selection.nonevolutionary_algorithms.SFS_MI_FS;

/**
 * <p>
 * @author Written by Ignacio Robles Paiz (University of Granada) 23/06/2010
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

import java.util.*;
import org.core.*;
import java.lang.Math;
import keel.Dataset.*;
import keel.Algorithms.Preprocess.Feature_Selection.*;



public class SFS {
/**
 * <p>
 * Simulated Annealing Algorithm
 * </p>
 */

    /** Datos class with all information about datasets and feature selection methods  */
    private Datos data;


    /** needed parameters for backward method */
    private Parametros params;


    /** a boolean array with selected features */
    private boolean features[];


    /** interior class using for reading all parameters */
    private class Parametros{

        /** algorithm name */
        String nameAlgorithm;


        /** number of nearest neighbours for KNN Classifier */
        int paramKNN;


        /** pathname of training dataset */
        String trainFileNameInput;


        /** pathname of test dataset */
        String testFileNameInput;


        /** pathname of test dataset only with selected features */
        String testFileNameOutput;


        /** pathname of training dataset only with selected features */
        String trainFileNameOutput;


        /** pathname of an extra file with additional information about the algorithm results */
        String extraFileNameOutput;

        /** beta value that regulates the relative importance of the MI between the candidate feature and the
         * already-selected features respect to the MI with the output class */
        double beta;


        /** seed for pseudo-random generator */
        long seed;


        /** measure threshold **/
        double threshold;





        /**
         * <p>
         * Constructor of the Class Parametros
         * </p>
         * @param nombreFileParametros is the pathname of input parameter file
         */
        Parametros (String nombreFileParametros){

            try{
                int i;
                String fichero, linea, tok;
                StringTokenizer lineasFile, tokens;

                /* read the parameter file using Files class */
                fichero = Files.readFile(nombreFileParametros);
                fichero += "\n";

                /* remove all \r characters. it is neccesary for a correst use in Windows and UNIX  */
                fichero = fichero.replace('\r', ' ');

                /* extracts the differents tokens of the file */
                lineasFile = new StringTokenizer(fichero, "\n");

                i=0;
                while(lineasFile.hasMoreTokens()) {

                    linea = lineasFile.nextToken();
                    i++;
                    tokens = new StringTokenizer(linea, " ,\t");
                    if(tokens.hasMoreTokens()){

                        tok = tokens.nextToken();
                        if(tok.equalsIgnoreCase("algorithm")) nameAlgorithm = getParamString(tokens);
                        else if(tok.equalsIgnoreCase("inputdata")) getInputFiles(tokens);
                        else if(tok.equalsIgnoreCase("outputdata")) getOutputFiles(tokens);
                        else if(tok.equalsIgnoreCase("seed")) seed = getParamLong(tokens);
                        else if(tok.equalsIgnoreCase("threshold")) threshold = getParamFloat(tokens);
                        else if(tok.equalsIgnoreCase("beta")) beta = getParamFloat(tokens);
                        else throw new java.io.IOException("Syntax error on line " + i + ": [" + tok + "]\n");
                    }

                }


            } catch(java.io.FileNotFoundException e){
                System.err.println(e + "Parameter file");
            }catch(java.io.IOException e){
                System.err.println(e + "Aborting program");
                System.exit(-1);
            }

            /** show the read parameter in the standard output */
            String contents = "-- Parameters echo --- \n";
            contents += "Algorithm name: " + nameAlgorithm +"\n";
            contents += "Input Train File: " + trainFileNameInput +"\n";
            contents += "Input Test File: " + testFileNameInput +"\n";
            contents += "Output Train File: " + trainFileNameOutput +"\n";
            contents += "Output Test File: " + testFileNameOutput +"\n";
            contents += "Measure threshold: " + threshold + "\n";
            contents += "Seed: " + seed + "\n";
            System.out.println(contents);

        }


        /** obtain a float value from the parameter file
            @param s is the StringTokenizer */
        private double getParamFloat(StringTokenizer s){
            String val = s.nextToken();
            val = s.nextToken();
            return Float.parseFloat(val);
        }

        /** obtain an integer value from the parameter file
            @param s is the StringTokenizer  */
        private int getParamInt(StringTokenizer s){
            String val = s.nextToken();
            val = s.nextToken();
            return Integer.parseInt(val);
        }


        /** obtain a string value from the parameter file
            @param s is the StringTokenizer */
        private String getParamString(StringTokenizer s){
            String contenido = "";
            String val = s.nextToken();
            while(s.hasMoreTokens())
                contenido += s.nextToken() + " ";

            return contenido.trim();
        }


        /** obtain a long value from the parameter file
            @param s is the StringTokenizer */
        private long getParamLong(StringTokenizer s){
            String val = s.nextToken();
            val = s.nextToken();
            return Long.parseLong(val);
        }


        /** obtain the names of the input files from the parameter file
            @param s is the StringTokenizer */
        private void getInputFiles(StringTokenizer s){
            String val = s.nextToken();

            trainFileNameInput = s.nextToken().replace('"',  ' ').trim();
            testFileNameInput = s.nextToken().replace('"',  ' ').trim();
        }


        /** obtain the names of the output files from the parameter file
            @param s is the StringTokenizer */
        private void getOutputFiles(StringTokenizer s){
            String val = s.nextToken();

            trainFileNameOutput = s.nextToken().replace('"',  ' ').trim();
            testFileNameOutput = s.nextToken().replace('"',  ' ').trim();
            extraFileNameOutput = s.nextToken().replace('"',  ' ').trim();

        }

    }


    /**
     * <p>
     * Creates a new instance of SA
     * </p>
     */
    public SFS(String ficParametros) {

        /* loads the parameter file */
        params = new Parametros(ficParametros);

        Randomize.setSeed(params.seed);

        /* loads both of training and test datasets */
        data = new Datos (params.trainFileNameInput, params.testFileNameInput, params.paramKNN);

        features = new boolean[data.returnNumFeatures()];

    }

        /**
     * <p>
     * This method returns Battiti measure (Mutual Information)
     * </p>
     * @param I is an array with the mutual information between each feature and the output class
     * @param IMV is a matrix that contains the mutual information between each pair of features
     * @param numCaracteristica is the specific feature that will be calculate its Battiti measure
     * @param fv is a boolean array that contains the selected features (it's neccesary to calculate I(feature; selected_features))
     * @return returns the Batitti measure
     */
    private double medidaBattiti(int numCaracteristica, boolean fv[], double I[], double IMV[][]){
        double suma, parcial;

        suma = I[numCaracteristica];
        parcial = 0;
        for(int i=0; i<fv.length; i++)
            /* if feature i has been selected, calculates its MI with the 'numCaracterisitica' feature */
            if(fv[i]==true)
                parcial += IMV[numCaracteristica][i];

        suma -= params.beta*parcial;

        return suma;

    }


    /**
     * <p>
     * Creates a boolean array with random values
     * </p>
     * @return returns a boolean vector with the random values
     */
    private boolean[] randomSolution(){
        boolean fv[];

        fv = new boolean[Attributes.getInputNumAttributes()];

        for(int i=0; i<fv.length; i++)
            if(Randomize.Randint(0,2) == 0)
                fv[i] = false;
            else
                fv[i] = true;

        if(cardinalidadCto(fv) == 0) //zero selected features is not allowed
            fv[Randomize.Randint(0,Attributes.getInputNumAttributes())] = true;

        return fv;
    }

     /**
     * <p>
     * Creates a boolean array with all values to false
     * </p>
     * @return returns a boolean vector with all values to false
     */
    private boolean[] startSolution(){
        boolean fv[];

        fv = new boolean[Attributes.getInputNumAttributes()];

        for(int i=0; i<fv.length; i++)
            fv[i] = false;


        return fv;
    }


    /**
     * <p>
     * Calculates the number of true values of a boolean array
     * </p>
     * @param featuresVector is the boolean array to check
     * @return returns an integer with the number of true values
     */
    private static int cardinalidadCto(boolean featuresVector[]){
        int cardinalidad = 0;

        for(int i=0; i<featuresVector.length; i++)
            if(featuresVector[i]) cardinalidad++;

        return cardinalidad;
    }

     /**
     * Generates a random neighbor of a given solution by flipping only one
     * feature.
     * @param s solution to generate its neighbor
     * @param size number of features
     * @param generator random number generator
     * @return randomly choosen neighbor of the given solution
     */
    private static boolean[] neighbor(boolean featuresVector[]){
        boolean [] fv = new boolean[featuresVector.length];
        for(int i=0; i<fv.length; i++)
                fv[i] = featuresVector[i];

        int toChange = Randomize.Randint(0, fv.length);
        fv[toChange] = !fv[toChange];

        if(cardinalidadCto(fv) == 0) //zero selected features is not allowed
            fv[Randomize.Randint(0,Attributes.getInputNumAttributes())] = true;

        return fv;
    }






    /**
     * <p>
     * Main method for SFS
     * </p>
     */
    private void runSFS() {
        features = startSolution();
        double I[];
        double IMV[][];


        /* loads the arrays with the Mutual Information */
        I = data.obtenerIMVarsClase();
        IMV = data.obtenerIMVars();

        double T = 1.0;
        int best_feature = 0;
        double best;
        double current;
        boolean forced_stop = false; //controls the situation where no features available to add

        while (T > params.threshold && !forced_stop) {
            best = Double.MAX_VALUE;  //we have no 'best' yet

            forced_stop = true;
            for (int i = 0; i < data.returnNumFeatures(); i++) {
                if (!features[i]) { //if feature i isnt selected already
                    features[i] = true;
                    current = medidaBattiti(i,features,I, IMV);
                    features[i] = false;

                    if (current > best) {
                        best_feature = i;
                        best = current;
                        forced_stop = false;
                    }

                }
            }

            if (!forced_stop) { //if there were new best features available to be selected...
                T = best;
                features[best_feature] = true;
            }
        }





        /* checks if a subset satisfies the condition (more than 0 selected features) */
        if (features == null) {
            System.err.println("ERROR: It couldn't be possible to find any solution.");
            System.exit(0);
        }

    }


    /**
     * <p>
     * Method interface for SFS algorithm
     * </p>
     */
    public void ejecutar(){
        String resultado;
        int i, numFeatures;
        Date d;

        d = new Date();
        resultado = "RESULTS generated at " + String.valueOf((Date)d)
                        + " \n--------------------------------------------------\n";
        resultado += "Algorithm Name: " + params.nameAlgorithm + "\n";

        /* call of SA algorithm */
        runSFS();

        resultado += "\nPARTITION Filename: "+ params.trainFileNameInput +"\n---------------\n\n";
        resultado += "Features selected: \n";

        for(i=numFeatures=0; i<features.length; i++)
            if(features[i] == true){
                resultado += Attributes.getInputAttribute(i).getName() + " - ";
                numFeatures++;
            }

        resultado += "\n\n" + String.valueOf(numFeatures) + " features of "
                + Attributes.getInputNumAttributes() + "\n\n" ;

        resultado += "Error in test (using train for prediction): "
                + String.valueOf(data.validacionCruzada(features)) + "\n";
        resultado += "Error in test (using test for prediction): "
                + String.valueOf(data.LVOTest(features)) + "\n";

        resultado += "---------------\n";

        System.out.println("Experiment completed successfully");

        /* creates the new training and test datasets only with the selected features */
        Files.writeFile(params.extraFileNameOutput, resultado);
        data.generarFicherosSalida(params.trainFileNameOutput, params.testFileNameOutput, features);


    }


}


