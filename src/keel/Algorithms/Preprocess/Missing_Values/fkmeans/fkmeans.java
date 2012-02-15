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
 * @author Written by Julián Luengo Martín 04/12/2006
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.fkmeans;
import java.io.*;
import java.util.*;
import keel.Dataset.*;
import keel.Algorithms.Preprocess.Basic.*;
import org.core.*;

/**
 * <p>
 * This class imputes the missing values by means of the Fuzzy K-means clustering algorithm. It creates a set of K fuzzy-clusters, and the missing values
 * are filled in with the all the centroids, weighting the values with the membership degree of the instance to each cluster (based on the distance).
 * </p>
 */
public class fkmeans {
    
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
    int K = 1; //number of clusters
    long semilla = 12345678;
    double minError = 1;
    int maxIter = 1000;
    double fuzzifier;
    InstanceSet IS,IStest;
    String input_train_name = new String();
    String input_test_name = new String();
    String output_train_name = new String();
    String output_test_name = new String();
    String temp = new String();
    String data_out = new String("");
    
    /** Creates a new instance of fkmeans
     * @param fileParam The path to the configuration file with all the parameters in KEEL format
     */
    public fkmeans(String fileParam) {
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
            out = line.split("seed = ");
            semilla = (new Long(out[1])).longValue(); //parse the string into a integer
            
            do{
                line = buf_reader.readLine();
            }while(line.length()==0);
            out = line.split("k = ");
            K = (new Integer(out[1])).intValue(); //parse the string into a integer
            
            do{
                line = buf_reader.readLine();
            }while(line.length()==0);
            out = line.split("error = ");
            minError = (new Double(out[1])).doubleValue(); //parse the string into a double
            
            do{
                line = buf_reader.readLine();
            }while(line.length()==0);
            out = line.split("iterations = ");
            maxIter = (new Integer(out[1])).intValue(); //parse the string into a double
            
            do{
                line = buf_reader.readLine();
            }while(line.length()==0);
            out = line.split("m = ");
            fuzzifier = (new Double(out[1])).doubleValue(); //parse the string into a integer
            
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
        return dist;
    }
    
