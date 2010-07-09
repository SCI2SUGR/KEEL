package keel.Algorithms.LQD.methods.FGFS_Original;

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

    public float getizd(){return izda;}
    public float getcent(){return centro;}
    public float getdere(){return dcha;}
	   
}
