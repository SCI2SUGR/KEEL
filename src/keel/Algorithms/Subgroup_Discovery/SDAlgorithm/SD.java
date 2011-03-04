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
 * @author Writed by Cristóbal J. Carmona (University of Jaen) 24/06/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDAlgorithm;

import java.io.IOException;
import java.util.Arrays;
import java.text.DecimalFormat;
import java.util.Collections;

import org.core.Files;

public class SD {

    /**
     * <p>
     * It is the main class of the SD algorithm
     * </p>
     */

    private String input_file_tra;
    private String input_file_eval;
    private String input_file_tst;
    private String output_file_tra;
    private String output_file_tst;
    private String rule_file;
    private String measure_file;

    private String algorithm;
    private int g;
    private int beamWidth;
    private int numRules;
    private float minSupp;

    private int muestPorClaseTrain[];
    private int muestPorClaseEval[];
    private int muestPorClaseTest[];
    private int[] valorClases;          // Class values
    private int nClases;                // Number of classes

    private Complex storeSelectors;     // The possible selectors

    private SetRules setFinalRules;     // Final rules obtained
    private EvaluateRules evRules;

    private SetData dataTra;            // Train data
    private SetData dataEva;            // Evaluat data
    private SetData dataTst;            // Test data
    private int classTra[];
    private int classTst[];
    private double time;

    private String theExit;

    private int nAttributes;

    private String[] nameAttributes;
    private String[] nameClasses;
    private boolean Continuous = false;

    public boolean isOk(){
        return (!Continuous);
    }
    
    /**
     * <p>
     * Constructs the object of SD_algorithm
     * </p>
     * @param input_ftrain          Name of the training file
     * @param input_feval           Name of the evaluating file
     * @param input_ftest           Name of the test file
     * @param output_ftrain         Name of the output training file
     * @param output_ftest          Name of the output test file
     * @param arule_file            Name of the rule file
     * @param ameasure_file         Name of the measure file
     * @param alg                   Name of the algorithm
     * @param abeamWidth            Value of the width for the beam
     * @param ag                    Value of g parameter for the algorithm
     * @param aminSupp              Value of minSupp parameter for the algorithm
     * @param anumRules             Value of anumRules parameter for the algorithm
     */
    public SD(String input_ftrain, String input_feval, String input_ftest,
            String output_ftrain, String output_ftest, String arule_file, String ameasure_file,
            String alg, int abeamWidth, int ag, float aminSupp, int anumRules) {

        algorithm = alg;

        System.out.println("Executing "+ algorithm);

        input_file_tra = input_ftrain;
        input_file_eval = input_feval;
        input_file_tst = input_ftest;

        output_file_tra = output_ftrain;
        output_file_tst = output_ftest;
        rule_file = arule_file;
        measure_file = ameasure_file;

        beamWidth = abeamWidth;
        g = ag;
        minSupp = aminSupp;
        numRules = anumRules;

        Dataset train = new Dataset();
        Dataset eval = new Dataset();
        Dataset test = new Dataset();

        // Check if the data sets have continuous variables
        try {
            train.readSet(input_file_tra, true);
            if (train.hayAtributosContinuos()){
                System.err.println("SD_algorithm may not work properly with continuous attributes.\nPlease discretize the data base");
                Continuous = true;
            }
            eval.readSet(input_file_eval, false);
            if (eval.hayAtributosContinuos()){
                System.err.println("SD_algorithm may not work properly with continuous attributes.\nPlease discretize the data base");
                Continuous = true;
            }
            test.readSet(input_file_tst, false);
        } catch (IOException e) {
            System.err.println("There was a problem while reading the data-set files:");
            System.err.println("-> " + e);
            System.exit(0);
        }

        theExit = new String("");
        theExit = test.copiaCabeceraTest();

        // We obtain the training and test datasets
        System.out.println("\nGenerating datasets");
        dataTra = new SetData();
        dataEva = new SetData();
        dataTst = new SetData();

        train.calculaMasComunes();
        eval.calculaMasComunes();
        test.calculaMasComunes();

        dataTra = createDataset(train);
        dataEva = createDataset(eval);
        dataTst = createDataset(test);

        classTra = train.getC();
        nClases = train.getnClasses();
        nAttributes = train.getnentradas();

        int[] auxiliar = train.getC();
        Arrays.sort(auxiliar);
        valorClases = new int[nClases];
        valorClases[0] = auxiliar[0];
        int valor = 0;
        for (int i = 1; i < nClases; i++) {
            int j;
            for (j = valor; auxiliar[j] == valorClases[i - 1]; j++) {
                ;
            }
            valorClases[i] = auxiliar[j];
            valor = j;
        }

        muestPorClaseTrain = new int[nClases];
        for (int j = 0; j < nClases; j++) {
            muestPorClaseTrain[j] = 0;
            for (int i = 0; i < dataTra.size(); i++) {
                if (valorClases[j] == classTra[i]) {
                    muestPorClaseTrain[j]++;
                }
            }
        }

        classTst = test.getC();
        muestPorClaseTest = new int[nClases];
        for (int j = 0; j < nClases; j++) {
            muestPorClaseTest[j] = 0;
            for (int i = 0; i < dataTst.size(); i++) {
                if (valorClases[j] == classTst[i]) {
                    muestPorClaseTest[j]++;
                }
            }
        }

        int [] clasesEval;
        clasesEval = eval.getC();
        muestPorClaseEval = new int[nClases];
        for (int j = 0; j < nClases; j++) {
            muestPorClaseEval[j] = 0;
            for (int i = 0; i < dataEva.size(); i++) {
                if (valorClases[j] == clasesEval[i]) {
                    muestPorClaseEval[j]++;
                }
            }
        }

        time = System.currentTimeMillis(); //Time

        nameAttributes = train.dameNombres();
        nameClasses = train.dameClases();
        if (nameClasses == null){
            nameClasses = new String[nClases];
            for (int i = 0; i < nClases; i++){
                nameClasses[i] = ""+valorClases[i];
            }
        }


    }


