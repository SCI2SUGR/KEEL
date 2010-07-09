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
