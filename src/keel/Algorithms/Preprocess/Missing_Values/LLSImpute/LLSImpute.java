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

//==================================================
//
// LLSimpute - Local Least Squares Imputation 
//
// (Missing Value Estimation Package)
//
// Author: Hyunsoo Kim
// Date: Fall/2003 - Spring/2004
// E-mail: hskim@cs.umn.edu
// Personal homepage: 
//     http://www.cs.umn.edu/~hskim
// Reference: Missing value estimation for DNA microarray gene 
//     expression data: Local Least Squares Imputation, H. Kim, 
//     G. H. Golub, and H. Park, Bioinformatics, to appear, 2004.
// This software may be free downloaded from site:
//     http://www.cs.umn.edu/~hskim/tools.html
// License:
//     It is free for academic or nonprofit insistutions. 
//     All right is reserved regarding commecial usage. 
//     Please consult if you try to use this package for 
//     commercial purpose.
// Comments:
//     Please let me know if you have done any improvement. 
// 
// Sample Usage:
//     // please use miss0.mat distributed in the same package 
//     load miss0.mat 
//     // read miss0.mat and impute
//     E=impute_llsq_l2_blind(0,1,210);
//     idx=find(miss_matrix==1e99);
//     answer=matrix(idx);
//     guess=E(idx);
//     nrmse=sqrt(mean((guess-answer).^2))/std(answer)
//     you will see ---> nrmse=0.5145
//
// Description:
//
// function E=impute_llsq_l2_blind(set,fig,mink) 
//     impute the missing values without k-value estimator
//
// Input parameter: 
//     set - the number of set (if set=0, it reads miss0.mat)
//     fig - draw helpful figure and echo some comments 
//     mink - the number of nearest neighbor genes
// Output parameter:
//     E - the estimated matrix (if set=0, it writes e0.csv and e0.mat)
// Data structure:
//     miss0.mat should contain miss_matrix variable.
//     missing values of miss_matrix should be 1e99.
// Needed other products: 
//     impute_rowavg.m
//
//====================================================
// Adapted to Java for KEEL by Julian Luengo
// julianlm@decsai.ugr.es
//====================================================	
package keel.Algorithms.Preprocess.Missing_Values.LLSImpute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import keel.Algorithms.Preprocess.Missing_Values.EM.util.MachineAccuracy;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.DatasetException;
import keel.Dataset.HeaderFormatException;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;

import no.uib.cipr.matrix.*;

/**
 * This class implements the Local Least Squares Imputation
 * @author Julian Luengo Martin
 */
public class LLSImpute {
	
	boolean f_rowaverage;
	int mink = 210;
	int initialMink;
	DenseMatrix A,B,Apart,Bpart;
	DenseVector w;
	
	static double eps = MachineAccuracy.EPSILON; //Floating-point relative accuracy

	InstanceSet IStrain;
	InstanceSet IStest;
	
	String input_train_name = new String();
	String input_test_name = new String();
	String output_train_name = new String();
	String output_test_name = new String();
	String temp = new String();
	String data_out = new String("");
	
	
	public LLSImpute(){
		super();
	}
	
