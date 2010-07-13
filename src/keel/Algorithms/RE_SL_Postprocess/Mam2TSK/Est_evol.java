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

package keel.Algorithms.RE_SL_Postprocess.Mam2TSK;
import java.lang.Math;
import org.core.*;


class Est_evol {

	public int n_gen_ee;
	public int Mu, Landa, n_sigma, n_alfa, n_total, nl_alfa, nm_alfa;
	public int Omega_x, Omega_sigma, Omega_alfa, Delta_x, Delta_sigma, Delta_alfa;
	public double Tau_0, Tau, Tau_1;
	public double [] Z;
	public int [] indices_seleccion;
	public int [] indices_recombinacion;
	public int [] ind_mayor;
	public int [] indices_ep;
	public Structure [] Padres;
	public Structure [] Hijos;

	public double Beta = 0.0873;
	public double Epsilon_sigma = 0.0001;
	/* parameter of the function "f" */
	public int q = 5;
	/* Percentage of the maximum matching with which a example is considerated in the initial population */
	public double porcentaje_h = 0.7;
	/* Individual Percentage of initial population generate with the 'a' values to 0 and a random 'b' value */
	public double porcentaje_Mu = 0.2; 
	/* Initial value of the sigmas */
	public double Valor_Inicial_Sigma = 0.001;

	public BaseR base_reglas;
	public Adap fun_adap;
	public MiDataset tabla;

	public Est_evol (BaseR base_r, Adap fun, MiDataset t, int n_gen, int m, int l, int n_sigm, int n_alf, int Omeg_x, int Omeg_sigma, int Omeg_alfa, int Delt_x, int Delt_sigma, int Delt_alfa) {
		int i;

		base_reglas = base_r;
		fun_adap = fun;
		tabla = t;

		n_gen_ee = n_gen;
		Mu = m;
		Landa = l;
		n_sigma = n_sigm;
		n_alfa = n_alf;
		Omega_x = Omeg_x;
		Omega_sigma = Omeg_sigma;
		Omega_alfa = Omeg_alfa;
		Delta_x = Delt_x;
		Delta_sigma = Delt_sigma;
		Delta_alfa = Delt_alfa;

		n_total = tabla.n_variables + n_sigma + n_alfa;
		Tau_0 = 1.0 / Math.sqrt (2 * tabla.n_variables);
		Tau = 1.0 / Math.sqrt (2 * Math.sqrt(tabla.n_variables));
		Tau_1 = 1.0 / Math.sqrt(tabla.n_variables);
		nl_alfa = tabla.n_variables + 1 - n_sigma;
		nm_alfa = tabla.n_variables - 1;

		Z = new double[tabla.n_variables];
		indices_seleccion = new int[Landa];
		indices_recombinacion = new int[(int) Adap.Maximo(Delta_x, Adap.Maximo (Delta_sigma,Delta_alfa))];
		ind_mayor = new int[tabla.long_tabla];
		indices_ep = new int[tabla.long_tabla];

		Padres = new Structure[Mu];
		Hijos = new Structure[Landa];

		for (i=0; i<Mu; i++)  Padres[i] = new Structure(n_total);
		for (i=0; i<Landa; i++)  Hijos[i] = new Structure(n_total);
	}


/* -------------------------------------------------------------------------
                         Recombination Process
  ----------------------------------------------- -------------------------- */

	private double signo (double x) {
		if (x >= 0.0)  return (1);
		else return (-1);
	}


	/** Rounds the generated value by the semantics */
	double Asigna (double val, double tope) {
		if (val>-1E-4 && val<1E-4)  return (0);
		if (val>tope-1E-4 && val<tope+1E-4)  return (tope);

		return (val);
	}


	/** Returns the 'a' values as little as we want */
	double f (double x, int y) {
		return (y * Math.PI/2 * Math.pow (x,q));
	}


