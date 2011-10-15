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

/*
 * Created on 20-Jul-2004

 *

 *

 */



/**
 * @author Salvador Garc�a L�pez

 *

 *

 */



package keel.Algorithms.ImbalancedClassification.Ensembles.Basic;

import keel.Dataset.*;
import org.core.*;

public class Output {

  public static void escribeSalida(String nombreFichero, int[][] salidaKNN,
                                   int[][] prediccion, Attribute entradas[],
                                   Attribute salida, int nEntradas,
                                   String relation) {

    int n_ejemplos, n_salidas = 0;
    String cadena = "";
    int i, j;

    /*Printing input attributes*/
    cadena += "@relation " + relation + "\n";
    for (i = 0; i < nEntradas; i++) {
      cadena += "@attribute " + entradas[i].getName() + " ";
      if (entradas[i].getType() == Attribute.NOMINAL) {
        cadena += "{";
        for (j = 0; j < entradas[i].getNominalValuesList().size(); j++) {
          cadena += (String) entradas[i].getNominalValuesList().elementAt(j);
          if (j < entradas[i].getNominalValuesList().size() - 1) {
            cadena += ", ";
          }
        }
        cadena += "}\n";
      }
      else {
        if (entradas[i].getType() == Attribute.INTEGER) {
          cadena += "integer";
        }
        else {
          cadena += "real";
        }
        cadena += " [" + String.valueOf(entradas[i].getMinAttribute()) + ", " +
            String.valueOf(entradas[i].getMaxAttribute()) + "]\n";
      }
    }

    /*Printing output attribute*/
    cadena += "@attribute " + salida.getName() + " ";
    if (salida.getType() == Attribute.NOMINAL) {
      cadena += "{";
      for (j = 0; j < salida.getNominalValuesList().size(); j++) {
        cadena += (String) salida.getNominalValuesList().elementAt(j);
        if (j < salida.getNominalValuesList().size() - 1) {
          cadena += ", ";
        }
      }
      cadena += "}\n";
    }
    else {
      cadena += "integer [" + String.valueOf(salida.getMinAttribute()) + ", " +
          String.valueOf(salida.getMaxAttribute()) + "]\n";
    }

    /*Printing the data*/
    cadena += "@data\n";

    Fichero.escribeFichero(nombreFichero, cadena);

    n_ejemplos = salidaKNN.length;

    if (n_ejemplos > 0) {
      n_salidas = salidaKNN[0].length;
    }

    for (i = 0; i < n_ejemplos; i++) {
      cadena = "";
      for (j = 0; j < n_salidas; j++) {
        cadena += "" + salidaKNN[i][j] + " ";
      }
      for (j = 0; j < n_salidas; j++) {
        cadena += "" + prediccion[i][j] + " ";
      }
      cadena += "\n";
      Fichero.AnadirtoFichero(nombreFichero, cadena);
    }
  }

  public static void escribeSalida(String nombreFichero, String[][] salidaKNN,
                                   String[][] prediccion, Attribute entradas[],
                                   Attribute salida, int nEntradas,
                                   String relation) {

    int n_ejemplos, n_salidas = 0;
    String cadena = "";
    int i, j;

    /*Printing input attributes*/
    cadena += "@relation " + relation + "\n";
    for (i = 0; i < nEntradas; i++) {
      cadena += "@attribute " + entradas[i].getName() + " ";
      if (entradas[i].getType() == Attribute.NOMINAL) {
        cadena += "{";
        for (j = 0; j < entradas[i].getNominalValuesList().size(); j++) {
          cadena += (String) entradas[i].getNominalValuesList().elementAt(j);
          if (j < entradas[i].getNominalValuesList().size() - 1) {
            cadena += ", ";
          }
        }
        cadena += "}\n";
      }
      else {
        if (entradas[i].getType() == Attribute.INTEGER) {
          cadena += "integer";
        }
        else {
          cadena += "real";
        }
        cadena += " [" + String.valueOf(entradas[i].getMinAttribute()) + ", " +
            String.valueOf(entradas[i].getMaxAttribute()) + "]\n";
      }
    }

    /*Printing output attribute*/
    cadena += "@attribute " + salida.getName() + " ";
    if (salida.getType() == Attribute.NOMINAL) {
      cadena += "{";
      for (j = 0; j < salida.getNominalValuesList().size(); j++) {
        cadena += (String) salida.getNominalValuesList().elementAt(j);
        if (j < salida.getNominalValuesList().size() - 1) {
          cadena += ", ";
        }
      }
      cadena += "}\n";
    }
    else {
      cadena += "integer [" + String.valueOf(salida.getMinAttribute()) + ", " +
          String.valueOf(salida.getMaxAttribute()) + "]\n";
    }

    /*Printing the data*/
    cadena += "@data\n";

    Fichero.escribeFichero(nombreFichero, cadena);
    n_ejemplos = salidaKNN.length;
    if (n_ejemplos > 0) {
      n_salidas = salidaKNN[0].length;
    }

    for (i = 0; i < n_ejemplos; i++) {
      cadena = "";
      for (j = 0; j < n_salidas; j++) {
        cadena += "" + salidaKNN[i][j] + " ";
      }
      for (j = 0; j < n_salidas; j++) {
        cadena += "" + prediccion[i][j] + " ";
      }
      cadena += "\n";
      Fichero.AnadirtoFichero(nombreFichero, cadena);
    }
  }
}
