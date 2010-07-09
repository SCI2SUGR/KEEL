/**
 * <p>
 * @author Written by Cristóbal J. Carmona (University of Jaen) 11/08/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package NMEEFSD;

public class Fuzzy {
    /**
     * <p>
     * Values for a fuzzy set definition
     * </p>
     */

     private float x0,x1,x3;
     private float y;


    /**
     * <p>
     * Methods to get the value of x0
     * </p>
     * @return          Value of x0
     */
    public float getX0 () {
        return x0;
    }

    /**
     * <p>
     * Methods to get the value of x1
     * </p>
     * @return          Value of x1
     */
    public float getX1 () {
        return x1;
    }

    /**
     * <p>
     * Methods to get the value of x3
     * </p>
     * @return          Value of x3
     */
    public float getX3 () {
        return x3;
    }


    /**
     * <p>
     * Method to set the values of x0, x1 y x3
     * </p>
     * @param vx0       Value for x0
     * @param vx1       Value for x1
     * @param vx3       Value for x3
     * @param vy        Value for y
     */
    public void setVal (float vx0, float vx1, float vx3, float vy) {
        x0 = vx0;
        x1 = vx1;
        x3 = vx3;
        y  = vy;
    }


    /**
     * <p>
     * Returns the belonging degree
     * </p>
     * @param X             Value to obtain the belonging degree
     * @return              Belonging degree
     */
    public float Fuzzy (float X) {
        if ((X<=x0) || (X>=x3))  // If value of X is not into range x0..x3
            return (0);          // then pert. degree = 0
        if (X<x1)
            return ((X-x0)*(y/(x1-x0)));
        if (X>x1)
            return ((x3-X)*(y/(x3-x1)));
        return (y);
    }


    /**
     * <p>
     * Creates a new instance of Fuzzy
     * </p>
     */
    public Fuzzy() {
    }

}