	/** Generates the initial population of fathers */
	public void InicializaPadres () {
		int i, j, y, Mu_primer_grupo, pos_ep, total_mayor;
		double y_med, y_min, y_max, h_max, h_exigido, x;
		double imagen;

		/* we calculate the average, maximum and minimum high, and the matching with which a example is considerated in the initial population */
		y_med = y_min = y_max = tabla.datos[indices_ep[0]].ejemplo[tabla.n_var_estado];
		h_max = tabla.datos[indices_ep[0]].nivel_cubrimiento;

		for (i=1; i<fun_adap.n_ejemplos_positivos; i++) {
			if (tabla.datos[indices_ep[i]].ejemplo[tabla.n_var_estado] > y_max)
				y_max = tabla.datos[indices_ep[i]].ejemplo[tabla.n_var_estado];
			if (tabla.datos[indices_ep[i]].ejemplo[tabla.n_var_estado] < y_min)
				y_min = tabla.datos[indices_ep[i]].ejemplo[tabla.n_var_estado];
			
			y_med += tabla.datos[indices_ep[i]].ejemplo[tabla.n_var_estado];
			if (tabla.datos[indices_ep[i]].nivel_cubrimiento > h_max)
				h_max = tabla.datos[indices_ep[i]].nivel_cubrimiento;
		}

		y_med /= fun_adap.n_ejemplos_positivos;
		h_exigido = porcentaje_h * h_max;

		/* Inicialization of a individual with 'b' value same as the average high and with the 'a' values to 0 */
		for (j=0; j<tabla.n_var_estado; j++)  Padres[0].Gene[j] = 0;
		Padres[0].Gene[tabla.n_var_estado] = Math.atan(y_med);

		/* Inicialization of the porcentaje_Mu * Mu individuals with 'b' value equal to a random value in the rank [y_min,y_max] and with the 'a' values to 0 */
		Mu_primer_grupo = (int) (porcentaje_Mu * Mu + 1);
		for (i=1; i<Mu_primer_grupo; i++) {
			for (j=0; j<tabla.n_var_estado; j++)  Padres[i].Gene[j] = 0;
			Padres[i].Gene[tabla.n_var_estado] = Math.atan(Randomize.Randdouble (y_min,y_max));
		}

		/* Inicialization of the remaining individuals with the random 'a' values and with a the 'b' value for any to example is in the plane */
		for (i=Mu_primer_grupo; i<Mu; i++) {
			for (j=0; j<tabla.n_var_estado; j++) {
				if (Randomize.Rand ()<.5)  y = -1;
				else  y=1;

				x = Randomize.Rand ();
				Padres[i].Gene[j] = f(x,y);
			}
			
			/* we select randomly a example with a matching more high than "h_exigido" */
			for (total_mayor=pos_ep=0; pos_ep<fun_adap.n_ejemplos_positivos; pos_ep++)
				if (tabla.datos[indices_ep[pos_ep]].nivel_cubrimiento >= h_exigido)
					ind_mayor[total_mayor++] = pos_ep;
			
			if (total_mayor==0) {
				System.out.println("Error: The matching, with which a example is considerated in the initial population, isn't surmounted");
			}
			
			pos_ep = ind_mayor[Randomize.RandintClosed (0,total_mayor-1)];
			for (imagen=0.0,j=0; j<tabla.n_var_estado; j++)
				imagen += Math.tan (Padres[i].Gene[j]) * tabla.datos[indices_ep[pos_ep]].ejemplo[j];
			
			Padres[i].Gene[tabla.n_var_estado] = Math.atan(tabla.datos[indices_ep[pos_ep]].ejemplo[tabla.n_var_estado]-imagen);
		}


		/* Inicialization of the vector of tipical desviations */
		for (i=0; i<Mu; i++)
			for (j=tabla.n_variables; j<tabla.n_variables+n_sigma; j++)  Padres[i].Gene[j] = Valor_Inicial_Sigma;

		/* Inicialization of the vector of angles: arcotangente of 1.0 */
		for (i=0; i<Mu; i++)
			for (j=tabla.n_variables + n_sigma; j<tabla.n_variables+n_sigma+n_alfa; j++)  
				Padres[i].Gene[j] = Math.atan (1.0);
	}



	/** Recombination Operators */
	private double OperadoresRecombinacion (int omega, int delta, int pos) {
		double u, suma, salida;
		int i, padre1, padre2;

		switch (omega) {
			/* Intermediate Global Recombination */
			case 1: for (suma=0.0,i=0;i<delta;i++)
						suma += Padres[indices_recombinacion[i]].Gene[pos];
					
					salida=suma/delta;
					break;

			/* Intermediate Local Recombination */
			case 2: padre1=Randomize.RandintClosed (0,delta-1);
					padre2=Randomize.RandintClosed (0,delta-1);
					u = Randomize.Rand ();
					salida = u * Padres[indices_recombinacion[padre1]].Gene[pos] + (1-u)*Padres[indices_recombinacion[padre2]].Gene[pos];
					break;

			/* Tactfully Recombination */
			default: padre1 = Randomize.RandintClosed (0,delta-1);
					salida = Padres[indices_recombinacion[padre1]].Gene[pos];
					break;
		}
		
		return (salida);
	}


