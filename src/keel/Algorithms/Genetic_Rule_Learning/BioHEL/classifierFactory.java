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