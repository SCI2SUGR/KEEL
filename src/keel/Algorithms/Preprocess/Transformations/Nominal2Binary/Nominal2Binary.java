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
 * @author Written by Julián Luengo Martín 18/02/2009
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Transformations.Nominal2Binary;
import java.io.*;
import java.util.*;
import keel.Dataset.*;
import keel.Algorithms.Preprocess.Basic.*;

/**
 * <p>
 * This class performs the nominal to binary transformation.
 * A nominal attribute is broken down into several binary ones, one per 
 * nominal value of the original attribute. These new attributes will
 * present the '1' value if the nominal value was present, '0' otherwise.
 * </p>
 */
public class Nominal2Binary {
    
    double tempData = 0;
    String[][] X = null;
    
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
    
    /** Creates a new instance of min_max
     * @param fileParam The path to the configuration file with all the parameters in KEEL format
     */
    public Nominal2Binary(String fileParam) {
        config_read(fileParam);
        IS = new InstanceSet();
    }
    
    /**
     * <p>
     * Process the training and test files provided in the parameters file to the constructor.
     * </p>
     */
    public void transform(){
    	InstanceSet transformed = null;
    	File file;
    	PrintWriter pw;
        try {
            
            // Load in memory a dataset that contains a classification problem
            IS.readSet(input_train_name,true);
            
            transformed = convertNominal2Binary(IS);
            

        }catch (Exception e){
            System.out.println("Nominal2Binary exception = " + e );
            e.printStackTrace();
            System.exit(1);
        }
        write_results(output_train_name,transformed);
        
        /***************************************************************************************/
        //does a test file associated exist?
        if(input_train_name.compareTo(input_test_name)!=0){
            try {
                
                // Load in memory a dataset that contains a classification problem
                IS.readSet(input_test_name,false);
                
                transformed = convertNominal2Binary(IS);

            }catch (Exception e){
                System.out.println("Nominal2Binary exception = " + e );
                e.printStackTrace();
                System.exit(1);
            }
            write_results(output_test_name,transformed);
        }
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
    
    private void write_results(String output,InstanceSet transformed){
    	Instance inst;
        //File OutputFile = new File(output_train_name.substring(1, output_train_name.length()-1));
        try {
            FileWriter file_write = new FileWriter(output);
            
            file_write.write(transformed.getNewHeader());
            
            //now, print the normalized data
            file_write.write("@data\n");
            for(int i=0;i<transformed.getNumInstances();i++){
            	inst = transformed.getInstance(i);
                file_write.write(inst.toString(transformed.getAttributeDefinitions()));
                file_write.write("\n");
            }
            file_write.close();
        } catch (IOException e) {
            System.out.println("IO exception = " + e );
            System.exit(-1);
        }
    }
    
	/**
	 * Creates a new allocated KEEL's set of Instances (i.e. Instances) from a KEEL's set of instances
	 * (i.e. InstanceSet). The new InstanceSet will not contain nominal values, as they 
	 * have been transformed into binary attributes.
	 * @param is The original KEEL Instance set 
	 * @return A new allocated KEEL formatted Instance set
	 */
	public InstanceSet convertNominal2Binary(InstanceSet is){
		Attribute a,newAt;
		Instance instW,instK;
		int out,in,newNumAttributes,enlargedValueVectorPos;
		double values[];
		InstanceSet data;
		Vector atts;

		// Create header of instances object
		out = Attributes.getInputNumAttributes(); //the class attribute is usually the last one
		//convert the nominal values to binary strings
		newNumAttributes = 0;
		atts = new Vector(Attributes.getNumAttributes());
		for(int i=0;i<Attributes.getNumAttributes();i++){
			a = Attributes.getAttribute(i);
			if(a.getType()==Attribute.NOMINAL && a.getDirectionAttribute()!=Attribute.OUTPUT){
				if(a.getNumNominalValues()>2){  //more than 2 nominal values implies 1 new attribute per value
					newNumAttributes+=a.getNumNominalValues();
					for(int j=0;j<a.getNumNominalValues();j++){
						newAt = new Attribute();
						newAt.setType(Attribute.INTEGER);
						newAt.setDirectionAttribute(a.getDirectionAttribute());
						newAt.setName(a.getName()+"="+a.getNominalValue(j));
						newAt.enlargeBounds(0);
						newAt.enlargeBounds(1);
						atts.addElement(newAt);
					}
				}else{ //if it has only 2 nominal values, the binary conversion is trivial
					newNumAttributes++;
					//the old attribute is not useful since it is nominal, create an integer equivalent 
					newAt = new Attribute();
					newAt.setType(Attribute.INTEGER);
					newAt.setDirectionAttribute(a.getDirectionAttribute());
					newAt.setName(a.getName());
					newAt.enlargeBounds(0);
					newAt.enlargeBounds(1);
					atts.addElement(newAt);

				}
			}
			if(a.getType()!=Attribute.NOMINAL){
				newNumAttributes++;
				atts.addElement(a);
			}
			if(a.getDirectionAttribute()==Attribute.OUTPUT){
				atts.addElement(a);
				out = newNumAttributes;
				newNumAttributes++;
			}
		}
		data = new InstanceSet(true);
		for(int i=0;i<atts.size();i++){
			data.addAttribute((Attribute)atts.get(i));
		}

		//now fill the data in the data instance set
		for(int i=0;i<is.getNumInstances();i++){
			instK = is.getInstance(i);
			in = out = 0;
			enlargedValueVectorPos = 0;
			values = new double[newNumAttributes];
			for(int j=0;j<Attributes.getNumAttributes();j++){
				a = Attributes.getAttribute(j);
				if(a.getDirectionAttribute()==Attribute.INPUT){
					if(a.getType()==Attribute.NOMINAL){
						if(a.getNumNominalValues()>2){
							for(int k=0;k<a.getNumNominalValues();k++){
								if(!instK.getInputMissingValues(in) && a.getNominalValue(k).compareTo(instK.getInputNominalValues(in))==0)
									values[enlargedValueVectorPos+k] = 1;
							}
							enlargedValueVectorPos+=a.getNumNominalValues();
						}else{
							values[enlargedValueVectorPos] = instK.getAllInputValues()[in];
							enlargedValueVectorPos++;
						}

					}else{
						values[enlargedValueVectorPos] = instK.getAllInputValues()[in];
						enlargedValueVectorPos++;
					}
					in++;
				}else{
					values[enlargedValueVectorPos] = instK.getAllOutputValues()[out];
					out++;
					enlargedValueVectorPos++;
				}
			}
			instW = new Instance(values,data.getAttributeDefinitions());
			data.addInstance(instW);
		}

		return data;
	}
		
    
}

