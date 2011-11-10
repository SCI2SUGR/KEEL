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
 * CheckException.java
 *
 * Created on 4 de febrero de 2005, 23:50
 */

package keel.Algorithms.Preprocess.Basic;

/**
 *<p>
* <b> CheckException </b>
 *</p>
 *
 * This class defines the exception that will be thrown if the
 * dataset not corresponding with classification
 *
 * @author  Salvador García López
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
 *
 */
  public CheckException(String msg){
    super(msg);
  }//end ChecktException

}//end CheckException

