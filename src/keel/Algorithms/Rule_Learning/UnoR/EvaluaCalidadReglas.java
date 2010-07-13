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

package keel.Algorithms.Rule_Learning.UnoR;

/**
 * <p>Title: Clase Evalua Calidad Reglas</p>
 *
 * <p>Description: Se encarga de obtener los datos estadï¿½ticos del algoritmo </p>
 *
 * <p>Copyright: Copyright Rosa (c) 2007</p>
 *
 * <p>Company: Mi Casa</p>
 *
 * @author Rosa Venzala
 * @version 1.0
 */
public class EvaluaCalidadReglas {
    private int nClases;
    private int nClasesTr;
    private int nClasesTst;
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
    private String[] valorNombreClasesTest;

    /**
     * Calculas las estadï¿½ticas finales para un conjunto de reglas dado y un conjunto de datos.
     * @param conjreg Conjunto de reglas (complejos) final
     * @param conjTrn Conjunto de datos de entrenamiento
     * @param conjTst Conjunto de datos de test
     * @param muestPorClaseTrain int[] Nmero de ejemplos de cada clase en el conj. de entrenamiento
     * @param muestPorClaseTest int[] Nmero de ejemplos de cada clase en el conj. de test
     * @param valorNombreClases String[] etiquetas para cada una de las clases
     */
    public EvaluaCalidadReglas(ConjReglas conjreg, ConjDatos conjTrn,
                               ConjDatos conjTst, int[] muestPorClaseTrain,
                               int[] muestPorClaseTest,
                               String[] valorNombreClases,String []valorNombreClasesTest) {

        reglas = conjreg; //referencia
        this.valorNombreClases = valorNombreClases;
	this.valorNombreClasesTest = valorNombreClasesTest;

        train = conjTrn.copiaConjDatos();
        test = conjTst.copiaConjDatos();

        nClases = conjreg.getUltimaRegla().getNClases();
	
	nClasesTr=nClases;
	nClasesTst=muestPorClaseTest.length;
	
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

        System.out.println("\nAccuracy: " + porcAciertoTst+"\n-----------------------------");

    }

