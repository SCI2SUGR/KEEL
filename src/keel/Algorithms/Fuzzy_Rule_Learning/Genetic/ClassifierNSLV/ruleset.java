
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierNSLV;

import java.io.*;

import org.core.Files;


public class ruleset {
	
	
	/**	
	 * <p>
	 * It contains the methods for handling the set of learned rules
	 * </p>
	 */

	int reservado;
	int n_rule;
	genetcode[][] rule;
	vectorvar[] domain;
	double[] peso;
	
	
	
	ruleset (){
		reservado = 10;
		n_rule = 0;
		rule = new genetcode[reservado][];
		peso = new double[reservado];
		domain = null;	
	}
	

	ruleset (vectorvar x){
		reservado = 10;
		n_rule = 0;
		rule = new genetcode[reservado][];
		peso = new double[reservado];
		domain = new vectorvar[1];
		domain[0] = x;
	}


	ruleset (ruleset x){
		reservado = x.reservado;
		n_rule = x.n_rule;
		rule = new genetcode[reservado][];
		peso = new double[reservado];
		for (int i=0; i<n_rule; i++){
			rule[i] = new genetcode[1];
			rule[i][0] = x.rule[i][0];
			peso[i] = x.peso[i];
		}
	}
	
	
	public void AddDomain (vectorvar x){
		domain = new vectorvar[1];
		domain[0] = x;
	}
	

	void Realloc (){
		genetcode[][] aux = new genetcode[2*reservado+1][];
		double[] p = new double[2*reservado+1];

		for (int i=0; i<n_rule; i++){
			aux[i] = rule[i];
			rule[i] = null;
			p[i] = peso[i];
		}
		rule = aux;
		peso = p;
		reservado = 2*reservado+1;	
	}
	
	
	/**
	 * <p>
	 * Add a new rule "x" to the last position of the set of learned rules
	 * with its weight associated "weight".
	 * </p>
	 * @param x genetcode The genetic information of the rule which is going to be added
	 * @param weight double The weight of the rule
	 */
	
	public void Add (genetcode x, double weight) {
		if (reservado == n_rule){
			Realloc();
		}
		rule[n_rule] = new genetcode[1];
		rule[n_rule][0] = x;
		peso[n_rule] = weight;
		n_rule++;
	}

	
	/**
	 * <p>
	 * Associates the weight "weight" to the rule "rule"
	 * </p>
	 * @param rule int The selected rule
	 * @param weight double The weight of the rule
	 */
	
	public void Add_Weight (int rule, double weight) {
	   peso[rule] = weight;
	}
	
	
	public double Get_Weight (int rule){
		return peso[rule];
	}

	
	
	/**
	 * <p>
	 * Removes the last rule of the set of learned rules.
	 * </p>
	 */
	
	public void Remove(){
		n_rule--;
	}


	/**
	 * <p>
	 * Removes the rule "i" from the ruleset
	 * </p>
	 * @param i int The selected rule
	 * @param weight Double_t The weight of the rule
	 * @return genetcode[] The genetic code of the extracted rule
	 */
	
	public genetcode[] Extract (int i, Double_t weight){
		genetcode[] aux = null;
		if (i < n_rule){
			aux = rule[i];
			weight.value = peso[i];
			for (int j=i+1; j<n_rule; j++){
				rule[j-1] = rule[j];
				peso[j-1] = peso[j];
			}
			n_rule--;
		}
		else
			weight.value = -1;

		return aux;
	}
	
	
	public void Insert (int i, genetcode[] r, double weight){
		if (i <= n_rule){
			if (reservado == n_rule)
				Realloc();

			for (int j=n_rule; j>i; j--){
				rule[j] = rule[j-1];
				peso[j] = peso[j-1];
			}

			rule[i] = r;
			peso[i] = weight;
			n_rule++;
		}
	}

	
	
