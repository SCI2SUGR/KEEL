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
 * @author Writed by Pedro González (University of Jaen) 15/02/2004
 * @author Modified by Pedro González (University of Jaen) 4/08/2007
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 20/04/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDIGA.SDIGA;

import org.core.*;

public class IndDNF  extends Individual{
    /**
     * <p>
     * Defines an individual composed by a DNF cromosome.
     * </p>
     */
      
    public CromDNF cromosoma;     // Individual contents

    /**
     * <p>
     * Creates new instance of IndDNF
     * </p>
     * @param length              Length of the individual
     * @param Variables             Structure of the variables
     */
    public IndDNF(int length, TableVar Variables) {
      tamano = length;
      cromosoma = new CromDNF(length, Variables);
      medidas = new QualityMeasures();
      evaluado = false;
    }


    /**
     * <p>
     * Creates random instance of DNF individual
     * </p>
     * @param Variables             Variables structure
     */
    public void RndInitInd(TableVar Variables) {
      cromosoma.initCrom();           // Random initialization method
      evaluado = false;               // Individual not evaluated
    }


    /**
     * <p>
     * Returns the Chromosome
     * </p>
     * @return              Chromosome
     */
    public CromDNF getIndivCromDNF () {
        return cromosoma;
    }

    /**
     * <p>
     * Returns the Chromosome
     * </p>
     * @return              Chromosome
     */
    public CromCAN getIndivCromCAN () {
        return null;
    }

    /**
     * <p>
     * Returns the indicated gene of the Chromosome
     * </p>
     * @param pos               Position of the gene
     * @return                  Value of the gene
     */
    public int getCromElem (int pos) {
        return 0;
    }

    /**
     * <p>
     * Returns the value of the indicated gene for the variable
     * </p>
     * @param pos               Position of the variable
     * @param elem              Position of the gene
     * @return                  Value of the gene
     */
    public int getCromElemGene (int pos, int elem) {
        return cromosoma.getCromElemGene (pos, elem);
    }

    /**
     * <p>
     * Sets the value of the indicated gene of the Chromosome
     * </p>
     * @param pos               Position of the variable
     * @param val               Value of the variable
     */
    public void setCromElem (int pos, int val) {
    }

    /**
     * <p>
     * Sets the value of the indicated gene of the Chromosome
     * </p>
     * @param pos               Position of the variable
     * @param elem              Position of the gene
     * @param val               Value of the variable
     */
    public void setCromElemGene (int pos, int elem, int val) {
        cromosoma.setCromElemGene(pos, elem, val);
    }

