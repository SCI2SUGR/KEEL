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
 * @author Written by Cristóbal J. Carmona (University of Jaen) 11/08/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
 
package keel.Algorithms.Subgroup_Discovery.NMEEFSD.NMEEFSD;

import org.core.Files;

public class IndCAN extends Individual {
    /**
     * <p>
     * Defines the individual of the population
     * </p>
     */

    public CromCAN cromosoma;   // Individual contents

    /**
     * <p>
     * Creates new instance of Canonical individual
     * </p>
     */
    public IndCAN(){
    }

    /**
     * </p>
     * Creates new instance of Canonical individual
     * </p>
     * @param lenght          Lenght of the individual
     * @param neje              Number of examples
     * @param nobj              Number of objectives
     */
    public IndCAN(int lenght, int neje, int nobj) {

        tamano = lenght;
        cromosoma = new CromCAN(lenght);
        medidas = new QualityMeasures(nobj);

        evaluado = false;
        cubre = new boolean [neje];

        overallConstraintViolation = 0.0;
        numberOfViolatedConstraints = 0;
        crowdingDistance = 0.0;
        n_eval = 0;

    }


    /**
     * <p>
     * Creates random instance of Canonical individual
     * </p>
     * @param Variables             Variables structure
     * @param neje                  Number of exaples
     * @param nFile                 Fichero to write the individual
     */
    public void RndInitInd(TableVar Variables, int neje, String nFile) {
        cromosoma.RndInitCrom(Variables);  // Random initialization method
        evaluado = false;                  // Individual not evaluated
        for (int i=0; i<neje; i++){
            cubre[i] = false;
        }

        overallConstraintViolation = 0.0;
        numberOfViolatedConstraints = 0;
        crowdingDistance = 0.0;
        n_eval = 0;
    }


    /**
     * <p>
     * Creates biased instance of Canonical individual
     * </p>
     * @param Variables             Variables structure
     * @param porcVar               Percentage of variables to form the individual
     * @param neje                  Number of exaples
     * @param nFile                 Fichero to write the individual
     */
    public void BsdInitInd(TableVar Variables, float porcVar, int neje, String nFile) {

        cromosoma.BsdInitCrom(Variables, porcVar);  // Random initialization method
        evaluado = false;                           // Individual not evaluated
        for (int i=0; i<neje; i++){
            cubre[i] = false;
        }

        overallConstraintViolation = 0.0;
        numberOfViolatedConstraints = 0;
        crowdingDistance = 0.0;
        n_eval = 0;
    }


    /**
     * <p>
     * Creates instance of Canonical individual based on coverage
     * </p>
     * @param pop           Actual population
     * @param Variables     Variables structure
     * @param Examples      Examples structure
     * @param porcCob       Percentage of variables to form the individual
     * @param nobj          Number of objectives
     * @param nFile         Fichero to write the individual
     */
    public void CobInitInd(Population pop, TableVar Variables, TableDat Examples, float porcCob, int nobj, String nFile) {

        cromosoma.CobInitCrom(pop, Variables, Examples, porcCob, nobj);
        evaluado = false;

        for (int i=0; i<Examples.getNEx(); i++){
            cubre[i] = false;
        }

        overallConstraintViolation = 0.0;
        numberOfViolatedConstraints = 0;
        crowdingDistance = 0.0;
        n_eval = 0;
    }


    /**
     * <p>
     * Returns the Chromosome
     * </p>
     * @return              Chromosome
     */
    public CromCAN getIndivCrom () {
        return cromosoma;
    }


    /**
     * <p>
     * Returns the indicated gene of the Chromosome
     * </p>
     * @param pos               Position of the gene
     * @return                  Value of the gene
     */
    public int getCromElem (int pos) {
        return cromosoma.getCromElem (pos);
    }


    /**
     * <p>
     * Returns the value of the indicated gene for the variable
     * </p>
     * @param pos               Position of the variable
     * @param elem              Position of the gene
     * @return                  Value of the gene
     */
    public boolean getCromGeneElem(int pos, int elem){
        return false;
    }


    /**
     * <p>
     * Sets the value of the indicated gene of the Chromosome
     * </p>
     * @param pos               Position of the variable
     * @param val               Value of the variable
     */
    public void setCromElem (int pos, int val) {
        cromosoma.setCromElem(pos, val);
    }

