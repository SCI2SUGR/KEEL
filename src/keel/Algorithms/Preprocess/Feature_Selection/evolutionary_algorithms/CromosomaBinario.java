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
 * CromosomaBinario.java
 *
 * Created on 23 de agosto de 2005, 0:26
 * 
 */

package keel.Algorithms.Preprocess.Feature_Selection.evolutionary_algorithms;
import org.core.Randomize;
import java.util.Vector;

/**
 * 
 * @author Manuel Chica Serrano
 *
 * class CromosomaBinario that defines a specificationn of Cromosoma, the internal representation will be a boolean array */

public class CromosomaBinario extends Cromosoma {
        
    /** Creates a new instance of CromosomaBinario 
     *  @param tama is the length of the chromosome  */
    public CromosomaBinario(int tama) {
        super(tama);          
    }
    
    
    /** random initialization of a chromosome (two values: 0 or 1)*/
    public void initRand(){
                
        for(int i=0; i<tamCromosoma; i++)
            rep[i] = Randomize.Randint(0,2);
        
    }
    
        
    /** this method is used in CHC method. 
     *   initializes a chromosome using a chromosome template and a random initialization
     *  @param crPlantilla is a chromosome template
     *  @param ratio is the ratio of random initialization */
     public void initPlantilla(Cromosoma crPlantilla, double ratio){
        
        for(int i=0; i<tamCromosoma; i++)
            if(Randomize.RandClosed() <= ratio)
                rep[i] = crPlantilla.devolverGen(i);
            else
                rep[i] = Randomize.Randint(0,2);
        
    }
    
    
    /** return the ith gene of chromosome 
        @param i is the ith position
        @return integer with gene value (0 or 1) */
    public int devolverGen(int i){
        
        if(i<0 || i>=tamCromosoma) {
            System.err.println("ERROR: Gen i out of chromosome bounds");
            System.exit(0); 
        }
        
        return rep[i];  
    }
    
       
    /** modifies the ith value of a gene 
        @param nuevoValorGen the new value for the gene 
        @param posGen the position what we want to change (0..tamCromsoma-1) */
     public void cambiarGen(int nuevoValorGen, int posGen){
        
        if(posGen<0 || posGen>=rep.length){
            System.err.println("ERROR: Gen 'posGen' out of chromosome bounds");
            System.exit(0); 
        }
        
        if(nuevoValorGen!=0 && nuevoValorGen!=1){
            System.err.println("ERROR: This is a binary chromosome, therefore it only contains binary values {0,1}");
            System.exit(0); 
        }
        
        rep[posGen] = nuevoValorGen;
        
    }
    
    
    /** crossover operator. The offsprings must be created before calling method
        @param padre2 is a parent
        @param hijo1 is the first offspring
        @param hijo2 is the second offspring */
    public void cruzar(Cromosoma padre2, Cromosoma hijo1, Cromosoma hijo2){
        int i, puntoCorte;
        
        if(padre2==null){
            System.err.println("ERROR: padre2 doesn't exist");
            System.exit(0); 
        }
        
        /* selects two cut points. it's neccesary to crossover operator */
        puntoCorte = Randomize.Randint(0, tamCromosoma);
        
        int vectorAux[] = new int[tamCromosoma];
        
        /* ------------------------------------------ FIRST OFFSPRING -------------------------------------*/
        for(i=0; i<puntoCorte; i++) vectorAux[i] = rep[i];
        for(i=puntoCorte; i<tamCromosoma; i++) vectorAux[i] = padre2.devolverGen(i);
        
        for(i=0; i<tamCromosoma; i++) hijo1.cambiarGen(vectorAux[i],  i);
               
        
        /* ------------------------------------------ SECOND OFFSPRING -------------------------------------*/
        for(i=0; i<puntoCorte; i++) vectorAux[i] = padre2.devolverGen(i);
        for(i=puntoCorte; i<tamCromosoma; i++) vectorAux[i] = rep[i];
        
        for(i=0; i<tamCromosoma; i++) hijo2.cambiarGen(vectorAux[i],  i);        
        
    }
    
    
    /** uniform crossover operator (HUX)
     *  If parents are very similar, the crossover operator doesn't apply
        @param padre2 is a parent        
        @param hijo1 is the first offspring
        @param hijo2 is the second offspring
        @param umbral is the threshold, needed by the operator 
        @return return true is the crossover operator was succesful, false in other case */
    public boolean cruzarHUX(Cromosoma padre2, Cromosoma hijo1, Cromosoma hijo2, int umbral){
        Vector v = new Vector();
        int i, aux, posACruzar, nGenesACruzar;
        
        if(padre2==null){
            System.err.println("ERROR: padre2 doesn't exist");
            System.exit(0); 
        }
        
        /* checks hamming distance */
        for(i=0; i<tamCromosoma; i++)
            if(rep[i]!=padre2.devolverGen(i))
                v.addElement(i);
        
        /* avoiding incest */
        if(v.size() < umbral)
            return false;
       
        hijo1.copy(this);
        hijo2.copy(padre2);
        hijo1.setFitness(-1);
        hijo2.setFitness(-1);
               
        i = 0;
        nGenesACruzar = v.size()/2;
        while(i < nGenesACruzar){
            /* selects a random value, removing it. Thereby it will not select it again */
            posACruzar = ((Integer)v.remove(Randomize.Randint(0,v.size()))).intValue();
           
            /* gene exchange */
            aux = hijo1.devolverGen(posACruzar);
            hijo1.cambiarGen(hijo2.devolverGen(posACruzar), posACruzar);
            hijo2.cambiarGen(aux, posACruzar);
            
            i++;            
        }
        
        return true;
    }
        
    
    /** binary mutation operator in one point */
    public void mutar(){
        int puntoAleat = Randomize.Randint(0, tamCromosoma);
        
        if(rep[puntoAleat] == 0) rep[puntoAleat]= 1;
        else rep[puntoAleat]= 0;      
        
        fitness = -1;
    }
    
    
    /** it prints a chromosome, gene by gene */
    public String print(){
        String res = new String();
        
        for(int i=0; i<tamCromosoma; i++)
            res += String.valueOf(rep[i]);
        
        return res;
    }  
    
    
    /** returns a boolean array needed for Leaving One Out, Cross Validation and other methods used in 
     *  Feature Selection Algorithm 
        @return a boolean array with the selected features */
    public boolean[] devolverFeaturesVector(){
        boolean featuresVector[];
        
        featuresVector = new boolean[tamCromosoma];
        
        /* converts integer values (0 & 1) to boolean java type (true or false)  */
        for(int i=0; i<tamCromosoma; i++)
            if(rep[i]==1) featuresVector[i]=true;
            else featuresVector[i]=false;
        
        return featuresVector;
    }
   
}

