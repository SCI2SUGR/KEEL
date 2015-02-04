
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierNSLV;

import java.io.*;
import java.util.Random;

import org.core.Randomize;

public class example_set {
	
	/**	
	 * <p>
	 * It contains the methods for handling the set of examples
	 * </p>
	 */

	static final double MISSING = -999999999;
	final double LAMBDA = 0.8;
	int n_example;
	int n_variable;
	double[][] data;
	boolean[] covered;
	double[] gcovered;
	int n_partition;
	int[] partition;
    double[] g_positive_covered;
    double[] g_negative_covered;
    double[] peso_positive;
    double[] peso_negative;
	
	example_set (){
		n_example = 0;
		n_variable = 0;
		data = null;
		covered = null;
		gcovered = null;
		n_partition = 0;
		partition = null;
		g_positive_covered=null;
		g_negative_covered=null;
		peso_positive = null;
		peso_negative = null;
	}
	
	example_set (int ejemplos, int variables){
		if ((ejemplos<=0) || (variables<=0))
			System.out.println ("Negative values are not allowed\n");
		else{
			n_example = ejemplos;
			n_variable = variables;
			n_partition = 1;
			covered = new boolean[n_example];
			gcovered = new double[n_example];
			data = new double[n_example][];
			partition = new int[n_example];
			g_positive_covered = new double[n_example];
			g_negative_covered = new double[n_example];
			peso_positive = new double[n_example];
			peso_negative = new double[n_example];
			
			for (int i=0; i<n_example; i++){
				data[i] = new double[variables];
				covered[i] = false;
				gcovered[i] = 0;
				partition[i] = 0;
				g_positive_covered[i]=0;
				g_negative_covered[i]=0;
				peso_positive[i] = 0;
				peso_negative[i] = 0;
			}
		}
	}
	
	example_set (example_set x){
		n_example = x.n_example;
		n_variable = x.n_variable;
		n_partition = x.n_partition;
		covered = new boolean[n_example];
		gcovered = new double[n_example];
		data = new double[n_example][];
		partition = new int[n_example];
		g_positive_covered = new double[n_example];
		g_negative_covered = new double[n_example];
		peso_positive = new double[n_example];
		peso_negative = new double[n_example];
		
		for (int i=0; i<n_example; i++){
			data[i] = new double[n_variable];
			covered[i] = x.covered[i];
			gcovered[i] = x.gcovered[i];
			partition[i] = x.partition[i];
			g_positive_covered[i]=x.g_positive_covered[i];
			g_negative_covered[i]=x.g_negative_covered[i];
			peso_positive[i] = x.peso_positive[i];
			peso_negative[i] = x.peso_negative[i];
		}
		
		for (int i=0; i<n_example; i++){
			for (int j=0; j<n_variable; j++)
				data[i][j] = x.data[i][j];
		}
	}
	
	example_set (char[] nom_fich) throws IOException{
		FileInputStream fich;
		
		try {
			fich = new FileInputStream(String.valueOf(nom_fich));
		} catch(FileNotFoundException e) {
			System.out.println("El fichero no existe.");
			return;
		}
		
		n_example = (int) fich.read();
		n_variable = (int) fich.read();
		
		n_partition = 1;
		partition = new int[n_example];
		data = new double[n_example][];
		covered = new boolean[n_example];
		gcovered = new double[n_example];
	    g_positive_covered = new double [n_example];
	    g_negative_covered = new double [n_example];
		peso_positive = new double [n_example];
		peso_negative = new double [n_example];
		
		for (int i=0; i<n_example; i++){
			data[i] = new double[n_variable];
			covered[i] = false;
			gcovered[i] = 0;
			partition[i] = 0;
			g_positive_covered[i] = 0;
			g_negative_covered[i] = 0;
			peso_positive[i] = 0;
			peso_negative[i] = 0;
		}
		
		for (int i=0; i<n_example; i++){
			for (int j=0; j<n_variable; j++)
				data[i][j] = (double) fich.read();
		}
	}
	
	
	
