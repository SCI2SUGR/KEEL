package keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets;


import java.util.Enumeration;

/** 
 * Interface to something that understands options.
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version $Revision: 1.1 $
 */
public interface OptionHandler {

  /**
   * Returns an enumeration of all the available options..
   *
   * @return an enumeration of all available options.
   */
  Enumeration listOptions();

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @exception Exception if an option is not supported
   */
  //@ requires options != null;
  //@ requires \nonnullelements(options);
  void setOptions(String[] options) throws Exception;

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  //@ ensures \result != null;
  //@ ensures \nonnullelements(\result);
  /*@pure@*/ String[] getOptions();
}








