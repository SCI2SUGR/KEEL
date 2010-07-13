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

package keel.Algorithms.Genetic_Rule_Learning.apriori;

/**
 * <p>Título: </p>
 * <p>Descripción: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Empresa: </p>
 * @author sin atribuir
 * @version 1.0
 */

import java.io.*;
import keel.Dataset.*;
import java.util.*;

public class Dataset {

    private int[][] X = null;
    private boolean[][] missing = null;
    private int[] C = null;
    private double[] emaximo;
    private double[] eminimo;
    private double smaximo;
    private double sminimo;

    private int ndatos; // Number of examples
    private int nvariables; // Numer of variables
    private int nentradas; // Number of inputs
    private int nclases; // Number of classes

    private InstanceSet IS;

    final static boolean debug = false;

    private int[] cambio;
    private int[] comunes;

    /**
     * Devuelve una copia completa del Dataset
     * @return Dataset Una copia del conjunto de datos leido
     */
    public Dataset copiaDataSet() {
        Dataset copia = new Dataset();
        copia.X = new int[this.ndatos][this.nentradas];
        copia.missing = new boolean[this.ndatos][this.nentradas];
        copia.C = new int[this.ndatos];
        copia.emaximo = new double[this.nentradas];
        copia.eminimo = new double[this.nentradas];
        for (int i = 0; i < this.ndatos; i++) {
            copia.C[i] = this.C[i];
            for (int j = 0; j < this.nentradas; j++) {
                copia.X[i][j] = this.X[i][j];
                copia.missing[i][j] = this.missing[i][j];
            }
        }
        for (int j = 0; j < this.nentradas; j++) {
            copia.emaximo[j] = this.emaximo[j];
            copia.eminimo[j] = this.eminimo[j];
        }
        copia.ndatos = this.ndatos;
        copia.nvariables = this.nvariables;
        copia.nentradas = this.nentradas;
        copia.nclases = this.nclases;
        copia.smaximo = this.smaximo;
        copia.sminimo = this.sminimo;
        return copia;
    }

    /**
     * Devuelve los valores de los atributos de entrada
     * @return double[][] Un array con los atributos de entrada
     */
    public int[][] getX() {
        return X;
    }

    /**
     * Devuelve los valores para la salida (clase)
     * @return int[] Un array con los valores de la clase
     */
    public int[] getC() {
        return C;
    }

    /**
     * Realiza una copia del valor de las clases
     * @return int[] El valor de la clase para cada ejemplo
     */
    public int[] copiaC() {
        int[] retorno = new int[C.length];
        for (int i = 0; i < C.length; i++) {
            retorno[i] = C[i];
        }
        return retorno;
    }

    /**
     * Devuelve un array con los valores máximos de los atributos de entrada
     * @return double[] idem
     */
    public double[] getemaximo() {
        return emaximo;
    }

    /**
     * Devuelve un array con los valores mínimos de los atributos de entrada
     * @return double[] idem
     */
    public double[] geteminimo() {
        return eminimo;
    }

    /**
     * Devuelve el número de datos
     * @return int el número de ejemplos
     */
    public int getndatos() {
        return ndatos;
    }

    /**
     * Devuelve el número de variables
     * @return int El número de variables (incluyendo entrada y salida)
     */
    public int getnvariables() {
        return nvariables;
    }

    /**
     * Devuelve el número de variables de entrada
     * @return int El total de variables de entrada
     */
    public int getnentradas() {
        return nentradas;
    }

    /**
     * Devuelve el número total de clases
     * @return int el número de clases distintas
     */
    public int getnclases() {
        return nclases;
    }

    /**
     * Comprueba si un atributo está "perdido" o no
     * @param i int Número de ejemplo
     * @param j int Número de atributo
     * @return boolean True si falta, False en otro caso
     */
    public boolean isMissing(int i, int j) {
        // True is the value is missing (0 in the table)
        return missing[i][j];
    }

    /**
     * Constructor. Crea un nuevo conjunto de instancias
     */
    public Dataset() {
        IS = new InstanceSet(); // Init a new set of instances
    }

