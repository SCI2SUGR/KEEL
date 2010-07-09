package keel.Algorithms.Associative_Classification.ClassifierCBA;

/**
 * <p>Title: Selected</p>
 *
 * <p>Description: This class contains the representation of the "Selected" structure <rule, defaultClass, totalErrors></p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */

public class Selected implements Comparable{
	/**
	 * This class contains the representation of the "Selected" structure <rule, defaultClass, totalErrors>
	 */
  int defaultClass, totalErrors;
  Rule rule;

  /**
   * <p>
   * Default Constructor
   * </p>
   */
  public Selected() {
  }

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param rule Rule Rule to sotre in the Selected structure
   * @param defaultClass int Default class set for the rule
   * @param totalErrors int Number of errors this rule has got while it was classifying
   */
  public Selected(Rule rule, int defaultClass, int totalErrors) {
	this.rule = rule;
    this.defaultClass = defaultClass;
    this.totalErrors = totalErrors;
  }

  /**
   * <p>
   * Clone function
   * </p>
   */
  public Selected clone () {
	Selected s = new Selected (this.rule, this.defaultClass, this.totalErrors);

	return (s);
  }

  /**
   * <p>
   * It returns the rule in the structure
   * </p>
   * @return Rule The rule stored in the structure
   */
  public Rule getRule () {
    return (this.rule);
  }

  /**
   * <p>
   * It sets the rule into the "selected" structure
   * </p>
   * @param rule Rule Rule to store
   */
  public void setRule (Rule rule) {
    this.rule = rule;
  }

  /**
   * <p>
   * It returns the default class in the structure
   * </p>
   * @return int The default class in the structure
   */
  public int getDefaultClass () {
    return (this.defaultClass);
  }

  /**
   * <p>
   * It sets the default class into the "selected" structure
   * </p>
   * @param defaultClass int Default class to store
   */
  public void setDefaultClass (int defaultClass) {
    this.defaultClass = defaultClass;
  }

  /**
   * <p>
   * It returns the total of errors in the structure
   * </p>
   * @return int The total of errors made by the rule
   */
  public int getTotalErrors () {
    return (this.totalErrors);
  }

  /**
   * <p>
   * It sets the total of errors made by the rule into the "selected" structure
   * </p>
   * @param int totalErrors Number of errors the rule made while it was classifying examples
   */
  public void setTotalErrors (int totalErrors) {
    this.totalErrors = totalErrors;
  }

  /**
   * Function to compare objects of the Selected class
   * Necessary to be able to use "sort" function
   * It sorts in an decreasing order of total of errors
   */
  public int compareTo(Object a) {
    if ( ( (Selected) a).totalErrors < this.totalErrors) {
      return 1;
    }
    if ( ( (Selected) a).totalErrors > this.totalErrors) {
      return -1;
    }
    return 0;
  }

}
