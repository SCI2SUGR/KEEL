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

package keel.Algorithms.LQD.preprocess.Prelabelling;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;



/**
 *
 * File: fuzzyRule.java
 *
 * Properties and functions of the fuzzy rule as obtain the antecedent and
 * the consequent of the rule from the confidence the this rule with
 * the instances
 *
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010
 * @version 1.0
 */

public class fuzzyrule {

	Integer[] antecedente;
	
	public fuzzyrule(Vector<fuzzypartition> pentradas,Vector<fuzzy> X)
	{
		antecedente = new Integer[pentradas.size()];
		for (int i=0;i<pentradas.size();i++) //pentradas indicates the total of variable in each example
   	 	{
			float valor=0;
			float maximo=-1;
			int particion=-1;
			for (int j=0;j<pentradas.get(i).size();j++) 
			{
				valor = pentradas.get(i).aproximation(j, X.get(i));			
				if(valor>maximo)
				{
					maximo=valor;
					particion=j;
				}
			}
				

			antecedente[i]=particion;
   	 	}
		
		
	}
	


	public fuzzy match_alpha(Vector<fuzzy> L,Vector<fuzzypartition> pentradas, int alpha) throws IOException
	{
		//Compatibility between the antecedent of the rule and the example (composes by imprecise data)
		//We obtain this compatibility from alpha cuts
		
		interval m= new interval(1,1); 
		float valoralpha=1;
		
		for (int i=0;i<antecedente.length;i++)
		{

			interval certainty = new interval(-1,-1);//save the certainty between the example and antecedent
			for(int c=0;c<alpha;c++)
		   	{
				//value of the alpha cut
		   		valoralpha=1-(float)((float)c*(float)(1/(float)(alpha-1)));
		   	//	Initialize the cuts obtained from alpha cuts
		   		Vector<Float> cut = new Vector<Float>();
		   		Vector<Float> cutparticion = new Vector<Float>();
		   		if(L.get(i).es_rect()==1) //fuzzy(rectangular)
	   			{
		   			//pentradas.get(i).get(antecedent[i]).show();
		   			//Obtain the value of the alpha cut in the partition (x) and them we check if x
		   			// is in the interval
		   			cut=pentradas.get(i).get(antecedente[i]).cut(valoralpha);
	   			}
		   		else //fuzzy(triangular)
		   		{
		   			//	obtain x from alpha and the variable of the example
		   			//and them we check the membership in the partition
		   			cut=L.get(i).cut(valoralpha);
		   			cutparticion=pentradas.get(i).get(antecedente[i]).cut(valoralpha);
		   		}
		   			
		   		
		   		for(int k=0; k<cut.size();k++ )
		   		{
		   			if(L.get(i).es_rect()==1) //fuzzy(rectangular)
		   			{
		   				if((cut.get(k)>=L.get(i).geta() && cut.get(k)<=L.get(i).getd()))
		   				{
		   					if(certainty.getmin()==-1 && certainty.getmax()==-1)
		   					{
		   						certainty.setmin(valoralpha);
		   						certainty.setmax(valoralpha);
		   					}
		   				
		   					certainty.setmin(tnorma(certainty.getmin(),valoralpha,0));//minimo
		   					certainty.setmax(tconorma(certainty.getmax(),valoralpha)); //maximo
		   				}
		   						
		   				}
		   			else //fuzzy(triangular)
		   			{
		   			//obtain x from alpha and the variable of the example
			   			//and them we check the membership or certainty of x in the antecedent of the rule

		   				if(certainty.getmin()==-1 && certainty.getmax()==-1)
		   				 {
		   					 certainty.setmin(pentradas.get(i).membership(antecedente[i],cut.get(k)));
		   					 certainty.setmax(pentradas.get(i).membership(antecedente[i],cut.get(k)));
		   				 }
		   				
		   					certainty.setmin(tnorma(certainty.getmin(),pentradas.get(i).membership(antecedente[i],cut.get(k)),0));//minimo
		   					certainty.setmax(tconorma(certainty.getmax(),pentradas.get(i).membership(antecedente[i],cut.get(k)))); //maximo
		   				
		   			}
		   			
		   		} //for  cuts
	   					
	   			
		   	} //for alphas
		   		
			//If the variable of the example (rectangular) no belong into any alpha,
			//rectangular: we calculate the membership the ends of the interval. This membership  will be
			//the compatibility the this antecedent and the variable of the example
			if(L.get(i).es_rect()==1) //fuzzy(rectangular)
   			{
		   		
				if(certainty.getmin()==-1 && certainty.getmax()==-1)
   			{
					certainty.setmin(pentradas.get(i).membership(antecedente[i], L.get(i).geta()));
	 		   		certainty.setmax(pentradas.get(i).membership(antecedente[i], L.get(i).getd()));
	 		   		certainty.ordenar();
				
				}
   			}
				
				
			m.setmin(tnorma(m.getmin(),certainty.getmin(),1));
			m.setmax(tnorma(m.getmax(),certainty.getmax(),1));
			
			
				
		}//for  antecedent
				
		fuzzy compatibilidad = new fuzzy();
		compatibilidad.borrosorectangular(m.getmin(), m.getmax());
		return compatibilidad;
	}
	
	public float sumatorio(float x, float y, int tnr)
	{
		return x+y;
	}
	public float tnorma(float x, float y, int tnr) 
	{
	    if (tnr==0) // is the minimum
	    {
	      if (x<y) return x; 
	      else return y;
	    } 
	    else return x*y;
	}
	public float tconorma (float x, float y)
	{
		  if (x<y) return y; else return x;//return the maximum
	}
	
	
	public void show1() throws IOException
	{
	 		
  		System.out.print("\n A: ");
  		
  		for(int j=0; j<antecedente.length; j++)
  		{
  			System.out.print( antecedente[j]+" ");
  			
  		}
  		System.out.print("\n C: ");
  		
  		
	  
	}
	
	public int size() { return antecedente.length;}
	public void setAntecedente(Integer[] A) { antecedente=A; }
	
	
    public Integer[] getAntecedente()  { return antecedente; }
	public int getAntecedente(int n) { return antecedente[n]; }
    


	 
	
	
}

