package keel.Algorithms.RE_SL_Methods.MamIRLHC;
import java.io.*; 
import org.core.*;
import java.util.*;

class BaseR {

	public Difuso [][] BaseReglas;
	public Structure [] Pob_reglas;
	public int n_reglas, n_genes;

	public double [] GradoEmp;
	public Difuso [] Consecuentes;

	public double [][]ListaTabu;

	public MiDataset tabla;
	public BaseD base_datos;

	public BaseR (int MaxReglas, BaseD base, MiDataset t) {
		int i, j;

		tabla = t;
		base_datos = base;

		n_reglas = 0;
		n_genes = tabla.n_variables * 4;

		Pob_reglas = new Structure[tabla.long_tabla];
		for (i=0; i<tabla.long_tabla; i++)  Pob_reglas[i] = new Structure (n_genes);

		BaseReglas = new Difuso [MaxReglas][tabla.n_variables];
		Consecuentes = new Difuso[MaxReglas];

		for (i=0; i<MaxReglas; i++) {
			BaseReglas[i] = new Difuso [tabla.n_variables];
			Consecuentes[i] = new Difuso();

			for (j=0; j<tabla.n_variables; j++)	BaseReglas[i][j] = new Difuso();
		}

		GradoEmp = new double[MaxReglas];
		ListaTabu = new double[MaxReglas][tabla.n_variables];
	}


	/** Inserts a rule in the RB */
	public void inserta_regla (double [] regla) {
		int i, pos_etiq;

		for (i=0; i < tabla.n_variables; i++) {
			pos_etiq = tabla.n_variables + 3 * i;
     		ListaTabu[n_reglas][i] = regla[pos_etiq + 1];
     		BaseReglas[n_reglas][i].x0 = regla[pos_etiq];
	     	BaseReglas[n_reglas][i].x1 = regla[pos_etiq + 1];
     		BaseReglas[n_reglas][i].x2 = BaseReglas[n_reglas][i].x1;
     		BaseReglas[n_reglas][i].x3 = regla[pos_etiq + 2];
     		BaseReglas[n_reglas][i].y = 1.0;
     		BaseReglas[n_reglas][i].Nombre = base_datos.BaseDatos[i][(int)regla[i]].Nombre;
     		BaseReglas[n_reglas][i].Etiqueta = base_datos.BaseDatos[i][(int)regla[i]].Etiqueta;
     	}

		n_reglas++;
	}

/* -------------------------------------------------------------------------
						  Fuzzification Interface  
  ------------------------------------------------------------------------- */

	public double Fuzzifica (double X, Difuso D) {
		/* If X are not in the rank D, the degree is 0 */
		if ((X<D.x0) || (X>D.x3))  return (0);
		if (X<D.x1)  return ((X-D.x0)*(D.y/(D.x1-D.x0)));
		if (X>D.x2)  return ((D.x3-X)*(D.y/(D.x3-D.x2)));

		return (D.y);
	}


/* -------------------------------------------------------------------------
							Conjunction Operator 
  ------------------------------------------------------------------------- */

	/* T-norma Minimal */
	public void Min (double [] entradas) {
		int b, b2;
		double minimo, y;

		for (b=0; b<n_reglas; b++) {
			minimo = Fuzzifica (entradas[0], BaseReglas[b][0]);

			for (b2=1; b2 < tabla.n_var_estado; b2++) {
				y = Fuzzifica (entradas[b2], BaseReglas[b][b2]);
				if (y < minimo)  minimo=y;
			}

			GradoEmp[b] = minimo;
		}
	}


/* -------------------------------------------------------------------------
							Implication Operator 
  ------------------------------------------------------------------------- */

