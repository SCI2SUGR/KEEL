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
	MixtGauss.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  2-3-09
	Copyright (c) 2009 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.MixtGauss;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.Chen.ChenGenerator;
import keel.Algorithms.Instance_Generation.HYB.HYBGenerator;
import keel.Algorithms.Instance_Generation.*;
import java.util.*;

import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;

import java.util.StringTokenizer;



/**
 * 
 * @author Isaac Triguero
 * @version 1.0
 */
public class MixtGaussGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  

  private int numberOfGaussians; // Number of gaussians per class. (number of prototypes in the condensed set..)
 
  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;

  
  /**
   * Build a new PSOGenerator Algorithm
   */
  
  public MixtGaussGenerator(PrototypeSet _trainingDataSet, int blocks, String choice)
  {
      super(_trainingDataSet);
      algorithmName="MixtGauss";
      

  }
  

  /**
   * Build a new MixtGaussGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public MixtGaussGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="MixtGauss";

      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
      this.numberOfGaussians = parameters.getNextAsInt(); // Lo tomo del patrï¿½n..
      this.numberOfPrototypes = getSetSizeFromPercentage(this.numberOfGaussians); // caluclo el porcentaje..
      //Calcluo el nuevo numer de gausisanas..
      
     // System.out.println("NUmber of prototypes = "+ this.numberOfPrototypes);
      this.numberOfGaussians = this.numberOfPrototypes / this.numberOfClass;
      
      if(this.numberOfGaussians == 0) this.numberOfGaussians = 1;
     // System.out.println("NUmber of gausianas = "+ this.numberOfGaussians);
     

     
     
  }

  /**
   * Return the value of PDF normal.
   * @param x
   * @return the value of PDF normal
   */
  public double pdfNormal(double x){
	  double result;
	  
	  result = 1/ Math.sqrt(2*Math.PI);
	  result *= Math.exp(-0.5*(x*x));
	  
	  return result;
 }
  
  /**
   * F(x) = N(x^t_k)
   * @param x
   * @return F(x) = N(x^t_k)
   */
  public double f_x(double x, double mu, double sigma){
	  double result = 1./sigma;
	  
	  return result*pdfNormal( (x-mu)/sigma);
  }
  
  /**
   * Accuracy per class.
   * @return Accuracy per class
   */
  
  public double[] CalculateAccuracy(PrototypeSet actual){
	 
	  double current_accuracy[] = new double[this.numberOfClass];
	  
	  for (int i=0; i< this.numberOfClass; i++){
		  if(actual.getFromClass(i).size() >0){
			  current_accuracy[i] = accuracy(actual.getFromClass(i),trainingDataSet.getFromClass(i));
		  }else{
			  current_accuracy[i] = 0;
		  }
		  
	  }
	  return current_accuracy;
  }
  
  /**
   * Expectation-Maximisation Algorithm
   */
  public Pair<PrototypeSet, PrototypeSet> EMstep(PrototypeSet actual, PrototypeSet SD){
	  
	  //stablish the index... Por lo que pueda pasar..
	   for(int j=0;j<actual.size();j++){
	         //Now, I establish the index of each prototype.
			actual.get(j).setIndex(j);
		}
	  //Se aplica independientemente a cada clase
	  double pdfs[][] = new double[this.numberOfGaussians][];
	  double pdfsNum[][] = new double[this.numberOfGaussians][];
	  double sumPDFS[];
	  
	  double acc = accuracy(actual, trainingDataSet);
	  double acc2 = acc-1;
	  
	    // Initial AlfaMJ		  
		 double alfaMj[] = new double[this.numberOfGaussians];
		 for(int j=0; j< this.numberOfGaussians; j++){
			 alfaMj[j] = 1./this.numberOfGaussians;
		 }
		 
		 
       for(int i=0; i<this.numberOfClass; i++){
	     PrototypeSet perClass = trainingDataSet.getFromClass(i);
		 
	     if(perClass.size() > 0){
		     acc2 = acc-1; // Initially..
			 
			 while(acc > acc2){ // Iterative process..
			      acc2 = acc;	  
			
			      //E step
				  
			      sumPDFS = new double[perClass.size()];
			      // Inicializaciï¿½n..
				  for(int j=0; j< this.numberOfGaussians; j++){
					  pdfs[j] = new double[perClass.size()]; // Save the probability Pm(xt, Cj)
					  pdfsNum[j] = new double[perClass.size()];
				  }
				  
			      
			      for(int t=0; t< perClass.size(); t++){ //Recorro cada una de las instancias de training.
	
					 
					  sumPDFS[t] =0;	 
					  for(int j=0; j< this.numberOfGaussians; j++){
	
						  
					  	 double productorio = 1;
					
						  for(int k=0; k< perClass.get(0).numberOfInputs(); k++){
							  double value =f_x(perClass.get(t).getInput(k), actual.getFromClass(i).get(j).getInput(k), SD.getFromClass(i).get(j).getInput(k));
							 // System.out.println("f_x = " + value+ " ,"+ SD.get(i).getInput(k));
							  productorio *= value;
						  }
					  
						  pdfsNum[j][t] = (alfaMj[j]* productorio*1.); 
						  sumPDFS[t] += pdfsNum[j][t];
					 }// Numeradores..
					  
					  
					  for(int j=0; j< this.numberOfGaussians; j++){
						  	pdfs[j][t] = pdfsNum[j][t]/sumPDFS[t];	
						  	
				//		  	System.out.println("PDF j = " +j+ " t= "+t+ " => " +pdfs[j][t]);
					  } // pdfs completos.
					  
					  
				  } 
				  
	
						
				  //M step
				  //Calculamos los nuevos alfaMj, y mu|Mkj
				  
				  double sum =0;
				  
				  for(int j=0; j< this.numberOfGaussians; j++){
					  sum =0;
					  alfaMj[j] = 1./perClass.size();
					  for(int t=0; t< perClass.size(); t++){ 
					  		sum += pdfs[j][t];  
					  }
					  alfaMj[j] *= sum;
				  }
				  
			  
				  //---------Calculo las medias..
				  PrototypeSet nuevo = new PrototypeSet(actual);
				  double  denominator =0;
				  double numerator[][] = new double[perClass.get(0).numberOfInputs()][this.numberOfGaussians];
				 
				  for(int k =0; k< perClass.get(0).numberOfInputs(); k++)
					  for(int j=0; j< this.numberOfGaussians; j++)
						  numerator[k][j] =0;
				  
				  for(int j=0; j< this.numberOfGaussians; j++){
					  denominator =0;
					  for(int t=0; t< perClass.size(); t++){	  
						  for(int k =0; k< perClass.get(0).numberOfInputs(); k++){
							  numerator[k][j]+= pdfs[j][t]*perClass.get(t).getInput(k);  
						  }
						  denominator += pdfs[j][t];
					  }
					  
					 // System.out.println("Denominator = "+ denominator);
					  Prototype element = new Prototype(perClass.get(0)); 
					  //Construyendo la media...
					  for(int k =0; k< perClass.get(0).numberOfInputs(); k++){
						  double media = numerator[k][j]/(denominator*1.);
						//  System.out.print(" , "+ media);
						  //System.out.print(", "+ numerator[k][j]);
							
						  element.setInput(k, media);				
						  
					  }
					  
					  //System.out.println(" ");
					  int index = actual.getFromClass(i).get(j).getIndex();
					  nuevo.set(index, element);
				  }
				  
	
				  // Calculo de las sigmas.		
				  
				  PrototypeSet newSD = new PrototypeSet(SD);
				  for(int k =0; k< perClass.get(0).numberOfInputs(); k++)
					  for(int j=0; j< this.numberOfGaussians; j++)
						  numerator[k][j] =0;
				  
				  for(int j=0; j< this.numberOfGaussians; j++){
					  denominator =0;
					  for(int t=0; t< perClass.size(); t++){	  
						  for(int k =0; k< perClass.get(0).numberOfInputs(); k++){
							  int index = actual.getFromClass(i).get(j).getIndex();
							  numerator[k][j]+= pdfs[j][t]* Math.pow(perClass.get(t).getInput(k)- nuevo.get(index).getInput(k),2);  
						  }
						  denominator += pdfs[j][t];
					  }
					  
					 // System.out.println("Denominator = "+ denominator);
					  Prototype element = new Prototype(perClass.get(0)); 
					  //Construyendo la media...
					  for(int k =0; k< perClass.get(0).numberOfInputs(); k++){
						  double media = numerator[k][j]/(denominator*1.);
						//  System.out.print(" , "+ media);
						  //System.out.print(", "+ numerator[k][j]);
							
						  element.setInput(k, media);				
						  
					  }
					  
					  //System.out.println(" ");
					  int index = actual.getFromClass(i).get(j).getIndex();
					  SD.set(index, element);
				  }
				  
				  
				  // ï¿½Seguir?
				  acc = accuracy(nuevo , trainingDataSet);
				  if(acc > acc2){
					  //System.out.println("Mejora");
					  actual = new PrototypeSet(nuevo);
					   for(int j=0;j<actual.size();j++){
					         //Now, I establish the index of each prototype.
							actual.get(j).setIndex(j);
						}
					   
					   SD = new PrototypeSet(newSD);
					   
				  }
				  //nuevo.print();  
	
			   
			      
			 } //End While
		 
	     }//end if
       }//End for
	  
       
       Pair <PrototypeSet, PrototypeSet> salida = new Pair<PrototypeSet,PrototypeSet> (actual,SD);
	  
	return salida;  
  }
  
  
  /**
   * Generate a reduced prototype set by the MixtGaussGenerator method.
   * @return Reduced set by MixtGaussGenerator's method.
   */
  
  
  
  @SuppressWarnings({ "unchecked", "static-access" })
