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
 * @author Written by Rosa Venzala (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Hyperrectangles.EACH;

import org.core.*;
import keel.Dataset.*;
import java.util.*;
public class Hyperrectangle  {
/**
 * <p>
 * Structure to store a hyperrectangle
 * </p>
 */

	// associated class
    int classAttribute;
    // maximum value for each attribute(hyperrectangle)
    double[]upperValues;
    // minimum value for each attribute(hyperrectangle)
    double []lowerValues;
    // Position of Training set upper values
    int []numIns_upper;
    int []numIns_lower;
    // The number of examples associated to hyperrectangle
    int numSamples;
    // The weight of the hyperrectangle
    double weight;
    // correct predictions used
    int passed,used;
    // features
    int attributes;
    double volume;
    int numInstance;
    // specify if the H es an exception(0), a line(1),... maximum will be the number of attributes
    int numDimensions; 

    /**
     * <p>
     * Constructor
     * </p>
     */
    public Hyperrectangle(){
    }
    
    /**
     * <p>
     * Constructor
     * </p>
     */
    public Hyperrectangle(int atributos, double []example,int numclase,int numinstancia){
	numSamples=1;
	passed=1;
	used=1;
	weight=1.;
	this.attributes=atributos;
	upperValues=new double[atributos];
	lowerValues=new double[atributos];
	numIns_upper=new int[atributos];
	numIns_lower=new int [atributos];
  	for(int i=0;i<atributos;i++){
		upperValues[i]=example[i];
		lowerValues[i]=example[i];
		numIns_upper[i]=numinstancia;
		numIns_lower[i]=numinstancia;
	}
	classAttribute=numclase;
	this.numInstance=numinstancia;
	
    }
    
    /**
     * <p>
     * Adds a new example to the hyperrectangle
     * </p>
     * @param example the new example to add
     * @param num the position in the Training set
     */
    public void generalizeExemplar(double []example,int num){
    	numSamples++;
	for(int i=0;i<attributes;i++){
		if(upperValues[i] < example[i]){upperValues[i]=example[i];numIns_upper[i]=num;}
		if(example[i] < lowerValues[i]){lowerValues[i]=example[i];numIns_lower[i]=num;}
	}
    }
    
    /**
     * <p>
     * Adjust the weight of the hyperrectangle by a goal
     * </p>
     */
    public void adjustWeightSuccess(){
    	passed++;
	used++;
	weight=(double)used/(double)passed;
    }
    
    /**
     * <p>
     * Adjust the weight of the hyperrectangle by a miss
     * </p>
     */
    public void adjustWeightFailure(){
	used++;
	weight=(double)used/(double)passed;
    }
    
   /**
    * <p>
    * Calculates the distance between hyperrectangle and the example(parameter)
    * </p>
    * @param example the instance to calculate the distance
    * @param weightAtrib a vector that contains the weight for each attribute
    * @return distance   
   */
   public double distance(double[]example,double []weightAtrib){
   	double factor,distancia=0.;
   	for(int i=0;i<attributes;i++){
		if(numSamples==1){//H en este caso es solo un punto
		factor=example[i]-upperValues[i];
		}
		else{
			if(example[i] > upperValues[i])
				factor=example[i]-upperValues[i];
			else{if(example[i] < lowerValues[i])
				factor=lowerValues[i]-example[i];
			     else factor=0;
			     }
		}
		factor=factor*weightAtrib[i];
		factor=factor*factor;
		distancia=distancia+factor;
	}
	distancia=Math.sqrt(distancia);
	distancia=distancia*weight;
	return distancia;
   	
  }
  
  /**
   * <p>
   * Calculates the area, volumn, ... of the hyperrectangle
   * </p>
   */
   public void calculeVolume(){
   	volume=1.;
	boolean noHole=false;
	numDimensions=attributes;
   	for(int i=0;i<attributes;i++){
		if(((upperValues[i]-lowerValues[i])>0.)){
			volume=volume*(upperValues[i]-lowerValues[i]);
			noHole=true;
		}
		else numDimensions--;
	}
	if(!noHole)volume=0.;//en una excepcion o hole el volumen ha de ser 0
					//ndimensiones habra sido 0 tambien
   }
   
   public double getVolume(){
   	return volume;
   }
   public int getDimensions(){
	return numDimensions;
    }
    public double getWeight(){
    	return weight;
   }
    public int getClassAttribute(){
    	return classAttribute;
   }

   public double[]getLowerValues(){
   	return lowerValues;
   }
   public double[]getUpperValues(){
   	return upperValues;
   }
   public int getNumExamples(){
   	return numSamples;
   }
   
   /**
    * <p>
    * Returns the positions of the minimum and maximum value of the attribute id in the hyperrectangle
    * </p>
    * @param id the number of the attribute
    * @return v a vector that contains those two numbers
   */
   public int[]getLowerAndUpperValues(int id){
   	int v[]=new int[2];
	/*v[0]=lowerValues[id];
	v[1]=upperValues[id];*/
	v[0]=numIns_lower[id];
	v[1]=numIns_upper[id];
	return v;
   }
   
   /**
    * <p>
    * Prints an instance
    * </p>
    */
   public void print(){
    System.out.print(numInstance+" ");
    System.out.print("clase "+classAttribute);
    /*System.out.println("numero de ejemplos "+numEjemplos);
    for(int i=0;i<atributos;i++){
    	System.out.println("ATRIB "+i+" ["+lowerValues[i]+" "+upperValues[i]+" ]");
    }*/
    }
   
}

