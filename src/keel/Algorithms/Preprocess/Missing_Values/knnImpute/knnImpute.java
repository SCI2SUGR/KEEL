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
 * @author Written by Julián Luengo Martín 05/11/2006
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.knnImpute;
import java.io.*;
import java.util.*;
import keel.Dataset.*;
import keel.Algorithms.Preprocess.Basic.*;

/**
 * <p>
 * This class computes the mean (numerical) or mode (nominal) value of the attributes with missing values for the selected
 * neighbours for a given instance with missing values
 * </p>
 */
public class knnImpute {
    
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
    int nneigh = 1; //number of neighbours
    
    InstanceSet IS,IStest;
    String input_train_name = new String();
    String input_test_name = new String();
    String output_train_name = new String();
    String output_test_name = new String();
    String temp = new String();
    String data_out = new String("");
    
    /** Creates a new instance of MostCommonValue 
     * @param fileParam The path to the configuration file with all the parameters in KEEL format
     */
    public knnImpute(String fileParam) {
        config_read(fileParam);
        IS = new InstanceSet();
        IStest = new InstanceSet();
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
    
    //Read the pattern file, and parse data into strings
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
            
            //parameters
            do{
                line = buf_reader.readLine();
            }while(line.length()==0);
            out = line.split("k = ");
            nneigh = (new Integer(out[1])).intValue(); //parse the string into a double
            
            file_reader.close();
            
        } catch (IOException e) {
            System.out.println("IO exception = " + e );
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    /**
     * <p>
     * Computes the distance between two instances (without previous normalization)
     * </p>
     * @param i First instance 
     * @param j Second instance
     * @return The Euclidean distance between i and j
     */
    private double distance(Instance i,Instance j){
        double dist = 0;
        int in = 0;
        int out = 0;
        
        for(int l = 0; l < nvariables;l++){
            Attribute a = Attributes.getAttribute(l);
            
            direccion = a.getDirectionAttribute();
            tipo = a.getType();
            
            if(direccion == Attribute.INPUT){
                if(tipo != Attribute.NOMINAL && !i.getInputMissingValues(in)){
                    //real value, apply euclidean distance
                    dist += (i.getInputRealValues(in)-j.getInputRealValues(in))*(i.getInputRealValues(in)-j.getInputRealValues(in));
                } else{
                    if(!i.getInputMissingValues(in) && i.getInputNominalValues(in)!=j.getInputNominalValues(in))
                        dist += 1;
                }
                in++;
            }else{
                if(direccion == Attribute.OUTPUT){
                    if(tipo != Attribute.NOMINAL && !i.getOutputMissingValues(out)){
                        dist += (i.getOutputRealValues(out)-j.getOutputRealValues(out))*(i.getOutputRealValues(out)-j.getOutputRealValues(out));
                    } else{
                        if(!i.getOutputMissingValues(out) && i.getOutputNominalValues(out)!=j.getOutputNominalValues(out))
                            dist += 1;
                    }
                    out++;
                }
            }
        }
        return Math.sqrt(dist);
    }
    
    /**
     * <p>
     *	Checks if two instances present MVs for the same attributes
     * </p>
     * @param inst1 the first instance
     * @param inst2 the second instance
     * @return true if both instances have missing values for the same attributes, false otherwise
     */
    protected boolean sameMissingInputAttributes(Instance inst1, Instance inst2){
    	boolean sameMVs = true;
    	
    	for(int i = 0;i < Attributes.getInputNumAttributes() && sameMVs ;i++){
    		if(inst1.getInputMissingValues(i) != inst2.getInputMissingValues(i))
    			sameMVs = false;
    	}
    	
    	return sameMVs;
    }
    
    /**
     * Finds the nearest neighbor with a valid value in the specified attribute
     * @param inst the instance to be taken as reference
     * @param a the attribute which will be checked
     * @return the nearest instance that has a valid value in the attribute 'a'
     */
    protected Instance nearestValidNeighbor(Instance inst, int a){
    	double distance = Double.POSITIVE_INFINITY;
    	Instance inst2;
    	int nn = 0;
    	
    	for(int i = 0;i<IS.getNumInstances();i++){
    		inst2 = IS.getInstance(i);
    		if(inst!= inst2 && !inst2.getInputMissingValues(a) && distance(inst,inst2)<distance){
    			distance = distance(inst,inst2);
    			nn = i;
    		}
    			
    	}
    	
    	return IS.getInstance(nn);
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
        double []outputs;
        double []outputs2;
        Instance neighbor;
        double dist,mean,dist2;
        int actual,totalN,nn_aux;
        int [] N = new int[nneigh];
        double []Ndist = new double [nneigh];
        boolean allNull;
        
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

            
            for(int i = 0;i < ndatos;i++){
                Instance inst = IS.getInstance(i);
                
                in = 0;
                out = 0;
                if(inst.existsAnyMissingValue()){
                    //since exists MVs, first we must compute the nearest
                    //neighbours for our instance
                    for(int n = 0;n<nneigh;n++){
                        Ndist[n] = Double.MAX_VALUE;
                        N[n] = -1;
                    }
                    for(int k=0;k<ndatos;k++){
                        neighbor = IS.getInstance(k);
                        
                        if(!sameMissingInputAttributes(inst, neighbor)){
                            dist = distance(inst, neighbor);
                            
                            actual = -1;
                            for(int n = 0;n<nneigh;n++){
                                if(dist < Ndist[n]){
                                    if(actual!=-1){
                                    	if(Ndist[n]>Ndist[actual]){
                                    		actual = n;
                                    	}
                                    }
                                    else
                                        actual = n;
                                }
                            }
                            if(actual!=-1){
                                N[actual] = k;
                                Ndist[actual] = dist;
                            }
                        }
                        	
                    }
                }
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
                            else{
                            	allNull = true;
                                timesSeen[j] = new FreqList();
                                if(tipo != Attribute.NOMINAL){
                                    mean = 0.0;
                                    totalN = 0;
                                    for(int m = 0;m < nneigh;m++){
                                    	if(N[m]!=-1){
                                    		Instance inst2 = IS.getInstance(N[m]);
                                    		if(!inst2.getInputMissingValues(in)){
                                    			mean += inst2.getInputRealValues(in);
                                    			totalN++;
                                    			allNull = false;
                                    		}
                                    		
                                    	}
                                    }
                                    if(!allNull){
                                    	mean = mean / (double)totalN;
                                    	if(tipo == Attribute.INTEGER)
                                    		mean = new Double(mean+0.5).intValue();
                                    	X[i][j] = new String(String.valueOf(mean));
                                    }
                                    else
                                    	//if no option left, lets take the nearest neighbor with a valid attribute value
                                    	X[i][j] = String.valueOf(nearestValidNeighbor(inst, in).getInputRealValues(in));
                                }else{
                                    for(int m = 0;m < nneigh;m++){
                                        Instance inst2 = IS.getInstance(N[m]);
                                        
                                        if(N[m]!=-1 && !inst2.getInputMissingValues(in)){
                                        	timesSeen[j].AddElement( inst2.getInputNominalValues(in));
                                        }
                                        
                                    }
                                    if(timesSeen[j].totalElements!=0)
                                    	X[i][j] = new String(timesSeen[j].mostCommon().getValue()); //replace missing data
                                    else
                                    	X[i][j] = nearestValidNeighbor(inst, in).getInputNominalValues(in);
                                }
                                
                            }
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
                                	allNull = true;
                                    timesSeen[j] = new FreqList();
                                    if(tipo != Attribute.NOMINAL){
                                        mean = 0.0;
                                        totalN = 0;
                                        for(int m = 0;m < nneigh;m++){
                                        	if(N[m]!=-1){
                                        		totalN++;
                                        		allNull = false;
                                        		Instance inst2 = IS.getInstance(N[m]);
                                            	mean += inst2.getOutputRealValues(out);
                                        	}
                                        }
                                        if(!allNull){
                                        	mean = mean / (double)totalN;
                                        	if(tipo == Attribute.INTEGER)
                                        		mean = new Double(mean+0.5).intValue();
                                        	
                                        	mean = this.boundValueToAttributeLimits(mean, a);
                                        	X[i][j] = new String(String.valueOf(mean));
                                        }
                                        else{
                                        	//if no option left, lets take the nearest neighbor with a valid attribute value
                                        	X[i][j] = new String("<null>");
                                        }
                                    }else{
                                        for(int m = 0;m < nneigh;m++){
                                            Instance inst2 = IS.getInstance(N[m]);
                                            
                                            if(N[m]!=-1){
                                            	timesSeen[j].AddElement( inst2.getOutputNominalValues(out));
                                            }
                                            
                                        }
                                        if(timesSeen[j].totalElements!=0)
                                        	X[i][j] = new String(timesSeen[j].mostCommon().getValue()); //replace missing data
                                        else
                                        	X[i][j] = new String("<null>");
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
            e.printStackTrace();
            System.exit(-1);
        }
        write_results(output_train_name);
        /***************************************************************************************/
        //does a test file associated exist?
        if(input_train_name.compareTo(input_test_name)!=0){
            try {
                
                // Load in memory a dataset that contains a classification problem
                IStest.readSet(input_test_name,false);
                int in = 0;
                int out = 0;
                
                ndatos = IStest.getNumInstances();
                nvariables = Attributes.getNumAttributes();
                nentradas = Attributes.getInputNumAttributes();
                nsalidas = Attributes.getOutputNumAttributes();
                
                X = new String[ndatos][nvariables];//matrix with transformed data
                
                timesSeen = new FreqList[nvariables];
                mostCommon = new String[nvariables];
                
                //now, search for missed data, and replace them with
                //the most common value
                
                for(int i = 0;i < ndatos;i++){
                    Instance inst = IStest.getInstance(i);
                    
                    in = 0;
                    out = 0;
                    if(inst.existsAnyMissingValue()){
                        //since exists MVs, first we must compute the nearest
                        //neighbours for our instance
                        for(int n = 0;n<nneigh;n++){
                            Ndist[n] = Double.MAX_VALUE;
                            N[n] = -1;
                        }
                        for(int k=0;k<ndatos;k++){
                            neighbor = IS.getInstance(k);
                            
                            if(!sameMissingInputAttributes(inst, neighbor)){
                                dist = distance(inst, neighbor);
                                
                                actual = -1;
                                for(int n = 0;n<nneigh;n++){
                                    if(dist < Ndist[n]){
                                        if(actual!=-1){
                                        	if(Ndist[n]>Ndist[actual]){
                                        		actual = n;
                                        	}
                                        }
                                        else
                                            actual = n;
                                    }
                                }
                                if(actual!=-1){
                                    N[actual] = k;
                                    Ndist[actual] = dist;
                                }
                            }
                        }
                    }
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
                                else{
                                	allNull = true;
                                    timesSeen[j] = new FreqList();
                                    if(tipo != Attribute.NOMINAL){
                                        mean = 0.0;
                                        totalN = 0;
                                        for(int m = 0;m < nneigh;m++){
                                        	if(N[m]!=-1){
                                        		Instance inst2 = IS.getInstance(N[m]);
                                        		if(!inst2.getInputMissingValues(in)){
                                        			mean += inst2.getInputRealValues(in);
                                        			totalN++;
                                        			allNull = false;
                                        		}
                                        		
                                        	}
                                        }
                                        if(!allNull){
                                        	mean = mean / (double)totalN;
                                        	if(tipo == Attribute.INTEGER)
                                        		mean = new Double(mean+0.5).intValue();
                                        	
                                        	mean = this.boundValueToAttributeLimits(mean, a);
                                        	X[i][j] = new String(String.valueOf(mean));
                                        }
                                        else
                                        	//if no option left, lets take the nearest neighbor with a valid attribute value
                                        	X[i][j] = String.valueOf(nearestValidNeighbor(inst, in).getInputRealValues(in));
                                    }else{
                                        for(int m = 0;m < nneigh;m++){
                                            Instance inst2 = IS.getInstance(N[m]);
                                            
                                            if(N[m]!=-1 && !inst2.getInputMissingValues(in)){
                                            	timesSeen[j].AddElement( inst2.getInputNominalValues(in));
                                            }
                                            
                                        }
                                        if(timesSeen[j].totalElements!=0)
                                        	X[i][j] = new String(timesSeen[j].mostCommon().getValue()); //replace missing data
                                        else
                                        	X[i][j] = nearestValidNeighbor(inst, in).getInputNominalValues(in);
                                    }
                                    
                                }
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
                                    	allNull = true;
                                        timesSeen[j] = new FreqList();
                                        if(tipo != Attribute.NOMINAL){
                                            mean = 0.0;
                                            totalN = 0;
                                            for(int m = 0;m < nneigh;m++){
                                            	if(N[m]!=-1){
                                            		totalN++;
                                            		allNull = false;
                                            		Instance inst2 = IS.getInstance(N[m]);
                                                	mean += inst2.getOutputRealValues(out);
                                            	}
                                            }
                                            if(!allNull){
                                            	mean = mean / (double)totalN;
                                            	if(tipo == Attribute.INTEGER)
                                            		mean = new Double(mean+0.5).intValue();
                                            	X[i][j] = new String(String.valueOf(mean));
                                            }
                                            else
                                            	X[i][j] = new String("<null>");
                                        }else{
                                            for(int m = 0;m < nneigh;m++){
                                                Instance inst2 = IS.getInstance(N[m]);
                                                
                                                if(N[m]!=-1){
                                                	timesSeen[j].AddElement( inst2.getOutputNominalValues(out));
                                                }
                                                
                                            }
                                            if(timesSeen[j].totalElements!=0)
                                            	X[i][j] = new String(timesSeen[j].mostCommon().getValue()); //replace missing data
                                            else
                                            	X[i][j] = new String("<null>");
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
                e.printStackTrace();
                System.exit(-1);
            }
            write_results(output_test_name);
        }
    }
    
}
