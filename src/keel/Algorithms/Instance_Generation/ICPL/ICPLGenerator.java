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
	ICPL.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  12-3-09
	Copyright (c) 2009 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.ICPL;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;


import keel.Algorithms.Instance_Generation.*;
import java.util.*;

import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;

import org.core.*;



import java.util.StringTokenizer;



/**
 * 
 * @param  numberOfInitialsCentroids
 * @param  k, to use with knn rule in the initialization.
 * @param GradientStep
 * @param Temperature
 * @author Isaac Triguero
 * @version 1.0
 */
public class ICPLGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  

  private int nAlg; // number of ICPL (1,2,3,4)
  private String method; // number of filtering (ENN, RT2, ACC)
  private int k; // for k-nn
  private int Q; // threshold for ACC filtering
  //others variables.
  protected int numberOfPrototypes;  
  protected int numberOfClass;
 
  
  /**
   * Build a new ICPLGenerator Algorithm
   *
   */
  
  public ICPLGenerator(PrototypeSet _trainingDataSet, int nalg, String Method, int k, int Q)
  {
      super(_trainingDataSet);
      algorithmName="ICPL";
      
      this.nAlg = nalg;
      this.method = Method;  
      this.k = k;
      this.Q = Q;

  }
  

  /**
   * Build a new RSPGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public ICPLGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="ICPL";
      
      this.nAlg = parameters.getNextAsInt();
      this.method = parameters.getNextAsString();
      this.k = parameters.getNextAsInt();
      this.Q = parameters.getNextAsInt();
      
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
    
      System.out.println("Isaac dice: nalg= " + this.nAlg+ " method = " + this.method );
      System.out.println("Number of class= "+ this.numberOfClass);
  }
  
  
  
  
  /**
   * ICPL1_Pseudo-code:
   * ICPL1( T training)
		1)	C1= abstraccion T
		2)	C2 = Filtrar T.
		3)	S = C1
		4)	Para cada prototipo P en  C2
				Tmp = S U P.
				Good = numero de instancias de T correctamente clasificadas por P en Tmp.
				Bad = numero de instancias de T mal clasificadas por P en Tmp.
				Si(Good>Bad)
		 	S= S u P. 
		5)	Devolver S.


   */
  
  protected PrototypeSet icpl1_or_3(int num){
	  PrototypeSet S;
	  PrototypeSet C1, C2,tmp; 
	  double Good, Bad;

	  if(num == 1){
		  System.out.println("Algorithm ICPL 1");
		  C1 = TPA();
		  //C1.print();
		  C2 = filtering();
	  }else{
		  System.out.println("Algorithm ICPL 3");
		  C2 = TPA();
		  C1 = filtering();	  
	  }
	  S = C1.clone();
	  

	  for(Prototype p: C2){
		  tmp = S.addPrototype2(p);
		  
		  //obtain the prototype that has p like the NN.
		  
		  Good = 0;
		  Bad = 0;
		  PrototypeSet neig = trainingDataSet.isTheNearPrototype(p);
		  
		  for(Prototype q: neig){
			  if(q.getOutput(0) == p.getOutput(0)){
				  Good++;
			  }else Bad++;
			  
		  }
		  
		  
		  //Good = KNN.classficationAccuracy(tmp, trainingDataSet);
		  //Bad = trainingDataSet.size() - Good;
		  
		  if( Good> Bad){
			  S.add(p);
		  }
	  }
	  
	  return S;
	    
  }

  
  /** 
   * ICPL2 - pseudo-code
   * ICPL2( T training)
		1)	C1= abstracciï¿½n T
		2)	C2 = Filtrar T.
		3)	S = C1
		4)	Para cada prototipo P en  C2
		Tmp = S U P.
		Good = nï¿½mero de instancias de T correctamente clasificadas por P en Tmp.
		Bad = nï¿½mero de instancias de T mal clasificadas por P en Tmp.
		Si(Good>Bad)
		 	S= S U P. 
		5)	Para cada prototipo P en  C1
		Tmp = S\ P.
		With= nï¿½mero de instancias de T correctamente clasificadas por S
		Without = nï¿½mero de instancias de T correctament clasificadas por  Tmp.
		Si(Without>With)
		 	S= S \ P. 
		6)	Devolver S.

   */
  
  
  protected PrototypeSet icpl2_or_4(int num){
	  PrototypeSet S = new PrototypeSet();
	  PrototypeSet C1, C2,tmp; 
	  double Good, Bad, With, Without;
	  
	  if(num == 2){
		  C1 = TPA();
		  C2 = filtering();
	  }else{
		  C2 = TPA();
		  C1 = filtering();	   // Change C2 by C1.
	  }
	  
	  S = new PrototypeSet(C1);
	  
	 // S.print();
	  System.out.println("C1 size =" + C1.size() + " C2size ="+ C2.size());
	  
	  
	  Prototype p2 = C1.get(1);
  
	  Good = 0;
	  Bad  = 0;
	  for(Prototype p: C2){
		  tmp = S.addPrototype2(p);
		  
		  Good = 0;
		  Bad = 0;
		  PrototypeSet neig = trainingDataSet.isTheNearPrototype(p);
		  
		  for(Prototype q: neig){
			  if(q.getOutput(0) == p.getOutput(0)){
				  Good++;
			  }else Bad++;
			  
		  }
		  //Good = KNN.classficationAccuracy(tmp, trainingDataSet);
		  //Bad = trainingDataSet.size() - Good;
		  
		  if( Good> Bad){
			  S.add(p);
		  }
	  }
	 
	  for(Prototype p: C1){
		  
		  tmp =  new PrototypeSet(S.without(p));
		  
		  With = KNN.classficationAccuracy(S, trainingDataSet);
		  Without = KNN.classficationAccuracy(tmp, trainingDataSet);
		  
		  if( Without>= With){
			  S = new PrototypeSet(tmp);
		  }
	  }
	  
	  return S;
	    
  }
  
  
  /**
   * 
   * @param index
   * @return
   */
  protected double typicality(int index){
	  double result =0.0;
	  double avgDifClass=0.0;
	  PrototypeSet sameClass, differentClass;
	  
	  
	  Prototype initial = trainingDataSet.get(index);
	  sameClass = trainingDataSet.getFromClass(initial.getOutput(0)).without(initial);  //same class (without the prototype)
	  differentClass = trainingDataSet.getAllDifferentFromClass(initial.getOutput(0));
	  
	  
	  for(Prototype p: sameClass){
		result +=1-Distance.d(initial, p);  
	  }
	  
	  result/= sameClass.size();
	  
	  for(Prototype p: differentClass){
		  avgDifClass +=1- Distance.d(initial,p);
	  }
	  
	  avgDifClass /= differentClass.size();
	  
	  //System.out.println("typicality index= "+ index + " = " + result/avgDifClass);
	  return result/avgDifClass;
  }
