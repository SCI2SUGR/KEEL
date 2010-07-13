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

package keel.Algorithms.Subgroup_Discovery.CN2SD;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

import org.core.Fichero;

/**
 * <p>Title: Clase CN2SD</p>
 *
 * <p>Description: Contiene los metodos principales del algoritmo cn2sd</p>
 *
 * <p>Copyright: Copyright Alberto (c) 2006</p>
 *
 * <p>Company: Mi casa </p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */
public class CN2SD {

    //Rutas y nombres de ficheros E/S
    private String ficheroSalida;
    private String ficheroSalidaTr;
    private String ficheroSalidaTst;

    private int muestPorClaseTrain[];
    private int muestPorClaseEval[];
    private int muestPorClaseTest[];
    private int[] valorClases;
    private int nClases; // Numero máximo de clases

    private Complejo almacenSelectores; // Almacena todos los posibles selectores
    private int tamanoEstrella;

    private ConjReglas conjReglasFinal; //Conjunto final de reglas
    private EvaluaCalidadReglas evReg; // Para evaluar la calidad de las reglas

    //Matrices de datos
    private ConjDatos datosTrain;
    private ConjDatos datosEval;
    private ConjDatos datosTest;
    private int clasesTrain[];
    private int clasesTest[];
    private long tiempo;

    private String miSalida;

    private double nu;
    private double seCubre;
    private int multiplicativo;
    private int nAtributos;

    private double div;
    private double umbral = 0.0;

    private int eficacia = 0;

    private String[] nombreAtributos;
    private String[] nombreClases;
    private boolean hayContinuos = false;

    public boolean todoBien(){
        return (!hayContinuos);
    }
    
