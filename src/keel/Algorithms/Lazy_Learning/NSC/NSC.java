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
 * 
 * File: NSC.java
 * 
 * The Nearest subclass algorithm.
 * A 1-Nearest Neighbor classifier which uses the MVC clustering algorithm
 * to generate new prototipes to enhance the classifier
 * 
 * @author Written by Joaquín Derrac (University of Granada) 15/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.NSC;

import keel.Algorithms.Lazy_Learning.LazyAlgorithm;

import java.util.*;
import org.core.*;

public class NSC extends LazyAlgorithm{
	
	//Parameters
	
	int k;
	int q;
	double maxVariance;
	int epochMax;
	int noChange;
	
	//Adictional structures
	
	Cluster clusters[][];
	int clustersIndex[][];
	int directory[];
	double centroids[][];
	int centroidsClass[];
	
	int epoch;
	int actualClass;
	
	private final int MAXITERATIONS = 10000;

	
	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public NSC (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="NSC";    
		
		//Inicialization of auxiliar structures
	    
	    clusters=new Cluster[nClasses][];
	    
	    for(int i=0;i<nClasses;i++){
	    	clusters[i]=new Cluster[nInstances[i]];
	    	for(int j=0;j<nInstances[i];j++){
	    		clusters[i][j]=new Cluster(inputAtt,q,k);
	    	}
	    }
	    
	    clustersIndex=new int[trainData.length][];
	    for(int i=0;i<nClasses;i++){
	    	clustersIndex[i]=new int[nInstances[i]];
	    	for(int j=0;j<nInstances[i];j++){
	    		clustersIndex[i][j]=j;
	    	}
	    }	

	    directory=new int[trainData.length];
	    
	    //Initialization of random generator
	    
	    Randomize.setSeed(seed);
	    
		//Initialization stuff ends here. So, we can start time-counting
		
		setInitialTime(); 
	    
	} //end-method 
	
	/** 
	 * Reads configuration script, to extract the parameter's values.
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */	
	protected void readParameters (String script) {
		
		String file;
		String line;
		StringTokenizer fileLines, tokens;
		
	    file = Files.readFile (script);
	    fileLines = new StringTokenizer (file,"\n\r");
	    
	    //Discard in/out files definition
	    
	    fileLines.nextToken();
	    fileLines.nextToken();
	    fileLines.nextToken();
	    
	    //Getting the seed
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    seed = Long.parseLong(tokens.nextToken().substring(1));
	    
	    //Getting the k parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    k = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the q parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    q = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the noChange parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    noChange = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the epochMax parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    epochMax = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the maxVariance parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    maxVariance = Double.parseDouble(tokens.nextToken().substring(1));
	    
	} //end-method
	
