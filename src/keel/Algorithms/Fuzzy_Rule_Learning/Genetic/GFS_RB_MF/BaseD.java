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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.GFS_RB_MF;

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

import org.core.Fichero;
import java.util.StringTokenizer;

public class BaseD {
  int n_variables, n_etiquetas;
  Difuso[][] baseDatos;
  Difuso[][] baseDatosIni;

  public BaseD() {
  }

  public BaseD(int n_etiquetas, int n_variables, double[][] rangos) {
    this.n_variables = n_variables;
    this.n_etiquetas = n_etiquetas;

    double marca, valor;
    baseDatos = new Difuso[n_variables][n_etiquetas];
    baseDatosIni = new Difuso[n_variables][n_etiquetas];
    for (int i = 0; i < n_variables; i++) {
      marca = (rangos[i][1] - rangos[i][0]) / ( (float) n_etiquetas - 1);
      for (int etq = 0; etq < n_etiquetas; etq++) {
        valor = rangos[i][0] + marca * (etq - 1);
        baseDatos[i][etq] = new Difuso();
        baseDatos[i][etq].x0 = Asigna(valor, rangos[i][1]);
        valor = rangos[i][0] + marca * etq;
        baseDatos[i][etq].x1 = baseDatos[i][etq].x2 = Asigna(valor, rangos[i][1]);
        valor = rangos[i][0] + marca * (etq + 1);
        baseDatos[i][etq].x3 = Asigna(valor, rangos[i][1]);
        baseDatos[i][etq].y = 1;
        baseDatos[i][etq].nombre = new String("L" + etq);
        baseDatos[i][etq].etiqueta = etq;
        baseDatosIni[i][etq] = new Difuso();
        baseDatosIni[i][etq].x0 = Asigna(valor, rangos[i][1]);
        valor = rangos[i][0] + marca * etq;
        baseDatosIni[i][etq].x1 = baseDatosIni[i][etq].x2 = Asigna(valor,
            rangos[i][1]);
        valor = rangos[i][0] + marca * (etq + 1);
        baseDatosIni[i][etq].x3 = Asigna(valor, rangos[i][1]);
        baseDatosIni[i][etq].y = 1;
        baseDatosIni[i][etq].nombre = new String("L" + etq);
        baseDatosIni[i][etq].etiqueta = etq;
      }
    }
  }

  private double Asigna(double val, double tope) {
    /* Redondea el valor generado para la semantica cuando sea necesario */

    if (val > -1E-4 && val < 1E-4) {
      return (0);
    }

    if (val > tope - 1E-4 && val < tope + 1E-4) {
      return (tope);
    }

    return (val);
  }

  public int numVariables() {
    return n_variables;
  }

  public double pertenencia(int i, int j, double X) {
    return baseDatos[i][j].Fuzzifica(X);
  }

  public Difuso copia(int i, int j) {
    return baseDatos[i][j].clone();
  }

  public String printString() {
    String cadena = new String("");
    for (int i = 0; i < n_variables; i++) {
      cadena += "\nVariable " + (i + 1) + ":\n";
      for (int j = 0; j < n_etiquetas; j++) {
        cadena += " Label " + (j + 1) + ": (" + baseDatos[i][j].x0 +
            "," + baseDatos[i][j].x1 + "," + baseDatos[i][j].x3 +
            ")\n";
      }
    }
    return cadena;
  }

  public void escribeFichero(String filename) {
    String cadenaSalida = new String("");
    cadenaSalida = printString();
    Fichero.escribeFichero(filename, cadenaSalida);
  }

  public int dameEtiqueta(int variable, double valor) {
    int i = 0;
    while (valor != baseDatos[variable][i].x0) {
      i++;
    }
    return i;
  }

  public BaseD clone() {
    BaseD base = new BaseD();
    base.n_etiquetas = this.n_etiquetas;
    base.n_variables = this.n_variables;
    base.baseDatos = new Difuso[n_variables][n_etiquetas];
    for (int i = 0; i < n_variables; i++) {
      for (int j = 0; j < n_etiquetas; j++) {
        base.baseDatos[i][j] = this.copia(i, j);
      }
    }

    return base;
  }

  public void ponBaseDatosInicial() {
    for (int i = 0; i < n_variables; i++) {
      for (int j = 0; j < n_etiquetas; j++) {
        baseDatos[i][j] = baseDatosIni[i][j].clone();
      }
    }
  }

  public void ajusta(double[] cromosoma) {
    //Primero pongo la base de datos inicial
    this.ponBaseDatosInicial();
    int k = 0;

    for (int i = 0; i < n_variables; i++) {
      for (int j = 0; j < n_etiquetas; j++) {
        baseDatos[i][j].x0 = cromosoma[k];
        baseDatos[i][j].x1 = cromosoma[k + 1];
        baseDatos[i][j].x3 = cromosoma[k + 2];
        k += 3;
      }
    }

  }

}

