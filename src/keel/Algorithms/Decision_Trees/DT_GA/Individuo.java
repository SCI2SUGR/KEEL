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

import java.util.ArrayList;
import org.core.Randomize;

public class Individuo
    implements Comparable {

  Selector[] antecedente;
  boolean[] seleccionado;
  String clase;
  myDataset train;
  double fitness;
  boolean n_e;
  int codigoRegla;

  public Individuo() {
    antecedente = new Selector[1];
    antecedente[0] = new Selector();
    seleccionado = new boolean[1];
    n_e = true;
  }

  public Individuo(boolean[] antecedentes, String clase, myDataset train,
                   int codigo) {
    this.clase = clase;
    this.train = train;
    antecedente = new Selector[antecedentes.length];
    seleccionado = new boolean[antecedentes.length];
    seleccionado = antecedentes.clone();
    for (int i = 0; i < antecedentes.length; i++) {
      if (seleccionado[i]) {
        antecedente[i] = new Selector(i, train); //lo genera aleatorio
      }
    }
    n_e = true;
    codigoRegla = codigo;
  }

  public Individuo(myDataset train, int pos_ejemplo) {
    this.clase = "?"; //se escoge dinamicamente
    this.train = train;
    antecedente = new Selector[train.getnInputs()];
    seleccionado = new boolean[train.getnInputs()];
    double [] ejemplo = train.getExample(pos_ejemplo);
    for (int i = 0; i < seleccionado.length; i++) {
      /*if (Randomize.Rand() < 0.5){
        seleccionado[i] = true;
             }else{
        seleccionado[i] = false;
             }*/
      seleccionado[i] = true;
    }
    for (int i = 0; i < antecedente.length; i++) {
      //if (seleccionado[i]) {
      antecedente[i] = new Selector(i, train); //lo genera aleatorio
      //}
      if (!antecedente[i].cubre(ejemplo)){ //Hago los cambios para que lo cubra
        antecedente[i].modifica(ejemplo);
      }
    }
    n_e = true;
  }

  public Individuo(Individuo padre, Individuo madre, int puntoCorte) {
    antecedente = new Selector[padre.size()];
    seleccionado = new boolean[padre.size()];
    for (int i = 0; i < puntoCorte; i++) {
      seleccionado[i] = padre.seleccionado[i];
      if (seleccionado[i]) {
        antecedente[i] = padre.antecedente[i].copia();
      }
    }
    for (int i = puntoCorte; i < madre.size(); i++) {
      seleccionado[i] = madre.seleccionado[i];
      if (seleccionado[i]) {
        antecedente[i] = madre.antecedente[i].copia();
      }
    }
    /*int contador = 0;
    for (int i = 0; i < seleccionado.length; i++){
      if (!seleccionado[i])
        contador++;
    }
    if (contador == seleccionado.length-1){
      System.err.println("OUCH!!");
      System.exit(-1);
    }*/
    this.clase = padre.clase; //en GA_large se define dinamicamente
    this.train = padre.train;
    fitness = 0.0;
    codigoRegla = padre.codigoRegla;
    n_e = true;
  }

  public String printString() {
    String cadena = new String("");
    cadena += "IF ";
    for (int i = 0; i < antecedente.length; i++) {
      if (seleccionado[i]) {
        cadena += antecedente[i].printString() + "AND ";
      }
    }
    cadena += " THEN Class = " + clase + "\n";
    return cadena;
  }

  /**
   * Convierte al cromosoma en una regla valida
   * @return Regla la regla creada
   */
  public Regla convertir() {
    Regla r = new Regla();
    r.antecedente = new ArrayList<Selector> ();
    for (int i = 0; i < antecedente.length; i++) {
      if (seleccionado[i]) {
        r.antecedente.add(antecedente[i].copia());
      }
    }
    r.clase = clase;
    r.train = train;
    r.ejemplosCubiertos = new int[train.size()];
    r.nCubiertos = 0;
    r.ejemplosBienCubiertos = new int[train.size()];
    r.nCubiertosOK = 0;
    r.fitness = this.fitness;
    r.codigoRegla = this.codigoRegla;
    r.cubrirEjemplos();
    return r;
  }

  public Individuo clone() {
    Individuo ind = new Individuo();
    ind.seleccionado = new boolean[this.seleccionado.length];
    ind.seleccionado = this.seleccionado.clone();
    ind.antecedente = new Selector[this.antecedente.length];
    for (int i = 0; i < this.antecedente.length; i++) {
      if (seleccionado[i]) {
        ind.antecedente[i] = this.antecedente[i].copia();
      }
    }
    ind.clase = this.clase;
    ind.fitness = this.fitness;
    ind.train = this.train;
    ind.n_e = this.n_e;
    ind.codigoRegla = this.codigoRegla;
    return ind;
  }

  public double clasifica(int[] ejemplos, int nEjemplos) {
    int tp, fn, tn, fp;
    tp = fn = tn = fp = 0;
    //teoricamente fitness = (tp/(tp+fn))*(tn/(fp+tn)); pero...
    boolean algunoCubierto = false;
    for (int j = 0; j < nEjemplos; j++) {
      boolean cubierto = true;
      boolean completo = false;
      double[] ejemplo = train.getExample(ejemplos[j]);
      for (int i = 0; (i < antecedente.length) && (cubierto); i++) {
        if (seleccionado[i]) {
          completo = true;
          cubierto = cubierto && (antecedente[i].cubre(ejemplo));
        }
      }
      cubierto = cubierto && completo; //si no tiene ningun antecedente no me vale!
      String clase = train.getOutputAsString(ejemplos[j]);
      if (cubierto) {
        algunoCubierto = true;
        if (clase.equalsIgnoreCase(this.clase)) {
          tp++;
        }
        else {
          fp++;
        }
      }
      else {
        if (clase.equalsIgnoreCase(this.clase)) {
          fn++; //inventada julian
        }
        else {
          tn++; //inventada julian
        }
      }
    }
    //System.err.println("Cromosoma: " + this.printString() + " Mira -> Tn: " +
    //                   tn + " Tp: " + tp + " Fn: " + fn + " Fp:" + fp);
    if (!algunoCubierto){
      tp = tn = fp = fn = 0;
    }
    double sens = 1.0;
    if (tp + fn > 0) {
      sens = (1.0 * tp / (tp + fn));
    }
    double spec = 1.0;
    if (tn + fp > 0) {
      spec = (1.0 * tn / (fp + tn));
    }
    if (tp + tn == 0) {
      sens = spec = 0;
    }
    n_e = false;
    fitness = sens * spec;
    return fitness;
  }

  public double clasificaLarge(int[] ejemplos, int nEjemplos) {
    int tp, fn, tn, fp;
    tp = fn = tn = fp = 0;
    //teoricamente fitness = (tp/(tp+fn))*(tn/(fp+tn)); pero...
    int nClases = train.getnClasses();
    int[] cubiertosClase = new int[nClases];
    boolean[] cubiertos = new boolean[train.size()];
    for (int j = 0; j < nEjemplos; j++) {
      boolean cubierto = true;
      boolean completo = false;
      double[] ejemplo = train.getExample(ejemplos[j]);
      for (int i = 0; (i < antecedente.length) && (cubierto); i++) {
        if (seleccionado[i]) {
          completo = true;
          cubierto = cubierto && (antecedente[i].cubre(ejemplo));
        }
      }
      cubierto = cubierto && completo;
      if (cubierto) {
        int clase = train.getOutputAsInteger(ejemplos[j]);
        cubiertosClase[clase]++;
      }
      cubiertos[j] = cubierto;
    }
    int maxCubiertos = 0;
    for (int i = 0; i < nClases; i++) {
      if (cubiertosClase[i] > maxCubiertos) {
        maxCubiertos = cubiertosClase[i];
        this.clase = train.nombreClase(i);
      }
    }
    if (maxCubiertos > 0) {
      for (int i = 0; i < train.size(); i++) {
        String clase = train.getOutputAsString(i);
        if (cubiertos[i]) {
          if (clase.equalsIgnoreCase(this.clase)) {
            tp++;
          }
          else {
            fp++;
          }
        }
        else {
          if (clase.equalsIgnoreCase(this.clase)) {
            fn++; //inventada julian
          }
          else {
            tn++; //inventada julian
          }
        }
      }

      //System.err.println("Cromosoma: " + this.printString() + " Mira -> Tn: " +
      //                   tn + " Tp: " + tp + " Fn: " + fn + " Fp:" + fp);
    }
    double sens = 1.0;
    if (tp + fn > 0) {
      sens = (1.0 * tp / (tp + fn));
    }
    double spec = 1.0;
    if (tn + fp > 0) {
      spec = (1.0 * tn / (fp + tn));
    }
    if (tp + tn == 0) {
      sens = spec = 0;
    }

    n_e = false;
    fitness = sens * spec;
    return fitness;
  }

  public int size() {
    return antecedente.length;
  }

  /**
   * Esta funcion me la invento...tomaya!!
   * @param mutProb probabilidad de mutacion
   */
  public void mutar(double mutProb) {
    for (int i = 0; i < antecedente.length; i++) {
      if ( (seleccionado[i]) && (Randomize.Rand() < mutProb)) {
        antecedente[i].mutar();
      }
    }
  }

  /**
   * Calcula la ganancia de informacion
   * @param atributo int atributo a considerar
   * @param ejemplos son los ejemplos de entrenamiento considerados
   * @param T int Numero de ejemplos de entrenamiento
   * @return double la ganancia
   *
   * Info(G|cond_i) = [ - (|Vi|/|T|)*sum_{j=1}^c{(|Vij|/|Vi|)*(log_2{|Vij|/|Vi|})}
   * - (|¬Vi|/|T|)*sum_{j=1}^c{(|¬Vij|/|¬Vi|)*(log_2{|¬Vij|/|¬Vi|})}
   *
   * |Vi| = numero de ejemplos que satisfacen la condicion <Ai OPi Vij>
   * |Vij| = numero de ejemplos que satisfacen la condicion <Ai OPi Vij> y tienen el valor j para la clase
   * |¬Vi| = numero de ejemplos que NO satisfacen la condicion <Ai OPi Vij>
   * |¬Vij| = numero de ejemplos que NO satisfacen la condicion <Ai OPi Vij> y tienen el valor j para la clase
   */
  private double infoGcondi(int atributo, int[] ejemplos, int T) {
    double info = 0;
    int Vi, Vij[], noVi, noVij[];
    Vi = noVi = 0;
    Vij = new int[train.getnClasses()];
    noVij = new int[train.getnClasses()];
    for (int i = 0; i < Vij.length; i++) {
      Vij[i] = noVij[i] = 0;
    }
    for (int j = 0; j < T; j++) {
      double[] ejemplo = train.getExample(ejemplos[j]);
      boolean cubierto = (antecedente[atributo].cubre(ejemplo));
      int clase = train.getOutputAsInteger(ejemplos[j]);
      if (cubierto) {
        Vi++;
        Vij[clase]++;
      }
      else {
        noVi++;
        noVij[clase]++;
      }
    }
    double sum1, sum2;
    sum1 = sum2 = 0;
    for (int i = 0; i < Vij.length; i++) { //Para las c clases
      double aux = 1.0 * Vij[i] / Vi;
      sum1 += (aux) * (Math.log(aux) / Math.log(2));
      aux = 1.0 * noVij[i] / noVi;
      sum2 += (aux) * (Math.log(aux) / Math.log(2));
    }
    sum1 *= 1.0 * Vi / T;
    sum2 *= 1.0 * noVi / T;
    info = -sum1 - sum2;
    return info;
  }

  public void pruning(double infoG, int nEjemplos, int[] ejemplos) {
    double[] info_gain = new double[antecedente.length];
    int[] atributos = new int[antecedente.length];
    int n = 0;
    for (int i = 0; i < antecedente.length; i++) { //calculo la ganancia de cada atributo
      atributos[i] = i;
      if (seleccionado[i]) {
        info_gain[i] = infoG - infoGcondi(i, ejemplos, nEjemplos);
        n++;
      }
    }
    if (n > 2) { //2 es el minimo numero de antecedentes permitido
      //Ordeno de menor a mayor la informacion (y los atributos)
      for (int i = 0; i < antecedente.length - 1; i++) {
        if (seleccionado[i]) {
          for (int j = i + 1; j < antecedente.length; j++) {
            if (seleccionado[j]) {
              if (info_gain[i] > info_gain[j]) {
                double temp = info_gain[i];
                info_gain[i] = info_gain[j];
                info_gain[j] = temp;
                int temp2 = atributos[i];
                atributos[i] = atributos[j];
                atributos[j] = temp2;
              }
            }
          }
        }
      }
      //Ahora pongo o quito antecedentes
      for (int i = 0; (i < antecedente.length) && (n > 2); i++) {
        if (seleccionado[i]) {
          if (Randomize.Rand() > info_gain[i]) {
            seleccionado[i] = false;
            n--;
          }
        }
      }
    }
  }

  public void pruning(double[] norm_acc) {
    int[] atributos = new int[antecedente.length];
    int n = 0;
    for (int i = 0; i < antecedente.length; i++) {
      atributos[i] = i;
      if (seleccionado[i]) {
        n++;
      }
    }
    if (n > 2) { //2 es el minimo numero de antecedentes permitido
      //Ordeno de menor a mayor la informacion (y los atributos)
      for (int i = 0; i < antecedente.length - 1; i++) {
        if (seleccionado[i]) {
          for (int j = i + 1; j < antecedente.length; j++) {
            if (seleccionado[j]) {
              if (norm_acc[i] > norm_acc[j]) {
                double temp = norm_acc[i];
                norm_acc[i] = norm_acc[j];
                norm_acc[j] = temp;
                int temp2 = atributos[i];
                atributos[i] = atributos[j];
                atributos[j] = temp2;
              }
            }
          }
        }
      }
      //Ahora pongo o quito antecedentes
      for (int i = 0; (i < antecedente.length) && (n > 2); i++) {
        if (seleccionado[i]) {
          if (Randomize.Rand() > norm_acc[i]) {
            seleccionado[i] = false;
            n--;
          }
        }
      }
    }

  }

  public int compareTo(Object a) {
    if ( ( (Individuo) a).fitness < this.fitness) {
      return -1;
    }
    if ( ( (Individuo) a).fitness > this.fitness) {
      return 1;
    }
    return 0;
  }

}

