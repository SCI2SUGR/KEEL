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

package keel.Algorithms.Neural_Networks.ModelMLPerceptron;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Shared.ClassicalOptim.*;
import org.core.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;




public class ModelMLPerceptron {
	/** 
	* <p> 
	* Regression model by means of a multi-layered perceptron. 
	* This class is a wrapper for regression problems to solve using conjugated gradient algorithm.
	* 
	* </p> 
	*/
	//Random seed generator
	static Randomize rand;
	/** 
     * <p> 
     *  Method that extracts the required data from the ProcessConfig object pc for instancing a Neural Network GCNet. 
     *  Then, the net will be trained using method GCNet.nntrain and after that the regression training and testing 
     *  error will be calculated.
     * 
     * </p>
     *  @param tty  unused boolean parameter, kept for compatibility.
     *  @param pc   ProcessConfig object to obtain the train and test datasets
     *              and the method's parameters.
     */ 
	private static void neuralModelling(boolean tty, ProcessConfig pc) {
        
        try {
            
            String line = new String();
            
            ProcessDataset pd=new ProcessDataset();
            
            line=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
            
            if (pc.parNewFormat) pd.processModelDataset(line,true);
            else pd.oldClassificationProcess(line);
            
            int nData=pd.getNdata();           	// Number of examples
            int nVariables=pd.getNvariables();   	// Number of variables
            int nInputs=pd.getNinputs();     		// Number of inputs
            int nOutputs=1;
            
            double[][] X = pd.getX();             	// Input data
            double[] Y = pd.getY();               	// Output data
			double[] Yt = new double[Y.length];
            pd.showDatasetStatistics();
            
            double Y1[][] = new double [Y.length][1];
            for (int i=0;i<nData;i++) Y1[i][0]=Y[i];
            
            double[] eMaximum = pd.getImaximum();   	// Maximum and minimum for input data
            double[] eMinimum = pd.getIminimum();
            
            double sMaximum = pd.getOmaximum();     	// Maximum and minimum for output data
            double sMinimum = pd.getOminimum();
            
            int []elements; int nLayers;
			elements=pc.parNetTopo; nLayers=elements.length;
            
            // Weight vector (return value)
            int weightDimension=0;
            if (nLayers==0) {
                weightDimension=(nInputs+1)*(nOutputs);
            } else {
                weightDimension=(nInputs+1)*elements[0];
                for (int i=1;i<nLayers;i++) 
                    weightDimension+=(elements[i-1]+1)*(elements[i]);
                weightDimension+=(nOutputs)*(elements[nLayers-1]+1);
            }
            double []weights=new double[weightDimension];
            
            GCNet gcn=new GCNet();
            double error=gcn.nntrain(nInputs,1,X,Y1,elements,weights,rand);
			for (int i=0;i<Yt.length;i++) {
				double output[]=gcn.nnoutput(X[i]);   
				Yt[i]=output[0];
			}
			pc.trainingResults(Y,Yt);
            
            // Result is printed
            System.out.println("MSE Train = "+error);
            
            // Test error
            ProcessDataset pdt = new ProcessDataset();
            int nTests,ntInputs,ntVariables;
			line=(String)pc.parInputData.get(ProcessConfig.IndexTest);
            
            if (pc.parNewFormat) pdt.processModelDataset(line,false);
            else pdt.oldClassificationProcess(line);
            
            nTests = pdt.getNdata();
            ntVariables = pdt.getNvariables();
            ntInputs = pdt.getNinputs();
            pdt.showDatasetStatistics();
            
            if (ntInputs!=nInputs) throw new IOException("IOERR Test file");
            
            double[][] Xp=pdt.getX(); double [] Yp=pdt.getY(); double [] Yo=new double[Yp.length];
            
            double RMS=0;
            for (int i=0;i<nTests;i++) {
                double output[]=gcn.nnoutput(Xp[i]);   
                RMS+=(output[0]-Yp[i])*(output[0]-Yp[i]);
                Yo[i]=output[0];
            }
            RMS/=nTests;
            
            System.out.println("Test MSE = "+RMS);
            pc.results(Yp,Yo); 
            
            
        } catch(FileNotFoundException e) {
            System.err.println(e+" File not found");
        } catch(IOException e) {
            System.err.println(e+" Read Error");
        }
        
    }
	
	
	
	/** 
     * <p> 
     *  Method that calls the private wrapper method "neuralModelling" that creates and runs a neural network for solving 
     *  a modelling problem using the Conjugated Gradient algorithm.
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
		ModelMLPerceptron cl=new ModelMLPerceptron();
		cl.neuralModelling(tty,pc);
		
	}
	
	
}

	
