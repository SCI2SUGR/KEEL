
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierSLAVEv0;
import java.io.*;
import java.util.Random;

import org.core.Randomize;


public class multipopulation implements Cloneable {
	
	/**	
	 * <p>
	 * It contains the methods for handling the different populations of the individuals
	 * and information regarding those
	 * </p>
	 */

	int elitismo;
	int n_individuos;
	boolean[] modificado;
	int n_valoracion;
	double[][] valoracion;
	int poblacionesB;
	int poblacionesE;
	int poblacionesR;
	populationbinary[] Pb;
	populationinteger[] Pe;
	populationreal[] Pr;
	
	multipopulation (){
		elitismo = 0;
		poblacionesB = 0;
		poblacionesE = 0;
		n_individuos = 0;
		modificado = null;
		n_valoracion = 0;
		valoracion = null;
		Pb = null;
		Pe = null;
	}
	
	multipopulation (int pbin, int pent, int preal, int[] rangoe, double[] rangori, double[] rangors, double[] mut, double[] cruce, int eli, int n, int[] tama, int n_val){
		elitismo = eli;
		n_individuos = n;
		poblacionesB = pbin;
		poblacionesE = pent;
		poblacionesR = preal;
		
		if (pbin > 0){
			Pb = new populationbinary[pbin];
			for (int j=0; j<pbin; j++)
				Pb[j] = new populationbinary (mut[j], cruce[j], eli, n, tama[j]);
		}
		
		if (pent > 0){
			Pe = new populationinteger[pent];
			for (int j=0; j<pent; j++)
				Pe[j] = new populationinteger (rangoe[j], mut[j+pbin], cruce[j+pbin], eli, n, tama[j+pbin]);
		}
		
		if (preal > 0){
			Pr = new populationreal[preal];
			for (int j=0; j<preal; j++)
				Pr[j] = new populationreal (rangori[j], rangors[j], mut[j+pbin+pent], cruce[j+pbin+pent], eli, n, tama[j+pbin+pent]);
		}
		
		modificado = new boolean[n_individuos];
		for (int i=0; i<n_individuos; i++)
			modificado[i] = true;
		
		n_valoracion = n_val;
		valoracion = new double[n_individuos][];
		for (int i=0; i<n_individuos; i++){
			valoracion[i] = new double[n_valoracion];
			for (int j=0; j<n_valoracion; j++)
				valoracion[i][j] = 0;
		}
	}
	
	multipopulation (multipopulation x){
		elitismo = x.elitismo;
		n_individuos = x.n_individuos;
		
		modificado = new boolean[n_individuos];
		for (int i=0; i<n_individuos; i++)
			modificado[i] = x.modificado[i];
		
		n_valoracion = x.n_valoracion;
		valoracion = new double[n_individuos][];
		for (int i=0; i<n_individuos; i++){
			valoracion[i] = new double[n_valoracion];
			for (int j=0; j<n_valoracion; j++)
				valoracion[i][j] = x.valoracion[i][j];
		}
		
		poblacionesB = x.poblacionesB;
		poblacionesE = x.poblacionesE;
		poblacionesR = x.poblacionesR;
		
		if (poblacionesB > 0){
			Pb = new populationbinary[poblacionesB];
			for (int j=0; j<poblacionesB; j++){
				Pb[j] = new populationbinary ();
				Pb[j] = x.Pb[j];
			}
		}
		
		if (poblacionesE > 0){
			Pe = new populationinteger[poblacionesE];
			for (int j=0; j<poblacionesE; j++){
				Pe[j] = new populationinteger ();
				Pe[j] = x.Pe[j];
			}
		}
		
		if (poblacionesR > 0){
			Pr = new populationreal[poblacionesR];
			for (int j=0; j<poblacionesR; j++){
				Pr[j] = new populationreal ();
				Pr[j] = x.Pr[j];
			}
		}
	}
	
	
	