/**
 * Identify a border by typicaly.
 * 
 * @return a boolean vector indicate which prototypes are border prototypes.
 */
  protected boolean[] identifyBorder(PrototypeSet pClass[]){
	  boolean borders[] = new boolean [trainingDataSet.size()];
	  double typ[] = new double[trainingDataSet.size()];
	  double Tmean[], Tsd[];
	  
	  Arrays.fill(borders, false);
	
	  for(int i = 0; i< trainingDataSet.size();i++){
		  //trainingDataSet.get(i).setIndex(i); // Establish the index, useful later.
		  typ[i] = typicality(i);		  
	  }
	  
	  //pClass = new PrototypeSet[this.numberOfClass];
	  Tmean = new double[this.numberOfClass];
	  Tsd = new double[this.numberOfClass];
	  
	  //Arrays.fill(Tmean, 0);
	  //Arrays.fill(Tsd,0);
	  
	  for(int i=0; i< this.numberOfClass; i++){
		  
		  pClass[i] = new PrototypeSet(trainingDataSet.getFromClass(i));
		
		  Arrays.fill(Tmean, 0);
		  Arrays.fill(Tsd,0);
		  		  
		  for (int m = 0; m < pClass[i].size(); m++){
			  //System.out.println(pClass[i].get(m).getIndex());  Lo de los index va de PM
			  Tmean[i] += typ[ pClass[i].get(m).getIndex()];
		  }
		  Tmean[i] /= pClass[i].size();
		  
		  for(int m=0; m < pClass[i].size(); m++){
			  double aux= typ[ pClass[i].get(m).getIndex()] - Tmean[i];
			  aux *= aux;  // ^2
			  Tsd[i] += aux;
		  }
		  Tsd[i] /= pClass[i].size();
		  
		  System.out.println("Tmean = "+ Tmean[i] + " Tsd = "+ Tsd[i]);
	  
		  //Sort class C instances in descending order of typicality.
		  // Bubble sort.
		  
	        for (int k = 0; k < pClass[i].size(); k++)
	        {
	            for (int j = 0; j < pClass[i].size()-1; j++)
	            {
	            	if( typ[pClass[i].get(j).getIndex()] < typ[pClass[i].get(j+1).getIndex()]){ //here we need the index
	            		
	            		Prototype aux =pClass[i].get(j);
	            		
	            		pClass[i].set(j, pClass[i].get(j+1));
	            		pClass[i].set(j+1,aux);
	            	}
	            	
	            }
	        }

	        
		  
	  } // End for each class C.
	  
	  
	  //for(int i=0 ; i< pClass[1].size(); i++){
	//	  System.out.println(typ[pClass[0].get(i).getIndex()]);
	  //}
	  
	  for(int i=0; i< this.numberOfClass; i++){
		  
		  for(int j= 0; j< pClass[i].size(); j++){ // For each instace I of class i int T.
			  
			  if(typ[pClass[i].get(j).getIndex()]<  (Tmean[i]-Tsd[i])){
				  borders[pClass[i].get(j).getIndex()] = true;
			  }else{
				  borders[pClass[i].get(j).getIndex()] = false;
			  }
			  
		  }
	  }
	  
	  
	  
	  
	  return borders;
  }
  
  
  
  /**
   * TPA.
   * 
   */
  
  protected PrototypeSet TPA(){
	  
	  PrototypeSet S= new PrototypeSet();
	  boolean borders[];
	  PrototypeSet pClass[] = new PrototypeSet[this.numberOfClass];
	  
	  
	  borders = identifyBorder(pClass); //this method has sorted pClass
	  
	  PrototypeSet process = new PrototypeSet();
	  PrototypeSet merge = new PrototypeSet(); // Need to merge process
	  
	  for (int i=0; i< this.numberOfClass; i++){
		  
		  //pClass[i] = new PrototypeSet(trainingDataSet.getFromClass(i));
		  //System.out.println("Class i =" + i ) ;
		  		  
		  for(int k= 0; k< pClass[i].size(); k++){
			  Prototype instance = pClass[i].get(k); // First instance with class i.
			 
			  //System.out.println("instance index =" + instance.getIndex());
			  //System.out.println(typicality(pClass[i].get(k).getIndex()));
			  
			  if(!borders[instance.getIndex()]){  // For each NON-BORDER
				  
				  if(!process.contains(instance)){
					  process.add(instance); // Denote like process.
					  
					 
					  Prototype P = Merge(S,borders,instance,merge);
					  if(!trainingDataSet.contains(P)){  // if P has been merged, abstract
						  S.add(P);
					  }
					 
				  }
				 
			  }
			  
		  }
	  }
	  
	  
	  System.out.println ( "S size = " + S.size());
	  
	  
	  
	  System.out.println("Abstracction Accuracy % " +accuracy(S,trainingDataSet));
	  //System.out.println("abstraction Reduction % " + (100-(S.size()*100)/trainingDataSet.size()) );
	  
	  System.out.println("data retention rate " + (S.size()*1.)/trainingDataSet.size());
	  return S;
	  
  }
  
  
  
  /**
   * Merge
   * @param S
   * @param borders
   * @param I
   * @return
   */
  protected Prototype Merge(PrototypeSet S, boolean borders[], Prototype I, PrototypeSet merge){
	 Prototype P = new Prototype();
	 PrototypeSet tData = new PrototypeSet(trainingDataSet);
	 Prototype N, M;
	 double Clase;
	 
	 //PrototypeSet merge = new PrototypeSet(); // We must save the prototypes merge.
	 
	 P = I;
	 N = tData.nearestTo(I);
	
	 // System.out.println("N index =" + N.getIndex());
	 
	 //if(N.equals(trainingDataSet.get(N.getIndex()))) System.out.println("Todo OK");
	 
	 Clase = I.getOutput(0);
	 
	 //int num_merges = 0;
	 //boolean mergeBefore = false;
	 while( (N.getOutput(0)!= Clase) || (!borders[N.getIndex()]) )
	 {
		
		 //System.out.println("N index =" + N.getIndex());
		 
		 if(N.getOutput(0) != Clase){
			 //System.out.println("Border or noisy");
			 
			 //It may be either a border point or a noise.
			 // N = next nearest neighbor
			 tData = tData.without(N);
			 
			 N = tData.nearestTo(I);
			 // N.print();
			 if( N.getOutput(0) != Clase){ // if the next nearest neighbor is different class, it's a border, not noise.
				// System.out.println("Nun merges hecho en este bucle  " + num_merges);
				 return P; // N is a border point
			 } // else, it will be noise, discard N.
			 
		 }
		 
		 if(borders[N.getIndex()]){
			 return P;
		 }else{
			
			 
			 if(!merge.contains(N)){ // here is the mistake.
				 
				 //System.out.println("Entrooo");
				 //mergeBefore = true;
				 //P.add(N);
				 P = P.avg(N);
				 merge.add(N); // save N like a prototype before merge.
		//		 num_merges++;
				 tData = tData.without(N);
				 N =  tData.nearestTo(I);
			 }else{
				 //System.out.println("Entrooo");
				 if(S.size() > 0){
					 M= S.nearestTo(N);				 
					//M = S.containing(N);
					 P = P.avg(M);
			//		 num_merges++;
					 S.remove(M);
				 }
				// System.out.println("Nun merges hecho en este bucle  " + num_merges);
					 return P;
			 }
		 }
	 }
	 
	 //System.out.println("Nun merges hecho en este bucle  " + num_merges);
	 return P;
  }
  
  
  /**
   * Method to call the appropriate method
   * @return
   */
  protected PrototypeSet filtering(){
		  
	  PrototypeSet result = new PrototypeSet();
	  
	  //System.out.println("metodo="+this.method+"!");
	  if(this.method.equals("ENN")){
		  result = ENN(trainingDataSet);
	  }else if(this.method.equals("ACC")){
		  result =  ACC(trainingDataSet);
	  }else if(this.method.equals("RT2") ){
		  result = RT2(trainingDataSet);
	  }
	  
	  //result.print();
	  
	  System.out.println("Filtering Accuracy % " +accuracy(result,trainingDataSet));
	  System.out.println("Filtering Reduction % " + (100-(result.size()*100)/trainingDataSet.size()) );
      
	  System.out.println("FILTERING retention rate " + (result.size()*1.)/trainingDataSet.size());
	  
	  return result;
	  
	  
  }
  
  
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
	  int majority = this.k / 2 + 1;
	 // System.out.println("Mayorï¿½a " + majority);


	  int toClean[] = new int [T.size()];
	  Arrays.fill(toClean, 0);
	  int pos = 0;
	  
	for ( Prototype p : T){
		 double class_p = p.getOutput(0);
		PrototypeSet neighbors = KNN.knn(p, trainingDataSet, this.k);
		
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
  
  
  
  /**
   * Retaining Center Instances
   * @param T
   * @return
   */
  protected PrototypeSet ACC (PrototypeSet T)
  {
	  PrototypeSet result = new PrototypeSet();
	  int accuracy[] = new int [T.size()]; 
	  
	  Arrays.fill(accuracy, 0);
	  
	  int pos = 0;
	  for(Prototype p: T){
		  
		  Prototype near = T.nearestTo(p); // Without itself
		  //System.out.println("near index = "+ near.getIndex());
		  if(p.getOutput(0) == near.getOutput(0) ){
			  
			  accuracy[near.getIndex()]++;
		  }
		  
		  pos++;
	  }
	  
	  for(int i = 0; i< T.size(); i++){
		 if(accuracy[i] > this.Q){
			 result.add(T.get(i));
		 }
	  }
	  return result;
  }
  
  
  
  /**
   * Return all the prototype in (this) that has other like the nearest neighbor
   * @return
   */
  protected PrototypeSet associatesPrototype(PrototypeSet one,Prototype other){
  	PrototypeSet result = new PrototypeSet();
  	
  	for(Prototype p: one){
  		
  		PrototypeSet nearest = KNN.getNearestNeighbors(p, one, this.k);
  		
  		if(nearest.contains(other)){ // if one of k neares neighbor is the prototype. is an associates.
  			result.add(p);
  		}
  	}
  	return result;
  	
  }
  
  
  /**
   * Retaining Border instances
   * @param T
   * @return
   */
  protected PrototypeSet RT2 (PrototypeSet T)
  {
	  PrototypeSet result = new PrototypeSet();
	  
	  result = ENN(T); // ENN is applied first to filter noise.
	  
	  //System.out.println("Result size tras ENN "+ result.size());
	  
	  //Stablish index. to result of ENN
	  
	  for(int i = 0; i< result.size(); i++)
		  result.get(i).setIndex(i);
	  
	  
	  // Bubble sort in descening order by the distnce of an instace to its nearest unlike neighbor.
	  double DistunlikeNeighbor[] = new double [result.size()]; 
	  
	  int pos =0;
	  for(Prototype p: result){
		  
		  Prototype nearestUnlike =  KNN.getNearestWithDifferentClassAs(p, result);
		  DistunlikeNeighbor[pos] = Distance.d(p, nearestUnlike);
		                     
		  pos++;
	  }
	  
	  
	  // sort descending order b distance of an instance to this nearest unlike neigbor.
      for (int k = 0; k < result.size(); k++)
      {
          for (int j = 0; j < result.size()-1; j++)
          {
        	  
        	  if(DistunlikeNeighbor[result.get(j).getIndex()] < DistunlikeNeighbor[result.get(j+1).getIndex()] ){
        		  Prototype aux = result.get(j);
            		result.set(j, result.get(j+1));
              		result.set(j+1,aux);
        		  
        	  }
         	
          }
      }
      

	// now we calculate the associates for each 
      
      PrototypeSet associates[] = new PrototypeSet[result.size()];
      
      pos = 0;
	  for(Prototype p : result){
		  associates[pos] = associatesPrototype(result,p);
          pos++;
	  }

	  int majorityK = this.k/2 +1;
	  int toClean[] = new int [result.size()];
	  Arrays.fill(toClean, 0);
	  
	  
	  for(int i=0; i< result.size();i++){
		  Prototype p = result.get(i);
		  int majority = associates[i].size()/2 + 1;
		  int asociatesFail = 0;
		  
		  PrototypeSet withoutP = result.without(p);
		  double class_p = p.getOutput(0);
		  
		  // we must to check associates are able to classify correct.
		  for(int j=0; j< associates[i].size(); j++){
			 
			  PrototypeSet newNeighbors = KNN.getNearestNeighbors(associates[i].get(j), withoutP, this.k);
			 
			  int counter= 0;
			  for(Prototype q1 : newNeighbors ){
				double class_q1 = q1.getOutput(0);
				
				if(class_q1 == class_p){
					counter++;
				} 
				
			  }
			  
			  if(counter < majorityK){ // it able to classify without p.
				  asociatesFail++;
			  }
			  			  
		  }
		  
		  if(asociatesFail >= majority){
			  toClean[i] = 1;
		  }
	  }
	  
	  PrototypeSet clean = new PrototypeSet();
	  
	  for(int i=0; i< result.size(); i++){
		  if(toClean[i] == 0){
			  clean.add(result.get(i));
		  }
	  }
	  
	  //result = new PrototypeSet(clean);
	  
	  return clean;
  }
  
  @SuppressWarnings({ "unchecked", "static-access" })
  public PrototypeSet reduceSet()
  {

	  System.out.print("\nThe algorithm is starting...\n Computing...\n");
	  System.out.println("Number of class "+ this.numberOfClass);
	  
	  
	 // trainingDataSet.print();
	  //Stablish index.
	  
	  for(int i = 0; i< trainingDataSet.size(); i++)
		  trainingDataSet.get(i).setIndex(i);
	  
	  
	 PrototypeSet outputDataSet = new PrototypeSet();
	 
	 
	 if ( this.nAlg == 1 || this.nAlg == 3) outputDataSet= icpl1_or_3(this.nAlg);
	 else outputDataSet = icpl2_or_4(this.nAlg);
	 
   // outputDataSet.print();	  
	  System.out.println("Accuracy % " +accuracy(outputDataSet,trainingDataSet));
	  System.out.println("Reduction % " + (100-(outputDataSet.size()*100)/trainingDataSet.size()) );
      
	  
	 //RT2(trainingDataSet);
	  /*
	  boolean marcas[];
	  marcas = new boolean[outputDataSet.size()];
	  Arrays.fill(marcas, true);

	  double accuracyInic =KNN.classficationAccuracy(outputDataSet, trainingDataSet);
	  double accuracy;
	  
	  for(int i=0; i< outputDataSet.size(); i++){
		  marcas[i] = false; //At the begining you don't think you can elimante.
		  
		  
		  PrototypeSet leaveOneOut = outputDataSet.without(outputDataSet.get(i));
		  accuracy = KNN.classficationAccuracy(leaveOneOut, trainingDataSet);
		  
		  if(accuracy > accuracyInic){
			  
			  marcas[i] = true; // we can eliminate
		  }
		  
		  
	  }
	  
	  //Then we create the result set..
	  
	  PrototypeSet clean = new PrototypeSet();
	  
	  for(int i=0; i< marcas.length; i++){
		  if(!marcas[i]){
			  clean.add(outputDataSet.get(i));
		  }
	  }
	  
      System.out.println("Accuracy % " +accuracy(clean,trainingDataSet));
      System.out.println("Reduction % " + (100-(clean.size()*100)/trainingDataSet.size()) );
	  
	  */
	  
	  
      return outputDataSet;   
  
    
	  
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
      Parameters.setUse("ICPL", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      ICPLGenerator.setSeed(seed);
      
     // int blocks =Parameters.assertExtendedArgAsInt(args,10,"number of blocks", 1, Integer.MAX_VALUE);
      
      //String[] parametersOfInitialReduction = Arrays.copyOfRange(args, 4, args.length);
     //System.out.print(" swarm ="+swarm+"\n");
      
      
      ICPLGenerator generator = new ICPLGenerator(training, 1, "ENN", 4,40);
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
