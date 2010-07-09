package keel.Algorithms.LQD.preprocess.Expert;

import java.io.IOException;

/**
 *
 * File: ranking.java
 *
 * Properties and function to ranking the fuzzy number. Method centroide
 *
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010
 * @version 1.0
 */

public class Ranking {
	
	static int wang( fuzzy actual, fuzzy novel) throws IOException
	{
		
		double ri=centroide(actual,novel);
		if(ri<0.5) //novel is better than actual. We are lookinf for actual and is dominated by novel (actual < novel)
		{
			return 1;
		}
		else if (ri==0.5) //both are the same
			return 2;
		else if (ri>0.5) //(actual > novel)
			return 0;
			
		
		return 0;
			     
	}
	static float centroide(fuzzy o1, fuzzy o2)
	{
		
		//if 01 < 02 return 0 we look for the fuzzy with biggest 1-e
		//if 01 > 02 return 1
		//if( 01 == 01) return 1 (we keep with o1)
		
		
		double xa=(Math.pow(o1.d,2)+Math.pow(o1.c,2)+(o1.c*o1.d)-Math.pow(o1.a,2)-Math.pow(o1.b,2)-(o1.a*o1.b))/(3*(o1.d+o1.c-o1.a-o1.b));
		double xb=(Math.pow(o2.d,2)+Math.pow(o2.c,2)+(o2.c*o1.d)-Math.pow(o2.a,2)-Math.pow(o2.b,2)-(o2.a*o1.b))/(3*(o2.d+o2.c-o2.a-o2.b));
		
		if(o1.es_crisp()==1)
			xa=o1.a;
		
		if(o2.es_crisp()==1)
			xb=o2.a;
		
		
		
		if(xa<xb)
			return 0;
		else if(xa>xb)
			return 1;
		else if (xa==xb)
		{

			double ya=0;
			double yb=0;
			if(o1.es_crisp()==1 && o1.a==0)
				ya=0;
			else
				ya=(((2*o1.b)+o1.a+o1.d+(2*o1.c))*o1.w)/(3*(o1.b+o1.a+o1.d+o1.c));
			
			if(o2.es_crisp()==1 && o2.a==0)
				yb=0;
			else
				yb=(((2*o2.b)+o2.a+o2.d+(2*o2.c))*o2.w)/(3*(o2.b+o2.a+o2.d+o2.c));	
			
			if(ya<yb)
				return 0;
			else if(ya>yb)
				return 1;
			else if (ya==yb)
				return (float)0.5;
			
			
		}
		
		
		return 0;
	}
	


}
