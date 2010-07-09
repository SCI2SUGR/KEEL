
/**
 * author Julian Luengo (julianlm@decsai.ugr.es) as a wrapper of Logistic classifier from
 * WEKA to KEEL. 
 * @version 0.1
 * @since JDK 1.5
 */

package keel.Algorithms.Statistical_Classifiers.Logistic;


public class Main {

	public static void main (String args[]) {
		Logistic model;
		if (args.length != 1)
			System.err.println("Error. Only a parameter is needed.");
		else {

			model = new Logistic (args[0]);
			model.runModel();

		}
	}
}

