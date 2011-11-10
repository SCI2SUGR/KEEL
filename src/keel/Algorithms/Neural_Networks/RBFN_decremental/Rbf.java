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


 /* rbf.java
 
/**
 * <p>
 * @author Writen by Antonio J. Rivera Rivas (University of Jaï¿½n) 03/03/2004
 * @author Modified by Marï¿½a Dolores Pï¿½rez Godoy (University of Jaï¿½n) 17/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Neural_Networks.RBFN_decremental;


import org.core.*;



public class Rbf implements Cloneable {
/**
 * <p>
 * This class codified a neuron or a RBF
 * </p>
 */
	
	
    static int count=0;
    int nInput;
    int nOutput;
    int vectors;
    double radius;
    double [] centre;
    double [] weight;
    String idRbf;



   /**
     * <p>
     * Creates a new instance of neuron/rbf 
     * </p>
     * 
     * 
     * @param inpt Number of inputs
     * @param outp Number of outputs
     */
    public Rbf(int inpt, int outp) {
    	
        nInput = inpt;
        nOutput = outp;
        centre = new double[inpt];
        weight = new double[outp];
        idRbf = String.valueOf(count++);
    }
    
   /**
     * <p>
     * Creates a new instance of neuron/rbf 
     * </p>
     * 
     * 
     * 
     * @param inpt Number of inputs
     * @param outp Number of outputs
     * @param _vectors
     */
    public Rbf(int inpt, int outp,int _vectors) {
        nInput = inpt;
        nOutput = outp;
        vectors = _vectors;
        centre = new double[inpt];
        weight = new double[outp];
        idRbf = String.valueOf(count++);
    }

    
   /**
    * <p>
    * Clones a neuron/rbf
    * </p>
    * @return a copy of a neuron
    */
    public Object clone  () {
        try{
            Rbf rbf=(Rbf)super.clone();
            rbf.centre=(double [])centre.clone();
            rbf.weight=(double [])weight.clone();
            rbf.vectors = vectors;
            return (rbf); }
        catch (CloneNotSupportedException e){
            throw new InternalError(e.toString());
        }
    }
    
   /**
     * <w>
     * Sets the main parameters of a neuron
     * </w>
     * 
     * @param c Vector of centres
     * @param r Radius
     * @param w Weights
     *
     */
    public void setParam(double [] c,double r, double [] w) {
        int i;

        radius = r;
        for (i=0 ; i<nInput ; i++)
            centre[i] = c[i];
        for (i=0 ; i<nOutput ; i++)
            weight[i] = w[i];
    }
    
   /**
     * <w>
     * Sets the main parameters of a neuron
     * </w>
     * 
     * 
     * @param c Vector of centres
     * @param r Radius
     * @param w Weights
     * @param _vectors
     *
     */
    
    public void setParam(double [] c,double r, double [] w,int _vectors) {
        int i;
        vectors = _vectors;
        radius = r;
        for (i=0 ; i<nInput ; i++)
            centre[i] = c[i];
        for (i=0 ; i<nOutput ; i++)
            weight[i] = w[i];
    }

   /**
    * <p>
    * Gets the vector of centres of a neuron
    * </p>
    * @return A vector of doubles with centre of a neuron
    */
   public double [] getCentre() {
       return(centre);
   }    

  /**
   * <p>
   * Sets the vector of centres of a neuron
   * </p>
   * @param c centre of a neuron
   * 
   */
   public void setCentre(double [] c) {
       int i;
       
       for (i=0 ; i<nInput ; i++)
           centre[i] = c[i];
   }    
   
  /**
   * <p>
   * Gets the radius of a neuron
   * </p>
   * @return A double with the radius of a neuron
   */
   public double getRadius() {
       return(radius);
   }
   
  /**
   * <p>
   * Sets the radius of a neuron
   * </p>
   * @param r Radius of a neuron
   *
   */
   public void setRadius(double r) {
        radius = r;
   }    
   
  /**
   * <p>
   * Gets the weights of a neuron
   * </p>
   * @return A vector of doubles with the weights of a neuron
   */
   public double [] getWeights() {
       return(weight);
   }
   
  /**
     * <w>
     * Sets the weights of a neuron
     * </w>
     * 
     * @param w A vector of doubles with the weights of a neuron
     *
     */
   public void setWeight(double [] w) {
       int i;

       for (i=0 ; i<nOutput ; i++)
           weight[i] = w[i];
   }
   
  /**
   * <p>
   * Gets the ith weight of a neuron
   * </p>
   * @param i Index of weights of a neuron to get
   * @return A double with weight to get
   */
   public double getWeight(int i){
       return(weight[i]);
   }

   
  /**
   * <p>
   * Sets the ith weight of a neuron
   * </p>
   * @param i Index of weights of a neuron to set
   * @param val Value of the weight
   *
   */
   public void setWeight(int i,double val){
       weight[i] = val;
   }
    
  /**
   * <p>
   * Computes the euclidean distance between a neuron and a vector
   * </p>
   * @param v A vector
   * @return A double with the euclidean distance
   */
   public double euclideaDist(double [] v) {
       int i;
       double aux=0;

       for (i=0; i<nInput; i++)
           aux+=(v[i]-centre[i])*(v[i]-centre[i]);
       return(Math.sqrt(aux));
   }

  /**
   * <p>
   * Computes the ouput of a RBF
   * </p>
   * @param _input Input vector
   * @return The ouput of a RBF
   */
   public double evaluationRbf (double [] _input ) {
       double aux;
       aux=RBFUtils.euclidean( _input, centre );
       aux*=aux;
       aux/=(2.0*radius*radius);
       return(Math.exp(-aux));
   }
   
  /**
   * <p>
   * Prints neuron on std out
   * </p>
   *
   */
   public void printRbf( ){
   	this.printRbf( "" );
   }

  /**
   * <p>
   * Prints neuron on a file.
   * </p>
   * @param _fileName Name of the file.
   *
   */
   public void printRbf( String _fileName ){
        int i;

        if ( _fileName!="" ) {
        	Files.addToFile( _fileName, "   Radius "+radius+"\n" );
        } else {
        	System.out.println("   Radius "+radius);
        }
        for (i=0;i<nInput;i++) {
        	if ( _fileName!="" ) {
        		Files.addToFile( _fileName, "   Center "+centre[i]+"\n" );
        	} else {
            	System.out.println("   Center "+centre[i] );
         	}
        }

        for (i=0;i<nOutput;i++) {
        	if( _fileName!="" ) {
            	Files.addToFile( _fileName, "   Weigth "+getWeight(i)+"\n" );
             } else {
                System.out.println("   Weigth "+getWeight(i));
             }
        }


   }

}

