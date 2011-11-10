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
 * @file Rbf.java
 * @author Written by Antonio Jesus Rivera Rivas (University of Jaen) 03/03/2004
 * @author Modified by Victor Manuel Rivas Santos (University of Jaen) 03/03/2004
 * @version 0.1
 * @since JDK1.5
 *</p>
 */


package keel.Algorithms.Neural_Networks.EvRBF_CL;

import org.core.*;

public class Rbf implements Cloneable {
/**
 * <p>
 * Class representing a Radial Basis Function Neuron. Needed to implement RBF Neural Networks
 * </p>
*/

		// Number of RBF defined
    static int cont=0;

		// Number of Inputs
    int nInputs;

		// Number of outputs
    int nOutputs;

		// Radius of the RBF
    double aRadius;

		// Point in which the RBF is centered
    double [] aCenter;

		// Weights from the neuron to outputs neurons
    double [] weights;

		// Radial basis function neuron identifier
    String idRbf;


    /** 
     * <p>
     * Creates a new instance of neuron/rbf 
     * </p>
     * @param ent Number of inputs
     * @param sal Number of outputs
     */
    public Rbf(int ent, int sal) {
	        nInputs = ent;
        nOutputs = sal;
        aCenter = new double[ent];
        weights = new double[sal];
        idRbf = String.valueOf(cont++);
    }

    /** 
     * <p>
     * Clones a neuron/rbf 
     * </p>*/
    public Object clone  () {
        try{
            Rbf rbf=(Rbf)super.clone();
            rbf.aCenter=(double [])aCenter.clone();
            rbf.weights=(double [])weights.clone();
            return (rbf); }
        catch (CloneNotSupportedException e){
            throw new InternalError(e.toString());
        }
    }
  
    /**
     * <p>
     * Sets the main parameters of a neuron
     * </p>
     * @param c Vector of centres
     * @param r Radius
     * @param p Weights
     */
    public void setParam(double [] c,double r, double [] p) {
        int i;

        aRadius = r;
        for (i=0 ; i<nInputs ; i++)
            aCenter[i] = c[i];
        for (i=0 ; i<nOutputs ; i++)
            weights[i] = p[i];
    }
    
    
   /** 
    * <p>
    * Gets the vector of centres of a neuron
    * </p>
    * @return A vector of doubles with centre of a neuron
    */
   public double [] getCenter() {
       return(aCenter);
   }    

   /**
    * <p>
    * Sets the vector of centres of a neuron
    * </p>
    * @param c centre of a neuron
		* return Nothing
    */
   public void setCenter(double [] c) {
       int i;
       
       for (i=0 ; i<nInputs ; i++)
           aCenter[i] = c[i];
   }    

   /**
    * <p>
    * Gets the radius of a neuron
    * </p>
    * @return A double with the radius of a neuron
    */
   public double getRadius() {
       return(aRadius);
   }
   
   /**
    * <p>
    * Sets the radius of a neuron
    * </p>
    * @param r Radius of a neuron
    */
   public void setRadius(double r) {
        aRadius = r;
   }    
  

   /** 
    * <p>
    * Gets the weights of a neuron
    * </p>
    * @return A vector of doubles with the weights of a neuron
    */
   public double [] getWeights() {
       return(weights);
   }
  
   /**
    * <p>
    * Sets the weights of a neuron
    * </p>
    * @param p A vector of doubles with the weights of a neuron
    */
   public void setWeights(double [] p) {
       int i;

       for (i=0 ; i<nOutputs ; i++)
           weights[i] = p[i];
   }
   
   /**
    * <p>
    * Gets the i-th weight of a neuron 
    * </p>
    * @param i Index of weights of a neuron to get
    * @return A double with weight to get
    */
   public double getWeight(int i){
       return(weights[i]);
   }


   /** 
    * <p>
    * Sets the ith weight of a neuron
    * </p>
    * @param i Index of weights of a neuron to set
    * @param val Value of the weight
    */
   public void setWeight(int i,double val){
       weights[i] = val;
   }

   /**
    * <p>
    * Gets the id of the  neuron
    * </p>
    * @return The ID of the neuron
    */
   public String getIdRbf(){
       return idRbf;
   }
    
   /**
    * <p>
    * Computes the euclidean distance between a neuron and a vector
    * </p>
    * @param v A vector
    * @return A double with the euclidean distance
    */
   public double euclideanDist(double [] v) {
       int i;
       double aux=0;

       for (i=0; i<nInputs; i++)
           aux+=(v[i]-aCenter[i])*(v[i]-aCenter[i]);
       return(Math.sqrt(aux));
   }

   /**
    * <p>
    * Computes the ouput of a RBF
    * </p>
    * @param _input Input vector
    * @return The ouput of a RBF
    */
   public double evalRbf (double [] _input ) {
       double aux;
       aux=RBFUtils.euclidean( _input, aCenter );
       aux*=aux;
       aux/=(aRadius*aRadius);
       return(Math.exp(-aux));
   }
   
   /**
    * <p>
    * Prints neuron on std out
    * </p>
    */
   public void paint( ){
   	this.paint( "" );
   }



   /**
    * <p>
    * Prints neuron on a file
    * </p>
    * @param _fileName Name of the file.
    */
   public void paint( String _fileName ){
        int i;
        if ( _fileName!="" ) {
        	Files.addToFile( _fileName, "   Radius "+aRadius+"\n" );
        } else {
        	System.out.println("   Radius "+aRadius);
        }
        for (i=0;i<nInputs;i++) {
        	if ( _fileName!="" ) {
        		Files.addToFile( _fileName, "   Center "+aCenter[i]+"\n" );
        	} else {
            	System.out.println("   Center "+aCenter[i] );
         	}
        }

        for (i=0;i<nOutputs;i++) {
        	if( _fileName!="" ) {
            	Files.addToFile( _fileName, "   Weigth "+getWeight(i)+"\n" );
             } else {
                System.out.println("   Weigth "+getWeight(i));
             }
        }
   }
}

