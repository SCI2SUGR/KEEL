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


package keel.Algorithms.Statistical_Classifiers.ClassifierLinearLMS;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Statistical_Classifiers.Shared.DiscrAnalysis.*;
import keel.Algorithms.Statistical_Classifiers.Shared.MatrixCalcs.*;
import keel.Algorithms.Shared.ClassicalOptim.*;
import org.core.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;




public class ClassifierLinearLMS {
	/**
	* <p>
	* In this class, a linear classifier using Least Mean Squares is implemented 
	* </p>
	*/	
	static Randomize rand;
	/**
	* <p>
	* In this method, a linear classifier using Least Mean Squares is estimated
	* @param tty  unused boolean parameter, kept for compatibility
	* @param pc   {@link ProcessConfig} object to obtain the train and test datasets
	*             and the method's parameters.
	* </p>
	*/ 
	private static void linearClassifierLMS(boolean tty, ProcessConfig pc) {
        
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
            int nClasses = pd.getNclasses();        // Number of classes
            
            double[] maxInput = pd.getImaximum();   // Maximum and minimum for input data
            double[] minInput = pd.getIminimum();
            int[] nInputFolds=new int[nInputs];
            
            // A vector is generated with classes 1 bit between n codified
            double Cbin[][] = new double[nData][nClasses];
            for (int i=0;i<nData;i++) Cbin[i][C[i]]=1;
            for (int i=0;i<X.length;i++) Ct[i]=-1;
            
            // 1-layer perceptron
            
            int nLayers=0;
            
            int []ELEM=new int[nLayers];
            
            
			// Weight vector (return value)
            int dimWeight=0;
            if (nLayers==0) {
                dimWeight=(nInputs+1)*(nClasses);
            } else {
                dimWeight=(nInputs+1)*ELEM[0];
                for (int i=1;i<nLayers;i++) 
                    dimWeight+=(ELEM[i-1]+1)*(ELEM[i]);
                dimWeight+=(nClasses)*(ELEM[nLayers-1]+1);
            }
            double []weights=new double[dimWeight];
            
            GCNet gcn=new GCNet();
            double error=gcn.nntrain(nInputs,nClasses,X,Cbin,ELEM,weights,rand);
            
            double faults=0;
			double debugRMS=0;
            try {
                for (int i=0;i<X.length;i++) {
                    double[] resp=gcn.nnoutput(X[i]);
                    int theClass=AD.argmax(resp);
					for (int i1=0;i1<resp.length;i1++) 
						debugRMS+=(resp[i1]-Cbin[i][i1])*(resp[i1]-Cbin[i][i1]);
                    if (theClass!=C[i]) faults++;
					Ct[i]=theClass;
                }
				debugRMS/=X.length;
				System.out.println("Failures="+faults+" size="+nData);
				System.out.println("Debug RMS="+debugRMS);
                faults/=nData;
                System.out.println("Train error="+faults);
                
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            pc.trainingResults(C,Ct);
            
            // Test
            ProcessDataset pdt = new ProcessDataset();
            int nTest,npInputs,npVariables;
            line=(String)pc.parInputData.get(ProcessConfig.IndexTest);
            
            if (pc.parNewFormat) pdt.processClassifierDataset(line,false);
            else pdt.oldClusteringProcess(line);
            
            nTest = pdt.getNdata();
            npVariables = pdt.getNvariables();
            npInputs = pdt.getNinputs();
            pdt.showDatasetStatistics();
            
            if (npInputs!=nInputs) throw new IOException("IOErr test file");
            
            double[][] Xp=pdt.getX(); int [] Cp=pdt.getC(); int [] Co=new int[Cp.length];
            
            // Test error
            try {
                faults=0;
                for (int i=0;i<Xp.length;i++) {
                    double[] resp=gcn.nnoutput(Xp[i]);
                    int theClass=AD.argmax(resp);
                    if (theClass!=Cp[i]) faults++;
                    Co[i]=theClass;
                }
				System.out.println("Failures="+faults+" total="+Xp.length);

                faults/=Cp.length;
                
                System.out.println("Test error="+faults);
                
                
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            
            pc.results(Cp,Co);
			
			
			
			// Now using the pseudoinverse matrix
			double[][]Ys = new double[X.length][nClasses];
			double[][]Xs = new double[X.length][X[0].length+1];
			double[][] A = new double[X[0].length+1][1];
			
			for (int i=0;i<Xs.length;i++) {
				for (int j=0;j<nClasses;j++) Ys[i][j]=Cbin[i][j];
				Xs[i][0]=1;
				for (int j=1;j<Xs[0].length;j++) Xs[i][j]=X[i][j-1];
			}
			
			try {
			 A=MatrixCalcs.matmul(
				 MatrixCalcs.matmul(
					MatrixCalcs.inv(
						MatrixCalcs.matmul(MatrixCalcs.tr(Xs),
										   Xs
						)
					),
				  MatrixCalcs.tr(Xs)
				),Ys);
				
				// Train error
				double Cs[][] = new double[Ys.length][Ys[0].length];
				Cs = MatrixCalcs.matmul(Xs,A);
				
				debugRMS=0;
				for (int i=0;i<Cs.length;i++)
					for (int j=0;j<Cs[i].length;j++)
						debugRMS+=(Cs[i][j]-Ys[i][j])*(Cs[i][j]-Ys[i][j]);
			    debugRMS/=Cs.length;
				
				System.out.println("DEBUG RMS PSEUDOINVERSE: "+debugRMS);
				
			} catch(Exception e) {
				System.err.println(e+" Matrix Calcs");
			}
			
			
			
			
        } catch(FileNotFoundException e) {
            System.err.println(e+" Input file not found");
        } catch(IOException e) {
            System.err.println(e+" Read error");
        }
    }
    
	
	/**
	* <p>
	* This method runs {@link ClassifierLinearLMS}
	* @param args A vector of string with command line arguments
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
		ClassifierLinearLMS cl=new ClassifierLinearLMS();
		cl.linearClassifierLMS(tty,pc);
		
	}
	
	
}

