/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Rule_Learning.Swap1;


/**
 *
 * @author halos
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	try {
	    // TODO code application logic here
	    
	    if(args.length!=1){
	    	System.out.println("Error en linea de comandos");
	    	System.exit(1);
	    }
	    else{
		Parameters.doParse(args[0]);
		swap1 sw = new swap1(Parameters.trainInputFile,Parameters.testInputFile);

		sw.train();

		System.out.println();
		System.out.println("---------------------------------------------");
		System.out.println("Inicio de las pruebas");
		System.out.println("---------------------------------------------");
		System.out.println();

		sw.test();
	    }
	} catch (ExNotNominalAttr ex) {
	    System.out.println("Se han encontrado atributos no nominales.\nFin de la aplicacio³n");
	}


    }

}
