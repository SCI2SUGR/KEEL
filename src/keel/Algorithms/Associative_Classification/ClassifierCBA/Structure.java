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

package keel.Algorithms.Associative_Classification.ClassifierCBA;

/**
 * This class contains the representation of the structure <dID, y, cRule, wRule>.
 *
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */

public class Structure {
	
  int dID, y;
  int cRule, wRule;

  /**
   * <p>
   * Default Constructor
   * </p>
   */
  public Structure() {
  }

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param dID Position of the correctly classified example
   * @param y Class of the "dID" example
   * @param cRule Position in the rule set for the rule that correctly classifies the "dID" instance
   * @param wRule Position in the rule set for the first rule that wrongly classifies the "dID" instance
   */
  public Structure(int dID, int y, int cRule, int wRule) {
    this.dID = dID;
    this.y = y;
	this.cRule = cRule;
	this.wRule = wRule;
  }

  /**
   * <p>
   * It returns the position in the training dataset for the example stored in the structure
   * </p>
   * @return int Position in the training dataset for the example stored in the structure
   */
  public int getdID () {
    return (this.dID);
  }

  /**
   * <p>
   * It sets in the structure the position in the training dataset of the wanted example
   * </p>
   * @param dID Position in the training dataset of the wanted example
   */
  public void setdID (int dID) {
    this.dID = dID;
  }

  /**
   * <p>
   * It returns the class for the example stored in the structure
   * </p>
   * @return int Class for the example stored in the structure
   */
  public int gety () {
    return (this.y);
  }

  /**
   * <p>
   * It sets in the structure the class of the example
   * </p>
   * @param y Class of the example
   */
  public void sety (int y) {
    this.y = y;
  }

  /**
   * <p>
   * It returns the position of the best rule that correctly classifies the example stored in the structure
   * </p>
   * @return int Position of the best rule that correctly classifies the example stored in the structure
   */
  public int getcRule () {
    return (this.cRule);
  }

  /**
   * <p>
   * It sets the position of the best rule that correctly classifies the example stored in the structure
   * </p>
   * @param cRule Position of the best rule that correctly classifies the example stored in the structure
   */
  public void setcRule (int cRule) {
    this.cRule = cRule;
  }

  /**
   * <p>
   * It returns the position of the first rule that wrongly classifies the example stored in the structure
   * </p>
   * @return int Position of the first rule that wrongly classifies the example stored in the structure
   */
  public int getwRule () {
    return (this.wRule);
  }

  /**
   * <p>
   * It sets the position of the first rule that wrongly classifies the example stored in the structure
   * </p>
   * @param wRule Position of the first rule that wrongly classifies the example stored in the structure
   */
  public void setwRule (int wRule) {
    this.wRule = wRule;
  }

}

