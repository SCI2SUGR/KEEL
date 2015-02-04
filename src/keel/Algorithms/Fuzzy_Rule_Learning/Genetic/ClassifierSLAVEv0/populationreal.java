
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierSLAVEv0;

import java.io.*;
import java.util.Random;

import org.core.Randomize;

public class populationreal implements Cloneable {
	
	/**	
	 * <p>
	 * It contains the methods for handling the real population of individuals
	 * </p>
	 */

	double prob_mutacion;
	double prob_cruce;
	int elitismo;
	int n_individuos;
	int[] tamano;
	boolean[] modificado;
	double rango_i;
	double rango_s;
	double[][] individuos;
	
	
	populationreal (){
		prob_mutacion = 0.0;
		prob_cruce = 0.0;
		elitismo = 0;
		n_individuos = 0;
		tamano = null;
		modificado = null;
		individuos = null;
		rango_i = 0.0;
		rango_s = 0.0;
	}
	
	
	populationreal (double raninf, double ransup, double mut, double cruce, int eli, int n){	
		prob_mutacion = mut;
		prob_cruce = cruce;
		elitismo = eli;
		n_individuos = n;
		rango_i = raninf;
		rango_s = ransup;
		tamano = new int[n_individuos];
		for (int i=0; i<n_individuos; i++)
			tamano[i] = 0;

		modificado = new boolean[n_individuos];
		for (int i=0; i<n_individuos; i++)
			modificado[i] = true;

		individuos = new double[n_individuos][];
		for (int i=0; i<n_individuos; i++)
			individuos[i] = null;
	}
	
	
	populationreal (double raninf, double ransup, double mut, double cruce, int eli, int n, int[] tama){
		prob_mutacion = mut;
		prob_cruce = cruce;
		elitismo = eli;
		n_individuos = n;
		rango_i = raninf;
		rango_s = ransup;
		tamano = new int[n_individuos];
		for (int i=0; i<n_individuos; i++)
			tamano[i] = tama[i];

		modificado = new boolean[n_individuos];
		for (int i=0; i<n_individuos; i++)
			modificado[i] = true;

		individuos = new double[n_individuos][];
		for (int i=0; i<n_individuos; i++)
			individuos[i] = new double[tamano[i]];
	}
	
	
	populationreal (double raninf, double ransup, double mut, double cruce, int eli, int n, int tama){
		prob_mutacion = mut;
		prob_cruce = cruce;
		elitismo = eli;
		n_individuos = n;
		rango_i = raninf;
		rango_s = ransup;
		tamano = new int[n_individuos];
		for (int i=0; i<n_individuos; i++)
			tamano[i] = tama;

		modificado = new boolean[n_individuos];
		for (int i=0; i<n_individuos; i++)
			modificado[i] = true;

		individuos = new double[n_individuos][];
		for (int i=0; i<n_individuos; i++)
			individuos[i] = new double[tamano[i]];
	}
	
	
	populationreal (populationreal x){
		prob_mutacion = x.prob_mutacion;
		prob_cruce = x.prob_cruce;
		elitismo = x. elitismo;
		n_individuos = x.n_individuos;
		rango_i = x.rango_i;
		rango_s = x.rango_s;
		
		tamano = new int[n_individuos];
		for (int i=0; i<n_individuos; i++)
			tamano[i] = x.tamano[i];


		modificado = new boolean[n_individuos];
		for (int i=0; i<n_individuos; i++)
			modificado[i] = x.modificado[i];


		individuos = new double[n_individuos][];
		for (int i=0; i<n_individuos; i++) {
			if (tamano[i]>0){
				individuos[i] = new double[tamano[i]];
				for (int j=0; j<tamano[i]; j++)
					individuos[i][j] = x.individuos[i][j];
			}
		}
	}
	
	
	
	public Object clone(){
		populationreal obj = null;
		try{
			obj = (populationreal) super.clone();
		}catch (CloneNotSupportedException ex){
			System.out.println ("\nNo se puede duplicar el objeto.\n");
		}
		obj.tamano = (int[]) obj.tamano.clone();
		obj.modificado = (boolean[]) obj.modificado.clone();
		
		obj.individuos = (double[][]) obj.individuos.clone();
		for (int i=0; i<obj.individuos.length; i++){
			obj.individuos[i] = (double[]) obj.individuos[i].clone();
		}
		
		return obj;
	}
	
	
	
	public double[] Individual (int i, Int_t tama){
		tama.value = tamano[i];
		return individuos[i];
	}
	
	
	public boolean Modified (int i){
		return modificado[i];
	}

	public void Pass (int i, populationreal x, int j){

		tamano[i] = x.tamano[i];
		individuos[i] = new double[tamano[i]];
		for (int k=0; k<tamano[i]; k++)
			individuos[i][k] = x.individuos[j][k];
	}


	public void Swap (int i, int j){
		Swap_int (tamano, i, j);
		Swap_bool (modificado, i, j);
		double[] p = individuos[i];
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
	
	
	public double[] Code (int i, int[] vector, int pos){
		vector[pos] = tamano[i];
		double[] v = new double[vector[pos]];
		for (int j=0; j<vector[pos]; j++)
			v[j] = individuos[i][j];
		
		return v;
	}


	public void Paint (int i){
		for (int j=0; j<tamano[i]; j++)
			System.out.println (individuos[i][j]+" ");
		System.out.println ("\n");
	}


	public void PaintBin (int i){
		for (int j=0; j<tamano[i]-1; j++)
			if (individuos[i][j]<individuos[i][tamano[i]-1])
				System.out.println ("0");
			else
				System.out.println ("1");
		System.out.println ("\n");
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
	
	
	public void PaintBinInFile (int i) throws IOException{
		FileOutputStream f;
		String cadena;
		
		try {
			f = new FileOutputStream("slave.log");
		} catch(FileNotFoundException e) {
			System.out.println("No se pudo crear.\n");
			return;
		}
		
		for (int j=0; j<tamano[i]-1; j++){
			if (individuos[i][j] < individuos[i][tamano[i]-1]){
				cadena = "0";
				byte[] buf = cadena.getBytes();
				f.write(buf);
			}
			else{
				cadena = "1";
				byte[] buf = cadena.getBytes();
				f.write(buf);
			}
		}
		cadena = "\n";
		byte[] buf = cadena.getBytes();
		f.write(buf);
		f.close();
	}
	
	
	public void PutValue (int indiv, int bit, double value){
		individuos[indiv][bit] = value;
	}
	
	
	public void RandomInitialPopulation (int n_item){
		for (int i=0; i<n_individuos; i++) 
			individuos[i][tamano[i]-1] = n_item;
	}
		
	
	
	
	
	
	
	
	
}
