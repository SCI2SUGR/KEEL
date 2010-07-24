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

/**
 * <p>
 * @author Created by Pedro González (University of Jaen) 18/02/2004
 * @author Modified by Pedro González (University of Jaen) 4/08/2007
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 30/06/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.MESDIF.MESDIF;

import java.util.*;

public class Utils {
    /**
     * <p>
     * Assorted methods to manage several topics
     * </p>
     */


    /**
     * <p>
     * Gets an integer from param file, skiping "="
     * </p>
     * @param s     Token
     * @return      Integer value of the token
     */
    public static int GetParamInt (StringTokenizer s) {
        String val = s.nextToken(); // skip "="
        val = s.nextToken();
        return Integer.parseInt(val);
    }

    /**
     * <p>
     * Gets an float from param file, skiping "="
     * </p>
     * @param s     Token
     * @return      Float value of the token
     */
    public static float GetParamFloat (StringTokenizer s) {
        String val = s.nextToken(); // skip "="
        val = s.nextToken();
        return Float.parseFloat(val);
    }

    /**
     * <p>
     * Gets an String from param file, skiping "="
     * </p>
     * @param s     Token
     * @return      String value of the token
     */
    public static String GetParamString(StringTokenizer s) {
        String contenido = "";
        String val = s.nextToken(); // skip "="
        do {
            if (!s.hasMoreTokens()) break;
            contenido += s.nextToken() + " ";
        } while(true);
        contenido = contenido.trim();
        return contenido;
    }

    /**
     * <p>
     * Gets the name for the file, eliminating "" and skiping "="
     * </p>
     * @param s     Token
     * @return      The name of the file
     */
    public static String GetFileName(StringTokenizer s) {
        String val = s.nextToken(); // skip "="
        val = s.nextToken();
        val = val.replace('"',' ').trim();
        return val;  // Only takes first name, second is ignored
    }


    /**
     * <p>
     * Returns the position of the element at the vector, -1 if does not appear
     * </p>
     * @param vect_valores  Vector of values
     * @param value         Value to seek
     * @return              Position of the value searched
     */
    public static int getposString (Vector vect_valores, String value ) {
        for (int i=0;  i<vect_valores.size(); i++)
            if (vect_valores.elementAt(i).equals(value))
                return (i);
        return (-1);
    }


    /**
     * <p>
     * Returns the minimum of two float values
     * </p>
     * @param x         A float
     * @param y         A float
     * @return          The minimum float in the comparison
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
     * @param x         A float
     * @param y         A float
     * @return          The maximum float in the comparison
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
     * @param v            Vector to be sorted
     * @param left         Position to sort
     * @param right        Final position to sort
     * @param index        The indexes of the original vector
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
     */
    public static float Assigned (float val, float tope) {
        if (val>-0.0001 && val<0.0001)
            return (0);
        if (val>tope-0.0001 && val<tope+0.0001)
            return (tope);
        return (val);
    }


}
