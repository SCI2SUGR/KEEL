
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierNSLV;

import java.io.*;
import java.util.Random;

import org.core.Randomize;



public class populationbinary implements Cloneable {
	
	
	/**	
	 * <p>
	 * It contains the methods for handling the binary population of individuals
	 * </p>
	 */

	double prob_mutacion;
	double prob_cruce;
	int elitismo;
	int n_individuos;
	int[] tamano;
	boolean[] modificado;
	double[] valoracion;
	char[][] individuos;
	
	populationbinary (){
		prob_mutacion = 0.0;
		prob_cruce = 0.0;
		elitismo = 0;
		n_individuos = 0;
		tamano = null;
		modificado = null;
		valoracion = null;
		individuos = null;
	}
	
	populationbinary (double mut, double cruce, int eli, int n){
		prob_mutacion = mut;
		prob_cruce = cruce;
		elitismo = eli;
		n_individuos = n;
		tamano = new int[n_individuos];
		
		for (int i=0; i<n_individuos; i++)
			tamano[i] = 0;

		modificado = new boolean[n_individuos];
		for (int i=0; i<n_individuos; i++)
			modificado[i] = true;

		valoracion = new double[n_individuos];

		individuos = new char[n_individuos][];
		for (int i=0; i<n_individuos; i++)
			individuos[i] = null;
	}

	populationbinary (double mut, double cruce, int eli, int n, int tama){
		prob_mutacion = mut;
		prob_cruce = cruce;
		elitismo = eli;
		n_individuos = n;
		tamano= new int[n_individuos];
		
		for (int i=0; i<n_individuos; i++)
			tamano[i] = tama;

		modificado = new boolean[n_individuos];
		for (int i=0; i<n_individuos; i++)
			modificado[i] = true;

		valoracion = new double[n_individuos];
		
		individuos = new char[n_individuos][];
		for (int i=0; i<n_individuos; i++)
			individuos[i] = new char[tamano[i]];
	}

	populationbinary (double mut, double cruce, int eli, int n, int[] tama){
		prob_mutacion = mut;
		prob_cruce = cruce;
		elitismo = eli;
		n_individuos = n;
		tamano= new int[n_individuos];
		
		for (int i=0; i<n_individuos; i++)
			tamano[i] = tama[i];

		modificado = new boolean[n_individuos];
		for (int i=0; i<n_individuos; i++)
			modificado[i] = true;

		valoracion = new double[n_individuos];
		
		individuos = new char[n_individuos][];
		for (int i=0; i<n_individuos; i++)
			individuos[i] = new char[tamano[i]];
	}
	
	populationbinary (populationbinary x){
		prob_mutacion = x.prob_mutacion;
		prob_cruce = x.prob_cruce;
		elitismo = x. elitismo;
		n_individuos = x.n_individuos;
		
		tamano = new int[n_individuos];
		for (int i=0; i<n_individuos; i++)
			tamano[i] = x.tamano[i];


		modificado= new boolean[n_individuos];
		for (int i=0; i<n_individuos; i++)
			modificado[i] = x.modificado[i];

		valoracion= new double[n_individuos];
		for (int i=0; i<n_individuos; i++)
			valoracion[i] = x.valoracion[i];

		individuos = new char[n_individuos][];
		for (int i=0; i<n_individuos; i++) {
			if (tamano[i]>0){
				individuos[i] = new char[tamano[i]];
				for (int j=0; j<tamano[i]; j++)
					individuos[i][j] = x.individuos[i][j];
			}
		}
	}
	
	
	
	public Object clone(){
		populationbinary obj = null;
		try{
			obj = (populationbinary) super.clone();
		}catch (CloneNotSupportedException ex){
			System.out.println ("\nNo se puede duplicar el objeto.\n");
		}
		obj.tamano = (int[]) obj.tamano.clone();
		obj.modificado = (boolean[]) obj.modificado.clone();
		obj.valoracion = (double[]) obj.valoracion.clone();
		
		obj.individuos = (char[][]) obj.individuos.clone();
		for (int i=0; i<obj.individuos.length; i++){
			obj.individuos[i] = (char[]) obj.individuos[i].clone();
		}
		
		return obj;
	}
	
	
	
	public char[] Individual (int i, Int_t tama){
		tama.value = tamano[i];
		return individuos[i];
	}
	
	public boolean Modified (int i){
		return modificado[i];
	}

	
	
	
	public void Pass (int i, populationbinary x, int j){
		tamano[i] = x.tamano[j];
		individuos[i] = new char[tamano[i]];
		for (int k=0; k<tamano[i]; k++)
			individuos[i][k] = x.individuos[j][k];
	}
	
