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

package keel.Algorithms.LQD.methods.FGFS_Rule_Weight_Penalty;

import java.util.Vector;


/**
 *
 * File: fun_aux.java
 *
 * Obtain a fuzzy number from one number. This fuzzy number can be triangular or rectangular
 *
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010
 * @version 1.0
 */


public class fun_aux {
	
	public static fuzzy to_fuzzy(String number)
	{
		//Convert the number in fuzzy
		fuzzy novel= new fuzzy();
		float iz, center,de;
		if(number.charAt(0)=='[')
		{
			int poscoma=number.indexOf(',', 1);
			int poscomaseg=-1;
			poscomaseg=number.indexOf(',', poscoma+1);
			int poscor = number.indexOf(']',poscoma+1);
			
			 iz=Float.parseFloat(number.substring(1, poscoma));
			 if(poscomaseg!=-1)
			 {
				 center=Float.parseFloat(number.substring(poscoma+1,poscomaseg));
				 de=Float.parseFloat(number.substring(poscomaseg+1,poscor));
				 novel.borrosotriangular(iz, center, de);
			 }
			 else
			 {
				 de=Float.parseFloat(number.substring(poscoma+1,poscor));
				 novel.borrosorectangular(iz,de);
			 }
			return novel;
		}
		
		
		//Is a crisp number
		iz=Float.parseFloat(number);
		
		novel.setizd(iz);
		novel.setdere(iz);
		return novel;
	}
	
	


}

