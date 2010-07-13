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

/**
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 23/12/2008
 * @author Modified by Jose A. Saez Munoz (ETSIIT, Universidad de Granada - Granada) 10/09/10
 * 
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.MPLCS;

import keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Globals.*;

public class Main {
	
	static double accTrain, accTest;
	static int numAttsBest = 0, numRulesBest;
	

  /** Creates a new instance of Control */
  public Main() {
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    long t1 = System.currentTimeMillis();

    ParserParameters.doParse(args[0]);

    LogManager.initLogManager();
    Rand.initRand();

    GA ga = new GA();
    ga.initGA();
    ga.run();

    LogManager.println(Chronometer.getChrons());
    long t2 = System.currentTimeMillis();
    LogManager.println("Total time: " + ( (t2 - t1) / 1000.0));

    LogManager.closeLog();
    
    
    System.out.print("\n\n************************************************");
    System.out.print("\nPorcertanje acierto train:\t"+accTrain);
    System.out.print("\nPorcertanje acierto test:\t"+accTest);
    System.out.print("\nNumero de reglas:\t\t"+numRulesBest);
    System.out.print("\nMedia de atributos/regla:\t"+((double)numAttsBest/(double)numRulesBest));
    System.out.print("\nTiempo:\t\t\t\t"+( (t2 - t1) / 1000.0) + " seg.");
    System.out.print("\n************************************************\n\n");
    
    
  }

}

