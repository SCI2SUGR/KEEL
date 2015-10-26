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

/***********************************************************************

	This file is part of the Fuzzy Instance Based Learning package, a
	Java package implementing Fuzzy Nearest Neighbor Classifiers as 
	complementary material for the paper:
	
	Fuzzy Nearest Neighbor Algorithms: Taxonomy, Experimental analysis and Prospects

	Copyright (C) 2012
	
	J. Derrac (jderrac@decsai.ugr.es)
	S. García (sglopez@ujaen.es)
	F. Herrera (herrera@decsai.ugr.es)

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



package keel.Algorithms.Fuzzy_Instance_Based_Learning;

import java.util.Arrays;

import org.core.Files;

/**
 * 
 * File: ReportTool.java
 * 
 * Class to print reports about the results of the classification process
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2012 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
public class ReportTool{
	
	private static int trOrig [];
	private static int trPre [];
	private static int tsOrig [];
	private static int tsPre [];
	
	private static int testUnclassified;	
	private static int trainUnclassified;	
	private static int testConfMatrix[][];
	private static int trainConfMatrix[][];
	
	private static int nClasses;
	
	private static String fileName;
	
	/**
	 * Provide information about the classification process to the report tool
	 * 
	 * @param trainReal Vector with the real classes of the training data
	 * @param trainPrediction Vector with the predicted classes for the training data
	 * @param testReal Vector with the real classes of the test data
	 * @param testPrediction Vector with the predicted classes for the test data
	 * @param nClas Number of classes defined for the data
	 */
	public static void setResults(int [] trainReal,int [] trainPrediction,int [] testReal,int [] testPrediction, int nClas){
		
		trOrig= new int [trainReal.length];
		trPre= new int [trainPrediction.length];
		tsOrig= new int [testReal.length];
		tsPre= new int [testPrediction.length];
		
		for(int i=0;i< trainReal.length; i++){
			trOrig[i]=trainReal[i];
		}
		
		for(int i=0;i< trainPrediction.length; i++){
			trPre[i]=trainPrediction[i];
		}
		
		for(int i=0;i< testReal.length; i++){
			tsOrig[i]=testReal[i];
		}
		
		for(int i=0;i< testPrediction.length; i++){
			tsPre[i]=testPrediction[i];
		}
		
		nClasses=nClas;
		
	}//end-method 
	
	/**
	 * Prints the output report
	 * 
	 */
	public static void printReport(){

		String text="";	
		
		computeConfussionMatrixes();
		
		//Accuracy
		text+="Accuracy: "+getAccuracy()+"\n";
		text+="Accuracy (Training): "+getTrainAccuracy()+"\n";
		
		//Kappa
		text+="Kappa: "+getKappa()+"\n";
		text+="Kappa (Training): "+getTrainKappa()+"\n";
		
		//Unclassified
		text+="Unclassified instances: "+testUnclassified+"\n";
		text+="Unclassified instances (Training): "+trainUnclassified+"\n";	
	
		//Model time
		text+= "Model time: "+Timer.getModelTime()+"\n";
		
		//Training time
		text+= "Training time: "+Timer.getTrainingTime()+"\n";
		
		//Test time
		text+= "Test time: "+Timer.getTestTime()+"\n";
		
		//Confusion matrix
		text+="Confussion Matrix:\n";
		for(int i=0;i<nClasses;i++){
			
			for(int j=0;j<nClasses;j++){
				text+=testConfMatrix[i][j]+"\t";
			}
			text+="\n";
		}
		text+="\n";
		
		text+="Training Confussion Matrix:\n";
		for(int i=0;i<nClasses;i++){
			
			for(int j=0;j<nClasses;j++){
				text+=trainConfMatrix[i][j]+"\t";
			}
			text+="\n";
		}
		text+="\n";


		//Finish additional output file
		Files.writeFile (fileName, text);
		
	}//end-method 
	
	/**
	 * Computes the confusion matrixes
	 * 
	 */
	private static void computeConfussionMatrixes(){
		
		testConfMatrix= new int [nClasses][nClasses];
		trainConfMatrix= new int [nClasses][nClasses];
		
		testUnclassified=0;
		
		for(int i=0;i<nClasses;i++){
			Arrays.fill(testConfMatrix[i], 0);
		}
		
		for(int i=0;i<tsPre.length;i++){
			if(tsPre[i]==-1){
				testUnclassified++;
			}else{
				testConfMatrix[tsPre[i]][tsOrig[i]]++;
			}
		}
		
		trainUnclassified=0;
		
		for(int i=0;i<nClasses;i++){
			Arrays.fill(trainConfMatrix[i], 0);
		}
		
		for(int i=0;i<trPre.length;i++){
			if(trPre[i]==-1){
				trainUnclassified++;
			}else{
				trainConfMatrix[trPre[i]][trOrig[i]]++;
			}
		}
		
	}//end-method 
	
	/**
	 * Computes the accuracy obtained on test set
	 * 
	 * @return Accuracy on test set
	 */
	private static double getAccuracy(){
		
		double acc;
		int count=0;
		
		for(int i=0;i<nClasses;i++){			
			count+=testConfMatrix[i][i];
		}
		
		acc=((double)count/(double)tsOrig.length);
		
		return acc;
		
	}//end-method 
	
	/**
	 * Computes the accuracy obtained on the training set
	 * 
	 * @return Accuracy on test set
	 */
	private static double getTrainAccuracy(){
		
		double acc;
		int count=0;
		
		for(int i=0;i<nClasses;i++){			
			count+=trainConfMatrix[i][i];
		}
		
		acc=((double)count/(double)trOrig.length);
		
		return acc;
		
	}//end-method 
	
	/**
	 * Computes the Kappa obtained on test set
	 * 
	 * @return Kappa on test set
	 */	
	private static double getKappa(){
		
		double kappa;
		double agreement,expected;
		int count,count2;
		double prob1,prob2;
		
		count=0;
		for(int i=0;i<nClasses;i++){			
			count+=testConfMatrix[i][i];
		}
		
		agreement=((double)count/(double)tsOrig.length);
		
		expected=0.0;
		
		for(int i=0;i<nClasses;i++){			
			
			count=0;
			count2=0;
			
			for(int j=0;j<nClasses;j++){
				count+=testConfMatrix[i][j];
				count2+=testConfMatrix[j][i];
			}
			
			prob1=((double)count/(double)tsOrig.length);
			prob2=((double)count2/(double)tsOrig.length);
			
			expected+=(prob1*prob2);
		}

		kappa=(agreement-expected)/(1.0-expected);
		
		return kappa;
		
	}//end-method 

	/**
	 * Computes the Kappa obtained on test set
	 * 
	 * @return Kappa on test set
	 */	
	private static double getTrainKappa(){
		
		double kappa;
		double agreement,expected;
		int count,count2;
		double prob1,prob2;
		
		count=0;
		for(int i=0;i<nClasses;i++){			
			count+=trainConfMatrix[i][i];
		}
		
		agreement=((double)count/(double)trOrig.length);
		
		expected=0.0;
		
		for(int i=0;i<nClasses;i++){			
			
			count=0;
			count2=0;
			
			for(int j=0;j<nClasses;j++){
				count+=trainConfMatrix[i][j];
				count2+=trainConfMatrix[j][i];
			}
			
			prob1=((double)count/(double)trOrig.length);
			prob2=((double)count2/(double)trOrig.length);
			
			expected+=(prob1*prob2);
		}

		kappa=(agreement-expected)/(1.0-expected);
		
		return kappa;
		
	}//end-method 
	
	/**
	 * Sets the name of the output file to print the report
	 * 
	 * @param name Name of the file
	 */
	public static void setOutputFile(String name){
		
		fileName=name;
		
	}//end-method
	
	/**
	 * Adds external information to the report
	 * 
	 * @param contents Information to add
	 */
	public static void addToReport(String contents){
	
		Files.addToFile(fileName, contents);
		
	}//end-method

}//end-class
