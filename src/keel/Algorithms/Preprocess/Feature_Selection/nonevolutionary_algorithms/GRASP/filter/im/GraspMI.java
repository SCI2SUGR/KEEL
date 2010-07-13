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
 * @author Written by Manuel Chica Serrano (University of Jaen) 01/09/2005
 * @author Modified by Jose Joaquin Aguilera Garcia (University of Jaen) 19/12/2008
 * @author Modified by Cristobal Jose Carmona del Jesus (University of Jaen) 19/12/2008
 * @author Modified by Jose Joaquin Aguilera Garcia (University of Jaen) 03/02/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Preprocess.Feature_Selection.nonevolutionary_algorithms.GRASP.filter.im;

import java.util.*;
import keel.Dataset.Attributes;
import org.core.*;
import keel.Algorithms.Preprocess.Feature_Selection.Datos;


public class GraspMI {
    /**
     * <p>
     * Main class of Grasp Algorithm using mutual information as evaluation mesure
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
        
         
        /** seed for pseudo-aleatorian generator */
        long seed;
        
         
        /** k value for a specific k-tournamente for GRASP Algorithm  */
        int valorKTorneo = 1;
                
        
        /** the number of features to be selected by GRASP Algorithm */
        int numberOfFeatures;
    
                
        /** beta value that regulates the relative importance of the MI between the candidate feature and the
         * already-selected features respect to the MI with the output class */
        double beta;
        
        
       /**
        * <p> 
        * Constructor of the Parametros Class
        * </p>
        * @param nombreFileParametros is the pathname of input parameter file
        */ 
        Parametros (String nombreFileParametros){
        
            try{
                int i;
                String fichero, linea, tok;
                StringTokenizer lineasFile, tokens;

                /* reads the parameter file using Files class */
                fichero = Files.readFile(nombreFileParametros);
                fichero += "\n";
                
                /* removes all \r characters. it is neccesary for a correst use in Windows and UNIX  */
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
                        else if(tok.equalsIgnoreCase("paramKNN")) paramKNN = getParamInt(tokens);    
                        else if(tok.equalsIgnoreCase("seed")) seed = getParamLong(tokens); 
                        else if(tok.equalsIgnoreCase("numberOfFeatures")) numberOfFeatures = getParamInt(tokens);
                        else if(tok.equalsIgnoreCase("beta")) beta = getParamFloat(tokens);                       
                        else if(tok.equalsIgnoreCase("k")) valorKTorneo = getParamInt(tokens); 
                        else throw new java.io.IOException("Syntax error on line " + i + ": [" + tok + "]\n");
                    }                                                      

                }


            } catch(java.io.FileNotFoundException e){
                System.err.println(e + "Parameter file");
            }catch(java.io.IOException e){
                System.err.println(e + "Aborting program");
                System.exit(-1);
            }

            /** shows the read parameter in the standard output */
            String contents = "-- Parameters echo --- \n";
            contents += "Algorithm name: " + nameAlgorithm +"\n";
            contents += "Input Train File: " + trainFileNameInput +"\n";
            contents += "Input Test File: " + testFileNameInput +"\n";
            contents += "Output Train File: " + trainFileNameOutput +"\n";
            contents += "Output Test File: " + testFileNameOutput +"\n";
            contents += "Parameter k of KNN Algorithm: " + paramKNN + "\n";
            contents += "Beta value for Battit Measure: " + beta + "\n";
            contents += "k Value for Grasp selection: " + valorKTorneo + "\n";
            contents += "Seed: " + seed + "\n";
            contents += "Number of Features to be selected: " + numberOfFeatures + "\n";
            System.out.println(contents);

        }
    
    
        /** obtain an integer value from the parameter file  
            @param s is the StringTokenizer  */
        private int getParamInt(StringTokenizer s){
            String val = s.nextToken();
            val = s.nextToken();
            return Integer.parseInt(val);
        }
        
    
        /** obtain a float value from the parameter file  
            @param s is the StringTokenizer */
        private float getParamFloat(StringTokenizer s){
            String val = s.nextToken();
            val = s.nextToken();
            return Float.parseFloat(val);
        }

        
        /** obtain a long value from the parameter file  
            @param s is the StringTokenizer */
        private long getParamLong(StringTokenizer s){
            String val = s.nextToken();
            val = s.nextToken();
            return Long.parseLong(val);           
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
     * Creates a new instance of GraspMI 
     * </p>
     * @param ficParametros is the name of the parameter file
     */
    public GraspMI(String ficParametros) {
        
        /* loads the parameter file */
        params = new Parametros(ficParametros);
        
        /* set the pseudo-aleatorian generator and loads the training and test dataset */
        Randomize.setSeed(params.seed);
        data = new Datos (params.trainFileNameInput, params.testFileNameInput, params.paramKNN);
        
        /* checks if the numberOfFeatures parameter is fewer than the total number of them */
        if(params.numberOfFeatures >= data.returnNumFeatures()){
              System.err.println("ERROR: The number of features to selected is greater or equal than total features");
              System.err.println("numberOfFeatures (parameter) must be fewer than " + data.returnNumFeatures());
              System.exit(0);
        }
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
     * calculates an array with the k-best features to be selected, according to a double array passed as an argument 
     * </p>
     * @param vector is a double vector with the mutual information for each feature
     * @return returns an array with the k-best features
     */

    private int[] mejoresKCaracteristicas(double vector[]){
        int temp[];
        int k,i;
        double max;
        
        temp = new int[params.valorKTorneo];
        
        k = 0;
        while(k<temp.length){
            max = vector[0];
            temp[k] = 0;
            for(i=1; i<vector.length; i++)
                if(vector[i]>max){
                    max = vector[i];
                    temp[k] = i;
                }
            
            /* set negative infinity to the MI of the best feature  */
            vector[(temp[k])] = Double.NEGATIVE_INFINITY;
            k++;
        }    
        
        return temp;
        
    }
    
    
    /**
     * <p>
     * main method of GraspMI for feature selection. Uses the previous methods
     * </p> 
     */  
    private void GRASP(){
        double I[], cantidad[];
        double IMV[][];
        int mejoresK[];
        int i,aleat,varsSelec;
        
        features = new boolean[data.returnNumFeatures()];
        
        /* loads the arrays with the Mutual Information */
        I = data.obtenerIMVarsClase();
        IMV = data.obtenerIMVars();
        
        
        /* selects the best-k features according to their Battiti measure */
        cantidad = new double[data.returnNumFeatures()];
        for(i=0; i<data.returnNumFeatures(); i++)
            cantidad[i] = I[i];
           
        mejoresK = mejoresKCaracteristicas(cantidad);
        
        /* gets a feature randomly */
        aleat = Randomize.Randint(0,params.valorKTorneo);
        
        /* sets the feature as selected */
        features[(mejoresK[aleat])] = true;
        
        varsSelec = 1;
        while(varsSelec<params.numberOfFeatures){
            for(i=0; i<features.length; i++)
                if(!features[i])
                    cantidad[i] = medidaBattiti(i,features,I, IMV);
                else cantidad[i] = Double.NEGATIVE_INFINITY;    
            
            /* 'cantidad' array contains the Battiti measures of all the non-selected features. We 
             * have to select the best-k of them, and get one randomly */
            mejoresK = mejoresKCaracteristicas(cantidad);
            aleat = Randomize.Randint(0,params.valorKTorneo);
            
            /* sets the feature as selected */
            features[(mejoresK[aleat])] = true;
            varsSelec++;
        }
        
    }
    
    
    /**
     * <p>
     * method interface for GRASP algorithm
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
       
        /* call of GRASP algorithm */
        GRASP();
            
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


