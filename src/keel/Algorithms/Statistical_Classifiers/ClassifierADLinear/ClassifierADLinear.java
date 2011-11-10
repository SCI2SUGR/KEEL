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
package keel.Algorithms.Statistical_Classifiers.ClassifierADLinear;
import keel.Algorithms.Statistical_Classifiers.Shared.DiscrAnalysis.*;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Shared.Exceptions.*;
import org.core.*;
import java.io.*;

public class ClassifierADLinear {
	/**
	* <p>
	* In this class, a classifier using Linear Discriminant Analysis is implemented 
	* </p>
	*/
	static Randomize rand;
	/**
	* <p>
	* In this method, a classifier is estimated using Linear Discriminant Analysis
	* @param tty  unused boolean parameter, kept for compatibility
	* @param pc   {@link ProcessConfig} object to obtain the train and test datasets
	*             and the method's parameters.
	* </p>
	*/   
    private static void lda(boolean tty, ProcessConfig pc) {
        
        try {
                        
            String line;
            ProcessDataset pd=new ProcessDataset();
            
            line=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
            
            if (pc.parNewFormat) pd.processClassifierDataset(line,true);
            else pd.oldClusteringProcess(line);
            
            int nData=pd.getNdata();           // Number of examples
            int nVariables=pd.getNvariables();   // Number of variables
            int nInputs=pd.getNinputs();     // Number of inputs
            
            double[][] X = pd.getX();             // Input data
            int[] C = pd.getC();                  // Output data
			int [] Ct=new int[C.length];
            int nClasses = pd.getNclasses();        // Number of classes
            pd.showDatasetStatistics();
            
            double[] maxInput = pd.getImaximum();   // Maximum and minimum for input data
            double[] minInput = pd.getIminimum();
            int[] nInputFolds=new int[nInputs];
            
            // A vector is generated with classes 1 bit between n codified 
            double Cbin[][] = new double[nData][nClasses];
            for (int i=0;i<nData;i++) {
			  Cbin[i][C[i]]=1;
			}
            
			for (int i=0;i<X.length;i++) Ct[i]=-1;
			
            AD adlin = new AD(X,Cbin);
            double faults=0;
            try {
                // Classifier is estimated
                boolean lineal=true;
                adlin.computeParameter(lineal);
                for (int i=0;i<X.length;i++) {
                    double[] resp=adlin.distances(X[i]);
                    int theClass=adlin.argmax(resp);
                    if (theClass!=C[i]) faults++;
					Ct[i]=theClass;
                }
                faults/=nData;
	
                System.out.println("Train error="+faults);
                
            } catch (Exception e) {
                System.out.println(e.toString());
            }
			
			pc.trainingResults(C,Ct);
            
            // Algorithm is evaluated over test set
            ProcessDataset pdt = new ProcessDataset();
            int nTest,npInputs,npVariables;
            
            line=(String)pc.parInputData.get(ProcessConfig.IndexTest);
            
            if (pc.parNewFormat) pdt.processClassifierDataset(line,false);
            else pdt.oldClusteringProcess(line);
            
            nTest = pdt.getNdata();
            npVariables = pdt.getNvariables();
            npInputs = pdt.getNinputs();
            pdt.showDatasetStatistics();
            
            if (npInputs!=nInputs) throw new IOException("IOERR test file");
            
            double[][] Xp=pdt.getX(); int [] Cp=pdt.getC(); int [] Co=new int[Cp.length];
            
            // Accuracy system test
            try {
                faults=0;
                for (int i=0;i<Xp.length;i++) {
                    double[] resp=adlin.distances(Xp[i]);
                    int aClass=adlin.argmax(resp);
                    if (aClass!=Cp[i]) faults++;
                    Co[i]=aClass;
                }
                faults/=Xp.length;
                
                System.out.println("test error="+faults);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            pc.results(Cp,Co);
            
        } catch(FileNotFoundException e) {
            System.err.println(e+" Train file not found");
        } catch(IOException e) {
            System.err.println(e+" Read Error");
        }
    }
    
	/**
	* <p>
	* This method runs {@link ClassifierADLinear}
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
		ClassifierADLinear a=new ClassifierADLinear();
		a.lda(tty,pc);
	}
		
}

