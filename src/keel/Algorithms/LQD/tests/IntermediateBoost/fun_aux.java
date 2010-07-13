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

package keel.Algorithms.LQD.tests.IntermediateBoost;

/**
 * 
 * File: fun_aux.java

 * 
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010 
 * @version 1.0 
 */
public class fun_aux {

	public static fuzzy afuzzy(String numero)
	{
		
		fuzzy nuevo= new fuzzy();
		float iz, centro,de;
		
		if(numero.charAt(0)=='[')
		{
			int poscoma=numero.indexOf(',', 1);
			int poscomaseg=-1;
		
			poscomaseg=numero.indexOf(',', poscoma+1);
			int poscor = numero.indexOf(']',poscoma+1);
			
			
			 iz=Float.parseFloat(numero.substring(1, poscoma));
			
			 if(poscomaseg!=-1)
			 {
				 centro=Float.parseFloat(numero.substring(poscoma+1,poscomaseg));
			
				 de=Float.parseFloat(numero.substring(poscomaseg+1,poscor));
			
				 
				 
				 nuevo.borrosotriangular(iz, centro, de);
				
			 }
			 else
			 {
				 de=Float.parseFloat(numero.substring(poscoma+1,poscor));
				 nuevo.borrosorectangular(iz,de);
				
			 }
			
			 
		
			
			return nuevo;
		}
		
		
		
			iz=Float.parseFloat(numero);
		
		
		
	
		nuevo.setizd(iz);
		nuevo.setdere(iz);
		return nuevo;
	}
	public static fuzzy trapezoidal(String numero)
	{
		
		fuzzy nuevo= new fuzzy();
		float iz, centro,centro1,de;
		
		if(numero.charAt(0)=='[')
		{
			int poscoma=numero.indexOf(',', 1);
			int poscomaseg=-1;
			int poscomater=-1;
			poscomaseg=numero.indexOf(',', poscoma+1);
			poscomater=numero.indexOf(',', poscomaseg+1);
			int poscor = numero.indexOf(']',poscoma+1);
			
			
		
			
			 iz=Float.parseFloat(numero.substring(1, poscoma));
			 
			 centro=Float.parseFloat(numero.substring(poscoma+1,poscomaseg));
			 centro1=Float.parseFloat(numero.substring(poscomaseg+1,poscomater));
				
				de=Float.parseFloat(numero.substring(poscomater+1,poscor));

				 nuevo.borrosotrapezoidal(iz, centro,centro1, de);
				
			
			 
		
			
			return nuevo;
		}
		
		
		
			iz=Float.parseFloat(numero);
		
		
		
		
		nuevo.setizd(iz);
		nuevo.setdere(iz);
		return nuevo;
	}

	
}

