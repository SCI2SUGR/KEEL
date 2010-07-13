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

package keel.Algorithms.LQD.methods.FGFS_Minimum_Risk;

import java.util.Vector;

/**
 * 
 * File: fuzzy.java
 * 
 * Properties and functions of fuzzy partitions
 * 
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010 
 * @version 1.0 
 */


public class fuzzyPartition {

	Vector<fuzzy> content= new Vector<fuzzy>();
	
	public fuzzyPartition(){}
	public int size()
	{
		return content.size();
	}
	public fuzzy get(int i)
	{
		return content.get(i);
	}
	
	public float aproximation(int partition, fuzzy x) 
	{
		if(partition==0)//left 
		{	
				if (x.getd()<=content.get(partition).getb()) 
					return 1;
				else if (x.geta()>=content.get(partition).getd())
					return 0;
				else //between 0 and 1
				{
					float value= 1-(x.geta()-content.get(partition).getb())/(content.get(partition).getd()-content.get(partition).getc());
					return value;			
				}
		
		 }
		
		else if (partition==content.size()-1)//right
		{
				if (x.geta()>=content.get(partition).getb()) 
					return 1;
				else if (x.getd()<=content.get(partition).geta()) 				
					return 0;
				else 
				{
					float value= (x.getd()-content.get(partition).geta())/(content.get(partition).getb()-content.get(partition).geta());
					return value;
				}
			
				
		}
		else //triangular
		{
				if (x.getd()<=content.get(partition).geta())
					return 0;
				else if (x.geta()>=content.get(partition).getd()) 
					return 0; 
				else if(x.geta()==content.get(partition).getb() && x.getd()==content.get(partition).getb())
					return 1;
				else if(x.geta()<content.get(partition).getb() && x.getd()>content.get(partition).getb())
					return 1;
			
				else if(x.getd()<content.get(partition).getb())
				{
					float value= (x.getd()-content.get(partition).geta())/(content.get(partition).getb()-content.get(partition).geta());
					return value;
				}
				else 
				{
					float value= 1-(x.geta()-content.get(partition).getb())/(content.get(partition).getd()-content.get(partition).getb());
					return value;
				}
				
		}
		
	
	}
	
	public float membership(int partition,float x)
	{
		
		if(partition==0)
		{
			if (x<=content.get(partition).getb()) 
				return 1;
			else if (x>=content.get(partition).getd()) 
				return 0;
			else 
			{
				float value= 1-(x-content.get(partition).getb())/(content.get(partition).getd()-content.get(partition).getb());
				return value;			
			}
		 }
		else if (partition==content.size()-1)
		{
			if (x>=content.get(partition).getb()) 
				return 1;
			else if (x<=content.get(partition).geta()) 				
				return 0;
			else 
			{
				float value= (x-content.get(partition).geta())/(content.get(partition).getb()-content.get(partition).geta());
				return value;
			}
				
		}
		else 
		{
			if (x<=content.get(partition).geta())
			{
				
				return 0;
			}
			else if (x>=content.get(partition).getd()) 
				return 0; 
			else if(x<=content.get(partition).getb())
			{
				float value= (x-content.get(partition).geta())/(content.get(partition).getb()-content.get(partition).geta());
				return value;
			}
			else
			{
				float value= 1-(x-content.get(partition).getb())/(content.get(partition).getd()-content.get(partition).getb());
				return value;
			}
				
		}
	}
		
	
	//Obtain the partition from the variables
    public fuzzyPartition(float min, float max, int n)
    {
    	 if (n<=0) return;
    	 fuzzy datos;
    	 float d=0;
    	 if(n==1)
    		 d=0;
    	 else
    		 d=(max-min)/(n-1);
    	 
    	 float iz=min-d, ce=min, de=min+d;
    	 datos= new fuzzy();
    	 content.addElement(datos.borrosotrapizda(ce,de));
    	 for (int i=1;i<n-1;i++) 
    	 {
    		 iz+=d; ce+=d; de+=d;
    		 datos= new fuzzy();
    		 content.addElement(datos.borrosotriangular(iz,ce,de));
    	 }
    	 iz+=d; ce+=d; de+=d;
    	 datos= new fuzzy();
    	 content.addElement(datos.borrosotrapdcha(iz,ce));

    	
    }
    
    public void show()
    {
    	System.out.println("FUZZY PARTITIONS:");
        for (int i=0; i<content.size();i++)
        {
        	if(i==0)
        		System.out.println(content.get(i).c+"    "+content.get(i).d);
        	else if (i==content.size()-1)
        		System.out.println(content.get(i).a+"    "+content.get(i).b);
        	else
        		System.out.println(content.get(i).a+"    "+content.get(i).c+"    "+content.get(i).d);
        }
        
    }

	

}

