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

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 *
 * File: rule.java
 *
 * Properties and functions of the fuzzy rule as obtain the antecedent and
 * the consequent of the rule from the confidence the this rule with
 * the instances
 *
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010
 * @version 1.0
 */

public class rule {

	Integer[] antecedent;
	Integer[] consequent; // the rule only have one consequent
	int NOIMPORTA=-1;	                           
	
	public rule(Vector<partition> pentradas,int clases)
	{
		antecedent = new Integer[pentradas.size()];
		consequent = new Integer[clases];
		
		for(int i=0; i<pentradas.size(); i++)
			antecedent[i]=NOIMPORTA;
		for(int i=0; i<clases; i++)
			consequent[i]=0;
	}
	
	public void obtain_rule(Interval[][] x,Vector<Vector<Float>> y,Vector<partition> pentradas,int clases, int COSTES, int alpha,
			 Vector<Float> valores_clases,Vector<Vector<Float>> pesos, int ejemplo) throws IOException
	{
		for (int i=0;i<pentradas.size();i++) 
   	 	{
			float valor_min=0;
			float valor_max=0;
			float valor=0;
			float maximo=-1;
			int particion=-1;
			for (int j=0;j<pentradas.get(i).size();j++) 
			{
				
				valor = pentradas.get(i).pertenenecia_intervalo(j, x[ejemplo][i]);
				
				if(valor>maximo)
				{
					maximo=valor;
					particion=j;
				}
			}
			
			antecedent[i]=particion;//+NOIMPORTA;
   	 	}
		
		//new BufferedReader(new InputStreamReader(System.in)).readLine();
		WM(x,y,pentradas, clases,COSTES, alpha,valores_clases,pesos);
		
	}
	public void obtain_rule_random(Interval[][] x,Vector<Vector<Float>> y,Vector<partition> pentradas,int clases, int COSTES, int alpha,
			 Vector<Float> valores_clases,Vector<Vector<Float>> pesos) throws IOException
	{	 
		 
		
		for (int i=0;i<pentradas.size();i++) 
   	 	{
			int valor=(int) (0+(float)(Math.random()*(pentradas.get(i).size()))); //valor entre [0 y el numero de particiones+1)
			antecedent[i]=valor;//+NOIMPORTA;
   	 	}
	
		
		WM(x,y,pentradas, clases,COSTES, alpha,valores_clases,pesos);	
		  
		 	
		  
	}
	
	void WM(Interval[][] x,Vector<Vector<Float>> y,Vector<partition> pentradas,int clases, int COSTES, int alpha,
			 Vector<Float> valores_clases,Vector<Vector<Float>> pesos)throws IOException
	{
		
		  Interval confianza=new Interval(0,0);
		  Interval actual_confianza=new Interval(0,0);
		  int mejorPC=0;
		  
		  

		  for (int cc=0;cc<consequent.length;cc++)
		  {
			
			  for (int i=0;i<consequent.length;i++) 
				  consequent[i]=0;
			  consequent[cc]=1;				 
			
			  confianza=compatibilidad_regla(x,y,pentradas, clases,COSTES, alpha,valores_clases, pesos);
			 
			  int ganador = Dominance.uniform_compatibility(actual_confianza, confianza);
			 
			  if(ganador==1)
			  {
				  mejorPC=cc;
				
				  actual_confianza.setmin(confianza.getmin());
				  actual_confianza.setmax(confianza.getmax());
			  }
			  
			}
			
		  for (int i=0;i<consequent.length;i++) 
			consequent[i]=0;
		  consequent[mejorPC]=1;	
	}
		
	public Interval compatibilidad_regla(Interval[][] x,Vector<Vector<Float>> y,Vector<partition> pentradas,int clases, int COSTES, int alpha,
			 Vector<Float> valores_clases,Vector<Vector<Float>> pesos) throws IOException
	{
	
		Interval confianza = new Interval(0,0);
		
		
		for (int i=0;i<x.length;i++) 
		{ 
		
			Interval ma=new Interval(0,0);
			ma=match_alpha(x[i],pentradas, alpha); //x[i], son los datos de las variables del ejemplo i, ma es un intervalo
		
			if(ma.getmin()!=0 && ma.getmax()!=0)
			{
				float mc=match_salida(y.get(i),clases,COSTES,valores_clases, pesos); //mc es un float
				confianza.setmin(confianza.getmin()+tnorma(ma.getmin(),mc,1)); //lo multiplica, multiplica un intervalo por un float
				confianza.setmax(confianza.getmax()+tnorma(ma.getmax(),mc,1));
			}
		}
		
		return confianza;
	}
	
