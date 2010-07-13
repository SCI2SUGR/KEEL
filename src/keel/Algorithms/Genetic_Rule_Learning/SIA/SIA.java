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

package keel.Algorithms.Genetic_Rule_Learning.SIA;

import java.util.*;
import java.io.IOException;
import org.core.*;

/**
 * <p>Title: Main class of the algorithm</p>
 * <p>Description: It contains the esential methods for the SIA algorithm</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: KEEL</p>
 * @author Alberto Fernández (University of Granada) 23/02/2005
 * @since JDK1.5
 * @version 1.8
 */
public class SIA {

    private String outputFile;
    private String outputFileTr;
    private String outputFileTst;

    private int nClasses;

    private ruleSet finalRuleSet;
    private evaluateRuleQuality evReg;

    private myDataset trainData;
    private myDataset evalData;
    private myDataset testData;
    private int trainClasses[], evalClasses[], testlClasses[];

    private long time;
    private long seed;

    private String myOutput;

    private int instPerClassTr[];
    private int instPerClassEval[];
    private int instPerClassTest[];
    private int valorClases[];

    private double[][] bounds;
    private int[] lims; //stores the length for each bound
    private int[] types; //type of the ancedent -> 0:real,1:integer,2:enumerate
    private double[] valoresMin;

    private int nAttributes;
    private int nbMax;

    private double Tstr;
    private double alfa, beta;

    private String[] nombreAtributos, nombreClases;
    private String[][] nombreValores;

    private int[] ejemplosDisponibles;
    private int nDisponibles;

    /**
     * Default Builder
     */
    public SIA() {

    };

    /**
     * Builder of the class SIA
     * It carries out a local copy of the name of the files for their posterior use<br/>
     * Then, obtains the data from the input files and stores it in a structure for the program<br/>
     * Finally, it creates the possible bounds for the attribute values<br/>
     *
     * @param ftrain Name of the input training data-set
     * @param feval Name of the input validation data-set
     * @param ftest Name of the input test data-set
     * @param fSalidatr Name of the output training data-set
     * @param fSalidatst Name of the output test data-set
     * @param fsal Name of the oupput statistics file
     * @param seed seed for the random generator
     * @param nb Maximum number of iterations of the genetic algorithm
     * @param alfa parameter for the criterium of the rule evaluation
     * @param beta parameter for the criterium of the rule evaluation
     * @param Tstr Threshold for the rule filtering
     */
    public SIA(String ftrain, String feval, String ftest, String fSalidatr,
               String fSalidatst,
               String fsal, long seed, int nb, double alfa, double beta,
               double Tstr) {
        int i;

        outputFile = fsal;
        outputFileTr = fSalidatr;
        outputFileTst = fSalidatst;
        this.seed = seed;
        this.nbMax = nb;
        this.alfa = alfa;
        this.beta = beta;
        this.Tstr = Tstr;

        System.out.println("\nGenerating data-sets...");

        Dataset train = new Dataset();
        Dataset eval = new Dataset();
        Dataset test = new Dataset();
        try {
            train.readSet(ftrain, true);
            eval.readSet(feval, false);
            test.readSet(ftest, false);
        } catch (IOException e) {
            System.err.println(
                    "There was a problem while trying to read the data-sets files:");
            System.err.println("-> " + e);
            System.exit(0);
        }

        myOutput = new String("");
        myOutput = test.copiaCabeceraTest();

        trainData = creaConjunto(train);
        evalData = creaConjunto(eval);
        testData = creaConjunto(test);
        createBounds();

        trainClasses = train.getC();
        time = System.currentTimeMillis();
        nClasses = train.getnClasses();

        int[] auxiliar = train.getC();
        Arrays.sort(auxiliar);
        valorClases = new int[nClasses];
        valorClases[0] = auxiliar[0];
        int valor = 0;
        for (i = 1; i < nClasses; i++) {
            int j;
            for (j = valor;
                     (j < auxiliar.length) && (auxiliar[j] == valorClases[i - 1]);
                     j++) {
                ;
            }
            if (j < auxiliar.length) {
                valorClases[i] = auxiliar[j];
                valor = j;
            }
        }

        instPerClassTr = new int[nClasses];
        for (int j = 0; j < nClasses; j++) {
            instPerClassTr[j] = 0;
            for (i = 0; i < trainData.size(); i++) {
                if (valorClases[j] == trainClasses[i]) {
                    instPerClassTr[j]++;
                }
            }
        }

        evalClasses = eval.getC();

        auxiliar = eval.getC();
        Arrays.sort(auxiliar);
        valorClases = new int[nClasses];
        valorClases[0] = auxiliar[0];
        valor = 0;
        for (i = 1; i < nClasses; i++) {
            int j;
            for (j = valor;
                     (j < auxiliar.length) && (auxiliar[j] == valorClases[i - 1]);
                     j++) {
                ;
            }
            if (j < auxiliar.length) {
                valorClases[i] = auxiliar[j];
                valor = j;
            }
        }

        instPerClassEval = new int[nClasses];
        for (int j = 0; j < nClasses; j++) {
            instPerClassEval[j] = 0;
            for (i = 0; i < evalData.size(); i++) {
                if (valorClases[j] == evalClasses[i]) {
                    instPerClassEval[j]++;
                }
            }
        }

        instPerClassTest = test.getC();

        auxiliar = test.getC();
        Arrays.sort(auxiliar);
        valorClases = new int[nClasses];
        valorClases[0] = auxiliar[0];
        valor = 0;
        for (i = 1; i < nClasses; i++) {
            int j;
            for (j = valor;
                     (j < auxiliar.length) && (auxiliar[j] == valorClases[i - 1]);
                     j++) {
                ;
            }
            if (j < auxiliar.length) {
                valorClases[i] = auxiliar[j];
                valor = j;
            }
        }

        instPerClassTest = new int[nClasses];
        testlClasses = test.getC();
        for (int j = 0; j < nClasses; j++) {
            instPerClassTest[j] = 0;
            for (i = 0; i < testData.size(); i++) {
                if (valorClases[j] == testlClasses[i]) {
                    instPerClassTest[j]++;
                }
            }
        }

        nombreAtributos = train.dameNombres();
        nombreClases = train.dameClases();
        nombreValores = train.dameValores();
        valoresMin = train.valoresMin();
        String[] nombreClasesAux = test.dameClases();
        if (nombreClases.length < nombreClasesAux.length) {
            nClasses = nombreClasesAux.length;
            nombreClases = new String[nClasses];
            for (i = 0; i < nClasses; i++) {
                nombreClases[i] = nombreClasesAux[i];
            }
        }

        nDisponibles = trainData.size();
        ejemplosDisponibles = new int[nDisponibles];
        for (i = 0; i < nDisponibles; i++) {
            ejemplosDisponibles[i] = i;
        }

    }

