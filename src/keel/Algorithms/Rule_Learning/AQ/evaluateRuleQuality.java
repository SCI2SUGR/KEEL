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

package keel.Algorithms.Rule_Learning.AQ;

/**
 * <p>Title: Evaluation of the quality of the rules</p>
 * <p>Description: This class computes the final statistics </p>
 * @author Written by José Ramón Cano de Amo (University of Jaén) 08/04/2004
 * @author Modified by Alberto Fernández (University of Granada) 11/30/2004
 * @version 1.1
 * @since JDK1.4
 */
public class evaluateRuleQuality {

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

    private myDataset train;
    private myDataset test;
    private ruleSet reglas;

    private int clasePorDefecto;

    private String[] nombreClases;

    /**
     * It computes the final statistics for a rule set and a data-set
     * @param conjreg Final Rule set (complexes)
     * @param conjTrn Training data-set
     * @param conjTst Test data-set
     * @param muestPorClaseTrain int[] Number of examples in each class in the training set
     * @param muestPorClaseTest int[] Number of examples in each class in the test set
     * @param _nombreClases String [] Name of the classes of the problem
     */
    public evaluateRuleQuality(ruleSet conjreg, myDataset conjTrn,
                               myDataset conjTst, int[] muestPorClaseTrain,
                               int[] muestPorClaseTest, String[] _nombreClases) {

        reglas = conjreg;
        reglas.print();

        train = conjTrn.copyDataSet();
        test = conjTst.copyDataSet();

        nDatos = conjTrn.size();
        nClases = conjreg.getLastRule().getNclasses();
        nombreClases = _nombreClases;

        // Training computation
        calculaIndices(train, muestPorClaseTrain, 0);
        System.out.print("\n\nIndices en Train: ");
        System.out.print("\n\n Tamaño reglas: " + tam +
                         "\nNº Atributos por regla medio: " + ant +
                         "\nCobertura: " +
                         cob);
//        System.out.print("\n\t Confidence: " + conf + "  ComplMed: " + complmed +
//                         "  Compl: " + compl);
        System.out.print("\nComplejidad (support completo): " + compl);
        System.out.print("\nRelevancia: " + rel + "\nAtipicidad: " + ati);
        System.out.print("\nAcierto: " + porcAciertoTr);

        //Test computation
        calculaIndices(test, muestPorClaseTest, 1);
        System.out.print("\n\nIndices en Test:");
        System.out.print("\n\n Tamaño reglas: " + tam +
                         "\nNº Atributos por regla medio: " + ant +
                         "\nCobertura: " +
                         cob);
        /*System.out.print("\n\t Confidence: " + conf +
                         "  Complejidad Media (support): " + complmed +
                         "  Complejidad (support completo): " + compl);
         */
        System.out.print("\nComplejidad (support completo): " + compl);
        System.out.print("\nRelevancia: " + rel + "\nAtipicidad: " + ati);
        System.out.print("\nAcierto: " + porcAciertoTst);

    }

    /**
     * It prints on a string the statistics
     * @return a string with the statistics
     */
    public String printString() {
        String cad = "";

        //cad += "Avg. Confidence; " + conf + " ; \n ";
        //cad += "Avg. Suppport; " + complmed + " ; \n ";
        cad += "Avg. Rule length; " + tam + " ; \n";
        cad += "Avg. Number of attributes by rule; " + ant + " ; \n";
        cad += "Avg. Coverage; " + cob + " ; \n";
        cad += "Avg. Support Completo; " + compl + " ; \n";
        cad += "Avg. Significance; " + rel + " ; \n";
        cad += "Avg. Unusualness; " + ati + " ; \n";
        cad += "Acierto Train. ; " + porcAciertoTr + " ; \n"; ;
        cad += "Acierto Test. ; " + porcAciertoTst;

        return cad;
    }

