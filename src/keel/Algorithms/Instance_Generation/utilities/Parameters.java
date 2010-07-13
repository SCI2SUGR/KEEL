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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package keel.Algorithms.Instance_Generation.utilities;

import java.util.ArrayList;

/**
 * Implements operations of getting and setting the algorithm parameters.
 * @author diegoj
 */
public class Parameters extends ArrayList<String>
{
    public static final String SEED_TXT = "seed";
    public static final String PERC_SIZE_TXT = "reduction size respect training size";
    
    /** Names of the parameteres. */
    protected ArrayList<String> names = null;
    
    /** Contains the message of correct use to the user of the program. */
    protected static String use = null;
    
    protected static String name = null;
    
    /** Current parameter */
    int current=0;
    
    protected static int indexExtendedArg = 2;
    
    public static String getFileName()
    {
        if(name==null)
            Debug.goout("Name member is null");
        return name;
    }
    
    
    static void goout()
    {
        if(use==null)
            Debug.goout("You have not assigned value to the use class member");
        Debug.goout(use);
    }
    
    public Parameters(int _size)
    {
        super(_size);
    }
    
    public Parameters(String[] args)
    {
        super(args.length);
        for(String s : args)
            add(s);
    }
    
    public Parameters(ArrayList<String> args)
    {
        super(args.size());
        for(String s : args)
            add(s);
    }
    
    public Parameters(String[] names, String[] args)
    {
        super(args.length);
        this.names = new ArrayList<String>(names.length);
        for(String s : names)
            this.names.add(s);
        for(String s : args)
            add(s);
    }
    
    /**
     * Returns and specified name of a parameter.
     * @param index Parameter in indexth position.
     * @return Name of indexth-parameter.
     */
    public String getName(int index)
    {
        return names.get(index);
    }
    
    public int getInt(int index)
    {
        return Integer.parseInt(get(index));
    }
    
    public double getDouble(int index)
    {
        return Double.parseDouble(get(index));
    }
    
    /**
     * Return next parameter as int.
     * @return Next parameter as an integer.
     */
    public int getNextAsInt()
    {
        return Integer.parseInt(get(current++));
    }
    
    /**
     * Return next parameter as double.
     * @return Next parameter as a double.
     */
    public double getNextAsDouble()
    {
        return Double.parseDouble(get(current++));
    }
    
    /**
     * Return next parameter as string.
     * @return Next parameter as a string.
     */
    public String getNextAsString()
    {
        return get(current++);
    }
    
    /**
     * Return next parameter as string.
     * @return Next parameter as a string.
     */
    public String[] getNextAsStringArray()
    {
        String [] stringArray = new String[size()-current];
        int _size = size();
        for(int i=current; i<_size; ++i)
            stringArray[i] = get(i);
        current = size();
        return stringArray;
    }
    
    public String[] getRemainingParameters()
    {
        int _size = size();
        String[] stringArray = new String[_size-current];        
        int index = 0;
        //Debug.errorln("size of " + size());
        for(int i=current; i<_size; ++i)
            stringArray[index++] = get(i);
        current = size();
        return stringArray;
    }
    
    /**
     * Informs if there are more arguments.
     * @return TRUE if there are more argumentos, FALSE in other chase.
     */
    public boolean existMore()
    {
        return current > size();
    }
    
    /**
     * Sets the use of the algorithm
     * @param algorithmName Human readable name of the implmented algorithm
     * @param advParams List of parameters of the program
     */
    public static void setUse(String algorithmName, String advParams)
    {
        use = algorithmName + " use:\n\t<training file> <test file> " + advParams;
    }
    
     /**
     * Assert the program arguments
     * @param args Arguments of the program
     */
    public static void assertBasicArgs(String[] args)
    {
        Debug.force(args.length>=1,"La longitud de los argumentos es menor que 2. Es " + args.length);
        name = args[0];
        //System.out.println("Fichero de training " + args[0]);
        //System.out.println("Fichero de test " + args[1]);
    }