    public double getAccuracyTrain(){
    	return porcAciertoTr;
    }
    public double getAccuracyTest(){
    	return porcAciertoTst;
    }
    /**
     * Imprime en una cadena las estadisticas (para test)
     * @return una cadena con las estadï¿½ticas
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
     * Calcula en si mismo todas las estadï¿½ticas, especialmente el porcentaje de aciertos
     * @param datos Conjunto de datos (entrenamiento o test)
     * @param muestPorClase int[] Nmero de ejemplos por cada clase en el conjunto de datos
     * @param code Codigo para saber si estamos tratando con entrenamiento o test
     */
    private void calculaIndices(ConjDatos datos, int[] muestPorClase, int code) {
        int i, j;
        int aciertos;

        nDatos = datos.size();
	
	if (code == 0) nClases=nClasesTr;
        else 	nClases=nClasesTst;
	
	int contClasesTra[]=new int[nClasesTr];
	
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

           // pclase = (double) contClases[cl] / nDatos;
	    pclase = (double) contClasesTra[cl] / nDatos;

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
	int clRegActivada=clasePorDefecto;
        for (i = 0; i < datos.size(); i++) { // Para el conjunto completo de datos
            for (j = 0; j < nClases; j++) { //Inicializo voto a 0
                voto[j] = 0;
                //verificados[j] = 1;
            }
            for (j = 0; j < reglas.size(); j++) { // vemos que reglas verifican a la muestra
                if (reglas.getRegla(j).cubre(datos.getDato(i))) {
			clRegActivada=reglas.getRegla(j).getClase();
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
	    cl=clRegActivada;
           if(code==0) {if (cl == datos.getDato(i).getClase()) {aciertos++;}}
	    else{
	    	if (valorNombreClases[cl] == valorNombreClasesTest[datos.getDato(i).getClase()]) {
                aciertos++;
            }
	    }
        }

        System.out.print("\n\n Accuracy: " + (double)aciertos/datos.size() +
                         " ... total data: " + datos.size());
        if (code == 0) {
            porcAciertoTr = (double) aciertos / datos.size();
        } else {
            porcAciertoTst = (double) aciertos / datos.size();
	    System.out.println("aciertos y total "+aciertos+" "+datos.size());
        }
    }

    /**
     * Genera un String con la lista de salidas, es decir, &lt;salida esperada&gt; &lt;salida del metodo&gt;
     * (en nuestro caso, clase en el fichero original, clase que obtiene el metodo)
     * @param datos Es el conjunto de datos con el que queremos comparar para nuestro conjunto de reglas
     * @return Una cadena con una lista de pares &lt;clase original&gt; &lt;clase calculada&gt;
     */
    public String salida(ConjDatos datos,boolean train) {
    
    	int clRegActivada;
    	if (train) nClases=nClasesTr;
        else 	nClases=nClasesTst;
	
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
	
	clRegActivada=clasePorDefecto;
	//if(!train)reglas.print(1);
	if(!train){
		for (j = 0; j < reglas.size(); j++) {
			//System.out.println("VAMOS POR "+j);
			evaluarComplejo(reglas.getRegla(j),datos);
		}
	}
	
        for (int i = 0; i < datos.size(); i++) { // Para el conjunto completo de datos
            for (j = 0; j < nClases; j++) { //Inicializo voto a 0
                voto[j] = 0;
            }
            for (j = 0; j < reglas.size(); j++) { // vemos que reglas verifican a la muestra
	    
                if (reglas.getRegla(j).cubre(datos.getDato(i))) {
			//como es con intervalos solo se activara una regla
			
		    clRegActivada=reglas.getRegla(j).getClase();
		   // if(train)System.out.println("DATO "+i+" regla clase "+clRegActivada);
                    distribucion = reglas.getRegla(j).getDistribucion();
		  // if(i<10){ System.out.println(clRegActivada+"  regla "+j+" cubre a "+i);}
                    for (int k = 0; k < nClases; k++) {
                        voto[k] += distribucion[k];
			//if(i<10)System.out.print(voto[k]+" , ");
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
	    
	    if(train){
	    cl=clRegActivada;
            cadena += new String(valorNombreClases[datos.getDato(i).getClase()] +
                                 " " +
                                 valorNombreClases[cl] + "\n");
				 // System.out.println(cl);
	    }
	    else{
	     cl=clRegActivada;
	    
            cadena += new String(valorNombreClasesTest[datos.getDato(i).getClase()] +
                                 " " +
                                 valorNombreClases[cl] + "\n");
	//cuidado con esto ultimo! es valorNombreClases de train no de test pq la indexacion de las reglas es evidentemente la referente a train
	    }
        }
        return cadena;
    }

    /** Evaluacion de los complejos sobre el conjunto de ejemplo para ver cuales se
     *   cubren de cada clase
     * @param c Complejo a evaluar
     * @param e Conjunto de datos
     */
    private void evaluarComplejo(Complejo c, ConjDatos e) {
        c.borraDistrib();

        for (int i = 0; i < e.size(); i++) {
            int cl = e.getDato(i).getClase();

            if (c.cubre(e.getDato(i))) {
	    	//System.out.println("dato "+i+" es cubierto por ");
                c.incrementaDistrib(cl);
            }
        }
    }

	/** Evaluacion de los complejos sobre el conjunto de ejemplo para ver cuales se
	*   cubren de cada clase
	* @param c Complejo a evaluar
	* @param e Conjunto de datos
	*/
	/*private void evaluarComplejo(Complejo c, ConjDatos e) {
		c.borraDistrib();
		for (int i = 0; i < e.size(); i++) {
		int cl = e.getDato(i).getClase();
	
		if (c.cubre(e.getDato(i))) {
			c.incrementaDistrib(cl);
		}
		}
		c.calculaLaplaciano();
	}*/

}