	 example_set (myDataset dataset){
		 
	    this.n_example = dataset.getnData();
	    this.n_variable = dataset.getnVars();
	    
	    this.n_partition = 1;
	    this.partition = new int[this.n_example];
	    this.data = new double[this.n_example][];
	    this.covered = new boolean[this.n_example];
	    this.gcovered = new double[this.n_example];
	    this.g_positive_covered = new double [this.n_example];
	    this.g_negative_covered = new double [this.n_example];
		this.peso_positive = new double [this.n_example];
		this.peso_negative = new double [this.n_example];
	    
	    
	    for (int i = 0; i < this.n_example; i++) {
	      this.data[i] = new double[this.n_variable];
	      this.covered[i] = false;
	      this.gcovered[i] = 0;
	      this.partition[i] = 0;
	      this.g_positive_covered[i] = 0;
	      this.g_negative_covered[i] = 0;
	      this.peso_positive[i] = 0;
	      this.peso_negative[i] = 0;
	    }

	    double[] aux = new double[this.n_variable];
	    for (int i = 0; i < this.n_example; i++) {
	      aux = dataset.getExample(i);
	      for (int j = 0; j < this.n_variable - 1; j++) {
	        this.data[i][j] = aux[j];
	      }
	      this.data[i][(this.n_variable - 1)] = dataset.getOutputAsInteger(i);
	    }
	  }
	
	
			
	private void Realloc (int new_examples){
		int final_n_examples = n_example + new_examples;
		
		int[] aux_partition = new int[final_n_examples];
		double[][] aux_data = new double[final_n_examples][];
		boolean[] aux_covered = new boolean[final_n_examples];
		double[] aux_gcovered = new double[final_n_examples];
	    double[] aux_g_positive_covered = new double [final_n_examples];
	    double[] aux_g_negative_covered = new double [final_n_examples];
		double[] aux_peso_positive = new double [final_n_examples];
		double[] aux_peso_negative = new double [final_n_examples];
		
		for (int i=0; i<n_example; i++){
			aux_data[i] = data[i];
			aux_covered[i] = covered[i];
			aux_gcovered[i] = gcovered[i];
			aux_partition[i] = partition[i];
			aux_g_positive_covered[i] = g_positive_covered[i];  
			aux_g_negative_covered[i] = g_negative_covered[i]; 
			aux_peso_positive[i] = peso_positive[i]; 
			aux_peso_negative[i] = peso_negative[i];
		}
		
		partition = aux_partition;
		data = aux_data;
		covered = aux_covered;
		gcovered = aux_gcovered;
		n_example = final_n_examples;
		g_positive_covered = aux_g_positive_covered;
		g_negative_covered = aux_g_negative_covered;
		peso_positive = aux_peso_positive;
		peso_negative = aux_peso_negative;
	}
	

	
	public double Data (int ejemplo, int variable){
		return (data[ejemplo][variable]);
	}
	
	
	public vectordouble Data (int ejemplo){
		vectordouble x = new vectordouble ();
		
		x.Put (data[ejemplo], n_variable);
		return x;
	}
	
	
	public int N_Examples (){
		return n_example;
	}
	
	
	public int N_Partitions (){
		return n_partition;
	}
	
	

	
	
	public boolean Is_Training_Example (int ejemplo, int particion){
		return (partition[ejemplo]!=particion);
	}
	
	
	public boolean Is_Test_Example (int ejemplo, int particion){
		return (partition[ejemplo]==particion);
	}
	
	
	public int Training_Example (int particion){
		int sum = 0;
		
		for (int i=0; i<n_example; i++){
			if (partition[i]!=particion)
				sum++;
		}
		
		return sum;
	}
	
	
	public int Test_Example (int particion){
		int sum = 0;
		
		for (int i=0; i<n_example; i++){
			if (partition[i]==particion)
				sum++;
		}
		
		return sum;		
	}
	
	
	public int Not_Covered_Training_Example (int particion){
		int sum = 0;
		
		for (int i=0; i<n_example; i++){
			if ((partition[i]!=particion) && (!covered[i]))
				sum++;
		}
		
		return sum;		
	}
	
	
	/**
	 * <p>
	 * It counts the number of not covered examples
	 * </p>
	 * @return int The number of not covered examples
	 */
	
	public int Not_Covered_Examples (){
		int numero = 0;
		
		for (int i=0; i<n_example; i++){
			if (!covered[i])
				numero++;
		}
		
		return numero;		
	}
	
	
	/**
	 * <p>
	 * For each class, it counts the number of examples
	 * </p>
	 * @param VarClass int The class variable
	 * @param particion int The partition used
	 * @param nclasses int The total number of classes
	 * @param n_examples_in_class int[] Vector containing the number of examples per class
	 */
	
	public void Examples_per_Class (int VarClass, int particion, int nclasses, int[] n_examples_in_class){
		int k;
		
		for (int i=0; i<nclasses; i++)
			n_examples_in_class[i] = 0;
		
		for (int i=0; i<n_example; i++){
			if (!covered[i]){
				k = (int) (data[i][VarClass]);
				n_examples_in_class[k]++;
			}
		}	
	}
	
	
	/**
	 * <p>
	 * For each class, it counts the number of examples
	 * </p>
	 * @param VarClass int The class variable
	 * @param nclasses int The total number of classes
	 * @param n_examples_in_class int[] Vector containing the number of examples per class
	 */
	
