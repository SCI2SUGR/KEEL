/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010

	F. Herrera (herrera@decsai.ugr.es)
    L. SÃ¡nchez (luciano@uniovi.es)
    J. AlcalÃ¡-Fdez (jalcala@decsai.ugr.es)
    S. GarcÃ­a (sglopez@ujaen.es)
    A. FernÃ¡ndez (alberto.fernandez@ujaen.es)
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

package keel.Algorithms.Semi_Supervised_Learning.Basic;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import keel.Algorithms.SVM.C_SVM.svmClassifier;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;

/**
 * HandlerCSVM for C support vector machine algorithm.
 */
public class HandlerCSVM {
		
	private int[][] predictions;
	private double[][] probabilities;
	private String algSufix = "CSVM";
	
    /**
     * Number of partitions.
     */
    public int numPartitions;

    /**
     * Number of instances.
     */
    public int numInstances;

    /**
     * Number of classes.
     */
    public int numClasses;

    /**
     * Training dataset filename.
     */
    public String trainInputFile;

    /**
     * Test dataset filename.
     */
    public String testInputFile;

    /**
     * Seed for random purposes.
     */
    public String seed;
    
    /**
     * Parameter constructor. Initiates the basics variables.
     * @param clases number of classes.
     * @param instances number of instances.
     * @param Seed seed for random purposes.
     */
    public HandlerCSVM(int clases, int instances, String Seed){
	      this.numPartitions = 1;
	      this.trainInputFile= "train1.dat";
	      this.testInputFile = "test1.dat";
	      this.numClasses = clases;
	      this.numInstances = instances;
	      this.seed = Seed;
	      
	}
	
    /**
     * Parameter constructor. Initiates the basics variables.
     * @param train Training instances set.
     * @param test Test instances set.
     * @param clases number of classes.
     * @param Seed seed for random purposes.
     *
     * @throws Exception if the configuration file can not be created.
     */
    public HandlerCSVM(InstanceSet train, InstanceSet test, int clases, String Seed) throws Exception{

	      this.numPartitions = 1;
	      this.trainInputFile= "train1.dat";
	      this.testInputFile = "test1.dat";
	      this.numClasses = clases;
	      this.numInstances = test.getNumInstances();
	      this.seed = Seed;
		
			
			
		// crear ficheros de configuracion
		createConfigurationFiles();

		for(int i = 0 ; i < this.numPartitions ; ++i){

	        String[] argumentos = new String[1];
	        argumentos[0] = "config_" + algSufix + "_" + (i+1) + ".txt";
	        
			svmClassifier model = new svmClassifier (argumentos[0]);

			model.process(train,test);
			probabilities = model.probabilities.clone();
			

		}
		
		// borrar ficheros de configuracion
		for(int i = 0 ; i < this.numPartitions ; ++i){
			File f = new File("config_" + algSufix + "_" + (i+1) + ".txt");
			f.delete();
		}
		
		//leer las instancias de cada particion		
		predictions = new int[this.numPartitions][this.numInstances];
		for(int i = 0 ; i < this.numPartitions ; ++i){
			
			BufferedReader fE = new BufferedReader(new FileReader("test_" + algSufix + "_" + (i+1) + ".dat"));
			
			while(!fE.readLine().contains("@data"));
			
			// guardo las clases predichas
			for(int q = 0 ; q < this.numInstances ; ++q){
				
				String linea = fE.readLine();
				String salida = linea.split(" ")[1];
				
				int claseInt = 0;
				boolean seguir = true;
				for(int sa = 0 ; sa < this.numClasses && seguir ; ++sa)
					if(Attributes.getOutputAttribute(0).getNominalValue(sa).equals(salida)){
						//System.out.println(salida);
						claseInt = sa;
						seguir = false;
					}
				
			
				
				predictions[i][q] = claseInt;
			}
			
			fE.close();
		}
	
		
	}
	
	// esta funcioÃ³n no vale pa nah! 

