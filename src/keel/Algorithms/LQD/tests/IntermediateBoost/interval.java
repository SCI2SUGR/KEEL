package keel.Algorithms.LQD.tests.IntermediateBoost;

public class interval {
private float a,b;
	
	public interval(){a=0;b=0;}
	public interval(float a2, float b2){a=a2;b=b2;}
	void setmin(float a2){a=a2;}
	void setmax(float b2){b=b2;}
	public float getmin() {return a;}
	public float getmax() {return b;}
	public float media() {return (float)((a+b)/2.0);}
	
	public interval multiplicar(float x)
	{
		if(x>0)
			return new interval(a*x,b*x);
		else
			return new interval(b*x,a*x);
	
	}
}
