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
 * @author Writen by Luciando Sánchez (University of Oviedo) 
 * @author Modified by Antonio J. Rivera Rivas (University of Jaen) 
 * @author Modified by Maria Dolores Perez Godoy (University of Jaen) 17/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Neural_Networks.RBFN_decremental_CL;

 import java.io.*;
 import java.util.Random;
 import java.util.StringTokenizer;
 import java.util.Vector;

 import keel.Dataset.*;

 // Wrapper for KEEL's Dataset class
 /**
  * <p>
  * Process the KEEL dataset
  * </p>
  * <p>
 * @author Writen by Luciando Sánchez (University of Oviedo) 
 * @author Modified by Antonio J. Rivera Rivas (University of Jaen) 
 * @author Modified by Maria Dolores Perez Godoy (University of Jaen) 17/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
  */ 
  
 public class ProcDataset {

     private double[][] X = null;
     private boolean[][] missing = null;
     private double[] Y = null;
     private int [] C = null;
     private double[] imax;
     private double[] imin;
     private double omax;
     private double omin;

     private int ndata;     // Number of examples
     private int nvariables; // Numer of variables
     private int ninputs;  // Number of inputs
     private int nclasses;    // Number of classes
  
     final static boolean debug = false;

     /**
     * Returns the whole input data.
     * @return the whole input data.
     */
    public double[][] getX() { return X; }

    /**
     * Returns the outputs of each example (regression).
     * @return the outputs of each example (regression).
     */
    public double [] getY() { return Y; }

    /**
     * Returns the outputs of each example (classification).
     * @return the outputs of each example (classification).
     */
    public int [] getC() { return C; }

    /**
     * Returns the maximum values of each input attribute.
     * @return the maximum values of each input attribute.
     */
    public double [] getimax() { return imax; }

    /**
     * Returns the minimum values of each input attribute.
     * @return the minimum values of each input attribute.
     */
    public double [] getimin() { return imin; }

    /**
     * Returns the maximum value of the output.
     * @return the maximum value of the output.
     */
    public double getomax() { return omax; }

    /**
     * Returns the minimun value of the output.
     * @return the minimun value of the output.
     */
    public double getomin() { return omin; }

    /**
     * Returns the number of examples.
     * @return the number of examples.
     */
    public int getndata() { return ndata; }

    /**
     * Returns the number of variables.
     * @return the number of variables.
     */
    public int getnvariables() { return nvariables; }

    /**
     * Returns the number of input attributes.
     * @return the number of input attributes.
     */
    public int getninputs() { return ninputs; }

    /**
     * Returns the number of classes.
     * @return the number of classes.
     */
    public int getnclasses() { return nclasses; }

    /**
     * Returns True if the value of j-th attribute of the i-th instance is missing and false, otherwise.
     * @param i instance's position.
     * @param j attribute's position.
     * @return True if the value of j-th attribute of the i-th instance is missing and false, otherwise.
     */
    public boolean isMissing(int i, int j) {
   // True is the value is missing (0 in the table)
     return missing[i][j];
     }
  //   public int gettype() { return type; }


 private InstanceSet IS;

/**
     * <p> 
     * Init a new set of instances
     * </p>
     * @param nfexamples Name of the dataset file
     * @param train The dataset file is for training or for test
     * @throws IOException if there is any semantical, lexical or sintactical error in the input file.
     */
 public ProcDataset(String nfexamples,boolean train) throws IOException{
   
   try {  
    IS = new InstanceSet();
    IS.readSet(nfexamples,train);
    System.out.println("Dataset analyzed: "+Attributes.getRelationName());
  } catch (Exception e) {
    System.out.println("Exception in readSet");
    e.printStackTrace();
  } 
 }

 /**
  * <p>
  * Returs the type of the dataset 0-Modelling, 1-Clasiffication, 2-Clustering
  * </p>
  * @return type of the dataset 0-Modelling, 1-Clasiffication, 2-Clustering
  */
 public int datasetType()  {
  
    if (Attributes.getOutputNumAttributes()>=1) {
          if (Attributes.hasNominalAttributes()) return (1);
        else return(0);
    } else return(2);
  
 }

 /**
  * <p>
  * Process a dataset for classification
  * </p>
  *
  * @throws java.io.IOException if the dataset is not appropriate for this algorithm
  */
 public void processClassifierDataset() throws IOException {
  try {

  ndata=IS.getNumInstances();
  ninputs=Attributes.getInputNumAttributes();
  nvariables=ninputs+Attributes.getOutputNumAttributes();
 // Check that there is only one output variable and
 // it is nominal
  if (Attributes.getOutputNumAttributes()>1) {
  System.out.println("This algorithm can not process MIMO datasets");
  System.out.println("All outputs but the first one will be removed");
  }
  boolean noOutputs=false;
  if (Attributes.getOutputNumAttributes()<1) {
        System.out.println("This algorithm cannot process datasets without outputs");
        System.out.println("Zero-valued output generated");
        noOutputs=true;
  }
 // Initialice and fill our own tables
  X = new double[ndata][ninputs];
  missing = new boolean[ndata][ninputs];
  C = new int[ndata];
 // Maximum and minimum of inputs
  imax=new double[ninputs];
  imin=new double[ninputs];
 // Maximum and minimum for output data
  omax=0;
  omin=0;
 // All values are casted into double/integer
  nclasses=0;
  for (int i=0;i<X.length;i++) {
    Instance inst = IS.getInstance(i);
    for (int j=0;j<ninputs;j++)  {
        X[i][j] = IS.getInputNumericValue(i,j);
        missing[i][j] = inst.getInputMissingValues(j);
        if (X[i][j]>imax[j] || i==0) imax[j]=X[i][j];
        if (X[i][j]<imin[j] || i==0) imin[j]=X[i][j];
    }
  if (noOutputs) C[i]=0; else C[i] = (int)IS.getOutputNumericValue(i,0);
  if (C[i]>nclasses) nclasses=C[i];
  }
  nclasses++;
  System.out.println("Number of classes="+nclasses);
  } catch (Exception e) {
  System.out.println("DBG: Exception in readSet");
  e.printStackTrace();
  }
 }


 /**
  * <p>
  * Process a dataset for modelling
  * </p>
  *       
  * @throws java.io.IOException if the dataset is not appropriate for this algorithm
  */
 public void processModelDataset()  throws IOException {
   try {

 // Load in memory a dataset that contains a classification problem
   //IS.readSet(nfejemplos,train);
   ndata=IS.getNumInstances();
   ninputs=Attributes.getInputNumAttributes();
   nvariables=ninputs+Attributes.getOutputNumAttributes();
   if (Attributes.getOutputNumAttributes()>1) {
     System.out.println("This algorithm can not process MIMO datasets");
     System.out.println("All outputs but the first one will be removed");
   }
  boolean noOutputs=false;
  if (Attributes.getOutputNumAttributes()<1) {
    System.out.println("This algorithm can not process datasets without outputs");
    System.out.println("Zero-valued output generated");
    noOutputs=true;
  }
 // Initialice and fill our own tables
  X = new double[ndata][ninputs];
  missing = new boolean[ndata][ninputs];
  Y = new double[ndata];
 // Maximum and minimum of inputs
  imax=new double[ninputs];
  imin=new double[ninputs];
 // Maximum and minimum for output data
  omax=0;
  omin=0;
 // All values are casted into double/integer
 nclasses=0;
  for (int i=0;i<X.length;i++) {
    Instance inst = IS.getInstance(i);
    for (int j=0;j<ninputs;j++)  {
        X[i][j] = IS.getInputNumericValue(i,j);
        missing[i][j] = inst.getInputMissingValues(j);
        if (X[i][j]>imax[j] || i==0) imax[j]=X[i][j];
        if (X[i][j]<imin[j] || i==0) imin[j]=X[i][j];
    }
    if (noOutputs) Y[i]=0; else Y[i] = IS.getOutputNumericValue(i,0);
    if (Y[i]>omax || i==0) omax=Y[i];
    if (Y[i]<omin || i==0) omin=Y[i];
  }
 } catch (Exception e) {
   System.out.println("DBG: Exception in readSet");
   e.printStackTrace();
 }
}

 /**
  * <p>
  * Process a Dataset for clustering
  * </p>
  * @param nfexamples 
  * @param train 
  * @throws java.io.IOException if the dataset is not appropriate for this algorithm
  */
 public void processClusterDataset(String nfexamples, boolean train) throws 
 IOException {

   try {

 // Load in memory a dataset that contains a classification problem
    //IS.readSet(nfexamples,train);

    ndata=IS.getNumInstances();
    ninputs=Attributes.getInputNumAttributes();
    nvariables=ninputs+Attributes.getOutputNumAttributes();

    if (Attributes.getOutputNumAttributes()!=0) {
     System.out.println("This algorithm can not process datasets with  outputs");
     System.out.println("All outputs will be removed");
   }

 // Initialice and fill our own tables
   X = new double[ndata][ninputs];
   missing = new boolean[ndata][ninputs];
 // Maximum and minimum of inputs
   imax=new double[ninputs];
   imin=new double[ninputs];
 // Maximum and minimum for output data
   omax=0;
   omin=0;

 // All values are casted into double/integer
   nclasses=0;
   for (int i=0;i<X.length;i++) {
    Instance inst = IS.getInstance(i);
    for (int j=0;j<ninputs;j++)  {
        X[i][j] = IS.getInputNumericValue(i,j);
        missing[i][j] = inst.getInputMissingValues(j);
        if (X[i][j]>imax[j] || i==0) imax[j]=X[i][j];
        if (X[i][j]<imin[j] || i==0) imin[j]=X[i][j];
    }
  }
 } catch (Exception e) {
    System.out.println("DBG: Exception in readSet");
    e.printStackTrace();
 }
 }



 /*************************************************************
 Generating Results*/

 /**
  * <p>
  * Generating the header of the output file
  * </p>
  * @ param p PrintStream
  * @return Nothing
  */
  private void CopyHeaderTest(PrintStream p) {

   // Header of the output file
   p.println("@relation "+Attributes.getRelationName());
   p.print(Attributes.getInputAttributesHeader());
   p.print(Attributes.getOutputAttributesHeader());
   p.println(Attributes.getInputHeader());
   p.println(Attributes.getOutputHeader());
   p.println("@data");


  }
/**
     * <p>
     * Generates output file for a modelling problem
     * </p>
     * @param Foutput Name of the output file
     * @param real Vector of outputs instances
     * @param obtained Vector of net outputs
     */
  public void generateResultsModeling(String Foutput,int []real, int[] obtained) {

         // Output file, modeling problems

         FileOutputStream out;
         PrintStream p;

         try
         {

             out = new FileOutputStream(Foutput);
             p = new PrintStream( out );
             CopyHeaderTest(p);
             for (int i=0;i<real.length;i++) {
                 p.print(real[i]+" "+obtained[i]+"\n");
             }
             p.close();
         }
         catch (Exception e)
         {
             System.err.println ("Error building file for results: "+Foutput);
         }
     }

 /**
     * <p>
     * Generates output file for a modelling problem
     * </p>
     * @param Foutput Name of the output file
     * @param real Vector of outputs instances
     * @param obtained Vector of net outputs
     *
     */
  public void generateResultsModeling(String Foutput,double []real, double[] obtained) {

         // Output file, modeling problems

         FileOutputStream out;
         PrintStream p;

         try
         {

             out = new FileOutputStream(Foutput);
             p = new PrintStream( out );
             CopyHeaderTest(p);
             for (int i=0;i<real.length;i++) {
                 p.print(real[i]+" "+obtained[i]+"\n");
             }
             p.close();
         }
         catch (Exception e)
         {
             System.err.println ("Error building file for results: "+Foutput);
         }
     }

    /**
     * <p>
     * Generates output file for a clasification problem
     * </p>
     * @param Foutput Name of the output file
     * @param real Vector of outputs instances
     * @param obtained Vector of net outputs
     *
     */
 public void generateResultsClasification(String Foutput,int []real, int[] obtained) {

         // Output file, classification problems
         FileOutputStream out;
         PrintStream p;
         Attribute at = Attributes.getOutputAttribute(0);

         // Check whether the output value is nominal or integer
         boolean isNominal=(at.getType()==at.NOMINAL);
         try
         {
             out = new FileOutputStream(Foutput);
             p = new PrintStream( out );
             CopyHeaderTest(p);
             //System.out.println("Longitudes "+real.length+" "+obtained.length);
             for (int i=0;i<real.length;i++) {
               // Write the label associated to the class number,
               // when the output is nominal
                if (isNominal) 
                    p.print(at.getNominalValue(real[i])+" "+ at.getNominalValue(obtained[i])+"\n");
                else p.print(real[i]+" "+obtained[i]+"\n");
             }
             p.close();
         }
         catch (Exception e)
         {
             System.err.println ("Error building file for results: "+Foutput);
         }
     }

 }
