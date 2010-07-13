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
 * MachineAccuracy.java
 *
 * Copyright (C) 2002-2006 Alexei Drummond and Andrew Rambaut
 *
 * This file is part of BEAST.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership and licensing.
 *
 * BEAST is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *  BEAST is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BEAST; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package keel.Algorithms.Preprocess.Missing_Values.BPCA;


/**
 * determines machine accuracy
 *
 * @version $Id: MachineAccuracy.java,v 1.1 2010-07-03 22:51:28 keel Exp $
 *
 * @author Korbinian Strimmer
 * @author Alexei Drummond
 */
public class MachineAccuracy
{
	//
	// Public stuff
	//

	/** machine accuracy constant */
	public static double EPSILON = 2.220446049250313E-16;
	
	public static double SQRT_EPSILON = 1.4901161193847656E-8;
	public static double SQRT_SQRT_EPSILON = 1.220703125E-4;

	/** compute EPSILON from scratch */
	public static double computeEpsilon()
	{
		double eps = 1.0;

		while( eps + 1.0 != 1.0 )
		{
			eps /= 2.0;
		}
		eps *= 2.0;
		
		return eps;
	}

	/**
	 * @return true if the relative difference between the two parameters
	 * is smaller than SQRT_EPSILON.
	 */
	public static boolean same(double a, double b) {
		return Math.abs((a/b)-1.0) <= SQRT_EPSILON;
	}
}

