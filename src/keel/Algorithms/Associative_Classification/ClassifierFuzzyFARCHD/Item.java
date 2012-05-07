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

package keel.Algorithms.Associative_Classification.ClassifierFuzzyFARCHD;

/**
 * <p>Title: Item</p>
 *
 * <p>Description: This class contains the representation of a item</p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Jesus Alcalá (University of Granada) 09/02/2011
 * @version 1.0
 * @since JDK1.6
 */

import java.util.*;


public class Item implements Comparable {
  int variable, value;

  public Item() {
  }

/**
* <p>
* Builder
* </p>
* @param variable Variable
* @param value Value or Label of the variable
* @return Return the item variable/value
*/
  public Item(int variable, int value) {
	  this.variable = variable;
	  this.value = value;
  }

  public void setValues (int variable, int value) {
	  this.variable = variable;
	  this.value = value;
  }

  public int getVariable () {
	  return (this.variable);
  }

  public int getValue () {
	  return (this.value);
  }

  public Item clone(){
    Item d = new Item();
    d.variable = this.variable;
    d.value = this.value;

	return d;
  }

  public boolean isEqual(Item a) {
	  if ((this.variable == a.variable) && (this.value == a.value))  return (true);
	  else  return (false);
  }


  public int compareTo(Object a) {
    if (((Item) a).variable > this.variable) {
      return -1;
    }
    else if (((Item) a).variable < this.variable) {
      return 1;
    }
    else if (((Item) a).value > this.value) {
      return -1;
    }
    else if (((Item) a).value < this.value) {
      return 1;
    }

    return 0;
  }

}
