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

package keel.Algorithms.Decision_Trees.SLIQ;

import java.util.*;


/**
 * 
 * This class implements the nodes of SLIQ decision tree.
 * @author Francisco Charte Ojeda
 * @version 1.0 (28/12/09 - 10/1/10)
 */
public class Node {

    /** Associated histogram. The first index corresponds to the class index and
     * the second to the two children (branch) (0-left, 1-right). Each value determinates
     * the frequency of that class in the given branch.  
     * The index 0 (class 0) is used to store the total number for each branch. 
     */
    private int[][] histograma;
    /** Gini index for the node. */
    private double indiceGini;
    /** Best starting gain (Gini) */
    private double mejorGini;
    /** References to the children (nodes). */
    private Node[] children;
    /** Class associated to the node if it is a leaf. */
    private int primeraClase;
    /** Leaf indentifier. */
    private boolean esHoja;
    /** Dataset associted to the node. */
    private Vector<ListaAtributos>[] data;
    /** Node's father. Null for the root. */
    private Node parent;
    /**
     * Continuous value (continuous attributes) or subset index (discrete attributes with the best cut. */
    private double mejorValor;
    /**The attribute used to divide the set in a leaf node. */
    private int mejorAtributo;
    /** Node cost (pruning).*/
    private int coste = -1;
    /** Number of classes. */
    private int numClases;

    /**
     * Paramenter constructor. The node structures will be initialized with the parameters given.
     * @param nClases number of classes.
     * 
     */
    public Node(int nClases) {
        // Inicializar los indicadores
        indiceGini = 1;
        mejorGini = 0;
        mejorAtributo = primeraClase = -1;
        esHoja = true;

        // Nodos hijo nulos
        children = new Node[2];
        children[0] = children[1] = null;

        // Inicializar tambiÃ©n el histograma asociado al nodo
        histograma = new int[nClases + 1][2];
        for (int indice = 0; indice <= nClases; indice++) {
            histograma[indice][0] = histograma[indice][1] = 0;
        }

        // Conservar el nÃºmero de clases
        numClases = nClases;
        parent = null;
    }


    /**
     * Adds an element of the class given to the node.
     * @param clase given class.
     */
    public void agregaElemento(int clase) {
        // Si es el primer elemento agregado se toma su clase como principal
        if (primeraClase == -1) {
            primeraClase = clase;
        } // Si se agregan elementos de una clase distinta a la principal
        else if (primeraClase != clase) {
            esHoja = false; // el nodo no puede considerarse una hoja
        }
        // Contabilizar el nuevo dato en la clase que le corresponda
        histograma[clase + 1][0]++;
        histograma[0][0]++; // y en el total
    }

    /**
     * Divides the nodes into its two children.
     */
    public void divide() {
        children[0] = new Node(histograma.length);
        children[1] = new Node(histograma.length);

        // Actualizar los punteros al padre
        children[0].parent = this;
        children[1].parent = this;
    }

    /**
     * Changes an element with the class given from the left leaf to the right one.
     * @param clase class index from the element to be changed.
     */
    protected void actualizaHistograma(int clase) {
        histograma[clase + 1][0]--;
        histograma[clase + 1][1]++;

        histograma[0][0]--;
        histograma[0][1]++;
    }

    /**
     * Actualizes the main class of the node by considering the frecuency of each class.
     * Used after pruning.
     */
    public void actualizaClasePrincipal() {
        int frecuenciaClase = 0;

        // Se recorren las clases
        for (int indice = 1; indice <= numClases; indice++) {
            // quedÃ¡ndose siempre con la clase mÃ¡s representativa
            if (histograma[indice][0] + histograma[indice][1] > frecuenciaClase) {
                frecuenciaClase = histograma[indice][0] + histograma[indice][1];
                primeraClase = indice - 1;
            }
        }
    }

