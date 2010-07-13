/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S·nchez (luciano@uniovi.es)
    J. Alcal·-Fdez (jalcala@decsai.ugr.es)
    S. GarcÌa (sglopez@ujaen.es)
    A. Fern·ndez (alberto.fernandez@ujaen.es)
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

/**
 * <p>
 * @author Administrator
 * @author Modified by Pedro Antonio Guti√©rrez and Juan Carlos Fern√°ndez (University of C√≥rdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package keel.GraphInterKeel.datacf.util;

public class Attribute {

    /**
     * <p>
     * Class representing an attribute in a dataset
     * </p>
     */

    /** Constant representing an integer attribute */
    static final int integer = 0;

    /** Constant representing a real attribute */
    static final int real = 1;

    /** Constant representing a nominal attribute */
    static final int nominal = 2;

    /** Private variables */
    private int type;
    private String min;
    private String max;
    private String[] nominals;

    /**
     * <p>
     * Constructor with attribute type and min and max bounds
     * </p>
     * @param type Type of Attribute
     * @param min Min value
     * @param max Max value
     */
    public Attribute(int type, String min, String max) {
        this.type = type;
        this.min = new String(min);
        this.max = new String(max);
    }

    /**
     * <p>
     * Constructor with attribute type
     * </p>
     * @param type Type of Attribute
     */
    public Attribute(int type) {
        this.type = type;
        this.max = new String();
        this.min = new String();
    }

    /**
     * <p>
     * Constructor with attribute type and nominal values
     * </p>
     * @param type Type of Attributes
     * @param nominalValues Array of values
     */
    public Attribute(int type, String[] nominalValues) {
        this.type = type;
        nominals = new String[nominalValues.length];
        for (int i = 0; i < nominalValues.length; i++) {
            nominals[i] = nominalValues[i];
        }
    }

    /**
     * <p>
     * Return a boolean for a given nominal values, true is valid value,
     * false is an invalid value.
     * </p>
     * @param value Value
     * @return true or false, valid or invalid value
     */
    public boolean isValid(String value) {
        if (type == integer) {
            return integerValid(value);
        } else if (type == real) {
            return realValid(value);
        } else {
            return nominalValid(value);
        }
    }

    /**
     * <p>
     * Return a boolean for a given int value,
     * true is valid value, false invalid value.
     * </p>
     * @param value Value
     * @return true or false, valid or invalid value
     */
    public boolean integerValid(String value) {
        if (value.equals("null")) {
            return (true);
        }

        if (min.length() == 0 && max.length() == 0) {
            return true;
        }

        try {
            Integer number = Integer.valueOf(value);
            if (!min.equals("")) {
                Integer minimum = Integer.valueOf(min);
                if (number.intValue() < minimum.intValue()) {
                    return (false);
                }
            }

            if (!max.equals("")) {
                Integer maximum = Integer.valueOf(max);
                if (number.intValue() > maximum.intValue()) {
                    return (false);
                }
            }
        } catch (Exception e) {
            return (false);
        }

        return (true);
    }

    /**
     * <p>
     * Return a boolean for a given real value,
     * true is valid value, false invalid value.
     * </p>
     * @param value Value
     * @return true or false, valid or invalid value
     */
    public boolean realValid(String value) {
        if (value.equals("null")) {
            return (true);
        }

        try {
            Float number = Float.valueOf(value);
            if (!min.equals("")) {
                Float minimum = Float.valueOf(min);
                if (number.floatValue() < minimum.floatValue()) {
                    return (false);
                }
            }

            if (!max.equals("")) {
                Float maximum = Float.valueOf(max);
                if (number.floatValue() > maximum.floatValue()) {
                    return (false);
                }
            }
        } catch (Exception e) {
            return (false);
        }
        return (true);
    }

    /**
     * <p>
     * Return a boolean for a given nominal value,
     * true is valid value, false invalid value.
     * </p>
     * @param value Value
     * @return true or false, valid or invalid value
     */
    public boolean nominalValid(String value) {
        if (nominals.length == 0 || value.equals("null")) {
            return (true);
        }

        for (int i = 0; i < nominals.length; i++) {
            if (nominals[i].equals(value)) {
                return (true);
            }
        }
        return (false);
    }
}
