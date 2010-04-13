/**
 * <p>
 * @author Written by Julián Luengo Martín 08/02/2007
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Genetic_Rule_Learning.COGIN;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Cogin cogin;
		if (args.length != 1)
            System.err.println("Error. Only a parameter is needed.");
		
		cogin = new Cogin(args[0]);
		
		cogin.run();
	}
}
