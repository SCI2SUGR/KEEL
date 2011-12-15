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

package keel.Algorithms.Decision_Trees.DT_oblicuo;

/**
 * 
 *
 * @author Anonymous - 2011
 * @version 1.0
 * @since JDK1.6
 */
public class Nodo {
  String clase;
  boolean isLeaf;
  double[] pesos; //para la combinación lineal
  double valor, impureza;
  myDataset train;
  int ejemplos[], ejemplosI[], ejemplosD[];
  int n_ejemplos, n_ejemplos_i, n_ejemplos_d, nGenerations;

  public Nodo() {

  }

  public Nodo(myDataset train, int n_ejemplos, int[] ejemplos, int nGenerations) {
    this.train = train;
    this.n_ejemplos = n_ejemplos;
    this.ejemplos = new int[ejemplos.length];
    this.ejemplos = ejemplos.clone();
    this.nGenerations = nGenerations;
    //Si todos los ejemplos son de la misma clase --> Hoja
    isLeaf = true;
    int clase = train.getOutputAsInteger(ejemplos[0]);
    int i;
    for (i = 1; (i < n_ejemplos) && (isLeaf); i++) {
      isLeaf = (train.getOutputAsInteger(ejemplos[i]) == clase);
    }
    pesos = new double[train.getnInputs()];
    if (isLeaf) {
      this.clase = train.getOutputAsString(ejemplos[0]);
    }
    /*else if ( ( (n_ejemplos < 10) && (totalNodos > 25)) || (totalNodos > 50)) { //ya tengo que ir cortando no?
      isLeaf = true;
      //Clase == clase mayoritaria
      int clases[] = new int[train.getnClasses()];
      for (i = 0; i < clases.length; i++) {
        clases[i] = 0;
      }
      for (i = 0; i < n_ejemplos; i++) {
        clases[train.getOutputAsInteger(ejemplos[i])]++;
      }
      int max = 0;
      for (i = 1; i < clases.length; i++) {
        if (clases[i] > clases[max]) {
          max = i;
        }
        this.clase = train.getOutputValue(max);
      }
    }*/
    else { //Hay que crear un nodo con el procedimiento chungo
      crearNodo();
    }
    if (this.impureza == Double.MIN_VALUE) { //No he encontrado ningun corte
      isLeaf = true;
      //Clase == clase mayoritaria
      int clases[] = new int[train.getnClasses()];
      for (i = 0; i < clases.length; i++) {
        clases[i] = 0;
      }
      for (i = 0; i < n_ejemplos; i++) {
        clases[train.getOutputAsInteger(ejemplos[i])]++;
      }
      int max = 0;
      for (i = 1; i < clases.length; i++) {
        if (clases[i] > clases[max]) {
          max = i;
        }
        this.clase = train.getOutputValue(max);
      }
    }
  }

  private void crearNodo() {
    //Primer paso: Calcular el mejor "axis-paralel" test
    calculaAxisParalel();
    //Segundo paso
    //Solo si el atributo no es nominal (si no no me vale para nada tanta parafernalia!!)
    int i;
    for (i = 0; pesos[i] != 0; i++) {
      ;
    }
    if (!train.esNominal(i)) {
      if (n_ejemplos > 2 * train.getnInputs()) { //condicion algoritmo
        crearHyperplane();
      }
    }
  }

  private void calculaAxisParalel() {
    //Pruebo todos los posibles tests hasta encontrar el de minima impureza (greedy)
    //double impureza = Double.MAX_VALUE;
    double impureza = Double.MIN_VALUE;
    int atributo = 0;
    double mi_valor = 0;
    for (int i = 0; i < train.getnInputs(); i++) {
      for (int j = 0; j < train.totalValores(i); j++) {
        valor = train.valor(i, j);
        for (int k = 0; k < train.getnInputs(); k++) {
          pesos[k] = 0;
        }
        pesos[i] = -1;
        double imp = calculaImpureza();
        if (imp > impureza) {
          impureza = imp;
          mi_valor = valor;
          atributo = i;
          //System.out.println("Escogido atributo["+atributo+"] y valor["+valor+"]: "+impureza+" ("+n_ejemplos_i+"/"+n_ejemplos_d+")");
        }
      }
    }
    for (int k = 0; k < train.getnInputs(); k++) {
      pesos[k] = 0;
    }
    pesos[atributo] = -1;
    valor = mi_valor;
    calculaImpureza(); //esto solo me vale para recalcular n_ejemplos
    /*
     System.out.println("Escogido atributo[" + atributo + "] y valor[" + valor +
                       "]: " + impureza + " (" + n_ejemplos_i + "/" +
                       n_ejemplos_d + ")");
    */
    //System.out.println("FIN -> "+impureza+" ("+n_ejemplos_i+"/"+n_ejemplos_d+")");
    //System.exit(0);
  }

