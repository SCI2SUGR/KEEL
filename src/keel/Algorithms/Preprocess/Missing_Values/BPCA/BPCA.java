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


//====================================================
// Adapted to Java for KEEL by Julian Luengo
// julianlm@decsai.ugr.es
//====================================================	
package keel.Algorithms.Preprocess.Missing_Values.BPCA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import no.uib.cipr.matrix.DenseMatrix;

import org.core.Fichero;

import jp.ac.naist.dynamix.mpca.BPCAFill;
import keel.Dataset.*;

public class BPCA {
	InstanceSet IStrain;
	InstanceSet IStest;
	double eps = MachineAccuracy.EPSILON; //Floating-point relative accuracy
	
	String input_train_name = new String();

	String input_test_name = new String();

	String output_train_name = new String();

	String output_test_name = new String();

	String temp = new String();

	String data_out = new String("");

	/**
	 * <p>
	 *	Creates a new object of BPCA using the parameter file indicated
	 * </p>
	 * @param fileParam The path to the parameter file
	 */
	public BPCA(String fileParam){
		config_read(fileParam);
		
		IStrain = new InstanceSet();
		IStest = new InstanceSet();
		
		try {
			IStrain.readSet(input_train_name, true);
			IStest.readSet(input_test_name, false);
		} catch (DatasetException e) {
			System.err.println("Data set loading error, now exiting BPCA");
			e.printStackTrace();
			System.exit(-1);
		} catch (HeaderFormatException e) {
			System.err.println("Data set loading error, now exiting BPCA");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * <p>
	 * Runs the BPCA algorithm.
	 * </p>
	 */
	public void run(){
		Instance inst;
		double inputs[];
		double value;
		String dataMatrix = new String("");
		String args[] = new String[2];
		String out[];
		String[][] X;
		int pos;
		DenseMatrix train,test;
		
		System.out.println("\n\t---Processing train file---\n");
		for(int i=0;i<IStrain.getNumInstances();i++){
			inst = IStrain.getInstance(i);
			inputs = inst.getAllInputValues();
			
			for(int j=0;j<inputs.length;j++){
				if(inst.getInputMissingValues(j))
					value = 999.0; //jBPCAfill flags the missing values as '999.0'
				else{
					value = inputs[j];
					if(value == 999.0) //if the real value was 999.0, change it slightly
						value += eps;
				}
				dataMatrix = dataMatrix + value;
				if(j<(inputs.length-1))
					dataMatrix = dataMatrix + "\t";
			}
			if(i < IStrain.getNumInstances()-1)
				dataMatrix = dataMatrix + "\n"; 
		}
		Fichero.escribeFichero("dataMatrix.tmp", dataMatrix);
		args[0] = "dataMatrix.tmp";
		args[1] = "filledMatrix.tmp";
		BPCAFill.main(args);
		
		train = new DenseMatrix(IStrain.getNumInstances(),Attributes.getInputNumAttributes());
		dataMatrix = Fichero.leeFichero(args[1]);
		
		out = dataMatrix.split("\\s");
		pos = 0;
		for(int i=0;i<IStrain.getNumInstances();i++){
			for(int j=0;j<Attributes.getInputNumAttributes();j++){
				train.set(i, j, Double.parseDouble(out[pos]));
				pos++;
			}
			while(pos<out.length && out[pos].compareTo("")==0)
				pos++;
		}
		
		X = new String[IStrain.getNumInstances()][Attributes.getNumAttributes()];//matrix with transformed data
		
		data2string(train,X,IStrain);
		write_results(output_train_name,X,IStrain);
		
		System.out.println("\n\t---Processing test file---\n");
		for(int i=0;i<IStest.getNumInstances();i++){
			inst = IStest.getInstance(i);
			inputs = inst.getAllInputValues();
			
			for(int j=0;j<inputs.length;j++){
				if(inst.getInputMissingValues(j))
					value = 999.0; //jBPCAfill flags the missing values as '999.0'
				else{
					value = inputs[j];
					if(value == 999.0) //if the real value was 999.0, change it slightly
						value += eps;
				}
				dataMatrix = dataMatrix + value;
				if(j<(inputs.length-1))
					dataMatrix = dataMatrix + "\t";
			}
			if(i < IStest.getNumInstances()-1)
				dataMatrix = dataMatrix + "\n"; 
		}
		Fichero.escribeFichero("dataMatrix.tmp", dataMatrix);
		args[0] = "dataMatrix.tmp";
		args[1] = "filledMatrix.tmp";
		BPCAFill.main(args);
		
		train = new DenseMatrix(IStest.getNumInstances(),Attributes.getInputNumAttributes());
		dataMatrix = Fichero.leeFichero(args[1]);
		
		out = dataMatrix.split("\\s");
		pos = 0;
		for(int i=0;i<IStest.getNumInstances();i++){
			for(int j=0;j<Attributes.getInputNumAttributes();j++){
				train.set(i, j, Double.parseDouble(out[pos]));
				pos++;
			}
			while(pos<out.length && out[pos].compareTo("")==0)
				pos++;
		}
		
		X = new String[IStest.getNumInstances()][Attributes.getNumAttributes()];//matrix with transformed data
		
		data2string(train,X,IStest);
		write_results(output_test_name,X,IStest);
		
	}
	
	
	
//	Read the pattern file, and parse data into strings
	protected void config_read(String fileParam) {
		File inputFile = new File(fileParam);

		if (inputFile == null || !inputFile.exists()) {
			System.out.println("parameter " + fileParam
					+ " file doesn't exists!");
			System.exit(-1);
		}
		// begin the configuration read from file
		try {
			FileReader file_reader = new FileReader(inputFile);
			BufferedReader buf_reader = new BufferedReader(file_reader);
			// FileWriter file_write = new FileWriter(outputFile);

			String line;

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0); // avoid empty lines for processing
											// ->
			// produce exec failure
			String out[] = line.split("algorithm = ");
			// alg_name = new String(out[1]); //catch the algorithm name
			// input & output filenames
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("inputData = ");
			out = out[1].split("\\s\"");
			input_train_name = new String(out[0].substring(1,out[0].length() - 1));
			input_test_name = new String(out[1].substring(0,out[1].length() - 1));
			if (input_test_name.charAt(input_test_name.length() - 1) == '"')
				input_test_name = input_test_name.substring(0, input_test_name
						.length() - 1);

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("outputData = ");
			out = out[1].split("\\s\"");
			output_train_name = new String(out[0].substring(1,
					out[0].length() - 1));
			output_test_name = new String(out[1].substring(0,
					out[1].length() - 1));
			if (output_test_name.charAt(output_test_name.length() - 1) == '"')
				output_test_name = output_test_name.substring(0,
						output_test_name.length() - 1);

			// parameters
//			do {
//				line = buf_reader.readLine();
//			} while (line.length() == 0);
//			out = line.split("seed = ");
//			seed = (new Integer(out[1])).intValue(); 
//			
			/*do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("SVMtype = ");
			svmType = (new String(out[1])); */


		} catch (IOException e) {
			System.out.println("IO exception = " + e);
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * Parse the DenseMatrix of INPUT real values to a String 2D array, ready for printing
	 * to a file. It also fits the values to the original bounds if needed.
	 * @param mat The DenseMatrix with the input values in double format
	 * @param X The output String matrix, ready to be printed
	 * @param IS The InstanceSet with the original values, used to obtain the OUTPUT values
	 */
	protected void data2string(DenseMatrix mat, String [][] X,InstanceSet IS){
		Attribute a;
		Instance inst;
		double value;
		int in,out;
		
		for(int i=0;i<X.length;i++){
			in = 0;
			out = 0;
			inst = IS.getInstance(i);
			for(int j=0;j<X[i].length;j++){
				a = Attributes.getAttribute(j);
				if(a.getDirectionAttribute() == Attribute.INPUT){
					value = mat.get(i, in);
					in++;
				}
				else{
					value = inst.getAllOutputValues()[out];
					out++;
				}
				if(a.getType() != Attribute.NOMINAL){
					if(value < a.getMinAttribute())
						value = a.getMinAttribute();
					else if(value > a.getMaxAttribute())
						value = a.getMaxAttribute();
				}

				if(a.getType() == Attribute.REAL)
					X[i][j] = String.valueOf(value);
				else if(a.getType() == Attribute.INTEGER)
					X[i][j] = String.valueOf(Math.round(value));
				else{
					value =  Math.round(value);
					if(value >= a.getNumNominalValues())
						value = a.getNumNominalValues()-1;
					if(value < 0)
						value = 0;
					
					X[i][j] = a.getNominalValue((int)value);
				}
			}
		}
		
	}
	
//	Write data matrix X to disk, in KEEL format
    protected void write_results(String output,String[][] X,InstanceSet IS){
        //File OutputFile = new File(output_train_name.substring(1, output_train_name.length()-1));
        try {
            FileWriter file_write = new FileWriter(output);
            
            file_write.write(IS.getHeader());
            
            //now, print the normalized data
            file_write.write("@data\n");
            for(int i=0;i<X.length;i++){
                //System.out.println(i);
                file_write.write(X[i][0]);
                for(int j=1;j<X[i].length;j++){
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

