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

import java.util.*;
import org.core.*;

public class BaseR
    implements Comparable {

  ArrayList<Regla> baseReglas;
  BaseD baseDatos;
  myDataset train;
  int n_variables, pesoRegla, tipoInf, tipoComp, fitness, totalParticiones;
  int noCubiertos;

  public boolean BETTER(int a, int b) {
    if (a > b) {
      return true;
    }
    return false;
  }

  public BaseR() {

  }

  public BaseR(BaseD baseDatos, myDataset train, int pesoRegla,
               int tipoInf, int tipoComp) {
    baseReglas = new ArrayList<Regla> ();
    this.baseDatos = baseDatos;
    this.train = train;
    this.n_variables = baseDatos.numVariables();
    this.pesoRegla = pesoRegla;
    this.tipoComp = tipoComp;
    this.tipoInf = tipoInf;
    totalParticiones = baseDatos.particiones; //L == (L-2)
  }

  public String printString() {
    int i, j;
    String [] nombres = train.nombres();
    String [] clases = train.clases();
    String cadena = new String("");
    cadena += "@Number of rules: " + baseReglas.size() + "\n\n";
    for (i = 0; i < baseReglas.size(); i++) {
      Regla r = baseReglas.get(i);
      cadena += (i+1)+": ";
      for (j = 0; j < n_variables-1; j++) {
        //cadena += r.baseDatos.print(j, r.antecedente[j]);
        cadena += nombres[j]+" IS " + r.baseDatos.print(j,r.antecedente[j]) + " AND ";
      }
      cadena += nombres[j]+" IS " + r.baseDatos.print(j,r.antecedente[j]) + ": " + clases[r.clase] + " with Rule Weight: " + r.peso + "\n";
    }
    return (cadena);
  }

  public void escribeFichero(String filename) {
    String cadenaSalida = new String("");
    cadenaSalida = printString();
    Fichero.escribeFichero(filename, cadenaSalida);
  }

  public int FRM(double[] example) {
    if (this.tipoInf == Fuzzy_Ish.CLASICO) {
      return FRM_WR(example);
    }
    else {
      return FRM_AC(example);
    }
  }

  private int FRM_WR(double[] example) {
    int clase = -1;
    double max = 0.0;
    for (int i = 0; i < baseReglas.size(); i++) {
      Regla r = baseReglas.get(i);
      double produc = r.compatibilidad(example);
      produc *= r.peso;
      if (produc > max) {
        max = produc;
        clase = r.clase;
      }
    }
    return clase;
  }

  private int FRM_AC(double[] example) {
    int clase = -1;
    double[] grado_clases = new double[1];
    for (int i = 0; i < baseReglas.size(); i++) {
      Regla r = baseReglas.get(i);

      double produc = r.compatibilidad(example);
      produc *= r.peso;
      if (r.clase > grado_clases.length - 1) {
        double[] aux = new double[grado_clases.length];
        for (int j = 0; j < aux.length; j++) {
          aux[j] = grado_clases[j];
        }
        grado_clases = new double[r.clase + 1];
        for (int j = 0; j < aux.length; j++) {
          grado_clases[j] = aux[j];
        }
      }
      grado_clases[r.clase] += produc;
    }
    double max = 0.0;
    for (int l = 0; l < grado_clases.length; l++) {
      //System.err.println("Grado_Clase["+l+"]: "+grado_clases[l]);
      if (grado_clases[l] > max) {
        max = grado_clases[l];
        clase = l;
      }
    }

    return clase;
  }

  /**
   * Genero el conjunto de reglas inicial para cada particion del espacio (2...L)
   */
  public void Generacion() {
    for (int i = 0; i < totalParticiones; i++) {
      int[] regla = new int[n_variables];
      this.RecorreAntecedentes(regla, 0, i);
    }
    //System.err.println("Base de Reglas -> " + this.printString());
    //System.exit(0);
  }

  void RecorreAntecedentes(int[] Regla_act, int pos, int particion) {
    if (pos == n_variables) {
      crearRegla(Regla_act);
    }
    else {
      for (Regla_act[pos] = baseDatos.baseDatos[particion][0][0].etiqueta;
           Regla_act[pos] <
           baseDatos.baseDatos[particion][0][baseDatos.baseDatos[particion][0].
           length - 1].etiqueta + 1;
           Regla_act[pos]++) {
        RecorreAntecedentes(Regla_act, pos + 1, particion);
      }
    }
  }

  void crearRegla(int[] antecedente) {
    Regla r = new Regla(baseDatos, tipoComp);
    r.asignaAntecedente(antecedente);
    r.calcula_consecuente(train);
    if (r.peso > 0) {
      baseReglas.add(r);
    }
  }

  public void evalua() {
    int n_clasificados = 0;
    int noCubiertos = 0;
    for (int j = 0; j < train.size(); j++) {
      int clase = this.FRM_WR(train.getExample(j));
      if (train.getOutputAsInteger(j) == clase) {
        n_clasificados++;
      }
    }
    fitness = n_clasificados;
  }

  public void borrar() {
    for (int i = 0; i < baseReglas.size(); ) {
      if (baseReglas.get(i).peso < 0.0) {
        baseReglas.remove(i);
      }
      else {
        i++;
      }
    }
  }

  public double getAccuracy() {
    return (double) fitness / train.size();
  }

  public BaseR clone() {
    BaseR br = new BaseR();
    br.baseDatos = baseDatos;
    br.baseReglas = new ArrayList<Regla> ();
    for (int i = 0; i < baseReglas.size(); i++) {
      br.baseReglas.add(baseReglas.get(i).clone());
    }
    br.train = train;
    br.n_variables = n_variables;
    br.pesoRegla = pesoRegla;
    br.tipoInf = tipoInf;
    br.tipoComp = tipoComp;
    br.fitness = fitness;
    br.noCubiertos = noCubiertos;
    return br;
  }

  public void mutar(double mutProb) {
    for (int j = 0; j < baseReglas.size(); j++) {
      baseReglas.get(j).mutar(train, mutProb);
    }
  }

  public int compareTo(Object a) {
    if ( ( (BaseR) a).fitness < this.fitness) {
      return -1;
    }
    if ( ( (BaseR) a).fitness > this.fitness) {
      return 1;
    }
    return 0;
  }

  public int size() {
    return baseReglas.size();
  }

  public void eliminaRegla(int pos) {
    baseReglas.remove(pos);
  }

  public void actualiza(boolean[] elegidas) {
    for (int j = this.size() - 1; j >= 0; j--) {
      if (!elegidas[j]) {
        this.eliminaRegla(j);
      }
    }
  }

  public double clasifica(){
    evalua();
    return this.getAccuracy();
  }
}

