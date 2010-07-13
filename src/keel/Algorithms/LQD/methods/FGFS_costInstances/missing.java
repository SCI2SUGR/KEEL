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

package keel.Algorithms.LQD.methods.FGFS_costInstances;

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
		Vector<Float> frequent_centerb= new Vector<Float>();
		Vector<Float> frequent_centerc= new Vector<Float>();
		
		for(int i=0;i<dimx;i++)
		{
			float mean_iz=0;
			float mean_der=0;
			float mean_centerb=0;
			float mean_centerc=0;
			int cont=0;
			frequent_iz.clear();
			frequent_der.clear();
			frequent_centerb.clear();
			frequent_centerc.clear();
			for(int j=0;j<nejemplos;j++)
			{
				if(X[j][i].geta()!=Main.MISSING && X[j][i].getb()!=Main.MISSING && X[j][i].getd()!=Main.MISSING)
				{
					mean_iz=mean_iz+X[j][i].geta();
					mean_der=mean_der+X[j][i].getd();
					mean_centerb=mean_centerb+X[j][i].getb();
					mean_centerc=mean_centerc+X[j][i].getc();
					cont++;
					
					frequent_iz.add(X[j][i].geta());
					frequent_der.add(X[j][i].getd());
					frequent_centerb.add(X[j][i].getb());
					frequent_centerc.add(X[j][i].getc());
				}
				
			
			}
			
			//Check with is the variable more frequent
			int max_ant_iz=0;
			float variable_iz=0;
			int max_ant_der=0;
			float variable_der=0;
			int max_ant_ceb=0;
			float variable_ceb=0;
			int max_ant_cec=0;
			float variable_cec=0;
			
			
			for(int f=0;f<frequent_centerb.size();f++)
			{
				int max_ceb=1;
				for(int t=0;t<frequent_centerb.size();t++)
				{
				    if(t!=f)
				     {
				    	if(frequent_centerb.get(t).compareTo(frequent_centerb.get(f))==0)
				    	{
				    		max_ceb++;				    	
				    	}
				    }
				}
				if(max_ceb>max_ant_ceb)
				{
					max_ant_ceb=max_ceb;
					variable_ceb=frequent_centerb.get(f);
				}

			}
			
			
			for(int f=0;f<frequent_centerc.size();f++)
			{
				int max_cec=1;
				for(int t=0;t<frequent_centerc.size();t++)
				{
				    if(t!=f)
				     {
				    	if(frequent_centerc.get(t).compareTo(frequent_centerc.get(f))==0)
				    	{
				    		max_cec++;
				    	}
				    }
				}
				if(max_cec>max_ant_cec)
				{
					max_ant_cec=max_cec;
					variable_cec=frequent_centerc.get(f);
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
			
			
		
			mean_iz=mean_iz/cont;
			mean_der=mean_der/cont;
			mean_centerc=mean_centerc/cont;
			mean_centerb=mean_centerb/cont;

			//if the value of the variable is missing we replace this value for the mean
			for(int j=0;j<nejemplos;j++)
			{
				if(X[j][i].es_crisp()==1 && X[j][i].geta()==Main.MISSING)
				{
				
					if(m==1)
					{
						X[j][i].setizd(mean_iz);
						X[j][i].setcenti(mean_centerb);
						X[j][i].setcentd(mean_centerc);
						X[j][i].setdere(mean_der);
					}
					else
					{
						X[j][i].setizd(variable_iz);
						X[j][i].setcenti(variable_ceb);
						X[j][i].setcentd(variable_cec);
						X[j][i].setdere(variable_der);
					}
				}
				
			
				
			}
		}
		
		
		
		return X;
	}
	
	
	
	static Vector<Integer> values_missing_test(float X[][],int nejemplos,int dimx)
	{
			
		//We indicate which example are eliminated for having missing values
		Vector<Integer> eliminate = new Vector<Integer>();
				    
		for(int j=0;j<nejemplos;j++)
		{
			for(int i=0;i<dimx;i++)
			{
				if(X[j][i]==-1)
				{
					eliminate.add(j);
					i=dimx;
				}
			}
		}
		
		
		
		return eliminate;
	}
	

}

