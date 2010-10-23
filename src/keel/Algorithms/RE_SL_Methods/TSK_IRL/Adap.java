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

package keel.Algorithms.RE_SL_Methods.TSK_IRL;

import java.lang.Math;

class Adap {

  public double[] grado_pertenencia;
  public double EC, EL;
  public int n_ejemplos_positivos;
  public int[] indices_ep;
  public double[] cubrimiento;

  public double omega, K, epsilon;
  public double[] puntos;
  public double F, G, g, PC;
  public int EmparejaAnt;
  public int tipo_fitness;

  public MiDataset tabla;
  public BaseR base_reglas;

  public Adap(MiDataset training, BaseR base_r, double valor_omega,
              double valor_k, double valor_epsilon, int t_fitness) {
    int i;

    tabla = training;
    base_reglas = base_r;
    omega = valor_omega;
    K = valor_k;
    epsilon = valor_epsilon;
    tipo_fitness = t_fitness;

    indices_ep = new int[tabla.long_tabla];
    cubrimiento = new double[tabla.long_tabla];
    puntos = new double[tabla.n_var_estado];
    grado_pertenencia = new double[tabla.n_var_estado];
  }

  public static double Minimo(double x, double y) {
    if (x < y) {
      return (x);
    }
    else {
      return (y);
    }
  }

  public static double Maximo(double x, double y) {
    if (x > y) {
      return (x);
    }
    else {
      return (y);
    }
  }

  /* -------------------------------------------------------------------------
                                 FUNCION FITNESS
   ------------------------------------------------------------------------- */

  /* ------------------------- Criterios de reglas -------------------------- */

  /* -------------------------- Medida Local de Error ----------------------- */

  /* Calcula el grado de compatibilidad (Ri(ek)) de los antecedentes de la regla
    con el ejemplo */
  double AntecedenteCubreEjemplo(double[] cromosoma, double[] ejem) {
    int i, pos_individuo;
    double min;
    Difuso D = new Difuso();

    for (i = 0; i < tabla.n_var_estado; i++) {
      pos_individuo = tabla.n_var_estado + 3 * i;
      D.x0 = cromosoma[pos_individuo];
      D.x1 = cromosoma[pos_individuo + 1];
      D.x2 = cromosoma[pos_individuo + 1];
      D.x3 = cromosoma[pos_individuo + 2];
      D.y = 1;
      grado_pertenencia[i] = base_reglas.Fuzzifica(ejem[i], D);
    }

    min = 1;
    for (i = 0; i < tabla.n_var_estado; i++) {
      if (grado_pertenencia[i] < min) {
        min = grado_pertenencia[i];
      }
    }

    return (min);
  }


  double Alfa_Error(double[] Consecuentes) {
    int i, j;
    double suma, salida;

    for (i = 0, suma = 0.0; i < n_ejemplos_positivos; i++) {
      /* Proceso de inferencia con una unica regla */
      /* inicializo la salida al valor b. Aplico la tangente para
          obtener el valor real porque dicho valor esta codificado
          con el angular coding */
      salida = Math.tan(Consecuentes[tabla.n_var_estado]);
      for (j = 0; j < tabla.n_var_estado; j++) {
        salida += Math.tan(Consecuentes[j]) *
            tabla.datos[indices_ep[i]].ejemplo[j];
      }

      suma += tabla.datos[indices_ep[i]].nivel_cubrimiento *
          Math.pow(tabla.datos[indices_ep[i]].ejemplo[tabla.n_var_estado] -
                   salida, 2.0);
    }

    return (suma);
  }

  /* Errores Cuadratico y Lineal */
  public void Error_tra() {
    int i, j;
    double suma1, suma2, fuerza;

    for (j = 0, suma1 = suma2 = 0.0; j < tabla.long_tabla; j++) {
      fuerza = base_reglas.FLC_TSK(tabla.datos[j].ejemplo);
      suma1 +=
          Math.pow(tabla.datos[j].ejemplo[tabla.n_var_estado] - fuerza, 2.0);
      suma2 += Math.abs(tabla.datos[j].ejemplo[tabla.n_var_estado] - fuerza);
    }

    EC = suma1 / (double) tabla.long_tabla;
    EL = suma2 / (double) tabla.long_tabla;
  }

  /* Errores Cuadratico y Lineal */
  public void Error_tst(MiDataset tabla_tst) {
    int i, j;
    double suma1, suma2, fuerza;

    for (j = 0, suma1 = suma2 = 0.0; j < tabla_tst.long_tabla; j++) {
      fuerza = base_reglas.FLC_TSK(tabla_tst.datos[j].ejemplo);
      suma1 +=
          Math.pow(tabla_tst.datos[j].ejemplo[tabla.n_var_estado] - fuerza, 2.0);
      suma2 += Math.abs(tabla_tst.datos[j].ejemplo[tabla.n_var_estado] - fuerza);
    }

    EC = suma1 / (double) tabla_tst.long_tabla;
    EL = suma2 / (double) tabla_tst.long_tabla;
  }

  /* ---------------------------- Funcion fitness --------------------------- */




  /* Guardo en tabla.datos[j].nivel_cubrimiento el matching con los antecedentes de la regla. Si el ejemplo es positivo, es decir, su matching es mayor que 0, guardo su posicion en la estructura indices_ep*/
  public void ejemplos_positivos(double[] ant) {
    int i;

    n_ejemplos_positivos = 0;
    for (i = 0; i < tabla.long_tabla; i++) {
      tabla.datos[i].nivel_cubrimiento = AntecedenteCubreEjemplo(ant,
          tabla.datos[i].ejemplo);
      if (tabla.datos[i].nivel_cubrimiento > 0.0) {
        indices_ep[n_ejemplos_positivos] = i;
        n_ejemplos_positivos++;
      }
    }
  }

  public void cubrimiento(double[] ant) {
    int i;
    double RCE;

    for (i = 0; i < tabla.long_tabla; i++) {
      RCE = AntecedenteCubreEjemplo(ant, tabla.datos[i].ejemplo);
      cubrimiento[i] += RCE;
      tabla.datos[i].maximo_cubrimiento = Maximo(tabla.datos[i].
                                                 maximo_cubrimiento, RCE);

      if ( (cubrimiento[i] >= epsilon) && (tabla.datos[i].cubierto == 0)) {
        tabla.datos[i].cubierto = 1;
        tabla.no_cubiertos--;
      }
    }
  }

  /* ---------------------------- Funcion fitness --------------------------- */


  double eval_ml(double[] cromosoma) {
    return (Alfa_Error(cromosoma));
  }



  /* -------------------------------------------------------------------------
               Funciones comunes
   ------------------------------------------------------------------------- */

  /* salida */
  public String getSalidaObli(MiDataset tabla_datos) {
    int j;
    double fuerza;
    String salida;

    salida = "@data\n";
    for (j = 0; j < tabla_datos.long_tabla; j++) {
      fuerza = base_reglas.FLC_TSK(tabla_datos.datos[j].ejemplo);
      salida += (tabla_datos.datos[j]).ejemplo[tabla_datos.n_var_estado] + " " +
          fuerza + " " + "\n";
    }

    salida = salida.substring(0, salida.length() - 1);

    return (salida);
  }

}

