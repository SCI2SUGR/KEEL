package keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets;

import java.util.Properties;
import java.util.Map;
import java.util.Enumeration;
import java.io.InputStream;

/**
 * Simple class that extends the Properties class so that the properties are
 * unable to be modified.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 1.1 $
 */
public class ProtectedProperties extends Properties {

  // the properties need to be open during construction of the object
  private boolean closed = false;

  /**
   * Creates a set of protected properties from a set of normal ones.
   *
   * @param props the properties to be stored and protected.
   */
  public ProtectedProperties(Properties props)
  {

    Enumeration propEnum = props.propertyNames();
    while (propEnum.hasMoreElements()) {
      String propName = (String) propEnum.nextElement();
      String propValue = props.getProperty(propName);
      super.setProperty(propName, propValue);
    }
    closed = true; // no modifications allowed from now on
  }

  /**
   * Overrides a method to prevent the properties from being modified.
   *
   * @return never returns without throwing an exception.
   * @exception UnsupportedOperationException always.
   */
  public Object setProperty(String key, String value)
    {
    
    if (closed) 
      throw new
	UnsupportedOperationException("ProtectedProperties cannot be modified!");
    else return super.setProperty(key, value);
  }

  /**
   * Overrides a method to prevent the properties from being modified. inStream returns without throwing an exception.
   * @exception UnsupportedOperationException always.
   */  
  public void load(InputStream inStream) {
    
    throw new
      UnsupportedOperationException("ProtectedProperties cannot be modified!");
  }

  /**
   * Overrides a method to prevent the properties from being modified.
   *
   * @exception UnsupportedOperationException always.
   */
  public void clear() {
    
    throw new
      UnsupportedOperationException("ProtectedProperties cannot be modified!");
  }

  /**
   * Overrides a method to prevent the properties from being modified.
   *
   * @return never returns without throwing an exception.
   * @exception UnsupportedOperationException always.
   */
  public Object put(Object key,
		    Object value) {

    if (closed) 
      throw new
	UnsupportedOperationException("ProtectedProperties cannot be modified!");
    else return super.put(key, value);
  }

  /**
   * Overrides a method to prevent the properties from being modified.
   *
   * @param t never returns without throwing an exception.
   * @exception UnsupportedOperationException always.
   */
  public void putAll(Map t) {
    
    throw new
      UnsupportedOperationException("ProtectedProperties cannot be modified!");
  }

  /**
   * Overrides a method to prevent the properties from being modified.
   *
   * @return never returns without throwing an exception.
   * @exception UnsupportedOperationException always.
   */
  public Object remove(Object key) {

    throw new
      UnsupportedOperationException("ProtectedProperties cannot be modified!");
  }

}

