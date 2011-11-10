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

package keel.Algorithms.Preprocess.NoiseFilters.EnsembleFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;
import org.core.Randomize;
import keel.Algorithms.Genetic_Rule_Learning.Globals.FileManagement;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;


/**
 * <p>
 * The Ensemble Filter...
 * Reference: 1999-Brodley-JAIR
 * </p>
 */
public class EnsembleFilter {
	
	private Instance[] instancesTrain;	// all the instances of the training set
	private Vector[] partitions;		// indexes of the instances in each partition
	private boolean[][] correctlyLabeled;	// indicates if the instance is correctly labeled
	private PartitionScheme partSch;	// partition scheme used
	private Vector noisyInstances;		// indexes of the noisy instances from training set
	private int numFilters = 3;
	

//*******************************************************************************************************************************

	/**
	 * <p>
	 * It initializes the partitions from training set
	 * </p>
	 */
	public EnsembleFilter(){
		
		Randomize.setSeed(Parameters.seed);

		// create instances
		partSch = new PartitionScheme();			// create the partitions
		instancesTrain = partSch.getInstances();	// get all the instances of training set
		partitions = partSch.getPartitions();
		
		partSch.createPartitionFiles();
		
		correctlyLabeled = new boolean[numFilters][Parameters.numInstances];
		for(int i = 0 ; i < numFilters ; ++i)
			for(int j = 0 ; j < Parameters.numInstances ; ++j)
				correctlyLabeled[i][j] = true;
	}
	
//*******************************************************************************************************************************

	/**
	 * <p>
	 * It initializes the partitions from training set
	 * </p>
	 *
	 * 
	 */
	public void run(){
		
		Instance[] train, test;

		for(int partTest = 0 ; partTest < Parameters.numPartitions ; ++partTest){
			
			// to train K-NN classifier
			train = partSch.getTrainPartition(partTest);
			test = partSch.getTestPartition(partTest);
				
		    KNN knn = new KNN(train, test);

		    knn.execute();
		    int[] pre3 = knn.getPredictions();
		        
		    for(int i = 0 ; i < partitions[partTest].size() ; ++i)
		    	correctlyLabeled[0][(Integer)partitions[partTest].get(i)] = (pre3[i] == instancesTrain[(Integer)partitions[partTest].get(i)].getOutputNominalValuesInt(0));
			
			
		    // to train C45 classifier
		    C45 c45 = null;

			try {
				c45 = new C45("train"+(partTest+1)+".dat","test"+(partTest+1)+".dat");
			} catch (Exception e) {
				e.printStackTrace();
			}
				
		     int[] pre2 = c45.getPredictions();
		     for(int i = 0 ; i < partitions[partTest].size() ; ++i)
		    	 correctlyLabeled[1][(Integer)partitions[partTest].get(i)] = (pre2[i] == instancesTrain[(Integer)partitions[partTest].get(i)].getOutputNominalValuesInt(0));

		     
		     // to train LDA classifier
			 LDA lda = new LDA();
			 lda.runMethod("train"+(partTest+1)+".dat","test"+(partTest+1)+".dat", train, test);
			 
			 int[] pre1 = lda.getPredictions();
		     for(int i = 0 ; i < partitions[partTest].size() ; ++i)
		    	 correctlyLabeled[2][(Integer)partitions[partTest].get(i)] = (pre1[i] == instancesTrain[(Integer)partitions[partTest].get(i)].getOutputNominalValuesInt(0));

		}
		
		
		
		if(Parameters.filterType.equals("consensus")){
			
			boolean ruido = false;
			noisyInstances = new Vector();
			
			for(int j = 0 ; j < Parameters.numInstances ; ++j){
				ruido = true;
				for(int i = 0 ; i < numFilters && ruido ; ++i)
					if(correctlyLabeled[i][j] ==  true){
						ruido = false;
					}
				
				if(ruido)
					noisyInstances.add(j);
			}
		}

		
		if(Parameters.filterType.equals("majority")){
			
			noisyInstances = new Vector();
			
			for(int j = 0 ; j < Parameters.numInstances ; ++j){
				int cont = 0;
				for(int i = 0 ; i < numFilters ; ++i)
					if(correctlyLabeled[i][j] ==  false){
						cont++;
					}
				
				if(cont > (double)numFilters/2)
					noisyInstances.add(j);
			}
		}
		
		
		System.out.println("\n\n*****************************************");
		System.out.println("Noisy instances = " + noisyInstances);
		System.out.println("Total = " + noisyInstances.size());
		System.out.println("*****************************************");

		createDatasets(Parameters.trainInputFile, Parameters.trainOutputFile, Parameters.testInputFile, Parameters.testOutputFile);
		
		partSch.deletePartitionFiles();
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It apllies the changes to remove the noise 
	 * </p>
	 */
	public void createDatasets(String trainIN, String trainOUT, String testIN, String testOUT){
		
		// to check if the noisyInstances vector is ordered
		if(noisyInstances.size() > 0){
			int menor = (Integer)noisyInstances.get(0);
			boolean correcto = true;
			for(int i = 1 ; i < noisyInstances.size() && correcto ; ++i){
				if((Integer)noisyInstances.get(i) <= menor)
					correcto = false;
				else
					menor = (Integer)noisyInstances.get(i);
			}
				
			if(!correcto){
				System.out.println("\n\nERROR: The noisy vector is not ordered!");
				System.exit(-1);
			}
		}
		// create the files...
		InstanceSet is = new InstanceSet();
		Instance[] instances = null;
		try {
			is.readSet(trainIN, false);
			instances = is.getInstances();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		int numAtt = Attributes.getInputNumAttributes();
		
		// create an array with the non-noisy instances
		Vector validInstances = new Vector();
		int cont = 0;
		
		if(noisyInstances.size() == 0){
			for(int i = 0 ; i < instances.length ; ++i)
				validInstances.add(i);
		}
		
		else{
			for(int i = 0 ; i < instances.length ; ++i){
				if(cont < noisyInstances.size() && (Integer)noisyInstances.get(cont) == i)
					cont++;
				else
					validInstances.add(i);
			}
		}
		
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
		
		try {
			
			fm.initWrite(trainOUT);
			fm.writeLine(header);
			
			
			for(int k = 0 ; k < validInstances.size() ; k++){
				
				int i = (Integer) validInstances.get(k);
				
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