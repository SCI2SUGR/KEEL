
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierSLAVE2;

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
	
	public void Put_NotModified (){
		for (int i=0; i<n_individuos; i++)
			modificado[i] = false;
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
	
	
	public void RandomInitialPopulation (){
		  
		for (int i=0; i<n_individuos; i++) {
			for (int j=0; j<tamano[i]-1; j++){
				individuos[i][j] = 0.8;
			}
			individuos[i][tamano[i]-1] = 0.5;
		}
	}

	public void RandomInitialPopulation (int n_item){
		for (int i=0; i<n_individuos; i++) 
			individuos[i][tamano[i]-1] = n_item;
	}
	
	

	
	public void InitialPopulationClassFixed (double[][] I, int clase){
		//Random rand = new Random();
		double max = I[0][0];
		double min = I[0][0];
		
		for (int j=0; j<tamano[0]-1; j++){
			if (I[j][clase+1]>max)
				max=I[j][clase+1];
			else{
				if (I[j][clase+1]<min)
					min=I[j][clase+1];
			}	
		}

		for (int i=0; i<n_individuos; i++) {
			for (int j=0; j<tamano[i]-1; j++)
				if (min == max)
					individuos[i][j] =  Randomize.Rand ();
				else
					individuos[i][j] = I[j][clase+1];
			
			individuos[i][tamano[i]-1] = (max-min) *  Randomize.Rand ();
		}
	}
	


	/**
	 * <p>
	 * Uniform mutation operator
	 * </p>
	 */

	public void UniformMutation (){
		double aux;
		//Random rand = new Random();
		
		for (int i=elitismo; i<n_individuos; i++){
			for (int j=0; j<tamano[i]; j++){
				if (Probability (prob_mutacion)){
					do{
						aux = (1.0*(rango_s-rango_i)* Randomize.Rand ());
					}while (aux == individuos[i][j]);
					individuos[i][j] = aux;
					modificado[i] = true;
				}
			}
		}	
	}
	
	
	private boolean Probability (double x){
		//Random rand = new Random();
		double a =  Randomize.Rand ();
		
		return (a <= x);
	}
	
	

	
	
	private int Select_Random_Individual (int n, int menos_este, int eli){
		int nd = n;
		//Random rand = new Random();
		int a = Randomize.Randint (0, nd);

		while (a==menos_este || a<eli)
			a = Randomize.Randint (0, nd);

		return a;
	}
	
	
	private int CrossPoint (int n){
		//Random rand = new Random();
		
		return Randomize.Randint (0, n);
	}
	
	
	/**
	 * <p>
	 * Two-points crossover operator
	 * </p>
	 */
	
	public void TwoPointsCrossover (){
		int a;
		Int_t p1 = new Int_t (0);
		Int_t p2 = new Int_t (0);
		double aux;
		
		for (int i=elitismo; i<n_individuos; i++){
			if (Probability (prob_cruce)){
				a = Select_Random_Individual (n_individuos, i, elitismo);
				CrossPoint2 (tamano[i], p1, p2);
				modificado[i] = true;
				modificado[a] = true;
				for (int j=p1.value; j<p2.value; j++){
					aux = individuos[i][j];
					individuos[i][j] = individuos[a][j];
					individuos[a][j] = aux;
				}
			}
		}	
	}
	
	
	private void CrossPoint2 (int n, Int_t a, Int_t b){
		//Random rand = new Random();
		int nd = n;
		
		a.value = Randomize.Randint (0, nd);
		b.value = Randomize.Randint (0, nd);
		if (a.value > b.value){
			int aux = a.value;
			a.value = b.value;
			b.value = aux;
		}
	}
	

	
	
	
	
	
	
	
	
}