    /**
     * Lee el fichero de ejemplos (train o test)
     * @param nfejemplos String Nombre del fichero de ejemplos
     * @param train boolean True si se refiere al conjunto de entrenamiento. False si es test
     * @throws IOException Un posible error de E/S
     */
    public void leeConjunto(String nfejemplos, boolean train) throws
            IOException {
        try {
            // Load in memory a dataset that contains a classification problem
            IS.readSet(nfejemplos, train);
            ndatos = IS.getNumInstances();
            nentradas = Attributes.getInputNumAttributes();
            nvariables = nentradas + Attributes.getOutputNumAttributes();

            // Check that there is only one output variable
            if (Attributes.getOutputNumAttributes() > 1) {
                System.out.println(
                        "This algorithm can not process MIMO datasets");
                System.out.println(
                        "All outputs but the first one will be removed");
                System.exit(1); //TERMINAR
            }
            boolean noOutputs = false;
            if (Attributes.getOutputNumAttributes() < 1) {
                System.out.println(
                        "This algorithm can not process datasets without outputs");
                System.out.println("Zero-valued output generated");
                noOutputs = true;
                System.exit(1); //TERMINAR
            }

            // Initialice and fill our own tables
            X = new int[ndatos][nentradas];
            missing = new boolean[ndatos][nentradas];
            C = new int[ndatos];

            // Maximum and minimum of inputs
            emaximo = new double[nentradas];
            eminimo = new double[nentradas];

            // All values are casted into double/integer
            nclases = 0;
            for (int i = 0; i < ndatos; i++) {
                Instance inst = IS.getInstance(i);
                for (int j = 0; j < nentradas; j++) {
                    X[i][j] = (int) IS.getInputNumericValue(i, j); //inst.getInputRealValues(j);
                    missing[i][j] = inst.getInputMissingValues(j);
                    if (X[i][j] > emaximo[j] || i == 0) {
                        emaximo[j] = X[i][j];
                    }
                    if (X[i][j] < eminimo[j] || i == 0) {
                        eminimo[j] = X[i][j];
                    }
                }

                if (noOutputs) {
                    C[i] = 0;
                } else {
                    C[i] = (int) IS.getOutputNumericValue(i, 0); //(int)inst.getOutputRealValues(i);
                }
                if (C[i] > nclases) {
                    nclases = C[i];
                }
            }
            nclases++;
            System.out.println("Number of classes=" + nclases);

        } catch (Exception e) {
            System.out.println("DBG: Exception in readSet");
            e.printStackTrace();
        }
    }

    /**
     * Devuelve un String con la cabecera del fichero
     * @return String Los datos de la cabecera del fichero (train)
     */
    public String copiaCabeceraTest() {
        // Header of the output file
        String p = new String("");
        p = "@relation " + Attributes.getRelationName() + "\n";
        p += Attributes.getInputAttributesHeader();
        p += Attributes.getOutputAttributesHeader();
        p += Attributes.getInputHeader() + "\n";
        p += Attributes.getOutputHeader() + "\n";
        p += "@data\n";
        return p;
    }

    /**
     * Convierte todos los valores del conjunto de datos en el intervalo [0,1]
     */
    public void normaliza() {
        int atts = this.getnentradas();
        double maximos[] = new double[atts];
        for (int j = 0; j < atts; j++) {
            maximos[j] = 1.0 / (emaximo[j] - eminimo[j]);
        }
        for (int i = 0; i < this.getndatos(); i++) {
            for (int j = 0; j < atts; j++) {
                if (isMissing(i, j)) {
                    ; //no escojo este ejemplo
                } else {
                    X[i][j] = (int) (X[i][j] - eminimo[j]) * (int) maximos[j];
                }
            }
        }
    }

    /**
     * Devuelve los tipos de cada entrada (NOMINAL[0] o NUMERICO[1])
     * @return int[] Un vector que contiene 0 o 1 en funcion de si los atributos son nominales o numericos
     */
    public int[] tiposVar() {
        int[] tipos = new int[this.nentradas];
        for (int i = 0; i < this.nentradas; i++) {
            tipos[i] = 1;
            if (Attributes.getAttribute(i).getType() == Attribute.NOMINAL) {
                tipos[i] = 0;
            }
        }
        return tipos;
    }

    /**
     * Calcula los valores mas comunes para cada columna o atributo
     */
    public void calculaMasComunes() {
        comunes = new int[nentradas];
        int[] aux = new int[ndatos];
        for (int i = 0; i < nentradas; i++) {
            for (int j = 0; j < ndatos; j++) {
                if (this.isMissing(j, i)) {
                    aux[j] = -1;
                } else {
                    aux[j] = (int) X[j][i];
                }
            }
            Arrays.sort(aux);
            int mascomun = aux[0];
            int contador = 1, j;
            for (j = 1; (aux[j] == mascomun) && (j < ndatos - 1); j++, contador++) {
                ;
            }
            int contador2 = 1;
            int mascomun2 = aux[j];
            if (j + 1 < ndatos) {
                for (j = j + 1; j < ndatos; j++) {
                    if (aux[j] == mascomun2) {
                        contador2++;
                    } else {
                        mascomun2 = aux[j];
                        if (contador2 > contador) {
                            contador = contador2;
                            mascomun = mascomun2;
                            contador2 = 1;
                        }
                    }
                }
            }
            comunes[i] = mascomun;
        }
    }

