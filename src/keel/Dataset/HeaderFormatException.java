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

