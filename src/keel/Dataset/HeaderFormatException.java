/*
 * HeaderFormatException.java
 *
 * Created on 28 de enero de 2005, 12:34
 */

package keel.Dataset;

/**
 * <p>
 * <b> HeaderFormatException </b>
 * </p>
 * Exception thrown when the header is not in the correct format
 *
 * @author  Albert Orriols Puig
 * @version keel0.1
 */

public class HeaderFormatException extends Exception{
    
/** 
 * Creates a new instance of HeaderFormatException 
 */
  public HeaderFormatException() {
    super();
  }//end HeaderFormatException
 
  
  public HeaderFormatException(String msg){
      super(msg);
  }//end HeaderFormatException
  
}//end of Class HeaderFormatException
