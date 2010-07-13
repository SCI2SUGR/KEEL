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

package keel.Algorithms.RE_SL_Methods.mogulIRL;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.io.*;
import java.util.*;
import java.lang.*;

public class Lanzar {

    public static void main(String[] args) {
                if (args.length != 1) {
                        System.out.println ("Remember: java Lanzar <file_configuration>");
                }
                else {
                        System.out.println("Step 1: Obtaining the initial Rule Base and Data Base");
                        MogulSC mogul = new MogulSC(args[0]);
                        mogul.run();
                        MiDataset tabla_tra = mogul.getTabla(true);
                        MiDataset tabla_tst = mogul.getTabla(false);
                        System.out.println("Step 2: Genetic Selection of the Rules");
                        Sel seleccion = new Sel(args[0],tabla_tra,tabla_tst);
                        seleccion.run();
                        System.out.println("Final Step: Genetic Tuning of the FRBS");
                        Tun_des tun = new Tun_des(args[0],tabla_tra,tabla_tst);
                        tun.run();
                        System.out.println("Algorithm Finished!");
                }
    }
}

