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

package keel.Algorithms.Neural_Networks.gmdh;

import java.util.Vector;

import keel.Dataset.Attributes;
import keel.Dataset.InstanceSet;

/**
 * <p>
 * Dataset interface simplification
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */

public class OpenDataset {
	
	/** Number of examples*/
	private int ndatos; 
	
	/** Number of variables */
	private int nvariables; 

	/** Number of inputs */
	private int nentradas; 
	
	/**  Number of outputs */
	private int nsalidas; 
	
	/** Number of classes */
	private int nclases; 

	/** Set of data instances */
	private InstanceSet IS;
	
	/**
	 * <p>
	 * Gets the number of examples
	 * </p>
	 * @return number of examples
	 */
	public int getndatos() {
		return ndatos;
	}

	/**
	 * <p>
	 * Gets the number of variables
	 * </p>
	 * @return number of variables
	 */
	public int getnvariables() {
		return nvariables;
	}

	/**
	 * <p>
	 * Gets the number of inputs
	 * </p>
	 * @return number of inputs
	 */
	public int getnentradas() {
		return nentradas;
	}

	/**
	 * <p>
	 * Gets the number of outputs
	 * </p>
	 * @return number of outputs
	 */
	public int getnsalidas() {
		return nsalidas;
	}
	
	/**
	 * <p>
	 * Return type (0 nominal, 1 integer, 2 float,..) of attribute at index.
	 * </p>
	 * @param index 
	 * @return type of attribute.
	 */
	public int getTiposAt (int index) {
		return Attributes.getAttribute(index).getType();
	}
	
	/**
	 * <p>
	 * Returns the nominal value list of an attribute
	 * </p>
	 * @param index Index of the attribute to consider
	 * @return Nominal value list
	 */
	public Vector getRangosVar (int index) {
		return Attributes.getAttribute(index).getNominalValuesList();
	}
	
	/**
	 * <p>
	 * Return example data at index in a string separated by comma without spaces
	 * </p>
	 * @param index Index of the instance to return
	 * @return String representation
	 */
	public String getDatosAt (int index) {
		return IS.getInstance(index).toString();
	}
	
	/**
	 * <p>
	 * Constructor
	 * </p>
	 */
	public OpenDataset() {
		// Init a new set of instances
		IS = new InstanceSet();
	}

	/**
	 * <p>
	 * Load a file and parse it.
	 * </p>
	 * @param nfejemplos File to load
	 * @param b Boolean indicating if the file is a training file
	 */
	public void processClassifierDataset(String nfejemplos, boolean b) {
		
		try {
			// Load in memory a dataset that contains a classification problem
			IS.readSet(nfejemplos, b);
			ndatos = IS.getNumInstances();
			nentradas = Attributes.getInputNumAttributes();
			nvariables = nentradas + Attributes.getOutputNumAttributes();
			nsalidas = Attributes.getOutputNumAttributes();
			
			// Check that there is only one output variable and
			// it is nominal

			if (Attributes.getOutputNumAttributes() > 1) {
				System.out.println("This algorithm can not process MIMO datasets");
				System.out.println("All outputs but the first one will be removed");
			}

			boolean noOutputs = false;
			if (Attributes.getOutputNumAttributes() < 1) {
				System.out
						.println("This algorithm can not process datasets without outputs");
				System.out.println("Zero-valued output generated");
				noOutputs = true;
			}
		
		} catch (Exception e) {
			System.out.println("DBG: Exception in readSet");
			e.printStackTrace();
			System.exit(-1);
		}

	}
}