    /**
     * <p>
     * Sets the value of the indicated gene of the Chromosome
     * </p>
     * @param pos               Position of the variable
     * @param elem              Position of the gene
     * @param val               Value of the variable
     */
    public void setCromGeneElem (int pos, int elem, boolean val){}


    /**
     * <p>
     * Returns the indicated Chromosome
     * </p>
     * @return                  The canonical Chromosome
     */
    public CromCAN getIndivCromCAN(){
        return cromosoma;
    }


    /**
     * <p>
     * Returns the indicated Chromosome
     * </p>
     * @return                  The DNF Chromosome
     */
    public CromDNF getIndivCromDNF(){
        return null;
    }

    /**
     * <p>
     * Copy the indicaded individual in "this" individual
     * </p>
     * @param a              The individual to Copy
     * @param neje              Number of examples
     * @param nobj              Number of objectives
     */
    public void copyIndiv (Individual a, int neje, int nobj) {
        for (int i=0;i<this.tamano;i++)
            this.setCromElem(i, a.getCromElem(i));

        this.setIndivEvaluated(a.getIndivEvaluated());
        for (int i=0;i<neje;i++)
           this.cubre[i] = a.cubre[i];

        this.setCrowdingDistance(a.getCrowdingDistance());
        this.setNumberViolatedConstraints(a.getNumberViolatedConstraints());
        this.setOverallConstraintViolation(a.getOverallConstraintViolation());
        this.setRank(a.getRank());
        this.setNEval(a.getNEval());

        //Copy quality measures
        for(int i=0; i<nobj; i++){
            this.setMeasureValue(i, a.getMeasureValue(i));
        }
        this.setCnfValue(a.getCnfValue());
    }