    /**
     * It creates a dataset (attributes/class) according to those obtained from a data-file
     * @param myData It must be a dataset read from file
     * @return The new dataset created, that is, a linked-list of objects "Instances"
     */
    private myDataset creaConjunto(Dataset myData) {
        myDataset datos = new myDataset();
        nAttributes = myData.getnentradas();
        double[] vars = new double[nAttributes];
        int clase = 0;
        types = new int[nAttributes];
        types = myData.getTypes();

        double[][] X;
        int[] C;
        X = myData.getX();
        C = myData.getC();
        for (int i = 0; i < myData.getndatos(); i++) {
            for (int j = 0; j < nAttributes; j++) {
                if (myData.isMissing(i, j)) {
                    vars[j] = Double.NaN;
                } else {
                    vars[j] = X[i][j];
                }
            }
            Instance m = new Instance(vars, C[i], nAttributes);
            m.setPosFile(i);
            m.setCovered(0);
            datos.addData(m);
        }
        return datos;
    }

    /**
     * It builds an array where we will store the different bounds for each one of the attributes.<br/>
     * The bounds are obtained copying all the possible values for the attributes, an then computing::
     * <em>{minValue,(minValue+v1)/2,(v1+v2)/2,...,(vn-1+vn)/2,(vn+maxValue)/2,maxValue};</em>
     */
    private void createBounds() {
        lims = new int[nAttributes];
        double[][] bounds_aux = new double[nAttributes][trainData.size()];
        double[] vars;
        boolean seguir = true;

        for (int i = 0; i < trainData.size(); i++) {
            vars = trainData.getData(i).getMuest();
            for (int j = 0; j < nAttributes; j++) {
                seguir = true;
                for (int k = 0; k < lims[j] && seguir; k++) {
                    //if ( (bounds_aux[j][k] == vars[j]) || (Double.isNaN(vars[j]))) { //Si ya esta en el vector o el valor es NaN..
                    if ((bounds_aux[j][k] ==
                         (double) Math.round(vars[j] * 100) / 100) ||
                        (Double.isNaN(vars[j]))) {
                        seguir = false;
                    }
                }
                if ((seguir) && (!(Double.isNaN(vars[j])))) { //If it is the first
                    bounds_aux[j][lims[j]] = (double) Math.round(vars[j] * 100) /
                                             100; //Adds the element
                    //bounds_aux[j][lims[j]] = vars[j]; //Añado el elemento
                    lims[j]++;
                }
            }
        }
        //Ordering bubble algorithm
        for (int i = 0; i < nAttributes; i++) {
            for (int j = 0; j < lims[i] - 1; j++) {
                for (int k = j + 1; k < lims[i]; k++) {
                    if (bounds_aux[i][j] > bounds_aux[i][k]) {
                        double temp = bounds_aux[i][j];
                        bounds_aux[i][j] = bounds_aux[i][k];
                        bounds_aux[i][k] = temp;
                    }
                }
            }
        }
        bounds = new double[nAttributes][]; //tamaño max (todos !=)
        for (int i = 0; i < nAttributes; i++) {
            /*while(lims[i] > trainData.size()){
              for (int j = 1, k = 2; j < lims[i]/2; j++, k+=2){
                bounds_aux[i][j] = bounds_aux[i][k];
              }
                   }
                   lims[i] /= 2;*/
            bounds[i] = new double[lims[i]];
            for (int j = 0; j < lims[i]; j++) {
                bounds[i][j] = bounds_aux[i][j];
            }
            //System.err.println("Numero de bounds -> " + bounds[i].length);
        }

        for (int i = 0; i < nAttributes; i++) {
            //System.out.print("\nAtributo["+i+"]:"+bounds[i][0]+",");
            for (int j = 1; j < lims[i] - 1; j++) {
                //bounds[i][j] = (bounds[i][j + 1] + bounds[i][j]) / 2.0;
                bounds[i][j] = (bounds[i][j - 1] + bounds[i][j]) / 2.0;
                //System.out.print(bounds[i][j]+",");
            }
            //System.out.print(bounds[i][lims[i]-1]+" .");
        }
        //System.exit(-1);
    }

