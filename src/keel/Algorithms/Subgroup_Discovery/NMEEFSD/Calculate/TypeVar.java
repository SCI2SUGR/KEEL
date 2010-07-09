/**
 * <p>
 * @author Written by Pedro González (University of Jaen) 15/08/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.NMEEFSD.Calculate;

import java.util.Vector;

public class TypeVar {
    /**
     * <p>
     * Class defined to store the attributes characteristics
     * </p>
     */

    String nombre;           // Name of the variable stored in the dataset
    char tipoDato;           // 'i': integer, 'r':real, 'e':enumerated
    boolean continua;        // true: continuous, false: discrete
    Vector valores;          // type "i" or "r": range of real values
                             // type "e": list of valid values
    int n_etiq;              // Number of labels (continuous vars) or 
    			     //    values (discrete vars)
    float min, max;          // Values for the min and max valid values. 

    /**
     * <p>
     * Creates a new instance of TypeVar
     * </p>
     */
    public TypeVar() {
    }
    
}

