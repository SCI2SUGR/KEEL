package keel.Algorithms.Associative_Classification.ClassifierCPAR;

/**
 * <p>Title: Literal</p>
 *
 * <p>Description: This class contains the representation of a item with its gain</p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */


public class Literal implements Comparable{
	/**
	 * <p>
	 * Class to store a Literal.
	 * It is almost the same as an Item, but it also stores the item's gain,
	 * which is calculated by the FOIL method.
	 * </p>
	 */
  int variable, value;
  double gain;

  /**
   * <p>
   * Default Constructor
   * </p>
   */
  public Literal() {
  }

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param variable int Attribute of the literal
   * @param value int Associated value to an attribute
   */
  public Literal(int variable, int value) {
	  this.variable = variable;
	  this.value = value;
	  this.gain = 0.0;
  }

  /**
   * <p>
   * It sets the values for a literal
   * </p>
   * @param variable int Attribute of the literal
   * @param value int Associated value to an attribute
   */
  public void setValues (int variable, int value) {
	  this.variable = variable;
	  this.value = value;
  }

  /**
   * <p>
   * It sets the gain for a literal
   * </p>
   * @param double value Gain to set
   */
  public void setGain (double value) {
	  this.gain = value;
  }

  /**
   * <p>
   * It returns the attribute stored in the literal
   * </p>
   * @return The attribute stored in the literal
   */
  public int getVariable () {
	  return (this.variable);
  }

  /**
   * <p>
   * It returns the value of the attribute stored in the literal
   * </p>
   * @return The value of the attribute attribute stored in the literal
   */
  public int getValue () {
	  return (this.value);
  }

  /**
   * <p>
   * It returns the gain of the literal
   * </p>
   * @return The gain of the literal
   */
  public double getGain () {
	  return (this.gain);
  }

  /**
   * <p>
   * Clone Function
   * </p>
   */
  public Literal clone(){
    Literal d = new Literal();
    d.variable = this.variable;
    d.value = this.value;
    d.gain = this.gain;

	return d;
  }

  /**
   * <p>
   * Function neccessary to sort literals
   * It sorts in a decreasing gain order
   * </p>
   */
  public int compareTo(Object a) {
    if ( ( (Literal) a).gain < this.gain) {
      return -1;
    }
    if ( ( (Literal) a).gain > this.gain) {
      return 1;
    }
    return 0;
  }
}
