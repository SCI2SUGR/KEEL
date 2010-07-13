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
 * 
 * File: OutputIFS.java
 * 
 * General Framework to print results of performing either instance or feature selection
 * 
 * @author Written by Joaquín Derrac (University of Granada) 21/3/2009 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Preprocess.Basic;

import keel.Dataset.*;
import org.core.*;

public class OutputFS {

	/** 
	 * General Train data output method. It performs a basic treatment of nominal and null values
	 * 
	 * @param outFile Name of the output file 
	 * @param data Raw train data in double format 	 
	 * @param nominal Nominal train data
	 * @param nulls Values in train data which should be treated as null values
	 * @param outData Output value of the train data
	 * @param features Selected features to be written
	 * @param inputs Input attributes information
	 * @param output Output attribute information
	 * @param inputAtt Initial number of features
	 * @param relation Name of the dataset
	 */
	public static void writeTrainOutput(String outFile, double data[][], int nominal[][], boolean nulls[][], int outData[],int features [], Attribute inputs[], Attribute output, int inputAtt, String relation) {

		String text = "";
		
		//Security measures to avoid destruction of the entire dataset
		if(voidVector(features)==true){
			features[0]=1;
		}
		
		//Printing input attributes
		text+= "@relation "+ relation +"\n";
		
		for (int i=0; i<inputAtt; i++) {
			
			//If corresponding feature has been selected
			if(features[i]==1){

				text+= "@attribute "+ inputs[i].getName()+" ";
				
				//Listing nominal values
				if (inputs[i].getType() == Attribute.NOMINAL) {
					text+= "{";
					for (int j=0; j<inputs[i].getNominalValuesList().size(); j++) {
						
						text+=(String)inputs[i].getNominalValuesList().elementAt(j);
						 
						if (j <inputs[i].getNominalValuesList().size() -1) {
							text+= ", ";
						}
					}
					text+= "}\n";
					
				} else {
					//Listing numerical values
					if (inputs[i].getType() == Attribute.INTEGER) {
						text+= "integer";
						text+= " ["+String.valueOf((int)inputs[i].getMinAttribute()) + ", " +  String.valueOf((int)inputs[i].getMaxAttribute())+"]\n";
					} else {
						text+= "real";
						text+= " ["+String.valueOf(inputs[i].getMinAttribute()) + ", " +  String.valueOf(inputs[i].getMaxAttribute())+"]\n";
					}
				}
			}
		}
		
		//Printing output attribute  
		text+= "@attribute "+ output.getName()+" ";
		
		//Listing nominal values
		if (output.getType() == Attribute.NOMINAL) {
			
			text+= "{";
			for (int j=0; j<output.getNominalValuesList().size(); j++) {
				text+= (String)output.getNominalValuesList().elementAt(j);
				if (j < output.getNominalValuesList().size() -1) {
					text+= ", ";
				}
			}
			text+= "}\n";
		} 
		
		//Listing numerical values
		else {
			text+= "integer ["+String.valueOf((int)output.getMinAttribute()) + ", " + String.valueOf((int)output.getMaxAttribute())+"]\n";
		}
		
		text+= "@data\n";
		
		//Writing headers to file
		Files.writeFile(outFile, text);  
		
		//Printing the data
		for (int i=0; i<data.length; i++) {

			text="";
				
			for (int j=0; j<data[i].length; j++) {
					
				//If corresponding feature has been selected
				if(features[j]==1){
					if (nulls[i][j] == false) {
						if (inputs[j].getType() == Attribute.REAL) {
							text+= String.valueOf(data[i][j]) + ",";
						} else if (Attributes.getInputAttribute(j).getType() == Attribute.INTEGER) {
							text+= String.valueOf((int)(data[i][j]))+ ",";
						} else {
							text+= (String)inputs[j].getNominalValuesList().elementAt(nominal[i][j]) + ",";
						}
					} 
					else {				  
						text+= "?,";
					}
				
				}
			}    
			//Printing output attribute
			if (output.getType() == Attribute.INTEGER) {
				text+= String.valueOf(outData[i]);
			}
			else {
			    text+= (String)output.getNominalValuesList().elementAt(outData[i]);
			}
			    
			text+= "\n";
			Files.addToFile(outFile, text);
		}

	} //end-method	
	
	
	/** 
	 * General Test data output method. 
	 * 
	 * @param outFile Name of the output file 
	 * @param data Data to be written	
	 * @param features Selected features to be written 
	 * @param inputs Input attributes information
	 * @param output Output attribute information
	 * @param inputAtt Initial number of features
	 * @param relation Name of the dataset
	 */
	public static void writeTestOutput(String outFile, InstanceSet data,int features[], Attribute inputs[], Attribute output, int inputAtt, String relation) {

		String text;
		String instance;

		//Security measure to avoid destruction of the entire dataset
		if(voidVector(features)==true){
			features[0]=1;
		}
		
		//Printing input attributes
		text= "@relation "+ relation +"\n";
		
		for (int i=0; i<inputAtt; i++) {

			if(features[i]==1){
				text+= "@attribute "+ inputs[i].getName()+" ";
					
				//Listing nominal values
				if (inputs[i].getType() == Attribute.NOMINAL) {
						
					text+= "{";
						
					for (int j=0; j<inputs[i].getNominalValuesList().size(); j++) {
							
						text+=(String)inputs[i].getNominalValuesList().elementAt(j);
							 
						if (j <inputs[i].getNominalValuesList().size() -1) {
							text+= ", ";
						}
					}
						
					text+= "}\n";
						
				}
				//Listing numerical values
				else {
				
					if (inputs[i].getType() == Attribute.INTEGER) {
						text+= "integer";
						text+= " ["+String.valueOf((int)inputs[i].getMinAttribute()) + ", " +  String.valueOf((int)inputs[i].getMaxAttribute())+"]\n";
					} 
					else {
						text+= "real";
						text+= " ["+String.valueOf(inputs[i].getMinAttribute()) + ", " +  String.valueOf(inputs[i].getMaxAttribute())+"]\n";
					}
				}
			}
		}
	    
		//Printing output attribute  
		text+= "@attribute "+ output.getName()+" ";
		
		//Listing nominal values
		if (output.getType() == Attribute.NOMINAL) {
			
			text+= "{";
			for (int j=0; j<output.getNominalValuesList().size(); j++) {
				
				text+= (String)output.getNominalValuesList().elementAt(j);
				
				if (j < output.getNominalValuesList().size() -1) {
					text+= ", ";
				}
			}
			
			text+= "}\n";
			
		} 
		//Listing numerical values
		else {
			text+= "integer ["+String.valueOf((int)output.getMinAttribute()) + ", " + String.valueOf((int)output.getMaxAttribute())+"]\n";
		}
		
		text+= "@data\n";
		
		//Writing headers to file
		Files.writeFile(outFile, text);  
	    
		//Printing the data
		for (int i=0; i<data.getNumInstances(); i++) {

			text = data.getInstance(i).toString()+"\n";
			
			instance = "";

		    for (int j=0, ins=0; j<text.length(); j++) {

		    	//Converting 'null' => '?'
		    	if (text.charAt(j)=='n' && (j+1)<text.length() && text.charAt(j+1)=='u' && (j+2)<text.length() && text.charAt(j+2)=='l' && (j+3)<text.length() && text.charAt(j+3) =='l') {
		    		j+=3;
		    		if(features[ins]==1){
		    			instance = instance.concat("?");
		    		}
		        } else {
		        	if(features[ins]==1){
		        		instance = instance.concat(String.valueOf(text.charAt(j)));
		        	}
		        	if(text.charAt(j)==','){
		        		ins++;
		        		if(ins==features.length){
		        			j++;
		        			while(j<text.length()){
		        				instance = instance.concat(String.valueOf(text.charAt(j)));
		        				j++;
		        			}
		        		}
		        	}
		        }
		    	
		    }
		    Files.addToFile(outFile, instance);
		}
		
	} //end-method	
	
	/** 
	 * Tests if a vector is completely filled by 0's (void vector) 
	 * 
	 * @param vector Vector to test.
	 * 
	 * @return True if the vector is void. False if not 
	 */
	private static boolean voidVector(int [] vector){
		
		boolean voidV=true;
		
		for(int i=0;i<vector.length&&voidV;i++){
			if(vector[i]==1){
				voidV=false;
			}
		}
		return voidV;
	}
	
} //end-class 