    /**
     * Generates the configuration files and the results files for the CSVM algorithm.
     * @throws Exception if the files can not be generated
     */
    	public void generateFiles() throws Exception{
		
		// crear ficheros de configuracion
		createConfigurationFiles();
		
		// ejecutar el metodo
		for(int i = 0 ; i < this.numPartitions ; ++i){
	        Attributes.clearAll();
	        String[] argumentos = new String[1];
	        argumentos[0] = "config_" + algSufix + "_" + (i+1) + ".txt";
	   //     keel.Algorithms.SVM.C_SVM.svmClassifier.main(argumentos);
		}
		
		// borrar ficheros de configuracion
		for(int i = 0 ; i < this.numPartitions ; ++i){
			File f = new File("config_" + algSufix + "_" + (i+1) + ".txt");
			f.delete();
		}
		
		//leer las instancias de cada particion		
		predictions = new int[this.numPartitions][this.numInstances];
		for(int i = 0 ; i < this.numPartitions ; ++i){
			
			BufferedReader fE = new BufferedReader(new FileReader("test_" + algSufix + "_" + (i+1) + ".dat"));
			
			while(!fE.readLine().contains("@data"));
			
			// guardo las clases predichas
			for(int q = 0 ; q < this.numInstances ; ++q){
				
				String linea = fE.readLine();
				String salida = linea.split(" ")[1];
				
				int claseInt = 0;
				boolean seguir = true;
				for(int sa = 0 ; sa < this.numClasses && seguir ; ++sa)
					if(Attributes.getOutputAttribute(0).getNominalValue(sa).equals(salida)){
						System.out.println(salida);
						claseInt = sa;
						seguir = false;
					}
				
			
				
				predictions[i][q] = claseInt;
			}
			
			fE.close();
		}
		
		//
		Attributes.clearAll();
		try {
			InstanceSet finalIS = new InstanceSet();
			finalIS.readSet(this.trainInputFile, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
    /**
     * Deletes all the files generated.
     */
    public void deleteFiles(){
		
		for(int i = 0 ; i < this.numPartitions ; ++i){
			File f = new File("train_" + algSufix + "_" + (i+1) + ".dat");
			f.delete();
			
			f = new File("test_" + algSufix + "_" + (i+1) + ".dat");
			f.delete();
		}
		
	}
	
    /**
     * Returns the predictions for a given partition.
     * @param part given partition.
     * @return the predictions for the given partition.
     */
    public int[] getPredictions(int part){
		return predictions[part];
	}
	
    /**
     * Returns the classes probabilities.
     * @return the classes probabilities.
     */
    public double[][] getProbabilities(){
		return probabilities;
	}

//*******************************************************************************************************************************
  	
  	private void createConfigurationFiles() throws IOException{
  		
  		//System.err.println("Creando ficheraco");
  		for(int i = 0 ; i < this.numPartitions ; ++i){
  			BufferedWriter bf = new BufferedWriter(new FileWriter("config_" + algSufix + "_"+(i+1)+".txt"));
  			String cad = "";
  			cad += "algorithm = " + algSufix + "\n";
  			cad += "inputData = \""+this.trainInputFile+"\""+" \""+this.trainInputFile+"\""+" \""+ this.testInputFile + "\"\n";
  			cad += "outputData = \"train_" + algSufix + "_" + (i+1) + ".dat\" \"test_" + algSufix + "_" + (i+1) + ".dat\" \"test2_" + algSufix + "_" + (i+1) + ".dat\"\n\n";
  			cad += "seed = " + this.seed + "\n";
  			cad += "KERNELtype = RBF\n";
  			cad += "C = 100.0\n";
  			cad += "eps = 0.001\n";
  			cad += "degree = 1\n";
  			cad += "gamma = 0.01\n";
  			cad += "coef0 = 0.0\n";
  			cad += "nu = 0.1\n";
  			cad += "p = 1.0\n";
  			cad += "shrinking = 1\n";
  			
  			//System.out.println(cad);
  			bf.write(cad);
  			bf.close();
  		}
  		
  	}

	
}
