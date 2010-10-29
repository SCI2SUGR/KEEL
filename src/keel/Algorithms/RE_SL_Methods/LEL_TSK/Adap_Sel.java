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

package keel.Algorithms.RE_SL_Methods.LEL_TSK;

import java.lang.Math;

class Adap_Sel {

  public double[] grado_pertenencia;
  public double medcb, mincb, tau, alfa;
  public double maxEC;
  public int radio_nicho, min_reglas, n_genes;
  public double EC, EL;
  public int tipo_fitness;
  public int cont_soluciones;
  ;
  public char[][] ListaTabu;

  public MiDataset tabla, tabla_tst;
  public BaseR_TSK base_reglas;
  public BaseR_TSK base_total;

  public Adap_Sel(MiDataset training, MiDataset test, BaseR_TSK base, BaseR_TSK base_t,
              int genes, double porc_radio_nicho, double porc_min_reglas,
              int n_soluciones, double valor_tau, double valor_alfa, int tipo) {
    int i;

    tabla = training;
    tabla_tst = test;
    base_reglas = base;
    base_total = base_t;
    n_genes = genes;

    tau = valor_tau;
    alfa = valor_alfa;
    tipo_fitness = tipo;
    cont_soluciones = 0;

    /* Calculo del radio del nicho */
    radio_nicho = (int) (porc_radio_nicho * n_genes);
    min_reglas = (int) (porc_min_reglas * (double) n_genes);

    maxEC = 0.0;
    for (i = 0; i < tabla.long_tabla; i++) {
      maxEC += Math.pow(tabla.datos[i].ejemplo[tabla.n_var_estado], 2.0);
    }

    maxEC /= 2.0;

    ListaTabu = new char[n_soluciones][n_genes];

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

  /* Calcula el grado de compatibilidad (Ri(ek)) de la regla con el ejemplo */
  public double ReglaCubreEjemplo(Difuso[] R, double[] ejem) {
    int i;
    double minimo;

    for (i = 0; i < tabla.n_var_estado; i++) {
      grado_pertenencia[i] = base_reglas.Fuzzifica(ejem[i], R[i]);
    }

    minimo = 1;
    for (i = 0; i < tabla.n_var_estado; i++) {
      if (grado_pertenencia[i] < minimo) {
        minimo = grado_pertenencia[i];
      }
    }

    return (minimo);
  }

  /* Calcula los grados de cubrimiento medio y minimo de la Base de Conocimiento
    sobre el conjunto de ejemplos  */
  public void Cubrimientos_Base() {
    int i, j;
    double RCE, cb;

    for (i = 0; i < tabla.long_tabla; i++) {
      tabla.datos[i].nivel_cubrimiento = 0.0;
      tabla.datos[i].maximo_cubrimiento = 0.0;
      for (j = 0; j < base_reglas.n_reglas; j++) {
        RCE = ReglaCubreEjemplo(base_reglas.BaseReglas[j].Ant, tabla.datos[i].ejemplo);
        tabla.datos[i].nivel_cubrimiento += RCE;
        tabla.datos[i].maximo_cubrimiento = Maximo(tabla.datos[i].maximo_cubrimiento, RCE);
      }
    }

    cb = 0;
    mincb = 10E37;
    for (i = 0; i < tabla.long_tabla; i++) {
      cb += tabla.datos[i].nivel_cubrimiento;
      if (tabla.datos[i].nivel_cubrimiento < mincb) {
        mincb = tabla.datos[i].nivel_cubrimiento;
      }
    }

    medcb = cb / (double) tabla.long_tabla;
  }

  /* ---------------------- Decodificacion del cromosoma -------------------- */

  /* Pasa la Base de Conocimiento codificada en el cromosoma a una estructura
    adecuada para inferir */
  void Decodifica(char[] cromosoma) {
    int i, j;

    base_reglas.n_reglas = 0;
    for (i = 0; i < base_total.n_reglas; i++) {
      if (cromosoma[i] == '1') {
        for (j = 0; j < tabla.n_var_estado; j++) {
          base_reglas.BaseReglas[base_reglas.n_reglas].Ant[j].x0 = base_total.BaseReglas[i].Ant[j].x0;
          base_reglas.BaseReglas[base_reglas.n_reglas].Ant[j].x1 = base_total.BaseReglas[i].Ant[j].x1;
          base_reglas.BaseReglas[base_reglas.n_reglas].Ant[j].x2 = base_total.BaseReglas[i].Ant[j].x2;
          base_reglas.BaseReglas[base_reglas.n_reglas].Ant[j].x3 = base_total.BaseReglas[i].Ant[j].x3;
          base_reglas.BaseReglas[base_reglas.n_reglas].Ant[j].y = base_total.BaseReglas[i].Ant[j].y;
        }

        for (j = 0; j < tabla.n_variables; j++) {
          base_reglas.BaseReglas[base_reglas.n_reglas].Cons[j] = base_total.BaseReglas[i].Cons[j];
        }

        base_reglas.n_reglas++;
      }
    }
  }

  /* --------------- Criterios especificos de la aplicacion ----------------- */

  /* Error Cuadratico */
  double ErrorCuadratico() {
    int i;
    double suma;

    for (i = 0, suma = 0.0; i < tabla.long_tabla; i++) {
      suma +=
          Math.pow(tabla.datos[i].ejemplo[tabla.n_var_estado] -
                   base_reglas.FLC_TSK(tabla.datos[i].ejemplo), 2.0);
    }

    return (suma / (double) tabla.long_tabla);
  }

  /* Errores Cuadratico y Lineal */
  void Error_tra() {
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
  void Error_tst() {
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

  double eval(char[] cromosoma) {
    if (tipo_fitness == 1) {
      return (eval_EC(cromosoma));
    }
    else {
      return (eval_EC_cubr(cromosoma));
    }
  }

  /* Funcion fitness empleada por Paco y Manolo: Se minimiza el error cuadratico
    siempre que la base posea un grado de cubrimiento minimo superior a tau. El
    cromosoma toma un valor maximo de fitness cuando la base que codifica no
    presenta el grado de cubrimiento exigido */
  double eval_EC(char[] cromosoma) {
    int i;
    double ec, fitness, Pen_nicho;

    /* Se calcula la adecuacion de la base de conocimiento codificada en el
       cromosoma actual, se estudia la posible penalizacion del mismo y se
       devuelve el valor final */
    Decodifica(cromosoma);
    Cubrimientos_Base();

    if (mincb >= tau && min_reglas <= base_reglas.n_reglas) {
      ec = ErrorCuadratico();
      Pen_nicho = P(cromosoma);

      if (Pen_nicho != 2.0) {
        fitness = ec * Pen_nicho;
      }
      /* Si el cromosoma codifica una solucion ya obtenida, se le da el peor valor de fitness posible */
      else {
        fitness = maxEC;
      }
    }
    else {
      fitness = maxEC;
    }

    return (fitness);
  }

  /* Funcion fitness que pondera el error cuadratico por la desviacion del grado
    de cubrimiento de la base con respecto al valor optimo 1 */
  double eval_EC_cubr(char[] cromosoma) {
    int i;
    double ec, fitness;

    /* Se calcula la adecuacion de la base de conocimiento codificada en el
       cromosoma actual, se estudia la posible penalizacion del mismo y se
       devuelve el valor final */
    Decodifica(cromosoma);
    Cubrimientos_Base();

    if (mincb >= tau && min_reglas <= base_reglas.n_reglas) {
      ec = ErrorCuadratico();
      fitness = (1 + Math.abs(1.0 - medcb)) * ec * P(cromosoma);
    }
    else {
      fitness = maxEC;
    }

    return (fitness);
  }

  /* -------------------------------------------------------------------------
                               Distancia de Hamming
   ------------------------------------------------------------------------- */

  /* Calculo de la distancia de Hamming entre c1 y c2 */
  int Hamming(char[] c1, char[] c2) {
    int i, d;

    d = 0;
    for (i = 0; i < n_genes; i++) {
      if (c1[i] != c2[i]) {
        d++;
      }
    }

    return (d);
  }

  /* -------------------------------------------------------------------------
        Funcion de penalizacion
   ------------------------------------------------------------------------- */

  /* Ley de la potencia. Recuerdese que estamos minimizando, con lo cual, el
    cromosoma que es penalizado ha de aumentar su fitness para no ser selec-
    cionado. Esto se hace calculando una variante de la ley de la potencia
    que aparece en [Beasley93] */
  double P(char[] cromosoma) {
    int i, dist, peor_dist;

    peor_dist = radio_nicho;
    for (i = 0; i < cont_soluciones; i++) {
      dist = Hamming(cromosoma, ListaTabu[i]);
      if (dist < peor_dist) {
        peor_dist = dist;
      }
    }

    /* Si el cromosoma se encuentra dentro de algun nicho en el que ya se ha
       hallado una solucion, entonces es penalizado. La penalizacion es maxima
     (=alfa) cuando el cromosoma estudiado ya fue seleccionado como solucion */
    if (peor_dist < radio_nicho) {
      return (2 - Math.pow(peor_dist / (double) radio_nicho, alfa));
    }

    /* Si no esta en ninguno de esos nichos, no se penaliza */
    return (1.0);
  }

  public void guardar_solucion(char[] cromosoma) {
    int i;

    for (i = 0; i < base_total.n_reglas; i++) {
      ListaTabu[cont_soluciones][i] = cromosoma[i];
    }

    cont_soluciones++;
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
