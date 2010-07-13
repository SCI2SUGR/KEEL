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

import java.util.Vector;

/**
 *
 * File: fuzzy.java
 *
 * Properties and functions of fuzzy number
 *
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010
 * @version 1.0
 */

public class fuzzy {
	
	float izda, center, dcha;
	public fuzzy(){izda=(float) -1;center=(float)-1;dcha=(float)-1;}
	public fuzzy borrosotrapizda(float c, float d)
	{
		center=c; dcha=d;
		return this;
	};
	
	public fuzzy borrosotriangular(float i, float c, float d) 
	{
	    izda=i; center=c; dcha=d;
	    return this;
	}
	
	public fuzzy borrosotrapdcha(float i, float c) 
	{
	    izda=i; center=c; dcha=-1;
	    return this;
	}
	public fuzzy borrosorectangular(float i, float d) 
	{
	    izda=i; dcha=d;center=-1;
	    return this;
	}
	
	public Vector<Float> cut (float alpha)
	{
		Vector<Float>  cut = new Vector<Float>();
		if(izda==-1)
		{
			if(alpha==1)
			{
			  cut.addElement(getb());
			  cut.addElement((float)-10000);
			  return cut;
			} 
			else if(alpha==0)
			{
				cut.addElement(getd()); 
				return cut;
			}
			else 
			{
				cut.addElement(getd() - (alpha*(getd()-getb())));
				return cut;			
			}
		
		 }
		else if (dcha==-1)
		{
			if (alpha==1) 
			{
				cut.addElement(getb());
				cut.addElement((float)10000);
				return cut;
			}
			else if (alpha==0) 
			{
				cut.addElement(geta());
				return cut;
			}
			else 
			{
				cut.addElement((alpha*(getb()-geta()))+geta());
				return cut;
			}
				
		}
		else 
		{
			if(alpha==1)
			{
				cut.addElement(getb());
				return cut;
			}
			else if (alpha==0)
			{
				cut.addElement(getd());
			    cut.addElement(geta());
			    return cut;
			}
			else  
			{
				cut.addElement((alpha* (getb()-geta()))+geta());
				cut.addElement(getd() - (alpha*(getd()-getb())));
				return cut;
			}
				
		}
		
	}

	
    public float geta(){return izda;}
    public float getb(){return center;}
    public float getd(){return dcha;}
    
    

    public void setizd(float i){izda=i;}
    public void setcent(float c){center=c;}
    public void setdere(float d){dcha=d;}
	   
}

