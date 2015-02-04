package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierSLAVEv0;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class variable_t implements Cloneable {
	
	/**	
	 * <p>
	 * It contains the methods for handling the information related to the variables
	 * </p>
	 */

	String nombre;
	domain_t[] dominio;
	boolean activa;
	boolean antecedente;
	
	
	variable_t (){
		dominio = null;
		nombre = "Sin asignar";
		activa = false;
		antecedente = true;
	}


	variable_t (variable_t x){
		nombre = x.nombre;
		activa = x.activa;
		antecedente = x.antecedente;
		if (x.dominio == null)
			dominio = null;
		else{
			dominio = new domain_t[1];
			dominio = x.dominio;
		}
	}
	
	
	
	public Object clone(){
		variable_t obj = null;
		try{
			obj = (variable_t) super.clone();
		}catch (CloneNotSupportedException ex){
			System.out.println ("\nError.\n");
		}

		obj.dominio = (domain_t[]) obj.dominio.clone();
		for (int i=0; i<obj.dominio.length; i++){
			obj.dominio[i] = (domain_t) obj.dominio[i].clone();
		}
		
		return obj;
	}
	
	
	
	public void Assign (int n, double inf, double sup, boolean menosinf, boolean masinf, String name){
		nombre = name;
		activa = true;
		antecedente = true;

		dominio = new domain_t[1];
		dominio[0].Assign (n, inf, sup, menosinf, masinf);
	}
	
	

	public void Assign (int n, String varname, int status, double inf, double sup, double[] a, double[] b, double[] c, double[] d, String[] name){
		nombre = varname;
		if (status == -1){
			activa = false;
			antecedente = true;
		}
		else{
			if (status == 0) {
				activa = true;
				antecedente = true;
			}
			else{
				activa = true;
				antecedente = false;
			}
		}	

		dominio = new domain_t[1];
		dominio[0] = new domain_t ();
		dominio[0].Assign (n, inf, sup, a, b, c, d, name);
	}
	
	
	/**
	 * <p>
	 * Calculates the adaptation degree of "x" with the variable
	 * </p>
	 * @param x double A value
	 * @return double The adaptation degree
	 */
	
	public double Adaptation (double x){
		if (dominio == null){
			System.out.println ("No domain associated to the variable.\n");
			System.exit(1);
		}
		return dominio[0].Adaptation (x);
	}



	public double Adaptation (double x, int etiqueta){
		if (dominio == null){
			System.out.println ("No domain associated to the variable.\n");
			System.exit(1);
		}
	  return dominio[0].Adaptation (x,etiqueta);
	}
	
	

	public double Adaptation (double x, String etiquetas){
		if (dominio == null){
			System.out.println ("No domain associated to the variable.\n");
			System.exit(1);
		}
		return dominio[0].Adaptation (x,etiquetas);
	}



	public void Paint (){
		if (dominio == null){
			System.out.println ("No domain associated to the variable.\n");
			System.exit(1);
		}
		System.out.println ("Variable: "+nombre+"\n");
		System.out.println ("========================\n");
		dominio[0].Paint ();
	}


	public void PrintVar (){
		System.out.println (nombre);
	}

	public String SPrintVar (){
		return nombre;
	}


	public void PrintDomain (int value){
		dominio[0].Print (value);
	}


	public String SPrintDomain (int value){
		return dominio[0].SPrint (value);
	}


	public int SizeDomain (){
		return dominio[0].Size ();
	}

	public boolean Active (){
		return activa;
	}


	public boolean Antecedent (){
		return antecedente;
	}


	public int N_labels (){
		return dominio[0].N_labels ();
	}



	public fuzzy_t FuzzyLabel (int i){
		fuzzy_t aux;
		aux = dominio[0].FuzzyLabel (i);
		return aux;
	}


	public double CenterLabel (int i){
		return dominio[0].CenterLabel (i);
	}


	public boolean IsDiscrete (){
		return dominio[0].IsDiscrete ();
	}



	public boolean IsInterval (){
		return dominio[0].IsInterval ();
	}



	public boolean IsFuzzy (){
		return dominio[0].IsInterval ();
	}



	public double Area (int l){
		return dominio[0].Area (l);
	}


	public domain_t Domain (){
		domain_t aux;
	  
		aux = dominio[0];
		return aux;
	}


	public variable_t Variable (){
		variable_t aux;
	  
		aux = (variable_t) this.clone();
		return aux;
	}



	public double Inf_Range (){
		return dominio[0].Inf_Range ();
	}

	public double Sup_Range (){
		return dominio[0].Sup_Range ();
	}

	
	
	
	
	
	
	
	
	
	
}
