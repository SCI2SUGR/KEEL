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
package keel.Algorithms.Preprocess.Missing_Values.ConceptAllPossibleValues;
import java.io.*;
import java.util.*;
import keel.Dataset.*;
import keel.Algorithms.Preprocess.Basic.*;

/**
 * <p>
 * This class computes all the possible values found in the data set for a given missing value and a determined class
 * </p>
 */
public class ConceptAllPossibleValues {

    double [] mean = null;
    double [] std_dev = null;
    double tempData = 0;
    Vector[] X = null; //matrix of transformed data
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
    
    /** 
     * <p>
     * Creates a new instance of ConceptAllPossibleValues 
     * </p> 
     * @param fileParam The path to the configuration file with all the parameters in KEEL format
     */
    public ConceptAllPossibleValues(String fileParam) {
        config_read(fileParam);
        IS = new InstanceSet();
    }
    
    //Write data matrix X to disk, in KEEL format
    private void write_results(){
        //File OutputFile = new File(output_train_name.substring(1, output_train_name.length()-1));
        try {
            FileWriter file_write = new FileWriter(output_train_name);
            
            file_write.write(IS.getHeader());
            
            //now, print the normalized data
            file_write.write("@data\n");
            for(int i=0;i<ndatos;i++){
                for(int inst=0;inst<X[i].size();inst++){
                    file_write.write(((String[])X[i].elementAt(inst))[0]);
                    for(int j=1;j<nvariables;j++){
                        file_write.write(","+((String[])X[i].elementAt(inst))[j]);
                    }
                    file_write.write("\n");
                }
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
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    /**
     * <p>
     * Process the training and test files provided in the parameters file to the constructor.
     * </p>
     */
    public void process(){
        double []outputs;
        double []outputs2;
        try {
            FileWriter file_write = new FileWriter(output_train_name);
            
            try {
                
                // Load in memory a dataset that contains a classification problem
                IS.readSet(input_train_name,true);
                int in = 0;
                int out = 0;
                int in2 = 0;
                int out2 = 0;
                int lastMissing = -1;
                boolean fin = false;
                boolean stepNext = false;
                
                ndatos = IS.getNumInstances();
                nvariables = Attributes.getNumAttributes();
                nentradas = Attributes.getInputNumAttributes();
                nsalidas = Attributes.getOutputNumAttributes();
                
                String[] row = null;
                X = new Vector[ndatos];//matrix with transformed data
                for(int i=0;i<ndatos;i++)
                    X[i] = new Vector();
                
                timesSeen = new FreqList[nvariables];
                mostCommon = new String[nvariables];
                
                file_write.write(IS.getHeader());
                
                //now, print the normalized data
                file_write.write("@data\n");
                
                //now, search for missed data, and replace them with
                //the most common value
                
                for(int i = 0;i < ndatos;i++){
                    Instance inst = IS.getInstance(i);
                    in = 0;
                    out = 0;
                    row = new String[nvariables];
                    
                    for(int j = 0; j < nvariables;j++){
                        Attribute a = Attributes.getAttribute(j);
                        
                        direccion = a.getDirectionAttribute();
                        tipo = a.getType();
                        
                        if(direccion == Attribute.INPUT){
                            if(tipo != Attribute.NOMINAL && !inst.existsAnyMissingValue()){
                                row[j] = new String(String.valueOf(inst.getInputRealValues(in)));
                            } else{
                                if(!inst.existsAnyMissingValue())
                                    row[j] = inst.getInputNominalValues(in);
                                else{
                                    //missing data
                                    outputs = inst.getAllOutputValues();
                                    in2 = 0;
                                    out2 = 0;
                                    for(int attr=0;attr<nvariables;attr++){
                                        Attribute b = Attributes.getAttribute(attr);
                                        direccion = b.getDirectionAttribute();
                                        tipo = b.getType();
                                        if(direccion == Attribute.INPUT){
                                            if(tipo != Attribute.NOMINAL && !inst.getInputMissingValues(in2)){
                                                row[attr] = new String(String.valueOf(inst.getInputRealValues(in2)));
                                            } else{
                                                if(!inst.getInputMissingValues(in2))
                                                    row[attr] = inst.getInputNominalValues(in2);
                                            }
                                            in2++;
                                        }else{
                                            if(direccion == Attribute.OUTPUT){
                                                if(tipo != Attribute.NOMINAL && !inst.getOutputMissingValues(out2)){
                                                    row[attr] = new String(String.valueOf(inst.getOutputRealValues(out2)));
                                                } else{
                                                    if(!inst.getOutputMissingValues(out2))
                                                        row[attr] = inst.getOutputNominalValues(out2);
                                                }
                                                out2++;
                                            }
                                        }
                                        
                                    }
                                    //make frecuencies  for each attribute
                                    for(int attr=0;attr<nvariables;attr++){
                                        Attribute b = Attributes.getAttribute(attr);
                                        
                                        direccion = b.getDirectionAttribute();
                                        tipo = b.getType();
                                        if(direccion == Attribute.INPUT && inst.getInputMissingValues(attr)){
                                            lastMissing = attr;
                                            timesSeen[attr] = new FreqList();
                                            for(int m = 0;m < ndatos;m++){
                                                Instance inst2 = IS.getInstance(m);
                                                outputs2 = inst2.getAllOutputValues();
                                                boolean sameClass = true;
                                                //are they same concept instances??
                                                for(int k=0;k<nsalidas && sameClass;k++)
                                                    if(outputs[k]!=outputs2[k])
                                                        sameClass = false;
                                                if(sameClass){
                                                    if(tipo != Attribute.NOMINAL && !inst2.getInputMissingValues(attr)){
                                                        timesSeen[attr].AddElement( new String(String.valueOf(inst2.getInputRealValues(attr))) );
                                                        
                                                    } else{
                                                        if(!inst2.getInputMissingValues(attr)){
                                                            timesSeen[attr].AddElement( inst2.getInputNominalValues(attr));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    for(int attr=0;attr<nvariables;attr++){
                                        if(direccion == Attribute.INPUT && inst.getInputMissingValues(attr)){
                                            timesSeen[attr].reset();
                                        }
                                    }
                                    fin = false;
                                    stepNext = false;
                                    while(!fin){
                                        in2 = 0;
                                        for(int attr=0;attr<nvariables && !fin;attr++){
                                            Attribute b = Attributes.getAttribute(attr);
                                            
                                            direccion = b.getDirectionAttribute();
                                            tipo = b.getType();
                                            if(direccion == Attribute.INPUT && inst.getInputMissingValues(in2)){
                                                if(stepNext){
                                                    timesSeen[attr].iterate();
                                                    stepNext = false;
                                                }
                                                if(timesSeen[attr].outOfBounds()){
                                                    stepNext = true;
                                                    if(attr == lastMissing)
                                                        fin = true;
                                                    timesSeen[attr].reset();
                                                }
                                                if(!fin)
                                                    row[attr] = ((ValueFreq)timesSeen[attr].getCurrent()).getValue(); //replace missing data
                                            }
                                            in2++;
                                        }
                                        if(!fin){
                                            stepNext = true;
                                            file_write.write(row[0]);
                                            for(int y=1;y<nvariables;y++){
                                                file_write.write(","+row[y]);
                                            }
                                            file_write.write("\n");
                                            //X[i].addElement(row);
                                            //row = (String[])row.clone();
                                        }
                                    }
                                }
                            }
                            in++;
                        } else{
                            if(direccion == Attribute.OUTPUT){
                                if(tipo != Attribute.NOMINAL && !inst.getOutputMissingValues(out)){
                                    row[j] = new String(String.valueOf(inst.getOutputRealValues(out)));
                                } else{
                                    if(!inst.getOutputMissingValues(out))
                                        row[j] = inst.getOutputNominalValues(out);
                                    else
                                        row[j] = new String("?");
                                }
                                out++;
                            }
                        }
                    }
                    if(!inst.existsAnyMissingValue()){
                        file_write.write(row[0]);
                        for(int y=1;y<nvariables;y++){
                            file_write.write(","+row[y]);
                        }
                        file_write.write("\n");
                    }
                }
            }catch (Exception e){
                System.out.println("Dataset exception = " + e );
                e.printStackTrace();
                System.exit(-1);
            }
            file_write.close();
        } catch (IOException e) {
            System.out.println("IO exception = " + e );
            e.printStackTrace();
            System.exit(-1);
        }
        
/***************************************************************************************/        
        //does a test file associated exist?
        if(input_train_name.compareTo(input_test_name)!=0){
            try {
                FileWriter file_write = new FileWriter(output_test_name);
                
                try {
                    
                    // Load in memory a dataset that contains a classification problem
                    IS.readSet(input_test_name,false);
                    int in = 0;
                    int out = 0;
                    int in2 = 0;
                    int out2 = 0;
                    int lastMissing = -1;
                    boolean fin = false;
                    boolean stepNext = false;
                    
                    ndatos = IS.getNumInstances();
                    nvariables = Attributes.getNumAttributes();
                    nentradas = Attributes.getInputNumAttributes();
                    nsalidas = Attributes.getOutputNumAttributes();
                    
                    String[] row = null;
                    X = new Vector[ndatos];//matrix with transformed data
                    for(int i=0;i<ndatos;i++)
                        X[i] = new Vector();
                    
                    timesSeen = new FreqList[nvariables];
                    mostCommon = new String[nvariables];
                    
                    file_write.write(IS.getHeader());
                    
                    //now, print the normalized data
                    file_write.write("@data\n");
                    
                    //now, search for missed data, and replace them with
                    //the most common value
                    
                    for(int i = 0;i < ndatos;i++){
                        Instance inst = IS.getInstance(i);
                        in = 0;
                        out = 0;
                        row = new String[nvariables];
                        
                        for(int j = 0; j < nvariables;j++){
                            Attribute a = Attributes.getAttribute(j);
                            
                            direccion = a.getDirectionAttribute();
                            tipo = a.getType();
                            
                            if(direccion == Attribute.INPUT){
                                if(tipo != Attribute.NOMINAL && !inst.existsAnyMissingValue()){
                                    row[j] = new String(String.valueOf(inst.getInputRealValues(in)));
                                } else{
                                    if(!inst.existsAnyMissingValue())
                                        row[j] = inst.getInputNominalValues(in);
                                    else{
                                        //missing data
                                        outputs = inst.getAllOutputValues();
                                        in2 = 0;
                                        out2 = 0;
                                        for(int attr=0;attr<nvariables;attr++){
                                            Attribute b = Attributes.getAttribute(attr);
                                            direccion = b.getDirectionAttribute();
                                            tipo = b.getType();
                                            if(direccion == Attribute.INPUT){
                                                if(tipo != Attribute.NOMINAL && !inst.getInputMissingValues(in2)){
                                                    row[attr] = new String(String.valueOf(inst.getInputRealValues(in2)));
                                                } else{
                                                    if(!inst.getInputMissingValues(in2))
                                                        row[attr] = inst.getInputNominalValues(in2);
                                                }
                                                in2++;
                                            }else{
                                                if(direccion == Attribute.OUTPUT){
                                                    if(tipo != Attribute.NOMINAL && !inst.getOutputMissingValues(out2)){
                                                        row[attr] = new String(String.valueOf(inst.getOutputRealValues(out2)));
                                                    } else{
                                                        if(!inst.getOutputMissingValues(out2))
                                                            row[attr] = inst.getOutputNominalValues(out2);
                                                    }
                                                    out2++;
                                                }
                                            }
                                            
                                        }
                                        //make frecuencies  for each attribute
                                        for(int attr=0;attr<nvariables;attr++){
                                            Attribute b = Attributes.getAttribute(attr);
                                            
                                            direccion = b.getDirectionAttribute();
                                            tipo = b.getType();
                                            if(direccion == Attribute.INPUT && inst.getInputMissingValues(attr)){
                                                lastMissing = attr;
                                                timesSeen[attr] = new FreqList();
                                                for(int m = 0;m < ndatos;m++){
                                                    Instance inst2 = IS.getInstance(m);
                                                    outputs2 = inst2.getAllOutputValues();
                                                    boolean sameClass = true;
                                                    //are they same concept instances??
                                                    for(int k=0;k<nsalidas && sameClass;k++)
                                                        if(outputs[k]!=outputs2[k])
                                                            sameClass = false;
                                                    if(sameClass){
                                                        if(tipo != Attribute.NOMINAL && !inst2.getInputMissingValues(attr)){
                                                            timesSeen[attr].AddElement( new String(String.valueOf(inst2.getInputRealValues(attr))) );
                                                            
                                                        } else{
                                                            if(!inst2.getInputMissingValues(attr)){
                                                                timesSeen[attr].AddElement( inst2.getInputNominalValues(attr));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        for(int attr=0;attr<nvariables;attr++){
                                            if(direccion == Attribute.INPUT && inst.getInputMissingValues(attr)){
                                                timesSeen[attr].reset();
                                            }
                                        }
                                        fin = false;
                                        stepNext = false;
                                        while(!fin){
                                            in2 = 0;
                                            for(int attr=0;attr<nvariables && !fin;attr++){
                                                Attribute b = Attributes.getAttribute(attr);
                                                
                                                direccion = b.getDirectionAttribute();
                                                tipo = b.getType();
                                                if(direccion == Attribute.INPUT && inst.getInputMissingValues(in2)){
                                                    if(stepNext){
                                                        timesSeen[attr].iterate();
                                                        stepNext = false;
                                                    }
                                                    if(timesSeen[attr].outOfBounds()){
                                                        stepNext = true;
                                                        if(attr == lastMissing)
                                                            fin = true;
                                                        timesSeen[attr].reset();
                                                    }
                                                    if(!fin)
                                                        row[attr] = ((ValueFreq)timesSeen[attr].getCurrent()).getValue(); //replace missing data
                                                }
                                                in2++;
                                            }
                                            if(!fin){
                                                stepNext = true;
                                                file_write.write(row[0]);
                                                for(int y=1;y<nvariables;y++){
                                                    file_write.write(","+row[y]);
                                                }
                                                file_write.write("\n");
                                                //X[i].addElement(row);
                                                //row = (String[])row.clone();
                                            }
                                        }
                                    }
                                }
                                in++;
                            } else{
                                if(direccion == Attribute.OUTPUT){
                                    if(tipo != Attribute.NOMINAL && !inst.getOutputMissingValues(out)){
                                        row[j] = new String(String.valueOf(inst.getOutputRealValues(out)));
                                    } else{
                                        if(!inst.getOutputMissingValues(out))
                                            row[j] = inst.getOutputNominalValues(out);
                                        else
                                            row[j] = new String("?");
                                    }
                                    out++;
                                }
                            }
                        }
                        if(!inst.existsAnyMissingValue()){
                            file_write.write(row[0]);
                            for(int y=1;y<nvariables;y++){
                                file_write.write(","+row[y]);
                            }
                            file_write.write("\n");
                        }
                    }
                }catch (Exception e){
                    System.out.println("Dataset exception = " + e );
                    e.printStackTrace();
                    System.exit(-1);
                }
                file_write.close();
            } catch (IOException e) {
                System.out.println("IO exception = " + e );
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
    
}

