package keel.Algorithms.LQD.methods.FGFS_Original;

/**
 *
 * File: interval.java
 *
 * Properties and functions of the interval. This is composed by the
 * float
 *
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010
 * @version 1.0
 */

public class Interval {

	private float a,b;
	
	public Interval(){a=0;b=0;}
	public Interval(float a2, float b2){a=a2;b=b2;}
	void setmin(float a2){a=a2;}
	void setmax(float b2){b=b2;}
	public float getmin() {return a;}
	public float getmax() {return b;}
	public float media() {return (float)((a+b)/2.0);}
	
	public Interval multiplicar(float x)
	{
		if(x>0)
			return new Interval((float)a*x,(float)b*x);
		else
			return new Interval((float)b*x,(float)a*x);
	
	}
	public Interval dividir(float x)
	{
		if(x>0)
			return new Interval((float)a/x,(float)b/x);
		else
		{
			System.out.println("DIVISION BY ZERO");
			System.exit(0);
		}
		return null;
	
	}
	public Interval suma(Interval x)
	{
		
		return new Interval((float)a+x.getmin(),(float)b+x.getmax());
	}
	public void ordenar()
	{
		float aux=b;
		if(a>b)
		{
			b=a;
			a=aux;
		}
			
	}
	

}
