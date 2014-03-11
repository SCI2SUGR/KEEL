
/*
	APSSC.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-1-2011
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.APSSC;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerator;
import keel.Algorithms.Semi_Supervised_Learning.Basic.Prototype;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;

import keel.Algorithms.Semi_Supervised_Learning.*;
import java.util.*;

import keel.Algorithms.Semi_Supervised_Learning.utilities.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.KNN.*;
import keel.Dataset.Attributes;

import org.core.*;

import org.core.*;

import java.util.StringTokenizer;



/**
 * This class implements the Self-traning wrapper. You can use: Knn, C4.5, SMO and Ripper as classifiers.
 * @author triguero
 *
 */

public class APSSCGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
 private int numberOfselectedExamples;

 private double spreadGaussian;
 private double evaporation;
 private double MT; //confidence
 
  

  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  /**
   * Build a new APSSCGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param perc Reduction percentage of the prototype set.
   */
  
  public APSSCGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="APSSC";
      
  }
  


  /**
   * Build a new APSSCGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
   * @param params Parameters of the algorithm (only % of reduced set).
   */
  public APSSCGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="APSSC";
   
    
      this.spreadGaussian = parameters.getNextAsDouble();
      this.evaporation = parameters.getNextAsDouble();
      this.MT = parameters.getNextAsDouble();

      
      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
    //  System.out.print("\nIsaacSSL dice:  " + this.numberOfselectedExamples+ ", "+ this.numberOfClass +"\n");

  }
  
  /**
   * Apply the Generator method.
   * @return 
   */
  
  
  public Pair<PrototypeSet, PrototypeSet> applyAlgorithm()
  {
	  System.out.print("\nThe algorithm APSSC is starting...\n Computing...\n");
	  
	  PrototypeSet ANTlabeled, ANTunlabeled;
	  
	  ANTlabeled = new PrototypeSet(trainingDataSet.getAllDifferentFromClass(this.numberOfClass)); // Selecting labeled prototypes from the training set.
	  ANTunlabeled = new PrototypeSet(trainingDataSet.getFromClass(this.numberOfClass));
	  
	  
	  // Accuracy with initial labeled data.
	  
	//  System.out.println("Labeled size = " +ANTlabeled.size());
	 // System.out.println("Unlabeled size = " + ANTunlabeled.size());
	  
	  // pheromone that the labeled ant i emitted to the j unlabeled ant.
	  double pheromone[][] = new double [ANTunlabeled.size()][this.numberOfClass];
	  
	  for(int i=0; i< ANTlabeled.size(); i++){
		  Arrays.fill(pheromone[i], 0);
	  }
	  
	  double aggregation[] = new double[ANTunlabeled.size()];
	  
	  double membership[][]= new double[ANTunlabeled.size()][this.numberOfClass];
	  
	  boolean stoppingCriteria = false;
	  
	  Prototype ColonyCenters[] = new Prototype[this.numberOfClass];
	  Prototype newColonyCenters[] = new Prototype[this.numberOfClass];
	
	  
	  for(int i=0 ; i< this.numberOfClass; i++){
		  ColonyCenters[i] = ANTlabeled.getFromClass(i).avg();
		
	  }
	  
	  
	  
	  
	  while(!stoppingCriteria){
		  
		  	for(int p=0; p<ANTunlabeled.size(); p++){
		  		
		  		
		  		//First Step: calculate aggregation and update pheromone.
		  		
		  		for(int q=0; q<this.numberOfClass; q++){
		  			
		  					  			
		  			aggregation[p] =0;
		  			PrototypeSet Cj = new PrototypeSet(ANTlabeled.getFromClass(q));
		  			
		  			if(Cj!=null){  // At least one prototpye of this class in the training Set!
		  				
			  			for(int z=0; z< Cj.size(); z++){
			  				// Average Aggregation
			  				aggregation[p] += Math.exp(-1*((Distance.distance(Cj.get(z), ANTunlabeled.get(p))))/(2*this.spreadGaussian*this.spreadGaussian));
			  			}
			  			
			  			aggregation[p]/= Cj.size();
		  		    
		  			}
		  			// update pheromone
		  			
		  			pheromone[p][q] = this.evaporation*pheromone[p][q] + aggregation[p];
		  		}
		  		
		  		// Second Step: compute membership
		  		
	  			double sumatoria = 0;
	  			
	  			for(int z=0; z< this.numberOfClass; z++){
	  				sumatoria+= pheromone[p][z];
	  			}
	  			
	  			double max_j = Double.MIN_VALUE;
	  			double clasej =0;
	  			
		  		for(int q=0; q<this.numberOfClass; q++){
		  			  			
		  			membership[p][q]= pheromone[p][q]/sumatoria; 
		  		
		  			if(membership[p][q]> max_j){
		  				max_j = membership[p][q];
		  				clasej= q;
		  			}
		  		}
		  		
		  		// Step 3: Determine if we are going to add the new ant
		  		
		  		//System.out.println("Max_j = " + max_j);
		  		if(max_j > this.MT){
		  			// We do not add the unlabeled data, but we assign the appropriate labeled data.
		  			//System.out.println("Añado");
		  			
		  			ANTunlabeled.get(p).setFirstOutput(clasej);
		  			
		  		}
		
		  				  		
		  		
		  		
		  	}
		  	
		  	
		  	//Checking stopping criteria
		  	
		  	  boolean checking = true;
		  	
		  	  PrototypeSet Union = new PrototypeSet(ANTlabeled.clone());
		  	  Union.add(ANTunlabeled.getAllDifferentFromClass(this.numberOfClass)); // we add all the unlabeled ants that has a class label.
		  	  
		  	  
			  for(int i=0 ; i< this.numberOfClass; i++){
				  
				  if(ColonyCenters[i]!=null){
				  
					  newColonyCenters[i] = Union.getFromClass(i).avg();
					  
					  if(!newColonyCenters[i].equals(ColonyCenters[i])){
						  checking = false; // we do not stop
					  }
				  }
			  }
			  
			  if(!checking){ // si voy a seguir
				  for(int i=0 ; i< this.numberOfClass; i++){
					  ColonyCenters[i] = new Prototype(newColonyCenters[i]);
			  	 }
			  }
			  stoppingCriteria = checking;
		  
	  }

	  
	   ANTlabeled.add(ANTunlabeled.getAllDifferentFromClass(this.numberOfClass)); // we add the labeled ants
	  
	   
		 // System.out.println("Labeled size = " +ANTlabeled.size());
		 // System.out.println("Unlabeled size = " + ANTunlabeled.size());
		  
	  
	  // begin testing
	   
		  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
		  PrototypeSet test = new PrototypeSet(this.testDataSet.clone());
		  
		  double pheromoneTrs[][] = new double [this.transductiveDataSet.size()][this.numberOfClass]; 
		  double pheromoneTst[][] = new double [this.testDataSet.size()][this.numberOfClass]; 
		  
		  int aciertoTrs = 0;
		  int aciertoTst = 0;
		  
		  
		  //We have to return the classification done.
		  
		  for(int i=0; i<this.transductiveDataSet.size(); i++){
			  
			  
			  for(int q=0; q<this.numberOfClass; q++){
				  
				 pheromoneTrs[i][q] = 0;
				 
				  for(int z=0; z<ANTlabeled.getFromClass(q).size() ; z++){
					  pheromoneTrs[i][q] += Math.exp(-1*((Distance.distance(ANTlabeled.getFromClass(q).get(z), transductiveDataSet.get(i))))/(2*this.spreadGaussian*this.spreadGaussian));
				  }
				  
				  pheromoneTrs[i][q]/= ANTlabeled.getFromClass(q).size();
				  
			  }
			  
			  double maxpheromone = Double.MIN_VALUE;
			  double ColonyLabel = 0;
			  
			  for(int q=0; q<this.numberOfClass; q++){
				  
				  if(pheromoneTrs[i][q] > maxpheromone){
					  maxpheromone = pheromoneTrs[i][q];
					  ColonyLabel = q;
				  }
			  }
			  
			   tranductive.get(i).setFirstOutput(ColonyLabel);
			   
			   if(ColonyLabel == transductiveDataSet.get(i).getOutput(0)){
				   aciertoTrs++;
			   }
			   
		  }
		  
		  System.out.println("% de acierto TRS = "+ (aciertoTrs*100.)/transductiveDataSet.size());
		  
		  for(int i=0; i<this.testDataSet.size(); i++){
			  
			  
			  for(int q=0; q<this.numberOfClass; q++){
				  
				 pheromoneTst[i][q] = 0;
				 
				  for(int z=0; z<ANTlabeled.getFromClass(q).size() ; z++){
					  pheromoneTst[i][q] += Math.exp(-1*((Distance.distance(ANTlabeled.getFromClass(q).get(z), testDataSet.get(i))))/(2*this.spreadGaussian*this.spreadGaussian));
				  }
				  
				  pheromoneTst[i][q]/= ANTlabeled.getFromClass(q).size();
				  
			  }
			  
			  double maxpheromone = Double.MIN_VALUE;
			  double ColonyLabel = 0;
			  
			  for(int q=0; q<this.numberOfClass; q++){
				  
				  if(pheromoneTst[i][q] > maxpheromone){
					  maxpheromone = pheromoneTst[i][q];
					  ColonyLabel = q;
				  }
			  }
			  
			   test.get(i).setFirstOutput(ColonyLabel);
			   
			   if(ColonyLabel == testDataSet.get(i).getOutput(0)){
				   aciertoTst++;
			   }
			   
		  }
		  
		  System.out.println("% de acierto TST = "+ (aciertoTst*100.)/testDataSet.size());

	  
      return new Pair<PrototypeSet,PrototypeSet>(tranductive,test);
  }
  
  /**
   * General main for all the prototoype generators
   * Arguments:
   * 0: Filename with the training data set to be condensed.
   * 1: Filename which contains the test data set.
   * 3: Seed of the random number generator.            Always.
   * **************************
   * @param args Arguments of the main function.
   */
  public static void main(String[] args)
  {  }

}
