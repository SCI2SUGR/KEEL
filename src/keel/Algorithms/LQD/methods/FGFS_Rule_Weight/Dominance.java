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

package keel.Algorithms.LQD.methods.FGFS_Rule_Weight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
/**
*
* File: Dominance.java
*
* Obtain the rule with more compatibility between the fitness
* of two rule expressed by a interval-value. Which rule
* is dominated by the other one
*
* @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010
* @version 1.0
*/


public class Dominance {
	

static int uniform_compatibility( Interval actual, Interval novel) throws IOException 
{
	
	
	//Compare the interval, the interval with more compatibility (nearest a 1), is the best
	
	double ri=probAbiggerB(actual,novel);
	if(ri<0.5) //novel is better than actual, actual is dominated by novel
	{
		return 1;
	}
	else if (ri==0.5) //equal
		return 0;
	else if (ri>0.5) //actual is better
		return 0;
		
	
	return 0;//the actual
}
static double probAbiggerB(Interval o1, Interval o2) throws IOException 
{
	//Domine the interval with more value, probability nearest a 1.
	//o1 respect to o2, indicate the probability that o1 is better than o2
	//if bigger than 0.5 o1 domine a o2 (o2 is dominated by o1)
	double a,b,c,d,prob;
	a=o1.getmin();
	b=o1.getmax();
	c=o2.getmin();
	d=o2.getmax();
	
	//0
	if(d==a && c==a && d==b && c==b)
		prob=(float) 0.5;
	//1
	else if(b<=c) 
		prob=0;
	//2	
	else if (d<=a && c<=a && d<=b && c<=b) 
		prob=1;
	//3
	else if (d>=a && c<=a && d<=b && c<=b) 
		prob=1-((d-a)*(d-a)/2)/((b-a)*(d-c));  
	//4
	else if (d>=a && c>=a && c<=b && d<=b) 
		prob=((b-d)*(d-c)+(d-c)*(d-c)/2)/((b-a)*(d-c));  

	//5
	else if (d>=a && c<=a && d>=b && c<=b) 
		prob=((b-a)*(a-c)+(b-a)*(b-a)/2)/((b-a)*(d-c));  
	
	//6
	else if (d>=a && c>=a && c<=b && d>=b) 
		prob=((b-c)*(b-c)/2)/((b-a)*(d-c));  
	
	else 
	{
		if (!(d>=a && c>=a && d>=b && c>=b)) 
		{
			System.out.println("Error "+ a+" " +b+ " "+ c + " " +d);
		}
		assert(d>=a && c>=a && d>=b && c>=b);
		prob=1;  
	}
	
	if(prob>1 || prob<0)
	{
		System.out.println("Errorrrr prob between 0 and 1"+ a+" " +b+ " "+ c + " " +d);
		new BufferedReader(new InputStreamReader(System.in)).readLine();
	}
	
	return prob;
}
static int domine( Vector<Float> p1, Vector<Float> p2) throws IOException
{
	
	//pasamos a intervalo el maximo y minimo de cada padre
	Interval pa1= new Interval(0,0); 
	Interval pa2= new Interval(0,0); 

	float minimop1=0; 
	float maximop1=0; 
	float minimop2=0; 
	float maximop2=0; 

	for(int i=0;i<p1.size();i++) 
	{
		if(i==0) 
		{
			minimop1=p1.get(i);
			maximop1=p1.get(i);
		}
		else 
		{
			if(minimop1>p1.get(i)) 
				minimop1=p1.get(i);

			if(maximop1<p1.get(i)) 
				maximop1=p1.get(i);
		}
	}

	pa1.setmin(minimop1);
	pa1.setmax(maximop1);

	
	for(int i=0;i<p2.size();i++) 
	{
		if(i==0) 
		{
			minimop2=p2.get(i);
			maximop2=p2.get(i);
		}
		else 
		{
			if(minimop2>p2.get(i)) 
				minimop2=p2.get(i);

			if(maximop2<p2.get(i)) 
				maximop2=p2.get(i);
		}
	}

	pa2.setmin(minimop2);
	pa2.setmax(maximop2);

	
	double prob = probAbiggerB(pa1,pa2);
	if(prob>0.5)
		return 1;
	else if(prob<0.5)
		return 0;
	else //son iguales 0.5, se determina mejor el primero (aqui el de maximo en el extremo superior u otro criterio)
		return 1;
						
					
}
static Vector<Interval> obtain_interval(Vector<Vector<Float>> fitness)
{
	

	float minimo=0; 
	float maximo=0; 
	Vector<Interval> intervalos= new Vector<Interval>();
	for(int i=0;i<fitness.size();i++) 
	{
		Interval p= new Interval(0,0); 
		for (int j=0;j<fitness.get(i).size();j++)
		{
			if(j==0) 
			{
				minimo=fitness.get(i).get(j);
				maximo=fitness.get(i).get(j);
			}
			else 
			{
			if(minimo>fitness.get(i).get(j)) 
				minimo=fitness.get(i).get(j);

			if(maximo<fitness.get(i).get(j)) 
				maximo=fitness.get(i).get(j);
			}
		}
		p.setmin(minimo);
		p.setmax(maximo);
		intervalos.addElement(p);
		
	}
	return intervalos;

	
}
static Vector<Integer> order( Vector<Vector<Float>> fitness) throws IOException
{
	
	   //Determine which individual are dominated and are not.
	   //Less front better
	   int contador=0, insercciones=0;
	  		
	   Vector <Interval> frente = new Vector<Interval>();//save the rule and the front
	   for(int i =0; i<fitness.size(); i++)
	   {
		   Interval nuevo = new Interval(-1,-1);
		   frente.addElement(nuevo);
	   }
	   
	   
	   //Calculate the minimum and maximum of the fitness of each rule
	   Vector<Interval> intervalos= new Vector<Interval>();
	   intervalos = obtain_interval(fitness);
	   Vector<Integer> contenido= new Vector<Integer>();
	   while(insercciones<fitness.size())
	   {
		 
		for (int i = 0; i < intervalos.size(); i++) 
		{
			
			int domina=-1;
			if(frente.get(i).getmax()==-1)//we don't know the front
			{
				domina=2;
				if(insercciones==fitness.size()-1)
				{
					Interval posicion = new Interval(i,contador);
					frente.set(i, posicion);
					insercciones++;
					domina=-1;
				}
				else
				{
					for (int j = 0; j < intervalos.size(); j++) 
					{
						if(frente.get(j).getmax()==(float)-1 && i!=j)
						{
							double prob = probAbiggerB(intervalos.get(i),intervalos.get(j));
							if(prob==0.5)//impy several rules in the same front
								domina=1;
							else if(prob>0.5)
								domina=2;
							else if (prob<0.5)//is dominated by other rule
							{
								domina=0;
								if(contenido.contains(i)==true)
								{
									Interval posicion = new Interval(i,contador);
									frente.set(i, posicion);
									insercciones++;
									contenido.clear();
								}
								else
								{
									contenido.addElement(i);
								}
								i=j-1;
								break;
							}
						}
					
					} // for j
				}
			}
			if(domina==2 || domina==1)
			{
				Interval posicion = new Interval(i,contador);
				frente.set(i, posicion);
				insercciones++;
				contenido.clear();
				if(domina==2)
					 contador++;
			}
			
			//new BufferedReader(new InputStreamReader(System.in)).readLine();		
		} //del for i
		
		//If the rules are not inserted is that all rules are dominated by all rules
		//All rules are in the same front
		if(insercciones==0)
		{
			for (int i = 0; i < fitness.size(); i++) 
			{
				Interval posicion = new Interval(i,contador);
				frente.set(i, posicion);
			}
			insercciones=fitness.size();
			
		}
		
	   } //while
	   
	   return collocate(frente);
	    
}

static Vector<Integer> collocate(Vector<Interval> frente) throws IOException
{

	Vector<Integer> rules_collocate= new Vector<Integer>(frente.size());
	for(int i=0;i<frente.size();i++)
		rules_collocate.add(i, i);
		
		
	//order the front
	Interval temporal = new Interval();
	for (int i = 0; i < frente.size(); i++) 
	{
		for (int j = 0; j < frente.size(); j++) 
		{
			if (frente.get(i).getmax() >frente.get(j).getmax()) 
			{
				temporal = frente.get(i);
				frente.set(i,frente.get(j));
				frente.set(j,temporal);
			
			}
		}
	}
	
	 for(int i=0;i<frente.size();i++) 
		{
		 rules_collocate.add(i,(int)frente.get(i).getmin());
		}
	return rules_collocate;
}

}

