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
 * CromosomaEntero.java
 *
 * Created on 23 de agosto de 2005, 11:50
 *
 */

package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms;
import org.core.Randomize;

/**
 * 
 * @author Manuel Chica Serrano
 
 * class CromosomaEntero that defines a specificationn of Cromosoma, the internal representation will be an integer array */
public class CromosomaEntero extends Cromosoma{

    /** numCaracteristicas is the number of the total dataset's features. This number will define the maximum gen value (0,numCaracs-1) */
    private int numCaracteristicas;
     
        
    /** Creates a new instance of CromosomaEntero 
     *  @param tam is the number of features to be selected
        @param numCaracs is the total dataset's features */
    public CromosomaEntero(int numCaracs, int tam) {
        
        super(tam);        
        numCaracteristicas = numCaracs;
                
    }
    
    
    /** random initialization of a chromosome. Tbe random values are defined between 0 and total dataset's features
		-1 */
    public void initRand(){
        int temp = 0,i,j;
        
        for(i=0; i<tamCromosoma; i++){
            
            j = -1;
            while(j!=i){ 
                temp = Randomize.Randint(0,numCaracteristicas);
            
                /** checks if temp value hasn't been selected yet */
                for(j=0; j<i && rep[j]!=temp; j++); 
            
            }
            
            rep[i] = temp;
        }
    }
    
    
    /** this method is used in CHC method. 
     *   initializes a chromosome using a chromosome template and a random initialization
     *  @param crPlantilla is a chromosome template
     *  @param ratio is the ratio of random initialization */
    public void initPlantilla(Cromosoma crPlantilla, double ratio){
        int temp = 0,i,j;
        
        /* firstly, copies the chromosome template into new chromosome */
        for(i=0; i<tamCromosoma; i++)
            if(Randomize.RandClosed() <= ratio)
                rep[i] = crPlantilla.devolverGen(i);
            else 
                rep[i] = -1;
        
        /* secondly, completes gens containing -1 with randomly numbers */
        for(i=0; i<tamCromosoma; i++){
            
            j = -1;
            while(j!=i){ 
                temp = Randomize.Randint(0, numCaracteristicas);
            
                /* vemos si no estaba ya el valor aleatorio en el cromosoma */
                for(j=0; j<tamCromosoma && rep[j]!=temp; j++); 
            
            }
            
            /* aqui tenemos ya un valor entero temp no repetido en el cromosoma */
            rep[i] = temp;
        }
    }
    
    
    /** return the ith gen of chromosome 
        @param i is the ith position
        @return integer with gen value */
    public int devolverGen(int i){
        
        if(i<0 || i>=tamCromosoma) {
            System.err.println("ERROR: Gen i is out of chromosome bounds");
            System.exit(0); 
        }
        
        return rep[i];  
    }
    
    
    /** modifies the ith value of a gene 
        @param nuevoValorGen the new value for the gene 
        @param posGen the position what we want to change (0..tamCromsoma-1) */
     public void cambiarGen(int nuevoValorGen, int posGen){
        int j;
        
        if(posGen<0 || posGen>=rep.length){
            System.err.println("ERROR: Gen 'posGen' is out of bounds");
            System.exit(0); 
        }
        
        if(nuevoValorGen<0){
            System.err.println("ERROR: The new value for gen doesn't correct. " +
                    "It must be greater than -1 & less than numFeatures-1");
            System.exit(0); 
        }
        
        /* checks if nuevoValorGen is in chromosome */
        for(j=0; j<rep.length && rep[j]!=nuevoValorGen; j++);
        
        if(j==rep.length) 
            rep[posGen] = nuevoValorGen;
        else {
            System.err.println("ERROR: New value for chromosome is incorrect. This value already exists");
            System.exit(0); 
        }
    }
    
    
    /** crossover operator. The offsprings must be created before calling method
        @param padre2 is a parent
        @param hijo1 is the first offspring
        @param hijo2 is the second offspring */
    public void cruzar(Cromosoma padre2, Cromosoma hijo1, Cromosoma hijo2){
        int i, pos, j, temp = 0, puntoCorte1, puntoCorte2;
        
        if(padre2==null){
            System.err.println("ERROR: padre2 doesn't exist");
            System.exit(0); 
        }
           
        /* selects two cut points. it's neccesary to crossover operator */
        do {
            puntoCorte1 = Randomize.Randint(0,tamCromosoma);
            puntoCorte2 = Randomize.Randint(0,tamCromosoma);
        } while(puntoCorte1 >= puntoCorte2);
        
        int vectorAux[] = new int[tamCromosoma];
        for(i=0; i<tamCromosoma; i++) vectorAux[i] = -1;
        
        /* ------------------------------------------ FIRST OFFSPRING -------------------------------------*/
        /* to create this first offspring we need to copy padre1's gens between puntoCorte1 and puntoCorte2
         * Then , completes with padre2's genes */
        
        for(i=puntoCorte1; i<puntoCorte2; i++) vectorAux[i] = rep[i];
        
        /* completes with padre2's genes */
        pos = puntoCorte2;
        for(i=puntoCorte2; i<tamCromosoma; i++){
            temp = padre2.devolverGen(i);
            for(j=0; j<i && vectorAux[j]!=temp; j++);

            if(j==i) vectorAux[pos++] = temp;            
        }
        
        for(i=0; i<puntoCorte1; i++){
            temp = padre2.devolverGen(i);
            for(j=i+1; j<tamCromosoma && vectorAux[j]!=temp; j++);

            if(j==tamCromosoma){ 
                if(pos==tamCromosoma) pos = 0;                    
                vectorAux[pos++] = temp;
            }
        }
        
        /* the chromosome has genes with -1. we have to change them with new integer values that are not in the chromosome */
        for(i=0; i<tamCromosoma; i++) 
            if(vectorAux[i] == -1) {
                /* finds a new valid value */
                j = 0;
                while(j!=tamCromosoma){ 
                    temp = Randomize.Randint(0, numCaracteristicas);
            
                    /** finds the new value in the chromosome. if this value exists, other new value will be created */
                    for(j=0; j<tamCromosoma && vectorAux[j]!=temp; j++); 
            
                }
                vectorAux[i] = temp;
            }
        for(i=0; i<tamCromosoma; i++) hijo1.cambiarGen(vectorAux[i], i);
        
        
        /* ------------------------------------------ SECOND OFFSPRING -------------------------------------*/
        /* creates the second offspring with inverse previous method */
        
        for(i=0; i<tamCromosoma; i++) vectorAux[i] = -1;
        
        for(i=puntoCorte1; i<puntoCorte2; i++) vectorAux[i] = padre2.devolverGen(i);
        
        /* completes with padre1 information */
        pos = puntoCorte2;
        for(i=puntoCorte2; i<tamCromosoma; i++){
            temp = rep[i];
            for(j=0; j<i && vectorAux[j]!=temp; j++);

            /* checks if temp value has repeated */
            if(j==i) vectorAux[pos++] = temp;            
        }

        for(i=0; i<puntoCorte1; i++){
            temp = rep[i];
            for(j=i+1; j<tamCromosoma && vectorAux[j]!=temp; j++);

            /* checks if temp value has repeated */
            if(j==tamCromosoma){ 
                if(pos==tamCromosoma) pos = 0;                    
                vectorAux[pos++] = temp;
            }   
        }
        
        /* it's possibly that new offspring has -1 values. this values will be changed with other valids integer values */
        for(i=0; i<tamCromosoma; i++) 
            if(vectorAux[i] == -1) {
                /* the new value must to not be in chromosome */
                j = 0;
                while(j!=tamCromosoma){ 
                    temp = Randomize.Randint(0, numCaracteristicas);
            
                    /** checks if this value was in chromosome */
                    for(j=0; j<tamCromosoma && vectorAux[j]!=temp; j++); 
            
                }
                vectorAux[i] = temp;
            }
        for(i=0; i<tamCromosoma; i++) hijo2.cambiarGen(vectorAux[i], i);
        

    }
    
    
    /** this method can't be applied to integer chromosome. This is a EMPTY METHOD */
    public boolean cruzarHUX(Cromosoma padre2, Cromosoma hijo1, Cromosoma hijo2, int umbral){        
        return false;
    }     
     
	 
    /** integer mutation operator in one point */
    public void mutar(){
        int puntoAleat = Randomize.Randint(0,tamCromosoma);
        int j, temp=0;
        
        /* the random gene has been initialized to -1 */
        rep[puntoAleat] = -1;
        
        j = -1;
        while(j!=tamCromosoma){ 
                temp = Randomize.Randint(0,numCaracteristicas);
            
                /** finds new value in chromosome */
                for(j=0; j<tamCromosoma && rep[j]!=temp; j++);             
        }
        
        rep[puntoAleat] = temp;
        fitness = -1;
    }
    
    
    /** it prints a chromosome, gene by gene */
    public String print(){
        String res = new String();
        
        for(int i=0; i<tamCromosoma; i++)
            res += "Gen " + String.valueOf(i+1) + ": Value " + String.valueOf(rep[i]) + "\n";
        
        return res;
    }  
    
    /** returns a boolean array needed for Leaving One Out, Cross Validation and other methods used in 
     *  Feature Selection Algorithm 
        @return a boolean array with the selected features */
    public boolean[] devolverFeaturesVector(){
        boolean featuresVector[];
        
        featuresVector = new boolean[numCaracteristicas];
        for(int i=0; i<numCaracteristicas; i++) 
            featuresVector[i] = false;
        
        for(int i=0; i<tamCromosoma; i++)
            featuresVector[rep[i]] = true;
           
        return featuresVector;
    }
    
    
}

