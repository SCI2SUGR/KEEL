/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

 package keel.Algorithms.RE_SL_Postprocess.Post_G_TS_LatAmp_FRBSs;
 /**
  * The class which contains the functions to do the decodification
  * @author Diana Arquillos
  *
  */

public class Base {
	private Difuso [][] B;
	private TipoIntervalo [] rangos;
	private Difuso [] Consecuentes;
	private int n_var_estado;
	private int n_reglas;
	private double [] GradoEmp;
	private int n_variables;//fichero entrada
	private double salidaPDEF;
	private int [][] Bregla;
	private Difuso [][] BDatos;
	private int [] index;
	private int [] n_etiquetas;
	private static int firstflag=1;
	/**
	 * It is the constructor of the class 
	 * @param variables it contains the number of variables
	 * @param reglas it contains the number of the rules
	 * @param nvarstado it contains the number of variables of state
	 * @param salida it contains the value of the exit
	 * @param base it contains the values of data base
	 */

	public Base(int variables,int reglas,int nvarstado,double salida,double []base){
		this.n_variables=variables;
		this.n_reglas =reglas;
		this.n_var_estado=nvarstado;
		this.salidaPDEF=salida;
		this.Bregla= new int [n_reglas][];
		for(int i=0;i<n_reglas;i++){
			this.Bregla[i] = new int[n_variables];
		}
		this.B = new Difuso[n_reglas][];
		for(int i1=0;i1<n_reglas;i1++){
			this.B[i1] = new Difuso[n_variables];

		}
		int m=0;
		for (int i=0;i<n_reglas;i++){
		
			for(int j=0;j<n_variables;j++){
				this.B[i][j] = new Difuso();

				this.B[i][j].setx0( base[m]);
				m++;
				this.B[i][j].setx1(base[m]);
				
				m++;
				this.B[i][j].setx3(base[m]);
				
				m++;
				this.B[i][j].setx2(this.B[i][j].x1());
				this.B[i][j].sety(1.0);
				this.B[i][j].setml(1.);
			}
		}
			if (this.salidaPDEF == 87654321)
				this.salidaPDEF = 87654321.001;
		   
			if (this.salidaPDEF == -1)
				this.salidaPDEF = 87654321;
		this.rangos = new TipoIntervalo[100];
		this.Consecuentes = new  Difuso [n_reglas];
		
		this.GradoEmp = new double [n_reglas];
		this.index = new int [n_reglas];
		this.n_etiquetas = new int [n_variables];
		 
		
		 this.BDatos = new Difuso[n_variables][];
		  for (int j=0; j<n_variables; j++){ 
			  this.BDatos[j] = null;
			  this.BDatos[j]=new Difuso[n_reglas];
			  this.n_etiquetas[j] = 0;
		  } 
	}
	
	/**
	 * It gets the euclidean distance
	 * @param entradas the vector which contains the input values 
	 * @param regla the value of the rule
	 * @return the distance
	 */
	double euclidea (double [] entradas, int regla) {

		int j;
		double eucl=0.;
		double aux1, aux2, aux3;
		for ( j=0; j<n_var_estado; j++){
			aux1=entradas[j]-B[regla][j].x1();
			aux2=rangos[j].max()-rangos[j].min();
			aux3= Math.pow(aux1/aux2,2);
			eucl += aux3;
		}
			return Math.sqrt(eucl);
	}
	/**
	 * It gives values to the vector of ranges
	 */
	void rango () {

		int i, j;
			for (j = 0; j < n_var_estado; j++) {
			rangos[j]= new TipoIntervalo();
			rangos[j].set_max(B[0][j].x1()); 
			rangos[j].set_min(B[0][j].x1());
		}

		for (i=1;i<n_reglas;i++)
			for (j=0;j<n_var_estado;j++) {
	         if (B[i][j].x1()<rangos[j].min())
	            rangos[j].set_min(B[i][j].x1());
	         if (B[i][j].x1()>rangos[j].max())
	            rangos[j].set_max(B[i][j].x1());
	      }
		 for (j=0;j<n_var_estado;j++)
			   	if ((rangos[j].max() - rangos[j].min()) <= 0)
			      	rangos[j].set_max( rangos[j].min() + 1);
	}
	/**
	 * it gets T-norm of the minimum like conjuntion operator
	 * @param entradas it is the vector for get the minimum
	 * @return the minimum
	 */
	
