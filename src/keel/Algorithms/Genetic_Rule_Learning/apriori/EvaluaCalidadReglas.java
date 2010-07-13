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
 * <p>Título: Evaluación de calidad de las reglas</p>
 * <p>Descripción: Esta clase calcula las estadísticas finales</p>
 * <p>Creado: 04-ago-2004 </p>
 * @author José Ramón Cano de Amo
 * @version 1.0
 */
public class EvaluaCalidadReglas {

    private double porcAciertoTr;
    private double porcAciertoTst;
    private ConjReglas reglas;
    private int nClases;
    private int clasePorDefecto;
    private double relTrain, relTest;
    private int tam;
    private String[] valorNombreClases;

    /**
     * Calculas las estadísticas finales para un conjunto de reglas dado y un conjunto de datos.
     * @param conjreg Conjunto de reglas (complejos) final
     * @param nClases int Numero de clases en el problema
     * @param valorNombreClases String[] etiquetas para cada una de las clases
     */
    public EvaluaCalidadReglas(ConjReglas conjreg, int nClases,
                               String[] valorNombreClases) {
        reglas = conjreg; //.copiaConjReglas();
        this.nClases = nClases;
        clasePorDefecto = 0;
        this.valorNombreClases = valorNombreClases;
    }

    /**
     * Imprime en una cadena las estadisticas
     * @return una cadena con las estadísticas
     */
    public String printString() {
        String cad = new String("####Final Results####\n\n");
        cad += "Rule Size: "+tam+ "\n";
        cad += "Avg. Significance (train): " + relTrain + "\n";
        cad += "Avg. Significance (test): " + relTest +
                "\n------------------\n";
        cad += "Accuracy Training: " + porcAciertoTr + "\n"; ;
        cad += "Accuracy Test: " + porcAciertoTst + "\n";

        return cad;
    }

    /**
     * Genera un String con la lista de salidas, es decir, &lt;salida esperada&gt; &lt;salida del metodo&gt;
     * (en nuestro caso, clase en el fichero original, clase que obtiene el metodo)
     * @param datos Es el conjunto de datos con el que queremos comparar para nuestro conjunto de reglas
     * @param conj Se refiere si es el conjunto de train o test (0,1)
     * @return Una cadena con una lista de pares &lt;clase original&gt; &lt;clase calculada&gt;
     */
    public String salida(ConjDatos datos, int conj) {
        String cadena = new String("");
        int cl, cl2, aciertos = 0;

        if (conj == 0) {
            int[] clases = new int[nClases];

            for (int i = 0; i < datos.size(); i++) {
                clases[datos.getDato(i).getClase()]++;
            }
            for (int i = 0, clase = -1; i < nClases; i++) {
                if (clases[i] > clase) {
                    clasePorDefecto = i;
                    clase = clases[i];
                }
            }
        }
        for (int i = 0; i < datos.size(); i++) { //Para cada ej.
            boolean seguir = true;
            cl2 = datos.getDato(i).getClase();
            cl = Integer.MIN_VALUE;
            for (int j = 0; j < reglas.size() && seguir; j++) { //busco la regla que lo cubre
                if (reglas.getRegla(j).cubre(datos.getDato(i))) {
                    cl = reglas.getRegla(j).getClase();
                    cadena += new String(valorNombreClases[cl2] + " " + valorNombreClases[cl] + "\n"); //Anoto
                    seguir = false;
                }
            }
            if (seguir) { //No ha habido ninguna regla que lo cubra
                cl = clasePorDefecto;
                cadena += new String(valorNombreClases[cl2] + " " + valorNombreClases[cl] + "\n"); //Anoto
            }
            if (cl == cl2) {
                aciertos++;
            }
        }
        if (conj == 0) {
            porcAciertoTr = (double) aciertos / datos.size();
        } else {
            porcAciertoTst = (double) aciertos / datos.size();
        }
        return cadena;
    }

    /**
     * Calcula el likelihood ratio para el conjunto de datos con las reglas del problema
     * @param datos Es el conjunto de datos con el que queremos comparar para nuestro conjunto de reglas
     * @param muestPorClase int[] Número de ejemplos por cada clase en el conjunto de datos
     * @param conj Se refiere si es el conjunto de train o test (0,1)
     */
    public void calculaSignificance(ConjDatos datos, int[] muestPorClase, int conj){
        int i,j;
        int nDatos = datos.size();

        tam = reglas.size(); // calculamos Tam

        // calculamos la distrib
        int[][] instCubiertas = new int[tam][nClases];
        for (i = 0; i < reglas.size(); i++) {
            for (j = 0; j < nClases; j++) {
                instCubiertas[i][j] = 0;
            }
        }
        for (i = 0; i < reglas.size(); i++) {
            for (j = 0; j < nDatos; j++) {
                Muestra m = datos.getDato(j);
                if (reglas.getRegla(i).cubre(m)) {
                    instCubiertas[i][m.getClase()]++;
                }
            }
        }
        // Calculamos la relevancia (significance)
        double sigParcial = 0;
        double[] pCondi = new double[reglas.size()]; //Factor normalizador -> coverage
        for (i = 0; i < reglas.size(); i++) {
            pCondi[i] = 0;
            for (j = 0; j < nClases; j++) {
                pCondi[i] += instCubiertas[i][j];
            }
            pCondi[i] *= (double) 1.0 / nDatos;
        }
        double rel = 0;
        for (i = 0; i < reglas.size(); i++) {
            sigParcial = 0;
            for (j = 0; j < nClases; j++) {
                double logaritmo = (double) instCubiertas[i][j] /
                                   (muestPorClase[j] * pCondi[i]);
                if ((logaritmo != 0) && (!Double.isNaN(logaritmo)) &&
                    (!Double.isInfinite(logaritmo))) {
                    logaritmo = Math.log(logaritmo);
                    logaritmo *= (double) instCubiertas[i][j];
                    sigParcial += logaritmo;
                }
            }
            rel += sigParcial * 2;
        }
        rel /= (double) reglas.size();
        relTest = rel;
        if (conj == 0){
            relTrain = rel;
        }
    }

}

