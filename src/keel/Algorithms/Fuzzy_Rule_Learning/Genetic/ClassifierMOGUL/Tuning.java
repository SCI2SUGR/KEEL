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

class Tuning {
/**	
 * <p>
 * It applies a tuning process to a previously learned RB
 * </p>
 */
 	
    public double semilla;
    public long cont_soluciones;
    public long Gen, n_genes, n_reglas, n_generaciones;
    public int n_soluciones, tipo_modificadores, compa, tipo_reglas,
    n_etiquetas;

    public String fich_datos_chequeo, fich_datos_tst;
    public String fichero_conf, ruta_salida;
    public String fichero_br, fichero_reglas, fich_tra_obli, fich_tst_obli;
    public String datos_inter = "";
    public String cadenaRules = "";

    public MyDataset tabla, tabla_tst;
    public RuleBase_Tun base_reglas;
    public Adap_Tun fun_adap;
    public GA_Tun alg_gen;
    public T_FRM FRM;
    double acc_max = 0.0;

    /**
     * <p>
     * Constructor
     * </p>
     * @param f_e String it is the filename of the configuration file.    
     * @param train MyDataset The set of training examples
     * @param train MyDataset The set of test examples     
     */
    public Tuning(String f_e, MyDataset train, MyDataset test) {
        fichero_conf = f_e;
        this.tabla = train;
        this.tabla_tst = test;
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
        int i, j;
        String cadenaEntrada, valor;
        double cruce, mutacion, a, b;
        int long_poblacion;

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
        fich_datos_chequeo = ((ficheros.nextToken()).replace('\"', ' ')).trim();
        fich_datos_tst = ((ficheros.nextToken()).replace('\"', ' ')).trim();
        //fichero_br = ((ficheros.nextToken()).replace('\"',' ')).trim();

        // we read the name of the output files
        sT.nextToken();
        valor = sT.nextToken();

        ficheros = new StringTokenizer(valor, "\t ", false);
        fich_tra_obli = ((ficheros.nextToken()).replace('\"', ' ')).trim();
        fich_tst_obli = ((ficheros.nextToken()).replace('\"', ' ')).trim();
        //fichero_reglas = ((ficheros.nextToken()).replace('\"',' ')).trim();
        String aux = ((ficheros.nextToken()).replace('\"', ' ')).trim(); //Initial RB
        aux = ((ficheros.nextToken()).replace('\"', ' ')).trim(); //DB
        fichero_br = ((ficheros.nextToken()).replace('\"', ' ')).trim(); //Simplification output BR
        fichero_reglas = ((ficheros.nextToken()).replace('\"', ' ')).trim(); //Tuning output RB

        ruta_salida = fich_tst_obli.substring(0,
                                              fich_tst_obli.lastIndexOf('/') + 1);

        // we read the seed of the random generator
        sT.nextToken();
        valor = sT.nextToken();
        semilla = Double.parseDouble(valor.trim());
        Randomize.setSeed((long) semilla); ;

        // we read the number of labels
        sT.nextToken();
        valor = sT.nextToken();
        n_etiquetas = Integer.parseInt(valor.trim());

        for (i = 0; i < 4; i++) {
            sT.nextToken(); //variable
            sT.nextToken(); //value
        }

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

        // we read the type of linguistic hedges
        sT.nextToken();
        valor = sT.nextToken();
        tipo_modificadores = Integer.parseInt(valor.trim());

        for (i = 0; i < 8; i++) {
            sT.nextToken(); //variable
            sT.nextToken(); //value
        }

        // we select the number of solutions
//                n_soluciones = 1;

        // we read the Number of Iterations
        sT.nextToken();
        valor = sT.nextToken();
        n_generaciones = Long.parseLong(valor.trim());

        // we read the Population Size
        sT.nextToken();
        valor = sT.nextToken();
        long_poblacion = Integer.parseInt(valor.trim());

        // we read the Parameter a
        sT.nextToken();
        valor = sT.nextToken();
        a = Double.parseDouble(valor.trim());

        // we read the Parameter b
        sT.nextToken();
        valor = sT.nextToken();
        b = Double.parseDouble(valor.trim());

        // we read the Cross Probability
        sT.nextToken();
        valor = sT.nextToken();
        cruce = Double.parseDouble(valor.trim());

        // we read the Mutation Probability
        sT.nextToken();
        valor = sT.nextToken();
        mutacion = Double.parseDouble(valor.trim());

        // we create all the objects
        /*tabla = new MyDataset(fich_datos_chequeo, true);
                         if (tabla.salir==false) {
                         base_reglas = new RuleBase(fichero_br, tabla);
                         fun_adap = new Adap(tabla, base_reglas);
                         alg_gen = new GA(long_poblacion, cruce, mutacion, a, b, porc_ind_ee, gen_ee, fun_adap, base_reglas);
                     }*/
        tabla.newTable();
        tabla_tst.newTable();
        base_reglas = new RuleBase_Tun(fichero_br, tabla, tipo_reglas,
                                    tipo_modificadores, n_etiquetas);
        fun_adap = new Adap_Tun(tabla, base_reglas, FRM, tipo_modificadores,
                                tipo_reglas, compa, n_etiquetas);
        alg_gen = new GA_Tun(long_poblacion, cruce, mutacion, a, b, fun_adap,
                             base_reglas, tabla.nClasses);
    }

