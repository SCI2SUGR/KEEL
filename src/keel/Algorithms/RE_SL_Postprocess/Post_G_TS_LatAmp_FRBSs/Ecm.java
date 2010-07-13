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
 * The class that contains the functions to do the mean square error(MSE)
 * @author Diana Arquillos
 *
 */

public class Ecm {
	private Base Bs;
	private int long_tabla_tra;//fichero entrada
	private int long_tabla_tst;
	private Ttabla[] tabla_tra;
	private Ttabla[] tabla_tst;
	private TipoIntervalo [] extremos;//unsigned
	
	/************************************************************/
	
	public Base base(){
		return Bs;
	}

	/************************************************************/

	public int num_reglas(){
		return Bs.getN_reglas();
	}

	/**
	 * Function which compares two vectors
	 * @param BR1 one vector which is compared
	 * @param BR2 one vector which is compared
	 * @return it returns an integer 1 or 0 to say if the first is smaller or not
	 */
	
	int MENOR (int []BR1, int[]BR2) {

		int j, igual=1, menor=0;

	   for (j=0; j<Bs.getN_variables()-1 && igual>0; j++)
			if (BR1[j] < BR2[j]) {
	      	menor = 1;
	      	igual = 0;
	      }
	      else
	      	if (BR1[j] > BR2[j])
	         	menor = igual = 0;

		return menor;
	}
	/**
	 * Constructor of the class
	 * @param long_tabla_tra it contains the size of the training table
	 * @param vtra it contains the input training data 
	 * @param stra it contains the output training data
	 * @param long_tabla_tst it contains the size of the test table
	 * @param vtest it contains the input training data 
	 * @param stest it contains the output training data 
	 * @param n_variables it contains the number of variables
	 * @param reglas it contains the number of rules
	 * @param var it contains the number of state variables
	 * @param sal it contains the defect exit value
	 * @param v it contains the values of data base
	 */
		
