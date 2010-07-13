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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Fuzzy_Ish_Selec;

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

public class BaseD {
  int n_variables, particiones;
  Difuso[][][] baseDatos;
  String nombres[];

  public BaseD() {
  }

  public BaseD(int particiones, int n_variables, double[][] rangos, String [] nombres) {
    this.n_variables = n_variables;
    this.particiones = particiones - 1;
    baseDatos = new Difuso[this.particiones][][];
    this.nombres = nombres.clone();

    double marca, valor;
    for (int j = 0; j < this.particiones; j++) {
      baseDatos[j] = new Difuso[n_variables][];
      for (int i = 0; i < n_variables; i++) {
        baseDatos[j][i] = new Difuso[2 + j];
        marca = (rangos[i][1] - rangos[i][0]) / ( (float) 1 + j);
        for (int etq = 0; etq < 2 + j; etq++) {
          valor = rangos[i][0] + marca * (etq - 1);
          baseDatos[j][i][etq] = new Difuso();
          baseDatos[j][i][etq].x0 = Asigna(valor, rangos[i][1]);
          valor = rangos[i][0] + marca * etq;
          baseDatos[j][i][etq].x1 = Asigna(valor, rangos[i][1]);
          valor = rangos[i][0] + marca * (etq + 1);
          baseDatos[j][i][etq].x3 = Asigna(valor, rangos[i][1]);
          baseDatos[j][i][etq].y = 1;
          baseDatos[j][i][etq].nombre = new String("L_" + etq + "(" + (j + 2) +
              ")");
          baseDatos[j][i][etq].etiqueta = (int) ( (1.5 * j) + (0.5 * j * j) +
                                                 etq);
        }
      }
    }
    //Ahora el D.C.
    /*baseDatos[4] = new Difuso[n_variables][];
         for (int i=0; i< n_variables; i++) {
       baseDatos[4][i] = new Difuso[1];
       baseDatos[4][i][0] = new Difuso();
       baseDatos[4][i][0].x0 = rangos[i][0];
       baseDatos[4][i][0].x1 = rangos[i][0];
       baseDatos[4][i][0].x3 = rangos[i][1];
       baseDatos[4][i][0].y = 1.0;
       baseDatos[4][i][0].nombre = new String("D.C.");
       baseDatos[4][i][0].etiqueta = 14;
         }*/
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

  public double pertenencia(int i, int j, int k, double X) {
    return baseDatos[i][j][k].Fuzzifica(X);
  }

  public Difuso copia(int i, int j, int k) {
    return baseDatos[i][j][k].clone();
  }

  public String print_triangle(int var, int etiqueta) {
    String cadena = new String("");
    int k;
    if (etiqueta <= 1) {
      k = 0;
    }
    else if (etiqueta <= 4) {
      k = 1;
      etiqueta -= 2;
    }
    else if (etiqueta <= 8) {
      k = 2;
      etiqueta -= 5;
    }
    else if (etiqueta <= 13) {
      k = 3;
      etiqueta -= 9;
    }
    else {
      k = 4;
      etiqueta = 0;
    }
    Difuso d = baseDatos[k][var][etiqueta];

    cadena = d.nombre + ": \t" + d.x0 +
        "\t"
        + d.x1 +
        "\t" + d.x3 + "\n";
    return cadena;
  }

  public String print(int var, int etiqueta) {
    String cadena = new String("");
    int k;
    if (etiqueta <= 1) {
      k = 0;
    }
    else if (etiqueta <= 4) {
      k = 1;
      etiqueta -= 2;
    }
    else if (etiqueta <= 8) {
      k = 2;
      etiqueta -= 5;
    }
    else if (etiqueta <= 13) {
      k = 3;
      etiqueta -= 9;
    }
    else {
      k = 4;
      etiqueta = 0;
    }
    return baseDatos[k][var][etiqueta].nombre;
  }

  public String printString() {
    String cadena = new String(
        "@Using Triangular Membership Functions as antecedent fuzzy sets");
    for (int k = 0; k < particiones; k++) {
      cadena += "\n\n@Number of Labels per variable: " + (k + 2) + "\n";
      for (int i = 0; i < n_variables; i++) {
        //cadena += "\nVariable " + (i + 1) + ":\n";
        cadena += "\n" + nombres[i] + ":\n";
        for (int j = 0; j < 2 + k; j++) {
          //" L_" + (j + 1)
          cadena += baseDatos[k][i][j].nombre + ": (" + baseDatos[k][i][j].x0 +
              "," + baseDatos[k][i][j].x1 + "," + baseDatos[k][i][j].x3 +
              ")\n";

        }
      }
    }
    return cadena;

  }

  public void escribeFichero(String filename) {
    String cadenaSalida = new String("");
    cadenaSalida = printString();
    Fichero.escribeFichero(filename, cadenaSalida);
  }

}