	/** 
	 * The MVC clustering algorithm
	 * 
	 */		
	public void doClustering(){
		
		int lastChange=0;
		int actual;
		int empty;
		double variance;
		double newVariance;
		boolean done;
		boolean oneClusterFinish;
		int instanceIndex;
	    int index;
	    int classInstances;
		
		
		for(actualClass=0;actualClass<nClasses;actualClass++){
			
			instanceIndex=0;
			
			//Allocate instances
			
		    for(int i=0;i<trainData.length;i++){

		    	if(trainOutput[i]==actualClass){
		    		clusters[actualClass][instanceIndex].add(i);
		    		directory[i]=instanceIndex;
		    		instanceIndex++;
		    	}else{
		    		directory[i]=-1;
		    	}
		    }

			epoch=0;
			oneClusterFinish=false;
			
			while((epoch-lastChange)<noChange && epoch < MAXITERATIONS){
				
				permutate();
				
				//for each cluster
				for(int i=0;i<clustersIndex[actualClass].length;i++){

					actual=clustersIndex[actualClass][i];
					done=false;
					
					//if the cluster have at least one instance
					if(clusters[actualClass][actual].isEmpty()==false){
						
						//check cluster's propierties
						checkCluster(actual);
						
						//calculate variance
						variance=clusters[actualClass][actual].getVariance();
						
						//*************************
						//       ISOLATION
						//*************************

						if((variance>maxVariance)&&(epoch<epochMax)){

							int candidate;
							
							candidate=getIsolationCandidate(actual);
							
							clusters[actualClass][actual].dropByContent(candidate);
							empty=firstEmpty();
							clusters[actualClass][empty].add(candidate);
							directory[candidate]=empty;
							
							done=true;
						}
						
						//If there is only one cluster and it can't be isolated
						
						if(numberClusters(actualClass)==1){
							oneClusterFinish=true;
							done=true;
						}
						
						//*************************
						//       UNION
						//*************************		
						if(!done && (variance<maxVariance)){
							
							int neigbour;
							int ins;
							int selected=-1;
							int candidates[]=clusters[actualClass][actual].getOuterBorder();
							double minVariance=maxVariance;

							for(int j=0;j<candidates.length;j++){

								if(candidates[j]>-1){

									neigbour=directory[candidates[j]];

									newVariance=varianceMerge(clusters[actualClass][actual],clusters[actualClass][neigbour]);
									
									if(minVariance>newVariance){
										minVariance=newVariance;
										selected=neigbour;
									}
								}
								
							}

							if(selected>-1){

								while(clusters[actualClass][selected].isEmpty()==false){

									ins=clusters[actualClass][selected].drop(0);
									clusters[actualClass][actual].add(ins);
									directory[ins]=actual;
									
								}
								
								done=true;
							}
		
						}
						
						//*************************
						//     PERTURBATION
						//*************************	
						if(!done){

							int candidate;
							int other;
							
							candidate=getPerturbationCandidate(actual);
							
							if(candidate>-1){

								other=directory[candidate];
								directory[candidate]=actual;
								clusters[actualClass][actual].add(candidate);
								clusters[actualClass][other].dropByContent(candidate);
								
								done=true;
							}
						}
						
						if(done==true){
							lastChange=epoch;
							if(oneClusterFinish==true){
								epoch+=noChange;
							}
						}
					}//end-if (cluster have at least one instance)
					
				}//end-for

				epoch++;
				
			}//end-while (end of Algorithm)
	
		}//end-for (end of all class)
		
	    //Now, we have to extract all the centroids of the final clusters
	    
	    classInstances=0;
	    
	    for(int i=0;i<clusters.length;i++){
	    	for(int j=0;j<clusters[i].length;j++){
		    	if(clusters[i][j].isEmpty()==false){
		    		classInstances++;
		    	}	
	    	}
	    	
	    }
	    
	    centroids=new double[classInstances][inputAtt];
	    centroidsClass=new int[classInstances];
	    
	    index=0;
	    for(actualClass=0;actualClass<clusters.length;actualClass++){
	    	for(int j=0;j<clusters[actualClass].length;j++){
		    	if(clusters[actualClass][j].isEmpty()==false){

		    		centroids[index]=calculateCentroid(clusters[actualClass][j].getAll());
		    		centroidsClass[index]=actualClass;
		    		index++;
		    	}	
	    	}
	    }
	}//end-method
	
	/** 
	 * Calculates the variance resulting by merging two clusters
	 * 
	 * @param a First cluster
	 * @param b Second cluster
	 * @return Variance resulting of the merge of both clusters
	 *
	 */
	private double varianceMerge(Cluster a,Cluster b){
		
		double quad;	
	    double mean;		
    	double var;
    	double value;
    	int auxIndex;
    	
		var=0.0;

		for (int j=0; j<inputAtt; j++) {	
		
			mean=0.0;
			quad=0.0;
			
			for (int i=0;i<a.getSize();i++){
				auxIndex=a.get(i);
				value=trainData[auxIndex][j];
				mean+=value; 
				quad+=value*value;
    		}	
			for (int i=0;i<b.getSize();i++){
				auxIndex=b.get(i);
				value=trainData[auxIndex][j];
				mean+=value; 
				quad+=value*value;
    		}
			
			mean /=(double)(a.getSize()+b.getSize());
			var+=(quad/(double)(a.getSize()+b.getSize()))- (mean*mean);

		}

		return var;
		
	}//end-method
	
