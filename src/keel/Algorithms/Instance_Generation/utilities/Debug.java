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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.utilities;

import java.util.*;

/**
 * Implements operations that changes the flow of the program
 * @author diegoj
 */
public class Debug
{
    /** Flag that allows to show standard messages */
    static private boolean STD_DEBUG_MODE = false;
    
    /** Flag that allows to show error messages */
    static private boolean ERROR_DEBUG_MODE = true;
    
    /**
     * Set error debug mode to a desired state.
     * @param value New state of error debug mode.
     */
    public static void setErrorDebugMode(boolean value){ ERROR_DEBUG_MODE = value;  }
    
    /**
     * Set standard debug mode to a desired state.
     * @param value New state of standard debug mode.
     */
    public static void setStdDebugMode(boolean value){ STD_DEBUG_MODE = value;  }
    
    /**
     * Set both debug modes to a desired state.
     * @param value New state of both debug modes.
     */
    public static void set(boolean value){ERROR_DEBUG_MODE = STD_DEBUG_MODE = value;}
    
    /**
     * Set both debug modes (set them to true).
     */
    public static void set(){ERROR_DEBUG_MODE = STD_DEBUG_MODE = true;}
    
    /**
     * Reset both debug modes (set them to false).
     */
    public static void reset(){ERROR_DEBUG_MODE = STD_DEBUG_MODE = false;}
    
    /**
     * Exits the current program
     */
    protected static void terminateProgram()
    {
        //System.exit(-1);
        //ojo, debes cambiarlo cuando est√© dentro de Keel
        //por las dos siguiente l√≠neas
        System.err.println("Program has commited and error");
        throw new RuntimeException();
    }
    
    /**
     * Prints a message in the standard console.
     * @param mess Message to be printed.
     */
    public static void println(String mess)
    {
        if(STD_DEBUG_MODE)
        {
            System.out.println(mess);
        }
    }
    
    public static void print(String mess, ArrayList<Double> array)
    { 
        if(STD_DEBUG_MODE)
        {
            System.out.println(mess);
            for(Double i : array)
                System.out.print(i + " ");
            System.out.print("\n");
        }
    }

    /**
     * Prints a message in the error console.
     * @param mess Message to be printed.
     */
    public static void errorln(String mess)
    {
        if(ERROR_DEBUG_MODE)
        {
            System.err.println(mess);
        }   
    }

    /**
     * Prints a message in the error console.
     * @param mess Message to be printed.
     */
    public static void printlnError(String mess)
    {
        errorln(mess);
    }
    
    /**
     * Prints a message in the error console.
     * @param mess Message to be printed.
     */    
    public static void perrorln(String mess)
    {
        errorln(mess);
    }
    
    /**
     * Force a condition.
     * @param cond Condition to be asserted.
     * @param mess Message to be printed in case of non-condition acomplished.
     */    
    public static void force(boolean cond, String mess)
    {
        if(!cond)
            goout(mess);
    }
    
    /**
     * Ends the program if the condition occurs.
     * @param cond Condition to be evaluated. If occurs, displays a message and terminates the program.
     * @param mess Message to be displayed.
     */
    public static void endsIf(boolean cond, String mess)
    {
        if(cond)
            goout(mess);
    }
    
    /**
     * Ends the program if the condition occurs.
     * @param obj Object to be tested if is null. If occurs, displays a message and terminates the program.
     * @param mess Message to be displayed.
     */
    public static void endsIfNull(Object obj, String mess)
    {
        if(obj==null)
            goout(mess);
    }
    
    /**
     * Shows a message and ends the program.
     * @param message Message showed to the user.
     */
    public static void goout(String message)
    {
        errorln(message);
        terminateProgram();
    }
}

