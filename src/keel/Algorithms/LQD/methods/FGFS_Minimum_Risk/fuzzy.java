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
 * Properties and functions of fuzzy number
 * 
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010 
 * @version 1.0 
 */


public class fuzzy {
	
	float a, b,c, d,w;
	public fuzzy(){a=(float) -1;b=(float)-1;c=(float)-1;d=(float)-1;w=1;}
	public fuzzy(float v){a=(float) v;b=(float)v;c=(float)v;d=(float)v;w=1;}
	public fuzzy borrosotrapizda(float center, float de)
	{
		c=center; d=de; a=-1; b=center;
		return this;
	};
	
	public fuzzy borrosotriangular(float i, float ce, float de) 
	{
	    a=i; b=ce;c=ce; d=de;
	    return this;
	}
	
	public fuzzy borrosotrapezoidal(float i, float ce, float ce2, float de) 
	{
	    a=i; b=ce;c=ce2; d=de;
	    return this;
	}
	
	public fuzzy borrosotrapdcha(float i, float ce) 
	{
	    a=i; b=ce; d=-1; c=ce;
	    return this;
	}
	public fuzzy borrosorectangular(float i, float de) 
	{
	    a=i; b=i;c=de;d=de;
	    return this;
	}
	
	public int es_rect()
	{
		if(a==b && c==d)
			return 1;
		else
			return 0;
			
	}
	public int es_crisp()
	{
		if(a==b && a==c && a==d)
			return 1;
		else
			return 0;
			
	}
	public int es_trian()
	{
		if(b==c)
			return 1;
		else
			return 0;
			
	}
	 static public fuzzy multi(fuzzy x, fuzzy y)
	{
		 fuzzy mul = new fuzzy();
		 mul.borrosotrapezoidal((x.a*y.a),(x.b*y.b),(x.c*y.c), (x.d*y.d));
		 
		 
		return mul;
	}
	 static public fuzzy multinumero(float peso, fuzzy y)
		{
			 fuzzy mul = new fuzzy();
			 mul.borrosotrapezoidal((peso*y.a),(peso*y.b),(peso*y.c), (peso*y.d));
			 
			 
			return mul;
		}
	 
	 static public fuzzy divnum(fuzzy x,float num)
		{
			 fuzzy mul = new fuzzy();
			 float a,b,c,d; 
				 a=(x.a/num);
			 
				 b=(x.b/num);
			
				 c=(x.c/num);
			
				 d=(x.d/num);
			
		
			 mul.borrosotrapezoidal(a,b,c,d);
			return mul;
		}
	 static public fuzzy div(fuzzy x, fuzzy y)
		{
			 fuzzy mul = new fuzzy();
			 float a=1,b=1,c=1,d=1;
			 
			 
			 if(x.a<y.d)
				 a=(x.a/y.d);
			 if(x.b<y.c)
				 b=(x.b/y.c);
			 if((x.c<y.b))
				 c=(x.c/y.b);
			 if((x.d<y.a))
				 d=(x.d/y.a);
		
			 mul.borrosotrapezoidal(a,b,c,d);
			return mul;
		}
	  public int getcero()
	 {
		 if(a==(float)0 && b==(float)0 && c==(float)0 && d==(float)0)
			 return 1;
		 else
			 return 0;
			 
	 }
	 static public fuzzy resta(fuzzy x, fuzzy y)
		{
			 fuzzy mul = new fuzzy();
			 mul.borrosotrapezoidal((x.a-y.d),(x.b-y.c),(x.c-y.b), (x.d-y.a));
			 
			 
			return mul;
		}
	 public fuzzy media()
	 {
		 if(a==b && c==d) //interval
			 return new fuzzy((a+d)/2);
		 else  //triangular
			 return new fuzzy(b);
			 
	
		 
	 }
	static public fuzzy suma_ltf(fuzzy x,fuzzy y)
	{
		fuzzy mul = new fuzzy();
		mul.borrosotrapezoidal((x.a+y.a-(x.a*y.a)), (x.b+y.b-(x.b*y.b)), (x.c+y.c-(x.c*y.c)), (x.d+y.d-(x.d*y.d)));
		
		return mul;
	}
	static public fuzzy suma(fuzzy x,fuzzy y)
	{
		fuzzy mul = new fuzzy();
		mul.borrosotrapezoidal((x.a+y.a), (x.b+y.b), (x.c+y.c), (x.d+y.d));
		
		return mul;
	}
	public Vector<Float> cut (float alfa)
	{
		Vector<Float>  cut = new Vector<Float>();
		if(a==-1)//Left trapeze
		{
			if(alfa==1)
			{
			  cut.addElement(getb());
			  cut.addElement((float)-10000);
			  return cut;
			} 
			else if(alfa==0)
			{
				cut.addElement(getd()); //value bigger that the last partition
				return cut;
			}
			else //betwenn zero and one
			{
				cut.addElement(getd() - (alfa*(getd()-getc())));
				return cut;			
			}
		
		 }
		else if (d==-1)//right trapeze
		{
			if (alfa==1)//value less thatn first partition 
			{
				cut.addElement(getb());
				cut.addElement((float)10000);//value less that first partition
				return cut;
			}
			else if (alfa==0) //value bigger than last partition
			{
				cut.addElement(geta());
				return cut;
			}
			else //between 0 and 1
			{
				cut.addElement((alfa*(getb()-geta()))+geta());
				return cut;
			}
				
		}
		else //the partition is triangular
		{
			if(alfa==1)
			{
				cut.addElement(getb());
				return cut;
			}
			else if (alfa==0)//value less that first partition
			{
				cut.addElement(getd());
			    cut.addElement(geta());
			    return cut;
			}
			else  
			{
				cut.addElement((alfa* (getb()-geta()))+geta());
				cut.addElement(getd() - (alfa*(getd()-getc())));
				return cut;
			}
				
		}
		
	}
	public int contains(float numero)
	{
		if(numero>=a && numero<=d)
			return 1;
		
		return 0;
	}


	public void show(){System.out.print(a+" "+b+" "+c+" "+d+"\n");}
    public float geta(){return a;}
    public float getb(){return b;}
    public float getc(){return c;}
    public float getd(){return d;}
    
   
    public void setizd(float i){a=i;}
    public void setcenti(float c){b=c;}
    public void setcentd(float ce){c=ce;}
    public void setdere(float de){d=de;}
	   
}