	/**
	 * <p>
	 * Returns the number of rules of the ruleset
	 * </p>
	 * @return int The number of rules
	 */
	
	public int N_rule(){
		return n_rule;
	}
	
	
	/**
	 * <p>
	 * Returns the average number of variables per rule.
	 * </p>
	 * @return double The average number of variables per rule
	 */
	
	public double Variables_per_rule (){
		int r;
		int[] nr;
		double cont = 0;
		double[][] mr;
		
		for (int i=0; i<n_rule; i++){
			r = rule[i][0].GetReal1 ();
			nr = rule[i][0].GetReal2 ();
			mr = rule[i][0].GetReal3 ();
			for (int j=0;j<nr[0]-1;j++)
				if (mr[0][j]>=mr[0][nr[0]-1])
					cont = cont+1;
		}

		return cont/n_rule;

	}
	
	

	/**
	 * <p>
	 * Returns the rate of variables used in rules related to all variables involved.
	 * </p>
	 * @return double The rate of variables used in rules related to all variables involved
	 */
	
	public double Variables_Used (){
		int b;
		int[] nb;
		char[][] mb;
		double cont = 0;
		int r;
		int[] nr;
		double[][] mr;

		r = rule[0][0].GetReal1 ();
		nr = rule[0][0].GetReal2 ();
		mr = rule[0][0].GetReal3 ();
		
		int[] used= new int[nr[0]];

		for (int j=0; j<nr[0]-1; j++)
			used[j] = 0;

		int donde, z, n_casos;
		char[] s;
		
		for (int i=0; i<n_rule; i++){
			r = rule[i][0].GetReal1 ();
			nr = rule[i][0].GetReal2 ();
			mr = rule[i][0].GetReal3 ();
			
			b = rule[i][0].GetBinary1 ();
			nb = rule[i][0].GetBinary2 ();
			mb = rule[i][0].GetBinary3 ();
			donde = 0;
			s = new char[nb[0]];
			for (int j=0; j<nb[0]; j++)
				s[j] = mb[0][j];

			for (int j=0;j<nr[0]-1;j++){
				n_casos = domain[0].SizeDomain (j);
				for(z=0; z<n_casos && s[donde+z]=='1';z++);
				
				if (mr[0][j]>=mr[0][nr[0]-1] && z!=n_casos)
					used[j]++;
				donde += n_casos;
			}

		}

		for (int j=0; j<nr[0]-1; j++)	
			if (used[j] > 0)
				cont = cont+1;

		return cont;
	}




	/**
	 * <p>
	 * Returns in "frec" the times that each variable appears in the rule base.
	 * </p>
	 * @param frec int[] Vector with the information required
	 * @return int[] Vector with the times that each variable appears in the rule base
	 */

	public double Frecuence_each_Variables (int[] frec){
		int b;
		int[] nb;
		char[][] mb;
		double cont = 0;
		int r;
		int[] nr;
		double[][] mr;

		r = rule[0][0].GetReal1 ();
		nr = rule[0][0].GetReal2 ();
		mr = rule[0][0].GetReal3 ();
		
		for (int j=0; j<nr[0]-1; j++)
			frec[j] = 0;

		int donde, z, n_casos;
		char[] s;
		for (int i=0; i<n_rule; i++){
			r = rule[i][0].GetReal1 ();
			nr = rule[i][0].GetReal2 ();
			mr = rule[i][0].GetReal3 ();
			
			b = rule[i][0].GetBinary1 ();
			nb = rule[i][0].GetBinary2 ();
			mb = rule[i][0].GetBinary3 ();
			donde = 0;
			s = new char[nb[0]];
			for (int j=0; j<nb[0]; j++)
				s[j] = mb[0][j];

			for (int j=0;j<nr[0]-1;j++){
				n_casos = domain[0].SizeDomain (j);
				for(z=0; z<n_casos && s[donde+z]=='1';z++);

				if (mr[0][j]>=mr[0][nr[0]-1] && z!=n_casos)
					frec[j]++;
				donde += n_casos;
			}

		}

		for (int j=0; j<nr[0]-1; j++)
			if (frec[j] > 0)
				cont = cont+1;



		return cont/(nr[0]-1);
	}

	
	
