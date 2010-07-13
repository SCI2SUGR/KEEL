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

import java.util.*;
import org.core.*;

public class BaseR {

  ArrayList<Regla> baseReglas;
  BaseD baseDatos;
  myDataset train;
  int n_variables, n_etiquetas;
  double[] GradoEmp;
  Difuso[] Consecuentes;

  public boolean BETTER(int a, int b) {
    if (a > b) {
      return true;
    }
    return false;
  }

  public BaseR() {

  }

  public BaseR(BaseD baseDatos, myDataset train) {
    baseReglas = new ArrayList<Regla> ();
    this.baseDatos = baseDatos;
    this.train = train;
    this.n_variables = baseDatos.numVariables();
    this.n_etiquetas = baseDatos.n_etiquetas;
    generaReglas();
  }

  private void generaReglas() {
    int[] regla = new int[n_variables-1];
    this.RecorreAntecedentes(regla, 0);
    GradoEmp = new double[baseReglas.size()];
    Consecuentes = new Difuso[baseReglas.size()];
    for (int i = 0; i < Consecuentes.length; i++){
      Consecuentes[i] = new Difuso();
    }
  }

  void RecorreAntecedentes(int[] Regla_act, int pos) {
    if (pos == (n_variables-1)) {
      crearRegla(Regla_act);
    }
    else {
      for (Regla_act[pos] = 0; Regla_act[pos] < n_etiquetas; Regla_act[pos]++) {
        RecorreAntecedentes(Regla_act, pos + 1);
      }
    }
  }

  void crearRegla(int[] antecedente) {
    Regla r = new Regla(baseDatos);
    r.asignaAntecedente(antecedente);
    baseReglas.add(r); //el consecuente se calcula a posteriori segun el cromosoma
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

  public void ajusta(Individuo ind) {
    int[] cromosoma1 = ind.cromosoma1; //consecuentes
    double[] cromosoma2 = ind.cromosoma2; //ajuste de la MF
    for (int i = 0; i < cromosoma1.length; i++) {
      baseReglas.get(i).consecuente = cromosoma1[i];
    }
    //Faltaria ajustar la BD
    baseDatos.ajusta(cromosoma2);
  }

  /* -------------------------------------------------------------------------
         Conjunction Operator
   ------------------------------------------------------------------------- */

  /* T-norma Minimal */
  public void Min(double[] entradas) {
    int b;
    for (b = 0; b < baseReglas.size(); b++) {
      GradoEmp[b] = baseReglas.get(b).compatibilidadMinimo(entradas);
    }
  }

  /* -------------------------------------------------------------------------
         Implication Operator
   ------------------------------------------------------------------------- */

  public void T_Min() {
    int b;

    for (b = 0; b < baseReglas.size(); b++) {
      Regla r = baseReglas.get(b);
      Difuso d = baseDatos.baseDatos[n_variables - 1][r.consecuente];
      if (GradoEmp[b] != 0) {
        if (GradoEmp[b] == 1.0) {
          Consecuentes[b].x0 = d.x0;
          Consecuentes[b].x1 = d.x1;
          Consecuentes[b].x2 = d.x2;
          Consecuentes[b].x3 = d.x3;
        }
        else {
          Consecuentes[b].x0 = d.x0;
          Consecuentes[b].x1 = d.x0 + (d.x1 - d.x0) * GradoEmp[b];
          Consecuentes[b].x2 = d.x3 + (d.x2 - d.x3) * GradoEmp[b];
          Consecuentes[b].x3 = d.x3;
        }
      }

      Consecuentes[b].y = GradoEmp[b];
    }
  }

  /* -------------------------------------------------------------------------
         Defuzzification Interface
   ------------------------------------------------------------------------- */

  /**
   * Functions to calculate the centre of gravity
   * @param x0 primer valor del trapecio (esquina izq.)
   * @param x1 segundo valor del trapecio (soporte izq.)
   * @param x2 tercer valor del trapecio (soporte dcho.)
   * @param x3 cuarto valor del trapecio (esquina dcha.)
   * @param y valor y
   * @return el area del trapecio
   */
  public double AreaTrapecioX(double x0, double x1, double x2, double x3,
                              double y) {
    double izq, centro, der;

    if (x1 != x0) {
      izq = (2 * x1 * x1 * x1 - 3 * x0 * x1 * x1 + x0 * x0 * x0) /
          (6 * (x1 - x0));
    }
    else {
      izq = 0;
    }

    centro = (x2 * x2 - x1 * x1) / 2.0;

    if (x3 != x2) {
      der = (2 * x2 * x2 * x2 - 3 * x3 * x2 * x2 + x3 * x3 * x3) /
          (6 * (x3 - x2));
    }
    else {
      der = 0;
    }

    return (y * (izq + centro + der));
  }

  public double AreaTrapecio(double x0, double x1, double x2, double x3,
                             double y) {
    double izq, centro, der;

    if (x1 != x0) {
      izq = (x1 * x1 - 2 * x0 * x1 + x0 * x0) / (2 * (x1 - x0));
    }
    else {
      izq = 0;
    }

    centro = x2 - x1;

    if (x3 != x2) {
      der = (x3 * x3 - 2 * x3 * x2 + x2 * x2) / (2 * (x3 - x2));
    }
    else {
      der = 0;
    }

    return (y * (izq + centro + der));
  }

  /** Returns the centre of gravity weight by matching
   * @return Returns the centre of gravity weight by matching
   * */
  public double WECOA() {
    double num, den;
    int i;

    num = 0;
    den = 0;
    for (i = 0; i < baseReglas.size(); i++) {
      if (Consecuentes[i].y != 0) {
        num += GradoEmp[i] *
            (AreaTrapecioX(Consecuentes[i].x0, Consecuentes[i].x1,
                           Consecuentes[i].x2, Consecuentes[i].x3,
                           Consecuentes[i].y) /
             AreaTrapecio(Consecuentes[i].x0, Consecuentes[i].x1,
                          Consecuentes[i].x2, Consecuentes[i].x3,
                          Consecuentes[i].y));
        den += GradoEmp[i];
      }
    }

    if (den != 0) {
      return (num / den);
    }
    else {
      return 0.0;
    }
  }

  /* -------------------------------------------------------------------------
           Fuzzy Controller
   ------------------------------------------------------------------------- */

  /**
   *  Returns the ouput of the controller
   * @param Entrada Es el ejemplo
   * @return the ouput of the controller
   * */
  public double FLC(double[] Entrada) {
    Min(Entrada);
    T_Min();
    return (WECOA());
  }

}

