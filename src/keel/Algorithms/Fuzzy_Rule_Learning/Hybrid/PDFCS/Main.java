/**
 * @author Julian Luengo (julianlm@decsai.ugr.es) as a wrapper of SMO classifier from
 * WEKA to KEEL. 
 * @version 0.1
 * @since JDK 1.5
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Hybrid.PDFCS;


public class Main {

	public static void main (String args[]) {

		PDFC model;

		if (args.length != 1)
			System.err.println("Error. Only a parameter is needed.");
		else {
			model = new PDFC (args[0]);
			model.runModel();

		}
	}
}
