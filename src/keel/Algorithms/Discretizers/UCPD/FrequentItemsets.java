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

import java.util.Vector;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Parameters;


/**
 * <p>
 * This class implements the algorithm to find the large itemsets of a dataset
 * </p>
 * 
 * @author Written by Jose A. Saez (University of Granada), 21/12/2009
 * @version 1.0
 * @since JDK1.6
 */
public class FrequentItemsets {

	static private int minSupport;				// minimal support of itemset

	static private int instances[][];			// all the instances
	static private int numAtt;					// number of attributes
	static private int numInstances;			// number of instances
	
	static private Vector<Itemset> lastCandidates;		// last candidates array
	static private Vector<Itemset> candidates;			// candidates array
	static private Vector<Itemset> frequentItemsets;	// the frequent itemsets

	
//******************************************************************************************************

	/**
	 * <p>
	 * It computes the frequent itemsets and returns them
	 * </p>
	 * @param examples matrix of instances
	 * @param numValues number of different values of each attribute
	 */	
	static public Vector<Itemset> getFrequentItemsets(int[][] examples, int[] numValues){
		
		frequentItemsets = new Vector<Itemset>();
		
		// initialize parameters
		instances = examples;
		numInstances = instances.length;
		numAtt = instances[0].length;
		minSupport = Parameters.minSupport;
		
		// compute the large 1-itemsets
		computeLarge1Itemsets(numValues);
		for(int i = 0 ; i < lastCandidates.size() ; ++i)
			frequentItemsets.add(lastCandidates.get(i));
		
		// compute the large k-itemsets
		while(lastCandidates.size() > 0){
			computeCandidates();	// compute the candidate itemsets in this pass
			computeFrecuence();		// compute the frequence of each candidate itemset and remove it if necessary
		}
		
		return frequentItemsets;
	}
	
//******************************************************************************************************
	
	/**
	 * <p>
	 * It computes the large 1-itemsets from the instances array and it saves them into the array lastCandidates
	 * </p>
	 * @param numValues number of different values of each attribute
	 */	
	static public void computeLarge1Itemsets(int[] numValues){
		
		int i, j, p;	// loop indexes
		
		// computes the number of occurrences of each value of each attribute
		int[][] numOccurrences = new int[numAtt][];
		for(i = 0 ; i < numAtt; ++i)
			numOccurrences[i] = new int[numValues[i]];
		
		for(i = 0 ; i < numAtt; ++i)
			for(j = 0 ; j < numValues[i] ; ++j)
				numOccurrences[i][j] = 0;
		
		for(i = 0 ; i < numInstances; ++i)
			for(j = 0 ; j < numAtt ; ++j)
				numOccurrences[j][instances[i][j]]++;
		
		
		// computes the large 1-itemsets
		lastCandidates = new Vector<Itemset>();
		
		for(i = 0 ; i < numAtt; ++i)
			for(j = 0 ; j < numValues[i] ; ++j)
				if(numOccurrences[i][j] >= minSupport){
					
					int[] aux = new int[numAtt];
					
					for(p = 0 ; p < numAtt ; ++p)
						aux[p] = -1;
					
					aux[i] = j;
					Itemset it = new Itemset(aux,numAtt,i);
					lastCandidates.add(it);
				}

	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It computes the candidate itemsets from lastCandidates and saves them into array candidates
	 * </p>
	 */	
	static public void computeCandidates(){
		
		int i, j; 	// loop indexes
		candidates = new Vector<Itemset>();

		for(i = 0 ; i < lastCandidates.size()-1 ; ++i){
			for(j = i+1 ; j < lastCandidates.size() ; ++j ){
				Itemset nuevo = lastCandidates.get(i).combine(lastCandidates.get(j));
				if(nuevo != null)
					candidates.add(nuevo);
			}
		}
	
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It computes the frecuence of each candidate itemset and accepts them if exceeds minSupport value.
	 * It returns the accepted itemsets into the array lastCandidates
	 * </p>
	 */	
	static private void computeFrecuence(){
		
		int i, j;	// loop indexes
		
		lastCandidates = new Vector<Itemset>();
		
		// compute the occurrence of each itemset
		int[] occurrences = new int[candidates.size()];
		for(i = 0 ; i < candidates.size() ; ++i)
			occurrences[i] = 0;
		
		for(i = 0 ; i < candidates.size() ; ++i)
			for(j = 0 ; j < numInstances; ++j)
				if(candidates.get(i).into(instances[j]))
					occurrences[i]++;
	
		// accept the itemsets that exceed minSupport
		for(i = 0 ; i < candidates.size() ; ++i)
			if(occurrences[i] >= minSupport)
				lastCandidates.add(candidates.get(i));
		

		// save the itemsets
		if(lastCandidates.size() > 0){
			
			for(i = 0 ; i < lastCandidates.size() ; ++i)
				frequentItemsets.add(lastCandidates.get(i));
		}
	}

}