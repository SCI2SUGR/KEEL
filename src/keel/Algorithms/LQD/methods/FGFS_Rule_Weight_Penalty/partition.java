package keel.Algorithms.LQD.methods.FGFS_Rule_Weight_Penalty;

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
					float value= 1-(x.geta()-content.get(partition).getb())/(content.get(partition).getd()-content.get(partition).getb());
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
        		System.out.println(content.get(i).center+"    "+content.get(i).dcha);
        	else if (i==content.size()-1)
        		System.out.println(content.get(i).izda+"    "+content.get(i).center);
        	else
        		System.out.println(content.get(i).izda+"    "+content.get(i).center+"    "+content.get(i).dcha);
        }
        
        //return 0;
    }

	

}
