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
* @author Written by Luciano Sanchez (University of Oviedo) 20/05/2004 
* @author Modified by J.R. Villar (University of Oviedo) 19/12/2008
* @version 1.0 
* @since JDK1.4 
* </p> 
*/ 

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierFuzzyPittsBurgh;
import org.core.*;

import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Algorithms.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Genotypes.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Classifier.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

public class PittsburghClassifier extends GeneticIndividualForClassification {
/** 
* <p> 
* PittsburghClassifier is designed to allow a Fuzzy Classifier evolve by means of
* an Genetic Algorithm. This class is a specification of class 
* {@link GeneticIndividualForClassification}.
* 
* </p> 
*/ 

/** 
* <p> 
* Default constructor 
* </p> 
*/ 	
    public PittsburghClassifier() { super(0); }

/** 
* <p> 
* A constructor of the class specifying the Fuzzy classifier o be train, the fitness 
* type and the Randomize object to use.
* </p> 
* @param s       the {@link FuzzyClassifier} to be cloned, used and train in this class 
* @param tf      the type of Fitness function to evaluate the individual
* @param r       the Randomize object to be used in the genetic evolution
*/ 	
    public PittsburghClassifier(FuzzyClassifier s, int tf, Randomize rand) {
	    super(tf);
        GenotypePitts gr=new GenotypePitts(s.size(),s.getNumConsequents(),rand);
        for (int i=0;i<s.size();i++) {
            gr.setInvolvedRule(i,s.getComponent(i).consequent);
            gr.setRuleWeight(i,s.getComponent(i).weight);
        }
        g=gr;
        c=s.clone(); 
    }

/** 
* <p> 
* The copy constructor for this class. 
* </p> 
* @param p       the {@link PittsburghClassifier} to be copied 
*/ 	
    public PittsburghClassifier(PittsburghClassifier p) {
	    super(p.fitnessType);
        c=p.c.clone(); 
        g=p.g.clone();
    }

/** 
* <p> 
* This method clones the current object.
* </p> 
* @return     a {@link GeneticIndividual} object which is a copy of the current object
*/ 	
    public GeneticIndividual clone() {
        return new PittsburghClassifier(this);
    }

/** 
* <p> 
* This method copies the given {@link PittsburghClassifier} in the current object.
* </p> 
* @param p   the {@link PittsburghClassifier} object to be assigned to the current one 
*/ 	
    public void set(PittsburghClassifier p) {
        c=p.c.clone();
        g=p.g.clone();
    }

/** 
* <p> 
* This method sets the current classifier of according to it's genotype.
* </p> 
*/ 	
    public void parametersFromGenotype() {
        GenotypePitts gr=(GenotypePitts)g;
        FuzzyClassifier s=(FuzzyClassifier)c;
        for (int i=0;i<gr.size();i++) {
            FuzzyRule r=new FuzzyRule(gr.getInvolvedRule(i),gr.getRuleWeight(i));
            s.setComponent(i,r);
        }

    }

/** 
* <p> 
* This method performs the mutation genetic operation of the current 
* FuzzyGAPClassifier. 
* This methods updates its classifier according its genotype.
* </p> 
* @param alpha   this parameter is fixed according to {@link GenotypePitts}
* @param mutationID the type of mutation operation as stated in {@link GenotypePitts}.
* @throws {@link invalidOptim} if non supported mutationID
*/ 	
    public void mutation(double alpha, int mutationID) throws invalidMutation {
        g.mutation(alpha, mutationID);
        parametersFromGenotype();
    }

/** 
* <p> 
* This method performs the crossover genetic operation between the current
* object and the first parameter. The crossed individuals are left in the 
* second and thrid parameters. Both individuals have their classifier updated
* according to their genotypes.
* </p> 
* @param p2      the {@link GeneticIndividual} to cross with 
* @param p3      the first crossed {@link GeneticIndividual}
* @param p4      the second crossed {@link GeneticIndividual}
* @param croosoverID this value should be fixed according to {@link GenotypePitts}
* @throws {@link invalidCrossover} in case of invalid crossoverID
*/ 	
    public void crossover(GeneticIndividual p2, GeneticIndividual p3, GeneticIndividual p4, int croosoverID) throws invalidCrossover {
        g.crossover(((PittsburghClassifier)p2).g,
                ((PittsburghClassifier)p3).g,
                ((PittsburghClassifier)p4).g,
				croosoverID);

        p3.parametersFromGenotype();
        p4.parametersFromGenotype();
    }

/** 
* <p> 
* This method initialize the current object randomly.
* </p> 
*/ 	
    public void Random() {
        g.Random();
        parametersFromGenotype();
    }
	
/** 
* <p> 
* This method performs the local optimization: as this method does not have any
* local optimization defined an exception is thrown.
* </p> 
* @param MAXITER   an integer with the maximum number of iterations in the
*                  local optimization loop
* @param loOptID   the chosen local optimization method
* @throws {@link invalidOptim} for all local optimization method
*/ 	
	public void localOptimization(int MAXITER, int loOptID) throws invalidOptim {
	   throw new invalidOptim("Unsupported local optimization method for the PittsburghClasifier");
	}
}