	/**
	 * <p>
	 * Returns the number of conditions per rule base.
	 * </p>
	 * @return int The number of conditions per rule base.
	 */

	public int Conditions_per_RB (){
		int r, b;
		int[] nr;
		int[] nb;
		char[][] mb;
		double[][] mr;

		r = rule[0][0].GetReal1 ();
		nr = rule[0][0].GetReal2 ();
		mr = rule[0][0].GetReal3 ();
		
		int donde, z, n_casos, medida=0;
		char[] s;
		
		for (int i=0; i<n_rule; i++){
			r = rule[i][0].GetReal1 ();
			nr = rule[i][0].GetReal2 ();
			mr = rule[i][0].GetReal3 ();
			
			b = rule[i][0].GetBinary1 ();
			nb = rule[i][0].GetBinary2 ();
			mb = rule[i][0].GetBinary3 ();
			donde = 0;
			s = new char[nb[0]];
			for (int j=0; j<nb[0]; j++)
				s[j] = mb[0][j];

			for (int j=0;j<nr[0]-1;j++){
				n_casos = domain[0].SizeDomain (j);
				for(z=0; z<n_casos && s[donde+z]=='1';z++);

				if (mr[0][j]>=mr[0][nr[0]-1] && z!=n_casos)
					medida++;
				donde += n_casos;
			}

		}

		return medida;

	}
	
	




	/**
	 * <p>
	 * Returns the class of the rule that better adapts to the example "v", the adaptation
	 * degree "grado" and the ordinal of the fired rule "regla_disparada".
	 * </p>
	 * @param v vectordouble The selected example
	 * @param grado Double_t The adaptation degree
	 * @param regla_disparada Int_t The ordinal of the fired rule
	 * @return int The class of the rule that better adapts to the example
	 */
	
