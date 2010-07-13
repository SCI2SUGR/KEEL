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

/**
 * <p>Title: Clase Evalua Calidad Reglas</p>
 *
 * <p>Description: Se encarga de obtener los datos estadísticos del algoritmo </p>
 *
 * <p>Copyright: Copyright Alberto (c) 2006</p>
 *
 * <p>Company: Mi Casa</p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */
public class EvaluaCalidadReglas {
    private int nClases;
    private int nDatos;
    private int contClases[];
    private int multiplicativo;
    private double nu;

    private int tam;
    private double ant;
    private double cob;
    private double conf;
    private double compl;
    private double complmed;
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
     * Calculas las estadísticas finales para un conjunto de reglas dado y un conjunto de datos.
     * @param conjreg Conjunto de reglas (complejos) final
     * @param conjTrn Conjunto de datos de entrenamiento
     * @param conjTst Conjunto de datos de test
     * @param muestPorClaseTrain int[] Número de ejemplos de cada clase en el conj. de entrenamiento
     * @param muestPorClaseTest int[] Número de ejemplos de cada clase en el conj. de test
     * @param mult int Valor para saber si aplicar el peso multiplicativo(1) o aditivo(0)
     * @param _nu double Valor de "nu" para el peso multiplicativo (si procede)
     * @param valorNombreClases String[] nombre que tiene cada clase del problema
     */
    public EvaluaCalidadReglas(ConjReglas conjreg, ConjDatos conjTrn,
                               ConjDatos conjTst, int[] muestPorClaseTrain,
                               int[] muestPorClaseTest, int mult, double _nu,String[] valorNombreClases) {

        reglas = conjreg; //referencia
        multiplicativo = mult;
        nu = _nu;

        train = conjTrn.copiaConjDatos();
        test = conjTst.copiaConjDatos();

        nClases = conjreg.getUltimaRegla().getNClases();
        nDatos = conjTrn.size();

        this.valorNombreClases = valorNombreClases;

        // Calculos en Entrenamiento
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

//		Calculos en test
        //Paso a recalcular el valor Wracc para las reglas pero en TEST
        for (int j = 0; j < test.size(); j++) {
            test.getDato(j).setCubierta(0);
        }
        for (int i = 0; i < reglas.size(); i++) {
            Complejo c = reglas.getRegla(i);
            evaluarComplejo(c,test);
            for (int j = 0; j < test.size(); j++) {
                Muestra m = test.getDato(j);
                if ((c.cubre(m)) &&
                    (c.getClase() == m.getClase())) {
                    //Cubre al ejemplo y es un verdadero positivo
                    m.incrementaCubierta();
                }
            }
        }

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
     * Imprime en una cadena las estadisticas (para test)
     * @return una cadena con las estadísticas
     */
    public String printString() {
        String cad = "####Average results for test data####\n";

        //cad += "Avg. Confidence; " + conf + " ; \n ";
        //cad += "Avg. Suppport; " + complmed + " ; \n ";
        cad += "Avg. Rule length: " + tam + "\n";
        cad += "Avg. Number of attributes by rule: " + ant + "\n";
        cad += "Avg. Coverage: " + cob + "\n";
        cad += "Avg. Support Completo: " + compl + "\n";
        cad += "Avg. Significance: " + rel + "\n";
        cad += "Avg. Unusualness: " + ati + "\n\n";

        cad += "Accuracy Training: " + porcAciertoTr + "\n"; ;
        cad += "Accuracy Test: " + porcAciertoTst;

        return cad;
    }

    /**
     * Calcula en si mismo todas las estadísticas, especialmente el porcentaje de aciertos
     * @param datos Conjunto de datos (entrenamiento o test)
     * @param muestPorClase int[] Número de ejemplos por cada clase en el conjunto de datos
     * @param code Codigo para saber si estamos tratando con entrenamiento o test
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

        // calculamos nº atributos por regla medio
        for (i = 0, ant = 0; i < reglas.size(); i++) {
            ant += reglas.getRegla(i).size();
        }

        ant = (double) ant / tam; //Nº de atributos por regla medio

        // calculamos la distrib
        muestCubiertas = 0; //nº ejemplos cubiertos por las reglas
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
        //cob = (double) muestCubiertas / (nDatos * tam * tam); //COV = 1/nR·SUM[Cov(Ri)] -- Cov(Ri) = n(Condi)/N //
        cob = (double) muestCubiertas / (tam*nDatos);
        //Cobertura -> porcentaje de ejemplos cubiertos por cada regla / nº de reglas

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
                if ((logaritmo != 0)&&(!Double.isNaN(logaritmo))&&(!Double.isInfinite(logaritmo))){
                    logaritmo = Math.log(logaritmo);
                    logaritmo *= (double) instCubiertas[i][j];
                    sigParcial += logaritmo;
                }
            }
            rel += sigParcial * 2;
        }
        rel /= (double) reglas.size();

        // Calculamos la atipicidad de las reglas (unusualness) [ati]
        ati = 0;
        for (i = 0; i < reglas.size(); i++) {
            ati += reglas.getRegla(i).getHeuristica();
        }
        ati /= (double) reglas.size();

        //Ahora el porcentaje de aciertos
        double voto[] = new double[nClases];
        aciertos = 0;
        int clases[] = contClases; //new int[nClases];
        //int verificados[] = new int[nClases];
        int clase, cl;
        double distribucion[], max;
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
            //      System.err.println("Votos -> "+voto[0]+", "+voto[1]+", "+voto[2]);
            //Cambios -> max = -1 ==> max = 0
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

        System.out.print("\n\n Porcentaje Aciertos: " + aciertos +
                         " ... total datos: " + datos.size());
        if (code == 0) {
            porcAciertoTr = (double) aciertos / datos.size();
        } else {
            porcAciertoTst = (double) aciertos / datos.size();
        }
    }

    /**
     * Genera un String con la lista de salidas, es decir, &lt;salida esperada&gt; &lt;salida del metodo&gt;
     * (en nuestro caso, clase en el fichero original, clase que obtiene el metodo)
     * @param datos Es el conjunto de datos con el que queremos comparar para nuestro conjunto de reglas
     * @return Una cadena con una lista de pares &lt;clase original&gt; &lt;clase calculada&gt;
     */
    public String salida(ConjDatos datos) {
        String cadena = new String("");
        double voto[] = new double[nClases];
        int clases[] = new int[nClases];
        double distribucion[], max;
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
        double n, ncond, nclascond, nclas;
        int cl;
        double val, peso = 0;

        n = 0;
        ncond = 0;
        nclascond = 0;
        nclas = 0;

        for (int i = 0; i < e.size(); i++) {
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
    }


}

