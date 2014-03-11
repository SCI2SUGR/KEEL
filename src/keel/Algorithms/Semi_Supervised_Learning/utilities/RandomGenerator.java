/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Semi_Supervised_Learning.utilities;
import org.core.*;
import java.util.*;

/**
 * Random Number Generator class to be used in the package Prototype_Generation.
 * @author diegoj
 */
public class RandomGenerator extends Randomize
{
    /** Object random used int the number generators */
    protected static Random random = null;
    
    /** Seed value. */
    protected static long seed = 0;
    
    /**
     * Set the seed of the random method.
     * @param s Seed of the random method.
     */
    public static void setSeed(long s)
    {
        RandomGenerator.seed = s;
        Randomize.setSeed(s);
        random = new Random();
        random.setSeed(s);        
    }
    
    /**
     * Generate a random sequence of integer between the bounds. Step between elements is specified.
     * @param min Lower bound.
     * @param max Upper bound.
     * @param inc Increment of the elements of the interval.
     * @return Random list of integers in [min, max].
     */
    public static ArrayList<Integer> generateDifferentRandomIntegersWithStep(int min, int max, int inc)
    {
        Debug.endsIf(max<min, "Limits of generateDifferentRandomIntegers are wrong. [" + min + ", " + max+"]");
        int number = (max-min+1)/inc;
        ArrayList<Integer> list = new ArrayList<Integer>(number);
        for(int i=min; i<=max; i+=inc)
            list.add(i);
        Collections.shuffle(list, random);
        return list;
    }
    
    /**
     * Generate a random sequence of integer between the bounds. Step between elements is 1.
     * @param min Lower bound.
     * @param max Upper bound.     
     * @return Random list of integers in [min, max].
     */
    public static ArrayList<Integer> generateDifferentRandomIntegers(int min, int max)
    {
        Debug.endsIf(max<min, "Limits of generateDifferentRandomIntegers are wrong. [" + min + ", " + max+"]");
        int number = max-min+1;
        ArrayList<Integer> list = new ArrayList<Integer>(number);
        for(int i=min; i<=max; ++i)
            list.add(i);
        Collections.shuffle(list, random);
        return list;
    }
    
    /**
     * Generate a random sequence of integer between the bounds. Step between elements is 1.
     * @param min Lower bound.
     * @param max Upper bound.     
     * @param number Number of elements.
     * @return Random list of integers in [min, max].
     */
    public static ArrayList<Integer> generateDifferentRandomIntegers(int min, int max, int number)
    {
        Debug.endsIf(max<min, "Limits of generateDifferentRandomIntegers are wrong. [" + min + ", " + max+"]");
        ArrayList<Integer> list = new ArrayList<Integer>(number);
        int count=0;
        for(int i=min; count<number  &&  i<=max; ++i)
        {
            list.add(i);
            ++count;
        }
        Collections.shuffle(list, random);
        return list;
    }
    
    /**
     * Generate a random sequence of double values between the bounds. Step between elements is specified.
     * @param min Lower bound.
     * @param max Upper bound.    
     * @param inc Increment of the elements of the interval. 
     * @return Random list of numbers in [min, max].
     */
    public static ArrayList<Double> generateDifferentRandomNumbersWithStep(double min, double max, double inc)
    {
        Debug.endsIf(max<min, "Limits of generateDifferentRandomIntegers are wrong. [" + min + ", " + max+"]");
        int number = (int)Math.floor((max-min+1)/inc);
        ArrayList<Double> list = new ArrayList<Double>(number);
        for(double i=min; i<=max; i+=inc)
            list.add(i);
        Collections.shuffle(list, random);
        return list;
    }
    
    /**
     * Generate a random sequence of double values between the bounds. Step between elements is 1.0.
     * @param min Lower bound.
     * @param max Upper bound.     
     * @return Random list of numbers in [min, max].
     */
    public static ArrayList<Double> generateDifferentRandomNumbers(double min, double max)
    {
        Debug.endsIf(max<min, "Limits of generateDifferentRandomNumbers are wrong. [" + min + ", " + max+"]");
        int number = (int)(max-min+1);
        ArrayList<Double> list = new ArrayList<Double>(number);
        for(double i=min; i<=max; ++i)
            list.add(i);
        Collections.shuffle(list, random);
        return list;
    }
    
    /**
     * Generate a random sequence of double values between the bounds. Step between elements is 1.0.
     * @param min Lower bound.
     * @param max Upper bound. 
     * @param number Number of elements generated.    
     * @return Random list of numbers in [min, max].
     */
    public static ArrayList<Double> generateDifferentRandomNumbers(double min, double max, int number)
    {
        Debug.endsIf(max<min, "Limits of generateDifferentRandomNumbers are wrong. [" + min + ", " + max+"]");
        ArrayList<Double> list = new ArrayList<Double>(number);
        int count = 0;
        for(double i=min; count<number && i<=max; ++i)
        {
            list.add(i);
            ++count;
        }
        Collections.shuffle(list, random);
        return list;
    }
    
    /**
     * Select by random method between two objects
     * @param a One object.
     * @param b Other object.
     */
    public static <T> T randomSelector(T a, T b)
    {
        T result = a;
        if(RandomGenerator.Randint(0, 1) == 0)
            result = b;
        return result;
    }

}
