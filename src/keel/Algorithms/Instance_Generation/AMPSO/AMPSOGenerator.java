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
	PSO.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-8-2008
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.AMPSO;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import java.util.*;

import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;

import org.core.*;

import org.core.*;

import java.util.StringTokenizer;

import java.util.LinkedList;

/**
 * 
 * @param  k
 * @param  MaxIter
 * @param SwarmSize
 * @param c1, c2, c3
 * 
 * @author Isaac Triguero
 * @version 1.0
 */
public class AMPSOGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  
  // We need the variable K to use with k-NN rule
  private int k;
  private int SwarmSize; 
  private int MaxIter; 
  private double C1;
  private double C2;
  private double C3;
  private double VMax;
  private double Winertia;
  private double Xfactor;
  private double ProbR;
  private double ProbD;
  
  protected int numberOfPrototypes;  // Swarmsize is the percentage
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;
  protected int numberOfClass;
  
  /**
   * Build a new PSOGenerator Algorithm
   *
   */
  
  public AMPSOGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion,  int iteraciones, double c1, double c2, double c3, double vmax, double inertia, double x, double pr, double pd)
  {
      super(_trainingDataSet);
      algorithmName="AMPSO";
      
      this.k = neigbors;
      this.SwarmSize = poblacion;
      this.MaxIter = iteraciones;

   // the number of inputs of each prototype
		// is the same that the number of attributes. 

      this.C1 = c1;
      this.C2 = c2;
      this.C3 = c3;
      this.VMax = vmax;
      this.Winertia = inertia;
      this.Xfactor = x;
      this.ProbR =pr;
      this.ProbD = pd;
  }
  


  /**
   * Build a new PSOGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public AMPSOGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="PSO";
      this.k =  parameters.getNextAsInt();
      this.SwarmSize =  parameters.getNextAsInt();
      this.MaxIter =  parameters.getNextAsInt();
      this.C1 = parameters.getNextAsDouble();
      this.C2 = parameters.getNextAsDouble();
      this.C3 = parameters.getNextAsDouble();
      this.VMax = parameters.getNextAsDouble();
      this.Winertia = parameters.getNextAsDouble();
      this.Xfactor = parameters.getNextAsDouble();
      this.ProbR = parameters.getNextAsDouble();
      this.ProbD = parameters.getNextAsDouble();
      
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
    
      this.numberOfPrototypes = getSetSizeFromPercentage(SwarmSize);
      System.out.print("\nIsaac dice:  " + k + " Swar= "+SwarmSize+ " ProbD=  "+ ProbD + " Maxiter= "+ MaxIter+" Winertia=  "+Winertia+ "\n");
      //numberOfPrototypes = getSetSizeFromPercentage(parameters.getNextAsDouble());
  }
  

  
  
  /**
   * Return the local-fitness of the particle.
   * @param Population
   * @param particleIndex
   * @return
   */
  
  
  protected double fitness(PrototypeSet Population, int particleIndex){
	  double fitness =0.0;
	  PrototypeSet g = new PrototypeSet();
	  PrototypeSet b = new PrototypeSet();
	  
 		/*	suponte que en {g} tenemos tres patrones. D_{g,i} es la distancia de la partï¿½cula al patrï¿½n i-ï¿½simo. 
		La expresiï¿½n G_f hace la sumatoria de todas ellas a la inversa. No tiene dificultad alguna.
		Es un fitness local que se calcula por partï¿½cula. 
		Una partï¿½cula clasificarï¿½ correcta e incorrectamente una seria de patrones 
		(aquellos que la tienen de vecina mï¿½s cercana de la misma clase, los primeros; y de distinta clase, los segunndos). 
		Esos son el conjunto {b} y el {g}. Se calcula la distancia a cada uno de los ptarines para obtener la 
		sumatorias de la derecha de la expresiï¿½n (3) y ya calcula el total fitness dependiendo si {b} o {g} estï¿½n vacï¿½os o no.
		 */
	  
	  // Respecto del Training Set. Conjunto de Patrones
	  for(int i = 0; i< trainingDataSet.size(); i++){
			  Prototype nearest = Population.nearestTo(trainingDataSet.get(i));
		    // Compruebo si el prototipo mï¿½s cercano es particleIndex
	    	  if(nearest.equalsInputs(Population.get(particleIndex))){ // it must be the particle index.
				  if(trainingDataSet.get(i).getOutput(0) == Population.get(particleIndex).getOutput(0)){
					  g.add(trainingDataSet.get(i)); 
				  }else{
					  b.add(trainingDataSet.get(i));
				  }
			  }
	  }
	  
	  
	  if( g.size() ==0 && b.size() == 0){ // case 1.
		  fitness =0;
	  }else{
		  double Gf=0., Bf=0.;
		  
		  for(int j=0; j< g.size() ; j++){
			  Gf += 1./ (1.0 + Distance.d(g.get(j), Population.get(particleIndex)));
		  }

		  for(int j=0; j< b.size() ; j++){
			  Bf += 1./ (1.0 + Distance.d(b.get(j), Population.get(particleIndex)));
		  }
		  
		  //System.out.println("Gf = " + Gf + " Bf = "+ Bf);
		  
		  if( b.size() == 0){
			  fitness = Gf / trainingDataSet.size() + 2.0;
			  //fitness = Gf / Population.size() + 2.0;
			  //System.out.println("Caso b");
		  }else{
			  fitness = (Gf - Bf)/(Gf+Bf) + 1.0;
			  //System.out.println("Caso c");
		  }
		  
		  
	  }
	  
	  //if(fitness >0 && fitness <2)
	  //System.out.println("Fitness = "+ fitness);
	  return fitness;	  
  }
  
  
  /**
   * Generate a reduced prototype set by the AMPSOGenerator method.
   * @return Reduced set by AMPSOGenerator's method.
   */
  
  
  @SuppressWarnings({ "static-access", "unchecked" })