    /**
     * <p>
     * Process the training and test files provided in the parameters file to the constructor.
     * </p>
     */
    public void process(){
        //declarations
        double []outputs;
        double []outputs2;
        Instance neighbor;
        double dist,mean,tmp;
        int actual;
        Randomize rnd = new Randomize();
        Instance ex;
        fuzzygCenter kmeans = null;
        int iterations = 0;
        double E;
        double prevE;
        int totalMissing = 0;
        boolean allMissing = true;
        
        rnd.setSeed(semilla);
        //PROCESS
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
            kmeans = new fuzzygCenter(K,ndatos,nvariables,fuzzifier);
            
            timesSeen = new FreqList[nvariables];
            mostCommon = new String[nvariables];
            
            //first, we choose k 'means' randomly from all
            //instances
            totalMissing = 0;
            for(int i = 0;i < ndatos;i++){
                Instance inst = IS.getInstance(i);
                if(inst.existsAnyMissingValue())
                	totalMissing++;
            }
            if(totalMissing == ndatos)
            	allMissing = true;
            else
            	allMissing = false;
            for(int numMeans = 0;numMeans<K;numMeans++){
                do{
                    actual = (int) (ndatos*rnd.Rand());
                    ex = IS.getInstance(actual);
                }while(ex.existsAnyMissingValue() && !allMissing);
                
                kmeans.copyCenter(ex,numMeans);
            }
            //now, iterate adjusting clusters' centers and
            //instances to them
            prevE = 0;
            iterations = 0;
            do{
                for(int i = 0;i < ndatos;i++){
                    Instance inst = IS.getInstance(i);
                    
                    kmeans.setMembershipOf(inst,i);
                    
                }
                //set new centers
                kmeans.recalculateCenters(IS);
                //compute RMSE
                E = 0;
                for(int i = 0;i < ndatos;i++){
                    Instance inst = IS.getInstance(i);
                    for(int k=0;k<K;k++){
                        E += (kmeans.distance(inst,k)*kmeans.getMembershipOf(i,k));
                    }
                }
                iterations++;
                //System.out.println(iterations+"\t"+E);
                if(Math.abs(prevE - E ) == 0)
                    iterations = maxIter;
                else
                    prevE = E;
            }while(E>minError && iterations < maxIter);
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
                            else{
                                if(tipo != Attribute.NOMINAL){
                                	tmp = -1.0;
                                    for(int k=0;k<K;k++){
                                    	if(kmeans.valueAt(k,j).compareTo("<null>")!=0){
                                    		if(tmp==-1.0)
                                    			tmp = 0.0;
                                    		tmp += kmeans.getMembershipOf(i,k)*new Double(kmeans.valueAt(k,j)).doubleValue();
                                    		if(tmp < a.getMinAttribute())
                                    			tmp = a.getMinAttribute();
                                    		if(tmp > a.getMaxAttribute())
                                    			tmp = a.getMaxAttribute();
                                    	}
                                    }
                                    if(tmp!=-1.0){
                                    	if(tipo==Attribute.INTEGER)
                                        	tmp = (int) tmp;
                                    	X[i][j] = new String(String.valueOf(tmp));
                                    }
                                    else
                                    	X[i][j] = "<null>";
                                }else{
                                    actual = kmeans.getClusterOf(inst);
                                    X[i][j] = new String(kmeans.valueAt(actual,j));
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
                                    if(tipo != Attribute.NOMINAL){
                                    	tmp = -1.0;
                                        for(int k=0;k<K;k++){
                                        	if(kmeans.valueAt(k,j).compareTo("<null>")!=0){
                                        		if(tmp==-1.0)
                                        			tmp = 0.0;
                                        		tmp += kmeans.getMembershipOf(i,k)*new Double(kmeans.valueAt(k,j)).doubleValue();
                                        		if(tmp < a.getMinAttribute())
                                        			tmp = a.getMinAttribute();
                                        		if(tmp > a.getMaxAttribute())
                                        			tmp = a.getMaxAttribute();
                                        	}
                                        }
                                        if(tmp!=-1.0){
                                        	if(tipo==Attribute.INTEGER)
                                            	tmp = (int) tmp;
                                        	X[i][j] = new String(String.valueOf(tmp));
                                        }
                                        else
                                        	X[i][j] = "<null>";
                                    }else{
                                        actual = kmeans.getClusterOf(inst);
                                        X[i][j] = new String(kmeans.valueAt(actual,j));
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
                
               
                for(int i = 0;i < ndatos;i++){
                    Instance inst = IStest.getInstance(i);
                    
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
                                else{
                                    if(tipo != Attribute.NOMINAL){
                                        tmp = -1.0;
                                        for(int k=0;k<K;k++){
                                        	if(kmeans.valueAt(k,j).compareTo("<null>")!=0){
                                        		if(tmp==-1.0)
                                        			tmp = 0.0;
                                        		tmp += kmeans.getMembershipOf(i,k)*new Double(kmeans.valueAt(k,j)).doubleValue();
                                        		if(tmp < a.getMinAttribute())
                                        			tmp = a.getMinAttribute();
                                        		if(tmp > a.getMaxAttribute())
                                        			tmp = a.getMaxAttribute();
                                        	}
                                        }
                                        if(tmp!=-1.0){
                                        	if(tipo==Attribute.INTEGER)
                                            	tmp = (int) tmp;
                                        	X[i][j] = new String(String.valueOf(tmp));
                                        }
                                        else
                                        	X[i][j] = "<null>";
                                    }else{
                                        actual = kmeans.getClusterOf(inst);
                                        X[i][j] = new String(kmeans.valueAt(actual,j));
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
                                        if(tipo != Attribute.NOMINAL){
                                        	tmp = -1.0;
                                            for(int k=0;k<K;k++){
                                            	if(kmeans.valueAt(k,j).compareTo("<null>")!=0){
                                            		if(tmp==-1.0)
                                            			tmp = 0.0;
                                            		tmp += kmeans.getMembershipOf(i,k)*new Double(kmeans.valueAt(k,j)).doubleValue();
                                            		if(tmp < a.getMinAttribute())
                                            			tmp = a.getMinAttribute();
                                            		if(tmp > a.getMaxAttribute())
                                            			tmp = a.getMaxAttribute();
                                            	}
                                            }
                                            if(tmp!=-1.0){
                                            	if(tipo==Attribute.INTEGER)
                                                	tmp = (int) tmp;
                                            	X[i][j] = new String(String.valueOf(tmp));
                                            }
                                            else
                                            	X[i][j] = "<null>";
                                        }else{
                                            actual = kmeans.getClusterOf(inst);
                                            X[i][j] = new String(kmeans.valueAt(actual,j));
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
