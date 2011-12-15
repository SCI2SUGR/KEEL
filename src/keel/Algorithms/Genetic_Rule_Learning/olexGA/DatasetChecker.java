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

package keel.Algorithms.Genetic_Rule_Learning.olexGA;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.DatasetException;


/**
 * Class to check if dataset is correct
 * 
 * <p>
 * @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
 * @version 0.1
 * @since JDK 1.5
 *</p>
 */
public class DatasetChecker {

	public static boolean checkUniqueOutput(Dataset ds) {

		return (Attributes.getOutputAttributes().length != 1 ? false : true);

	}

	public static boolean checkClassType(Dataset ds) {
		return (Attributes.getOutputAttribute(0).getType() == Attribute.NOMINAL ? true
				: false);
	}

	public static boolean testWithFail(Dataset ds) throws DatasetException {

		if (!checkUniqueOutput(ds)) {
			return false;
		}
		if (!checkClassType(ds)) {
			return false;
		}

		return true;
		// ds.deleteWithMissingClass();
	}

//	private static boolean check(Dataset ds) throws DatasetException {
//
//		Vector list = ds.attributes;
//		for (int at_i = 0; at_i < list.size(); at_i++) {
//			OlexGA_Attribute attribute =  (OlexGA_Attribute) list
//					.get(at_i);
//
//			if (attribute.isDiscret())
//				if (attribute.getNumNominalValues() != 2) {
//					throw new DatasetException(
//							"OlexGA supports only binary nominal attributes",
//							new Vector());
//				}
//			String first = attribute.getNominalValue(0);
//			String second = attribute.getNominalValue(1);
//
//			if ((first.equalsIgnoreCase("T") && second.equalsIgnoreCase("F"))
//					|| (first.equalsIgnoreCase("F") && second
//							.equalsIgnoreCase("T"))
//					|| (first.equalsIgnoreCase("1") && second
//							.equalsIgnoreCase("0"))
//					|| (first.equalsIgnoreCase("0") && second
//							.equalsIgnoreCase("1"))
//					|| (first.equalsIgnoreCase("true") && second
//							.equalsIgnoreCase("false"))
//					|| (first.equalsIgnoreCase("false") && second
//							.equalsIgnoreCase("true"))
//					|| (first.equalsIgnoreCase("n") && second
//							.equalsIgnoreCase("y"))
//					|| (first.equalsIgnoreCase("y") && second
//							.equalsIgnoreCase("n"))
//					|| (first.equalsIgnoreCase("no") && second
//							.equalsIgnoreCase("yes"))
//					|| (first.equalsIgnoreCase("yes") && second
//							.equalsIgnoreCase("no"))) {
//			} else {
//				throw new DatasetException(
//						"Nominal Attribute "
//								+ attribute.getName()
//								+ " not supported. \n OlexGA recognizes nominal attributes of the following types: "
//								+ "{0,1},  {t,f}, {true,false}, {y,n}, {yes,no}",
//						new Vector());
//			}
//
//		}
//		return true;
//	}

}
