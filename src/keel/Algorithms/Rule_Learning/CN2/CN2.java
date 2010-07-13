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

package keel.Algorithms.Rule_Learning.CN2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.core.Files;

/**
 * <p>Title: Main class of the algorithm</p>
 * <p>Description: It contains the esential methods for the CN2 algorithm</p>
 * <p>Created: November 26th 2004</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: KEEL</p>
 * @author Alberto Fernández (University of Granada) 26/11/2004
 * @since JDK1.5
 * @version 1.6
 */
public class CN2 {

    //names of the I/O files
    private String outputFile;
    private String outputFileTr;
    private String outputFileTst;


    private int instancesClassTrain[];
    private int instancesClassEval[];
    private int instancesClassTest[];

    private int nClasses; // Maximum number of classes

    private Complex selectors; // It stores all possible selectors
    private int starSize; //Maximum size of the Star (beam search)

    private ruleSet finalRuleSet; //Final RuleSet
    private evaluateRuleQuality evReg; // To evaluate the performance of the rules

    //Data-sets
    private myDataset trainData;
    private myDataset evalData;
    private myDataset testData;

    private int trainClasses[];
    private int testClasses[];
    private long tiempo;

    private String miOutput; //To store the data-set header

    private double covered;
    private int nAttributes;

    private double div;
    private double threshold = 0.0;

    private int accuracy = 0;
    private String[] attributesName;
    private String[] className;
    private boolean anyContinous = false;
    private boolean problem = false;

    /**
     * It checks if some of the preconditions are not satisfied: There are any continuous value or
     * there was a problem while reading the data files
     * @return boolean true if the algorithm can run normally, false in other case
     */
    public boolean everythingOK() {
        return ((!anyContinous) && (!problem));
    }

    /**
     * Default builder
     */
    public CN2() {
    }

    /**
     * CN2 class builder</br>
     * It does a local copy of the filenames for their posterior use.<br/>
     * Then, it obtains all data from file and stores it in a format recognizable for the program.<br/>
     * Finally, it creates all possible selectors for the dataset and stores them.
     * @param ftrain Name of the input training file
     * @param feval Name of the input validation file
     * @param ftest Name of the input test file
     * @param foutputTr Name of the output training file
     * @param foutputTst Name of the output test file
     * @param fsal Name of the output information file
     * @param starSize It is the maximum size for the star in the search process (beam search)
     * @param _covered The percentage of maximum examples to cover
     * @param _accuracy It refers wether the complete selectors will be employed (disjunctions).
     */
    public CN2(String ftrain, String feval, String ftest,
               String foutputTr,
               String foutputTst, String fsal, int starSize,
               double _covered, int _accuracy) {
        int i;

        //System.out.println("Executing CN2");

        outputFile = fsal;

        covered = _covered;
        accuracy = _accuracy;

        outputFileTr = foutputTr;
        outputFileTst = foutputTst;

        Dataset train = new Dataset(); //ficheroTrain);
        Dataset eval = new Dataset();
        Dataset test = new Dataset(); //ficheroTest);  }
        try {
            //System.out.println("\nLeyendo train: " + ftrain);
            train.readSet(ftrain, true);
            if (train.hayAtributosContinuos()) {
                System.err.println("CN2 may not work properly with real attributes.\nPlease discretize the data-set");
                //System.exit(-1);
                anyContinous = true;
            }
            //System.out.println("\nLeyendo eval: " + feval);
            eval.readSet(feval, false);
            if (eval.hayAtributosContinuos()) {
                System.err.println("CN2 may not work properly with real attributes.\nPlease discretize the data-set");
                //System.exit(-1);
                anyContinous = true;
            }
            //System.out.println("\nLeyendo test: " + ftest);
            test.readSet(ftest, false);
        } catch (IOException e) {
            System.err.println(
                    "There was a problem while trying to read the dataset files:");
            System.err.println("-> " + e);
            problem = true;
        }

        if (this.everythingOK()) {
            miOutput = new String("");
            miOutput = test.copiaCabeceraTest();

            System.out.println("\nGenerating datasets...");
            trainData = new myDataset();
            evalData = new myDataset();
            testData = new myDataset();

            train.calculaMasComunes();
            eval.calculaMasComunes();
            test.calculaMasComunes();

            trainData = creaConjunto(train);
            evalData = creaConjunto(eval);
            testData = creaConjunto(test);

            trainClasses = train.getC();
            nClasses = train.getnClasses();
            int aux = test.getnClasses();
            if (aux > nClasses) {
                nClasses = aux;
            }
            nAttributes = train.getnentradas();

            instancesClassTrain = new int[nClasses];
            for (int j = 0; j < nClasses; j++) {
                instancesClassTrain[j] = 0;
                for (i = 0; i < trainData.size(); i++) {
                    if (j == trainClasses[i]) {
                        instancesClassTrain[j]++;
                    }
                }
            }

            int[] clasesEval;
            clasesEval = eval.getC();
            instancesClassEval = new int[nClasses];
            for (int j = 0; j < nClasses; j++) {
                instancesClassEval[j] = 0;
                for (i = 0; i < evalData.size(); i++) {
                    if (j == clasesEval[i]) {
                        instancesClassEval[j]++;
                    }
                }
            }

            testClasses = test.getC();

            instancesClassTest = new int[nClasses];
            for (int j = 0; j < nClasses; j++) {
                instancesClassTest[j] = 0;
                for (i = 0; i < testData.size(); i++) {
                    //if (valorClasesTst[j] == testClasses[i]) {
                    if (j == testClasses[i]) {
                        instancesClassTest[j]++;
                    }
                }
            }

            this.starSize = starSize;
            tiempo = System.currentTimeMillis();
            div = (double) 1.0 / trainData.size();
            attributesName = train.dameNombres();
            className = train.dameClases();
            String[] classNameAux = test.dameClases();
            if (className.length < classNameAux.length) {
                //nClasses = classNameAux.length;
                className = new String[nClasses];
                for (i = 0; i < nClasses; i++) {
                    className[i] = classNameAux[i];
                }
            }
        }
    }