    /**
     * We execute here the main SIA algorithm and then we creat the output files
     */
    public void lanzar() {

        Randomize.setSeed(seed);
        System.out.println("Running SIA!");
        finalRuleSet = hacerSIA();
        System.out.println("Rules created. Algorithm finished");
        //---------------------
        System.out.println(" Time elapsed (minutes): " +
                           (System.currentTimeMillis() - time) / 60000);
        time = System.currentTimeMillis() - time;
        evReg = new evaluateRuleQuality(finalRuleSet, nClasses, bounds, lims,
                                        types, nombreClases);
        generaSalida();

    }

    /**
     * It performs the SIA algorithm.<br/>
     * <b>ALGORITHM:</b><br/>
     * <ol>
     * <li> R = empty rule set</li>
     * <li> Set as UNCOVERED all classes of the examples</li>
     * <li> Select randomly an UNCOVERED example [ex]</li>
     * <li> Make R_init the most specific rule that covers ex</li>
     * <li> Generalize R_init to find the optimum rule R* que that cover ex maximizing Cq(R)</li>
     * <li> Label COVERED all classes covered by R*</li>
     * <li> Add R* to R</li>
     * <li> If some UNCOVERED examples remains, go to (3)</li>
     * <li> Remove rules from R using a filtering rule algorithm</li>
     * <li> 'Return' R.</li></ol>
     *
     * @return a rule set that covers all examples
     */
    private ruleSet hacerSIA() {

        ruleSet R = new ruleSet(); //(1), (2) it's made by default when creating the data-set
        Instance ejseed;
        boolean quedanUncovered = true;
        System.out.println("Generating Rules");
        do {
            do {
                ejseed = trainData.getData(ejemplosDisponibles[Randomize.
                                               RandintClosed(0,
                        nDisponibles-1)]);
            } while (ejseed.getCovered() > 0); //(3)
            Rule Rinit = especifica(ejseed); //(4)
            Rule Rmax = generaliza(Rinit, ejseed); //(5)
            Rmax.addAttributeNames(nombreAtributos);
            Rmax.addClassName(nombreAtributos[nAttributes]);
            Rmax.addClassNames(nombreClases);
            Rmax.addValuesNames(nombreValores);
            quedanUncovered = cubiertos(Rmax); //(6)
            Rmax.setMinValues(valoresMin);
            R.addRule(Rmax); //(7)
            //Rmax.print();
        } while (quedanUncovered);
        //System.out.println("Proceso de Filtrado de ruleSet");
        filtrado(R);
        return R;
    }

