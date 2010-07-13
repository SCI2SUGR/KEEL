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
 * @author Written by Luciano Sánchez (University of Oviedo) 21/01/2004
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
public class FuzzyGPModel extends Model {
/**
 * Class for management fuzzy models in GP
 */
    NodeRuleBase R; 
    static  FuzzyPartition C;
    int defuzType;
   
    /**
     * <p>
     * Constructor. Inicialize a new fuzzy model for GP
     * </p>
     * @param pR Base rule
     * @param c Fuzzy partition
     * @param td Type of defuzzifier
     */
    public FuzzyGPModel( NodeRuleBase pR, FuzzyPartition c, int td) {
        R=(NodeRuleBase)pR.clone();
        C=c.clone();
        defuzType=td;
    }

    /**
     * <p>
     * Constructor. Initialize a new fuzzy model fro GP from another one
     * </p>
     * @param mb The fuzzy model for GP
     */
    public FuzzyGPModel(FuzzyGPModel mb) {
        R=(NodeRuleBase)mb.R.clone();
        C=mb.C.clone();
        defuzType=mb.defuzType;
    }

    /**
     * <p>
     * This method assign a fuzzy model for GP to anothe one
     * </p>
     * @param mb The fuzzy model for GP
     */
    public void set(FuzzyGPModel mb) {
        R=(NodeRuleBase)mb.R.clone();
        C=mb.C.clone();
        defuzType=mb.defuzType;
    }
    
    /**
     * <p>
     * This method clone a fuzzy model for GP
     * </p>
     */
    public Model clone()  {
        return new FuzzyGPModel(this);
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
     * This method return the output of the model defuzzified
     * </p>
     * @param x The output
     */
    public double output(double [] x) {
		
		FuzzyAlphaCut xfuzzy[] = new FuzzyAlphaCut[x.length];
		for (int i=0;i<x.length;i++) xfuzzy[i]=new FuzzyAlphaCut(new FuzzyNumberTRIANG(x[i],x[i],x[i]));
		
        R.replaceTerminals(xfuzzy);
        IntDouble[] result=R.CrispEval();

        if (defuzType==RuleBase.DEFUZCDM) {
        double addcenter=0, addweight=0;
        for (int i=0;i<result.length;i++) {
            addcenter+=C.getComponent(result[i].consequent).massCentre()*result[i].weight;
            addweight+=result[i].weight;
        }

        if (addweight==0) return 0; // The output is not covered. This mustn't ocurr.
        return addcenter/addweight;
        }
        
        if (defuzType==RuleBase.DEFUZMAX) {
            double center=0, maxweight=0;
            for (int i=0;i<result.length;i++) {
                
                if (result[i].weight>=maxweight) {
                    maxweight=result[i].weight;
                    center=C.getComponent(result[i].consequent).massCentre();
                }
            }
            
            if (maxweight==0) return 0; // The output is not covered. This musn't ocurr.
            return center;
            
        }
        
        return 0;
        
    }
}

