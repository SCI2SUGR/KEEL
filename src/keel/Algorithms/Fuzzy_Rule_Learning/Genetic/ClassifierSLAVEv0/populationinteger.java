

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierSLAVEv0;
import java.io.*;
import java.util.Random;

import org.core.Randomize;


public class populationinteger implements Cloneable {
	
	/**	
	 * <p>
	 * It contains the methods for handling the integer population of individuals
	 * </p>
	 */

	double prob_mutacion;
	double prob_cruce;
	int elitismo;
	int n_individuos;
	int[] tamano;
	boolean[] modificado;
	double[] valoracion;
	int rango;
	int[][] individuos;
	
	
	populationinteger (){
		prob_mutacion = 0.0;
		prob_cruce = 0.0;
		elitismo = 0;
		n_individuos = 0;
		tamano = null;
		modificado = null;
		valoracion = null;
		individuos = null;
		rango = 0;
	}
	
	
	populationinteger (int rang, double mut, double cruce, int eli, int n){
		prob_mutacion = mut;
		prob_cruce = cruce;
		elitismo = eli;
		n_individuos = n;
		rango = rang;
		tamano = new int[n_individuos];
		
		for (int i=0; i<n_individuos; i++)
			tamano[i] = 0;

		modificado = new boolean[n_individuos];
		for (int i=0; i<n_individuos; i++)
			modificado[i] = true;

		valoracion = new double[n_individuos];

		individuos = new int[n_individuos][];
		for (int i=0; i<n_individuos; i++)
			individuos[i] = null;
	}
	

	populationinteger (int rang, double mut, double cruce, int eli, int n, int tama){
		prob_mutacion = mut;
		prob_cruce = cruce;
		elitismo = eli;
		n_individuos = n;
		rango = rang;
		tamano= new int[n_individuos];
		
		for (int i=0; i<n_individuos; i++)
			tamano[i] = tama;

		modificado = new boolean[n_individuos];
		for (int i=0; i<n_individuos; i++)
			modificado[i] = true;

		valoracion = new double[n_individuos];
		
		individuos = new int[n_individuos][];
		for (int i=0; i<n_individuos; i++)
			individuos[i] = new int[tamano[i]];
	}
	

	populationinteger (populationinteger x){
		prob_mutacion = x.prob_mutacion;
		prob_cruce = x.prob_cruce;
		elitismo = x. elitismo;
		n_individuos = x.n_individuos;
		rango = x.rango;
		
		tamano = new int[n_individuos];
		for (int i=0; i<n_individuos; i++)
			tamano[i] = x.tamano[i];


		modificado= new boolean[n_individuos];
		for (int i=0; i<n_individuos; i++)
			modificado[i] = x.modificado[i];

		valoracion = new double[n_individuos];
		for (int i=0; i<n_individuos; i++)
			valoracion[i] = x.valoracion[i];

		individuos = new int[n_individuos][];
		for (int i=0; i<n_individuos; i++) {
			if (tamano[i]>0){
				individuos[i] = new int[tamano[i]];
				for (int j=0; j<tamano[i]; j++)
					individuos[i][j] = x.individuos[i][j];
			}
		}
	}
	
	
	
	public Object clone(){
		populationinteger obj = null;
		try{
			obj = (populationinteger) super.clone();
		}catch (CloneNotSupportedException ex){
			System.out.println ("\nNo se puede duplicar el objeto.\n");
		}
		obj.tamano = (int[]) obj.tamano.clone();
		obj.modificado = (boolean[]) obj.modificado.clone();
		obj.valoracion = (double[]) obj.valoracion.clone();
		
		obj.individuos = (int[][]) obj.individuos.clone();
		for (int i=0; i<obj.individuos.length; i++){
			obj.individuos[i] = (int[]) obj.individuos[i].clone();
		}
		
		return obj;
	}
	
	
	
	public int[] Individual (int i, Int_t tama){
		tama.value = tamano[i];
		return individuos[i];
	}
	
	
	public boolean Modified (int i){
		return modificado[i];
	}
	
	
	public void Pass (int i, populationinteger x, int j){
		tamano[i] = x.tamano[i];
		individuos[i] = new int[tamano[i]];
		for (int k=0; k<tamano[i]; k++)
			individuos[i][k] = x.individuos[j][k];
	}
	
	
	public void Swap (int i, int j){
		Swap_int (tamano, i, j);
		Swap_bool (modificado, i, j);
		Swap_double (valoracion, i, j);
		int[] p = individuos[i];
		individuos[i] = individuos[j];
		individuos[j] = p;
	}
	
	
	public void Swap_int (int[] v, int i, int j){
		int aux = v[i];
		v[i] = v[j];
		v[j] = aux;
	}
	
	
	public void Swap_double (double[] v, int i, int j){
		double aux = v[i];
		v[i] = v[j];
		v[j] = aux;
	}
	
	
	public void Swap_bool (boolean[] v, int i, int j){
		boolean aux = v[i];
		v[i] = v[j];
		v[j] = aux;
	}
	
	
	public int[] Code (int i, int[] vector, int pos){
		vector[pos] = tamano[i];
		int[] v = new int[vector[pos]];
		for (int j=0; j<vector[pos]; j++)
			v[j] = individuos[i][j];
		
		return v;
	}
	
	
	public void Sort (){
		for (int i=0; i<n_individuos-1; i++){
			for (int j=n_individuos-1; j>i; j--){
				if (valoracion[j]>valoracion[j-1])
					Swap(j, j-1);
			}
		}	
	}
	
	
	public void Paint (int i){
		for (int j=0; j<tamano[i]; j++)
			System.out.println (individuos[i][j]+" ");
		System.out.println ("\n");
	}
	
	
	public void PaintFitness (int i){
		System.out.println ("Fitness: "+valoracion[i]+"\n");
	}
	
	
	public void PaintInFile (int i) throws IOException{
		FileOutputStream f;
		String cadena;
		
		try {
			f = new FileOutputStream("slave.log");
		} catch(FileNotFoundException e) {
			System.out.println("No se pudo crear.\n");
			return;
		}
		
		for (int j=0; j<tamano[i]; j++){
			cadena = ""+individuos[i][j]+" ";
			byte[] buf = cadena.getBytes();
			f.write(buf);
		}
		cadena = "\n";
		byte[] buf = cadena.getBytes();
		f.write(buf);
		f.close();
	}
	
	
	public void PaintFitnessInFile (int i) throws IOException{
		FileOutputStream f;
		String cadena;
		
		try {
			f = new FileOutputStream("slave.log");
		} catch(FileNotFoundException e) {
			System.out.println("No se pudo crear.\n");
			return;
		}
		
		cadena = "Fitness: "+valoracion[i]+"\n";
		byte[] buf = cadena.getBytes();
		f.write(buf);
		f.close();
	}
	
	
	public void PutValue (int indiv, int bit, int value){
		individuos[indiv][bit] = value;
	}
	
	
	public int GetValue (int indiv, int bit){
		return individuos[indiv][bit];
	}
	
	
	
	
	

	
}