    /**
     * <p>
     * Creates a dataset through the files of the parameteres
     * </p>
     * @param Dataset       A dataset of a file
     * @return              The dataset created. A linked list with samples
     */
    private SetData createDataset(Dataset mis_datos) {

        SetData datos = new SetData();      //Create a new dataset
        int tam = mis_datos.getnentradas(); //The number of input attributes
        double[] vars = new double[tam];    //The vector with the values of the attributes
        double[][] X;
        int[] C;
        int clase = 0; //Variable with the value of the class

        X = mis_datos.getX();
        C = mis_datos.getC();
        for (int i = 0; i < mis_datos.getndatos(); i++) {
            //System.out.print("\n"+i+":");
            for (int j = 0; (j < tam); j++) {
                //System.out.print(" "+X[i][j]);
                if (mis_datos.isMissing(i, j)) {
                    vars[j] = mis_datos.masComun(j);
                } else {
                    vars[j] = X[i][j];
                }
            }
            clase = C[i];
            Instance m = new Instance(vars, clase, tam);
            m.setPosFile(i);
            datos.addDato(m);
        }
        return datos;
    }

    /**
     * <p>
     * Execute the algorithm SD
     * </p>
     */
    public void execute() {

        makeSelectors();
        SetData datosTrainAux = new SetData();
        datosTrainAux = dataTra.copiaConjDatos();
        SDClasses(datosTrainAux, valorClases);
        time = System.currentTimeMillis() - time;

        if(setFinalRules.size() == 0){
            //If there are not rules with good support
            System.out.println("\nThere are not rules with good level of support");
        } else {
            evRules = new EvaluateRules(setFinalRules, dataEva, dataTst,
                                            muestPorClaseEval, muestPorClaseTest,
                                            nameClasses, measure_file);
            generateExit();
        }
        System.out.println("\n\nExecuting finished\n");
    }

