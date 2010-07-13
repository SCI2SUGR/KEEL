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
 * @author Written by Luciano Sánchez (University of Oviedo) 20/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Model;

import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

// Wrappers used with genetic algorithms
public class FuzzyModel extends Model {
	/**
	 * <p>
	 * Class for management of fuzzy models
	 * </p>
	 */
    RuleBase R;
    int defuzType;

    /**
     * <p>
     * Constructor. Generate a new rule base
     * </p>
     * @param a The fuzzy partition 
     * @param b The fuzzy partition
     * @param c
     * @param d
     * @param td Type of defuzzifier
     */
    public FuzzyModel(FuzzyPartition[] a, FuzzyPartition b, int c, int d, int td) {
        R=new RuleBase(a,b,c,d);
        defuzType=td;
    }
    
    /**
     * <p>
     * Construxtor. Generate a new rule base besed in another fuzzy model
     * </p>
     * @param m The fuzzy model
     */
    public FuzzyModel(FuzzyModel m) {
        R=m.R.clone();
        defuzType=m.defuzType;
    }
    
    /**
     * <p>
     * This method asign a fuzzy mothed to another one
     * </p>
     * @param m The fuzzy model
     */
    public void set(FuzzyModel m) {
        R=m.R.clone();
        defuzType=m.defuzType;
    }
    
    /**
     * <p>
     * This method defuzzified the output and return a value
     * </p> 
     * @param x The output
     * @return
     */
    public double output(double [] x) {
        return R.defuzzify(R.output(x),defuzType);
    }
    
    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        R.debug();
    }

    /**
     * <p>
     * This method clone a fuzzy model
     * </p>
     * @return The fuzzy model cloned
     */
    public Model clone() {
        return new FuzzyModel(this);
    }
    
    /**
     * <p>
     * This methos return the size of the fuzzy model
     * </p>
     * @return The size of the fuzzy model
     */
    int size() {
        return R.size();
    }
    
    /**
     * <p>
     * This method return the number of consequents of the fuzzy model
     * </p>
     * @return The number of consequents
     */
    int numConsequents() {
        return R.numConsequents();
    }
    
    /**
     * <p>
     * This method obtain the consequent and the weight of the rule
     * </p>
     * @param n The position of the rule
     * @return
     */
    FuzzyRule getComponent(int n) {
        return R.getComponent(n);
    }

   /**
    * <p>
    * This method assign the values of the fuzzy rule to another one
    * </p>
    * @param i The position of the rule
    * @param b A fuzzy rule
    */
    void setComponent(int i, FuzzyRule b) {
        R.setComponent(i,b.clone());
    }

}