public PrototypeSet reduceSet()
{
    System.out.print("\nThe algorithm MixtGauss is starting...\n Computing...\n");
    PrototypeSet result = new PrototypeSet(); // In the condensed set, each class is represented by the same number of prototypes.
   
    // Initialisation Process.
   
    Prototype mean = new Prototype();
    PrototypeSet SD = new PrototypeSet();
    
    for(int i=0; i< this.numberOfClass; i++){
    	PrototypeSet classi = trainingDataSet.getFromClass(i);
       
    	if(classi.size() >0){
    		 
	    	mean = classi.avg();
	    	for(int j=0; j< this.numberOfGaussians ; j++){
	        	Prototype Perturbance = new Prototype(mean);
	            Prototype sdP =  new Prototype(mean);
	            
	        	for(int k=0; k< Perturbance.numberOfInputs(); k++){
	        		 Perturbance.setInput(k, mean.getInput(k)+RandomGenerator.Randdouble(-0.01, 0.01));
	        	     sdP.setInput(k,0.1); // Initially Sigma to 0,1		
	         		//double Rg = mean[i].getInput(k)*RandomGenerator.RandGaussian() + 0.1;
	         		//Perturbance.setInput(k, Rg/500. + RandomGenerator.Randdouble(0, 1) ); // Normal distribution
	        	}
	               	
	        	result.add(Perturbance);
	        	SD.add(sdP); // Gaussian. Mean and sigma..
	        }
    	}
    }
    

    result.applyThresholds();
   
   // result.print();
    
    // Iterative Optimisation
    double current_accuracy[] = new double[this.numberOfClass];
    current_accuracy = CalculateAccuracy(result);
    double classes_improve = -1;
    
    PrototypeSet PreviousGaussians  = new PrototypeSet();
    double previous_accuracy[] = new double[this.numberOfClass];
    
    while(classes_improve !=0){
    	PreviousGaussians = new PrototypeSet(result);
    	Pair <PrototypeSet,PrototypeSet > salidaR =  EMstep(PreviousGaussians, SD);
    	result = salidaR.first();
    	SD = salidaR.second();

    	previous_accuracy = current_accuracy;
    	
    	current_accuracy = CalculateAccuracy(result);
    	
    	classes_improve = 0;
    	
    	for( int c=0; c< this.numberOfClass; c++){
    		if( current_accuracy[c] > previous_accuracy[c]){
    			classes_improve += 1;
    		}else if(current_accuracy[c] < previous_accuracy[c]){
    			
    			for(int g=0; g< this.numberOfGaussians; g++){
    				int index = c*this.numberOfGaussians + g;
    				result.set(index, PreviousGaussians.get(index));
    			}
    		}
    	}
    }
    
    //Print Result.
	PrototypeSet nominalPopulation = new PrototypeSet();
    nominalPopulation.formatear(result);
	System.err.println("\n% de acierto en training Nominal " + KNN.classficationAccuracy(nominalPopulation,trainingDataSet,1)*100./trainingDataSet.size() );
	
		 
	//result.print();
	return result;
	 

}
  
  /**
   * General main for all the prototoype generators
   * Arguments:
   * 0: Filename with the training data set to be condensed.
   * 1: Filename which contains the test data set.
   * 3: Seed of the random number generator.            Always.
   * **************************
   * 4: .Number of blocks

   * @param args Arguments of the main function.
   */
  public static void main(String[] args)
  {
      Parameters.setUse("MixtGauss", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      MixtGaussGenerator.setSeed(seed);
      
      int blocks =Parameters.assertExtendedArgAsInt(args,10,"number of blocks", 1, Integer.MAX_VALUE);
      
      //String[] parametersOfInitialReduction = Arrays.copyOfRange(args, 4, args.length);
     //System.out.print(" swarm ="+swarm+"\n");
      
      
      MixtGaussGenerator generator = new MixtGaussGenerator(training,blocks , "diameter");
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
