package keel.Algorithms.Preprocess.Missing_Values.EM;

import no.uib.cipr.matrix.DenseMatrix;

/**
 * This class stores a set of eigenvalues and eigenvectors
 * @author Julián Luengo Martín
 *
 */
public class EV {

	/** eigenvectors */
	public DenseMatrix V;
	/** eigenvalues */
	public double [] d;
	
	/**
	 * Copy constructor (soft)
	 * @param eigenVectors the original eigenVectors 
	 * @param eigenValues the original eigenValues
	 */
	public EV(DenseMatrix eigenVectors,double[] eigenValues){
		V = eigenVectors;
		d = eigenValues;
	}
}