public PrototypeSet reduceSet()
  {
	  PrototypeSet result = new PrototypeSet();
	  PrototypeSet Population  = new PrototypeSet();
	  PrototypeSet bestPosition = new PrototypeSet();
	  PrototypeSet AttractionCenter = new PrototypeSet(); // Each prototype will be the atracttion center to the Population 
	  PrototypeSet RepulsionCenter = new PrototypeSet();
	  PrototypeSet nextPosition = new PrototypeSet();
	  PrototypeSet nominalPopulation  = new PrototypeSet();
	  
	  double success_rate; // initial, there is no success.
	  double aleatorio;
	  
	  PrototypeSet Velocities = new PrototypeSet();
	  ArrayList LocalFitness = new ArrayList();
	  ArrayList bestLocalFitness = new ArrayList();
	  ArrayList SocialFactor = new ArrayList();

	  double chance_reproduction = 0;
	  double chance_deletion = 0;
	  
	  System.out.print("\nThe algorithm is starting...\n Computing...\n");
	  System.out.println("Number of prototypes, result set = "+numberOfPrototypes+ "\n");
	  System.out.println("Reduction %, result set = "+((trainingDataSet.size()-numberOfPrototypes)*100)/trainingDataSet.size()+ "\n");

	  //Algorithm
	  // First, we create the population
	  // like a prototypeSet
	  //Population = new PrototypeSet(selecRandomSet(this.numberOfPrototypes,true)); 	
	  PrototypeSet clases[] = new PrototypeSet [this.numberOfClass];
	  for(int i=0; i< this.numberOfClass; i++){
		  clases[i] = new PrototypeSet(trainingDataSet.getFromClass(i));
	  }
	
	  // We have numberofPrototypes/ numberOfclass for each class.
	  
	  int eachClass = this.numberOfPrototypes/this.numberOfClass +1;
	  System.out.println("De cada clase "+ eachClass);
	  
		  for(int j=0; j< this.numberOfClass; j++){
			  for(int i=0; i< eachClass;i++){
				  if(clases[j].size()!=0){
					  Prototype aux = new Prototype(clases[j].getRandom()); 
					  Population.add(aux);
				  }
			  }
		  }
	
	  
	  //Population.print();
	  //Initially best Position is the initial position.
	  bestPosition = new PrototypeSet(Population);
	  this.numberOfPrototypes = Population.size();
	  
	  nominalPopulation = new PrototypeSet();
	  nominalPopulation.formatear(Population);
		  
	  success_rate  = accuracy(nominalPopulation,trainingDataSet);
	  
	 // Population.print();
	 // System.out.println("Accuracy initial set (Aleatory) = "+ success_rate);	  // stablish the initial success rate.

	  
	  //Initial local fitness. && Social factor
	  for(int i= 0; i< this.numberOfPrototypes; i++){
		  LocalFitness.add(i, fitness(Population, i));
		   double value = 1/( (Double)LocalFitness.get(i) + 1.0);
		   SocialFactor.add(i,value); 
	  }
		  //Initially best Local Fitness is the initial local fitness.
	  bestLocalFitness = (ArrayList) LocalFitness.clone();
	  
 
	   
	  //Initial Velocity.
	  
	  double output[] = new double[1];
	  Arrays.fill(output,0);
	  
	  for(int i = 0; i< this.numberOfPrototypes; i++){
		 // Arrays.fill(inputs, RandomGenerator.Randdouble(-VMax, VMax));
		  double inputs[] = new double [trainingDataSet.get(0).numberOfInputs()];
		  
		  for(int j=0; j<inputs.length; j++){
			  inputs[j] = RandomGenerator.Randdouble(-VMax, VMax);
		  }
		  Prototype aux = new Prototype(inputs,output);
		  Velocities.add(i, aux);
	  }
	  
	 // Velocities.print();
	 
	  // Atracction center and repulsion Initial
	  // to avoid possible problems we initial atraction and repusion withe the same Population.
	  AttractionCenter = Population.clone();
	  RepulsionCenter = Population.clone();
	  
		// Main loop

	   for(int iter=0; iter< MaxIter && success_rate != 100.0; iter++){ 
		   
		 	//Check for particle reproduction and deletion.
		 
		   boolean clean[] = new boolean [this.numberOfPrototypes];
		   boolean reprod[] = new boolean [this.numberOfPrototypes];
		   Arrays.fill(clean, false);
		   Arrays.fill(reprod, false);
		   
		   for( int i = 0; i< this.numberOfPrototypes; i++){
			   aleatorio =RandomGenerator.Randdouble(0, 1);
			   
			  if( (Double) LocalFitness.get(i) == 0){
				  chance_deletion= (ProbD*iter/ MaxIter);
				  //System.out.println("Chance of deletion = "+ chance_deletion);
			  	 			  	  
			  	if(aleatorio < chance_deletion){
			  		//System.out.println ( "Need to Clean the particle i = " + i);
			  		clean[i] = true;
			  	}
			  }else{
				  chance_reproduction= ProbR/((Double)LocalFitness.get(i)*100);
				  //System.out.println( "Chance reprod =" + chance_reproduction);
				  //System.out.println("Local fitness = " +(Double)LocalFitness.get(i) );
				  //System.out.println ( "aleatorio = " + aleatorio);
				  if(aleatorio < chance_reproduction){
					  //System.out.println ( "Need to reproduct the particle i = " + i);
					  	reprod[i] = true;
				  }
			  }
		   }
		   
		   for(int i = 0; i< this.numberOfPrototypes; i++){
			   if(clean[i]){
				   Population.remove(i);
				   this.numberOfPrototypes--;
			   }else if(reprod[i]){
				
					  Prototype child = Population.get(i); // Position of the parent 
					  Population.add(child);
					  bestPosition.add(Population.size()-1,child);
					  bestLocalFitness.add(Population.size()-1, LocalFitness.get(i));
					  //Velocities randomized.
					  double inputs[] = new double[trainingDataSet.get(0).numberOfInputs()];
					  Arrays.fill(inputs, RandomGenerator.Randdouble(-VMax, VMax));
					  Prototype aux = new Prototype(inputs,output);
					  Velocities.add(Population.size()-1, aux);  
					  AttractionCenter.add(Population.size()-1, child);
					  RepulsionCenter.add(Population.size()-1, child);
					  LocalFitness.add(Population.size()-1, LocalFitness.get(i));
					  SocialFactor.add(Population.size()-1, SocialFactor.get(i));
			   }
		   }
		   this.numberOfPrototypes = Population.size(); // update the swarm size, because it's possible there was reproduction
		   
		   
		   
		   // Calculate which particles are in the competing and non-competing sets for every class
		   /*

		    * En el punto 4(b), tienes que calcular los conjuntos competitivos y no competitivos para cada clase. 
		    * Yo, por ejemplo, tendrï¿½a un vector de booleanos para cada conjunto que indica si pertenece o no al conjunto competitivo o no 
		    * competitivo. Para ver si pertenecen, te ciï¿½es a lo que pone en la secciï¿½n 3.4: "Si un prototipo clasifica mal a otro,
		    * se incluye en el set no-competing". Esto se traduce a que si el prototipo mï¿½s cercano a
		    *  uno dado es de otra clase, ï¿½ste se incluye en el no-competing (pones su bit a true). IDEM para el set competing.
		    */
		   
		   boolean competing[][] = new boolean [this.numberOfClass][];
		   boolean nonCompeting[][] = new boolean[this.numberOfClass][];
		   
		   	   
		   for(int i=0; i< this.numberOfClass; i++){
			   competing[i] = new boolean[this.numberOfPrototypes];
			   nonCompeting[i] = new boolean[this.numberOfPrototypes];
		   }
		   
		   
		   // stablish the index, previously.
		   
		   for(int i=0; i< this.numberOfPrototypes; i++){
			   Population.get(i).setIndex(i);
		   }
		   
		// Competing.
		   for(int i=0; i< this.numberOfClass; i++){
			   Arrays.fill(competing[i],false);
			   
			   PrototypeSet patterns = trainingDataSet.getFromClass(i);
			
			   for(int j=0; j< patterns.size() ; j++){
					   Prototype clasificado = Population.nearestTo(patterns.get(j));
					   
					   if(clasificado.getOutput(0) == i){ // si es de la misma clase...
						  competing[i][clasificado.getIndex()] = true;
					   }
			   }
     	   }
		   
		   
			// non-Competing.
		   for(int i=0; i< this.numberOfClass; i++){
			   Arrays.fill(nonCompeting[i],false);
			   
			   PrototypeSet patterns = trainingDataSet.getAllDifferentFromClass(i);
			
			   for(int j=0; j< patterns.size() ; j++){
					   Prototype clasificado = Population.nearestTo(patterns.get(j));
					   
					   if(clasificado.getOutput(0) == i){ // si es de la misma clase...
						  nonCompeting[i][clasificado.getIndex()] = true;
					   }
			   }
     	   }
		   
		   
		   // antiguo calculo. probablemente MAL
		   /*
			   for(int j= 0; j<this.numberOfPrototypes;j++){
				  
				   Prototype one = new Prototype();
					  one =  trainingDataSet.nearestTo(Population.get(j));
				   //int clase = (int) one.getOutput(0);
				   int clase = (int) Population.get(j).getOutput(0);
				   
				   if (one.getOutput(0) !=  Population.get(j).getOutput(0)){
					   nonCompeting[clase][j] = true;
					   //competing[clase][j] = false;
				   }else{
					   competing[clase][j] = true;
					   //nonCompeting[clase][j] = false;
				   }
			   }
			 */  

		   
		   /*
	
			   for(int j= 0; j<this.numberOfClass;j++){
				   for(int i=0; i< this.numberOfPrototypes; i++){
					   if(competing[j][i]){
						   System.out.println("Competing clase " +j + " es" + i);
					   }
					   if(nonCompeting[j][i]){
						   System.out.println("Non Competing clase " +j + " es" + i);
					   }
				   }
				  
			   }
	*/	   
			   
		   // For each particle.
			   //Calculate next Position.
			   nextPosition = new PrototypeSet(Population);
			   
		   for(int i=0; i< this.numberOfPrototypes; i++){
			   int particleClass= (int)Population.get(i).getOutput(0);
			   
			    // Calculate Local fitness.
			   double value2 = fitness(Population, i);
			   LocalFitness.set(i, value2);
			 
			   		   
			   if(value2 > (Double)bestLocalFitness.get(i)){
				   bestLocalFitness.set(i,value2);
			   }
			   
			   
			   //Calculate Social Factor.
			   //System.out.println("Swarm = " + SwarmSize);
			   double value = 1/( (Double)bestLocalFitness.get(i) + 1.0);
			   SocialFactor.set(i,value);
			   
			   //Find the closet particle in the non-competing set for particle class.
	   			/*
	   			 * Justo despuï¿½s, en el punto 4(c) iii; que como ves se hace para cada partï¿½cula, se localiza el protitpo mï¿½s cercano
	   			 *  a esta partï¿½cula que pertenezca al conjunto de no competitivos. Esto es, coges tu vector de booleanos no-competing 
	   			 *  y calculas el prototipo mï¿½s cercano de aquellos cuyo bit estï¿½ a true. ï¿½ste es tu centro de atracciï¿½n
	   			 *  (lï¿½gico, la partï¿½cula se mueve hacia un lugar donde pueda evitar una mal clasificaciï¿½n, y ï¿½ste serï¿½ el mï¿½s cercano).
	   			 *   IDEM para el punto iv.
	   			 */			  
			   
			   double ClosestNonCompeting = Double.MAX_VALUE;
			   int CNCindex =i;
			   double ClosestCompeting = Double.MAX_VALUE;
			   int CCindex =i;			   
			   double dist =0.0;
			   
			   for(int k=0; k < this.numberOfPrototypes; k++){
				   if(i!=k){
					   if(nonCompeting[particleClass][k]){
							 dist =  Distance.d(Population.get(i), Population.get(k));
							 if(dist < ClosestNonCompeting){
								 ClosestNonCompeting = dist;
								 CNCindex = k;
							 }
					   }
					   
					   if(competing[particleClass][k]){
							 dist =  Distance.d(Population.get(i), Population.get(k));
							 if(dist < ClosestCompeting){
								 ClosestCompeting = dist;
								 CCindex = k;
							 }
							 

					   }
				   
				   }
			   }
			   
			  AttractionCenter.set(i,Population.get(CNCindex));
			  RepulsionCenter.set(i,Population.get(CCindex));

			  
			 Prototype speed = new Prototype(Velocities.get(i));
			 //speed.print();
			 speed = speed.mul(Winertia); // w* Vidt
	
			 aleatorio =RandomGenerator.Randdouble(0, 1);
			 Prototype RestaBestParticle = (bestPosition.get(i)).sub(Population.get(i)); // pid - xid
			 RestaBestParticle = RestaBestParticle.mul(aleatorio*this.C1); // C1*Y*(Pid-Xid)
			   
			 speed = speed.add(RestaBestParticle); // add this.
	
			   
			   //-----------
			   
			   Prototype RestaAttraction = AttractionCenter.get(i).sub(Population.get(i)); // aid - xid
			   //RestaAttraction.print();
			   //
			   aleatorio =RandomGenerator.Randdouble(0, 1) ; // Other aleatory.
			   	// C2*Y*sign(aid-Xid) * Sfi
			   Prototype term2 = new Prototype(RestaAttraction.numberOfInputs(),1);
			   double valor;
			   for(int r=0; r< RestaAttraction.numberOfInputs(); r++){
				   
				   valor = this.C2 * aleatorio * (Double)SocialFactor.get(i);
				   if(RestaAttraction.getInput(r) < 0){  // negative sign.
					   valor *= -1; // Negative.
				   }
				   term2.setInput(r, valor);
			   }
			   
			  // RestaAttraction = RestaAttraction.mul(aleatorio*this.C2 * (Double)SocialFactor.get(i)); // C2*Y*(aid-Xid) * Sfi
			   speed = speed.add(term2); // add this.

			   //-----------
			   
			   aleatorio =RandomGenerator.Randdouble(0, 1) ; // Other aleatory.
			   Prototype RestaRepelled = Population.get(i).sub(RepulsionCenter.get(i)); // xid - ridt
			   //RestaRepelled.print();
			   			   Prototype term3 = new Prototype(RestaAttraction.numberOfInputs(),1);
			   for(int r=0; r< RestaRepelled.numberOfInputs(); r++){
				   
				   valor = this.C3 * aleatorio * (Double)SocialFactor.get(i);
				   if(RestaRepelled.getInput(r) < 0){  // negative sign.
					   valor *= -1; // Negative.
				   }
				   term3.setInput(r, valor);
			   }
			   
			   //RestaRepelled = RestaRepelled.mul(aleatorio*this.C3* (Double)SocialFactor.get(i)); // C3*Y*(Xid- rid)*Sfi
			   speed = speed.add(term3); // add this.		   
			
			   //speed.print();
			   
			   speed = speed.mul(this.Xfactor); // Xfactor Constriction factor
			   
			   for(int m=0; m< speed.numberOfInputs(); m++){
				   if(speed.getInput(m) < -this.VMax){
					   speed.setInput(m,-this.VMax);
				   }else if(speed.getInput(m) > this.VMax){
					   speed.setInput(m,this.VMax);
				   }
			   }
			   //speed.applyThresholds();
			   //speed.print();
			   Velocities.set(i,speed); // Update velocites.
			   
			   //speed.print();
			  //
			   Prototype incre = Population.get(i);
			   //incre.print();
			   //speed.print();
			   incre = incre.add(speed);
			   //incre.print();
			   
			   incre.applyThresholds();
			   
			  
			   nextPosition.set(i, incre); // Next Position.
			  
			   //double accuracy= accuracy(nextPosition,trainingDataSet);
			   //System.out.println ("Accurracy = " + accuracy);
			   //nextPosition.applyThresholds();
		   } // end for each particle.
		   
		   
		   //Move the particles
		 //  nextPosition.print();
		//	Population =  new PrototypeSet();
			Population = new PrototypeSet(nextPosition);
			//Success?
		  // Population.applyThresholds();
			  nominalPopulation = new PrototypeSet();
			  nominalPopulation.formatear(Population);
			  
			double accuracy = accuracy(nominalPopulation,trainingDataSet);
			
			//System.out.println ("Accurracy = " + accuracy);
			if(accuracy > success_rate){
				//System.out.println("Best result " + accuracy);
				//System.out.println("Size mejor posititon = "+ SwarmSize);
				success_rate = accuracy;
				bestPosition = new PrototypeSet(Population); // Copy the best.
				 //  bestPosition.print();
			}
	   } // End Main Loop
	  
	   
	
	   //RNN
		  nominalPopulation = new PrototypeSet();
		  nominalPopulation.formatear(bestPosition);
		  
	      result = new PrototypeSet(nominalPopulation);
	   
		  boolean marcas[];
		  marcas = new boolean[result.size()];
		  Arrays.fill(marcas, true);

		  double accuracyInic =KNN.classficationAccuracy(result, trainingDataSet);
		  double accuracy;
		  
		  for(int i=0; i< result.size(); i++){
			  marcas[i] = false; //At the begining you don't think you can elimante.
			  
			  //System.out.println("result size = " + result.size());
			  PrototypeSet leaveOneOut = result.without(result.get(i));
			  //System.out.println("leave size = " + leaveOneOut.size());
			  accuracy = KNN.classficationAccuracy(result, trainingDataSet);
			  
			  if(accuracy > accuracyInic){
				  
				  marcas[i] = true; // we can eliminate
			  }
			  
			  
		  }
		  
		  //Then we create the result set..
		  
		  PrototypeSet clean = new PrototypeSet();
		  
		  for(int i=0; i< marcas.length; i++){
			  if(!marcas[i]){
				  clean.add(result.get(i));
			  }
		  }
 
		  
		 // System.out.println("Final Swarm = " + bestPosition.size());
		
		  /*
		 System.out.println("Accuracy % " +accuracy(result,trainingDataSet));
	   System.out.println("Reduction % " + (100-(result.size()*100.)/trainingDataSet.size()) );
	   
	   System.out.println("Accuracy RNN % " +accuracy(clean,trainingDataSet));
	   System.out.println("Reduction RNN % " + (100-(clean.size()*100.)/trainingDataSet.size()) );	 
	   */
	   
	   return clean; // CLEAN
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
  {
      Parameters.setUse("PSO", "<seed> <Number of neighbors>\n<Swarm size>\n<MaxIter>\n<C1>\n<C2>\n<C3>\n<VMax>\n<Winertia>\n<Xfactor>\n<ProbR>\n<ProbD>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      AMPSOGenerator.setSeed(seed);
      
      int k = Parameters.assertExtendedArgAsInt(args,3,"number of neighbors", 1, Integer.MAX_VALUE);
      int swarm = Parameters.assertExtendedArgAsInt(args,4,"swarm size", 1, Integer.MAX_VALUE);
      int iter = Parameters.assertExtendedArgAsInt(args,5,"max iter", 1, Integer.MAX_VALUE);
      double c1 = Parameters.assertExtendedArgAsInt(args,6,"c1", 1, Double.MAX_VALUE);
      double c2 =Parameters.assertExtendedArgAsInt(args,7,"c2", 1, Double.MAX_VALUE);
      double c3 =Parameters.assertExtendedArgAsInt(args,8,"c3", 1, Double.MAX_VALUE);
      double vmax =Parameters.assertExtendedArgAsInt(args,9,"vmax", 1, Double.MAX_VALUE);
      double winertia = Parameters.assertExtendedArgAsInt(args,10,"winertia", 1, Double.MAX_VALUE);
      double xfactor =Parameters.assertExtendedArgAsInt(args,11,"xfactor", 1, Double.MAX_VALUE);
      double probr =Parameters.assertExtendedArgAsInt(args,11,"probr", 1, Double.MAX_VALUE);
      double probd =Parameters.assertExtendedArgAsInt(args,11,"probd", 1, Double.MAX_VALUE);
      //String[] parametersOfInitialReduction = Arrays.copyOfRange(args, 4, args.length);
     //System.out.print(" swarm ="+swarm+"\n");
      
      
      AMPSOGenerator generator = new AMPSOGenerator(training, k,swarm,iter, c1,c2,c3,vmax,winertia,xfactor, probr,probd );
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
