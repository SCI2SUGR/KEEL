 /** 
 * <p> 
 * @author Written by Luciano Sanchez (University of Oviedo) 20/01/2004 
 * @author Modified by J.R. Villar (University of Oviedo) 18/12/2008
 * @version 1.0
 * @since JDK1.4 
 * </p> 
 */ 
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Genotypes;
import org.core.*;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Shared.Exceptions.*;


public abstract class Genotype {
/** 
* <p> 
* Genotype is the base clase to represent the genotype of any GeneticIndividual. 
* This class is inherit by the following classes: {@link GenotypeFuzzyGAP}, 
* {@link GenotypeFuzzyGP}, {@link GenotypeFuzzyGPRegSym}, and
* {@link GenotypePitts}.
* </p> 
*/ 
	//The random number generator to be used
    public static Randomize rand;
    
/**
* <p>
* Class constructor with the following parameters:
* </p>
* @param r the {@link Randomize} object
*/
    public Genotype(Randomize r) { rand=r; }
/**
* <p>
* abstract method for carrying out the crossover genetic operations.
* </p>
* @param parent the second parent in the crossover operation, it's an {@link Genotype} object
* @param ofs1 the {@link Genotype} object with the first offspring
* @param ofs2 the {@link Genotype} object with the second offspring
* @param crossoverID an int with the crossover operation to be carried out. Refer to inherited classes for
*                    specification of the valid values
* @throws {@link invalidCrossover} if crossoverID is not valid
*/
    public abstract void crossover(Genotype parent, Genotype ofs1, Genotype ofs2, int crossoverID) throws invalidCrossover;
/**
* <p>
* abstract method to randomly initialize a Genotype and then the corresponding individual.
* </p>
*/
	public abstract void Random();
/**
* <p>
* abstract method for carrying out the mutation genetic operations.
* </p>
* @param alpha double needed for some kind of mutations. Refer to inherited classes for
*                    specification of the valid values.
* @param mutationID an int with the mutation operation to be carried out. Refer to inherited classes for
*                    specification of the valid values.
* @throws {@link invalidMutation} if mutationID is not valid
*/
    public abstract void mutation(double alpha, int mutationID) throws invalidMutation;
/**
* <p>
* abstract method for generating a perfect copy of the current Genotype.
* </p>
* @return the newly created {@link Genotype} which is a perfect copy of current individual
*/
    public abstract Genotype clone();
/**
* <p>
* abstract method for printing debug information.
* </p>
*/
    public abstract void debug();
/**
* <p>
* abstract method for determining if the given {@link Genotype} is similar to current one.
* </p>
* @return true if both {@link Genotype} are similar, otherwise it returns in false.
*/
    public abstract boolean isRelated(Genotype g);
}