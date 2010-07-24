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
 * @author Written by Jose A. Saez Munoz, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * Date: 06/01/10
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Preprocess.NoiseFilters.PANDA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;
import org.core.Randomize;
import keel.Algorithms.Genetic_Rule_Learning.Globals.FileManagement;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.InstanceSet;


/**
 * <p>
 * This noise detection algorithm, PANDA, seeks to identify those instances with a large deviation from normal given the
 * values of a pair of attributes. When a set of instances have similar values for one attribute, large deviations from
 * normal for the second attribute may be considered suspicious. The output of PANDA is a list of instances ordered from
 * most noisy to least noisy. Each instance is assigned an output score (Noise Factor), which is used to rank the instance
 * relative to the other instances in the data set. After obtaining a noise ranking, some of the instances may be discarded
 * from the data set, which would result in a cleaner data set with which to perform additional analysis.
 * Reference: 2007-Hulse-KIS
 * </p>
 */
public class PANDA {
	
	private double[] noiseFactor;
	private int[][] discretized;
	private int[][] ranking;
	private int numActualIntervals;
	private Vector noisyInstances;
	private int numErrors;


//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * Constructor of the class 
	 * </p>
	 */
	public PANDA(){
		
		Randomize.setSeed(Parameters.seed);
		
		// read the training file
		Parameters.is = null;
		try {	
			Parameters.is = new InstanceSet();
			Parameters.is.readSet(Parameters.trainInputFile, true);
        }catch(Exception e){
        	System.out.println(e.toString());
            System.exit(1);
        }
        
        Parameters.numAttributes = Attributes.getInputNumAttributes();
        Parameters.instances = Parameters.is.getInstances();
        Parameters.numClasses = Attributes.getOutputAttribute(0).getNumNominalValues();
        Parameters.numInstances = Parameters.instances.length;
        
        // this method does not support categorical attributes
        Attribute[] attrs = Attributes.getInputAttributes();
        for(int i = 0 ; i < Parameters.numAttributes ; ++i){
        	if(attrs[i].getType() == Attribute.NOMINAL){
        		System.out.println("\n\nError: categorical attributes are not allowed in the data set");
        		System.exit(1);
        	}
        }

        numActualIntervals = Parameters.minIntervals;
        ranking = new int[Parameters.numExecutions][Parameters.numInstances];
        discretized = new int[Parameters.numInstances][Parameters.numAttributes];
	}
	
	
	public void run(){
		
		int incIntervals = (Parameters.maxIntervals - Parameters.minIntervals) / (Parameters.numExecutions - 1);
		numErrors = (int)Math.round(Parameters.numInstances*Parameters.estimatedNoiseLevel);
		
		
		for(int ex = 0 ; ex < Parameters.numExecutions ; ++ex){
			
	        // apply the discretization
			UniformFrequencyDiscretizer disc = new UniformFrequencyDiscretizer(numActualIntervals);
	        disc.buildCutPoints(Parameters.is);
	        
	        for(int i = 0 ; i < Parameters.numInstances ; ++i)
	            for(int j = 0 ; j < Parameters.numAttributes ; ++j)
	            	discretized[i][j] = disc.discretize(j, Parameters.instances[i].getInputRealValues(j));
			
			// compute the ranking of noise	
			CalculateNoiseFactor();
			int[] pos = Quicksort.sort(noiseFactor, Parameters.numInstances, Quicksort.HIGHEST_FIRST);
			
			for(int i = 0 ; i < Parameters.numInstances ; ++i)
				ranking[ex][i] = pos[i];

			numActualIntervals += incIntervals;
		}
		
		
		noisyInstances = new Vector();
		
		// compute the mean of each ranking
		double[] meanRanking = new double[Parameters.numInstances];
		Arrays.fill(meanRanking, 0);
		for(int in = 0 ; in < Parameters.numInstances ; ++in){
			for(int ex = 0 ; ex < Parameters.numExecutions ; ++ex){
				meanRanking[in] += ranking[ex][in];
			}
			
			meanRanking[in] /= Parameters.numExecutions;
		}
		
		// sort the mean ranking
		int[] posmr = Quicksort.sort(meanRanking, Parameters.numInstances, Quicksort.LOWEST_FIRST);
		
		// tengo que devolver los primeros numErrors valores de posmr, ordenados, por ello aplico una nueva ordenacion
		double[] orden = new double[numErrors];
		for(int i = 0 ; i < numErrors ; ++i)
			orden[i] = posmr[i];
		
		int[] posUlt = Quicksort.sort(orden, numErrors, Quicksort.LOWEST_FIRST);
		
		
		for(int i = 0 ; i < numErrors ; ++i)
			noisyInstances.add((int)orden[posUlt[i]]);

		
		//System.out.println(noisyInstances);
		createDatasets(Parameters.trainInputFile,Parameters.trainOutputFile,Parameters.testInputFile,Parameters.testOutputFile);
	}
	
