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

package keel.Algorithms.Discretizers.UCPD;

import org.core.Randomize;


/**
 * <p>
 * This class helps managing a sampling without replacement process 
 * </p>
 * 
 * @author Written by Jose A. Saez (University of Granada), 21/12/2009
 * @version 1.0
 * @since JDK1.6
 */
public class Sampling {
	
	int maxSize;	// total number of elements
	int num;		// actual number of elements
	int []sample;	// actual elements


//******************************************************************************************************

	/**
	 * <p>
	 * Class constructor
 	 * </p>
	 * @param _maxSize number of elements
	 */	
	public Sampling(int _maxSize){
		
		maxSize = _maxSize;
		sample = new int[maxSize];
		initSampling();
	}

//******************************************************************************************************

	/**
	 * <p>
	 * It initializes the sampling
 	 * </p>
	 */		
	void initSampling(){
		
		for(int i = 0; i < maxSize; i++)
			sample[i] = i;
		
		num = maxSize;
	}

//******************************************************************************************************

	/**
	 * <p>
	 * It returns one value of the sampling
 	 * </p>
	 * @return the sampled value
	 */	
	public int getSample(){
		
		int pos = Randomize.Randint(0, num);
		int value = sample[pos];
		sample[pos] = sample[num-1];
		num--;

		if(num == 0)
			initSampling();

		return value;
	}
	
}