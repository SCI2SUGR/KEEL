/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

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

