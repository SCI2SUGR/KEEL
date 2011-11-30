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
 * @author 
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.StringTokenizer;

public class BaseR {

  ArrayList<Regla> baseReglas;
  myDataset train;
  int umbralS;

  public BaseR() {
    baseReglas = new ArrayList<Regla> ();
  }

  /**
   * Obtengo la base de reglas a traves del fichero de reglas (extraido a partir del arbol de decision)
   * @param reglas String
   * @param train myDataset conjunto de datos de entrenamiento
   */
  public BaseR(myDataset train, String reglas) {
    baseReglas = new ArrayList<Regla> ();
    this.train = train;
    StringTokenizer tokens = new StringTokenizer(reglas, "\n");
    while (tokens.hasMoreTokens()) {
      String regla = tokens.nextToken();
      //System.err.println("Regla -> "+regla);
      Regla r = new Regla(train, regla);
      baseReglas.add(r);
    }
  }

  public BaseR genetico(int type, int S, int nGenerations, int popSize,
                        double crossProb, double mutProb) {
    BaseR br = new BaseR();
    br.train = this.train;
    this.umbralS = S;
    boolean hacerLarge = false;
    boolean[] ejemplosTr = new boolean[train.size()]; //ejemplos para GA-Large
    for (int i = 0; i < ejemplosTr.length; i++) {
      ejemplosTr[i] = false;
    }
    //Por si si, o por si no, calculo el accuracy normalizado para cada atributo
    double [] norm_acc = calculaAccuracy();

    for (int i = 0; i < baseReglas.size(); i++) {
      if (baseReglas.get(i).cubiertos() < S) {
        if (type == DT_GA.GA_SMALL) {
          for (int k = 0; k < train.getnClasses(); k++){
            ArrayList<Regla> reglas = new ArrayList<Regla> ();
            Poblacion p = new Poblacion(i, baseReglas.get(i).copia(), nGenerations,
                                        popSize, crossProb, mutProb, train, train.nombreClase(k));
            p.GA_Small();
            reglas = p.dameReglas();
            for (int j = 0; j < reglas.size(); j++) {
              br.baseReglas.add(reglas.get(j).copia());
            }
          }
        }
        else {
          hacerLarge = true;
          int[] cubiertos = baseReglas.get(i).ejemplosCubiertos.clone();
          int nCubiertos = baseReglas.get(i).cubiertos();
          for (int j = 0; j < nCubiertos; j++) {
            ejemplosTr[cubiertos[j]] = true;
          }
        }
      }
    }
    if (hacerLarge) {
      ArrayList<Regla> reglas = new ArrayList<Regla> ();
      Poblacion p = new Poblacion(ejemplosTr, nGenerations, popSize, crossProb,
                                  mutProb, train, norm_acc);
      p.GA_Large();
      reglas = p.dameReglas();
      for (int j = 0; j < reglas.size(); j++) {
        br.baseReglas.add(reglas.get(j).copia());
      }
    }
    return br;
  }

  public String printString() {
    String cadena = new String("");
    cadena += "Number of Rules: " + baseReglas.size() + "\n";
    for (int i = 0; i < baseReglas.size(); i++) {
      cadena += "Rule[" + (i + 1) + "]: " + baseReglas.get(i).printString();
    }
    return cadena;
  }

  public int size() {
    return baseReglas.size();
  }

  /**
   * Detecta las reglas que cubren un small-disjunt
   */
  public void cubrirEjemplos() {
    for (int i = 0; i < this.size(); i++) {
      baseReglas.get(i).cubrirEjemplos();
    }
  }

  /**
   * Clasifica un ejemplo
   * @param tree true si la regla es de tipo "arbol", false si es de tipo "GA"
   * @param ejemplo double[] el ejemplo a clasificar (valores de los atributos de entrada)
   * @param clase_ StringBuffer el valor de la clase que se va a devolver
   * @return boolean true si la regla que lo clasifica pertence a un small disjunct
   */
  public boolean clasifica(boolean tree, double [] ejemplo, StringBuffer clase_){
    boolean smallDisjunct = false;
    String clase = "<unclassified>";
    if (tree){
      int i;
      for (i = 0; (i < size()) && (clase.equals("<unclassified>")); i++) {
        if (baseReglas.get(i).cubre(ejemplo)) {
          clase = baseReglas.get(i).clase;
        }
      }
      i--; //suma uno al salir
      smallDisjunct = (baseReglas.get(i).cubiertos() < umbralS);
    }else{
      int i;
      double pesoMax = 0.0;
      String claseAux;
      for (i = 0; i < size(); i++) {
        //System.err.print(baseReglas.get(i).printString());
        if (baseReglas.get(i).cubre(ejemplo)) {
          claseAux = baseReglas.get(i).clase;
          double peso = baseReglas.get(i).fitness;
          if (peso > pesoMax){
            clase = claseAux;
            pesoMax = peso;
          }
        }
      }
      //System.err.println("Predicho -> "+clase);
    }
    clase_.append(clase);
    return smallDisjunct;
  }

  private double [] calculaAccuracy(){
    int atts = train.getnInputs();
    double [] acc = new double[atts];
    int attsNoUsados = 0;
    boolean [] noUsado = new boolean[atts];
    double minAcc = 1.0;
    int [] clasificados = new int[atts];
    int [] correctamenteClas = new int[atts];
    for(int i = 0; i < atts; i++){
      boolean aparece = false;
      for (int j = 0; j < this.size(); j++){ //Compruebo en cuantos caminos esta el atributo "i"
        if (baseReglas.get(j).contieneAtributo(i)){
          clasificados[i] += baseReglas.get(j).cubiertos();
          correctamenteClas[i] += baseReglas.get(j).cubiertosOK();
          aparece = true;
        }
      }
      noUsado[i] = !aparece;
      if (!aparece){
        attsNoUsados++;
      }else{
        acc[i] = 1.0* correctamenteClas[i] / clasificados[i];
        if (acc[i] < minAcc){
          minAcc = acc[i];
        }
      }
    }
    double totalAcc = 0.0;
    for (int i = 0; i < atts; i++){
      if (noUsado[i]){ //si no esta usado
        acc[i] = minAcc / attsNoUsados;
      }
      totalAcc += acc[i];
    }
    for (int i = 0; i < atts; i++){
      acc[i] /= totalAcc; //normalizar
      //System.out.println("Accuracy["+i+"] = "+acc[i]);
    }
    return acc;
  }

}

