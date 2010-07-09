/**
 * <p>
 * @author Written by Pedro González (University of Jaen) 15/08/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.NMEEFSD.Calculate;

import java.util.*;

public class Utils {
    
    /**
     * <p>
     * Assorted methods to manage several topics
     * </p>
     */

    /**
     * <p>
     * Returns the position of the element at the vector, -1 if does not appear
     * </p>
     * @param vvalue      Vector with values
     * @param value             The value to search
     * @return                  Position of the value in the the vvalue
     */
    public static int getPosString (Vector vvalue, String value) {
        for (int i=0;  i<vvalue.size(); i++)
            if (vvalue.elementAt(i).equals(value))
                return (i);
        return (-1);
    }

    
    /**
     * <p>
     * Returns the minimum of two float values
     * </p>
     * @param x     Some float
     * @param y     Some float
     * @return      The float with the minimum value
     */
    public static float Minimum (float x, float y) {
        if (x<y)
            return (x);
        else
            return (y);
    }

    /**
     * <p>
     * Returns the maximum of two float values
     * </p>
     * @param x     Some float
     * @param y     Some float
     * @return      The float with the maximum value
     */
    public static float Maximum (float x, float y) {
        if (x>y)
            return (x);
        else
            return (y);
    }


    /**
     * <p>
     * Returns if the first float argument is better than the second
     * </p>
     * @param X     Some float
     * @param Y     Some float
     * @return      True if X is better than Y and false in other way
     */
    public static boolean BETTER (float X, float Y) {
        if (X > Y) return true;
        else return false;
    }

    /**
     * <p>
     * Returns if the first integer argument is better than the second
     * </p>
     * @param X     Some integer
     * @param Y     Some integer
     * @return      True if X is better than Y and false in other way
     */
    public static boolean BETTER (int X, int Y) {
        if (X > Y) return true;
        else return false;
    }

    /**
     * <p>
     * Returns if the first double argument is better than the second
     * </p>
     * @param X     Some double
     * @param Y     Some double
     * @return      True if X is better than Y and false in other way
     */
    public static boolean BETTER (double X, double Y) {
        if (X > Y) return true;
        else return false;
    }

  
    /**
     * <p>
     * C.A.R, Hoare Quick sort. Based on sort by interchange. Decreasing sort.
     * </p>
     * @param v             Vector to be sorted
     * @param left          Position to sort
     * @param right         Final position to sort
     * @param index         The indexes of the original vector
     */
    public static void OrDecIndex (float v[], int left, int right, int index[])  {
        int i,j,aux;
        float x,y;
        
        i = left;
        j = right;
        x = v[(left+right)/2];
        do {
            while (v[i]>x && i<right)
                i++;
            while (x>v[j] && j>left)
                j--;
            if (i<=j) {
                y = v[i];
                v[i] = v[j];
                v[j] = y;
                aux = index[i];
                index[i] = index[j];
                index[j] = aux;
                i++;
                j--;
            }
        } while(i<=j);
        if (left<j)
            OrDecIndex (v,left,j,index);
        if (i<right)
            OrDecIndex (v,i,right,index);
        
    }
    
    
    /**
     * <p>
     * C.A.R, Hoare Quick sort. Based on sort by interchange. Incresing sort.
     * </p>
     * @param v		Vector to be sorted
     * @param left	Initial position to sort
     * @param right	Final position to sort
     * @param index	The indexes of the original vector
     */
    public static void OrCrecIndex (float v[], int left, int right, int index[])  {
        int i,j,aux;
        float x,y;
        
        i = left;
        j = right;
        x = v[(left+right)/2];
        do {
            while (v[i]<x && i<right)
                i++;
            while (x<v[j] && j>left)
                j--;
            if (i<=j) {
                y = v[i];
                v[i] = v[j];
                v[j] = y;
                aux = index[i];
                index[i] = index[j];
                index[j] = aux;
                i++;
                j--;
            }
        } while(i<=j);
        if (left<j)
            OrCrecIndex (v,left,j,index);
        if (i<right)
            OrCrecIndex (v,i,right,index);
    }


    /**
     * <p>
     * Rounds the generated value for the semantics when necesary
     * </p>
     * @param val       The value to round
     * @param tope
     * @return 
     */
    public static float Assigned (float val, float tope) {
        if (val>-0.0001 && val<0.0001) 
            return (0);
        if (val>tope-0.0001 && val<tope+0.0001) 
            return (tope);
        return (val);
    }


   /**
    * <p>
    * Counts the number of examples of the DataSet belonging to the number of the class indicated
    * </p>
    * @param num_clase      The number of the objective class
    * @return               The number of examples of the class
    **/ 
    public static int ExamplesClass (int num_class) {

        int num=0;
        
        for (int i=0; i<StCalculate.n_eje; i++) {
            // If the example class is the target class, increase the number of examples of the target class
            if (StCalculate.tabla[i].clase == num_class)
                num++;
        }
        
        return num;
        
    }
    
}
