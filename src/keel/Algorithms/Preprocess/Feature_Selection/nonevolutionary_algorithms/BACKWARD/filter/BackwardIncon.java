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

/*
 * BackwardIncon.java
 *
 * Created on 25 de agosto de 2005, 12:49
 *
 */

package keel.Algorithms.Preprocess.Feature_Selection.nonevolutionary_algorithms.BACKWARD.filter;

import org.core.Fichero;
import java.util.Arrays.*;
import java.util.*;
import keel.Dataset.*;
import keel.Algorithms.Preprocess.Feature_Selection.Datos;

/** MAIN CLASS OF BACKWARD FEATURE SELECTION ALGORITHM 
 *  USING INCONSISTENCY COUNT
 *
 *  @author Manuel Chica Serrano 
 */

public class BackwardIncon {
    
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
    
    
        /* ----------- METHODS ------------ */
        /** Parametros Class constructor 
         *  @param nombreFicheroParametros is the pathname of input parameter file */ 
        Parametros (String nombreFicheroParametros){
        
            try{
                int i;
                String fichero, linea, tok;
                StringTokenizer lineasFichero, tokens;

                /* read the parameter file using Fichero class */
                fichero = Fichero.leeFichero(nombreFicheroParametros);
                fichero += "\n";
                
                /* remove all \r characters. it is neccesary for a correst use in Windows and UNIX  */
                fichero = fichero.replace('\r', ' ');

                /* extract the differents tokens of the file */
                lineasFichero = new StringTokenizer(fichero, "\n");

                i=0;
                while(lineasFichero.hasMoreTokens()) {
                    
                    linea = lineasFichero.nextToken();    
                    i++;
                    tokens = new StringTokenizer(linea, " ,\t");
                    if(tokens.hasMoreTokens()){

                        tok = tokens.nextToken();
                        if(tok.equalsIgnoreCase("algorithm")) nameAlgorithm = getParamString(tokens);
                        else if(tok.equalsIgnoreCase("inputdata")) getInputFiles(tokens);
                        else if(tok.equalsIgnoreCase("outputdata")) getOutputFiles(tokens);
                        else if(tok.equalsIgnoreCase("paramKNN")) paramKNN = getParamInt(tokens);                    
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
            contents += "Parameter k of KNN Algorithm: " + paramKNN + "\n";
            System.out.println(contents);

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
    
        
    /** --------------------------- */
    /**         METHODS             */
    /**
     * Creates a new instance of BackwardIncon 
     * @param paramsFile is a string with the path of parameter filename
     */
    public BackwardIncon(String paramsFile) {
        
        params = new Parametros(paramsFile);        
        data = new Datos (params.trainFileNameInput, params.testFileNameInput, params.paramKNN);
        
    }  
    
        
    /** main method of BACKWARD ALGORITHM. Firstable, we have all features, and in each loop delete one feature. Stopping criteria will
        be non-decrease inconsistency ratio */
    private void lanzarBackward(){
        int i,j,mejorCarac;
        double min, ratioInicial, ratioActual;
        boolean featuresVector[];
        boolean newFeatureDeleted;
        
        
        featuresVector = new boolean[data.returnNumFeatures()];
      
        for(i = 0; i<featuresVector.length; i++)
            featuresVector[i] = true;
        
        ratioInicial = data.medidaInconsistencia(featuresVector);
        
        /** at the begining, all features are selected */
        
        /* 'while' stop when no new feature was added */ 
        newFeatureDeleted = true;
        while(newFeatureDeleted){
            /** it will delete the feature that produce the fewer inconsistency ratio */
            
            min = Double.MAX_VALUE;
            mejorCarac = -1;
            
            for(i = 0; i<featuresVector.length; i++)
                /* checks if feature hadn't been removed before */
                if(featuresVector[i]==true){
                    featuresVector[i] = false;
                    ratioActual = data.medidaInconsistencia(featuresVector);
                    if(min > ratioActual){
                        min = ratioActual;
                        mejorCarac = i;
                    }
                    /* adds feature in to the set to test with an other one */
                    featuresVector[i] = true;
                }
            
            /* mejorCarac contains the best feature to remove permanently. 'ratioInicial' will be the 
               inconsistency ratio without this feature */
            if(min < ratioInicial) {
                featuresVector[mejorCarac] = false;
                ratioInicial = min;
                newFeatureDeleted = true;
            } else newFeatureDeleted = false;
            
        }
        
        features = featuresVector;
        
    }
    
    
    /** method interface for backward algorithm. */
    public void ejecutar(){
        String resultado;
        int i, numFeatures;
        Date d;
       
        d = new Date();
        resultado = "RESULTS generated at " + String.valueOf((Date)d) 
                        + " \n--------------------------------------------------\n";
        resultado += "Algorithm Name: " + params.nameAlgorithm + "\n";
       
        /* call of backward algorithm */
        lanzarBackward();
            
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
        Fichero.escribeFichero(params.extraFileNameOutput, resultado);
        data.generarFicherosSalida(params.trainFileNameOutput, params.testFileNameOutput, features);
               
        
    }
    
}
 