    /**
     * It creates an specific rules that covers an example
     * @param ej The example to cover
     * @return the most specific rule that covers ej
     */
    private Rule especifica(Instance ej) {
        Rule r = new Rule(nAttributes);
        for (int i = 0; i < nAttributes; i++) {
            Condition c;
            if (Double.isNaN(ej.getAttribute(i))) {
                c = new Condition(i); //Cond -> *
                c.setType(0);
            } else if (types[i] == 2) { //enumerate
                c = new Condition(i, ej.getAttribute(i)); //Cond -> Ai = ei
                c.setType(1);
            } else { //real or integer
                //Cond -> B1 <= Ai <= B2 [being B1 and B2 the closest bounds
                c = new Condition(i, ej.getAttribute(i));
                c.setType(2);
                double valor = ej.getAttribute(i);
                for (int j = 0; j < lims[i] - 1; j++) {
                    if ((valor >= bounds[i][j]) &&
                        (valor <= bounds[i][j + 1])) {
                        c.setLowerBound(bounds[i][j]);
                        c.setUpperBound(bounds[i][j + 1]);
                        break;
                    }
                }
            }
            r.setCondition(c);
        }
        r.setClas(ej.getClas());
        return r;
    }

    /**
     * It generalizes a rule for a seed example in order to find an optimum rule set
     * @param Rinit Initial rule
     * @param ej Seed example
     * @return The best rule of those found during the search (the strongest one)
     */
    private Rule generaliza(Rule Rinit, Instance ej) {
        Rule r = new Rule(nAttributes);
        ruleSet poblacion = new ruleSet(); //P = {}
        int iteraciones = 0;
        double fuerzaAux;

        Rule R1 = new Rule(nAttributes);
        Rule R2 = new Rule(nAttributes);
        R1 = crea(Rinit);
        poblacion.addRule(R1);
        fuerzaAux = R1.getStrength();
        do {
            double prob = Randomize.RandClosed();
            if (prob <= 0.1) { //Creation
                R1 = crea(Rinit); //Creates and evaluates (strength)
                poblacion.insertion(R1);
            } else if (prob < 0.8) { //Generalization
                int aleat = Randomize.RandintClosed(0, poblacion.size()-1);
                R1 = poblacion.getRule(aleat);
                R2 = crea(R1);
                if (R1.getStrength() < R2.getStrength()) { //Higher Strength -> replace
                    poblacion.deleteRule(aleat);
                    poblacion.addRule(R2);
                } else {
                    poblacion.insertion(R2);
                }
            } else { //Crossover
                int aleat = Randomize.RandintClosed(0, poblacion.size()-1);
                int aleat2 = 0;
                do {
                    aleat2 = Randomize.RandintClosed(0, poblacion.size()-1);
                } while ((aleat2 == aleat) && (poblacion.size() > 1));
                R1 = poblacion.getRule(aleat);
                R2 = poblacion.getRule(aleat2);
                Rule desc1 = new Rule(nAttributes);
                Rule desc2 = new Rule(nAttributes);
                cruza(R1, R2, desc1, desc2);
                poblacion.insertion(desc1);
                poblacion.insertion(desc2);
            }
            Collections.sort(poblacion.getRuleSet()); //Ordering
            if (poblacion.getRule(0).getStrength() <= fuerzaAux) {
                iteraciones++;
            } else {
                iteraciones = 0;
                fuerzaAux = poblacion.getRule(0).getStrength();
            }
        } while (iteraciones <= nbMax);
        r = poblacion.getRule(0);
        return r;
    }

    /**
     * Crossover operator of the algorithm:<br/>
     * <ul>
     * <li> Each offpsring is a copy of its parent</li>
     * <li> From a given crossover probability:</li>
     * <li> For all conditions (i)</li><ul>
     * <li> If the probability is reached -> Exchange condition (i) of each parent to both offspring</li></ul>
     * <li> Copy the class of each offspring</li>
     * <li> Evaluate the new offspring to obtain their strength</li></ul>
     *
     * @param padre1 Parent rule number 1
     * @param padre2 Parent rule number 2
     * @param desc1 One offspring
     * @param desc2 The other offspring
     *
     */
    private void cruza(Rule padre1, Rule padre2, Rule desc1, Rule desc2) {
        for (int i = 0; i < nAttributes; i++) {
            if (Randomize.RandClosed() <= 0.5) {
                desc1.setCondition(padre2.getCondition(i)); //Exchange
                desc2.setCondition(padre1.getCondition(i));
            } else {
                desc1.setCondition(padre1.getCondition(i)); //Leave the same
                desc2.setCondition(padre2.getCondition(i));
            }
        }
        desc1.setClas(padre1.getClas());
        desc2.setClas(padre2.getClas());
        evalua(desc1);
        evalua(desc2);
    }