	public void Examples_per_Class (int VarClass, int nclasses, int[] n_examples_in_class){
		int k;
		
		for (int i=0; i<nclasses; i++)
			n_examples_in_class[i] = 0;
		
		for (int i=0; i<n_example; i++){
			if (!covered[i]){
				k = (int) (data[i][VarClass]);
				n_examples_in_class[k]++;
			}
		}	
	}
	
	
	public int N_Variables (){
		return n_variable;
	}
	
	
	public void Remove (int[] v, int tama){
		int n = n_example;
		
		for (int i=0; i<tama; i++){
			if (data[v[i]]!=null){
				data[v[i]] = null;
				n--;
			}			
		}
		
		int j = n;
		for (int i=0; i<n; i++){
			if (data[i] == null){
				while ((j<n_example) && (data[j]==null))
					j++;
				data[i] = data[j];
				data[j] = null;
			}
		}
		n_example = n;
	}
	
	

	
	
	public void Mark (int[] v, int tama, double[] grado_p, double[] peso_p, double[] grado_n, double[] peso_n){

		for (int i=0; i<tama; i++){
			if (v[i]==1)
				covered[i]=true;
		    else
		    	covered[i]=false;

		    g_positive_covered[i]=grado_p[i];
		    peso_positive[i]=peso_p[i];
		    g_negative_covered[i]=grado_n[i];
		    peso_negative[i]=peso_n[i];
		 }
	}
	
	

	
	/**
	 * <p>
	 * Set all positions (referred to examples) of vector "covered" to false (not covered)
	 * Set all positions (referred to examples) of vector "gcovered" (degree of coverage) to 0
	 * Set all positions (referred to examples) of vectors "g_positive_covered" and "g_negative_covered" (degree of positive/negative coverage) to 0
	 * Set all positions (referred to examples) of vectors "peso_positive" and "peso_negative" (positive/negative weights) to 0
	 * </p>
	 */

	public void UnMarkAll (){
		for (int i=0; i<n_example; i++){
			covered[i] = false;
			gcovered[i] = 0;
			g_positive_covered[i]=0;
			g_negative_covered[i]=0;
			peso_positive[i] = 0;
			peso_negative[i] = 0;
			
		}		
	}
	
	
	/**
	 * <p>
	 * Returns whether the individual "i" is covered or not
	 * </p>
	 * @param i int The individual
	 */
	
	public boolean Is_Covered (int i){
		return covered[i];
	}
	
	

	
	
	public double Weight_Is_Positive_Covered (int i){
	    return peso_positive[i];
	}
	
	
	public double Weight_Is_Negative_Covered (int i){
	    return peso_negative[i];
	}
	
	public double Grade_Is_Negative_Covered (int i){
		return g_negative_covered[i];
	}
	
	
	public double Grade_Is_Positive_Covered (int i){
		return g_positive_covered[i];
	}
	
	
	public double Grade_Is_Covered (int i){
		  if (g_positive_covered[i]>g_negative_covered[i] || (g_positive_covered[i]==g_negative_covered[i] && peso_positive[i]>peso_negative[i]) )
			    return g_positive_covered[i];
			  else
			    return -g_negative_covered[i];
	}
	
	
	public example_set Extract_Training_Set (int particion){
		int sum = 0;
		
		for (int i=0; i<n_example; i++){
			if (partition[i]!=particion)
				sum++;			
		}
		
		System.out.println ("examples: "+sum+" variables: "+n_variable+"\n");
		example_set E1 = new example_set (sum, n_variable);
		
		int l=0;
		for (int i=0; i<n_example; i++){
			if (partition[i]!=particion){
				for (int j=0; j<n_variable; j++)
					E1.data[l][j] = data[i][j];
				l++;
			}
		}
		return E1;
	}
	
	
	public example_set Extract_Test_Set (int particion){
		int sum = 0;
		
		for (int i=0; i<n_example; i++){
			if (partition[i]==particion)
				sum++;			
		}
		
		example_set E1 = new example_set (sum, n_variable);
		
		int l=0;
		for (int i=0; i<n_example; i++){
			if (partition[i]==particion){
				for (int j=0; j<n_variable; j++)
					E1.data[l][j] = data[i][j];
				l++;
			}
		}
		return E1;		
	}
	
}
