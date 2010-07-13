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

package keel.Algorithms.RE_SL_Methods.mogulHC;

class Adap {

  public static double omega, K;
  public double[] grado_pertenencia;
  public double[] puntos;
  public double F, G, g, PC;
  public int EmparejaAnt;
  public double EC, EL;
  public int tipo_fitness, tipo_nichos;

  public MiDataset tabla, tabla_tst;
  public BaseR base_reglas;

  public Adap(MiDataset training, BaseR base, double valor_omega,
              double valor_k, int fitness, int nichos) {
    tabla = training;
    base_reglas = base;

    omega = valor_omega;
    K = valor_k;
    tipo_fitness = fitness;
    tipo_nichos = nichos;

    puntos = new double[tabla.n_variables];
    grado_pertenencia = new double[tabla.n_variables];
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
  public double ReglaCubreEjemplo(double[] cromosoma, double[] ejem) {
    int i, pos_etiq;
    double minimo_ant, minimo_con;
    Difuso D = new Difuso();

    EmparejaAnt = 0;

    for (i = 0; i < tabla.n_variables; i++) {
      pos_etiq = tabla.n_variables + 3 * i;
      D.x0 = cromosoma[pos_etiq];
      D.x1 = cromosoma[pos_etiq + 1];
      D.x2 = D.x1;
      D.x3 = cromosoma[pos_etiq + 2];
      D.y = 1;
      grado_pertenencia[i] = base_reglas.Fuzzifica(ejem[i], D);
    }

    minimo_ant = 1;
    minimo_con = 1;
    for (i = 0; i < tabla.n_var_estado; i++) {
      if (grado_pertenencia[i] < minimo_ant) {
        minimo_ant = grado_pertenencia[i];
      }
    }

    if (minimo_ant > 0) {
      EmparejaAnt = 1;
    }

    for (i = tabla.n_var_estado; i < tabla.n_variables; i++) {
      if (grado_pertenencia[i] < minimo_con) {
        minimo_con = grado_pertenencia[i];
      }
    }

    return (Minimo(minimo_ant, minimo_con));
  }

  public void CriteriosReglas(double[] cromosoma) {
    /* Calculo de tres de los criterios de reglas empleados:
          - frecuencia de una regla [Her95]
          - grado de cubrimiento medio de ejemplos positivos [Her95]
          - penalizacion sobre los ejemplos negativos [Gon95]
          - penalizacion por sobreaprendizaje de ejemplos */
    int i, n_ejem_pos, n_ejem_neg, n_ejem_ya_cub;
    double RCE, frec_acumulada, SumaRCEpositivos, umbral;

    n_ejem_pos = n_ejem_neg = 0;
    frec_acumulada = SumaRCEpositivos = 0.0;

    /* Contabilizacion de la frecuencia total de cubrimiento, los ejemplos
       positivos y los negativos */
    for (i = 0; i < tabla.long_tabla; i++) {
      RCE = ReglaCubreEjemplo(cromosoma, tabla.datos[i].ejemplo);
      if (tabla.datos[i].cubierto == 0) {
        frec_acumulada += RCE;
        if (RCE >= omega) /* Si el ejemplo esta cubierto en el grado deseado */
            {
        /* omega, entonces se considera positivo */
          n_ejem_pos++;
          SumaRCEpositivos += RCE;
        }
      }
      if (RCE == 0.0 && EmparejaAnt > 0)
          /* Si estan cubiertas las entradas pero no las */
           {
        n_ejem_neg++; /* salidas, entonces es un ejemplo negativo. Estos */
      }
    }
    /* son determinados sobre todos los ejemplos. */

    /* Calculo de la frecuencia de una regla [Her95] */
    F = frec_acumulada / tabla.no_cubiertos;

    /* Calculo del grado de cubrimiento medio de ejemplos positivos [Her95] */
    if (n_ejem_pos > 0) {
      G = SumaRCEpositivos / (double) n_ejem_pos;
    }
    else {
      G = 0.0;
    }

    /* Calculo de la penalizacion sobre los ejemplos negativos [Gon95] */
    umbral = K * n_ejem_pos;
    /* Si el numero de negativos es menor o igual que el umbral, entonces no hay penalizacion*/
    if (n_ejem_neg <= umbral) {
      g = 1.0;
    }
    else {
      g = 1.0 / (n_ejem_neg - umbral + Math.exp(1.0));
    }

    /* Si es necesario, se calcula la penalizacion de sobreaprendizaje de
       ejemplos */
    if (tipo_fitness == 2) {
      n_ejem_ya_cub = 0;
      for (i = 0; i < tabla.long_tabla; i++) {
        RCE = ReglaCubreEjemplo(cromosoma, tabla.datos[i].ejemplo);

        /* Si el ejemplo ya estaba cubierto y la regla actual lo vuelve a
           cubrir se contabiliza para penalizar */
        if (tabla.datos[i].cubierto > 0 && RCE != 0) {
          n_ejem_ya_cub++;
        }

        /* Se efectua la penalizacion correpondiente */
        if (n_ejem_ya_cub <= umbral) {
          PC = 1.0;
        }
        else {
          PC = 1.0 / (n_ejem_ya_cub - umbral + Math.exp(1.0));
        }
      }
    }
  }

  /* --------------- Criterios especificos de la aplicacion ----------------- */

  /* Errores Cuadratico y Lineal */
  public void Error_tra() {
    int j;
    double suma1, suma2, fuerza;

    for (j = 0, suma1 = suma2 = 0.0; j < tabla.long_tabla; j++) {
      fuerza = base_reglas.FLC(tabla.datos[j].ejemplo);
      suma1 +=
          Math.pow(tabla.datos[j].ejemplo[tabla.n_var_estado] - fuerza, 2.0);
      suma2 += Math.abs(tabla.datos[j].ejemplo[tabla.n_var_estado] - fuerza);
    }

    EC = suma1 / (double) tabla.long_tabla;
    EL = suma2 / (double) tabla.long_tabla;
  }

  /* Errores Cuadratico y Lineal TEST */
  public void Error_tst(MiDataset tabla_tst) {
    int j;
    double suma1, suma2, fuerza;

    for (j = 0, suma1 = suma2 = 0.0; j < tabla_tst.long_tabla; j++) {
      fuerza = base_reglas.FLC(tabla_tst.datos[j].ejemplo);
      suma1 +=
          Math.pow(tabla_tst.datos[j].ejemplo[tabla_tst.n_var_estado] - fuerza,
                   2.0);
      suma2 +=
          Math.abs(tabla_tst.datos[j].ejemplo[tabla_tst.n_var_estado] - fuerza);
    }

    EC = suma1 / (double) tabla_tst.long_tabla;
    EL = suma2 / (double) tabla_tst.long_tabla;
  }

  /* ---------------------------- Funcion fitness --------------------------- */


  public double eval(double[] cromosoma) {
    if (tipo_fitness == 1) {
      return (eval_mulmodal(cromosoma));
    }
    else {
      return (eval_criterios(cromosoma));
    }
  }

  /* Fitness para el niching secuencial. La penalizacion del sobreaprendizaje se
    implementa con la penalizacion del algoritmo multimodal */
  public double eval_mulmodal(double[] cromosoma) {
    double fitness;

    /* Se calcula la adecuacion de la regla codificada en el cromosoma actual, se
     estudia la posible penalizacion del mismo y se devuelve el valor final */
    CriteriosReglas(cromosoma);
    fitness = F * G * g * LNIR(cromosoma);

    return (fitness);
  }

  /* La penalizacion del sobreaprendizaje se implementa segun un criterio de reglas */
  public double eval_criterios(double[] cromosoma) {
    double fitness;

    /* Se calcula la adecuacion de la regla codificada en el cromosoma actual, se
     estudia la posible penalizacion del mismo y se devuelve el valor final */
    CriteriosReglas(cromosoma);
    fitness = F * G * g * PC;

    return (fitness);
  }

  /* -------------------------------------------------------------------------
                 Funciones de calculo del Ratio de Interaccion
   ------------------------------------------------------------------------- */


  /* Penalizacion debil: Penaliza la regla codificada en el cromosoma cuanto mas se acerque a los centros de las reglas anteriormente aprendidas (almacena dos en ListaTabu) */
  public double NIR1(double[] cromosoma) {
    int i;
    double cubr_act, max_cubr;

    /* Se obtiene el maximo grado en que la regla codificada en cromosoma cubre a los centros de las reglas anteriores (medida de proximidad de la regla actual a las anteriores aprendidas) */
    max_cubr = (double) 0.0;
    for (i = 0; i < base_reglas.n_reglas; i++) {
      cubr_act = ReglaCubreEjemplo(cromosoma, base_reglas.ListaTabu[i]);

      if (cubr_act > max_cubr) {
        max_cubr = cubr_act;
      }
    }

    return (max_cubr);
  }

  /* Penalizacion media 1: Penaliza la regla codificada en el cromosoma cuanto mas toque tanto los centros como el soporte de las reglas anteriormente aprendidas */
  public double NIR2(double[] cromosoma) {
    int i, j;
    double[] cubr_act = new double[3];
    double max_cubr;

    /* Se obtiene el maximo grado en que la regla codificada en cromosoma cubre a los centros y los soportes de las reglas anteriores (medida de proximidad de la regla actual a las anteriores aprendidas) */
    max_cubr = (double) 0.0;
    for (i = 0; i < base_reglas.n_reglas; i++) {
      /* Punto izquierdo */
      for (j = 0; j < tabla.n_variables; j++) {
        puntos[j] = base_reglas.BaseReglas[i][j].x0;
      }

      cubr_act[0] = ReglaCubreEjemplo(cromosoma, puntos);
      if (cubr_act[0] > max_cubr) {
        max_cubr = cubr_act[0];
      }

      /* Centro */
      for (j = 0; j < tabla.n_variables; j++) {
        puntos[j] = base_reglas.BaseReglas[i][j].x1;
      }

      cubr_act[1] = ReglaCubreEjemplo(cromosoma, puntos);
      if (cubr_act[1] > max_cubr) {
        max_cubr = cubr_act[1];
      }

      /* Punto derecho */
      for (j = 0; j < tabla.n_variables; j++) {
        puntos[j] = base_reglas.BaseReglas[i][j].x3;
      }
      cubr_act[2] = ReglaCubreEjemplo(cromosoma, puntos);
      if (cubr_act[2] > max_cubr) {
        max_cubr = cubr_act[2];
      }
    }

    return (max_cubr);
  }

  /* Penalizacion media 2: Penaliza la regla codificada en el cromosoma cuanto
    mas se acerque a los centros de las reglas anteriormente aprendidas (alma-
    cenados en ListaTabu) y cuanto mas se acerquen las otras al centro de
    esta */
  public double NIR3(double[] cromosoma) {
    int i, j;
    double cubr_act, max_cubr_otras, max_cubr_nueva, grado_pertenencia;

    /* Se obtiene el maximo grado en que la regla codificada en cromosoma cubre a
       los centros de las reglas anteriores (medida de proximidad de la regla
       actual a las anteriores aprendidas) */
    max_cubr_otras = (double) 0.0;
    for (i = 0; i < base_reglas.n_reglas; i++) {
      cubr_act = ReglaCubreEjemplo(cromosoma, base_reglas.ListaTabu[i]);
      if (cubr_act > max_cubr_otras) {
        max_cubr_otras = cubr_act;
      }
    }

    /* Se obtiene el maximo grado en que las reglas ya aprendidas cubren a la
       codificada en el cromosoma (medida de proximidad de las reglas anterior-
       mente aprendida a la actual) */
    max_cubr_nueva = (double) 0.0;

    for (i = 0; i < base_reglas.n_reglas; i++) {
      cubr_act = 1.0;
      for (j = 0; j < tabla.n_variables; j++) {
        grado_pertenencia = base_reglas.Fuzzifica(cromosoma[tabla.n_variables +
                                                  (3 * j) + 1],
                                                  base_reglas.BaseReglas[i][j]);
        if (grado_pertenencia < cubr_act) {
          cubr_act = grado_pertenencia;
        }
      }

      if (cubr_act > max_cubr_nueva) {
        max_cubr_nueva = cubr_act;
      }
    }

    return (Adap.Maximo(max_cubr_otras, max_cubr_nueva));
  }

  /* Penalizacion fuerte: Penaliza la regla codificada en el cromosoma cuanto
    mas toque tanto los centros como el soporte de las reglas anteriormente
    aprendidas y cuanto mas toquen las otras el centro y el soporte de esta */
  public double NIR4(double[] cromosoma) {
    int i, j;
    double[] cubr_act = new double[3];
    double max_cubr_otras, max_cubr_nueva, grado_pertenencia;

    /* Se obtiene el maximo grado en que la regla codificada en cromosoma cubre a
       los centros y los soportes de las reglas anteriores (medida de
       proximidad de la regla actual a las anteriores aprendidas) */
    max_cubr_nueva = (double) 0.0;
    for (i = 0; i < base_reglas.n_reglas; i++) {
      /* Punto izquierdo */
      for (j = 0; j < tabla.n_variables; j++) {
        puntos[j] = base_reglas.BaseReglas[i][j].x0;
      }

      cubr_act[0] = ReglaCubreEjemplo(cromosoma, puntos);
      if (cubr_act[0] > max_cubr_nueva) {
        max_cubr_nueva = cubr_act[0];
      }

      /* Centro */
      for (j = 0; j < tabla.n_variables; j++) {
        puntos[j] = base_reglas.BaseReglas[i][j].x1;
      }

      cubr_act[1] = ReglaCubreEjemplo(cromosoma, puntos);
      if (cubr_act[1] > max_cubr_nueva) {
        max_cubr_nueva = cubr_act[1];
      }

      /* Punto derecho */
      for (j = 0; j < tabla.n_variables; j++) {
        puntos[j] = base_reglas.BaseReglas[i][j].x3;
      }

      cubr_act[2] = ReglaCubreEjemplo(cromosoma, puntos);
      if (cubr_act[2] > max_cubr_nueva) {
        max_cubr_nueva = cubr_act[2];
      }
    }

    /* Se obtiene el maximo grado en que las reglas ya aprendidas cubren el
       soporte y los centros de la codificada en el cromosoma (medida de proxi-
       midad de las reglas anteriormente aprendida a la actual) */

    max_cubr_otras = (double) 0.0;
    for (i = 0; i < base_reglas.n_reglas; i++) {
      cubr_act[0] = cubr_act[1] = cubr_act[2] = 1.0;
      for (j = 0; j < tabla.n_variables; j++) {
        /* Punto izquierdo */
        grado_pertenencia = base_reglas.Fuzzifica(cromosoma[tabla.n_variables +
                                                  (3 * j)],
                                                  base_reglas.BaseReglas[i][j]);
        if (grado_pertenencia < cubr_act[0]) {
          cubr_act[0] = grado_pertenencia;
        }

        /* Centro */
        grado_pertenencia = base_reglas.Fuzzifica(cromosoma[tabla.n_variables +
                                                  (3 * j) + 1],
                                                  base_reglas.BaseReglas[i][j]);
        if (grado_pertenencia < cubr_act[1]) {
          cubr_act[1] = grado_pertenencia;
        }

        /* Punto derecho */
        grado_pertenencia = base_reglas.Fuzzifica(cromosoma[tabla.n_variables +
                                                  (3 * j) + 2],
                                                  base_reglas.BaseReglas[i][j]);
        if (grado_pertenencia < cubr_act[2]) {
          cubr_act[2] = grado_pertenencia;
        }
      }

      if (cubr_act[0] > max_cubr_otras) {
        max_cubr_otras = cubr_act[0];
      }
      if (cubr_act[1] > max_cubr_otras) {
        max_cubr_otras = cubr_act[1];
      }
      if (cubr_act[2] > max_cubr_otras) {
        max_cubr_otras = cubr_act[2];
      }
    }

    return (Adap.Maximo(max_cubr_otras, max_cubr_nueva));
  }

  /* -------------------------------------------------------------------------
               Criterio de penalizacion del Ratio de Interaccion
   ------------------------------------------------------------------------- */

  /* Se penaliza en funcion de la interaccion existente entre la regla actual
    y las anteriores. Cuanto menor sea dicha interaccion, menos penalizada es
    la regla (LNIR -> 1). Cuanto mayor sea, mas penalizada es (LNIR -> 0) */
  public double LNIR(double[] cromosoma) {
    double salida = 1;

    switch (tipo_nichos) {
      case 1:
        salida = 1 - NIR1(cromosoma);
        break;
      case 2:
        salida = 1 - NIR2(cromosoma);
        break;
      case 3:
        salida = 1 - NIR3(cromosoma);
        break;
      case 4:
        salida = 1 - NIR4(cromosoma);
        break;
    }

    return (salida);
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
      fuerza = base_reglas.FLC(tabla_datos.datos[j].ejemplo);
      salida += (tabla_datos.datos[j]).ejemplo[tabla_datos.n_var_estado] + " " +
          fuerza + " " + "\n";
    }

    salida = salida.substring(0, salida.length() - 1);

    return (salida);
  }

}

