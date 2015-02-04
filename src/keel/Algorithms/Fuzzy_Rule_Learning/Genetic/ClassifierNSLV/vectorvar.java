
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierNSLV;

import java.io.*;

import keel.Dataset.Attributes;


public class vectorvar {
	
	/**	
	 * <p>
	 * It contains the methods for handling the variables
	 * </p>
	 */

	int numero;
	variable_t[] lista;
	
	
	vectorvar (){
		numero = 0;
		lista = null;
	}
	
	
	// - oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
	// - oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
	
	
	
	/**	
	 * <p>
	 * Parametric constructor
	 * </p>
	 * @param nomfich char[] Name of the file
	 * @param dataset myDataset Information regarding the variables/examples of the problem
	 * @param nom_clas NameClasses Name of the classes
	 * @param etiquetas int Number of labels for variables
	 */	
	
	vectorvar (char[] nomfich, myDataset dataset, NameClasses nom_clas, int etiquetas) throws IOException{
		
		
		double[] va = null;
		double[] vb = null;
		double[] vc = null;
		double[] vd = null;
		
		int numero_etiquetas = 0;
				
		Double_t inf = new Double_t (0);
		Double_t sup = new Double_t (0);
		String nom_var = new String ();
		String[] attributes;
		int va_read = 0, pos = 0;
		
		String headerDefinition = Attributes.getInputAttributesHeader() + Attributes.getOutputAttributesHeader();
		char[] arrayHeader = headerDefinition.toCharArray();
		
		
		//----------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------------------------------------------		
		
		numero = dataset.getnVars();
		attributes = new String[numero];
		
		lista = new variable_t[numero];
		
		for (int cont=0; cont<numero; cont++){
			if (arrayHeader[pos]=='@' && arrayHeader[pos+1]=='a'){
				int start = pos;
				int tam = 0;
				pos++;
				while (arrayHeader[pos]!='@' && (pos < arrayHeader.length-1)){
					tam++;
					pos++;
				}
				if (pos == arrayHeader.length-1)
					tam++;
				attributes[cont] = String.valueOf(arrayHeader, start, tam);
			}
			else
				pos++;
		}
		
		
		for (int i=0; i<numero; i++){
			if (i < numero-1){  
				numero_etiquetas = Count_Labels (attributes[i], etiquetas);
				va = new double[numero_etiquetas];
				vb = new double[numero_etiquetas];
				vc = new double[numero_etiquetas];
				vd = new double[numero_etiquetas];
				nom_clas.vname = new String[numero_etiquetas];
				nom_var = Store_Variable (attributes[i], va, vb, vc, vd, nom_clas.vname, inf, sup, true, dataset.getnClasses(), numero_etiquetas);
				lista[i] = new variable_t ();
				lista[i].Assign (numero_etiquetas, nom_var, 0, inf.value, sup.value, va, vb, vc, vd, nom_clas.vname);
			}
			else{ 
				va = new double[dataset.getnClasses()];
				vb = new double[dataset.getnClasses()];
				vc = new double[dataset.getnClasses()];
				vd = new double[dataset.getnClasses()];
				nom_clas.vname = new String[dataset.getnClasses()];
				nom_var = Store_Variable (attributes[i], va, vb, vc, vd, nom_clas.vname, inf, sup, false, dataset.getnClasses(), 0);
				lista[i] = new variable_t ();
				lista[i].Assign (dataset.getnClasses(), nom_var, 1, inf.value, sup.value, va, vb, vc, vd, nom_clas.vname);
			}
			
		}	
		
		
	}
	
	
	

	
	
	// - oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
	// - oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
	
	
	vectorvar (int tamano){
		numero = tamano;
		lista = new variable_t[tamano];
	}