    /**
     * It creates a dataset (attributes/class) according to those obtained from a data-file
     * @param myData It must be a dataset read from file
     * @return The new dataset created, that is, a linked-list of objects "Instances"
     */
    private myDataset creaConjunto(Dataset myData) {
        myDataset datos = new myDataset();
        int tam = myData.getnentradas();
        double[] vars = new double[tam];
        double[][] X;
        int[] C;
        int clase = 0;
        X = myData.getX();
        C = myData.getC();
        for (int i = 0; i < myData.getndatos(); i++) {
            boolean salir = false;
            for (int j = 0; (j < tam) && (!salir); j++) {
                if (myData.isMissing(i, j)) {
                    salir = true;
                } else {
                    vars[j] = X[i][j];
                }
            }
            if (!salir) {
                clase = C[i]; //Integer.parseInt(mis_datos.getDatosIndex(i, tam));
                Instance m = new Instance(vars, clase, tam);
                m.setPosFile(i);
                datos.addData(m);
            }
        }
        return datos;
    }

    /**
     * We execute here the CN2 algorithm and we create the necessary output data
     */
    public void execute() {
        makeSelectors(); //I create all the possible selectors for the rules
        unorderedCN2(trainData); //, valorClasesT);
        tiempo = System.currentTimeMillis() - tiempo;

        /** I delete possible repeated rules **/
        for (int i = 0; (i < finalRuleSet.size() - 1); i++) {
            boolean compara = false;
            for (int j = i + 1; (j < finalRuleSet.size()) && (!compara); j++) {
                compara = finalRuleSet.getRule(i).same(finalRuleSet.
                        getRule(j));
            }
            if (compara) { //there is
                finalRuleSet.deleteRule(i); //I remove it
                i--;
            }
        }

        evReg = new evaluateRuleQuality(finalRuleSet, evalData, testData,
                                        instancesClassEval, instancesClassTest,
                                        className); //We evaluate the quality of the rules ...

        generateOutput(); //We write the output files
    }

