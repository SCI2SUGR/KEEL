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
 * @author Written by Rosa Venzala (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Hyperrectangles.EACH;


public class RuleQualityEvaluation {
/**
 * <p>
 * To evaluate the rules
 * </p>
 */
	
    private int numClasses;
    private int numTrainClasses;
    private int numTestClasses;
    private int numData;
    private int contClasses[];

    private int size;
    private double before;
    private double cob;
    private double compl;
    private double rel;
    private double ati;
    private double trainAcc;
    private double testAcc;
    private double samplesCovered;

    private EachDataSet train;
    private EachDataSet test;
    private RuleSet rules;
    private String[] valueClassNames;
    private String[] valueClassNamesTrain;

    /**
     * <p>
     * Calculates the final statistical for a set of rules and a set of data
     * </p>
     * @param conjreg Set of rules(complex) final
     * @param conjTrn Set of data Train
     * @param conjTst Set of data Test
     * @param muestPorClaseTrain int[] Number of examples of each class in the Train set
     * @param muestPorClaseTest int[] Number of examples of each class in the Test set
     * @param valorNombreClases String[] Labels for each class
     */
    public RuleQualityEvaluation(RuleSet conjreg, EachDataSet conjTrn,
                               EachDataSet conjTst, int[] muestPorClaseTrain,
                               int[] muestPorClaseTest,
                               String[] valorNombreClases,String []valorNombreClasesTest) {

        rules = conjreg; //referencia
        this.valueClassNames = valorNombreClases;
	this.valueClassNamesTrain = valorNombreClasesTest;

        train = conjTrn.copyDataSet();
        test = conjTst.copyDataSet();

        numClasses = conjreg.getLastRule().getNClases();
	
	numTrainClasses=numClasses;
	numTestClasses=muestPorClaseTest.length;
	
        numData = conjTrn.size();

        // Calculos en Entrenamiento
        computeIndexes(train, muestPorClaseTrain, 0);
        System.out.print("\n\nTrain Statistics: ");
        System.out.print("\n\n Size of the rule set: " + size +
                         "\nAverage number of attributes per rule: " + before +
                         "\nCoverage: " +
                         cob);
//        System.out.print("\n\t Confidence: " + conf + "  ComplMed: " + complmed +
//                         "  Compl: " + compl);
        System.out.print("\nSupport: " + compl);
        System.out.print("\nRelevance: " + rel + "\nUnusualness: " + ati);
        System.out.print("\nAccuracy: " + trainAcc);

//		Calculos en test
        computeIndexes(test, muestPorClaseTest, 1);
        System.out.print("\n\nTest Statistics:");
        System.out.print("\n\n Size of the rule set: " + size +
                         "\nAverage number of attributes per rule: " + before +
                         "\nCoverage: " +
                         cob);
//        System.out.print("\n\t Confidence: " + conf + "  ComplMed: " + complmed +
//                         "  Compl: " + compl);
        System.out.print("\nSupport: " + compl);
        System.out.print("\nRelevance: " + rel + "\nUnusualness: " + ati);

        System.out.println("\nAccuracy: " + testAcc+"\n-----------------------------");

    }
    
	/**
	 * <p>
	 * Get Accuracyy Train
	 * </p>
	 * @return percent
	 */
    public double getAccuracyTrain(){
    	return trainAcc;
    }
    
    
	/**
	 * <p>
	 * Get Accuracyy Test
	 * </p>
	 * @return percent
	 */
    public double getAccuracyTest(){
    	return testAcc;
    }
    
    /**
     * <p>
     * Prints on a string the statistical results
     * </p>
     * @return a string with the statistical results
     */
    public String printString() {
    	String cad="";
       // String cad = "####Average results for test data####\n";

        //cad += "Avg. Confidence; " + conf + " ; \n ";
        //cad += "Avg. Suppport; " + complmed + " ; \n ";
	
      /*  cad += "Avg. Rule length: " + tam + "\n";
        cad += "Avg. Number of attributes by rule: " + ant + "\n";
        cad += "Avg. Coverage: " + cob + "\n";
        cad += "Avg. Support: " + compl + "\n";
        cad += "Avg. Significance: " + rel + "\n";
        cad += "Avg. Unusualness: " + ati + "\n\n";*/

        cad += "Accuracy Training: " + trainAcc + "\n"; ;
        cad += "Accuracy Test: " + testAcc;

        return cad;
    }

    /**
     * <p>
     * Calculates all the statistical, especially percent accuracy
     * </p>
     * @param datos Set of data (Train or Test)
     * @param muestPorClase int[] Number of examples for each class in the set of data
     * @param code Codigo Train or Test
     */
    private void computeIndexes(EachDataSet datos, int[] muestPorClase, int code) {
        int i, j;
        int aciertos;

        numData = datos.size();
	
	if (code == 0) numClasses=numTrainClasses;
        else 	numClasses=numTestClasses;
	
	int contClasesTra[]=new int[numTrainClasses];
	
        // contamos el numero de mustras por clase
        contClasses = new int[numClasses];
        for (i = 0; i < numClasses; i++) {
            contClasses[i] = muestPorClase[i];
        }

        size = rules.size(); // calculamos Tam

        // calculamos n atributos por regla medio
        for (i = 0, before = 0; i < rules.size(); i++) {
            before += rules.getRule(i).size();
        }

        before = (double) before / size; //N de atributos por regla medio

        // calculamos la distrib
        samplesCovered = 0; //n ejemplos cubiertos por las reglas
        int muestBienCubiertas = 0;
        int[][] instCubiertas = new int[size][numClasses];

        for (j = 0; j < numData; j++) {
            datos.getData(j).setCovered(0);
        }
        for (i = 0; i < rules.size(); i++) {
            for (j = 0; j < numClasses; j++) {
                instCubiertas[i][j] = 0;
            }
        }
        samplesCovered = 0;
        for (i = 0; i < rules.size(); i++) {
            for (j = 0; j < numData; j++) {
                Sample m = datos.getData(j);
                if (rules.getRule(i).isCovered(m)) {
                    samplesCovered++;
                    instCubiertas[i][m.getClassSelector()]++;
                    if (rules.getRule(i).getClassAttribute() == m.getClassSelector()) {
                        if (m.getCovered() == 0) {
                            muestBienCubiertas++;
                            m.incrementCovered();
                        }
                    }
                }
            }
        }
        //System.err.println("Muestras cubiertas -> "+muestCubiertas);
        //System.err.println("Total datos -> "+nDatos);
        //cob = (double) muestCubiertas / (nDatos * tam * tam); //COV = 1/nRSUM[Cov(Ri)] -- Cov(Ri) = n(Condi)/N //
        cob = samplesCovered / (size * numData);
        //Cobertura -> porcentaje de ejemplos cubiertos por cada regla / n de reglas

        // Calculamos completitud y completitud  media [support]
        compl = (double) muestBienCubiertas / numData;

        // Calculamos la relevancia (significance)
        double sigParcial = 0;
        double[] pCondi = new double[rules.size()]; //Factor normalizador -> coverage
        for (i = 0; i < rules.size(); i++) {
            pCondi[i] = 0;
            for (j = 0; j < numClasses; j++) {
                pCondi[i] += instCubiertas[i][j];
            }
            pCondi[i] *= (double) 1.0 / numData;
        }
        rel = 0;
        for (i = 0; i < rules.size(); i++) {
            sigParcial = 0;
            for (j = 0; j < numClasses; j++) {
                double logaritmo = (double) instCubiertas[i][j] /
                                   (contClasses[j] * pCondi[i]);
                if ((logaritmo != 0) && (!Double.isNaN(logaritmo)) &&
                    (!Double.isInfinite(logaritmo))) {
                    logaritmo = Math.log(logaritmo);
                    logaritmo *= (double) instCubiertas[i][j];
                    sigParcial += logaritmo;
                }
            }
            rel += sigParcial * 2;
        }
        rel /= (double) rules.size();

        // Calculamos la atipicidad de las reglas (unusualness) [ati]
        double aux;
        for (i = 0, aux = 0; i < rules.size(); i++) { // para cada regla
            double ncondi, pcond, pclase, pcondclase;
            int cl = rules.getRule(i).getClassAttribute();
            for (j = 0, ncondi = 0; j < numTrainClasses; j++) {
                ncondi += rules.getRule(i).getDistributionClass(j); //ncondi
            }
            pcond = ncondi / numData;

           // pclase = (double) contClases[cl] / nDatos;
	    pclase = (double) contClasesTra[cl] / numData;

            pcondclase = rules.getRule(i).getDistributionClass(cl) / numData;

            aux += pcond * (pcondclase - pclase);
        }

        ati = aux / rules.size();

        //Ahora el porcentaje de aciertos
        int voto[] = new int[numClasses];
        aciertos = 0;
        int clases[] = contClasses; //new int[nClases];
        //int verificados[] = new int[nClases];
        int clase, cl;
        int distribucion[], max;
        int clasePorDefecto = 0;
	
	double volumen;
	int ndimensiones;
        /*for (i = 0; i < datos.size(); i++) {
            clases[datos.getDato(i).getClase()]++;
                 }*/
        for (i = 0, clase = -1; i < numClasses; i++) {
            if (clases[i] > clase) {
                clasePorDefecto = i;
                clase = clases[i];
            }
        }
	int clRegActivada=clasePorDefecto;
        for (i = 0; i < datos.size(); i++) { // Para el conjunto completo de datos
            for (j = 0; j < numClasses; j++) { //Inicializo voto a 0
                voto[j] = 0;
                //verificados[j] = 1;
            }
	    volumen=1000000;
		ndimensiones=100;
            for (j = 0; j < rules.size(); j++) { // vemos que reglas verifican a la muestra
                if (rules.getRule(j).isCovered(datos.getData(i))) {
                	
                	if( (rules.getRule(j).getDimensions()< ndimensiones) ||((rules.getRule(j).getDimensions()==ndimensiones)&&(rules.getRule(j).getVolume()<volumen))){
                		//si tienen la misma dimension es ahora cuando comparamos las cantidades
						clRegActivada=rules.getRule(j).getClassAttribute();
			            distribucion = rules.getRule(j).getDistribution();
			             /*for (int k = 0; k < nClases; k++) {
			                        voto[k] += distribucion[k];
			                        //verificados[k]++;
			             }*/
					    //ACTUALIZAR EL VOLUMEN
					    volumen=rules.getRule(j).getVolume();
					    ndimensiones=rules.getRule(j).getDimensions();
                	}
                	
                }
            }
            /*for (int k = 0; k < nClases; k++) {
                voto[k] /= verificados[k];
                         }*/
            //System.out.println("");
            for (j = 0, max = 0, cl = 0; j < numClasses; j++) { //Obtengo la clase que me da mis reglas
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
           if(code==0) {if (cl == datos.getData(i).getClassSelector()) {aciertos++;}}
	    else{
	    	if (valueClassNames[cl] == valueClassNamesTrain[datos.getData(i).getClassSelector()]) {
                aciertos++;
            }
	    }
        }

        System.out.print("\n\n Accuracy: " + (double)aciertos/datos.size() +
                         " ... total data: " + datos.size());
        if (code == 0) {
            trainAcc = (double) aciertos / datos.size();
        } else {
            testAcc = (double) aciertos / datos.size();
	    System.out.println("aciertos y total "+aciertos+" "+datos.size());
        }
    }

    /**
     * <p>
     * Generates a string with out-put lists 
     * </p>
     * @param datos Set of data to compare with the set of rules
     * @return A string with pairs (original class; calculated class;)
     */
    public String out(EachDataSet datos,boolean train) {
    
    	int clRegActivada;
    	if (train) numClasses=numTrainClasses;
        else 	numClasses=numTestClasses;
	
        String cadena = new String("");
        int voto[] = new int[numClasses];
        int clases[] = new int[numClasses];
        int distribucion[], max;
        int j, cl, clasePorDefecto = 0;
	
	double volumen;
	int ndimensiones;
        for (int i = 0; i < datos.size(); i++) {
            clases[datos.getData(i).getClassSelector()]++;
        }
        for (int i = 0, clase = -1; i < numClasses; i++) {
            if (clases[i] > clase) {
                clasePorDefecto = i;
                clase = clases[i];
            }
        }
	
	clRegActivada=clasePorDefecto;
	if(!train){
		for (j = 0; j < rules.size(); j++) {
			complexEvaluation(rules.getRule(j),datos);
		}
	}
	
        for (int i = 0; i < datos.size(); i++) { // Para el conjunto completo de datos
            for (j = 0; j < numClasses; j++) { //Inicializo voto a 0
                voto[j] = 0;
            }
	    volumen=1000000;
	    ndimensiones=100000;
            for (j = 0; j < rules.size(); j++) { // vemos que reglas verifican a la muestra
	    
		if ((rules.getRule(j).isCovered(datos.getData(i)))) {
		
		if( (rules.getRule(j).getDimensions()< ndimensiones) ||((rules.getRule(j).getDimensions()==ndimensiones)&&(rules.getRule(j).getVolume()<volumen))){
			//o tiene menos dimensiones o estas son iguales pero es mas pequeÃ±o
		    clRegActivada=rules.getRule(j).getClassAttribute();
                    distribucion = rules.getRule(j).getDistribution();
                   /* for (int k = 0; k < nClases; k++) {
                        voto[k] += distribucion[k];
                    }*/
		    //ACTUALIZAR EL VOLUMEN y las dimensiones
		    volumen=rules.getRule(j).getVolume();
		    ndimensiones=rules.getRule(j).getDimensions();
                }
		}
            }
            for (j = 0, max = 0, cl = 0; j < numClasses; j++) { //Obtengo la clase que me da mis reglas
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
            cadena += new String(valueClassNames[datos.getData(i).getClassSelector()] +
                                 " " +
                                 valueClassNames[cl] + "\n");
	    }
	    else{
	     cl=clRegActivada;
	    
            cadena += new String(valueClassNamesTrain[datos.getData(i).getClassSelector()] +
                                 " " +
                                 valueClassNames[cl] + "\n");
		//es valorNombreClases de train no de test pq la indexacion de las reglas es la referente a train
	    }
        }
        return cadena;
    }

    /** 
     * <p>
     * Evaluation of the complex over the example set for see the matching class
     * </p>
     * @param c Complex to evaluate
     * @param e Set of data
     */
    private void complexEvaluation(Complex c, EachDataSet e) {
        c.removeDistribution();

        for (int i = 0; i < e.size(); i++) {
            int cl = e.getData(i).getClassSelector();

            if (c.isCovered(e.getData(i))) {
	    	//System.out.println("dato "+i+" es cubierto por ");
                c.incrementDistribution(cl);
            }
        }
    }


}

