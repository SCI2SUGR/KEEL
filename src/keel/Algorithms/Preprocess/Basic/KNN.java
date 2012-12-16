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
 * File: KNN.java
 * 
 * An auxiliary implementation of the KNN classifier for using in Instance Selection algorithms
 * 
 * @author Written by Salvador Garcï¿½a (University of Granada) 20/07/2004 
 * @author Modified by Isaac Triguero (University of Granada) 20/06/2010 
 * @version 0.2
 * @since JDK1.5
 * 
 */
package keel.Algorithms.Preprocess.Basic;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;

import org.core.Files;

public class KNN {




    /**
	 * Prints output files.
	 * 
	 * @param filename Name of output file
	 * @param realClass Real output of instances
	 * @param prediction Predicted output for instances
	 */
	public static void writeOutput(String filename, int [][] realClass, int [][] prediction, Attribute inputs[], Attribute output, String relation) {
	
		String text = "";
		
                    
		/*Printing input attributes*/
		text += "@relation "+ relation +"\n";

		for (int i=0; i<inputs.length; i++) {
			
			text += "@attribute "+ inputs[i].getName()+" ";
			
		    if (inputs[i].getType() == Attribute.NOMINAL) {
		    	text += "{";
		        for (int j=0; j<inputs[i].getNominalValuesList().size(); j++) {
		        	text += (String)inputs[i].getNominalValuesList().elementAt(j);
		        	if (j < inputs[i].getNominalValuesList().size() -1) {
		        		text += ", ";
		        	}
		        }
		        text += "}\n";
		    } else {
		    	if (inputs[i].getType() == Attribute.INTEGER) {
		    		text += "integer";
		        } else {
		        	text += "real";
		        }
		        text += " ["+String.valueOf(inputs[i].getMinAttribute()) + ", " +  String.valueOf(inputs[i].getMaxAttribute())+"]\n";
		    }
		}

		/*Printing output attribute*/
		text += "@attribute "+ output.getName()+" ";

		if (output.getType() == Attribute.NOMINAL) {
			text += "{";
			
			for (int j=0; j<output.getNominalValuesList().size(); j++) {
				text += (String)output.getNominalValuesList().elementAt(j);
		        if (j < output.getNominalValuesList().size() -1) {
		        	text += ", ";
		        }
			}		
			text += "}\n";	    
		} else {
		    text += "integer ["+String.valueOf(output.getMinAttribute()) + ", " + String.valueOf(output.getMaxAttribute())+"]\n";
		}

		/*Printing data*/
		text += "@data\n";

		Files.writeFile(filename, text);
		
		if (output.getType() == Attribute.INTEGER) {
			
			text = "";
			
			for (int i=0; i<realClass.length; i++) {
			      
			      for (int j=0; j<realClass[0].length; j++){
			    	  text += "" + realClass[i][j] + " ";
			      }
			      for (int j=0; j<realClass[0].length; j++){
			    	  text += "" + prediction[i][j] + " ";
			      }
			      text += "\n";			      
			      if((i%10)==9){
			    	  Files.addToFile(filename, text);
			    	  text = "";
			      }     
			}			
			
			if((realClass.length%10)!=0){
				Files.addToFile(filename, text);
			}
		}
		else{
			
			text = "";
			
			for (int i=0; i<realClass.length; i++) {
			      
			      for (int j=0; j<realClass[0].length; j++){
			    	  text += "" + (String)output.getNominalValuesList().elementAt(realClass[i][j]) + " ";
			      }
			      for (int j=0; j<realClass[0].length; j++){
			    	  if(prediction[i][j]>-1){
			    		  text += "" + (String)output.getNominalValuesList().elementAt(prediction[i][j]) + " ";
			    	  }
			    	  else{
			    		  text += "" + "Unclassified" + " ";
			    	  }
			      }
			      text += "\n";
			      
			      if((i%10)==9){
			    	  Files.addToFile(filename, text);
			    	  text = "";
			      } 
			}			
			
			if((realClass.length%10)!=0){
				Files.addToFile(filename, text);
			}		
		}
		
	}//end-method 
	
        
    
