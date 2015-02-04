package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierNSLV;

import java.io.*;

public class vectordouble {

	static final double MISSING = -999999999;
	int reservado;
	int numero;
	double[] data;
	
	vectordouble (){
		reservado = 0;
		numero = 0;
		data = null;
	}
	
	vectordouble (int tamano){
		reservado = tamano;
		numero = 0;
		data = new double[tamano];
	}
	
	vectordouble (double[] x, int tamano){
		reservado = tamano;
		numero = tamano;
		data = new double[tamano];
		
		for (int i=0; i<tamano; i++)
			data[i] = x[i];
	}
	
	vectordouble (vectordouble x){
		reservado = x.reservado;
		numero = x.numero;
		data = new double[reservado];
		
		for (int i=0; i<numero; i++)
			data[i] = x.data[i];
	}
	
	public void Realloc (){
		double[] x;
		
		x = new double[reservado*2+1];
		for (int i=0; i<numero; i++)
			x[i] = data[i];
		
		data = x;
		reservado = reservado*2+1;
	}
	

	
	public void Put (double x, int pos){
		if ((pos<0) || (pos>numero)){
			System.out.println ("Does not exist that positioin "+pos+"\n");
		}
		else
			data[pos] = x;		
	}
	
	public void Put (double[] x, int tamano){
		reservado = tamano;
		numero = tamano;
		data = new double[tamano];
		
		for (int i=0; i<tamano; i++)
			data[i] = x[i];
	}
	

	
	public double At (int pos){
		if ((pos<0) || (pos>numero)){
			System.out.println ("Does not exist that positioin "+pos+"\n");
			return MISSING; //Devuelve un error
		}
		else
			return data[pos];
	}
	

	
}
