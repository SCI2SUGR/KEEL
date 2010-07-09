/**
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Discretizers.UniformWidth_Discretizer;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Parameters;


public class UniformWidthDiscretizer extends Discretizer {
/**
 * <p>
 * This class implements the Uniform Width discretizer.
 * </p>
 */
	
	double numCP;
	
	/**
	 * <p>
	 * Constructor of the class, initializes the numCP attribute
	 * </p>
	 * @param _numCP
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
