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

import java.util.Vector;


/**
 * 
 * File: missing.java
 * 
 * Properties and functions when the variables have missing values.
 * The missing values are replaced by the mean, more frequent or
 * the mean of the minimum and maximum
 * 
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010 
 * @version 1.0 
 */


public class missing {

	static Interval[][] values_missing(Interval X[][],int nejemplos,int dimx, int m)
	{
				
		
		Vector<Float> frequent_min= new Vector<Float>();
		Vector<Float> frequent_max= new Vector<Float>();
		for(int i=0;i<dimx;i++)
		{
			float mean_min=0;
			float mean_max=0;
			int contador=0;
			frequent_min.clear();
			frequent_max.clear();
			for(int j=0;j<nejemplos;j++)
			{
				if(X[j][i].getmin()!=Main.MISSING && X[j][i].getmax()!=Main.MISSING)
				{
					mean_min=mean_min+X[j][i].getmin();
					mean_max=mean_max+X[j][i].getmax();
					contador++;
					
					frequent_min.add(X[j][i].getmin());
					frequent_max.add(X[j][i].getmax());
				
				}
			}
		
			int max_ant_min=0;
			float variable_min=0;
			int max_ant_max=0;
			float variable_max=0;
			for(int f=0;f<frequent_min.size();f++)
			{
				
				int max_min=1;
				int max_max=1;
				for(int t=0;t<frequent_min.size();t++)
				{
				  
				    if(t!=f)
				     {
				    
				    	if(frequent_min.get(t).compareTo(frequent_min.get(f))==0)
				    	{
				    		max_min++;
				    	
				    	}
				    	if(frequent_max.get(t).compareTo(frequent_max.get(f))==0)
				    	{
				    		max_max++;
				    	
				    	}
				    	
				    }
				   		
				}
				if(max_min>max_ant_min)
				{
					
					max_ant_min=max_min;
					variable_min=frequent_min.get(f);
				}
				if(max_max>max_ant_max)
				{
				
					max_ant_max=max_max;
					variable_max=frequent_max.get(f);
				}
				
			}
			
			
			
			
			
			
			
			mean_min=mean_min/contador;
			mean_max=mean_max/contador;
		   for(int j=0;j<nejemplos;j++)
			{
				if(X[j][i].getmin()==Main.MISSING && X[j][i].getmax()==Main.MISSING)
				{
					if(m==1)
					{
						X[j][i].setmin(mean_min);
						X[j][i].setmax(mean_max);
					}
					else
					{
						X[j][i].setmin(variable_min);
						X[j][i].setmax(variable_max);
					}
				}
			}
		}
		
		
		
		return X;
	}

}

