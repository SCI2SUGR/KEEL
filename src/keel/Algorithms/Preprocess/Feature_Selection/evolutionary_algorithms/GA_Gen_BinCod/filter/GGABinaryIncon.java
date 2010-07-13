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

package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.GA_Gen_BinCod.filter;

import java.util.*;
import org.core.*;
import keel.Dataset.Attributes;
import keel.Algorithms.Preprocess.Feature_Selection.Datos;
import keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms.*;

 
 public class GGABinaryIncon {
      /**
     * <p>
     * Main class of Generational Genetic Algorithm for feature selection using inconsistency ratio as evaluation measure
     * This class implements a Generational GA with binary representation for feature selection Inconsistency ratio (FILTER) 
     *  Inputs: nearest neighbours for K-NN, k value for k-Tournamentvalor, crossover probability, 
     *       mutation probability, seed, population size, number of  evaluations, alfa value for fitness balancing 
     *  Encoding: Binary
     *  Selection: k-Tournament
     *  Replacement: Descendants always replaces parents
     *  Crossover and mutation in one point 
     *  Fitness: (1-alfa)*%hits + alfa*features_selected
     *  Stopping criteria: number of evaluations 
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
    private class Parametros{
    
        /** string with the name of the algorithm */
        String nameAlgorithm;
        
        
        /** nearest neighbours for KNN classifier */
        int paramKNN;
    
    
        /** k value for K-TOURNAMENT */
        int valorKTorneo;
        
        
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
        
        
        /** crossover probability for GA */
        double probCruce;
    
                
        /** mutation probability for GA */
        double probMutacion;
        
        
        /** weight used in the fitness function (between precision & selected features)*/
        double alfa;
        
        
        /** population size */
        int tamPoblacion;
        
        
        /** seed for pseudo-random generator */
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
                
                /* remove all \r characters. it is necessary for a correct use in Windows and UNIX  */
                fichero = fichero.replace('\r', ' ');

                /* extract the different tokens of the file */
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
                        else if(tok.equalsIgnoreCase("crossProb")) probCruce = getParamDouble(tokens);  
                        else if(tok.equalsIgnoreCase("mutProb")) probMutacion = getParamDouble(tokens);  
                        else if(tok.equalsIgnoreCase("seed")) seed = getParamInt(tokens);  
                        else if(tok.equalsIgnoreCase("nEval")) numEvaluaciones = getParamInt(tokens);  
                        else if(tok.equalsIgnoreCase("alfa")) alfa = getParamDouble(tokens);  
                        else if(tok.equalsIgnoreCase("popLength")) tamPoblacion = getParamInt(tokens);
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

            /** show the read parameter in the standard output */
            String contents = "-- Parameters echo --- \n";
            contents += "Algorithm name: " + nameAlgorithm +"\n";
            contents += "Input Train File: " + trainFileNameInput +"\n";
            contents += "Input Test File: " + testFileNameInput +"\n";
            contents += "Output Train File: " + trainFileNameOutput +"\n";
            contents += "Output Test File: " + testFileNameOutput +"\n";
            contents += "Parameter k of KNN Algorithm: " + paramKNN + "\n";
            contents += "Cross Prob. : " + probCruce + "\n";
            contents += "Mutation Prob.: " + probMutacion + "\n";
            contents += "Alfa: " + alfa + "\n";
            contents += "Population: " + tamPoblacion + "\n";
            contents += "Number of Evals: " + numEvaluaciones + "\n";
            contents += "Seed: " + seed + "\n";            
            contents += "k value for k-tournament: " + valorKTorneo + "\n";
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
     * Creates a new instance of GGABinaryIncon
     * </p>
     * param ficParametros is the name of the parameter file
     */
    public GGABinaryIncon(String ficParametros) {
        
        /* load the parameter file from the Parametros class */
        params = new Parametros(ficParametros);
        
        /* set the seed parameter */
        Randomize.setSeed(params.seed);
        
        data = new Datos (params.trainFileNameInput, params.testFileNameInput, params.paramKNN);
               
        /* generate the initial population */
        poblacion = new Cromosoma[params.tamPoblacion];
        
        mejorIndividuo = new CromosomaBinario(data.returnNumFeatures());
        nEvalMejorIndividuo = -1;
        
        for(int i=0; i<params.tamPoblacion; i++)
            poblacion[i] = new CromosomaBinario(data.returnNumFeatures());
                        
    }
    
    
     /**
      * <p>
      * return the fitness of a chromosome
      * </p>
      * @param cromosoma a chromosome
      * @return the fitnees of a chromosome (Maximization problem)
      */
    private double fitness(Cromosoma cr){
        double precision;
        int numCaracSel;
        boolean fv[];
        
        if(cr==null){            
            System.err.println("ERROR: Chromosome doesn't exist");
            System.exit(0); 
        }
        
        fv = cr.devolverFeaturesVector();
        precision = data.medidaInconsistencia(fv);               
        
        for(int i=numCaracSel=0; i<fv.length; i++) if(fv[i]==true) 
            numCaracSel++;
        
        return (1-params.alfa)*(1-precision) - params.alfa*((double)numCaracSel/fv.length);
        
    }
    
    
    /** THIS IMPLEMENTATION HASN'T BEEN USED. seleccionPorTorneo REPLACES IT */
    private Cromosoma[] seleccionProporcional(){
        double probabilidades[];
        double fitnessTotal, nAleatorio;
        int i,k,lim;
        Cromosoma cr[] = new Cromosoma[params.tamPoblacion];

        probabilidades = new double[params.tamPoblacion];
        
        /* firstly,  it calculates all population fitnesses */
        fitnessTotal = 0;
        for(i=0; i<params.tamPoblacion; i++)
            fitnessTotal += fitness(poblacion[i]);

        for(i=0; i<params.tamPoblacion; i++)
            probabilidades[i]=fitness(poblacion[i])/fitnessTotal;

        for(k=0; k<params.tamPoblacion; k++){
        
            /* creates a random value and selects an element of the roulette */
            nAleatorio = Randomize.RandClosed();
            i = 0;
            lim = 0;
            while(i<params.tamPoblacion && (nAleatorio>lim))
                lim += probabilidades[i++];  
      
            /* selects it as parent */
            cr[k] = poblacion[i-1];
        } 
        
        return cr;
    }
    
    
    /** 
     * <p>
     * algorithm for parents selection. It uses a k-tournament selection
     * </p>
     * @return returns a chromosome array that contains the selected chromosomes (the array only contains references of objects, not new objects )
     */
    private Cromosoma[] seleccionPorTorneo(){     
        Cromosoma individuosTorneo[];
        Cromosoma padres[];
        int i,k;
        Cromosoma mejor;
   
        padres = new Cromosoma[params.tamPoblacion];
        
        individuosTorneo = new Cromosoma[params.valorKTorneo];
        
        /* for each selected parent (population size) do a k-tournament and choose the best */
        for(k=0; k<padres.length; k++){
            
            /* select k elements randomly choosing the best */
            for(i=0; i<individuosTorneo.length; i++) 
                individuosTorneo[i] = poblacion[Randomize.Randint(0,params.tamPoblacion)];
   
            mejor = individuosTorneo[0];
                
            for(i=1; i<individuosTorneo.length; i++)
                if(individuosTorneo[i].getFitness() > mejor.getFitness())
                    mejor = individuosTorneo[i];

            /* saves the k-tournament winner as selected parent */
            padres[k] = mejor;
   
        }
        
        return padres;
    }
            
            
    /**
     * <p>
     * this method crossover and mutate all population according to probabilities
     * </p>
     * @param seleccionados a vector with selected parents
     * @return returns an array with generated chromosomes 
     */
    private Cromosoma[] mutarYCruzar(Cromosoma seleccionados[]){        
        Cromosoma descendientes[];
        int posPadre1, posPadre2, posDescendientes;
        double aleat;
        
        descendientes = new Cromosoma[params.tamPoblacion];
        
        posPadre1 = posPadre2 = -1;
        posDescendientes = 0;
        /* rounds all parents crossing them to generate new elements */
        for(int i=0; i<params.tamPoblacion; i++){
            aleat = Randomize.RandClosed();
            
            if(aleat <= params.probCruce)
                if(posPadre1==-1) posPadre1 = i;
                else posPadre2 = i;
            else {
                descendientes[posDescendientes++] = poblacion[i];
            }
            
            /* if parents have been selected, then they are crossed over (posPadre1 & posPadre2 !=-1) */
            if(posPadre1!=-1 && posPadre2!=-1){
                descendientes[posDescendientes] = new CromosomaBinario(data.returnNumFeatures());
                descendientes[posDescendientes+1] = new CromosomaBinario(data.returnNumFeatures());
                
                poblacion[posPadre1].cruzar(poblacion[posPadre2], descendientes[posDescendientes], 
                                            descendientes[posDescendientes+1]);
                
                posDescendientes += 2;
                posPadre1 = posPadre2 = -1;
            }
            
        }
        
        /* if a single parent have been selected, then copy it directly to descendants pool */
        if(posPadre1!=-1 && posPadre2==-1 && posDescendientes < params.tamPoblacion){
            descendientes[posDescendientes] = poblacion[posPadre1];
        }
        
        /* lastly, mutates a small part of them */
        for(int i=0; i<params.tamPoblacion; i++){
            aleat = Randomize.RandClosed();
            
            if(aleat <= params.probMutacion)
                descendientes[i].mutar();
        }
        
        return descendientes;
    }
    
                
    /**
     * <p>
     * this method maintain elitism. Finds the best element and replaces it to the worst element of the new generation
     * </p>
     */
    private void elitismo(){
        boolean existe;
        int peor,i;
        double fitnessPeor;
        
        for(i=0; i<params.tamPoblacion && poblacion[i].isEqual(mejorIndividuo)==false; i++);
        
        /* looks best individual out. In affirmative case, finds the worst and replaces it */
        if(i<params.tamPoblacion){
            peor = 0;
            fitnessPeor = poblacion[0].getFitness();
            for(i=1; i<params.tamPoblacion; i++)
                if(fitnessPeor > poblacion[i].getFitness()){
                    fitnessPeor = poblacion[i].getFitness();
                    peor = i;
                }
            
            /* replaces it */
            poblacion[peor].copy(mejorIndividuo); 
        }
        
    }
    
    
    /**
     * <p>
    * checks if exists an empty chromosome, in other words, a chromosome that it hasn't selected any feature. 
    * An empty chromosome is created randomly
    * </p>
    */
    private void comprobarCromosomasVacios(){
        boolean vacio;
    
        int i = 0;
        while(i<params.tamPoblacion){ 
            vacio = true;
            for(int j=0; j<poblacion[i].devolverTamCromosoma() && vacio; j++)
                if(poblacion[i].devolverGen(j)!=0)
                    vacio = false;
            
            if(vacio){
                poblacion[i].initRand();
                poblacion[i].setFitness(-1);
            } else i++;
        }
    }
    
    
    /**
     * <p>
     * main method for Generational GA
     * </p>
     */
    private void modeloGeneracional(){
        int i, nEvaluaciones;
        boolean necesarioElitismo;
        Cromosoma padresSeleccionados[];
        Cromosoma descendientes[];
        
        /* number of evaluations count */
        nEvaluaciones = 0;

        /* creates the initial generation */
        for(i=0; i<params.tamPoblacion; i++) 
            poblacion[i].initRand();
   
        while(nEvaluaciones < params.numEvaluaciones){
         
            /* checks if exists an empty chromosome */
            comprobarCromosomasVacios();
            
            necesarioElitismo = true;
            /* evaluates the current generation (only non evaluated individuals) */
            for(i=0; i<params.tamPoblacion; i++) 
                if(poblacion[i].getFitness() == -1){
                    nEvaluaciones++;
                    poblacion[i].setFitness(fitness(poblacion[i]));
                                      
                    /* finds the best individual */
                    if(mejorIndividuo==null || poblacion[i].getFitness()>mejorIndividuo.getFitness()){ 
                            mejorIndividuo.copy(poblacion[i]);
                            nEvalMejorIndividuo = nEvaluaciones;           
                            necesarioElitismo = false;
                    }
                } 

            if(necesarioElitismo) 
                elitismo();

            /* selects the best parents */
            padresSeleccionados = seleccionPorTorneo();

            /* crosses over and mutates parentes. generates the new generation */
            descendientes = mutarYCruzar(padresSeleccionados);

            /* all descendants replace parents (although they were worst than them) */     
            poblacion = descendientes;
            
        }
    }  
        
    
    /**
     * <p>
     * method inteface for CHC algorithm.
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
       
        /* Generational GA algorithm */
        modeloGeneracional();
            
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


