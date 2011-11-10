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
 * @author Writed by Alberto Fernández (University of Granada) 15/01/2006
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 24/06/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDAlgorithm;

import java.text.DecimalFormat;
import org.core.Files;

public class EvaluateRules {

    /**
     * <p>
     * Evaluate the rules obtained by the algorithm
     * </p>
     */

    private int nClases;
    private int nDatos;
    private int contClases[];

    private int tam;
    private double ant;
    private double cob;
    private double conf;
    private double compl;
    private double rel;
    private double ati;
    private double sens;
    private double porcAciertoTr;
    private double porcAciertoTst;
    private double muestCubiertas;
    private int muestrasCubiertasTotales[];

    private String measure_file;

    private SetData train;
    private SetData test;
    private SetRules reglas;
    private String[] valorNombreClases;

    /**
     * <p>
     * Calculate the quality measures of the rules obtained by the algorithm
     * </p>
     * @param setRul            Set of final rules
     * @param setTra            Set of train data
     * @param setTst            Set of test data
     * @param examClassTra      Number of examples for each class in the train data
     * @param examClassTst      Number of examples for each class in the test data
     * @param valueNameClass    Name for each class
     * @param mea_file      Name of the measure file
     */
    public EvaluateRules(SetRules setRul, SetData setTra,
                               SetData setTst, int[] examClassTra,
                               int[] examClassTst, String[] valueNameClass, String mea_file) {

        reglas = setRul;

        train = setTra.copiaConjDatos();
        test = setTst.copiaConjDatos();

        nClases = setRul.getUltimaRegla().getNClass();
        nDatos = setTra.size();

        measure_file = mea_file;

        this.valorNombreClases = valueNameClass;

        // Calculate the train
        calculaIndicesTra(train, examClassTra);
        System.out.print("\n\nTrain index: ");
        DecimalFormat d = new DecimalFormat("0.0000");
        System.out.print("\n\n#Rules: " + d.format(tam) +
                         "\n#Vars: " + d.format(ant) +
                         "\nCoverage: " + d.format(cob) +
                         "\nSignificance: " + d.format(rel) +
                         "\nUnusualness: " + d.format(ati) +
                         "\nAccuracy: " + d.format(porcAciertoTr) +
                         "\nSupport: " + d.format(compl) +
                         "\nConfidence: " + d.format(conf));


        // Calculate test
        for (int j = 0; j < test.size(); j++) {
            test.getDato(j).setCovered(0);
        }

        calculaIndicesTst(test, examClassTst);
        System.out.print("\n\n#Rules: " + d.format(tam) +
                         "\n#Vars: " + d.format(ant) +
                         "\nCoverage: " + d.format(cob) +
                         "\nSignificance: " + d.format(rel) +
                         "\nUnusualness: " + d.format(ati) +
                         "\nAccuracy: " + d.format(porcAciertoTr) +
                         "\nSupport: " + d.format(compl) +
                         "\nConfidence: " + d.format(conf));

    }

    /**
     * <p>
     * Print the results in a String
     * </p>
     * @return          Results in a string
     */
    public String printString() {
        String cad = "####Average results for test data####\n";

        DecimalFormat sixDecimals = new DecimalFormat("0.0000");

        cad += "Avg. Rule length: " + sixDecimals.format(tam) + "\n";
        cad += "Avg. Number of attributes by rule: " + sixDecimals.format(ant) + "\n";
        cad += "Avg. Coverage: " + sixDecimals.format(cob) + "\n";
        cad += "Avg. Significance: " + sixDecimals.format(rel) + "\n";
        cad += "Avg. Unusualness: " + sixDecimals.format(ati) + "\n";
        cad += "Avg. Support: " + sixDecimals.format(compl) + "\n";
        cad += "Avg. Confidence: " + sixDecimals.format(conf) + "\n";

        cad += "Accuracy Training: " + sixDecimals.format(porcAciertoTr) + "\n";
        cad += "Accuracy Test: " + sixDecimals.format(porcAciertoTst);

        return cad;
    }

    /**
     * <p>
     * Print the quality measures in the measure file
     * </p>
     * @param cad        String with the values to introduce
     */
    public void printMeasure(String cad) {

    }

