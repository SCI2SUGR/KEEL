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

package keel.Algorithms.LQD.methods.FGFS_Original;

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
	
	public static Interval to_fuzzy(String numero)
	{
		
		Interval nuevo= new Interval(0,0);
		float i1, i2;
		if(numero.charAt(0)=='[')
		{
			int poscoma=numero.indexOf(',', 1);
			int poscor = numero.indexOf(']',poscoma);
			
			
			 i1=Float.parseFloat(numero.substring(1, poscoma));
			 i2=Float.parseFloat(numero.substring(poscoma+1,poscor));
			
			
			
			nuevo.setmin(i1);
			nuevo.setmax(i2);
			return nuevo;
		}
		
		
			i1=Float.parseFloat(numero);
		
		
		
		nuevo.setmin(i1);
		nuevo.setmax(i1);
		return nuevo;
	}
	
	


}

