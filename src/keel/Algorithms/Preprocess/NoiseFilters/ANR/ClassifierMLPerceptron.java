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

package keel.Algorithms.Preprocess.NoiseFilters.ANR;

import org.core.*;
import java.io.*;
import java.util.Vector;
import keel.Algorithms.Genetic_Rule_Learning.Globals.FileManagement;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;


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
	static Vector noisyInstances;
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
			
			System.out.println(nLayers);
			System.out.println(nInputs);
			System.out.println(nClasses);
			System.out.println(elements[0]);

			
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
				//pc.trainingResults(C,Ct);
                
                System.out.println("Train error="+nfaults);
                
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            
            
            // Algorithm is evaluated using test set
            ProcessDataset pdt = new ProcessDataset();
            int nprueba,npentradas,npvariables;
            line=(String)pc.parInputData.get(ProcessConfig.IndexTest-1);
            
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
            //pc.results(Cp,Co);
            
            
            //*********************************************************
            //obtener instancias con ruido y crear archivos
            noisyInstances = gcn.getNoisyInstances();
            System.out.println(noisyInstances);
            System.out.println((String)pc.parInputData.get(0));
            System.out.println(pc.parResultTrainName);
            System.out.println((String)pc.parInputData.get(1));
            System.out.println(pc.parResultName);

            createDatasets((String)pc.parInputData.get(0), pc.parResultTrainName, (String)pc.parInputData.get(1), pc.parResultName);
            
            //*********************************************************
            
            
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
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It apllies the changes to remove the noise 
	 * </p>
	 */
	public static void createDatasets(String trainIN, String trainOUT, String testIN, String testOUT){
		
		InstanceSet is = new InstanceSet();
		Instance[] instances = null;
		try {
			is.readSet(trainIN, false);
			instances = is.getInstances();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		int numAtt = Attributes.getInputNumAttributes();
		
		
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
		boolean saltar = false;
		
		try {
			
			fm.initWrite(trainOUT);
			fm.writeLine(header);
			
			int numNoisyEx = 0;
			for(int i = 0 ; i < instances.length ; i++){
				
				saltar = false;

				if( numNoisyEx < noisyInstances.size() ){
					Integer auxInteger1 = (Integer)noisyInstances.get(numNoisyEx);
					int auxInt2 = auxInteger1.intValue();
					if(auxInt2 == i){
						numNoisyEx++;
						saltar = true;
					}
				}
				
				if(!saltar){
					
					boolean[] missing = instances[i].getInputMissingValues();
					String newInstance = "";
					
					for(int j = 0 ; j < numAtt ; j++){
						
						if(missing[j])
							newInstance += "?";
						
						else{
							if(att[j].getType() == Attribute.REAL)
								newInstance += instances[i].getInputRealValues(j);
							if(att[j].getType() == Attribute.INTEGER)
								newInstance += (int)instances[i].getInputRealValues(j);
							if(att[j].getType() == Attribute.NOMINAL)
								newInstance += instances[i].getInputNominalValues(j);
						}
						
						newInstance += ", "; 
					}
					
					String className = instances[i].getOutputNominalValues(0);
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
