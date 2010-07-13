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

package keel.Algorithms.Decision_Trees.Target;

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

import org.core.Randomize;

public class Nodo {
  String clase;
  int claseInt;
  boolean nominal, isLeaf;
  String[] valoresNom;
  double valor;
  int numAtributos; //uno, dos ó tres (combinación lineal)
  int[] atributos;
  double[] pesos; //para la combinación lineal
  myDataset train;

  public Nodo() {
  }

  public Nodo(boolean isLeaf, myDataset train, double prob1, double prob2) {
    this.train = train;
    this.isLeaf = isLeaf;
    if (isLeaf) { //solo almaceno la clase
      clase = "?"; //se genera dinamicamente al evaluar/clasificar
    }
    else {
      nuevo(prob1,prob2);
    }
  }

  public void nuevo(double prob1,double prob2) {
    //primero elijo la variable:
    int atributo = Randomize.RandintClosed(0, train.getnInputs()-1);
    nominal = train.esNominal(atributo);
    splitSet(atributo,prob1,prob2);
  }

  public void splitSet(int atributo,double prob1,double prob2){
    if (nominal) { //el atributo es cualitativo
      //hago una división en dos conjuntos (aunque solo necesito guardar uno)
      int totalNominales = train.totalNominales(atributo);
      int nominalesEscogidos = Randomize.RandintClosed(1, totalNominales);
      valoresNom = new String[nominalesEscogidos];
      int[] noSeleccionados = new int[totalNominales];
      for (int i = 0; i < totalNominales; i++) {
        noSeleccionados[i] = i;
      }
      for (int i = 0; i < valoresNom.length; i++) {
        int seleccion = Randomize.RandintClosed(0, totalNominales-1);
        double aux = 1.0 * noSeleccionados[seleccion];
        valoresNom[i] = train.valorNominal(atributo, aux);
        noSeleccionados[seleccion] = noSeleccionados[totalNominales - 1];
        totalNominales--;
      }
      numAtributos = 1;
      atributos = new int[1];
      atributos[0] = atributo;
    }
    else { //es cuantitativo
      //Hay dos opciones: a) At <= valor, b) Combinacion lineal: b.1) w1·X1 + w2·X2 <= w1·s1+w2·s2, b.2) 3 atributos
      if (Randomize.Rand() <= prob1) { //At <= valor
        int ejemplo = Randomize.RandintClosed(0, train.size()-1);
        this.valor = train.getExample(ejemplo)[atributo];
        numAtributos = 1;
        atributos = new int[1];
        atributos[0] = atributo;
        pesos = new double[1];
        pesos[0] = 1;
      }
      else {
        if (Randomize.Rand() <= prob2) { //2 variables solo
          numAtributos = 2;
          atributos = new int[2];
          atributos[0] = atributo;
          int att2;
          do {
            att2 = Randomize.RandintClosed(0, train.getnInputs()-1);
            //System.err.println("Mira -> "+att2+"/"+train.getnInputs());
          }
          while ( (att2 == atributo) || (train.esNominal(att2)));
          atributos[1] = att2;
          int ejemplo = Randomize.RandintClosed(0, train.size()-1);
          double valor1 = train.getExample(ejemplo)[atributo];
          ejemplo = Randomize.RandintClosed(0, train.size()-1);
          double valor2 = train.getExample(ejemplo)[att2];
          pesos = new double[2];
          asignaPesos(1.0);
          valor = pesos[0] * valor1 + pesos[1] * valor2;
        }
        else { //tomo tres variables
          numAtributos = 3;
          atributos = new int[3];
          atributos[0] = atributo;
          int att2, att3;
          do {
            att2 = Randomize.RandintClosed(0, train.getnInputs()-1);
            //System.err.println("Mira -> "+att2+"/"+train.getnInputs());
          }
          while ( (att2 == atributo) || (train.esNominal(att2)));
          atributos[1] = att2;
          do {
            att3 = Randomize.RandintClosed(0, train.getnInputs()-1);
            //System.err.println("Mira -> "+att2+"/"+train.getnInputs());
          }
          while ( (att3 == atributo) || (att3 == att2) ||
                 (train.esNominal(att3)));
          atributos[2] = att3;
          int ejemplo = Randomize.RandintClosed(0, train.size()-1);
          double valor1 = train.getExample(ejemplo)[atributo];
          ejemplo = Randomize.RandintClosed(0, train.size()-1);
          double valor2 = train.getExample(ejemplo)[att2];
          ejemplo = Randomize.RandintClosed(0, train.size()-1);
          double valor3 = train.getExample(ejemplo)[att3];
          pesos = new double[3];
          asignaPesos(1.0);
          valor = pesos[0] * valor1 + pesos[1] * valor2 + pesos[2] * valor3;
        }
      }
    }

  }