    /**
     * <p>
     * Evaluate a individual. This function evaluates an individual.
     * </p>
     * @param AG                Genetic algorithm
     * @param Variables         Variables structure
     * @param Examples          Examples structure
     */
    public void evalInd (Genetic AG, TableVar Variables, TableDat Examples) {

        int ejCompAntFuzzy=0;                // Number of compatible examples with the antecedent of any class - fuzzy version --- unused
        int ejCompAntCrisp=0;                // Number of compatible examples with the antecedent of any class - crisp version
        int ejCompAntClassFuzzy=0;           // Number of compatible examples (antecedent and class) - fuzzy version
        int ejCompAntClassCrisp=0;           // Number of compatible examples (antecedent and class) - crisp version
        int ejCompAntClassNewFuzzy=0;        // Number of new covered compatible examples (antec and class) - fuzzy version
        int ejCompAntClassNewCrisp=0;        // Number of new covered compatible examples (antec and class) - crisp version

        float gradoCompAntFuzzy=0;           // Total compatibility degree with the antecedent - fuzzy version
        float gradoCompAntClassFuzzy=0;      // Tot compatibility degree with antecedent and class - fuzzy version
        float gradoCompAntClassNewEjFuzzy=0; // Tot compatibility degree with antecedent and class of new covered examples - fuzzy version

        float disparoFuzzy;    // Final compatibility degree of the example with the individual - fuzzy version
        float disparoCrisp;    // Final compatibility or not of the example with the individual - crisp version

        float completitud, fsupport, csupport, confianza, cconfianza;
        float coverage, unusualness, accuracy, significance;

        float valorConf, valorComp;   // Variables to store de selected measures

        int ejClase[] = new int[Variables.getNClass()];
        int cubreClase[] = new int[Variables.getNClass()];
        for (int i=0; i<Variables.getNClass(); i++) {
            cubreClase[i]=0;
            ejClase[i] = Examples.getExamplesClass (i);
        }

        //int por_cubrir;        // Number of examples of the class not covered yet - for fuzzy version

        int numVarNoInterv=0;  // Number of variables not taking part in the individual


        for (int i=0; i<Examples.getNEx(); i++) { // For each example of the dataset
            // Initialization
            disparoFuzzy = 1;
            disparoCrisp = 1;
            numVarNoInterv = 0;

            // Compute all chromosome values
            for (int j=0; j<Variables.getNVars(); j++) {
                if (!Variables.getContinuous(j)) {  // Discrete Variable
                    if (cromosoma.getCromElem(j)<=Variables.getMax(j)){
                        // Variable j takes part in the rule
                        if ((Examples.getDat(i,j) != cromosoma.getCromElem(j)) && (!Examples.getLost(Variables,i,j))) {
                            // If chromosome value <> example value, and example value is not a lost value
                            disparoFuzzy = 0;    
                            disparoCrisp = 0;    
                        }
                    }
                    else
                        numVarNoInterv++;  // Variable does not take part
                }
                else {	// Continuous variable
                    if (cromosoma.getCromElem(j)<Variables.getNLabelVar(j)) {
                        // Variable takes part in the rule
                        // Fuzzy computation
                        if (!Examples.getLost(Variables,i,j)) {
                            // If the value is not a lost value
                            float pertenencia = Variables.Fuzzy(j, cromosoma.getCromElem(j), Examples.getDat(i,j));
                            disparoFuzzy = Utils.Minimum (disparoFuzzy, pertenencia);
                        }
                        // Crisp computation
                        if (!Examples.getLost(Variables,i,j))
                            if (NumInterv (Examples.getDat(i,j),j, Variables)!= cromosoma.getCromElem(j))
                                disparoCrisp = 0;
                    }
                    else
                        numVarNoInterv++;  // Variable does not take part
                }
            } // End FOR all chromosome values

            // Update counters and mark example if needed
            gradoCompAntFuzzy += disparoFuzzy;
            if (disparoFuzzy>0) {
            	ejCompAntFuzzy++;
                if (Examples.getClass(i) == Variables.getNumClassObj()) {
                    gradoCompAntClassFuzzy +=disparoFuzzy;
                    ejCompAntClassFuzzy ++;
                }
                if ((!Examples.getCovered(i)) &&  (Examples.getClass(i) == Variables.getNumClassObj())) {
                    // If example was not previusly covered and belongs to the target class increments the number of covered examples
                    ejCompAntClassNewFuzzy++;
                    gradoCompAntClassNewEjFuzzy += disparoFuzzy;
                    cubre[i]=true;
                }
            }
            if (disparoCrisp>0) {
                ejCompAntCrisp++;
                if (Examples.getClass(i) == Variables.getNumClassObj()) {
                    ejCompAntClassCrisp ++;
                }
                cubreClase[Examples.getClass(i)]++;
                if ((!Examples.getCovered(i)) &&  (Examples.getClass(i) == Variables.getNumClassObj())) {
                    // If example was not previusly covered and belongs to the target class increments the number of covered examples */
                    ejCompAntClassNewCrisp++;
                    cubre[i]=true;
                }
            }

        } // End of cycle for each example


        // Compute the measures

        for(int i=0; i<AG.getNumObjectives(); i++){

            if(AG.getNObjectives(i).compareTo("COMP")==0){
                if (Examples.getExamplesClassObj() != 0)
                    completitud = ((float)ejCompAntClassFuzzy/Examples.getExamplesClassObj());
                else
                    completitud = 0;
                valorComp = completitud;
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(i, 0);
                else medidas.setObjectiveValue(i, valorComp);
            }
            if(AG.getNObjectives(i).compareTo("CSUP")==0){
                csupport = ((float)ejCompAntClassCrisp/Examples.getNEx());

                // store the values
                valorComp = csupport;
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(i, 0);
                else medidas.setObjectiveValue(i, valorComp);
            }
            if(AG.getNObjectives(i).compareTo("FSUP")==0){
                if (Examples.getNEx() != 0)
                    fsupport = ((float)gradoCompAntClassFuzzy/Examples.getNEx());
                else
                    fsupport = 0;
                valorComp = fsupport;
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(i, 0);
                else medidas.setObjectiveValue(i, valorComp);
            }
            if(AG.getNObjectives(i).compareTo("CCNF")==0){
                if (ejCompAntCrisp != 0)
                    cconfianza = (float)ejCompAntClassCrisp/ejCompAntCrisp;
                else
                    cconfianza = 0;
                valorConf = cconfianza;
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(i, 0);
                else medidas.setObjectiveValue(i, valorConf);
            }
            if(AG.getNObjectives(i).compareTo("FCNF")==0){
                if (gradoCompAntFuzzy != 0)
                    confianza = (float)gradoCompAntClassFuzzy/gradoCompAntFuzzy;
                else
                    confianza = 0;
                valorConf = confianza;
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(i, 0);
                else medidas.setObjectiveValue(i, valorConf);
            }
            if(AG.getNObjectives(i).compareTo("UNUS")==0){
                coverage = ((float)ejCompAntCrisp/Examples.getNEx());
                if (ejCompAntCrisp==0)
                    unusualness = 0;
                else
                    unusualness =  coverage * ( (float)ejCompAntClassCrisp/ejCompAntCrisp - (float)Examples.getExamplesClassObj()/Examples.getNEx());

                float normUnus = unusualness + (float)Examples.getExamplesClassObj()/Examples.getNEx();
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(i, 0);
                else medidas.setObjectiveValue(i, normUnus);
            }
            if(AG.getNObjectives(i).compareTo("SIGN")==0){
                coverage = ((float)ejCompAntCrisp/Examples.getNEx());
                float sumaSignClase=0;
                for (int aux=0; aux<Variables.getNClass(); aux++) {
                    if (cubreClase[aux]!=0)
                        sumaSignClase += cubreClase[aux] * Math.log10 ((float)cubreClase[aux]/(ejClase[aux]*coverage));
                }
                significance = 2 * sumaSignClase;
                float maxSignif=0;
                for (int a=0; a<Variables.getNClass(); a++) {
                    if (cubreClase[a]!=0 && coverage!=0)  
                        maxSignif += cubreClase[a] * Math.log10 ((float)1/coverage);
                }
                maxSignif = 2*maxSignif ; 
                float normSignif=0;
                if (maxSignif!=0)
                   normSignif = significance/maxSignif;
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(i, 0);
                else medidas.setObjectiveValue(i, normSignif);
            }
            if(AG.getNObjectives(i).compareTo("ACCU")==0){
                accuracy = (float)(ejCompAntClassCrisp+1) / (ejCompAntCrisp + Variables.getNClass());
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(i, 0);
                else medidas.setObjectiveValue(i, accuracy);
            }
            if(AG.getNObjectives(i).compareTo("COVE")==0){
                coverage = ((float)ejCompAntCrisp/Examples.getNEx());
                if (numVarNoInterv >= Variables.getNVars())
                    medidas.setObjectiveValue(i, 0);
                else medidas.setObjectiveValue(i, coverage);
            }

        }

        if (gradoCompAntFuzzy != 0)
            confianza = (float)gradoCompAntClassFuzzy/gradoCompAntFuzzy;
        else
            confianza = 0;
        valorConf = confianza;
        medidas.setCnf(valorConf);

        // Set the individual as evaluated
        evaluado = true;

    }