    /**
     * <p>
     * It launches the algorithm
     * </p>
     */
    public void run() {
        int i, j;
        double porcen_tra, porcen_tst;

        /* We read the configutate file and we initialize the structures and variables */
        leer_conf();

        /* Generation of the initial population */
        Gen = 0;
        alg_gen.Initialize();

        /* Evaluation  of the initial population */
        alg_gen.Evaluate();
        Gen++;

        /* Main of the genetic algorithm */
        do {
            /* Interchange of the new and old population */
            alg_gen.Swap();

            /* Selection by means of Baker */
            alg_gen.Select();

            /* Crossover */
            alg_gen.Max_Min_Crossover();

            /* Mutation */
            alg_gen.Non_Uniform_Mutation(Gen, n_generaciones);

            /* Elitist selection */
            alg_gen.Elitist();

            /* Evaluation of the current population */
            alg_gen.Evaluate();

            /* we increment the counter */
            Gen++;

            fun_adap.Decode(alg_gen.solucion(), alg_gen.RuleBase);
            fun_adap.Clasification_accuracy(true, tabla);
            porcen_tra = fun_adap.ClaTra;
            if (porcen_tra > acc_max){
                acc_max = porcen_tra;
                System.out.println(" Iteration=" + (Gen - 1) + " %TRA=" +
                                   porcen_tra + " " + " #R=" +
                                   base_reglas.n_reglas);
            }
        } while (Gen <= n_generaciones);

        fun_adap.Decode(alg_gen.solucion(), alg_gen.RuleBase);

        /* we calcule the clasification percentaje on training */
        fun_adap.Clasification_accuracy(true, tabla);
        porcen_tra = fun_adap.ClaTra;

        /* we calcule the clasification percentaje on test */
        tabla_tst = new MyDataset(fich_datos_tst, false);
        fun_adap.Clasification_accuracy(false, tabla_tst);
        porcen_tst = fun_adap.ClaTst;

        /* we write the RB */
        cadenaRules = base_reglas.BRtoString();
        cadenaRules += "\n%Tra: " + porcen_tra + " %Tst: " + porcen_tst + "\n";

        MyFile.WriteMyFile(fichero_reglas, cadenaRules);

        /* we write the obligatory output files*/
        String salida_tra = tabla.getHeader();
        salida_tra += fun_adap.ObligatoryOutputFile(tabla);
        MyFile.WriteMyFile(fich_tra_obli, salida_tra);

        String salida_tst = tabla_tst.getHeader();
        salida_tst += fun_adap.ObligatoryOutputFile(tabla_tst);
        MyFile.WriteMyFile(fich_tst_obli, salida_tst);

        /* we write the MSEs in specific files */
        MyFile.AddtoMyFile(ruta_salida + "tuningcomunR.txt",
                                "" + base_reglas.n_reglas + "\n");
        MyFile.AddtoMyFile(ruta_salida + "tuningcomunTRA.txt",
                                "" + porcen_tra + "\n");
        MyFile.AddtoMyFile(ruta_salida + "tuningcomunTST.txt",
                                "" + porcen_tst + "\n");
    }

}

