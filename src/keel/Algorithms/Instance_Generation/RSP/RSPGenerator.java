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
	RSP.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  2-3-09
	Copyright (c) 2009 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.RSP;

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
 * @param  numberOfBlocks
 * @author Isaac Triguero
 * @version 1.0
 */
public class RSPGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  
  // We need the variable K to use with k-NN rule
  private int numberOfBlocks;
 
  protected int numberOfPrototypes;  // Particle size is the percentage
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;
  
  private String Subset_choice = "diameter";
  
  /**
   * Build a new PSOGenerator Algorithm
   * 
   */
  
  public RSPGenerator(PrototypeSet _trainingDataSet, int blocks, String choice)
  {
      super(_trainingDataSet);
      algorithmName="RSP";
      
      this.numberOfBlocks = blocks;
      this.Subset_choice = choice;

  }
  

  /**
   * Build a new RSPGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public RSPGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="RSP";
      this.numberOfBlocks =  parameters.getNextAsInt();
      this.Subset_choice=  parameters.getNextAsString();
     
      System.out.println("Isaac dice: numberOFblock= " + this.numberOfBlocks + " choice = " + this.Subset_choice);

  }
  
  /**
   * Generate a reduced prototype set by the RSPGenerator method.
   * @return Reduced set by RSPGenerator's method.
   */
  
  /**
   * 
   * Edited nearest neighbor of T.
   * @return
   */
  protected PrototypeSet ENN (PrototypeSet T)
  {
	//T.print();
	 PrototypeSet Sew = new PrototypeSet (T);
	
	 //this.k = 7;
	  // Elimination rule kohonen
	  int majority = 3/2 + 1;
	 // System.out.println("Mayorï¿½a " + majority);


	  int toClean[] = new int [T.size()];
	  Arrays.fill(toClean, 0);
	  int pos = 0;
	  
	for ( Prototype p : T){
		 double class_p = p.getOutput(0);
		PrototypeSet neighbors = KNN.knn(p, trainingDataSet, 3);
		
		  int counter= 0;
		  for(Prototype q1 :neighbors ){
			double class_q1 = q1.getOutput(0);
			
			if(class_q1 == class_p){
				counter++;
			} 
			
		  }
		  
		  //System.out.println("Misma clase = "+ counter);
		  if ( counter < majority){ // We must eliminate this prototype.
			  toClean [pos] = 1; // we will clean			  
		  }
		   pos++;
	}
	
	//Clean the prototypes.
	PrototypeSet aux= new PrototypeSet();
	for(int i= 0; i< toClean.length;i++){
		if(toClean[i] == 0)
			aux.add(T.get(i));
		
	}
	//Remove aux prototype set
	
	Sew = aux;
	
	//System.out.println("Result of filtering");	
	//Sew.print();

	return Sew;
	  
  }
  
  
  @SuppressWarnings({ "unchecked", "static-access" })
