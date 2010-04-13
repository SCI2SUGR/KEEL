/**
* <p>
* @author Written by Alberto Fernandez (University of Granada)01/01/2008
* @version 1.0
* @since JDK1.5
* </p>
*/


package keel.Algorithms.Statistical_Tests.Shared.Test_Friedman;


public class Pair implements Comparable {
	/**
	* <p>
	* This class defines a pair of index and value
	* </p>
	*/
  public double index;
  public double value;

	/**
	* <p>
	* Default constructor
	* </p>
	*/
  public Pair() {

  }
	/**
	* <p>
	* Constructor from two double
	* </p>
	*/
  public Pair(double i, double v) {
    index = i;
    value = v;
  }

	/**
	* <p>
	* This method returns -1, 1 or 0 depending on the result of the
	* comparison of absolute value of second attribute of the class
	* @param o1 An object to be compared against a value of the class
	* @return An int, 1 if the absolute value of 'value' attribute of object at the left side
	* is < than the absolute value of 'value' attribute of the object at the right side, 1 if >, 0 if equal.
	* </p>
	*/
  public int compareTo (Object o1) { //ordena por valor absoluto

    if (Math.abs(this.value) > Math.abs(((Pair)o1).value))
      return -1;
    else if (Math.abs(this.value) < Math.abs(((Pair)o1).value))
      return 1;
    else return 0;
  }


}
