/**
 * <p>
 * @author Administrator
 * @author Modified by Pedro Antonio Gutiérrez and Juan Carlos Fernández (University of Córdoba) 23/10/2008
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