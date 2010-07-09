package keel.Algorithms.Neural_Networks.IRPropPlus_Clas;

/**
 * <p>
 * @author by Pedro Antonio Gutierrez Penia (University of Cordoba) 27/10/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface IOptimizableFunc
{
	/**
	 * <p>
	 * Interface to specify a model or function from a set of coefficients
	 * and the gradient of an error function using this model
	 * </p> 
	 */
	
	
	/**
	 * <p>
	 * Returns the initial value of a[], that is, the coefficients of 
	 * the model
	 * @return double array of initial coefficients values
	 * </p>
	 */

	double[] getCoefficients();

	/**
	 * <p>
	 * Establish the final value of a[], that is, the coefficients of
	 * model 
	 * @param double array of final coefficients values
	 * </p>
	 */

	void setCoefficients(double[] a);
	
	/** 
	 * <p>
	 * Returns the gradient vector of the derivative of an error function (E)
	 * with respect to each coefficient of the model, using an input observation
	 * matrix (x[]) and an expected output matrix (y[]). Also returns the
	 * error associated.
	 * 
	 * @param x Array with all inputs of all observations
	 * @param y Array with all expected outputs of all observations
	 *  
	 * @return double Resulting gradient vector of dE/da for all coefficients
	 * </p>
	 */

	public double[] gradient(double [][] x, double [][] y);
	
	/**
	 * <p>
	 * Last error of the model
	 * 
	 * @return double Error of the function of the model with respect to data y[]
	 * </p>
	 */
	
	public double getLastError();

}
