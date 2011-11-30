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
* <p> 
* @author Written by Luciano Sanchez (University of Oviedo) 21/07/2008 
* @author Modified by J.R. Villar (University of Oviedo) 19/12/2008
* @version 1.0 
* @since JDK1.4 
* </p> 
*/ 

package keel.Algorithms.Clustering_Algorithms.ClusterKMeans;

import java.io.*;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import keel.Algorithms.Shared.Parsing.*;
import org.core.*;

/**
 * KMeans is a private class to cluterize a dataset using the k-means clustering
 * algorithm. The initial centroids are chosen randomly between the examples in the dataset. 
 * Each centroid is updated as the mean value of its nearest examples in the dataset.
 *
 * @version 1.0
 * @since JDK1.5
 */
class KMeans {
	
	//the dataset to be clusterized
	double train[][]; 
	//the desired number of clusters
	int clusters;          
    //the centroids of the clusters for all variables
	double cclusters[][];  
	//Randomize object used in this class
	static Randomize rand;
	
/** 
* <p> 
* KMeans constructor: the cluster centroids are obtained for the given dataset. 
* Firstly, the cluster's centroids are randomly chosen. Then the centroids are
* updated as the mean vlaue of nearest examples in the dataset. 
* The updating is carried out until no changes in the centroids is achieved.
* </p> 
* @param X  The dataset to be clusterized
* @param nclusters The desired number of clusters 
* @param vrand The Randomize object to be used
*/ 	
	public KMeans(double [][]X, int nclusters, Randomize vrand) {
		
		rand=vrand;
		train=X; clusters=nclusters; cclusters=new double[nclusters][X[0].length];
		
		for (int i=0;i<nclusters;i++) {
			int pos=(int)(rand.Rand()*X.length);
			for (int j=0;j<cclusters[i].length;j++) cclusters[i][j]=X[pos][j];
		}
		
		int []C = new int[X.length]; int []C_old = new int[X.length];
		for (int i=0;i<X.length;i++) { C_old[i] = nearestCentroid(X[i]); }
		centroidsUpdating(C_old);
		
		int cambios=0, iteracion=0;
		do {
			
			iteracion++;
			System.out.println("Iter="+iteracion+" changes="+cambios);
			cambios=0;
			for (int i=0;i<X.length;i++) { 
				C[i] = nearestCentroid(X[i]); 
				if (C[i]!=C_old[i]) cambios++;
				C_old[i]=C[i];   
			} 
			centroidsUpdating(C);
		} while(cambios>0);
		
	}
	
/** 
* <p> 
* This method updates the centroids of the clusters as the mean value of the
* nearest examples to each centroid in the dataset.
* The list of the nearest centroid to each example is given as an argument.
* This method modifies cclusters.
* </p> 
* @param C  The list of the nearest centroid to each example in the dataset
*/ 	
	private void centroidsUpdating(int C[]) {
		
		
		for (int c=0;c<clusters;c++) {
			for (int j=0;j<cclusters[c].length;j++) cclusters[c][j]=0; 
		}
		
		int []nejemplos = new int[clusters];
		for (int i=0;i<nejemplos.length;i++) nejemplos[i]=0;
		for (int i=0;i<C.length;i++) {
		    for (int j=0;j<cclusters[C[i]].length;j++) cclusters[C[i]][j]+=train[i][j];
			nejemplos[C[i]]++;
		}
		
		for (int c=0;c<clusters;c++) {
			for (int j=0;j<cclusters[c].length;j++) cclusters[c][j]/=nejemplos[c]; 
		}
		
		
	}
	
/** 
* <p> 
* This private method computes the distance between an example in the dataset 
* and a cluster centroid. 
* The distance is measure as the square root of the sum of the squares of the 
* differences between the example and the cetroid for all the dimensions.
* </p> 
* @param a  The example in the dataset
* @param b  The culster centroid
* @return  The distance between a and b as a double precision float value.
*/ 	
	private static double distance(double a[], double b[]) {
		
		//Euclid distance between two patterns
		double d=0;
		for (int i=0;i<a.length;i++) d+=(a[i]-b[i])*(a[i]-b[i]);
		return (double)Math.sqrt(d);
	}
	
	
/** 
* <p> 
* This method determines the nearest cluster centroid for a given example in the
* dataset. The distance is measure by means of the private method distance.
* </p> 
* @param x  The example in the dataset
* @return The index of the nearest cluster centroid as an integer values.
*/ 	
	public int nearestCentroid(double x[]) {
		
		// A patters is classified respect cluster centroids
		int cmin=0; double dmin=distance(x,cclusters[cmin]);
		for (int i=1;i<cclusters.length;i++) {
			double dx=distance(x,cclusters[i]);
			if (dx<dmin) {
				dmin=dx;
				cmin=i;
			}
		}
		return cmin;
		
	}
	
/** 
* <p> 
* This method computes the distance between an example in the dataset 
* and a cluster centroid. 
* The distance is measure as the square root of the sum of the squares of the 
* differences between the example and the cetroid for all the dimensions.
* </p> 
* @param a  The example in the dataset
* @param b  The culster centroid
* @return  The distance between a and b as a double precision float value.
*/ 	
    public void print() {
	System.out.println("Number of clusters: " + cclusters.length);
	if (cclusters.length <0) return;
	boolean distintos = false;
	int features = 0;
	for(int i=0; i<cclusters.length && !distintos; i++){
	    if (i==0) features = cclusters[0].length;
	    else distintos = (features!=cclusters[i].length);
	}
	if (distintos) {
	    System.out.println("Distinto nÃºmero de atributos por cluster...");
	    return;
	}
	for(int i=0; i < cclusters[0].length; i++) {
	    System.out.println("Feature: "+i+", number of clusters: "+cclusters.length);
	    for(int j=0; j < cclusters.length; j++) {
		System.out.print(""+ cclusters[j][i]+" ");
	    }
	    System.out.println(); 
	}
    }
	
}