    	/** 
	 * Calculates the Euclidean distance between two instances
	 * 
	 * @param instance1 First instance 
	 * @param instance2 Second instance
	 * @return The Euclidean distance
	 * 
	 */
	protected static double distance(double instance1[],double instance2[]){
		
		double length=0.0;

		for (int i=0; i<instance1.length; i++) {
			length += (instance1[i]-instance2[i])*(instance1[i]-instance2[i]);
		}
			
		length = Math.sqrt(length); 
				
		return length;
		
	} //end-method
        
             
    /** 
	 * Evaluates a instance to predict its class.
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted
	 * 
	 */
	public static int evaluate (double example[], double trainData[][],int nClasses,int trainOutput[],int k) {
	
		double minDist[];
		int nearestN[];
		int selectedClasses[];
		double dist;
		int prediction;
		int predictionValue;
		boolean stop;

		nearestN = new int[k];
		minDist = new double[k];
	
	    for (int i=0; i<k; i++) {
			nearestN[i] = 0;
			minDist[i] = Double.MAX_VALUE;
		}
		
	    //KNN Method starts here
	    
		for (int i=0; i<trainData.length; i++) {
		
		    dist = distance(trainData[i],example);

			if (dist > 0.0){ //leave-one-out
			
				//see if it's nearer than our previous selected neighbors
				stop=false;
				
				for(int j=0;j<k && !stop;j++){
				
					if (dist < minDist[j]) {
					    
						for (int l = k - 1; l >= j+1; l--) {
							minDist[l] = minDist[l - 1];
							nearestN[l] = nearestN[l - 1];
						}	
						
						minDist[j] = dist;
						nearestN[j] = i;
						stop=true;
					}
				}
			}
		}
		
		//we have check all the instances... see what is the most present class
		selectedClasses= new int[nClasses];
	
		for (int i=0; i<nClasses; i++) {
			selectedClasses[i] = 0;
		}	
		
		for (int i=0; i<k; i++) {
                 //   System.out.println("nearestN i ="+i + " =>"+nearestN[i]);
                  // System.out.println("trainOutput ="+trainOutput[nearestN[i]]);
                    
			selectedClasses[trainOutput[nearestN[i]]]+=1;
		}
		
		prediction=0;
		predictionValue=selectedClasses[0];
		
		for (int i=1; i<nClasses; i++) {
		    if (predictionValue < selectedClasses[i]) {
		        predictionValue = selectedClasses[i];
		        prediction = i;
		    }
		}
		
		return prediction;
	
	} //end-method	
    
	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param nClases Number of classes of the problem
	 *
	 * @return Class of the new instance
	 */	
	public static int evaluacionKNN (int nvec, double conj[][], int clases[], double ejemplo[], int nClases) {
		return evaluacionKNN2 (nvec,conj, clases, ejemplo, nClases);
		  
	} //end-method
	
	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param nClases Number of classes of the problem
	 *
	 * @return Class of the new instance
	 */	
	public static int evaluacionKNN2 (int nvec, double conj[][], int clases[], double ejemplo[], int nClases) {

		int i, j, l;
		boolean parar = false;
		int vecinosCercanos[];
		double minDistancias[];
		int votos[];
		double dist;
		int votada, votaciones;

		if (nvec > conj.length)
		  nvec = conj.length;

		votos = new int[nClases];
		vecinosCercanos = new int[nvec];
		minDistancias = new double[nvec];
		for (i=0; i<nvec; i++) {
		  vecinosCercanos[i] = -1;
		  minDistancias[i] = Double.POSITIVE_INFINITY;
		}
		for (i=0; i<conj.length; i++) {
		  dist = distancia(conj[i], ejemplo);
		  if (dist > 0) {
			parar = false;
			for (j = 0; j < nvec && !parar; j++) {
			  if (dist < minDistancias[j]) {
				parar = true;
				for (l = nvec - 1; l >= j+1; l--) {
					minDistancias[l] = minDistancias[l - 1];
					vecinosCercanos[l] = vecinosCercanos[l - 1];
				}
				minDistancias[j] = dist;
				vecinosCercanos[j] = i;
			  }
			}
		  }
		}

		for (j=0; j<nClases; j++) {
		  votos[j] = 0;
		}
		for (j=0; j<nvec; j++) {
		  if (vecinosCercanos[j] >= 0)
			votos[clases[vecinosCercanos[j]]] ++;
		}

		votada = 0;
		votaciones = votos[0];
		for (j=1; j<nClases; j++) {
		  if (votaciones < votos[j]) {
			votaciones = votos[j];
			votada = j;
		  }
		}

		return votada;
		  
	} //end-method

  
	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param nClases Number of classes of the problem
	 *
	 * @return The neighboors' classes.
	 */
	public static int[] evaluacionKNN3 (int nvec, double conj[][], int clases[], double ejemplo[], int nClases) {

	    int i, j, l;
	    boolean parar = false;
	    int vecinosCercanos[];
	    int clasesVecinosCercanos[];
	    double minDistancias[];
	    double dist;
	 
	    if (nvec > conj.length)
	      nvec = conj.length;

	    vecinosCercanos = new int[nvec];
	    clasesVecinosCercanos= new int[nvec];
	    minDistancias = new double[nvec];
	    for (i=0; i<nvec; i++) {
	      vecinosCercanos[i] = -1;
	      clasesVecinosCercanos[i]=-1;
	      minDistancias[i] = Double.POSITIVE_INFINITY;
	    }
	    for (i=0; i<conj.length; i++) {
	      dist = distancia(conj[i], ejemplo);
	      if (dist > 0) {
	        parar = false;
	        for (j = 0; j < nvec && !parar; j++) {
	          if (dist < minDistancias[j]) {
	            parar = true;
	            for (l = nvec - 1; l >= j+1; l--) {
	                minDistancias[l] = minDistancias[l - 1];
	                vecinosCercanos[l] = vecinosCercanos[l - 1];
	            }
	            minDistancias[j] = dist;
	            vecinosCercanos[j] = i;
	          }
	        }
	      }
	    }

	    for (j=0; j<vecinosCercanos.length; j++) {
	    	if(vecinosCercanos[j]!=-1)
	    	clasesVecinosCercanos[j] =clases[vecinosCercanos[j]];
	    }

	    return clasesVecinosCercanos;
		  
	} //end-method

	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param nClases Number of classes of the problem
	 * @param nVotos Maximun number of votes achieved
	 *
	 * @return Class of the new instance
	 */	  
	public static int evaluacionKNN2 (int nvec, double conj[][], int clases[], double ejemplo[], int nClases, Referencia nVotos) {

		int i, j, l;
		boolean parar = false;
		int vecinosCercanos[];
		double minDistancias[];
		int votos[];
		double dist;
		int votada, votaciones;

		if (nvec > conj.length)
		  nvec = conj.length;

		votos = new int[nClases];
		vecinosCercanos = new int[nvec];
		minDistancias = new double[nvec];
		for (i=0; i<nvec; i++) {
		  vecinosCercanos[i] = -1;
		  minDistancias[i] = Double.POSITIVE_INFINITY;
		}

		for (i=0; i<conj.length; i++) {
		  dist = distancia(conj[i], ejemplo);
		  if (dist > 0) {
			parar = false;
			for (j = 0; j < nvec && !parar; j++) {
			  if (dist < minDistancias[j]) {
				parar = true;
				for (l = nvec - 1; l >= j+1; l--) {
					minDistancias[l] = minDistancias[l - 1];
					vecinosCercanos[l] = vecinosCercanos[l - 1];
				}
				minDistancias[j] = dist;
				vecinosCercanos[j] = i;
			  }
			}
		  }
		}

		for (j=0; j<nClases; j++) {
		  votos[j] = 0;
		}

		for (j=0; j<nvec; j++) {
		  if (vecinosCercanos[j] >= 0)
			votos[clases[vecinosCercanos[j]]] ++;
		}

		votada = 0;
		votaciones = votos[0];
		for (j=1; j<nClases; j++) {
		  if (votaciones < votos[j]) {
			votaciones = votos[j];
			votada = j;
		  }
		}

		nVotos.entero = votaciones;
		return votada;
		  
	} //end-method
	
	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param nClases Number of classes of the problem
	 * @param vecinos Neighbors of the new instance
	 *
	 * @return Class of the new instance
	 */	
	public static int evaluacionKNN2 (int nvec, double conj[][], int clases[], double ejemplo[], int nClases, int vecinos[]) {
    
		int i, j, l;
		boolean parar = false;
		int vecinosCercanos[];
		double minDistancias[];
		int votos[];
		double dist;
		int votada, votaciones;

		if (nvec > conj.length)
		  nvec = conj.length;
		votos = new int[nClases];
		vecinosCercanos = new int[nvec];
		minDistancias = new double[nvec];
		for (i=0; i<nvec; i++) {
		  vecinosCercanos[i] = -1;
		  minDistancias[i] = Double.POSITIVE_INFINITY;
		}

		for (i=0; i<conj.length; i++) {
		  dist = distancia(conj[i], ejemplo);
		  if (dist > 0) {
			parar = false;
			for (j = 0; j < nvec && !parar; j++) {
			  if (dist < minDistancias[j]) {
				parar = true;
				for (l = nvec - 1; l >= j+1; l--) {
				  minDistancias[l] = minDistancias[l - 1];
				  vecinosCercanos[l] = vecinosCercanos[l - 1];
				}
				minDistancias[j] = dist;
				vecinosCercanos[j] = i;
			  }
			}
		  }
		}

		for (j=0; j<nClases; j++) {
		  votos[j] = 0;
		}
		for (j=0; j<nvec; j++) {
		  if (vecinosCercanos[j] >= 0)
			votos[clases[vecinosCercanos[j]]] ++;
		}

		votada = 0;
		votaciones = votos[0];
		for (j=1; j<nClases; j++) {
		  if (votaciones < votos[j]) {
			votaciones = votos[j];
			votada = j;
		  }
		}

		for (i=0; i<vecinosCercanos.length; i++)
		  vecinos[i] = vecinosCercanos[i];

		return votada;
		  
	} //end-method
	
