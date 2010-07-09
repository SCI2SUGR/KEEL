/**
 * @author Julian Luengo (julianlm@decsai.ugr.es) as a wrapper of SMO classifier from
 * WEKA to KEEL. 
 * @version 0.1
 * @since JDK 1.5
 */

package keel.Algorithms.SVM.SMO;


public class Main {

	public static void main (String args[]) {

		SMO model;

		if (args.length != 1)
			System.err.println("Error. Only a parameter is needed.");
		else {
			model = new SMO (args[0]);
			model.runModel();

		}
	}
}
