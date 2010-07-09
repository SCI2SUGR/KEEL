package keel.Algorithms.Associative_Classification.ClassifierFuzzyFCRA;

/**
 * <p>Title: Selected</p>
 *
 * <p>Description: This class contains the representation to select rules</p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Jesus Alcala (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */

public class Selected implements Comparable{
  double probability;
  int post;

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param porb double Probability
   * @param pos int Position
   */
  public Selected(double prob, int pos) {
    this.probability = prob;
    this.post = pos;
  }

  /**
   * Function to compare objects of the Selected class
   * Necessary to be able to use "sort" function
   * It sorts in an increasing order of probability
   */
  public int compareTo(Object a) {
    if ( ( (Selected) a).probability < this.probability) {
      return -1;
    }
    if ( ( (Selected) a).probability > this.probability) {
      return 1;
    }
    return 0;
  }

}
