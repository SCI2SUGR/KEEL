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

import java.io.*;
import org.core.*;
import java.util.*;
import java.lang.Math;

class Lear_m3 {
  public int MaxReglas;
  public double semilla, epsilon;
  public long Gen, n_generaciones;

  public String fich_datos_chequeo, fich_datos_tst, fich_datos_val;
  public String fichero_conf, fichero_inf, ruta_salida;
  public String fichero_br, fichero_reglas, fich_tra_obli, fich_tst_obli;
  public String informe = "";
  public String cadenaReglas = "";

  public MiDataset tabla, tabla_tst, tabla_val;
  public BaseR base_reglas;
  public BaseD base_datos;
  public Adap fun_adap;
  public AG alg_gen;
  public Est_evol ee;

  public Lear_m3(String f_e) {
    fichero_conf = f_e;
  }

  private String Quita_blancos(String cadena) {
    StringTokenizer sT = new StringTokenizer(cadena, "\t ", false);
    return (sT.nextToken());
  }

  /** Reads the data of the configuration file */
  public void leer_conf() {
    int i, j;
    String cadenaEntrada, valor;
    double porc_pob_ee, cruce, mutacion, K, omega, valor_a, valor_b, sigma;
    int tipo_fitness, tipo_nichos, long_poblacion, n_gen_ee, n_etiquetas;

    // we read the file in a String
    informe = "";
    cadenaEntrada = Fichero.leeFichero(fichero_conf);
    StringTokenizer sT = new StringTokenizer(cadenaEntrada, "\n\r=", false);

    // we read the algorithm's name
    sT.nextToken();
    sT.nextToken();

    // we read the name of the training and test files
    sT.nextToken();
    valor = sT.nextToken();

    StringTokenizer ficheros = new StringTokenizer(valor, "\t ", false);
    fich_datos_chequeo = ( (ficheros.nextToken()).replace('\"', ' ')).trim();
    fich_datos_val = ( (ficheros.nextToken()).replace('\"', ' ')).trim();
    fich_datos_tst = ( (ficheros.nextToken()).replace('\"', ' ')).trim();

    // we read the name of the output files
    sT.nextToken();
    valor = sT.nextToken();

    ficheros = new StringTokenizer(valor, "\t ", false);
    fich_tra_obli = ( (ficheros.nextToken()).replace('\"', ' ')).trim();
    fich_tst_obli = ( (ficheros.nextToken()).replace('\"', ' ')).trim();
    fichero_reglas = ( (ficheros.nextToken()).replace('\"', ' ')).trim();
    fichero_inf = ( (ficheros.nextToken()).replace('\"', ' ')).trim();
    ruta_salida = fichero_reglas.substring(0,
                                           fichero_reglas.lastIndexOf('/') + 1);

    // we read the seed of the random generator
    sT.nextToken();
    valor = sT.nextToken();
    semilla = Double.parseDouble(valor.trim());
    Randomize.setSeed( (long) semilla);

    // we read the Number of Iterations
    sT.nextToken();
    valor = sT.nextToken();
    n_generaciones = Long.parseLong(valor.trim());

    // we read the Evolutionary Strategy Iterations
    sT.nextToken();
    valor = sT.nextToken();
    n_gen_ee = Integer.parseInt(valor.trim());

    // we read the Rate of the Population to which the ES is applied
    sT.nextToken();
    valor = sT.nextToken();
    porc_pob_ee = Double.parseDouble(valor.trim());

    // we read the Population Size
    sT.nextToken();
    valor = sT.nextToken();
    long_poblacion = Integer.parseInt(valor.trim());

    // we read the Parameter a
    sT.nextToken();
    valor = sT.nextToken();
    valor_a = Double.parseDouble(valor.trim());

    // we read the Parameter b
    sT.nextToken();
    valor = sT.nextToken();
    valor_b = Double.parseDouble(valor.trim());

    // we read the Omega parameter for the maching degree of the positive instances
    sT.nextToken();
    valor = sT.nextToken();
    omega = Double.parseDouble(valor.trim());

    // we read the K parameter for the percentage of allowed negative instances
    sT.nextToken();
    valor = sT.nextToken();
    K = Double.parseDouble(valor.trim());

    // we read the Epsilon parameter for the minimun maching degree required to the KB
    sT.nextToken();
    valor = sT.nextToken();
    this.epsilon = Double.parseDouble(valor.trim());

    // we read the Type of Niches
    sT.nextToken();
    valor = sT.nextToken();
    tipo_nichos = Integer.parseInt(valor.trim());

    // we read the Type of Fitness Function
    sT.nextToken();
    valor = sT.nextToken();
    tipo_fitness = Integer.parseInt(valor.trim());

    // we read the Cross Probability
    sT.nextToken();
    valor = sT.nextToken();
    cruce = Double.parseDouble(valor.trim());

    // we read the Mutation Probability
    sT.nextToken();
    valor = sT.nextToken();
    mutacion = Double.parseDouble(valor.trim());

    // we read the Number of Labels
    sT.nextToken();
    valor = sT.nextToken();
    n_etiquetas = Integer.parseInt(valor.trim());

    // we create all the objects
    tabla = new MiDataset(fich_datos_chequeo, true);
    tabla_val = new MiDataset(fich_datos_val, false);
    tabla_tst = new MiDataset(fich_datos_tst, false);
    base_datos = new BaseD(n_etiquetas, tabla.n_variables);

    for (i = 0; i < tabla.n_variables; i++) {
      base_datos.n_etiquetas[i] = n_etiquetas;
      base_datos.extremos[i].min = tabla.extremos[i].min;
      base_datos.extremos[i].max = tabla.extremos[i].max;
    }

    //MaxReglas = (new Double(Math.pow(n_etiquetas, tabla.n_var_estado))).intValue();
    MaxReglas = 10000;
    base_reglas = new BaseR(MaxReglas, base_datos, tabla);
    fun_adap = new Adap(tabla, base_reglas, omega, K, tipo_fitness, tipo_nichos);
    alg_gen = new AG(long_poblacion, tabla.n_variables, cruce, mutacion,
                     valor_a, valor_b, fun_adap);
    ee = new Est_evol(base_datos, fun_adap, alg_gen, porc_pob_ee, n_gen_ee);
  }

