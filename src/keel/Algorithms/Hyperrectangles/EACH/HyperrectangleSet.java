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

public class HyperrectangleSet{
/**
 * <p>
 * Structure to store a set of hyperrectangle
 * </p>
 */
	
	// Size f the set
    int size;
    Hyperrectangle []set;
    // weight for each attribute
    double []weightAtrib;
    int attributes;
    // Attribute used to adjust the weight of the attributes
    double increment;

    /**
     * <p>
     * Constructor
     * </p>
     * @param setv a vector that contains the hyperrectangles
     * @param atributos the number of attributes of the set of data
     * @param ndatos the number of data of train set 
     * @param utiles the actual number of hyperrectangles
     * @param delta parameter to adjust the weight of the attributes
     */
  public HyperrectangleSet(Hyperrectangle[]setv,int atributos,int ndatos,int utiles,double delta){
	size=utiles;
	set=new Hyperrectangle[ndatos];
	this.set=setv;
	increment=delta;/*0.2*/
	this.attributes=atributos;
	weightAtrib=new double[atributos];
    }
    
    public void setWeightAtrib(double []weight){
    	weightAtrib=weight;
    }
    
    /**
     * <p>
     * Return the weight for each attribute
     * </p>
     */
    public double[]getWeightAttributes(){
    	double []weights=new double[attributes];
	for(int i=0;i<attributes;i++)weights[i]=weightAtrib[i];
	return weights;
	
    }
    
    /**
     * <p>
     * Stores in memory the new hyperrectangle 
     *</p>
     */
    public void store_in_memory(Hyperrectangle H){
    	set[size]=H;
	size++;
    }
    
    /**
     * <p>
     * Adjust the weights of the attributes
     * </p>
     * @param example the associated instance
     * @param H the index in the set of the hyperrectangle associated
     */
    public void adjustFeatureWeights(double[] example,int H){
    	for(int i=0;i<attributes;i++){
		//match (para el atributo i, el valor de E esta dentro de los limites de H
		//por lo que incrementamos el peso
		//an increase in weight causes the two objects to seem farther apart, and the
		//idea is that since Each made a mistake matching E and H, it should push them
		//apart in space
	     if(example[i]<=set[H].getUpperValues()[i]&&example[i]>=set[H].getLowerValues()[i]){
			weightAtrib[i]*=(1+increment);}
		//no match
		else weightAtrib[i]*=(1-increment);
	}
    	
    }
    /**
     * <p>
     * Calculates the two lowest distances of the example to two hyperrectangles
     * </p>
     * @param example the instance
    */
    public int [] distance(double[] example){
    	int matches[]=new int[2];
	int match1,match2;
	double dist1,dist2,act,aux;
	dist1=set[0].distance(example,weightAtrib);
	if(size==1){match1=0;match2=0;}
	else{
	dist2=set[1].distance(example,weightAtrib);
	match1=0;match2=1;
	if(dist2<dist1){aux=dist1;dist1=dist2;dist2=aux;match1=1;match2=0;}
	for(int i=2;i<size;i++){
		act=set[i].distance(example,weightAtrib);
		if(act<dist1){dist2=dist1;dist1=act;match2=match1;match1=i;}
		else{
		  if(act<dist2){dist2=act;match2=i;}
		}
	}
	matches[0]=match1;
	matches[1]=match2;
	}//del else
	return matches;
    }
    
   /**
    * <p>
    * Returns a hyperrectangles of the set
    * </p>
    * @param indice position in the set
    */
    public Hyperrectangle getHyperrectangle(int indice){
    	return set[indice];
   }
   
   /**
    * <p>
    * Returns on a string the name and its weight of each attribute
    * </p>
    */
   public String printWeightsAtributes(String []nombreAtributos){
   	String cad = "";
	cad +="\n\nAttributes Weights: ";
        for (int i = 0; i < attributes; i++) {
            cad += nombreAtributos[i] + " = " + weightAtrib[i]+"   ";
        }
        return cad;
   }
    
   
}