    /**
     * It computes all statistics, accuracy, support, significance...
     * @param datos Data-set (training or test)
     * @param muestPorClase int[] Number of examples for each class
     * @param code Code to know if we are treating with training or test
     */
    private void calculaIndices(myDataset datos, int[] muestPorClase, int code) {
        int i, j;
        int aciertos;

        nDatos = datos.size();

        contClases = new int[nClases];
        for (i = 0; i < nClases; i++) {
            contClases[i] = muestPorClase[i];
        }

        tam = reglas.size();

        for (i = 0, ant = 0; i < reglas.size(); i++) {
            ant += reglas.getRule(i).size();
        }

        ant = (double) ant / tam;

        muestCubiertas = 0;
        int muestBienCubiertas = 0;
        int[][] instCubiertas = new int[tam][nClases];

        for (j = 0; j < nDatos; j++) {
            datos.getData(j).setCovered(0);
        }
        for (i = 0; i < reglas.size(); i++) {
            for (j = 0; j < nClases; j++) {
                instCubiertas[i][j] = 0;
            }
        }
        muestCubiertas = 0;
        for (i = 0; i < reglas.size(); i++) {
            for (j = 0; j < nDatos; j++) {
                Instance m = datos.getData(j);
                if (reglas.getRule(i).covered(m)) {
                    muestCubiertas++;
                    instCubiertas[i][m.getClas()]++;
                    if (reglas.getRule(i).getClas() == m.getClas()) {
                        if (m.getCovered() == 0) {
                            muestBienCubiertas++;
                            m.addCovered();
                        }
                    }
                }
            }
        }
        //cob = (double) muestCubiertas / (nDatos * tam * tam); //COV = 1/nR·SUM[Cov(Ri)] -- Cov(Ri) = n(Condi)/N //
        cob = muestCubiertas / (tam * nDatos);

        compl = (double) muestBienCubiertas / nDatos;

        double sigParcial = 0;
        double[] pCondi = new double[reglas.size()];
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

        double aux;
        for (i = 0, aux = 0; i < reglas.size(); i++) {
            double ncondi, pcond, pclase, pcondclase;
            int cl = reglas.getRule(i).getClas();
            for (j = 0, ncondi = 0; j < nClases; j++) {
                ncondi += reglas.getRule(i).getClassDistribution(j);
            }
            pcond = ncondi / nDatos;

            pclase = (double) contClases[cl] / nDatos;

            pcondclase = reglas.getRule(i).getClassDistribution(cl) / nDatos;

            aux += pcond * (pcondclase - pclase);
        }

        ati = aux / reglas.size();

        //Accuracy
        aciertos = 0;
        int clases[] = contClases; //new int[nClases];
        int clase, cl;
        int distribucion[], max;
        int clasePorDefecto = 0;
        for (i = 0, clase = -1; i < nClases; i++) {
            if (clases[i] > clase) {
                clasePorDefecto = i;
                clase = clases[i];
            }
        }
        for (i = 0; i < datos.size(); i++) {
            //It uses ordered rules (classical AQ)
            boolean continuar = true;
            cl = clasePorDefecto;
            for (j = 0; (j < reglas.size()) && (continuar); j++) {
                if (reglas.getRule(j).covered(datos.getData(i))) {
                    cl = reglas.getRule(j).getClas();
                    continuar = false;
                }
            }
            if (cl == datos.getData(i).getClas()) {
                aciertos++;
            }
        }

        System.out.print("\n\n Accuracy: " + (float) aciertos / datos.size() +
                         " ... total data: " + datos.size());
        if (code == 0) {
            porcAciertoTr = (double) aciertos / datos.size();
        } else {
            porcAciertoTst = (double) aciertos / datos.size();
        }
    }

    /**
     * It generates a string with the output list, that is, &lt;expected output&gt; &lt;output of the method&gt;
     * @param datos Data-set
     * @return A string with a pair list &lt;original class&gt; &lt;output class&gt;
     */
    public String salida(myDataset datos) {
        String cadena = new String("");
        int voto[] = new int[nClases];
        int clases[] = new int[nClases];
        int j, cl, max;
        for (int i = 0; i < datos.size(); i++) {
            boolean continuar = true;
            cl = clasePorDefecto;
            for (j = 0; (j < reglas.size()) && (continuar); j++) {
                if (reglas.getRule(j).covered(datos.getData(i))) {
                    cl = reglas.getRule(j).getClas();
                    continuar = false;
                }
            }

            /*
             for (j = 0; j < nClases; j++) { //Inicializo voto a 0
              voto[j] = 0;
                   }
             for (j = 0; j < reglas.size(); j++) { // vemos que reglas verifican a la Instance
              if (verificaRegla(reglas.getRule(j), datos.getDato(i))) {
                voto[reglas.getRule(j).getClase()]++;
              }
                   }

                   for (j = 0, max = -1, cl = 0; j < nClases; j++) { //Obtengo la clase que me da mis reglas
              if (voto[j] > max) {
                max = voto[j];
                cl = j;
              }
                   }*/
            cadena +=
                    new String(nombreClases[datos.getData(i).getClas()] + " " +
                               nombreClases[cl] + "\n");
        }
        return cadena;
    };

}

