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
 * @author Written by Alberto Fernandez (University of Granada) 01/02/2006
 * @author Modified by Nicola Flugy Papa (Politecnico di Milano) 24/03/2009
 * @author Modified by Cristobal J. Carmona (University of Jaen) 10/07/2010
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDMap.SDMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;
import java.text.DecimalFormat;

import keel.Dataset.*;
import org.core.Files;

public class FPgrowth {
	/**
	 * <p>
	 * It gathers all the parameters, launches the algorithm, and prints out the results
	 * </p>
	 */

    private myDataset trans;
    private myDataset test;
    
    private String rulesFilename;
    private String valuesFilename;
    private String outputTra;
    private String outputTst;
    private FPgrowthProcess proc;
    private ArrayList<AssociationRule> associationRules;
	

    private int nPartitionForNumericAttributes = 1;
    private double minSupport;
    private double minConfidence;

    private int rulesReturn;
    
    private boolean somethingWrong = false;

    private Vector q;

    /**
     * <p>
     * Default constructor
     * </p>
     */
    public FPgrowth() {
    }

    /**
     * <p>
     * It reads the data from the input files and parse all the parameters
     * from the parameters array.
     * </p>
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public FPgrowth(parseParameters parameters) {
    	
        this.rulesFilename = parameters.getAssociationRulesFile();
        this.valuesFilename = parameters.getAssociationMeasuresFile();
        this.outputTra = parameters.getOutputFileTra();
        this.outputTst = parameters.getOutputFileTst();
        
        try {
            System.out.println("\nReading the transaction set: " + parameters.getTransactionsInputFile());

            this.trans = new myDataset(this.nPartitionForNumericAttributes);
            this.trans.readDataSet(parameters.getTransactionsInputFile(),true);

            System.out.println("\nReading the test set: " + parameters.getTestInputFile());

            this.test = new myDataset(this.nPartitionForNumericAttributes);
            this.test.readDataSet(parameters.getTestInputFile(),false);
        }
        catch (IOException e) {
            System.err.println("There was a problem while reading the input transaction set: " + e);
            somethingWrong = true;
        }

        this.minSupport = Double.parseDouble(parameters.getParameter(0));
        this.minConfidence = Double.parseDouble(parameters.getParameter(1));
        this.rulesReturn = Integer.parseInt(parameters.getParameter(2));
    }

    /**
     * <p>
     * Checks if the dataset has continuous variables
     * </p>
     * @return                  boolean: True (There are continuous variables) and False in another case
     */
    public boolean isContinuous(){
        boolean centi = false;
        for(int i=0; i<this.trans.getnVars()-1; i++){
            if(this.trans.getAttributeType(i)==2){
                centi = true;
            }
        }

        return centi;

    }



