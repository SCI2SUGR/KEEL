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

import org.core.Fichero;
import java.util.StringTokenizer;

public class BaseD {
  int n_variables, n_etiquetas;
  Difuso[][] baseDatos;
  Difuso[][] baseDatosIni;

  public BaseD() {
  }

  public BaseD(String fichero, int n_variables) {
    this.n_variables = n_variables;
    String cadena = Fichero.leeFichero(fichero);
    StringTokenizer linea = new StringTokenizer(cadena, "\n");

    linea.nextToken(); //Initial DB_
    linea.nextToken(); // Variable1:
    n_etiquetas = 0;
    while (linea.nextToken().startsWith("    Label ")) {
      n_etiquetas++;
    }

    //Releo sabiendo el numero de etiquetas
    linea = new StringTokenizer(cadena, "\n");
    linea.nextToken(); //Initial DB_
    baseDatos = new Difuso[n_variables][];
    baseDatosIni = new Difuso[n_variables][];
    for (int i = 0; i < n_variables; i++) {
      baseDatos[i] = new Difuso[n_etiquetas];
      baseDatosIni[i] = new Difuso[n_etiquetas];
      linea.nextToken(); // Variable1:
      for (int j = 0; j < n_etiquetas; j++) {
        String etiquetas = linea.nextToken();
        StringTokenizer et = new StringTokenizer(etiquetas, "(,)");
        et.nextToken(); //label
        baseDatos[i][j] = new Difuso();
        baseDatos[i][j].x0 = Double.parseDouble(et.nextToken());
        baseDatos[i][j].x1 = Double.parseDouble(et.nextToken());
        baseDatos[i][j].x3 = Double.parseDouble(et.nextToken());
        baseDatos[i][j].y = 1.0;
        baseDatos[i][j].nombre = new String("L" + j);
        baseDatos[i][j].etiqueta = j;
        baseDatosIni[i][j] = new Difuso();
        baseDatosIni[i][j].x0 = baseDatos[i][j].x0;
        baseDatosIni[i][j].x1 = baseDatos[i][j].x1;
        baseDatosIni[i][j].x3 = baseDatos[i][j].x3;
        baseDatosIni[i][j].y = 1.0;
        baseDatosIni[i][j].nombre = new String("L" + j);
        baseDatosIni[i][j].etiqueta = j;
      }
    }
  }

  public int numVariables() {
    return n_variables;
  }

  public double pertenencia(int i, int j, double X) {
    if ((j == 0)&&(X <= baseDatos[i][j].x1)){ //si es la primera etiqueta y esta por debajo del valor maximo
      return 1.0; //El grado de pertenecia = 1 (barra horizontal hasta el eje de coordenadas)
    }else if ((j == n_etiquetas-1)&&(X >= baseDatos[i][j].x1)){ //si es la última etiqueta y esta por encima del valor maximo
      return 1.0;
    }
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

  public void ponBaseDatosInicial(){
    for (int i = 0; i < n_variables; i++){
      for (int j = 0; j < n_etiquetas; j++){
        baseDatos[i][j] = baseDatosIni[i][j].clone();
      }
    }
  }

  public void ajusta(double[] cromosoma) {
    //Primero pongo la base de datos inicial
    this.ponBaseDatosInicial();
    int k = 0;

    for (int i = 0; i < n_variables; i++){
      for (int j = 0; j < n_etiquetas; j++){
        baseDatos[i][j].x0 += cromosoma[k] - cromosoma[k+1];
        baseDatos[i][j].x1 += cromosoma[k];
        baseDatos[i][j].x3 += cromosoma[k] + cromosoma[k+1];
        k+= 2;
      }
    }

  }

}

