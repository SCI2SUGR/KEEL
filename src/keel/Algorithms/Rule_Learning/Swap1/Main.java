/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

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