  public boolean cubre(double[] ejemplo) {
    double aux = 0;
    for (int j = 0; j < ejemplo.length; j++) {
      aux += ejemplo[j] * pesos[j];
    }
    aux += valor;
    return (aux >= 0);
  }

  private double calculaImpureza() {
    //double imp = Double.MAX_VALUE;
    double imp = Double.MIN_VALUE;
    int ejemplos_ii[], ejemplos_di[];
    n_ejemplos_i = n_ejemplos_d = 0;
    ejemplosD = new int[n_ejemplos];
    ejemplosI = new int[n_ejemplos];
    ejemplos_ii = new int[train.getnClasses()];
    ejemplos_di = new int[train.getnClasses()];
    //calculo lo necesario
    for (int i = 0; i < n_ejemplos; i++) {
      double[] ejemplo = train.getExample(ejemplos[i]);
      int clase = train.getOutputAsInteger(ejemplos[i]);
      if (this.cubre(ejemplo)) {
        ejemplosI[n_ejemplos_i] = ejemplos[i];
        n_ejemplos_i++;
        ejemplos_ii[clase]++;
      }
      else {
        ejemplosD[n_ejemplos_d] = ejemplos[i];
        n_ejemplos_d++;
        ejemplos_di[clase]++;
      }
    }
    if ( (n_ejemplos_i > 0) && (n_ejemplos_d > 0)) {
      imp = 0;
      for (int i = 0; i < train.getnClasses(); i++) {
        imp += Math.abs(
            ( (1.0 * ejemplos_ii[i] / n_ejemplos_i) -
             (1.0 * ejemplos_di[i] / n_ejemplos_d)));
        //System.err.println("imp["+i+"]: "+imp+" ("+( (1.0 * ejemplos_ii[i] / n_ejemplos_i) - (1.0 * ejemplos_di[i] / n_ejemplos_d))+") n_i:"+ejemplos_ii[i]+", n_d:" +ejemplos_di[i]);
      }
      imp *= imp;
      imp *= (n_ejemplos_i * n_ejemplos_d);
      imp /= (1.0 * n_ejemplos * n_ejemplos);
      /*if (valor == 0.1){
       System.err.println("impT: "+imp+" n_i:"+n_ejemplos_i+", n_d:" +n_ejemplos_d);
             }*/
    }
    impureza = imp;
    return imp;
  }

  private void crearHyperplane() {
    Poblacion p = new Poblacion(train, n_ejemplos, ejemplos, this.nGenerations,
                                pesos, valor);
    p.genetico();
    if (p.mejor_fitness > impureza) {
      double[] aux = p.mejorSolucion();
      for (int i = 0; i < pesos.length; i++) {
        pesos[i] = aux[i];
      }
      valor = aux[aux.length - 1];
      calculaImpureza(); //actualiza los valores de ejemplosD y ejemplosI
    }
  }

  public String printString() {
    String cadena = new String("");
    if (isLeaf) {
      cadena += train.nombreVar(train.getnInputs()) + " = " + clase + " (" +
          n_ejemplos + ")";
    }
    else {
      for (int i = 0; i < train.getnInputs(); i++) {
        if (pesos[i] != 0) {
          cadena += pesos[i] + "*" + train.nombreVar(i) + " + ";
        }
      }
      cadena += valor + " >= 0 ";
    }
    return cadena;
  }

  public Nodo copia() {
    Nodo n = new Nodo();
    n.clase = this.clase;
    n.isLeaf = this.isLeaf;
    n.pesos = this.pesos.clone();
    n.valor = this.valor;
    n.ejemplos = this.ejemplos.clone();
    n.ejemplosD = this.ejemplosD.clone();
    n.ejemplosI = this.ejemplosI.clone();
    n.n_ejemplos = this.n_ejemplos;
    n.n_ejemplos_d = this.n_ejemplos_d;
    n.n_ejemplos_i = this.n_ejemplos_i;
    n.nGenerations = this.nGenerations;
    return n;
  }

}