	vectorvar (vectorvar x){
		numero = x.numero;
		lista = new variable_t[numero];
		
		for (int i=0; i<numero; i++)
			lista[i] = x.lista[i];
	}
	
	
	// - oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
	// - oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
	
	
	public static int Count_Labels (String linea, int max){
		final int MAX_VAL_ENTEROS = 10;
		int etiquetas = 0, comas = 0;
		int loc_inf = 0, loc_sup = 0;
		String var_entera = "integer";
		String substr = "";
		String[] cad_desglosada = null;
		String[] cad_inicial = null;
		char[] cad_aux = linea.toCharArray();
		
		loc_inf = linea.indexOf ('{', 0);
		if (loc_inf == -1){
			if (linea.indexOf (var_entera) != -1){
				loc_inf = linea.indexOf ('[', 0);
				loc_sup = linea.indexOf (']', 0);
				substr = String.valueOf (cad_aux, loc_inf+1, loc_sup-loc_inf); 
				cad_inicial = substr.split (",");
				cad_desglosada = Normalize_String (cad_inicial);
				
				etiquetas = Integer.valueOf (cad_desglosada[1]) - Integer.valueOf (cad_desglosada[0]) + 1;
				
				if (etiquetas > MAX_VAL_ENTEROS)
					etiquetas = max;
			}
			else{
				etiquetas = max;
			}	
		}	
		else{
			while ((loc_sup = linea.indexOf (',', loc_inf)) != -1){
				comas++;
				loc_inf = loc_sup+1;
			}	
			etiquetas = comas + 1;
		}
		
		return etiquetas;
	}
	
	
	
	public static boolean Is_Number (String cadena){
		try {
			Double.parseDouble (cadena);
			return true;
		} catch (NumberFormatException nfe){
			return false;
		}
	}
	
	
	
	public static boolean Check_Values (String[] cadena, int etiq){
		boolean no_nominal = true;
		
		for (int cont=0; cont< etiq; cont++){
			if (!Is_Number (cadena[cont]))
				no_nominal = false;
		}
		
		return no_nominal;
	}
	
	
		
	public static String[] Normalize_String (String[] cadena){
		String[] aux = new String[cadena.length];
		final int LONG_CADENA = 1000;
		char[] cad_aux = new char[LONG_CADENA];
		char[] copia = new char[LONG_CADENA];
		int cont_copia = 0;
		
		for (int cont=0; cont<cadena.length; cont++){
			cad_aux = cadena[cont].toCharArray();
			cont_copia = 0;
			for (int i=0; i<cadena[cont].length(); i++){
				if ((cad_aux[i] != ' ') && (cad_aux[i] != '}') && (cad_aux[i] != ']')){
					copia[cont_copia] = cad_aux[i];
					cont_copia++;
				}
			}
			aux[cont] = String.valueOf (copia, 0, cont_copia);
		}
		
		return aux;
	}
	
	
	
