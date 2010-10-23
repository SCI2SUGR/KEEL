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

package keel.Algorithms.RE_SL_Methods.LEL_TSK;

/*
 * Created on 07-feb-2004
 *
 * @author Jesus Alcala Fernandez
 *
 */

import java.lang.*;

public class Lanzar {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Remember: java Lanzar <file_configuration>");
    }
    else {
      System.out.println(
          "Step 1: Obtaining the initial Rule Base and Data Base");
      MogulSC mogul = new MogulSC(args[0]);
      mogul.run();
      System.out.println("Step 2: Learning tsk-fuzzy models based on MOGUL");
      Mam2Tsk cons = new Mam2Tsk(args[0]);
      cons.run();
      System.out.println("Step 3: Genetic Selection of the Rules");
      Simplif simplificacion = new Simplif(args[0]);
      simplificacion.run();
      System.out.println("Final Step: Genetic Tuning of the FRBS");
      Tun_TSK tun = new Tun_TSK(args[0]);
      tun.run();
      System.out.println("Algorithm Finished!");
    }
  }
}