    /**
     * Assert the program arguments which depends on the specific algorithm.
     * @param args Arguments of the program.
     * @param pos Position in the array of strings.
     * @param name Name of the argument.
     * @param llim Left value of the interval in which the argument is (only used if the argument is numeric)
     * @param rlim Right value of the interval in which the argument is (only used if the argument is numeric)
     * @return String with the argument.
     */    
    public static String assertExtendedArg(String[] args, int pos, String name, double llim, double rlim)
    {
        Debug.force(pos < args.length,pos+">="+args);            

        double val = Double.parseDouble(args[pos]);
        String mess = name + " debe estar en [" + llim +", " + rlim + "] y su valor es " + val;
        Debug.force((val>=llim && val<=rlim), mess);
        return args[pos];
    }
    
    /**
     * Assert the program arguments which depends on the specific algorithm.
     * @param args Arguments of the program.
     * @param pos Position in the array of strings.
     * @param name Name of the argument.
     * @param values Posibly values of the parameter.
     * @return String with the argument.
     */  
    public static String assertExtendedArgAsString(String[] args, int pos, String name, ArrayList<String> values)
    {
        Debug.force(values.contains(args[pos]),"Parameter " + name + " doesn't match for value " + args[pos] + " in the list of possible arguments");
        return args[pos];
    }
    
    /**
     * Assert the program arguments which depends on the specific algorithm.
     * @param args Arguments of the program.
     * @param pos Position in the array of strings.
     * @param name Name of the argument.
     * @param values Posibly values of the parameter.
     * @return String with the argument.
     */  
    public static String assertExtendedArgAsString(String[] args, int pos, String name, String[] values)
    {
        ArrayList<String> v = new ArrayList<String>(values.length);
        for(String s : values)
            v.add(s);
        return assertExtendedArgAsString(args, pos, name, v);
    }
    
    
    /**
     * Assert a double arguments which depends on the specific algorithm.
     * @param args Arguments of the program.
     * @param pos Position in the array of strings.
     * @param name Name of the argument.
     * @param llim Left value of the interval in which the argument is.
     * @param rlim Right value of the interval in which the argument is.
     * @return Argument as double.
     */    
    public static double assertExtendedArgAsDouble(String[] args, int pos, String name, double llim, double rlim)
    {
        return Double.parseDouble(assertExtendedArg(args, pos,  name, llim, rlim));
    }
    
    /**
     * Assert an integer argument which depends on the specific algorithm.
     * @param args Arguments of the program.
     * @param pos Position in the array of strings.
     * @param name Name of the argument.
     * @param llim Left value of the interval in which the argument is.
     * @param rlim Right value of the interval in which the argument is.
     * @return Argument as int.
     */    
    public static int assertExtendedArgAsInt(String[] args, int pos, String name, double llim, double rlim)
    {
        return Integer.parseInt(assertExtendedArg(args, pos,  name, llim, rlim));
    }
    
        /**
     * Assert the program arguments which depends on the specific algorithm.
     * @param args Arguments of the program.     
     * @param name Name of the argument.
     * @param llim Left value of the interval in which the argument is (only used if the argument is numeric)
     * @param rlim Right value of the interval in which the argument is (only used if the argument is numeric)
     * @return String with the argument.
     */    
    public static String assertExtendedArg(String[] args, String name, double llim, double rlim)
    {
        Debug.force(indexExtendedArg < args.length, indexExtendedArg+">="+args);            

        double val = Double.parseDouble(args[indexExtendedArg]);
        String mess = name + " debe estar en [" + llim +", " + rlim + "] y su valor es " + val;
        Debug.force((val>=llim && val<=rlim), mess);
        return args[indexExtendedArg++];        
    }
    
    /**
     * Assert a double arguments which depends on the specific algorithm.
     * @param args Arguments of the program.     
     * @param name Name of the argument.
     * @param llim Left value of the interval in which the argument is.
     * @param rlim Right value of the interval in which the argument is.
     * @return Argument as double.
     */    
    public static double assertExtendedArgAsDouble(String[] args, String name, double llim, double rlim)
    {
        return Double.parseDouble(assertExtendedArg(args, indexExtendedArg++,  name, llim, rlim));
    }
    
    /**
     * Assert an integer argument which depends on the specific algorithm.
     * @param args Arguments of the program.     
     * @param name Name of the argument.
     * @param llim Left value of the interval in which the argument is.
     * @param rlim Right value of the interval in which the argument is.
     * @return Argument as int.
     */    
    public static int assertExtendedArgAsInt(String[] args, String name, double llim, double rlim)
    {
        return Integer.parseInt(assertExtendedArg(args, indexExtendedArg++,  name, llim, rlim));
    }

}