    /**
     * Checks a given cut and computes a possible improvement. (Discrete attributes).
     * @param indAtributo   attribute index.
     * @param listaClases   classes list.
     * @param atributo      attribute reference.
     */
    public void pruebaCorte(int indAtributo, ListaClases[] listaClases, Attribute atributo) {
        // NÃºmero mÃ¡ximo de valores a comprobar de manera exhaustiva, segÃºn la
        // descripciÃ³n del algoritmo SLIQ de Mehta
        final int MAXSETSIZE = 10; 

        int numValores = atributo.numValues(), // NÃºmero de valores distintos que puede tomar el atributo
            numClases = listaClases.length;    // NÃºmero de clases a las que pueden pertenecer

        // Matriz de ocurrencias por valor y clase
        int[][] ocurrencias = new int[numClases][numValores];

        int totalOcurrencias = 0;

        // Se inicializa todo a 0
        for (int clase = 0; clase < numClases; clase++) {
            for (int valor = 0; valor < numValores; valor++) {
                ocurrencias[clase][valor] = 0;
            }
        }

        // Se recorre la lista de valores del nodo
        for (int indice = 0; indice < data[indAtributo].size(); indice++) {
            // Y se incrementa en la matriz de ocurrencias el elemento que corresponda
            int clase = listaClases[data[indAtributo].get(indice).indice].clase;
            int valor = (int) data[indAtributo].get(indice).valor;

            ocurrencias[clase][valor]++;
            totalOcurrencias++;
        }

        // --- Proceso para obtener el subconjunto con el mejor Gini ---
        double giniActual, giniSubconjunto = 1;
        int mejorSubconjunto = 0;

        // Ciclos para recorrer todas las combinaciones posibles
        int ciclos = (int) Math.pow(2, numValores) - 1;

        // Si no se supera el umbral, pueden probarse todas las combinaciones posibles
        if (atributo.numValues() <= MAXSETSIZE) {

            for (int indice = 0; indice < ciclos; indice++) {
                // Se obtiene el Ã­ndice Gini para este subconjunto
                giniActual = calculaGini(indice, ocurrencias, numValores, numClases, totalOcurrencias);
                // Si es mejor que el mejor encontrado hasta ahora
                if (giniActual < giniSubconjunto) {
                    mejorSubconjunto = indice; // Se guarda el Ã­ndice del subconjunto
                    giniSubconjunto = giniActual; // y el nuevo Gini
                }
            }
        } else { // Hay demasiados valores, usar algoritmo greedy
            mejorSubconjunto = 0;
            ciclos++;
            boolean mejorado;
            do {
                mejorado = false; // En cada ciclo se asume que no hay mejora
                for (int indice = 1; indice < ciclos; indice *= 2) {
                    // se comprueba el subconjunto
                    if ((mejorSubconjunto & indice) == 0) {
                        giniActual = calculaGini(mejorSubconjunto + indice, ocurrencias, numValores, numClases, totalOcurrencias);
                        if (giniActual < giniSubconjunto) {
                            // Si hay mejora
                            mejorSubconjunto += indice;
                            giniSubconjunto = giniActual;
                            mejorado = true;
                        }
                    }
                }
            } while (mejorado); // Mientras se mejore
        }

        // Anotar el mejor corte posible para este atributo
        indiceGini = giniSubconjunto;
        mejorAtributo = indAtributo;
        // Se almacena como valor el Ã­ndice del mejor subconjunto encontrado
        mejorValor = mejorSubconjunto;
    }

    /** Checks a given cut and computes a possible improvement. (Continuous attributes).
     *
     * @param atributo   attribute index.
     * @param listaClases   classes list.
     * @param valor     cut value to check.
     * @param siguiente next value.
     */
    public void pruebaCorte(int atributo, ListaClases[] listaClases, double valor, double siguiente) {
        // Calcular el valor intermedio entre valor y el siguiente (la lista estÃ¡ ordenada)
        double valorMedio = valor + (siguiente - valor) / 2;

        // Se guarda el histograma actual del nodo
        int[][] copiaHistograma = histograma.clone();

        // Creo los dos nodos en los que se dividirÃ­a la lista de datos
        Node nodoI = new Node(histograma.length), nodoD = new Node(histograma.length);

        // Se recorre la lista de valores del atributo indicado
        for (int indice = 0; indice < data[atributo].size(); indice++) // y se agrega la distribuciÃ³n en el nodo que corresponda
        {
            if (data[atributo].get(indice).valor <= valorMedio) {
                nodoI.agregaElemento(listaClases[data[atributo].get(indice).indice].clase);
            } else {
                // Si el nodo cambia a la rama derecha
                nodoD.agregaElemento(listaClases[data[atributo].get(indice).indice].clase);
                // hay que actualizar tambiÃ©n el histograma de este nodo
                actualizaHistograma(listaClases[data[atributo].get(indice).indice].clase);
            }
        }

        // Calcular el Ã­ndice Gini
        indiceGini = calculaGini();

        // ProporciÃ³n de entradas en cada nodo
        double propIzq = nodoI.histograma[0][0] / (nodoI.histograma[0][0] + nodoD.histograma[0][0]),
                propDcho = nodoD.histograma[0][0] / (nodoI.histograma[0][0] + nodoD.histograma[0][0]);

        // CÃ¡lculo de la ganancia que se obtendrÃ­a
        double GiniGain = indiceGini -
                nodoI.calculaGini() * propIzq -
                nodoD.calculaGini() * propDcho;

        // Si el GiniGain es mejor que mejorGini, guardarlo 
        if (GiniGain > mejorGini) {
            mejorGini = GiniGain;
            mejorValor = valorMedio;         // guardar los datos
            mejorAtributo = atributo;
        }

        // Recuperar el histograma original del nodo, para realizar correctamente
        // pruebas de cortes posteriores
        histograma = copiaHistograma;
    }

