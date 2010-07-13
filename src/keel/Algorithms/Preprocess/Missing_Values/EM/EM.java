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

package keel.Algorithms.Preprocess.Missing_Values.EM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

import org.core.Fichero;
import org.netlib.lapack.Dgetri;

import keel.Algorithms.Preprocess.Missing_Values.EM.util.UnivariateMinimum;
import keel.Dataset.*;
import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.*;
import keel.Algorithms.Preprocess.Missing_Values.EM.util.*;

/*
    Missing values are imputed with a regularized expectation
    maximization (EM) algorithm. In an iteration of the EM algorithm,
    given estimates of the mean and of the covariance matrix are
    revised in three steps. First, for each record X(i,:) with
    missing values, the regression parameters of the variables with
    missing values on the variables with available values are
    computed from the estimates of the mean and of the covariance
    matrix. Second, the missing values in a record X(i,:) are filled
    in with their conditional expectation values given the available
    values and the estimates of the mean and of the covariance
    matrix, the conditional expectation values being the product of
    the available values and the estimated regression
    coefficients. Third, the mean and the covariance matrix are
    re-estimated, the mean as the sample mean of the completed
    dataset and the covariance matrix as the sum of the sample
    covariance matrix of the completed dataset and an estimate of the
    conditional covariance matrix of the imputation error.

    In the regularized EM algorithm, the parameters of the regression
    models are estimated by a regularized regression method. By
    default, the parameters of the regression models are estimated by
    an individual ridge regression for each missing value in a
    record, with one regularization parameter (ridge parameter) per
    missing value.  Optionally, the parameters of the regression
    models can be estimated by a multiple ridge regression for each
    record with missing values, with one regularization parameter per
    record with missing values. The regularization parameters for the
    ridge regressions are selected as the minimizers of the
    generalized cross-validation (GCV) function. As another option,
    the parameters of the regression models can be estimated by
    truncated total least squares. The truncation parameter, a
    discrete regularization parameter, is fixed and must be given as
    an input argument. The regularized EM algorithm with truncated
    total least squares is faster than the regularized EM algorithm
    with with ridge regression, requiring only one eigendecomposition
    per iteration instead of one eigendecomposition per record and
    iteration. But an adaptive choice of truncation parameter has not
    been implemented for truncated total least squares. So the
    truncated total least squares regressions can be used to compute
    initial values for EM iterations with ridge regressions, in which
    the regularization parameter is chosen adaptively.

    As default initial condition for the imputation algorithm, the
    mean of the data is computed from the available values, mean
    values are filled in for missing values, and a covariance matrix
    is estimated as the sample covariance matrix of the completed
    dataset with mean values substituted for missing
    values. Optionally, initial estimates for the missing values and
    for the covariance matrix estimate can be given as input
    arguments.

    The OPTIONS structure specifies parameters in the algorithm:

     Field name         Parameter                                  Default

     OPTIONS.regress    Regression procedure to be used:           'mridge'
                        'mridge': multiple ridge regression
                        'iridge': individual ridge regressions
                        'ttls':   truncated total least squares
                                  regression

     OPTIONS.stagtol    Stagnation tolerance: quit when            5e-3
                        consecutive iterates of the missing
                        values are so close that
                          norm( Xmis(it)-Xmis(it-1) )
                             <= stagtol * norm( Xmis(it-1) )

     OPTIONS.maxit      Maximum number of EM iterations.           30

     OPTIONS.inflation  Inflation factor for the residual          1
                        covariance matrix. Because of the
                        regularization, the residual covariance
                        matrix underestimates the conditional
                        covariance matrix of the imputation
                        error. The inflation factor is to correct
                        this underestimation. The update of the
                        covariance matrix estimate is computed
                        with residual covariance matrices
                        inflated by the factor OPTIONS.inflation,
                        and the estimates of the imputation error
                        are inflated by the same factor.

     OPTIONS.disp       Diagnostic output of algorithm. Set to     1
                        zero for no diagnostic output.

     OPTIONS.regpar     Regularization parameter.                  not set
                        For ridge regression, set regpar to
                        sqrt(eps) for mild regularization; leave
                        regpar unset for GCV selection of
                        regularization parameters.
                        For TTLS regression, regpar must be set
                        and is a fixed truncation parameter.

     OPTIONS.relvar_res Minimum relative variance of residuals.    5e-2
                        From the parameter OPTIONS.relvar_res, a
                        lower bound for the regularization
                        parameter is constructed, in order to
                        prevent GCV from erroneously choosing
                        too small a regularization parameter.

     OPTIONS.minvarfrac Minimum fraction of total variation in     0
                        standardized variables that must be
                        retained in the regularization.
                        From the parameter OPTIONS.minvarfrac,
                        an approximate upper bound for the
                        regularization parameter is constructed.
                        The default value OPTIONS.minvarfrac = 0
                        essentially corresponds to no upper bound
                        for the regularization parameter.

     OPTIONS.Xmis0      Initial imputed values. Xmis0 is a         not set
                        (possibly sparse) matrix of the same
                        size as X with initial guesses in place
                        of the NaNs in X.

     OPTIONS.C0         Initial estimate of covariance matrix.     not set
                        If no initial covariance matrix C0 is
                        given but initial estimates Xmis0 of the
                        missing values are given, the sample
                        covariance matrix of the dataset
                        completed with initial imputed values is
                        taken as an initial estimate of the
                        covariance matrix.

     OPTIONS.Xcmp       Display the weighted rms difference        not set
                        between the imputed values and the
                        values given in Xcmp, a matrix of the
                        same size as X but without missing
                        values. By default, REGEM displays
                        the rms difference between the imputed
                        values at consecutive iterations. The
                        option of displaying the difference
                        between the imputed values and reference
                        values exists for testing purposes.

     OPTIONS.neigs      Number of eigenvalue-eigenvector pairs     not set
                        to be computed for TTLS regression.
                        By default, all nonzero eigenvalues and
                        corresponding eigenvectors are computed.
                        By computing fewer (neigs) eigenvectors,
                        the computations can be accelerated, but
                        the residual covariance matrices become
                        inaccurate. Consequently, the residual
                        covariance matrices underestimate the
                        imputation error conditional covariance
                        matrices more and more as neigs is
                        decreased.

    References:
    [1] T. Schneider, 2001: Analysis of incomplete climate data:
        Estimation of mean values and covariance matrices and
        imputation of missing values. Journal of Climate, 14,
        853--871.
    [2] R. J. A. Little and D. B. Rubin, 1987: Statistical
        Analysis with Missing Data. Wiley Series in Probability
        and Mathematical Statistics. (For EM algorithm.)
    [3] P. C. Hansen, 1997: Rank-Deficient and Discrete Ill-Posed
        Problems: Numerical Aspects of Linear Inversion. SIAM
        Monographs on Mathematical Modeling and Computation.
        (For regularization techniques, including the selection of
        regularization parameters.)
 */
//====================================================
//Adapted to Java for KEEL by Julian Luengo
//julianlm@decsai.ugr.es
//====================================================	
/**
 * This class implements the Regularized Expectation-Maximization imputation for 
 * Missing Values
 * @author Julian Luengo Martin
 */
public class EM{
	final public static int TTLS = 1;
	final public static int MRIDGE = 2;
	final public static int IRIDGE = 3;

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
	
	//variables
	double peff = 0;
	double h = 0;
	DenseMatrix B,S;
	DenseVector hv,peffv;
	int kmisr[][],kavlr[][],lastIterations;
	double previousMean[] = null;
	
	String input_train_name = new String();

	String input_test_name = new String();

	String output_train_name = new String();

	String output_test_name = new String();

	String temp = new String();

	String data_out = new String("");
	
	/**
	 * Default constructor.
	 * No initialization is made
	 */
	public EM(){
		super();
	}
	
