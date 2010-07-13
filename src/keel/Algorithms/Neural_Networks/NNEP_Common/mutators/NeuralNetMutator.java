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