	/** Returns 1 if the current index is in the list of selected indexes */
	private int Pertenece (int [] indices_seleccionados, int ind_act, int n_seleccionados) {
		int i;

		i=0;
		while (i<n_seleccionados)
			if (indices_seleccionados[i]==ind_act)  return (1);
			else  i++;
		
		return (0);
	}


	/** Recombination process */
	private void Recombinacion () {
		int n_hijo, i, j, cadena, omega, delta, princ_cadena, fin_cadena, padre;

		for (n_hijo=0; n_hijo<Landa; n_hijo++) {
		/* Recombination of the individual's parties:
         cadena=0 -> solution vector x.
         cadena=1 -> standard deviation vector sigma.
         cadena=2 -> vector of inclination angles alfa.
         */
			for (cadena=0;cadena<=2;cadena++) {
				switch (cadena) {
					case 0: omega = Omega_x; 
							delta = Delta_x; 
							princ_cadena = 0;
							fin_cadena = tabla.n_variables; 
							break;

					case 1: omega = Omega_sigma; 
							delta = Delta_sigma; 
							princ_cadena = tabla.n_variables;
							fin_cadena = tabla.n_variables + n_sigma; 
							break;

					default: omega = Omega_alfa;
							delta = Delta_alfa; 
							princ_cadena = tabla.n_variables + n_sigma;
							fin_cadena = tabla.n_variables + n_sigma + n_alfa; 
							break;
				}

				/* we perform if the string isn't empty */
				if (princ_cadena!=fin_cadena) {
					/* we select the individual */

					/* we copy directly all if each individual is selected */
					if (delta==Mu)     
						for (i=0; i<delta; i++)  indices_recombinacion[i] = i;
					/* we select randomly the individuals if all individuals aren't selected */
					else {               
						for (i=0; i<delta; i++) 
							do
								indices_recombinacion[i]=Randomize.RandintClosed (0,delta-1);
							while (Pertenece (indices_recombinacion,indices_recombinacion[i],i)==1);
					}

					if (omega==0) {
						/* we select randomly a individual */
						padre=Randomize.RandintClosed (0,delta-1);
						for (j=princ_cadena; j<fin_cadena; j++)
							Hijos[n_hijo].Gene[j] = Padres[indices_recombinacion[padre]].Gene[j];
					}
					else
						/* we recombine the current string with the selected operator omega */
						for (j=princ_cadena; j<fin_cadena; j++)
							Hijos[n_hijo].Gene[j] = OperadoresRecombinacion (omega,delta,j);
				}
			}
		}
	}


/* -------------------------------------------------------------------------
			                     Mutation Process
  ------------------------------------------------------------------------- */

	/** Generates a normal value with hope 0 and tipical deviation "desv */
	private double ValorNormal (double desv) {
		double u1, u2;

		/* we generate 2 uniform values [0,1] */
		u1=Randomize.Rand ();
		u2=Randomize.Rand ();

		/* we calcules a normal value with the uniform values */
		return (desv * Math.sqrt (-2 * Math.log(u1)) * Math.sin (2*Math.PI*u2));
	}


