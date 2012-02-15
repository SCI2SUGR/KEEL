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
 * @author Written by Julián Luengo Martín 31/12/2005
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.MostCommonValue;
import java.io.*;
import java.util.*;
import keel.Dataset.*;
import keel.Algorithms.Preprocess.Basic.*;

/**
 * <p>
 * This class computes the mean (numerical) or mode (nominal) value of the attributes with missing values for all classes
 * </p>
 */
public class MostCommonValue {
    
    double [] mean = null;
    double [] std_dev = null;
    double tempData = 0;
    String[][] X = null; //matrix of transformed data
    FreqList[] timesSeen = null; //matrix with frequences of attribute values
    String[] mostCommon;
    
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
    
    /** Creates a new instance of MostCommonValue
     * @param fileParam The path to the configuration file with all the parameters in KEEL format 
     */
    public MostCommonValue(String fileParam) {
        config_read(fileParam);
        IS = new InstanceSet();
    }
    
    //Write data matrix X to disk, in KEEL format
    private void write_results(String output){
        //File OutputFile = new File(output_train_name.substring(1, output_train_name.length()-1));
        try {
            FileWriter file_write = new FileWriter(output);
            
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
    
    //Read the patron file, and parse data into strings
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
    
    /**
     * <p>
     *	Takes a value and checks if it belongs to the attribute interval. If not, it returns the nearest limit.
     *	IT DOES NOT CHECK IF THE ATTRIBUTE IS NOT NOMINAL
     * </p>
     * @param value the value to be checked
     * @param a the attribute to which the value will be checked against
     * @return the original value if it was in the interval limits of the attribute, or the nearest boundary limit otherwise.
     */
    public double boundValueToAttributeLimits(double value, Attribute a){
    	
    	if(value < a.getMinAttribute())
    		value = a.getMinAttribute();
    	else if(value > a.getMaxAttribute())
    		value = a.getMaxAttribute();
    	
    	return value;
    }
    
    /**
     * <p>
     * Process the training and test files provided in the parameters file to the constructor.
     * </p>
     */
    public void process(){
        ValueFreq vf;
        double mean;
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
            
            timesSeen = new FreqList[nvariables];
            mostCommon = new String[nvariables];
            for(int j=0;j<nvariables;j++){
                timesSeen[j] = new FreqList();
            }
            
            
            
            //First, create a reference list with all values
            //for each attribute, so we can pick the most common one
            for(int i = 0;i < ndatos;i++){
                Instance inst = IS.getInstance(i);
                
                in = 0;
                out = 0;
                
                for(int j = 0; j < nvariables;j++){
                    Attribute a = Attributes.getAttribute(j);
                    
                    direccion = a.getDirectionAttribute();
                    tipo = a.getType();
                    
                    if(direccion == Attribute.INPUT){
                        if(tipo != Attribute.NOMINAL && !inst.getInputMissingValues(in)){
                            timesSeen[j].AddElement( new String(String.valueOf(inst.getInputRealValues(in))) );
                            
                        } else{
                            if(!inst.getInputMissingValues(in)){
                                timesSeen[j].AddElement( inst.getInputNominalValues(in));
                            } else{
                                //do nothing
                            }
                        }
                        in++;
                    } else{
                        if(direccion == Attribute.OUTPUT){
                            if(tipo != Attribute.NOMINAL && !inst.getOutputMissingValues(out)){
                                timesSeen[j].AddElement(new String(String.valueOf(inst.getOutputRealValues(out))));
                            } else{
                                if(!inst.getOutputMissingValues(out)){
                                    timesSeen[j].AddElement(inst.getOutputNominalValues(out));
                                } else{
                                    //do nothing
                                }
                            }
                            out++;
                        }
                        /*else{
                           What should we do with non-defined direction values?
                        }*/
                    }
                }
                
            }
            //take for each attribute the most common value, so it
            //can be taken quickly
            ValueFreq elem = null;
            for(int k=0;k<nvariables;k++){
                elem = timesSeen[k].mostCommon();
                if(elem!=null)
                    mostCommon[k] = elem.getValue();
                else
                    mostCommon[k] = "?"; //this attribute has no good values (all are missing data)
            }
            //now, search for missed data, and replace them with
            //the most common value
            
            for(int i = 0;i < ndatos;i++){
                Instance inst = IS.getInstance(i);
                
                in = 0;
                out = 0;
                
                for(int j = 0; j < nvariables;j++){
                    Attribute a = Attributes.getAttribute(j);
                    
                    direccion = a.getDirectionAttribute();
                    tipo = a.getType();
                    
                    if(direccion == Attribute.INPUT){
                        if(tipo != Attribute.NOMINAL && !inst.getInputMissingValues(in)){
                            X[i][j] = new String(String.valueOf(inst.getInputRealValues(in)));
                        } else{
                            if(!inst.getInputMissingValues(in))
                                X[i][j] = inst.getInputNominalValues(in);
                            else
                                X[i][j] = new String(mostCommon[j]); //replace missing data
                        }
                        in++;
                    } else{
                        if(direccion == Attribute.OUTPUT){
                            if(tipo != Attribute.NOMINAL && !inst.getOutputMissingValues(out)){
                                X[i][j] = new String(String.valueOf(inst.getOutputRealValues(out)));
                            } else{
                                if(!inst.getOutputMissingValues(out))
                                    X[i][j] = inst.getOutputNominalValues(out);
                                else{
                                    if(tipo==Attribute.NOMINAL)
                                        X[i][j] = new String(mostCommon[j]); //replace missing data
                                    else{
                                        timesSeen[j].reset();
                                        mean = 0;
                                        while(!timesSeen[j].outOfBounds()){
                                            vf = timesSeen[j].getCurrent();
                                            mean += (new Double(vf.getValue()).doubleValue()*vf.getFreq());
                                        }
                                        mean = mean / (double)timesSeen[j].totalElems();
                                        mean = boundValueToAttributeLimits(mean,a);
                                        X[i][j] = new String(String.valueOf(mean));
                                    }
                                }
                            }
                            out++;
                        }
                    }
                }
            }
        }catch (Exception e){
            System.out.println("Dataset exception = " + e );
            System.exit(-1);
        }
        write_results(output_train_name);
        /***************************************************************************************/
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
                
                timesSeen = new FreqList[nvariables];
                mostCommon = new String[nvariables];
                for(int j=0;j<nvariables;j++){
                    timesSeen[j] = new FreqList();
                }
                
                
                
                //First, create a reference list with all values
                //for each attribute, so we can pick the most common one
                for(int i = 0;i < ndatos;i++){
                    Instance inst = IS.getInstance(i);
                    
                    in = 0;
                    out = 0;
                    
                    for(int j = 0; j < nvariables;j++){
                        Attribute a = Attributes.getAttribute(j);
                        
                        direccion = a.getDirectionAttribute();
                        tipo = a.getType();
                        
                        if(direccion == Attribute.INPUT){
                            if(tipo != Attribute.NOMINAL && !inst.getInputMissingValues(in)){
                                timesSeen[j].AddElement( new String(String.valueOf(inst.getInputRealValues(in))) );
                                
                            } else{
                                if(!inst.getInputMissingValues(in)){
                                    timesSeen[j].AddElement( inst.getInputNominalValues(in));
                                } else{
                                    //do nothing
                                }
                            }
                            in++;
                        } else{
                            if(direccion == Attribute.OUTPUT){
                                if(tipo != Attribute.NOMINAL && !inst.getOutputMissingValues(out)){
                                    timesSeen[j].AddElement(new String(String.valueOf(inst.getOutputRealValues(out))));
                                } else{
                                    if(!inst.getOutputMissingValues(out)){
                                        timesSeen[j].AddElement(inst.getOutputNominalValues(out));
                                    } else{
                                        //do nothing
                                    }
                                }
                                out++;
                            }
                        /*else{
                           What should we do with non-defined direction values?
                        }*/
                        }
                    }
                    
                }
                //take for each attribute the most common value, so it
                //can be taken quickly
                ValueFreq elem = null;
                for(int k=0;k<nvariables;k++){
                    elem = timesSeen[k].mostCommon();
                    if(elem!=null)
                        mostCommon[k] = elem.getValue();
                    else
                        mostCommon[k] = "?"; //this attribute has no good values (all are missing data)
                }
                //now, search for missed data, and replace them with
                //the most common value
                
                for(int i = 0;i < ndatos;i++){
                    Instance inst = IS.getInstance(i);
                    
                    in = 0;
                    out = 0;
                    
                    for(int j = 0; j < nvariables;j++){
                        Attribute a = Attributes.getAttribute(j);
                        
                        direccion = a.getDirectionAttribute();
                        tipo = a.getType();
                        
                        if(direccion == Attribute.INPUT){
                            if(tipo != Attribute.NOMINAL && !inst.getInputMissingValues(in)){
                                X[i][j] = new String(String.valueOf(inst.getInputRealValues(in)));
                            } else{
                                if(!inst.getInputMissingValues(in))
                                    X[i][j] = inst.getInputNominalValues(in);
                                else
                                    X[i][j] = new String(mostCommon[j]); //replace missing data
                            }
                            in++;
                        } else{
                            if(direccion == Attribute.OUTPUT){
                                if(tipo != Attribute.NOMINAL && !inst.getOutputMissingValues(out)){
                                    X[i][j] = new String(String.valueOf(inst.getOutputRealValues(out)));
                                } else{
                                    if(!inst.getOutputMissingValues(out))
                                        X[i][j] = inst.getOutputNominalValues(out);
                                    else{
                                        if(tipo==Attribute.NOMINAL)
                                            X[i][j] = new String(mostCommon[j]); //replace missing data
                                        else{
                                            timesSeen[j].reset();
                                            mean = 0;
                                            while(!timesSeen[j].outOfBounds()){
                                                vf = timesSeen[j].getCurrent();
                                                mean += (new Double(vf.getValue()).doubleValue()*vf.getFreq());
                                            }
                                            mean = mean / (double)timesSeen[j].totalElems();
                                            mean = boundValueToAttributeLimits(mean,a);
                                            X[i][j] = new String(String.valueOf(mean));
                                        }
                                    }
                                }
                                out++;
                            }
                        }
                    }
                }
            }catch (Exception e){
                System.out.println("Dataset exception = " + e );
                System.exit(-1);
            }
            write_results(output_test_name);
        }
    }
    
}
