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
 * @author Written by Nicola Flugy Papa (Politecnico di Milano) 24/03/2009
 * @author Modified by Cristobal J. Carmona (University of Jaen) 10/07/2010
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDMap.SDMap;

import java.util.*;

import keel.Algorithms.Subgroup_Discovery.SDMap.FPTree.FPtree;

public class FPgrowthProcess {
  /**
   * <p>
   * It provides the implementation of the algorithm to be run in a process
   * </p>
   */
  
  private double minSupport;
  private double minConfidence;
  
  private myDataset dataset;
  private FPtree newFPtree;
  
  /**
   * <p>
   * It creates a new process for the algorithm by setting up its parameters
   * </p>
   * @param dataset The instance of the dataset for dealing with its records
   * @param minSupport The user-specified minimum support for the mined association rules
   * @param minConfidence The user-specified minimum confidence for the mined association rules
   */
  public FPgrowthProcess(myDataset dataset, double minSupport, double minConfidence) {
	  this.minSupport = minSupport;
	  this.minConfidence = minConfidence;
	  
	  this.dataset = dataset;
  }
  
  /**
   * <p>
   * It runs the algorithm for mining association rules
   * </p>
   */
  public void run() {
	  this.newFPtree = new FPtree(this.dataset, this.minSupport, this.minConfidence);
	  
	  this.newFPtree.idInputDataOrdering();
	  this.newFPtree.recastInputDataAndPruneUnsupportedAtts();
	  this.newFPtree.setNumOneItemSets();
	  
	  this.newFPtree.createFPtree();
	  this.newFPtree.startMining();
  }
  
  /**
   * <p>
   * It constructs a rules set once the algorithm has been carried out
   * </p>
   * @return An array of association rules having both minimum confidence and support
   */
  public ArrayList<AssociationRule> generateRulesSet() {
	  return ( this.newFPtree.getRulesSet() );
  }
  
}