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

import org.core.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Algorithms.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Genotypes.*;
import keel.Algorithms.Shared.Exceptions.*;

public class PittsburghModel extends GeneticIndividualForModels {

    
	/**
	 * <p>
	 * Constructor. Generates a new Pittsburgh model
	 * </p>
	 */
    public PittsburghModel() {
	    super(0);
        m=null;
        g=null;
    }
    
    /**
     * <p>
     * Constructor. Generates a new Pittsburgh model 
     * </p>
     * @param s The fuzzy model
     * @param tf The type of fitness
     * @param r Random
     */
    public PittsburghModel(FuzzyModel s, int tf, Randomize r) {
	    super(tf);
        GenotypePitts gr=new GenotypePitts(s.size(),s.numConsequents(),r);
        for (int i=0;i<s.size();i++) {
            gr.setInvolvedRule(i,s.getComponent(i).consequent);
            gr.setRuleWeight(i,s.getComponent(i).weight);
        }
        g=gr;
        m=s.clone();
    }

    /**
     * <p>
     * This method clone a genetic individual from a Pittsburgh model 
     * </p>
     * @return The new genetic individual
     */
    public GeneticIndividual clone() {
        return new PittsburghModel(this);
    }
    
    /**
     * <p>
     * Constructor. Generate a new pittsburgh model from another one
     * </p>
     * @param p The new one
     */
    public PittsburghModel(PittsburghModel p) {
	    super(p.fitnessType);
        m=p.m.clone();
        g=p.g.clone();
    }

    /**
     * <p>
     * This method sets the properties of a Pittsburgh model to another one
     * </p>
     * @param p The Pittsburgh model
     */    
    public void set(PittsburghModel p) {
        m=p.m.clone();
        g=p.g.clone();
    }
    
    /**
     * <p>
     * This method obtain the parameters of a genetic individual from the genotype
     * </p>
     */
    public void parametersFromGenotype() {
        GenotypePitts gr=(GenotypePitts)(g);
        FuzzyModel s=(FuzzyModel)(m);
        for (int i=0;i<gr.size();i++) {
            s.getComponent(i).consequent=gr.getInvolvedRule(i);
            s.getComponent(i).weight=gr.getRuleWeight(i);
        }      
    }

    /**
     * <p>
     * This method implement the mutation operation
     * </p>
     * @param alpha Index mutation
     * @param IDMUTA Type of mutation
     * @throws invalidMutation message if error
     */
    public void mutation(double alpha, int IDMUTA) throws invalidMutation {
        g.mutation(alpha,IDMUTA);
        parametersFromGenotype();
    }

    /**
     * <p>
     * This method implement the cross operation.
     * The cross generates two objects of class 'individuogen'
     * </p>
     * @param p2 Genetic individual
     * @param p3 Genetic individual
     * @param p4 Genetic individual
     * @param IDCRUCE Type of cross
     * @throws invalidCrossover Message if error
     */    
    public void crossover(GeneticIndividual p2, GeneticIndividual p3, GeneticIndividual p4, int IDCRUCE) throws invalidCrossover {
        g.crossover(((PittsburghModel)(p2)).g, ((PittsburghModel)(p3)).g, ((PittsburghModel)(p4)).g, IDCRUCE);

        p3.parametersFromGenotype();
        p4.parametersFromGenotype(); 
    }

    /**
     * <p>
     * This method generate a random genotype and obtain the parameters from another one
     * </p>
     */    
    public void Random() {
        g.Random();
        parametersFromGenotype();
    }
	
	/**
	 * <p>
	 * This method calculate a local optimization
	 * </p>
	 * @param MAXITER Maximum iterations
	 * @param idoptimiza Type of optimization
	 * @throws invalidOptim Message if error
	 */    
	public void localOptimization(int MAXITER, int idoptimization) throws invalidOptim {
	   throw new invalidOptim("Optimizacion local no implementada en PittsburghModelo");
	}
	
}

