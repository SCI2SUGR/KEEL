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
 * SSGAIntegerLVO.java
 * @author Written by Manuel Chica Serrano (University of Jaen) 01/09/2005
 * @author Modified by Jose Joaquin Aguilera Garcia (University of Jaen) 19/12/2008
 * @author Modified by Cristobal Jose Carmona del Jesus (University of Jaen) 19/12/2008
 * @author Modified by Jose Joaquin Aguilera Garcia (University of Jaen) 03/02/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */


package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.GA_SS_IntCod.wrapper;

import org.core.*;
import java.util.*;
import keel.Dataset.Attributes;
import keel.Algorithms.Preprocess.Feature_Selection.Datos;
import keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.*;


public class SSGAIntegerLVO {
    /**
     * <p>
     * Main class for stationary state GA with integer encoding for feature selection using LVO as wrapper algorithm
     * Inputs: seed, nearest neighbours for KNN Classifier, population size, number of evaluations, number of selected features
     * Encoding: Integer
     * Selection: NO SELECTION. The two best individual have been selected
     * Replacement: A couple of descendants replaces the two worst elements of population
     * Crossover and mutation in one point
     * Fitness: %hits
     * Stopping criteria: number of evaluations
     * </p>
     */

    /** Datos class with all information about datasets and feature selection methods  */
    private Datos data;
    
    
    /** needed parameters */
    private Parametros params;
        
    
    /** population */
    private Cromosoma poblacion[];
    
        
    /** best chromosome of the population */
    private Cromosoma mejorIndividuo;
    
    
    /** evaluation in which the best individual is obtained */
    private int nEvalMejorIndividuo;
    
    
    /** interior class using for reading all parameters */
    private class Parametros {
    
