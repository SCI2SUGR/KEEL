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
 * @author Written by Jose A. Saez Munoz, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * Date: 06/01/10
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Preprocess.NoiseFilters.SaturationFilter;

import java.util.Vector;
import keel.Dataset.Instance;


/**
 * <p>
 * This class lets to handle pnPair structure
 * </p>
 */
public class pnPair {

	private int posEx;		// index of the positive class example
	private int negEx;		// index of the negative class example
	private double weight;	// pnPair weight
		

//******************************************************************************************************
	
	/**
	 * <p>
	 * Constructor of the class
	 * </p>
	 * @param e1 index of the positive example
	 * @param e2 index of the negative example
	 */
	public pnPair(int e1, int e2){
		posEx = e1;
		negEx = e2;
		weight = 0;
	}
//******************************************************************************************************
	
	/**
	 * <p>
	 * It sets the posEx value
	 * </p>
	 * @param index the new posEx value
	 */
	public void setPosEx(int index){
		posEx = index;
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It returns the posEx value
	 * </p>
	 * @return the posEx value
	 */
	public int getPosEx(){
		return posEx;
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It sets the negEx value
	 * </p>
	 * @param index the new negEx value
	 */
	public void setNegEx(int index){
		negEx = index;
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It returns the negEx value
	 * </p>
	 * @return the negEx value
	 */
	public int getNegEx(){
		return negEx;
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It sets the new weight for the pnPair
	 * </p>
	 * @param w the new weight
	 */
	public void setWeight(double w){
		weight = w;
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It returns the weight value
	 * </p>
	 * @return the weight value
	 */
	public double getWeight(){
		return weight;
	}

//******************************************************************************************************
	
	/**
	 * <p>
	 * It computes the number of literals that covers the pnPair
	 * </p>
	 * @param literals
	 * @param numlit
	 * @return the number of literals
	 */
	public int numliteralCovers(boolean[][] literals, int numlit){
		
		int num = 0;
		
		for(int l = 0 ; l < numlit ; ++l)
			if(isCovered(literals,l))
				num++;
		
		return num;
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It computes the indexes of the literals that cover the pnPair
	 * </p>
	 * @param literals
	 * @param numlit
	 * @return the indexes
	 */
	public Vector<Integer> indexLiterals(boolean[][] literals, int numlit){
		
		Vector<Integer> res = new Vector<Integer>();
				
		for(int l = 0 ; l < numlit ; ++l)
			if(isCovered(literals,l))
				res.add(l);

		return res;
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It checks if only the literal lit covers the pnPair and the other do not
	 * </p>
	 * @return true or false
	 */
	public boolean onlyOneLiteral(boolean[][] literals, Vector<Integer> L, int lit){
		
		// si no lo cubre, false
		if( !isCovered(literals,lit) )
			return false;
		
		// si lo cubre y no es el objetivo, false
		for(int l = 0 ; l < L.size() ; ++l) // el lo cubre y no es el objetivo
			if( isCovered(literals,L.get(l)) && lit != L.get(l) )
				return false;

		return true;
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It checks if only the literal lit covers the pnPair and the other do not
	 * </p>
	 */
	public boolean isCovered(boolean[][] literals, int lit){
		
		if( literals[posEx][lit] == true && literals[negEx][lit] == false )
			return true;
		
		//if( literals[posEx][lit] == false && literals[negEx][lit] == true )
		//	return true;
		
		return false;	
	}
	
//******************************************************************************************************
	
	static public Vector<pnPair> getPNpairs(IntegerSet E, Instance[] instances, int positiveClass){
		
		Vector<pnPair> U = new Vector<pnPair>();
		
		// necesito conjuntos de todos (pos, neg)
		for(int i = 0 ; i < E.size() ; ++i)
			if(instances[E.getElement(i)].getOutputNominalValuesInt(0) == positiveClass){
				for(int j = 0 ; j < E.size() ; ++j)
					if(instances[E.getElement(j)].getOutputNominalValuesInt(0) != positiveClass){
						U.add(new pnPair(E.getElement(i),E.getElement(j)));
					}
			}
		
		return U;
	}
	
	public void print(){
		System.out.println("(" + posEx + " , " + negEx +"), weight = " + weight);
	}
	
}