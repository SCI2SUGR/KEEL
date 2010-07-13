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

import java.util.*;
import org.core.*;
import java.io.IOException;

/**
 * <p>Title: Main class of the algorithm</p>
 * <p>Description: It contains the esential methods for the AQ algorithm</p>
 * <p>Created: November 26th 2004</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: KEEL</p>
 * @author Alberto Fernández (University of Granada) 11/26/2004
 * @since JDK1.5
 * @version 1.6
 */
public class AQ {

    //names of the I/O files
    private String outputFile;
    private String outputFileTr;
    private String outputFileTst;

    private int nClasses, nClassesTr; // Maximum number of classes

    private Complex selectors; // It stores all possible selectors
    private int starSize; //Maximum size of the Star (beam search)

    private ruleSet finalRuleSet; //Final RuleSet
    private evaluateRuleQuality evReg; // To evaluate the performance of the rules

    //Data-sets
    private myDataset trainData;
    private myDataset testData;
    private myDataset evalData;

    private int trainClasses[], testClasses[];
    private long seed;

    private String miSalida; //To store the data-set header

    private int instancesClassTrain[];
    private int instancesClassEval[];
    private int instancesClassTest[];
    private int classValues[];

    private int accuracy;
    //private double w;

    private String[] attributeNames;
    private String[] classNames;

    private boolean continousValues = false;
    private boolean problem = false;

    /**
     * It checks if some of the preconditions are not satisfied: There are any continuous value or
     * there was a problem while reading the data files
     * @return boolean true if the algorithm can run normally, false in other case
     */
    public boolean everythingOK() {
        return ((!continousValues) && (!problem));
    }

    /**
     * Default builder
     */
    public AQ() {

    };

