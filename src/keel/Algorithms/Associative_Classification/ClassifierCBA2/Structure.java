package keel.Algorithms.Associative_Classification.ClassifierCBA2;

/**
 * <p>Title: Structure</p>
 *
 * <p>Description: This class contains the representation of the structure <dID, y, cRule, wRule></p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */

public class Structure {
	/**
	 * <p>
	 * This class contains the representation of the structure <dID, y, cRule, wRule>
	 * </p>
	 */
  int dID, y;
  int cRule, wRule;

  /**
   * <p>
   * Default Constructor
   * </p>
   */
  public Structure() {
  }

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param dID int Position of the correctly classified example
   * @param y int Class of the "dID" example
   * @param cRule int Position in the rule set for the rule that correctly classifies the "dID" instance
   * @param wRule int Position in the rule set for the first rule that wrongly classifies the "dID" instance
   */
  public Structure(int dID, int y, int cRule, int wRule) {
    this.dID = dID;
    this.y = y;
	this.cRule = cRule;
	this.wRule = wRule;
  }

  /**
   * <p>
   * It returns the position in the training dataset for the example stored in the structure
   * </p>
   * @return int Position in the training dataset for the example stored in the structure
   */
  public int getdID () {
    return (this.dID);
  }

  /**
   * <p>
   * It sets in the structure the position in the training dataset of the wanted example
   * </p>
   * @param dID int Position in the training dataset of the wanted example
   */
  public void setdID (int dID) {
    this.dID = dID;
  }

  /**
   * <p>
   * It returns the class for the example stored in the structure
   * </p>
   * @return int Class for the example stored in the structure
   */
  public int gety () {
    return (this.y);
  }

  /**
   * <p>
   * It sets in the structure the class of the example
   * </p>
   * @param y int Class of the example
   */
  public void sety (int y) {
    this.y = y;
  }

  /**
   * <p>
   * It returns the position of the best rule that correctly classifies the example stored in the structure
   * </p>
   * @return int Position of the best rule that correctly classifies the example stored in the structure
   */
  public int getcRule () {
    return (this.cRule);
  }

  /**
   * <p>
   * It sets the position of the best rule that correctly classifies the example stored in the structure
   * </p>
   * @param cRule int Position of the best rule that correctly classifies the example stored in the structure
   */
  public void setcRule (int cRule) {
    this.cRule = cRule;
  }

  /**
   * <p>
   * It returns the position of the first rule that wrongly classifies the example stored in the structure
   * </p>
   * @return int Position of the first rule that wrongly classifies the example stored in the structure
   */
  public int getwRule () {
    return (this.wRule);
  }

  /**
   * <p>
   * It sets the position of the first rule that wrongly classifies the example stored in the structure
   * </p>
   * @param cRule int Position of the first rule that wrongly classifies the example stored in the structure
   */
  public void setwRule (int wRule) {
    this.wRule = wRule;
  }

}
