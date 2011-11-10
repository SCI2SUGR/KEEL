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
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 30/06/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.MESDIF.MESDIF;

import keel.Algorithms.Subgroup_Discovery.MESDIF.Calculate.*;

import org.core.*;


public class IndCAN  extends Individual {
    /**
     * <p>
     * Defines an individual composed by a Canonical cromosome.
     * </p>
     */
    
    public CromCAN cromosoma;     // Individual contents
     
    /**
     * <p>
     * Creates new instance of IndCAN
     * </p>
     * @param AG                  Instance of the genetic algorithm
     * @param length              Length of the chromosome
     * @param numExamples         Number of examples of the data set
     * @param numObjectives      Number of objectives of the algorithm
     */
    public IndCAN(Genetic AG, int length, int numExamples,int numObjectives) {
      tamano = length;
      cromosoma = new CromCAN(length);
      medidas = new QualityMeasures(AG, numObjectives);
      evaluado = false;
      cubre = new boolean [numExamples];
    }


    /**
     * <p>
     * Returns if the indicated individual is equal to "this"
     * <p>
     * Used to know if two individuals describe the same rule
     *
     * @param otro     Individual to compare with this
     * @return equals     true if the individuals are equal
     */
    public boolean equalTo (Individual otro) {
        boolean equals= true;
        for (int i=0;i<this.tamano;i++)
            if (this.getCromElem(i) != otro.getCromElem(i)) {
                equals = false;
                break;
            }
        return equals;
    }


    /**
     * <p>
     * Creates random instance of Canonical individual
     * </p>
     * @param Variables             Variables structure
     */
    public void InitIndRnd(TableVar Variables) {
      cromosoma.initCromRnd(Variables);  // Random initialization method
      evaluado = false;               // Individual not evaluated
    }

    /**
     * <p>
     * Creates biased random instance of Canonical individual
     * </p>
     * @param Variables             Variables structure
     */
    public void InitIndBsd(TableVar Variables, float porcVar) {
      cromosoma.initCromBsd(Variables, porcVar);  // Biased random initialization method
      evaluado = false;               // Individual not evaluated
    }


    /**
     * <p>
     * Copy the indicaded individual in "this" individual
     * <p>
     * @param otro        Individual to be copied
     */
    public void copyIndiv (Individual otro) {
        for (int i=0;i<this.tamano;i++)
            this.setCromElem(i, otro.getCromElem(i));
        this.setIndivFitness(otro.getIndivFitness());
        this.setIndivEvaluated(otro.getIndivEvaluated());
        this.setIndivDom(otro.getIndivDom());
        this.medidas.copy (otro.medidas);
        for (int i=0;i<cubre.length;i++)
           this.cubre[i] = otro.cubre[i];
    }


    /**
     * <p>
     * Returns the Chromosome
     * </p>
     * @return              Chromosome
     */
    public CromCAN getIndivCromCAN () {
        return cromosoma;
    }


