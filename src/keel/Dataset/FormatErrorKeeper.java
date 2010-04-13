/*
 * Created on 28 de enero de 2005, 8:57
 */

package keel.Dataset;
import java.util.*;

/**
 * <p>
 * <b> FormatErrorKeeper </b>
 * </p>
 * This class is a warehouse of format dataset errors. All the errors are stored in this
 * class, identifying each error by an identifier. At the end of a run, if there has been
 * some error, an exception is throws, from which the FormatErrorKeeper can be recovered.
 *
 * @author Albert Orriols Puig
 * @version keel0.1
 */

public class FormatErrorKeeper {
  
/**
 * A vector where all the errors are stored
 */
  private Vector errors;
    
/** 
 * Creates a new instance of FormatErrorKeeper 
 */
  public FormatErrorKeeper() {
      errors = new Vector();
  }//end FormatErrorKeeper
    
  
/**
 * Adds one error
 * @param er is the Error to be added.
 */
  public void setError(ErrorInfo er){
      errors.add(er);
  }//end setError
  
  
/**
 * Return the information about one error.
 * @param i is the error that is wanted to be returned.
 * @return an ErrorInfo object with the error information.
 */
  public ErrorInfo getError(int i){
    return (ErrorInfo)errors.elementAt(i);
  }//end ErrorInfo
  
/**
 * Returns the number of errors.
 * @return an int with the number of errors.
 */
  public int getNumErrors(){
      return errors.size();
  }//end getNumErrors
  
/**
 * It does return all the errors
 */
  public Vector getAllErrors(){
    return errors;
  }//end getAllErrors

/**
 * Initializes the error vector
 */
  public void init(){
    errors = new Vector();
  }//end init
}//end Class FormatErrorKeeper
