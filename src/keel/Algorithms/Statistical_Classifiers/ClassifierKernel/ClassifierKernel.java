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
* @author Written by Luciano Sanchez (University of Oviedo) 01/01/2004
* @author Modified by Jose Otero (University of Oviedo) 01/12/2008
* @version 1.0
* @since JDK1.5
* </p>
*/

package keel.Algorithms.Statistical_Classifiers.ClassifierKernel;
import keel.Algorithms.Shared.Parsing.*;

import org.core.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;




public class ClassifierKernel {
	/**
	* <p>
	* In this class, a kernel classifier is implemented 
	* </p>
	*/		
	static Randomize rand;
	/**
	* <p>
	* In this method, a kernel classifier is estimated
	* @param tty  unused boolean parameter, kept for compatibility
	* @param pc   {@link ProcessConfig} object to obtain the train and test datasets
	*             and the method's parameters.
	* </p>
	*/ 
	private static void kernelClassifier(boolean tty, ProcessConfig pc) {
        
        try {
            
            
            String line;
            ProcessDataset pd=new ProcessDataset();
            
            line=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
            
            if (pc.parNewFormat) pd.processClassifierDataset(line,true);
            else pd.oldClusteringProcess(line);
            
            int nData=pd.getNdata();           // Number of examples
            int nVariables=pd.getNvariables();   // Number of variables
            int nInputs=pd.getNinputs();     // Number of inputs
            
            pd.showDatasetStatistics();
            
            double[][] X = pd.getX();             // Input data
            int[] C = pd.getC();                  // Output data
			int [] Ct=new int[C.length];
            int nClass = pd.getNclasses();        // Number of classes
            
            double[] maxInput = pd.getImaximum();   // Scaling data
            double[] minInput = pd.getIminimum();
            int[] nInputFolds=new int[nInputs];
            
            double s;
            s=pc.parKernel;
            
            System.out.println("Sigma="+s);
            
			for (int i=0;i<X.length;i++) Ct[i]=-1;
            Kernel K= new Kernel(X,C,s,nClass);
            double faults=0;
            try {
                // Classifier is estimated
                for (int i=0;i<X.length;i++) {
                    int theClass=K.internalClassifier(X[i]);
                    if (theClass!=C[i]) faults++;
					Ct[i]=theClass;
                }
                faults/=nData;
								
                
                System.out.println("Train error="+faults);
                
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            pc.trainingResults(C,Ct);

            
            // Algorithm is evaluated using test set
            ProcessDataset pdt = new ProcessDataset();
            int nTest,npInputs,npVariables;
            
            line=(String)pc.parInputData.get(ProcessConfig.IndexTest);
            
            if (pc.parNewFormat) pdt.processClassifierDataset(line,false);
            else pdt.oldClusteringProcess(line);
            
            nTest = pdt.getNdata();
            npVariables = pdt.getNvariables();
            npInputs = pdt.getNinputs();
            pdt.showDatasetStatistics();
            
            if (npInputs!=nInputs) throw new IOException("IOERR in test");
            
            double[][] Xp=pdt.getX(); int [] Cp=pdt.getC(); int [] Co=new int[Cp.length];
            
            // Classifier error in test
            try {
                faults=0;
                for (int i=0;i<Xp.length;i++) {
                    int clase=K.internalClassifier(Xp[i]);
                    if (clase!=Cp[i]) faults++;
                    Co[i]=clase;
                }
                faults/=Cp.length;
                
                System.out.println("Test error="+faults);
				
                
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            pc.results(Cp,Co);
            
        } catch(FileNotFoundException e) {
            System.err.println(e+" Examples file not found");
        } catch(IOException e) {
            System.err.println(e+" Read error");
        }
    }
    
	/**
	* <p>
	* This method runs {@link ClassifierKernel}
	* @param args Vector of string with command line arguments
	* </p>
	*/ 
	public static void main(String args[]) {
		
		boolean tty=false;
		ProcessConfig pc=new ProcessConfig();
		System.out.println("Reading configuration file: "+args[0]);
		if (pc.fileProcess(args[0])<0) return;
		int algo=pc.parAlgorithmType;
		rand=new Randomize();
		rand.setSeed(pc.parSeed);
		ClassifierKernel ck=new ClassifierKernel();
		ck.kernelClassifier(tty,pc);
		
	}
	
	
}