	public void CalculateNoiseFactor(){
		
		double[][][] s;
		
		// initialization
        s = new double[Parameters.numInstances][Parameters.numAttributes][Parameters.numAttributes];
        
        for(int i = 0 ; i < Parameters.numInstances ; ++i)
        	for(int k = 0 ; k < Parameters.numAttributes ; ++k)
            	for(int j = 0 ; j < Parameters.numAttributes ; ++j)
            		s[i][k][j] = 0;
        

        double[] mean = new double[numActualIntervals];
        double[] desv = new double[numActualIntervals];
        
        
        // method
        for(int j = 0 ; j < Parameters.numAttributes ; ++j){
        	
            for(int k = 0 ; k < Parameters.numAttributes ; ++k){
            
            	if( k != j ){
            		
            		// calcular la media y la desviacion tipica (real) de cada intervalo (en k)
            		for(int l = 0 ; l < numActualIntervals ; ++l){
            			mean[l] = computeMean(j,k,l);
            			desv[l] = computeDesv(j,k,l,mean[l]);
            		}
            		
                    for(int i = 0 ; i < Parameters.numInstances ; ++i){
                    	if(desv[discretized[i][j]] != 0)
                    		s[i][k][j] = Math.abs(Parameters.instances[i].getInputRealValues(k)-mean[discretized[i][j]]) / desv[discretized[i][j]];
                    	else
                    		s[i][k][j] = 0;
                    }
            		
            	}
            	
            }
        }
        
        
        noiseFactor = new double[Parameters.numInstances];
        Arrays.fill(noiseFactor, 0);
        
        
        if(Parameters.function.equals("sum")){
        	
            for(int i = 0 ; i < Parameters.numInstances ; ++i){
            	for(int k = 0 ; k < Parameters.numAttributes ; ++k){
            		for(int j = 0 ; j < Parameters.numAttributes ; ++j){
            			noiseFactor[i] += s[i][k][j];
            		}
            	}
            }
        }
		
        else if (Parameters.function.equals("max")){
        	
            for(int i = 0 ; i < Parameters.numInstances ; ++i){
            	double max = s[i][0][0];
            	for(int k = 0 ; k < Parameters.numAttributes ; ++k){
            		for(int j = 0 ; j < Parameters.numAttributes ; ++j){
            			if(s[i][k][j] > max)
            				max = s[i][k][j];
            		}
            	}
            	
            	noiseFactor[i] = max;
            }
        }
		
	}
	
	public double computeMean(int j, int k, int l){
		
		double mean = 0;
		int numExamples = 0;
		
		for(int i = 0 ; i < Parameters.numInstances ; ++i){
			
			if(discretized[i][j] == l){
				numExamples++;
				mean += Parameters.instances[i].getInputRealValues(k);
			}
		}
		
		if(numExamples > 0)
			mean /= numExamples;
		
		return mean;
	}
	
	
	public double computeDesv(int j, int k, int l, double mean){
		
		double sum = 0;
		int numExamples = 0;
		
		for(int i = 0 ; i < Parameters.numInstances ; ++i){
			
			if(discretized[i][j] == l){
				numExamples++;
				sum += Math.pow(Parameters.instances[i].getInputRealValues(k)-mean, 2);
			}
		}
		
		if(numExamples > 0){
			sum /= (numExamples-1);
			sum = Math.sqrt(sum);
		}
		
		return sum;
	}
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It apllies the changes to remove the noise 
	 * </p>
	 */
	public void createDatasets(String trainIN, String trainOUT, String testIN, String testOUT){
		
		
		// to create the train file-----------------------------------------
  		String header = "";
  		header = "@relation " + Attributes.getRelationName() + "\n";
  		header += Attributes.getInputAttributesHeader();
  		header += Attributes.getOutputAttributesHeader();
  		header += Attributes.getInputHeader() + "\n";
  		header += Attributes.getOutputHeader() + "\n";
        header += "@data\n";
		
		FileManagement fm = new FileManagement();
		Attribute []att = Attributes.getInputAttributes();

		try {
			
			fm.initWrite(trainOUT);
			fm.writeLine(header);
			
			int numNoisyEx = 0;
			for(int i = 0 ; i < Parameters.instances.length ; i++){
				
				if( (numNoisyEx < noisyInstances.size()) && (Integer)noisyInstances.get(numNoisyEx) == i){
					numNoisyEx++;
				}
				
				else{
					
					boolean[] missing = Parameters.instances[i].getInputMissingValues();
					String newInstance = "";
					
					for(int j = 0 ; j < Parameters.numAttributes ; j++){
						
						if(missing[j])
							newInstance += "?";
						
						else{
							if(att[j].getType() == Attribute.REAL)
								newInstance += Parameters.instances[i].getInputRealValues(j);
							if(att[j].getType() == Attribute.INTEGER)
								newInstance += (int)Parameters.instances[i].getInputRealValues(j);
							if(att[j].getType() == Attribute.NOMINAL)
								newInstance += Parameters.instances[i].getInputNominalValues(j);
						}
						
						newInstance += ", "; 
					}
					
					String className = Parameters.instances[i].getOutputNominalValues(0);
					newInstance += className + "\n";
					
					fm.writeLine(newInstance);
				}
			}
				
			fm.closeWrite();
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		
		
		// to create the test file-----------------------------------------
		try {
			String s;
			File Archi1 = new File(testIN);
		    File Archi2 = new File(testOUT);
		    BufferedReader in;
			in = new BufferedReader(new FileReader(Archi1));
		    PrintWriter out = new PrintWriter(new FileWriter(Archi2));
		      
		    while ((s = in.readLine()) != null)
		    	out.println(s);
		    
		    in.close();
		    out.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

}