	public Ecm(int long_tabla_tra,double [][]vtra,double []stra,int long_tabla_tst,double [][]vtest,double [] stest,int n_variables,int reglas, int var, double sal,double[] v){
		Bs = new Base(n_variables,reglas, var, sal,v);
		tabla_tra = new Ttabla [long_tabla_tra];
		tabla_tst = new Ttabla [long_tabla_tst];
		this.long_tabla_tra=long_tabla_tra;
		this.long_tabla_tst=long_tabla_tst;
		
		for(int i=0;i< long_tabla_tra;i++){
			tabla_tra[i] = new Ttabla(n_variables);
		}
		for(int i=0;i<long_tabla_tra;i++){
			tabla_tra[i].ejemplo = new double[n_variables];
			for(int j=0;j<n_variables-1;j++){
				
				tabla_tra[i].ejemplo[j]=vtra[i][j];
				
			}
			
			tabla_tra[i].ejemplo[n_variables-1]=stra[i];
			
		}
		
		for(int i=0;i< long_tabla_tst;i++){
			tabla_tst[i] = new Ttabla(n_variables);
		}
		for(int i=0;i<long_tabla_tst;i++){
			tabla_tst[i].ejemplo = new double[n_variables];
			for(int j=0;j<n_variables-1;j++){
				
				tabla_tst[i].ejemplo[j]=vtest[i][j];
			}
			tabla_tst[i].ejemplo[n_variables-1]=stest[i];
			
		}
		extremos=new TipoIntervalo [n_variables];  
		 
			for (int i = 0; i < n_variables; i++) {
				extremos[i]=new TipoIntervalo();
				extremos[i].set_max( tabla_tra[0].ejemplo[i]);
				extremos[i].set_min ( tabla_tra[0].ejemplo[i]);
			}
			int i,j;
			for (i=0;i<long_tabla_tra;i++)
				for (j=0;j<Bs.getN_variables();j++) {
		         if (tabla_tra[i].ejemplo[j]<extremos[j].min())
		        	 extremos[j].set_min(tabla_tra[i].ejemplo[j]);
		         if (tabla_tra[i].ejemplo[j]>extremos[j].max())
		        	 extremos[j].set_max(tabla_tra[i].ejemplo[j]);
		      }

			this.Bs.setN_var_estado(Bs.getN_variables()-1);
			 for (i=0;i<Bs.getN_reglas();i++)
				 this.Bs.setIndex(i, i);
			 for (i=0;i<Bs.getN_variables();i++)
				 this.Bs.setN_etiquetas(i,0);
			
			 /* Extracción de la BD */
			int k;
			int  temp, num_etiq;
			double ancho;
		   Difuso Dtemp=new Difuso();
			   for (j=0; j<Bs.getN_variables(); j++){
			      for (i=0; i<Bs.getN_reglas(); i++) {
			         for (k=0; k<Bs.getN_etiquetas(j); k++){
			        	 if(Bs.getBDatos(j,k)!=null){
			            if (Bs.getB(i,j).x0()==((Difuso) Bs.getBDatos(j,k)).x0() && Bs.getB(i,j).x1()==((Difuso) Bs.getBDatos(j,k)).x1() &&
			                Bs.getB(i,j).x3()==((Difuso) Bs.getBDatos(j,k)).x3())
			               break;
			         }
			         }
			         if (k == Bs.getN_etiquetas(j)) { /* etiqueta nueva */
			        	 Bs.setN_etiquetas(j,Bs.getN_etiquetas(j)+1);
			        	 Bs.setBDatos(j,k,new Difuso());
			        	 ((Difuso) Bs.getBDatos(j,k)).setx0(Bs.getB(i,j).x0());
			        	 ((Difuso) Bs.getBDatos(j,k)).setx1(Bs.getB(i,j).x1());
			        	 ((Difuso) Bs.getBDatos(j,k)).setx3(Bs.getB(i,j).x3());
			        	 ((Difuso) Bs.getBDatos(j,k)).setx2(Bs.getB(i,j).x2());
			        	 ((Difuso) Bs.getBDatos(j,k)).sety(Bs.getB(i,j).y());
			        	 ((Difuso) Bs.getBDatos(j,k)).setml(Bs.getB(i,j).ml());
			         
			         }
			      }
			   }
			   /* Ordenacion de la BD */
			   for (j=0; j<Bs.getN_variables(); j++)
				   for (i=0;i<Bs.getN_etiquetas(j);i++)
				   	for (k=0;k<Bs.getN_etiquetas(j)-1-i;k++){
				   		if (((Difuso) ((Base) Bs).getBDatos(j,k+1)).x1()< ((Difuso) Bs.getBDatos(j,k)).x1()) {
				   			Dtemp.set_difuso((Difuso) Bs.getBDatos(j,k));
				   			((Difuso) Bs.getBDatos(j,k)).set_difuso((Difuso) Bs.getBDatos(j,k+1));
				   			((Difuso) Bs.getBDatos(j,k+1)).set_difuso(Dtemp);
				   		}
				   	}
				/* Completando la BD */
			   for (j=0; j<Bs.getN_variables(); j++) {
			   	Dtemp.set_difuso((Difuso) Bs.getBDatos(j,0));
			      ancho = (Dtemp.x3()-Dtemp.x0());
			      num_etiq = Bs.getN_etiquetas(j);

				   for (;extremos[j].min() < Dtemp.x0()+ancho*0.05;) {
					   		Bs.setN_etiquetas(j, Bs.getN_etiquetas(j)+1);
					   		Bs.setBDatos(j,Bs.getN_etiquetas(j)-1,new Difuso());
						   	 ((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1)).setx0( Dtemp.x1() - ancho);
					         ((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1)).setx1( Dtemp.x0());
					         ((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1)).setx2(Dtemp.x0());
					         ((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1)).setx3(Dtemp.x1());
					         Dtemp.set_difuso((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1));
					 
				  }

				   for (i=1;i<num_etiq;)
						if ((Dtemp.x1()-ancho*0.05 < ((Difuso) Bs.getBDatos(j,i)).x0()) && (Dtemp.x1()+ancho*0.05 > ((Difuso) Bs.getBDatos(j,i)).x0())) {
					   	Dtemp.set_difuso( (Difuso) Bs.getBDatos(j,i));
			      		ancho = (Dtemp.x3()-Dtemp.x0());
							i++;
			         }
			         else {
			        	 Bs.setN_etiquetas(j,Bs.getN_etiquetas(j)+1);
			        	Bs.setBDatos(j,Bs.getN_etiquetas(j)-1,new Difuso());
			            ((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1)).setx0(Dtemp.x1());
			            ((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1)).setx1(Dtemp.x3());
			            ((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1)).setx2(Dtemp.x3());
			            ((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1)).setx3(Dtemp.x1() + ancho);
			            Dtemp.set_difuso((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1));
 
			   		}

				   for (;extremos[j].max() > Dtemp.x3()-ancho*0.05;) {
						  
					   Bs.setN_etiquetas(j,Bs.getN_etiquetas(j)+1);
			 		  Bs.setBDatos(j,Bs.getN_etiquetas(j)-1,new Difuso()); 
			 		  ((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1)).setx0 (Dtemp.x1());
			          ((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1)).setx1 (Dtemp.x3());
			          ((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1)).setx2(Dtemp.x3());
			          ((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1)).setx3 (Dtemp.x1() + ancho);
			          Dtemp.set_difuso((Difuso) Bs.getBDatos(j,Bs.getN_etiquetas(j)-1));
			          
					 }
			   }


				/* Ordenacion de la BD */
			   for (j=0; j<Bs.getN_variables(); j++)
				   for (i=0;i<Bs.getN_etiquetas(j);i++)
				   	for (k=0;k<Bs.getN_etiquetas(j)-1-i;k++)
				   		if (((Difuso) Bs.getBDatos(j,k+1)).x1()< ((Difuso) Bs.getBDatos(j,k)).x1()) {
				   			Dtemp.set_difuso((Difuso) Bs.getBDatos(j,k));
				   			((Difuso) Bs.getBDatos(j,k)).set_difuso((Difuso) Bs.getBDatos(j,k+1));
				   			((Difuso) Bs.getBDatos(j,k+1)).set_difuso(Dtemp);
				      	}
			   
				/* Extracción de la BR */
			   for (j=0; j<Bs.getN_variables(); j++)
				      for (i=0; i<Bs.getN_reglas(); i++) {
				         for (k=0; k<Bs.getN_etiquetas(j); k++)
				            if (Bs.getB(i,j).x0()==((Difuso) Bs.getBDatos(j,k)).x0() && Bs.getB(i,j).x1()==((Difuso) Bs.getBDatos(j,k)).x1() &&
				                Bs.getB(i,j).x3()==((Difuso) Bs.getBDatos(j,k)).x3())
				               break;
				         
				         Bs.setBregla(i,j,k);//[i][j] = k;
				      }
		
				            
			   for (i=0; i<Bs.getN_reglas(); i++)
				   for (j=0; j<Bs.getN_reglas()-1-i; j++)
			   		if (MENOR (Bs.getBregla(Bs.getIndex(j+1)),Bs.getBregla(Bs.getIndex(j)))>0) {
			   			temp=Bs.getIndex(j);
			   			Bs.setIndex(j,Bs.getIndex(j+1));
			   			Bs.setIndex(j+1,temp);
			      	}
}

