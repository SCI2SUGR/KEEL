package keel.Algorithms.LQD.methods.FGFS_Rule_Weight;

import java.util.Vector;

/**
*
* File: partitions.java
*
* Properties and functions of fuzzy partitions
*
* @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010
* @version 1.0
*/

public class fuzzy {
	
	float izda, centro, dcha;
	public fuzzy(){izda=(float) -1;centro=(float)-1;dcha=(float)-1;}
	public fuzzy borrosotrapizda(float c, float d)
	{
		centro=c; dcha=d;
		return this;
	};
	
	public fuzzy borrosotriangular(float i, float c, float d) 
	{
	    izda=i; centro=c; dcha=d;
	    return this;
	}
	
	public fuzzy borrosotrapdcha(float i, float c) 
	{
	    izda=i; centro=c; dcha=-1;
	    return this;
	}
	public fuzzy borrosorectangular(float i, float d) 
	{
	    izda=i; dcha=d;centro=-1;
	    return this;
	}
	
	public Vector<Float> cut (float alfa)
	{
		Vector<Float>  cut = new Vector<Float>();
		if(izda==-1)
		{
			if(alfa==1)
			{
			  cut.addElement(getcent());
			  cut.addElement((float)-10000);
			  return cut;
			} 
			else if(alfa==0)
			{
			
				cut.addElement(getdere()); 
				return cut;
			}
			else 
			{
				cut.addElement(getdere() - (alfa*(getdere()-getcent())));
				return cut;			
			}
		
		 }
		else if (dcha==-1)
		{
			if (alfa==1) 
			{
				cut.addElement(getcent());
				cut.addElement((float)10000);
				return cut;
			}
			else if (alfa==0) 
			{
				cut.addElement(getizd());
				return cut;
			}
			else 
			{
				cut.addElement((alfa*(getcent()-getizd()))+getizd());
				return cut;
			}
				
		}
		else 
		{
			if(alfa==1)
			{
				cut.addElement(getcent());
				return cut;
			}
			else if (alfa==0)
			{
				cut.addElement(getdere());
			    cut.addElement(getizd());
			    return cut;
			}
			else  
			{
				cut.addElement((alfa* (getcent()-getizd()))+getizd());
				cut.addElement(getdere() - (alfa*(getdere()-getcent())));
				return cut;
			}
				
		}
		
	}

	
    public float getizd(){return izda;}
    public float getcent(){return centro;}
    public float getdere(){return dcha;}
    
    

    public void setizd(float i){izda=i;}
    public void setcent(float c){centro=c;}
    public void setdere(float d){dcha=d;}
	   
}