	public static String Store_Variable (String linea, double[] va, double[] vb, double[] vc, double[] vd, String[] vname, Double_t inf, Double_t sup, boolean antecedente, int clases, int numero_etiquetas){
		final int MAX_VAL_ENTEROS = 10;
		int loc_inf = 0, loc_sup = 0, lim_inf = 0, lim_sup = 0;
		char[] cad_aux = linea.toCharArray();
		double incremento = 0, valor_discreto = 0;
		boolean var_continua = false, var_discreta = false, var_entera = false, primera_vez = true;
		double a = 0, b = 0, c = 0;
		String nom_var = "", substr = "";
		String[] cad_desglosada = null;
		String[] cad_inicial = null;
		String var_real = "real";
		String var_no_real = " {";


		loc_inf = linea.indexOf (' ', 0);
		if (linea.indexOf('[') != -1){
			loc_sup = linea.indexOf (' ', loc_inf+1);
		}
		else{
			if (linea.indexOf(var_no_real) != -1)
				loc_sup = linea.indexOf (' ', loc_inf+1);
			else
				loc_sup = linea.indexOf ('{', loc_inf+1);
		}
		
		nom_var = String.valueOf (cad_aux, loc_inf+1, loc_sup-(loc_inf+1));
		
		loc_inf = linea.indexOf ('[', 0);
		
		
		if (loc_inf != -1){  
			if (linea.indexOf(var_real) != -1)
				var_continua = true;
			else
				var_entera = true;
				
			loc_sup = linea.indexOf (']', 0);
			substr = String.valueOf (cad_aux, loc_inf+1, loc_sup-loc_inf); 
			cad_inicial = substr.split (","); 
		}
		else{ 
			loc_inf = linea.indexOf ('{', 0);
			loc_sup = linea.indexOf ('}', 0);
			substr = String.valueOf (cad_aux, loc_inf+1, loc_sup-loc_inf); 
			cad_inicial = substr.split (",");				
		}
		
		cad_desglosada = Normalize_String (cad_inicial);
		
		if (var_entera){
			lim_inf = Integer.valueOf (cad_desglosada[0]);
			lim_sup = Integer.valueOf (cad_desglosada[1]);
			
			if (lim_sup-lim_inf+1 > MAX_VAL_ENTEROS){
				var_entera = false;
				var_continua = true;
			}
		}
		
		if (antecedente){
			
			if (var_continua || var_entera){
				
				inf.value = Double.valueOf (cad_desglosada[0]);
			
				sup.value = Double.valueOf (cad_desglosada[1]);
				
				if ((var_continua) || ((var_entera) && (sup.value-inf.value >= 11))){
			
					incremento = Calculate_Increase (inf.value, sup.value);
			
					a = inf.value + incremento;
					b = a + incremento;
					c = b + incremento;
			
			
					va[0] = inf.value;
					va[1] = inf.value;
					va[2] = a;
					va[3] = b;
					va[4] = c;
			
					vb[0] = inf.value;
					vb[1] = a;
					vb[2] = b;
					vb[3] = c;
					vb[4] = sup.value;
			
					vc[0] = inf.value;
					vc[1] = a;
					vc[2] = b;
					vc[3] = c;
					vc[4] = sup.value;
			
					vd[0] = a;
					vd[1] = b;
					vd[2] = c;
					vd[3] = sup.value;
					vd[4] = sup.value;
			
					vname[0] = "VeryLow";
					vname[1] = "Low";
					vname[2] = "Medium";
					vname[3] = "High";
					vname[4] = "VeryHigh";
				}
				else{
					int cuenta_et = 0;
					
					for (int i=(int)inf.value; i<=(int)sup.value; i++){
						va[cuenta_et] = vb[cuenta_et] = vc[cuenta_et] = vd[cuenta_et] = i;
						vname[cuenta_et] = "T" + cuenta_et;
						cuenta_et++;
					}
					
				}
				
			}
			else{ 
				var_discreta = Check_Values (cad_desglosada, numero_etiquetas); 
				
				for (int i=0; i<numero_etiquetas; i++){
					if (var_discreta){ 
						if (i < numero_etiquetas-1){ 					
							valor_discreto = Double.valueOf (cad_desglosada[i]); 
							if (i == 0)
								inf.value = 0;
								
						}
						else{ 
							valor_discreto = Double.valueOf (cad_desglosada[i]);
							//sup.valor = valor_discreto;
							sup.value = numero_etiquetas-1;
						}
						//va[i] = vb[i] = vc[i] = vd[i] = valor_discreto;
						va[i] = vb[i] = vc[i] = vd[i] = i;
						vname[i] = "T" + i;
					}
					else{ 
						if (primera_vez){ 
							inf.value = 0;
							sup.value = numero_etiquetas-1;
							primera_vez = false;
						}
						va[i] = vb[i] = vc[i] = vd[i] = i;
						vname[i] = cad_desglosada[i];
					}
					
				}
			}	
		}
		else{ 
			inf.value = 0;
			sup.value = clases-1;
			
			for (int cont=0; cont<clases; cont++){  	
				vname[cont] = cad_desglosada[cont];		
				va[cont] = vb[cont] = vc[cont] = vd[cont] = cont;
			}	
			
		}
		
		return nom_var;
	}
	
	
	public static double Calculate_Increase (double inf, double sup){
		double resultado;
		
		resultado = (sup - inf)/4;
		
		resultado = resultado * 100;
		resultado = Math.round (resultado);
		resultado = resultado / 100;
		
		return resultado;
	}
	
	
	// - oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
	// - oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
	
	
	public void Assign (int pos, variable_t x){
		if (pos < numero)
			lista[pos] = x;
		else
			System.out.println ("The position does not exist.\n");
	}