  public void asignaPesos(double prob){
    for (int i = 0; i < numAtributos; i++) {
      if (Randomize.Rand() <= prob){
        pesos[i] = Randomize.RanddoubleClosed( -1, 1);
      }
    }

  }

  public Nodo copia() {
    Nodo n = new Nodo();
    n.clase = clase;
    n.claseInt = claseInt;
    n.isLeaf = isLeaf;
    n.train = train;
    if (!isLeaf) {
      n.nominal = nominal;
      n.numAtributos = numAtributos;
      n.atributos = new int[atributos.length];
      n.atributos = atributos.clone();
      if (nominal) {
        n.valoresNom = new String[valoresNom.length];
        n.valoresNom = valoresNom.clone();
      }
      else {
        n.valor = valor;
        n.pesos = new double[pesos.length];
        n.pesos = pesos.clone();
      }
    }
    return n;
  }

  public String printString() {
    String cadena = new String("");
    if (isLeaf) {
      cadena += train.nombreVar(train.getnInputs()) + " = " + clase;
    }
    else {
      //imprimir <at, op, valor>
      if (nominal) {
        cadena += train.nombreVar(atributos[0]); //atributo
        cadena += " = {";
        for (int i = 0; i < valoresNom.length; i++) {
          cadena += ", " + valoresNom[i];
        }
        cadena += "} ";
      }
      else {
        if (numAtributos == 1) {
          cadena += train.nombreVar(atributos[0]); //atributo
          cadena += " <= " + valor;
        }
        else { //combinacion lineal
          int i;
          for (i = 0; i < numAtributos - 1; i++) {
            cadena += pesos[i] + "*" + train.nombreVar(atributos[i]) + " + ";
          }
          cadena += pesos[i] + "*" + train.nombreVar(atributos[i]);
          cadena += " <= " + valor;
        }
      }
    }
    return cadena;
  }

  /*
    double valor;
    int numAtributos; //uno, dos ó tres (combinación lineal)
    int[] atributos;
    double[] pesos; //para la combinación lineal
   */
  public boolean cubre(int posEjemplo) {
    double[] ejemplo = train.getExample(posEjemplo);
    return cubre(ejemplo);
  }

  public boolean cubre(double [] ejemplo){
    boolean cubierto = false;
    if (nominal) {
      String valorN = train.valorNominal(atributos[0], ejemplo[atributos[0]]);
      for (int i = 0; (i < valoresNom.length) && (!cubierto); i++) {
        cubierto = (valoresNom[i] == valorN);
      }
    }
    else {
      if (numAtributos == 1) {
        cubierto = (ejemplo[atributos[0]] <= valor);
      }
      else {
        double valorAux = 0;
        for (int i = 0; i < numAtributos; i++) {
          valorAux += pesos[i] * ejemplo[atributos[i]];
        }
        cubierto = (valorAux <= valor);
      }
    }
    return cubierto;

  }

  public void copiar(Nodo copia) {
    this.clase = copia.clase;
    this.claseInt = copia.claseInt;
    this.isLeaf = copia.isLeaf;
    this.train = copia.train;
    if (!isLeaf) {
      this.numAtributos = copia.numAtributos;
      this.nominal = copia.nominal;
      this.atributos = new int[copia.atributos.length];
      this.atributos = copia.atributos.clone();
      if (nominal) {
        this.valoresNom = new String[copia.valoresNom.length];
        this.valoresNom = copia.valoresNom.clone();
      }
      else {
        this.valor = copia.valor;
        this.pesos = new double[copia.pesos.length];
        this.pesos = copia.pesos.clone();

      }
    }
  }
}