    /**
     * <p>
     * Returns the number of the interval of the indicated variable to which belongs
     * the value. It is performed seeking the greater belonging degree of the
     * value to the fuzzy sets defined for the variable
     * </p>
     * @param value                 Value to calculate
     * @param num_var               Number of the variable
     * @param Variables             Variables structure
     * @return                      Number of the interval
     */
    public int NumInterv (float value, int num_var, TableVar Variables) {
        float pertenencia=0, new_pert=0;
        int interv = -1;

        for (int i=0; i<Variables.getNLabelVar(num_var); i++) {
            new_pert = Variables.Fuzzy(num_var, i, value);
            if (new_pert>pertenencia) {
                interv = i;
                pertenencia = new_pert;
            }
        }
        return interv;

    }


    /**
     * <p>
     * Method to Print the contents of the individual
     * </p>
     * @param nFile             Fichero to write the individual
     */
    public void Print(String nFile) {
        String contents;
        cromosoma.Print(nFile);

        contents = "DistanceCrowding "+ this.getCrowdingDistance()+ "\n";
        contents+= "Evaluated - " + evaluado + "\n";
        contents+= "Evaluación Generado " + n_eval + "\n\n";
        if (nFile=="")
            System.out.print (contents);
        else
           Files.addToFile(nFile, contents);
    }


}
