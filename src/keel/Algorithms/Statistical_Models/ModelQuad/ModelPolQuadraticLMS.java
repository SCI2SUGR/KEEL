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
package keel.Algorithms.Statistical_Models.ModelQuad;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Shared.ClassicalOptim.*;
import org.core.*;
import java.io.*;

public class ModelPolQuadraticLMS {
	/**
	* <p>
	* In this class, Quadratic Least Squares Regression is implemented 
	* </p>
	*/
	static Randomize rand;
	/**
	* <p>
	* In this method, a Quadratic Least Squares model is estimated
	* @param tty  unused boolean parameter, kept for compatibility
	* @param pc   {@link ProcessConfig} object to obtain the train and test datasets
	*             and the method's parameters.
	* </p>
	*/      	
	private static void quadraticModel(boolean tty, ProcessConfig pc) {
        try {
            
            String line = new String();
            
            ProcessDataset pd=new ProcessDataset();
            
			line=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
            
            if (pc.parNewFormat) pd.processModelDataset(line,true);
            else pd.oldClassificationProcess(line);
            
            int nData=pd.getNdata();           // Number of examples
            int nVariables=pd.getNvariables();   // Number of variables
            int nInputs=pd.getNinputs();     // Number of inputs
            int nOutputs=1;
                        
            double[][] X = pd.getX();             // Input data
            double[] Y = pd.getY();               // Output data
			double[] Yt = new double[Y.length];
            pd.showDatasetStatistics();
            
            double Y1[][] = new double [Y.length][1];
            for (int i=0;i<nData;i++) Y1[i][0]=Y[i];
            
            double[] maxInput = pd.getImaximum();   // Maximum and minimum for input data
            double[] minInput = pd.getIminimum();
            
            double maxOutput = pd.getOmaximum();     // Maximum and minimum for output data
            double minOutput = pd.getOminimum();
            
            // Number of coeficients
            int dimWeights=((nInputs)*(nInputs)+(nInputs))*(nOutputs)+(nInputs)+(nOutputs);
            double []pesos=new double[dimWeights];
            
            // Conjugated gradient optimization
            GCQuad gcn=new GCQuad();
            double error=gcn.cuatrain(nInputs,1,X,Y1,rand);
			for (int i=0;i<Yt.length;i++) {
				double output[]=gcn.cuaoutput(X[i]);   
				Yt[i]=output[0];
			}
			pc.trainingResults(Y,Yt);

            
            // Test Error
            ProcessDataset pdt = new ProcessDataset();
            int nTest,npInputs,npVariables;
			line=(String)pc.parInputData.get(ProcessConfig.IndexTest);
            
            if (pc.parNewFormat) pdt.processModelDataset(line,false);
            else pdt.oldClassificationProcess(line);
            
            nTest = pdt.getNdata();
            npVariables = pdt.getNvariables();
            npInputs = pdt.getNinputs();
            pdt.showDatasetStatistics();
            
            if (npInputs!=nInputs) throw new IOException("IOERR Test file");
            
            double[][] Xp=pdt.getX(); double [] Yp=pdt.getY(); double [] Yo=new double[Yp.length];
            
            double RMS=0;
            for (int i=0;i<nTest;i++) {
                double salida[]=gcn.cuaoutput(Xp[i]);   
                RMS+=(salida[0]-Yp[i])*(salida[0]-Yp[i]);
                Yo[i]=salida[0];
            }
            RMS/=nTest;
            
            System.out.println("ECM Test = "+RMS);
            pc.results(Yp,Yo); 
            
            
        } catch(FileNotFoundException e) {
            System.err.println(e+" File not found");
        } catch(IOException e) {
            System.err.println(e+" Read Error");
        }
        
    }
	
	/**
	* <p>
	* This public static method runs {@link ModelPolQuadraticLMS}
	* @param args A vector of string with the command line parameters
	* </p>
	*/   
	   public static void main(String args[]) {
		   
		   boolean tty=false;
		   ProcessConfig pc=new ProcessConfig();
		   System.out.println("Reading configuration file: "+args[0]);
		   if (pc.fileProcess(args[0])<0) return;
		   int algorithm=pc.parAlgorithmType;
		   rand=new Randomize();
		   rand.setSeed(pc.parSeed);
		   ModelPolQuadraticLMS cp=new ModelPolQuadraticLMS();
		   cp.quadraticModel(tty,pc);
		   
	   }
	   
	   
}

	
