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

package keel.Algorithms.Preprocess.NoiseFilters.IterativePartitioningFilter;

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
public class IterativePartitioningFilter {
	
	private Instance[] instancesTrain;	// all the instances of the training set
	private boolean[][] correctlyLabeled;	// indicates if the instance is correctly labeled
	private PartitionScheme partSch;	// partition scheme used
	private Vector noisyInstances;		// indexes of the noisy instances from training set
	private int numFilters;
	

//*******************************************************************************************************************************

	/**
	 * <p>
	 * It initializes the partitions from training set
	 * </p>
	 */
	public IterativePartitioningFilter(){
		
		numFilters = Parameters.numPartitions;
		Randomize.setSeed(Parameters.seed);

		InstanceSet is = new InstanceSet();
		
		try {	
			is.readSet(Parameters.trainInputFile, true);
        }catch(Exception e){
            System.exit(1);
        }
        
        instancesTrain = is.getInstances();
        Parameters.numClasses = Attributes.getOutputAttribute(0).getNumNominalValues();
        Parameters.numAttributes = Attributes.getInputAttributes().length;
        Parameters.numInstances = instancesTrain.length;
	}
	
//*******************************************************************************************************************************

	/**
	 * <p>
	 * It initializes the partitions from training set
	 * </p>
	 */
	public void run(){
		
		boolean stop = false;
		int countToStop = 0;
		int iter = 0;
		Instance[] insAux;
		
		partSch = new PartitionScheme(Parameters.trainInputFile, Parameters.numPartitions);
		insAux = partSch.getInstances();
		partSch.createPartitionFiles();
		
		noisyInstances = new Vector();
		createDatasetTrain(Parameters.trainInputFile, "IPF_train_0.txt");
		

		while(!stop){
			
			correctlyLabeled = new boolean[numFilters][insAux.length];
			for(int i = 0 ; i < numFilters ; ++i)
				for(int j = 0 ; j < insAux.length ; ++j)
					correctlyLabeled[i][j] = true;
			
			
			for(int k = 0 ; k < Parameters.numPartitions ; ++k){
				
			    // to train C45 classifier
			    C45 c45 = null;

				try {
					c45 = new C45("train"+(k+1)+".dat", "IPF_train_"+iter+".txt");
				} catch (Exception e) {
					e.printStackTrace();
				}
					
			     int[] pre = c45.getPredictions();
			     for(int i = 0 ; i < insAux.length ; ++i)
			    	 correctlyLabeled[k][i] = (pre[i] == insAux[i].getOutputNominalValuesInt(0));
			}
			
			
			noisyInstances = new Vector();
			
			if(Parameters.filterType.equals("consensus")){
				
				boolean ruido;
				
				for(int j = 0 ; j < insAux.length ; ++j){
					ruido = true;
					for(int i = 0 ; i < numFilters && ruido ; ++i)
						if(correctlyLabeled[i][j] ==  true){
							ruido = false;
						}
					
					if(ruido && correctlyLabeled[partSch.getPartitionOfInstance(j)][j] == false)
						noisyInstances.add(j);
				}
			}

			
			if(Parameters.filterType.equals("majority")){
								
				for(int j = 0 ; j < insAux.length ; ++j){
					int cont = 0;
					for(int i = 0 ; i < numFilters ; ++i)
						if(correctlyLabeled[i][j] ==  false){
							cont++;
						}
					
					if(cont > (double)numFilters/2 && correctlyLabeled[partSch.getPartitionOfInstance(j)][j] == false)
						noisyInstances.add(j);
				}
			}
			
			
			// remove instances from training set and create new partitions
			// elimino las instancias recogidas hasta ahora de la ultima particion
			createDatasetTrain("IPF_train_"+iter+".txt", "IPF_train_"+(iter+1)+".txt");
				
			// delete old partition files and old IPF_train file
			partSch.deletePartitionFiles();
			File fi = new File("IPF_train_"+iter+".txt");
			fi.delete();
				

					
			
			if(noisyInstances.size() < Parameters.numInstances*0.01)
				countToStop++;
			else
				countToStop = 0;
			
			if(countToStop == 3)
				stop = true;
			else{
				// create the new partition files
				partSch = new PartitionScheme("IPF_train_"+(iter+1)+".txt",Parameters.numPartitions);
				insAux = partSch.getInstances();
				partSch.createPartitionFiles();
			}

			iter++;
		}


		// la solucion esta... en el fichero IPF_train_(iter)!!!!!
		createDatasets("IPF_train_"+iter+".txt", Parameters.trainOutputFile, Parameters.testInputFile, Parameters.testOutputFile);
		
		File fi = new File("IPF_train_"+iter+".txt");
		fi.delete();
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It apllies the changes to remove the noise 
	 * </p>
	 */
	public void createDatasets(String trainIN, String trainOUT, String testIN, String testOUT){
		
		// to create the train file-----------------------------------------
		try {
			String s;
			File Archi1 = new File(trainIN);
		    File Archi2 = new File(trainOUT);
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
	
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It apllies the changes to remove the noise 
	 * </p>
	 */
	public void createDatasetTrain(String trainIN, String trainOUT){
		
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
		
	}
	
}