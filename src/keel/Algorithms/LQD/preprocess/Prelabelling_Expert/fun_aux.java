package keel.Algorithms.LQD.preprocess.Prelabelling_Expert;

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