    /**
     * AQ class builder</br>
     * It does a local copy of the filenames for their posterior use.<br/>
     * Then, it obtains all data from file and stores it in a format recognizable for the program.<br/>
     * Finally, it creates all possible selectors for the dataset and stores them.
     * @param ftrain Name of the input training file
     * @param feval Name of the input validation file
     * @param ftest Name of the input test file
     * @param foutputTr Name of the output training file
     * @param foutputTst Name of the output test file
     * @param fsal Name of the output information file
     * @param seed Seed for the random number generator
     * @param starSize It is the maximum size for the star in the search process (beam search)
     * @param _accuracy It refers wether the complete selectors will be employed (disjunctions).
     */
    public AQ(String ftrain, String feval, String ftest, String foutputTr,
              String foutputTst,
              String fsal, long seed, int starSize, int _accuracy) {
        int i;

        outputFile = fsal;
        outputFileTr = foutputTr;
        outputFileTst = foutputTst;
        this.seed = seed;
        accuracy = _accuracy;
        //w = _w;

        // we obtain the data
        Dataset train = new Dataset();
        Dataset eval = new Dataset();
        Dataset test = new Dataset();
        try {
            //System.out.println("\nLeyendo train: " + ftrain);
            train.readSet(ftrain, true);
            if (train.hayAtributosContinuos()) {
                System.err.println(
                        "AQ may not handle continuous attributes.\nPlease discretize the data-set");
                //System.exit( -1);
                continousValues = true;
            }
            //System.out.println("\nLeyendo validacion: " + feval);
            eval.readSet(feval, false);
            if (eval.hayAtributosContinuos()) {
                System.err.println(
                        "AQ may not handle continuous attributes.\nPlease discretize the data-set");
                //System.exit( -1);
                continousValues = true;
            }
            //System.out.println("\nLeyendo test: " + ftest);
            test.readSet(ftest, false);
        } catch (IOException e) {
            System.err.println(
                    "There was a problem while trying to read the dataset files:");
            System.err.println("-> " + e);
            problem = true; //System.exit(0);
        }

        if (this.everythingOK()) {
            miSalida = new String("");
            miSalida = test.copiaCabeceraTest();

            train.calculaMasComunes();
            test.calculaMasComunes();

            System.out.println("\nGenerating datasets");
            trainData = creaConjunto(train); //We read the training data
            evalData = creaConjunto(eval);
            testData = creaConjunto(test); //Idem TEST

            this.starSize = starSize;

            trainClasses = train.getC();
            nClassesTr = train.getnClasses();
            nClasses = nClassesTr;
            if (test.getnClasses() > nClassesTr) {
                nClasses = test.getnClasses();
            }

            int[] auxiliar = train.getC();
            Arrays.sort(auxiliar);
            classValues = new int[nClasses];
            classValues[0] = auxiliar[0];
            int valor = 0;
            for (i = 1; i < nClasses; i++) {
                int j;
                for (j = valor;
                         (j < auxiliar.length) &&
                         (auxiliar[j] == classValues[i - 1]);
                         j++) {
                    ;
                }
                if (j < auxiliar.length) {
                    classValues[i] = auxiliar[j];
                    valor = j;
                }
            }

            instancesClassTrain = new int[nClasses];
            for (int j = 0; j < nClasses; j++) {
                instancesClassTrain[j] = 0;
                for (i = 0; i < trainData.size(); i++) {
                    if (classValues[j] == trainClasses[i]) {
                        instancesClassTrain[j]++;
                    }
                }
            }

            instancesClassTest = test.getC();

            auxiliar = eval.getC();
            Arrays.sort(auxiliar);
            classValues = new int[nClasses];
            classValues[0] = auxiliar[0];
            valor = 0;
            for (i = 1; i < nClasses; i++) {
                int j;
                for (j = valor;
                         (j < auxiliar.length) &&
                         (auxiliar[j] == classValues[i - 1]);
                         j++) {
                    ;
                }
                if (j < auxiliar.length) {
                    classValues[i] = auxiliar[j];
                    valor = j;
                }
            }

            int[] clasesEval;
            clasesEval = eval.getC();
            instancesClassEval = new int[nClasses];
            for (int j = 0; j < nClasses; j++) {
                instancesClassEval[j] = 0;
                for (i = 0; i < evalData.size(); i++) {
                    if (classValues[j] == clasesEval[i]) {
                        instancesClassEval[j]++;
                    }
                }
            }

            auxiliar = test.getC();
            Arrays.sort(auxiliar);
            classValues = new int[nClasses];
            classValues[0] = auxiliar[0];
            valor = 0;
            for (i = 1; i < nClasses; i++) {
                int j;
                for (j = valor;
                         (j < auxiliar.length) &&
                         (auxiliar[j] == classValues[i - 1]);
                         j++) {
                    ;
                }
                if (j < auxiliar.length) {
                    classValues[i] = auxiliar[j];
                    valor = j;
                }
            }

            instancesClassTest = new int[nClasses];
            testClasses = test.getC();
            for (int j = 0; j < nClasses; j++) {
                instancesClassTest[j] = 0;
                for (i = 0; i < testData.size(); i++) {
                    if (classValues[j] == testClasses[i]) {
                        instancesClassTest[j]++;
                    }
                }
            }

            attributeNames = train.dameNombres();
            classNames = train.dameClases();
            if (classNames.length < nClasses) {
                classNames = test.dameClases();
            }
            if (classNames == null) {
                classNames = new String[nClasses];
                for (i = 0; i < nClasses; i++) {
                    classNames[i] = "" + classValues[i];
                }
            }
        }
    }

    /**
     * It creates a dataset (attributes/class) according to those obtained from a data-file
     * @param myData It must be a dataset read from file
     * @return The new dataset created, that is, a linked-list of objects "muestras"
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
     * We execute here the AQ algorithm and we create the necessary output data
     */
    public void execute() {
        Randomize.setSeed(seed);
        algorithmAQ();
        evReg = new evaluateRuleQuality(finalRuleSet, evalData, testData,
                                        instancesClassEval, instancesClassTest,
                                        classNames); //We evaluate the quality of the rules ...

        generaSalida(); //We write the output files

    }

    /**
     * Main process of the AQ algorithm
     */
    private void algorithmAQ() {
        ruleSet reg;
        makeSelectors();
        finalRuleSet = new ruleSet();
        finalRuleSet.addClassNames(classNames);
        finalRuleSet.addClassName(attributeNames[trainData.getData(0).
                                        getNattributes()]);

        System.out.println("\nExecuting AQ: #" + nClassesTr + " Classes");

        for (int i = 0; i < nClasses; i++) {
            if (instancesClassTrain[i] > 0) {
                myDataset auxTrain = trainData.copyDataSet(); //back up
                reg = AQforOneClass(i, auxTrain); //It computes the rule that defines this class for the examples
                for (int j = 0; j < reg.size(); j++) {
                    boolean seguir = true;
                    for (int k = 0; (k < finalRuleSet.size()) && (seguir); k++) {
                        /** We delete possible repeated rules **/
                        seguir = !(reg.getRule(j).same(finalRuleSet.getRule(k)));
                    }
                    if (seguir) {
                        finalRuleSet.addRule(reg.getRule(j)); //It adds all rules to the solution
                    }
                }
            }
        }
        finalRuleSet.print();
    };

