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

package keel.Algorithms.Preprocess.Missing_Values.SVDimpute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import keel.Algorithms.Preprocess.Missing_Values.EM.*;
import keel.Algorithms.Preprocess.Missing_Values.EM.util.MachineAccuracy;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.DatasetException;
import keel.Dataset.HeaderFormatException;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.SVD;
import flanagan.analysis.Regression;

/**
 * This class implements the Single Value Decomposition Imputation 
 * @author Julian Luengo Martin
 */
public class SVDimpute {
	final static int TTLS = 1;
	final static int MRIDGE = 2;
	final static int IRIDGE = 3;

	DenseMatrix m;
	double eps = MachineAccuracy.EPSILON; //Floating-point relative accuracy
	InstanceSet IStrain;
	InstanceSet IStest;
	
	//parameters
	int maxit;
	double stagtol = 5e-10;
	int optRegression;
	int neigs;
	double regpar = Double.NaN;
	double minvarfrac = 0;
	double inflation = 1;
	int trunc = 4;
	boolean useRegPar = false;
	int nSingularValuestaken = 10;

	String input_train_name = new String();

	String input_test_name = new String();

	String output_train_name = new String();

	String output_test_name = new String();

	String temp = new String();

	String data_out = new String("");
	
	/**
	 * <p>
	 * Creates a new object of SVDI based on the parameter file provided
	 * </p>
	 * @param fileParam the path to the parameter file
	 */
	public SVDimpute(String fileParam) {
		config_read(fileParam);
		
		IStrain = new InstanceSet();
		IStest = new InstanceSet();
		
		try {
			IStrain.readSet(input_train_name, true);
			IStest.readSet(input_test_name, false);
		} catch (DatasetException e) {
			System.err.println("Data set loading error, now exiting SVDimpute");
			e.printStackTrace();
			System.exit(-1);
		} catch (HeaderFormatException e) {
			System.err.println("Data set loading error, now exiting SVDimpute");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * <p>
	 * It runs the SVDI algorithm once the configuration has been readed
	 * </p>
	 */
	public void run(){
		DenseMatrix train,test;
		Regression reg;
		Attribute at;
		Instance inst;
		String[][] X;
		EM initialEstimation;
		SVD sings = null;
		EV greaterEV;
		DenseMatrix V_t;
		int kmisr[][],kvalr[][],pos,in,out,minSize;
		double y[],x[][],coefs[],result[];
		
		initialEstimation = new EM(maxit,stagtol,optRegression,neigs,regpar,
				minvarfrac, inflation, trunc,useRegPar);
		
		//put the train data into a DenseMatrix Class, looking for easier matrix operations
		train = new DenseMatrix(IStrain.getNumInstances(),Attributes.getNumAttributes());
		for(int i=0;i<IStrain.getNumInstances();i++){
			inst = IStrain.getInstance(i);
			in = out = 0;
			for(int j=0;j<Attributes.getNumAttributes();j++){
				at = Attributes.getAttribute(j);
				if(at.getDirectionAttribute() == Attribute.INPUT){
					train.set(i, j, inst.getAllInputValues()[in]);
					in++;
				}
				else{
					train.set(i, j, inst.getAllOutputValues()[out]);
					out++;
				}
			}
		}
		if(nSingularValuestaken > Attributes.getNumAttributes()-3){
			System.out.print("\nWarning: There are less attributes than Singular Values desired. ");
			System.out.println("Reducing the amount of Singular Values from "+nSingularValuestaken+" to "+(Attributes.getInputNumAttributes()-3));;
			nSingularValuestaken = Attributes.getNumAttributes()-3;
		}
		minSize = Math.min(IStrain.getNumInstances(),IStest.getNumInstances());
		if(nSingularValuestaken > minSize){
			System.out.print("\nWarning: There are less instances than Singular Values desired. ");
			System.out.println("Reducing the amount of Singular Values from "+nSingularValuestaken+" to "+minSize);;
			nSingularValuestaken = minSize;
		}
		//Impute by means of EM regression
		//the results are stored in the matrix passed by argument
		initialEstimation.regem(train,IStrain);
		System.out.print("Computing the SVD fot the EM refined data... ");
		//compute the eigenvectors from the data set
		greaterEV = psings(train, nSingularValuestaken);
//		try {
//			sings = SVD.factorize(train);
//		} catch (NotConvergedException e) {
//			System.err.println("Error: Matrix Singular Value Descomposition didn't converge");
//			e.printStackTrace();
//			System.exit(1);
//		}
		V_t = greaterEV.V;
//		V_t = sings.getVt();
		System.out.println("Done");
		//obtain the positions of the missing values
		//previously computed by the EM algorithm
		//and impute them with the SVD regression
		System.out.print("Applying SVD regression... ");
		kmisr = initialEstimation.getKmisr();
		x = new double[nSingularValuestaken][Attributes.getNumAttributes()-1];
		y = new double[Attributes.getNumAttributes()-1];
		for(int i=0;i<kmisr.length;i++){
			result = new double[kmisr[i].length];
			for(int j=0;j<kmisr[i].length;j++){
				pos = kmisr[i][j];
				for(int k=0,a=0;k<Attributes.getNumAttributes();k++){
					if(k!=pos){
						y[a] = train.get(i, k);
						a++;
					}
				}
				for(int b=0;b<nSingularValuestaken;b++){
					for(int k=0,a=0;k<Attributes.getNumAttributes();k++){
						if(k!=pos){
							x[b][a] = V_t.get(b, k);
							a++;
						}
					}
				}
				//apply the regression
				reg = new Regression(x,y);
				//general linear regression
				reg.linear();
				coefs = reg.getCoeff();
				
				result[j] = coefs[0];
				for(int k=1;k<coefs.length;k++){
					result[j] += coefs[k] * V_t.get(k-1, pos);
				}
			}
			for(int j=0;j<kmisr[i].length;j++){
				pos = kmisr[i][j];
				train.set(i, pos, result[j]);
			}
		}
		System.out.println("Done");
		
		X = new String[IStrain.getNumInstances()][Attributes.getNumAttributes()];//matrix with transformed data
		
		data2string(train,X,IStrain);
		write_results(output_train_name,X,IStrain);
		
		/** Apply on test data **/
		System.out.println("\n\n Test partition");
		
		test = new DenseMatrix(IStest.getNumInstances(),Attributes.getNumAttributes());
		for(int i=0;i<IStest.getNumInstances();i++){
			inst = IStest.getInstance(i);
			in = out = 0;
			for(int j=0;j<Attributes.getNumAttributes();j++){
				at = Attributes.getAttribute(j);
				if(at.getDirectionAttribute() == Attribute.INPUT){
					test.set(i, j, inst.getAllInputValues()[in]);
					in++;
				}
				else{
					test.set(i, j, inst.getAllOutputValues()[out]);
					out++;
				}
			}
		}
//		Impute by means of EM regression
		//the results are stored in the matrix passed by argument
		initialEstimation.regem(test,IStest);
		System.out.print("Computing the SVD fot the EM refined data... ");
		//compute the eigenvectors from the data set
		greaterEV = psings(test, nSingularValuestaken);
		V_t = greaterEV.V;
		System.out.println("Done");
		//obtain the positions of the missing values
		//previously computed by the EM algorithm
		//and impute them with the SVD regression
		System.out.print("Applying SVD regression... ");
		kmisr = initialEstimation.getKmisr();
		x = new double[nSingularValuestaken][Attributes.getNumAttributes()-1];
		y = new double[Attributes.getNumAttributes()-1];
		for(int i=0;i<kmisr.length;i++){
			result = new double[kmisr[i].length];
			for(int j=0;j<kmisr[i].length;j++){
				pos = kmisr[i][j];
				for(int k=0,a=0;k<Attributes.getNumAttributes();k++){
					if(k!=pos){
						y[a] = train.get(i, k);
						a++;
					}
				}
				for(int b=0;b<nSingularValuestaken;b++){
					for(int k=0,a=0;k<Attributes.getNumAttributes();k++){
						if(k!=pos){
							x[b][a] = V_t.get(b, k);
							a++;
						}
					}
				}
				//apply the regression
				reg = new Regression(x,y);
				//general linear regression
				reg.linear();
				coefs = reg.getCoeff();
				
				result[j] = coefs[0];
				for(int k=1;k<coefs.length;k++){
					result[j] += coefs[k] * V_t.get(k-1, pos);
				}
			}
			for(int j=0;j<kmisr[i].length;j++){
				pos = kmisr[i][j];
				train.set(i, pos, result[j]);
			}
		}
		System.out.println("Done");
		
		X = new String[IStest.getNumInstances()][Attributes.getNumAttributes()];//matrix with transformed data
		
		data2string(test,X,IStest);
		write_results(output_test_name,X,IStest);
	}
	
	/**
	 * <p>
	 * Computes the rmax eigenvalues of a given matrix (with greater absolute value)
	 * </p>
	 * @param A The matrix from which we want to compute the eigenvalues
	 * @param rmax the maximum number of greatest eigenvalues obtained
	 * @return the rmax eigenvalues with greater absolute values
	 */
	public EV psings(DenseMatrix A, int rmax){
		EV values = null;
		int m,n,r;
		DenseMatrix V,V_t;
		double d[] = null;
		double posEigen[] = null;
		double d_min;
		EVpair p[];
		SVD sings;

		m = A.numRows();
		n = A.numColumns();
		if(rmax > Math.min(m, n))
			rmax = Math.min(m, n);
		
		//get first rmax eigenvectors of A
		sings = new SVD(m, n);

		try {
			sings = SVD.factorize(A);
			d = sings.getS();
		} catch (NotConvergedException e) {
			System.err.println("Error: Matrix Singular Value Descomposition didn't converge");
//			e.printStackTrace();
			System.exit(1);
		}

		p = new EVpair[d.length];
		for(int i=0;i<p.length;i++)
			p[i] = new EVpair(d[i],i);
		
		//sort in ascending order
//		Arrays.sort(p);
		//ensure that eigenvalues are monotonically decreasing
//		int len = d.length;
//		int hlen = len / 2;
//		EVpair temp;
//		for(int i = 0; i < hlen; i++)
//		{
//		    temp = p[i];
//		    p[i] = p[len - 1 - i];
//		    p[len - 1 - i] = temp;
//		}
		Arrays.sort(p,Collections.reverseOrder());
		
//		d_min = p[0].eigenValue * Math.max(m,n) * eps;
//		r = 0;
//		for(int i=0;i<d.length;i++)
//			if(p[i].eigenValue>d_min)
//				r++;
		r = d.length;
		
		posEigen = new double[r];
		V_t = sings.getVt();
		V = new DenseMatrix(rmax,n);
		for(int i=0;i<Math.min(r,rmax);i++){
			posEigen[i] = p[i].eigenValue;
			for(int j=0;j<n;j++){
				V.set(i,j,V_t.get(p[i].evIndex,j));
			}
		}
		values = new EV(V,posEigen);
		return values;
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
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("RegrParameter = ");
			regpar = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("MaxIter = ");
			maxit = (new Integer(out[1])).intValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("RegressionType = ");
			if(out[1].compareTo("mridge")==0)
				optRegression = EM.MRIDGE;
			else if(out[1].compareTo("iridge")==0)
				optRegression = EM.IRIDGE;
			else if(out[1].compareTo("ttls")==0)
				optRegression = EM.TTLS;
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("StagnationTolerance = ");
			stagtol = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("NumberOfEigens = ");
			neigs = (new Integer(out[1])).intValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("MinimumFractionOfTotalVariation = ");
			minvarfrac = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("CovMatrixInflationFactor = ");
			inflation = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("UseRegPar = ");
			useRegPar = (out[1].compareTo("Yes")==0); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("NumOfSingularVectors = ");
			nSingularValuestaken = (new Integer(out[1])).intValue(); // parse the string into

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
				//older version - SVDi only used inputs attributes
//				if(a.getDirectionAttribute() == Attribute.INPUT){
//					value = mat.get(i, in);
//					in++;
//				}
//				else{
//					value = inst.getAllOutputValues()[out];
//					out++;
//				}
				
				value = mat.get(i, j);
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

