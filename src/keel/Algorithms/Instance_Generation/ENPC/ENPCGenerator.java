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
	ENPC.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  20-3-09
	Copyright (c) 2009 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.ENPC;

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
 * 
 * @param  numberOfInitialsCentroids
 * @param  k, to use with knn rule in the initialization.
 * @param GradientStep
 * @param Temperature
 * @author Isaac Triguero
 * @version 1.0
 */
public class ENPCGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  
  // We need the variable K to use with k-NN rule
  private int k;
  private int MaxIter;

  //others variables.
  protected int numberOfPrototypes;  
  protected int numberOfClass;
 
  
  /**
   * Build a new ENPCGenerator Algorithm
   *
   */
  
  public ENPCGenerator(PrototypeSet _trainingDataSet, int k, int max)
  {
      super(_trainingDataSet);
      algorithmName="ENPC";
      
      this.k = k;
      this.MaxIter = max;

  }
  

  /**
   * Build a new RSPGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public ENPCGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="ENPC";
      
      this.k = parameters.getNextAsInt();
      this.MaxIter = parameters.getNextAsInt();
       
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
    
      System.out.println("Isaac dice: k= " + this.k );
      System.out.println("Number of class= "+ this.numberOfClass);
  }
  
  
  /**
   * Function regions, see equation (2).
   * @param sj
   * @param S
   * @return
   */

  protected int regions(double sj, PrototypeSet S){
	  int number = 0;
	  
	  for(Prototype p: S){
		  if(p.getOutput(0) == sj){
			  number++;
		  }
	  }
	  
	  return number;
  }
  
  
  /**
   * Function expectation of class sj in the S prototypeSet., see equation (3).
   * @param sj
   * @param S
   * @return
   */

  protected double expectation(double sj, PrototypeSet S){
	  return (S.size()*1.)/regions(sj,S);
  }
  
  
  
  /**
   * Mutation operator.
   * Label each prototype with the most populate class in each region.
   * It is the way of obtain the main class that is usually use when unsupervised learning is applied to supervise classification. 
   * But not onl in a posteriori phase. Remember tha the quality of each prototype depends on the relationship among the number of patterns in 
   * its regions and....,
   * @param output
   */
  
  protected void mutation(PrototypeSet classifier, PrototypeSet V[][]){
	  
	  for(int i=0; i< classifier.size(); i++){
		int max = 1;
		double clasToLabel =classifier.get(i).getOutput(0);
		
		for(int j=0; j< V[i].length; j++){
			if(V[i][j].size() > max || (V[i][j].size() == max && j != classifier.get(i).getOutput(0))){
				max = V[i][j].size();
				clasToLabel = V[i][j].get(0).getOutput(0); // obtain one of class from one prototype.
			}
		}
		 
		//Solo modificamos si no hay empates.
		
		if(max != V[i][(int) classifier.get(i).getOutput(0)].size()){
			//System.out.println("Class original =" +classifier.get(i).getOutput(0));
			classifier.get(i).setFirstOutput(clasToLabel);
			//System.out.println("Class etiquetada =" +classifier.get(i).getOutput(0));
		}
	}
  }
  
  
  
  /**
   * Reproduction operator.
   * Introduce new prototypes in the classifier, each prototype has the opportunity to introduce a new prototype
   * in order to increase its own quality. If a region r1 has two non-empty sets V11 and V12 we need
   * to have another region r2 that contain V12
   * @param output
   */
  
  protected PrototypeSet[][] reproduction(PrototypeSet classifier, PrototypeSet V[][]){
	  
	  
	
	  //Each prototype has the opportunity of introducing a new prototype
	  
	  int initialSize = classifier.size();
	  
	  for(int i=0; i< initialSize; i++){
		  // probability to reproduce. Proportional to number of non-empty sets.
		  int max = Integer.MIN_VALUE;
		  double clase =0.0;
		 
		  //Intento ruleta
		  double limiteRuleta = 0;
		  for(int j=0; j<this.numberOfClass; j++){
			  limiteRuleta+= V[i][j].size();
		  }
		  //System.out.println("Limite Ruleta = " + limiteRuleta);
		  double aleatorio= RandomGenerator.Randdouble(0, limiteRuleta);
		  //System.out.println("Aleatorio = "+aleatorio);
		  
		  double suma =0;
		  boolean encontrado = false;
		  for(int j=0; j< this.numberOfClass && !encontrado; j++){
			  suma += V[i][j].size();
			if( aleatorio < suma){
				clase = j*1.0;
				encontrado =true;
		 	}
			
		 }
		  
		  
		  /*// intento mï¿½ximo
		  for(int j=0; j<this.numberOfClass; j++){
			  if(V[i][j].size() > max || (V[i][j].size() == max && j != classifier.get(i).getOutput(0))){
				  max = V[i][j].size();
				  clase = j*1.0;
				  
				  
			  }
		  }
		 */
	  
		  
		  if(clase != classifier.get(i).getOutput(0)){ // Reproduction execute.
			  //System.out.println("Reproduction " + i + ", " + clase + " pero "+ classifier.get(i).getOutput(0));
			  //V[i][(int)clase].avg().print();
			  classifier.add(V[i][(int)clase].avg()); // Nos quedamos con el centroide
			  
			  // modifcamos los conjuntos de patrones.
			  PrototypeSet nuevoV[][] = new PrototypeSet[classifier.size()][];
			  int j;
			  
			  for( j=0; j< classifier.size()-1; j++){
				nuevoV[j]= new PrototypeSet[this.numberOfClass];
				for(int m=0; m<this.numberOfClass; m++){
					if(V[j][m] != null)
						nuevoV[j][m] = V[j][m].clone(); //Hard.Copy.
					else
						nuevoV[j][m] =null;
				}
			  }
			  nuevoV[j]= new PrototypeSet[this.numberOfClass];
			  
			  nuevoV[classifier.size()-1][(int)clase] = V[i][(int)clase].clone();
			  nuevoV[i][(int) clase] = null;
			  // Lo copiamos de nuevo.
			 
			  V = new PrototypeSet[classifier.size()][];
			  V = nuevoV.clone();
			  
		  }
		  
	  }
	  
	  return V;
  }
  
  
  protected void fight(PrototypeSet classifier, PrototypeSet V[][], double quality[]){
	  
	  //Establish the index
	  for (int i=0;i<classifier.size();i++){
		  	classifier.get(i).setIndex(i);
	  }
	  
	  for(int i=0; i< classifier.size(); i++){
		  PrototypeSet neighbors = KNN.getNearestNeighbors(classifier.get(i), classifier, k);
		  neighbors.remove(classifier.get(i));
		  
		  // ï¿½Con que vecino luchamos?
		 double max= Double.MIN_VALUE;
		  Prototype select = new Prototype();
		  for(Prototype p: neighbors){
			  if((quality[i] - quality[p.getIndex()]) > max ){
				  select = p;
				  max = quality[i] - quality[p.getIndex()];
			  }
		  }

		  double aleatorio= RandomGenerator.Randint(0, 1);
		  double si= classifier.get(i).getOutput(0);
		  if( max < aleatorio){ // Fight
			if (select.getOutput(0) != si ){
				// cooperation
				V[i][(int) si].add(V[select.getIndex()][(int)si]);
				V[select.getIndex()][(int)si] = null;
			} else{
				// competition
				  double limiteRuleta = quality[i]+quality[select.getIndex()];
		  		  aleatorio= RandomGenerator.Randdouble(0, limiteRuleta);
				  				  
		  		  if (aleatorio < quality[i]){
		  			  // Win si
		  			V[i][(int) si].add(V[select.getIndex()][(int)si]);
		  			V[select.getIndex()][(int)si] = null;
		  		  }else{
		  			  // Win si'
		  			V[select.getIndex()][(int)si].add(V[i][(int) si]);
		  			V[i][(int) si] = null;
		  		  }
			} 
		  }
		  
	  }
  }
  
  
  
  /**
   * Lo ï¿½ncio que hace esta funciï¿½n es llevarlo todo a su centroide.
   * @param classifier
   * @param V
   */
  protected void move(PrototypeSet classifier, PrototypeSet V[][]){
	  
	  for( int i=0; i< classifier.size(); i++){
		  int clase = (int) classifier.get(i).getOutput(0);
		  
		  if(V[i][clase].size() >0)
			  classifier.get(i).set(V[i][clase].avg());
     }
  }
  
  /**
   * Die operator. Sirve para eliminar los prototipos que no tienen buena calidad.
   * @param classifier
   * @param V
   */
  protected PrototypeSet die(PrototypeSet classifier, double quality[]){
	  double pDie = 0.0;
	  
	  boolean toClean[] = new boolean [classifier.size()];
     for (int j=0; j< classifier.size(); j++){
    	  if (quality[j] > 0.5){
    		 pDie =0.0;
    	  }else{
    		 pDie = 1- 2*quality[j];
    	  } 
    	  double aleatorio = RandomGenerator.Randdouble(0, 1);
    	 // System.out.println("Prob to die = "+  aleatorio + ", pDie ="+ pDie);
    	  if(aleatorio < pDie){
    		  toClean[j] = true;   
    		 // System.out.println("Muere!!");
    	  }else{
    		  toClean[j] = false;
    	  }
      }
      
     PrototypeSet clean = new PrototypeSet();
     for(int i=0; i< classifier.size();i++){
   	  if(!toClean[i]){
   		  clean.add(classifier.get(i));
   	  }
   	  
     }
     
     return clean;
  }
  
  /**
   * Funciï¿½n que devuelve que prototipos de un conjunto dado pertenece a cada regiï¿½n (outputdatSet)
   * @return
   */
  
  PrototypeSet[] nearPrototype(PrototypeSet initial, PrototypeSet outputDataSet)
  {
      double dMin = Double.POSITIVE_INFINITY;
      PrototypeSet region[] = new PrototypeSet[outputDataSet.size()];
      Prototype nearest = null;
      
      for(int i=0; i< outputDataSet.size(); i++){
    	  region[i] = new PrototypeSet();
    	  if(outputDataSet.get(i)!=null)
    		  outputDataSet.get(i).setIndex(i); // Establish the index.
      }
      
     // para cada prototipo del trainiing.
      for(Prototype p : initial)
      {
    	  dMin = Double.POSITIVE_INFINITY;
    	  
    	  for(Prototype q : outputDataSet){ // calculo cual es el mï¿½s cercano en los que tengo en el clasificador
    		  if(q!=null){
	    		  double d = Distance.d(q, p);
		          
		          if(d < dMin  &&  q!=p)
		          {
		              dMin = d;
		              nearest = q;
		          }
	          }
          }
    	  
    	  region[nearest.getIndex()].add(p); // lo aï¿½ado a la regiï¿½n perteneciente.
      }
	  
	  return region;
  }
  
  /**
   * Funciï¿½n que devuelve que prototipos de un conjunto dado pertenece a cada conjunto Vij (outputdatSet)
   * @return
   */
  
  PrototypeSet[][] nearPrototypeWithClass(PrototypeSet initial, PrototypeSet outputDataSet)
  {
      double dMin = Double.POSITIVE_INFINITY;
      PrototypeSet region[][] = new PrototypeSet[outputDataSet.size()][];
      Prototype nearest = null;
      
      for(int i=0; i< outputDataSet.size(); i++){
    	  region[i] = new PrototypeSet[this.numberOfClass];
    	  for(int j=0; j< this.numberOfClass; j++)
    		  region[i][j] = new PrototypeSet();
    	  
    	  outputDataSet.get(i).setIndex(i); // Establish the index.
      }
      
     // para cada prototipo del trainiing.
      for(Prototype p : initial)
      {
    	  dMin = Double.POSITIVE_INFINITY;
    	  
    	  for(Prototype q:outputDataSet){ // calculo cual es el mï¿½s cercano en los que tengo en el clasificador
	          double d = Distance.d(q, p);
	          
	          if(d < dMin  &&  q!=p)
	          {
	              dMin = d;
	              nearest = q;
	          }
          }
    	  
    	  region[nearest.getIndex()][(int)p.getOutput(0)].add(p); // lo aï¿½ado a la Vij perteneciente.
      }
	  
	  return region;
  }
  
  
  /**
   * Generate a reduced prototype set by the ENPCGenerator method.
   * @return Reduced set by ENPCGenerator's method.
   */
  
  
  @SuppressWarnings({ "unchecked", "static-access" })
