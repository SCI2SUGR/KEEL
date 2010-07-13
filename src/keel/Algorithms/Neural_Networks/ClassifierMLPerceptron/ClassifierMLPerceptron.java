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
* @author Written by Luciano Sánchez (University of Oviedo) 21/07/2005
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Neural_Networks.ClassifierMLPerceptron;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Shared.ClassicalOptim.*;
import org.core.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

public class ClassifierMLPerceptron {
	/** 
	* <p> 
	* Classification model by means of a multi-layered perceptron. 
	* This class is a wrapper for classification problems to solve using conjugated gradient algorithm.
	* 
	* </p> 
	*/
	//Random seed generator
	static Randomize rand;
	/** 
     * <p> 
     *  Returns the index corresponding to the maximum value of vector x.
     * 
     * </p>
     * @param x the individual whose maximum is to be calculated. 
     * @return the index corresponding to the maximum value of vector x. 
     */ 
	private static int argmax(double []x) {
		double max=x[0]; int imax=0;
		for (int i=1;i<x.length;i++)
			if (x[i]>max) { max=x[i]; imax=i; }
				return imax;
	}
	
	/** 
     * <p> 
     *  Method that extracts the required data from the ProcessConfig object pc for instancing a Neural Network GCNet. 
     *  Then, the net will be trained using method GCNet.nntrain and after that the classification training and testing 
     *  error will be calculated.
     * 
     * </p>
     *  @param tty  unused boolean parameter, kept for compatibility.
     *  @param pc   ProcessConfig object to obtain the train and test datasets
     *              and the method's parameters.
     */ 
	private static void neuralClassificationLS(boolean tty, ProcessConfig pc) {
        
        try {
            String line;
            ProcessDataset pd=new ProcessDataset();
            
            line=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
            
            if (pc.parNewFormat) pd.processClassifierDataset(line,true);
            else pd.oldClusteringProcess(line);
            
            int nData=pd.getNdata();           // Number of examples
            int nVariables=pd.getNvariables();  // Number of variables
            int nInputs=pd.getNinputs();     	// Number of inputs
            
            pd.showDatasetStatistics();
              
			double[][] X = pd.getX();             	// Input data
            int[] C = pd.getC();                  	// Output data
			int [] Ct=new int[C.length];
            int nClasses = pd.getNclasses();        	// Number of classes
            
            double[] eMaximum = pd.getImaximum();   	// Maximum and Minimum for input data
            double[] eMinimum = pd.getIminimum();
            int[] nIpartition=new int[nInputs];     // Input partition sizes
            
            double Cbin[][] = new double[nData][nClasses];
            for (int i=0;i<nData;i++) Cbin[i][C[i]]=1;
            
            
            // multi-layer perceptron
            int []elements; int nLayers;
			
			
			{ elements=pc.parNetTopo; nLayers=elements.length; }
            
            // Weight vector (return value)
            int weightDimension=0;
            if (nLayers==0) {
                weightDimension=(nInputs+1)*(nClasses);
            } else {
                weightDimension=(nInputs+1)*elements[0];
                for (int i=1;i<nLayers;i++) 
                    weightDimension+=(elements[i-1]+1)*(elements[i]);
                weightDimension+=(nClasses)*(elements[nLayers-1]+1);
            }
            double []weights=new double[weightDimension];
            
            // Conjuged gradient optimization
            GCNet gcn=new GCNet();
            double error=gcn.nntrain(nInputs,1,X,Cbin,elements,weights,rand);
            
            double nfaults=0;
            try {
                for (int i=0;i<X.length;i++) {
                    double[] resp=gcn.nnoutput(X[i]);
                    int clase=argmax(resp);
                    if (clase!=C[i]) nfaults++;
					Ct[i]=clase;
                }
                nfaults/=nData;
				pc.trainingResults(C,Ct);
                
                System.out.println("Train error="+nfaults);
                
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            
            
            // Algorithm is evaluated using test set
            ProcessDataset pdt = new ProcessDataset();
            int nprueba,npentradas,npvariables;
            line=(String)pc.parInputData.get(ProcessConfig.IndexTest);
            
            if (pc.parNewFormat) pdt.processClassifierDataset(line,false);
            else pdt.oldClusteringProcess(line);
            
            nprueba = pdt.getNdata();
            npvariables = pdt.getNvariables();
            npentradas = pdt.getNinputs();
            pdt.showDatasetStatistics();
            
            if (npentradas!=nInputs) throw new IOException("Test file IOERR");
            
            double[][] Xp=pdt.getX(); int [] Cp=pdt.getC(); int [] Co=new int [Cp.length];
            
            // Test
            try {
                nfaults=0;
                for (int i=0;i<Xp.length;i++) {
                    double[] resp=gcn.nnoutput(Xp[i]);
                    int clase=argmax(resp);
                    if (clase!=Cp[i]) nfaults++;
                    Co[i]=clase;
                }
                nfaults/=Cp.length;
                
                System.out.println("Test error="+nfaults);
                
                
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            pc.results(Cp,Co);
            
        } catch(FileNotFoundException e) {
            System.err.println(e+" File not found");
        } catch(IOException e) {
            System.err.println(e+" Read Error");
        }
    }
    
	
	/** 
     * <p> 
     *  Method that calls the private wrapper method "neuralClassificationLS" that creates and runs a neural network for solving 
     *  a classification problem using the Conjugated Gradient algorithm.
     * 
     * 
     * </p>
     *  @param args command line parameters with the name of configuration file with the information 
     *  			for classification process in position arg[0].
     */ 
	public static void main(String args[]) {
		
		boolean tty=false;
		ProcessConfig pc=new ProcessConfig();
		System.out.println("Reading configuration file: "+args[0]);
		if (pc.fileProcess(args[0])<0) return;
		int algo=pc.parAlgorithmType;
		rand=new Randomize();
		rand.setSeed(pc.parSeed);
		ClassifierMLPerceptron cl=new ClassifierMLPerceptron();
		cl.neuralClassificationLS(tty,pc);
		
	}
	
	
}

