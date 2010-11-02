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

import java.io.*;
import org.core.*;
import java.util.*;
import java.lang.Math;

class Mam2Tsk {
  public double semilla;

  public String fich_datos_chequeo, fich_datos_tst, fich_datos_val;
  public String fichero_conf, ruta_salida;
  public String fichero_br, fichero_reglas, fich_tra_obli, fich_tst_obli;
  public String datos_inter = "";
  public String cadenaReglas = "";

  public MiDataset tabla, tabla_tst, tabla_val;
  public BaseR_TSK base_reglas;
  public Adap_M2TSK fun_adap;
  public Est_evol_M2TSK ee;

  public Mam2Tsk(String f_e) {
    fichero_conf = f_e;
  }

  private String Quita_blancos(String cadena) {
    StringTokenizer sT = new StringTokenizer(cadena, "\t ", false);
    return (sT.nextToken());
  }

  /* Reads the data of the configuration file */
  public void leer_conf() {
    int i, j;
    String cadenaEntrada, valor;
    int gen_ee, Mu, Landa, N_sigma, N_alfa;
    int Omega_x, Omega_sigma, Omega_alfa, Delta_x, Delta_sigma, Delta_alfa;

    // we read the file in a String
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

    fichero_br = ( (ficheros.nextToken()).replace('\"', ' ')).trim(); //BR del anterior
    String aux = ( (ficheros.nextToken()).replace('\"', ' ')).trim(); //BD
    fichero_reglas = ( (ficheros.nextToken()).replace('\"', ' ')).trim(); //Nueva BR
    aux = ( (ficheros.nextToken()).replace('\"', ' ')).trim(); //BR de seleccion
    aux = ( (ficheros.nextToken()).replace('\"', ' ')).trim(); //BR de Tuning
    ruta_salida = fich_tst_obli.substring(0, fich_tst_obli.lastIndexOf('/') + 1);


    // we read the seed of the random generator
    sT.nextToken();
    valor = sT.nextToken();
    semilla = Double.parseDouble(valor.trim());
    Randomize.setSeed( (long) semilla);

    for (i = 0; i < 8; i++) { //leo los 8 primeros parametros que son del método anterior MOGUL
      sT.nextToken(); //nombre parametro
      sT.nextToken(); //valor parametro
    }

    // we read the Evolutionary Strategy iterations
    sT.nextToken();
    valor = sT.nextToken();
    gen_ee = Integer.parseInt(valor.trim());

    // we read the Number of Parents for the Evolutionary Strategy (Mu)
    sT.nextToken();
    valor = sT.nextToken();
    Mu = Integer.parseInt(valor.trim());

    // we read the Number of offspring for the Evolutionary Strategy (Landa)
    sT.nextToken();
    valor = sT.nextToken();
    Landa = Integer.parseInt(valor.trim());

    // we read the Size of the Standar Deviation String (N_sigma)
    sT.nextToken();
    valor = sT.nextToken();
    N_sigma = Integer.parseInt(valor.trim());

    // we read the Size of the Angle String (N_alfa)
    sT.nextToken();
    valor = sT.nextToken();
    N_alfa = Integer.parseInt(valor.trim());

    // we read the Recombination Operator for the Solution String (Omega_x)
    sT.nextToken();
    valor = sT.nextToken();
    Omega_x = Integer.parseInt(valor.trim());

    // we read the Recombination Operator for the Deviation String (Omega_sigma)
    sT.nextToken();
    valor = sT.nextToken();
    Omega_sigma = Integer.parseInt(valor.trim());

    // we read the Recombination Operator for the Angle String (Omega_alfa)
    sT.nextToken();
    valor = sT.nextToken();
    Omega_alfa = Integer.parseInt(valor.trim());

    // we read the Number of Parents to recombine the Solution String (Delta_x)
    sT.nextToken();
    valor = sT.nextToken();
    Delta_x = Integer.parseInt(valor.trim());

    // we read the Number of Parents to recombine the Deviation String (Delta_sigma)
    sT.nextToken();
    valor = sT.nextToken();
    Delta_sigma = Integer.parseInt(valor.trim());

    // we read the Number of Parents to recombine the Angle String (Delta_alfa)
    sT.nextToken();
    valor = sT.nextToken();
    Delta_alfa = Integer.parseInt(valor.trim());

    // we create all the objects
    tabla = new MiDataset(fich_datos_chequeo, false);
    if (tabla.salir == false) {
      tabla_val = new MiDataset(fich_datos_val, false);
      tabla_tst = new MiDataset(fich_datos_tst, false);
      base_reglas = new BaseR_TSK(fichero_br, tabla, false);
      fun_adap = new Adap_M2TSK(tabla, tabla_tst, base_reglas);
      ee = new Est_evol_M2TSK(base_reglas, fun_adap, tabla, gen_ee, Mu, Landa,
                              N_sigma, N_alfa, Omega_x, Omega_sigma, Omega_alfa,
                              Delta_x, Delta_sigma, Delta_alfa);
    }
  }

  public void run() {
    int i, j, tmp;
    double ec_tra, el_tra, ec_tst, el_tst;

    /* We read the configutate file and we initialize the structures and variables */
    leer_conf();


    if (tabla.salir == false) {
      for (i = 0; i < base_reglas.n_reglas; i++) {
        /* we obtain the positive examples */
        fun_adap.ejemplos_positivos(i);

        /* we apply the strategy Evolution for learning the consequent */
        ee.EE_Mu_Landa();

        /* we store the rule in the RB */
        base_reglas.inserta_cons(i, ee.solucion(), fun_adap);
      }

      /* we calcule the MSEs */
      fun_adap.Error_tra();
      ec_tra = fun_adap.EC;
      el_tra = fun_adap.EL;

      fun_adap.Error_tst();
      ec_tst = fun_adap.EC;
      el_tst = fun_adap.EL;

      /* we write the RB */
      cadenaReglas = base_reglas.BRtoString();
      cadenaReglas += "\nECMtra: " + ec_tra + "  ELMtra: " + el_tra;
      cadenaReglas += "\nECMtst: " + ec_tst + "  ELMtst: " + el_tst;
      Fichero.escribeFichero(fichero_reglas, cadenaReglas);

      /* we write the obligatory output files*/
      String salida_tra = tabla.getCabecera();
      salida_tra += fun_adap.getSalidaObli(tabla_val);
      Fichero.escribeFichero(fich_tra_obli, salida_tra);

      String salida_tst = tabla_tst.getCabecera();
      salida_tst += fun_adap.getSalidaObli(tabla_tst);
      Fichero.escribeFichero(fich_tst_obli, salida_tst);

      /* we write the MSEs in specific files */
      Fichero.AnadirtoFichero(ruta_salida + "mam2tskcomunR.txt",
                              "" + base_reglas.n_reglas + "\n");
      Fichero.AnadirtoFichero(ruta_salida + "mam2tskcomunTRA.txt",
                              "" + ec_tra + "\n");
      Fichero.AnadirtoFichero(ruta_salida + "mam2tskcomunTST.txt",
                              "" + ec_tst + "\n");
    }
  }
}
