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
 * @author Written by Julián Luengo Martín 10/11/2005
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.ignore_missing;
import java.io.*;
import java.util.*;
import keel.Dataset.*;
import keel.Algorithms.Preprocess.Basic.*;

/**
 * <p>
 * This class delete all instances with at least one missing value from the data set
 * </p>
 */
public class ignore_missing {
    
    double [] mean = null;
    double [] std_dev = null;
    double tempData = 0;
    String[][] X = null; //matrix of transformed data
    
    int ndatos = 0;
    int nentradas = 0;
    int tipo = 0;
    int direccion = 0;
    int nvariables = 0;
    int nsalidas = 0;
    
    InstanceSet IS;
    String input_train_name = new String();
    String input_test_name = new String();
    String output_train_name = new String();
    String output_test_name = new String();
    String temp = new String();
    String data_out = new String("");
    
    /**
     * Creates a new instance of ignore_missing
     * @param fileParam The path to the configuration file with all the parameters in KEEL format
     */
    public ignore_missing(String fileParam) {
        config_read(fileParam);
        IS = new InstanceSet();
    }
    
    /**
     * <p>
     * Process the training and test files provided in the parameters file to the constructor.
     * </p>
     */
    public void process(){
        try {
            
            // Load in memory a dataset that contains a classification problem
            IS.readSet(input_train_name,true);
            int in = 0;
            int out = 0;
            
            ndatos = IS.getNumInstances();
            nvariables = Attributes.getNumAttributes();
            nentradas = Attributes.getInputNumAttributes();
            nsalidas = Attributes.getOutputNumAttributes();
            
            X = new String[ndatos][nvariables];//matrix with transformed data
            boolean [] isMissed = new boolean[ndatos];//vector which points out instances with missed data
            
            try {
                FileWriter file_write = new FileWriter(output_train_name);
                
                file_write.write(IS.getHeader());
                
                //now, print the normalized data
                file_write.write("@data\n");
                //file_write.close();
                PrintWriter pw = new PrintWriter(file_write); 
                for(int i=0;i<ndatos;i++){
                    Instance inst = IS.getInstance(i);
                    if (!inst.existsAnyMissingValue()){
                    	inst.printAsOriginal(pw);
                        //file_write.write(inst.toString()); // DOES NOT WRITE BACK NON-DEF DIRECTION ATTRIBUTES!!!!
                        file_write.write("\n");
                    }
                }
                pw.close();
                file_write.close();
            } catch (IOException e) {
                System.out.println("IO exception = " + e );
                System.exit(-1);
            }
            
        }catch (Exception e){
            System.out.println("Dataset exception = " + e );
            e.printStackTrace();
            System.exit(-1);
        }
        //does a test file associated exist?
        if(input_train_name.compareTo(input_test_name)!=0){
            try {
                
                // Load in memory a dataset that contains a classification problem
                IS.readSet(input_test_name,false);
                int in = 0;
                int out = 0;
                
                ndatos = IS.getNumInstances();
                nvariables = Attributes.getNumAttributes();
                nentradas = Attributes.getInputNumAttributes();
                nsalidas = Attributes.getOutputNumAttributes();
                
                X = new String[ndatos][nvariables];//matrix with transformed data
                boolean [] isMissed = new boolean[ndatos];//vector which points out instances with missed data
                
                try {
                    FileWriter file_write = new FileWriter(output_test_name);
                    
                    file_write.write(IS.getHeader());
                    
                    //now, print the normalized data
                    file_write.write("@data\n");
                    PrintWriter pw = new PrintWriter(file_write); 
                    for(int i=0;i<ndatos;i++){
                        Instance inst = IS.getInstance(i);
                        if (!inst.existsAnyMissingValue()){
                        	inst.printAsOriginal(pw);
                            file_write.write("\n");
                        }
                    }
                    pw.close();
                    file_write.close();
                } catch (IOException e) {
                    System.out.println("IO exception = " + e );
                    System.exit(-1);
                }
                
            }catch (Exception e){
                System.out.println("Dataset exception = " + e );
                e.printStackTrace();
                System.exit(-1);
            }
        }
        
        //write_results(); / since there ins't any data transformation, is not needed
    }
    
    
    private void config_read(String fileParam){
        File inputFile = new File(fileParam);
        
        if (inputFile == null || !inputFile.exists()) {
            System.out.println("parameter "+fileParam+" file doesn't exists!");
            System.exit(-1);
        }
        //begin the configuration read from file
        try {
            FileReader file_reader = new FileReader(inputFile);
            BufferedReader buf_reader = new BufferedReader(file_reader);
            //FileWriter file_write = new FileWriter(outputFile);
            
            String line;
            
            do{
                line = buf_reader.readLine();
            }while(line.length()==0); //avoid empty lines for processing -> produce exec failure
            String out[]= line.split("algorithm = ");
            //alg_name = new String(out[1]); //catch the algorithm name
            //input & output filenames
            do{
                line = buf_reader.readLine();
            }while(line.length()==0);
            out= line.split("inputData = ");
            out = out[1].split("\\s\"");
            input_train_name = new String(out[0].substring(1, out[0].length()-1));
            input_test_name = new String(out[1].substring(0, out[1].length()-1));
            if(input_test_name.charAt(input_test_name.length()-1)=='"')
                input_test_name = input_test_name.substring(0,input_test_name.length()-1);
            
            do{
                line = buf_reader.readLine();
            }while(line.length()==0);
            out = line.split("outputData = ");
            out = out[1].split("\\s\"");
            output_train_name = new String(out[0].substring(1, out[0].length()-1));
            output_test_name = new String(out[1].substring(0, out[1].length()-1));
            if(output_test_name.charAt(output_test_name.length()-1)=='"')
                output_test_name = output_test_name.substring(0,output_test_name.length()-1);
            
            file_reader.close();
            
        } catch (IOException e) {
            System.out.println("IO exception = " + e );
            System.exit(-1);
        }
    }
    
    private void write_results(){
        //File OutputFile = new File(output_train_name.substring(1, output_train_name.length()-1));
        try {
            FileWriter file_write = new FileWriter(output_train_name);
            
            file_write.write(IS.getHeader());
            
            //now, print the normalized data
            file_write.write("@data\n");
            for(int i=0;i<ndatos;i++){
                file_write.write(X[i][0]);
                for(int j=1;j<nvariables;j++){
                    file_write.write(","+X[i][j]);
                }
                file_write.write("\n");
            }
            file_write.close();
        } catch (IOException e) {
            System.out.println("IO exception = " + e );
            System.exit(-1);
        }
    }
    
}