	public void Swap (int i, int j){
		Swap_int (tamano, i, j);
		Swap_bool (modificado, i, j);
		Swap_double (valoracion, i, j);
		char[] p = individuos[i];
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
	
	public char[] Code (int i, int[] vector, int pos){
		vector[pos] = tamano[i];
		char[] v = new char[vector[pos]];
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
			cadena = ""+individuos[i][j];
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
	
	public void PutValue (int indiv, int bit, char value){
		individuos[indiv][bit] = value;
	}
	
	private boolean Probability (double x){
		//Random rand = new Random();
		double a = Randomize.Rand();
		
		return (a <= x);
	}
	
	public void RandomInitialPopulation (){
		for (int i=0; i<n_individuos; i++){
			for (int j=0; j<tamano[i]; j++){
				if (Probability (0.6))
					individuos[i][j] = '1';
				else
					individuos[i][j] = '0';
			}
		}
	}
	
	public void InitialPopulation (int[][] sujeto, int tama, int[] tama_dom){
		int j;
		
		for (int i=0; i<n_individuos; i++){
			j=0;
			for (int k=0; k<tama; k++){
				if (sujeto[i][k] == -1){
					for (int l=0; l<tama_dom[k]; l++){
						if (Probability (0.9))
							individuos[i][j] = '1';
						else
							individuos[i][j] = '0';
						j++;
					}
				}
				else {
					for (int l=0; l<tama_dom[k]; l++)
						individuos[i][j+l]='0';
					individuos[i][j+sujeto[i][k]] = '1'; 
					j += tama_dom[k]; 
				}
			}
		}
	}
	
	public void InitialPopulationValue (char valor){
		for (int i=0; i<n_individuos; i++){
			for (int j=0; j<tamano[i]; j++)
				individuos[i][j] = valor;
		}	
	}
	
	

	/**
	 * <p>
	 * Stationary uniform mutation operator
	 * </p>
	 */
	
	public void UniformMutation_Stationary (){
		for (int i=n_individuos-2; i<n_individuos; i++){
			for (int j=0; j<tamano[i]; j++){
				if (Probability (prob_mutacion)){ 
					if (individuos[i][j] == '1')
						individuos[i][j] = '0';
					else
						individuos[i][j] = '1';
					modificado[i] = true;
				}
			}
		}
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
		
		a.value = Randomize.Randint(0, nd);
		b.value = Randomize.Randint(0, nd);
		if (a.value > b.value){
			int aux = a.value;
			a.value = b.value;
			b.value = aux;
		}
	}


	
	
	/**
	 * <p>
	 * Given the individual "i", a rotation operation is made.
	 * The resultant individual are placed stay in position i.
	 * in the positions "i" of the population 
	 * </p>
	 * @param i int An individual
	 */
	
	public void Rotation (int i){
		int p;
		char[] copia = new char[tamano[i]+1];

		for (int j=0; j<tamano[i]; j++)
			copia[j] = individuos[i][j];
		
		p = CrossPoint (tamano[i]);

		for (int j=0; j<tamano[i]; j++)
			individuos[i][j] = copia[(j+p)%tamano[i]];

	}
	
	



	private int ProbabilityOption (double prob_cru2ptos, double prob_or, double prob_and, double prob_rot, double prob_generalizacion){
		//Random rand = new Random();
		double a = (prob_cru2ptos+prob_or+prob_and+prob_rot+prob_generalizacion) * Randomize.Rand();
		
		if (a<=prob_cru2ptos && prob_cru2ptos!=0)
			return 0;
		a = a - prob_cru2ptos;

		if (a<=prob_or && prob_or!=0)
			return 1;
		 
		a = a - prob_or;

		if (a<=prob_and && prob_and!=0)
			return 2;

		a = a - prob_and;

		if (a<=prob_rot && prob_rot!=0)
			return 3;

		if (prob_generalizacion!=0)
			return 4;

		return 0;
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
		int i=n_individuos-2;
		

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
		int i=n_individuos-2;

		CrossPoint2 (tamano[i], p1, p2);
		modificado[i] = true;
		modificado[i+1] = true;
		for (int j=p1.value; j<p2.value; j++){
			if (individuos[indiv1][j] =='1' && individuos[indiv2][j] =='1')
				individuos[i][j] = '1';
			else
				individuos[i][j] = '0';

			if (individuos[indiv1][j] =='1' || individuos[indiv2][j] =='1')
				individuos[i+1][j] = '1';
			else
				individuos[i+1][j] = '0';
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
		int i = n_individuos-2;
		
		modificado[i] = true;
		modificado[i+1] = true;
		for (int j=0; j<tamano[i]; j++){
			if (individuos[indiv1][j] =='1' && individuos[indiv2][j] =='1')
				individuos[i][j] = '0';
			else
				individuos[i][j] = '1';

			if (individuos[indiv1][j] =='1' || individuos[indiv2][j] =='1')
				individuos[i+1][j] = '0';
			else
				individuos[i+1][j] = '1';
		}
	}
	
}
