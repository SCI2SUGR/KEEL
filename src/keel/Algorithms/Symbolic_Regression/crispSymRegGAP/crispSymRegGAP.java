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
* @author Written by Luciano Sánchez (University of Oviedo) 27/02/2007
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Symbolic_Regression.crispSymRegGAP;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;
import keel.Algorithms.Symbolic_Regression.Shared.*;
import keel.Algorithms.Shared.Parsing.*;
import org.core.*;



public class crispSymRegGAP {
	/**
	 * Wrapper for symbolicRegression with crisp values and based on GAP (Genetic Algorithm Programming) paradigm.
	 */
	//Random number generator
	static Randomize rand;
	/**
	 * <p>
	 * Loads the configuration for SA algorithm (with crisp sets) from file args[0] using class ProcessConfig.
	 * </p>
	 * @param args command line parameters. args[0] must contains the configuration file.
	 */
	public static void main(String args[]) {
		
		boolean tty=false;
		ProcessConfig pc=new ProcessConfig();
		System.out.println("Reading configuration file: "+args[0]);
		if (pc.fileProcess(args[0])<0) return;
		int result=pc.parAlgorithmType;
		rand=new Randomize();
		rand.setSeed(pc.parSeed);
		
		ParseFileRegSym pl=new ParseFileRegSym();
		pl.symbolicRegressionFuzzyGAP(FuzzyRegressor.Crisp,tty,pc,rand);
		
	}
}