    /**
     * Computes and returns the Gini index for the node (continuous attributes).
     * @return the Gini index for the node
     */
    public double calculaGini() {
        // Tomar los totales
        double totalIzquierdo = histograma[0][0],
                totalDerecho = histograma[0][1];
        double total = totalIzquierdo + totalDerecho;
        double probIzquierdo = 0, probDerecho = 0, prob = 0;

        // Si todos los datos estÃ¡n en una rama
        if (totalIzquierdo == 0 || totalDerecho == 0) {
            return 1; // no hay nada que calcular
        }

        // Acumular las probabilidades
        for (int indice = 1; indice < histograma.length; indice++) {
            prob = histograma[indice][0] / totalIzquierdo;
            probIzquierdo += prob * prob;
            prob = histograma[indice][1] / totalDerecho;
            probDerecho += prob * prob;
        }

        // Y calcular el Ã­ndice a devolver
        return (totalIzquierdo / total) * (1 - probIzquierdo) +
                (totalDerecho / total) * (1 - probDerecho);
    }

    /** Computes and returns the Gini index for the node (discrete attributes).
     *  Based on the implementation of the class Count_Matrix of the Nathan Rountree
     *  thesis called 'Initialising Neural Networks with Prior Knowledge'. In particular, 
     *  the chapter where a study of decision trees and  its splitting and pruning methods is done.
     *
     * @param indSubconjunto    subset index to check.
     * @param ocurrencias       occurrences matrix.
     * @param numValores        number of values in the matrix.
     * @param numClases         number of classes in the matrix.
     * @param totalOcurrencias  total number of occurrences
     * @return Gini index.
     */
    public double calculaGini(int indSubconjunto, int[][] ocurrencias, int numValores, int numClases, int totalOcurrencias) {
        int indice = 0, ciclos = numValores * numClases,
                tmpDerecha = 0, totalDerecha = 0,
                tmpIzquierda = 0, totalIzquierda = 0;
        double giniDerecha = 1, giniIzquierda = 1,
                peso, resultado;
        int[] subconjunto = new int[ciclos];

        // InicializaciÃ³n a cero de contadores
        for (int ind = 0; ind < ciclos; ind++) {
            subconjunto[ind] = 0;
        }

        // Contabilizar los datos que quedarÃ­an en el nodo izquierdo
        while (indSubconjunto > 0) {
            if (indSubconjunto % 2 != 0) { // Se dejan los valores impares en este subconjunto
                subconjunto[indice] = 1;
                for (int ind = 0; ind < numClases; ind++) {
                    totalIzquierda += ocurrencias[ind][indice];
                }
            }
            indSubconjunto /= 2; // Se va dividiendo por 2
            indice++;
        }

        // Y en el nodo derecho
        totalDerecha = totalOcurrencias - totalIzquierda;

        // Acumular las distribuciones de los datos segÃºn las clases
        for (int i = 0; i < numClases; i++) {
            for (int j = 0; j < numValores; j++) {
                if (subconjunto[j] == 1) {
                    tmpIzquierda += ocurrencias[i][j];
                } else {
                    tmpDerecha += ocurrencias[i][j];
                }
            }
            peso = (double) tmpIzquierda / (double) totalIzquierda;
            peso *= peso;
            giniIzquierda -= peso;

            peso = (double) tmpDerecha / (double) totalDerecha;
            peso *= peso;
            giniDerecha -= peso;

            tmpIzquierda = tmpDerecha = 0;

        }

        // Calcular el Ã­ndice Gini
        resultado = (totalIzquierda * giniIzquierda + totalDerecha * giniDerecha) / totalOcurrencias;

        return resultado;
    }

