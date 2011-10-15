/*
 * CheckException.java
 *
 * Created on 4 de febrero de 2005, 23:50
 */

package keel.Algorithms.ImbalancedClassification.Ensembles.Basic;

/**
 *<p>
* <b> CheckException </b>
 *</p>
 *
 * This class defines the exception that will be thrown if the
 * dataset not corresponding with classification
 *
 * @author  Salvador Garc�a L�pez
 * @version keel0.1
 */
public class CheckException  extends Exception{


/**
 * Creates a new instance of CheckException
 */
  public CheckException() {
    super();
  }//end CheckException


/**
 * Does instance a new CheckException with the message
 * specified and the Vector with all the errors.
 * @param msg is the message of the exception
 * @param _errors is a vector with all the errors.
 */
  public CheckException(String msg){
    super(msg);
  }//end ChecktException

}//end CheckException
