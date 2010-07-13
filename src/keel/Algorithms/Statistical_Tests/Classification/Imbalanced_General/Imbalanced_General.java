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
* @author Written by Luciano Sanchez (University of Oviedo) 01/01/2004
* @author Modified by Amelia Zafra (University of Granada) 01/01/2007
* @author Modified by Jose Otero (University of Oviedo) 01/12/2008
* @author Modified by Victoria Lopez (University of Granada) 05/06/2010
* @version 1.0
* @since JDK1.5
* </p>
*/
package keel.Algorithms.Statistical_Tests.Classification.Imbalanced_General;
import keel.Algorithms.Statistical_Tests.Shared.*;
import keel.Algorithms.Shared.Parsing.*;
import org.core.*;



public class Imbalanced_General {
	/**
	* <p>
	* This class has only a main method that calls Clasif_General output method for classification problems, defined in StatTest
	* </p>
	*/
	static Randomize rand;
	/**
	* <p>
	* This method reads a configuration file and calls statisticalTest with appropriate values to run
	* Clasif_General output module for classification problems, defined in StatTest class
	* @param args A string that contains the command line arguments
	* </p>
	*/	
	public static void main(String args[]) {
		
		boolean tty=false;
		ProcessConfig pc=new ProcessConfig();
		System.out.println("Reading configuration file: "+args[0]);
		if (pc.fileProcess(args[0])<0) return;
		int algorithm=pc.parAlgorithmType;
		rand=new Randomize();
		rand.setSeed(pc.parSeed);
		
		ParseFileList pl=new ParseFileList();
		pl.statisticalTest(StatTest.generalI,tty,pc);
		
	}
}

