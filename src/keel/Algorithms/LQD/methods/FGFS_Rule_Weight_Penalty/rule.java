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

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class rule {

	Integer[] antecedent;
	Float[] consequent; // the rule only have one consequent
	Interval IDEAL= new Interval(1,1);
	int NOIMPORTA=-1;
	int asing_weight_rule=0;
	
	public rule(Vector<partition> pentradas,int classes,int asign_weight_re)
	{
		antecedent = new Integer[pentradas.size()];
		consequent = new Float[classes];
		asing_weight_rule=asign_weight_re;
		
		for(int i=0; i<pentradas.size(); i++)
			antecedent[i]=NOIMPORTA;
		for(int i=0; i<classes; i++)
			consequent[i]=(float)0;
	}
	
	public void obtain_rule(fuzzy[][] x,Vector<Vector<Float>> y,Vector<partition> pentradas,int classes, int COST, int alpha,
			 Vector<Float> values_classes,Vector<Vector<Float>> pesos, int ejemplo) throws IOException
	{
		for (int i=0;i<pentradas.size();i++) //pentradas indicates the total of variables for each example
   	 	{
			float value=0;
			float max=-1;
			int particion=-1;
			for (int j=0;j<pentradas.get(i).size();j++) 
			{
				value = pentradas.get(i).aproximation(j, x[ejemplo][i]);
				
				if(value>max)
				{
					max=value;
					particion=j;
					
				}
			}
			antecedent[i]=particion;
   	 	}
		
		calcularconsequent(x,y,pentradas, classes,COST, alpha,values_classes,pesos);
		
	}
	public void obtain_rule_random(fuzzy[][] x,Vector<Vector<Float>> y,Vector<partition> pentradas,int clases, int COSTES, int alpha,
			 Vector<Float> valores_clases,Vector<Vector<Float>> pesos) throws IOException
	{	 
		  //Creamos el antecedente de la regla aleatoriamente
		
		for (int i=0;i<pentradas.size();i++) //pentradas indica el total de variables de entrada para cada ejemplo
   	 	{
			int valor=(int) (0+(float)(Math.random()*(pentradas.get(i).size()))); //valor entre [0 y el numero de particiones+1)
			 //Y LUEGO LE sumamos NOIMPORTA
			//System.out.println(valor);
			antecedent[i]=valor;//+NOIMPORTA;
   	 	}
		//[NOIMPORTA 2 3] [010]
		
		calcularconsequent(x,y,pentradas, clases,COSTES, alpha,valores_clases,pesos);	
		  
		 	
		  
	}
	
	void calcularconsequent(fuzzy[][] x,Vector<Vector<Float>> y,Vector<partition> pentradas,int classes, int COST, int alpha,
			 Vector<Float> values_classes,Vector<Vector<Float>> pesos)throws IOException
	{
		  Interval maxconfidence=new Interval(0,0);
		  Interval savesoporte=new Interval(0,0);
		  Interval sum_confidence=new Interval(0,0);
		  Vector<Interval>confidence= new Vector<Interval>();
		  
		  
		  for (int cc=0;cc<consequent.length;cc++)
		  {
			  confidence.add(new Interval(0,0));
		  }
		  
		  int mejorPC=0;//best partition
		  
		  
		  

		  for (int cc=0;cc<consequent.length;cc++)
		  {
			  for (int i=0;i<consequent.length;i++) 
				  consequent[i]=(float)0;
			  consequent[cc]=(float)1;				 
			
			 
			  Vector<Interval> weight_rule= new Vector<Interval>();//1 la confianza, en la segunda el soporte
			  calculateConfidence(x,y,pentradas, classes,COST, alpha,values_classes, pesos,weight_rule);
			 
			  confidence.set(cc, weight_rule.get(0));
			
			  
			  
			  if(Dominance.uniform_compatibility(maxconfidence, weight_rule.get(0))==1)//si la nueva confianza es mayor que maxconfianza
			  {
				  mejorPC=cc;
				 // System.out.println("maxconfiaza es "+maxconfianza.getmin()+" "+maxconfianza.getmax());
				  maxconfidence=weight_rule.get(0);
				 // System.out.println("maxconfiaza sustituida por la confianza es "+maxconfianza.getmin()+" "+maxconfianza.getmax());
				  savesoporte=weight_rule.get(1);
				  
			  }
			  else
				  weight_rule.get(0).suma( sum_confidence);
			  
			}
			
		  
		  if(asing_weight_rule!=0)
		  {
			  if(savesoporte.mayor_zero()==true)
			  {
				  for (int i=0;i<consequent.length;i++) 
					  consequent[i]=(float)0;
				  float peso=0;
				  if(asing_weight_rule==1)
					  peso=1-(haausdorff(IDEAL,maxconfidence));
				  else if (asing_weight_rule==2)
					  peso=haausdorff(maxconfidence,(sum_confidence.dividir((consequent.length-1))));
				  else if (asing_weight_rule==3)
				  {
					  float segundo=2;
					  for(int c=0;c<confidence.size();c++)
					  {
						  if(c!=mejorPC) 
						  {
							  float distancia = haausdorff(maxconfidence,confidence.get(c));
							  if(distancia<segundo)
					    			  segundo=distancia;
							  
						  }
					  }
					  peso=segundo;
				  }
				  else if (asing_weight_rule==4)
				  {
					  					  
					  peso=haausdorff(maxconfidence,sum_confidence);
				  }
					  
					  
					  
				  if(peso==0)
					  consequent[mejorPC]=(float)0;
				  else
					  consequent[mejorPC]=peso;
						
			  }
		  }
		  else
		  {
			  for (int i=0;i<consequent.length;i++) 
				  consequent[i]=(float)0;
			  consequent[mejorPC]=(float)1;
		  }
	}
		
	public void calculateConfidence(fuzzy[][] x,Vector<Vector<Float>> y,Vector<partition> pentradas,int clases, int COSTES, int alpha,
			 Vector<Float> valores_clases,Vector<Vector<Float>> pesos,Vector<Interval> pesos_regla) throws IOException
	{
	
		Interval degree = new Interval(0,0);
		int ej_compatibles=0;;
		
		for(int i=0;i<2;i++)
		{
			Interval inicio= new Interval(0,0);
			pesos_regla.add(i, inicio);
		}

		
		
		for (int i=0;i<x.length;i++) 
		{ 
			Interval ma=new Interval(0,0);
			ma=match_alpha(x[i],pentradas, alpha); 
			
			float mc=match_salida(y.get(i),clases,COSTES,valores_clases, pesos); 
			degree.setmin(degree.getmin()+tnorma(ma.getmin(),mc,1)); 
			degree.setmax(degree.getmax()+tnorma(ma.getmax(),mc,1));
			if(ma.getmax()!=0)
				ej_compatibles++;
			
		}
		if (ej_compatibles>0)
		{
			Interval confianza= degree.dividir(ej_compatibles);
			pesos_regla.set(0,confianza); 
		}
	
	
		pesos_regla.set(1,degree.dividir(x.length));
		//

		
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
	public Interval match_alpha(fuzzy[] x,Vector<partition> pentradas, int alpha) throws IOException
	{
		Interval m= new Interval(1,1); 
		float valoralpha=1;
		
		
		for (int i=0;i<antecedent.length;i++) 
		{
			
			Interval certeza = new Interval(-1,-1);
			for(int c=0;c<alpha;c++)
		   	{
		   		valoralpha=1-(float)((float)c*(float)(1/(float)(alpha-1)));
		 
		   		Vector<Float> cut = new Vector<Float>();
		   		if(x[i].getb()==-1) 
	   			{
		   			cut=pentradas.get(i).get(antecedent[i]).cut(valoralpha);
	   			}
		   		else 
		   			cut=x[i].cut(valoralpha);
		   			
		   		
		   		for(int k=0; k<cut.size();k++ )
		   		{
		   		
		   			
		   			if(x[i].getb()==-1) 
		   			{
		   			
		   				if((cut.get(k)>=x[i].geta() && cut.get(k)<=x[i].getd()))
		   				{
		   					if(certeza.getmin()==-1 && certeza.getmax()==-1)
		   					{
		   						certeza.setmin(valoralpha);
		   						certeza.setmax(valoralpha);
		   					}
		   				
		   					//	 
		   					certeza.setmin(tnorma(certeza.getmin(),valoralpha,0));//minimum
		   					certeza.setmax(tconorma(certeza.getmax(),valoralpha)); //maximum	 		   					
		   					//k=cut.size();
		   				}
		   			}
		   			else
		   			{
		   				 if(certeza.getmin()==-1 && certeza.getmax()==-1)
		   				 {
		   					
		   					 certeza.setmin(pentradas.get(i).membership(antecedent[i],cut.get(k)));
		   					 certeza.setmax(pentradas.get(i).membership(antecedent[i],cut.get(k)));
		   				 }
		   				
		   					certeza.setmin(tnorma(certeza.getmin(),pentradas.get(i).membership(antecedent[i],cut.get(k)),0));//minimo
		   					certeza.setmax(tconorma(certeza.getmax(),pentradas.get(i).membership(antecedent[i],cut.get(k)))); //maximo	 		   					
		   				
		   				
		   			}
		   			
		   			
		   		}
		   		
		   	}//for 
			
			if(x[i].getb()==-1) 
   			{

				if(certeza.getmin()==-1 && certeza.getmax()==-1)
				{
				
					certeza.setmin(pentradas.get(i).membership(antecedent[i], x[i].geta()));
	 		   		certeza.setmax(pentradas.get(i).membership(antecedent[i], x[i].getd()));
	 		   		certeza.ordenar();
	 		   	}
   			}
			
				
				
			m.setmin(tnorma(m.getmin(),certeza.getmin(),1));
			m.setmax(tnorma(m.getmax(),certeza.getmax(),1));
				
		}
		
		
		return m;
	}
	
	public float match(float[] x,Vector<partition> pentradas)
	{
		float m=1,m1;
		for (int i=0;i<antecedent.length;i++) 
		{
			m1=pentradas.get(i).membership(antecedent[i],x[i]);

			m=tnorma(m,m1,1);
		}

		
		return m;
	}
	public float tnorma(float x, float y, int tnr) 
	{
	    if (tnr==0)  
	    {
	      if (x<y) return x; 
	      else return y;
	    } 
	    else return x*y;
	}
	public float tconorma (float x, float y)
	{
		  if (x<y) return y; else return x;
	}
	
	public void show(FileWriter fs1) throws IOException
	{
	 		
  	
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
  			fs1.write(+consequent[j]+" ");//variables
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
	public void setAntecedente(Integer[] A) { antecedent=A; }
	public void setconsequent(Float[] C) { consequent=C; }
	
    public Integer[] getAntecedente()  { return antecedent; }
	public int getAntecedente(int n) { return antecedent[n]; }
    
	public Float[] getconsequent(){ return consequent; }
    public float getconsequent(int c) { return consequent[c]; }
		// float & getconsequent(int c) { return consequent[c]; }
	public void setconsequent(int c,float v) { consequent[c]=v; }
	 public float getpeso() 
	    { 
	    	for (int i=0;i<consequent.length;i++) 
	    	{
	    		if(consequent[i]!=0)
	    			return consequent[i];
	    	}
			return 0;
	    }
	 
	 public void setpeso(float v) 
	    { 
	    	for (int i=0;i<consequent.length;i++) 
	    	{
	    		if(consequent[i]!=0)
	    			consequent[i]=v;
	    	}
			
	    }
	 
	 
	 
	 static float haausdorff(Interval A, Interval B)
	 {
			return calculo_hausdorff(Math.abs(A.getmin() - B.getmin()),
						Math.abs(A.getmax() - B.getmax()));	  

	 }


	 static float calculo_hausdorff(float min, float max)
	 {
	 	if(min>max) return min;
	 	else 
	 		return max;
	 	}



	 
	
	
}