    /**
     * It obtains the rule set for one class by means of the AQ algorithm
     * @param clas class value (1,2,3...)
     * @param auxTrain A "copy" of the original training set
     * @return The rule set that defines class "clas" (ONE COVER -> OR of complexes)
     */
    private ruleSet AQforOneClass(int clas, myDataset auxTrain) {
        Complex c;
        ruleSet reg = new ruleSet();
        int i;
        System.out.println("\nExtracting rules for class " + classNames[clas]);
        int cont = 0;
        myDataset positives = new myDataset();
        myDataset negatives = new myDataset();

        for (i = 0; i < auxTrain.size(); i++) {
            Instance m = auxTrain.getData(i);
            if (m.getClas() == clas) { //If it is from the same class -> positives
                positives.addData(m);
            } else {
                negatives.addData(m); //it is from other class -> negative
            }
        }
        System.out.println("\n***** #POSITIVE instances: " + positives.size());

        while (positives.size() > 0) { //While there are any positive example not covered by any rule
            c = STAR(positives, negatives); //It computes the star and returns the best complex
            c.setClass(clas);
            c.addAttributeNames(attributeNames);
            reg.addRule(c);

            cont++;
            for (i = 0; i < positives.size(); ) { // We delete positive examples covered by the best complext
                if (c.covered(positives.getData(i))) {
                    positives.deleteData(i);
                } else {
                    i++;
                }
            }
//      System.out.println("\n*****Nº Instances quedan: " + positives.size());
        }
        return reg;
    };

    /**
     * It generates and searches for the best condition:<br/>
     * <ol>
     * <li> It selects the seed of the positive examples</li>
     * <li> It generates a star that covers the seed and no one of the negatives examples</li>
     * <li> It selects the best complex of the star following the LEF criteria (simpler and more positives covered) </li></ol>
     * @param ejpositives Positive examples set
     * @param ejnegatives Negative examples set
     * @return the best complex of the star
     */
    private Complex STAR(myDataset ejpositives, myDataset ejnegatives) {
        ruleSet star = new ruleSet(); //The star is a rule set
        boolean negatives = true;
        Complex mejorCompl = new Complex(nClasses);
        Instance ejNegativo;
        Instance seedExample = ejpositives.getData(Randomize.RandintClosed(0,
                ejpositives.size() - 1));
        /** First solution -> Star with 1 selector complexes **/
        ejNegativo = cercano(seedExample, ejnegatives, star); //we select the negative example nearest to the seed
        if (ejNegativo == null) {
            return makeComplex(seedExample);
        }
        star = extension(ejNegativo, seedExample); //We create the extension (selectors that covers the seedExample but not ejNegativo)
        calculameValorComplex(star, ejnegatives, ejpositives);
        Collections.sort(star.getruleSet()); //we sort according to weight
        //eliminaSubsumidos(star); //Eliminar todo Complex subsumido por otros
        mejorCompl = star.getRule(0).copyRule();
        /** New star: **/
        negatives = evaluateStar(mejorCompl, ejnegatives);
        while (negatives) { //While star covers any negative example
            ejNegativo = cercano(seedExample, ejnegatives, star);
            if (ejNegativo == null) {
                return star.getRule(0).copyRule();
            }
            //We specialise the complex of star for not include the negative example
            ruleSet ext = extension(ejNegativo, seedExample);
            star = conjuncion(ext, star); // x ^ y -> (x \in STAR, y \in EXT)
            calculameValorComplex(star, ejnegatives, ejpositives);
            Collections.sort(star.getruleSet());
            //eliminaSubsumidos(star); //Eliminar todo Complex subsumido por otros
            eliminaPeores(star);
            if (star.size() > 0) {
                if ((evaluateStar(mejorCompl, ejnegatives)) ||
                    (mejorCompl.getWeight() > star.getRule(0).getWeight())) {
                    mejorCompl = star.getRule(0).copyRule();
                }
                negatives = evaluateStar(mejorCompl, ejnegatives);
            } else {
                negatives = false; //I cannot create any other star (i must exit)
            }
        }

        //mejorCompl = star.getRule(0).copiaRegla();
        //mejorCompl.setDistribucion(star.getRule(0).getDistribucion());

        return mejorCompl; //Last rule is the one with less weight (covers more positive examples, less negatives and less complex)
    };

