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

package keel.Algorithms.Genetic_Rule_Learning.M5Rules;

import java.util.Vector;
import java.util.Enumeration;

/**
 * Class to implement an attribute
 */
public class MyAttribute {
  /** Continuous attribute. */
  public final static int CONTINUOUS = 0;

  /** Discret attribute. */
  public final static int DISCRET = 1;

  /** The name.*/
  private String name;

  /** The type. */
  private int type;

  /** Values of a list attribute. */
  private Vector values;

  /** The minor value of a numeric attribute. */
  private float bottom;

  /** The bigger value of a numeric attribute. */
  private float top;

  /** The index. */
  private int index;

  /** Is included in the inputs or outputs?. */
  private boolean used;

  /** Is an attribute synthetizied from an enumerate one?*/
  private boolean isEnum=false;

  /** Constructor for continuous attributes.
   *
   * @param attributeName the attribute's name.
   * @param attributeIndex the attribute's index.
   */
  public MyAttribute(String attributeName, int attributeIndex) {
    name = attributeName;
    index = attributeIndex;
    values = null;
    type = CONTINUOUS;
    used = false;
    isEnum=false;
  }

  /** Constructor for discret attributes.
   *
   * @param attributeName The name of the attribute.
   * @param attributeValues The values of the attributes.
   * @param attributeIndex The index of the attribute.
   */
  public MyAttribute(String attributeName, Vector attributeValues, int attributeIndex) {
    name = attributeName;
    index = attributeIndex;
    type = DISCRET;
    values = new Vector(attributeValues.size());
    used = false;
    isEnum=false;

    for (int i = 0; i < attributeValues.size(); i++) {
      Object store = attributeValues.elementAt(i);
      values.addElement(store);
    }
  }

  /** Constructor for continuous attributes.
   *
   * @param attributeName the attribute's name.
   */
  public MyAttribute(String attributeName) {
    name = attributeName;
    index = -1;
    values = null;
    type = CONTINUOUS;
    used = false;
    isEnum=false;
  }

  /** Constructor for discret attributes.
   *
   * @param attributeName The name of the attribute.
   * @param attributeValues The values of the attributes.
   */
  public MyAttribute(String attributeName, Vector attributeValues) {
    name = attributeName;
    index = -1;
    type = DISCRET;
    values = new Vector(attributeValues.size());
    used = false;
    isEnum=false;

    for (int i = 0; i < attributeValues.size(); i++) {
      Object store = attributeValues.elementAt(i);
      values.addElement(store);
    }
  }


  /** Function to get the index of a value in the list of values.
   *
   * @param value The value.
   *
   * @return The index of the value.
   */
  public final int valueIndex(String value) {
    int i = 0;
    if (!isDiscret()) {
      return -1;
    }

    Enumeration enum2 = values.elements();

    while (enum2.hasMoreElements()) {
      String element = (String) enum2.nextElement();

      if (element.equalsIgnoreCase(value)) {
        return i;
      }

      i++;
    }

    return -1;
  }

  /** Returns if the attribute is discret or not.
   * @return if the attribute is discret or not.
   */
  public final boolean isDiscret() {
    return (type == DISCRET);
  }

  /** Returns if the attribute is continuous or not.
   * @return if the attribute is continuous or not.
   */
  public final boolean isContinuous() {
    return (type == CONTINUOUS);
  }

  /** Returns the name of the attribute.
   * @return the name of the attribute.
   */
  public final String name() {
    return name;
  }

  /** Returns the index of the attribute.
   * @return the index of the attribute.
   */
  public final int index() {
    return index;
  }

  /** Function to get the number of values of a discret attribute.
   *
   * @return The number of values of the attribute.
   */
  public final int numValues() {
    if (!isDiscret()) {
      return 0;
    } else {
      return values.size();
    }
  }

  /** Returns the value with the given index.
   *
   * @param valIndex The index of the value.
   * @return the value with the given index.
   */
  public final String value(int valIndex) {
    if (!isDiscret()) {
      return "";
    } else {
      Object val = values.elementAt(valIndex);

      return (String) val;
    }
  }