/** 
* <p> 
* ClusterKMeans is a class to cluterize a dataset using the k-means clustering 
* algorithm. The initial centroids are chosen randomly between the examples in
* the dataset. Each centroid is updated as the mean value of its nearest 
* examples in the dataset.
* 
* The k-means algorithm is carried out by the KMEans class, while ClusterKMeans  
* acts as the interface with the KEEL environment.
* </p> 
*/ 
public class ClusterKMeans {

	//The random numbers generator used in this process
	static Randomize rand;
	
/** 
* <p> 
* This private method extract the dataset and the method's parameters from 
* the KEEL environment, calculates the centroids using the KMeans class and
* print out the results with the validation dataset. 
* </p> 
* @param tty  unused boolean parameter, kept for compatibility
* @param pc   ProcessConfig object to obtain the train and test datasets
*             and the method's parameters.
*/ 	
	private void clustering_kmeans(boolean tty, ProcessConfig pc) {
        
        try {
            
            
            String linea;
            ProcessDataset pd=new ProcessDataset();
            
            linea=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
            
            if (pc.parNewFormat) pd.processClusterDataset(linea,true);
            else pd.procesa_clustering_old(linea);
            
            int ndatos=pd.getNdata();           // Number of examples
            int nvariables=pd.getNvariables();   // Number of variables
            int nentradas=pd.getNinputs();     // Number of inputs
            pd.showDatasetStatistics();
            
            System.out.println("Number of examples="+ndatos);
            System.out.println("Number of inputs="+nentradas);
            
            double[][] X = pd.getX();             // Input data
            
            double[] emaximo = pd.getImaximum();   // Maximum and Minimum for input data
            double[] eminimo = pd.getIminimum();
            int[] neparticion=new int[nentradas];
            
            int s;
            s=pc.parNClusters;
            
            KMeans KM= new KMeans(X,s,rand);
            double fallos=0;
            try {
                for (int i=0;i<X.length;i++) {
                    int clase=KM.nearestCentroid(X[i]);
                    // System.out.println("pattern="+i+" cluster="+clase);
                }                
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            
            
            // Clusters in the test set
            ProcessDataset pdt = new ProcessDataset();
            int nprueba,npentradas,npvariables;
            linea=(String)pc.parInputData.get(ProcessConfig.IndexTestKMeans);
            
            if (pc.parNewFormat) pdt.processClusterDataset(linea,false);
            else pdt.procesa_clustering_old(linea);
            
            nprueba = pdt.getNdata();
            npvariables = pdt.getNvariables();
            npentradas = pdt.getNinputs();
            pdt.showDatasetStatistics();
            
            if (npentradas!=nentradas) throw new IOException("Error in test file");
            
            double[][] Xp=pdt.getX(); 
            int[] Co=new int[Xp.length];
            
            // Test set is classified
            try {
                for (int i=0;i<Xp.length;i++) {
                    Co[i]=KM.nearestCentroid(Xp[i]);
                    // System.out.println("pattern test="+i+" cluster="+Co[i]);
                }
                
                
            } catch (Exception e) {
                System.out.println(e.toString());
            }
			
            // Output format for clustering algorithms 
			pc.results(Xp,Co);
			KM.print();
            
        } catch(FileNotFoundException e) {
            System.err.println(e+" Training data not found");
        } catch(IOException e) {
            System.err.println(e+" Read error");
        }
    }
	
	
/** 
* <p> 
* This public static method runs the algorithm that this class concerns with. 
* </p> 
* @param args  Array of strings to sent parameters to the main program. The 
*              path of the algorithm's parameters file must be given.
*/ 	
	public static void main(String args[]) {
		
		boolean tty=false;
		ProcessConfig pc=new ProcessConfig();
		System.out.println("Reading configuration file: "+args[0]);
		if (pc.fileProcess(args[0])<0) return;
		int algo=pc.parAlgorithmType;
		rand=new Randomize();
		rand.setSeed(pc.parSeed);
		ClusterKMeans km=new ClusterKMeans();
		km.clustering_kmeans(tty,pc);
		
	}
}

