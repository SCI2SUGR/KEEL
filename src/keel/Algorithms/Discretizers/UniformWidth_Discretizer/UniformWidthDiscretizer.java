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

package keel.Algorithms.Discretizers.UniformWidth_Discretizer;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Parameters;

/**
 * <p>
 * This class implements the Uniform Width discretizer.
 * </p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004 </p>
 * Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.5
 */
public class UniformWidthDiscretizer extends Discretizer {
	
	double numCP;
	
	/**
	 * <p>
	 * Constructor of the class, initializes the numCP attribute
	 * </p>
	 * @param _numCP number of cutpoints
	 */
	public UniformWidthDiscretizer(int _numCP) {
		if (_numCP > 0)
			numCP=_numCP;
		else 
			numCP = (Parameters.numInstances / (100)) > Parameters.numClasses?Parameters.numInstances / (100):Parameters.numClasses;
	}

	/**
	 * <p>
	 * Returns a vector with the discretized values.
	 * @param attribute
	 * @param values
	 * @param begin not used
	 * @param end
	 * @return vector with the discretized values
	 * </p>
	 */
	protected Vector discretizeAttribute(int attribute, int []values, int begin, int end) {
		double min=realValues[attribute][values[0]];
		double max=realValues[attribute][values[end]];
		double intervalWidth=(max-min)/(numCP+1);
		Vector cp=new Vector();
		double val=min;
		
		for(int i=0;i<numCP;i++) {
			val+=intervalWidth;
			cp.addElement(new Double(val));	
		}
		return cp;
	}
}