  /**
   * Sets the index of this attribute.
   *
   * @param index the index of this attribute
   */
  final public void setIndex(int index) {

    this.index = index;
  }


  /** Sets the range of a continuous attribute.
   *
   * @param minRange The minimum value of the range.
   * @param maxRange The maximum value of the range.
   */
  final void setRange(float minRange, float maxRange) {
    if (isDiscret()) {
      throw new IllegalArgumentException("Can only set value of numeric attribute!");
    }
    else {
      bottom = minRange;
      top = maxRange;
    }
  }

  /** Sets the range of a continuous attribute.
   *
   * @param minRange The minimum value of the range.
   * @param maxRange The maximum value of the range.
   */
  final void setRange(int minRange, int maxRange) {
    if (isDiscret()) {
      throw new IllegalArgumentException("Can only set value of numeric attribute!");
    } else {
      bottom = minRange;
      top = maxRange;
    }
  }

  /** Returns the minor value of a continuous attribute.
   * @return the minor value of a continuous attribute.
   */
  public final float getMinRange() {
    if (isDiscret()) {
      throw new IllegalArgumentException("Can only set value of numeric attribute!");
    }
    else {
      return bottom;
    }
  }

  /** Gets the bigger value of a continuous attribute.
   * @return the bigger value of a continuous attribute.
   */
  public final float getMaxRange() {
    if (isDiscret()) {
      throw new IllegalArgumentException("Can only set value of numeric attribute!");
    }
    else {
      return top;
    }
  }

  /** Gets the type of the attribute (CONTINUOUS or DISCRET).
   * @return the type of the attribute (CONTINUOUS or DISCRET).
   */
  public final float getType() {
    return type;
  }


  /** Sets the attribute as used.
   *
   */
  public void activate() {
    used = true;
  }

  /** Sets the attribute as synthetizied from an enumerate one.
   *
   */
  public void enumerate() {
    isEnum = true;
  }


  /** Returns true if this attribute used in output or input clause.
   * @return true if this attribute used in output or input clause.
   */
  public boolean isActive() {
    return used;
  }

  /** Returns true if this attribute has been synthetizied from an enumerate one.
   * @return true if this attribute has been synthetizied from an enumerate one.
   */
  public boolean isEnumerate() {
    return isEnum;
  }


  /**
   * Adds an attribute value. Creates a fresh list of attribute
   * values before adding it.
   *
   * @param value the attribute value
   * @return -1 if the attribute is continuos
   */
  final int addStringValue(String value) {

    if (!isDiscret()) {
      return -1;
    }

    values.add(value);
    return 0;
  }

  /**
   * Adds an attribute value. Creates a fresh list of attribute
   * values before adding it.
   *
   * @param att the attribute
   * @param index the index of the value
   * @return -1 if the attribute is continuos
   */
  final int addValue(MyAttribute att,int index) {

    if (!isDiscret()) {
      return -1;
    }

    values.add(att.values.elementAt(index));
    return 0;
  }

  /**
   * Produces a shallow copy of this attribute.
   *
   * @return a copy of this attribute with the same index
   */
  public Object copy() {

    MyAttribute copy = new MyAttribute(name,index);

    if (!isDiscret()) {
      copy.bottom=bottom;
      copy.top=top;
      return copy;
    }
    copy.type = type;
    copy.values = values;
    copy.used=used;

    return copy;
  }

  /**
   * Tests if given attribute is equal to this attribute.
   *
   * @param other the Object to be compared to this attribute
   * @return true if the given attribute is equal to this attribute
   */
  public final boolean equals(Object other) {

    if ((other == null) || !(other.getClass().equals(this.getClass()))) {
      return false;
    }
    MyAttribute att = (MyAttribute) other;
    if (!name.equals(att.name)) {
      return false;
    }
    if (isContinuous() && att.isContinuous()) {
      if (bottom==att.bottom && top==att.top)
        return true;
      else
        return false;
    }
    if (isContinuous() || att.isContinuous()) {
      return false;
    }
    if (values.size() != att.values.size()) {
      return false;
    }
    for (int i = 0; i < values.size(); i++) {
      if (!values.elementAt(i).equals(att.values.elementAt(i))) {
        return false;
      }
    }
    return true;
  }
}