    /**
     * <p>
     * Create the total set of selectrs for obtaining the possible rules
     * </p>
     */
    private void makeSelectors() {

        int totalAtributos = dataTra.getDato(0).getNattributes();
        int ejemplos = dataTra.size();
        double[][] lista = new double[ejemplos + 1][totalAtributos]; //To see !=
        for (int i = 0; i < totalAtributos; i++) { // For each attribute
            lista[0][i] = dataTra.getDato(0).getMuest()[i]; //Init
            lista[1][i] = Double.POSITIVE_INFINITY; //Mark
        }

        for (int i = 0; i < totalAtributos; i++) { // For each attribute
            for (int j = 1; j < ejemplos; j++) { //For each example
                double valor = dataTra.getDato(j).getMuest()[i];
                int k = 0;
                while (!(Double.isInfinite(lista[k][i]))) { //While not mark
                    if (lista[k][i] == valor) { // It is the same
                        break;
                    }
                    k++;
                }
                if (Double.isInfinite(lista[k][i])) { //Final position list
                    lista[k][i] = valor;
                    lista[k + 1][i] = Double.POSITIVE_INFINITY;
                }
            }
        }
        storeSelectors = new Complex(nClases); //Selectors
        for (int i = 0; i < totalAtributos; i++) { // For each attribute
            for (int h = 0; h < ejemplos; h++) { // For each example
                if (Double.isInfinite(lista[h][i])) {
                    break; // Next attribute
                }
                for (int j = 0; j < 4; j++) { // <>,<=,>
                    Selector s = new Selector(i, j, lista[h][i]); // We take the value for each attribute [attr,op,value]
                    storeSelectors.addSelector(s); // Introduce if not the same
                }
            }
        }
    }


    /**
     * <p>
     * It obtains the rules for the values of the class
     * </p>
     * @param datosTrainAux             It is the examples of the training set
     * @param valorClases               It is the values of the classes
     */
    private void SDClasses(SetData datosTrainAux, int[] valorClases) {

        setFinalRules = new SetRules();
        setFinalRules.addNameClasses(nameClasses);
        setFinalRules.addNameClass(nameAttributes[nAttributes]);
        System.out.println("\n Extracting rules for the different classes:");
        for (int i = 0; i < nClases; i++) { //For each class
            SD(datosTrainAux, valorClases[i]);
        }

    }

    /**
     * <p>
     * It obtains the rules for a value of the class
     * </p>
     * @param train         It is the examples of the training set
     * @param clase         It is the class to study
     */
    private void SD(SetData train, int clase) {

        boolean continuar = false;
        System.out.println("\n We search the best rules for class " + nameClasses[clase]);

        SetRules beam = new SetRules();
        SetRules newbeam = new SetRules();
        beam.addNameClasses(nameClasses);
        beam.addNameClass(nameClasses[clase]);

        //Create the initial beam
        for (int i = 0; i < storeSelectors.size(); i++) {
            Complex aux = new Complex(nClases);
            aux.setClas(clase);
            aux.adjuntaNombreAtributos(nameAttributes);
            aux.addSelector(storeSelectors.getSelector(i));
            evaluateRuleInit(aux, train);
            beam.addRegla(aux);
        }
        //Sort out the Beam
        Collections.sort(beam.getConjReglas());

        beam.eliminaSubsumidos(beam.size());
        beam.deleteRulesLowSupport(beamWidth, minSupp);
        beam.deleteEqualAttributes(beamWidth);

        for (int j = beam.size() - 1; beam.size() > beamWidth; j--) {
            beam.deleteRegla(j);
        }

        //Copy the beam in newbeam
        newbeam.addNameClasses(nameClasses);
        newbeam.addNameClass(nameClasses[clase]);
        newbeam.addReglas(beam);

        do { // while improvement
            continuar = false;
            for (int i = 0; i < storeSelectors.size(); i++) {
                Selector s = storeSelectors.getSelector(i);
                for (int j = 0; j < beam.size(); j++) {
                    Complex aux2 = beam.getRule(j);
                    Complex aux = new Complex(nClases);
                    boolean sigue = true;
                    for (int h = 0; (h < aux2.size()) && (sigue); h++) {
                        Selector s2 = aux2.getSelector(h);
                        aux.addSelector(s2);
                        if (s2.compareTo(s) < 2) { // It is the same attribute
                            sigue = false; // Don´t add this attribute
                        }
                    }
                    if (sigue) { //This is a new selector to add to the rule
                        aux.addSelector(s);
                        aux.setClas(clase);
                        aux.adjuntaNombreAtributos(nameAttributes);
                        //Evaluate TP/|E| and q_g
                        boolean improvement = evaluateRule(aux, train, newbeam);
                        if ((improvement)&&(isRelevant(aux, newbeam)&&(aux!=null))){
                            //The new rule is added to newbeam
                            newbeam.addRegla(aux);
                            //There is an improvement in NewBeam
                            continuar = true;
                        }
                    }
                }
            }
            // Sort out newbeam
            Collections.sort(newbeam.getConjReglas());
            // Eliminate rules not valid
            newbeam.eliminaSubsumidos(newbeam.size());
            newbeam.deleteNull();
            newbeam.deleteEqual(newbeam.size());
            for (int j = newbeam.size() - 1; newbeam.size() > beamWidth; j--) {
                newbeam.deleteRegla(j);
            }
            beam.deleteAll();
            // Copy newbeam in beam
            beam.addReglas(newbeam);

        } while (continuar);


        // if number of rules is lower than beamWidth
        if ((numRules != 0)&&(numRules < beamWidth)){
            int conta = 0;
            for(int i=0; i<beamWidth && conta<numRules; i++){
                if(beam.getRule(i).getSup() > minSupp){
                    setFinalRules.addRegla(beam.getRule(i));
                    conta++;
                }
            }
        } else {
            for(int i=0; i<beamWidth; i++){
                if(beam.getRule(i).getSup() > minSupp){
                    setFinalRules.addRegla(beam.getRule(i));
                }
            }
        }

    }

