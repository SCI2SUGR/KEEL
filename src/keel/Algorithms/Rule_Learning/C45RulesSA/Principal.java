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
 * @author Written by Antonio Alejandro Tortosa (University of Granada) 01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.C45RulesSA;

class Principal {
  public static void main(String[] args) {


   try
    {
      if (args.length != 1)
        throw new Exception("\nError: you have to specify the parameters file\n\tusage: java -jar C45.java parameterfile.txt");
      else {
        String param=args[0];
        parseParameters par = new parseParameters();
        par.parseConfigurationFile(param);
        C45 c45tree = new C45(par);
        Tree t = c45tree.getTree();
        C45RulesSA c45rules=new C45RulesSA(t,par);
        c45rules.execute();
      }
    }
    catch (Exception e)
    {
      System.err.println(e.getMessage());
      System.exit(-1);
    }
  }
}
