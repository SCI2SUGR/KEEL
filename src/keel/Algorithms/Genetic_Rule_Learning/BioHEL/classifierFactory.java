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

package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

public class classifierFactory {
	
	int classifierType;
	final static int KR_HYPERRECT_LIST_REAL = 0;
	final static int KR_HYPERRECT_LIST = 1;

	public classifierFactory(){
		
		classifierType = -1;
		
		Attribute[] attrs = Attributes.getInputAttributes();

		for(int i = 0 ; i < Parameters.NumAttributes ; ++i)
			if(attrs[i].getType() == Attribute.NOMINAL)
				classifierType = KR_HYPERRECT_LIST;
		
		if(classifierType == -1)
			classifierType = KR_HYPERRECT_LIST_REAL;
	}	

		public classifier createClassifier(){

			if (classifierType == KR_HYPERRECT_LIST)
				return new classifier_hyperrect_list();
			if (classifierType == KR_HYPERRECT_LIST_REAL)
				return new classifier_hyperrect_list_real();

			return null;
		}


		public classifier cloneClassifier(classifier orig){
			
			if (classifierType == KR_HYPERRECT_LIST)
				return new classifier_hyperrect_list((classifier_hyperrect_list) orig);
				
			if (classifierType == KR_HYPERRECT_LIST_REAL)
				return new classifier_hyperrect_list_real((classifier_hyperrect_list_real) orig);

			return null;
		}
		
}
