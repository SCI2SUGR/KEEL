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

package keel.Algorithms.LQD.preprocess.Expert;

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

	

	static Vector<Vector<fuzzy>> values_missing(Vector<Vector<fuzzy>> X,int nejemplos,int dimx, int m)
	{
					
			Vector<Float> frequent_iz= new Vector<Float>();
			Vector<Float> frequent_der= new Vector<Float>();
			Vector<Float> frequent_centrob= new Vector<Float>();
			Vector<Float> frequent_centroc= new Vector<Float>();
			
			for(int i=0;i<dimx;i++)
			{
				float mean_iz=0;
				float mean_der=0;
				float mean_centrob=0;
				float mean_centroc=0;
				
				float min=1000;
				float max=-1;
				
				int contador=0;
				frequent_iz.clear();
				frequent_der.clear();
				frequent_centrob.clear();
				frequent_centroc.clear();
				for(int j=0;j<nejemplos;j++)
				{
					if(X.get(j).get(i).geta()!=Main.MISSING && X.get(j).get(i).getb()!=Main.MISSING && X.get(j).get(i).getd()!=Main.MISSING)
					{
						mean_iz=mean_iz+X.get(j).get(i).geta();
						mean_der=mean_der+X.get(j).get(i).getd();
						mean_centrob=mean_centrob+X.get(j).get(i).getb();
						mean_centroc=mean_centroc+X.get(j).get(i).getc();
						contador++;
						
						frequent_iz.add(X.get(j).get(i).geta());
						frequent_der.add(X.get(j).get(i).getd());
						frequent_centrob.add(X.get(j).get(i).getb());
						frequent_centroc.add(X.get(j).get(i).getc());
						
						
						if(X.get(j).get(i).geta()<min)
							min=X.get(j).get(i).geta();
						if(X.get(j).get(i).getd()>max)
							max=X.get(j).get(i).getd();
					}
					
				
				}
				
				int max_ant_iz=0;
				float variable_iz=0;
				int max_ant_der=0;
				float variable_der=0;
				int max_ant_ceb=0;
				float variable_ceb=0;
				int max_ant_cec=0;
				float variable_cec=0;
				
				
				for(int f=0;f<frequent_centrob.size();f++)
				{

					int max_ceb=1;
					for(int t=0;t<frequent_centrob.size();t++)
					{
					    if(t!=f)
					     {					 
					    	if(frequent_centrob.get(t).compareTo(frequent_centrob.get(f))==0)
					    	{
					    		max_ceb++;					    	
					    	}
					    }
					}
					if(max_ceb>max_ant_ceb)
					{
						max_ant_ceb=max_ceb;
						variable_ceb=frequent_centrob.get(f);
					}

				}
				
				
				for(int f=0;f<frequent_centroc.size();f++)
				{
					int max_cec=1;
					for(int t=0;t<frequent_centroc.size();t++)
					{
					    if(t!=f)
					     {
					    	if(frequent_centroc.get(t).compareTo(frequent_centroc.get(f))==0)
					    	{
					    		max_cec++;					    	
					    	}
					    }
					}
					if(max_cec>max_ant_cec)
					{
						max_ant_cec=max_cec;
						variable_cec=frequent_centroc.get(f);
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
				mean_centroc=mean_centroc/contador;
				mean_centrob=mean_centrob/contador;
			 
				for(int j=0;j<nejemplos;j++)
				{
					if(X.get(j).get(i).es_crisp()==1 && X.get(j).get(i).geta()==-2)
					{
						if(m==1)
						{
							X.get(j).get(i).setizd(mean_iz);
							X.get(j).get(i).setcenti(mean_centrob);
							X.get(j).get(i).setcentd(mean_centroc);
							X.get(j).get(i).setdere(mean_der);
							
							
						}
						else if (m==2)
						{
							X.get(j).get(i).setizd(min);
							X.get(j).get(i).setcenti(min);
							X.get(j).get(i).setcentd(max);
							X.get(j).get(i).setdere(max);	
						}
						else
						{
							X.get(j).get(i).setizd(variable_iz);
							X.get(j).get(i).setcenti(variable_ceb);
							X.get(j).get(i).setcentd(variable_cec);
							X.get(j).get(i).setdere(variable_der);
						}
					}
					
				
					
				}
			}
			
			
			
			return X;
		}
		
		
		


	}

