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

/***********************************************************************

	This file is part of the Fuzzy Instance Based Learning package, a
	Java package implementing Fuzzy Nearest Neighbor Classifiers as 
	complementary material for the paper:
	
	Fuzzy Nearest Neighbor Algorithms: Taxonomy, Experimental analysis and Prospects

	Copyright (C) 2012
	
	J. Derrac (jderrac@decsai.ugr.es)
	S. García (sglopez@ujaen.es)
	F. Herrera (herrera@decsai.ugr.es)

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


 package keel.Algorithms.Fuzzy_Instance_Based_Learning.JFKNN;

import java.util.Arrays;

/**
 * 
 * File: Triplet.java
 * 
 * A Triplet class for the JFKNN algorithm. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
public class Triplet {
	
	public double w [][];
	public int k;
	public double error;
	
	public Triplet(int size,int classes){
		
		k=0;
		error=1.0;
		w=new double [size][classes];
		
		for(int i=0;i<size;i++){
			Arrays.fill(w[i],0.0);
		}
	}
	
	public Triplet(double wMatrix [][]){
		
		k=0;
		error=1.0;
		w=new double [wMatrix.length][wMatrix[0].length];
		
		for(int i=0;i<w.length;i++){
			System.arraycopy(wMatrix[i], 0, w[i], 0, w[i].length);
		}
		
	}
	
	public Triplet(Triplet other){
		
		k=other.k;
		error=other.error;
		w=new double [other.w.length][other.w[0].length];
		
		for(int i=0;i<w.length;i++){
			System.arraycopy(other.w[i], 0, w[i], 0, w[i].length);
		}
		
	}
	

}