	public int InferenceC (vectordouble v, Double_t grado, Int_t regla_disparada){
		int b, n;
		int[] nb;
		int[] nn;
		int[][] nnn;
		char[][] mb;
		double max = 0, aux;
		int re = -1;
		String regla;
		int r;
		int[] nr;
		double[][] mr;
		
		for (int i=0; i<n_rule; i++){
			b = rule[i][0].GetBinary1 ();
			nb = rule[i][0].GetBinary2 ();
			mb = rule[i][0].GetBinary3 ();
			
			char[] s = new char[nb[0]];
			
			for (int j=0; j<nb[0]; j++)
				s[j] = mb[0][j];

			regla = new String (s);
			
			n = rule[i][0].GetInteger1 ();
			nn = rule[i][0].GetInteger2 ();
			nnn = rule[i][0].GetInteger3 ();
			
			r = rule[i][0].GetReal1 ();
			nr = rule[i][0].GetReal2 ();
			mr = rule[i][0].GetReal3 ();
			
			aux = domain[0].Adaptation (v,regla, mr[0], mr[0][nr[0]-1]);
			
			aux = aux * peso[i];

			if (aux > max){
				max = aux;
				re = i;
				grado.value = aux;
			}
			else{
				if (re!=-1 && aux>0 && aux==max && peso[i]>peso[re]) {
					max = aux;
					re = i;
					grado.value = aux;
				}
			}	
		}

		regla_disparada.value = re;

		if (re!=-1) {
			n = rule[re][0].GetInteger1 ();
			nn = rule[re][0].GetInteger2 ();
			nnn = rule[re][0].GetInteger3 ();
			return nnn[0][0];
		}
		else
			return -1;
	}
	
	

	
	
	
	public int InferenceC (vectordouble v, int clase, double[] grado_p, double[] peso_p, double[] grado_n, double[] peso_n, int[] regla_disparada, int[] aislada, int pos){
		 double max=0,aux, grado=0;
		 int re=-1;
		 int b,n, r;
		 int[] nb;
		 int[] nn;
		 int[][] nnn;
		 int[] nr;
		 char[][] mb;
		 double[][] mr;
		 String regla;
		 int var1, var2;
		 double[] adaptacion_particular = new double[n_rule];
		 boolean clasifica_bien=false;
		 int de_clase=0, de_noclase=0, indice_pos=n_rule, indice_neg=n_rule;

		 grado_p[pos] = 0;
		 peso_p[pos] = 0;
		 grado_n[pos] = 0;
		 peso_n[pos] = 0;

		 for (int i=0; i<n_rule; i++){
			b = rule[i][0].GetBinary1 ();
			nb = rule[i][0].GetBinary2 ();
			mb = rule[i][0].GetBinary3 ();
			
		    char[] s = new char[nb[0]];
		    for (int j=0; j<nb[0]; j++)
		      s[j] = mb[0][j];
		    
		    regla = new String (s);


			n = rule[i][0].GetInteger1 ();
			nn = rule[i][0].GetInteger2 ();
			nnn = rule[i][0].GetInteger3 ();
			
			r = rule[i][0].GetReal1 ();
			nr = rule[i][0].GetReal2 ();
			mr = rule[i][0].GetReal3 ();
			
			aux=domain[0].Adaptation(v,regla,mr[0],mr[0][nr[0]-1]);


		    aux = aux*peso[i];

		    if (nnn[0][0]==clase)
		      adaptacion_particular[i]=aux;
		    else
		      adaptacion_particular[i]=-aux;



		    if (aux>0) {
		      if (nnn[0][0]==clase){
		        if ((aux>grado_p[pos]) || ((aux==grado_p[pos]) && (peso[i]>peso_p[pos]))){
		          grado_p[pos]=aux;
		          peso_p[pos]=peso[i]; 
		          indice_pos=i;
		        }
		      }
		      else {
		        if ((aux>grado_n[pos]) || ((aux==grado_n[pos]) && (peso[i]>peso_n[pos]))){
		          grado_n[pos]=aux;
		          peso_n[pos]=peso[i];
		          indice_neg=i;
		        } 
		      }
		    }


		    if (aux>max){
		     max=aux;
		     re=i;
		     grado = aux;
		     if (nnn[0][0]==clase)
		       clasifica_bien=true;
		     else
		       clasifica_bien=false;
		    }
		    else if (re!=-1 && aux>0 && aux==max && peso[i]>peso[re]) {
		           max=aux;
		           re=i;
		           grado = aux;
		           if (nnn[0][0]==clase)
		             clasifica_bien=true;
		           else
		            clasifica_bien=false;
		         }
		 }


		 if (clasifica_bien){
		   de_clase=0;
		   for (int i=0; i<n_rule;i++)
		     if (adaptacion_particular[i]>0 && 
		        ((adaptacion_particular[i]>grado_n[pos]) || 
		        (adaptacion_particular[i]==grado_n[pos] && peso[i]>peso_n[pos]) ||
		        (adaptacion_particular[i]==grado_n[pos] && peso[i]==peso_n[pos] && i<indice_neg)) )
		       de_clase++;
		   if (de_clase==0){      
		      System.out.println ("Error!");
		   }
		     
		   aislada[pos]=de_clase;
		 }
		 else {
		   if (grado_p[pos]==0)
		     de_noclase=-1000;
		   else {
		     de_noclase=0;
		     for (int i=0; i<n_rule; i++)
		       if (adaptacion_particular[i]<0 && 
		          ((-adaptacion_particular[i]>grado_p[pos]) || 
		          (-adaptacion_particular[i]==grado_p[pos] && peso[i]>peso_p[pos]) || 
		          (-adaptacion_particular[i]==grado_p[pos] && peso[i]==peso_p[pos] && i<indice_pos) ))
		         de_noclase--;
		   }

		   aislada[pos]=de_noclase;
		 }

		 


		 regla_disparada[pos]=re;

		 if (re!=-1) {
				n = rule[re][0].GetInteger1 ();
				nn = rule[re][0].GetInteger2 ();
				nnn = rule[re][0].GetInteger3 ();
				return nnn[0][0];
		 }
		 else
		   return -1;
	}
	


	
	