public PrototypeSet reduceSet()
  {
	  System.out.print("\nThe algorithm is starting...\n Computing...\n");
	
	  
	  System.out.print("\nEditing algorithm is needed...\n Computing...\n");
	  trainingDataSet = new PrototypeSet(ENN(trainingDataSet));
	  /*
	   * RSP3=
	  En esta aproximaciï¿½n, el parï¿½metro inicial 'b' no es necesario. Siempre y cuando dentro
	  de un bloque tengamos mezcla entre dos o mï¿½s clases, ese bloque se va a dividir. Por
	  tanto, no se conoce apriori el nï¿½mero de bloques final.

	  Por ejemplo, suponte que tienes un bloque con 5 ejemplos de la clase A y 4 de la B.
	  Divides, calculas los dos mï¿½s alejados y consigues un subbloque con 3 ejemplos de la
	  clase A y los 4 de la B, mientras que el otro tiene el resto (2 de la clase A). El
	  primer bloque se tiene que dividir de nuevo (cuando le toque, dependerï¿½ del solapamiento
	  y/o diï¿½metro comparado con los demï¿½s). El segundo ya es homogï¿½neo.
	  */
	  
	  if(this.numberOfBlocks == 0){
		   System.out.println("Executing RSP3 with " + this.Subset_choice);
	  }else if(this.Subset_choice == "diameter"){
		  System.out.println("Executing RSP2");
	  }else{
		  System.out.println("Executing RSP1");
	  }
	  // Variables.
	  
	  int bc ; // Current number of subset in T.
	  int i ;
	  
	  ArrayList<PrototypeSet> C = new ArrayList<PrototypeSet>(this.numberOfBlocks);// To save the subsets.
	

	  
	  PrototypeSet B = new PrototypeSet( trainingDataSet.clone());//note: hard-copy --> B = T
      for( i=0; i<B.size(); ++i)
          B.get(i).setIndex(i);
      
      //for ( bc=1; bc < this.numberOfBlocks; bc++){
    //	  C.add(new PrototypeSet());    	  
     // }
      
      i=0;
      
      Prototype p1,p2;
	  p1 = (B.farthestPrototypes()).first();
	  p2 = (B.farthestPrototypes()).second();
  
	  boolean everyHomogenity = false;
	  boolean rsp3 = false;
	  
	  if(this.numberOfBlocks == 0){ //RSP3
		  rsp3 = true; // Always
	  }
	  
	  // if rsp3 is true, while there is no homogenity there is no end.
	  
	  for ( bc=1; (bc <this.numberOfBlocks || (rsp3 && !everyHomogenity)); bc++){ //


    	  
    	  //System.out.println("PRototypos mas lejanos son " + p1.getIndex() + " y " + p2.getIndex());
    	  
    	  Pair<PrototypeSet,PrototypeSet> Di = B.partIntoSubsetsWhichSeedPointsAre(p1.formatear(),p2.formatear());
    	  
    	  C.remove(B);
          PrototypeSet D1 = Di.first();
          PrototypeSet D2 = Di.second(); 
    	  C.add(D1);
    	  C.add(D2);
  
 		 //Before new iteration, if rsp3, we must check the homogenity.
          if(rsp3){
        	  everyHomogenity = true;
			  for(PrototypeSet pSet : C)
	          {
	              if(!pSet.homogeneity())
	                  everyHomogenity = false;
	              }
          }
          
    	  
    	  // I sets
          ArrayList<PrototypeSet> I = null;
          ArrayList<PrototypeSet> I1 = new ArrayList<PrototypeSet>();
          ArrayList<PrototypeSet> I2 = new ArrayList<PrototypeSet>();
          //System.out.println("C.size ="+ C.size());
          for(PrototypeSet pSet : C)
          {
              if(pSet.containsSeveralClasses())
                  I1.add(pSet);
              else
                  I2.add(pSet);
          }
          
    	  
    	  if(I1.size() != 0)
    		  I=I1;
    	  else
    		  I =I2;
    	  
    	  double distMax = -1.0;
          PrototypeSet Cj =(PrototypeSet) I.get(0);
          Pair<Prototype,Prototype> diameterPoints = null;
          
          
         /* Esto solo modifica el punto 9, la selecciï¿½n del bloque mï¿½s apropiado entre los que
          tienes disponibles.
          Elegir entre ovelapping o distancia mï¿½s lejanos.
          
           */
          for(PrototypeSet q : I)
          {
              if(q.size()>1)//limit-chase. Prototype set with only 1 element
              {
                  Pair<Prototype,Prototype> farthest = q.farthestPrototypes();
                  
                  double curDist;
                  
                  if (this.Subset_choice == "diameter"){
                	  curDist= Distance.d(farthest.first().formatear(), farthest.second().formatear());
                  }else{ // If you use overlapping.
                	  curDist = q.Overlapping();
                	  //System.out.println ("Over = "+ curDist);
                  }
                  if(distMax < curDist)
                  {
                      distMax = curDist;
                      Cj = q;
                      diameterPoints = farthest;
                  }
              }
          }
          
          B = Cj;
          
          if(diameterPoints != null){
        	  p1 = diameterPoints.first();
        	  p2 = diameterPoints.second();
          }else{
        	  // limit -chase, finish.
        	  everyHomogenity = true;
          }
          
          
      }
      
      
      //Find the centroids
      
      int numberOfClass = trainingDataSet.get(0).possibleValuesOfOutput().size();
      
      PrototypeSet result = new PrototypeSet();
      for(i=0; i<bc; ++i)
      {
    	  for( int j= 0; j< numberOfClass; j++){
    		  PrototypeSet aux = C.get(i).getFromClass(j);
    		  //Calculate centroid.
    		  if(aux.size()>0){ // Checking there is this class in the subset.
	    		  Prototype averaged = aux.avg();
	    		  result.add(averaged.formatear()); // FORMATEANDO AQUï¿½
    		  }
                    
    	  }
      }
      
      PrototypeSet nominalPopulation;
	  nominalPopulation = new PrototypeSet();
      nominalPopulation.formatear(result);
      numberOfPrototypes = result.size();
   	  System.err.println("\n% de acierto en training Nominal " + RSPGenerator.accuracy(nominalPopulation, trainingDataSet) );
		  
		//  nominalPopulation.print();

   	 System.out.println("Reduction %, result set = "+((trainingDataSet.size()-numberOfPrototypes)*100)/trainingDataSet.size()+ "\n");
	return nominalPopulation;
	 
	/*
      System.out.println("% de acierto en training " + RSPGenerator.accuracy(result, trainingDataSet) );
      
      numberOfPrototypes = result.size();


      return result;
*/
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
      Parameters.setUse("RSP", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      RSPGenerator.setSeed(seed);
      
      int blocks =Parameters.assertExtendedArgAsInt(args,10,"number of blocks", 1, Integer.MAX_VALUE);
      
      //String[] parametersOfInitialReduction = Arrays.copyOfRange(args, 4, args.length);
     //System.out.print(" swarm ="+swarm+"\n");
      
      
      RSPGenerator generator = new RSPGenerator(training,blocks , "diameter");
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
