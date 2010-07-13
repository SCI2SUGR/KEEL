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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierMOGUL;

/**
 * <p>
 * @author Written by Jesus Alcala Fernandez (University of Granada) 01/01/2004
 * @author Modified by Francisco José Berlanga (University of Jaén) 09/12/2008 
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
import java.io.*;
import org.core.*;
import java.util.*;
import java.lang.Math;

class Mogul {
/**	
 * <p>
 * It learns the initial Rule Base (RB) and Data Base (DB)
 * </p>
 */
 	
  public int MaxRules;

  public int tipo_reglas, compa, repetidas;
  public double semilla, epsilon;

  public int[] indices_nc;
  public int[] Rule_act;

  public String fich_datos_chequeo, fich_datos_tst;
  public String fichero_conf, fichero_inf, ruta_salida;
  public String fichero_datos, fichero_reglas, fich_tra_obli, fich_tst_obli;
  public String datos_inter = "";
  public String cadenaRules = "";

  public Structure Padre, Hijo;
  public MyDataset tabla, tabla_tst;
  public RuleBase base_reglas;
  public DataBase base_datos;
  public Adap fun_adap;
  public T_FRM FRM;

    /**
     * <p>
     * Constructor
     * </p>
     * @param f_e String it is the filename of the configuration file.      
     */
  public Mogul(String f_e) {
    fichero_conf = f_e;
  }

    /**
     * <p>
     * Removes the blank spaces from a String
     * </p>
     * @return String The String without blank spaces
     */
  private String Remove_spaces(String cadena) {
    StringTokenizer sT = new StringTokenizer(cadena, "\t ", false);
    return (sT.nextToken());
  }

    /**
     * <p>
     * Reads the data of the configuration file
     * </p>
     */
  public void leer_conf() {
    int i, j, n_etiquetas, tipo_fitness;
    double omega, K;

    String cadenaEntrada, valor;

    // we read the file in a String
    cadenaEntrada = MyFile.ReadMyFile(fichero_conf);
    StringTokenizer sT = new StringTokenizer(cadenaEntrada, "\n\r=", false);

    // we read the algorithm's name
    sT.nextToken();
    sT.nextToken();

    // we read the name of the training and test files
    sT.nextToken();
    valor = sT.nextToken();

    StringTokenizer ficheros = new StringTokenizer(valor, "\t ", false);
    ficheros.nextToken();
    fich_datos_chequeo = ( (ficheros.nextToken()).replace('\"', ' ')).trim();
    fich_datos_tst = ( (ficheros.nextToken()).replace('\"', ' ')).trim();

    // we read the name of the output files
    sT.nextToken();
    valor = sT.nextToken();

    ficheros = new StringTokenizer(valor, "\t ", false);
    fich_tra_obli = ( (ficheros.nextToken()).replace('\"', ' ')).trim();
    fich_tst_obli = ( (ficheros.nextToken()).replace('\"', ' ')).trim();
    fichero_reglas = ( (ficheros.nextToken()).replace('\"', ' ')).trim(); //Initial RB
    fichero_inf = ( (ficheros.nextToken()).replace('\"', ' ')).trim(); //DB
    String aux = ( (ficheros.nextToken()).replace('\"', ' ')).trim(); //Simplification output BR
    aux = ( (ficheros.nextToken()).replace('\"', ' ')).trim(); //Tuning output RB
    ruta_salida = fichero_reglas.substring(0,
                                           fichero_reglas.lastIndexOf('/') + 1);

    // we read the seed of the random generator
    sT.nextToken();
    valor = sT.nextToken();
    semilla = Double.parseDouble(valor.trim());
    Randomize.setSeed( (long) semilla);

    // we read the number of labels
    sT.nextToken();
    valor = sT.nextToken();
    n_etiquetas = Integer.parseInt(valor.trim());

    // we read the omega parameter for the maching degree of the positive instances
    sT.nextToken();
    valor = sT.nextToken();
    omega = Double.parseDouble(valor.trim());

    // we read the K parameter for the percentage of allowed negative instances
    sT.nextToken();
    valor = sT.nextToken();
    K = Double.parseDouble(valor.trim());

    // we read the epsilon parameter for the minimun maching degree required to the KB
    sT.nextToken();
    valor = sT.nextToken();
    epsilon = Double.parseDouble(valor.trim());

    // we read if repeated rules are allowed in the population
    sT.nextToken();
    valor = sT.nextToken();
    repetidas = Integer.parseInt(valor.trim());

    // we read the type of rule
    sT.nextToken();
    valor = sT.nextToken();
    tipo_reglas = Integer.parseInt(valor.trim());

    // we read the type of compability among an example and a antecedent of a rule
    sT.nextToken();
    valor = sT.nextToken();
    compa = Integer.parseInt(valor.trim());

    FRM = new T_FRM();

    // we read the type of FRM
    sT.nextToken();
    valor = sT.nextToken();
    FRM.fagre = Integer.parseInt(valor.trim());

    // we read the param alfa, used in some FRMs
    sT.nextToken();
    valor = sT.nextToken();
    FRM.palfa = Double.parseDouble(valor.trim());

    // we read the param p, used in some FRMs
    sT.nextToken();
    valor = sT.nextToken();
    FRM.p = Double.parseDouble(valor.trim());

    // we read the param a, used in some FRMs
    sT.nextToken();
    valor = sT.nextToken();
    FRM.a = Double.parseDouble(valor.trim());

    // we read the param b, used in some FRMs
    sT.nextToken();
    valor = sT.nextToken();
    FRM.b = Double.parseDouble(valor.trim());

    // we read the type of fitness function
//                sT.nextToken();
//                valor = sT.nextToken();
//                tipo_fitness = Integer.parseInt(valor.trim());
    tipo_fitness = 1;

    // we create all the objects
    tabla = new MyDataset(fich_datos_chequeo, true);

    base_datos = new DataBase(n_etiquetas, tabla.n_inputs);

//                MaxRules = (new Double(Math.pow(n_etiquetas, tabla.n_inputs))).intValue();
    MaxRules = tabla.long_tabla;
    base_reglas = new RuleBase(MaxRules, base_datos, tabla, tipo_reglas);

    fun_adap = new Adap(tabla, base_reglas, FRM, tipo_reglas, compa);
    fun_adap.omega = omega;
    fun_adap.K = K;
    fun_adap.tipo_fitness = tipo_fitness;

    indices_nc = new int[tabla.long_tabla];
    Rule_act = new int[tabla.n_inputs];
    Padre = new Structure(base_reglas.n_genes, tabla.nClasses);
    Hijo = new Structure(base_reglas.n_genes, tabla.nClasses);

    for (i = 0; i < tabla.n_inputs; i++) {
      base_datos.n_etiquetas[i] = n_etiquetas;
      base_datos.extremos[i].min = tabla.extremos[i].min;
      base_datos.extremos[i].max = tabla.extremos[i].max;
    }
  }


    /**
     * <p>
     * It launches the algorithm
     * </p>
     */
  public void run() {
    int i, j, pos_individuo, encontrado, contador;
    double RCE, min_CR, min_CVR, porcen_tra, porcen_tst, PN, fitness;

    /* We read the configutate file and we initialize the structures and variables */
    leer_conf();

    /* we generate the semantics of the linguistic variables */
    base_datos.Semantic();

    /* we store the DB in the report file */
    String informe = "Initial Data Base: \n\n";
    for (i = 0; i < tabla.n_inputs; i++) {
      informe += "  Variable " + (i + 1) + ":\n";
      for (j = 0; j < base_datos.n_etiquetas[i]; j++) {
        informe += "    Label " + (j + 1) + ": (" +
            base_datos.BaseDatos[i][j].x0 + "," + base_datos.BaseDatos[i][j].x1 +
            "," + base_datos.BaseDatos[i][j].x3 + ")\n";
      }

      informe += "\n";
    }

    informe += "\n";
    MyFile.WriteMyFile(fichero_inf, informe);

    /* Inicialization of the counters */
    tabla.no_cubiertos = tabla.long_tabla;
    base_reglas.n_reglas = 0;

    /* Iterative Rule Learning */
    do {

      /* Generation of the better rule */
      Generate();

      //System.out.println("Number of rules generated: " + (base_reglas.n_reglas + 1));

      fun_adap.RulesCriteria(Padre);
      fitness = fun_adap.F * fun_adap.G * fun_adap.g;
      /*			if (fun_adap.tipo_fitness==1) {
                                      PN = fun_adap.LNIR (Padre.Gene);
                                      fitness *= PN;
                              }
                              else {
                                      fitness *= fun_adap.PC;
                              }
       */
      encontrado = 0;
      if (repetidas == 0) {
        /* We look if the rule was previously in the rules structure,
         in order to avoid repeated rules in the final RB. */
        for (i = 0; i < base_reglas.n_reglas && encontrado == 0; i++) {
          contador = 0;
          for (j = 0; j < tabla.n_inputs; j++) {
            pos_individuo = tabla.n_inputs + 3 * j;
            if ( (base_reglas.BaseReglas[i][j].x0 == Padre.Gene[pos_individuo]) &&
                (base_reglas.BaseReglas[i][j].x1 ==
                 Padre.Gene[pos_individuo + 1]) &&
                (base_reglas.BaseReglas[i][j].x3 ==
                 Padre.Gene[pos_individuo + 2])) {
              contador++;
            }
          }
          if (contador == tabla.n_inputs) {
            encontrado = 1;
          }
        }
      }

      if (encontrado == 0) {
        /* The rule is stored in the RB */
        base_reglas.insert_rule(Padre);
      }
      else {
        System.out.println("Repeated rule");
      }

      /* we calculate the matching degree of the rule with each example. the covered examples are marked */
      for (i = 0; i < tabla.long_tabla; i++) {
        RCE = fun_adap.RuleCoversExample(Padre, tabla.datos[i].ejemplo);
        tabla.datos[i].nivel_cubrimiento += RCE;
        tabla.datos[i].maximo_cubrimiento = Adap.Maximum(tabla.datos[i].
            maximo_cubrimiento, RCE);
        if ( (tabla.datos[i].nivel_cubrimiento >= epsilon) &&
            (tabla.datos[i].cubierto == 0)) {
          tabla.datos[i].cubierto = 1;
          tabla.no_cubiertos--;
        }
      }

      /* the multimodal GA finish when the condition is true */
    }
    while (StopCondition() == 0);
    System.out.println("Number of rules generated: " + (base_reglas.n_reglas + 1));

    /* we calculate the minimum and maximum matching */
    min_CR = 1.0;
    min_CVR = 10E37;
    for (i = 0; i < tabla.long_tabla; i++) {
      min_CR = Adap.Minimum(min_CR, tabla.datos[i].maximo_cubrimiento);
      min_CVR = Adap.Minimum(min_CVR, tabla.datos[i].nivel_cubrimiento);
    }

    /* we calcule the clasification percentaje on training */
    fun_adap.Clasification_accuracy(true, tabla);
    /* we calcule the clasification percentaje on test */
    tabla_tst = new MyDataset(fich_datos_tst, false);
    fun_adap.Clasification_accuracy(false, tabla_tst);

    porcen_tra = fun_adap.ClaTra;
    porcen_tst = fun_adap.ClaTst;

    /* we write the RB */
    cadenaRules = base_reglas.BRtoString();
    cadenaRules += "\n%Tra: " + porcen_tra + " %Tst: " + porcen_tst +
        "\nMinimun C_R: " + min_CR + " MSE CV_R: " + min_CVR + "\n";

    MyFile.WriteMyFile(fichero_reglas, cadenaRules);

    /* we write the obligatory output files*/
    String salida_tra = tabla.getHeader();
    salida_tra += fun_adap.ObligatoryOutputFile(tabla);
    MyFile.WriteMyFile(fich_tra_obli, salida_tra);

    String salida_tst = tabla_tst.getHeader();
    salida_tst += fun_adap.ObligatoryOutputFile(tabla_tst);
    MyFile.WriteMyFile(fich_tst_obli, salida_tst);

    /* we write the MSEs in specific files */
    MyFile.AddtoMyFile(ruta_salida + "MogulFScomunR.txt",
                            "" + base_reglas.n_reglas + "\n");
    MyFile.AddtoMyFile(ruta_salida + "MogulFScomunTRA.txt",
                            "" + porcen_tra + "\n");
    MyFile.AddtoMyFile(ruta_salida + "MogulFScomunTST.txt",
                            "" + porcen_tst + "\n");
  }

  /** Returns 1 if the best current rule is in the list "L" yet */
  private int Pertenece(int n_generadas) {
    int nreg, var, esta;

    nreg = 0;
    while (nreg < n_generadas) {
      esta = 1;
      var = 0;

      while ( (var < tabla.n_inputs) && (esta == 1)) {
        if (Rule_act[var] != base_reglas.Pob_reglas[nreg].Gene[var]) {
          esta = 0;
        }
        else {
          var++;
        }
      }

      if (esta == 1) {
        return (1);
      }
      nreg++;
    }

    return (0);
  }


    /**
     * <p>
     * Generates the best rule
     * </p>
     */  
  public void Generate() {
    int i, j, k, etiqueta, pos_individuo, n_reg_generadas, indice_mejor, clase;
    double grado_pertenencia, max_pert;

    /* we obtain the uncovered examples */
    i = j = 0;
    while ( (i < tabla.no_cubiertos) && (j < tabla.long_tabla)) {
      if (tabla.datos[j].cubierto == 0) {
        indices_nc[i] = j;
        i++;
      }
      j++;
    }

    /* we generate the best rule for each example */
    n_reg_generadas = 0;
    for (i = 0; i < tabla.no_cubiertos; i++) {
      /* Determination of the best label for each variable */
      for (j = 0; j < tabla.n_inputs; j++) {
        max_pert = 0;
        etiqueta = 0;
        for (k = 0; k < base_datos.n_etiquetas[j]; k++) {
          grado_pertenencia = base_reglas.Fuzzification(tabla.datos[indices_nc[i]].
              ejemplo[j], base_datos.BaseDatos[j][k]);
          if (grado_pertenencia > max_pert) {
            max_pert = grado_pertenencia;
            etiqueta = k;
          }
        }

        Rule_act[j] = etiqueta;
      }

      /* if the rule aren't in the set, it's insert */
      if (Pertenece(n_reg_generadas) == 0) {
        for (j = 0; j < tabla.n_inputs; j++) {
          etiqueta = Rule_act[j];
          pos_individuo = tabla.n_inputs + 3 * j;
          base_reglas.Pob_reglas[n_reg_generadas].Gene[j] = (double) etiqueta;
          base_reglas.Pob_reglas[n_reg_generadas].Gene[pos_individuo] =
              base_datos.BaseDatos[j][etiqueta].x0;
          base_reglas.Pob_reglas[n_reg_generadas].Gene[pos_individuo +
              1] = base_datos.BaseDatos[j][etiqueta].x1;
          base_reglas.Pob_reglas[n_reg_generadas].Gene[pos_individuo +
              2] = base_datos.BaseDatos[j][etiqueta].x3;
        }

        clase = (int) tabla.datos[indices_nc[i]].ejemplo[tabla.n_inputs];
        switch (tipo_reglas) {
          case 1:
            Consequent_type1(n_reg_generadas, clase);
            break;
          case 2:
            Consequent_type2Mean(n_reg_generadas, clase);
            break;
          case 3:
            Consequent_type3Mean(n_reg_generadas, clase);
        }

        n_reg_generadas++;
      }
    }

    /* we obtain the best rule */
    Padre.Perf = 0;
    indice_mejor = 0;
    for (i = 0; i < n_reg_generadas; i++) {
      base_reglas.Pob_reglas[i].Perf = fun_adap.eval(base_reglas.Pob_reglas[i]);

      if (base_reglas.Pob_reglas[i].Perf > Padre.Perf) {
        Padre.Perf = base_reglas.Pob_reglas[i].Perf;
        indice_mejor = i;
      }
    }

    for (i = 0; i < base_reglas.n_genes; i++) {
      Padre.Gene[i] = base_reglas.Pob_reglas[indice_mejor].Gene[i];
    }

    Padre.Perf = base_reglas.Pob_reglas[indice_mejor].Perf;

    switch (tipo_reglas) {
      case 1:
      case 2:
        Padre.Consecuente[0].clase = base_reglas.Pob_reglas[indice_mejor].
            Consecuente[0].clase;
        Padre.Consecuente[0].gcerteza = base_reglas.Pob_reglas[indice_mejor].
            Consecuente[0].gcerteza;
        break;
      case 3:
        for (j = 0; j < tabla.nClasses; j++) {
          Padre.Consecuente[j].clase = base_reglas.Pob_reglas[indice_mejor].
              Consecuente[j].clase;
          Padre.Consecuente[j].gcerteza = base_reglas.Pob_reglas[indice_mejor].
              Consecuente[j].gcerteza;
        }
    }
  }

    /**
     * <p>
     * Sets the class of the rule to "clase" and its certainty degree to 1.0
     * </p>
     * @param regla int The number of rule
     * @param clase int The class for the rule "regla"
     */ 
  public void Consequent_type1(int regla, int clase) {
    base_reglas.Pob_reglas[regla].Consecuente[0].clase = clase;
    base_reglas.Pob_reglas[regla].Consecuente[0].gcerteza = 1.0;
  }


    /**
     * <p>
     * Sets the class of the rule to "clase" and calculates its certainty degree
     * </p>
     * @param regla int The number of rule
     * @param clase int The class for the rule "regla"
     */ 
  public void Consequent_type2Mean(int regla, int clase) {
    int i, etiqueta, pos_individuo;
    double comp, sumaclase, total, cf;
    FuzzySet[] D = new FuzzySet[tabla.n_inputs];

    /* sumaclases collect the compatibility degree among the antecedent of the
       rule and the examples belonging to the class */
    sumaclase = 0.0;
    total = 0.0;

    for (i = 0; i < tabla.n_inputs; i++) {
      D[i] = new FuzzySet();

      pos_individuo = tabla.n_inputs + 3 * i;
      D[i].x0 = base_reglas.Pob_reglas[regla].Gene[pos_individuo];
      D[i].x1 = base_reglas.Pob_reglas[regla].Gene[pos_individuo + 1];
      D[i].x3 = base_reglas.Pob_reglas[regla].Gene[pos_individuo + 2];
      D[i].y = 1;
    }

    /* We calculate the sum by classes */
    for (i = 0; i < tabla.long_tabla; i++) {
      comp = fun_adap.MatchingDegree(tabla.datos[i].ejemplo, D);
      if (comp > 0.0) {
        if ( (int) tabla.datos[i].ejemplo[tabla.n_inputs] == clase) {
          sumaclase += comp;
        }
        total += comp;
      }
    }
    cf = sumaclase / total;
    base_reglas.Pob_reglas[regla].Consecuente[0].clase = clase;
    base_reglas.Pob_reglas[regla].Consecuente[0].gcerteza = cf;
  }


    /**
     * <p>
     * Sets the class of the rule to "clase" and calculates its certainty degree
     * </p>
     * @param regla int The number of rule
     * @param clase int The class for the rule "regla"
     */ 
  public void Consequent_type3Mean(int regla, int clase) {
    int i, j, pos, c, pos_individuo;
    double comp, total, certeza;
    ;
    int num_clases = tabla.nClasses;
    double[] suma_ejemplos = new double[num_clases];
    FuzzySet[] D = new FuzzySet[tabla.n_inputs];

    /* sumaclases collect the compatibility degree among the antecedent of the
      rule and the examples belonging to the class */
    for (i = 0; i < num_clases; i++) {
      suma_ejemplos[i] = 0.0;
    }

    total = 0.0;
    for (i = 0; i < tabla.n_inputs; i++) {
      D[i] = new FuzzySet();

      pos_individuo = tabla.n_inputs + 3 * i;
      D[i].x0 = base_reglas.Pob_reglas[regla].Gene[pos_individuo];
      D[i].x1 = base_reglas.Pob_reglas[regla].Gene[pos_individuo + 1];
      D[i].x3 = base_reglas.Pob_reglas[regla].Gene[pos_individuo + 2];
      D[i].y = 1;
    }

    /* We calculate the sum by classes */
    for (i = 0; i < tabla.long_tabla; i++) {
      comp = fun_adap.MatchingDegree(tabla.datos[i].ejemplo, D);
      if (comp > 0.0) {
        suma_ejemplos[ (int) tabla.datos[i].ejemplo[tabla.n_inputs]] += comp;
        total += comp;
      }
    }
    for (i = 0; i < num_clases; i++) {
      base_reglas.Pob_reglas[regla].Consecuente[i].clase = i;
      certeza = suma_ejemplos[i] / total;
      if (certeza > 0.0) {
        base_reglas.Pob_reglas[regla].Consecuente[i].gcerteza = certeza;
      }
      else {
        base_reglas.Pob_reglas[regla].Consecuente[i].gcerteza = 0.0;
      }
    }
  }

    /**
     * <p>
     * Criterion of stop
     * </p>
     * @return int Returns 1 if it is necessary to stop. 0 otherwise
     */  
  public int StopCondition() {
    if ( (tabla.no_cubiertos == 0) || (base_reglas.n_reglas == MaxRules)) {
      return (1);
    }
    else {
      return (0);
    }
  }

    /**
     * <p>
     * Returns the set of examples
     * </p>
     * @param train boolean If TRUE returns the training set of examples. If FALSE returns the test training set of examples
     * @return MyDatasets The set of examples
     */  
  public MyDataset getTable(boolean train) {
    if (train) {
      return tabla;
    }
    else {
      return tabla_tst;
    }
  }

}

