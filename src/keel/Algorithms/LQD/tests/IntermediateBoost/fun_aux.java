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
