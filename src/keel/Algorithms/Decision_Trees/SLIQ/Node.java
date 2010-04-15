package keel.Algorithms.Decision_Trees.SLIQ;

import java.util.*;

/**
Implementación en Java del algoritmo SLIQ
Basada parcialmente en el código del algoritmo ID3 de Cristóbal Romero Morales (UCO)
@author Francisco Charte Ojeda (práctica ICO de la UJA)
@version 1.0 (28/12/09 - 10/1/10)
 */

/**
 *  Clase que representa un nodo del árbol
 */
public class Node {

    /** Histograma asociado al nodo. El primer índice es el índice de clase y
     * el segundo es 0-izquierda ó 1-derecha, mientras que el valor indicaría
     * la frecuencia de esa clase en la rama indicada. La clase 0 está reservada
     * para conservar el total de cada rama.
     */
    private int[][] histograma;
    /** Índice Gini de este nodo */
    private double indiceGini;
    /** Mejor ganancia para partir */
    private double mejorGini;
    /** En los nodos interiores, referencias a los nodos hijo. */
    private Node[] children;
    /** Clase asociada al nodo si es un nodo hoja*/
    private int primeraClase;
    /** Indica si el nodo es una hoja o no */
    private boolean esHoja;
    /** El conjunto de datos asociados al nodo. */
    private Vector<ListaAtributos>[] data;
    /** El padre de este nodo.  En la raíz parent == null. */
    private Node parent;
    /** Valor (atributos continuos) con el mejor corte o
        índice del subconjunto (atributos discretos) con el mejor corte */
    private double mejorValor;
    /** En los nodos hoja, el atributo que se utiliza para dividir el conjunto de datos. */
    private int mejorAtributo;
    /** Coste del nodo (para la fase de poda) */
    private int coste = -1;
    /** Número de clases existentes */
    private int numClases;

    /** Crea un nuevo nodo.
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

        // Inicializar también el histograma asociado al nodo
        histograma = new int[nClases + 1][2];
        for (int indice = 0; indice <= nClases; indice++) {
            histograma[indice][0] = histograma[indice][1] = 0;
        }

        // Conservar el número de clases
        numClases = nClases;
        parent = null;
    }

    /** Agregar un elemento al nodo
     *
     * @param clase Clase a la que pertenece el elemento
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

    /** Método que divide el nodo actual en dos que se agregan como hijos
     * 
     */
    public void divide() {
        children[0] = new Node(histograma.length);
        children[1] = new Node(histograma.length);

        // Actualizar los punteros al padre
        children[0].parent = this;
        children[1].parent = this;
    }

    /** Registra un elemento de la clase indicada que pasa de la hoja izquierda a la derecha
     *
     * @param clase Índice de la clase del elemento
     */
    protected void actualizaHistograma(int clase) {
        histograma[clase + 1][0]--;
        histograma[clase + 1][1]++;

        histograma[0][0]--;
        histograma[0][1]++;
    }

    /** Método que actualiza la clase principal del nodo contando la frecuencia
     *  de las clases. Se usa después de podar un nodo
     */
    public void actualizaClasePrincipal() {
        int frecuenciaClase = 0;

        // Se recorren las clases
        for (int indice = 1; indice <= numClases; indice++) {
            // quedándose siempre con la clase más representativa
            if (histograma[indice][0] + histograma[indice][1] > frecuenciaClase) {
                frecuenciaClase = histograma[indice][0] + histograma[indice][1];
                primeraClase = indice - 1;
            }
        }
    }