	public void T_Min () {
		int b;

		for (b=0; b<n_reglas; b++) {
			if (GradoEmp[b] != 0) {
				if (GradoEmp[b] == 1.0) {
					Consecuentes[b].x0 = BaseReglas[b][tabla.n_var_estado].x0;
					Consecuentes[b].x1 = BaseReglas[b][tabla.n_var_estado].x1;
					Consecuentes[b].x2 = BaseReglas[b][tabla.n_var_estado].x2;
					Consecuentes[b].x3 = BaseReglas[b][tabla.n_var_estado].x3;
				}
				else {
					Consecuentes[b].x0 = BaseReglas[b][tabla.n_var_estado].x0;
					Consecuentes[b].x1 = BaseReglas[b][tabla.n_var_estado].x0 + (BaseReglas[b][tabla.n_var_estado].x1 - BaseReglas[b][tabla.n_var_estado].x0)*GradoEmp[b];
					Consecuentes[b].x2 = BaseReglas[b][tabla.n_var_estado].x3 + (BaseReglas[b][tabla.n_var_estado].x2 - BaseReglas[b][tabla.n_var_estado].x3)*GradoEmp[b];
					Consecuentes[b].x3 = BaseReglas[b][tabla.n_var_estado].x3;
				}
			}

			Consecuentes[b].y = GradoEmp[b];
		}
	}


/* -------------------------------------------------------------------------
							Defuzzification Interface 
  ------------------------------------------------------------------------- */

	/** Functions to calculate the centre of gravity */
	public double AreaTrapecioX (double x0, double x1, double x2, double x3, double y) {
		double izq, centro, der;

		if (x1!=x0)  izq = (2*x1*x1*x1-3*x0*x1*x1+x0*x0*x0)/(6*(x1-x0));
		else  izq = 0;

		centro = (x2*x2-x1*x1) / 2.0;

		if (x3 != x2)  der = (2*x2*x2*x2-3*x3*x2*x2+x3*x3*x3) / (6*(x3-x2));
		else  der = 0;

		return (y * (izq + centro + der));
	}


	public double AreaTrapecio (double x0, double x1, double x2, double x3, double y) {
		double izq, centro, der;

		if (x1 != x0)  izq = (x1*x1-2*x0*x1+x0*x0)/(2*(x1-x0));
		else  izq = 0;

		centro = x2 - x1;

		if (x3 != x2)  der = (x3 * x3 - 2 * x3 * x2 + x2 * x2) / (2 * (x3 - x2));
		else  der = 0;

		return (y * (izq + centro + der));
	}



	/** Returns the centre of gravity weight by matching */
	public double WECOA () {
		double num, den;
		int i;

		num = 0;
		den = 0;
		for (i=0; i < n_reglas; i++)
			if (Consecuentes[i].y!=0) {
				num += GradoEmp[i] * (AreaTrapecioX (Consecuentes[i].x0,Consecuentes[i].x1, Consecuentes[i].x2,Consecuentes[i].x3,Consecuentes[i].y) / AreaTrapecio (Consecuentes[i].x0,Consecuentes[i].x1, Consecuentes[i].x2,Consecuentes[i].x3,Consecuentes[i].y));
				den += GradoEmp[i];
			}

		if (den != 0)  return (num / den);
		else  return ((base_datos.extremos[tabla.n_var_estado].max - base_datos.extremos[tabla.n_var_estado].min)/2.0);
	}



/* -------------------------------------------------------------------------
									Fuzzy Controller
  ------------------------------------------------------------------------- */

	/** Returns the ouput of the controller */
	public double FLC (double [] Entrada) {
		Min (Entrada);
		T_Min ();
		return (WECOA ());
	}





	/** RB to String */
	public String BRtoString () {
		int i, j;
		String cadena="";

		cadena += "Numero de reglas: " + n_reglas + "\n\n";
		for (i=0; i < n_reglas; i++) {
			for (j=0; j < tabla.n_variables; j++)
				cadena += "" + BaseReglas[i][j].x0 + " " + BaseReglas[i][j].x1 + " " + BaseReglas[i][j].x3 + "\n";

			cadena += "\n";
		}

		return (cadena);
	}
}