    /**
     * Returns the the Gini index.
     * @return the the Gini index. 
     */
    public double getIndiceGini() {
        return indiceGini;
    }

    /**
     * Sets the dataset that satisfies the node's condition.
     * @param newData 	Given dataset.
     */
    public void setData(Vector<ListaAtributos>[] newData) {
        // Se guardan los datos
        data = newData;
    }

    /** 
     * Checks if the node is a leaf.
     * 
     *
     * @return true if the node is a leaf.
     */
    public boolean esHoja() {
        return this.esHoja;
    }

    /** 
     * Sets the leaf contition.
     *
     * @param b true if the node is a leaf.
     */
    public void setHoja(boolean b) {
        esHoja = b;
    }

    /** 
     *  Returns the dataset that satisfies the node condition.
     * @return the dataset that satisfies the node condition. 
     */
    public Vector<ListaAtributos>[] getData() {
        return data;
    }

    /** 
     * Returns the class of the node.
     *
     * @return the class of the node.
     */
    public int getClase() {
        return primeraClase;
    }

    /**
     * Retuns the dataset that satisfies the node's condition.
     * @return the dataset that satisfies the node's condition.
     */
    public int getDecompositionAttribute() {
        return mejorAtributo;
    }

    /** 
     * Returns the value used to divide the node.
     * @return the value used to divide the node. 
     */
    public double getDecompositionValue() {
        return mejorValor;
    }

    /**
     *  Sets the children of the node.
     *
     * @param nodes 	the children of the node.
     */
    public void setChildren(Node[] nodes) {
        children = nodes;
    }

    /** Adds a child to the node.
     *
     * @param node 	given child.
     */
    public void addChildren(Node node) {
        children[numChildren()] = node;
    }

    /** Returns the number of children.
     * @return the number of children.
     */
    public int numChildren() {
        int nChildren = 0;

        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                nChildren++;
            }
        }

        return nChildren;
    }

    /**
     * Returns the children of the node.
     * @return the children of the node.
     */
    public Node[] getChildren() {
        return children;
    }

    /** Returns the child with the given index. 
     *
     * @param index		given child index.
     * @return  the child with the given index. 
     */
    public Node getChildren(int index) {
        return children[index];
    }

    /** Sets the father of the node with the given node.
     *
     * @param node		given father.
     */
    public void setParent(Node node) {
        parent = node;
    }

    /** Returns the father node.
     *
     * @return the father node.
     */
    public Node getParent() {
        return parent;
    }

    /**  Returns the associated cost. 
     *
     * @return the associated cost. 
     */
    public int getCoste() {
        if (coste == -1) {
            calculaCoste(1);
        }

        return coste;
    }

    /**
     * Computes the cost of each node of the tree.
     * @param fase Pruning phase identifier (1, first; 2, second).
     */
    public void calculaCoste(int fase) {
        coste = fase; // El coste es 1 para la primera fase y 2 para la segunda

        coste++; // Sumar el coste de la prueba del corte

        if (children[0] != null) // Si hay un hijo a la izquierda sumar su coste
        {
            coste += children[0].getCoste();
        }

        if (children[1] != null) // Lo mismo si hay un hijo a la derecha
        {
            coste += children[1].getCoste();
        }

        // Si Ã©ste es un nodo hoja o se estÃ¡ en la segunda fase de la poda
        if (esHoja() || fase == 2) // agregar tambiÃ©n el coste del error
        {
            for (int indice = 1; indice <= numClases; indice++) {
                coste += histograma[indice][0] == primeraClase ? 0 : 1;
            }
        }
    }

    /** 
     * Computes the error cost of adding a child to the node.
     * @param hijo  child node to add.
     * @return  rerror cost.
     */
    public int costeError(Node hijo) {
        int suma = 0;

        // Sumar aquellos elementos cuya clase no coincida con la primeraClase
        // del nodo padre al que se incorporarÃ¡n los datos
        for (int indice = 1; indice <= numClases; indice++) {
            if(indice != primeraClase)
                suma += hijo.histograma[indice][0];
            //suma += hijo.histograma[indice][0] == primeraClase ? 0 : 1;
        }

        return suma;
    }

    /** 
     * Sets the node cost with the value given.
     *
     * @param coste given cost.
     */
    public void setCoste(int coste) {
        this.coste = coste;
    }
}
