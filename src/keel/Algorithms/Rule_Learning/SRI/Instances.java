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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Rule_Learning.SRI;

import java.util.*;

/**
 * <p>Title: Intances class </p>
 *
 * <p>Description: it stores the dataset in a way that this Rule learning algorithm can understand. </p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Ismael Duque García
 * @version 1.0
 */
public class Instances {
    
    LinkedList<LinkedList<Double>> examples = new LinkedList <LinkedList<Double>>();
    LinkedList<String> outputs = new LinkedList<String>();
    int num_clases;//Posibles valores de la clase salida
    
    /**
     * Default constructor.
     */
    public Instances(){}
    
     /**
     * Parameter constructor. 
     * Creates a dataset by adding the given examples given.
     * @param e double[][] examples of the dataset.
     * @param o String[] Outputs of the examples.
     * @param num_a not used.
     * @param n_class number of classes in the dataset.
     */
    public Instances(double[][] e,String[] o, int num_a,int n_class){
        
        for(int i=0; i< e.length;i++){
            LinkedList<Double> aux = new LinkedList <Double> ();
            for(int j=0; j<e[i].length;j++){
                aux.add(e[i][j]);                
            }
            examples.add(aux);
        }
        
        for(int i=0; i< o.length;i++){
           outputs.add(o[i]); 
        }
        num_clases = n_class;
    }
    
    /**
     * Returns the output value of the example in the position given.
     * @param i int given index/position.
     * @return String the output value of the ith example
     */
    public String getOutputAsString(int i){
        return outputs.get(i);
    }
    
    /**
     * Returns the number of attributes of the dataset.
     * @return int number of attributes.
     */
    public int getnInputs(){
        return examples.get(0).size();
    }
    
    /**
     * Returns the number of examples.
     * @return int number of examples.
     */
    public int getnData(){
        return examples.size();
    }
    
    /**
     * Returns the number of classes or output values.
     * @return int the number of classes or output values.
     */
    public int getnClass(){return num_clases;}
    
    /**
     * Returns the example  in the position given. 
     * @param i int given index/position.
     * @return LinkedList values of the ith example.
     */
    public LinkedList<Double> getExample(int i){
        return examples.get(i);
    }
    
    /**
     * Removes the example in the given position.
     * @param i int given index/position.
     */
    public void removeInstance(int i){
        this.examples.remove(i);
        this.outputs.remove(i);
    }
   
    /**
     * Removes all the examples whose indeces are given as parameter.
     * @param eliminar LinkedList<Integer> indeces of the examples to be removed.
     */
    public void removeInstances(LinkedList<Integer> eliminar){
        LinkedList<LinkedList<Double>> auxiliar_examples = new LinkedList<LinkedList<Double>>();
        LinkedList<String> auxiliar_outputs = new LinkedList<String>(); 
        
        int tama=0;
        for(Integer i=0; i<examples.size(); i++){
            if(!eliminar.contains(i)){
                auxiliar_examples.add(examples.get(i));
                auxiliar_outputs.add(outputs.get(i));
                tama++;
            }
        }
        examples = (LinkedList) auxiliar_examples.clone();
        outputs = (LinkedList) auxiliar_outputs.clone();
    }
    
    /**
     * Checks if the dataset is empty.
     * @return boolean True if it is empty, false otherwise.
     */
    public boolean isEmpty(){
        return examples.isEmpty();
    }

}