    /**
     * Constructor de la clase CN2</br>
     * Simplemente nos ocupamos de hacer una copia local a la clase de los nombres
     * de los ficheros para su posterior uso;<br/>
     * Despues obtenemos todos los datos del fichero y los guardamos en un formato
     * reconocible por el programa.</br>
     * Por último crea todos los posibles selectores que se puedan dar para el conjunto
     * concreto de datos y los almacena.
     * @param ftrain Nombre del fichero donde reside el conjunto de entrenamiento
     * @param feval Nombre del fichero donde reside el conjunto de validación
     * @param ftest Nombre del fichero donde reside el conjunto de test
     * @param fSalidaTr Nombre del fichero de salida donde guardaremos el resultado de entrenamiento
     * @param fSalidaTst Nombre del fichero de salida donde guardaremos el resultado de test
     * @param fsal Nombre del fichero donde guardaremos los datos generales de salida (reglas, tiempo...)
     * @param tamEstrella es el tamaño máximo de la estrella para la busqueda (beam search)
     * @param _nu es el valor del peso multiplicativo para el peso de los ejemplos
     * @param _seCubre porcentaje minimo para decir que una clase de ejemplos ya queda cubierta por un conjunto de reglas
     * @param _multi Se refiere a si se utilizará el peso multiplicativo (1) o el aditivo (0)
     * @param _eficacia Usamos un método supuestamente mas eficaz (más selectores) o más eficiente
     */
    public CN2SD(String ftrain, String feval, String ftest,
                 String fSalidaTr,
                 String fSalidaTst, String fsal, int tamEstrella,
                 double _nu,
                 double _seCubre, int _multi, int _eficacia) {
        int i;

        System.out.println("Executing CN2SD");

        ficheroSalida = fsal;

        nu = _nu;
        seCubre = _seCubre;
        multiplicativo = _multi;
        eficacia = _eficacia;

        ficheroSalidaTr = fSalidaTr;
        ficheroSalidaTst = fSalidaTst;

        Dataset train = new Dataset(); //ficheroTrain);
        Dataset eval = new Dataset(); //ficheroTest);  }
        Dataset test = new Dataset(); //ficheroTest);  }
        try {
            //System.out.println("\nLeyendo train: " + ftrain);
            train.leeConjunto(ftrain, true);
            if (train.hayAtributosContinuos()){
                System.err.println("CN2SD may not work properly with continuous attributes.\nPlease discretize the data base");
                //System.exit(-1);
                hayContinuos = true;
            }
            //System.out.println("\nLeyendo validacion: " + feval);
            eval.leeConjunto(feval, false);
            if (eval.hayAtributosContinuos()){
                System.err.println("CN2SD may not work properly with continuous attributes.\nPlease discretize the data base");
                //System.exit(-1);
                hayContinuos = true;
            }
            //System.out.println("\nLeyendo test: " + ftest);
            test.leeConjunto(ftest, false);
        } catch (IOException e) {
            System.err.println("There was a problem while reading the data-set files:");
            System.err.println("-> " + e);
            System.exit(0);
        }

        miSalida = new String("");
        miSalida = test.copiaCabeceraTest();

        // obtenemos conjuntos de training y de test
        System.out.println("\nGenerating datasets");
        datosTrain = new ConjDatos();
        datosEval = new ConjDatos();
        datosTest = new ConjDatos();

        train.calculaMasComunes();
        eval.calculaMasComunes();
        test.calculaMasComunes();

        datosTrain = creaConjunto(train); //Leemos los datos de entrenamiento (todos seguidos como un String)
        datosEval = creaConjunto(eval);
        datosTest = creaConjunto(test); //Idem TEST

        clasesTrain = train.getC();
        nClases = train.getnclases();
        nAtributos = train.getnentradas();

        int[] auxiliar = train.getC();
        Arrays.sort(auxiliar);
        valorClases = new int[nClases];
        valorClases[0] = auxiliar[0];
        int valor = 0;
        for (i = 1; i < nClases; i++) {
            int j;
            for (j = valor; auxiliar[j] == valorClases[i - 1]; j++) {
                ;
            }
            valorClases[i] = auxiliar[j];
            valor = j;
        }

        muestPorClaseTrain = new int[nClases];
        for (int j = 0; j < nClases; j++) {
            muestPorClaseTrain[j] = 0;
            for (i = 0; i < datosTrain.size(); i++) {
                if (valorClases[j] == clasesTrain[i]) {
                    muestPorClaseTrain[j]++;
                }
            }
        }

        clasesTest = test.getC();
        muestPorClaseTest = new int[nClases];
        for (int j = 0; j < nClases; j++) {
            muestPorClaseTest[j] = 0;
            for (i = 0; i < datosTest.size(); i++) {
                if (valorClases[j] == clasesTest[i]) {
                    muestPorClaseTest[j]++;
                }
            }
        }

        int [] clasesEval;
        clasesEval = eval.getC();
        muestPorClaseEval = new int[nClases];
        for (int j = 0; j < nClases; j++) {
            muestPorClaseEval[j] = 0;
            for (i = 0; i < datosEval.size(); i++) {
                if (valorClases[j] == clasesEval[i]) {
                    muestPorClaseEval[j]++;
                }
            }
        }

        tamanoEstrella = tamEstrella; // Establace el tamaño de la estrella
        tiempo = System.currentTimeMillis(); // medimos tiempo
        div = (double) 1.0 / datosTrain.size();

        nombreAtributos = train.dameNombres();
        nombreClases = train.dameClases();
        if (nombreClases == null){
            nombreClases = new String[nClases];
            for (i = 0; i < nClases; i++){
                nombreClases[i] = ""+valorClases[i];
            }
        }


    }

    /**
     * Crea un conjunto de datos (atributos/clase) segun los obtenidos de un fichero de datos
     * @param mis_datos Debe ser un conjunto de datos leido del fichero (mirar doc Dataset.java)
     * @return El conjunto de datos ya creado, es decir, una lista enlazada de muestras (consultar ConjDatos.java y Muestra.java)
     */
    private ConjDatos creaConjunto(Dataset mis_datos) {
        ConjDatos datos = new ConjDatos(); //Creo un nuevo conjunto de datos
        int tam = mis_datos.getnentradas(); //Pillo el número de atributos de entrada (suponemos una sola salida [clase])
        double[] vars = new double[tam]; //Creamos el vector que guardará los valores de los atributos (aun siendo enteros o enum)
        double[][] X;
        int[] C;
        int clase = 0; //Variable que contendrá el valor para la clase
        boolean salir = false;
        X = mis_datos.getX();
        C = mis_datos.getC();
        for (int i = 0; i < mis_datos.getndatos(); i++) {
            salir = false;
            //System.out.print("\n"+i+":");
            for (int j = 0; (j < tam); j++) {
                //System.out.print(" "+X[i][j]);
                if (mis_datos.isMissing(i, j)) {
                    vars[j] = mis_datos.masComun(j);
                } else { //CAMBIAR POR OTROS METODOS DE MANEJO DE VALORES PERDIDOS (15-NN).
                    vars[j] = X[i][j]; //Double.parseDouble(mis_datos.getDatosIndex(i, j)); //pillo el valor del atributo j para el ejemplo i
                }
            }
            //if (!salir) {
            clase = C[i]; //Integer.parseInt(mis_datos.getDatosIndex(i, tam));
            Muestra m = new Muestra(vars, clase, tam); //Creo un nuevo dato del conjunto con sus variables, su clase y el num variables
            m.setPosFile(i);
            datos.addDato(m);
            //}
        }
        return datos;
    }

