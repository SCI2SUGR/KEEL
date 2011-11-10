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
	PSCSA.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  23-7-2009
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.PSCSA;

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

import org.core.*;

import org.core.*;

import java.util.StringTokenizer;



/**
 * @param k Number of neighbors
 * @param Population Size.
 * @param ParticleSize.
 * @param Scaling Factor.
 * @param Crossover rate.
 * @param Strategy (1-5).
 * @param MaxIter
 * @author Isaac Triguero
 * @version 1.0
 */
public class PSCSAGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  
  // We need the variable K to use with k-NN rule
  private int k;
 
  private double HyperMutationRate;
  private double ClonalRate;
  private double MUTATION_RATE;
  private String StimulationFunc;
  private double STIMULATION_THRESHOLD;
  private int MaxIter;
  private int ParticleSize;
  private double alpha; // scalar value
  protected int numberOfClass;
  private double AS[]; // average Stimulation. Calculate a priori.

  protected int numberOfPrototypes;  // Particle size is the percentage
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  /**
   * Build a new PSCSAGenerator Algorithm
   */
  
  public PSCSAGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double F, double CR, int strg)
  {
      super(_trainingDataSet);
      algorithmName="PSCSA";
      
      this.k = neigbors;

      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
  }
  


  /**
   * Build a new PSCSAGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public PSCSAGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="PSCSA";
      this.k =  parameters.getNextAsInt();
      this.HyperMutationRate =  parameters.getNextAsDouble();
      this.ClonalRate =  parameters.getNextAsDouble();
      this.MUTATION_RATE = parameters.getNextAsDouble();
     // this.StimulationFunc = parameters.getNextAsString();
      this.STIMULATION_THRESHOLD = parameters.getNextAsDouble();
      //this.MaxIter = parameters.getNextAsInt();
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
   //   this.ParticleSize =  parameters.getNextAsInt();
      this.alpha = parameters.getNextAsDouble();
      
      this.numberOfPrototypes = getSetSizeFromPercentage(ParticleSize);
      
      //AS calculations. Burrada :)
      AS = new double [this.numberOfClass];
      PrototypeSet clases [] = new PrototypeSet[this.numberOfClass];
      for(int i=0; i< this.numberOfClass; i++){
    	  clases[i] = trainingDataSet.getFromClass(i);
    	  String clasesS[][]= (clases[i]).to8GrayString(); // Lo paso a String.
    	  
    	  
    	  for(int j=0; j< clases[i].size(); j++){
    		  for(int k=j+1; k< clases[i].size(); k++){
    			  AS[i]+=stimString2(clasesS[j], clasesS[k]);
    		  }
    	  }
    	  AS[i] /= clases[i].size();
    	  
    	  AS[i] = 1./AS[i];
    	//  System.out.println("AS " + i+ " , "+ AS[i]);
      }
      
      
      
      
      System.out.print("\nIsaac dice:  " + k + "HYrate= "+this.HyperMutationRate+ " Stimulation=  "+ this.StimulationFunc);

  }
  
  
  /**
   * Stim for binary codification
   */
  public double stimString(String [][] binario, int index, String [][] binarioTrain, int pIndex){
	  double hamming= 0.0;
	  double valor1, valor2;
	  
	  for(int i=0; i< binario[index].length; i++){ // Para cada uno de los atributos
		  for(int j=0; j< binario[index][i].length(); j++){ // para cada uno de los dï¿½gitos de los atributos.
			  valor1 = Character.getNumericValue(binario[index][i].charAt(j));
			  valor2 = Character.getNumericValue(binarioTrain[pIndex][i].charAt(j));
			  hamming += Math.abs(valor1-valor2);
		  }
	  }
	  
	  int maxHaming = trainingDataSet.get(0).numberOfInputs() * 8;
	  
	  hamming = 1 - (1.*hamming)/maxHaming;
	
	  return hamming;
  }
  
  /**
   * Stim for binary codification
   */
  public double stimString2(String [] binario, String [] binarioTrain){
	  double hamming= 0.0;
	  int valor1, valor2;
	  
	  for(int i=0; i< binario.length; i++){ // Para cada uno de los atributos
		  //System.out.println("Binario " + binario[i]);
		  //System.out.println("BinarioTrain " + binarioTrain[i]);
		  for(int j=0; j< binario[i].length(); j++){ // para cada uno de los dï¿½gitos de los atributos.
			  valor1 = Character.getNumericValue(binario[i].charAt(j));
			 // System.out.println(binario[i]);
			  valor2 = Character.getNumericValue(binarioTrain[i].charAt(j));
			  hamming += Math.abs(valor1-valor2);
		  }
	  }
	  
	  int maxHaming = trainingDataSet.get(0).numberOfInputs() * 8;
	  
	  hamming = 1 - (1.*hamming)/maxHaming;
	//  System.out.println("Hamming = " +hamming);
	  
	  /*if(hamming == 0){
		  return 1;
	  }*/
	
	  return hamming;
  }
  
    /**
   * Stim function. Is used to measure the response of B cell to an antigen. For CSA
   * is inversely proportional to the Hamming distance between the feature
   * vectors of the argument elements.
   */
  public double stim(Prototype agi, Prototype mj){
	  
	  double hamming = 0.0;
	  
	  
	  if(this.StimulationFunc.equals("Hamming")){
		  for(int i=0; i<agi.numberOfInputs(); i++){
			 hamming += Math.abs(agi.getInput(i) - mj.getInput(i)); 
		  }
	  }else{ // Use Euclidean Distance
		  
		 hamming = Distance.euclideanDistance(agi, mj);
		 
	  }
	  
	  
	  if(hamming == 0){
		  return 1;
	  }
	
	  return 1/hamming;
  }
  
  /**
   * PROLIFERATION-I: HyperMutacion of the m match.
   * @return the Hypermutation's PrototypeSet.
   */
  
  public Pair<String [][], double []> HyperMutation(String mmatch[], double classMatch, String agk[], double classAgk, int proliferation){
	
	int numClones=0;
	Boolean mut ;
	String bj[];
	double claseGenerada = 0.0; 
	
	// initially B = mmatch

		// How many clones we must to Create? 
	//System.out.println("Stim agk, mi = " + stim(agk,mmatch));
	if (proliferation == 1){
		numClones = (int) (this.HyperMutationRate*this.ClonalRate* stimString2(agk,mmatch));
	}else{
		numClones = (int) (this.ClonalRate* stimString2(agk,mmatch));
	}
	
	//PrototypeSet B = new PrototypeSet(numClones);
	//B.add(mmatch);
	String B [][] = new String[numClones][mmatch.length];
	double clasesB  []= new double[numClones];
	
	int generados = 0;
	//System.out.println("Number of Clones =" + numClones + " stim = "+ stimString2(agk,mmatch));
	while(generados < numClones){
		
		bj = new String[mmatch.length];
		
		Pair <String[], Boolean> salida;
		
		salida =mutate(mmatch, agk, claseGenerada);
		
		mut = salida.second();
		//System.out.println("mmatch " + mmatch[0]);
		//System.out.println("bj = "+ bj[0]);
		//bj.print();
		if(mut){ 
			//System.out.println("COnfirmo mutaciï¿½n");
			B[generados] = salida.first(); // Aï¿½ado el generado
			double random = RandomGenerator.Randdouble(0, 1);
			double clase = RandomGenerator.Randint(0, this.numberOfClass);
			  
			  if(random < this.MUTATION_RATE){
				  clasesB[generados] = clase;
				  
			  }else{
				  clasesB[generados] = classAgk;
			  }
			 
			// clasesB[generados] = claseGenerada;
				//System.out.println("class AGk = "+ classAgk + " CLASE = "+ clasesB[generados]);
			generados++;
		
		};
		//else {System.out.println("NO entra");}
	}
	
	Pair <String[][], double[]> salida = new Pair<String[][],double[]> (B,clasesB);
	return salida;
	  
  }
  
  /**
   * Production of mutated clones
   */
  public Pair<String [], Boolean> mutate(String Mmatch[], String agk[],double claseBj){
	 
	  Boolean flag = false;
	  double random;
	 double clase, value;
	 int toggle;
	
	 String bj [] = new String[Mmatch.length];
	 
	 //Prototype x = new Prototype(Mmatch);
	 
	  // For each element of X
	  for(int i=0; i< bj.length; i++){
		  
		  bj[i] = new String(""); //String mutado = "";
		  for(int j= 0; j< Mmatch[i].length(); j++){ // Para cada Binario del String
			  random = RandomGenerator.Randdouble(0, 1);
			  int valor1 = Character.getNumericValue(Mmatch[i].charAt(j));	
			  
			  if (random < this.MUTATION_RATE){
					// System.out.println("MUTADO! digito "+ j +" valor 1 =" + valor1); 
				  //Toggle
				  if(valor1== 0 ){
					  bj[i] +="1";
				  }else{
					  bj[i] +="0";
				  }
							  
				  //x.setInput(i, agk.getInput(i)); // Togle == mutate con respecto agk
				  flag = true;
			  }else{
				  bj[i] += Mmatch[i].charAt(j);  
			  }
		  
		      
			  if(flag){
				  random = RandomGenerator.Randdouble(0, 1);
				  clase = RandomGenerator.Randint(0, this.numberOfClass-1);
				  
				  if(random < this.MUTATION_RATE){
					  claseBj = clase;
				  }
			  }
		  }
		  //System.out.println("mutado = " + mutado);
		  //	bj[i] = mutado;
	  }
	  
	  
	  
   /* System.out.println("Original");
	  Mmatch.print();
	  System.out.println("Mutado");
	  bj.print();
	 */
	  //if(flag) System.out.println("Deberï¿½a entrar");
	  Pair<String[],Boolean> part = new Pair<String[],Boolean>(bj,flag);
	  return part;
  }
  
  
  /**
   * To minimize the computaional cost in generation clones
   */
  public Pair<String [][], double []> ResourceAllocation(String B[][], double clasesB[], String agk[], double claseAgk){
	  double stim[] = new double[B.length];
	  double resources[]  = new double[B.length];
	  double minStim = Double.MAX_VALUE, maxStim = Double.MIN_VALUE;
	  
	  for(int i=0; i< B.length; i++){
		 // B.get(i).print();
		 // System.out.println("agk = " + agk[0] + "Bi = " + B[i][0]);
		  stim[i] = stimString2(agk,B[i]);
		  
		  // Calculate the min and max Stim
		  if(stim[i] < minStim){
			  minStim = stim[i];
		  }
		  
		  if(stim[i] > maxStim){
			  maxStim = stim[i];
		  }
		  //System.out.print(stim[i] +",");
	  }
	  
	  
	  /***************/
	  
	  for(int i=0; i<B.length ; i++){
		 
		  if(clasesB[i] == claseAgk){
			  stim[i] = (stim[i]- minStim) / (maxStim-minStim); 
		  }else{
			  stim[i] = 1 - (stim[i]- minStim) / (maxStim-minStim); 
		  }  
		  
		  resources[i] = stim[i] * this.ClonalRate*1.;
		  
	  }
	  
	//  System.out.println( "Fin primer paso");
	  
	  double clases = 0.0;
	  double numResAllowed, numResRemove;
	  
	  int[] borrar = new int [B.length]; //Apunto los indices a borrar
	  int utilborrar=-1;
	  
	  double totalNumResources =0;
	  
	  for(int i=0; i<B.length;i++){
		  totalNumResources += resources[i];
	  } 
	  
	  while (clases < this.numberOfClass){
		  double resAllocated = 0;
		  
		  for(int i=0; i<B.length;i++){
			  if (clasesB[i]== clases){
				  resAllocated += resources[i];  // Recursos para la clase Clases
			  }
			  
		  } //Calculate the resources allocated
		  
		 // System.out.println("Total Num Resources "+ totalNumResources);
		  
		  
		  if(clases == claseAgk){
			  numResAllowed = (totalNumResources)/ 2.;  // La mitad para los de la misma clase que el antigeno
		  }else{ 
			  numResAllowed =(totalNumResources)/ (2.*(this.numberOfClass-1)); // La otra mitad se divide entre las clones de otras clases
	      }
		  
		  // Si no hay de una clase, lo lï¿½gico es que resALlocated sea 0.
		//System.out.println("totalNumReources = "+ totalNumResources);
		  //System.out.println("NumResAllowed = "+ numResAllowed);
		  //System.out.println("ResAllocated = "+ resAllocated);
		  numResRemove=2;
		  double recursosRemovidos = -1;

		  int iter = 50;
		  while(resAllocated > numResAllowed && iter >0 ){ //&& numResRemove >1
			  numResRemove = resAllocated - numResAllowed;
			  //if(numResRemove >1){
				//  System.out.println("NumResRemove = " + numResRemove);
				  //System.out.println("NumResAllowed = "+ numResAllowed);
				  // Find Bremove having the lowest stimulation biS 
				  double min = Double.MAX_VALUE;
				  int remove =-1;
				  for(int i=0; i<B.length; i++){
					  /*
					   * Hay que comprobar que no estï¿½ ya borrado.
					   */
					  boolean seguir = true;
					  if(utilborrar!=-1){
						  for(int j=0; j<=utilborrar && seguir; j++){
							  if(i==borrar[j]){ // si esta borrado, no sigo.
								  seguir = false;
							  }
						  }
					  }
					  if(seguir){ // Si estï¿½ en el conjunto B actualmetn, sigo computando...
						  if(clasesB[i]==clases){
							 // System.out.println("Stim que peta");
							  //System.out.println(stim[i]);
							  if(stim[i]<min){
								  min = stim[i];
								  remove = i;
							  }
						  }
					  }
				  }
				  //System.out.println("ResAllocated = "+ resAllocated);
				  //B.print();
				 // System.out.println("Class =" + clases);
				  
				  //System.out.println("Bremove = "+ remove);
				  //System.out.println("resources remove" + resources[remove]);
				  
				  
				  //
				   if(resources[remove] <= numResRemove+1){ // Creo que es asï¿½.
						//  System.out.println("Elimino el clone = "+remove);
						  //B.remove(remove);
					   utilborrar++;
						 borrar[utilborrar] = remove;
						 
						 /* for(int i=0; i<B.size(); i++){
							  if(i!=remove){
								  C.add(B.get(i));
							  }
						  }*/
						 //System.out.println("Bsize = " + B.size());
						  //B = new PrototypeSet(C);
						  // Apunto que tengo que borrar... SE BORRA A POSTERIORI
						  if(recursosRemovidos == -1)
							  resAllocated = resAllocated - resources[remove];
						 else{
							  resAllocated = resAllocated - recursosRemovidos;
						  }
						  //System.out.println("Bsize tras borrar = " + B.size());
						  
						  /* Recalculamos los stim y resources PORQUE hemos eliminado un clone */
						  /*
						  for(int i=0; i< B.size(); i++){
								 // B.get(i).print();
								  stim[i] = stim(agk,B.get(i));
								  
								  // Calculate the min and max Stim
								  if(stim[i] < minStim){
									  minStim = stim[i];
								  }
								  
								  if(stim[i] > maxStim){
									  maxStim = stim[i];
								  }
							  }
							  
							  
							  /***************/
							/*  
							  for(int i=0; i<B.size() ; i++){
								 
								  if(B.get(i).getOutput(0) == agk.getOutput(0)){
									  stim[i] = (stim[i]- minStim) / (maxStim-minStim); 
								  }else{
									  stim[i] = 1. - (stim[i]- minStim) / (maxStim-minStim); 
								  }  
								  
								  resources[i] = stim[i] * this.ClonalRate*1.;
								  
							  }
							  */
					  }else{
						 // System.out.println("HOOOOOOOOOOOLA");
						  recursosRemovidos = resources[remove]; // me quedo con ï¿½l antes.
						  resources[remove] = resources[remove] - numResRemove;
						  
					  }
					 // System.out.println("resources remove2: " + resources[remove]);
					  
			 // }// End if
				   
				   iter--;
		  }//End While Interno
		  
		  clases = clases +1.;
		  
		  //System.out.println("CLASES = "+ clases);
	  }
	  
	  //PrototypeSet C = new PrototypeSet();
	  String [][] C = new String [B.length - utilborrar][agk.length];
	  double clasesC []= new double[B.length - utilborrar];
	  int almacenados =0;
	  if(utilborrar!= -1){
		 //  C = new PrototypeSet(B.size()-utilborrar);
		  
		  boolean seguir = true;
		  for (int i=0; i< B.length; i++){
			  seguir = true;
			  for(int j=0; j < utilborrar && seguir; j++){
				if(i == borrar[j]) seguir = false;  
			  }
			  
			  if(seguir){
				  C[almacenados] = B[i];
				  clasesC[almacenados] = clasesB[i];
				  almacenados++;
			  }
		  }
	  }else{
		  C = B.clone();
		  clasesC = clasesB.clone();
	  }
	  //System.out.println("Borrados " + utilborrar +" C size = " +C.size());
  
	  Pair <String[][], double[]> salida = new Pair <String[][], double[]>(C,clasesC);
	  
	  return salida;
  }
  
  /*
   * Update of immne Memory
   */
  
  
  public Pair<String[][], double[]> UpdateMemory(String[][] binario, double binarioClass[],int matchIndex, String [] Antigen, double claseAntigen, String [] most, double mostC, String [] Match, double claseMatch){
	  double CandStim, MatchStim, CellAff;
	  String Immune[][] = new String [binario.length][binario[0].length];
	  double classI[] = new double [binarioClass.length];
	  
	  
	  
	  CandStim = stimString2(Antigen,most);
	  MatchStim = stimString2(Antigen, Match);
	  CellAff = stimString2(Match, most);
	// System.out.println("Cell Aff = " + CellAff);
	 // System.out.println("Cand Stim = " + CandStim + ", Match Stim = " + MatchStim );
	  
	 // Mmatch.print();
	 // Bcandidate.print();
	  if( CandStim > MatchStim){
		 
		  
		  if(CellAff > (this.alpha*AS[(int)claseAntigen]) ){
			  //System.out.println("Replacement");
			  //Memory Replacement
			  binario[matchIndex] = most;
			  binarioClass[matchIndex] = mostC;
			  classI = binarioClass.clone(); // Lo copio idï¿½ntico.
			  Immune = binario.clone();
		  }else{
			//  System.out.println("Updated");
			  Immune = new String[binario.length+1][binario[0].length];
			  classI = new double[binario.length+1];
			  
			  for(int i=0; i< binario.length; i++){
				  Immune[i] = binario[i].clone(); //Hard-copy
				  classI[i] = binarioClass[i];
			  }
			  Immune[binario.length] = most; // Al final aï¿½ado el mï¿½s estimulado
			  classI[binario.length] = mostC;
		  }
	  }else{
		  classI = binarioClass.clone(); // Lo copio idï¿½ntico.
		  Immune = binario.clone();
	  }
	  
	  Pair <String[][], double[]> salida = new Pair <String[][], double[]>(Immune,classI);
	
	 // System.out.println("IM size 2 = " + IM.size());
	  return salida;
	  
  }
  /**
   * Generate a reduced prototype set by the PSCSAGenerator method.
   * @return Reduced set by PSCSAGenerator's method.
   */
  
  
  public PrototypeSet reduceSet()
  {
	 
	  /**********************************************************************/
	  
	  System.out.print("\nThe algorithm  PSCSA is starting...\n Computing...\n");
	  
	  //System.out.println("Number of prototypes, result set = "+numberOfPrototypes+ "\n");
	  
	  //System.out.println("Reduction %, result set = "+((trainingDataSet.size()-numberOfPrototypes)*100)/trainingDataSet.size()+ "\n");

	  /**********************************************************************/
	  
	  //Algorithm


	  // Initialization process. One prototype of each class.

	  trainingDataSet.randomize(); // Desordeno.
	  String [][] binarioTrain = trainingDataSet.to8GrayString();
	  double TrainingClases [] = new double[trainingDataSet.size()];
	  for(int i=0; i<trainingDataSet.size(); i++){
		  TrainingClases[i] = trainingDataSet.get(i).getOutput(0);
	  }
	  
	  PrototypeSet IM = new PrototypeSet(); // Inmune Memory.
	  Prototype agk = new Prototype(); // Antigen agk
	  Prototype Mmatch = new Prototype(); // Mmatch
	  PrototypeSet B = new PrototypeSet(); // B cell
	  PrototypeSet B1, B2;
	  PrototypeSet Best = new PrototypeSet();
	  double BestFitness = Double.MIN_VALUE;
	  
	  
	  for(int i=0; i<this.numberOfClass; i++){
		  PrototypeSet clase = trainingDataSet.getFromClass(i);
		  if(clase.size() >0)
			  IM.add(clase.getRandom());
	  }
	  
	  
	//  System.out.println("Binary Training set");
	  String [][] binario = IM.to8GrayString();
	  double binarioClass [] = new double[IM.size()];
	    
	 // IM.print();
	  
	 // System.err.println("acierto en training Inicial " + KNN.classficationAccuracy(IM,trainingDataSet,1)*100./trainingDataSet.size());
	  
	  
	  int iter =0;
	  //while(IM.size() <= this.numberOfPrototypes || iter > 10000){
		  // Clone Generation.
		 // System.out.println("IM size = " + IM.size());
		  
	   // Inicializaciï¿½n
	      binario = IM.to8GrayString();
		  for(int i=0; i<IM.size(); i++){
			  binarioClass[i] = IM.get(i).getOutput(0);
		  }
		  
		  for(int p=0; p< trainingDataSet.size(); p++){ // For each training example.
		  
			  //agk = new Prototype(trainingDataSet.get(p)); //Cogemos un antï¿½geno,
			  									
			   //System.out.println("Antigeno de la iter = "+ iter);
			  //agk.print();
			  
			 /* for(int i=0; i< IM.size(); i++){
				  IM.get(i).setIndex(i);
			  }*/
			  
			  
			  /* Buscar el mas cercano en el conjunto Inmmune memory */
			  double Cercano = Double.MIN_VALUE;
			  int indexCercano = -1;
			  double stim;
			  for(int i=0; i< binario.length; i++){
				 stim = stimString(binario, i, binarioTrain, p); 
				 //stim = stimString2(binario[i],binarioTrain[p]);
				 // System.out.println("Stim = " + stim);
				  if((stim > Cercano) && (binarioClass[i]==TrainingClases[p])){
					 Cercano = stim;
					 indexCercano = i;
				 }
			  }
			  
		//	  System.out.println("El mas cercano estï¿½ a un stim de = "+ Cercano);
			 
			  //Mmatch = new Prototype( IM.get(indexCercano));  // Cogo el mï¿½s cercano!!
			  String Match [] = binario[indexCercano].clone();
			  double claseMatch = binarioClass[indexCercano];
			  String Antigen [] = binarioTrain[p].clone();
			  double claseAntigen = TrainingClases[p];
			  
			//  System.out.println("Mmatch de la iter = "+ iter);
			  //Mmatch.print();
			  // Proliferation-I
			  double ClasesB [] = null;
			  
			   Pair <String [][], double[] > salida = HyperMutation(Match,claseMatch,Antigen,claseAntigen,1);
			   String Bstring [][] = salida.first();
			   ClasesB = salida.second().clone();
			   
	
				  
			 // System.out.println("IM size ="+ IM.size());
	//		 System.out.println("B cells ="+ Bstring.length);
			// B.print();
	      //		 B = new PrototypeSet(ResourceAllocation(agk, B).clone());
			 //System.out.println("B cells Resource ="+ B.size());
			 Pair <String [][], double[] > salidaR = ResourceAllocation (Bstring, ClasesB, Antigen, claseAntigen);
			 Bstring = salidaR.first().clone();
			 ClasesB = salidaR.second().clone();
			 
		
			  
			//System.out.println("B cells Resource ="+ Bstring.length);
		  // Proliferation-II
		
					
			  boolean stoppingCriteria = false;
			 int numProlifer = 0;
			 while(!stoppingCriteria ){ //&& numProlifer <5 
				 
				 //System.out.println("Bucle");
				 //for(int i=0;i< B.size();i++) B.get(i).setIndex(i); //Stablish the index
				 //Mmatch = B.nearestTo(agk);
				 
				// B.print();
				 
				 //System.out.println("Mmatch");
				 //Mmatch.print();
				 
				 //HAY que HIPERMUTAR CADA UNO DE LOS CLONES DE B.
			
				 String [][][]result = new String [Bstring.length][][];	
				 double clasesResult [][] = new double[Bstring.length][];
				 int tamanio = 0;
				 for(int i=0; i<Bstring.length; i++){
					 Pair <String [][], double[] > salida2 = HyperMutation(Bstring[i],ClasesB[i],Antigen,claseAntigen,2);
					 result[i] = salida2.first().clone();
					 clasesResult[i] = salida2.second().clone();
		
					 tamanio += result[i].length;
				 }
				 // Ahora los uno en una sola matriz de nuevo.
				 
				
				 
				 String Hyper [][] = new String [tamanio][];
				 double ClasesH [] = new double[tamanio];
				 
				 int cont = 0;
				 int cont2 = 0;
				 for (int i=0; i<Bstring.length; i++){
					 for(int j=0; j< result[i].length; j++){
						 Hyper[cont] = result[i][j];
						 cont++;
					 }
					 for(int j=0; j< clasesResult[i].length; j++){
						 ClasesH[cont2] = clasesResult[i][j];
						 cont2++;
					 }
				}
				  
		
				 
			
				  
				 //System.out.println("Hypermutados, B cells = " + Hyper.length);
				 salidaR = ResourceAllocation (Hyper, ClasesH, Antigen, claseAntigen);
				 
				 Bstring = salidaR.first().clone();
				 ClasesB = salidaR.second().clone();
				 
			/*	 for(int j=0; j< ClasesB.length; j++){
				  System.out.println(ClasesB[j]);
			  }*/
				 
				 
				// System.out.println("Resource Hypermutados, B cells = " + Bstring.length);
				 //B =new PrototypeSet(ResourceAllocation(agk, hypermut).clone());
			  //   System.out.println("Hyper Resour cells ="+ B.size());
			     
			     //Stopping?
				 cont =0;
				 cont2 = 0;
				 for(int i=0; i< Bstring.length; i++){
					 if(ClasesB[i] == claseAntigen){
						 cont++;
					 }else{
						 cont2++;
					 }
					 
				 } // Contamos para poder inicializar
				 
				 double sum1=0, sum2=0;
				 
				 if(Bstring.length >0){
					 String [][] B1s = new String[cont][Bstring[0].length];
					 String [][] B2s = new String[cont2][Bstring[0].length];
					 cont =0;
					 cont2 = 0;
					 for(int i=0; i< Bstring.length; i++){
						 if(ClasesB[i] == claseAntigen){
							 B1s[cont] = Bstring[i];
							 cont++;
						 }else{
							 B2s[cont2] = Bstring[i];
							 cont2++;
						 }
						 
					 }
				 
				
				    // B1 = B.getFromClass(agk.getOutput(0)); // Is the set with the same class like agk
				     //B2 = B.getAllDifferentFromClass(agk.getOutput(0)); // All differents
				     
				    // System.out.println("B1 size ="+ B1.size());
				     // System.out.println("B2 size ="+ B2.size());
				     
				     
				     for (int i =0; i<B1s.length; i++){
				    	 sum1 += stimString2(Antigen,B1s[i]);
				     }
				     //if(B1.size()!=0)
				    	// sum1 /= B1.size();
				     
				     for (int i =0; i<B2s.length; i++){
				    	 sum2 += stimString2(Antigen,B2s[i]);
				     }
			     
				 }
			     //if(B2.size()!=0)
			    	// sum2 /= B2.size();
			     
			     //System.out.println("sum 1 = " + sum1);
			     //System.out.println("sum 2 = " + sum2);
			     stoppingCriteria = (sum1-sum2) > this.STIMULATION_THRESHOLD;
			    		     
			     numProlifer++; // Limito el nï¿½mero de proliferaciones.
			     
			     // if(sum2 ==0){ stoppingCriteria = true;}
			    // agk = trainingDataSet.getRandom();
			 } //End proliferation-II
		     
			 
			 //System.out.println("B final =" + Bstring.length);
			 int mostStimulated = -1;
	 		 double maximumStimulation = Double.MIN_VALUE;
			 for(int i=0; i< Bstring.length;i++ ){
	 			 double stimB = stimString2(Antigen,Bstring[i]) ;
	 			 //System.out.println("Stim B =" + stimB);
	 			 if(stimB> maximumStimulation && ClasesB[i] == claseAntigen ){ //
	 				 mostStimulated = i;
	 				 maximumStimulation = stimB;
	 			 }
	 		 }
		     
	 		// System.out.println("El candidato es= ");
	 		 //B.get(mostStimulated).print();
	 		 //System.out.println("EL Mmatch es =");
	 		 //Mmatch.print();
	 		 

		  
	 		 //if(B.size()!=0)
		    	// IM = UpdateMemory(IM, agk, B.get(mostStimulated), Mmatch);
	 		 Pair <String[][], double[]> update = UpdateMemory(binario, binarioClass, indexCercano, Antigen,claseAntigen, Bstring[mostStimulated], ClasesB[mostStimulated],Match,claseMatch );
		
	 		 
	 		 binario = new String[update.first().length][update.first()[0].length];
	 		 binario = update.first().clone();
	 		 
	 		 binarioClass = new double[update.second().length];
	 		 binarioClass = update.second().clone();
			 //System.out.println("Fin Iter");
			 
	 		 IM = new PrototypeSet();
			  IM.toPrototypeSet(binario, binarioClass);
			  double fitness = KNN.classficationAccuracy(IM,trainingDataSet,1)*100./trainingDataSet.size();
		      
			 // System.out.println("Fitness = " + fitness);
			  if (fitness > BestFitness){
		    	  BestFitness = fitness;
		    	  Best = new PrototypeSet(IM);
		      }
			 
		      
			  
		  //}
		  
		  
		 // System.out.println("Iteraciones =" + p);
		 // System.out.println("IM size = " + binario.length);
	 
	  } // End for "for each example.."
	 
		  
		  /*
		   * Reconstruyo el conjunto..
		   * 
		   * 
		   */
		  /*for(int i=0; i< binarioClass.length; i++){
			  System.out.println(binarioClass[i]);
		  }*/
		 /* IM = new PrototypeSet();
		  IM.toPrototypeSet(binario, binarioClass);
	 
		  System.err.println("\n% de acierto en training " + KNN.classficationAccuracy(IM,trainingDataSet,1)*100./trainingDataSet.size());
	  System.out.println("\nReduction %, result set = "+((trainingDataSet.size()-IM.size())*100)/trainingDataSet.size()+ "\n");
*/
		  	 
		  System.err.println("\n% de acierto en training " + KNN.classficationAccuracy(Best,trainingDataSet,1)*100./trainingDataSet.size());
	  System.out.println("\nReduction %, result set = "+((trainingDataSet.size()-Best.size())*100)/trainingDataSet.size()+ "\n"); 
		  
	  
	  //IM.print();
		 return Best;
  }
  
  /**
   * General main for all the prototoype generators
   * Arguments:
   * 0: Filename with the training data set to be condensed.
   * 1: Filename which contains the test data set.
   * 3: Seed of the random number generator.            Always.
   * **************************
   * 4: .Number of neighbors
   * 5:  Swarm Size
   * 6:  Particle Size
   * 7:  Max Iter
   * 8:  C1
   * 9: c2
   * 10: vmax
   * 11: wstart
   * 12: wend
   * @param args Arguments of the main function.
   */
  public static void main(String[] args)
  {
      Parameters.setUse("PSCSA", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      PSCSAGenerator.setSeed(seed);
      
      int k = Parameters.assertExtendedArgAsInt(args,3,"number of neighbors", 1, Integer.MAX_VALUE);
      int swarm = Parameters.assertExtendedArgAsInt(args,4,"swarm size", 1, Integer.MAX_VALUE);
      int particle = Parameters.assertExtendedArgAsInt(args,5,"particle size", 1, Integer.MAX_VALUE);
      int iter = Parameters.assertExtendedArgAsInt(args,6,"max iter", 1, Integer.MAX_VALUE);
      double c1 = Parameters.assertExtendedArgAsInt(args,7,"c1", 1, Double.MAX_VALUE);
      double c2 =Parameters.assertExtendedArgAsInt(args,8,"c2", 1, Double.MAX_VALUE);
      double vmax =Parameters.assertExtendedArgAsInt(args,9,"vmax", 1, Double.MAX_VALUE);
      double wstart = Parameters.assertExtendedArgAsInt(args,10,"wstart", 1, Double.MAX_VALUE);
      double wend =Parameters.assertExtendedArgAsInt(args,11,"wend", 1, Double.MAX_VALUE);
      
      //String[] parametersOfInitialReduction = Arrays.copyOfRange(args, 4, args.length);
     //System.out.print(" swarm ="+swarm+"\n");
      
      
      PSCSAGenerator generator = new PSCSAGenerator(training, k,swarm,particle,iter, 0.5,0.5,1);
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