    /**
     * <p>
     * Returns the Chromosome
     * </p>
     * @return              Chromosome
     */
    public CromDNF getIndivCromDNF () {
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
    public int getCromElemGene (int pos, int elem) {
        return 0;
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
    public void setCromElemGene (int pos, int elem, int val) {
        
    }
    

    /**
     * <p>
     * Evaluate a individual. This function evaluates an individual.
     * </p>
     * @param AG                Genetic algorithm
     * @param Variables         Variables structure
     * @param Examples          Ejemplos structure
     */
    public void evalInd (Genetic AG, TableVar Variables, TableDat Examples) {
        
        int ejCompAntFuzzy=0;                // Number of compatible examples with the antecedent of any class - fuzzy version
        int ejCompAntCrisp=0;                // Number of compatible examples with the antecedent of any class - crisp version
        int ejCompAntClassFuzzy=0;           // Number of compatible examples (antecedent and class) - fuzzy version
        int ejCompAntClassCrisp=0;           // Number of compatible examples (antecedent and class) - crisp version

        float gradoCompAntFuzzy=0;           // Total compatibility degree with the antecedent - fuzzy version
        float gradoCompAntClassFuzzy=0;      // Tot compatibility degree with antecedent and class - fuzzy version

        float disparoFuzzy;    // Final compatibility degree of the example with the individual - fuzzy version
        float disparoCrisp;    // Final compatibility or not of the example with the individual - crisp version

        float completitud, fsupport, support, confianza, cconfianza;
        float coverage, unusualness, accuracy, significance;
        
        // For the significance computation we need the number of examples and the number of examples covered of each class
        int ejClase[] = new int[Variables.getNClass()];
        int cubreClase[] = new int[Variables.getNClass()];
        for (int i=0; i<Variables.getNClass(); i++) {
            cubreClase[i]=0;
            ejClase[i] = Examples.getExamplesClass (i);
        }

        /* Variable initialization */
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
                    if (cromosoma.getCromElem(j)<=Variables.getMax(j)){
                        // Variable j takes part in the rule
                        if ((Examples.getDat(i,j) != cromosoma.getCromElem(j)) && (!Examples.getLost(Variables,i,j))) {
                            disparoFuzzy = 0;     // not compatible
                            disparoCrisp = 0;     // not compatible
                        }
                    }
                    else 
                        numVarNoInterv++;  // Variable does not take part
                }
                else {	
                    // Continuous variable
                    if (cromosoma.getCromElem(j)<Variables.getNLabelVar(j)) {
                        // Variable takes part in the rule
                        // Fuzzy computation
                        if (!Examples.getLost(Variables,i,j)) {
                            float pertenencia = Variables.Fuzzy(j, cromosoma.getCromElem(j), Examples.getDat(i,j));
                            disparoFuzzy = Utils.Minimum (disparoFuzzy, pertenencia);
                        }
                        // Crisp computation
                        if (!Examples.getLost(Variables,i,j))
                            if (NumInterv(Examples.getDat(i,j),j, Variables)!= cromosoma.getCromElem(j))
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
            }
            if (disparoCrisp>0) {
                ejCompAntCrisp++;
                if (Examples.getClass(i) == Variables.getNumClassObj()) {
                    ejCompAntClassCrisp ++;
                }
                // (Sign computation)
                // Increases #examples of the class of the example covered by the rule
                // with independence of the class of the rule
                cubreClase[Examples.getClass(i)]++;
            }
            
        } // End of cycle for each example
        

        // Compute the measures
        
        // Support (Lavrac) - (Crisp): Number of examples compatibles with antecedent and class / total number of exammples
        support = ((float)ejCompAntClassCrisp/Examples.getNEx());

        // FSupport - (Fuzzy): degree of compatibility with antecedent and class / total examples
        if (Examples.getNEx() != 0)
            fsupport = ((float)gradoCompAntClassFuzzy/Examples.getNEx());
        else
            fsupport = 0;

        // CSupport (completitud) - our definition:  examples of the class compatibles / examples of the class
        if (Examples.getExamplesClassObj() != 0)
            completitud = ((float)ejCompAntClassFuzzy/Examples.getExamplesClassObj());
        else
            completitud = 0;

        // Confidence - (Crisp): number of examples compatibles with antecedent and class / number examples compatibles with antecedent
        if (ejCompAntCrisp != 0)
            cconfianza = (float)ejCompAntClassCrisp/ejCompAntCrisp;
        else
            cconfianza = 0;
        
        // fuzzy confidence - OUR DEFINITION (fuzzy): degree of compatibility with antecedent and class / degree of compatibility with antecedent
        if (gradoCompAntFuzzy != 0)
            confianza = (float)gradoCompAntClassFuzzy/gradoCompAntFuzzy;
        else
            confianza = 0;

        // Accuracy
        accuracy = (float)(ejCompAntClassCrisp+1) / (ejCompAntCrisp + Variables.getNClass());
        
        //Coverage
        coverage = ((float)ejCompAntCrisp/Examples.getNEx());

        // Significance
        float sumaSignClase=0;
        for (int j=0; j<Variables.getNClass(); j++) {
            if (cubreClase[j]!=0)
                sumaSignClase += cubreClase[j] * Math.log10 ((float)cubreClase[j]/(ejClase[j]*coverage));
        }
        significance = 2 * sumaSignClase;
        if (numVarNoInterv >= Variables.getNVars())
            significance = 0;

        // Unusualness
        if (ejCompAntCrisp==0)
            unusualness = 0;
        else
            unusualness =  coverage * ( (float)ejCompAntClassCrisp/ejCompAntCrisp - (float)Examples.getExamplesClassObj()/Examples.getNEx());
        if (numVarNoInterv >= Variables.getNVars())
            unusualness = 0;


        // Measures are set to 0 if no variable takes part in the rule (empty rule)
        if (numVarNoInterv >= Variables.getNVars()) {
            completitud = confianza = 0;
            fsupport = support =  cconfianza = 0;
            accuracy = coverage = significance = unusualness = 0;
            for (int x=0; x<Examples.getNEx(); x++)
               cubre[x] = false;   // The individual does not cover any example
        }

        // Set the individual as evaluated
        evaluado = true;

        // Store the quality measures
        medidas.setFCnf(confianza);
        medidas.setCCnf(cconfianza);
        medidas.setCSup(support);
        medidas.setFSup(fsupport);
        medidas.setComp(completitud);
        medidas.setAccu(accuracy);
        medidas.setCove(coverage);
        medidas.setSign(significance);
        medidas.setUnus(unusualness);

        // Store the values of the objectives from the measures computed
        medidas.loadObjValues();

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
        contents = "Fitness "+/*fitness*/medidas.getFitness() + "\n";
        contents+= "Evaluated? " + evaluado + "\n";
        if (nFile.equals(""))
            System.out.print (contents);
        else 
           Files.addToFile(nFile, contents);
    }
   
    
}
