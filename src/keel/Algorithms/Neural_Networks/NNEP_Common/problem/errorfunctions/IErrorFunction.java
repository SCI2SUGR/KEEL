package keel.Algorithms.Neural_Networks.NNEP_Common.problem.errorfunctions;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penya, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface IErrorFunction<T> {
	
	/**
	 * <p>
	 * Interface of an error function
	 * </p>
	 * @param <T> Type of evaluated entities
	 */

	/**
	 * <p>
	 * Calculate error of obtained entity, comparated with
	 * expected entity
	 * </p>
	 * @param obtained T entity obtained
	 * @param expected T entity expected
	 * @return double Error of obtained entity, comparated with
	 *                expected entity
	 */    
    public double calculateError(T obtained, T expected);
}
