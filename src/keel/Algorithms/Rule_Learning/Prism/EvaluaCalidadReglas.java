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

/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.Prism;


public class EvaluaCalidadReglas {
/**
 * Get the statistical data from the algorithm
 */
	
    private int nClases;
    private int nDatos;
    private int contClases[];

    private int tam;
    private double ant;
    private double cob;
    private double compl;
    private double rel;
    private double ati;
    private double porcAciertoTr;
    private double porcAciertoTst;
    private double muestCubiertas;

    private ConjDatos train;
    private ConjDatos test;
    private ConjReglas reglas;
    private String[] valorNombreClases;

    /**
     * <p>
     * Calculates the final statisticals for a set of rules and a set of data
     * </p>
     * @param conjreg Set of rules(complex) final
     * @param conjTrn Set of train data
     * @param conjTst Set of test data
     * @param muestPorClaseTrain int[] Number of examples of each class in train set
     * @param muestPorClaseTest int[] Number of examples of each class in train test
     * @param valorNombreClases String[] Labels for each class
     */
    public EvaluaCalidadReglas(ConjReglas conjreg, ConjDatos conjTrn,
                               ConjDatos conjTst, int[] muestPorClaseTrain,
                               int[] muestPorClaseTest,
                               String[] valorNombreClases) {

        reglas = conjreg; //referencia
        this.valorNombreClases = valorNombreClases;

        train = conjTrn.copiaConjDatos();
        test = conjTst.copiaConjDatos();

        nClases = conjreg.getUltimaRegla().getNClases();
        nDatos = conjTrn.size();

        // Calculos en Entrenamiento
        calculaIndices(train, muestPorClaseTrain, 0);
        System.out.print("\n\nTrain Statistics: ");
        System.out.print("\n\n Size of the rule set: " + tam +
                         "\nAverage number of attributes per rule: " + ant +
                         "\nCoverage: " +
                         cob);
//        System.out.print("\n\t Confidence: " + conf + "  ComplMed: " + complmed +
//                         "  Compl: " + compl);
        System.out.print("\nSupport: " + compl);
        System.out.print("\nRelevance: " + rel + "\nUnusualness: " + ati);
        System.out.print("\nAccuracy: " + porcAciertoTr);

//		Calculos en test
        calculaIndices(test, muestPorClaseTest, 1);
        System.out.print("\n\nTest Statistics:");
        System.out.print("\n\n Size of the rule set: " + tam +
                         "\nAverage number of attributes per rule: " + ant +
                         "\nCoverage: " +
                         cob);
//        System.out.print("\n\t Confidence: " + conf + "  ComplMed: " + complmed +
//                         "  Compl: " + compl);
        System.out.print("\nSupport: " + compl);
        System.out.print("\nRelevance: " + rel + "\nUnusualness: " + ati);

        System.out.print("\nAccuracy: " + porcAciertoTst);

    }

    /**
     * <p>
     * Prints on a string the statistical(for test)
     * </p>
     * @return the statisticals
     */
    public String printString() {
        String cad = "####Average results for test data####\n";

        //cad += "Avg. Confidence; " + conf + " ; \n ";
        //cad += "Avg. Suppport; " + complmed + " ; \n ";
        cad += "Avg. Rule length: " + tam + "\n";
        cad += "Avg. Number of attributes by rule: " + ant + "\n";
        cad += "Avg. Coverage: " + cob + "\n";
        cad += "Avg. Support: " + compl + "\n";
        cad += "Avg. Significance: " + rel + "\n";
        cad += "Avg. Unusualness: " + ati + "\n\n";

        cad += "Accuracy Training: " + porcAciertoTr + "\n"; ;
        cad += "Accuracy Test: " + porcAciertoTst;

        return cad;
    }

