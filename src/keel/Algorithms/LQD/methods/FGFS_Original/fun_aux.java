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