    /**
     * <p>
     * It launches the algorithm
     * </p>
     */
    public void execute() {
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } else {

            System.out.println("Executing Algorithm.");

            this.proc = new FPgrowthProcess(this.trans, this.minSupport, this.minConfidence);
            this.proc.run();
            this.associationRules = this.proc.generateRulesSet();

            //Calculate the function of quality for the subgroups
            q = new Vector();
            for(int i = 0; i<this.associationRules.size(); i++){
                associationRules.get(i).deleteDistrib();
                CalculateQ(i);
                associationRules.get(i).adjustDistrib();
            }

            //Delete the rules with respect to the RulesReturn param
            deleteAssociationRules();

            //Generates the exit files
            exitResult();

            //Generates the rule file
            writeRules();
            //Generates the measure file
            writeMeasures();

            System.out.println("\nAlgorithm Finished");

        }
    }
    
    private void createRule(int fake_value, double[] step_values, PrintWriter w) {
    	int id_attr, true_value;
    	
    	id_attr = fake_value % trans.getnVars();
		true_value = (fake_value - id_attr) / trans.getnVars();
		
		w.print(trans.getAttributeName(id_attr) + " = ");
		
		if (trans.getAttributeType(id_attr) == myDataset.NOMINAL) w.print( trans.getNominalValue(id_attr, true_value) );
		else w.print("[" + (this.trans.getMin(id_attr) + step_values[id_attr] * true_value) + ", " + (this.trans.getMin(id_attr) + step_values[id_attr] * (true_value + 1)) + "]");
    }

    private void CalculateQ(int rule){

        float disparo;
        float tp = 0;
        float fp = 0;
        float TP = 0;
        float FP = 0;
        float TPm = 0;
        float FPm = 0;
        double quality = 0;
        double ejAnt = 0;
        double ejAntCon = 0;
        double ejCon = 0;

        short[] terms_ant;
        short[] terms_con;
        ArrayList<Integer> id_attr_values = this.trans.getIDsOfAllAttributeValues();
        terms_ant = this.associationRules.get(rule).getAntecedent();
        terms_con = this.associationRules.get(rule).getConsequent();

        int fake_value_cons = id_attr_values.get(terms_con[0] - 1);
        int id_attr_cons = fake_value_cons % trans.getnVars();
        int true_value_cons = (fake_value_cons - id_attr_cons) / trans.getnVars();

        //Calculamos los valores para la regla

        for(int i=0; i<this.trans.getnTrans(); i++){
        //PARA CADA EJEMPLO
            disparo = 1;

            for (int j=0; j < terms_ant.length; j++){
                //PARA LAS VARIABLES DEL ANTECEDENTE
                int fake_value = id_attr_values.get(terms_ant[j] - 1);
                int id_attr = fake_value % trans.getnVars();
                int true_value = (fake_value - id_attr) / trans.getnVars();
                if(true_value != trans.getValTrueTransactions(i,id_attr)){
                    // Variable id_attr does not take part in the rule
                    disparo = 0;
                }
            }
            if(disparo==1){
                ejAnt++;
                TP++;
                associationRules.get(rule).incrementDistrib((int)trans.getValTrueTransactions(i,trans.getnVars()-1));
                if(true_value_cons == trans.getValTrueTransactions(i,trans.getnVars()-1)){
                    ejAntCon++;
                    tp++;
                } else fp++;
            } else FP++;

            if(true_value_cons == trans.getValTrueTransactions(i,trans.getnVars()-1)){
                ejCon++;
            }
        }

        //FIN
        double N = trans.getnTrans();
        double first = (ejAntCon/ejCon)-(ejAntCon/N)*Math.sqrt(ejCon);
        double second = Math.sqrt((ejAntCon/N)*(1-(ejAntCon/N)));
        double third = Math.sqrt(N/(N-ejCon));
        quality = (first/second)*third;

        //INCLUIR LAS MEDIDAS EN LA ESTRUCTURA
        QualitySubgroup med = new QualitySubgroup(tp,fp,TP,FP,quality);
        this.q.add(rule, med);

//        System.out.println("TP:"+TP+" | FP:"+FP);
//        System.out.println("tp:"+tp+" | fp:"+fp);
//        System.out.println("ejAnt:"+ejAnt+" | ejAntCon:"+ejAntCon+" | ejCon:"+ejCon);
//        System.out.println("  ");

    }

    private void deleteAssociationRules(){
        if(this.rulesReturn < this.associationRules.size()){
            double[] ordenado = new double[this.associationRules.size()];
            int izq = 0;
            int der = this.associationRules.size()-1;
            int indices[] = new int[this.associationRules.size()];
            for(int i=0; i<this.associationRules.size(); i++){
                indices[i] = i;
                QualitySubgroup aux = (QualitySubgroup) q.get(i);
                ordenado[i] = aux.get_q();
            }
            OrDecIndex(ordenado, izq, der, indices);

            Vector aux = new Vector();
            for(int i=0; i<this.rulesReturn; i++){
                aux.add(q.get(indices[i]));
            }
            q.removeAllElements();
            for(int i=0; i<this.rulesReturn; i++){
                q.add(aux.get(i));
            }

            ArrayList<AssociationRule> l_aux = new ArrayList();
            for (int i=0; i<this.rulesReturn; i++){
                AssociationRule auxiliar = new AssociationRule(associationRules.get(indices[i]).getAntecedent(),
                        associationRules.get(indices[i]).getConsequent(),
                        associationRules.get(indices[i]).getRuleSupport(),
                        associationRules.get(indices[i]).getAntecedentSupport(),
                        associationRules.get(indices[i]).getConfidence(),
                        trans.getNValOutput(),
                        associationRules.get(indices[i]).getDistribEx());
                l_aux.add(auxiliar);
            }
            associationRules.clear();
            for (int i=0; i<this.rulesReturn; i++){
                AssociationRule auxiliar = new AssociationRule(l_aux.get(i).getAntecedent(),
                        l_aux.get(i).getConsequent(),
                        l_aux.get(i).getRuleSupport(),
                        l_aux.get(i).getAntecedentSupport(),
                        l_aux.get(i).getConfidence(),
                        trans.getNValOutput(),
                        l_aux.get(i).getDistribEx());
                associationRules.add(auxiliar);
            }


        }
    }

    private void exitResult(){
        Files.writeFile(outputTra, this.trans.header() + "@data\n" + exitResultTran());
        Files.writeFile(outputTst, this.trans.header() + "@data\n" + exitResultTst());
    }

    private void writeRules(){
        try {
            int r, i;
            short[] terms;
            AssociationRule a_r;

            double[] step_values = this.trans.getSteps();
            ArrayList<Integer> id_attr_values = this.trans.getIDsOfAllAttributeValues();

            PrintWriter rules_writer = new PrintWriter(this.rulesFilename);

            for (r=0; r < this.associationRules.size(); r++) {
                a_r = this.associationRules.get(r);

                rules_writer.print("Rule "+ r+": ");

                terms = a_r.getAntecedent();

                for (i=0; i < terms.length-1; i++){
                    this.createRule(id_attr_values.get(terms[i] - 1), step_values, rules_writer);
                    rules_writer.print(" AND ");
                }
                this.createRule(id_attr_values.get(terms[i] - 1), step_values, rules_writer);


                rules_writer.print(" THEN ");
                terms = a_r.getConsequent();

                for (i=0; i < terms.length; i++)
                        this.createRule(id_attr_values.get(terms[i] - 1), step_values, rules_writer);

                rules_writer.print(associationRules.get(r).printDistribucionString());
                rules_writer.print("\n\n");
            }

            rules_writer.close();

        }
        catch (FileNotFoundException e) {
                e.printStackTrace();
        }
    }

    private void writeMeasures(){

        try {
            PrintWriter measure_writer = new PrintWriter(this.valuesFilename);
            measure_writer.println("#Rule\t#Vars\tCov\tSign\tUnus\tSupp\tCnf");

        double VAR = 0,COV = 0,SIG = 0,UNU = 0,SUP = 0,CNF = 0;
        DecimalFormat d;

        for(int rule=0; rule<associationRules.size(); rule++){

            int j;

            int nDatos = test.getnTrans();

            int[] contClases = new int[test.getNValOutput()];
            for (int i = 0; i < test.getnTrans(); i++) {
                contClases[(int) test.getValTrueTransactions(i,test.getnVars()-1)]++;
            }

            int tam = associationRules.size(); // Calculate Tam

            // Number of attributes
            double ant=0;
            ant += associationRules.get(rule).getAntecedent().length;
            ant += associationRules.get(rule).getConsequent().length;

            VAR += (double) ant; //Nº attributes per rule

            short[] terms_cons;
            ArrayList<Integer> id_attr_values = this.test.getIDsOfAllAttributeValues();
            terms_cons = this.associationRules.get(rule).getConsequent();
            int fake_value_cons = id_attr_values.get(terms_cons[0] - 1);
            int id_attr_cons = fake_value_cons % trans.getnVars();
            int true_value_cons = (fake_value_cons - id_attr_cons) / trans.getnVars();

            // Calculate the distrib
            double muestCubiertas = 0; //Number of covered examples
            int muestBienCubiertas = 0;
            int[] instCubiertas = new int[test.getNValOutput()];

            for (j = 0; j < test.getNValOutput(); j++) {
                instCubiertas[j] = 0;
            }

            muestCubiertas = 0;

            for (j = 0; j < nDatos; j++) {
                int clas_ej = cover(j,rule,false);
                if (clas_ej!=-1){
                    muestCubiertas++;
                    instCubiertas[clas_ej]++;
                    if (clas_ej == true_value_cons) {
                        muestBienCubiertas++;
                    }
                }
            }

            //Calculate coverage
            double cob = (double) muestCubiertas / (tam*nDatos);
            COV += cob;

            //Calculate support
            double compl = (double) muestBienCubiertas / nDatos;
            SUP += compl;

            //Calculate confidence
            double conf = 0;
            if (muestCubiertas!=0){
                conf = (double) muestBienCubiertas / muestCubiertas;
                CNF += conf;
            }

            //Calculate unusualness
            double ati = 0;
            double val;

            double n, ncond, nclascond, nclas;
            int cl;
            val = 0;

            n = 0;
            ncond = 0;
            nclascond = 0;
            nclas = 0;

            for (j = 0; j < test.getnTrans(); j++) {
                cl = (int) test.getValTrueTransactions(j, test.getnVars()-1);
                n++;

                if (cover(j,rule,false)!=-1) {
                    associationRules.get(rule).incrementDistrib(cl);
                    ncond++;
                    if (cl == true_value_cons) {
                        nclascond++;
                    }
                }
                if (cl == true_value_cons) {
                    nclas++;
                }
            }
            if (n != 0 && ncond != 0) {
                ati = (ncond / n) * ((nclascond / ncond) - (nclas / n));
            } else {
                ati = Double.MIN_VALUE;
            }

            UNU += ati;


            //Calculate significance
            double sigParcial = 0;
            double pCondi;

            pCondi = 0;
            for (j = 0; j < test.getNValOutput(); j++) {
                pCondi += instCubiertas[j];
            }
            pCondi *= (double) 1.0 / nDatos;

            double rel = 0;
            sigParcial = 0;
            for (j = 0; j < test.getNValOutput(); j++) {
                double logaritmo = (double) instCubiertas[j] /
                                   (contClases[j] * pCondi);
                if ((logaritmo != 0)&&(!Double.isNaN(logaritmo))&&(!Double.isInfinite(logaritmo))){
                    logaritmo = Math.log(logaritmo);
                    logaritmo *= (double) instCubiertas[j];
                    sigParcial += logaritmo;
                }
            }
            rel = sigParcial * 2;
            SIG += rel;

            d = new DecimalFormat("0.000");

            measure_writer.println(rule+"\t"+d.format(ant)+"\t"+d.format(cob)+"\t"+d.format(rel)+"\t"+d.format(ati)+"\t"+d.format(compl)+"\t"+d.format(conf));
        }

            VAR /= associationRules.size();
            COV /= associationRules.size();
            SIG /= associationRules.size();
            UNU /= associationRules.size();
            SUP /= associationRules.size();
            CNF /= associationRules.size();

            d = new DecimalFormat("0.000");

            measure_writer.println("-\t"+d.format(VAR)+"\t"+d.format(COV)+"\t"+d.format(SIG)+"\t"+d.format(UNU)+"\t"+d.format(SUP)+"\t"+d.format(CNF));
            measure_writer.close();

            System.out.println("Average results:\n\n" +
                    "#Rules: "+associationRules.size()+"\n"+
                    "#Variables: "+d.format(VAR)+"\n"+
                    "Coverage: "+d.format(COV)+"\n"+
                    "Significance: "+d.format(SIG)+"\n"+
                    "Unusualness: "+d.format(UNU)+"\n"+
                    "Support: "+d.format(SUP)+"\n"+
                    "Confidence: "+d.format(CNF)+"\n");

        }
        catch (FileNotFoundException e) {
                e.printStackTrace();
        }


    }

    private String exitResultTran( ) {

        String cadena = new String("");
        double voto[] = new double[trans.getNValOutput()];
        double clases[] = new double[trans.getNValOutput()];
        double max;
        int j, cl, clasePorDefecto = 0;
        for (int i = 0; i < trans.getnTrans(); i++) {
            clases[(int) trans.getValTrueTransactions(i, trans.getnVars()-1)]++;
        }
        for (int i = 0, clase = -1; i < trans.getNValOutput(); i++) {
            if (clases[i] > clase) {
                clasePorDefecto = i;
                clase = (int) clases[i];
            }
        }
        int clas=0;
        for (int i = 0; i < trans.getnTrans(); i++) {
            for (j = 0; j < trans.getNValOutput(); j++) {
                voto[j] = 0;
            }
            for (j = 0; j < associationRules.size(); j++) {
                clas = cover(i,j,true);
                if (clas!=-1){
                    voto[clas]++;
                } else voto[clasePorDefecto]++;
            }
            for (j = 0, max = 0, cl = 0; j < trans.getNValOutput(); j++) {
                if (voto[j] > max) {
                    max = voto[j];
                    cl = j;
                }
            }
            if (max == 0) {
                cl = clasePorDefecto;
            }

            cadena += new String(Attributes.getOutputAttribute(0).getNominalValue((int) trans.getValTrueTransactions(i,trans.getnVars()-1)) +
                                 " " + Attributes.getOutputAttribute(0).getNominalValue(cl) + "\n");
        }
        return cadena;
    }

    private String exitResultTst( ) {

        String cadena = new String("");
        double voto[] = new double[test.getNValOutput()];
        double clases[] = new double[test.getNValOutput()];
        double max;
        int j, cl, clasePorDefecto = 0;
        for (int i = 0; i < test.getnTrans(); i++) {
            clases[(int) test.getValTrueTransactions(i, test.getnVars()-1)]++;
        }
        for (int i = 0, clase = -1; i < test.getNValOutput(); i++) {
            if (clases[i] > clase) {
                clasePorDefecto = i;
                clase = (int) clases[i];
            }
        }
        int clas=0;
        for (int i = 0; i < test.getnTrans(); i++) {
            for (j = 0; j < test.getNValOutput(); j++) {
                voto[j] = 0;
            }
            for (j = 0; j < associationRules.size(); j++) {
                clas = cover(i,j,false);
                if (clas!=-1){
                    voto[clas]++;
                }
            }
            for (j = 0, max = 0, cl = 0; j < test.getNValOutput(); j++) {
                if (voto[j] > max) {
                    max = voto[j];
                    cl = j;
                }
            }
            if (max == 0) {
                cl = clasePorDefecto;
            }
            cadena += new String(Attributes.getOutputAttribute(0).getNominalValue((int) test.getValTrueTransactions(i,trans.getnVars()-1)) +
                                 " " + Attributes.getOutputAttribute(0).getNominalValue(cl) + "\n");
        }
        return cadena;
    }

    private int cover(int ex, int rule, boolean isTrain){

        int disparo = 0;

        short[] terms_ant;
        ArrayList<Integer> id_attr_values = this.trans.getIDsOfAllAttributeValues();
        terms_ant = this.associationRules.get(rule).getAntecedent();

        for (int j=0; j < terms_ant.length; j++){
            //PARA LAS VARIABLES DEL ANTECEDENTE
            int fake_value = id_attr_values.get(terms_ant[j] - 1);
            int id_attr = fake_value % trans.getnVars();
            int true_value = (fake_value - id_attr) / trans.getnVars();
            if(isTrain){
                if(true_value != trans.getValTrueTransactions(ex,id_attr)){
                    // Variable id_attr takes part in the rule
                    disparo = -1;
                }
            } else {
                if(true_value != test.getValTrueTransactions(ex,id_attr)){
                    // Variable id_attr takes part in the rule
                    disparo = -1;
                }
            }
        }

        if(isTrain){
            if(disparo!=-1){
                return (int) trans.getValTrueTransactions(ex, trans.getnVars()-1);
            }
        } else {
            if(disparo!=-1){
                return (int) test.getValTrueTransactions(ex, trans.getnVars()-1);
            }
        }

        return disparo;

    }

    private void OrDecIndex (double v[], int left, int right, int index[])  {
        int i,j,aux;
        double x,y;

        i = left;
        j = right;
        x = v[(left+right)/2];
        do {
            while (v[i]>x && i<right)
                i++;
            while (x>v[j] && j>left)
                j--;
            if (i<=j) {
                y = v[i];
                v[i] = v[j];
                v[j] = y;
                aux = index[i];
                index[i] = index[j];
                index[j] = aux;
                i++;
                j--;
            }
        } while(i<=j);
        if (left<j)
            OrDecIndex (v,left,j,index);
        if (i<right)
            OrDecIndex (v,i,right,index);

    }
    
}
