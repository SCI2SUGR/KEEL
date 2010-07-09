package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.EARMGA;

/**
 * <p>
 * @author Written by Nicolò Flugy Papè (Politecnico di Milano) 15/06/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

public class Interval {
  /**
   * <p>
   * It represents an interval
   * </p>
   */
	
  private double x0;
  private double x1;
  
  /**
   * <p>
   * Default constructor
   * </p>
   */
  public Interval() {
  }
  
  /**
   * <p>
   * It checks whether a value is covered by an interval
   * </p>
   * @param x The value to check
   * @return True if the value is covered by this interval; False otherwise
   */
  public boolean isCovered(double x) {
	if ((x >= this.x0) && (x <= this.x1)) return true;
	else return (false);
  }
  
  /**
   * <p>
   * It returns the left bound of an interval
   * </p>
   * @return A value representing the left bound of the interval
   */
  public double getLeft() {
	return this.x0;
  }
  
  /**
   * <p>
   * It returns the right bound of an interval
   * </p>
   * @return A value representing the right bound of the interval
   */
  public double getRight() {
	return this.x1;
  }
  
  /**
   * <p>
   * It sets the left bound of an interval
   * </p>
   * @param value A value representing the left bound of the interval
   */
  public void setLeft(double value) {
	this.x0 = value;
  }
  
  /**
   * <p>
   * It sets the right bound of an interval
   * </p>
   * @param value A value representing the right bound of the interval
   */
  public void setRight(double value) {
	this.x1 = value;
  }

  /**
   * <p>
   * It allows to clone correctly an interval
   * </p>
   * @return A copy of the interval
   */
  public Interval clone() {
    Interval interval = new Interval();
    
    interval.x0 = this.x0;
    interval.x1 = this.x1;
    
	return interval;
  }
  
}
