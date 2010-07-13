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

	

	static fuzzy[][] values_missing(fuzzy X[][],int nejemplos,int dimx, int m)
	{
				
		Vector<Float> frequent_iz= new Vector<Float>();
		Vector<Float> frequent_der= new Vector<Float>();
		Vector<Float> frequent_centro= new Vector<Float>();
		
		for(int i=0;i<dimx;i++)
		{
			float mean_iz=0;
			float mean_der=0;
			float mean_centro=0;
			int contador=0;
			frequent_iz.clear();
			frequent_der.clear();
			frequent_centro.clear();
			
			for(int j=0;j<nejemplos;j++)
			{
				if(X[j][i].geta()!=Main.MISSING && X[j][i].getd()!=Main.MISSING)
				{
					mean_iz=mean_iz+X[j][i].geta();
					mean_der=mean_der+X[j][i].getd();
					contador++;
					
					frequent_iz.add(X[j][i].geta());
					frequent_der.add(X[j][i].getd());
				}
				
				if(X[j][i].getb()!=Main.MISSING)
				{
					mean_centro=mean_centro+X[j][i].getb();
					frequent_centro.add(X[j][i].getb());
				}
			}
			

			int max_ant_iz=0;
			float variable_iz=0;
			int max_ant_der=0;
			float variable_der=0;
			int max_ant_ce=0;
			float variable_ce=0;
			
			
			for(int f=0;f<frequent_centro.size();f++)
			{
				int max_ce=1;
				for(int t=0;t<frequent_centro.size();t++)
				{
				    if(t!=f)
				     {				    
				    	if(frequent_centro.get(t).compareTo(frequent_centro.get(f))==0)
				    	{
				    		max_ce++;				    	
				    	}
				    }
				}
				if(max_ce>max_ant_ce)
				{					
					max_ant_ce=max_ce;
					variable_ce=frequent_centro.get(f);
				}

			}
			
			for(int f=0;f<frequent_iz.size();f++)
			{
				int max_min=1;
				int max_max=1;
				for(int t=0;t<frequent_iz.size();t++)
				{				   
				    if(t!=f)
				     {
				    	if(frequent_iz.get(t).compareTo(frequent_iz.get(f))==0)
				    	{
				    		max_min++;
				    	}
				    	if(frequent_der.get(t).compareTo(frequent_der.get(f))==0)
				    	{
				    		max_max++;
				    	}
				    	
				    }
				   		
				}
				if(max_min>max_ant_iz)
				{
					max_ant_iz=max_min;
					variable_iz=frequent_iz.get(f);
				}
				if(max_max>max_ant_der)
				{
					max_ant_der=max_max;
					variable_der=frequent_der.get(f);
				}
				
			}
			
			
		
			mean_iz=mean_iz/contador;
			mean_der=mean_der/contador;
			mean_centro=mean_centro/contador;
		   
			for(int j=0;j<nejemplos;j++)
			{
				if(X[j][i].geta()==Main.MISSING && X[j][i].getd()==Main.MISSING)
				{
					if(m==1)
					{
						X[j][i].setizd(mean_iz);
						X[j][i].setdere(mean_der);
					}
					else
					{
						X[j][i].setizd(variable_iz);
						X[j][i].setdere(variable_der);
					}
				}
				
				if(X[j][i].getb()==Main.MISSING )
				{
					if(m==1)
						X[j][i].setcent(mean_centro);
					else
						X[j][i].setcent(variable_ce);
				}
				
			}
		}
		
		
		
		return X;
	}
	
	
	
	static float [][] values_missing_test(float X[][],int nejemplos,int dimx,  int m)
	{
				
		//calculamos la mean de cada una de las variables
		Vector<Float> frequent= new Vector<Float>();
		for(int i=0;i<dimx;i++)
		{
			float mean=0;
			int contador=0;
			frequent.clear();
			for(int j=0;j<nejemplos;j++)
			{
				if(X[j][i]!=-1)
				{
					mean=mean+X[j][i];
					contador++;
					
					frequent.add(X[j][i]);

				}
			}

			int max_ant=0;
			float variable=0;
			for(int f=0;f<frequent.size();f++)
			{
				
				int max=1;
				for(int t=0;t<frequent.size();t++)
				{
				    if(t!=f)
				     {
				    	if(frequent.get(t).compareTo(frequent.get(f))==0)
				    	{
				    		max++;
				    		
				    	}
				    }
				   		
				}
				if(max>max_ant)
				{
					max_ant=max;
					variable=frequent.get(f);
				}
				
			}
			
			mean=mean/contador;
		    for(int j=0;j<nejemplos;j++)
			{
				if(X[j][i]==-1)
				{
					if(m==1)
						X[j][i]=mean;
					else
						X[j][i]=variable;
				}
			}
		}
		
		
		
		return X;
	}
	

}