    /**
     * Creation operator for the genetic algorithm<br/>
     * Here we generalize the initial rule  in order to add a new starting point for the search
     *
     * @param Rinit Initial rule
     * @return A new "offspring" rule generalized from the initial rule
     *
     */
    private Rule crea(Rule Rinit) {
        Rule r = new Rule(nAttributes);
        boolean seguir;
        r.setClas(Rinit.getClas());
        for (int i = 0; i < nAttributes; i++) {
            Condition c = Rinit.getCondition(i);
            if (Randomize.RandClosed() <= 0.5) { //50% probability to generalize each attribute
                switch (c.getType()) {
                case 0:
                    break;
                case 1:
                    c.setType(0); // Ai = value -> *
                    break;
                case 2:
                    seguir = true;
                    int j;
                    for (j = 1; j < lims[i] - 1 && seguir; j++) {
                        if (c.getLowerBound() == bounds[i][j]) {
                            c.setLowerBound(bounds[i][j - 1]);
                            seguir = false;
                        }
                    }
                    if (seguir) {
                        c.setType(0); //Out of bounds ->
                    } else {
                        seguir = true;
                        for (int k = j; k < lims[i] - 1 && seguir; k++) {
                            if (c.getUpperBound() == bounds[i][k]) {
                                c.setUpperBound(bounds[i][k + 1]);
                                seguir = false;
                            }
                        }
                        if (seguir) {
                            c.setType(0); //Out of bounds -> *
                        }
                    }
                    break;
                }
            }
            r.setCondition(c);
        }
        evalua(r);
        return r;
    }

    /**
     * It evaluates a rule to obtain its strength<br/>
     * <b>strength = (c - (alfa * nc) + beta * g) / cSize;</b> where
     * <ul>
     * <li>c -> Number of correct classifications</li>
     * <li>nc -> Number of misclassifications</li>
     * <li>alfa -> parameter that determines the consistency or precision</li>
     * <li>beta -> parameter that beams the search into specific or general rules (-0.001 o +0.001 respec.)</li>
     * <li>g -> Measure of the generality of the rule (number of "*" conditions)</li>
     * <li>cSize -> Number of examples in the data-set with the same class as the consequent of the rule</li></ul>
     *
     * @param r Rule to evaluate
     *
     */
    private void evalua(Rule r) {
        double fuerza;
        int c = 0, nc = 0, g = 0;
        for (int i = 0; i < nAttributes; i++) {
            if (r.getCondition(i).getType() == 0) {
                g++;
            }
        }
        g /= nAttributes;
        for (int i = 0; i < trainData.size(); i++) {
            Instance m = trainData.getData(i);
            if (cubierto(r, m)) {
                if (m.getClas() == r.getClas()) {
                    c++;
                } else {
                    nc++;
                }
            }
        }
        fuerza = (c - (alfa * nc) + beta * g) / instPerClassTr[r.getClas()];
        r.setStrength(fuerza);
    }

    /**
     * It checks if there are more examples to cover
     * @param r New rule
     * @return True if some examples remain uncovered. False in other case
     */
    private boolean cubiertos(Rule r) {
        boolean cubiertos = false;
        int ejDisponiblesAux[] = new int[nDisponibles];
        int nDispAux = 0;
        for (int i = 0; i < nDisponibles; i++) {
            Instance m = trainData.getData(ejemplosDisponibles[i]);
            boolean c = cubierto(r, m);
            if (c) {
                m.setCovered(1);
            } else {
                cubiertos = true;
                ejDisponiblesAux[nDispAux] = ejemplosDisponibles[i];
                nDispAux++;
            }
        }
        for (int i = 0; i < nDispAux; i++) {
            ejemplosDisponibles[i] = ejDisponiblesAux[i];
        }
        nDisponibles = nDispAux;
        //r.print();
        //System.err.println("Todavia me quedan -> "+nDisponibles);
        //System.out.println(nDisponibles+" examples remaining...");
        return cubiertos;
    }