    /**
     * <p>
     * Evaluate a individual. This function evaluates an individual.
     * </p>
     * @param AG                Genetic algorithm
     * @param Variables         Variables structure
     * @param Examples          Ejemplos structure
     * @param marcar            Indicates to mark the covered examples
     */
    public void evalInd (Genetic AG, TableVar Variables, TableDat Examples, boolean marcar) {

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
        float disparoCrisp;    // Final compatibility degree of the example with the individual - crisp version

        float completitud, lcompletitud, fsupport, lfsupport, csupport, lcsupport, confianza, cconfianza;
        float coverage, unusualness, accuracy, significance;
        float valorConf, valorComp, valorlComp;   // Variables to store de selected measures

        // For the significance computation we need the number of examples and the number of examples covered of each class
        int ejClase[] = new int[Variables.getNClass()];
        int cubreClase[] = new int[Variables.getNClass()];
        for (int i=0; i<Variables.getNClass(); i++) {
            cubreClase[i]=0;
            ejClase[i] = Examples.getExamplesClass (i);
        }

        int por_cubrir;        // Number of examples of the class not covered yet - for fuzzy version

        int numVarNoInterv=0;  // Number of variables not taking part in the individual


        for (int i=0; i<Examples.getNEx(); i++) { // For each example of the dataset
            // Initialization
            disparoFuzzy = 1;
            disparoCrisp = 1;
            numVarNoInterv = 0;

            // Compute all chromosome values
            for (int j=0; j<Variables.getNVars(); j++) {
                if (!Variables.getContinuous(j)) {
                    // Discrete Variable
                    if (cromosoma.getCromElemGene(j,Variables.getNLabelVar(j))==1){
                        // Variable j takes part in the rule
                        if ((cromosoma.getCromElemGene(j,(int)Examples.getDat(i,j))==0) && (!Examples.getLost(Variables,i,j))) {
                            disparoFuzzy = 0;     // not compatible
                            disparoCrisp = 0;     // not compatible
                        }
                    }
                    else
                        numVarNoInterv++;  
                        // Variable does not take part
                }
                else {	
                    // Continuous variable
                    if (cromosoma.getCromElemGene(j,Variables.getNLabelVar(j))==1){
                        // Variable takes part in the rule
                        // Fuzzy computation
                        if (!Examples.getLost(Variables,i,j)) {
                            float pertenencia = 0;
                            float pert;
                            for (int k=0; k<Variables.getNLabelVar(j); k++) {
                                if (cromosoma.getCromElemGene(j,k)==1) 
                                    pert = Variables.Fuzzy (j, k, Examples.getDat(i,j));
                                else pert = 0;
                                pertenencia = Utils.Maximum (pertenencia, pert);
                            }
                            disparoFuzzy = Utils.Minimum (disparoFuzzy, pertenencia);
                        }
                        // Crisp computation
                        if (!Examples.getLost(Variables,i,j))
                            if (cromosoma.getCromElemGene(j,NumInterv(Examples.getDat(i,j),j, Variables))==0)
                                disparoCrisp = 0;
                    }
                    else
                        numVarNoInterv++;  // Variable does not take part
                }
            } // End FOR all chromosome values

            // Update globals counters
            gradoCompAntFuzzy += disparoFuzzy;
            if (disparoFuzzy>0) {
            	ejCompAntFuzzy++;
                if (Examples.getClass(i) == Variables.getNumClassObj()) {
                    gradoCompAntClassFuzzy +=disparoFuzzy;
                    ejCompAntClassFuzzy ++;
                }
                if ((!Examples.getCovered(i)) &&  (Examples.getClass(i) == Variables.getNumClassObj())) {
                    // If example was not previusly covered and belongs to the target class
                    // increments the number of covered examples
                    ejCompAntClassNewFuzzy++;
                    gradoCompAntClassNewEjFuzzy += disparoFuzzy;
                    if (marcar && AG.getObj1()!="CSUP")
                        // We only mark the example as covered if "marcar" is true
                        // and support is measured by a fuzzy measure
                        Examples.setCovered(i,true);
                }
            }
            if (disparoCrisp>0) {
                ejCompAntCrisp++;
                if (Examples.getClass(i) == Variables.getNumClassObj()) {
                    ejCompAntClassCrisp ++;
                }
                // (Sign computation) Increases #examples of the class of the example covered by the rule
                //  with independence of the class of the rule
                cubreClase[Examples.getClass(i)]++;
                if ((!Examples.getCovered(i)) &&  (Examples.getClass(i) == Variables.getNumClassObj())) {
                    // If example was not previusly covered and belongs to the target class
                    // increments the number of covered examples
                    ejCompAntClassNewCrisp++;
                    if (marcar && AG.getObj1()!="CSUP")
                        // We only mark the example as covered if "marcar" is true
                        // and support is measured by a crisp measure
                        Examples.setCovered(i,true);
                }
            }

        } // End of cycle for each example


        // Compute the new number of not covered examples (only fuzzy version)
        por_cubrir = Examples.getExamplesClassObj() - Examples.getExamplesCovered();


        float auxNum=0, auxDen=0;

        // Compute the measures
        if(AG.getObj1().compareTo("CSUP")==0){
            // Compute CSupport (Lavrac)
            // (Crisp): Number of examples compatibles with antecedent and class / total number of exammples
            csupport = ((float)ejCompAntClassCrisp/Examples.getNEx());
            // Compute Local CSupport
            // (Crisp): number of new examples covered with antecedent and class / uncovered examples of the class
            if (por_cubrir != 0)
                lcsupport = ((float) ejCompAntClassNewCrisp/por_cubrir);
            else
                lcsupport = 0;

            valorComp = csupport;
            valorlComp = lcsupport;
        } else if (AG.getObj1().compareTo("FSUP")==0){
            // Compute FSupport
            // (Fuzzy): degree of compatibility with antecedent and class / total examples
            if (Examples.getNEx() != 0)
                fsupport = ((float)gradoCompAntClassFuzzy/Examples.getNEx());
            else
                fsupport = 0;
            // Compute Local FSupport
            // (Fuzzy): degree compatibility of new examples covered with ant^class / uncovered examples of the class
            if (por_cubrir != 0)
                lfsupport = ((float)gradoCompAntClassNewEjFuzzy/por_cubrir);
            else
                lfsupport = 0;

            valorComp = fsupport;
            valorlComp = lfsupport;
        } else {
            // Compute Support (completitud)
            // OUR DEFINITION:  examples of the class compatibles / examples of the class
            if (Examples.getExamplesClassObj() != 0)
                completitud = ((float)ejCompAntClassFuzzy/Examples.getExamplesClassObj());
            else
                completitud = 0;
            // Compute Local Support (lcompletitud)
            // OUR DEFINITION:  new examples of the class compatibles / uncovered examples of the class */
            if (por_cubrir != 0)
                lcompletitud = ((float)ejCompAntClassNewFuzzy/por_cubrir);
            else
                lcompletitud = 0;

            valorComp = completitud;
            valorlComp = lcompletitud;
        }

        // Confidence
        if (AG.getObj2().compareTo("CCNF")==0) {
            // Compute crisp confidence
            // (Crisp): number of examples compatibles with antecedent and class / number examples compatibles with antecedent
            if (ejCompAntCrisp != 0)
                cconfianza = (float)ejCompAntClassCrisp/ejCompAntCrisp;
            else
                cconfianza = 0;

            valorConf = cconfianza;
        } else {  // =="conf"
            // Compute fuzzy confidence
            // OUR DEFINITION (fuzzy): degree of compatibility with antecedent and class / degree of compatibility with antecedent
            if (gradoCompAntFuzzy != 0)
                confianza = (float)gradoCompAntClassFuzzy/gradoCompAntFuzzy;
            else
                confianza = 0;

            valorConf = confianza;
        }

        float val3 = 0;
        if (AG.getObj3().compareTo("NULL")!=0){
            if (AG.getObj3().compareTo("ACCU")==0){
                accuracy = (float)(ejCompAntClassCrisp+1) / (ejCompAntCrisp + Variables.getNClass());
                val3 = accuracy;
            }
            if (AG.getObj3().compareTo("COVE")==0){
                coverage = ((float)ejCompAntCrisp/Examples.getNEx());
                val3 = coverage;
            }
            if (AG.getObj3().compareTo("SIGN")==0){
                coverage = ((float)ejCompAntCrisp/Examples.getNEx());
                float sumaSignClase=0;
                for (int j=0; j<Variables.getNClass(); j++) {
                    if (cubreClase[j]!=0)
                        sumaSignClase += cubreClase[j] * Math.log10 ((float)cubreClase[j]/(ejClase[j]*coverage));
                }
                significance = 2 * sumaSignClase;
                if (numVarNoInterv >= Variables.getNVars()) {
                    significance = 0;
                }
                // Normalise to (0..1) significance for the fitness
                float maxSignif=0;
                for (int ii=0; ii<Variables.getNClass(); ii++) {
                    if (cubreClase[ii]!=0 && coverage!=0)   // cubreclase contains the frecuency
                        maxSignif += cubreClase[ii] * Math.log10 ((float)1/coverage);
                }
                maxSignif = 2*maxSignif ;
                // Then, normalize the value
                float normSignif=0;
                if (maxSignif!=0) normSignif = significance/maxSignif;
                val3 = normSignif;
            }
            if (AG.getObj3().compareTo("UNUS")==0){
                coverage = ((float)ejCompAntCrisp/Examples.getNEx());
                if (ejCompAntCrisp==0)
                    unusualness = 0;
                else
                    unusualness =  coverage * ( (float)ejCompAntClassCrisp/ejCompAntCrisp - (float)Examples.getExamplesClassObj()/Examples.getNEx());
                if (numVarNoInterv >= Variables.getNVars()) {
                    unusualness = 0;
                }
                float normUnus = unusualness + (float)Examples.getExamplesClassObj()/Examples.getNEx();
                val3 = normUnus;
            }
        }

        // Mark the covered examples if indicated in the parameter
        if (marcar)
            // Once the support is computed, update the number of covered examples
            if (AG.getObj1().compareTo("CSUP")==0)
                // If the support measure is crisp
                Examples.setExamplesCovered (Examples.getExamplesCovered()+ejCompAntClassNewCrisp);
            else
                // If the support measure is fuzzy
                Examples.setExamplesCovered (Examples.getExamplesCovered()+ejCompAntClassNewFuzzy);


        // Measures are set to 0 if no variable takes part in the rule (empty rule)
        if (numVarNoInterv >= Variables.getNVars()) {
            lcompletitud = completitud = confianza = 0;
            fsupport = lfsupport = csupport =  lcsupport = cconfianza = 0;
            val3 = accuracy = coverage = significance = unusualness = 0;
            valorComp = valorlComp = valorConf = 0;
        }

        if (AG.getW1()!=0) {
            if (marcar)
                auxNum += AG.getW1()*valorComp;
            else
                auxNum += AG.getW1()*valorlComp;
            auxDen+= AG.getW1();
        }
        if (AG.getW2()!=0) {
            auxNum += AG.getW2()*valorConf;
            auxDen += AG.getW2();
        }
        if (AG.getW3()!=0) {
            auxNum += AG.getW3()*val3;
            auxDen += AG.getW3();
        }

        medidas.setSup(valorComp);
        medidas.setLSup(valorlComp);
        medidas.setCnf(valorConf);
        medidas.setVal3(val3);
        medidas.setFitness(auxNum/auxDen);

        // Set the individual as evaluated
        evaluado = true;

    }


    /**
     * <p>
     * Method to print the contents of the individual
     * </p>
     * @param nFile             File to write the individual
     */
    public void Print(String nFile) {
        String contents;
        cromosoma.print(nFile);
        contents = "Fitness "+medidas.getFitness() + "\n";
        contents+= "Evaluated? " + evaluado + "\n";
        if (nFile=="")
            System.out.print (contents);
        else
           Files.addToFile(nFile, contents);
    }


}