	public String SPrint (int i){
		String buffer = "";
		int b, n;
		int[] nb;
		int[] nn;
		int[][] nnn;
		char[][] mb;
		String regla;
		int n_ant;
		int r;
		int[] nr;
		double[][] mr;
		
		// extraer la componente binaria
		b = rule[i][0].GetBinary1 ();
		nb = rule[i][0].GetBinary2 ();
		mb = rule[i][0].GetBinary3 ();
		
		char[] s = new char[nb[0]];
		
		for (int j=0; j<nb[0]; j++)
			s[j]=mb[0][j];

		regla = new String (s);

		n = rule[i][0].GetInteger1 ();
		nn = rule[i][0].GetInteger2 ();
		nnn = rule[i][0].GetInteger3 ();
		
		r = rule[i][0].GetReal1 ();
		nr = rule[i][0].GetReal2 ();
		mr = rule[i][0].GetReal3 ();
		
		n_ant = domain[0].N_Antecedent ();
		
		int z = 0;
		int j = 0;
		int donde = 0;
		int n_casos = 0;
		char[] regla_aux;
		
		regla_aux = regla.toCharArray ();
		
		buffer += "IF\n";
		
		while (j<n_ant){	
			if (domain[0].IsActive (j) && domain[0].IsAntecedent (j)){	
				n_casos=domain[0].SizeDomain (j);
				for(z=0; z<n_casos && regla_aux[donde+z]=='1';z++);
				if (mr[0][j]>=mr[0][nr[0]-1] && z != n_casos){
					buffer += "\t";
					buffer += domain[0].SPrintVar (j);
					buffer += " = {";
					for(int t=donde; t<donde+n_casos;t++){
						if (regla_aux[t]=='1'){
							buffer += " ";
							buffer += domain[0].SPrintDomain (j,t-donde);
						}
					}
					buffer += "}\n";
				}

			}
			donde += n_casos;
			j++;
		}

		//Conclusion de la regla
		buffer += "THEN ";
		buffer += domain[0].SPrintVar (domain[0].Consequent ());
		buffer += " IS ";
		buffer += domain[0].SPrintDomain (domain[0].Consequent (), nnn[0][0]);
		buffer += "   W ";
		buffer += peso[i];
		buffer += "\n";

		return buffer;

	}
	
	

	
	
	public void SaveRuleInterpreted (char[] nomfich) throws IOException{
		OutputStream fich;
		String regla;
		byte[] buf;
		
		try {
			fich = new FileOutputStream (new String (nomfich));
		} catch(FileNotFoundException e) {
			System.out.println("Could not create file!");
			return;
		}
		
		System.out.println("Number of rules = "+ n_rule + "\n");
		
		for (int i=0; i<n_rule; i++){
			regla = SPrint (i);
			regla = regla+"\n";
			buf = regla.getBytes();
			fich.write(buf);
		}

		fich.close();
	}
	


	
	/**
	 * <p>
	 * Writes each rule of the rule base in file "fich".
	 * </p>
	 * @param fich String Name of the output file
     * @throws java.io.IOException if the file can not be read.
	 */
	
	public void SaveRuleInterpreted_append (String fich) throws IOException{
		String regla;
		byte[] buf;
		
		
		for (int i=0; i<n_rule; i++){
			regla = SPrint (i);
			regla = regla+"\n";
			Files.addToFile (fich, regla);
		}

	}
	
	

	
}