	public int N_Antecedent (){
		int s = 0;
		  
		for (int i=0; i<numero; i++){
			if (lista[i].Active() && lista[i].Antecedent())
				s++;
		}  

		return s;
	}
	
	
	public void Encode (int[] tama, int[] rango, int pos1, int pos2){
		tama[pos1] = 0;
		  
		for (int i=0; i<numero; i++){
			if (lista[i].Active() && lista[i].Antecedent())
				tama[pos1] += lista[i].N_labels();
			else{
				if (lista[i].Active() && !lista[i].Antecedent())
					rango[pos2] = lista[i].N_labels();
			}	
		}	
	}


	public void Paint (int variable){
		if (variable >=0 && variable<numero)
			lista[variable].Paint ();
		else
			System.out.println ("The variable does not exist.\n");
	}


	public void Paint(){
		for (int i=0; i<numero; i++)
			lista[i].Paint ();
	}


	public void PrintVar (int variable){
		lista[variable].PrintVar ();
	}

	
	String SPrintVar (int variable){
		return lista[variable].SPrintVar ();
	}

	
	public void PrintDomain (int variable, int value){
		lista[variable].PrintDomain (value);
	}
	

	public String SPrintDomain (int variable, int value){
		return lista[variable].SPrintDomain (value);
	}
	

	public boolean IsActive (int variable){
		return lista[variable].Active ();
	}


	public boolean IsAntecedent (int variable){
		return lista[variable].Antecedent ();
	}


	public int TotalVariables (){
		return numero;
	}
	
	
	
	/**
	 * <p>
	 * Returns the number of labels belonging to the domain of the variable "variable"
	 * </p>
	 * @param variable int The selected variable
	 * @return int The number of labels belonging to the domain of the variable
	 */

	public int SizeDomain (int variable){
		return lista[variable].SizeDomain ();
	}


	public double Adaptation (double x, int variable){
		if (variable >=0 && variable<numero)
			return lista[variable].Adaptation (x);
		else{
			System.out.println ("The variable does not exist.\n");
			System.exit(1);
			return 0;  
		}
	}


	public double Adaptation (double x, int variable, int dominio){
		if (variable >=0 && variable<numero)
			return lista[variable].Adaptation (x, dominio);
		else{
			System.out.println ("The variable does not exist.\n");
			System.exit(1);
			return 0;
		}
	}


	public double Adaptation (double x, int variable, String dominio){
		if (variable >=0 && variable<numero)
			return lista[variable].Adaptation (x, dominio);
		else{
			System.out.println ("The variable does not exist..\n");
			System.exit(1);
			return 0;
		}

	}


	public double Adaptation (vectordouble x, String regla){
		double max = 1, aux;
		String sub;
		int trozo = 0, tam;
		  
		for (int i=0; i<numero && max>0; i++){
			if (lista[i].Active() && lista[i].Antecedent()){
				tam = lista[i].N_labels();
				sub = regla.substring (trozo, trozo+tam);
				aux = lista[i].Adaptation (x.At(i), sub);
				if (aux < max)
					max = aux;
				trozo += tam;
			}
		}

		return max;
	}


	public int NumberActiveLabels (String cadena, int tam){
		int n = 0;
		char[] sub = cadena.toCharArray();
		  
		for (int i=0; i<tam; i++)
			if (sub[i] == '1')
				n++;
		return n;
	}

	public void SequenceActiveLabels (String cadena, int tam, Int_t unos, Int_t ceros, Int_t n_unos){
		//int n = 0;
		unos.value = 0; 
		ceros.value = 0;
		n_unos.value = 0;
		boolean last_uno;
		int i = 1;
		char[] sub = cadena.toCharArray();
		
		if (sub[0] == '0'){
			ceros.value++;
			last_uno = false;
		}
		else{
			unos.value++;
			last_uno = true;
			n_unos.value++;
		}
		
		while (i < tam){
			if (sub[i] == '1')
				n_unos.value++;
			if (last_uno && sub[i]=='0'){
				last_uno = false;
				ceros.value++;
			}
			else{
				if (!last_uno && sub[i]=='1'){
					last_uno = true;
					unos.value++;
				}
			}	
			i++;
		}
	}
	