    /**
     * <p>
     * Calculate the quality of the set of rules for the training file
     * </p>
     * @param SetData           Set of data to study
     * @param muestPorClase     Number of examples for each class
     */
    private void calculaIndicesTra(SetData datos, int[] muestPorClase) {
        int i, j;
        int aciertos;

        nDatos = datos.size();

        contClases = new int[nClases];
        for (i = 0; i < nClases; i++) {
            contClases[i] = muestPorClase[i];
        }

        tam = reglas.size(); // Calculate Tam

        // Number of attributes
        for (i = 0, ant = 0; i < reglas.size(); i++) {
            ant += reglas.getRule(i).size();
        }

        //Add the variables of the consequent
        ant += reglas.size();

        ant = (double) ant / tam; //Nº attributes per rule

        // Calculate the distrib
        muestCubiertas = 0; //Number of covered examples
        int muestBienCubiertas = 0;
        int[][] instCubiertas = new int[tam][nClases];

        for (j = 0; j < nDatos; j++) {
            datos.getDato(j).setCovered(0);
        }
        for (i = 0; i < reglas.size(); i++) {
            for (j = 0; j < nClases; j++) {
                instCubiertas[i][j] = 0;
            }
        }
        muestCubiertas = 0;
        for (i = 0; i < reglas.size(); i++) {
            for (j = 0; j < nDatos; j++) {
                Instance m = datos.getDato(j);
                if (reglas.getRule(i).cover(m)) {
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

        //Calculate coverage
        cob = (double) muestCubiertas / (tam*nDatos);

        //Calculate support
        compl = (double) muestBienCubiertas / nDatos;

        //Calculate confidence
        conf = (double) muestBienCubiertas / muestCubiertas;

        //Calculate unusualness
        ati = 0;
        double val;
        for(i = 0; i < reglas.size(); i++){
            val = evaluateUnus(reglas.getRule(i),datos);
            ati += val;
        }
        ati /= (double) reglas.size();

        //Calculate significance
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
                if ((logaritmo != 0)&&(!Double.isNaN(logaritmo))&&(!Double.isInfinite(logaritmo))){
                    logaritmo = Math.log(logaritmo);
                    logaritmo *= (double) instCubiertas[i][j];
                    sigParcial += logaritmo;
                }
            }
            rel += sigParcial * 2;
        }
        rel /= (double) reglas.size();


        //Correct classified examples
        double voto[] = new double[nClases];
        aciertos = 0;
        int clases[] = contClases;

        int clase, cl;
        double distribucion[], max;
        int clasePorDefecto = 0;
        for (i = 0, clase = -1; i < nClases; i++) {
            if (clases[i] > clase) {
                clasePorDefecto = i;
                clase = clases[i];
            }
        }
        for (i = 0; i < datos.size(); i++) {
            for (j = 0; j < nClases; j++) {
                voto[j] = 0;
            }
            for (j = 0; j < reglas.size(); j++) {
                if (reglas.getRule(j).cover(datos.getDato(i))) {
                    distribucion = reglas.getRule(j).getDistrib();
                    for (int k = 0; k < nClases; k++) {
                        voto[k] += distribucion[k];
                    }
                }
            }
            //System.out.println("");
            for (j = 0, max = 0, cl = 0; j < nClases; j++) {
                if (voto[j] > max) {
                    max = voto[j];
                    cl = j;
                }
            }
            if (max == 0) {
                cl = clasePorDefecto;
            }
            if (cl == datos.getDato(i).getClas()) {
                aciertos++;
            }
        }

        porcAciertoTr = (double) aciertos / datos.size();
    }

    /**
     * <p>
     * Calculate the quality measures
     * </p>
     * @param SetData           Set of data to study
     * @param muestPorClase     Number of examples for each class
     */
    private void calculaIndicesTst(SetData datos, int[] muestPorClase) {

        float medVar = 0;
        float medCob = 0;
        float medSig = 0;
        float medUnu = 0;
        float medSen = 0;
        float medCon = 0;
        float medSup = 0;

        int j;
        int aciertos;

        nDatos = datos.size();
        tam = reglas.size(); // Calculate Tam
        contClases = new int[nClases];
        for(int i = 0; i < nClases; i++) {
            contClases[i] = muestPorClase[i];
        }
        muestrasCubiertasTotales = new int[nDatos];
        for(int i=0; i < nDatos; i++)
            muestrasCubiertasTotales[i] = 0;

        DecimalFormat sixDecimals = new DecimalFormat("0.0000");
        String cad = "#Rule \t #Vars \t Cov \t Sign \t Unus \t Acc \t Sens \t Supp \t Cnf\n";
        
        Files.writeFile(measure_file, cad);

        //For each rule we calculate the quality measures
        for(int i=0; i<reglas.size(); i++){
            calculaIndicesRule(i, datos);
            //Add the values to the average results
            medVar += ant+1;
            medCob += cob;
            medSig += rel;
            medUnu += ati;
            medSen += sens;
            medCon += conf;
            //Print the rule
            cad = i +"\t"+ sixDecimals.format(ant+1) +"\t"+ sixDecimals.format(cob) +"\t"+ sixDecimals.format(rel) +"\t"+ sixDecimals.format(ati) +"\t ---- \t"+ sixDecimals.format(sens) +"\t" +sixDecimals.format(compl) +"\t"+ sixDecimals.format(conf);
            Files.addToFile(measure_file, cad+"\n");
        }

        //Correct classified examples
        double voto[] = new double[nClases];
        aciertos = 0;
        int clases[] = contClases;

        int cl;
        double distribucion[], max;
        int clasePorDefecto = 0;
        for (int i=0, clase = -1; i < nClases; i++) {
            if (clases[i] > clase) {
                clasePorDefecto = i;
                clase = clases[i];
            }
        }
        for (int i = 0; i < datos.size(); i++) {
            for (j = 0; j < nClases; j++) {
                voto[j] = 0;
            }
            for (j = 0; j < reglas.size(); j++) {
                if (reglas.getRule(j).cover(datos.getDato(i))) {
                    distribucion = reglas.getRule(j).getDistrib();
                    for (int k = 0; k < nClases; k++) {
                        voto[k] += distribucion[k];
                    }
                }
            }
            //System.out.println("");
            for (j = 0, max = 0, cl = 0; j < nClases; j++) {
                if (voto[j] > max) {
                    max = voto[j];
                    cl = j;
                }
            }
            if (max == 0) {
                cl = clasePorDefecto;
            }
            if (cl == datos.getDato(i).getClas()) {
                aciertos++;
            }
        }

        porcAciertoTst = (double) aciertos / datos.size();

        medVar /= tam;
        medCob /= tam;
        medSig /= tam;
        medUnu /= tam;
        medSen /= tam;
        medCon /= tam;
        for(int i=0; i<nDatos; i++){
            if(muestrasCubiertasTotales[i]==1) medSup++;
        }
        medSup /= nDatos;

        //Print the average results
        cad = "---\t"+ sixDecimals.format(medVar) +"\t"+ sixDecimals.format(medCob) +"\t"+ sixDecimals.format(medSig) +"\t"+ sixDecimals.format(medUnu) +"\t"+ sixDecimals.format(porcAciertoTst) +"\t"+ sixDecimals.format(medSen) +"\t"+ sixDecimals.format(medSup) +"\t"+ sixDecimals.format(medCon);
        Files.addToFile(measure_file, cad);


    }

    private void calculaIndicesRule(int pos, SetData datos){

        ant = reglas.getRule(pos).size();

        // Calculate the distrib
        muestCubiertas = 0; //Number of covered examples
        int muestBienCubiertas = 0;
        int[][] instCubiertas = new int[tam][nClases];

        for (int j = 0; j < nDatos; j++) {
            datos.getDato(j).setCovered(0);
        }
        for (int i = 0; i < reglas.size(); i++) {
            for (int j = 0; j < nClases; j++) {
                instCubiertas[i][j] = 0;
            }
        }
        muestCubiertas = 0;
        for (int j = 0; j < nDatos; j++) {
            Instance m = datos.getDato(j);
            if (reglas.getRule(pos).cover(m)) {
                muestCubiertas++;
                muestrasCubiertasTotales[j]=1;
                instCubiertas[pos][m.getClas()]++;
                if (reglas.getRule(pos).getClas() == m.getClas()) {
                    if (m.getCovered() == 0) {
                        muestBienCubiertas++;
                        m.addCovered();
                    }
                }
            }
        }

        //Calculate coverage
        cob = (double) muestCubiertas / nDatos;

        //Calculate support
        compl = (double) muestBienCubiertas / nDatos;

        //Calculate confidence
        if(muestCubiertas!=0)
            conf = (double) muestBienCubiertas / muestCubiertas;
        else conf = 0;

        //Calculate sensitivity
        sens = (double) muestBienCubiertas / datos.getExamplesClass(reglas.getRule(pos).getClas());

        //Calculate unusualness
        ati = 0;
        double val;
        val = evaluateUnus(reglas.getRule(pos),datos);
        ati += val;

        //Calculate significance
        double sigParcial = 0;
        double[] pCondi = new double[reglas.size()]; //Factor normalizador -> coverage
        pCondi[pos] = 0;
        for (int j = 0; j < nClases; j++) {
            pCondi[pos] += instCubiertas[pos][j];
        }
        pCondi[pos] *= (double) 1.0 / nDatos;
        rel = 0;
        sigParcial = 0;
        for (int j = 0; j < nClases; j++) {
            double logaritmo = (double) instCubiertas[pos][j] /
                               (contClases[j] * pCondi[pos]);
            if ((logaritmo != 0)&&(!Double.isNaN(logaritmo))&&(!Double.isInfinite(logaritmo))){
                logaritmo = Math.log10(logaritmo);
                logaritmo *= (double) instCubiertas[pos][j];
                sigParcial += logaritmo;
            }
        }
        rel += sigParcial * 2;

    }

    /**
     * <p>
     * Generate a string with the classification of the total examples for a data set
     * </p>
     * </p>
     * @param Data      The data set to study
     * @return          String with the result
     */
    public String exitResult(SetData Data) {

        String cadena = new String("");
        double voto[] = new double[nClases];
        int clases[] = new int[nClases];
        double distribucion[], max;
        int j, cl, clasePorDefecto = 0;
        for (int i = 0; i < Data.size(); i++) {
            clases[Data.getDato(i).getClas()]++;
        }
        for (int i = 0, clase = -1; i < nClases; i++) {
            if (clases[i] > clase) {
                clasePorDefecto = i;
                clase = clases[i];
            }
        }
        for (int i = 0; i < Data.size(); i++) {
            for (j = 0; j < nClases; j++) {
                voto[j] = 0;
            }
            for (j = 0; j < reglas.size(); j++) {
                if (reglas.getRule(j).cover(Data.getDato(i))) {
                    distribucion = reglas.getRule(j).getDistrib();
                    for (int k = 0; k < nClases; k++) {
                        voto[k] += distribucion[k];
                    }
                }
            }
            for (j = 0, max = 0, cl = 0; j < nClases; j++) { 
                if (voto[j] > max) {
                    max = voto[j];
                    cl = j;
                }
            }
            if (max == 0) { 
                cl = clasePorDefecto;
            }
            cadena += new String(valorNombreClases[Data.getDato(i).getClas()] +
                                 " " +
                                 valorNombreClases[cl] + "\n");
        }
        return cadena;
    }

    /**
     * <p>
     * Evaluation of the unusualness measures
     * </p>
     * @param c             Complex to evaluate
     * @param e             Data set
     */
    private double evaluateUnus(Complex c, SetData e) {
        double n, ncond, nclascond, nclas;
        int cl;
        double val = 0;

        n = 0;
        ncond = 0;
        nclascond = 0;
        nclas = 0;

        for (int i = 0; i < e.size(); i++) {
            cl = e.getDato(i).getClas();
            n++;

            if (c.cover(e.getDato(i))) {
                c.incrementDistrib(cl);
                ncond++;
                if (cl == c.getClas()) {
                    nclascond++;
                }
            }
            if (cl == c.getClas()) {
                nclas++;
            }
        }
        if (n != 0 && ncond != 0) {
            val = (ncond / n) * ((nclascond / ncond) - (nclas / n));
        } else {
            val = Double.MIN_VALUE;
        }
        return (val);
    }


}