	/** 
	 * Select the best candidate for perturbation operation
	 * 
	 * @param clust Cluster to select
	 * @return Candidate selected
	 * 
	 */
	private int getPerturbationCandidate(int clust){
		
		Set<Integer> s = new HashSet<Integer>();

		int candidate;
		int selected;
		int aux;
		int otherCluster;
		double gainA;
		double gainB;
		double gainAx;
		double gainBx;
		double gain;
		double best;
		Cluster auxA;
		Cluster auxB;
		
		selected=(int)Math.ceil(Math.sqrt( q ));

		s.clear();

		for(int i=0;i<selected;i++){
			
			do{
				aux=Randomize.Randint(0,q);
			}while(s.contains(aux));
			s.add(aux);

		}
		
		//get the best of the candidates
		
		best=0;
		candidate=-1;
		
		gainA=calculateGain(clusters[actualClass][clust].getAll(),clusters[actualClass][clust].getCentroid());
		
		//foreach candidate

	    Iterator<Integer> iter = s.iterator();
	    
	    while (iter.hasNext()) {
	      
	    	selected=iter.next();
	    	selected=clusters[actualClass][clust].getOuterBorder()[selected];
	    	otherCluster=directory[selected];
	    	
	    	gainB=calculateGain(clusters[actualClass][otherCluster].getAll(),clusters[actualClass][otherCluster].getCentroid());
	    
	    	auxA=clusters[actualClass][clust].clone();
	    	auxB=clusters[actualClass][otherCluster].clone();
	    	
	    	auxA.add(selected);
	    	auxB.dropByContent(selected);
	    	
	    	//set new centroids
	    	
	    	auxA.setCentroid(calculateCentroid(auxA.getAll()));
	    	auxB.setCentroid(calculateCentroid(auxB.getAll()));
	    	
	    	gainAx=calculateGain(auxA.getAll(),auxA.getCentroid());
	    	gainBx=calculateGain(auxB.getAll(),auxB.getCentroid());
	    	
	    	gain=gainA+gainB-gainAx-gainBx;
	    	
	    	if(gain>best){
	    		candidate=selected;
	    	}
	    	
	    }

		return candidate;
		
	}//end-method
	
	/** 
	 * Calculate squared error from each instance of a cluster and its centroid
	 * 
	 * @param instances Instances of the cluster
	 * @parama centroid Centroid of the cluster
	 * @return Squared error calculated
	 * 
	 */
	private double calculateGain(int instances[],double centroid[]){
		
		double gain=0.0;
		double norm;
		double value;
		
		if (instances.length<2){
			return 0.0;
		}
		
		for(int i=0;i<instances.length;i++){
		
			norm=0.0;
			
			for(int j=0;j<centroid.length;j++){
				value=(trainData[instances[i]][j]) - centroid[j];
				value=value*value;
				norm+=value;
			}
			
			gain+=norm;
		}

		return gain;
	}//end-method
	
	/** 
	 * Calculate the centroid of a cluster
	 * 
	 * @param ins Instances of the cluster
	 * @return The centroid
	 * 
	 */
	private double [] calculateCentroid(int ins[]){
		
		double centroid []=new double [trainData[0].length];
		
		for(int j=0;j<centroid.length;j++){
		
			centroid[j]=0.0;
			for(int i=0;i<ins.length;i++){
				centroid[j]+=trainData[ins[i]][j];
			}
			centroid[j]/= (double)ins.length;
		}

		return centroid;
	}//end-method

	/** 
	 * Select the best candidate for isolation operation
	 * 
	 * @param clust Cluster to select
	 * @return Candidate selected
	 * 
	 */
	private int getIsolationCandidate(int clust){
		
		Set<Integer> s = new HashSet<Integer>();

		int candidate;
		int selected;
		int aux;

		selected=(int)Math.ceil(Math.sqrt( q ));

		s.clear();
		
		candidate=q+1;
		
		for(int i=0;i<selected;i++){
			
			do{
				aux=Randomize.Randint(0,q);
			}while(s.contains(aux));
			s.add(aux);
			
			if(aux<candidate){
				candidate=aux;			
			}
		}
		candidate=clusters[actualClass][clust].getInnerBorder()[candidate];
		
		return candidate;
	}//end-method
	
	/** 
	 * Find the first cluster empty
	 * 
	 * @return Cluster found
	 * 
	 */
	private int firstEmpty(){
		
		boolean found=false;
		int index=-1;
		
		while(!found){
			
			index++;
			if(clusters[actualClass][index].isEmpty()){
				found=true;
			}
			
		}
		
		return index;
		
	}//end-method
	
	/** 
	 * Counts the effective number of clusters for a class
	 * 
	 * @param nClass Class to explore
	 * @return Number of clusters found
	 * 
	 */
	private int numberClusters(int nClass){
		
		int count=0;

		for (int i=0;i<clusters[nClass].length;i++){
			if(clusters[nClass][i].isEmpty()==false){
				count++;
			}
		}

		return count;
		
	}//end-method
	
