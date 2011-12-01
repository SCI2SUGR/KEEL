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

package keel.Algorithms.Discretizers.UCPD;

import keel.Algorithms.Statistical_Classifiers.Logistic.core.matrix.EigenvalueDecomposition;
import keel.Algorithms.Statistical_Classifiers.Logistic.core.matrix.Matrix;


/**
 * <p>
 * This class implements the PCA algorithm
 * </p>
 * 
 * @author Written by Jose A. Saez (University of Granada), 21/12/2009
 * @version 1.0
 * @since JDK1.6
 */
public class PCA {
	
	private double[][] COVAR;			// covariance matrix
	private int numInstances;			// number of instances
	private int numAtt;					// number of continuous attributes
	private double instances[][];		// all the instances
	
	private double[] allEigenvalues;		// all the eigenvalues
	private double[][] allEigenvectors;	// all the eigenvectors
	
	private double[] eigenvalue;		// sorted eigenvalues of selected eigenvectors
	private int numDimensions;			// number of selected eigenvalues (dimensions)
	
	
//******************************************************************************************************

	/**
	 * <p>
	 * Constructor of the class
	 * </p>
	 * @param examplesp matrix of instances
	 */
	public PCA(double [][]examplesp) {
		
		instances = examplesp;
		numInstances = instances.length;
		numAtt = instances[0].length;
	    COVAR = new double[numAtt][numAtt];
	}
	    
//******************************************************************************************************

	/**
	 * <p>
	 * It computes all necesary parameters
	 * </p>
	 */
	public void ComputeParameters(){

		int attr1, attr2, i;
		double sum;
		
		// compute the covarianze matrix
		for(attr1 = 0 ; attr1 < numAtt ; ++attr1){
			
			for(attr2 = attr1 ; attr2 < numAtt ; ++attr2){
				
				//compute
				sum = 0;
				for(i = 0 ; i<numInstances ; ++i)
					sum += (double)((double)(instances[i][attr1])*(double)(instances[i][attr2]));
				
				sum /= (double)(numInstances-1);
				
				//put sum in positions [attr1][attr2] and vice versa
				COVAR[attr1][attr2] = COVAR[attr2][attr1] = sum;
			}
		}
		
		
		// compute eigenvectors of COVAR and their eigenvalues
		Matrix Covariance = new Matrix(COVAR, numAtt, numAtt);					// create the covariance matrix
		EigenvalueDecomposition ev = new EigenvalueDecomposition(Covariance);	// compute eigenvalues and eigenvectors
		
		allEigenvalues = ev.getRealEigenvalues();								// get real part of eigenvalues		
		allEigenvectors = ev.getV().getArray();									// get eigenvectors
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It computes the most representative eigenvectors
	 * </p>
	 * @param percent percentage needed to compute the representative eigenvectors
	 * @return the selected eigenvectors
	 */
	public double[][] getEigenvectors(double percent){
		
		int i, j;
		
		int pos[] = Quicksort.sort(allEigenvalues, allEigenvalues.length, Quicksort.HIGHEST_FIRST);
		
		// compute the sum of eigenvalues
		double total = 0;
		for(i = 0 ; i < allEigenvalues.length ; ++i)
			total += allEigenvalues[i];
		
		// take the perc of total sumatory
		double threshold = total*percent;
		
		total = 0;
		numDimensions = 0;
		for(i = 0 ; total < threshold ; ++i){
			total += allEigenvalues[pos[i]];
			numDimensions++;
		}
		
		eigenvalue = new double[numDimensions];
		for(i = 0 ; i < numDimensions ; ++i)
			eigenvalue[i] = allEigenvalues[pos[i]];
		
		//take the perc of the eigenvector with higher eigenvalues
		double[][] selectedEvectors = new double[numAtt][numDimensions];
		for(i = 0 ; i < numAtt ; ++i){
			for(j = 0 ; j < numDimensions ; ++j)
				selectedEvectors[i][j] = allEigenvectors[i][pos[i]];
		}
		
		return selectedEvectors;
	}

//******************************************************************************************************

	/**
	 * <p>
	 * It computes the final data
  	 * </p>
	 * @param selectedEvectors selected eigenvectors
	 * @return the final data matrix
	 */
	public double[][] DerivingNewData(double[][] selectedEvectors){
		
		// data Projection onto Eigenspace
		Matrix RowFeatureVector = new Matrix(selectedEvectors, numAtt, numDimensions);	// create the covariance matrix
		RowFeatureVector = RowFeatureVector.transpose();
		
		Matrix RowDataAdjust = new Matrix(instances, numInstances, numAtt);	// create the covariance matrix
		RowDataAdjust = RowDataAdjust.transpose();

		Matrix FinalData = RowFeatureVector.times(RowDataAdjust);
		FinalData = FinalData.transpose();
		
		return FinalData.getArray();
	}
	
//******************************************************************************************************

	/**
	  * <p>
	  * It returns the number of dimensions
	  * </p>
	  * @return the number of dimensions
	  */
	public int getNumDimensions(){
		
		return numDimensions;
	}
	
//******************************************************************************************************

	/**
	  * <p>
	  * It returns the selected eigenvalues vector
	  * </p>
	  * @return the selected eigenvalues vector
	  */
	public double[] getEigenvalues(){
		
		return eigenvalue;
	}
	
//******************************************************************************************************

	/**
	  * <p>
	  * It returns the matrix of covariance
	  * </p>
	  * @return the matrix of covariance
	  */
	public double[][] getCovarianceMatrix(){
		
		return COVAR;
	}
	
}