	/**
	 * Constructor which extract the parameters from a KEEL pattern file and
	 * initializes the InstanceSet structures
	 * @param fileParam
	 */
	public LLSImpute(String fileParam) {
		config_read(fileParam);
		
		initialMink = mink;
		IStrain = new InstanceSet();
		IStest = new InstanceSet();
		
		try {
			IStrain.readSet(input_train_name, true);
			IStest.readSet(input_test_name, false);
		} catch (DatasetException e) {
			System.err.println("Data set loading error, now exiting EM");
			e.printStackTrace();
			System.exit(-1);
		} catch (HeaderFormatException e) {
			System.err.println("Data set loading error, now exiting EM");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * Parse the paramete file in KEEL format to obtain the parameters and working files
	 * @param fileParam Pattern file in KEEL format
	 */
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
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("Mink = ");
			mink = (new Integer(out[1])).intValue(); // parse the string into
			
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * Function that runs the LLSImpute over the data sets given in the pattern file in
	 * KEEL format
	 */
	public void run(){
		DenseMatrix train,test,E;
		Instance inst;
		String mat = new String();
		String[][] X;
		int in,out;
		Attribute a;
		int maxMink;
		
		//put the train data into a DenseMatrix Class, looking for easier matrix operations
		train = new DenseMatrix(IStrain.getNumInstances(),Attributes.getNumAttributes());
		maxMink = 0;
		for(int i=0;i<IStrain.getNumInstances();i++){
			inst = IStrain.getInstance(i);
			in = out = 0;
			if(!inst.existsAnyMissingValue())
				maxMink++;
			for(int j=0;j<Attributes.getNumAttributes();j++){
				a = Attributes.getAttribute(j);
				if(a.getDirectionAttribute() == Attribute.INPUT){
					train.set(i, j, inst.getAllInputValues()[in]);
					in++;
				}
				else{
					train.set(i, j, inst.getAllOutputValues()[out]);
					out++;
				}
			}
		}
		System.out.println("\nProcessing the train partition");
		if(mink > maxMink)
			mink = maxMink;
		E = impute_llsq_l2_blind(train,IStrain);
		
		//convert the estimated matrix to a String matrix ready to be printed
		X = new String[IStrain.getNumInstances()][Attributes.getNumAttributes()];//matrix with transformed data
		
		data2string(E,X,IStrain);
		write_results(output_train_name,X,IStrain);
		System.out.println("Done");
		//*************************************************************************
		//proceed with the test partition
		//*************************************************************************
		test = new DenseMatrix(IStest.getNumInstances(),Attributes.getNumAttributes());
		maxMink = 0;
		for(int i=0;i<IStest.getNumInstances();i++){
			inst = IStest.getInstance(i);
			in = out = 0;
			if(!inst.existsAnyMissingValue())
				maxMink++;
			for(int j=0;j<Attributes.getNumAttributes();j++){
				a = Attributes.getAttribute(j);
				if(a.getDirectionAttribute() == Attribute.INPUT){
					test.set(i, j, inst.getAllInputValues()[in]);
					in++;
				}
				else{
					test.set(i, j, inst.getAllOutputValues()[out]);
					out++;
				}
			}
		}
		System.out.println("\nProcessing the test partition");
		if(mink > maxMink)
			mink = maxMink;
		E = impute_llsq_l2_blind(test,IStest);
		
		//convert the estimated matrix to a String matrix ready to be printed
		X = new String[IStest.getNumInstances()][Attributes.getNumAttributes()];//matrix with transformed data
		
		data2string(E,X,IStest);
		write_results(output_test_name,X,IStest);
		System.out.println("Done");
	}
	
	/**
	 * Function that applies the Local Least Squares Imputation to a given array
	 * @param train The Matrix with the data
	 * @param IS The original Instance set, used for reference (attributes, etc.)
	 * @return A new allocated DenseMatrix of same size than 'train' which has the missing values imputed
	 */
	public DenseMatrix impute_llsq_l2_blind(DenseMatrix train,InstanceSet IS){
		int m,n,total,minexp,fid,m_gene_include,k_max,len_miss,len_nomiss,in,out;
		Attribute a;
		ArrayList<Integer> gene_include = new ArrayList<Integer>();
		ArrayList<Integer> missidxj = new ArrayList<Integer>();
		ArrayList<Integer> nomissidxj = new ArrayList<Integer>();
		ArrayList<Double> guess = new ArrayList<Double>();
		Instance inst;
		DenseMatrix tmp,E;
		DenseVector X=null,estimate=null;
		
		
		m = train.numRows();
		n = train.numColumns();
		total = m*n;
		
		//the number of minimal experiments for estimating missing values by llsq
		minexp = 2;
		//minexp=n*0.3;
		
		fid = 1;
		
		//initial guess
		f_rowaverage = false; // turn-off default rowaverage
		//f_rowaverage = true; % default rowaverage
		
		//if no complete example is available, force row average :(
		if( f_rowaverage || mink==0) { 
			System.out.println("consider all instances/genes after imputing missing values by row-average.");
			E = impute_rowavg(train,minexp,IS); 
			//[E]=impute_knn(miss_matrix);
			for(int i=0;i<m;i++){
				gene_include.add(i);
			}
			f_rowaverage=true;
			mink = Math.min(initialMink, E.numRows()-1);
		}else{
			System.out.println("exclude instances/genes that have missing values for accurate imputation.");
			E = train.copy();
			for(int i=0;i<m;i++){
				if(!IS.getInstance(i).existsAnyMissingValue())
					gene_include.add(i);
			}
			//set minexp to 0 since we do not perform rowaverage
			minexp=0; 
			f_rowaverage=false;
		}
		
		m_gene_include = gene_include.size();
		k_max = m_gene_include;
		
		
		System.out.println("\n");
		System.out.println("----------------------------------------------");
		System.out.println("LLSimpute/L2/ITER");
		System.out.println("----------------------------------------------");
		System.out.println("\n");
		
		System.out.println("miss_matrix("+m+","+n+") total: "+total+" minexp: "+minexp+" f_rowaverage: "+f_rowaverage+"\n");
		
		System.out.print("Estimating missing values...");  
		
		for(int i=0;i<m;i++){
			missidxj.clear();
			nomissidxj.clear();
			inst = IS.getInstance(i);
			
			//get the attributes with missing values, and without them
			in = out = 0;
			for(int j=0;j<Attributes.getNumAttributes();j++){
				a = Attributes.getAttribute(j);
				if(a.getDirectionAttribute() == Attribute.INPUT){
					if(inst.getInputMissingValues()[in])
						missidxj.add(j);
					else
						nomissidxj.add(j);
					in++;
				}
				else{
					if( inst.getOutputMissingValues()[out])
						missidxj.add(j);
					else
						nomissidxj.add(j);
					out++;
				}
			}
				
			len_miss = missidxj.size();
			len_nomiss = nomissidxj.size();
			if ( ((len_nomiss < minexp) || (len_nomiss < 2)) && (f_rowaverage)){
				System.out.println(i+"th gene: skip due to nomiss_exp("+len_nomiss+")<"+minexp+" or < 2");
			}else if (len_miss > 0){  

				//if fig==1
				//fprintf('%dth: gene apply llsq --- %d missing\n',  i, len_miss);
				//end
				similargene(i,missidxj,nomissidxj,m,n,train,E,gene_include,m_gene_include);
				//answer=[answer; matrix(i,missidxj)'];         

				// for mink
				//Apart=A(1:mink,:);
				tmp = new DenseMatrix(mink,A.numColumns());
				for(int j=0;j<mink;j++){
					for(int k=0;k<A.numColumns();k++){
						tmp.set(j, k, A.get(j, k));
					}
				}
				//Apart=Apart';
				Apart = new DenseMatrix(tmp.numColumns(),tmp.numRows());
				tmp.transpose(Apart);
				
				//Bpart=B(1:mink,:);
				Bpart = new DenseMatrix(mink,B.numColumns());
				for(int j=0;j<mink;j++){
					for(int k=0;k<B.numColumns();k++){
						Bpart.set(j, k, B.get(j, k));
					}
				}
				//linear combination of experiments
				//X = pinv(Apart)*w';
				tmp = pinv(Apart);
				X = new DenseVector(tmp.numRows());
				tmp.mult(w,X);
				
				//guess = [guess; Bpart'*X];
				estimate = new DenseVector(Bpart.numColumns());
				tmp = new DenseMatrix(Bpart.numColumns(),Bpart.numRows());
				Bpart.transpose(tmp);
				tmp.mult(X, estimate);
				for(int j=0;j<estimate.size();j++)
					guess.add(estimate.get(j));

			}//if       

		}//i
		
		//store estimated values in the final matrix which will be printed
		int s = 0;
		for(int i=0;i<m;i++){
			missidxj.clear();
			nomissidxj.clear();
			inst = IS.getInstance(i);
			
			//get the attributes with missing values, and without them
			in = out = 0;
			for(int j=0;j<Attributes.getNumAttributes();j++){
				a = Attributes.getAttribute(j);
				if(a.getDirectionAttribute() == Attribute.INPUT){
					if(inst.getInputMissingValues()[in])
						missidxj.add(j);
					else
						nomissidxj.add(j);
					in++;
				}
				else{
					if( inst.getOutputMissingValues()[out])
						missidxj.add(j);
					else
						nomissidxj.add(j);
					out++;
				}
			}
				
			len_miss = missidxj.size();
			len_nomiss = nomissidxj.size();
			if ( ((len_nomiss < minexp) || (len_nomiss < 2)) && (f_rowaverage)){
				//skip
			}else if (len_miss > 0){
				for(int j=0;j<missidxj.size();j++){
					E.set(i, missidxj.get(j), guess.get(s));
					s++;
				}
			}
		}
		
		return E;
	}
	
	/**
	 * Computes the most similar (nearest) instances to a given one. The result are stoed
	 * in matrix A,B and vector w, which are fields of the current object.
	 * @param i The number of the given instance in the E matrix (i.e. the row number)
	 * @param missidxj Indices of the missing attributes
	 * @param nomissidxj Indices of the non-missing (complete) attributes
	 * @param m Rows of the E matrix
	 * @param n Columns of the E matrix
	 * @param miss_matrix Original matrix which has no missing value imputed, same size of E
	 * @param E Working matrix which has the previous found missing values estimated
	 * @param gene_include The indices of the genes (rows or instances) which we will consider as neighbours to instance i
	 * @param m_gene_include Number of neighbours to be considered (size of gene_include)
	 */
	public void  similargene(int i,ArrayList<Integer> missidxj,ArrayList<Integer> nomissidxj,int m,
			int n, DenseMatrix miss_matrix,DenseMatrix E,ArrayList<Integer> gene_include,int m_gene_include){
		int mm1,mm2,pos;
		DenseMatrix BB1,tmp2,tmp3,tmp4;
		DenseVector AA1,BB2,tmp;
		double AA2,distance;
		ArrayList<IndexValuePair> sorted;
		int gene[];

		// L2-norm distance calculation
		mm1=1;
		mm2=m_gene_include;
		
		AA1 = new DenseVector(nomissidxj.size());
		for(int j=0;j<nomissidxj.size();j++){
			AA1.set(j,E.get(i,nomissidxj.get(j)));
		}
		
		BB1 = new DenseMatrix(gene_include.size(),nomissidxj.size());
		for(int j=0;j<gene_include.size();j++){
			for(int k=0;k<nomissidxj.size();k++){
				BB1.set(j, k, E.get(gene_include.get(j), nomissidxj.get(k)));
			}
		}
		tmp = new DenseVector(AA1);
		for(int j=0;j<tmp.size();j++)
			tmp.set(j, tmp.get(j)*tmp.get(j));
		AA2 = sum(tmp); 
		
		tmp2 = new DenseMatrix(BB1);
		for(int j=0;j<tmp2.numRows();j++)
			for(int k=0;k<tmp2.numColumns();k++)
				tmp2.set(j, k, Math.pow(tmp2.get(j, k),2));
		BB2 = sumbyRows(tmp2);
		
		//distance=repmat(AA2,1,mm2)+repmat(BB2',mm1,1)-2*AA1*BB1';
		//let's begin with the operations
		tmp2 = new DenseMatrix(1,1);
		tmp2.set(0, 0, AA2);
		//tmp2=repmat(AA2,1,mm2)
		tmp2 = repmat(tmp2,1,mm2);
		//tmp4 = BB2', since BB2 is a column vector, tmp4 will be a row vector
		tmp4 = new DenseMatrix(1,BB2.size());
		for(int j=0;j<BB2.size();j++)
			tmp4.set(0,j,BB2.get(j));;
		tmp3 = repmat(tmp4,mm1,1);
		
		//tmp2 = repmat(AA2,1,mm2)+repmat(BB2',mm1,1)
		tmp2.add(tmp3);
		
		tmp3 = new DenseMatrix(1,AA1.size());
		for(int j=0;j<AA1.size();j++)
			tmp3.set(0, j, AA1.get(j));
		//tmp4 = -2*AA1*BB1'
		tmp4 = new DenseMatrix(tmp3.numRows(),BB1.numRows());
		tmp3.transBmult(-2.0,BB1, tmp4);
		//tmp2 = distance!
		tmp2.add(tmp4);
		
		//sort the distances
		sorted = new ArrayList<IndexValuePair>();
		for(int j=0;j<tmp2.numColumns();j++)
			sorted.add(new IndexValuePair(tmp2.get(0, j),j));
		Collections.sort(sorted);
		// gene number
		if(f_rowaverage)
			gene = new int[sorted.size()-1];
		else
			gene = new int[sorted.size()];
		for(int j=0,k=0;j<sorted.size();j++){
			pos = sorted.get(j).index;
			if(gene_include.get(pos)!=i){
				gene[k] = gene_include.get(pos);
				k++;
			}
		}
		
		//A=E(gene,nomissidxj);
		A = new DenseMatrix(gene.length,nomissidxj.size());
		for(int j=0;j<gene.length;j++){
			for(int k=0;k<nomissidxj.size();k++){
				A.set(j, k, E.get(gene[j], nomissidxj.get(k)));
			}
		}
		
		//B=E(gene,missidxj);
		B = new DenseMatrix(gene.length,missidxj.size());
		for(int j=0;j<gene.length;j++){
			for(int k=0;k<missidxj.size();k++){
				B.set(j, k, E.get((int)gene[j], missidxj.get(k)));
			}
		}
		
		//w=miss_matrix(i,nomissidxj);
		w = new DenseVector(nomissidxj.size());
		for(int j=0;j<nomissidxj.size();j++){
			w.set(j, miss_matrix.get(i, nomissidxj.get(j)));
		}
	}
	
	/**
	 * Perform the row-average of given matrix
	 * @param miss_matrix The original matrix with all missing values
	 * @param minexp The minimum number of non-missing values to compute the row-average
	 * @param IS The reference InstanceSet
	 *
	 */
	public DenseMatrix impute_rowavg(DenseMatrix miss_matrix,int minexp,InstanceSet IS){
		int exp,nomissidxj,in,out;
		int m = miss_matrix.numRows();
		int n = miss_matrix.numColumns();
		double avg;
		Attribute a;
		int gene0,gene1;
		Instance inst;
		DenseMatrix E = miss_matrix.copy();
		
		System.out.println("Generating row-averaged E...");

		gene0=0;
		gene1=0;
		for (int i=0;i<m;i++){
			avg = 0;
			exp = 0;
			inst = IS.getInstance(i);
			in = out = 0;
			for(int j=0;j<n;j++){
				a = Attributes.getAttribute(j);
				if(a.getDirectionAttribute()==Attribute.INPUT){
					if(!inst.getInputMissingValues(in)){
						avg += miss_matrix.get(i, j);
						exp++;
					}
					in++;
				}
				if(a.getDirectionAttribute()==Attribute.OUTPUT){ 
					if(!inst.getOutputMissingValues(out)){
						avg += miss_matrix.get(i, j);
						exp++;
					}
					out++;
				}
			}
			if(exp == n)
				gene0++;
			else if(exp < minexp)
				gene1++;
			avg = (double)avg / exp;
			in = out = 0;
			for(int j=0;j<n;j++){
				a = Attributes.getAttribute(j);
				if(a.getDirectionAttribute()==Attribute.INPUT){
					if(inst.getInputMissingValues(in)){
						E.set(i, j, avg);
					}
					in++;
				}
				if(a.getDirectionAttribute()==Attribute.OUTPUT){
					if(inst.getOutputMissingValues(out)){
						E.set(i, j, avg);
					}
					out++;
				}
			}
		}
		
		System.out.println("the number of genes that have no non-missing entries: "+gene0);
		System.out.println("the number of genes that have less than "+ minexp +" non-missing entries: "+gene1);
		
		return E;
	}
	
	/**
	 * Computes the pseudoinverse of matrix A -> pinv(A) = V * pinv(S) * U'
	 * That is, Moore-Penrose pseudoinverse of a matrix
	 * If A is square and not singular, then pinv(A) is an expensive way to compute inv(A)
	 * @param A The matrix from we compute the pseudoinverse
	 * @return The pseudoinverse of matrix A
	 */
	public static DenseMatrix pinv(DenseMatrix A) {
		DenseMatrix inv,tmp,tmp2;
		DenseMatrix pinvSingVal;
		double tol;
		double sing[];
//		double data[] = new double[]{64, 2,3,61,60, 6, 9,55,54,12,13,51,17,47,46,20,21,43,
//		40,26,27,37,36,30,32,34,35,29,28,38,41,23,22,44,45,19,49,15,14,52,53,11, 8,58,59, 5, 4,62};
//		double square[] = new double[]{1,2,3, 5,8,7, 8,1,4};
//		DenseMatrix A = new DenseMatrix(8,6);
		DenseMatrix B = new DenseMatrix(3,3);
//		for(int i=0,k=0;i<8;i++){
//			for(int j=0;j<6;j++,k++){
//				A.set(i, j, data[k]);
//			}
//		}
		
//		for(int i=0,k=0;i<3;i++){
//			for(int j=0;j<3;j++,k++){
//				B.set(i, j, square[k]);
//			}
//		}
//		inv = inv(B);
		
//		DenseMatrix id = new DenseMatrix(8,8);
//		id.zero();
//		for(int i=0;i<8;i++)
//			id.set(i, i, 1);
		
		SVD svd;

		try {
			//the pseudoinverse can be computed as:
			// pinv(A) = V * pinv(S) * U'
			//where U,V and S are obtained from the Singular Value Decomposition of A
			svd = SVD.factorize(A);
			
			//compute the pseudoinverse of the Singular values
			//i.e. the reciprocal of those singular values (the inverse)
			//since the singular values are in the diagonal of the matrix
			sing = svd.getS();
			pinvSingVal = new DenseMatrix(svd.getVt().numRows(), svd.getU().numColumns());
			pinvSingVal.zero();
			//we also state the minimum threshold for the singular values
			tol = Math.max(A.numRows(), A.numColumns());
			tol *= sing[0] * eps;
			//we take the pinv of Singular values vector.
			//since it is a diagonal matrix, the pseudoinverse is the inverse of the 
			//diagonal elemnts, in a matrix with transposed dimensions
			for(int i=0;i<sing.length;i++){
				//if the singular value is too small, the reciprocal will be enormous!
				//for this reason, we only use the values of at least 'tol'
				if(sing[i] > tol) 
					pinvSingVal.set(i, i, 1.0/sing[i]);
			}
			
			//compute tmp = V * pinv(S)
			tmp = new DenseMatrix(svd.getVt().numColumns(),pinvSingVal.numColumns());
			svd.getVt().transAmult(pinvSingVal, tmp);
			
			//At last, compute the pseudoinverse
			tmp2 = new DenseMatrix(svd.getU().numRows(),svd.getU().numColumns());
			for(int i=0;i<tmp2.numRows();i++){
				for(int j=0;j<tmp2.numColumns();j++){
					tmp2.set(i, j, svd.getU().get(i, j));
				}
			}
			inv = new DenseMatrix(A.numColumns(),A.numRows());
			tmp.transBmult(tmp2, inv);
				
//			for(int i=0;i<inv.numRows();i++){
//				for(int j=0;j<inv.numColumns();j++){
//					System.out.print(inv.get(i, j)+" ");
//				}
//				System.out.println();
//			}
			
			return inv;
		} catch (NotConvergedException e) {
			System.err.println("Error: The SVD did not converge :(");
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
	
	/**
	 * Computes the inverse of a square non-singular Matrix
	 * @param A The Matrix from which we will compute the inverse
	 * @return A new allocated matrix which contains the inverse of A
	 */
	public static DenseMatrix inv(DenseMatrix A){
		DenseMatrix identity = new DenseMatrix(A.numRows(),A.numRows());
		DenseLU lu;
		
		//compute the LU decomposition of A
		lu = DenseLU.factorize(A);
		
		//create the Identity matrix which would be the result of 
		//A·A'
		identity.zero();
		for(int i=0;i<identity.numRows();i++){
			identity.set(i, i, 1);
		}
		//solve the inverse solving the system (note that identity is overwritten!)
		lu.solve(identity);
		
		return identity;
		
	}
	
	/**
	 * Compute the sum of all members of vector v
	 * @param v The reference vector
	 * @return Summatory of all elements of v
	 */
	public double sum(DenseVector v){
		double total = 0;
		for(int i=0;i<v.size();i++)
			total += v.get(i);
		return total;
	}
	
	/**
	 * From a given matrix mat, it performs the summatory by rows of such matrix
	 * @param mat The reference matrix
	 * @return A new allocated array with the sum of each row's elements
	 */
	public DenseVector sumbyRows(DenseMatrix mat){
		DenseVector v = new DenseVector(mat.numRows());
		for(int i=0;i<mat.numRows();i++){
			v.set(i, 0);
			for(int j=0;j<mat.numColumns();j++){
				v.add(i, mat.get(i, j));
			}
		}
		return v;
	}
	
	/**
	 * Replicate and tile an array.
	 * B = repmat(A,m,n) creates a large matrix B consisting of an m-by-n tiling of copies of A. The size of B is [size(A,1)*m, (size(A,2)*n]. 
	 * The statement repmat(A,n) creates an n-by-n tiling.
	 * @param mat The original matrix
	 * @param m Number of rowwise replications
	 * @param n Number of columnwise replications
	 * @return A new allocated matrix with the tiling of matrix mat
	 */
	public DenseMatrix repmat(DenseMatrix mat,int m, int n){
		int totalRow = m * mat.numRows();
		int totalCol = n * mat.numColumns();
		DenseMatrix newMat = new DenseMatrix(totalRow,totalCol);
		
		for(int i=0;i<totalRow;i++){
			for(int j=0;j<totalCol;j++){
				newMat.set(i, j, mat.get(i%mat.numRows(), j%mat.numColumns()));
			}
		}
		
		return newMat;
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
	
	/** Write data matrix X to disk, in KEEL format
	 * @param output The file to which we print
	 * @param X The 2D array with the values of the attributes parsed to a string
	 * @param IS The reference InstanceSet
	 */
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

