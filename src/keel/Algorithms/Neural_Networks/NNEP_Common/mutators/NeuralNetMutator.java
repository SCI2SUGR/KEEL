package keel.Algorithms.Neural_Networks.NNEP_Common.mutators;



import keel.Algorithms.Neural_Networks.NNEP_Common.INeuralNetSpecies;
import keel.Algorithms.Neural_Networks.NNEP_Common.NeuralNetIndividual;
import net.sf.jclec.ISpecies;
import net.sf.jclec.base.AbstractMutator;

/**  
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Written by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @version 0.1
 * @since JDK1.5
 * @param <I> Type of individuals to mutate
 * </p>
 */

public abstract class NeuralNetMutator<I extends NeuralNetIndividual> extends AbstractMutator<I>{
	
	/**
	 * <p>
	 * NeuralNetIndividual mutator.
	 * </p>
	 */
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Attributes
	/////////////////////////////////////////////////////////////////

	/** Individuals species */
	
	protected INeuralNetSpecies<I> species;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Empty constructor
	 */
	
	public NeuralNetMutator() 
	{
		super();
	}
	
	/////////////////////////////////////////////////////////////////
	// -------------------------- Overwriting AbstractMutator methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 *  This method prepares the mutation, stablising the species
	 * </p>
	 */
	
	@Override	
	protected void prepareMutation()
	{
		ISpecies<I> species = context.getSpecies();
		if (species instanceof INeuralNetSpecies){
			// Set individuals speciess
			this.species = (INeuralNetSpecies<I>) species;		
		}
		else {
			throw new IllegalStateException("Invalid species in context");
		}
	}
}
