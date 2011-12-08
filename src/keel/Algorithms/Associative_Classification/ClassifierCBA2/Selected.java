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

package keel.Algorithms.Associative_Classification.ClassifierCBA2;


/**
 * This class contains the representation of the "Selected" structure <rule, defaultClass, totalErrors>
 *
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class Selected implements Comparable{
	
  int defaultClass, totalErrors;
  Rule rule;

  /**
   * <p>
   * Default Constructor
   * </p>
   */
  public Selected() {
  }

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param rule Rule to sotre in the Selected structure
   * @param defaultClass Default class set for the rule
   * @param totalErrors Number of errors this rule has got while it was classifying
   */
  public Selected(Rule rule, int defaultClass, int totalErrors) {
	this.rule = rule;
    this.defaultClass = defaultClass;
    this.totalErrors = totalErrors;
  }

  /**
   * <p>
   * Clone function
   * </p>
   */
  public Selected clone () {
	Selected s = new Selected (this.rule, this.defaultClass, this.totalErrors);

	return (s);
  }

  /**
   * <p>
   * It returns the rule in the structure
   * </p>
   * @return Rule The rule stored in the structure
   */
  public Rule getRule () {
    return (this.rule);
  }

  /**
   * <p>
   * It sets the rule into the "selected" structure
   * </p>
   * @param rule Rule to store
   */
  public void setRule (Rule rule) {
    this.rule = rule;
  }

  /**
   * <p>
   * It returns the default class in the structure
   * </p>
   * @return int The default class in the structure
   */
  public int getDefaultClass () {
    return (this.defaultClass);
  }

  /**
   * <p>
   * It sets the default class into the "selected" structure
   * </p>
   * @param defaultClass Default class to store
   */
  public void setDefaultClass (int defaultClass) {
    this.defaultClass = defaultClass;
  }

  /**
   * <p>
   * It returns the total of errors in the structure
   * </p>
   * @return int The total of errors made by the rule
   */
  public int getTotalErrors () {
    return (this.totalErrors);
  }

  /**
   * <p>
   * It sets the total of errors made by the rule into the "selected" structure
   * </p>
   * @param totalErrors Number of errors the rule made while it was classifying examples
   */
  public void setTotalErrors (int totalErrors) {
    this.totalErrors = totalErrors;
  }

  /**
   * Function to compare objects of the Selected class
   * Necessary to be able to use "sort" function
   * It sorts in an decreasing order of total of errors
   */
  public int compareTo(Object a) {
    if ( ( (Selected) a).totalErrors < this.totalErrors) {
      return 1;
    }
    if ( ( (Selected) a).totalErrors > this.totalErrors) {
      return -1;
    }
    return 0;
  }

}