public PrototypeSet reduceSet()
  {
	  System.out.print("\nThe algorithm is starting...\n Computing...\n");
	  System.out.println("Number of class "+ this.numberOfClass);
	  
      PrototypeSet outputDataSet = new PrototypeSet();        
      
      //Initialization. 
      int aleatory = RandomGenerator.Randint(0, trainingDataSet.size()-1);
      outputDataSet.add(trainingDataSet.get(aleatory));

      
      int iter =0 ;
      
      PrototypeSet Sj[] = new PrototypeSet[this.numberOfClass];

      
      //Main Loop.
      
      
      while (iter<this.MaxIter){
    	  
    	  
    	  //First, getting information.
    	 // System.out.println("Getting Information");
    	     	  
    	  /*
    	   * La ecuaciï¿½n (4) define una funciï¿½n de pertenencia a los conjuntos de patrones, 
    	   * mientras que la ecuaciï¿½n (6) define la funciï¿½n de pertenencia a los conjuntos de patrones.
    	   *  Ademï¿½s, la ecuaciï¿½n (1) define la funciï¿½n de pertenencia a un conjunto de clase. 
    	   *  Un prototipo pertenece a la clase Sj si es de la clase sj (ecuaciï¿½n 1).  Una instancia pertenece a 
    	   *  un conjunto de prototipos Ri, si el prototipo ri es el mï¿½s cercano a la instancia (ecuaciï¿½n (6)); 
    	   *  una instancia pertenece a un conjunto de patrones Vij. si es de la clase sj y su prototipo mï¿½s cercano es el prototipo Ri.
    	   *  Si te fijas en la Figura 1, puede ver cï¿½mo un conjunto Vij no es mï¿½s que la intersecciï¿½n enter un Ri y un Sj. 
    	   
    	   *
    	   *
    	   *La ï¿½nica distinciï¿½n es que Vij se corresponde con el conjunto de patrones que estï¿½n mï¿½s
cerca del prototipo i-ï¿½simo que de cualquier otro y pertenecen a la clase j. Ri es el
conjunto de patrones mï¿½s cercanos al prototipo i-ï¿½simo, sin tener en cuenta la clase.
Los autores se inventan dos nombres que no aclaran nada, pero la idea es esa (una chorrez).
    	   *
    	   *
    	   *
    	   */
    	  
    	  // Getting information (1), Sj.(4) and (6)
    	  
          PrototypeSet V[][]= new PrototypeSet[outputDataSet.size()][];
          PrototypeSet R[] = new PrototypeSet[outputDataSet.size()];
          double accuracy[] = new double[outputDataSet.size()];
          double apportation[] = new double[outputDataSet.size()];
          double quality[] = new double[outputDataSet.size()];
          

    	  for(int i=0; i< this.numberOfClass; i++){
    		  Sj[i] = new PrototypeSet(trainingDataSet.getFromClass(i));
    		  
    	  }
    	  
    	  
    	  R = nearPrototype(trainingDataSet,outputDataSet);
    	  /*for(int i=0; i< outputDataSet.size();i++)
    	  System.out.println("Size R_"+i+ " = "+ R[i].size());
		  */
		  V = nearPrototypeWithClass(trainingDataSet,outputDataSet);
		  /*
		  for(int i=0; i< outputDataSet.size();i++){
		  for(int j=0; j< this.numberOfClass; j++){
			     System.out.println("V "+j+ " ="+ V[i][j].size()+ "  ");
		      }
		  }
		  */
		  
    	  for(int i=0; i< outputDataSet.size();i++){
    		  int clase =(int) outputDataSet.get(i).getOutput(0);
    		  accuracy[i] = V[i][clase].size()*1./R[i].size();
    		//  System.out.println("accuracy = " +accuracy[i]);
			  apportation[i] = V[i][clase].size()/ (this.expectation(clase, Sj[clase])/2);
    		  quality[i] = Math.min(1, accuracy[i]*apportation[i]); 
    		  //System.out.println("Quality " + quality[i]);
    		}
			
		   	  
    	  int initialSize = outputDataSet.size();  
    	 // Mutation.
    	 mutation(outputDataSet,V);
    	 // Reproduction.
    	  //System.out.println("V before = " + V.length);
    	 // outputDataSet.print();
    	  
    	  V = reproduction(outputDataSet,V);
       	  //System.out.println("V after = " +V.length);
       	  
    	  //outputDataSet.print();
       	  
    	  // Calculamos de nuevo las calidades. si se ha reproducido
          if(outputDataSet.size()> initialSize){
	    	  accuracy = new double[outputDataSet.size()];
	          apportation = new double[outputDataSet.size()];
	          quality = new double[outputDataSet.size()];
	          
	          R = nearPrototype(trainingDataSet,outputDataSet);
	          //Check if there is some region ==null
	          boolean toClean[] = new boolean[R.length];
	          for (int j=0; j< R.length; j++){
	        	  if (R[j].size()==0){
	        		  toClean[j] = true;
	        	  }else{toClean[j] = false;} 
	          }
	          
	          PrototypeSet clean = new PrototypeSet();
	          for(int i=0; i< outputDataSet.size();i++){
	        	  if(!toClean[i]){
	        		  clean.add(outputDataSet.get(i));
	        	  }
	        	  
	          }
	          outputDataSet = new PrototypeSet(clean);// Copy again
	          
	          // Recalculamos..
	          R = nearPrototype(trainingDataSet,outputDataSet);	          
	          V = nearPrototypeWithClass(trainingDataSet,outputDataSet);
	    	
	          /*for(int i=0; i< outputDataSet.size();i++){
	        	  System.out.println("Size R_"+i+ " = "+ R[i].size());
	    	  
	        	  for(int j=0; j< this.numberOfClass; j++){
				  if(V[i][j] != null){
				     System.out.println("V "+i+","+j+ " ="+ V[i][j].size()+ "  ");
				     }
			      }
	    	  }*/
	    	  
	          for(int i=0; i< outputDataSet.size();i++){
	    		  int clase =(int) outputDataSet.get(i).getOutput(0);
	    		  
	    		  if(V[i][clase] != null){ // Checking it's not empty.
	    		  	  accuracy[i] = V[i][clase].size()*1./ R[i].size();
	    		  	  apportation[i] = V[i][clase].size()/ (this.expectation(clase, Sj[clase])/2);
				  }else{
					  accuracy[i] = 0;
					  apportation[i] =0;
				  }
	    		  
	    		  quality[i] = Math.min(1, accuracy[i]*apportation[i]); 
	    		  //System.out.println("Quality " + quality[i]);
	    		}
          }
    	  // Fight
    	  fight(outputDataSet,V, quality);
    	  
    	  // Move operator
    	  
    	  move(outputDataSet, V);
    	  
    	  
    	  // die operator
    	  outputDataSet = new PrototypeSet(die(outputDataSet,quality));
    	  
    	 // System.out.println("*********Fin iter *******");
    	  iter++;
    	  
      }
      
      
      
      System.out.println("Accuracy % " +accuracy(outputDataSet,trainingDataSet));
      System.out.println("Reduction % " + (100-(outputDataSet.size()*100)/trainingDataSet.size()) );
      
      /*
      System.out.println("Cleaning");
	  // Limpiamos, aï¿½ado RNN para mejora los resultados
	  
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
      Parameters.setUse("ENPC", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      ENPCGenerator.setSeed(seed);
      
      int blocks =Parameters.assertExtendedArgAsInt(args,10,"number of blocks", 1, Integer.MAX_VALUE);
      
      //String[] parametersOfInitialReduction = Arrays.copyOfRange(args, 4, args.length);
     //System.out.print(" swarm ="+swarm+"\n");
      
      
      ENPCGenerator generator = new ENPCGenerator(training, 3, 250);
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
