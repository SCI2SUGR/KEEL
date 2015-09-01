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

package keel.Algorithms.Preprocess.NoiseFilters.IterativeMCSbFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;
import org.core.Randomize;
import keel.Algorithms.Decision_Trees.C45.*;
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
public class IMCSF_FILTER {
	
	private Instance[] instancesTrain;	// all the instances of the training set
	private boolean[][] correctlyLabeled;	// indicates if the instance is correctly labeled
	private PartitionScheme partSch;	// partition scheme used
	private Vector noisyInstances;		// indexes of the noisy instances from training set
	private int numFilters;
	private int NUM_ALGORITHMS;

//*******************************************************************************************************************************

	/**
	 * <p>
	 * It initializes the partitions from training set
	 * </p>
	 */
	public IMCSF_FILTER(){
		
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
        
        NUM_ALGORITHMS = 0;
		if(Parameters.use_C45)
			NUM_ALGORITHMS++;
		if(Parameters.use_KNN)
			NUM_ALGORITHMS++;
		if(Parameters.use_SVM)
			NUM_ALGORITHMS++;
		if(Parameters.use_LOG)
			NUM_ALGORITHMS++;
	}
	
//*******************************************************************************************************************************

	/**
	 * <p>
	 * It initializes the partitions from training set
	 * </p>
	 * @throws Exception 
	 */
	public void run() throws Exception{
		
		PrintWriter LOG_OUT = new PrintWriter(new FileWriter(Parameters.logOutputFile));
		
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
			
			LOG_OUT.println("Iteration " + iter);
			
			correctlyLabeled = new boolean[numFilters][insAux.length];
			for(int i = 0 ; i < numFilters ; ++i)
				for(int j = 0 ; j < insAux.length ; ++j)
					correctlyLabeled[i][j] = true;
			
			
			for(int k = 0 ; k < Parameters.numPartitions ; ++k){
				
				boolean[] correctlyLabeled_c45 = new boolean[insAux.length];
				boolean[] correctlyLabeled_knn = new boolean[insAux.length];
				boolean[] correctlyLabeled_svm = new boolean[insAux.length];
				boolean[] correctlyLabeled_log = new boolean[insAux.length];
				
				// C4.5 -----------------------------------------------------------------
				if(Parameters.use_C45){
					Attributes.clearAll();
					
					if(Parameters.numPartitions != 1)
						CreateConfigFileC45("train"+(k+1)+".dat", "IPF_train_"+iter+".txt","tra_out_c45.dat","test_out_c45.dat","log_out.dat");
					else
						CreateConfigFileC45("test"+(k+1)+".dat", "IPF_train_"+iter+".txt","tra_out_c45.dat","test_out_c45.dat","log_out.dat");
					
					String[] args = new String[1]; args[0] = "config_c45.txt";
					C45.main(args);
					
				     int[] pre = GetPredictions("test_out_c45.dat", insAux.length);
				     for(int i = 0 ; i < insAux.length ; ++i)
				    	 correctlyLabeled_c45[i] = (pre[i] == insAux[i].getOutputNominalValuesInt(0));
				     
				     File fichero = new File("config_c45.txt");fichero.delete();
				     fichero = new File("tra_out_c45.dat");fichero.delete();
				     fichero = new File("test_out_c45.dat");fichero.delete();
				     fichero = new File("log_out.dat");fichero.delete();
				}
				
			     // 1-NN -----------------------------------------------------------------
				if(Parameters.use_KNN){
				     
				     Attributes.clearAll();
				     
				     if(Parameters.numPartitions != 1)
				    	 CreateConfigFileKNN("train"+(k+1)+".dat", "IPF_train_"+iter+".txt","tra_out_knn.dat","test_out_knn.dat","log_out_knn.dat", Parameters.k_value);
				     else
				    	 CreateConfigFileKNN("test"+(k+1)+".dat", "IPF_train_"+iter+".txt","tra_out_knn.dat","test_out_knn.dat","log_out_knn.dat", Parameters.k_value);
				     
				     String[] args = new String[1]; args[0] = "config_knn.txt";
				     keel.Algorithms.Lazy_Learning.KNN.Main.main(args);
				
						
				     int[] pre = GetPredictions("test_out_knn.dat", insAux.length);
				     for(int i = 0 ; i < insAux.length ; ++i)
				    	 correctlyLabeled_knn[i] = (pre[i] == insAux[i].getOutputNominalValuesInt(0));				
				    
				    File fichero = new File("config_knn.txt");fichero.delete(); 
				    fichero = new File("tra_out_knn.dat");fichero.delete();
				    fichero = new File("test_out_knn.dat");fichero.delete();
					fichero = new File("log_out_knn.dat");fichero.delete();  
				}
				
				// SVM -----------------------------------------------------------------
				if(Parameters.use_SVM){
					
					Attributes.clearAll();
					
					if(Parameters.numPartitions != 1)
						CreateConfigFileSVM("train"+(k+1)+".dat", "IPF_train_"+iter+".txt","tra_out_svm.dat","test_out_svm.dat","log_out_svm.dat");
					else
						CreateConfigFileSVM("test"+(k+1)+".dat", "IPF_train_"+iter+".txt","tra_out_svm.dat","test_out_svm.dat","log_out_svm.dat");
					
					String[] args = new String[1]; args[0] = "config_svm.txt";
					keel.Algorithms.SVM.SMO.Main.main(args);
				
						
					int[] pre = GetPredictions("test_out_svm.dat", insAux.length);
					for(int i = 0 ; i < insAux.length ; ++i)
						correctlyLabeled_svm[i] = (pre[i] == insAux[i].getOutputNominalValuesInt(0));				
					
					File fichero = new File("config_svm.txt");fichero.delete();
					fichero = new File("tra_out_svm.dat");fichero.delete();
					fichero = new File("test_out_svm.dat");fichero.delete();
					fichero = new File("log_out_svm.dat");fichero.delete();
				}
				
				
				
			     // LOGISTIC -----------------------------------------------------------------
			    if(Parameters.use_LOG){ 
					
				     Attributes.clearAll();
				     
				     if(Parameters.numPartitions != 1)
				    	 CreateConfigFileLogistic("train"+(k+1)+".dat", "IPF_train_"+iter+".txt","tra_out_log.dat","test_out_log.dat","log_out_log.dat");
				     else
				    	 CreateConfigFileLogistic("test"+(k+1)+".dat", "IPF_train_"+iter+".txt","tra_out_log.dat","test_out_log.dat","log_out_log.dat");
				     
				     String[] args = new String[1]; args[0] = "config_log.txt";
				     keel.Algorithms.Statistical_Classifiers.Logistic.Main.main(args);
				
						
				     int[] pre = GetPredictions("test_out_log.dat", insAux.length);
				     for(int i = 0 ; i < insAux.length ; ++i)
				    	 correctlyLabeled_log[i] = (pre[i] == insAux[i].getOutputNominalValuesInt(0));				
				    
				    File fichero = new File("config_log.txt");fichero.delete(); 
				    fichero = new File("tra_out_log.dat");fichero.delete();
				    fichero = new File("test_out_log.dat");fichero.delete();
					fichero = new File("log_out_log.dat");fichero.delete(); 
			    }
				
				
				//-------- combinacion
				for(int i = 0 ; i < insAux.length ; ++i){
					int contnoise = 0;
					
					if(Parameters.use_C45)
						if(!correctlyLabeled_c45[i])
							contnoise++;
					
					if(Parameters.use_KNN)
						if(!correctlyLabeled_knn[i])
							contnoise++;
					
					if(Parameters.use_SVM)
						if(!correctlyLabeled_svm[i])
							contnoise++;
					
					if(Parameters.use_LOG)
						if(!correctlyLabeled_log[i])
							contnoise++;
				 
					if(contnoise >= ((double) NUM_ALGORITHMS/2.0)){
					//if(contnoise == NUM_ALGORITHMS){
						correctlyLabeled[k][i] = false;
						//LOG_OUT.println("particion " + k + " ejemplo " + i + " tiene ruido");
				 }
			 }

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
					
					if(cont > (double)numFilters/2 && correctlyLabeled[partSch.getPartitionOfInstance(j)][j] == false){
						noisyInstances.add(j);
						LOG_OUT.println("\n\n Ejemplo" + j + " tiene RUIDO");
					}
				}
			}
			
			
			