    /**
     * <p>
     * Calculates all the statistical results, especially percent mathes
     * </p>
     * @param datos Set of data(train or test)
     * @param muestPorClase int[] Number of examples for each class in the data set
     * @param code Train or Test
     */
    private void calculaIndices(ConjDatos datos, int[] muestPorClase, int code) {
        int i, j;
        int aciertos;

        nDatos = datos.size();

        // contamos el numero de mustras por clase
        contClases = new int[nClases];
        for (i = 0; i < nClases; i++) {
            contClases[i] = muestPorClase[i];
        }

        tam = reglas.size(); // calculamos Tam

        // calculamos n atributos por regla medio
        for (i = 0, ant = 0; i < reglas.size(); i++) {
            ant += reglas.getRegla(i).size();
        }

        ant = (double) ant / tam; //N de atributos por regla medio

        // calculamos la distrib
        muestCubiertas = 0; //n ejemplos cubiertos por las reglas
        int muestBienCubiertas = 0;
        int[][] instCubiertas = new int[tam][nClases];

        for (j = 0; j < nDatos; j++) {
            datos.getDato(j).setCubierta(0);
        }
        for (i = 0; i < reglas.size(); i++) {
            for (j = 0; j < nClases; j++) {
                instCubiertas[i][j] = 0;
            }
        }
        muestCubiertas = 0;
        for (i = 0; i < reglas.size(); i++) {
            for (j = 0; j < nDatos; j++) {
                Muestra m = datos.getDato(j);
                if (reglas.getRegla(i).cubre(m)) {
                    muestCubiertas++;
                    instCubiertas[i][m.getClase()]++;
                    if (reglas.getRegla(i).getClase() == m.getClase()) {
                        if (m.getCubierta() == 0) {
                            muestBienCubiertas++;
                            m.incrementaCubierta();
                        }
                    }
                }
            }
        }
        //System.err.println("Muestras cubiertas -> "+muestCubiertas);
        //System.err.println("Total datos -> "+nDatos);
        //cob = (double) muestCubiertas / (nDatos * tam * tam); //COV = 1/nRSUM[Cov(Ri)] -- Cov(Ri) = n(Condi)/N //
        cob = muestCubiertas / (tam * nDatos);
        //Cobertura -> porcentaje de ejemplos cubiertos por cada regla / n de reglas

        // Calculamos completitud y completitud  media [support]
        compl = (double) muestBienCubiertas / nDatos;

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
        rel = 0;
        for (i = 0; i < reglas.size(); i++) {
            sigParcial = 0;
            for (j = 0; j < nClases; j++) {
                double logaritmo = (double) instCubiertas[i][j] /
                                   (contClases[j] * pCondi[i]);
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

        // Calculamos la atipicidad de las reglas (unusualness) [ati]
        double aux;
        for (i = 0, aux = 0; i < reglas.size(); i++) { // para cada regla
            double ncondi, pcond, pclase, pcondclase;
            int cl = reglas.getRegla(i).getClase();
            for (j = 0, ncondi = 0; j < nClases; j++) {
                ncondi += reglas.getRegla(i).getDistribucionClase(j); //ncondi
            }
            pcond = ncondi / nDatos;

            pclase = (double) contClases[cl] / nDatos;

            pcondclase = reglas.getRegla(i).getDistribucionClase(cl) / nDatos;

            aux += pcond * (pcondclase - pclase);
        }

        ati = aux / reglas.size();

        //Ahora el porcentaje de aciertos
        int voto[] = new int[nClases];
        aciertos = 0;
        int clases[] = contClases; //new int[nClases];
        //int verificados[] = new int[nClases];
        int clase, cl;
        int distribucion[], max;
        int clasePorDefecto = 0;
        /*for (i = 0; i < datos.size(); i++) {
            clases[datos.getDato(i).getClase()]++;
                 }*/
        for (i = 0, clase = -1; i < nClases; i++) {
            if (clases[i] > clase) {
                clasePorDefecto = i;
                clase = clases[i];
            }
        }
        for (i = 0; i < datos.size(); i++) { // Para el conjunto completo de datos
            for (j = 0; j < nClases; j++) { //Inicializo voto a 0
                voto[j] = 0;
                //verificados[j] = 1;
            }
            for (j = 0; j < reglas.size(); j++) { // vemos que reglas verifican a la muestra
                if (reglas.getRegla(j).cubre(datos.getDato(i))) {
                    distribucion = reglas.getRegla(j).getDistribucion();
                    for (int k = 0; k < nClases; k++) {
                        voto[k] += distribucion[k];
                        //verificados[k]++;
                    }
                }
            }
            /*for (int k = 0; k < nClases; k++) {
                voto[k] /= verificados[k];
                         }*/
            //System.out.println("");
            for (j = 0, max = 0, cl = 0; j < nClases; j++) { //Obtengo la clase que me da mis reglas
                //System.out.print(" Voto["+j+"]="+voto[j]);
                if (voto[j] > max) {
                    max = voto[j];
                    cl = j;
                }
            }
            if (max == 0) { //No se ha activado ninguna regla
                cl = clasePorDefecto;
                //System.out.println("X defecto -> "+code);
            }
            if (cl == datos.getDato(i).getClase()) {
                aciertos++;
            }
        }

        System.out.print("\n\n Accuracy: " + (double)aciertos/datos.size() +
                         " ... total data: " + datos.size());
        if (code == 0) {
            porcAciertoTr = (double) aciertos / datos.size();
        } else {
            porcAciertoTst = (double) aciertos / datos.size();
        }
    }

    /**
     * <p>
     * Generates a string with the out-put lists
     * </p>
     * @param datos Set of data witch to compare the set of rules 
     * @return A list of pairs: 'original class;calculated class;'
     */
    public String salida(ConjDatos datos) {
        String cadena = new String("");
        int voto[] = new int[nClases];
        int clases[] = new int[nClases];
        int distribucion[], max;
        int j, cl, clasePorDefecto = 0;
        for (int i = 0; i < datos.size(); i++) {
            clases[datos.getDato(i).getClase()]++;
        }
        for (int i = 0, clase = -1; i < nClases; i++) {
            if (clases[i] > clase) {
                clasePorDefecto = i;
                clase = clases[i];
            }
        }
        for (int i = 0; i < datos.size(); i++) { // Para el conjunto completo de datos
            for (j = 0; j < nClases; j++) { //Inicializo voto a 0
                voto[j] = 0;
            }
            for (j = 0; j < reglas.size(); j++) { // vemos que reglas verifican a la muestra
                if (reglas.getRegla(j).cubre(datos.getDato(i))) {
                    distribucion = reglas.getRegla(j).getDistribucion();
                    for (int k = 0; k < nClases; k++) {
                        voto[k] += distribucion[k];
                    }
                }
            }
            for (j = 0, max = 0, cl = 0; j < nClases; j++) { //Obtengo la clase que me da mis reglas
                if (voto[j] > max) {
                    max = voto[j];
                    cl = j;
                }
            }
            if (max == 0) { //No se ha activado ninguna regla
                cl = clasePorDefecto;
            }
            cadena += new String(valorNombreClases[datos.getDato(i).getClase()] +
                                 " " +
                                 valorNombreClases[cl] + "\n");
        }
        return cadena;
    }

    /** 
     * <p>
     * Evaluation of the complex over the set of examples
     * </p>
     * @param c Complex to evaluate
     * @param e Set of data
     */
    private void evaluarComplejo(Complejo c, ConjDatos e) {
        c.borraDistrib();

        for (int i = 0; i < e.size(); i++) {
            int cl = e.getDato(i).getClase();

            if (c.cubre(e.getDato(i))) {
                c.incrementaDistrib(cl);
            }
        }
    }


}

