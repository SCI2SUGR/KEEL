
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierNSLV;
import java.io.*;

	/**	
	 * <p>
	 * It contains the methods for handling the domain of the variables
	 * </p>
	 */
public class domain_t implements Cloneable {
	

	
	int numero;	//Number of labels
	double rango_inf;	//Lower value of the range
	double rango_sup;	//Higher value of the range
	boolean inf_inf;	//Determines if the lower value is delimited
	boolean sup_inf;	//Determines if the higher value is delimited
	fuzzy_t[] label;	//Labels

	
	
	/**
	 * <p>
	 * Default Constructor
	 * </p>
	 */
	
 	domain_t (){
		numero = 0;
		rango_inf = rango_sup = 0;
		inf_inf = sup_inf = false;
		label = null;
	}
	
	/**
	 * <p>
	 * Class builder with parameters
	 * </p>
	 * @param n int number of labels of the domain
	 * @param inf double inferior limit of the domain
	 * @param sup double superior limit of the domain
	 * @param menosinf boolean when true the inferior limit is not delimited
	 * @param masinf boolean when true the superior limit is not delimited
	 */
 	domain_t (int n, double inf, double sup, boolean menosinf, boolean masinf){
		double ancho;
		
		numero = n;
		rango_inf = inf;
		rango_sup = sup;
		inf_inf = menosinf;
		sup_inf = masinf;
		label = new fuzzy_t[n];
		
		
		ancho = (sup-inf)/(n-1);
		String aux = new String();
		String nom_label = new String();
		
		for (int i=1; i<n-1; i++){
			aux = String.format("L%d", i);
			nom_label = aux;
			label[i].Assign (rango_inf+(ancho*(i-1)), rango_inf+(ancho*i), rango_inf+(ancho*i), rango_inf+(ancho*(i+1)), nom_label, false, false);						
		}
		
		label[0].Assign (rango_inf, rango_inf, rango_inf, rango_inf+ancho, "L0", menosinf, false);
		aux = String.format("L%d", n-1);
		nom_label = aux;
		label[n-1].Assign (rango_sup-ancho, rango_sup, rango_sup, rango_sup, nom_label, false, masinf);
	}

	
	domain_t (domain_t x){
		numero = x.numero;
		rango_inf = x.rango_inf;
		rango_sup = x.rango_sup;
		inf_inf = x.inf_inf;
		sup_inf = x.sup_inf;
		label = new fuzzy_t[numero];
		
		for (int i=0; i<numero; i++)
			label[i] = x.label[i];
	}
	
	
	
	public Object clone(){
		domain_t obj = null;
		try{
			obj = (domain_t) super.clone();
		}catch (CloneNotSupportedException ex){
			System.out.println ("\nError.\n");
		}
		
		obj.label = (fuzzy_t[]) obj.label.clone();
		for (int i=0; i<obj.label.length; i++){
			obj.label[i] = (fuzzy_t) obj.label[i].clone();
		}
		
		return obj;
	}
	
	
	
	
	public void Assign (int n, double inf, double sup, boolean menosinf, boolean masinf){
		double ancho;
		
		numero = n;
		rango_inf = inf;
		rango_sup = sup;
		inf_inf = menosinf;
		sup_inf = masinf;
		label = new fuzzy_t[n];
		
		
		ancho = (sup-inf)/(n-1);
		String aux = new String();
		String nom_label = new String();
		
		for (int j = 0; j < n; j++) {
			label[j] = new fuzzy_t ();
		}
		
		for (int i=1; i<n-1; i++){
			aux = String.format("L%d", i);
			nom_label = aux;
			label[i].Assign (rango_inf+(ancho*(i-1)), rango_inf+(ancho*i), rango_inf+(ancho*i), rango_inf+(ancho*(i+1)), nom_label, false, false);						
		}
		
		label[0].Assign (rango_inf, rango_inf, rango_inf, rango_inf+ancho, "L0", menosinf, false);
		aux = String.format("L%d", n-1);
		nom_label = aux;
		label[n-1].Assign (rango_sup-ancho, rango_sup, rango_sup, rango_sup, nom_label, false, masinf);		
	}
	
	
	public void Assign (int n, double inf, double sup, double[] a, double[] b, double[] c, double[] d, String[] name){
		numero = n;
		rango_inf = inf;
		rango_sup = sup;
		inf_inf = true;
		sup_inf = true;
		label = new fuzzy_t[numero];
		
		for (int i=0; i<numero; i++){
			label[i] = new fuzzy_t ();
			label[i].Assign (a[i], b[i], c[i], d[i], name[i]);
		}	
	}
	
	
	public int N_labels (){
		return numero;
	}
	
	
	public int Size (){
		return numero;
	}
	

	public double Adaptation (double x){
		double mayor, nuevo;
		
		if (numero == 0){
			System.out.println("The domain is not created\n");
			System.exit(1);
		}
		
		mayor = label[0].Adaptation (x);
		
		for (int i=1; i<numero; i++){
			nuevo = label[i].Adaptation (x);
			if (nuevo > mayor)
				mayor = nuevo;
		}
		return mayor;
	}
	

	public double Adaptation (double x, int etiqueta){
		if (numero == 0){
			System.out.println("The domain is not created\n");
			System.exit(1);
		}
		
		if (etiqueta >= numero){
			System.out.println("That label does not belong to the domain");
			System.exit(1);
		}
		return label[etiqueta].Adaptation(x)/Adaptation(x);
	}
	

	public double Adaptation (double x, String etiquetas){
		double mayor, nuevo;
		char[] etiquetas_aux;
		
		etiquetas_aux = etiquetas.toCharArray(); 
		
		if (numero == 0){
			System.out.println("The domain is not created\n");
			System.exit(1);
		}
		
		mayor = etiquetas.length();
		if (mayor > numero){
			System.out.println("That label does not belong to the domain");
			System.exit(1);
		}
		
		mayor = 0;
		for (int i=0; i<numero; i++){
			if (etiquetas_aux[i] == '1'){
				nuevo = label[i].Adaptation (x);
				if (nuevo > mayor)
					mayor = nuevo;
			}
		}
		return mayor/Adaptation (x);
	}
	

	public void Paint (){
		for (int i=0; i<numero; i++){
			label[i].Paint ();
		}
	}
	

	public void Paint (int i){
		label[i].Paint ();
	}
	
	public void Print (int i){
		label[i].Print ();
	}
	
	public String SPrint (int i){
		return label[i].SPrint ();
	}
	

	public fuzzy_t FuzzyLabel (int i){
		fuzzy_t aux;
		aux = label[i].FuzzyLabel ();
		return aux;
	}
	

	public double CenterLabel (int i){
		return label[i].CenterLabel ();
	}
	

	public boolean IsDiscrete  (){
		int i = 0;
		
		while ((i < numero) && (label[i].IsDiscrete ()))
			i++;
		
		return (i == numero);
	}
	

	public boolean IsInterval (){
		int i = 0;
		
		while ((i < numero) && (label[i].IsInterval ()))
			i++;
		
		return (i == numero);		
	}
	

	public boolean IsFuzzy (){
		int i = 0;
		
		while ((i < numero) && (label[i].IsFuzzy ()))
			i++;
		
		return (i == numero);		
	}	
	

	public double Area (int l){
		return label[l].Area ();
	}
	

	public domain_t Domain (){
		domain_t aux;
		aux = (domain_t) this.clone();
		return aux;
	}
	

	public double Inf_Range (){
		return rango_inf;
	}
	

	public double Sup_Range (){
		return rango_sup;
	}
	

	
}