	int Min (double [] entradas)
	/* T-norma del Minimo como operador de conjuncion */
	{
	   int i, j, cubrimiento = 0;
	   double minimo, y;
	   for (i=0; i<n_reglas; i++) {
	      minimo = B[i][0].Fuzzifica (entradas[0]);
	      for (j=1; minimo!=0.0 && j<n_var_estado; j++) {
	         y = B[i][j].Fuzzifica (entradas[j]);
	         if (y<minimo) minimo = y;
	      }
	      GradoEmp[i]=minimo;
	      if (minimo > 0)
	      	cubrimiento = 1;
	   }
	   if ((cubrimiento == 0) && (n_reglas>0)) {
		EmpMasCercanas (entradas);
	      return 1;
	   }
	   return 0;
	}
	
	/**
	 * it matchs the rules nearer 
	 * @param entradas el vector sobre el cual se calcula
	 */
void EmpMasCercanas (double [] entradas) {

		   double [] ents;
		   ents = new double [100];
		   int i, j, first, second;
		   if (n_var_estado > 100)
		   		System.exit(1);
			rango(); 
		   first = 0;
			GradoEmp[0]=euclidea (entradas,0);
		   for (i=1; i<n_reglas; i++) {
		      GradoEmp[i]=euclidea (entradas,i);
				if (GradoEmp[i] < GradoEmp[first])
		         first = i;
		   }

		   for (j=0; j < n_var_estado; j++)
		   	if (entradas[j] <= B[first][j].x0())
		      	ents[j]=B[first][j].x0() + (B[first][j].x3()-B[first][j].x0())*0.5;
		      else
		      	if (entradas[j] >= B[first][j].x3())
		         	ents[j]=B[first][j].x3() - (B[first][j].x3()-B[first][j].x0())*0.5;
		         else
		         	ents[j]= entradas[j];

			if (first == 0)
		   	second = 1;
		   else
		   	second = 0;
			GradoEmp[second]= euclidea (ents,second);
		   for (i=second+1; i<n_reglas; i++) {
		      GradoEmp[i]=euclidea (ents,i);
				if (GradoEmp[i] < GradoEmp[second] && i != first)
					second = i;
		   }

			/* Si sus consecuentes son cercanos, mejor disparar la más cercana,
		      ya que su información será buena */
			if ((B[first][n_var_estado].x1() >= B[second][n_var_estado].x1() &&
					B[first][n_var_estado].x0() < B[second][n_var_estado].x3())
		      ||
		   	(B[first][n_var_estado].x1() <= B[second][n_var_estado].x1() &&
		   			B[first][n_var_estado].x3() > B[second][n_var_estado].x0())) {
		   	for (i=0; i<n_reglas; i++)
		      	GradoEmp[i]=0;
		      GradoEmp[first]=1;
		      return;
		   }

			/* En otro caso se desplaza el punto dentro de la zona de cubrimiento:
		      1) las coordenadas del punto que no están cubiertas por la regla más
		         cercana toman como valor el centro de la correspondiente etiqueta
		      2) Por otro lado, puesto que las salidas de las reglas más cercanas son muy
		         diferentes, en el caso de que esten muy cerca (y por lo tanto pueda haber
		         cubrimiento de ambas) la salida en dicha zona debe ser por interpolacion
		         de ambas. Por lo tanto, para evitar que la regla más cercana anule a
		         la segunda, el resto de coordenadas (las que ya tenían cubrimiento) se
		         acercan hacia la segunda regla. Para ello, las coordenadas se desplazan
		         hacia el extremo de la primera regla que este mas cercano a la segunda,
		         a un 10 por ciento de la longitud del soporte de dicha etiqueta
		      3) Se infiere de manera normal para dicho punto
		   */
		   for (j=0; j < n_var_estado; j++) {
		   	if (entradas[j] <= B[first][j].x0())
		      	ents[j]=B[first][j].x0() + (B[first][j].x3()-B[first][j].x0())*0.5;
		      else
		      	if (entradas[j] >= B[first][j].x3())
		         	ents[j]=B[first][j].x3() - (B[first][j].x3()-B[first][j].x0())*0.5;
		         else{
		         	if (entradas[j] <= B[second][j].x1()) {
		            	ents[j]=B[first][j].x3() - (B[first][j].x3()-B[first][j].x0())*0.1;
							if (ents[j] < entradas[j]) /* Evito que el punto se aleje */
		               	ents[j]= entradas[j];
		            }
		            else {
		            	double aux1 = B[first][j].x3()-B[first][j].x0();
		            	ents[j]=B[first][j].x0() + (aux1)*0.1;
		            	
		            	if (ents[j] > entradas[j]) /* Evito que el punto se aleje */
			               	ents[j]= entradas[j];
		            }
		         }	
		   }
		   
		   Min (ents);
		}
	/**
	 *It gets the t-min like implication operator
	 */
	void T_Min (){
	   int i;
	   		
	   
	   for (i = 0; i<n_reglas; i++) {
	      if (GradoEmp[i] != 0.0) {
	         if (GradoEmp[i] == 1.0){
	        	 
	            Consecuentes[i].set_difuso(B[i][n_variables-1]);
	         }
	            else {
	     	    	
	            Consecuentes[i].setx0(B[i][n_variables-1].x0());
	            Consecuentes[i].setx1(B[i][n_variables-1].x0()+
	            (B[i][n_variables-1].x1()-B[i][n_variables-1].x0())*GradoEmp[i]);
	            Consecuentes[i].setx2(B[i][n_variables-1].x3()+
	            (B[i][n_variables-1].x2()-B[i][n_variables-1].x3())*GradoEmp[i]);
	            Consecuentes[i].setx3(B[i][n_variables-1].x3());
	         }
	      }
	      Consecuentes[i].sety(GradoEmp[i]);
	   }
	}
	/**
	 * It gets the adjust gravity center 
	 * @return the result
	 */
	double WECOA ()
	/* Centro de gravedad ponderado por el matching */
	{
	   double num, den;
	   int i;
	   num = den = 0.0;

	   for (i=0; i<n_reglas; i++){
	      if (Consecuentes[i].y()!= 0.0) {
	 	    
	         num += B[i][n_variables-1].y() * GradoEmp[i] *
	               (Consecuentes[i].AreaTrapecioX()/Consecuentes[i].AreaTrapecio ());
	         den += B[i][n_variables-1].y() * GradoEmp[i];
	        
	   	}
   } if (salidaPDEF != 87654321 && den==0.0){
		   return salidaPDEF;
	   }
		if (den==0.0) {
				num=0.0;
	  		for (i=0;i<n_reglas;i++)
	   		num+=B[i][n_variables-1].x1();
	  		den = n_reglas;
	 	}
	   if (den==0.0)
	   	return (0.0);

	 	return (num/den);
	}
	/**
	 * It is the fuzzy controller
	 * @param Entrada a vector to gets the results
	 * @param n_reglas_total number of total rules
	 * @return the precise value
	 */
		double FLC (double [] Entrada, int n_reglas_total){

	   double sal;

	   if (firstflag==1) {
	   	GradoEmp = new double [n_reglas_total];
	   	Consecuentes = new Difuso[n_reglas_total];
	      firstflag = 0;//0
	      for(int i=0;i<n_reglas_total;i++)
			  Consecuentes[i]= new Difuso();
	   }
	   
	   if (Min (Entrada)>0) {
	 	    
		   T_Min ();
	      sal = WECOA();
       	return (sal);
		}
	   T_Min ();
	   return (WECOA ());
	}
		/**
		 * It do the decodification of the chromosome
		 * @param cromosoma it has the values of the chromosomes
		 * @param cromosomaA it has the values of the amplitude of the chromosomes
		 * @param cromosomaR it has the values for the number of rules of the chromosomes
		 * @param n_reglas_total it has the number of total rules
		 */
	void Decodifica (double [] cromosoma,double [] cromosomaA,int [] cromosomaR,int n_reglas_total)
	/* Pasa la Base de Conocimiento codificada en el cromosoma a una estructura
	   adecuada para inferir */
	{
		int i, j, k, pos;
		  double desplazamiento, variacionSoporte;

		  n_reglas = 0;
		  desplazamiento =0;
		  Difuso [][] baseaux;
		  baseaux= BDatos;
		  for (i=0; i<n_reglas_total; i++) {
		    if (cromosomaR[i]==1) {
		      for (j=0; j<n_variables; j++) {
		    	  B[n_reglas][j].setx0(BDatos[j][Bregla[index[i]][j]].x0());
		    	  B[n_reglas][j].setx1(BDatos[j][Bregla[index[i]][j]].x1());
		    	  B[n_reglas][j].setx2(BDatos[j][Bregla[index[i]][j]].x2());
		    	  B[n_reglas][j].setx3(BDatos[j][Bregla[index[i]][j]].x3());
		          B[n_reglas][j].sety(1.0);
					 
			    for (k=0, pos=0; k<j; k++)  pos += n_etiquetas[k];
			    pos += Bregla[index[i]][j];
			         
				if (Bregla[index[i]][j]==0)
			      desplazamiento = (cromosoma[pos]-0.5) * (baseaux[j][Bregla[index[i]][j]+1].x1() - baseaux[j][Bregla[index[i]][j]].x1());
				if (Bregla[index[i]][j]==(n_etiquetas[j]-1))
			      desplazamiento = (cromosoma[pos]-0.5) * (baseaux[j][Bregla[index[i]][j]].x1() - baseaux[j][Bregla[index[i]][j]-1].x1());
				if (Bregla[index[i]][j]>0 &&Bregla[index[i]][j]<(n_etiquetas[j]-1)) {
			      if ((cromosoma[pos]-0.5)<0)
				    desplazamiento = (cromosoma[pos]-0.5) * (baseaux[j][Bregla[index[i]][j]].x1() - baseaux[j][Bregla[index[i]][j]-1].x1());
			      else
				    desplazamiento = (cromosoma[pos]-0.5) * (baseaux[j][Bregla[index[i]][j]+1].x1() - baseaux[j][Bregla[index[i]][j]].x1());
			    }

				if (j<n_var_estado) {
				  variacionSoporte = (cromosomaA[pos]-0.5) * (baseaux[j][Bregla[index[i]][j]].x3() - baseaux[j][Bregla[index[i]][j]].x1());
				  variacionSoporte /= 2.0;
				}
				else  variacionSoporte = 0.0;

			    B[n_reglas][j].setx0(B[n_reglas][j].x0() + desplazamiento - variacionSoporte) ;
			    B[n_reglas][j].setx1( B[n_reglas][j].x1() + desplazamiento);
			    B[n_reglas][j].setx2( B[n_reglas][j].x1()); 
			    B[n_reglas][j].setx3( B[n_reglas][j].x3() + desplazamiento + variacionSoporte);
			  }
		      n_reglas++;
		    }
		  }  
	}
	public Difuso[][] getB() {
		return B;
	}
	public void setB(Difuso[][] b) {
		B = b;
	}
	public TipoIntervalo [] getRangos() {
		return rangos;
	}
	public void setRangos(TipoIntervalo [] rangos) {
		this.rangos = rangos;
	}
	public Difuso [] getConsecuentes() {
		return Consecuentes;
	}
	public void setConsecuentes(Difuso [] consecuentes) {
		Consecuentes = consecuentes;
	}
	public int getN_var_estado() {
		return n_var_estado;
	}
	public void setN_var_estado(int n_var_estado) {
		this.n_var_estado = n_var_estado;
	}
	public int getN_reglas() {
		return n_reglas;
	}
	public void setN_reglas(int n_reglas) {
		this.n_reglas = n_reglas;
	}
	public double [] getGradoEmp() {
		return GradoEmp;
	}
	public void setGradoEmp(double [] gradoEmp) {
		GradoEmp = gradoEmp;
	}
	public int getN_variables() {
		return n_variables;
	}
	public void setN_variables(int n_variables) {
		this.n_variables = n_variables;
	}
	public double getSalidaPDEF() {
		return salidaPDEF;
	}
	public void setSalidaPDEF(double salidaPDEF) {
		this.salidaPDEF = salidaPDEF;
	}
	public int [][] getBregla() {
		return Bregla;
	}
	public void setBregla(int [][] bregla) {
		Bregla = bregla;
	}
	public Difuso [][] getBDatos() {
		return BDatos;
	}
	public void setBDatos(Difuso[][] datos) {
		BDatos = datos;
	}
	public int [] getIndex() {
		return index;
	}
	public void setIndex(int [] index) {
		this.index = index;
	}
	public int [] getN_etiquetas() {
		return n_etiquetas;
	}
	public void setN_etiquetas(int [] n_etiquetas) {
		this.n_etiquetas = n_etiquetas;
	}
	public int getN_etiquetas(int pos) {
		return n_etiquetas[pos];
	}


	public void setN_etiquetas(int i, int j) {
		n_etiquetas [i]=j;
		
	}


	public void setIndex(int i, int i2) {
		index[i]=i2;
		
	}


	public int getIndex(int i) {
		
		return index[i];
	}


	public void setBregla(int i, int j, int k) {
		Bregla[i][j]=k;
		
	}


	public int[] getBregla(int index2) {
		
		return Bregla[index2];
	}


	public Object getBDatos(int j, int k) {
		
		return BDatos[j][k];
	}


	public void setBDatos(int j, int k, Difuso difuso) {
		BDatos[j][k]=new Difuso();
		BDatos[j][k].set_difuso(difuso);
		
	}


	public Difuso getB(int i, int j) {
		return B[i][j];
	}

	public double getBDatos_x0(int j, int k) {
		
		return BDatos[j][k].x0();
	}
	public double getBDatos_x1(int j, int k) {
		
		return BDatos[j][k].x1();
	}
	public double getBDatos_x3(int j, int k) {
		
		return BDatos[j][k].x3();
	}
	public int getBregla(int i, int j) {
		
		return Bregla[i][j];
	}
}