    /**
     * It checks if a rule covers an example<br/>
     * For each condition(rule) / attribute(example)
     * <ul>
     * <li> If conditions is '*' -> covers the attribute </li>
     * <li> If the value of the attribute is missing -> it is only covered if the condition is '*' </li>
     * <li> If the condition is for enumarate values (att = value) -> it is only covered if value(cond) == value(att) </li>
     * <li> If the condition is for integer/real values (B1 &lt;= att &lt;= B2) ->
     * it is only covered if value(att) &lt;= than B1 && &lt;= than B2</li>
     *
     * @param r Rule
     * @param ejemplo Exaple
     * @return True if the rule covers the example. False in other case
     *
     */
    private boolean cubierto(Rule r, Instance ejemplo) {
        boolean cubre = true;
        double[] atts = ejemplo.getMuest();
        for (int i = 0; i < nAttributes && cubre; i++) {
            if ((Double.isNaN(atts[i])) && (r.getCondition(i).getType() != 0)) {
                cubre = false;
            } else {
                switch (r.getCondition(i).getType()) {
                case 0:
                    break;
                case 1: //enumerate
                    if (r.getCondition(i).getValue() != atts[i]) {
                        cubre = false;
                    }
                    break;
                case 2: //interval
                    if ((atts[i] > r.getCondition(i).getUpperBound()) ||
                        (atts[i] < r.getCondition(i).getLowerBound())) {
                        cubre = false;
                    }
                    break;
                }
            }
        }
        return cubre;
    }

    /**
     * It filters a rule set to remove some redundant ones<br>
     * <b>ALGORITHM:</b><br/>
     * <ol>
     * <li> Make strength[i] = 0; for all rules.</li>
     * <li> For each example ex = (e,Cl,w) from Ex do:</li><ol>
     *    <li> Be M the rule set that covers ex and R* the subset of M that have the greatest value of Cq(R*) [strength]</li>
     *    <li> Do strength[i] = strength[i]+w for all rules R from R*</li></ol>
     * <li> Do strength[i] =  (strength[i] / c+nc) for all rules. (c+nc is the total weight of the examples covered by R)</li>
     * <li> Remove each rule R whose strength[i] being lower than the threshold, which is defined by the expert.</li></ol>
     *
     * @param r The rule set
     *
     */
    private void filtrado(ruleSet r) {
        System.out.println("Filtering rules...");
        double[] fuerzas = new double[r.size()];
        int[] nMasNc = new int[r.size()];
        boolean verifica;
        double fuerzaMax = 0, fuerza;
        for (int i = 0; i < trainData.size(); i++) {
            int[] M = new int[r.size()];
            int numero = 0;
            for (int j = 0; j < r.size(); j++) {
                verifica = cubierto(r.getRule(j), trainData.getData(i));
                if (verifica) {
                    M[numero] = j;
                    numero++;
                    nMasNc[j]++;
                }
            }
            int[] Rest = new int[numero];
            int numero2 = 0;
            fuerzaMax = Double.MIN_VALUE;
            for (int j = 0; j < numero; j++) {
                if ((fuerza = r.getRule(M[j]).getStrength()) > fuerzaMax) {
                    fuerzaMax = fuerza;
                    numero2 = 1;
                    Rest[numero2 - 1] = M[j];
                } else {
                    if (fuerza == fuerzaMax) {
                        numero2++;
                        Rest[numero2 - 1] = M[j];
                    }
                }
            }
            for (int j = 0; j < numero2; j++) {
                fuerzas[Rest[j]] += 1;
            }
        } //for examples
        for (int i = 0, j = 0; i < r.size(); i++) {
            fuerzas[i] = fuerzas[i] / (1.0 * nMasNc[i]);
            if (fuerzas[i] < Tstr) {
                r.deleteRule(j);
            } else {
                j++;
            }
        }
    }

    /**
     *  This functions generates the output statistics and prints them into a file
     */
    private void generaSalida() {
        Files f = new Files();
        String cad = "";

        String salidaTr = evReg.salida(evalData, 0);
        String salidaTst = evReg.salida(testData, 1);
        f.writeFile(outputFileTr, myOutput + salidaTr);
        f.writeFile(outputFileTst, myOutput + salidaTst);

        cad += evReg.printString() + "\n@Execution Time:" + (time / 1000) +
                "seconds.\n@Rule Base: " + finalRuleSet.size() +
                " rules\n\n";
        cad += finalRuleSet.printString();

        f.writeFile(outputFile, cad);
        System.out.println(evReg.printString());
    }

}