    /**
     * Devuelve el valor mas común del atributo i-esimo
     * @param i int Número de atributo
     * @return int Valor mas comnun para esta variable
     */
    public int masComun(int i) {
        return comunes[i];
    }

    /**
     * Devuelve el nombre de las variables del problema
     * @return String[] Un Array con los nombres de las variables del problema
     */
    public String[] dameNombres() {
        String[] salida = new String[nvariables];
        for (int i = 0; i < nentradas; i++) {
            salida[i] = Attributes.getInputAttribute(i).getName();
        }
        salida[nentradas] = Attributes.getOutputAttribute(0).getName();
        return salida;
    }

    /**
     * Devuelve el valor de las clases
     * @return String[] Un array con el valor para las distintas salidas (clases)
     */
    public String[] dameClases() {
        String[] salida = new String[nclases];
        Attribute at = Attributes.getOutputAttribute(0);
        if (at.getType() == at.NOMINAL) {
            for (int i = 0; i < nclases; i++) {
                salida[i] = at.getNominalValue(i);
            }
        } else {
            salida = null; //luego guardaré el valor de las clases numéricas
        }
        return salida;
    }

    /**
     * Comprueba si en la base de datos hay alguna entrada de tipo real o contínua
     * @return boolean True si existe alguna entrada continua. False en caso contrario
     */
    public boolean hayAtributosContinuos() {
        return Attributes.hasRealAttributes();
    }

    /**
     * Ordena un conjunto de datos lexicograficamente por columnas y filas respectivamente
     * @return int[] Un array de enteros correspondiente al nuevo valor maximo de cada columna
     */
    public int[] ordenLexicografico() {
        int[] maximos = new int[this.nentradas + 1];
        int[] auxi = new int[this.ndatos];
        int[] auxi2 = new int[this.ndatos];

        cambio = new int[this.ndatos * (this.nentradas + 1)]; //Aqui guardo los cambios realizados

        int anterior = 0;
        for (int i = 0; i < this.nentradas; i++) { //para cada columna (atributos)
            //Ordeno de menor a mayor la columna
            for (int j = 0; j < this.ndatos; j++) {
                if (this.isMissing(j, i)) {
                    auxi[j] = -1;
                } else {
                    auxi[j] = this.X[j][i];
                }
            }
            Arrays.sort(auxi);
            int valor = -1;
            int total = 0;
            for (int j = 0; valor != this.emaximo[i]; j++) { //Ahora elimino repetidos
                if (valor != auxi[j]) {
                    auxi2[total] = auxi[j];
                    valor = auxi[j];
                    total++;
                }
            }
            for (int j = 0; j < this.ndatos; j++) {
                boolean seguir = true;
                for (int l = 0; (l < total) && (seguir); l++) { //total
                    if (X[j][i] == auxi2[l]) {
                        if (this.isMissing(j, i)) {
                            X[j][i] = -1;
                            seguir = false;
                        } else {
                            cambio[l + anterior] = X[j][i];
                            X[j][i] = l + anterior;
                            seguir = false;
                        }
                    }
                }
            }
            anterior += total;
            maximos[i] = anterior - 1;
        }
        //Ahora para la clase
        for (int j = 0; j < this.ndatos; j++) {
            auxi[j] = this.C[j];
        }
        Arrays.sort(auxi);
        int valor = Integer.MIN_VALUE;
        int total = 0;
        for (int j = 0; j < ndatos; j++) { //Ahora elimino repetidos
            if (valor != auxi[j]) {
                auxi2[total] = auxi[j];
                valor = auxi[j];
                total++;
            }
        }
        for (int j = 0; j < this.ndatos; j++) {
            boolean seguir = true;
            for (int l = 0; (l < total) && (seguir); l++) {
                if (C[j] == auxi2[l]) {
                    cambio[l + anterior] = C[j];
                    C[j] = l + anterior;
                    seguir = false;
                }
            }
        }
        maximos[this.nentradas] = anterior + total - 1;
        return maximos;
    }

    /**
     * Devuelve los valores iniciales del conjunto de datos antes de realizar el cambio
     * @return int[] Los valores originales del conjunto (0 -> X, 1 -> Y...)
     */
    public int[] getCambio() {
        return cambio;
    }
}

