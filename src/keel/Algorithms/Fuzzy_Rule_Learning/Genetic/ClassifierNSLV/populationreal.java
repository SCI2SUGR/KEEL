
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierNSLV;

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
		elitismo = x.elitismo;
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
	
	
	public void RandomInitialPopulation (double[][] I, int rango){
		//Random rand = new Random();
		double max = I[0][0];
		double min = I[0][0];

		for (int j=0; j<tamano[0]-1; j++){
			if (I[j][0] > max)
				max = I[j][0];
			else{
				if (I[j][0] < min)
					min = I[j][0];
			}	
		}

		for (int i=0; i<n_individuos; i++) {
			for (int j=0; j<tamano[i]-1; j++){
				if (min == max)
					individuos[i][j] =  Randomize.Rand();
				else
					individuos[i][j] = I[j][(i%rango)+1];
			}	
			individuos[i][tamano[i]-1] = (1.0*(max-min)*Randomize.Rand ())+min;
		}
	}
	
	

	
	



	public void InitialPopulationValue (double valor){
		if (valor<rango_i || valor >rango_s)
			valor = (rango_s-rango_i)/2.0;

		for (int i=0; i<n_individuos; i++){
			for (int j=0; j<tamano[i]; j++){
				individuos[i][j] = valor;
			}
		}	
	}



	
	
	private boolean Probability (double x){
		//Random rand = new Random();
		double a = Randomize.Rand ();
		
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
	

	/**
	 * <p>
	 * Stationary uniform mutation operator
	 * </p>
	 */
	
	public void UniformMutation_Stationary (){
		
		for (int i=n_individuos-2; i<n_individuos; i++){
			for (int k=0; k<tamano[i]; k++){
				if (Probability (prob_mutacion)){
					if (individuos[i][k]<=individuos[i][tamano[i]-1])
						individuos[i][k] = individuos[i][tamano[i]-1]+0.01;
					else 
						individuos[i][k] = individuos[i][tamano[i]-1]-0.01;

					if (individuos[i][k] > 1)
						individuos[i][k] = 1;
					else{ 
						if (individuos[i][k] < 0)
							individuos[i][k] = 1;
					}	
				}
			}
			modificado[i] = true;
		}
	}
	
	
	public void Rotation (int i){
		int p;
		double[] copia = new double[tamano[i]];

		// Hago una copia del comosoma
		for (int j=0; j<tamano[i]; j++)
			copia[j] = individuos[i][j];
		
		// Selecciono un punto en el cromosoma
		p = CrossPoint (tamano[i]);

		// Modifico el cromosoma con la rotacion
		for (int j=0; j<tamano[i]; j++)
			individuos[i][j] = copia[(j+p)%tamano[i]];

	}
	
	

	
	

	/**
	 * <p>
	 * Given the individuals "indiv1" and "indiv2", it selects two points
	 * exchanging their central zones.
	 * </p>
	 * @param indiv1 int An individual
	 * @param indiv2 int An individual
	 */
	
	public void TwoPointsCrossover_Stationary (int indiv1, int indiv2){
		Int_t p1 = new Int_t (0);
		Int_t p2 = new Int_t (0);		  
		int i = n_individuos-2;
		

		CrossPoint2 (tamano[i], p1, p2);
		modificado[i] = true;
		modificado[i+1] = true;
		for (int j=0; j<p1.value; j++){
			individuos[i][j] = individuos[indiv1][j];
			individuos[i+1][j] = individuos[indiv2][j];
		}
		for (int j=p1.value; j<p2.value; j++){
			individuos[i][j] = individuos[indiv2][j];
			individuos[i+1][j] = individuos[indiv1][j];
		}
		for (int j=p2.value; j<tamano[i]; j++){
			individuos[i][j] = individuos[indiv1][j];
			individuos[i+1][j] = individuos[indiv2][j];
		}
		      
	}
	
	
	
	/**
	 * <p>
	 * Given the individuals "indiv1" and "indiv2", it selects two points
	 * and makes the AND/OR operation between their central zones. 
	 * </p>
	 * @param indiv1 int An individual
	 * @param indiv2 int An individual
	 */
	
	public void AND_OR_Stationary (int indiv1, int indiv2){
		Int_t p1 = new Int_t (0);
		Int_t p2 = new Int_t (0);
		int i = n_individuos-2;
		  
		CrossPoint2 (tamano[i]-1, p1, p2);
		modificado[i] = true;
		modificado[i+1] = true;
		for (int j=p1.value; j<=p2.value; j++){
			if (individuos[indiv1][j] < individuos[indiv2][j]){
				individuos[i][j] = individuos[indiv2][j];
				individuos[i+1][j] = individuos[indiv1][j];
			}
			else {
				individuos[i][j] = individuos[indiv1][j];
				individuos[i+1][j] = individuos[indiv2][j];
			}
		}
	}

	
	
	/**
	 * <p>
	 * Given the individuals "indiv1" and "indiv2", it selects two points
	 * and makes the NAND/NOR operation between their central zones. 
	 * </p>
	 * @param indiv1 int An individual
	 * @param indiv2 int An individual
	 */

	public void NAND_NOR_Stationary (int indiv1, int indiv2){
		Int_t p1 = new Int_t (0);
		Int_t p2 = new Int_t (0);
		int i = n_individuos-2;
		
		CrossPoint2 (tamano[i]-1, p1, p2);
		modificado[i] = true;
		modificado[i+1] = true;
		for (int j=p1.value; j<=p2.value; j++){
			if (individuos[indiv1][j] < individuos[indiv2][j]){
				individuos[i][j] = 1-individuos[indiv2][j];
				individuos[i+1][j] = 1-individuos[indiv1][j];
			}
			else {
				individuos[i][j] = 1-individuos[indiv1][j];
				individuos[i+1][j] = 1-individuos[indiv2][j];
			}
		}
	}
	
	
	
	
	
}
