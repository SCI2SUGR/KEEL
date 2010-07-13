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

/*
 * UniformWidthDiscretizer.java
 *
 */

/**
 *
 */

package keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Discretizers.UniformWidth_Discretizer;

import java.util.*;
import keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Discretizers.Basic.*;

public class UniformWidthDiscretizer extends Discretizer {
	double numCP;

	public UniformWidthDiscretizer(int _numCP) {
		numCP=_numCP;
	}

	protected Vector discretizeAttribute(int attribute,int []values,int begin,int end) {
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

