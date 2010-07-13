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


public class Interval {

	private float a,b;
	
	public Interval(){a=0;b=0;}
	public Interval(float a2, float b2){a=a2;b=b2;}
	void setmin(float a2){a=a2;}
	void setmax(float b2){b=b2;}

	public float getmin() {return a;}
	public float getmax() {return b;}
	public float media() {return (float)((a+b)/2.0);}
	
	
	public boolean mayor_zero()
	{
	 if(a>0 || b>0)
		 return true;
	 
	 return false;
	}
	public Interval multiplicar(float x)
	{
		if(x>0)
			return new Interval((float)a*x,(float)b*x);
		else
			return new Interval((float)b*x,(float)a*x);
	
	}
	public Interval dividir(float x)
	{
		if(x>0)
			return new Interval((float)a/x,(float)b/x);
		else
		{
			System.out.println("DIVISION BY ZERO");
			System.exit(0);
		}
		return null;
	
	}
	
	public void suma(Interval x)
	{
		x.setmin(x.getmin()+a);
		x.setmax(x.getmax()+b);
		//return x;
	}
	public Interval resta(Interval x)
	{
		
		return new Interval((float)a-x.getmax(),(float)b-x.getmin());
	}
	
	public Interval prod_interval(Interval x)
	{
		
		float min=a*x.getmin();
		float max=a*x.getmin();;
		
		if(min>b*x.getmax())
			min=b*x.getmax();
		

		if(max<b*x.getmax())
			max=b*x.getmax();
		
		if(min>a*x.getmax())
			min=a*x.getmax();
		
		if(max<a*x.getmax())
			max=a*x.getmax();
		
		if(min>b*x.getmin())
			min=b*x.getmin();
		
		if(max<b*x.getmin())
			max=b*x.getmin();
		
		
		return new Interval(min,max);
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

