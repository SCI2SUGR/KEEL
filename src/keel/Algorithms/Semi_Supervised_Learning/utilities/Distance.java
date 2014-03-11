package keel.Algorithms.Semi_Supervised_Learning.utilities;

import keel.Algorithms.Semi_Supervised_Learning.Basic.Prototype;
import keel.Algorithms.Semi_Supervised_Learning.*;
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
        for (int i = 0; i < one.numberOfInputs(); i++)
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
        for (int i = 0; i < one.numberOfInputs(); ++i)
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
