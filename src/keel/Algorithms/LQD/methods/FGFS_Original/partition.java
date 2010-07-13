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
* File: partitions.java
*
* Properties and functions of the partitions of the fuzzy number
*
* @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010
* @version 1.0
*/

public class partition {

	Vector<fuzzy> content= new Vector<fuzzy>();
	
	public partition(){}
	public int size()
	{
		return content.size();
	}
	public fuzzy get(int i)
	{
		return content.get(i);
	}
	
	public Vector<Float> content (int particion, float alfa)
	{
		
		Vector<Float>  valor = new Vector<Float>();
		if(particion==0)
		{
			
			if(alfa==1)
			{
			
			  valor.addElement(content.get(particion).getcent());
			  valor.addElement((float)-10000);
			  return valor;
			} 
			else if(alfa==0)
			{
			
				valor.addElement(content.get(particion).getdere()); 
				return valor;
			}
			else
			{
				valor.addElement(content.get(particion).getdere() - (alfa*(content.get(particion).getdere()-content.get(particion).getcent())));
				return valor;			
			}
		 }
		else if (particion==content.size()-1)
		{
			if (alfa==1) 
			{
				valor.addElement(content.get(particion).getcent());
				valor.addElement((float)10000);
				return valor;
			}
			else if (alfa==0) 
			{
				valor.addElement(content.get(particion).getizd());
				return valor;
			}
			else
			{
				valor.addElement((alfa*(content.get(particion).getcent()-content.get(particion).getizd()))+content.get(particion).getizd());
				return valor;
			}
				
		}
		else 
		{
			if(alfa==1)
			{
				valor.addElement(content.get(particion).getcent());
				return valor;
			}
			else if (alfa==0)
			{
				valor.addElement(content.get(particion).getdere());
			    valor.addElement(content.get(particion).getizd());
			    return valor;
			}
			else  
			{
				valor.addElement((alfa* (content.get(particion).getcent()-content.get(particion).getizd()))+content.get(particion).getizd());
				valor.addElement(content.get(particion).getdere() - (alfa*(content.get(particion).getdere()-content.get(particion).getcent())));
				return valor;
			}
				
		}
		
		
	}
	
	public float pertenenecia_intervalo(int particion, Interval x)
	{
		
		if(particion==0)//trapecio izquierdo
		{
			
			if (x.getmax()<=content.get(particion).getcent() || x.getmin()<=content.get(particion).getcent()) 
				return 1;
			else if (x.getmin()>=content.get(particion).getdere())
				return 0;
			else 
			{
				float valor= 1-(x.getmin()-content.get(particion).getcent())/(content.get(particion).getdere()-content.get(particion).getcent());
				return valor;			
			} 
		 }
		
		else if (particion==content.size()-1)
		{
			
			if (x.getmin()>=content.get(particion).getcent() || x.getmax()>=content.get(particion).getcent())
				return 1;
			else if (x.getmax()<=content.get(particion).getizd())			
				return 0;
			else 
			{
				float valor= (x.getmax()-content.get(particion).getizd())/(content.get(particion).getcent()-content.get(particion).getizd());
				return valor;
			}
				
		}
		else 
		{
			
			if (x.getmax()<=content.get(particion).getizd())
				return 0;
			else if (x.getmin()>=content.get(particion).getdere())
				return 0; 
			else if(x.getmin()==content.get(particion).getcent() || x.getmax()==content.get(particion).getcent())
				return 1;
			else if(x.getmin()<content.get(particion).getcent() && x.getmax()>content.get(particion).getcent())
				return 1;
			
			else if(x.getmax()<content.get(particion).getcent())
			{
				float valor= (x.getmax()-content.get(particion).getizd())/(content.get(particion).getcent()-content.get(particion).getizd());
				return valor;
			}
			else 
			{
				float valor= 1-(x.getmin()-content.get(particion).getcent())/(content.get(particion).getdere()-content.get(particion).getcent());
				return valor;
			}
				
		}
		
		
		
		
	}
	
	public float membership(int partition,float x)
	{
		
		if(partition==0)
		{
			if (x<=content.get(partition).getcent()) 
				return 1;
			else if (x>=content.get(partition).getdere())
				return 0;
			else 
			{
				float valor= 1-(x-content.get(partition).getcent())/(content.get(partition).getdere()-content.get(partition).getcent());
				return valor;			
			}
		 }
		else if (partition==content.size()-1)
		{
			if (x>=content.get(partition).getcent()) 
				return 1;
			else if (x<=content.get(partition).getizd()) 				
				return 0;
			else 
			{
				float valor= (x-content.get(partition).getizd())/(content.get(partition).getcent()-content.get(partition).getizd());
				return valor;
			}
				
		}
		else 
		{
			
			if (x<=content.get(partition).getizd())
			{
				
				return 0;
			}
			else if (x>=content.get(partition).getdere()) 
				return 0; 
			else if(x<=content.get(partition).getcent())
			{
				float valor= (x-content.get(partition).getizd())/(content.get(partition).getcent()-content.get(partition).getizd());
				return valor;
			}
			else
			{
				float valor= 1-(x-content.get(partition).getcent())/(content.get(partition).getdere()-content.get(partition).getcent());
				return valor;
			}
				
		}
	}
		
    public partition(float min, float max, int n)
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
        		System.out.println(content.get(i).centro+"    "+content.get(i).dcha);
        	else if (i==content.size()-1)
        		System.out.println(content.get(i).izda+"    "+content.get(i).centro);
        	else
        		System.out.println(content.get(i).izda+"    "+content.get(i).centro+"    "+content.get(i).dcha);
        }
        
        //return 0;
    }

	

}