    /**
     * <p>
     * Check if the new rule is significant
     * </p>
     * @param Complex           The rule to analyse
     * @param SetRules          A set of rules to compare
     * @return                  True if it is significant
     */
    private boolean isRelevant(Complex c, SetRules newBeam) {

        Complex rule;
        boolean relevant = true;

        for(int i=0; i<newBeam.size(); i++){
            rule = newBeam.getRule(i);
            if((c.getTP() < rule.getTP())&&(c.getFP() > rule.getFP()))
                relevant = false;
        }

        return(relevant);

    }


    /**
     * <p>
     * Evaluate the new rule with respect the rules of newbeam.
     * </p>
     * @param c             The rule to evaluate
     * @param e             The set of data to check
     * @param newbeam       The set of rules to check
     * @return              If there is an improvement in this rule
     */
    private boolean evaluateRule(Complex c, SetData e, SetRules newbeam){

        boolean improvement = true;

        int i;
        int cl;
        float tp = 0;
        float fp = 0;

        c.deleteDistrib();

        for (i = 0; i < e.size(); i++) {
            cl = e.getDato(i).getClas();

            if (c.cover(e.getDato(i))) {
                c.incrementDistrib(cl);
                if (cl == c.getClas()) {
                    tp++;
                } else fp++;
            }
        }

        c.setTP(tp);
        c.setFP(fp);
        c.setQg(tp/(fp+g));
        c.setSup(tp/e.size());
        c.adjustDistrib();

        for (i=0; i<newbeam.size(); i++){
            float aux_q_g = (float) newbeam.getRule(i).getQg();
            if (c.getQg() <= aux_q_g){
                improvement = false;
            }
        }

        if(improvement){
            if (c.getSup() <= minSupp){
                improvement = false;
            }
        }

        return improvement;

    }

    /**
     * <p>
     * Evaluate a new rule at the initialisation
     * </p>
     * @param c             The rule to evaluate
     * @param e             The set of data to check
     * @return              If there is an improvement in this rule
     */
    private void evaluateRuleInit(Complex c, SetData e){

        int i;
        int cl;
        float tp = 0;
        float fp = 0;

        c.deleteDistrib();

        for (i = 0; i < e.size(); i++) {
            cl = e.getDato(i).getClas();

            if (c.cover(e.getDato(i))) {
                c.incrementDistrib(cl);
                if (cl == c.getClas()) {
                    tp++;
                } else fp++;
            }
        }

        c.setQg(tp/(fp+g));
        c.setSup(tp/e.size());
        c.adjustDistrib();

    }

    /**
     * <p>
     * Generate the exit files
     * </p>
     */
    private void generateExit() {
        
        String cad = "";
        DecimalFormat d = new DecimalFormat("0.000");

        // Print screen
        cad = setFinalRules.printString();
        time = (double) time / 1000;

        //cad += "\n\n" + evRules.printString() + "\nTime: " + d.format(time);
        cad += "\n\nTime: " + d.format(time);
        // Print the result of rules and quality measures in "rule file"
        Files.writeFile(rule_file, cad);

        // Print results of train and test
        Files.writeFile(output_file_tra, theExit + evRules.exitResult(dataTra));
        Files.writeFile(output_file_tst, theExit + evRules.exitResult(dataTst));

    }

}