  public void run() {
    int i, j;
    double RCE, min_CR, min_CVR, ec, el, ec_tst, el_tst, PN, fitness;

    /* We read the configutate file and we initialize the structures and variables */
    leer_conf();

    if (tabla.salir == false) {
      /* we generate the semantics of the linguistic variables */
      base_datos.Semantica();

      /* we store the DB in the report file */
      informe += "Initial Data Base: \n\n";
      informe += base_datos.BDtoString() + "\n";
      Fichero.escribeFichero(fichero_inf, informe);

      /* Inicialization of the counter */
      base_reglas.n_reglas = 0;

      do {
        /* Generation of the initial population */
        alg_gen.Initialize(base_reglas, base_datos, tabla);
        Gen = 0;

        /* Evaluation of the initial population inicial */
        alg_gen.Evaluate();
        Gen++;

        /* Main of the genetic algorithm */
        do {
          /* Interchange of the new and old population */
          alg_gen.Intercambio();

          /* Selection by means of Baker */
          alg_gen.Select();

          /* Crossover */
          alg_gen.Cruce_MMA_Simple(tabla.n_variables);

          /* Mutation */
          alg_gen.Mutacion_Thrift_No_Uniforme(Gen, n_generaciones, base_datos);

          /* Elitist Selection */
          alg_gen.Elitist();

          /* Evaluation of the current population */
          alg_gen.Evaluate();

          /* Evolution Strategy */
          ee.Estrategia_Evolucion();

          /* we increment the counter */
          Gen++;
        }
        while (Gen <= n_generaciones);

        fun_adap.CriteriosReglas(alg_gen.solucion());
        fitness = fun_adap.F * fun_adap.G * fun_adap.g;
        if (fun_adap.tipo_fitness == 1) {
          PN = fun_adap.LNIR(alg_gen.solucion());
          fitness *= PN;
        }
        else {
          fitness *= fun_adap.PC;
        }

        /* The rule is stored in the RB */
        base_reglas.inserta_regla(alg_gen.solucion());

        /* we calculate the matching degree of the rule with each example. the covered examples are marked */
        for (i = 0; i < tabla.long_tabla; i++) {
          RCE = fun_adap.ReglaCubreEjemplo(alg_gen.solucion(),
                                           tabla.datos[i].ejemplo);
          tabla.datos[i].nivel_cubrimiento += RCE;
          tabla.datos[i].maximo_cubrimiento = Adap.Maximo(tabla.datos[i].
              maximo_cubrimiento, RCE);
          if ( (tabla.datos[i].nivel_cubrimiento >= epsilon) &&
              (tabla.datos[i].cubierto == 0)) {
            tabla.datos[i].cubierto = 1;
            tabla.no_cubiertos--;
          }
        }

        /* the GA finish when the condition is true */
      }
      while (Parada() == 0);

      /* we calculate the minimum and maximum matching */
      min_CR = 1.0;
      min_CVR = 10E37;
      for (i = 0; i < tabla.long_tabla; i++) {
        min_CR = Adap.Minimo(min_CR, tabla.datos[i].maximo_cubrimiento);
        min_CVR = Adap.Minimo(min_CVR, tabla.datos[i].nivel_cubrimiento);
      }

      /* we calcule the MSEs */
      fun_adap.Error_tra();
      ec = fun_adap.EC;
      el = fun_adap.EL;

      fun_adap.Error_tst(tabla_tst);
      ec_tst = fun_adap.EC;
      el_tst = fun_adap.EL;

      /* we write the RB */
      cadenaReglas = base_reglas.BRtoString();
      cadenaReglas += "\nMSEtra: " + ec + " MSEMtst: " + ec_tst + "\nMLEtra: " +
          el + " MLEtst: " + el_tst + "\nMinimum of C_R: " + min_CR +
          " Minimum of CV_R: " + min_CVR + "\n";

      Fichero.escribeFichero(fichero_reglas, cadenaReglas);

      /* we write the obligatory output files*/
      String salida_tra = tabla.getCabecera();
      salida_tra += fun_adap.getSalidaObli(tabla_val);
      Fichero.escribeFichero(fich_tra_obli, salida_tra);

      String salida_tst = tabla_tst.getCabecera();
      salida_tst += fun_adap.getSalidaObli(tabla_tst);
      Fichero.escribeFichero(fich_tst_obli, salida_tst);

      /* we write the MSEs in specific files */
      Fichero.AnadirtoFichero(ruta_salida + "MogulHCcomunR.txt",
                              "" + base_reglas.n_reglas + "\n");
      Fichero.AnadirtoFichero(ruta_salida + "MogulHCcomunTRA.txt",
                              "" + ec + "\n");
      Fichero.AnadirtoFichero(ruta_salida + "MogulHCcomunTST.txt",
                              "" + ec_tst + "\n");
    }
  }

  /** Criterion of stop */
  public int Parada() {
    if ( (tabla.no_cubiertos == 0) || (base_reglas.n_reglas == MaxReglas)) {
      //System.out.println("Cubiertos -> "+tabla.no_cubiertos);
      //System.out.println("Reglas -> "+base_reglas.n_reglas);
      return (1);
    }
    else {
      return (0);
    }
  }
}