			// --------------------------------------------
			//ElegirEjemplosAEliminar();
			//for(int i_ = 0 ; i_ < noisyInstances.size() ; ++i_)
			//	LOG_OUT.println("\n\n Ejemplo" + noisyInstances.get(i_) + " tiene RUIDO");
			// ---------------------------------------------------

			
			
			
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

		createDatasets("IPF_train_"+iter+".txt", Parameters.trainOutputFile, Parameters.testInputFile, Parameters.testOutputFile);
		
		File fi = new File("IPF_train_"+iter+".txt");
		fi.delete();
		
		
		LOG_OUT.close();
	}
	
	
	
	/*public void ElegirEjemplosAEliminar(){
		
		int numej = (int) (Parameters.numInstances*Parameters.MaxPercRemoved);
		
		if(noisyInstances.size() <= numej)
			return;

		
		boolean[] usado = new boolean[noisyInstances.size()];
		Arrays.fill(usado, false);
		
		for(int i = 0 ; i < numej ; ++i){
			boolean correct = false;
			
			while(!correct){
				int rand_ind = Randomize.Randint(0, noisyInstances.size());
				
				if(!usado[rand_ind]){
					usado[rand_ind] = true;
					correct = true;
				}
			}
		}
		
		// actualizar noisyInstances
		int[] indicesej = new int[numej];
		int cont = 0;
		for(int i = 0 ; i < noisyInstances.size() ; ++i){
			if(usado[i])
				indicesej[cont++] = (Integer) noisyInstances.get(i);
		}
		
		
		noisyInstances = new Vector();
		for(int i = 0 ; i < numej ; ++i){
			noisyInstances.add(indicesej[i]);
		}
		
	}*/
	
	
	
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
	
	public static void CreateConfigFileC45(String tra_in, String tst_in, String tra_out, String tst_out, String log_out) throws Exception{
		
		BufferedWriter fS = new BufferedWriter(new FileWriter("config_c45.txt"));
		String content = "";
		
		
		content += 	"algorithm = C4.5 Decision Tree";
		content += "\ninputData = \"" + tra_in + "\" \"" + tra_in + "\" \"" + tst_in + "\"";
		content += "\noutputData = \"" + tra_out + "\" \"" + tst_out + "\" \"" + log_out + "\"";
		
		content += "\n\npruned = TRUE";
		content += "\nconfidence = 0.25";
		content += "\ninstancesPerLeaf = 2\n";

		fS.write(content);
		fS.close();
	}
	
	public static void CreateConfigFileKNN(String tra_in, String tst_in, String tra_out, String tst_out, String log_out, int k_value) throws Exception{
		
		BufferedWriter fS = new BufferedWriter(new FileWriter("config_knn.txt"));
		String content = "";
		
		
		content += 	"algorithm = K Nearest Neighbors Classifier";
		content += "\ninputData = \"" + tra_in + "\" \"" + tra_in + "\" \"" + tst_in + "\"";
		content += "\noutputData = \"" + tra_out + "\" \"" + tst_out + "\" \"" + log_out + "\"";
		
		content += "\n\nK Value = " + Parameters.k_value;
		content += "\ndistance Function = Euclidean";

		fS.write(content);
		fS.close();
	}
	
	
	public static void CreateConfigFileSVM(String tra_in, String tst_in, String tra_out, String tst_out, String log_out) throws Exception{
		
		BufferedWriter fS = new BufferedWriter(new FileWriter("config_svm.txt"));
		String content = "";
		
		content += 	"algorithm = SMO";
		content += "\ninputData = \"" + tra_in + "\" \"" + tra_in + "\" \"" + tst_in + "\"";
		content += "\noutputData = \"" + tra_out + "\" \"" + tst_out + "\" \"" + log_out + "\"";
		
		content += "\n\nseed = 1286082570";
		content += "\nC = 100";
		content += "\ntoleranceParameter = 0.001";
		content += "\nepsilon = 1.0E-12";
		content += "\nRBFKernel_gamma = 0.01";
		content += "\n-Normalized-PolyKernel_exponent = 1";
		content += "\n-Normalized-PolyKernel_useLowerOrder = False";
		content += "\nPukKernel_omega = 1.0";
		content += "\nPukKernel_sigma = 1.0";
		content += "\nStringKernel_lambda = 0.5";
		content += "\nStringKernel_subsequenceLength = 3";
		content += "\nStringKernel_maxSubsequenceLength = 9";
		content += "\nStringKernel_normalize = False";
		content += "\nStringKernel_pruning = None";
		content += "\nKERNELtype = Puk";
		content += "\nFitLogisticModels = True";
		content += "\nConvertNominalAttributesToBinary = True";
		content += "\nPreprocessType = Normalize";

		fS.write(content);
		fS.close();
	}	
	
	
	public static void CreateConfigFileLogistic(String tra_in, String tst_in, String tra_out, String tst_out, String log_out) throws Exception{
		
		BufferedWriter fS = new BufferedWriter(new FileWriter("config_log.txt"));
		String content = "";
		
		
		content += 	"algorithm = Multinomial logistic regression model";
		content += "\ninputData = \"" + tra_in + "\" \"" + tra_in + "\" \"" + tst_in + "\"";
		content += "\noutputData = \"" + tra_out + "\" \"" + tst_out + "\" \"" + log_out + "\"";
		
		content += "\n\nRidge = 1e-8";
		content += "\nMaxIter = -1";

		fS.write(content);
		fS.close();
	}
	
	static public int[] GetPredictions(String file, int num_ex) throws Exception{
		
		int[] res = new int[num_ex];
		BufferedReader fE = new BufferedReader(new FileReader(file));
		
		String line = "";
		while( !(line = fE.readLine()).equals("@data") );
		
		for(int i = 0 ; i < num_ex ; ++i){
			res[i] = getnv(fE.readLine().split(" ")[1]);
		}
		
		fE.close();
		return res;
	}
	
	static public int getnv(String clase){
		for(int v = 0 ; v < Attributes.getOutputAttribute(0).getNumNominalValues() ; ++v)
			if(Attributes.getOutputAttribute(0).getNominalValue(v).equals(clase))
				return v;
		return -1;
	}

}