	/**
	 * It calculates the mean square error(MSE) of the training data
	 * @param cromosoma it contains the chromosome values
	 * @param cromosomaA it contains the chromosome of amplitude values
	 * @param cromosomaR it contains the chromosome of rules values
	 * @param n_reglas_total the number of total rules
	 * @return the mean square error
	 */

	public double ECM_tra (double [] cromosoma,double []cromosomaA, int[]cromosomaR ,int n_reglas_total){
	   int i;
	   double suma;
	
	   Bs.Decodifica(cromosoma,cromosomaA,cromosomaR,n_reglas_total);
	   for (i=0,suma=0.0; i<long_tabla_tra; i++){
		   double aux=Bs.FLC(tabla_tra[i].ejemplo, n_reglas_total);
	      suma += 0.5 * Math.pow (tabla_tra[i].ejemplo[Bs.getN_var_estado()]-aux,2.);
	   }
	   return (suma/(double)this.long_tabla_tra);
	}

	/**
	 * It calculates the mean square error(MSE) of the test data
	 * @param cromosoma it contains the chromosome values
	 * @param cromosomaA it contains the chromosome of amplitude values
	 * @param cromosomaR it contains the chromosome of rules values
	 * @param n_reglas_total the number of total rules
	 * @return the mean square error
	 */
	public double ECM_tst (double [] cromosoma, double []cromosomaA, int []cromosomaR,int n_reglas_total){
	   int i;
	   double suma;
	   
	   Bs.Decodifica(cromosoma,cromosomaA,cromosomaR,n_reglas_total);
		
	   for (i=0,suma=0.0; i<long_tabla_tst; i++){
	      suma += 0.5 * Math.pow (tabla_tst[i].ejemplo[Bs.getN_var_estado()]-Bs.FLC (tabla_tst[i].ejemplo,n_reglas_total),2.);
	   }
	   return (suma/(double)this.long_tabla_tst);
	}
	
	/**
	 * It evaluates the mean square error(MSE)
	 * @param cromosoma it contains the chromosome values
	 * @param cromosomaA it contains the chromosome of amplitude values
	 * @param cromosomaR it contains the chromosome of rules values
	 * @param n_reglas_total the number of total rules
	 * @return the mean square error
	 */
	
	public double eval_EC (double []cromosoma, double []cromosomaA, int []cromosomaR,int n_reglas_total)
	{
	   return (ECM_tra (cromosoma,cromosomaA,cromosomaR,n_reglas_total));
	}
}

