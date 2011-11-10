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


import java.io.File;
import java.util.Arrays;
import java.util.Vector;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;
import org.core.Files;
import org.core.Randomize;


/**
 * <p>
 * This class implements a stratified scheme (equal number of examples of each class in each partition) to partition a dataset
 * </p>
 */
public class PartitionScheme {

	private Instance[] instances;
	private Vector[] partitions;
	private Instance[][] trainPartition;
	private Instance[][] testPartition;
	
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It reads the training set and creates the partitions
	 * </p>
	 */
	public PartitionScheme(){
			
		InstanceSet is = new InstanceSet();
		
		try {	
			is.readSet(Parameters.trainInputFile, true);
        }catch(Exception e){
            System.exit(1);
        }
                
        instances = is.getInstances();
        Parameters.numClasses = Attributes.getOutputAttribute(0).getNumNominalValues();
        Parameters.numAttributes = Attributes.getInputAttributes().length;
        Parameters.numInstances = instances.length;
        
        createPartitions();
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It creates the partitions from the original training set
	 * </p>
	 */
	private void createPartitions(){

       	// 1) to count the number of examples of each class
        int[] numExClass = new int[Parameters.numClasses];
        Arrays.fill(numExClass, 0);
        for(int i = 0 ; i < Parameters.numInstances ; i++)
        	numExClass[instances[i].getOutputNominalValuesInt(0)]++;
               
        // 2) to sort the indexes of examples per class
        int sortedIndex[] = new int[Parameters.numInstances];
        int k = 0;
        for (int i = 0; i < Parameters.numClasses ; i++)
            for (int j = 0; j < Parameters.numInstances ; j++)
                if (instances[j].getOutputNominalValuesInt(0) == i)
                	sortedIndex[k++] = j;

        // 3) to shuffle the examples of each class
        int tmp;
        k = 0;
        for(int i = 0 ; i < Parameters.numClasses ; i++){
            for(int j = 0 ; j < numExClass[i] ; j++){
                int randPos = Randomize.Randint(j, numExClass[i]);
                tmp = sortedIndex[j+k];
                sortedIndex[j+k] = sortedIndex[randPos+k];
                sortedIndex[randPos+k] = tmp;
            }
            k += numExClass[i];
        }

        // 4) to create the partitions 
        partitions = new Vector[Parameters.numPartitions];
        for (int i = 0; i < Parameters.numPartitions; i++)
        	partitions[i] = new Vector();
        
        for (int i = 0; i < Parameters.numInstances; i++)
        	partitions[i % Parameters.numPartitions].add(new Integer(sortedIndex[i]));
   
        // 5) create the training and test partitions
        getTrainTest();
	}
	
//*******************************************************************************************************************************
  	
  	/**
  	 * <p>
  	 * Main method
  	 * </p>
  	 * @param args the command line arguments
  	 */
  	private void getTrainTest(){
  		
  		trainPartition = new Instance[Parameters.numPartitions][];
  		testPartition = new Instance[Parameters.numPartitions][];

  		for(int par = 0 ; par < Parameters.numPartitions ; ++par){
  			
  			// count the number of instances in train number par
  	  		int tam = 0;
  	  		for(int i = 0 ; i < Parameters.numPartitions ; ++i)
  	  			if(i != par)
  	  				tam += partitions[i].size();
  			
  	  		trainPartition[par] = new Instance[tam];
  	  		testPartition[par] = new Instance[partitions[par].size()];
  	  		
  	  		// create the training partition
  	  		int size = 0;
  	  		for(int i = 0 ; i < Parameters.numPartitions ; ++i)
  	  			if(i != par){
  	  				for(int j = 0 ; j < partitions[i].size() ; ++j)
  	  					trainPartition[par][size++] = instances[(Integer)partitions[i].get(j)];
  	  			}
  	  		
  	  		// create the test partition
  	  		for(int j = 0 ; j < partitions[par].size() ; ++j)
  	  			testPartition[par][j] = instances[(Integer)partitions[par].get(j)];
  		}

  	}
  	
//*******************************************************************************************************************************

	/**
	 * <p>
	 * It returns the training partition specified
	 * </p>
	 * @param num number of the partition
	 * @return the training partition
	 */
  	public Instance[] getTrainPartition(int num){
  		Instance[] res = new Instance[trainPartition[num].length];
  		for(int i = 0 ; i < res.length ;++i ){
  			res[i] = new Instance(trainPartition[num][i]);
  		}  			
  		return res;
  	}

//*******************************************************************************************************************************
  	
	/**
	 * <p>
	 * It returns the test partition specified
	 * </p>
	 * @param num number of the partition
	 * @return the test partition
	 */
  	public Instance[] getTestPartition(int num){
  		Instance[] res = new Instance[testPartition[num].length];
  		for(int i = 0 ; i < res.length ;++i ){
  			res[i] = new Instance(testPartition[num][i]);
  		}  			
  		return res;
  	}
  	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It returns all the original instances
	 * </p>
	 * @return the instances
	 */
	public Instance[] getInstances(){
		return instances;
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It returns the indexes of the original instances in all partitions
	 * </p>
	 * @return the indexes of the instances in each partition
	 */
	public Vector[] getPartitions(){
		return partitions;
	}
  	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It creates the files of each training and test partition
	 * </p>
	 */
  	public void createPartitionFiles(){
  		
		Attribute []att = Attributes.getInputAttributes();

  		String header = "";
  		header = "@relation " + Attributes.getRelationName() + "\n";
  		header += Attributes.getInputAttributesHeader();
  		header += Attributes.getOutputAttributesHeader();
  		header += Attributes.getInputHeader() + "\n";
  		header += Attributes.getOutputHeader() + "\n";
        header += "@data\n";
        
        String outputTrain = "", outputTest = "";

        for (int i = 0; i < Parameters.numPartitions ; i++) {
        	
        	outputTest = header;
        	outputTrain = header;
        	
        	// create test partition-----------------------------
        	for(int j = 0 ; j < testPartition[i].length ; ++j){
        		
        		boolean[] missing = testPartition[i][j].getInputMissingValues();
				String newInstance = "";
				
				for(int ak = 0 ; ak < Parameters.numAttributes ; ak++){
					
					if(missing[ak])
						newInstance += "?";
					
					else{
						if(att[ak].getType() == Attribute.REAL)
							newInstance += testPartition[i][j].getInputRealValues(ak);
						if(att[ak].getType() == Attribute.INTEGER)
							newInstance += (int)testPartition[i][j].getInputRealValues(ak);
						if(att[ak].getType() == Attribute.NOMINAL)
							newInstance += testPartition[i][j].getInputNominalValues(ak);
					}
					
					newInstance += ", "; 
				}
				
				String className = testPartition[i][j].getOutputNominalValues(0);
				newInstance += className + "\n";
        		
        		outputTest += newInstance;
        	}

        	// create train partition-----------------------------
        	for(int j = 0 ; j < trainPartition[i].length ; ++j){
        		
        		boolean[] missing = trainPartition[i][j].getInputMissingValues();
				String newInstance = "";
				
				for(int ak = 0 ; ak < Parameters.numAttributes ; ak++){
					
					if(missing[ak])
						newInstance += "?";
					
					else{
						if(att[ak].getType() == Attribute.REAL)
							newInstance += trainPartition[i][j].getInputRealValues(ak);
						if(att[ak].getType() == Attribute.INTEGER)
							newInstance += (int)trainPartition[i][j].getInputRealValues(ak);
						if(att[ak].getType() == Attribute.NOMINAL)
							newInstance += trainPartition[i][j].getInputNominalValues(ak);
					}
					
					newInstance += ", "; 
				}
				
				String className = trainPartition[i][j].getOutputNominalValues(0);
				newInstance += className + "\n";
        		
        		outputTrain += newInstance;
        	}

            Files.addToFile("train" + String.valueOf(i + 1) + ".dat", outputTrain);
            Files.addToFile("test" + String.valueOf(i + 1) + ".dat", outputTest);
        }
  		
  	}

//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It deletes the files of each training and test partition
	 * </p>
	 */	
  	public void deletePartitionFiles(){

  		for(int i = 0 ; i < Parameters.numPartitions ; ++i){

			File fichero = new File("train"+(i+1)+".dat");
			fichero.delete();
			fichero = new File("test"+(i+1)+".dat");
			fichero.delete();
		}
  	}

}