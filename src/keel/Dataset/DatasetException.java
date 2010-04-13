/*
 * DatasetException.java
 *
 * Created on 28 de enero de 2005, 10:31
 */

package keel.Dataset;
import java.util.*;

/**
 *<p>
* <b> DatasetException </b>
 *</p>
 *
 * This class defines the exception that will be thrown if something bad
 * happens during the dataset reading.
 *
 * @author  Albert Orriols Puig
 * @version keel0.1
 */
public class DatasetException  extends Exception{
    
  private Vector errors;
  
  
/** 
 * Creates a new instance of DatasetException 
 */
  public DatasetException() {
    super();
  }//end DatasetException
    

/**
 * Does instance a new DatasetException with the message
 * specified and the Vector with all the errors.
 * @param msg is the message of the exception
 * @param _errors is a vector with all the errors.
 */
  public DatasetException(String msg, Vector _errors){
    super(msg);
    errors = _errors;
  }//end DatasetException
  
/**
 * Sets the vector with the errors.
 * @param _errors is the vector with the errors.
 */
  public void setLogger(Vector _errors){
      errors = _errors;
  }//end setLogger

  
/**
 * Gets the vector with the errors.
 * @return a Vector with all the errors. 
 */
  public Vector getLogger(){
    return errors;
  }//end getLogger
  

/**
 * It does print all the errors. 
 */
  public void printAllErrors(){
    System.out.println ("\n\n------------------------------------------");
    System.out.println ("           ERRORS FOUNDS IN DATASETS          ");
    System.out.println ("------------------------------------------");
    for (int i=0; i<errors.size(); i++){
        System.out.println (">>>ERROR "+i);
        ((ErrorInfo)errors.elementAt(i)).print();
        System.out.println ("------------------------------------------");
    }
    System.out.println ("------------------------------------------");
  }//end printAllErrors
  
}//end DatasetException