	/**
	 * Parametrized constructor
	 * @param _maxit Maximum number of iterations to converge
	 * @param _stagtol Stagnation tolerance
	 * @param _optRegression Regression procedure to be used
	 * @param _neigs Number of eigenvalue-eigenvector pairs not set to be computed for TTLS regression
	 * @param _regpar regularization parameter
	 * @param _minvarfrac Minimum fraction of total variation in standardized variables that must be retained in the regularization
	 * @param _inflation Inflation factor for the residual covariance matrix
	 * @param _trunc Fixed truncation parameter
	 * @param _useRegPar Wheter to use the regularization parameter or not
	 */
	public EM(int _maxit,double _stagtol,int _optRegression,int _neigs,double _regpar,
			double _minvarfrac, double _inflation, int _trunc,boolean _useRegPar){
		maxit = _maxit;
		stagtol = _stagtol;
		optRegression = _optRegression;
		neigs = _neigs;
		regpar = _regpar;
		minvarfrac = _minvarfrac;
		inflation = _inflation;
		trunc = _trunc;
		useRegPar = _useRegPar;
	}
	
	/**
	 * Constructor for KEEL parameter file
	 * @param fileParam The KEEL-formated parameter file
	 */
	public EM(String fileParam) {
		config_read(fileParam);
		
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
	 * Sets a new limit to the stagnation tolerance
	 * @param newLimit the new limit
	 */
	public void setStagtol(double newLimit){
		stagtol = newLimit;
	}
	
	/**
	 * Set the maximum nomber of iterations
	 * @param newLimit the new limit
	 */
	public void setMaxIterations(int newLimit){
		maxit = newLimit;
	}
	
	/**
	 * Gets the last number of iterations performed
	 * @return the last number of iterations
	 */
	public int getLastIterations(){
		return lastIterations;
	}
	
	/**
	 * Gets the maximum number of allowed iterations
	 * @return the limit for the iterations
	 */
	public int getMaxIterations(){
		return maxit;
	}
	
	/**
	 * Gets the stagnation tolerance
	 * @return the current stagnation tolerance
	 */
	public double getStagnationTolerance(){
		return stagtol;
	}
	
	/**
	 * MATLAB - Replicate and tile an array
	 * @param mat the original matrix
	 * @param n the multiplier for the rows
	 * @param m the multiplier for the columns
	 * @return a new allocated matrix of mat.rows*n, mat.columns*m
	 */
	public DenseMatrix repmat(DenseMatrix mat,int n, int m){
		int totalRow = n * mat.numRows();
		int totalCol = m * mat.numColumns();
		DenseMatrix newMat = new DenseMatrix(totalRow,totalCol);
		
		for(int i=0;i<totalRow;i++){
			for(int j=0;j<totalCol;j++){
				newMat.set(i, j, mat.get(i%mat.numRows(), j%mat.numColumns()));
			}
		}
		
		return newMat;
	}
	
	/**
	 * Creates a new matrix filled with ones
	 * @param m the number of rows
	 * @param n the number of columns
	 * @return a new allocated matrix of m rows, n columns filled with ones
	 */
	public DenseMatrix ones(int m,int n){
		DenseMatrix mat = new DenseMatrix(m,n);
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				mat.set(i, j, 1);
		return mat;
	}
	
	/**
	 * Sum of the diagonal elements
	 * @param a the considered matrix
	 * @return the sum of the elements in the diagonal
	 */
	public double trace(DenseMatrix a){
		double total = 0;
		for(int i=0;i<Math.min(a.numColumns(), a.numRows());i++)
			total += a.get(i, i);
		return total;
	}
	
	/**
	 * Centers data by subtraction of the mean
	 * @param m the considered matrix
	 * @param dataSource the InstanceSet with the examples (for missing values reference)
	 * @return the array with the computed means from 'm' matrix for each column
	 */
	public double[] centerDataMatrix(DenseMatrix m,InstanceSet dataSource){
		double mean[] = new double [m.numColumns()];
		int counter[] = new int [m.numColumns()];
		Attribute a;
		int in,out;
		
		for(int i=0;i<m.numRows();i++){
			in = out = 0;
			for(int j=0;j<m.numColumns();j++){
				a = Attributes.getAttribute(j);
				if(a.getDirectionAttribute() == Attribute.INPUT){
					if(!dataSource.getInstance(i).getInputMissingValues()[in]){
						mean[j] += m.get(i, j);
						counter[j]++;
					}
					in++;
				}
				else{
					if(!dataSource.getInstance(i).getOutputMissingValues()[out]){
						mean[j] += m.get(i, j);
						counter[j]++;
					}
					out++;
				}
			}
		}
		
		for(int j=0;j<m.numColumns();j++){
			if(counter[j]!=0)
				mean[j] /= (double)counter[j];
			else if(previousMean!=null && previousMean[j]!=0) //if no available value, use train mean for this attribute
				mean[j] = previousMean[j];
			else{
				a = Attributes.getAttribute(j);
				if(a.getType() == Attribute.NOMINAL)
					mean[j] = Math.round(a.getNumNominalValues()/2);
				else
					mean[j] = (a.getMaxAttribute() - a.getMinAttribute())/2.0;
			}
		}
		
		for(int i=0;i<m.numRows();i++){
			in = out = 0;
			for(int j=0;j<m.numColumns();j++){
				a = Attributes.getAttribute(j);
				if(a.getDirectionAttribute() == Attribute.INPUT){
					if(!dataSource.getInstance(i).getInputMissingValues()[in]){
						m.set(i, j, m.get(i, j)-mean[j]);
						in++;
					}
				}
				else{
					if(!dataSource.getInstance(i).getInputMissingValues()[out]){
						m.set(i, j, m.get(i, j)-mean[j]);
						out++;
					}
				}
			}
		}
		return mean;
		
	}
	
	/**
	 * Returns the diagonal of the matrix
	 * @param m the reference matrix
	 * @return a new allocated vector with the diagonal elements
	 */
	public DenseVector diag(DenseMatrix m){
		DenseVector diagonal = new DenseVector(m.numColumns());
		
		for(int i=0;i<m.numColumns();i++){
			diagonal.set(i, m.get(i, i));
		}
		return diagonal;
	}
	
	/**
	 * Finds the minimum element of the vector
	 * @param v the reference vector
	 * @return the minimum element value
	 */
	public double min(DenseVector v){
		int min = 0;
		for(int i=1;i<v.size();i++){
			if(v.get(i)<v.get(min))
				min = i;
		}
		return v.get(min);
	}
	
	/**
	 * Finds the minimum element of the vector, and returns it
	 * @param v the reference vector
	 * @param posMin the index of the minimum elemnt (or indices if many) are stored here
	 * @return the minimum value
	 */
	public double min(DenseVector v, ArrayList<Integer> posMin){
		int min = 0;
		ArrayList<Integer> index = new ArrayList<Integer>();
		for(int i=1;i<v.size();i++){
			if(v.get(i)<v.get(min)){
				min = i;
				index.clear();
				index.add(i);
			}
			else if(v.get(i)==v.get(min))
				index.add(i);
		}
		if(min == 0)
			index.add(0);
//		posMin = new ArrayList<Integer>(index.size());
		for(int i=0;i<index.size();i++)
			posMin.add(index.get(i).intValue());
		return v.get(min);
	}
	
	/**
	 * Finds the maximum element of a vector
	 * @param v the reference vector
	 * @return the maximum value of v
	 */
	public double max(DenseVector v){
		int max = 0;
		for(int i=1;i<v.size();i++){
			if(v.get(i)>v.get(max))
				max = i;
		}
		return v.get(max);
	}
	
	/**
	 * Sum of the elements of the vector
	 * @param v the reference vector
	 * @return the sum of the elements
	 */
	public double sum(DenseVector v){
		double total = 0;
		for(int i=0;i<v.size();i++)
			total += v.get(i);
		return total;
	}
	
	/**
	 * Cummulative sum of the elements of the vector.
	 * Each position contais the sum till such position of the reference vector.
	 * @param v the reference vector
	 * @return a new allocated vector with the cummulative sum
	 */
	public DenseVector cumsum(DenseVector v){
		DenseVector cumm = new DenseVector(v);
		double sum = 0;
		
		for(int i=0;i<cumm.size();i++){
			cumm.set(i, cumm.get(i)+sum);
			sum = cumm.get(i);
		}
		return cumm;
	}
	
	/**
	 * Performs the sum by the rows of the matrix
	 * @param mat the reference matrix
	 * @return a new vector with the sum of the elements in each row
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
	 * Square root of the elements of the vector
	 * @param v the reference vector
	 * @return a new vector with the square root of each element of v
	 */
	public DenseVector sqrt(DenseVector v){
		DenseVector d = new DenseVector(v);
		for(int i=0;i<d.size();i++)
			d.set(i, Math.sqrt(v.get(i)));
		return d;
	}
	
	/**
	 * Gets the matrix with the positions of missing values
	 * @return a matrix, in which each row there are stored the indices of the missing values of that record
	 */
	public int[][] getKmisr(){
		return kmisr;
	}
	
	/**
	 * Gets the matrix with the positions of non-missing values
	 * @return a matrix, in which each row there are stored the indices of the present values of that record
	 */
	public int[][] getKavlr(){
		return kavlr;
	}
	
	/**
	 * Finds positive eigenvalues and corresponding eigenvectors.
	 * It calls the function EIGS to compute the first rmax
	 * eigenpairs of A by Arnoldi iterations
	 * @param A the reference matrix
	 * @param rmax upper bound on the number of positive eigenvalues of A
	 * @return The eigenvectors and eigenvalues of A
	 */
	public EV peigs(DenseMatrix A, int rmax){
		EV values = null;
		int m,n,r;
		DenseMatrix V;
		double d[] = null;
		double posEigen[] = null;
		double d_min;
		EVpair p[];
		EVD eigs;

		m = A.numRows();
		n = A.numColumns();
		if(rmax > Math.min(m, n))
			rmax = Math.min(m, n);
		
		//get first rmax eigenvectors of A
		eigs = new EVD(Math.min(m, n));

		try {
			eigs = EVD.factorize(A);
			d = eigs.getRealEigenvalues();
		} catch (NotConvergedException e) {
			System.err.println("Error: Matrix Eigen Value Descomposition didn't converge");
			e.printStackTrace();
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
		
		d_min = p[0].eigenValue * Math.max(m,n) * eps;
		r = 0;
		for(int i=0;i<d.length;i++)
			if(p[i].eigenValue>d_min)
				r++;
		
		r = Math.min(r,rmax);
		posEigen = new double[r];
		V = new DenseMatrix(Math.min(m, n),r);
		for(int i=0;i<r;i++){
			posEigen[i] = p[i].eigenValue;
			for(int j=0;j<Math.min(m, n);j++){
				V.set(j,i,eigs.getLeftEigenvectors().get(j, p[i].evIndex));
			}
		}
		values = new EV(V,posEigen);
		return values;
	}
	
	/**
	 * Runs the EM imputation, once the parameters have been set
	 */
	public void run(){
		DenseMatrix train,test;
		Instance inst;
		String mat = new String();
		String[][] X;
		int in,out;
		Attribute a;
		
		//put the train data into a DenseMatrix Class, looking for easier matrix operations
		train = new DenseMatrix(IStrain.getNumInstances(),Attributes.getNumAttributes());
		for(int i=0;i<IStrain.getNumInstances();i++){
			inst = IStrain.getInstance(i);
			in = out = 0;
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
		//print the matrix in MATLAB format for test purposes
		//Fichero.escribeFichero("c:/eclipse/matrix.dat", mat);
		//Impute by means of EM regression
		//the results are stored in the matrix passed by argument
		regem(train,IStrain);
		
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
//		Impute by means of EM regression
		//the results are stored in the matrix passed by argument
		regem(test,IStest);
		
		X = new String[IStest.getNumInstances()][Attributes.getNumAttributes()];//matrix with transformed data
		
		data2string(test,X,IStest);
		write_results(output_test_name,X,IStest);
	}
	
	/**
	 * Imputation of missing values with regularized EM algorithm
	 * @param X the reference matrix (that is, the input instances of referenceSet in matrix form)
	 * @param referenceSet the InstanceSet which contains the information of the data set
	 */
	public void regem(DenseMatrix X,InstanceSet referenceSet){
		double dofC,rdXmis,peff_ave,dofS,dXmis,nXmis_pre;
		double d[];
		Instance inst;
		int it,p,n,pa,pm,nmis,totalmis,pos,in,out;
		DenseMatrix C,CovRes,tmp,tmp2,V,tmp3;
		DenseVector D,cons,ncons,M,err,Mup,dofSv;
		SparseVector XerrCol;
		int allrows[];
		FlexCompRowMatrix Xerr,Xmis;
		EV ev;
		Attribute a;
		
		p = X.numColumns();
		n = X.numRows();
		Xerr = new FlexCompRowMatrix(n,p);
		Xmis = new FlexCompRowMatrix(n,p);
		if(neigs == 0)
			neigs  = Math.min(n-1, p);
		kmisr = new int[n][];
		kavlr = new int[n][];
		allrows = new int[referenceSet.getNumInstances()];
		totalmis = nmis = 0;
		for(int i=0;i<n;i++){
			allrows[i] = i;
			inst = referenceSet.getInstance(i);
			ArrayList<Integer> index = new ArrayList<Integer>();
			ArrayList<Integer> completeData = new ArrayList<Integer>();
//			if(inst.existsAnyMissingValue())
//				nmis++;
			in = out = 0;
			for(int j=0;j<p;j++){
				a = Attributes.getAttribute(j);
				if(a.getDirectionAttribute() == Attribute.INPUT){
					if(inst.getInputMissingValues(in)){
						index.add(j);
						totalmis++;
					}
					else
						completeData.add(j);
					in++;
				}
				else{
					if(inst.getOutputMissingValues(out)){
						index.add(j);
						totalmis++;
					}
					else
						completeData.add(j);
					out++;
				}
			}
			kmisr[i] = new int[index.size()];
			kavlr[i] = new int[completeData.size()];
			for(int j=0;j<kmisr[i].length;j++)
				kmisr[i][j] = index.get(j).intValue();
			for(int j=0;j<kavlr[i].length;j++)
				kavlr[i][j] = completeData.get(j).intValue();
		}
		nmis = totalmis;
		trunc = (int)regpar;
		System.out.println("\tPercentage of values missing: "+((double)nmis/(n*p)*100.0));
		System.out.println("\tStagnation tolerance: "+stagtol);
		System.out.println("\tMaximum number of iterations: "+maxit);
		System.out.println("\tInitialization of missing values by mean substitution.");
		switch (optRegression){
		case EM.MRIDGE:
			System.out.println("\tOne multiple ridge regression per record:");
			System.out.println("\t==> one regularization parameter per record.");
			break;
		case EM.IRIDGE:
			System.out.println("\tOne individual ridge regression per missing value:");
			System.out.println("\t==> one regularization parameter per missing value.");
			break;
		case EM.TTLS:
			System.out.println("\tOne total least squares regression per record.");
			System.out.println("\tFixed truncation parameter: "+ trunc);
			break;
		}
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH) ;
		nf.setGroupingUsed(false) ;     // don't group by threes
		nf.setMaximumFractionDigits(12) ;
		nf.setMinimumFractionDigits(12) ;
		System.out.println("\n\tIter \tmean(peff) \t|D(Xmis)| \t|D(Xmis)|/|Xmis|");
		
		
		d = centerDataMatrix(X,referenceSet);
		previousMean = Arrays.copyOf(d, d.length);
		M = new DenseVector(d);
		// number of degrees of freedom for estimation of covariance matrix
		dofC = referenceSet.getNumInstances() - 1; // use degrees of freedom correction
		//initial estimate of covariance matrix in C
		C = new DenseMatrix(X.numColumns(),X.numColumns());
		X.transAmult(1.0/dofC,X, C);
		
		if(totalmis!=0)
			it = 0;
		else
			it = maxit;
		rdXmis = Double.MAX_VALUE;
		V = null;
		while (it < maxit && rdXmis > stagtol){
			it++;
			
			// initialize for this iteration ...
		    CovRes     = new DenseMatrix(p,p);  // ... residual covariance matrix
		    CovRes.zero();
		    peff_ave   = 0;                // ... average effective number of variables
		    
		    // scale variables to unit variance
		    D = sqrt(diag(C));
		    
		    cons = new DenseVector(D.size());
		    ncons = new DenseVector(D.size());
		    for(int i=0;i<D.size();i++){
		    	if(D.get(i)<eps){
		    		cons.set(i, 1);
		    		ncons.set(i, 0);
		    	}
		    	else{
		    		cons.set(i, 0);
		    		ncons.set(i, 1);
		    	}
		    }
		    
		    //do not scale constant variables
		    if(sum(cons)!= 0){
		    	for(int i=0;i<D.size();i++)
		    		D.set(i, D.get(i)*ncons.get(i));
		    	D = (DenseVector) D.add(cons);
		    }
		    DenseMatrix transp = new DenseMatrix(1,D.size());
		    new DenseMatrix(D).transpose(transp);
		    tmp = repmat(transp, n, 1);
		    for(int i=0;i<X.numRows();i++){
		    	for(int j=0;j<X.numColumns();j++){
		    		X.set(i, j, X.get(i, j)/tmp.get(i, j));
		    	}
		    }
		    //correlation matrix
		    tmp = repmat(new DenseMatrix(D), 1, p);
		    new DenseMatrix(D).transpose(transp);
		    tmp2 = repmat(transp, p, 1); 
		    
		    for(int i=0;i<C.numRows();i++){
		    	for(int j=0;j<C.numColumns();j++){
		    		C.set(i, j, C.get(i, j)/tmp.get(i, j)/tmp2.get(i, j));
		    	}
		    }
//		    for(int f=0;f<C.numRows();f++){
//		    	for(int c=0;c<C.numColumns();c++)
//		    		System.out.print(C.get(f,c)+"\t");
//		    	System.out.println();
//		    }
		    if(optRegression == TTLS){
		    	//compute eigendecomposition of correlation matrix
		    	ev = this.peigs(C, neigs);
		    	V = ev.V;
		    	d = ev.d;
		    	peff_ave = trunc;
		    }
		    
		    //cycle over records
		    for(int i=0;i<n;i++){
		    	
		    	pm = kmisr[i].length; //number of missing values in this record
		    	if(pm > 0){
		    		pa = p - pm;
		    		//regression of missing variables on available variables
		    		if(optRegression == MRIDGE){
		    			//one multiple ridge regression per record
		    			mridge(new DenseMatrix(Matrices.getSubMatrix(C, kavlr[i], kavlr[i])),
		    					new DenseMatrix(Matrices.getSubMatrix(C, kmisr[i], kmisr[i])),
		    					new DenseMatrix(Matrices.getSubMatrix(C, kavlr[i], kmisr[i])), n-1);
		    			
		    			peff_ave = peff_ave + peff*pm/nmis;  //add up eff. number of variables
		    			dofS     = dofC - peff;              // residual degrees of freedom
		    			
		    			//inflation of residual covariance matrix
	                    S = (DenseMatrix)S.scale(inflation);
	                    
	                    //bias-corrected estimate of standard error in imputed values
	                    err = sqrt(diag(S));
	                    err.scale(dofC/dofS);
	                    for(int j=0;j<kmisr[i].length;j++)
	                    	Xerr.set(i, kmisr[i][j],err.get(j));
		    			
		    		}else if(optRegression == IRIDGE){
		    			//one individual ridge regression per missing value in this record
		    			iridge(new DenseMatrix(Matrices.getSubMatrix(C, kavlr[i], kavlr[i])),
		    					new DenseMatrix(Matrices.getSubMatrix(C, kmisr[i], kmisr[i])),
		    					new DenseMatrix(Matrices.getSubMatrix(C, kavlr[i], kmisr[i])), n-1);
		    			
		    			peff_ave = peff_ave + sum(peffv)/nmis;  //add up eff. number of variables
		    			dofSv = new DenseVector(peffv.size());
		    			for(int l=0;l<dofSv.size();l++)
		    			dofSv.set(l, dofC - peffv.get(l));              // residual degrees of freedom
		    			
		    			//inflation of residual covariance matrix
	                    S = (DenseMatrix)S.scale(inflation);
	                    err = sqrt(diag(S));
	                    err.scale(dofC);
	                    for(int j=0;j<kmisr[i].length;j++)
	                    	Xerr.set(i, kmisr[i][j],err.get(j)/dofSv.get(j));
		    		}else if(optRegression == TTLS){
		    			//truncated total least squares with fixed truncation parameter
		    			pttls(V,new DenseVector(d), kavlr[i], kmisr[i],trunc);
		    			
		    			dofS = dofC - trunc;        // residual degrees of freedom
		    			//inflation of residual covariance matrix
	                    S = (DenseMatrix)S.scale(inflation);
	                    
	                    //bias-corrected estimate of standard error in imputed values
	                    err = sqrt(diag(S));
	                    err.scale(dofC/dofS);
	                    for(int j=0;j<kmisr[i].length;j++)
	                    	Xerr.set(i, kmisr[i][j],err.get(j));
		    			
		    		}
		    		int r[] = new int[1];
		    		r[0] = i;
		    		//missing value estimates
		    		tmp = new DenseMatrix(Matrices.getSubMatrix(X, r, kavlr[i]));
		    		tmp2 = new DenseMatrix(tmp.numRows(),B.numColumns());
		    		tmp.mult(B, tmp2);
		    		for(int j=0;j<kmisr[i].length;j++)
		    			Xmis.set(i, kmisr[i][j], tmp2.get(0, j));
		    		
		    		//add up contribution from residual covariance matrices
		    		for(int j=0;j<kmisr[i].length;j++)
		    			for(int j2=0;j2<kmisr[i].length;j2++)
		    				CovRes.set(kmisr[i][j], kmisr[i][j2], CovRes.get(kmisr[i][j], kmisr[i][j2]) + S.get(j, j2));
		    	}
		    } //loop over records

		    //rescale variables to original scaling
		    tmp2 = new DenseMatrix(1,D.size());
		    new DenseMatrix(D).transpose(tmp2);
		    tmp = repmat(tmp2, n, 1);
		    for(int i=0;i<X.numRows();i++)
		    	for(int j=0;j<X.numColumns();j++)
		    		X.set(i, j, X.get(i, j)*tmp.get(i, j)); 
		    for(int i=0;i<Xerr.numRows();i++)
		    	for(int j=0;j<Xerr.numColumns();j++)
		    		Xerr.set(i, j, Xerr.get(i, j)*tmp.get(i, j)); 
		    for(int i=0;i<Xmis.numRows();i++)
		    	for(int j=0;j<Xmis.numColumns();j++)
		    		Xmis.set(i, j, Xmis.get(i, j)*tmp.get(i, j));
		    
		    tmp3 = new DenseMatrix(1,D.size());
		    new DenseMatrix(D).transpose(tmp3);
		    tmp = repmat(tmp3, p, 1);
		    tmp2 = repmat((DenseMatrix)new DenseMatrix(D), 1, p);
		    for(int i=0;i<C.numRows();i++)
		    	for(int j=0;j<C.numColumns();j++)
		    		C.set(i,j, C.get(i,j) * tmp.get(i, j) * tmp2.get(i, j));
		    for(int i=0;i<CovRes.numRows();i++)
		    	for(int j=0;j<CovRes.numColumns();j++)
		    		CovRes.set(i,j, CovRes.get(i,j) * tmp.get(i, j) * tmp2.get(i, j));
		    
		    //rms change of missing values
		    err = new DenseVector(totalmis);
		    pos = 0;
		    for(int i=0;i<n;i++){
		    	for(int j=0;kmisr[i]!=null && j<kmisr[i].length;j++){
		    		err.set(pos, Xmis.get(i, kmisr[i][j])-X.get(i, kmisr[i][j]));
		    		pos++;
		    	}
		    }
		    
		    dXmis = err.norm(no.uib.cipr.matrix.Vector.Norm.Two) / Math.sqrt(nmis);
		    
		    //relative change of missing values
		    err = new DenseVector(totalmis);
		    pos = 0;
		    for(int i=0;i<n;i++){
		    	for(int j=0;kmisr[i]!=null && j<kmisr[i].length;j++){
		    		err.set(pos, X.get(i, kmisr[i][j])+M.get(kmisr[i][j]));
		    		pos++;
		    	}
		    }
		    
		    nXmis_pre = err.norm(no.uib.cipr.matrix.Vector.Norm.Two) / Math.sqrt(nmis);
		    
		    if (nXmis_pre < eps)
		    	rdXmis = Double.POSITIVE_INFINITY;
		    else
		    	rdXmis = dXmis / nXmis_pre;
		    
		    //update data matrix X
		    for(int i=0;i<n;i++){
		    	for(int j=0;kmisr[i]!=null && j<kmisr[i].length;j++){
		    		X.set(i,kmisr[i][j], Xmis.get(i,kmisr[i][j]));
		    	}
		    }
		    
		    //re-center data and update mean
		    Mup = new DenseVector( centerDataMatrix(X,referenceSet) );  // re-center data
		    M.add(Mup);                    // updated mean vector
		    
		    //update covariance matrix estimate
		    //C = (X'*X + CovRes)/dofC;
		    X.transAmult(X, C);
		    C.add(CovRes);
		    C.scale(1.0/dofC);
		    
		    System.out.println("\t"+it+"\t"+nf.format(peff_ave)+"\t"+nf.format(dXmis)+"\t"+nf.format(rdXmis));
//		    System.out.println("\t"+it+"\t"+peff_ave+"\t"+dXmis+"\t"+rdXmis);
		} //EM iteration
		
		//add mean to centered data matrix
		tmp = new DenseMatrix(1,M.size());
		new DenseMatrix(M).transpose(tmp);
		X.add( repmat(tmp, n, 1) );
		
//		nf.setMaximumFractionDigits(4) ;
//		nf.setMinimumFractionDigits(4) ;
//		for(int i=0;i<X.numRows();i++){
//			for(int j=0;j<X.numColumns();j++){
//				System.out.print(nf.format(X.get(i,j))+" ");
//			}
//			System.out.println();
//		}
		
		lastIterations = it;
	}
	
	/**
	 * Read the pattern file, and parse data into strings
	 * @param fileParam the KEEL formatted file with the parameters
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

		} catch (IOException e) {
			System.out.println("IO exception = " + e);
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
//	public static void main(String[] args) {
//		EM regem = new EM();
//		double dd[];
//		DenseMatrix A = new DenseMatrix(3,3);
////		A.set(0, 0, 1);
////		A.set(0, 1, -0.522366707284231);
////		A.set(0, 2, 0.566363076138308);
////		A.set(1, 0, 0.522366707284231);
////		A.set(1, 1, 1);
////		A.set(1, 2, -0.998624177234139);
////		A.set(2, 0, 0.566363076138308);
////		A.set(2, 1, -0.998624177234139);
////		A.set(2, 2, 1);
//		A.set(0, 0, 1);
//		A.set(0, 1, 2);
//		A.set(0, 2, 3);
//		A.set(1, 0, 4);
//		A.set(1, 1, 5);
//		A.set(1, 2, 6);
//		A.set(2, 0, 7);
//		A.set(2, 1, 8);
//		A.set(2, 2, 9);
//		
//		dd = regem.peigs(A,2);
//		for(int i=0;i<dd.length;i++)
//			System.out.print(dd[i]+" ");
//	}
	
	/**
	 * MATLAB - Multiple ridge regression with generalized cross-validation.
	<p>
   [B, S, h, peff] = MRIDGE(Cxx, Cyy, Cxy, dof, OPTIONS) returns a
   regularized estimate B = Mxx_h Cxy of the coefficient matrix in
   the multivariate multiple regression model Y = X*B + noise(S). The
   matrix Mxx_h is the regularized inverse of the covariance matrix
   Cxx,
</p><p>
             Mxx_h = inv(Cxx + h^2 * I).
</p><p>
   The matrix Cxx is an estimate of the covariance matrix of the
   independent variables X, Cyy is an estimate of the covariance
   matrix of the dependent variables Y, and Cxy is an estimate of the
   cross-covariance matrix of the independent variables X and the
   dependent variables Y. The scalar dof is the number of degrees of
   freedom that were available for the estimation of the covariance
   matrices.
</p><p>
   The input structure OPTIONS contains optional parameters for the
   algorithm:
</p><p>
     Field name         Parameter                                   Default
</p><p>
     OPTIONS.regpar     Regularization parameter h. If regpar       not set
                        is set, the scalar OPTIONS.regpar is
                        taken as the regularization parameter h. 
                        If OPTIONS.regpar is not set (default), 
                        the regularization parameter h is selected 
                        as the minimizer of the generalized 
                        cross-validation (GCV) function. The output
                        variable h then contains the selected 
                        regularization parameter.
  </p><p>
     OPTIONS.relvar_res Minimum relative variance of residuals.       5e-2
                        From the parameter OPTIONS.relvar_res, a
                        lower bound for the regularization parameter
                        is constructed, in order to prevent GCV from
                        erroneously choosing too small a 
                        regularization parameter (see GCVRIDGE).
</p><p>
   The OPTIONS structure is also passed to GCVRIDGE.
</p><p>
   MRIDGE returns the ridge estimate B of the matrix of regression
   coefficients. Also returned are an estimate S of the residual
   covariance matrix, the regularization parameter h, and the scalar
   peff, an estimate of the effective number of adjustable
   parameters in B.  
</p><p>  
   MRIDGE computes the estimates of the coefficient matrix and of the
   residual covariance matrix from the covariance matrices Cxx, Cyy,
   and Cxy by solving the regularized normal equations. The normal
   equations are solved via an eigendecomposition of the covariance
   matrix Cxx. However, if the data matrices X and Y are directly
   available, a method based on a direct factorization of the data
   matrices will usually be more efficient and more accurate.
</p>
	 *@param cxx is an estimate of the covariance matrix of the independent variables X
	 *@param cyy is an estimate of the covariance matrix of the dependent variables Y
	 *@param cxy is an estimate of the cross-covariance matrix of the independent variables X and the
	 *@param dof the number of degrees of freedom that were available for the estimation of the covariance matrices dependent variables Y
	 */
	protected void mridge(DenseMatrix cxx, DenseMatrix cyy,DenseMatrix cxy,int dof){
		int px,py,rmax,r;
		double relvar_res = 5e-2;
		double trSmin;
		DenseMatrix V = null,tmp,F,S0,_d,tmp2;
		DenseVector foo;
		double d[] = null;
		EV ev;
		
		px = cxx.numRows();
		py = cyy.numRows();
		
		if(px!=cxx.numColumns() || py!=cyy.numColumns() || cxy.numRows()!=px || cxy.numColumns()!=py){
			System.err.println("Error: Incompatible sizes of covariance matrices.");
			System.exit(-1);
		}

		//eigendecomposition of Cxx
		rmax = Math.min(dof, px);     //maximum possible rank of Cxx
		ev = peigs(cxx, rmax);
		V = ev.V;
		d = ev.d;
		r = d.length;

		for(int i=0;i<V.numColumns();i++){
			if(V.get(0, i) < 0){
				for(int j=0;j<V.numRows();j++)
					V.set(j,i, V.get(j, i)*-1);
			}
		}

		// Fourier coefficients. (The following expression for the Fourier
		// coefficients is only correct if Cxx = X'*X and Cxy = X'*Y for
		// some, possibly scaled and augmented, data matrices X and Y; for
		// general Cxx and Cxy, all eigenvectors V of Cxx must be included,
		// not just those belonging to nonzero eigenvalues.)
		tmp = ones(r, 1);
		for(int i=0;i<r;i++)
			tmp.set(i, 0, tmp.get(i, 0)/Math.sqrt(d[i]));
		F = repmat(tmp, 1, px);
		tmp = new DenseMatrix(V.numColumns(),V.numRows());
		V.transpose(tmp);
		for(int i=0;i<F.numRows();i++){
			for(int j=0;j<F.numColumns();j++){
				F.set(i,j,F.get(i, j)*tmp.get(i,j ));
			}
		}
		tmp = new DenseMatrix(F.numRows(),cxy.numColumns());
		F.mult(cxy, tmp);
		F = tmp;

		// Part of residual covariance matrix that does not depend on the
		// regularization parameter h:
		if (dof > r){
			tmp = new DenseMatrix(F.numColumns(),F.numColumns());
			F.transAmult(F, tmp);
			tmp2 = cyy.copy();
			S0 = (DenseMatrix)tmp2.add(-1, tmp);
		}
		else
			S0 = new DenseMatrix(py, py);

		if(!useRegPar || Double.isNaN(regpar)){
			//approximate minimum squared residual
			trSmin = relvar_res * trace(cyy);
			//find regularization parameter that minimizes the GCV object function
			h = gcvridge(F, d, trace(S0), dof, r, trSmin);
		}
//		h = 235.587064966832e-003;
		//get matrix of regression coefficients
		foo = new DenseVector(d);
		for(int i=0;i<foo.size();i++){
			foo.set(i, Math.sqrt(foo.get(i))/ (foo.get(i)+Math.pow(h, 2)) );
		}
		_d = new DenseMatrix(foo);
		_d = repmat(_d, 1, py);
		for(int i=0;i<_d.numRows();i++){
			for(int j=0;j<_d.numColumns();j++){
				_d.set(i, j, _d.get(i, j) * F.get(i, j));
			}
		}
		//B = V * _d
		B = new DenseMatrix(V.numRows(),_d.numColumns());
		V.mult(_d, B);

		//get estimate of covariance matrix of residuals
		foo = new DenseVector(d);
		for(int i=0;i<foo.size();i++){
			foo.set(i, Math.pow(h, 4) / Math.pow(foo.get(i)+Math.pow(h, 2),2) );
		}
		_d = new DenseMatrix(foo);
		_d = repmat(_d, 1, py);
		for(int i=0;i<_d.numRows();i++){
			for(int j=0;j<_d.numColumns();j++){
				_d.set(i, j, _d.get(i, j) * F.get(i, j));
			}
		}
		//F' * _d
		S = new DenseMatrix(F.numColumns(),_d.numColumns());;
		F.transAmult(_d, S);
		S.add(S0);
//		for(int f=0;f<S.numRows();f++){
//		for(int c=0;c<S.numColumns();c++)
//		System.out.print(S.get(f,c)+"\t");
//		System.out.println();
//		}
		//effective number of adjusted parameters: peff = trace(Mxx_h Cxx)
		foo = new DenseVector(d);
		for(int i=0;i<foo.size();i++){
			foo.set(i, foo.get(i) / (foo.get(i)+Math.pow(h, 2)) );
		}
		peff = sum(foo);
	}
	
	/**
	 * MATLAB - Finds minimum of GCV function for ridge regression.
<p>
   GCVRIDGE(F, d, trS0, n, r, trSmin, OPTIONS) finds the
   regularization parameter h that minimizes the generalized
   cross-validation function
</p><p>
                         trace S_h
                 G(h) = ----------- 
                          T(h)^2
</p><p>
   of the linear regression model Y = X*B + E. The data matrices X
   and Y are assumed to have n rows, and the matrix Y of dependent
   variables can have multiple columns. The matrix S_h is the second
   moment matrix S_h = E_h'*E_h/n of the residuals E_h = Y - X*B_h,
   where B_h is, for a given regularization parameter h, the
   regularized estimate of the regression coefficients,
</p><p> 
                B_h = inv(X'*X + n h^2*I) * X'*Y.
</p><p>
   The residual second second moment matrix S_h can be represented
   as
</p><p>
                S_h = S0 + F' * diag(g.^2) * F
</p><p>
   where g = h^2 ./ (d + h^2) = 1 - d.^2 ./ (d + h^2) and d is a
   column vector of eigenvalues of X'*X/n. The matrix F is the matrix
   of Fourier coefficients. In terms of a singular value
   decomposition of the rescaled data matrix n^(-1/2) * X = U *
   diag(sqrt(d)) * V', the matrix of Fourier coefficients F can be
   expressed as F = n^(-1/2) * U' * Y. In terms of the eigenvectors V
   and eigenvalues d of X'*X/n, the Fourier coefficients are F =
   diag(1./sqrt(d)) * V' * X' * Y/n. The matrix S0 is that part of
   the residual second moment matrix that does not depend on the
   regularization parameter: S0 = Y'*Y/n - F'*F.
 </p><p>
   As input arguments, GCVRIDGE requires:
        F:  the matrix of Fourier coefficients,
        d:  column vector of eigenvalues of X'*X/n,
     trS0:  trace(S0) = trace of generic part of residual 2nd moment matrix,
        n:  number of degrees of freedom for estimation of 2nd moments,
        r:  number of nonzero eigenvalues of X'*X/n,
   trSmin:  minimum of trace(S_h) to construct approximate lower bound
            on regularization parameter h (to prevent GCV from choosing
            too small a regularization parameter).
</p><p>
   The vector d of nonzero eigenvalues of X'*X/n is assumed to be
   ordered such that the first r elements of d are nonzero and ordered 
   from largest to smallest.
 </p><p>
   The input structure OPTIONS contains optional parameters for the
   algorithm:
</p><p>
     Field name           Parameter                                  Default
</p><p>
     OPTIONS.minvarfrac Minimum fraction of total variation in X     0
                        that must be retained in the 
                        regularization. From the parameter 
                        OPTIONS.minvarfrac, an approximate upper 
                        bound for the regularization parameter is
                        constructed. The default value 
                        OPTIONS.minvarfrac = 0  corresponds to no
                        upper bound for the regularization parameter.   
 </p><p>
   References:
   GCVRIDGE is adapted from GCV in Per Christian Hansen's REGUTOOLS
       toolbox:
   P.C. Hansen, "Regularization Tools: A Matlab package for
       analysis and solution of discrete ill-posed problems,"
       Numer. Algorithms, 6 (1994), 1--35.
</p><p>
   see also: 
   G. Wahba, "Spline Models for Observational Data",
       CBMS_NSF Regional Conference Series in Applied Mathematics,
       SIAM, 1990, chapter 4.
</p>
	 * @param F matrix of Fourier coefficients
	 * @param d column vector of eigenvalues of X'*X/n
	 * @param trS0 column vector of eigenvalues of X'*X/n
	 * @param n number of degrees of freedom for estimation of 2nd moments
	 * @param r number of nonzero eigenvalues of X'*X/n
	 * @param trSmin minimum of trace(S_h) to construct approximate lower bound on regularization parameter h (to prevent GCV from choosing too small a regularization parameter)
	 * @return the regularization parameter h that minimizes the generalized cross-validation function
	 */
	public double gcvridge(DenseMatrix F, double[] d, double trS0, int n, int r, double trSmin){
		int p;
		int minindex[] = null;
		DenseVector fc2,varfrac;
		DenseVector rtsvd;
		DenseMatrix foo;
		double h_tol,h_max,d_max,h_min,tmp,h_opt;
		Gcvfctn _f;
		ArrayList<Integer> intW = new ArrayList<Integer>();
		
		if(d.length < r){
			System.err.println("Error: All nonzero eigenvalues must be given");
			System.exit(-1);
		}
		
		p = F.numRows();
		
		if(p<r){
			System.err.println("Error: F must have at least as many rows as there are nonzero eigenvalues d");
			System.exit(-1);
		}
		
		// row sum of squared Fourier coefficients
		foo = new DenseMatrix(F.numRows(),F.numColumns());
		for(int i=0;i<F.numRows();i++){
			for(int j=0;j<F.numColumns();j++){
				foo.set(i, j, F.get(i, j)*F.get(i, j));
			}
		}
		fc2 = sumbyRows(foo);
		//accuracy of regularization parameter 
		h_tol = .2/Math.sqrt(n);        

		//heuristic upper bound on regularization parameter
		varfrac = cumsum(new DenseVector(d));
		varfrac.scale(1.0/sum(new DenseVector(d)));
		if (minvarfrac > min(varfrac)){
			d_max = Interpolation.newtonInterp(varfrac.getData(), d).valueAt(minvarfrac);
			h_max = Math.sqrt( d_max );
		}
		else{            
			h_max = Math.sqrt( max(new DenseVector(d)) ) / h_tol;
		}
		
		//heuristic lower bound on regularization parameter
		if( trS0 > trSmin){
			//squared residual norm is greater than a priori bound for all 
		    // regularization parameters
			h_min = Math.sqrt(eps);
		} else{
			// find squared residual norms of truncated SVD solutions
			rtsvd = new DenseVector(r);
			rtsvd.zero();
			rtsvd.set((r-1),trS0);
			
			for(int j=r-2;j>-1;j--){
				rtsvd.set(j, rtsvd.get(j+1) + fc2.get(j+1));
			}
			//take regularization parameter equal to square root of eigenvalue 
		    // that corresponds to TSVD truncation level with residual norm closest 
		    // to a priori bound trSmin
			for(int i=0;i<rtsvd.size();i++){
				rtsvd.set(i,Math.abs(rtsvd.get(i)-trSmin));
			}
			tmp = min(rtsvd,intW);
			minindex = new int[intW.size()];
			for(int i=0;i<intW.size();i++)
				minindex[i] = intW.get(i).intValue();
			h_min = Math.max(d[minindex[0]],min(new DenseVector(d))/n);
		}
		_f = new Gcvfctn(d,fc2.getData(),trS0,n-r);
		if(h_min < h_max){
			//find minimizer of GCV function
			_f.setBounds(h_min, h_max);
			UnivariateMinimum minimizer = new UnivariateMinimum();
//			h_opt = minimizer.findMinimum(_f);
			h_opt = minimizer.optimize(_f,h_tol);
//			System.out.println(_f.evaluate(h_opt));
		}
		else{
			System.out.println("Warning: Upper bound on regularization parameter smaller than lower bound.");
			h_opt  = h_min; 
		}
		
		return h_opt;
	}
	
	/**
	 * MATLAB - Individual ridge regressions with generalized cross-validation.
<p>
   [B, S, h, peff] = IRIDGE(Cxx, Cyy, Cxy, dof) returns a regularized
   estimate B of the coefficient matrix for the multivariate multiple
   regression model Y = X*B + noise(S).  Each column B(:,k) of B is
   computed by a ridge regression as B(:,k) = Mxx_hk Cxy(:,k), where
   Mxx_hk is a regularized inverse of Cxx,
</p><p>
             Mxx_h = inv(Cxx + hk^2 * I).
</p><p>
   For each column k of B, an individual regularization parameter
   ('ridge parameter') hk is selected as the minimizer of the
   generalized cross-validation function. The matrix Cxx is an
   estimate of the covariance matrix of the independent variables X,
   Cyy is an estimate of the covariance matrix of the dependent
   variables Y, and Cxy is an estimate of the cross-covariance matrix
   of the independent variables X and the dependent variables Y. The
   scalar dof is the number of degrees of freedom that were available
   for the estimation of the covariance matrices.
</p><p>
   The input structure OPTIONS contains optional parameters for the
   algorithm:
</p><p>
     Field name         Parameter                                   Default
</p><p>
     OPTIONS.relvar_res Minimum relative variance of residuals.       5e-2
                        From the parameter OPTIONS.relvar_res, a
                        lower bound for the regularization parameter
                        is constructed, in order to prevent GCV from
                        erroneously choosing too small a 
                        regularization parameter (see GCVRIDGE).
</p><p>
   The OPTIONS structure is also passed to GCVRIDGE.
</p><p>     
   IRIDGE returns an estimate B of the matrix of regression
   coefficients. Also returned are an estimate S of the residual
   covariance matrix, a vector h containing the regularization
   parameters hk for the columns of B, and the scalar peff, an
   estimate of the effective number of adjustable parameters in each
   column of B.
</p><p>
   IRIDGE computes the estimates of the coefficient matrix and of the
   residual covariance matrix from the covariance matrices Cxx, Cyy,
   and Cxy by solving the regularized normal equations. The normal
   equations are solved via an eigendecomposition of the covariance
   matrix Cxx. However, if the data matrices X and Y are directly
   available, a method based on a direct factorization of the data
   matrices will usually be more efficient and more accurate.
</p>
	 * @param cxx is an estimate of the covariance matrix of the independent variables X
	 * @param cyy is an estimate of the covariance matrix of the dependent variables Y
	 * @param cxy is an estimate of the cross-covariance matrix of the independent variables X and the dependent variables Y
	 * @param dof is the number of degrees of freedom that were available for the estimation of the covariance matrices
	 */
	protected void iridge(DenseMatrix cxx, DenseMatrix cyy,DenseMatrix cxy,int dof){
		int px,py,rmax,r;
		int rows[],col[];
		double relvar_res = 5e-2,aux;
		DenseVector trSmin,diagS;
		DenseMatrix V = null,tmp,F,S0,_d,tmp2;
		DenseVector foo;
		double d[] = null;
		EV ev;
		
		px = cxx.numRows();
		py = cyy.numRows();
		
		if(px!=cxx.numColumns() || py!=cyy.numColumns() || cxy.numRows()!=px || cxy.numColumns()!=py){
			System.err.println("Error: Incompatible sizes of covariance matrices.");
			System.exit(-1);
		}
		
		//eigendecomposition of Cxx
		rmax = Math.min(dof, px);     //maximum possible rank of Cxx
		ev = peigs(cxx, rmax);
		V = ev.V;
    	d = ev.d;
		r = d.length;
		
//		 Fourier coefficients. (The following expression for the Fourier
		// coefficients is only correct if Cxx = X'*X and Cxy = X'*Y for
		// some, possibly scaled and augmented, data matrices X and Y; for
		// general Cxx and Cxy, all eigenvectors V of Cxx must be included,
		// not just those belonging to nonzero eigenvalues.)
		tmp = ones(r, 1);
		for(int i=0;i<r;i++)
			tmp.set(i, 0, tmp.get(i, 0)/Math.sqrt(d[i]));
		F = repmat(tmp, 1, px);
		tmp = new DenseMatrix(V.numColumns(),V.numRows());
		V.transpose(tmp);
		for(int i=0;i<F.numRows();i++){
			for(int j=0;j<F.numColumns();j++){
				F.set(i,j,F.get(i, j)*tmp.get(i,j ));
			}
		}
		tmp = new DenseMatrix(F.numRows(),cxy.numColumns());
		F.mult(cxy, tmp);
		F = tmp;
		
		// Part of residual covariance matrix that does not depend on the
		// regularization parameter h:
		if (dof > r){
			tmp = new DenseMatrix(F.numColumns(),F.numColumns());
			F.transAmult(F, tmp);
			tmp2 = cyy.copy();
			S0 = (DenseMatrix)tmp2.add(-1, tmp);
		}
		else
			S0 = new DenseMatrix(py, py);

		//approximate minimum squared residual
		trSmin = diag(cyy).scale(relvar_res);
		
		//initialize output
		hv = new DenseVector(py);
		hv.zero();
		B = new DenseMatrix(px, py);
		B.zero();
		S = new DenseMatrix(py, py);
		S.zero();
		peffv = new DenseVector(py);
		peffv.zero();
		
		rows = new int[F.numRows()];
		for(int i=0;i<F.numRows();i++)
			rows[i] = i;
		col = new int[1];
		for(int k=0;k<py;k++){
			//compute an individual ridge regression for each y-variable
			
			//find regularization parameter that minimizes the GCV object function
			col[0] = k;
			hv.set(k, gcvridge((DenseMatrix)Matrices.getSubMatrix(F,rows,col).copy(), d, S0.get(k,k), dof, r, trSmin.get(k)));
			
			//k-th column of matrix of regression coefficients
			foo = new DenseVector(d);
			for(int i=0;i<foo.size();i++){
				foo.set(i, Math.sqrt(foo.get(i))/ (foo.get(i)+Math.pow(hv.get(k), 2)) );
			}
			for(int i=0;i<F.numRows();i++)
				foo.set(i, F.get(i, k)*foo.get(i));
			tmp = new DenseMatrix(foo);
			tmp2 = new DenseMatrix(d.length,1);
			V.mult(tmp, tmp2);
			for(int i=0;i<B.numRows();i++){
				B.set(i,k, tmp2.get(i, 0));
			}
			
			//assemble estimate of covariance matrix of residuals
			for(int j=0;j<=k;j++){
				diagS = new DenseVector(d.length);
				aux = Math.pow(hv.get(j),2) * Math.pow(hv.get(k),2);
				for(int l=0;l<d.length;l++)
					diagS.set(l, aux /  ((d[l] + Math.pow(hv.get(j),2)) * (d[l] + Math.pow(hv.get(k),2))) );
				for(int l=0;l<diagS.size();l++)
					diagS.set(l,diagS.get(l) * F.get(l, k));
				
				for(int l=0;l<diagS.size();l++)
					S.add(j, k, F.get(l, j) * diagS.get(l));
				S.add(j, k, S0.get(j, k));
				S.set(k,j,S.get(j,k));
			}
			
			// effective number of adjusted parameters in this column
			// of B: peff = trace(Mxx_h Cxx)
			foo = new DenseVector(d);
			for(int l=0;l<foo.size();l++){
				foo.set(l,d[l] / (d[l]+ Math.pow(hv.get(k), 2)));
			}
			peffv.set(k, sum(foo));
		}
		
	}
	
	/**
	 * MATLAB - Truncated TLS regularization with permuted columns.
<p>
    Given matrices A and B, the total least squares (TLS) problem
    consists of finding a matrix Xr that satisfies
</p><p>
                (A+dA)*Xr = B+dB.
</p><p>
    The solution must be such that the perturbation matrices dA
    and dB have minimum Frobenius norm rho=norm( [dA dB], 'fro')
    and each column of B+dB is in the range of A+dA [1].
  </p><p>
    [Xr, Sr, rho, eta] = PTTLS(V, d, colA, colB, r) computes the
    minimum-norm solution Xr of the TLS problem, truncated at rank r
    [2]. The solution Xr of this truncated TLS problem is a
    regularized error-in-variables estimate of regression
    coefficients in the regression model A*X = B + noise(S). The
    model may have multiple right-hand sides, represented as columns
    of B.
</p><p>
    As input, PTTLS requires the right singular matrix V of the
    augmented data matrix C = U*diag(s)*V' and the vector d=s.^2 with
    the squared singular values. Only right singular vectors V(:,j)
    belonging to nonzero singular values s(j) are required.  Usually,
    the first n columns of the augmented data matrix C correspond to
    the n columns of the matrix A, and the k last columns to the k
    right-hand sides B, so that the augmented data matrix is of the
    form C=[A B]. PTTLS allows a more flexible composition of the
    data matrix C: the columns with indices colA correspond to
    columns of A; the columns with indices colB correspond to columns
    of B.
</p><p>
    The right singular vectors V and the squared singular values d
    may be obtained from an eigendecomposition of the matrix C'*C = v
    * d * v', which, for centered data, is proportional to the sample
    covariance matrix.
</p><p>
    PTTLS returns the rank-r truncated TLS solution Xr and the matrix
    Sr = dB'*dB, which is proportional to the estimated covariance
    matrix of the residual dB. Also returned are the Frobenius norm 
</p><p>
                    rho = norm([dA dB], 'fro') 
</p><p>
    of the residuals and the Frobenius norm 
</p><p>
                    eta = norm(Xr,'fro') 
</p><p>
    of the solution matrix Xr.
</p><p>
    If the truncation parameter r(1:nr) is a vector of length nr,
    then Xr is a 3-D matrix with
</p><p>
         Xr(:,:, 1:nr) = [ Xr(r(1)), Xr(r(2)), ..., Xr(r(nr)) ] .
</p><p>
    The covariance matrix estimate Sr has an analogous structure, and
    the residual norm rho(1:nr) and the solution norm eta(1:nr) are
    vectors with one element for each r(1:nr).
</p><p>
    If r is not specified or if r > n, r = n is used.
</p><p>
     References: 
     [1] Van Huffel, S. and J.Vandewalle, 1991:
         The Total Least Squares Problem: Computational Aspects
         and Analysis. Frontiers in Mathematics Series, vol. 9. SIAM.
     [2] Fierro, R. D., G. H. Golub, P. C. Hansen and D. P. O'Leary, 1997:
         Regularization by truncated total least squares, SIAM
         J. Sci. Comput., 18, 1223-1241
     [3] Golub, G. H, and C. F. van Loan, 1989: Matrix
         Computations, 2d ed., Johns Hopkins University Press,
         chapter 12.3 
</p>
	 * @param V right singular vectors V
	 * @param d squared singular values
	 * @param colA columns of A used (indices)
	 * @param colB columns of B used (indices)
	 * @param r rank of truncation
	 */
	protected void pttls(DenseMatrix V,DenseVector d, int[] colA, int[] colB, int r){
		int na,ma,nd,n,k,nr,rc;
		int cols[];
		DenseMatrix V11,V21,V22,tmp,tmp2,res;
		
		na = V.numRows();
		ma = V.numColumns();
		
		nd = d.size();
		
		if (ma < nd){
			System.err.println("All right singular vectors with nonzero singular value are required");
			System.exit(1);
		}
		
		n = colA.length;
		k = colB.length;
		if (n + k != na){
	      System.err.println("Impossible set of column indices.");
	      System.exit(1);
		}
		
		nr = 1; //length(r) is 1 <-- only one truncation parameter
		if(r < 1){
			System.err.println("Impossible truncation parameter");
			System.exit(1);
		}
		if(r > n){
			System.out.println("\t Truncation parameter lowered");
			r = n;
		}
		
		//initialize output variables
		B = new DenseMatrix(n,k);
		B.zero();
		S = new DenseMatrix(k,k);
		S.zero();
		
		//compute a separate solution for each r
		for(int ir=0;ir<nr;ir++){
			rc = r;
			cols = new int[rc];
			for(int i=0;i<cols.length;i++){
				cols[i] = i;
			}
			V11 = new DenseMatrix(Matrices.getSubMatrix(V, colA, cols));
			V21 = new DenseMatrix(Matrices.getSubMatrix(V, colB, cols));
			
			tmp = new DenseMatrix(V11.numColumns(),V11.numColumns());
			V11.transAmult(V11, tmp);
			//since B/A = (A'\B')', we apply the transformations needed to achieve the division:
			//					V11 / tmp = (tmp' \ V11')' = (tmp' \ tmp2)'
			tmp2 = new DenseMatrix(V11.numColumns(),V11.numRows());
			V11.transpose(tmp2); //V11' = tmp2
			res = new DenseMatrix(tmp.numRows(),tmp2.numColumns());
			tmp.transSolve(tmp2, res); // (tmp' \ tmp2)
			tmp2 = new DenseMatrix(res.numColumns(),res.numRows());
			res.transpose(tmp2); // (tmp' \ tmp2)' = V11 / tmp as we initially intended
			
			tmp2.transBmult(V21, B);
			
			//estimated covariance matrix of residuals dB0'*dB0 (up to a
		    // scaling factor)
			cols = new int[nd-rc];
			for(int i=rc,j=0;i<nd;i++,j++){
				cols[j] = i;
			}
			V22 = new DenseMatrix(Matrices.getSubMatrix(V, colB, cols));
			tmp = new DenseMatrix(Matrices.getSubVector(d, cols));
			tmp2 = repmat(tmp,1,k);
			tmp = new DenseMatrix(V22.numColumns(),V22.numRows());
			V22.transpose(tmp);
			for(int i=0;i<tmp2.numRows();i++){
				for(int j=0;j<tmp2.numColumns();j++){
					tmp2.set(i, j, tmp2.get(i, j) * tmp.get(i, j));
				}
			}
			V22.mult(tmp2, S);
			
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
				//older version - EM only used inputs attributes
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
	
	/**
	 * Write data matrix X to disk, in KEEL format
	 * @param output the output file path
	 * @param X the matrix with the data to be written
	 * @param IS the reference InstanceSet of the original KEEL data set loaded
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