    /** Método que prueba un corte y calcula la mejora que se obtendría. Para atributos discretos
     *
     * @param indAtributo   Índice del atributo
     * @param listaClases   Lista de clases
     * @param atributo      Referencia al atributo
     */
    public void pruebaCorte(int indAtributo, ListaClases[] listaClases, Attribute atributo) {
        // Número máximo de valores a comprobar de manera exhaustiva, según la
        // descripción del algoritmo SLIQ de Mehta
        final int MAXSETSIZE = 10; 

        int numValores = atributo.numValues(), // Número de valores distintos que puede tomar el atributo
            numClases = listaClases.length;    // Número de clases a las que pueden pertenecer

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
                // Se obtiene el índice Gini para este subconjunto
                giniActual = calculaGini(indice, ocurrencias, numValores, numClases, totalOcurrencias);
                // Si es mejor que el mejor encontrado hasta ahora
                if (giniActual < giniSubconjunto) {
                    mejorSubconjunto = indice; // Se guarda el índice del subconjunto
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
        // Se almacena como valor el índice del mejor subconjunto encontrado
        mejorValor = mejorSubconjunto;
    }

    /** Método que prueba un corte y calcula la mejora que se obtendría. Para atributos continuos
     *
     * @param atributo  Índice del atributo
     * @param listaClases   Lista de clases
     * @param valor     Valor a comprobar
     * @param siguiente Valor siguiente
     */
    public void pruebaCorte(int atributo, ListaClases[] listaClases, double valor, double siguiente) {
        // Calcular el valor intermedio entre valor y el siguiente (la lista está ordenada)
        double valorMedio = valor + (siguiente - valor) / 2;

        // Se guarda el histograma actual del nodo
        int[][] copiaHistograma = histograma.clone();

        // Creo los dos nodos en los que se dividiría la lista de datos
        Node nodoI = new Node(histograma.length), nodoD = new Node(histograma.length);

        // Se recorre la lista de valores del atributo indicado
        for (int indice = 0; indice < data[atributo].size(); indice++) // y se agrega la distribución en el nodo que corresponda
        {
            if (data[atributo].get(indice).valor <= valorMedio) {
                nodoI.agregaElemento(listaClases[data[atributo].get(indice).indice].clase);
            } else {
                // Si el nodo cambia a la rama derecha
                nodoD.agregaElemento(listaClases[data[atributo].get(indice).indice].clase);
                // hay que actualizar también el histograma de este nodo
                actualizaHistograma(listaClases[data[atributo].get(indice).indice].clase);
            }
        }

        // Calcular el índice Gini
        indiceGini = calculaGini();

        // Proporción de entradas en cada nodo
        double propIzq = nodoI.histograma[0][0] / (nodoI.histograma[0][0] + nodoD.histograma[0][0]),
                propDcho = nodoD.histograma[0][0] / (nodoI.histograma[0][0] + nodoD.histograma[0][0]);

        // Cálculo de la ganancia que se obtendría
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

    /** Método encargado de calcular el índice Gini del nodo para atributos continuos
     *
     */
    public double calculaGini() {
        // Tomar los totales
        double totalIzquierdo = histograma[0][0],
                totalDerecho = histograma[0][1];
        double total = totalIzquierdo + totalDerecho;
        double probIzquierdo = 0, probDerecho = 0, prob = 0;

        // Si todos los datos están en una rama
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

        // Y calcular el índice a devolver
        return (totalIzquierdo / total) * (1 - probIzquierdo) +
                (totalDerecho / total) * (1 - probDerecho);
    }

    /** Método encargado de calcular el índice Gini para atributos discretos.
     *  Está basado parcialmente en la implementación de la clase count_matrix
     *  de la tésis de Nathan Rountree titulada 'Initialising Neural Networks 
     *  with Prior Knowledge', en la que hay un capítulo dedicado específicamente
     *  al estudio de árboles, las técnicas de splitting y de poda.
     *
     * @param indSubconjunto    Índice del subconjunto a probar
     * @param ocurrencias       Matriz de ocurrencias
     * @param numValores        Número de valores en la matriz
     * @param numClases         Número de clases en la matriz
     * @param totalOcurrencias  Total de ocurrencias
     * @return
     */
    public double calculaGini(int indSubconjunto, int[][] ocurrencias, int numValores, int numClases, int totalOcurrencias) {
        int indice = 0, ciclos = numValores * numClases,
                tmpDerecha = 0, totalDerecha = 0,
                tmpIzquierda = 0, totalIzquierda = 0;
        double giniDerecha = 1, giniIzquierda = 1,
                peso, resultado;
        int[] subconjunto = new int[ciclos];

        // Inicialización a cero de contadores
        for (int ind = 0; ind < ciclos; ind++) {
            subconjunto[ind] = 0;
        }

        // Contabilizar los datos que quedarían en el nodo izquierdo
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

        // Acumular las distribuciones de los datos según las clases
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

        // Calcular el índice Gini
        resultado = (totalIzquierda * giniIzquierda + totalDerecha * giniDerecha) / totalOcurrencias;

        return resultado;
    }

    /** Método que facilita el índice Gini asociado al índice
     *
     */
    public double getIndiceGini() {
        return indiceGini;
    }

    /** Método para establecer los conjuntos de elementos que satisfacen la condición del nodo.
     *
     * @param newData 	Los conjuntos de elementos.
     */
    public void setData(Vector<ListaAtributos>[] newData) {
        // Se guardan los datos
        data = newData;
    }

    /** Indica si el nodo es hoja
     *
     * @return true si el nodo es una hoja
     */
    public boolean esHoja() {
        return this.esHoja;
    }

    /** Establece la condición de hoja de un nodo
     *
     * @param b true si el nodo es hoja
     */
    public void setHoja(boolean b) {
        esHoja = b;
    }

    /** Devuelve los conjuntos de elementos que satisfacen la condición del nodo.
     */
    public Vector<ListaAtributos>[] getData() {
        return data;
    }

    /** Facilita la clase más representativa del nodo
     *
     * @return Índice de la clase
     */
    public int getClase() {
        return primeraClase;
    }

    /** Devuelve el índice del atributo usado para descomponer el nodo.
     *
     */
    public int getDecompositionAttribute() {
        return mejorAtributo;
    }

    /** Devuelve el valor usado para descomponer el nodo.
     *
     */
    public double getDecompositionValue() {
        return mejorValor;
    }

    /** Método para establecer los hijos de un nodo.
     *
     * @param nodes 	Hijos del nodo.
     */
    public void setChildren(Node[] nodes) {
        children = nodes;
    }

    /** M�todo para a�adir un hijo al nodo.
     *
     * @param node 	Nuevo hijo.
     */
    public void addChildren(Node node) {
        children[numChildren()] = node;
    }

    /** Devuelve el número de hijos del nodo.
     *
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

    /** Devuelve los hijos del nodo.
     *
     */
    public Node[] getChildren() {
        return children;
    }

    /** Devuelve el hijo correspondiente a un índice.
     *
     * @param index		�ndice del hijo.
     */
    public Node getChildren(int index) {
        return children[index];
    }

    /** Método para establecer el nodo padre.
     *
     * @param node		El padre del nodo.
     */
    public void setParent(Node node) {
        parent = node;
    }

    /** Devuelve el padre del nodo.
     *
     */
    public Node getParent() {
        return parent;
    }

    /** Devuelve el coste asociado al nodo
     *
     * @return El coste
     */
    public int getCoste() {
        if (coste == -1) {
            calculaCoste(1);
        }

        return coste;
    }

    /** Método para calcular el coste de tener un nodo en el árbol
     *
     * @param fase Indica si se está en la fase de poda 1 o en la 2
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

        // Si éste es un nodo hoja o se está en la segunda fase de la poda
        if (esHoja() || fase == 2) // agregar también el coste del error
        {
            for (int indice = 1; indice <= numClases; indice++) {
                coste += histograma[indice][0] == primeraClase ? 0 : 1;
            }
        }
    }

    /** Método que calcula el coste del error al incorporar un nodo hijo
     *
     * @param hijo  Hijo cuyos datos se incorporarían al padre
     * @return  Coste del error
     */
    public int costeError(Node hijo) {
        int suma = 0;

        // Sumar aquellos elementos cuya clase no coincida con la primeraClase
        // del nodo padre al que se incorporarán los datos
        for (int indice = 1; indice <= numClases; indice++) {
            if(indice != primeraClase)
                suma += hijo.histograma[indice][0];
            //suma += hijo.histograma[indice][0] == primeraClase ? 0 : 1;
        }

        return suma;
    }

    /** Establece el coste del nodo
     *
     * @param coste Coste
     */
    public void setCoste(int coste) {
        this.coste = coste;
    }
}