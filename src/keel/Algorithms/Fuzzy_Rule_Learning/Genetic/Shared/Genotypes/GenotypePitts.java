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
 * @author Written by Luciano Sanchez (University of Oviedo) 20/01/2004 
 * @author Modified by J.R. Villar (University of Oviedo) 18/12/2008
 * @version 1.0 
 * @since JDK1.4 
 * </p> 
 */ 

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Genotypes;
import java.util.Vector;
import org.core.*;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

public class GenotypePitts extends  Genotype {
/** 
* <p> 
* GenotypePitts is the base clase to represent the genotype when a fuzzy
* model is to be learned with the genetic algorithm and the Pittsburg
* approach.
* 
* </p> 
*/ 
 
    //The allele for the involved rule allele
    private int[] involvedRuleAllele;
	//The allele for the weight of the rules
    private double[] ruleWeightAllele;
	//The number of consequents
    private int numberOfConsequents;

 /**
* <p>
* Class constructor with the following parameters:
* </p>
* @param n the number of alleles
* @param nlables the number of output available labels
* @param r the {@link Randomize} object
*/
    public GenotypePitts(int n, int nlabels, Randomize r) {
        super(r);
        numberOfConsequents=nlabels;
        // weights between  0 and 1
        ruleWeightAllele=new double[n];
        // Consecuents values are between 0 and the output label
        involvedRuleAllele=new int[n];
    }


/**
* <p>
* The copy constructor.
* </p>
* @param p the {@link GenotypePitts} to be copied
*/
    public GenotypePitts(GenotypePitts p) {
        super(p.rand);
        involvedRuleAllele = new int[p.involvedRuleAllele.length];
        for (int i=0;i<involvedRuleAllele.length;i++) involvedRuleAllele[i]=p.involvedRuleAllele[i];
        ruleWeightAllele = new double[p.ruleWeightAllele.length];
        for (int i=0;i<ruleWeightAllele.length;i++) ruleWeightAllele[i]=p.ruleWeightAllele[i];
        numberOfConsequents=p.numberOfConsequents;
    }

/**
* <p>
* This method copies the given parameter into the current object.
* </p>
* @param p the {@link GenotypePitts} to be copied
*/
    public void set(GenotypePitts p) {
        involvedRuleAllele = new int[p.involvedRuleAllele.length];
        for (int i=0;i<involvedRuleAllele.length;i++) involvedRuleAllele[i]=p.involvedRuleAllele[i];
        ruleWeightAllele = new double[p.ruleWeightAllele.length];
        for (int i=0;i<ruleWeightAllele.length;i++) ruleWeightAllele[i]=p.ruleWeightAllele[i];
        numberOfConsequents=p.numberOfConsequents;
    }
    
/**
* <p>
* This method is intended for generating a perfect copy of the current Genotype.
* </p>
* @return the newly created {@link Genotype} which is a perfect copy of current individual
*/
    public Genotype clone() {
        return new GenotypePitts(this);
    }
    
/**
* <p>
* This method determines if the given {@link Genotype} is of the same
* type thant the current object.
* </p>
* @param p the {@link Genotype} to be compared
* @return true if both objects are related
*/
    public boolean isRelated(Genotype p) {
        if (!(p instanceof GenotypePitts)) return false;
        return true;
    }
    

/**
* <p>
* The method intended to randomly initialize a Genotype and then the corresponding individual.
* </p>
*/
    public void Random() {
        // weights between  0 and 1
        for (int i=0;i<ruleWeightAllele.length;i++)
            ruleWeightAllele[i]=rand.Rand();

        // Consecuents values are between 0 and the output label
        for (int i=0;i<involvedRuleAllele.length;i++)
            involvedRuleAllele[i]=(int)(numberOfConsequents*(rand.Rand()));
        
    }

/**
* <p>
* The method for carrying out the crossover genetic operations.
* </p>
* @param padre2 the second parent in the crossover operation, it's an {@link Genotype} object
* @param offspng1 the {@link Genotype} object with the first offspring
* @param offspng2 the {@link Genotype} object with the second offspring
* @param crossoverID an int with the crossover operation to be carried out:
*                    {@link OperatorIdent.CRUCEGENERICO} for genetic algorithm crossover
* @throws {@link invalidCrossover} if crossoverID is not valid
*/
    public void crossover(Genotype padre2, Genotype offspng1, Genotype offspng2, int crossoverID) throws invalidCrossover {
        // Rules weights: Uniform Arithmetic Crossover
		
		if (crossoverID!=OperatorIdent.GENERICROSSOVER) throw new invalidCrossover();

        GenotypePitts p2=(GenotypePitts)padre2;
        GenotypePitts of1=(GenotypePitts)offspng1;
        GenotypePitts of2=(GenotypePitts)offspng2;

        double alpha=1.5*(rand.Rand()-0.5);
        for (int i=0;i<ruleWeightAllele.length;i++) {
            double val1=ruleWeightAllele[i]+alpha*(p2.ruleWeightAllele[i]-ruleWeightAllele[i]);
            if (val1<0) val1=0;
            if (val1>1) val1=1;
            of1.ruleWeightAllele[i]=val1;

            double val2=p2.ruleWeightAllele[i]+alpha*(ruleWeightAllele[i]-p2.ruleWeightAllele[i]);
            if (val2<0) val2=0;
            if (val2>1) val2=1;
            of2.ruleWeightAllele[i]=val2;
        }

        // Consecuents: two points crossover
        int pos1=(int)(rand.Rand()*involvedRuleAllele.length);
        int pos2=(int)(rand.Rand()*involvedRuleAllele.length);
        if (pos2<pos1) { int tmp=pos2; pos2=pos1; pos1=tmp; }
        for (int i=0;i<involvedRuleAllele.length;i++) {
            if (i>=pos1 && i<=pos2) {
                of2.involvedRuleAllele[i]=involvedRuleAllele[i];
                of1.involvedRuleAllele[i]=p2.involvedRuleAllele[i];
            } else {
                of1.involvedRuleAllele[i]=involvedRuleAllele[i];
                of2.involvedRuleAllele[i]=p2.involvedRuleAllele[i];
            }
        }
    }
    
/**
* <p>
* Method for carrying out the mutation genetic operations.
* </p>
* @param alpha double value kept for compatibility, not used.
* @param mutationID an int with the crossover operation to be carried out:
*                   {@link OperatorIdent.MUTACIONGENERICA} for the genetic algorithm mutation
* @throws {@link invalidMutation} if mutationID is not valid
*/
    public void mutation(double alpha, int mutationID) throws invalidMutation {
	
		if (mutationID!=OperatorIdent.GENERICMUTATION) throw new invalidMutation();
	
        GenotypePitts randomGenotype=new GenotypePitts(ruleWeightAllele.length,numberOfConsequents,rand);
        randomGenotype.Random();

        // Weight rules: Uniform Arithmetic Crossover
        for (int i=0;i<ruleWeightAllele.length;i++) {
            double val=ruleWeightAllele[i]+alpha*(randomGenotype.ruleWeightAllele[i]-ruleWeightAllele[i]);
            if (val<0) val=0;
            if (val>1) val=1;
            ruleWeightAllele[i]=val;
        }
        // Consecuents: Chaing an elelo
        int pos=(int)(rand.Rand()*involvedRuleAllele.length);
        involvedRuleAllele[pos]=(int)(rand.Rand()*numberOfConsequents);
    }

/**
* <p>
* This method is intended for printing debug information.
* </p>
*/
    public void debug() {
        String s="GenotypePitts [";
        for (int i=0;i<involvedRuleAllele.length;i++) s+=(involvedRuleAllele[i]+" ");
        s+="] [";
        for (int i=0;i<ruleWeightAllele.length;i++) s+=(ruleWeightAllele[i]+" ");
        s+="]";
        System.out.println(s);
    }
      
/**
* <p>
* This method returns a rule in the rule base.
* </p>
* @param n index of the desired rule
* @returnthe int value indexin the used rule in the rule base
*/
    public int getInvolvedRule(int n) {
        return involvedRuleAllele[n];
    }
    
/**
* <p>
* This method updates the rule base.
* </p>
* @param n the index of the rule in the FRBS to update
* @param v the new rule, an int value
*/
    public void setInvolvedRule(int n, int v) {
        involvedRuleAllele[n]=v;
    }
    
/**
* <p>
* This method returns the weight of a rule
* </p>
* @param n the index of the rule which weight is desired
* @return the dobule value with the corresponding weigth
*/
    public double getRuleWeight(int n)  {
        return ruleWeightAllele[n];
    }
    
/**
* <p>
* This method updates the weight of a rule.
* </p>
* @param n the index of the rule to update
* @param v the double value to fix in the corresponding weight
*/
    public void setRuleWeight(int n, double v) {
        ruleWeightAllele[n]=v;
    }
    
/**
* <p>
* This method returns the number of rules of the FRBS
* </p>
* @return the int with the size of the rule base
*/
    public int size()  {
        return involvedRuleAllele.length;
    }

}

