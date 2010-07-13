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

package keel.Algorithms.Decision_Trees.DT_GA;

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
public class Clasificador {

  BaseR reglasArbol, reglasGA;
  int umbralS;
  int tipoGA;
  String claseMayoritaria;

  public Clasificador() {
  }

  public Clasificador(BaseR reglasArbol, BaseR reglasGA, int tipoGA,
                      int umbralS,String claseMay) {
    this.reglasArbol = reglasArbol;
    this.reglasGA = reglasGA;
    this.umbralS = umbralS;
    this.tipoGA = tipoGA;
    this.claseMayoritaria = claseMay;
  }

  /**
   * Clasifica un ejemplo
   * @param ejemplo El ejemplo a clasificar
   * @return el valor de la clase predicha
   */
  public String clasifica(double[] ejemplo) {
    boolean smallDisjunct = false;
    String clase = "<unclassified>";
    int i; //guarda la regla que clasifica
    for (i = 0; (i < reglasArbol.size()) && (clase.equals("<unclassified>")); i++) {
      if (reglasArbol.baseReglas.get(i).cubre(ejemplo)) {
        clase = reglasArbol.baseReglas.get(i).clase;
      }
    }
    i--; //suma uno al salir
    if (i == -1){
      return claseMayoritaria; //El arbol esta vacio!!!
    }
    int reglaArbol = i;
    smallDisjunct = (reglasArbol.baseReglas.get(i).cubiertos() < umbralS);
    if (smallDisjunct) {
      if (tipoGA == DT_GA.GA_SMALL) {
        double pesoMax = 0.0;
        String claseAux;
        for (i = 0; i < reglasGA.size(); i++) {
          Regla r = reglasGA.baseReglas.get(i);
          if (r.codigoRegla == reglaArbol) {
            if (r.cubre(ejemplo)) {
              claseAux = r.clase;
              double peso = r.fitness;
              if (peso > pesoMax) {
                clase = claseAux;
                pesoMax = peso;
              }
            }
          }
        }
      }
      else {
        double pesoMax = 0.0;
        String claseAux;
        for (i = 0; i < reglasGA.size(); i++) {
          Regla r = reglasGA.baseReglas.get(i);
          if (r.cubre(ejemplo)) {
            claseAux = r.clase;
            double peso = r.fitness;
            if (peso > pesoMax) { //esto me lo invento porque no viene en el paper
              clase = claseAux;
              pesoMax = peso;
            }
          }
        }
      }
    }
    return clase;
  }

}

