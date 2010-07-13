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

/**
 * 
 * File: interval.java
 * 
 * Properties and functions of the interval. This is composed by the
 * float
 * 
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010 
 * @version 1.0 
 */

public class interval {

	private float a,b;
	
	public interval(){a=0;b=0;}
	public interval(float a2, float b2){a=a2;b=b2;}
	void setmin(float a2){a=a2;}
	void setmax(float b2){b=b2;}
	public interval(float a2){a=a2;b=a2;}

	public float getmin() {return a;}
	public float getmax() {return b;}
	public float media() {return (float)((a+b)/2.0);}
	
	public void show() {System.out.println("["+a+" "+b+"]");}
	public boolean mayor_zero()
	{
	 if(a>0 || b>0)
		 return true;
	 
	 return false;
	}
	
	 public int escero()
	 {
		 if(a==(float)0 && b==(float)0)
			 return 1;
		 else
			 return 0;
			 
	 }
	public interval multiplicar(float x)
	{
		if(x>0)
			return new interval((float)a*x,(float)b*x);
		else
			return new interval((float)b*x,(float)a*x);
	
	}
	
	public void ordenar()
	{
		float aux=b;
		if(a>b)
		{
			b=a;
			a=aux;
		}
			
	}
	

}