	public boolean Is_Valid (String regla, double[] var, double umbral, Double_t simplicidad){
		String sub;
		int trozo = 0, tam;
		Int_t unos = new Int_t (0);
		Int_t ceros = new Int_t (0);
		Int_t n_unos = new Int_t (0);
		int i = 0;
		simplicidad.value = 0;
		boolean valida = true;
		  
		while (i<numero && valida){
			if (lista[i].Active() && lista[i].Antecedent()){
				tam = lista[i].N_labels();
				if (var[i]>=umbral){
					sub = regla.substring (trozo,trozo+tam);
					SequenceActiveLabels (sub,tam,unos,ceros, n_unos);
					valida = (unos.value!=0);
					if (valida){
						if (unos.value==1 || ceros.value==1)
							simplicidad.value = simplicidad.value+1;
					}
				}
				trozo += tam;
			}
			i++;
		}

		return valida;
	}




	public double Adaptation (vectordouble x, String regla, double[] var, double umbral){
		double max = 1, aux;
		String sub;
		int trozo = 0, tam, unos;
		  
		for (int i=0; i<numero && max>0; i++){
			if (lista[i].Active() && lista[i].Antecedent()){
				tam = lista[i].N_labels();
				if (var[i]>=umbral){
					sub = regla.substring (trozo, trozo+tam);
					unos = NumberActiveLabels (sub, tam);
					if (unos == 0)
						max = 0;
					else{
 						if (unos < tam){
							aux = lista[i].Adaptation (x.At(i), sub);
							if (aux < max)
								max = aux;
						}
					}	
				}
				trozo += tam;
			}
		}

		return max;
	}


	public double Adaptation (vectordouble x, String regla, double[] var, double umbral, double umbral2){
		double max = 1, aux;
		String sub;
		int trozo = 0, tam, unos;

		if (umbral2 < 0)
			umbral2 =- umbral2;

		for (int i=0; i<numero && max>=umbral2 && max>0; i++){
			if (lista[i].Active() && lista[i].Antecedent()){
				tam = lista[i].N_labels();
				if (var[i] >= umbral){
					sub = regla.substring (trozo, trozo+tam);
					unos = NumberActiveLabels (sub, tam);
					if (unos == 0)
						max = 0;
					else{
						if (unos < tam){
							aux = lista[i].Adaptation (x.At(i),sub);
							if (aux < max)
								max = aux;
						}
					}	
				}
				trozo += tam;
			}
		}

		if (max >= umbral2)
			return max;
		else
			return 0;
	}


	public double Adaptation (vectordouble x, genetcode regla){
		return 0;
	}



	public void AdaptationC (vectordouble x, int etiq, Double_t pos, Double_t neg){
		double valor, aux;
		int i = 0;
		  
		while (lista[i].Antecedent() && i<numero-1)
			i++;

		valor = x.At(i);
		pos.value = lista[i].Adaptation (valor, etiq);
		neg.value = 0;
		for (int j=0; j<lista[i].N_labels(); j++){
			if (j != etiq){
				aux = lista[i].Adaptation (valor, j);
				if (aux > neg.value)
					neg.value = aux;
			}
		}	
	}


	public double Area (int var, int lab){
		return lista[var].Area (lab);
	}
	

	public fuzzy_t FuzzyLabel (int var, int lab){
		return lista[var].FuzzyLabel (lab);
	}
	

	public double CenterLabel (int var, int lab){
		return lista[var].CenterLabel (lab);
	}
	

	public boolean IsDiscrete (int var){
		return lista[var].IsDiscrete ();
	}
	

	public	boolean IsInterval (int var){
		return lista[var].IsInterval ();
	}

	
	public	boolean IsFuzzy (int var){
		return lista[var].IsFuzzy ();
	}

	
	public	domain_t Domain (int var){
		domain_t aux;
		aux = lista[var].Domain ();
		return aux;
	}

	public	variable_t Variable (int var){
		variable_t aux;
		aux = lista[var].Variable ();
		return aux;
	}


	public	double Inf_Range (int var){
		return lista[var].Inf_Range ();
	}

	
	public double Sup_Range (int var){
		return lista[var].Sup_Range ();
	}


	public int Consequent (){
		int i=0;
		  
		while ((lista[i].Antecedent() || !lista[i].Active()) && i<numero)
			i++;

		if (i != numero)
			return i;
		else{
			System.out.println ("There is no consequent variable.\n");
			return -1;
		}
	}

	

	
}