	public Object clone(){
		multipopulation obj = null;
		try{
			obj = (multipopulation) super.clone();
		}catch (CloneNotSupportedException ex){
			System.out.println ("\nNo se puede duplicar el objeto.\n");
		}
		obj.modificado = (boolean[]) obj.modificado.clone();
		
		obj.valoracion = (double[][]) obj.valoracion.clone();
		for (int i=0; i<obj.valoracion.length; i++){
			obj.valoracion[i] = (double[]) obj.valoracion[i].clone();
		}
		
		obj.Pb = (populationbinary[]) obj.Pb.clone();
		for (int i=0; i<obj.Pb.length; i++){
			obj.Pb[i] = (populationbinary) obj.Pb[i].clone();
		}
		
		obj.Pe = (populationinteger[]) this.Pe.clone();
		for (int i=0; i<obj.Pe.length; i++){
			obj.Pe[i] = (populationinteger) obj.Pe[i].clone();
		}
		
		obj.Pr = (populationreal[]) this.Pr.clone();
		for (int i=0; i<obj.Pr.length; i++){
			obj.Pr[i] = (populationreal) obj.Pr[i].clone();
		}
		
		return obj;
	}
	
	
	
	public void Swap (int i, int j){
		for (int k=0; k<poblacionesB; k++)
			Pb[k].Swap (i, j);
		
		for (int k=0; k<poblacionesE; k++)
			Pe[k].Swap (i, j);
		
		for (int k=0; k<poblacionesR; k++)
			Pr[k].Swap (i, j);
		
		Swap_bool (modificado, i, j);
		
		double[] p = valoracion[i];
		valoracion[i] = valoracion[j];
		valoracion[j] = p;
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
	
	
	
	/**
	 * <p>
	 * Sorts the individuals according to their fitness values
	 * </p>
	 */
	
	public void Sort (){
		int k;
		
		for (int i=0; i<n_individuos-1; i++){
			for (int j=n_individuos-1; j>i; j--){
				k = 0;
				while ((k<n_valoracion) && (valoracion[j][k]==valoracion[j-1][k]))
					k++;
				if ((k<n_valoracion) && (valoracion[j][k]>valoracion[j-1][k]))
					Swap (j, j-1);
			}
		}
	}
	
	public int ClassIndividual (int ind){
		return Pe[0].GetValue (ind, 0);
	}
	
	public int ValueIndividual (int population, int ind){
		return Pe[population].GetValue (ind, 0);
	}
	
	
	/**
	 * <p>
	 * Returns the binary subpopulation of the individual "individuo"
	 * </p>
	 * @param subpoblacion int The selected subpopulation
	 * @param individuo int The selected individual
	 * @param tama Int_t Size of the subpopulation
	 * @return char[] The created structure
	 */
	
	public char[] Subpopulation_Binary (int subpoblacion, int individuo, Int_t tama){
		char[] puntero;
		
		puntero = Pb[subpoblacion].Individual (individuo, tama);
		return puntero;
	}
	
	
	/**
	 * <p>
	 * Returns the integer subpopulation of the individual "individuo"
	 * </p>
	 * @param subpoblacion int The selected subpopulation
	 * @param individuo int The selected individual
	 * @param tama Int_t Size of the subpopulation
	 * @return int[] The created structure
	 */
	
	public int[] Subpopulation_Integer (int subpoblacion, int individuo, Int_t tama){
		int[] puntero;
		
		puntero = Pe[subpoblacion].Individual (individuo, tama);
		return puntero;
	}
	
	
	
	/**
	 * <p>
	 * Returns the real subpopulation of the individual "individuo"
	 * </p>
	 * @param subpoblacion int The selected subpopulation
	 * @param individuo int The selected individual
	 * @param tama Int_t Size of the subpopulation
	 * @return double[] The created structure
	 */
	
	public double[] Subpopulation_Real (int subpoblacion, int individuo, Int_t tama){
		double[] puntero;
		
		puntero = Pr[subpoblacion].Individual (individuo, tama);
		return puntero;
	}
	

	
	public void Better (int nclases, int[] better, Int_t nbetter){
		nbetter.value = 1;
		better[0] = 0;
	}
	
	
	public boolean Higher (double[] v1, double[] v2, int n){
		int k = 0;
		
		while ((k<n) && (v1[k]==v2[k]))
			k++;
		
		if (k == n)
			return false;
		else
			return (v1[k] > v2[k]);
	}
	

	
	public void Paint (int i){
		for (int j=0; j<poblacionesB; j++)
			Pb[j].Paint (i);
		
		for (int j=0; j<poblacionesE; j++)
			Pe[j].Paint (i);
		
		for (int j=0; j<poblacionesR; j++)
			Pr[j].Paint (i);
	}
	
	public void Paint (){
		for (int i=0; i<n_individuos; i++){
			Paint (i);
			PaintFitness (i);
		}
	}
	

	
	public double MeanFitness (){
		double media = 0;
		
		for (int i=0; i<n_individuos; i++){
			media += valoracion[i][0];
		}
		return media/n_individuos;
	}
	
	
	public void PaintFitness (int i){
		System.out.println ("Fitness: ");
		
		for (int j=0; j<n_valoracion; j++)
			System.out.println (valoracion[i][j]+" ");
		System.out.println ("["+MeanFitness()+"]\n");
	}
	
	public void PaintFitnessInFile (int i) throws IOException{
		OutputStream f;
		String cadena;
		
		try {
			f = new FileOutputStream("slave.log");
		} catch(FileNotFoundException e) {
			System.out.println("No se pudo crear.\n");
			return;
		}
		
		cadena = "Fitness: ";
		byte[] buf = cadena.getBytes();
		f.write(buf);
		
		for (int j=0; j<n_valoracion; j++){
			cadena = valoracion[i][j]+" ";
			buf = cadena.getBytes();
			f.write(buf);
		}
		cadena = "["+MeanFitness()+"]\n";	
		buf = cadena.getBytes();
		f.write(buf);
		f.close ();
	}
	
	public void PaintFitness_Stationary (int i){
		System.out.println ("Fitness: ");
		for (int j=0; j<n_valoracion; j++)
			System.out.println (valoracion[i][j]+"   ");
		System.out.println ("Clase => "+ClassIndividual (i)+"\n");
	}
	
	public void PaintIndividual (int i){
		 for (int j=0; j<poblacionesR; j++)
			 Pr[j].PaintBin (i);
		 for (int j=0; j<poblacionesE; j++)
			 Pe[j].Paint (i);
		 for (int j=0; j<poblacionesB; j++)
			 Pb[j].Paint (i);
		 System.out.println ("Fitnes: ");
		 for (int j=0; j<n_valoracion; j++)
			 System.out.println (valoracion[i][j]+" ");
		 System.out.println ("\n\n");
	}
	
	public int N_individuals (){
		return n_individuos;
	}
	
	public int Elite (){
		return elitismo;
	}
	
	public int N_Val (){
		return n_valoracion;
	}
	
	
	/**
	 * <p>
	 * Returns in "code" the genetic code of the individual "i"
	 * </p>
	 * @param i int The selected individual
	 * @param code genetcode The structure storing the genetic code
	 */
	
	public void Code (int i, genetcode code){
		
		// Stores in "code" the binary part
		char[][] bin = new char[poblacionesB][];
		for  (int j=0; j<poblacionesB; j++)
			bin[j] = null;
		
		int[] tb = new int[poblacionesB];
		for (int j=0; j<poblacionesB; j++)
			bin[j] = Pb[j].Code (i, tb, j);
		
		code.PutBinary (poblacionesB, tb, bin);
		
		// Stores in "code" the integer part
		int[][] ent = new int[poblacionesE][];
		for  (int j=0; j<poblacionesE; j++)
			ent[j] = null;
		
		int[] te = new int[poblacionesE];
		for (int j=0; j<poblacionesE; j++)
			ent[j] = Pe[j].Code (i, te, j);
		
		code.PutInteger (poblacionesE, te, ent);
		
		// Stores in "code" the real part
		double[][] rea = new double[poblacionesR][];
		for  (int j=0; j<poblacionesR; j++)
			rea[j] = null;
		
		int[] tr = new int[poblacionesR];
		for (int j=0; j<poblacionesR; j++)
			rea[j] = Pr[j].Code (i, tr, j);
		
		code.PutReal (poblacionesR, tr, rea);		
	}
	
	public void PutCode (int i, genetcode code){
		
		for (int j=0; j<poblacionesB; j++){
			for (int k=0; k<code.SizeBinary (j); k++)
				Pb[j].PutValue (i, k, code.GetValueBinary (j, k));
		}
		
		for (int j=0; j<poblacionesE; j++){
			for (int k=0; k<code.SizeInteger (j); k++)
				Pe[j].PutValue (i, k, code.GetValueInteger (j, k));
		}
		
		for (int j=0; j<poblacionesR; j++){
			for (int k=0; k<code.SizeReal (j); k++)
				Pr[j].PutValue (i, k, code.GetValueReal (j, k));
		}		
	}
	
	public boolean Modified (int i){
		return modificado[i];
	}
	
	public void PutModified (int i){
		modificado[i] = true;
	}
	
	public void Assessment (int i, double[] valor){
		for (int j=0; j<n_valoracion; j++)
			valoracion[i][j] = valor[j];
		modificado[i] = false;
	}
	
	
	
	/**
	 * <p>
	 * Generates an initial population
	 * </p>
	 * @param I double[][] Information measures
	 * @param rango int Number of classes
	 * @param n_items int Number of examples
	 * @param consecuente int The selected class
	 * @param sujetos int[][] The individuals found according to the examples
	 * @param n_var int Number of antecedent variables
	 * @param n_etiquetas_variable int[] Information related to the domain of each variable
	 */
	
	public void InitialPopulation (double[][] I, int rango, int n_items, int consecuente, int[][] sujetos, int n_var, int[] n_etiquetas_variable){
		// Value level
		Pb[0].InitialPopulation (sujetos, n_var, n_etiquetas_variable);
		
		Pr[1].RandomInitialPopulation (n_items);
		
		for (int j=0; j<n_individuos; j++)
			modificado[j] = true;
		
		for (int j=0; j<n_individuos; j++){
			for (int k=0; k<n_valoracion; k++)
				valoracion [j][k] = 0;
		}
	}
	

	
	public void Put_NotModified (){
		for (int i=0; i<n_individuos; i++)
			modificado[i] = false;
		
		for (int j=0; j<poblacionesB; j++)
			Pb[j].Put_NotModified ();
	}
	
	
	
	/**
	 * <p>
	 * Uniform mutation operator
	 * </p>
	 */
	
	public void UniformMutation (){
		for (int j=0; j<poblacionesB; j++){
			Pb[j].UniformMutation ();
			for (int i=elitismo; i<n_individuos; i++){
				if (Pb[j].Modified (i))
					modificado[i] = true;
			}
		}
	}
	


	/**
	 * <p>
	 * Generational crossover operator
	 * </p>
	 */
	
	public void GenerationalCrossover (){
		Pb[0].GenerationalCrossover ();
		for (int i=0; i<n_individuos; i++){
			if (Pb[0].Modified (i))
				modificado[i] = true;
		}
	}
	
	
	
	private int Select_Random_Individual (int n, int menos_este, int eli){
		int nd = n;
		//Random rand = new Random(1);
		int a = Randomize.Randint (0, nd);

		while (a==menos_este || a<eli)
			a = Randomize.Randint (0, nd);

		return a;
	}
	
	
	private boolean Probability (double x){
		//Random rand = new Random(1);
		double a = Randomize.Rand();
		
		return (a <= x);
	}
	
	

	/**
	 * <p>
	 * Selection operator
	 * </p>
	 */
	
	public void Selection (){
		multipopulation Paux = (multipopulation) this.clone();
		double a;
		int j = 0;
		//Random rand = new Random(1);
		
		for (int i=elitismo; i<n_individuos; i++){
			a = (Randomize.Rand() % 10) + 1;
			while (a > 0){
				a = a - (1.0/(j+1))*0.5;
				j++;
				if  (j == n_individuos)
					j = 0;
			}
			
			for (int k=0; k<poblacionesB; k++){
				Pb[k].Pass (i, Paux. Pb[k], j);
			}

			for (int k=0; k<poblacionesE; k++){
				Pe[k].Pass (i, Paux. Pe[k], j);
			}	

			for (int k=0; k<poblacionesR; k++){
				Pr[k].Pass (i, Paux. Pr[k], j);
			}	

			for (int k=0; k<n_valoracion; k++){
				valoracion[i][k] = Paux.valoracion[j][k];

			}
			modificado[i] = Paux.modificado[j];
		}
	}
	
	
	// Returns the first component of the fitness function
	public double ValueFitness (int i){
		return valoracion[i][0];
	}
	
	public double ValueFitness (int i, int j){
		return valoracion[i][j];
	}
	

	
	
	private double Maximum (double a, double b){
		if (a > b)
			return a;
		else
			return b;
	}
	


}
