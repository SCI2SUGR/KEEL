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

package keel.Algorithms.Preprocess.NoiseFilters.EnsembleFilter;

import keel.Algorithms.Statistical_Classifiers.Shared.DiscrAnalysis.AD;
import keel.Algorithms.Statistical_Classifiers.Shared.MatrixCalcs.ErrorDimension;
import keel.Algorithms.Statistical_Classifiers.Shared.MatrixCalcs.ErrorSingular;
import keel.Dataset.Attributes;
import keel.Dataset.DatasetException;
import keel.Dataset.HeaderFormatException;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;

public class LDA {
	
	int[] predict;
	
	public LDA(){}
	
	/**
	* <p>
	* In this method, a classifier is estimated using Linear Discriminant Analysis
	*/
	public void runMethod(String train_file, String test_file, Instance[] train, Instance[] test){
		
			try {
				runMethod_private(train_file, test_file);
			} catch (DatasetException e) {
				e.printStackTrace();
			} catch (HeaderFormatException e) {
				e.printStackTrace();
			} catch (ErrorDimension e) {
				e.printStackTrace();
			} catch (ErrorSingular e) {
				Parameters.numNeighbors += 3;
			    KNN knn = new KNN(train, test);
			    knn.execute();
			    predict = knn.getPredictions();
			    Parameters.numNeighbors -= 3;
			}
		
	}
	
    private void runMethod_private(String train_file, String test_file) throws DatasetException, HeaderFormatException, ErrorDimension, ErrorSingular {
    	
    		Attributes.clearAll();
    	
            InstanceSet isTRA = new InstanceSet();
            isTRA.readSet(train_file, true);
            Instance[] instancesTRA = isTRA.getInstances();
            
            
            int nData = instancesTRA.length;
            
            double[][] X = new double[nData][];
            
            for(int k = 0 ; k < nData ; ++k)
            	X[k] = instancesTRA[k].getAllInputValues();	// Input data
            
            int[] C = new int[nData];
            for(int k = 0 ; k < nData; ++k)
            	C[k] = instancesTRA[k].getOutputNominalValuesInt(0);
            
            int [] Ct=new int[C.length];
            
            int nClasses = Attributes.getOutputAttribute(0).getNumNominalValues(); // Number of classes
                      
            
            // A vector is generated with classes 1 bit between n codified 
            double Cbin[][] = new double[nData][nClasses];
            for (int i=0;i<nData;i++) {
			  Cbin[i][C[i]]=1;
			}
            
			for (int i=0;i<X.length;i++) Ct[i]=-1;
			
            AD adlin = new AD(X,Cbin);
            double faults=0;
            
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
	
                //System.out.println("Train error="+faults);
                
           
			
			//pc.trainingResults(C,Ct);
            
            // Algorithm is evaluated over test set
            InstanceSet isTST = new InstanceSet();
            isTST.readSet(test_file, false);
            Instance[] instancesTST = isTST.getInstances();
            

            int nTest = instancesTST.length;
            predict = new int[nTest];
            
            
            double[][] Xp = new double[nTest][];
            
            for(int k = 0 ; k < nTest ; ++k)
            	Xp[k] = instancesTST[k].getAllInputValues();	// Input data
            
            int[] Cp = new int[nTest];
            for(int k = 0 ; k < nTest; ++k)
            	Cp[k] = instancesTST[k].getOutputNominalValuesInt(0);
            
            int [] Co=new int[Cp.length];
            
            // Accuracy system test
            
                faults=0;
                for (int i=0;i<Xp.length;i++) {
                    double[] resp=adlin.distances(Xp[i]);
                    int aClass=adlin.argmax(resp);
                    predict[i] = aClass;
                    if (aClass!=Cp[i]) faults++;
                    Co[i]=aClass;
                }
                faults/=Xp.length;
                
                //System.out.println("test error="+faults);
         
            
            //pc.results(Cp,Co);
    }
    
    public int[] getPredictions(){
    	return predict;
    }


}
