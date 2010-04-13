/**
* <p>
* @author Written by Alberto Fernandez (University of Granada)01/01/2008
* @version 1.0
* @since JDK1.5
* </p>
*/
package keel.Algorithms.Statistical_Tests.Shared.Test_Friedman;

public class Relation {
	/**
	* <p>
	* This class defines a relation
	* </p>
	*/
  public int i;
  public int j;
	/**
	* <p>
	* Default constructor
	* </p>
	*/
  public Relation() {
  }
  
  /**
   * <p>
   * Constructor from two int
   * @parameter x The value of the first attribute
   * @parameter y The value of the first attribute
   * </p>
   */
  public Relation(int x, int y) {
	  /**
	   * <p>
	   * Constructor from two int
	   * @parameter x The value of the first attribute
	   * @parameter y The value of the first attribute
	   * </p>
	   */
	  i = x;
	  j = y;
  }

  public String toString() {
	  /**
	   * <p>
	   * This method converts the values of class attributes to a string
	   * @parameter x The value of the first attribute
	   * @parameter y The value of the first attribute
	   * @return A string containing the values of class attributes separated by ',' and surrounded by '()'
	   * </p>
	   */	  
	  return "("+i+","+j+")";
  }

}
