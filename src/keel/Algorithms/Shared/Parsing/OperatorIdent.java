/** 
* <p> 
* @author Written by Luciano Sánchez (University of Oviedo) 26/02/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Shared.Parsing;


public class OperatorIdent {
	/**
	 * <p>
	 * Class with constant identifiers of operators and methods.
	 * </p>
	 */

     // Identifiers for crossover and mutation operators  
	public static final int GENERICROSSOVER = 1;
	public static final int GENERICMUTATION = 2;
	public static final int GAPCROSSGA = 1001;
    public static final int GAPCROSSGP = 1002;
	public static final int GAPMUTAGA = 1003;
    public static final int GAPMUTAGP = 1004;
	
     //Identifiers for local optimizations	
	public static final int AMEBA = 2001;
	
	public static final int GI_STANDARD = 0;
	public static final int GI_CUSTOM_CESAR = 1;


}