    /**
     * It builds the total set of selectors to obtain all possible rules
     */
    private void makeSelectors() {
        int totalAtributos = trainData.getData(0).getNattributes();
        int examples = trainData.size();
        double[][] lista = new double[examples + 1][totalAtributos];
        for (int i = 0; i < totalAtributos; i++) {
            lista[0][i] = trainData.getData(0).getMuest()[i];
            lista[1][i] = Double.POSITIVE_INFINITY; //index
        }

        for (int i = 0; i < totalAtributos; i++) {
            for (int j = 1; j < examples; j++) {
                double valor = trainData.getData(j).getMuest()[i];
                int k = 0;
                while (!(Double.isInfinite(lista[k][i]))) {
                    if (lista[k][i] == valor) {
                        break;
                    }
                    k++;
                }
                if (Double.isInfinite(lista[k][i])) {
                    lista[k][i] = valor;
                    lista[k + 1][i] = Double.POSITIVE_INFINITY;
                }
            }
        }
        selectors = new Complex(nClasses);
        for (int i = 0; i < totalAtributos; i++) {
            for (int h = 0; h < examples; h++) {
                if (Double.isInfinite(lista[h][i])) {
                    break;
                }
                for (int j = 0; j < 4; j++) { //For the 3 possible values in the comparison <>,<=,>
                    Selector s = new Selector(i, j, lista[h][i]);
                    selectors.addSelector(s);
                }
            }
        }
        //Operator = (disjunts values)
        if (accuracy == 1) {
            for (int i = 0; i < totalAtributos; i++) {
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
                        listaAux.add(valores);
                        Selector s = new Selector(i, 0, valores);
                        selectors.addSelector(s);
                    }
                }
                for (int l = 3; l < total - 2; l++) {
                    double[] auxi = new double[l - 1];
                    double[] auxi2 = new double[l - 1];
                    list.addAll(listaAux);
                    listaAux.clear();
                    while (!list.isEmpty()) {
                        boolean salir = false;
                        auxi = (double[]) list.remove(0);
                        for (int j = 0; (j < list.size()) && (!salir); j++) {
                            auxi2 = (double[]) list.get(j);
                            for (int k = 0; (k < auxi.length - 1) && (!salir);
                                         k++) {
                                salir = !(auxi[k] == auxi2[k]);
                            }
                            if (!salir) {
                                double[] valores = new double[l];
                                for (int k = 0; k < l - 1; k++) {
                                    valores[k] = auxi[k];
                                }
                                valores[l - 1] = auxi2[l - 2];
                                listaAux.add(valores);
                                Selector s = new Selector(i, 0, valores);
                                selectors.addSelector(s);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * It generates unordered rules for each class of the training set
     * @param trainData myDataset Training set
     */
    private void unorderedCN2(myDataset trainData) {
        finalRuleSet = new ruleSet();
        finalRuleSet.addClassNames(className);
        finalRuleSet.addClassName(attributesName[nAttributes]);
        System.out.println("\n Extracting rules for each class:");
        for (int i = 0; i < nClasses; i++) { //for each class in training
            CN2forOneClass(trainData.copyDataSet(), i); //valorClasesT[i]);
            //finalRuleSet.addReglas(ruleSetAux);
        }
    }

    /**
     * It obtains the rules for a single class
     * @param train myDataset training data-set
     * @param clase int Class for which we want to generate the rules
     */
    private void CN2forOneClass(myDataset train, int clase) {
        boolean continuar = false;
        int quedan = instancesClassTrain[clase];
        System.out.println("\n Searching for the best complex for class " +
                           clase + " [" + quedan + " examples remaining]");
        continuar = quedan > 0;
        while (continuar) {
            continuar = false;
            Complex bestComplex = findBestComplex(train, clase);
            if (bestComplex != null) {
                bestComplex.addAttributeNames(attributesName);
                System.out.println("\n\nComplex found:");
                bestComplex.print();
                finalRuleSet.addRule(bestComplex);
                for (int i = 0; i < train.size(); i++) {
                    Instance m = train.getData(i);
                    if ((bestComplex.covered(m)) &&
                        (bestComplex.getClas() == m.getClas())) { //It covers the example and it is a true positive
                        train.deleteData(i);
                        i--;
                        quedan--;
                    }
                }
                continuar = true;
            }
            double porc = 1.0 - ((double) quedan / instancesClassTrain[clase]);
            if ((porc >= covered) || (bestComplex == null)) {
                continuar = false;
            }
            System.out.println("\nPercentage of covered examples -> " +
                               porc * 100 + "% <" + quedan + "> remaining");
        }
        //return reglas;
    }

    /**
     * It discovers the best complex for the given instances
     * @param train myDataset training set
     * @param clas int
     * @return Complex
     */
    private Complex findBestComplex(myDataset train, int clas) {
        Complex bestComplex = new Complex(nClasses);
        ruleSet star = new ruleSet();
        //star.adjuntaclassName(className);
        //star.adjuntaNombreClase(attributesName[nAttributes]);
        boolean continuar = true;
        //I create the initial star
        for (int i = 0; i < selectors.size(); i++) {
            Complex aux = new Complex(nClasses);
            aux.setClass(clas);
            aux.addSelector(selectors.getSelector(i));
            evaluateComplex(aux, train);
            star.addRule(aux);
        }
        //Order
        Collections.sort(star.getruleSet());
        //Check statistical significance of the best complex (optional)
        //....
        //Obtain the best complex
        star.deleteSubsumed(starSize);

        for (int j = star.size() - 1; star.size() > starSize;
                     star.deleteRule(j), j--) {
            ; //Beam search
        }
        for (int k = star.size() - 1; k >= 0; k--) {
            if (star.getRule(k).getClassDistribution(clas) == 0) {
                star.deleteRule(k);
            }
        }
        if (star.size() == 0) {
            continuar = false;
            bestComplex = null;
        } else {
            bestComplex = star.getRule(0);
        }

        //star.print();
        int tam = 1;
        while (continuar) {
            //for (int tam = 1; tam < nAttributes; tam++) {
            //a) Specialize every complex in STAR
            ruleSet newStar = new ruleSet();
            for (int i = 0; i < selectors.size(); i++) {
                Selector s = selectors.getSelector(i);
                for (int j = 0; j < star.size(); j++) {
                    Complex aux2 = star.getRule(j);
                    Complex aux = new Complex(nClasses);
                    boolean sigue = true;
                    for (int h = 0; (h < aux2.size()) && (sigue); h++) {
                        Selector s2 = aux2.getSelector(h);
                        aux.addSelector(s2);
                        if (s2.compareTo(s) < 2) { //same attribute
                            sigue = false; //I do not add it
                        }
                    }
                    if (sigue) { //Selector is not repeated in the complex of "star"
                        aux.addSelector(s);
                        aux.setClass(clas);
                        evaluateComplex(aux, train);
                        newStar.addRule(aux);
                    }
                }
            }
            Collections.sort(newStar.getruleSet());
            //esSignificativa(bestComplex);
            removeInvalid(newStar); //we remove repeated and subsumed complexes

            for (int k = newStar.size() - 1; k >= 0; k--) {
                if (newStar.getRule(k).getClassDistribution(clas) == 0) {
                    newStar.deleteRule(k);
                }
            }
            if (newStar.size() > 0) {
                if (bestComplex.compareTo(newStar.getRule(0)) == 1) { //es peor
                    bestComplex = newStar.getRule(0);
                }
                //for (; newStar.size() > starSize; newStar.deleteRule(0)) {
                for (int j = newStar.size() - 1;
                             newStar.size() > starSize;
                             newStar.deleteRule(j), j--) {
                    ; //Beam search
                }
                star.deleteAll();
                star.addRules(newStar);
                tam++;
                continuar = (tam < nAttributes);
            } else {
                continuar = false;
            }
            System.out.print("New star created of size " + tam + ", ");
            if (((tam + 1) % 6) == 0) {
                System.out.println("");
            }
        } //while(continuar);
        /*if (bestComplex.getClassDistribution(clase) == 0) {
            bestComplex = null;
                 }*/
        return bestComplex;
    }

    /**
     * It removes those complexes which are subsumed by others in newStar
     * @param newStar ruleSet The new set of complexes we are building
     */
    private void removeInvalid(ruleSet newStar) {
        //Primero quitamos los nulos: se repiten atributos!
        //newStar.eliminaNulos(); //Esta hecho conforme se construye [tienen distinto atributo]

        //Eliminamos los Complexs que esten repetidos dentro de newStar!
        //newStar.eliminaRepetidos(starSize); //Elimino hasta quedarme con "tamEstrella"

        newStar.deleteSubsumed(starSize); //We delete rules that are semantically the same (At = 1, At <> 0, At = [0,1])

    }

    /**
     * Test of Statistical Signficance . Complex c is significant if its value is higher than a given threshold
     * <br/>The computation is carried out as 2*SUM[fi·log(fi/ei)] where:
     * <br/>fi is the distribution of examples covered by c
     * <br/>ei is the distribution of examples randomly covered -> #examples of class i / #examples
     * @param c Complex The complex to analyse
     * @return boolean True if it is significant (higher than the threshold) false in other case
     */
    private boolean esSignificativa(Complex c) {
        double significancia = 0;
        double pCond = 0;
        for (int j = 0; j < nClasses; j++) {
            pCond += c.getClassDistribution(j);
        }
        pCond *= 1.0 / trainData.size();
        for (int j = 0; j < nClasses; j++) {
            double logaritmo = (double) c.getClassDistribution(j) /
                               (this.instancesClassTrain[j] * pCond);
            if (logaritmo != 0) {
                logaritmo = Math.log(logaritmo);
                logaritmo *= (double) c.getClassDistribution(j);
                significancia += logaritmo;
            }
        }
        significancia *= 2.0;
        //System.out.println("threshold -> " + significancia);

        return (significancia >= threshold);

    }

    /**
     * Evaluation of the complexes over the examples set, in order to see which ones are covered in each class
     * @param c Complex to evaluate
     * @param e Data-set
     */
    private void evaluateComplex(Complex c, myDataset e) {
        c.deleteDistribution();
        for (int i = 0; i < e.size(); i++) {
            int cl = e.getData(i).getClas();

            if (c.covered(e.getData(i))) {
                c.addClassDistribution(cl);
            }
        }
        c.computeLaplacian();
    }

    /**
     * It computes the statistical data and creates the output files
     */
    private void generateOutput() {
        Files f = new Files();
        String cad = "";

        //System.out.println("\n Estas son las reglas encontradas:");
        //finalRuleSet.print();

        cad = finalRuleSet.printString();
        cad += "\n\n" + evReg.printString() + "\n\n  Time (seconds); " +
                (tiempo / 1000);
        f.writeFile(outputFile, cad);

        f.writeFile(outputFileTr,
                         miOutput + evReg.salida(trainData));
        f.writeFile(outputFileTst,
                         miOutput + evReg.salida(testData));

    }
}