    /**
     * Realiza el algoritmo CN2SD
     */
    public void ejecutar() {
        hazSelectores(); //Creo todos los posibles selectores para las reglas
        ConjDatos datosTrainAux = new ConjDatos();
        datosTrainAux = datosTrain.copiaConjDatos();
        CN2desordenado(datosTrainAux, valorClases);
        tiempo = System.currentTimeMillis() - tiempo;

        /** Elimino posibles reglas repetidas **/
        for (int i = 0; (i < conjReglasFinal.size()-1); i++){
            boolean compara = false;
            for (int j = i+1; (j < conjReglasFinal.size())&&(!compara); j++){
                compara = conjReglasFinal.getRegla(i).esIgual(conjReglasFinal.getRegla(j));
            }
            if (compara){ //está
                //System.out.println("entro");
                conjReglasFinal.deleteRegla(i); //lo borro
                i--;
            }
        }

        evReg = new EvaluaCalidadReglas(conjReglasFinal, datosEval, datosTest,
                                        muestPorClaseEval, muestPorClaseTest,
                                        multiplicativo, nu,nombreClases);

        generaSalida();
    }

    /**
     * Crea el conjunto total selectores para obtener así todas las posibles reglas
     */
    private void hazSelectores() {
        int totalAtributos = datosTrain.getDato(0).getNatributos();
        int ejemplos = datosTrain.size();
        double[][] lista = new double[ejemplos + 1][totalAtributos]; //Para ver los !=
        for (int i = 0; i < totalAtributos; i++) { // para todos los atributos [columnas]
            lista[0][i] = datosTrain.getDato(0).getMuest()[i]; //Inicializo
            lista[1][i] = Double.POSITIVE_INFINITY; //marcador
        }

        for (int i = 0; i < totalAtributos; i++) { // para todos los atributos [columnas]
            for (int j = 1; j < ejemplos; j++) { //Para todos los ejemplos
                double valor = datosTrain.getDato(j).getMuest()[i];
                int k = 0;
                while (!(Double.isInfinite(lista[k][i]))) { //Mientras no vea la marca
                    if (lista[k][i] == valor) { //Si está repe
                        break;
                    }
                    k++;
                }
                if (Double.isInfinite(lista[k][i])) { //Si se ha recorrido toda la lista
                    lista[k][i] = valor;
                    lista[k + 1][i] = Double.POSITIVE_INFINITY;
                }
            }
        }
        almacenSelectores = new Complejo(nClases); //Aqui voy a almacenar los selectores (numVariable,operador,valor)
        for (int i = 0; i < totalAtributos; i++) { // para todos los atributos [columnas]
            for (int h = 0; h < ejemplos; h++) { //Para todos los ejemplos
                if (Double.isInfinite(lista[h][i])) {
                    break; //Siguiente atributo, ya los he gastado todos
                }
                for (int j = 0; j < 4; j++) { // Para 3 posibles valores en la comparac <>,<=,>
                    Selector s = new Selector(i, j, lista[h][i]); //Tomo el valor de cada atributo (columna) [atr,op,valor]
                    almacenSelectores.addSelector(s); //Lo meto si no esta repetido
                }
            }
        }
        //Ahora hago los del operador = (valores disjuntos)
        if (eficacia == 1) {
            for (int i = 0; i < totalAtributos; i++) { // para todos los atributos [columnas]
                int total;
                for (total = 0; !(Double.isInfinite(lista[total][i])); total++) {
                    ;
                }
                ArrayList list = new ArrayList();
                ArrayList listaAux = new ArrayList();
                for (int j = 0; j < total - 1; j++) {
                    for (int k = j + 1; k < total; k++) {
                        double[] valores = new double[2];
                        valores[0] = lista[j][i];
                        valores[1] = lista[k][i];
                        /*System.out.print("\n Selector -> Atributo(" + i +
                                         ") = " + valores[0]);
                                             for (int x = 1; x < 2; x++) {
                            System.out.print(" ó " + valores[x]);
                                             }*/
                        listaAux.add(valores);
                        Selector s = new Selector(i, 0, valores); //Tomo el valor de cada atributo (columna) [atr,op,valor]
                        almacenSelectores.addSelector(s); //Lo meto si no esta repetido
                    }
                }
                //Ahora voy a crear todos los subconjuntos a partir de los de tamaño 2:
                for (int l = 3; l < total - 2; l++) {
                    double[] auxi = new double[l - 1];
                    double[] auxi2 = new double[l - 1];
                    list.addAll(listaAux);
                    listaAux.clear();
                    while (!list.isEmpty()) {
                        boolean salir = false;
                        auxi = (double[]) list.remove(0); //lo cojo y lo elimino
                        for (int j = 0; (j < list.size()) && (!salir); j++) {
                            auxi2 = (double[]) list.get(j);
                            for (int k = 0; (k < auxi.length - 1) && (!salir);
                                         k++) {
                                salir = !(auxi[k] == auxi2[k]);
                            }
                            if (!salir) {
                                double[] valores = new double[l]; //contendra los subconjuntos
                                for (int k = 0; k < l - 1; k++) {
                                    valores[k] = auxi[k];
                                }
                                valores[l - 1] = auxi2[l - 2];
                                listaAux.add(valores);
                                Selector s = new Selector(i, 0, valores);
                                /*System.out.print("\n Selector -> Atributo(" + i +
                                                 ") = " + valores[0]);
                                 for (int x = 1; x < l; x++) {
                                    System.out.print(" ó " + valores[x]);
                                                             }*/
                                almacenSelectores.addSelector(s);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Procedimiento CN2desordenado. Genera reglas desordenadas para cada clase del conj. entrenamiento
     * @param datosTrainAux ConjDatos Conjunto de ejemplos de entrenamiento
     * @param valorClases int[] Clases de entrenamiento
     */
    private void CN2desordenado(ConjDatos datosTrainAux, int[] valorClases) {
        conjReglasFinal = new ConjReglas();
        conjReglasFinal.adjuntaNombreClases(nombreClases);
        conjReglasFinal.adjuntaNombreClase(nombreAtributos[nAtributos]);
        System.out.println("\n Extracting rules for the different classes:");
        for (int i = 0; i < nClases; i++) { //Para cada clase en train
            CN2paraUnaClase(datosTrainAux, valorClases[i]);
            //conjReglasFinal.addReglas(conjReglasAux);
        }
    }

    /**
     * Obtiene las reglas para una clase concreta.
     * @param train ConjDatos datos de ejemplo de entrenamiento
     * @param clase int Clase para la que obtendremos las reglas
     */
    private void CN2paraUnaClase(ConjDatos train, int clase) {
        //ConjReglas reglas = new ConjReglas();      * @return ConjReglas Las reglas (desordenadas) que explican 'train' para la clase 'clase'
        boolean continuar = false;
        int contador = 0;
        int quedan = muestPorClaseTrain[clase];
        System.out.println("\n We search for the best complex for class " +
                           nombreClases[clase]+" ["+quedan+" examples remaining]");
        do {
            continuar = false;
            Complejo mejorComplex = encontrarMejorComplejo(train, clase);
            if (mejorComplex != null) {
                mejorComplex.adjuntaNombreAtributos(nombreAtributos);
                System.out.println("\n\nComplex Found:");
                mejorComplex.print();
                conjReglasFinal.addRegla(mejorComplex);
                for (int i = 0; i < train.size(); i++) {
                    Muestra m = train.getDato(i);
                    if ((mejorComplex.cubre(m)) &&
                        (mejorComplex.getClase() == m.getClase())) {
                        //Cubre al ejemplo y es un verdadero positivo
                        m.incrementaCubierta();
                        if (m.getCubierta() == 1) {
                            contador++;
                            quedan--;
                        }
                    }
                }
                continuar = true;
            }
            double porc = (double) contador / muestPorClaseTrain[clase];
            if (porc >= seCubre) {
                continuar = false;
            }
            System.out.println("\nPorcentage of examples covered -> " +
                               porc * 100 + "% Remaining:<" + quedan + ">");
        } while (continuar);
        //return reglas;
    }

    /**
     * Procedimiento que se encarga de descubrir el mejor complejo para los datos dados
     * @param train ConjDatos
     * @param clase int
     * @return Complejo
     */
    private Complejo encontrarMejorComplejo(ConjDatos train, int clase) {
        Complejo mejorComplex = new Complejo(nClases);
        ConjReglas star = new ConjReglas();
        //boolean continuar = true;
        //Creo la estrella inicial
        for (int i = 0; i < almacenSelectores.size(); i++) {
            Complejo aux = new Complejo(nClases);
            aux.setClase(clase);
            aux.addSelector(almacenSelectores.getSelector(i));
            evaluarComplejo(aux, train);
            star.addRegla(aux);
        }
        //Ordenar
        Collections.sort(star.getConjReglas());
        //Comprobar significancia estadistica mejor complejo (opcional)
        //....
        //Obtener mejor complejo
        star.eliminaSubsumidos(tamanoEstrella);
        /*boolean compara = true;
        for (int j = 0; (j < star.size())&&(compara); j++){
            compara = false;
            for (int i = 0; (i < conjReglasFinal.size())&&(!compara); i++){
                compara = star.getRegla(j).esIgual(conjReglasFinal.getRegla(i));
            }
            if (compara){ //está
                star.deleteRegla(j); //lo borro
                j--;
            }
        }*/
        mejorComplex = star.getRegla(0);
        for (int j = star.size() - 1; star.size() > tamanoEstrella;
                     star.deleteRegla(j), j--) {
            ; //Borramos -> busqueda dirigida
        }
        //star.print();
        //do {
        for (int tam = 1; tam < nAtributos; tam++) {
            //a) Especializar todo complejo en Star
            ConjReglas newStar = new ConjReglas();
            for (int i = 0; i < almacenSelectores.size(); i++) {
                Selector s = almacenSelectores.getSelector(i);
                for (int j = 0; j < star.size(); j++) {
                    Complejo aux2 = star.getRegla(j); //complejo a especializar
                    Complejo aux = new Complejo(nClases); //complejo especializado
                    boolean sigue = true;
                    for (int h = 0; (h < aux2.size()) && (sigue); h++) {
                        Selector s2 = aux2.getSelector(h);
                        aux.addSelector(s2);
                        if (s2.compareTo(s) < 2) { //mismo atributo
                            sigue = false; //no lo añado
                        }
                    }
                    if (sigue) { //El selector no esta repetido en el complejo dado de "star"
                        aux.addSelector(s);
                        aux.setClase(clase);
                        evaluarComplejo(aux, train);
                        newStar.addRegla(aux);
                    }
                }
            }
            Collections.sort(newStar.getConjReglas());
            //Comprobar significancia estadistica mejor complejo (opcional)
            //....
            //Obtener mejor complejo
            //esSignificativa(mejorComplex);
            //mejorComplex = newStar.getUltimaRegla();
            eliminaNoValidos(newStar); //elimino repetidos y subsumidos para quedarme las tamEstrella mejores reglas
            //Compruebo que el mejor complejo no está ya en mi conjunto de reglas
            /*compara = true;
            for (int j = 0; (j < newStar.size())&&(compara); j++){
                compara = false;
                for (int i = 0; (i < conjReglasFinal.size())&&(!compara); i++){
                    compara = newStar.getRegla(j).esIgual(conjReglasFinal.getRegla(i));
                }
                if (compara){ //está
                    newStar.deleteRegla(j); //lo borro
                    j--;
                }
            }*/
            if (mejorComplex.compareTo(newStar.getRegla(0)) == 1) { //es peor
                mejorComplex = newStar.getRegla(0);
            }
            //for (; newStar.size() > tamanoEstrella; newStar.deleteRegla(0)) {
            for (int j = newStar.size() - 1; newStar.size() > tamanoEstrella;
                         newStar.deleteRegla(j), j--) {
                ; //Borramos -> busqueda dirigida
            }
            star.deleteAll();
            star.addReglas(newStar);
            System.out.print("\nNew Star created of size " + (tam + 1));
            //star.print();
        } //while(continuar);
        return mejorComplex;
    }

    /**
     * Se encarga de eliminar los complejos no validos en newStar: Estan en 'star' o son nulos (at1 = 0 ^ at1 <> 0)
     * @param newStar ConjReglas El nuevo conjunto de complejos que estamos creando
     */
    private void eliminaNoValidos(ConjReglas newStar) {
        //Primero quitamos los nulos: se repiten atributos!
        //newStar.eliminaNulos(); //Esta hecho conforme se construye [tienen distinto atributo]

        //Eliminamos los complejos que esten repetidos dentro de newStar!
        //newStar.eliminaRepetidos(tamanoEstrella); //Elimino hasta quedarme con "tamEstrella"

        newStar.eliminaSubsumidos(tamanoEstrella); //Elimino reglas que sean semánticamente iguales (At = 1, At <> 0, At = [0,1])

    }

    /**
     * Test de significancia estadística. El complejo c es significativo si su valor superar un umbral dado.
     * <br/>El cálculo se realiza como 2*SUM[fi·log(fi/ei)] donde:
     * <br/>fi es la distribución de ejemplos cubiertos por c -> Nº ej's cubiertos clase i / nº ejemplos
     * <br/>ei es la distribución de ejemplos cubiertos supuestamente aleatoriamente -> Nº ej's clase i / nº ejemplos
     * @param c Complejo El complejo a analizar
     * @return boolean True si es significativa (supera el umbral dado) false en otro caso
     */
    private boolean esSignificativa(Complejo c) {
        double significancia = 0;
        double pCond = 0;
        for (int j = 0; j < nClases; j++) {
            pCond += c.getDistribucionClaseEj(j);
        }
        pCond *= 1.0 / datosTrain.size();
        for (int j = 0; j < nClases; j++) {
            double logaritmo = (double) c.getDistribucionClaseEj(j) /
                               (this.muestPorClaseTrain[j] * pCond);
            if (logaritmo != 0) {
                logaritmo = Math.log(logaritmo);
                logaritmo *= (double) c.getDistribucionClaseEj(j);
                significancia += logaritmo;
            }
        }
        significancia *= 2.0;
        //System.out.println("Umbral -> " + significancia);

        return (significancia >= umbral);

    }


    /**
     * ReCalcula el peso multiplicativo para un ejemplo
     * @param i el número de reglas que cubren al ejemplo
     * @return el nuevo peso
     */
    private double pesoMultiplicativo(int i) {
        double aux;
        aux = Math.pow(nu, i);
        return aux;
    }

    /**
     * ReCalcula el peso aditivo para un ejemplo
     * @param i int el número de reglas que cubren al ejemplo
     * @return double el nuevo peso
     */
    private double pesoAditivo(int i) {
        double aux;
        aux = 1.0 / (i + 1);
        return aux;
    }

    /** Evaluacion de los complejos sobre el conjunto de ejemplo para ver cuales se
     *   cubren de cada clase
     * @param c Complejo a evaluar
     * @param e Conjunto de datos
     */
    private void evaluarComplejo(Complejo c, ConjDatos e) {
        int i;
        double n, ncond, nclascond, nclas;
        int cl;
        double val, peso = 0;

        n = 0;
        ncond = 0;
        nclascond = 0;
        nclas = 0;

        c.borraDistrib();

        for (i = 0; i < e.size(); i++) {
            cl = e.getDato(i).getClase();
            if (multiplicativo == 1) {
                peso = this.pesoMultiplicativo(e.getDato(i).getCubierta());
            } else {
                peso = this.pesoAditivo(e.getDato(i).getCubierta());
            }
            n += peso;

            if (c.cubre(e.getDato(i))) {
                c.incrementaDistrib(cl);
                ncond += peso;
                if (cl == c.getClase()) {
                    nclascond += peso;
                }
            }
            if (cl == c.getClase()) {
                nclas += peso;
            }
        }
        if (n != 0 && ncond != 0) {
            val = (ncond / n) * ((nclascond / ncond) - (nclas / n));
        } else {
            val = Double.MIN_VALUE;
        }
        c.setHeuristica(val);
        c.ajustaDistrib();
    }

    /**
     * Calcula los datos estadísticos necesarios y crea los ficheros KEEL de salida
     */
    private void generaSalida() {
        Fichero f = new Fichero();
        String cad = "";

        //System.out.println("\n Estas son las reglas encontradas:");
        //conjReglasFinal.print();

        cad = conjReglasFinal.printString();
        cad += "\n\n" + evReg.printString() + ";\n  Time; " + (tiempo / 1000);
        f.escribeFichero(ficheroSalida, cad);

        f.escribeFichero(ficheroSalidaTr, miSalida + evReg.salida(datosTrain));
        f.escribeFichero(ficheroSalidaTst, miSalida + evReg.salida(datosTest));

    }

}