        /** string with the name of the algorithm */
        String nameAlgorithm;
        
        
        /** nearest neighbours for KNN classifier */
        int paramKNN;
            
        
        /** pathname of training input file */
        String trainFileNameInput;
    
    
        /** pathname of test input file */
        String testFileNameInput;
    
    
        /** pathname of training output file. selected features will be only saved in this file */
        String testFileNameOutput;
        
    
        /** pathname of test output file. selected features will be only saved in this file */
        String trainFileNameOutput;
    
    
        /** pathname of an extra file with additional information about the algorithm results */
        String extraFileNameOutput;

        
        /** number of features to be selected by the algorithm */
        int numberOfFeatures;
        
        
        /** population size */
        int tamPoblacion;
        
        
        /** seed for pseudo-aleatorian generator */
        long seed;
        
        
        /** number of evaluations */
        int numEvaluaciones;      
                     
        
       /**
        * <p> 
        * Constructor of the Parametros Class
        * </p>
        *  @param nombreFileParametros is the pathname of input parameter file
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

                /* extract the differents tokens of the file */
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
                        else if(tok.equalsIgnoreCase("seed")) seed = getParamInt(tokens);  
                        else if(tok.equalsIgnoreCase("nEval")) numEvaluaciones = getParamInt(tokens);  
                        else if(tok.equalsIgnoreCase("numberOfFeatures")) numberOfFeatures = getParamInt(tokens);  
                        else if(tok.equalsIgnoreCase("popLength")) tamPoblacion = getParamInt(tokens);
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
            contents += "Number of Features to Select: " + numberOfFeatures + "\n";
            contents += "Population: " + tamPoblacion + "\n";
            contents += "Number of Evals: " + numEvaluaciones + "\n";
            contents += "Seed: " + seed + "\n";
            System.out.println(contents);

        }
    
    
        /** obtain an integer value from the parameter file  
            @param s is the StringTokenizer  */
        private int getParamInt(StringTokenizer s){
            String val = s.nextToken();
            val = s.nextToken();
            return Integer.parseInt(val);           
        }
        
        
        /** obtain a long value from the parameter file  
            @param s is the StringTokenizer */
        private long getParamLong(StringTokenizer s){
            String val = s.nextToken();
            val = s.nextToken();
            return Long.parseLong(val);           
        }
        
    
        /** obtain a double value from the parameter file  
            @param s is the StringTokenizer */
        private double getParamDouble(StringTokenizer s){
            String val = s.nextToken();
            val = s.nextToken();
            return Double.parseDouble(val);
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


        /**obtain the names of the input files from the parameter file  
            @param s is the StringTokenizer */
        private void getInputFiles(StringTokenizer s){
            String val = s.nextToken();

            trainFileNameInput = s.nextToken().replace('"',  ' ').trim();
            testFileNameInput = s.nextToken().replace('"',  ' ').trim();
        }


        /**obtain the names of the output files from the parameter file  
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
     * Creates a new instance of SSGAIntegerLVO
     * </p>
     * @param ficParametros is the name of the param file
     */
    public SSGAIntegerLVO(String ficParametros) {
        
        /* load the parameter file from the Parametros class */
        params = new Parametros(ficParametros);
        
        /* set the seed parameter */
        Randomize.setSeed(params.seed);
        
        data = new Datos (params.trainFileNameInput, params.testFileNameInput, params.paramKNN);
                
        if(params.numberOfFeatures >= data.returnNumFeatures()){
          System.err.println("ERROR: The number of features to selected is greater or equal than total features \n");
          System.err.println("numberOfFeatures (parameter) must be fewer than " + data.returnNumFeatures());
          System.exit(0);
        }
        
        /* generates the initial population */
        poblacion = new Cromosoma[params.tamPoblacion];
        
        mejorIndividuo = new CromosomaEntero(data.returnNumFeatures(), params.numberOfFeatures);
        nEvalMejorIndividuo = -1;
        
        for(int i=0; i<params.tamPoblacion; i++)
            poblacion[i] = new CromosomaEntero(data.returnNumFeatures(), params.numberOfFeatures);
                        
    }
    
    
    /**
     * <p>
     * returns the fitness of a chromosome
     * </p>
     * @param Cromosoma a chromosome
     * @return the fitness of a chromosome (Maximization problem)
     * </p>
     */
    private double fitness(Cromosoma cr){
        double precision;
        boolean fv[];
        
        if(cr==null){            
            System.err.println("ERROR: Chromosome doesn't exist");
            System.exit(0); 
        }
        
        fv = cr.devolverFeaturesVector();
        precision = data.LVO(fv);
                
        return (1-precision);        
    }  
       
        
    /**
     * <p>
     * selects the two best elements of the population. This method only returns two references for the best individuals, not a copy 
     * </p>
     * @return returns an array containing two chromosome references
     */
    private Cromosoma[] seleccionDosMejoresPadres(){
        Cromosoma mejores[];
        
        mejores = new Cromosoma[2];
        
        mejores[0] = poblacion[0];
                
        for(int i=1; i<params.tamPoblacion; i++)
            if(poblacion[i].getFitness() > mejores[0].getFitness())
                mejores[0] = poblacion[i];
        
        /* mejores[0] contains the best element of the population, then selects the other one */
        if(poblacion[0] != mejores[0])
            mejores[1] = poblacion[0];
        else mejores[1] = poblacion[1];
                
        for(int i=1; i<params.tamPoblacion; i++)
            if(poblacion[i]!=mejores[0] && poblacion[i].getFitness() > mejores[1].getFitness())
                mejores[1] = poblacion[i];
        
        return mejores;        
    }
   
    
    /**
     * <p>
     * replaces the two best elements (passed as argument) with the two worst elements of the population
     * </p>
     * @param descendientes is an array with the descendants
     * </p>
     */
    private void reemplazarPorPeores(Cromosoma descendientes[]){
        
        if(descendientes==null || descendientes[0]==null || descendientes[1]==null){            
            System.err.println("ERROR: Descendants chromosomes don't exist");
            System.exit(0); 
        }
        
        /* firstly, finds the worst and replaces it with the first descendant */
        Cromosoma peor = poblacion[0];
                        
        for(int i=1; i<params.tamPoblacion; i++)
            if(poblacion[i].getFitness() < peor.getFitness())
                peor = poblacion[i];
            
        peor.copy(descendientes[0]);
        
        /* finds the 'second' worst element */
        peor = poblacion[0];
                        
        for(int i=1; i<params.tamPoblacion; i++)
            if(poblacion[i].getFitness() < peor.getFitness())
                peor = poblacion[i];
            
        peor.copy(descendientes[1]);
        
    }
            
            
    /**
     * <p>
     * main method for Stationary State GA
     */
    private void modeloEstacionario(){
        int i, nEvaluaciones;
        boolean necesarioElitismo;
        Cromosoma padresSeleccionados[];
        Cromosoma descendientes[];
        
        descendientes = new Cromosoma[2];
        
        /* number of evaluations count */
        nEvaluaciones = 0;

        /* creates the initial generation */
        for(i=0; i<params.tamPoblacion; i++) 
            poblacion[i].initRand();
   
        /* evaluate the initial population */
        for(i=0; i<params.tamPoblacion; i++){
                nEvaluaciones++;
                poblacion[i].setFitness(fitness(poblacion[i]));

                /* checks if the current individual is the best of the population */
                if(mejorIndividuo==null || poblacion[i].getFitness()>mejorIndividuo.getFitness()){ 
                        mejorIndividuo.copy(poblacion[i]);
                        nEvalMejorIndividuo = nEvaluaciones;     
                }
        } 
        
        while(nEvaluaciones < params.numEvaluaciones){
            
            /* selects 2 best parents for crossover */
            padresSeleccionados = seleccionDosMejoresPadres();

            /* mating */
            descendientes[0] = new CromosomaEntero(data.returnNumFeatures(), params.numberOfFeatures);
            descendientes[1] = new CromosomaEntero(data.returnNumFeatures(), params.numberOfFeatures);
                
            padresSeleccionados[0].cruzar(padresSeleccionados[1], descendientes[0], 
                                            descendientes[1]);

            for(i=0; i<2; i++){
                descendientes[i].setFitness(fitness(descendientes[i]));
                nEvaluaciones++;
                /* checks if the current individual is the best of the population */
                if(descendientes[i].getFitness()>mejorIndividuo.getFitness()){ 
                        mejorIndividuo.copy(descendientes[i]);
                        nEvalMejorIndividuo = nEvaluaciones;
                }
            }
            
            /* replaces descendants with the worst elements of population */
            reemplazarPorPeores(descendientes);
                 
        }        
        
    }  
        
    
    /**
     * <p>
     * method inteface for CHC algorithm
     * </p>
     */
    public void ejecutar(){
        String resultado;
        int i, numFeatures;
        Date d;
        boolean features[];
       
        d = new Date();
        resultado = "RESULTS generated at " + String.valueOf((Date)d) 
                        + " \n--------------------------------------------------\n";
        resultado += "Algorithm Name: " + params.nameAlgorithm + "\n";
       
        /* Stationary State GA algorithm */
        modeloEstacionario();
            
        /* una vez ejecutado el algoritmo componemos los datos en un String */
        resultado += "\nPARTITION Filename: "+ params.trainFileNameInput +"\n---------------\n\n";
        resultado += "Features selected: \n";
            
        features = mejorIndividuo.devolverFeaturesVector();
        for(i=numFeatures=0; i<features.length; i++) 
            if(features[i] == true){ 
                resultado += Attributes.getInputAttribute(i).getName() + " - ";
                numFeatures++;
            }   
        resultado += "\n Best individual find at " + nEvalMejorIndividuo + "evaluation. ";
        resultado += "\n\n" + String.valueOf(numFeatures) + " features of " 
                + Attributes.getInputNumAttributes() + "\n\n" ;
        
        resultado += "Error in test (using train for prediction): " 
                + String.valueOf(data.validacionCruzada(features)) + "\n";
        resultado += "Error in test (using test for prediction): " 
                + String.valueOf(data.LVOTest(features)) + "\n";
        
        resultado += "---------------\n";        
            
        System.out.println("Experiment completed successfully");
              
        /* writes the ouput files */
        Files.writeFile(params.extraFileNameOutput, resultado);
        data.generarFicherosSalida(params.trainFileNameOutput, params.testFileNameOutput, features);
               
        
    }
    
}



