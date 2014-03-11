
/*
	Democratic.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-1-2011
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.Democratic;

import keel.Algorithms.Semi_Supervised_Learning.Basic.C45.*;
import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerNB;
import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerRipper;
import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerSMO;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerator;
import keel.Algorithms.Semi_Supervised_Learning.Basic.Prototype;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.Basic.Utilidades;

import keel.Algorithms.Semi_Supervised_Learning.*;
import java.util.*;

import keel.Algorithms.Semi_Supervised_Learning.utilities.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.KNN.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.InstanceAttributes;
import keel.Dataset.InstanceSet;

import org.core.*;

import java.util.StringTokenizer;



/**
 * This class implements the Co-traning wrapper. You can use: Knn, C4.5, SMO and Ripper as classifiers.
 * @author triguero
 *
 */

public class DemocraticGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
  protected int numberOfClassifiers = 3;
  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  int pre[][] = new int[this.numberOfClassifiers][];
  double [][][] probabilities = new double[this.numberOfClassifiers][][];
  double average[] = new double[this.numberOfClassifiers];
  double deviation[] = new double[this.numberOfClassifiers];
  double li[] = new double[this.numberOfClassifiers];
  double hi[] = new double[this.numberOfClassifiers];
  double wi[] = new double[this.numberOfClassifiers];
  
  
  /**
   * Build a new DemocraticGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param perc Reduction percentage of the prototype set.
   */
  
  public DemocraticGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="Democratic";
      
  }
  


  /**
   * Build a new DemocraticGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
   * @param params Parameters of the algorithm (only % of reduced set).
   */
  public DemocraticGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="Democratic";
   
    
      this.numberOfClassifiers = parameters.getNextAsInt();
      
      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
      System.out.print("\nIsaacSSL dice:  " +  this.numberOfClass +"\n");

  }
  
  
  /**
   * This methods implement the voting rule in order to classify unlabeled data with the prediction pre[][]
   * @param unlabeled
   * @param pre
   * @return
   */
  
  double [] votingRule(PrototypeSet unlabeled, int pre[][]){
	  double predicho[] = new double[unlabeled.size()];
	  
	  for(int i=0; i< unlabeled.size(); i++){ // voting rule
		  
		  
		  
		  double perClass[] =  new double [this.numberOfClass];
		  Arrays.fill(perClass, 0);
		  
		  for(int j=0; j< this.numberOfClassifiers; j++){
			  if(pre[j][i]!=-1)
			  perClass[(int) pre[j][i]]++;
		  }
		  
		  int Maximo = Integer.MIN_VALUE;
		  
		  for (int j=0 ; j< this.numberOfClass; j++){
			  if(perClass[j]>Maximo){
				  Maximo =(int) perClass[j];
				  predicho[i] = j;
			  }
		  }
	  } // End voting Rule
	  
	  
	  return predicho;
  }

  
  public void getSolicitaGarbageColector(){

	  try{
	//  System.out.println( "********** INICIO: 'LIMPIEZA GARBAGE COLECTOR' **********" );
	  Runtime basurero = Runtime.getRuntime();
	//  System.out.println( "MEMORIA TOTAL 'JVM': " + basurero.totalMemory() );
	 // System.out.println( "MEMORIA [FREE] 'JVM' [ANTES]: " + basurero.freeMemory() );
	  basurero.gc(); //Solicitando ...
	 // System.out.println( "MEMORIA [FREE] 'JVM' [DESPUES]: " + basurero.freeMemory() );
	  //System.out.println( "********** FIN: 'LIMPIEZA GARBAGE COLECTOR' **********" );
	  }
	  catch( Exception e ){
	  e.printStackTrace();
	  }
	  
  
  }
  
  
  /**
   * Classify and calculing intervals of confidence
   * @param train
   * @param test
   * @throws Exception
   */
  
  public double[] clasificar(PrototypeSet train[], PrototypeSet test) throws Exception{
	  
	  double predicho[] = new double[test.size()];
	  
	  for (int i=0; i<this.numberOfClassifiers; i++){
		  
		  getSolicitaGarbageColector();
		  
		  if(i%3==0){ // 3NN
			//  System.out.println("Executing KNN");
			  pre[i] = KNN.classify(train[i], test, 3, probabilities[i]);

		  }else if(i%3==1){ // NB
			  //System.out.println("Executing NB");
			  HandlerNB nb = new HandlerNB(train[i].prototypeSetTodouble(), train[i].prototypeSetClasses(), test.prototypeSetTodouble(), test.prototypeSetClasses(),this.numberOfClass);
			  
		      pre[i] = nb.getPredictions();    
	  
		      probabilities[i] = nb.getProbabilities();
		      
		      nb = null;
		      
		  }else if(i%3==2){ //C45
			  //System.out.println("Executing C45");
			  
			  InstanceSet uno = train[i].toInstanceSet();
			  InstanceSet dos =  test.toInstanceSet();
			  
			  C45 c45 = new C45(train[i].toInstanceSet(), test.toInstanceSet());      // C4.5 called
			  
		      pre[i] = c45.getPredictions();    
			  
		      probabilities[i] = c45.getProbabilities();
		      
		      uno = null;
		      dos = null;
		      c45  = null;
 

		  }
	  }
	  
	  for (int i=0; i< this.numberOfClassifiers; i++){

			  average[i]=0;
			  deviation[i]=0;
			  
			  
			  for(int z=0; z<test.size(); z++){
				  int clase = pre[i][z];
				  if(clase!=-1)  average[i] += probabilities[i][z][clase];
			  }
			  
			  average[i] /= test.size();
			  
			//  System.out.println("Average = "+i+" es "+ average[i]);
			  
			  for(int z=0; z<test.size(); z++){
				  int clase = pre[i][z];
				  if(clase!=-1) deviation[i] += probabilities[i][z][clase]-average[i];
			  }
			  
			  deviation[i] /= test.size();
			  
			  deviation[i] = Math.sqrt(Math.abs(deviation[i]));
			  
		  
			  //System.out.println("Deviation = "+i+" es "+ deviation[i]);
			  
	  }
	  
	  
	  for (int i=0; i< this.numberOfClassifiers; i++){
		  
		  // computing the 95% -conf int. [li,hi] for each classifier

		  // Calculating the average and standard deviation.

			  li[i] = average[i] -1.96*(deviation[i]/Math.sqrt(test.size()));
			  hi[i] = average[i] +1.96*(deviation[i]/Math.sqrt(test.size()));
			  wi[i] = (li[i]+hi[i])/2.;
	

	  }	  
	  
	  
	  predicho = votingRule(test, pre); // in predicho we have the possible label, but we have to contrast this information with the confidence level.
		
	  
	  getSolicitaGarbageColector();
	  
	  return predicho;
	  
  }
  /**
   * Apply the DemocraticGenerator method with 3 classifiers:  C45, NB, and 3NN
   * 
   * @return 
   */
  
  
  public Pair<PrototypeSet, PrototypeSet> applyAlgorithm() throws Exception
  {
	  System.out.print("\nThe algorithm Democratic-CoLearning is starting...\n Computing...\n");
	  
	  PrototypeSet labeled;
	  PrototypeSet unlabeled;
	  
	  labeled = new PrototypeSet(trainingDataSet.getAllDifferentFromClass(this.numberOfClass)); // Selecting labeled prototypes from the training set.
	  unlabeled = new PrototypeSet(trainingDataSet.getFromClass(this.numberOfClass));
	  
	  // establishing the indexes

      for (int j=0; j< labeled.size();j++){
          labeled.get(j).setIndex(j); 
      }
      
      for (int j=0; j< unlabeled.size();j++){
    	  unlabeled.get(j).setIndex(j); 
      }
      
	  // In order to avoid problems with C45 and NB.
	  for(int p=0; p<unlabeled.size(); p++){
		  unlabeled.get(p).setFirstOutput(0); // todos con un valor válido.
	  }
	  
      
	  PrototypeSet Li[] = new PrototypeSet[this.numberOfClassifiers];
	  PrototypeSet LiP[] = new PrototypeSet[this.numberOfClassifiers];
	  double Ei[] = new double[this.numberOfClassifiers];
	  double EiP[] = new double[this.numberOfClassifiers];
	  
	  
      probabilities = new double[this.numberOfClassifiers][unlabeled.size()][this.numberOfClass];

	  
	  
	  for(int i=0; i<this.numberOfClassifiers; i++){
		  Li[i] = new PrototypeSet(labeled.clone());  // labeled data for Ai
		  Ei[i] = 0; // estimate for # mislabeled exs in Li 
	  }
	  
	  
	  

	  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
	  PrototypeSet test = new PrototypeSet(this.testDataSet.clone());
	 
	 
	  double traPrediction[] = null;
	  double tstPrediction[] = null;
	  int aciertoTrs = 0;
	  int aciertoTst = 0;
	  
	  
	   probabilities = new double[this.numberOfClassifiers][tranductive.size()][this.numberOfClass];
	   
	  traPrediction = clasificar(Li, tranductive);

	  int pertenece[][] = new int[tranductive.size()][this.numberOfClass]; 
	
	  double wii[] = new double[this.numberOfClass];
	  
	  for(int i=0; i<tranductive.size(); i++){
		
		  Arrays.fill(pertenece[i], 0);

		  Arrays.fill(wii,0);
		  
		  for(int j=0; j< this.numberOfClassifiers; j++){
			  
			  for (int z=0; z < this.numberOfClass; z++){
				  if(pre[j][i]==z && wi[j]>0.5){
					  // Allocate this calssifier y group Gj.
					  pertenece[i][z]++;
					  wii[z]+=wi[j];
				  }
				  
			  }
		  }
		  
		  
		  double countGj[] = new double[this.numberOfClass];
		  double max= Double.MIN_VALUE;
		  int clase =0;
		  
		  for(int j=0; j< this.numberOfClass; j++){
			  /* Compute group averge mean confidence */
			  
			  countGj[j] = (pertenece[i][j]+0.5)/ (pertenece[i][j]+1) * ((wii[j])/pertenece[i][j]);
			  
			  if(countGj[j]>max){
				  max = countGj[j];
				  clase = j;  
			  }
		  }
		  
		  
		  traPrediction[i]=clase;
		  
		  if(tranductive.get(i).getOutput(0) == traPrediction[i]){
			  aciertoTrs++;
	       }
		  
		  tranductive.get(i).setFirstOutput(traPrediction[i]);
		  
	  }
	  
	  
	  // Test phase
	  
	   probabilities = new double[this.numberOfClassifiers][test.size()][this.numberOfClass];
	  tstPrediction = clasificar(Li, test);
	  
	  

	  pertenece = new int[test.size()][this.numberOfClass]; 
		
	  
	  for(int i=0; i<test.size(); i++){
		
		  Arrays.fill(pertenece[i], 0);

		  Arrays.fill(wii,0);
		  
		  for(int j=0; j< this.numberOfClassifiers; j++){
			  
			  for (int z=0; z < this.numberOfClass; z++){
				  if(pre[j][i]==z && wi[j]>0.5){
					  // Allocate this calssifier y group Gj.
					  pertenece[i][z]++;
					  wii[z]+=wi[j];
				  }
				  
			  }
		  }
		  
		  
		  double countGj[] = new double[this.numberOfClass];
		  double max= Double.MIN_VALUE;
		  int clase =0;
		  
		  for(int j=0; j< this.numberOfClass; j++){
			  /* Compute group averge mean confidence */
			  
			  countGj[j] = (pertenece[i][j]+0.5)/ (pertenece[i][j]+1) * ((wii[j])/pertenece[i][j]);
			  
			  if(countGj[j]>max){
				  max = countGj[j];
				  clase = j;  
			  }
		  }
		  
		  
		  tstPrediction[i]=clase;
		  
		  if(test.get(i).getOutput(0) == tstPrediction[i]){
				  aciertoTst++;
		  }
		  
		  
		  test.get(i).setFirstOutput(tstPrediction[i]);
		  
	  }
	  
	  

	  System.out.println("Initial-Labeled size "+ Li[1].size());
	  
	  System.out.println("Initial % de acierto TRS = "+ (aciertoTrs*100.)/transductiveDataSet.size());
	  System.out.println("Initial % de acierto TST = "+ (aciertoTst*100.)/testDataSet.size());
	  
	  

	  

	  boolean changes = true;
	  
	  while(changes){
		  changes = false;
		  
		  double predicho[] = new double[unlabeled.size()];
	      probabilities = new double[this.numberOfClassifiers][unlabeled.size()][this.numberOfClass];

		  predicho = clasificar(Li, unlabeled); 		  
	  
		  // Choose which exs to propose for labeling 
		  for (int i=0; i< this.numberOfClassifiers; i++){
	    	  LiP[i] = new PrototypeSet();                 // data proposed for adding to Li
		  }	  
			  
 		  
		 // for(int i=0; i< this.numberOfClassifiers; i++){
		//	  System.out.println(wi[i]);  
		  //}
		  
		  
		  for(int j=0; j<unlabeled.size(); j++){ // For each unlabeld data.
			  
			  
			  // is the sum of the mean confidence values of the learners in the majority group is greater than the sum of the mean confidence values in the minority group??
			  
			  double sumWi[] = new double[this.numberOfClass];
			  Arrays.fill(sumWi, 0);
			  
			  for(int i=0; i< this.numberOfClassifiers; i++){
				  if(pre[i][j]!=-1)  sumWi[pre[i][j]] += wi[i];  
			  }
			  
			  
			  /*
			  for(int i=0; i< this.numberOfClass; i++){
				  System.out.println(sumWi[i]);  
			  }
			  
			  System.out.println("******************************************");
			  
			  */
			  
			  // Calculate the maximum condifence with different label to PREDICHO.
			  double Max = Double.MIN_VALUE;
			  
			  for(int i=0; i<this.numberOfClass; i++){
				  if(i!= (int) predicho[j]){      // different from labeld predicho.
					  if(sumWi[i]> Max){
						  Max = sumWi[i];
					  }
				  }
			  }
			  
					  
			  if(sumWi[(int)predicho[j]]> Max){ // second condition to label.
				  
				  for(int i=0; i< this.numberOfClassifiers; i++){ // if the classifier i does not label this X unlabeled as predicho[j], adding in Li.
					  
					  if(pre[i][j]!=predicho[j]){
						  Prototype anadir = new Prototype(unlabeled.get(j));
						  anadir.setFirstOutput(predicho[j]);
						  LiP[i].add(anadir);
					//	  System.out.println("PREVIOUSAdding");
						  
					  }
				  }
				  
			
			  }
		  
		  }
		  
		  
		  // Estimate if adding Li' to Li improves the accuracy
		  // Re-classifying and recalculing confidence interval.
		  
		  
		  PrototypeSet prueba[] = new PrototypeSet[this.numberOfClassifiers];
		  
		  for (int i=0; i<this.numberOfClassifiers; i++){
			  PrototypeSet aux = new PrototypeSet(Li[i].clone());
			  aux.add(LiP[i].clone());
    		  prueba[i] = new PrototypeSet(aux.clone());
		  }
		  
		  clasificar(prueba, unlabeled);
		  
		  
		  //double sumli[] = new double[this.numberOfClassifiers];
		  
		  double sumli=0;
		  
		  for (int i=0; i< this.numberOfClassifiers; i++){
			  
			  // computing the 95% -conf int. [li,hi] for each classifier
		 
			  sumli+=li[i];
			  //sumli[i] =average[i] -1.96*(deviation[i]/Math.sqrt(unlabeled.size()));;
						  
			  ///sumli[i]/= this.numberOfClass;
		  }	  
		  sumli/=this.numberOfClass;
		  
		  
		  double qi[] = new double[this.numberOfClassifiers];
		  double qiP[] = new double[this.numberOfClassifiers];
		  
		  for (int i=0; i< this.numberOfClassifiers ; i++){
			  
			  if(LiP[i].size() !=0){
				  qi[i] = Li[i].size() * Math.pow((1-2*(Ei[i]/Li[i].size())), 2);
				  EiP[i] = LiP[i].size() * (1.-sumli);
				  qiP[i] = (Li[i].size()+LiP[i].size())*(1.-((2.*(Ei[i]+EiP[i]))/(Li[i].size()+LiP[i].size())));
				  
				  System.out.println("qi -> "+qi[i]);
				  System.out.println("qiP -> + " + qiP[i]);
				  
				  if(qiP[i]> qi[i] && Li[i].size()<unlabeled.size()){
					  System.out.println("Adding");
					  changes = true;
					  Li[i].add(LiP[i].clone());
					  Ei[i] = Ei[i]+ EiP[i];	
					  
					  
					  //******************
					  
					  tranductive = new PrototypeSet(this.transductiveDataSet.clone());
					   test = new PrototypeSet(this.testDataSet.clone());
					 
					 
					   traPrediction = null;
					   tstPrediction = null;
					   aciertoTrs = 0;
					   aciertoTst = 0;
					  
					   probabilities = new double[this.numberOfClassifiers][tranductive.size()][this.numberOfClass];
					   
					  traPrediction = clasificar(Li, tranductive);

					   pertenece = new int[tranductive.size()][this.numberOfClass]; 
					
					  wii= new double[this.numberOfClass];
					  
					  for(int m=0; m<tranductive.size(); m++){
						
						  Arrays.fill(pertenece[m], 0);

						  Arrays.fill(wii,0);
						  
						  for(int j=0; j< this.numberOfClassifiers; j++){
							  
							  for (int z=0; z < this.numberOfClass; z++){
								  if(pre[j][m]==z && wi[j]>0.5){
									  // Allocate this calssifier y group Gj.
									  pertenece[m][z]++;
									  wii[z]+=wi[j];
								  }
								  
							  }
						  }
						  
						  
						  double countGj[] = new double[this.numberOfClass];
						  double max= Double.MIN_VALUE;
						  int clase =0;
						  
						  for(int j=0; j< this.numberOfClass; j++){
							  /* Compute group averge mean confidence */
							  
							  countGj[j] = (pertenece[m][j]+0.5)/ (pertenece[m][j]+1) * ((wii[j])/pertenece[m][j]);
							  
							  if(countGj[j]>max){
								  max = countGj[j];
								  clase = j;  
							  }
						  }
						  
						  
						  traPrediction[m]=clase;
						  
						  if(tranductive.get(m).getOutput(0) == traPrediction[m]){
							  aciertoTrs++;
					       }
						  
						  tranductive.get(m).setFirstOutput(traPrediction[m]);
						  
					  }
					  
					  
					  // Test phase
					  
					   probabilities = new double[this.numberOfClassifiers][test.size()][this.numberOfClass];
					  tstPrediction = clasificar(Li, test);
					  
					  

					  pertenece = new int[test.size()][this.numberOfClass]; 
						
					  
					  for(int m=0; m<test.size(); m++){
						
						  Arrays.fill(pertenece[m], 0);

						  Arrays.fill(wii,0);
						  
						  for(int j=0; j< this.numberOfClassifiers; j++){
							  
							  for (int z=0; z < this.numberOfClass; z++){
								  if(pre[j][m]==z && wi[j]>0.5){
									  // Allocate this calssifier y group Gj.
									  pertenece[m][z]++;
									  wii[z]+=wi[j];
								  }
								  
							  }
						  }
						  
						  
						  double countGj[] = new double[this.numberOfClass];
						  double max= Double.MIN_VALUE;
						  int clase =0;
						  
						  for(int j=0; j< this.numberOfClass; j++){
							  /* Compute group averge mean confidence */
							  
							  countGj[j] = (pertenece[m][j]+0.5)/ (pertenece[m][j]+1) * ((wii[j])/pertenece[m][j]);
							  
							  if(countGj[j]>max){
								  max = countGj[j];
								  clase = j;  
							  }
						  }
						  
						  
						  tstPrediction[m]=clase;
						  
						  if(test.get(m).getOutput(0) == tstPrediction[m]){
								  aciertoTst++;
						  }
						  
						  
						  test.get(m).setFirstOutput(tstPrediction[m]);
						  
					  }
					  
					  

					  System.out.println("update-Labeled size "+ Li[i].size());

					  System.out.println("update-% de acierto TRS = "+ (aciertoTrs*100.)/transductiveDataSet.size());
					  System.out.println("update-% de acierto TST = "+ (aciertoTst*100.)/testDataSet.size());
					  
					  
			     }
					  
			  }
			  System.out.println("Li[i] size = "+ Li[i].size());
			  System.out.println("LiP[i] size = "+ LiP[i].size());
			  
		  }
		  
		  

		  
		  
	  } // End while no change!
	  
	  
	 // Combining stage.

	  /*
	  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
	  PrototypeSet test = new PrototypeSet(this.testDataSet.clone());
	 
	 
	  double traPrediction[] = null;
	  double tstPrediction[] = null;
	  int aciertoTrs = 0;
	  int aciertoTst = 0;
	  */
	  
	  tranductive = new PrototypeSet(this.transductiveDataSet.clone());
	   test = new PrototypeSet(this.testDataSet.clone());
	 
	 
	   traPrediction = null;
	   tstPrediction = null;
	   aciertoTrs = 0;
	   aciertoTst = 0;
	  
	   probabilities = new double[this.numberOfClassifiers][tranductive.size()][this.numberOfClass];
	   
	  traPrediction = clasificar(Li, tranductive);

	   pertenece = new int[tranductive.size()][this.numberOfClass]; 
	
	  wii= new double[this.numberOfClass];
	  
	  for(int i=0; i<tranductive.size(); i++){
		
		  Arrays.fill(pertenece[i], 0);

		  Arrays.fill(wii,0);
		  
		  for(int j=0; j< this.numberOfClassifiers; j++){
			  
			  for (int z=0; z < this.numberOfClass; z++){
				  if(pre[j][i]==z && wi[j]>0.5){
					  // Allocate this calssifier y group Gj.
					  pertenece[i][z]++;
					  wii[z]+=wi[j];
				  }
				  
			  }
		  }
		  
		  
		  double countGj[] = new double[this.numberOfClass];
		  double max= Double.MIN_VALUE;
		  int clase =0;
		  
		  for(int j=0; j< this.numberOfClass; j++){
			  /* Compute group averge mean confidence */
			  
			  countGj[j] = (pertenece[i][j]+0.5)/ (pertenece[i][j]+1) * ((wii[j])/pertenece[i][j]);
			  
			  if(countGj[j]>max){
				  max = countGj[j];
				  clase = j;  
			  }
		  }
		  
		  
		  traPrediction[i]=clase;
		  
		  if(tranductive.get(i).getOutput(0) == traPrediction[i]){
			  aciertoTrs++;
	       }
		  
		  tranductive.get(i).setFirstOutput(traPrediction[i]);
		  
	  }
	  
	  
	  // Test phase
	  
	   probabilities = new double[this.numberOfClassifiers][test.size()][this.numberOfClass];
	  tstPrediction = clasificar(Li, test);
	  
	  

	  pertenece = new int[test.size()][this.numberOfClass]; 
		
	  
	  for(int i=0; i<test.size(); i++){
		
		  Arrays.fill(pertenece[i], 0);

		  Arrays.fill(wii,0);
		  
		  for(int j=0; j< this.numberOfClassifiers; j++){
			  
			  for (int z=0; z < this.numberOfClass; z++){
				  if(pre[j][i]==z && wi[j]>0.5){
					  // Allocate this calssifier y group Gj.
					  pertenece[i][z]++;
					  wii[z]+=wi[j];
				  }
				  
			  }
		  }
		  
		  
		  double countGj[] = new double[this.numberOfClass];
		  double max= Double.MIN_VALUE;
		  int clase =0;
		  
		  for(int j=0; j< this.numberOfClass; j++){
			  /* Compute group averge mean confidence */
			  
			  countGj[j] = (pertenece[i][j]+0.5)/ (pertenece[i][j]+1) * ((wii[j])/pertenece[i][j]);
			  
			  if(countGj[j]>max){
				  max = countGj[j];
				  clase = j;  
			  }
		  }
		  
		  
		  tstPrediction[i]=clase;
		  
		  if(test.get(i).getOutput(0) == tstPrediction[i]){
				  aciertoTst++;
		  }
		  
		  
		  test.get(i).setFirstOutput(tstPrediction[i]);
		  
	  }
	  
	  

	  System.out.println("Labeled size "+ Li[1].size());

	  System.out.println("% de acierto TRS = "+ (aciertoTrs*100.)/transductiveDataSet.size());
	  System.out.println("% de acierto TST = "+ (aciertoTst*100.)/testDataSet.size());
	  
     // tranductive.print();
	 // tranductive.save("outputDemocratic.dat");

	  
	  
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