  	/** 
	 * Calculates the Euclidean distance between two instances
	 * 
	 * @param ej1 First instance 
	 * @param ej2 Second instance
	 * @return The Euclidean distance
	 * 
	 */
	public static double distancia (double ej1[], double ej2[]) {

		int i;
		double suma = 0;

		for (i=0; i<ej1.length; i++) {
		  suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);
		}
		suma = Math.sqrt(suma);

		return suma;
		  
	} //end-method
	
  	/** 
	 * Calculates the unsquared Euclidean distance between two instances
	 * 
	 * @param ej1 First instance 
	 * @param ej2 Second instance
	 * @return The unsquared Euclidean distance
	 * 
	 */
	public static double distancia2 (double ej1[], double ej2[]) {

		int i;
		double suma = 0;

		for (i=0; i<ej1.length; i++) {
		  suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);
		}
		return suma;  
		  
	} //end-method


	/*****************************************************************************/
	/*Adapted Methods for HVDM distance*/
  
  
  
	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param real  Reference to the training set (real valued)
	 * @param nominal  Reference to the training set (nominal valued)	 
	 * @param nulos  Reference to the training set (null values)	
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param ejReal New instance to classifiy	 (real valued)
	 * @param ejNominal New instance to classifiy	 (nominal valued)	
	 * @param ejNulos New instance to classifiy	 (null values)	
	 * @param nClases Number of classes of the problem
	 * @param distance True= Euclidean distance; False= HVDM
	 *
	 * @return Class of the new instance
	 */	   
	public static int evaluacionKNN (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance) {
		  return evaluacionKNN2 (nvec, conj, real, nominal, nulos, clases, ejemplo, ejReal, ejNominal, ejNulos, nClases, distance);
		  
	} //end-method
	
 	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param real  Reference to the training set (real valued)
	 * @param nominal  Reference to the training set (nominal valued)	 
	 * @param nulos  Reference to the training set (null values)	
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param ejReal New instance to classifiy	 (real valued)
	 * @param ejNominal New instance to classifiy	 (nominal valued)	
	 * @param ejNulos New instance to classifiy	 (null values)	
	 * @param nClases Number of classes of the problem
	 * @param distance True= Euclidean distance; False= HVDM
	 *
	 * @return Class of the new instance
	 */	  
	public static int evaluacionKNN2 (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance) {

		  int i, j, l;
		  boolean parar = false;
		  int vecinosCercanos[];
		  double minDistancias[];
		  int votos[];
		  double dist;
		  int votada, votaciones;

		  if (nvec > conj.length)
			  nvec = conj.length;
		  votos = new int[nClases];
		  vecinosCercanos = new int[nvec];
		  minDistancias = new double[nvec];
		  for (i=0; i<nvec; i++) {
			  vecinosCercanos[i] = -1;
			  minDistancias[i] = Double.POSITIVE_INFINITY;
		  }

		  for (i=0; i<conj.length; i++) {
			  dist = distancia(conj[i], real[i], nominal[i], nulos[i], ejemplo, ejReal, ejNominal, ejNulos, distance);
			  if (dist > 0) {
				  parar = false;
				  for (j = 0; j < nvec && !parar; j++) {
					  if (dist < minDistancias[j]) {
						  parar = true;
						  for (l = nvec - 1; l >= j+1; l--) {
							  minDistancias[l] = minDistancias[l - 1];
							  vecinosCercanos[l] = vecinosCercanos[l - 1];
						  }
						  minDistancias[j] = dist;
						  vecinosCercanos[j] = i;
					  }
				  }
			  }
		  }

		  for (j=0; j<nClases; j++) {
			  votos[j] = 0;
		  }
		  for (j=0; j<nvec; j++) {
			  if (vecinosCercanos[j] >= 0)
				  votos[clases[vecinosCercanos[j]]] ++;
		  }
		  votada = 0;
		  votaciones = votos[0];
		  for (j=1; j<nClases; j++) {
			  if (votaciones < votos[j]) {
				  votaciones = votos[j];
				  votada = j;
			  }
		  }

		  return votada;
		  
	} //end-method
	
 	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param real  Reference to the training set (real valued)
	 * @param nominal  Reference to the training set (nominal valued)	 
	 * @param nulos  Reference to the training set (null values)	
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param ejReal New instance to classifiy	 (real valued)
	 * @param ejNominal New instance to classifiy	 (nominal valued)	
	 * @param ejNulos New instance to classifiy	 (null values)	
	 * @param nClases Number of classes of the problem
	 * @param distance True= Euclidean distance; False= HVDM
	 * @param nVotos Maximun number of votes achieved
	 *
	 * @return Class of the new instance
	 */	 
	public static int evaluacionKNN2 (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance, Referencia nVotos) {

		  int i, j, l;
		  boolean parar = false;
		  int vecinosCercanos[];
		  double minDistancias[];
		  int votos[];
		  double dist;
		  int votada, votaciones;

		  if (nvec > conj.length)
			  nvec = conj.length;
		  votos = new int[nClases];
		  vecinosCercanos = new int[nvec];
		  minDistancias = new double[nvec];
		  for (i=0; i<nvec; i++) {
			  vecinosCercanos[i] = -1;
			  minDistancias[i] = Double.POSITIVE_INFINITY;
		  }

		  for (i=0; i<conj.length; i++) {
			  dist = distancia(conj[i], real[i], nominal[i], nulos[i], ejemplo, ejReal, ejNominal, ejNulos, distance);
			  if (dist > 0) {
				  parar = false;
				  for (j = 0; j < nvec && !parar; j++) {
					  if (dist < minDistancias[j]) {
						  parar = true;
						  for (l = nvec - 1; l >= j+1; l--) {
							  minDistancias[l] = minDistancias[l - 1];
							  vecinosCercanos[l] = vecinosCercanos[l - 1];
						  }
						  minDistancias[j] = dist;
						  vecinosCercanos[j] = i;
					  }
				  }
			  }
		  }

		  for (j=0; j<nClases; j++) {
			  votos[j] = 0;
		  }
		  for (j=0; j<nvec; j++) {
			  if (vecinosCercanos[j] >= 0)
				  votos[clases[vecinosCercanos[j]]] ++;
		  }
		  votada = 0;
		  votaciones = votos[0];
		  for (j=1; j<nClases; j++) {
			  if (votaciones < votos[j]) {
				  votaciones = votos[j];
				  votada = j;
			  }
		  }

		  nVotos.entero = votaciones;
		  return votada;
		  
	} //end-method

  
 	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param real  Reference to the training set (real valued)
	 * @param nominal  Reference to the training set (nominal valued)	 
	 * @param nulos  Reference to the training set (null values)	
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param ejReal New instance to classifiy	 (real valued)
	 * @param ejNominal New instance to classifiy	 (nominal valued)	
	 * @param ejNulos New instance to classifiy	 (null values)	
	 * @param nClases Number of classes of the problem
	 * @param distance True= Euclidean distance; False= HVDM
	 *
	 * @return The neighboors' classes.
	 */	 
	public static int[] evaluacionKNN3 (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance) {

		  int i, j, l;
		  boolean parar = false;
		  int vecinosCercanos[];
		  int clasesVecinosCercanos[];
		  double minDistancias[];

		  double dist;


		  if (nvec > conj.length)
			  nvec = conj.length;

		  vecinosCercanos = new int[nvec];
		  clasesVecinosCercanos= new int[nvec];
		  minDistancias = new double[nvec];
		  for (i=0; i<nvec; i++) {
			  vecinosCercanos[i] = -1;
			  clasesVecinosCercanos[i]=-1;
			  minDistancias[i] = Double.POSITIVE_INFINITY;
		  }

		  for (i=0; i<conj.length; i++) {
			  dist = distancia(conj[i], real[i], nominal[i], nulos[i], ejemplo, ejReal, ejNominal, ejNulos, distance);
			  if (dist > 0) {
				  parar = false;
				  for (j = 0; j < nvec && !parar; j++) {
					  if (dist < minDistancias[j]) {
						  parar = true;
						  for (l = nvec - 1; l >= j+1; l--) {
							  minDistancias[l] = minDistancias[l - 1];
							  vecinosCercanos[l] = vecinosCercanos[l - 1];
						  }
						  minDistancias[j] = dist;
						  vecinosCercanos[j] = i;
					  }
				  }
			  }
		  }

			for (j=0; j<vecinosCercanos.length; j++) {
				if(vecinosCercanos[j]!=-1)
					clasesVecinosCercanos[j] =clases[vecinosCercanos[j]];
			}

			return clasesVecinosCercanos;
		  
	} //end-method

 	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param real  Reference to the training set (real valued)
	 * @param nominal  Reference to the training set (nominal valued)	 
	 * @param nulos  Reference to the training set (null values)	
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param ejReal New instance to classifiy	 (real valued)
	 * @param ejNominal New instance to classifiy	 (nominal valued)	
	 * @param ejNulos New instance to classifiy	 (null values)	
	 * @param nClases Number of classes of the problem
	 * @param distance True= Euclidean distance; False= HVDM
	 * @param vecinos Neighbors of the new instance
	 *
	 * @return Class of the new instance
	 */	   
	public static int evaluacionKNN2 (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance, int vecinos[]) {

		  int i, j, l;
		  boolean parar = false;
		  int vecinosCercanos[];
		  double minDistancias[];
		  int votos[];
		  double dist;
		  int votada, votaciones;

		  if (nvec > conj.length)
			  nvec = conj.length;
		  votos = new int[nClases];
		  vecinosCercanos = new int[nvec];
		  minDistancias = new double[nvec];
		  for (i=0; i<nvec; i++) {
			  vecinosCercanos[i] = -1;
			  minDistancias[i] = Double.POSITIVE_INFINITY;
		  }

		  for (i=0; i<conj.length; i++) {
			  dist = distancia(conj[i], real[i], nominal[i], nulos[i], ejemplo, ejReal, ejNominal, ejNulos, distance);
			  if (dist > 0) {
				  parar = false;
				  for (j = 0; j < nvec && !parar; j++) {
					  if (dist < minDistancias[j]) {
						  parar = true;
						  for (l = nvec - 1; l >= j+1; l--) {
							  minDistancias[l] = minDistancias[l - 1];
							  vecinosCercanos[l] = vecinosCercanos[l - 1];
						  }
						  minDistancias[j] = dist;
						  vecinosCercanos[j] = i;
					  }
				  }
			  }
		  }

		  for (j=0; j<nClases; j++) {
			  votos[j] = 0;
		  }
		  for (j=0; j<nvec; j++) {
			  if (vecinosCercanos[j] >= 0)
				  votos[clases[vecinosCercanos[j]]] ++;
		  }
		  votada = 0;
		  votaciones = votos[0];
		  for (j=1; j<nClases; j++) {
			  if (votaciones < votos[j]) {
				  votaciones = votos[j];
				  votada = j;
			  }
		  }

		  for (i=0; i<vecinosCercanos.length; i++)
			  vecinos[i] = vecinosCercanos[i];
		  
		  return votada;
		  
	} //end-method
	
  	/** 
	 * Calculates the HVDM distance between two instances
	 * 
	 * @param ej1 First instance 
	 * @param ej1Real First instance (Real valued)	 
	 * @param ej1Nom First instance (Nominal valued)	
	 * @param ej1Nul First instance (Null values)		 
	 * @param ej2 Second instance
	 * @param ej2Real First instance (Real valued)	 
	 * @param ej2Nom First instance (Nominal valued)	
	 * @param ej2Nul First instance (Null values)	
	 * @param Euc Use euclidean distance instead of HVDM
	 *
	 * @return The HVDM distance 
	 */
	public static double distancia (double ej1[], double ej1Real[], int ej1Nom[], boolean ej1Nul[], double ej2[], double ej2Real[], int ej2Nom[], boolean ej2Nul[], boolean Euc) {

		  int i;
		  double suma = 0;
			
		  if (Euc == true) {
			  for (i=0; i<ej1.length; i++) {
				  suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);
			  }
			  suma = Math.sqrt(suma);    
		  } else {
			  for (i=0; i<ej1.length; i++) {
				  if (ej1Nul[i] == true || ej2Nul[i] == true) {
					  suma += 1;
				  } else if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
					  suma += Metodo.nominalDistance[i][ej1Nom[i]][ej2Nom[i]];
				  } else {
					  suma += Math.abs(ej1Real[i]-ej2Real[i]) / 4*Metodo.stdDev[i];
				  }
			  }
			  suma = Math.sqrt(suma);       	
		  }

		  return suma;
		  
	} //end-method
	
  	/** 
	 * Calculates the unsquared HVDM distance between two instances
	 * 
	 * @param ej1 First instance 
	 * @param ej1Real First instance (Real valued)	 
	 * @param ej1Nom First instance (Nominal valued)	
	 * @param ej1Nul First instance (Null values)		 
	 * @param ej2 Second instance
	 * @param ej2Real First instance (Real valued)	 
	 * @param ej2Nom First instance (Nominal valued)	
	 * @param ej2Nul First instance (Null values)	
	 * @param Euc Use euclidean distance instead of HVDM
	 *
	 * @return The unsquared  HVDM distance 
	 */
    public static double distancia2 (double ej1[], double ej1Real[], int ej1Nom[], boolean ej1Nul[], double ej2[], double ej2Real[], int ej2Nom[], boolean ej2Nul[], boolean Euc) {

		  int i;
		  double suma = 0;
			
		  if (Euc == true) {
			  for (i=0; i<ej1.length; i++) {
				  suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);
			  }
		  } else {
			  for (i=0; i<ej1.length; i++) {
				  if (ej1Nul[i] == true || ej2Nul[i] == true) {
					  suma += 1;
				  } else if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
					  suma += Metodo.nominalDistance[i][ej1Nom[i]][ej2Nom[i]];
				  } else {
					  suma += Math.abs(ej1Real[i]-ej2Real[i]) / (4*Metodo.stdDev[i]);
				  }
			  }
		  }

		  return suma;
		  
	} //end-method

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
} //end-class




