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
* @author Written by Luciano Sanchez (University of Oviedo) 08/03/2004 
* @version 1.0 
* @since JDK1.4 
* </p> 
*/ 



package keel.Algorithms.Fuzzy_Rule_Learning.Random_Sets.FSS98;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Algorithms.SimulatedAnnealing;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Model.FuzzyGAPModelIndividual;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;
import org.core.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;




public class FSS98 {
	/** 
	* <p> 
	* ModelFuzzySAP is intended to generate a Fuzzy Rule Based System (FRBS)
	* regression model using the fuzzy random sets regression algorithm. 
	* 
	* This class makes used of the following classes:
	*      {@link RSFSS}: the model to be learned
	*
	* Detailed in:
	*
	* L. Sánchez. A Random Sets-Based Method for Identifying Fuzzy Models. Fuzzy Sets
	* and Systems 98:3 (1998) 343-354.
	* 
	* </p> 
	*/ 
	//The Randomize object used in this class
	static Randomize rand;
	
	/** 
	* <p> 
	* This private static method extracts the dataset and the method's parameters  
	* from the KEEL environment, carries out with the partitioning of the
	* input and output spaces, learn the FRBS model --which is a 
	* {@link RSFSS} instance-- using the random sets regression algorithm and print
	* out the results with the validation dataset. 
	*	* 
	* </p> 
	* @param tty  unused boolean parameter, kept for compatibility
	* @param pc   {@link ProcessConfig} object to obtain the train and test datasets
	*             and the method's parameters.
	*/ 	
	public static void fuzzyFSSmodeling(boolean tty,ProcessConfig pc) {
        
        
        try {
            
            String readALine = new String();
            
            ProcessDataset pd=new ProcessDataset();
            
            readALine=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
            
            if (pc.parNewFormat) pd.processModelDataset(readALine,true);
            else pd.oldClassificationProcess(readALine);
            
            int nData=pd.getNdata();           // Number of examples
            int nVariables=pd.getNvariables();   // Number of variables
            int nInputs=pd.getNinputs();     // Number of inputs
            int nsalidas=1;
                        
            double[][] X = pd.getX();             // Input data
            double[] Y = pd.getY();               // Output data
			double[] Yt = new double[Y.length];
            pd.showDatasetStatistics();
            
            
            double[] inputMaximum = pd.getImaximum();   // Maximum and Minimum for input data
            double[] inputMinimum = pd.getIminimum();
            
            double outputMaximum = pd.getOmaximum();     // Maximum and Minimum for output data
            double outputMinimum = pd.getOminimum();
            
            int nc=pc.parRuleNumber;
            
            // Conjuged gradient optimization
            RSFSS rs=new RSFSS(X,Y);
            
            rs.RSFSSX2(nc,rand,pc.parSigma);
            
            double error=0;
            for (int i=0;i<nData;i++) {
                double theEvaluation[]=rs.getOutput(X[i]);   
                error+=(theEvaluation[0]-Y[i])*(theEvaluation[0]-Y[i]);
				Yt[i]=theEvaluation[0];
            }
            error/=nData;
			pc.trainingResults(Y,Yt);
            System.out.println("MSE Train = "+error);
            
            // Test error
            ProcessDataset pdt = new ProcessDataset();
            int nTest,nTestInputs,nTestVariables;
            readALine=(String)pc.parInputData.get(ProcessConfig.IndexTest);
            
            if (pc.parNewFormat) pdt.processModelDataset(readALine,false);
            else pdt.oldClassificationProcess(readALine);
            
            nTest = pdt.getNdata();
            nTestVariables = pdt.getNvariables();
            nTestInputs = pdt.getNinputs();
            pdt.showDatasetStatistics();
            
            if (nTestInputs!=nInputs) throw new IOException("IOERR Test file");
            
            double[][] Xp=pdt.getX(); double [] Yp=pdt.getY(); double []Yo=new double[Yp.length];
            
            double RMS=0;
            for (int i=0;i<nTest;i++) {
                double theEvaluation[]=rs.getOutput(Xp[i]);   
                RMS+=(theEvaluation[0]-Yp[i])*(theEvaluation[0]-Yp[i]);
                Yo[i]=theEvaluation[0];
            }
            RMS/=nTest;
            
            System.out.println("MSE Test = "+RMS);
            pc.results(Yp,Yo); 
            
        } catch(FileNotFoundException e) {
            System.err.println(e+" File not found");
        } catch(IOException e) {
            System.err.println(e+" Read Error");
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
		FSS98 wm=new FSS98();
		wm.fuzzyFSSmodeling(tty,pc);
		
	}
	
	
}

