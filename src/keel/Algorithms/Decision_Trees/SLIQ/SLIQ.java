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

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/** Implementation of the SLIQ algorithm.
 * 
* 
@author Francisco Charte Ojeda (UJA)
@version 1.0 (28/12/09 - 10/1/10)
 */
public class SLIQ extends Algorithm {

    /** Tree root */
    Node root;
    /** For division process. */
    Node subnodoIzquierdo, subnodoDerecho;
    /** Number of nodes in the tree. */
    int NumberOfNodes;
    /** Number of leaves in the tree. */
    int NumberOfLeafs;
    /** Classes list. */
    ListaClases[] listaClases;
    /** Ordered attributes list*/
    Vector<ListaAtributos>[] listas;
    /** List with the nodes that have not been yet processed. */
    Queue<Node> listaNodos;

    /** Constructor.
     *
     * @param paramFile			parameters file.
     *
     */
    public SLIQ(String paramFile) {
        try {
            // Inicia el temporizador
            startTime = System.currentTimeMillis();

            // Establecer las opciones de ejecución del algoritmo
            StreamTokenizer tokenizer = new StreamTokenizer(new BufferedReader(new FileReader(paramFile)));
            initTokenizer(tokenizer);
            setOptions(tokenizer);

            // Inicializa el dataset a procesar
            modelDataset = new Dataset(modelFileName, true);
            // Obtener los conjuntos de datos de entrenamiento y prueba
            trainDataset = new Dataset(trainFileName, false);
            testDataset = new Dataset(testFileName, false);

            // Se generan la lista de clases y las listas de atributos, ya ordenadas
            generaListas();

            // El árbol está vacío
            NumberOfNodes = 0;
            NumberOfLeafs = 0;

            // Genera el árbol según el algoritmo SLIQ.
            generateTree();

            // Imprimir los resultados generados
            printTrain();
            printTest();
            printResult();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Constructs the lists of attriibutes and classes used during SLIQ execution.
     */
    protected void generaListas() {
        int n = 0; // Para contabilizar las clases

        // La lista de clases tendrá tantas entradas como muestras de datos en el dataset
        listaClases = new ListaClases[modelDataset.numItemsets()];

        // La lista de listas de atributos tendrá un vector por atributo
        listas = (Vector<ListaAtributos>[]) Array.newInstance(Vector.class, modelDataset.numAttributes());

        // y cada elemento de la lista será un vector que hay que crear
        for (int indice = 0; indice < modelDataset.numAttributes(); indice++) {
            listas[indice] = new Vector<ListaAtributos>();
        }

        // Se recorren todas las muestras de datos
        Enumeration datos = modelDataset.enumerateItemsets();
        Itemset dato;

        while (datos.hasMoreElements()) {
            // Agregar el valor de clase a la lista de clases
            dato = (Itemset) datos.nextElement();
            listaClases[n] = new ListaClases((int) dato.getClassValue(), root);

            // Agregar cada atributo a la lista correspondiente según el atributo
            Enumeration atributos = modelDataset.enumerateAttributes();
            while (atributos.hasMoreElements()) {
                Attribute atributo = (Attribute) atributos.nextElement();
                // Se introduce el atributo con el índice que corresponde a la clase
                listas[atributo.getIndex()].add(new ListaAtributos(dato.getValue(atributo.getIndex()), n));
            }
            n++;
        }

        // Ahora hay que ordenar las listas de atributos
        for (int indice = 0; indice < modelDataset.numAttributes(); indice++) {
            // Los atributos discretos (categóricos) no hay que ordenarlos
            if (modelDataset.getAttribute(indice).isContinuous()) {
                Collections.sort(listas[indice], new ListaAtributos.Comparador());
            }
        }
    }

    /**
     * Generates the tree with the SLIQ algorithm.
     */
    public void generateTree() {
        // Se crea el nodo raíz para todas las clases
        root = new Node(listaClases.length);
        // Asociándoles las listas de atributos con todos los valores
        root.setData(listas);

        // y se agregan todas las clases
        for (int indice = 0; indice < listaClases.length; indice++) {
            listaClases[indice].hoja = root;
            root.agregaElemento(listaClases[indice].clase);
        }

        // Se crea la cola de nodos para procesar el árbol en anchura, no en profundidad
        listaNodos = new LinkedList<Node>();
        listaNodos.add(root); // y se agrega la raíz

        // Mientras haya nodos a procesar en la cola
        while (!listaNodos.isEmpty()) {
            Node nodo = listaNodos.poll(); // Obtener el nodo a procesar

            // Si no es un nodo puro y aún no ha sido dividido
            if (!nodo.esHoja() && nodo.numChildren() == 0) {
                // Recorrer todos los atributos
                for (int indice = 0; indice < nodo.getData().length; indice++) {
                    if (indice != modelDataset.getClassIndex()) {
                        // calculando el mejor corte posible
                        calculaMejorCorte(indice, nodo);
                    }
                }

                // Y a continuación aplicar ese corte a los nodos a dividir
                for (int indice = 0; indice < listaClases.length; indice++) {
                    // que son aquellos no considerados hoja y a los que se ha agregado hijos
                    if (indice != modelDataset.getClassIndex() &&
                            !listaClases[indice].hoja.esHoja() &&
                            listaClases[indice].hoja.numChildren() != 0) {
                        aplicaMejorCorte(listaClases[indice].hoja);
                    }
                } // for
            } //else
        } // while

        // Realizar la poda
        podaArbol();

    } // generateTree

    /** 
     * Prunes the tree.
     */
    protected void podaArbol() {
        int Lt = 2;

        // Se inicia la primera fase de la poda (pasos 1 y 2)
        root.calculaCoste(1);

        // Partir desde los nodos hoja
        for (int indice = 0; indice < listaClases.length; indice++) {
            // Obtener la referencia al nodo padre
            Node padre = listaClases[indice].hoja.getParent();

            do {
                // y comprobar si el coste justifica la poda de ambos hijos
                if (padre.getCoste() < padre.getChildren(0).getCoste() + padre.getChildren(1).getCoste()) {
                    podaNodoCompleto(padre); // Aplicar la primera fase de poda

                    // Si se ha podado salir de este bucle y reiniciar el bucle exterior
                    indice = -1;
                    break;
                }
                padre = padre.getParent(); // Subir por el árbol
            } while (padre != null); // Hasta alcanzar la raíz
        }

        // Se inicia la segunda fase de la poda (pasos 2 a 4)

        Vector<Node> listaNodos = new Vector<Node>();
        for(int indice = 0; indice < listaClases.length; indice++)
            if(!listaNodos.contains(listaClases[indice].hoja.getParent()))
                listaNodos.add(listaClases[indice].hoja.getParent());

        // Recorrer los nodos hoja, en este caso no hay que subir por el árbol
        //for (int indice = 0; indice < listaClases.length; indice++) {
        for(int indice = 0; indice < listaNodos.size(); indice++) {
            // Obtener la referencia al nodo padre, del que cuelga este nodo y otro
            Node padre = listaNodos.get(indice); // listaClases[indice].hoja.getParent();

            // Si ya se le ha podado una rama
         //   if ( padre == null ||
         //       padre.getChildren(0) == null || padre.getChildren(1) == null) {
         //       continue; // no procesarlo
         //   }

            padre.calculaCoste(2); // Calcular el coste adecuado para la segunda fase
            padre.getChildren(0).calculaCoste(2);
            padre.getChildren(1).calculaCoste(2);

            int costeAmbos = padre.getCoste(); // Coste de tener ambos hijos

            // Costes teniendo solamente uno, se resta el coste de tener un hijo
            // y se considera el error que se añadiría
            int costeIzq = costeAmbos - Lt + padre.costeError(padre.getChildren(0));
            int costeDch = costeAmbos - Lt + padre.costeError(padre.getChildren(1));

            if (costeIzq < costeAmbos) // Si el coste de tener solamente el izquierdo
            {
                podaNodoParcial(padre, 1); // es menor que ambos, se poda el derecho
            } else if (costeDch < costeAmbos) // y viceversa
            {
                podaNodoParcial(padre, 0);
            }
        }
    }

    /** 
     * Prunes the node given as parameter, deleting its children.
     * 
     * @param padre given node to be pruned.
     */
    protected void podaNodoCompleto(Node padre) {
        // Se podan los dos hijos
        podaNodoParcial(padre, 0);
        podaNodoParcial(padre, 1);

        // Marcar padre como nodo hoja, aunque tenga datos de más de una clase
        padre.setHoja(true);
    }

    /** 
     * Prunes the node given as parameter, deleting the given child.
     *
     * @param padre    given father to be pruned.
     * @param indHijo   given child index to delete.
     */
    protected void podaNodoParcial(Node padre, int indHijo) {
        // Referencia al hijo que corresponda
        Node hijo = padre.getChildren(indHijo);

        // Hay que agregar al nodo padre los datos del hijo
        agregaDatos(padre, hijo);

        // Eliminar el nodo hijo
        padre.getChildren()[indHijo] = null;
    }

    /** Aggregates the data of the given child that will be pruned into the father given.
     *
     * @param padre Father node.
     * @param hijo  Child node.
     */
    protected void agregaDatos(Node padre, Node hijo) {
        // Se recorre la lista de datos que contiene el hijo, agregando los
        // valores de sus atributos al padre
        for (int atributo = 0; atributo < hijo.getData().length; atributo++) {
            Vector<ListaAtributos> lista = hijo.getData()[atributo];
            for (int indice = 0; indice < lista.size(); indice++) {
                // Se contabiliza en el histograma
                padre.agregaElemento(listaClases[lista.get(indice).indice].clase);
                // y se agrega a la lista de atributos correspondiente
                padre.getData()[atributo].add(lista.get(indice));

                // Se actualiza la entrada en la lista de clases para que apunte al nodo padre
                listaClases[lista.get(indice).indice].hoja = padre;
            }
        }

        // Actualizar la clase principal en el padre, que puede ahora ser otra
        padre.actualizaClasePrincipal();
    }

    /**
     * Computes the best cut of the given attribute for the given node.
     *
     * @param indAtributo   Attribute index.
     * @param nodo          Node to be divided.
     */
    protected void calculaMejorCorte(int indAtributo, Node nodo) {
        // Se recorre la lista ordenada de valores para el atributo índAtributo
        for (int indice = 0; indice < nodo.getData()[indAtributo].size() - 1; indice++) {
            // Se obtiene el nodo hoja que pertenece al valor examinado
            Node nodoHoja = listaClases[nodo.getData()[indAtributo].get(indice).indice].hoja;

            // Si es un nodo impuro que aún no ha sido dividido
            if (!nodoHoja.esHoja()) {
                if (nodoHoja.numChildren() == 0) { // Si no tiene aún hijos
                    nodoHoja.divide(); // dividirlo

                    // Y agregar a la lista de nodos pendientes de procesar
                    listaNodos.add(nodoHoja.getChildren(0));
                    listaNodos.add(nodoHoja.getChildren(1));
                }

                // Probar el corte por el atributo y valor indicados
                nodoHoja.pruebaCorte(indAtributo, listaClases,
                        nodo.getData()[indAtributo].get(indice).valor,
                        nodo.getData()[indAtributo].get(indice + 1).valor);
            }
        }
    }

    /** Método que aplica en un nodo el mejor corte obtenido previamente
     *  Apply the best cut fouded to divide the given node.
     *
     * @param nodo  node to be divided.
     */
    protected void aplicaMejorCorte(Node nodo) {
        if (modelDataset.getAttribute(nodo.getDecompositionAttribute()).isDiscret()) 
            aplicaMejorCorteDiscreto(nodo);
        else                                                                                                                      aplicaMejorCorteContinuo(nodo);
    }
    
    /**
     * Divides the node in the more optimal way possible (Discrete attribute).
     *
     * @param nodo  node to be divided.
     */
    protected void aplicaMejorCorteDiscreto(Node nodo) {
        // Índice del atributo por el que se dividirá
        int indAtributo = nodo.getDecompositionAttribute();

        // Se toman las listas de atributos ordenadas del nodo a dividir
        Vector<ListaAtributos>[] listaI = nodo.getData().clone();
        Vector<ListaAtributos>[] listaD = nodo.getData().clone();

        // Y se generan nuevas listas inicialmente vacías
        for (int indice = 0; indice < listaI.length; indice++) {
            listaI[indice] = new Vector<ListaAtributos>();
            listaD[indice] = new Vector<ListaAtributos>();
        }

        // Referencias a los nodos hijo entre los que se repartirán los datos
        Node nodoI = nodo.getChildren(0), nodoD = nodo.getChildren(1);

        // Procesar la lista de valores correspondiente a cada atributo
        // y dividirla entre los dos nodos según el criterio  subconjunto
        for (int atributo = 0; atributo < nodo.getData().length; atributo++) {
            // Lista de valores a dividir
            Vector<ListaAtributos> lista = nodo.getData()[atributo];
            for (int indice = 0; indice < lista.size(); indice++) {
                // Índice del mejor subconjunto encontrado
                int indSubconjunto = (int) nodo.getDecompositionValue();

                // Obtener el subconjunto que corresponde a indSubconjunto
                while (indSubconjunto > 0) {
                    if (indSubconjunto % 2 != 0) {
                        // indSubconjunto es el índice del atributo que quedaría a la izquierda
                        if (modelDataset.itemset(lista.get(indice).indice).getValue(indAtributo) == indSubconjunto) {
                            nodoI.agregaElemento(listaClases[lista.get(indice).indice].clase);
                            listaI[atributo].add(lista.get(indice));

                            // Se actualiza la entrada en la lista de clases para que apunte al nuevo nodo
                            listaClases[lista.get(indice).indice].hoja = nodoI;
                        }
                    } else {
                        if (modelDataset.itemset(lista.get(indice).indice).getValue(indAtributo) == indSubconjunto) {
                            // indSubconjunto es el índice del atributo que quedaría a la derecha
                            nodoD.agregaElemento(listaClases[lista.get(indice).indice].clase);
                            listaD[atributo].add(lista.get(indice));
                            // Se actualiza la entrada en la lista de clases para que apunte al nuevo nodo
                            listaClases[lista.get(indice).indice].hoja = nodoD;
                        }
                    }
                    indSubconjunto /= 2; // Partir el conjunto en dos subconjuntos
                }
            }
        }

        // Facilitar a cada nodo su lista de atributos y valores
        nodoI.setData(listaI);
        nodoD.setData(listaD);
    }

    /**
     * Divides the node in the more optimal way possible (Continuous attribute).
     *
     * @param nodo  node to be divided.
     */
    protected void aplicaMejorCorteContinuo(Node nodo) {

        // Índice del atributo por el que se dividirá
        int indAtributo = nodo.getDecompositionAttribute();
        // Valor por el que se dividirá
        double valor = nodo.getDecompositionValue();

        // Se toman las listas de atributos ordenadas del nodo a dividir
        Vector<ListaAtributos>[] listaI = nodo.getData().clone();
        Vector<ListaAtributos>[] listaD = nodo.getData().clone();

        // Y se generan nuevas listas inicialmente vacías
        for (int indice = 0; indice < listaI.length; indice++) {
            listaI[indice] = new Vector<ListaAtributos>();
            listaD[indice] = new Vector<ListaAtributos>();
        }

        // Referencias a los nodos hijo entre los que se repartirán los datos
        Node nodoI = nodo.getChildren(0), nodoD = nodo.getChildren(1);

        // Procesar la lista de valores correspondiente a cada atributo
        // y dividirla entre los dos nodos según el criterio <= valor
        for (int atributo = 0; atributo < nodo.getData().length; atributo++) {
            // Lista de valores a dividir
            Vector<ListaAtributos> lista = nodo.getData()[atributo];
            for (int indice = 0; indice < lista.size(); indice++) {
                if (modelDataset.itemset(lista.get(indice).indice).getValue(indAtributo) <= valor) {
                    nodoI.agregaElemento(listaClases[lista.get(indice).indice].clase);
                    listaI[atributo].add(lista.get(indice));

                    // Se actualiza la entrada en la lista de clases para que apunte al nuevo nodo
                    listaClases[lista.get(indice).indice].hoja = nodoI;
                } else {
                    nodoD.agregaElemento(listaClases[lista.get(indice).indice].clase);
                    listaD[atributo].add(lista.get(indice));

                    // Se actualiza la entrada en la lista de clases para que apunte al nuevo nodo
                    listaClases[lista.get(indice).indice].hoja = nodoD;
                }
            }
        }

        // Facilitar a cada nodo su lista de atributos y valores
        nodoI.setData(listaI);
        nodoD.setData(listaD);

    /**** Posiblemente nodo.data ya no sea necesario y pueda eliminarse,
     *  reduciendo la ocupación en memoria
     */
    }

    /** 
     * Returns the predicted class of the given example obtained from the given node.
     * 
     * @param itemset		given example.
     * @param node		given node.
     *
     * @return predicted class.
     */
    public int evaluateItemset(Itemset itemset, Node node) {
        try {
            // Si el nodo es una hoja
            if (node.esHoja() ||
                    // o a pesar de no serlo tiene un solo hijo que corresponde a la condición a evaluar
                    itemset.getValue(node.getDecompositionAttribute()) <= node.getDecompositionValue() && node.getChildren(0) == null ||
                    itemset.getValue(node.getDecompositionAttribute()) > node.getDecompositionValue() && node.getChildren(1) == null) {
                return node.getClase(); // Se devuelve el índice de clase
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Evaluar los nodos hijo.
        if (itemset.getValue(node.getDecompositionAttribute()) <= node.getDecompositionValue()) {
            return (evaluateItemset(itemset, node.getChildren()[0]));
        } else {
            return (evaluateItemset(itemset, node.getChildren()[1]));
        }
    }

    /** Counts the number of nodes pending from the given node.
     *
     * @param node given node.
     */
    public void cuentaNodosHojas(Node node) {

        NumberOfNodes++;    // Contabilizar el número total de nodos

        // Descendiendo por el árbol
        if (node.getChildren(0) != null) {
            cuentaNodosHojas(node.getChildren(0));
        }
        if (node.getChildren(1) != null) {
            cuentaNodosHojas(node.getChildren(1));
        }

        // Hasta alcanzar las hojas
        if (node.esHoja()) {
            NumberOfLeafs++;
        }
    }

    /** Writes the statistical measurements obtained on the output file.
     *
     * @exception IOException	if the file can not be written.
     */
    public void printResult() throws IOException {
        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
        long seconds = totalTime % 60;
        long minutes = ((totalTime - seconds) % 3600) / 60;
        String tree = "";
        PrintWriter resultPrint;

        cuentaNodosHojas(root);

        tree += "\n@TotalNumberOfNodes " + NumberOfNodes;
        tree += "\n@NumberOfLeafs " + NumberOfLeafs;

        tree += "\n\n@NumberOfItemsetsTraining " + trainDataset.numItemsets();
        tree += "\n@NumberOfCorrectlyClassifiedTraining " + correct;
        tree += "\n@PercentageOfCorrectlyClassifiedTraining " + (float) (correct * 100.0) / (float) trainDataset.numItemsets() + "%";
        tree += "\n@NumberOfInCorrectlyClassifiedTraining " + (trainDataset.numItemsets() - correct);
        tree += "\n@PercentageOfInCorrectlyClassifiedTraining " + (float) ((trainDataset.numItemsets() - correct) * 100.0) / (float) trainDataset.numItemsets() + "%";

        tree += "\n\n@NumberOfItemsetsTest " + testDataset.numItemsets();
        tree += "\n@NumberOfCorrectlyClassifiedTest " + testCorrect;
        tree += "\n@PercentageOfCorrectlyClassifiedTest " + (float) (testCorrect * 100.0) / (float) testDataset.numItemsets() + "%";
        tree += "\n@NumberOfInCorrectlyClassifiedTest " + (testDataset.numItemsets() - testCorrect);
        tree += "\n@PercentageOfInCorrectlyClassifiedTest " + (float) ((testDataset.numItemsets() - testCorrect) * 100.0) / (float) testDataset.numItemsets() + "%";

        tree += "\n\n@ElapsedTime " + (totalTime - minutes * 60 - seconds) / 3600 + ":" + minutes / 60 + ":" + seconds;

        resultPrint = new PrintWriter(new FileWriter(resultFileName));
        resultPrint.print(getHeader() + "\n@decisiontree\n\n" + tree);
        resultPrint.close();
    }

    /** Evaluates the training dataset and writes the results on the output file.
     *
     */
    public void printTrain() {
        String text = getHeader();
        for (int i = 0; i < trainDataset.numItemsets(); i++) {
            try {
                Itemset itemset = trainDataset.itemset(i);
                int cl = evaluateItemset(itemset, root);

                if (cl == (int) itemset.getValue(trainDataset.getClassIndex())) {
                    correct++;
                }

                text += trainDataset.getClassAttribute().value(cl) + " " +
                        trainDataset.getClassAttribute().value(((int) itemset.getClassValue())) + "\n";
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
        }

        try {
            PrintWriter print = new PrintWriter(new FileWriter(trainOutputFileName));
            print.print(text);
            print.close();
        } catch (IOException e) {
            System.err.println("No es posible abrir el archivo de salida de entrenamiento: " + e.getMessage());
        }
    }

    /**
     * Evaluates the test dataset and writes the results on the output file.
     */
    public void printTest() {
        String text = getHeader();

        for (int i = 0; i < testDataset.numItemsets(); i++) {
            try {
                int cl = (int) evaluateItemset(testDataset.itemset(i), root);
                Itemset itemset = testDataset.itemset(i);

                if (cl == (int) itemset.getValue(testDataset.getClassIndex())) {
                    testCorrect++;
                }

                text += testDataset.getClassAttribute().value(((int) itemset.getClassValue())) + " " +
                        testDataset.getClassAttribute().value(cl) + "\n";
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        try {
            PrintWriter print = new PrintWriter(new FileWriter(testOutputFileName));
            print.print(text);
            print.close();
        } catch (IOException e) {
            System.err.println("No es posible abrir el archivo de salida de pruebas.");
        }
    }

    /** 
     * Reads the parameters used by the algorith.
     * @param options 		StreamTokenizer used to read the different parameters
     *
     * @throws Exception	if the input file format is wrong.
     */
    protected void setOptions(StreamTokenizer options) throws Exception {

        options.nextToken();

        // Comprobar que el archivo comienza con el token 'algorithm'
        if (options.sval.equalsIgnoreCase("algorithm")) {
            options.nextToken(); // Saltar el s?mbolo =
            options.nextToken(); // y tomar el nombre del algoritmo

            // que debe ser 'SLIQ'
            if (!options.sval.equalsIgnoreCase("SLIQ")) {
                throw new Exception("El nombre del algoritmo no es correcto.");
            }

            options.nextToken();
            options.nextToken();

            // Recuperar los nombres de los archivos de entrada
            if (options.sval.equalsIgnoreCase("inputData")) {
                options.nextToken();
                options.nextToken();
                modelFileName = options.sval;

                if (options.nextToken() != StreamTokenizer.TT_EOL) {
                    trainFileName = options.sval;
                    options.nextToken();
                    testFileName = options.sval;
                    if (options.nextToken() != StreamTokenizer.TT_EOL) {
                        trainFileName = modelFileName;
                        options.nextToken();
                    }
                }
            } else {
                throw new Exception("El archivo debe comenzar con la palabra 'inputData'.");
            }

            // Avanzar en el archivo hasta la marca 'outputData'
            while (true) {
                if (options.nextToken() == StreamTokenizer.TT_EOF) {
                    throw new Exception("No se han indicado archivos de salida.");
                }

                if (options.sval == null) {
                    continue;
                } else if (options.sval.equalsIgnoreCase("outputData")) {
                    break;
                }
            }

            /* Recuperar los nombres de los archivos de salida */

            options.nextToken();
            options.nextToken();
            trainOutputFileName = options.sval;
            options.nextToken();

            testOutputFileName = options.sval;
            options.nextToken();

            resultFileName = options.sval;
        } else {
            throw new Exception("El archivo debe comenzar con la palabra 'algorithm' seguida del nombre del algoritmo.");
        }

    } // setOptions

    /** Main function. Executes the SLIQ algorithm with the configuration file given.
     *
     * @param args Arguments of the program (a configuration script, generally) 
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("\nError: debe especificar el archivo de parámetros\n\tuso: java -jar SLIQ.jar archivoparametros.txt");
            System.exit(-1);
        } else {
            new SLIQ(args[0]);
        }
    }
} // sliq