	/** 
	 * Calculates variance, centroid, inner border and outer border
	 * of a cluster 
	 * 
	 * @param number Index of the cluster
	 * 
	 */
	private void checkCluster(int number){
		
		int index[];
		double centroid[];
		double distances[];
		int inner[];
		int outer[];		
		int auxIndex;
		double value;
		int size;
		
		if(clusters[actualClass][number].getProperties()==false){
		
			//get cluster elements
			index=clusters[actualClass][number].getAll();
			size=index.length;
			centroid=new double[inputAtt];
			
			//calculate variance and centroid
			if(size>1){
				double quad;	
			    double mean;		
		    	double var;	
				
				var=0.0;
				
				for (int j=0; j<inputAtt; j++) {	
				
					mean=0.0;
					quad=0.0;
					for (int i=0;i<size;i++){
						auxIndex=clusters[actualClass][number].get(i);
						value=trainData[auxIndex][j];
						mean+=value; 
						quad+=value*value;
		    		}	
					
					mean /=(double)size;
					
					centroid[j]=mean;
	
					var+=(quad/(double)size) - (mean*mean);
	
				}
				
				clusters[actualClass][number].setVariance(var/(double)inputAtt);
				clusters[actualClass][number].setCentroid(centroid);
			}
			else{
				clusters[actualClass][number].setVariance(0.0);
				clusters[actualClass][number].setCentroid(trainData[clusters[actualClass][number].get(0)]);
			}
			
			//calculate innner border
			
			if(size>q){
				double dist;
				boolean oneMax;
				
				distances= new double[q];
				inner=new int[q];
				
				for(int i=0;i<q;i++){
					distances[i]=0.0;
				}
				
				for(int i=0;i<size;i++){
	
					dist=absoluteDistance(trainData[clusters[actualClass][number].get(i)],centroid);
					oneMax=false;
					for(int j=q-1;j>-1;j--){
					
						if(dist>distances[j]){
							oneMax=true;
						}else{
							if(oneMax==true){
								for(int k=q-1;k>j;k--){
									inner[k]=inner[k-1];
								}
								inner[j]=i;
								oneMax=false;
							}
						}
					
					}
					if(oneMax==true){
						for(int k=q-1;k>0;k--){
							inner[k]=inner[k-1];
						}
						inner[0]=i;		
					}
				}
				
				clusters[actualClass][number].setInnerBorder(inner);
			}
			else{
				inner=new int[q];
				for(int i=0;i<size;i++){
					inner[i]=clusters[actualClass][number].get(i);
				}
				for(int i=size;i<q;i++){
					inner[i]=-1;
				}
				clusters[actualClass][number].setInnerBorder(inner);
			}
			
			//calculate outer border

			double dist;
			boolean oneMin;
			
			distances= new double[k];
			outer=new int[k];
			
			for(int i=0;i<k;i++){
				distances[i]=Double.MAX_VALUE;
				outer[i]=-1;
			}
			
			for(int i=0;i<trainData.length;i++){

				if((directory[i]!=number)&&(directory[i]>-1)){
					dist=absoluteDistance(trainData[i],centroid);
					oneMin=false;
					for(int j=k-1;j>-1;j--){
					
						if(dist<distances[j]){
							oneMin=true;
						}else{
							if(oneMin==true){
								for(int l=k-1;l>j;l--){
									outer[l]=outer[l-1];
								}
								outer[j]=i;
								oneMin=false;
							}
						}
					
					}
					if(oneMin==true){
						for(int l=k-1;l>0;l--){
							outer[l]=outer[l-1];
						}
						outer[0]=i;		
					}
				}
			}	

			clusters[actualClass][number].setOuterBorder(outer);
			
			//Properties are OK
			clusters[actualClass][number].setProperties();
		}
	}//end-method
	
	/** 
	 * Calculates the absolute distance between two instances
	 * 
	 * @param train Train instance
	 * @param mean Centroid of a cluster
	 * @return Distance calculated
	 * 
	 */
	
	private double absoluteDistance(double train[],double mean []){
		
		double value=0.0;
		double aux;
			
		for(int i=0;i<inputAtt;i++){
		
			aux=Math.abs(train[i]-mean[i]);
			value+=aux;
		}
		
		return value;
	}//end-method
	
	/** 
	 * Make a random permutation of the array of clusters
	 * 
	 */
	private void permutate(){
		
		int temp;
		int aux;
		
		for ( int i = 0; i < clustersIndex[actualClass].length; i++ ) {  
			aux =  ( int ) ( Math.random() * clustersIndex[actualClass].length );  
			temp = clustersIndex[actualClass][ i ];  
			clustersIndex[actualClass][ i ] = clustersIndex[actualClass][ aux ];  
			clustersIndex[actualClass][ aux ] = temp;  
		}
	}//end-method
	
	/** 
	 * Evaluates a instance to predict its class.
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted
	 * 
	 */
	protected int evaluate (double example[]) {
		
		int output=-1;
		double aux;
		double min=Double.MAX_VALUE;
		
		//1-NN rule
		for(int i=0;i<centroids.length;i++){
			aux=euclideanDistance(example,centroids[i]);
			if(aux<min){
				min=aux;
				output=i;
			}
		}
		
		output=centroidsClass[output];
		
		return output;
		
	}//end-method
	
} //end-class 