    /**
     * This function generates the output in the file specified by "outputFile"
     */
    private void generaSalida() {
        Files f = new Files();
        String cad = "";
        int i;

        for (i = 0; i < finalRuleSet.size(); i++) {
            cad = finalRuleSet.printString();

        }
        cad += "\n\n" + evReg.printString();

        f.writeFile(outputFile, cad);

        f.writeFile(outputFileTr, miSalida + evReg.salida(evalData));
        f.writeFile(outputFileTst, miSalida + evReg.salida(testData));

    };

    /**
     * It selects the negative example closer to the positive example. If every attribute is the same we delete
     * this negative example (noise).
     * @param example The positive example "seed"
     * @param datos The data-set (containing only negative examples)
     * @param star The complexes star computed at this moment
     * @return The closer negative example to the seed
     */
    private Instance cercano(Instance example, myDataset datos,
                            ruleSet star) {
        Instance negativo = null;
        double valorEj = 0;
        double valorNeg, total = Float.MAX_VALUE;
        int posicion = 0;
        boolean verifica;
        for (int j = 0; j < example.getMuest().length; j++) {
            valorEj += example.getMuest()[j];
        }
        valorEj /= example.getNattributes();
        if (star.size() > 0) {
            for (int i = 0; i < datos.size(); i++) {
                negativo = datos.getData(i).copy();
                verifica = false;
                for (int k = 0; k < star.size() && (!verifica); k++) {
                    verifica = star.getRule(k).covered(negativo);
                }
                if (verifica) { //Negativo is covered by the star
                    if (example.compare(negativo)) {
                        datos.deleteData(i); //I must delete the negative example, since it is the same than seed
                        instancesClassTrain[negativo.getClas()]--;
                    } else {
                        valorNeg = 0;
                        for (int j = 0; j < negativo.getMuest().length; j++) {
                            valorNeg += negativo.getMuest()[j];
                        }
                        valorNeg /= negativo.getNattributes();
                        if (Math.abs(valorEj - valorNeg) < total) {
                            total = Math.abs(valorEj - valorNeg);
                            posicion = i;
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < datos.size(); i++) {
                negativo = datos.getData(i).copy();
                if (example.compare(negativo)) {
                    datos.deleteData(i);
                    instancesClassTrain[negativo.getClas()]--;
                } else {
                    valorNeg = 0;
                    for (int j = 0; j < negativo.getMuest().length; j++) {
                        valorNeg += negativo.getMuest()[j];
                    }
                    valorNeg /= negativo.getNattributes();
                    if (Math.abs(valorEj - valorNeg) < total) {
                        total = Math.abs(valorEj - valorNeg);
                        posicion = i;
                    }
                }
            }
        }
        if (example.compare(datos.getData(posicion))) {
            datos.deleteData(posicion);
            instancesClassTrain[negativo.getClas()]--;
            return null;
        }
        return datos.getData(posicion);
    }
    ;

    /**
     * It computes the value for the weight of each complex by means of:<br/>
     * <ol>
     * <li> Number of positive examples covered</li>
     * <li> Number of negative examples excluded </li>
     * <li> Complexity of the complex </li></ol>
     * <br/>
     * Weight = (a) - (b) / (c)
     * @param star Rule set "star"
     * @param negatives Negative examples set
     * @param positives Positive examples set
     */
    private void calculameValorComplex(ruleSet star,
                                        myDataset negatives,
                                        myDataset positives) {
        for (int i = 0; i < star.size(); i++) {
            int pos = evaluaComplex(star.getRule(i), positives);
            int negs = evaluaComplex(star.getRule(i), negatives);
            //int P = positives.size();
            int N = negatives.size();
            int excl = N - negs; //Excluded = total - covered
            //double compl = (double)pos/N; //completitud
            //double consig = (double)((((double)pos/(pos + negs)) - ((double)P /(P+N))) * (P+N) / N); //ganancia de consistencia
            //double peso = Math.pow(compl,w)*Math.pow(consig,1.0-w);
            double peso = pos + excl;
            star.getRule(i).setWeight(peso); //asignamos el peso
        }
//    Collections.sort(star.getruleSet()); //Ordena según el valor del peso :) [menos peso, lo ponemos antes]
    }
    ;

    /**
     * It evaluates a star complex to check if it covers any negative example
     * @param c Complex 'i'-th of the star
     * @param e Negative examples set
     * @return True if it covers any negative example; false in other case.
     */
    private boolean evaluateStar(Complex c, myDataset e) {
        for (int i = 0; i < e.size(); i++) {
            if (c.covered(e.getData(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * It removes the worst rules of the star until a predetermined size (maxstar) [beam search]
     * @param star Rule set "star"
     */
    private void eliminaPeores(ruleSet star) {
        for (int i = star.size() - 1; i >= starSize;
                     star.deleteRule(i), i--) {
            ;
        }

    }

    /**
     * Evaluation of all complexes over the training set to check how many are covered of each class<br/>
     * In the end, we obtain the number of examples that are satisfied in each class by the selected complex
     * @param c Selected complex
     * @param e Training set
     * @return Number of examples from "e" that are covered by "c"
     *
     */
    private int evaluaComplex(Complex c, myDataset e) {
        int i, contador = 0;
        for (i = 0; i < e.size(); i++) {
            if (c.covered(e.getData(i))) {
                contador++;
                c.addClassDistribution(e.getData(i).getClas());
            }
        }
        return contador;
    }

    /**
     * It computes a complexes set called "extension", that covers the seed but not the negative example
     * @param negative The negative example that is closer to the seed
     * @param seed The selected positive example
     * @return A selectors set that covers "seed" but not "negative"
     */
    private ruleSet extension(Instance negative, Instance seed) {
        ruleSet ext = new ruleSet();
        myDataset neg = new myDataset();
        myDataset pos = new myDataset();
        neg.addData(negative);
        pos.addData(seed);
        for (int i = 0; i < selectors.size(); i++) {
            Selector s = selectors.getSelector(i);
            Complex c = new Complex(s, nClasses);
            c.setClass(pos.getData(0).getClas());
            if ((!evaluateStar(c, neg)) && (evaluateStar(c, pos))) {
                ext.addRule(c);
            }
        }
        return ext;
    };

    /**
     * It does the conjunction between Extension and Seed (an AND of each complex)
     * @param ext Extension set
     * @param star Rule set star
     * @return star AND Extension [each rule of star have another complex, if not included previously]
     */
    private ruleSet conjuncion(ruleSet ext, ruleSet star) {
        if (star.size() == 0) { //Para el caso inicial
            return ext;
        }
        ruleSet starAux = new ruleSet();
        int clase = ext.getRule(0).getClas();
        for (int i = 0; i < ext.size(); i++) {
            Selector s = ext.getRule(i).getSelector(0); //extension has only one selector!
            for (int j = 0; j < star.size(); j++) {
                Complex aux2 = star.getRule(j);
                Complex aux = new Complex(nClasses);
                boolean sigue = true;
                for (int h = 0; (h < aux2.size()) && (sigue); h++) {
                    Selector s2 = aux2.getSelector(h);
                    aux.addSelector(s2);
                    if (s2.compareTo(s) < 2) { //same attribute
                        sigue = false; //not added
                    }
                }
                if (sigue) { //Selector is not included in any complex of star
                    aux.addSelector(s);
                    aux.setClass(clase);
                    //evaluarComplex(aux, train);
                    starAux.addRule(aux);
                }
            }
        }
        return starAux;
    }

    /**
     * To remove every complex subsumed by other </br>
     *
     * @param star The star
     *
     */
    private void eliminaSubsumidos(ruleSet star) {
        if (star.size() > starSize) {
            star.deleteSubsumed(starSize);
        }
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
     * Hace un Complex especifico para el example
     * @param example Instance example de entrada
     * @return Complex El Complex más específico que cubre al example
     */
    private Complex makeComplex(Instance example) {
        Selector s = new Selector(0, 0, example.getAttribute(0));
        Complex complex = new Complex(s, nClasses);
        for (int i = 1; i < example.getNattributes(); i++) {
            s = new Selector(i, 0, example.getAttribute(i));
            complex.addSelector(s);
        }
        complex.setClass(example.getClas());
        return complex;
    }
}