	/* Mutation process of the generated son by means of the recombination */
	private void Mutacion () {
		int n_hijo, i, j, nq, n1, n2;
		double z0, z1, x1, x2, si, co;

		for (n_hijo=0; n_hijo<Landa; n_hijo++) {
			/* Mutation of sigma */
			if (n_sigma==1)  /* if we use only a sigma, the sigma is adapted with Tau_1 */
				Hijos[n_hijo].Gene[tabla.n_variables] *= ValorNormal (Tau_1);
			else {
				z0 = ValorNormal (Tau_0);
				for (i=tabla.n_variables; i<tabla.n_variables + n_sigma; i++) {
					z1 = ValorNormal (Tau);
					Hijos[n_hijo].Gene[i] *= Math.exp (z1+z0);

					/* The standard desviation is Epsilon_sigma if it becomes 0 */
					if (Hijos[n_hijo].Gene[i]==0.0)
						Hijos[n_hijo].Gene[i] = Epsilon_sigma;
				}
			}  
       
			/* Mutation of alfa */
			for (i = tabla.n_variables + n_sigma; i<tabla.n_variables + n_sigma + n_alfa; i++) {
				z0 = ValorNormal (Beta);
				Hijos[n_hijo].Gene[i] += z0;

				/* Si el valor mutado se sale del intervalo [-i,i], se proyecta
				circularmente el valor a dicho intervalo */
				if (Math.abs(Hijos[n_hijo].Gene[i])>i)
					Hijos[n_hijo].Gene[i] -= 2 *i * signo (Hijos[n_hijo].Gene[i]);
			}

			/* Mutation of x */
   
			/* we calculate the uncorrelated vector of mutations */
			for (i=0; i<tabla.n_variables; i++)
				if (tabla.n_variables + i < tabla.n_variables + n_sigma)
					Z[i] = ValorNormal (Hijos[n_hijo].Gene[tabla.n_variables+i]);
				else /* if there aren't more tipical desviations we use the latest */
					Z[i] = ValorNormal (Hijos[n_hijo].Gene[tabla.n_variables+n_sigma-1]);      
    
			/* Correlation of the vector if we use the angles */  
			if (n_alfa!=0) {
				nq = n_alfa;
				for (j=nl_alfa; j<=nm_alfa; ++j) {
					n1 = tabla.n_variables - j;
					n2 = tabla.n_variables;
					for (i=1; i<=j; ++i) {
						x1 = Z[n1-1];
						x2 = Z[n2-1];
						si = Math.sin(Hijos[n_hijo].Gene[tabla.n_variables + n_sigma + nq - 1]);
						co = Math.cos(Hijos[n_hijo].Gene[tabla.n_variables + n_sigma + nq - 1]);
						Z[n2-1] = x1*si + x2*co;
						Z[n1-1] = x1*co - x2*si;
						--n2;
						--nq;
					}
				} 
			}
    
			/* Final mutation of X */ 
			for (i=0; i<tabla.n_variables; i++) {
				Hijos[n_hijo].Gene[i] += Z[i];	

				if (Hijos[n_hijo].Gene[i] < -(Math.PI/2.0))
					Hijos[n_hijo].Gene[i] = -(Math.PI/2.0) + 1E-10;
				if (Hijos[n_hijo].Gene[i] > (Math.PI/2.0))
					Hijos[n_hijo].Gene[i] = (Math.PI/2.0) - 1E-10;
			}
		}
	}


/* -------------------------------------------------------------------------
			                 Selection Process
  ------------------------------------------------------------------------- */

	/** Selection process of the best Mu sons of the generated Landa sons */
	private void Seleccion () {
		int i, j, temp;

		/* we evaluate the Landa sons */
		for (i=0; i<Landa; i++)
			Hijos[i].Perf = fun_adap.eval (Hijos[i].Gene);

		/* we order the sons by mean of the bubble method */
		for (i=0; i<Landa; i++)  indices_seleccion[i]=i;

		for (i=0; i<Landa; i++)
			for (j=0; j<Landa-i-1; j++)
			if (Hijos[indices_seleccion[j+1]].Perf < Hijos[indices_seleccion[j]].Perf) {
				temp = indices_seleccion[j];
				indices_seleccion[j] = indices_seleccion[j+1];
				indices_seleccion[j+1] = temp;
			}

		/* we select the best Mu sons */
		for (i=0; i<Mu; i++)
			for (j=0; j<n_total; j++)
				Padres[i].Gene[j] = Hijos[indices_seleccion[i]].Gene[j];
	}




/* -------------------------------------------------------------------------
                      Evolution Strategy (Mu,Landa)
  ------------------------------------------------------------------------- */

	/** Main of the Evolution Strategy (Mu,Landa) */
	public void EE_Mu_Landa () {
		int i;

		InicializaPadres ();
		for (i=0; i<n_gen_ee; i++) {
			Recombinacion ();
			Mutacion ();
			Seleccion ();

		}
	}

	public double [] solucion () {
		return (Padres[0].Gene);
	}
}


