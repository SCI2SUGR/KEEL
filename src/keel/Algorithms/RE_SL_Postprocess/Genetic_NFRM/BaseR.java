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

package keel.Algorithms.RE_SL_Postprocess.Genetic_NFRM;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.util.*;
import org.core.*;

public class BaseR {

  ArrayList<Regla> baseReglas;
  BaseD baseDatos;
  myDataset train;
  int n_variables, n_etiquetas;

  public boolean BETTER(int a, int b) {
    if (a > b) {
      return true;
    }
    return false;
  }

  public BaseR() {

  }

  public BaseR(String fichero, BaseD baseDatos) {
    String cadena = Fichero.leeFichero(fichero);
    this.baseDatos = baseDatos;
    baseReglas = new ArrayList<Regla> ();
    n_variables = baseDatos.n_variables;
    n_etiquetas = baseDatos.n_etiquetas;
    StringTokenizer lineas = new StringTokenizer(cadena, "\n");
    String reglas = lineas.nextToken();
    StringTokenizer palabras = new StringTokenizer(reglas, " ");
    for (int i = 0; i < 3; i++) {
      palabras.nextToken();
    }
    int n_reglas = Integer.parseInt(palabras.nextToken());
    for (int i = 0; i < n_reglas; i++) {
      int[] antecedente = new int[n_variables - 1];
      for (int j = 0; j < n_variables - 1; j++) {
        String linea = lineas.nextToken();
        StringTokenizer valor = new StringTokenizer(linea, " ");
        antecedente[j] = baseDatos.dameEtiqueta(j,
                                                Double.parseDouble(valor.
            nextToken()));
      }
      String linea = lineas.nextToken();
      StringTokenizer valor = new StringTokenizer(linea, " ");
      int consecuente = baseDatos.dameEtiqueta(n_variables - 1,
                                               Double.
                                               parseDouble(valor.nextToken()));
      Regla r = new Regla(baseDatos);
      r.asignaAntecedente(antecedente);
      r.consecuente = consecuente;
      this.baseReglas.add(r);
    }
  }

  public String printString() {
    int i, j;
    String cadena = "";

    cadena += "Number of rules: " + baseReglas.size() + "\n\n";
    for (i = 0; i < baseReglas.size(); i++) {
      Regla r = baseReglas.get(i);
      for (j = 0; j < n_variables - 1; j++) {
        cadena += baseDatos.baseDatos[j][r.antecedente[j]].nombre + ": \t" +
            baseDatos.baseDatos[j][r.antecedente[j]].x0 +
            "\t"
            + baseDatos.baseDatos[j][r.antecedente[j]].x1 +
            "\t" + baseDatos.baseDatos[j][r.antecedente[j]].x3 + "\n";
      }
      cadena += "Output: " + baseDatos.baseDatos[n_variables -
          1][r.consecuente].nombre + ": \t" +
          baseDatos.baseDatos[j][r.consecuente].x0 +
          "\t"
          + baseDatos.baseDatos[j][r.consecuente].x1 +
          "\t" + baseDatos.baseDatos[j][r.consecuente].x3 + "\n\n";
    }

    return (cadena);
  }

  public void escribeFichero(String filename) {
    String cadenaSalida = new String("");
    cadenaSalida = printString();
    Fichero.escribeFichero(filename, cadenaSalida);
  }

  public int size() {
    return baseReglas.size();
  }

  public int numEtiquetas() {
    return n_etiquetas;
  }

  public Regla dameRegla(int pos) {
    return baseReglas.get(pos);
  }

  public double FRM(double[] ejemplo, MatrizR matriz){ //Individuo ind) {
    double[] x = new double[this.size()]; //grado de pertenencia del ejemplo a cada regla
    double[][] w = new double[n_etiquetas][this.size()]; //grado de salida de la matriz
    double[] y = new double[n_etiquetas]; //una salida por cada regla
    //MatrizR matriz = ind.cromosoma1;
    //double [] ajuste = ind.cromosoma2;
    for (int i = 0; i < this.size(); i++) {
      x[i] = baseReglas.get(i).compatibilidadMinimo(ejemplo); //,ajuste);
      for (int j = 0; j < n_etiquetas; j++) {
        w[j][i] = matriz.damePeso(i, j);
      }
    }
    for (int i = 0; i < n_etiquetas; i++) {
      y[i] = sumaProducto(x, w[i]);
    }
    return momento(y);
  }

  private double sumaProducto(double[] x, double[] w) {
    double acumulado = 0;
    for (int i = 0; i < x.length; i++) {
      acumulado += x[i] * w[i];
    }
    return acumulado;
  }

  private double momento(double[] y) {
    double salida = 0, denominador = 0;
    for (int i = 0; i < y.length; i++) {
      denominador += y[i];
    }
    if (denominador == 0){
      return Double.MAX_VALUE;
    }
    for (int i = 0; i < y.length; i++) {
      salida += (y[i] * centroide(i)) / denominador;
    }
    return salida;
  }

  private double centroide(int etiqueta) {
    return baseDatos.baseDatos[this.n_variables - 1][etiqueta].x1;
  }

}

