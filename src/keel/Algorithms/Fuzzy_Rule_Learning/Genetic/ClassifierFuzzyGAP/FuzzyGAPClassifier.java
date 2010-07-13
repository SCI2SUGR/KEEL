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
* @author Written by Luciano Sanchez (University of Oviedo) 21/01/2004 
* @author Modified by J.R. Villar (University of Oviedo) 19/12/2008
* @version 1.0 
* @since JDK1.4 
* </p> 
*/ 
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierFuzzyGAP;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Algorithms.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Genotypes.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Classifier.*;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import org.core.*;


public class FuzzyGAPClassifier extends GeneticIndividualForClassification {
/** 
* <p> 
* FuzzyGAPClassifier is designed to allow a Fuzzy Classifier evolve by means of
* an Genetic Algorithm and Programming (GAP). This class is a specification of
* class {@link GeneticIndividualForClassification}.
* 
* </p> 
*/ 
	//The input space partitions
    static FuzzyPartition[] A;
 	//The class variable or output variable partitions or classes
    static FuzzyPartition C;
/** 
* <p> 
* This private method returns the Fuzzy label which corresponds with the
* nlabel linguistic label of a certain input variable. 
* </p> 
* @param nv       an integer specifying the input variable 
* @param nlabel   an integer specifying the linguistic label to return
* @return     the corresponding {@link Fuzzy} linguistic label 
*/ 	
    private Fuzzy getLabel(int nv, int nlabel) { return A[nv].getComponent(nlabel); }
/** 
* <p> 
* This private method returns the number of defined labels for a certain input 
* variable. 
* </p> 
* @param nv       an integer specifying the input variable 
* @param nlabel    to return
* @return     an integer with the number of the defined linguistic labels
*/ 	
    private int getNumLabels(int nv) { return A[nv].size(); }
    
/** 
* <p> 
* A constructor of the class specifying the input and class variables 
* partitions, the maximum height of the tree, the fitness type and the 
* Randomize object to use.
* </p> 
* @param a       the input variables {@link FuzzyPartition} array 
* @param b       the class variable {@link FuzzyPartition} 
* @param MAXH    the maximum height of the tree
* @param tf      the type of Fitness function to evaluate the individual
* @param r       the Randomize object to be used in the genetic evolution
*/ 	
    public FuzzyGAPClassifier(FuzzyPartition[] a, FuzzyPartition b, int MAXH, int tf, Randomize r) {
	    super(tf);
        A=a;
        C=b;
        GenotypeFuzzyGAP gf=new GenotypeFuzzyGAP(A,C,MAXH,r);
        g=gf;
        //The object of class Clasifica shares the tree defined in the genotype
        c=new FuzzyFGPClassifier((NodeRuleBase)(gf.getRootNode()),C);
    }
    
/** 
* <p> 
* The copy constructor for this class. 
* </p> 
* @param p       the {@link FuzzyGAPClassifier} to be copied 
*/ 	
    public FuzzyGAPClassifier(FuzzyGAPClassifier p) {
	    super(p.fitnessType);
        g=p.g.clone();
        GenotypeFuzzyGAP gf=(GenotypeFuzzyGAP)(g);
        c=new FuzzyFGPClassifier((NodeRuleBase)(gf.getRootNode()),C);
    }
    
/** 
* <p> 
* This method copies the given FuzzyGAPClassifier in the current object.
* </p> 
* @param p   the {@link FuzzyGAPClassifier} object to be assigned to the current one 
*/ 	
    public void set(FuzzyGAPClassifier p) {
        g=p.g.clone();
        GenotypeFuzzyGAP gf=(GenotypeFuzzyGAP)(g);
        c=new FuzzyFGPClassifier((NodeRuleBase)(gf.getRootNode()),C);
    }
    
/** 
* <p> 
* This method clones the current object.
* </p> 
* @return     a {@link GeneticIndividual} object which is a copy of the current object
*/ 	
    public GeneticIndividual clone() {
        return  new FuzzyGAPClassifier(this);
    }
    
/** 
* <p> 
* This method sets the current classifier of according to it's genotype.
* </p> 
*/ 	
	public void parametersFromGenotype() {
        GenotypeFuzzyGAP gf=(GenotypeFuzzyGAP)(g);
        c=new FuzzyFGPClassifier((NodeRuleBase)(gf.getRootNode()),C);
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
* This method performs the mutation genetic operation of the current 
* FuzzyGAPClassifier. 
* This methods updates its classifier according its genotype.
* </p> 
* @param alpha   this parameter is fixed according to {@link GenotypeFuzzyGAP}
* @param mutationID the type of mutation operation as stated in {@link GenotypeFuzzyGAP}.
* @throws {@link invalidOptim} if non supported mutationID
*/ 	
    public void mutation(double alpha, int mutationID) throws invalidMutation {
        g.mutation(alpha,mutationID);
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
* @param croosoverID this value should be fixed to:
*	{@link OperatorIdent.GAPCRUCEGA} for a genetic algorithm crossover
*	{@link OperatorIdent.GAPCRUCEGP} for genetic programming crossover
* @throws {@link invalidCrossover} in case of invalid crossoverID
*/ 	
    public void crossover(GeneticIndividual p2, GeneticIndividual p3, GeneticIndividual p4, int croosoverID) throws invalidCrossover {
        
        FuzzyGAPClassifier f2=(FuzzyGAPClassifier)(p2);
        FuzzyGAPClassifier f3=(FuzzyGAPClassifier)(p3);
        FuzzyGAPClassifier f4=(FuzzyGAPClassifier)(p4);
        
        g.crossover(f2.g,f3.g,f4.g,croosoverID);
        
        //The crossover generates two objects of class GeneticIndividual
        f3.parametersFromGenotype();
        f4.parametersFromGenotype();
        
    }
    
/** 
* <p> 
* This method performs the debug operation, which allow to analyze the behaviour
* of the learning process.
* </p> 
*/ 	
    public void debug() { c.debug(); } // Overload debug from GeneticIndividualForClassification
	
/** 
* <p> 
* This method performs the local optimization: as this method does not have any
* local optimization defined an exception is thrown.
* </p> 
* @param MAXITER   an integer with the maximum number of iterations in the
*                  local optimization loop
* @param loOptID   the chosen local optimization method
* @throws {@link invalidOptim}
*/ 	
	public void localOptimization(int MAXITER, int loOptID) throws invalidOptim {
	   throw new invalidOptim("Local optimization is not developed for the FuzzyGAPClassifier class");
	}

    
};








