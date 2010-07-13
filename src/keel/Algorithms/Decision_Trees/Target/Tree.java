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

public class Tree
    implements Comparable {

  Tree hijoD, hijoI; //los dos hijos (arbol binario)
  Tree padre; //mi padre
  Nodo nodo; //informacion relevante (<at,op,valor> ó clase>
  boolean isLeaf; //si corresponde a un nodo hoja
  double fitness;
  boolean n_e, marcado;
  myDataset train;
  int indiceNodoT, indiceNodo;
  double prob1_var, prob2_var;

  /** Number of Leafs in the tree */
  public static int nodosT, nodos;
  public static int maxNodos = 100;
  public static int bienCubiertos;

  /** Number of examples for each leaf/class **/
  public static int ejemplos[][];

  public Tree() {
  }

  public Tree(Tree pae, myDataset train, double pSplit, boolean primero,
              double prob1, double prob2) {
    this.train = train;
    this.prob1_var = prob1;
    this.prob2_var = prob2;
    padre = pae;
    if (primero) {
      isLeaf = false;
      nodosT = 0;
      nodos = 0;
      indiceNodo = nodos;
      indiceNodoT = -1;
      nodo = new Nodo(isLeaf, train, prob1, prob2);
      hijoD = new Tree(this, train, pSplit, false, prob1, prob2);
      hijoI = new Tree(this, train, pSplit, false, prob1, prob2);
    }
    else {
      if ( (nodos < maxNodos) && (Randomize.Rand() < pSplit)) {
        //yo soy un nodo interior
        isLeaf = false;
        nodo = new Nodo(isLeaf, train, prob1, prob2);
        hijoD = new Tree(this, train, pSplit, false, prob1, prob2);
        hijoI = new Tree(this, train, pSplit, false, prob1, prob2);
        nodos++;
        indiceNodo = nodos;
        indiceNodoT = -1;
      }
      else {
        indiceNodoT = nodosT;
        indiceNodo = -1;
        nodosT++;
        isLeaf = true; //es el último
        nodo = new Nodo(isLeaf, train, prob1, prob2);
      }
    }
    n_e = true;
  }

  public Tree copia(Tree padre) {
    Tree t = new Tree();
    t.padre = padre;
    /*if (t.padre != null){
      t.padre = this.padre.copia();
         }else{
      t.padre = null;
         }*/
    try {
      t.nodo = nodo.copia();
    }
    catch (java.lang.NullPointerException e) {
      System.err.println(this.printString());
      System.exit(0);
    }
    t.fitness = this.fitness;
    t.n_e = this.n_e;
    t.train = this.train;
    t.indiceNodo = this.indiceNodo;
    t.isLeaf = this.isLeaf;
    t.prob1_var = this.prob1_var;
    t.prob2_var = this.prob2_var;
    if (!this.isLeaf) {
      t.hijoD = this.hijoD.copia(t);
      t.hijoI = this.hijoI.copia(t);
    }
    return t;
  }

  public String printString() {
    StringBuffer text = new StringBuffer();
    printTree(0, text);
    return text.toString();
  }

  /** Function to print the tree.
   *
   * @param depth			Depth of the node in the tree.
   * @param text			The tree.
   *
   */
  private void printTree(int depth, StringBuffer text) {
    String aux = "";

    for (int k = 0; k < depth; k++) {
      aux += "\t";
    }

    text.append(aux);
    if (isLeaf) {
      //text.append(nodo.printString() + " ["+indiceNodoT+"]\n");
      text.append(nodo.printString() + " \n");
    }
    else {
      /*text.append("[" + indiceNodo + "/" + marcado + "] if ( " +
                  nodo.printString() +
                  " ) then{\n");*/
      text.append(" if ( " + nodo.printString() + " ) then{\n");
      hijoI.printTree(depth + 1, text);
      text.append(aux + "else{ \n");
      hijoD.printTree(depth + 1, text);
    }
    text.append(aux + "}\n");
  }

  private void calculaNodosTerminales() {
    if (isLeaf) {
      indiceNodoT = nodosT;
      nodosT++;
    }
    else {
      hijoI.calculaNodosTerminales();
      hijoD.calculaNodosTerminales();
    }
  }

  private void calculaNodos() {
    if (isLeaf) {
      //nada
      marcado = true;
    }
    else {
      indiceNodo = nodos;
      nodos++; //soy un  nodo
      marcado = false; //para contar luego
      hijoI.calculaNodos();
      hijoD.calculaNodos();
    }
  }

  public int elijeNodo() {
    nodos = 0;
    this.calculaNodos();
    int nodo = Randomize.RandintClosed(0, nodos - 2);
    return nodo;
  }

  /*public Tree clone() {
    Tree arbol = new Tree();
    //copiar cosas...

    return arbol;
     }*/

  public double evaluar() {
    double Dbic;
    int clases = train.getnClasses();
    //Recalculo nodosT porque es variable de clase y da mal rollo...
    nodosT = 0;
    calculaNodosTerminales();

    ejemplos = new int[nodosT][clases];
    boolean[] ejemplitos = new boolean[train.size()];
    for (int i = 0; i < ejemplitos.length; i++) {
      ejemplitos[i] = true; //todos cubiertos...
    }
    //System.err.println("Arbol: \n"+this.printString());
    bienCubiertos = 0;
    algoritmoEvaluacion(ejemplitos);
    //Ahora hago el recuento:
    double DT, dt;
    Dbic = DT = 0;
    boolean salir = false;
    for (int i = 0; (i < nodosT) && (!salir); i++) {
      double aux = 0;
      int nt = 0;
      for (int j = 0; j < clases; j++) {
        nt += ejemplos[i][j];
      }
      for (int j = 0; (j < clases) && (!salir); j++) {
        if (ejemplos[i][j] > 0) {
          aux += ejemplos[i][j] * Math.log(1.0 * nt/ejemplos[i][j]);
          //System.err.println("Nodo["+i+"], Clase["+j+"]: "+(1.0 * nt / ejemplos[i][j]));
        }/*else {
          aux = Double.MAX_VALUE;
          salir = true;
        }*/
      }
      //System.err.println("Mira -> "+aux);
      dt = 2 * aux;
      DT += dt;
    }
    Dbic = DT + ( (clases + 1) * nodosT - 1) * Math.log(train.size());
    n_e = false;
    fitness = Dbic;
    return Dbic;
  }

  public double evaluar2() {
    double Dbic;
    int clases = train.getnClasses();
    //Recalculo nodosT porque es variable de clase y da mal rollo...
    nodosT = 0;
    calculaNodosTerminales();

    ejemplos = new int[nodosT][clases];
    boolean[] ejemplitos = new boolean[train.size()];
    for (int i = 0; i < ejemplitos.length; i++) {
      ejemplitos[i] = true; //todos cubiertos...
    }
    //System.err.println("Arbol: \n"+this.printString());
    bienCubiertos = 0;
    algoritmoEvaluacion(ejemplitos);
    //Ahora hago el recuento:
    double DT, dt;
    Dbic = DT = 0;
    boolean salir = false;
    int nt = 0;
    /*for(int i = 0; i < nodosT; i++){
        nt+=ejemplos[indiceNodoT][nodo.claseInt];
         }*/
    Dbic = train.size() - bienCubiertos;
    /*if (nodosT == 1){
      System.err.println("Mira -> " + bienCubiertos);
         }*/
    //System.err.println("DT: "+DT+", Jarl:"+( (clases + 1) * nodosT - 1) * Math.log(train.size()));
    n_e = false;
    fitness = Dbic;
    return Dbic;
  }

  private void algoritmoEvaluacion(boolean[] ejemplitos) {
    if (isLeaf) {
      //pues me cojo todos los ejemplitos y los guardo en ejemplos para este nodooooooo
      for (int i = 0; i < ejemplitos.length; i++) {
        if (ejemplitos[i]) { //esta cubierto en este camino
          //System.err.println("Para el nodo["+indiceNodoT+"/"+nodosT+"] escojo la clase "+train.getOutputAsInteger(i));
          ejemplos[indiceNodoT][train.getOutputAsInteger(i)]++; //Un ejemplo mas para el nodo/clase
        }
      }
      //Asocio la clase a la de mas ejemplos cubiertos
      int clase = 0;
      int ejemplosCubiertos = ejemplos[indiceNodoT][0];
      int[] cubiertos = new int[train.getnClasses()];
      cubiertos[0] = ejemplosCubiertos;
      for (int i = 1; i < train.getnClasses(); i++) {
        if (ejemplos[indiceNodoT][clase] < ejemplos[indiceNodoT][i]) {
          clase = i;
          ejemplosCubiertos = ejemplos[indiceNodoT][i];
        }
        cubiertos[i] = ejemplos[indiceNodoT][i];
      }
      nodo.clase = train.nombreClase(clase);
      nodo.claseInt = clase;
      bienCubiertos += ejemplosCubiertos;
      /*System.err.print("Para el nodo["+indiceNodo+"]: ");
             for (int i = 0; i < train.getnClasses(); i++){
       System.err.print("Clase "+train.nombreClase(i)+" : "+cubiertos[i]+", ");
             }
             System.err.println("");*/
    }
    else {
      //System.err.println("Para el nodo["+indiceNodo+"/"+nodosT+"] sigo investigando...");
      //genero dos conjuntos de ejemplitos segun esten cubiertos por el nodo
      boolean[] ejemplitosI = new boolean[ejemplitos.length];
      boolean[] ejemplitosD = new boolean[ejemplitos.length];
      ejemplitosI = ejemplitos.clone();
      ejemplitosD = ejemplitos.clone();
      for (int i = 0; i < ejemplitos.length; i++) {
        if (ejemplitos[i]) {
          if (nodo.cubre(i)) {
            ejemplitosD[i] = false; //lo cubre el nodo izquierdo y no esta en los ejemplos del derecho
          }
          else {
            ejemplitosI[i] = false; //al reves que el anterior
          }
        }
      }
      hijoI.algoritmoEvaluacion(ejemplitosI);
      hijoD.algoritmoEvaluacion(ejemplitosD);
    }
  }

  public void nodeSwap(int nodoYo, int nodoPadre, Tree padre) {
    //primero busco los dos nodos
    nodos = 0;
    this.calculaNodos();
    Nodo miNodo = buscar(nodoYo);
    nodos = 0;
    padre.calculaNodos();
    Nodo suNodo = padre.buscar(nodoPadre);
    //ahora simplemente intercambio su informacion
    Nodo aux = miNodo.copia();
    miNodo.copiar(suNodo);
    suNodo.copiar(aux);
  }

  private Nodo buscar(int codigoNodo) {
    this.marcado = true;

    if (indiceNodo == codigoNodo) {
      return nodo.copia();
    }
    else {
      if (!hijoI.marcado) {
        return hijoI.buscar(codigoNodo);
      }
      else if (!hijoD.marcado) {
        return hijoD.buscar(codigoNodo);
      }
      return padre.buscar(codigoNodo);
    }
  }

  public void treeSwap(int nodoYo, int nodoPadre, Tree padre) {
    //primero busco los dos nodos
    nodos = 0;
    this.calculaNodos();
    Nodo miNodo = buscar(nodoYo);
    nodos = 0;
    padre.calculaNodos();
    Nodo suNodo = padre.buscar(nodoPadre);
    //ahora simplemente intercambio los punteros
    Nodo aux = miNodo;
    miNodo = suNodo;
    suNodo = aux;
  }

  public void splitSet(int nodo) {
    //primero busco los dos nodos
    nodos = 0;
    this.calculaNodos();
    Nodo miNodo = buscar(nodo);
    miNodo.splitSet(miNodo.atributos[0], prob1_var, prob2_var);
  }

  public void splitRule(int nodo) {
    //primero busco los dos nodos
    nodos = 0;
    this.calculaNodos();
    Nodo miNodo = buscar(nodo);
    if (miNodo.numAtributos > 1) {
      if (Randomize.Rand() < 0.5) {
        miNodo.asignaPesos(0.5);
      }
      else {
        miNodo.nuevo(prob1_var, prob2_var);
      }
    }
    else {
      miNodo.nuevo(prob1_var, prob2_var);
    }
  }

  public String clasificar(double[] ejemplo) {
    if (isLeaf) {
      return nodo.clase;
    }
    else {
      if (nodo.cubre(ejemplo)) {
        return hijoI.clasificar(ejemplo);
      }
      else {
        return hijoD.clasificar(ejemplo);
      }
    }

  }

  /**
   * Funcion de minimizacion del fitness
   * @param a Object Otro arbol
   * @return int el valor para la comparativa
   */
  public int compareTo(Object a) {
    if ( ( (Tree) a).fitness > this.fitness) {
      return -1;
    }
    if ( ( (Tree) a).fitness < this.fitness) {
      return 1;
    }
    return 0;
  }

}

