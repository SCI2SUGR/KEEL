
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierSLAVEv0;

import java.io.*;

public class fuzzy_t implements Cloneable {
	
	/**	
	 * <p>
	 * It contains the methods for handling the fuzzy labels
	 * </p>
	 */
	
	final double MISSING = -999999999;
	double a;
	double b;
	double c;
	double d;
	boolean menosinfinito;	
	boolean masinfinito;	
	String nombre;	
	

	/**
	 * <p>
	 * Default Constructor
	 * </p>
	 */
	
	fuzzy_t (){
		a = b = c = d = 0;
		menosinfinito = masinfinito = false;
		nombre = "Creado, no usado";
	}
	

	fuzzy_t (double a, double b, double c, double d, String name){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		nombre = name;
		menosinfinito = masinfinito = false;
	}
	

	fuzzy_t (double a, double b, double c, double d, String name, boolean menos, boolean mas){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		menosinfinito = menos;
		masinfinito = mas;
		nombre = name;
	}
	

	fuzzy_t (fuzzy_t x){
		System.out.println("Copying...\n");
		
		a = x.a;
		b = x.b;
		c = x.c;
		d = x.d;
		menosinfinito = x.menosinfinito;
		masinfinito = x.masinfinito;
		nombre = x.nombre;
	}
	
	
	
	public Object clone(){
		fuzzy_t obj = null;
		try{
			obj = (fuzzy_t) super.clone();
		}catch (CloneNotSupportedException ex){
			System.out.println ("\nError.\n");
		}
		
		return obj;
	}
	
	
	
	
	public void Assign (double a, double b, double c, double d, String name, boolean menos, boolean mas){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		menosinfinito = menos;
		masinfinito = mas;
		nombre = name;
	}
	

	public void Assign (double a, double b, double c, double d, String name){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		menosinfinito = false;
		masinfinito = false;
		nombre = name;
	}
	
	
	/**
	 * <p>
	 * Calculates the adaptation degree of "x" with a label
	 * </p>
	 * @param x double A value
	 * @return double The adaptation degree
	 */
	
	public double Adaptation (double x){
		if (x == MISSING)
			return 1;
		if ((menosinfinito && x<c) || (masinfinito && x>b))
			return 1;
		if (x<a)
			return 0;
		else{
			if (x<b)
				return (x-a)/(b-a);
			else{
				if (x<=c)
					return 1;
				else{
					if (x<d)
						return (d-x)/(d-c);
					else
						return 0;
				}
			}	
		}
	}
	

	
	
	public void Paint (){
		System.out.println("Name: "+nombre+"\n");
		if (menosinfinito)
			System.out.println("[-inf, -inf, "+c+", "+d+"]\n");
		else{
			if (masinfinito)
				System.out.println("["+a+", "+b+", inf, inf]\n");
			else
				System.out.println("["+a+", "+b+", "+c+", "+d+"]\n");
		}
	}
	
	
	public void Print (){
		System.out.println(nombre);
	}
	
	
	public String SPrint (){
		return nombre;
	}
	
	
	public fuzzy_t FuzzyLabel (){
		fuzzy_t aux;
		
		aux = (fuzzy_t) this.clone();
		return aux;
	}
	
	
	public double CenterLabel (){
		if (menosinfinito)
			return c;
		else{
			if (masinfinito)
				return b;
			else
				return (b+c)/2.0;
		}
	}
	
	
	public boolean IsDiscrete (){
		return (a == d);
	}
	
	
	public boolean IsInterval (){
		return (a==b && c==d && b!=c);
	}
	
	
	public boolean IsFuzzy (){
		return (!IsDiscrete () && !IsInterval ());
	}
	
	
	public double Area (){
		return ((b-a)/2.0)+((d-c)/2.0)+(c-d);
	}
	

	
	
	

}