	public float match_salida(Vector<Float> salida,int clases, int COSTES, Vector<Float> valores_clases,Vector<Vector<Float>> pesos)
	{
		float maximo=0;
	
		for (int i=0;i<clases;i++)  
		{
			if(consequent[i]!=0)
			{
	
				if(COSTES==0)
				{
					for(int j=0;j<salida.size();j++)
					{
					
						int ant_salida=-1;
						for(int v=0; v<valores_clases.size(); v++)
						{
						
							if(valores_clases.get(v).compareTo(salida.get(j))==0)
							{
							
								ant_salida=v;
								break;
							}
						}
					
						if(ant_salida!=-1)		  	
		   		  		{	   		  		
							if(ant_salida==i)
							{
								
		   		  				return 1;
		   		  			}
		   		  		}
					
					} 
				}
				
				else
				{
					for(int j=0;j<salida.size();j++)
					{
				
						for(int v=0; v<valores_clases.size(); v++)
						{
				
							if(valores_clases.get(v).compareTo(salida.get(j))==0)
							{
				
								if(maximo<1-pesos.get(i).get(v))
									maximo=1-pesos.get(i).get(v);
				
							}
						}
					}
				}
				
				break;
			}
 
		}
		
		
		if(COSTES==0)
		{
		
			return 0;
		}
		else
			return maximo;
		
		
	}
	public Interval match_alpha(Interval[] x,Vector<partition> pentradas, int alpha)
	{
		Interval m= new Interval(1,1);
		float valoralpha=1;
		
		
		for (int i=0;i<antecedent.length;i++) 
		{
			Interval parti_alpha = new Interval(-1,-1);
			for(int c=0;c<alpha;c++)
		   	{
				
		   		valoralpha=1-(float)((float)c*(float)(1/(float)(alpha-1)));
		   		
		 
		   		
		   		Vector<Float> valor = new Vector<Float>();
		   		valor=pentradas.get(i).content(antecedent[i], valoralpha);
		   		
		   		
		   		for(int k=0; k<valor.size();k++ )
		   		{
		   		
		   			
		   			if((valor.get(k)>=x[i].getmin() && valor.get(k)<=x[i].getmax()))
		   			{
		   				if(parti_alpha.getmin()==-1 && parti_alpha.getmax()==-1)
		   				{
		   					
		   					parti_alpha.setmin(valoralpha);
		   					parti_alpha.setmax(valoralpha);
		   				}
		   				
		   			
		   				parti_alpha.setmin(tnorma(parti_alpha.getmin(),valoralpha,0));//minimo
		   				parti_alpha.setmax(tconorma(parti_alpha.getmax(),valoralpha)); //maximo	 		   					
		   				k=valor.size();
		   			}
		   			
		   		}
		   		
		   	}//for alphas
			
			if(parti_alpha.getmin()==-1 && parti_alpha.getmax()==-1)
			{
				
					parti_alpha.setmin(pentradas.get(i).membership(antecedent[i], x[i].getmin()));
	 		   	parti_alpha.setmax(pentradas.get(i).membership(antecedent[i], x[i].getmax()));
	 		   	parti_alpha.ordenar();
	 		   
			}
				
				
		
			m.setmin(tnorma(m.getmin(),parti_alpha.getmin(),1));
			m.setmax(tnorma(m.getmax(),parti_alpha.getmax(),1));
				
		}//for
		
		
		return m;
	}
	public float tnorma(float x, float y, int tnr) 
	{
	    if (tnr==0)  
	    {
	      if (x<y) return x; else return y;
	    } 
	    else return x*y;
	}
	public float tconorma (float x, float y)
	{
		  if (x<y) return y; else return x;
	}
	
	public void show(FileWriter fs1) throws IOException
	{
	 		
  		//System.out.print("\n A: ");
  		fs1.write("\n A: ");//variables
  		for(int j=0; j<antecedent.length; j++)
  		{
  			//System.out.print( antecedent[j]+" ");
  			fs1.write(antecedent[j]+" ");//variables
  		}
  		//System.out.print("\n C: ");
  		fs1.write("\n C: ");//variables
  		for(int j=0; j<consequent.length; j++)
  		{
  			//System.out.print(consequent[j]+" ");
  			fs1.write(consequent[j]+" ");//variables
  		}
	  
	}
	public void show1() throws IOException
	{
	 		
  		System.out.print("\n A: ");
  		
  		for(int j=0; j<antecedent.length; j++)
  		{
  			System.out.print( antecedent[j]+" ");
  			
  		}
  		System.out.print("\n C: ");
  		
  		for(int j=0; j<consequent.length; j++)
  		{
  			System.out.print(consequent[j]+" ");
  			
  		}
  		//new BufferedReader(new InputStreamReader(System.in)).readLine();
	  
	}
	
	public int size() { return antecedent.length;}
	public void setantecedent(Integer[] A) { antecedent=A; }
	public void setconsequent(Integer[] C) { consequent=C; }
	
    public Integer[] getantecedent()  { return antecedent; }
	public int getantecedent(int n) { return antecedent[n]; }
    
	public Integer[] getconsequent(){ return consequent; }
    public Integer getconsequent(int c) { return consequent[c]; }
		// float & getconsequent(int c) { return consequent[c]; }
	public void setconsequent(int c,Integer v) { consequent[c]=v; }
	
	
	
}

