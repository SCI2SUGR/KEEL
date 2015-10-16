/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    S. García (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
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
 * 
 * File: Timer.java
 * 
 * Auxiliar class to support timing reports in model+training+test algorithms
 * 
 * @author Written by Joaquín Derrac (University of Granada) 20/04/2010 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
package keel.Algorithms.RST_Learning;

public class Timer{
	
	private static long initialTime;
	
	private static double modelTime;
	private static double trainingTime;
	private static double testTime;
	
	/** 
	 * Resets the time counter
	 * 
	 */
	public static void resetTime(){
		
		initialTime = System.currentTimeMillis();
		
	}//end-method
	
	/** 
	 * Set model time
	 * 
	 */
	public static void setModelTime(){
		
		modelTime=((double)System.currentTimeMillis()-initialTime)/1000.0;
		
	}//end-method
	
	/** 
	 * Set training time
	 * 
	 */
	public static void setTrainingTime(){
		
		trainingTime=((double)System.currentTimeMillis()-initialTime)/1000.0;
		
	}//end-method
	
	/** 
	 * Sets training time
	 * 
	 */
	public static void setTestTime(){
		
		testTime=((double)System.currentTimeMillis()-initialTime)/1000.0;
		
	}//end-method
	
	/**
	 * Get model time
	 * 
	 * @return Model time
	 */
	public static double getModelTime(){
		
		return modelTime;
		
	}//end-method
	
	/**
	 * Get training time
	 * 
	 * @return Training time
	 */
	public static double getTrainingTime(){
		
		return trainingTime;
		
	}//end-method
	
	/**
	 * Get test time
	 * 
	 * @return Test time
	 */
	public static double getTestTime(){
		
		return testTime;
		
	}//end-method

}//end-class
