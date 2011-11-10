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
 * @author Written by Jose A. Saez Munoz, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * Date: 06/01/10
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Preprocess.NoiseFilters.ClassificationFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;
import keel.Algorithms.Genetic_Rule_Learning.Globals.FileManagement;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;


/**
 * <p>
 * The Classification Filter begins with n equal-sized disjoint subsets of the training set E (done with n-fold cross validation)
 * and the empty output set A of detected noisy examples. The main loop is repeated for each training subset Ei. Ey is formed
 * which includes all examples from E except those in Ei. Set Ey is used as the input for an arbitrary inductive learning
 * algorithm that induces a hypothesis (a classifier) Hy. Those examples from Ei for which the hypothesis Hy does not give the
 * correct classification are added to A as potentially noisy examples.
 * Reference: 1999-Gamberger-ICML
 * </p>
 */
public class ClassificationFilter {
	
	private Instance[] instancesTrain;	// all the instances of the training set
	private Vector[] partitions;		// indexes of the instances in each partition
	private boolean[] correctlyLabeled;	// indicates if the instance is correctly labeled
	private PartitionScheme partSch;	// partition scheme used
	private Vector noisyInstances;		// indexes of the noisy instances from training set
	

//*******************************************************************************************************************************

	/**
	 * <p>
	 * It initializes the partitions from training set
	 * </p>
	 */
	public ClassificationFilter(){

		// create instances
		partSch = new PartitionScheme();			// create the partitions
		instancesTrain = partSch.getInstances();	// get all the instances of training set
		partitions = partSch.getPartitions();
		
		if(Parameters.classifier.equals("c45")){
			partSch.createPartitionFiles();
		}
		
		correctlyLabeled = new boolean[Parameters.numInstances];
		Arrays.fill(correctlyLabeled, true);
	}
	
//*******************************************************************************************************************************

	/**
	 * <p>
	 * It initializes the partitions from training set
	 * </p>
	 * 
	 */
	public void run(){
		
		Instance[] train, test;

		for(int partTest = 0 ; partTest < Parameters.numPartitions ; ++partTest){

			if(Parameters.classifier.equals("knn")){
				
				train = partSch.getTrainPartition(partTest);
				test = partSch.getTestPartition(partTest);
				
		        KNN knn = new KNN(train, test);

		        knn.execute();
		        int[] pre = knn.getPredictions();
		        
		        for(int i = 0 ; i < partitions[partTest].size() ; ++i)
		        	correctlyLabeled[(Integer)partitions[partTest].get(i)] = (pre[i] == instancesTrain[(Integer)partitions[partTest].get(i)].getOutputNominalValuesInt(0));
			}
			
			if(Parameters.classifier.equals("c45")){
				
				C45 c45 = null;

				try {
					c45 = new C45("train"+(partTest+1)+".dat","test"+(partTest+1)+".dat");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			     int[] pre = c45.getPredictions();
			     for(int i = 0 ; i < partitions[partTest].size() ; ++i)
			    	 correctlyLabeled[(Integer)partitions[partTest].get(i)] = (pre[i] == instancesTrain[(Integer)partitions[partTest].get(i)].getOutputNominalValuesInt(0));
			}
			
		}
		
		
		noisyInstances = new Vector();
		for(int j = 0 ; j < Parameters.numInstances ; ++j){		
			if(correctlyLabeled[j] ==  false)
				noisyInstances.add(j);
		}
			
		//System.out.println(noisyInstances + "\n" + noisyInstances.size());
		createDataset(Parameters.trainOutputFile);
		
		// generate the test file
		try {
			String s;
			File Archi1 = new File(Parameters.testInputFile);
		    File Archi2 = new File(Parameters.testOutputFile);
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
		
		if(Parameters.classifier.equals("c45")){
			partSch.deletePartitionFiles();
		}

	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It apllies the changes to remove the noise 
	 * </p>
	 */
	public void createDataset(String out){
		
  		String header = "";
  		header = "@relation " + Attributes.getRelationName() + "\n";
  		header += Attributes.getInputAttributesHeader();
  		header += Attributes.getOutputAttributesHeader();
  		header += Attributes.getInputHeader() + "\n";
  		header += Attributes.getOutputHeader() + "\n";
        header += "@data\n";
		
		FileManagement fm = new FileManagement();
		Attribute []att = Attributes.getInputAttributes();

		try {
			
			fm.initWrite(out);
			fm.writeLine(header);
			
			int numNoisyEx = 0;
			for(int i = 0 ; i < instancesTrain.length ; i++){
				
				if( (numNoisyEx < noisyInstances.size()) && (Integer)noisyInstances.get(numNoisyEx) == i){
					numNoisyEx++;
				}
				
				else{
					//System.out.print(i + ", ");
					boolean[] missing = instancesTrain[i].getInputMissingValues();
					String newInstance = "";
					
					for(int j = 0 ; j < Parameters.numAttributes ; j++){
						
						if(missing[j])
							newInstance += "?";
						
						else{
							if(att[j].getType() == Attribute.REAL)
								newInstance += instancesTrain[i].getInputRealValues(j);
							if(att[j].getType() == Attribute.INTEGER)
								newInstance += (int)instancesTrain[i].getInputRealValues(j);
							if(att[j].getType() == Attribute.NOMINAL)
								newInstance += instancesTrain[i].getInputNominalValues(j);
						}
						
						newInstance += ", "; 
					}
					
					String className = instancesTrain[i].getOutputNominalValues(0);
					newInstance += className + "\n";
					
					fm.writeLine(newInstance);
				}
			}
				
			fm.closeWrite();
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
}