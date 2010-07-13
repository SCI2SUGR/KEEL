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

package keel.Algorithms.Instance_Generation.utilities;

import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.*;
import java.util.*;

/**
 * Distance measurer between prototypes.
 * @author diegoj
 */
public class Distance implements Comparator<Prototype>
{
    /** Base prototype of the sortings. */
    protected static Prototype basePrototype = null;
    
    /** Number of inputs of the prototypes (used to optimize calculations). */
    protected static int numberOfInputs = 0;

    /**
     * Assigns the number of inputs of the prototypes.
     * @param _n Number of inputs of the prototypes.
     */
    public static void setNumberOfInputs(int _n)
    {
        numberOfInputs = _n;
    }

    /**
     * Assigns base prototype. That is, prototype to compare in sortings and other operations.
     * @param p New base prototype.
     */
    public void setPrototypeToCompare(Prototype p)
    {
        basePrototype = p;
    }

    /**
     * Construct a new Distance object.
     * @param p Base prototype.
     */
    public Distance(Prototype p)
    {
        basePrototype = p;
    }

    /**
     * Compute the squared euclidean distance between two prototypes.
     * @param one One prototype.
     * @param two Other prototype.
     * @return squared euclidean distance between one and two.
     */
    public static double squaredEuclideanDistance(Prototype one, Prototype two)
    {
        final double[] oneInputs = one.getInputs();
        final double[] twoInputs = two.getInputs();
        //final int _size = one.numberOfInputs();
        double acc = 0.0;
        for (int i = 0; i < numberOfInputs; i++)
        {
            acc += (oneInputs[i] - twoInputs[i]) * (oneInputs[i] - twoInputs[i]);
        }
        return acc;
    }

    /**
     * Compute the Squared euclidean distance between two prototypes.
     * @param one One prototype.
     * @param two Other prototype.
     * @return squared euclidean distance between one and two.
     */
    public static double dSquared(Prototype one, Prototype two)
    {
        return squaredEuclideanDistance(one, two);
    }

    /**
     * Compute the Euclidean Distance between two prototypes.
     * @param one One prototype.
     * @param two Other prototype.
     * @return Euclidean Distance between one and two.
     */
    public static double d(Prototype one, Prototype two)
    {
        return Math.sqrt(squaredEuclideanDistance(one, two));
    }

    /**
     * Compute the Euclidean Distance between two prototypes.
     * @param one One prototype.
     * @param two Other prototype.
     * @return Euclidean Distance between one and two.
     */
    public static double euclideanDistance(Prototype one, Prototype two)
    {
        return d(one, two);
    }
    
    /**
     * Compute the Euclidean Distance between two prototypes.
     * @param one One prototype.
     * @param two Other prototype.
     * @return Euclidean Distance between one and two.
     */
    public static double distance(Prototype one, Prototype two)
    {
        return d(one, two);
    }

    /**
     * Compute the Absolute Distance between two prototypes.
     * @param one One prototype.
     * @param two Other prototype.
     * @return Absolute Distance between one and two.
     */
    public static double absoluteDistance(Prototype one, Prototype two)
    {
        double[] oneInputs = one.getInputs();
        double[] twoInputs = two.getInputs();
        //int _size = one.numberOfInputs();
        double acc = 0.0;
        for (int i = 0; i < numberOfInputs; ++i)
        {
            acc += Math.abs(oneInputs[i] - twoInputs[i]);
        }

        return acc;
    }

     /**
     * Overloading of the compare function.
     * @param one One prototype.
     * @param two Other prototype.
     * @return if d(base,one)>d(base,two) 1; if d(base,one)==d(base,two) 0; else -1.
     */
    public int compare(Prototype one, Prototype two)
    {
        double one_d = Distance.d(basePrototype, one);
        double two_d = Distance.d(basePrototype, two);
        if (one_d > two_d)
        {
            return 1;
        } else if (one_d == two_d)
        {
            return 0;
        } else
        {
            return -1;
        }
    }
}//end-of-Distance

