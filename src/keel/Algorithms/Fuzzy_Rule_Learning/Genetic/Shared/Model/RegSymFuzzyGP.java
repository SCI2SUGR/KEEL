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
 * @author Written by Luciano Sánchez (Universisty of Oviedo) 25/01/2004
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

import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.OptimLocal.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Algorithms.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Genotypes.*;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Shared.Parsing.*;

public class RegSymFuzzyGP extends GeneticIndividualForSymbRegr {
   
    static double KMIN, KMAX;
    static int constType;
    
    /**
     * <p>
     * Constructor. Generate a new fuzzy system of symbolic regression
     * </p> 
     * @param kmin Minimum k
     * @param kmax Maximum k
     * @param ne Number of inputs
     * @param typectes Type of constant
     * @param NCTES Number of constant
     * @param MAXH Maximum height
     * @param tf Type of fitness
     * @param r Random
     */
    public RegSymFuzzyGP(double kmin, double kmax, int ne, int typectes, int NCTES, int MAXH, int tf, Randomize r) {
	    super(tf);
        KMIN=kmin; KMAX=kmax;
        GenotypeFuzzyGPRegSym gf=new GenotypeFuzzyGPRegSym(kmin,kmax,ne,typectes,NCTES,MAXH,r); 
        g=gf;
        //The object of class Model shares the tree defined in the genotype
        constType=typectes;
        m=new FuzzyGPRegSymModel((NodeExprHold)(gf.getRootNode()),kmin,kmax,1,typectes);
        
    }
    
    /**
     * <p>
     * Constructor. Generate a new fuzzy system of symbolic regession from another one
     * </p>
     * @param p The fuzzy system of symbolic regession
     */
    public RegSymFuzzyGP(RegSymFuzzyGP p) {
	    super(p.fitnessType);
        g=p.g.clone();
        GenotypeFuzzyGPRegSym gf=(GenotypeFuzzyGPRegSym)(g);
        m=new FuzzyGPRegSymModel((NodeExprHold)(gf.getRootNode()),KMIN,KMAX,1,constType);
    }
    
    /**
     * <p>
     * This method clone a fuzzy system of symbolic regession
     * </p>
     */
    
    public GeneticIndividual clone() {
        return new RegSymFuzzyGP(this);
    }
    
    /**
     * <p>
     * This method assing the properties of a fuzzy system of symbolic regession to another one
     * </p>
     * @param p The fuzzy system of symbolic regession
     */  
    public void set(RegSymFuzzyGP p) {
        g=p.g.clone();
        GenotypeFuzzyGPRegSym gf=(GenotypeFuzzyGPRegSym)(g);
        m=new FuzzyGPRegSymModel((NodeExprHold)(gf.getRootNode()),KMIN,KMAX,1,constType);
    }
    
    /**
     * <p>
     * This method generate a genetic individual from a fuzzy system of symbolic regession
     * </p>
     * @return The genetic individua
     */    
    public GeneticIndividual FuzzyGPRegresionSimbolicaClona() {
        return  new RegSymFuzzyGP(this);
    }
    
    /**
     * <p>
     * This method obtain the parameters of a genetic individual from the genotype
     * </p>
     */
    public void parametersFromGenotype() {
        GenotypeFuzzyGPRegSym gf=(GenotypeFuzzyGPRegSym)(g);
        m=new FuzzyGPRegSymModel((NodeExprHold)(gf.getRootNode()),KMAX,KMIN,1,constType);
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
     * This method implement the mutation operation
     * </p>
     * @param alpha Index mutation
     * @param IDMUTA Type of mutation
     * @throws invalidMutation message if error
     */    
    public void mutation(double alpha,int IDMUTA) throws invalidMutation {
        g.mutation(alpha, IDMUTA);
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
        
        RegSymFuzzyGP f2=(RegSymFuzzyGP)(p2);
        RegSymFuzzyGP f3=(RegSymFuzzyGP)(p3);
        RegSymFuzzyGP f4=(RegSymFuzzyGP)(p4);
        
        g.crossover(f2.g,f3.g,f4.g, IDCRUCE);
        
        //The crossover generates two objects of class 'individuogen'
        f3.parametersFromGenotype();
        f4.parametersFromGenotype();
        
    }
    
    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() { g.debug(); } // Overload debug from IndividuoGenModel
	
    /**
     * <p>
     * This method modifies constant part for this individual with parameter passed
     * </p>
     * @param ctes The constant 
     */
	public void setConsts(double[] ctes) {
	    // It modifies constant part for this individual with  parameter "ctes"
            GenotypeFuzzyGPRegSym gf=(GenotypeFuzzyGPRegSym)g;
            gf.setChain(ctes);
	}
	
	/**
	 * <p>
	 * This methods return a copy of the constant part
	 * </p>
	 * @return The constant part
	 */
	public double[] getConsts() {
	    //It returns a copy of constant part
            GenotypeFuzzyGPRegSym gf=(GenotypeFuzzyGPRegSym)g;
	    return gf.getChainValue();
	}
    
	/**
	 * This method returns information about used constant
	 * @return A vector marked in the position where constants were used
	 */
    public boolean[] getUsedConsts() {
        //It marks constants in string that appears in genotype
        GenotypeFuzzyGPRegSym gf=(GenotypeFuzzyGPRegSym)g;
        return gf.getUsedConstants();
        
    }

	/**
	 * <p>
	 * This method calculates a local optimization
	 * </p>
	 * @param MAXITER Maximum iterations
	 * @param idoptimization Type of optimization
	 * @throws invalidOptim Message if error
	 */    
	public void localOptimization(int MAXITER, int idoptimization) throws invalidOptim {
	   if (idoptimization!=OperatorIdent.AMEBA) 
		throw new invalidOptim("Optimizacion local no implementada en RegSymFuzzyGP");
	   
	   double consts[] = getConsts();
           int total=0;
           boolean usedctes[] = getUsedConsts();
           for (int i=0;i<usedctes.length;i++) 
               if (usedctes[i]) total++;
           
           // There's nothing to optimize
           if (total==0) return;
           
           FUNGPRS f = new FUNGPRS(this,usedctes,consts);
	   Ameba ameba = new Ameba();
	   ameba.itera(f, consts, MAXITER*total);
	   setConsts(consts);
	}
    
}




