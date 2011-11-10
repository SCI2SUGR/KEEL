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
	SGP.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-3-09
	Copyright (c) 2009 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.SGP;

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

/* TENEMOS QUE METERLO.
import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import Jama.Matrix.*;
import Jama.SingularValueDecomposition;

 * */

/**
 * 
 * @author Isaac Triguero
 * @version 1.0
 */
public class SGPGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  

	// SGP1 or SGP2, use the pruning and merge or not..
  private int method;

  //threshold Rmin and Rmis
  protected int Rmin;  
  protected int Rmis;
 
  protected int numberOfClass;
  
  /**
   * Build a new SGPGenerator Algorithm
   *
   */
  
  public SGPGenerator(PrototypeSet _trainingDataSet, int method, int Rmin, int Rmis)
  {
      super(_trainingDataSet);
      algorithmName="SGP";
      
      this.method = method;
      this.Rmin = Rmin;
      this.Rmis = Rmis;

  }
  
  
  void intercambiar(PrototypeSet v[],int pos1,int pos2){
	  
	  PrototypeSet aux = v[pos2];
	  v[pos2] = v[pos1];
	  v[pos1] = aux;	  
  }

  /**
   * Build a new SGPGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public SGPGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="SGP";
      
      
      this.method = parameters.getNextAsInt();
      this.Rmin = parameters.getNextAsInt();
      this.Rmis = parameters.getNextAsInt();
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
    
      System.out.println("Isaac dice: method SGP" + this.method+ " Rmin = " + this.Rmin + " Rmis= "+ this.Rmis );
      System.out.println("Number of class= "+ this.numberOfClass);
  }
  
  
  

  @SuppressWarnings({ "unchecked", "static-access" })
  public PrototypeSet reduceSet()
  {

	  System.out.print("\nThe algorithm is starting...\n Computing...\n");
	  System.out.println("Number of class "+ this.numberOfClass);
	  
	  
	  PrototypeSet G[] = new PrototypeSet [this.numberOfClass*100]; // enough memory.

	  PrototypeSet outputDataSet = new PrototypeSet(this.numberOfClass*5000);
	  PrototypeSet realoutput;
	  
	  int k,M;
	  
	  // 1. Set Gk.
	  // 2. Compute initial prototypes Pk = means(Gk), outputDataSet == Pk
	  for( int i = 0; i< this.numberOfClass; i++){
		  G[i] = new PrototypeSet(trainingDataSet.getFromClass(i));
		  
		  //System.out.println("G[ "+i+"] . size ="+ G[i].size());
		  if(G[i].size()>0)  // check the class is not empty.
			  outputDataSet.add(G[i].avg());
	  }
	  
	  PrototypeSet nominalPopulation;
	  nominalPopulation = new PrototypeSet();
	  nominalPopulation.formatear(outputDataSet);
	  //
	  System.out.println("Initial Accuracy % " +accuracy(nominalPopulation,trainingDataSet));
	  System.out.println("Initial reduction % " + (100-(outputDataSet.size()*100.)/trainingDataSet.size()) );
	  
	  //3)
	  k=0;
	  //M= this.numberOfClass;// initially the number of groups is numberOfclass
	  M = outputDataSet.size();
	  //4. Compute the distance
	  
	  boolean cambio = true;
	  
	  while (cambio){
		  //cambio = false;
		  
		  //  Distancia entre cada prototipo xj de G a las distintas medias.
		  
		  double distance[][];
		  distance = new double[G[k].size()][];
		  double min = Double.MAX_VALUE;  
		  //int indexNN[][] = new int[M][]; // index of the nearest prototype
		  int indexNN[] = new int[M];
		  int pkNN[][]= new int[G[k].size()][];
		  // initial mejor index, a su grupo.
		    for(int j=0; j< M;j++){
			 indexNN[j]=k;
			
		    }
		 
				 
		  
		  
		 
			  
			 // indexNN[i] = new int[G[k].size()];
			  
			   
			  for(int j=0; j< G[k].size(); j++){ // para cada prototipo del grupo.
				  distance[j] = new double[M];
				  pkNN [j]= new int[M];
				  Prototype xj = G[k].get(j);
				
				  min = Double.MAX_VALUE;
				  for(int i = 0; i< M; i++){  // para cada grupo
					  
					  
					  //System.out.println(outputDataSet.size());
					 // System.out.println("**********************");
					  //outputDataSet.get(i).print();
					  if(outputDataSet.get(i) != null){
						  distance[j][i]= Distance.d(xj.formatear(), outputDataSet.get(i).formatear()); // distancia entre el prototipo y la media.
						  
						  if(distance[j][i] < min){
							  min = distance[j][i];
							  indexNN[k] = j;  // te quedas con el prototipo del grupo
							  pkNN[j][k] = i; // te quedas con el grupo.  Pk
						  }
					  }
				  else{
						  distance[j][i] = Double.MAX_VALUE;
					  }
			  }
		  }
		  

		  
		  // Comprobamos si para cada grupo el vecino mï¿½s cercano eres tu mismo.(tu media)
		  // First occur, All patterns of a gruop the closest prototype is the group prototype.
		  // Then no modification is performed.
		  
		  boolean continuar = false;
		  for(int i=0; i< G[k].size() && !continuar;i++){
			  //System.out.print(pkNN[i][k]+" ");
			  if(pkNN[i][k]!=k){
				  continuar  = true; // go to step 4
			  }
			  
		  }
		  
		  boolean paso7 = true;
		  
		  if(continuar){ // si podemos continuar paso7.
			 //System.out.println("Continuo");
			 
			 for(int i=0; i<G[k].size() && paso7;i++){
				 // Pk y  Pij
				 if((outputDataSet.get(k).getOutput(0) != outputDataSet.get(pkNN[i][k]).getOutput(0)) && G[k].size()>1){ //
					 paso7 = true;
				 }else{
					// System.out.println("falla paso 7" );
					 paso7 = false; // si falla una vez, paramos no se hace el paso 7.
				 }
				 
			 }
			 
			 if(paso7){
				  // 7, C(Pij*) != C(Pk)
				// System.out.println("Paso 7") ;
					  paso7 = true;
					  
					  //System.out.println("Splitting Gk = "+ k + " size= "+ G[k].size());
					  //Split G(k)
					  
					  // Point mean of the Gk.
					  Prototype mean = G[k].avg();
					  
					  PrototypeSet zi = new PrototypeSet(G[k].size());
					  
					  
					  // Centramos los valores en el 0,0
					  for (int l=0; l<G[k].size(); l++){
						  zi.add( G[k].get(l).sub(mean));
					  }
					  
					  
					  //zi.print();
					
					  // Falta calcular el auto-vector,  alpha, y se divide, tal que.
//descomentar					  Matrix alpha= new Matrix(zi.prototypeSetTodouble());
					  
					  //EigenvalueDecomposition autovalores = new EigenvalueDecomposition(alpha);
					 
					 // alpha.
//descomentar					  SingularValueDecomposition val = new SingularValueDecomposition(alpha);
					 
//descomentar					  Matrix aux =val.getV();
					  //System.out.println("Filas*Columnas" + aux.getRowDimension() +aux.getColumnDimension());
					  
					  double autoval[] = new double[G[k].get(0).numberOfInputs()];
//descomentar   			  for(int i=0; i<aux.getColumnDimension(); i++)
//descomentar						  autoval[i] = aux.get(0,i);
					   
					 
					  // zi*alpha >= 0, un grupo, zi*alpha <0 otro grupo.
					  double output[] = new double[1];
					  output[0] = 0;
					  Prototype alpha2 = new Prototype(autoval,output);
				  
					  //  HIPERPLANO !! :S
										  
					  PrototypeSet removed = new PrototypeSet();
					  
					  for(int l=0; l< G[k].size();l++){
						  //zi.get(l).print();
						  //alpha2.print();
						  //System.out.println("Pro escalar = " + alpha2.mulEscalar(zi.get(l)));
						  if(alpha2.mulEscalar(zi.get(l))>0) {
							  removed.add(G[k].get(l));
						  }				  
					  
					  }
					  
					  //System.out.println("Removed size= "+ removed.size());
					  
					  for(int i=0; i<removed.size();i++)
						  G[k].remove(removed.get(i));
					  
					// Tenemos que tener en cuenta el Rmin.
					  // si no tiene un minimo nï¿½mero se pueden descartar :)
					  if(removed.size()>Rmin){
						  G[M] = new PrototypeSet(removed.size());
						  
						  for(int i=0; i<removed.size();i++)
							  G[M].add(removed.get(i));
						  
						  
							 outputDataSet.set(k, G[k].avg());
							 // System.out.println("Tamaï¿½o output = "+ outputDataSet.size());
							  outputDataSet.add(G[M].avg());
							  
							  //Stablish the class too.
							 //double ClassK= outputDataSet.get(k).getOutput(0);
							  outputDataSet.get(M).setFirstOutput(outputDataSet.get(k).getOutput(0));
						 /* System.err.println(" Gk ");
						  G[k].print();
						  System.err.println(" GM");
						  G[M].print();
						  System.err.println(" ****");
						  
						  */
						  M++;
						
						   
					  }else{
						  paso7 =false;
					  }
					  
					 // System.out.println("G[k] size = "+ G[k].size()+"\n**************\n");

					  //G[k].print();
					  //cambio  = false;
					  
				  } // Fin paso 7.
			 
			 
			 // paso 8. Cpij = Cpk and Pi != Pk for some xj E Gk.
			 
			 if(!paso7 && G[k].size()>1){
				
				 PrototypeSet removed = new PrototypeSet();
				 boolean paso8 = false;
				 for(int i=0; i<G[k].size();i++){
					 if((outputDataSet.get(k).getOutput(0) == outputDataSet.get(pkNN[i][k]).getOutput(0)) && (pkNN[i][k]!=k)){
						 //System.out.println("Paso 8") ;
						 paso8=true;
						 removed.add(G[k].get(i));
						  // add to the Group ij*.
						  G[pkNN[i][k]].add(G[k].get(i));
						  
						  outputDataSet.set(pkNN[i][k], G[pkNN[i][k]].avg());
					 }
					 
				 }
				 
				 //eliminar del G[k]
				  for(int i=0; i<removed.size();i++)
					  G[k].remove(removed.get(i));
				  
				  if(paso8){
					  outputDataSet.set(k, G[k].avg());
				  }
				
				 //System.out.println("Paso 8 -> G[k] size = "+ G[k].size()+"\n**************\n");
				  removed = new PrototypeSet();
				  boolean paso9 = false;
				  //Paso 9
				  for(int i=0; i<G[k].size();i++){
					  if(outputDataSet.get(k).getOutput(0) != outputDataSet.get(pkNN[i][k]).getOutput(0) && (pkNN[i][k]!=k)){ //
						  paso9=true;
						 // System.out.println("Gk size = " + G[k].size());
						 
						  removed.add( G[k].get(i));
						  						  
						  

						  
					 }
				  
				  }
				  
				  // Si tiene el mismo tamaï¿½o no vamos aborrar todos :)
				  if(removed.size() < G[k].size()){
					  for(int i=0; i<removed.size();i++)
						  G[k].remove(removed.get(i));
					  //System.out.println("Paso 9-> G[k] size = "+ G[k].size()+"\n************** " + removed.size());
					  
					  if(paso9){
						  //System.out.println("Paso 9") ;
						  G[M] = new PrototypeSet();
						  for(int i=0; i<removed.size();i++)
							  G[M].add(removed.get(i));
						 	
						  outputDataSet.set(k, G[k].avg());
						  outputDataSet.add(M, G[M].avg());
						  M++;
					  }
				  }
			 
			 }
				  
		} // Fin continuar
		  
		  //System.out.println(k);

		  
		  if(!paso7 || !continuar){
			  //System.out.println("M = " + M);
			  if(k==M-1){
				  cambio = false;  //END
			 }else if(k != M-1){
				  k++;
				  cambio =true; // come back to 4.
			  }
		  } 
			

		  // When we finish the SGP algorithm we check if the methos selected is SGP2, so we introduce the merging and
		  // pruning steps now.
		  
		  
			
		  if(this.method == 2){
			  System.out.println("Method SGP2");
			  // Merging step. We check by pair.
			  for(int i=0; i <outputDataSet.size(); i++){
				  for(int j=0; j< outputDataSet.size(); j++){
					  if(i!=j){
						  if(outputDataSet.get(i).getOutput(0) == outputDataSet.get(j).getOutput(0)){
							  // A == i, B == j
							  //System.out.println("Clases Son iguales " +i+","+j);
							  boolean continuar2 = true;
							  // si para todos los prototipos el 2ï¿½ mï¿½s ceano es j.
							  for(int m=0; m< G[i].size() && continuar2;m++){
								  // obtain the index of the seconde nearest neighbor
								  int indexSNN =outputDataSet.IndexSecondNearestTo(G[i].get(m));
								  //int prueba = outputDataSet.IndexNearestTo(G[i].get(m));
								  if (indexSNN != j){
									  //System.out.println("prueba = "+prueba+" index "+ indexSNN+ ", "+i+","+j);
									  continuar2 = false;
								  }
							  }
							  
							  if (continuar2)
							  {
								// Now we check the opposite situation.
								  System.out.println("Pre-Merging");
								  for(int m=0; m< G[i].size() && continuar2;m++){
									  // obtain the index of the seconde nearest neighbor
									  int indexSNN =outputDataSet.IndexSecondNearestTo(G[j].get(m));
									  if (indexSNN != i){
										  
										  continuar2 = false;
									  }
								  }
								  
								  if(continuar2){
									  // Merging.
									  System.out.println("Merging");
									  G[i].add(G[j]);
									  outputDataSet.get(i).set(G[i].avg());
								
									  intercambiar(G,j,M-1);
									  M--; // Lo pongo al final
									  //outputDataSet.remove(j);
								  }
							  }						  
							  
						  }
					  }
				  }
			  }
			  
			  
			  // Pruning step.

			  
			  
		  } // End Method 2.
		  
		  
		 
	  } // Fin while
	  

  

	  realoutput = new PrototypeSet(M); // with M prototypes, M groups.
	  for(int i=0; i< M; i++){
		  if(outputDataSet.get(i)!=null)
			  realoutput.add(outputDataSet.get(i));		  
	  }
	  
	  
	  nominalPopulation = new PrototypeSet();
	  nominalPopulation.formatear(realoutput);
	  /*if( M<1){
		  realoutput = new PrototypeSet(this.numberOfClass);
		  for(int i=0; i< this.numberOfClass; i++){
			  realoutput.add(trainingDataSet.getFromClass(i).getRandom());
		  }
	  }
	  */
	  System.out.println("Accuracy % " +accuracy(nominalPopulation,trainingDataSet));
	  System.out.println("Reduction % " + (100-(realoutput.size()*100.)/trainingDataSet.size()) );
	  
	  
	  
	  
	  
	  
	  // Limpiamos!
	  /*
	  boolean marcas[];
	  marcas = new boolean[realoutput.size()];
	  Arrays.fill(marcas, true);

	  double accuracyInic =KNN.classficationAccuracy(realoutput, trainingDataSet);
	  double accuracy;
	  
	  for(int i=0; i< realoutput.size(); i++){
		  marcas[i] = false; //At the begining you don't think you can elimante.
		  
		  
		  PrototypeSet leaveOneOut = realoutput.without(realoutput.get(i));
		  accuracy = KNN.classficationAccuracy(leaveOneOut, trainingDataSet);
		  
		  if(accuracy > accuracyInic){
			  
			  marcas[i] = true; // we can eliminate
		  }
		  
		  
	  }
	  
	  //Then we create the result set..
	  
	  PrototypeSet clean = new PrototypeSet();
	  
	  for(int i=0; i< marcas.length; i++){
		  if(!marcas[i]){
			  clean.add(realoutput.get(i));
		  }
	  }
	  
      System.out.println("Accuracy % " +accuracy(clean,trainingDataSet));
      System.out.println("Reduction % " + (100-(clean.size()*100)/trainingDataSet.size()) );
      */
      
      
      
      return nominalPopulation;   
  
    
	  
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
      Parameters.setUse("SGP", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      SGPGenerator.setSeed(seed);
      
     // int blocks =Parameters.assertExtendedArgAsInt(args,10,"number of blocks", 1, Integer.MAX_VALUE);
      
      //String[] parametersOfInitialReduction = Arrays.copyOfRange(args, 4, args.length);
     //System.out.print(" swarm ="+swarm+"\n");
      
      
      SGPGenerator generator = new SGPGenerator(training, 1, 1, 1);
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
