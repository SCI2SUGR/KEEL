/**
 * <p>
 * @author Written by Julián Luengo Martín 08/02/2007
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Genetic_Rule_Learning.OIGA;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Oiga oiga;
		if (args.length != 1)
            System.err.println("Error. Only one parameter is needed.");
		
		oiga = new Oiga(args[0]);
		
		oiga.run();
	}

}
