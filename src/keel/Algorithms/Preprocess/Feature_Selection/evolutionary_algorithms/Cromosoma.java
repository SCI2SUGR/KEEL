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
 * Cromosoma.java
 *
 * Created on 23 de agosto de 2005, 0:11
 * 
 */

package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms;

/**
 * 
 * @author Manuel Chica Serrano
 *
 * Abstract class Chromosome that defines a generalization of CromosomaBinario & CromosomaEntero */
public abstract class Cromosoma {
    
    /** chromosome length */
    protected int tamCromosoma;
        
    
    /** this vector represents a chromosome  */
    protected int rep[];
            
    
    /** chromosome fitness */
    protected double fitness;
        
    
    /** Creates a new instance of Cromosoma 
        @param tam is the chromosome length
         */
    public Cromosoma(int tam) {
        if(tam<=1){
            System.err.println("ERROR: Chromosome length must be greater than 1");
            System.exit(0); 
        }
        
        fitness = -1;
        
        tamCromosoma = tam;
        rep = new int[tam];     
        for(int i=0; i<tamCromosoma; i++) rep[i] = -1;
    }    
    
    
    /** random initialization of a chromosome */
    public abstract void initRand();
    
    
    /** this method is used in CHC method. 
     *   initializes a chromosome using a chromosome template and a random initialization
     *  @param crPlantilla is a chromosome template
     *  @param ratio is the ratio of random initialization */
    public abstract void initPlantilla(Cromosoma crPlantilla, double ratio);
    
    /** return the ith gen of chromosome 
        @param i is the ith position
        @return integer with gene value */
    public abstract int devolverGen(int i);
    
    
    /** modifies the ith value of a gene 
        @param nuevoValorGen the new value for the gene 
        @param posGen the position what we want to change (0..tamCromsoma-1) */
    public abstract void cambiarGen(int nuevoValorGen, int posGen);
    
    
    /** crossover operator. The offsprings must be created before calling method
        @param padre2 is a parent
        @param hijo1 is the first offspring
        @param hijo2 is the second offspring */
    public abstract void cruzar(Cromosoma padre2, Cromosoma hijo1, Cromosoma hijo2);
        
    
    /** uniform crossover operator (HUX)
     *  If parents are very similar, the crossover operator doesn't apply
        @param padre2 is a parent        
        @param hijo1 is the first offspring
        @param hijo2 is the second offspring
        @param umbral is the threshold, needed by the operator 
        @return return true is the crossover operator was succesful, false in other case */
    public abstract boolean cruzarHUX(Cromosoma padre2, Cromosoma hijo1, Cromosoma hijo2, int umbral);
    
    
    /** mutation operator */
    public abstract void mutar();
    
    
    /** it prints a chromosome, gene by gene */
    public abstract String print();    
    
    
    /** returns a boolean array needed for Leaving One Out, Cross Validation and other methods used in 
     *  Feature Selection Algorithm 
        @return a boolean array with the selected features */
    public abstract boolean[] devolverFeaturesVector();
    
    
    /** set the fitness for the chromosome 
        @param calidad this is the fitness to be applied */
    public void setFitness(double calidad){
        fitness = calidad;
    }
    
    
    /** return the fitness of the chromosome 
        @return calidad this is the fitness of the chromosome */
    public double getFitness(){
        return fitness;
    }
    
    
    /** return the length of the chromosome */
    public int devolverTamCromosoma(){
        return tamCromosoma;
    }
    
    
    /** this boolean method return true if two chromosomes are equal in all of its gens 
        @param cr is the other chromosome to compare */
    public boolean isEqual(Cromosoma cr){
        if(tamCromosoma != cr.devolverTamCromosoma()){
            System.err.println("ERROR: Chromosome length isn't equal");
            System.exit(0); 
        }
        
        for(int i=0; i<tamCromosoma; i++)
            if(rep[i] != cr.devolverGen(i))
                return false;
        
        return true;
    }
    
    
    /** it copies all gens of the cr chromosome 
        @param cr is the chromosome that will be copied */
    public void copy(Cromosoma cr){
        if(tamCromosoma != cr.devolverTamCromosoma()){
            System.err.println("ERROR: Chromosome length isn't equal");
            System.exit(0); 
        }
        for(int i=0; i<tamCromosoma; i++)
            rep[i] = cr.devolverGen(i);
        
        fitness = cr.getFitness();
    }
}

