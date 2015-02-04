
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierNSLV;

import java.io.*;
import java.util.Random;
import org.core.*;


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
	

	/**
	 * <p>
	 * Sorts the individuals according to their fitness values
	 * </p>
	 * @param n_clases int The number of classes
	 * @param examples_per_class int[] Vector containing the number of examples per class
	 */
	
	public void Sort (int n_clases, int[] n_examples_per_class){
		//int k;
		int[][] vector = new int[n_clases][n_individuos];
		vector[0] = new int[n_clases*n_individuos];
		
		double[] maximo1 = new double[n_clases];
		double[] minimo1 = new double[n_clases];
		int numero_ejemplos;
		
		numero_ejemplos = 0;
		for (int i=0; i<n_clases; i++){
			vector[i][0] = 0;
			numero_ejemplos += n_examples_per_class[i];
		}
		
		boolean cambio = true;
		for (int i=0; i<n_individuos-1 && cambio; i++){
			cambio = false;
			for (int j=n_individuos-1; j>i; j--){
				int k = 0;
				while ((k<n_valoracion) && (valoracion[j][k]==valoracion[j-1][k]))
					k++;
				if ((k<n_valoracion) && (valoracion[j][k]>valoracion[j-1][k])){
					Swap (j, j-1);
					cambio = true;
				}
			}
		}
		
		
		double[] MediaPobClase = new double[n_clases];
		
		for (int k=0; k<n_clases; k++){
			MediaPobClase[k] = 0;
			maximo1[k] = 0;
			minimo1[k] = 0;
		}
		
		for (int i=0; i<n_individuos-2; i++){
			int k = ClassIndividual(i);
			double d;
			if (vector[k][0] == 0){
				maximo1[k] = valoracion[i][0];
				MediaPobClase[k] = valoracion[i][0];
			}
			else{
				minimo1[k] = valoracion[i][0];
				MediaPobClase[k] += valoracion[i][0];
			}
			
			vector[k][vector[k][0]+1] = i;
			vector[k][0]++;
		}
		
		
		int[] PobMinClase = new int[n_clases];
		for (int k=0; k<n_clases; k++){
			PobMinClase[k] = n_individuos/(n_clases)-1;
		}
		
		
		for (int k=0; k<n_clases; k++){
			if (vector[k][0] > PobMinClase[k] && Math.abs (maximo1[k]-(1.0*MediaPobClase[k]/vector[k][0])) < 0.05){
				for (int t=PobMinClase[k]; t<=vector[k][0]; t++){
					valoracion[vector[k][t]][0] = -999999999.0;
					Pb[0].Rotation (vector[k][t]);
					Pr[0].Rotation (vector[k][t]);
				}
			}
		}
		
		
		cambio = true;
		for (int i=0; i<n_individuos-1 && cambio; i++){
			cambio = false;
			for (int j=n_individuos-1; j>i; j--){
				int k = 0;
				while ((k<n_valoracion) && (valoracion[j][k]==valoracion[j-1][k]))
					k++;
				if ((k<n_valoracion) && (valoracion[j][k]>valoracion[j-1][k])){
					Swap (j, j-1);
					cambio = true;
				}
			}
		}
		
		
		for (int i=n_individuos-2; i<n_individuos; i++){
			int k = ClassIndividual (i);
			if (vector[k][0] < PobMinClase[k]){
				int j = n_individuos-3;
				while (vector[ClassIndividual(j)][0] < PobMinClase[ClassIndividual(j)])
					j--;
				
			    vector[ClassIndividual(i)][vector[ClassIndividual(i)][0]+1] = vector[ClassIndividual(j)][vector[ClassIndividual(j)][0]+1];
			    vector[ClassIndividual(i)][0]++;
			    vector[ClassIndividual(j)][0]--;
			    Swap(i,j);				
			}
		}
		
	}
	
	public void Better (int nclases, int[] better, Int_t nbetter, int[][] ind_clase){
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
	
	
	
	/**
	 * <p>
	 * Sorts the individuals according to their fitness values
	 * </p>
	 * @param E example_set The set of examples
	 * @param n_clases int The number of classes
	 * @param n_examples_per_class int[] Vector containing the number of examples per class
	 * @param ind_clase int[][] Number of individuals per class
	 * @param adap_reglas double[] double[] Vector with the adaptation of rules
	 * @param peso_reglas double[] Vector with the weight of rules
	 * @param x_examples int Number of examples
	 * @return double Number of successes
	 */
	
	public double Sort_4L (example_set E, int n_clases, int[] n_examples_per_class, int[][] ind_clase, double[] adap_reglas, double[] peso_reglas, int n_examples){
		int k;
		int[] cl;
		int[] don;
		int ndo, nadapta;
		Int_t ncl = new Int_t (0);
		boolean ordenar;
		double acierto = 0, acierto_old = 0, fallo = 0, fallo_old = 0;


		int numero_ejemplos;

		// Sort the population

		boolean cambio = true;
		
		for (int i=0; i<n_individuos-1 && cambio; i++){
			cambio = false;
			for (int j=n_individuos-1; j>i; j--){
				k = 0;
				while (k<3 && valoracion[j][k]==valoracion[j-1][k])
					k++;
				
				if (k<3 && valoracion[j][k]>valoracion[j-1][k]){
					Swap (j, j-1);
					cambio = true;
				}
			}
		} 


		// Calculates the total number of examples
		
		numero_ejemplos = 0;
		
		for (int i=0; i<n_clases; i++){
			ind_clase[i][0] = 0;
			numero_ejemplos += n_examples_per_class[i];
		}



		// Re-calculates the subpopulations after the last sorting

		for (int i=0; i<n_clases; i++)
			ind_clase[i][0] = 0;

		for (int i=0; i<n_individuos; i++){
			cl = Subpopulation_Integer (0, i, ncl);
			ind_clase[cl[0]][0]++;
			ind_clase[cl[0]][ind_clase[cl[0]][0]] = i;
		}



		int[] PobMinClase = new int[n_clases];
		
		for (int r=0;r<n_clases;r++){
			if (n_examples_per_class[r] == 0)
				PobMinClase[r] = 0;
			else{
				if (n_examples_per_class[r]/(1.0*numero_ejemplos)<0.05)
					PobMinClase[r] = n_individuos/(2*n_clases);
		
				else{
					if (ind_clase[r][0]>0 && Math.abs (valoracion[ind_clase[r][1]][0]-valoracion[ind_clase[r][ind_clase[r][0]]][0] )<0.1)
						PobMinClase[r] = n_individuos/(2*n_clases);
					else
						PobMinClase[r] = n_individuos/(1+n_clases);
				}	
			}	
		}


		/* Fixing the individuals that will be replaced */

		for (int i=n_individuos-2; i<n_individuos; i++){
			k = ClassIndividual(i);
			if (ind_clase[k][0]<PobMinClase[k] ){
				int j = n_individuos-3;
				while (ind_clase[ClassIndividual(j)][0]<PobMinClase[ClassIndividual(j)])
					j--;

				ind_clase[ClassIndividual(i)][ind_clase[ClassIndividual(i)][0]+1] = ind_clase[ClassIndividual(j)][ind_clase[ClassIndividual(j)][0]+1];
				ind_clase[ClassIndividual(i)][0]++;
				ind_clase[ClassIndividual(j)][0]--;
				Swap(i,j);
			}
		}

		return acierto_old;
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
	
	public double MeanFitness_Stationary (){
		double media = 0;
		
		for (int i=0; i<n_individuos-2; i++){
			media += valoracion[i][0];
		}
		return media/(n_individuos-2);
	}
	
	public double MeanFitness_Stationary (Double_t min_f0, Double_t max_f0, Double_t min_f1, Double_t max_f1, Double_t min_f2, Double_t max_f2){
		  double media = 0;
		  
		  min_f0.value = valoracion[0][0];
		  max_f0.value = valoracion[0][0];
		  min_f1.value = valoracion[0][1];
		  max_f1.value = valoracion[0][1];
		  min_f2.value = valoracion[0][2];
		  max_f2.value = valoracion[0][2];

		  for (int i=0; i<n_individuos-2; i++){
		    media += valoracion[i][0];

		    if (valoracion[i][0] > max_f0.value)
		    	max_f0.value = valoracion[i][0];
		    else{
		    	if (valoracion[i][0] < min_f0.value)		    		
		    		min_f0.value = valoracion[i][0];
		    }	

		    if (valoracion[i][1] > max_f1.value)
		    	max_f1.value = valoracion[i][1];
		    else{
		    	if (valoracion[i][1] < min_f1.value)
		    		min_f1.value = valoracion[i][1];
		    }	

		    if (valoracion[i][2] > max_f2.value)
		    	max_f2.value = valoracion[i][2];
		    else{
		    	if (valoracion[i][2] < min_f2.value)
		    		min_f2.value = valoracion[i][2];
		    }	
		  }
		  return media/(n_individuos-2);		
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
	 * @param sujetos int[][] The individuals found according to the examples
	 * @param tama int Number of antecedent variables
	 * @param tama_dom int[] Information related to the domain of each variable
	 */
	
	public void InitialPopulation_4L (double[][] I, int rango, int n_items, int[][] sujetos, int tama, int[] tama_dom){
		//Value level
		Pb[0].InitialPopulation (sujetos, tama, tama_dom);

		// Consequent
		Pe[0].RandomInitialPopulation();
		
		Pe[1].RandomInitialPopulation (0);
		
		// Variable level
		Pr[0].RandomInitialPopulation (I, rango);
		
		Pr[1].RandomInitialPopulation (n_items);
		
		for (int j=0; j<n_individuos; j++)
			modificado[j] = true;
		
		for (int j=0; j<n_individuos; j++){
			for (int k=0; k<n_valoracion; k++)
				valoracion [j][k] = 0;
		}
	}
	

	

	/**
	 * <p>
	 * Stationary uniform mutation operator
	 * </p>
	 */
	
	public void UniformMutation_Stationary (){
		
		Pb[0].UniformMutation_Stationary ();
		
		Pe[0].UniformMutation_Stationary();

		Pr[0].UniformMutation_Stationary();
		
	}
	

	
	
	private int Select_Random_Individual (int n, int menos_este, int eli){
		int nd = n;
		//Random rand = new Random();
		int a =  Randomize.Randint (0, nd);

		while (a==menos_este || a<eli)
			a =  Randomize.Randint(0, nd);

		return a;
	}
	
	
	private boolean Probability (double x){
		//Random rand = new Random();
		double a = Randomize.Rand();
		
		return (a <= x);
	}
	
	
	public void Pasar (int i, int j){
	    for (int k=0; k<poblacionesB; k++){
	        Pb[k].Pass(i,this.Pb[k],j);
	     }

	     for (int k=0; k<poblacionesE; k++)
	        Pe[k].Pass(i,this.Pe[k],j);

	     for (int k=0; k<poblacionesR; k++)
	        Pr[k].Pass (i,this.Pr[k],j);


	}
	
	
	
	/**
	 * <p>
	 * Stationary crossover operator
	 * </p>
	 */
	
	public void CruceBasedLogical_Estacionario (double it){
		int a, b;
		
		if (Probability(1-it)){
			    a=Select_Random_Individual(n_individuos-2,-1,0);
			    b=Select_Random_Individual(n_individuos-2,a,0);
		}
		else {
			    a=0;
			    b=Select_Random_Individual(n_individuos-2,a,0);
		}
		
		Pasar(n_individuos-2,a);
		Pasar(n_individuos-1,b);

		if (Probability (Parameters.prob_cross)){
			Pb[0].TwoPointsCrossover_Stationary (a,b);

		    Pe[0].TwoPointsCrossover_Stationary (a,b);

		    Pr[0].TwoPointsCrossover_Stationary (a,b);
		}
		else{
			if (ClassIndividual(a) == ClassIndividual(b)){
				Pb[0].AND_OR_Stationary (a,b);
				Pe[0].TwoPointsCrossover_Stationary (a,b);
				Pr[0].AND_OR_Stationary (a,b);
			}
			else{
				Pb[0].NAND_NOR_Stationary (a,b);
				Pe[0].TwoPointsCrossover_Stationary (a,b);
				Pr[0].NAND_NOR_Stationary (a,b);
			}
		}
		modificado[n_individuos-2] = true;
		modificado[n_individuos-1] = true;
	}
	

	
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
