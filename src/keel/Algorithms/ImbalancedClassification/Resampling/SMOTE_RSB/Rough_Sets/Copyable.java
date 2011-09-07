package keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets;


/**
 * Interface implemented by classes that can produce "shallow" copies
 * of their objects. (As opposed to clone(), which is supposed to
 * produce a "deep" copy.)
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version $Revision: 1.1 $
 */
public interface Copyable {

  /**
   * This method produces a shallow copy of an object.
   * It does the same as the clone() method in Object, which also produces
   * a shallow copy.
   */
  Object copy();
}
