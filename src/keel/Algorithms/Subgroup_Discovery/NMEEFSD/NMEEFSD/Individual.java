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



package keel.Algorithms.Subgroup_Discovery.NMEEFSD.NMEEFSD;

/**
 * @author sergio
 */
public abstract class Individual {

    /**
     * Size of the individual.
     */
    public int tamano;      

    /**
     * Evaluated flag.
     */
    public boolean evaluado;
 
    /**
     * Stores if the invididual covers each example.
     */
    public boolean cubre[]; 
      
    /**
     * Rank.
     */
    public int rank;

    /**
     * Overall Constraint Violation percentage.
     */
    public double overallConstraintViolation;

    /**
     * Number of Violated Constraints.
     */
    public int numberOfViolatedConstraints;

    /**
     * Crowding Distance.
     */
    public double crowdingDistance;

    /**
     * Cover percentage.
     */
    public float cubr;
             
    /**
     * Number of evalutations.
     */
    public int n_eval;             

    /**
     * Measurements of the individual.
     */
    public QualityMeasures medidas;

    /**
     * Default Constructor.
     */
    public Individual() {

    }

     /**
     * <p>
     * Creates random instance of individual
     * </p>
     * @param Variables             Variables structure
     * @param neje                  Number of exaples
     * @param nFile                 Fichero to write the individual
     */
    public abstract void RndInitInd(TableVar Variables, int neje, String nFile);


        /**
     * <p>
     * Creates biased instance of individual
     * </p>
     * @param Variables             Variables structure
     * @param porcVar               Percentage of variables to form the individual
     * @param neje                  Number of exaples
     * @param nFile                 Fichero to write the individual
     */
    public abstract void BsdInitInd(TableVar Variables, float porcVar, int neje, String nFile);


        /**
     * <p>
     * Creates nstance of individual based on coverage
     * </p>
     * @param pop           Actual population
     * @param Variables     Variables structure
     * @param Examples      Examples structure
     * @param porcCob       Percentage of variables to form the individual
     * @param nobj          Number of objectives
     * @param nFile         Fichero to write the individual
     */
    public abstract void CobInitInd(Population pop, TableVar Variables, TableDat Examples, float porcCob, int nobj, String nFile);

    /**
     * <p>
     * Returns the position i of the array cubre
     * </p>
     * @param pos               Position of example
     * @return                  Value of the example
     */
    public boolean getIndivCovered (int pos) {
        return cubre[pos];
    }
    
    /**
     * <p>
     * Returns if the individual has been evaluated
     * </p>
     * @return                  Value of the example
     */
    public boolean getIndivEvaluated () {
        return evaluado;
    }

    /**
     * <p>
     * Sets that the individual has been evaluated
     * </p>
     * @param val               Value of the state of the individual
     */
    public void setIndivEvaluated (boolean val) {
        evaluado = val;
    }

    /**
     * <p>
     * Returns the crowdingDistance of the individual
     * </p>
     * @return                  Crowding distance of the individual
     */
    public double getCrowdingDistance () {
        return crowdingDistance;
    }
    
    /**
     * <p>
     * Sets the crowdingDistance of the individual
     * </p>
     * @param cd                Crowding distance for the individual
     */
    public void setCrowdingDistance (double cd) {
        crowdingDistance = cd;
    }

    /**
     * <p>
     * Returns the numberOfViolatedConstraints of the individual
     * </p>
     * @return                  Number of constraints violated
     */
    public int getNumberViolatedConstraints () {
        return numberOfViolatedConstraints;
    }
    
    /**
     * <p>
     * Sets the numberOfViolatedConstraints of the individual
     * </p>
     * @param novc              Number of constraints violated
     */
    public void setNumberViolatedConstraints (int novc) {
        numberOfViolatedConstraints = novc;
    }

    /**
     * <p>
     * Returns the overallConstraintViolation of the individual
     * </p>
     * @return                  Number over all constraints violated
     */
    public double getOverallConstraintViolation () {
        return overallConstraintViolation;
    }
    
    /**
     * <p>
     * Sets the overallConstraintViolation of the individual
     * </p>
     * @param ocv                Number over all constraints violated
     */
    public void setOverallConstraintViolation (double ocv) {
        overallConstraintViolation = ocv;
    }

    /**
     * <p>
     * Returns the rank of the individual
     * </p>
     * @return              Ranking of the individual
     */
    public int getRank (){
        return rank;
    }
    
    /**
     * <p>
     * Sets the rank of the individual
     * </p>
     * @param arank         Ranking of the individual
     */
    public void setRank (int arank){
        rank = arank;
    }
    
    /**
     * <p>
     * Returns the number of evaluation when the individual was created
     * </p>
     * @return                  Number of evalution when the individual was created
     */
    public int getNEval (){
        return n_eval;
    }
    
    /**
     * <p>
     * Sets the number of evaluation when the individual was created
     * </p>
     * @param eval              Number of evaluation when the individual was created
     */
    public void setNEval (int eval){
        n_eval = eval;
    }

    /**
     * <p>
     * Return the quality measure of the individual
     * </p>
     * @return                  Quality measures of the individual
     */
    public QualityMeasures getMeasures(){
        return medidas;
    }

    /**
     * <p>
     * Gets the value of the quality measure in the position pos
     * </p>
     * @param pos               Position of the quality measure
     * @return                  Value of the quality measure
     */
    public double getMeasureValue(int pos){
        return medidas.getObjectiveValue(pos);
    }

    /**
     * <p>
     * Sets the value of the quality measure in the position pos
     * </p>
     * @param pos               Position of the quality measure
     * @param value             Value of the quality measure
     */
    public void setMeasureValue(int pos, double value){
        medidas.setObjectiveValue(pos, value);
    }

    /**
     * <p>
     * Sets the value of confidence of the individual
     * </p>
     * @param value             Value of confidence of the individual
     */
    public void setCnfValue(double value){
        medidas.setCnf(value);
    }

    /**
     * <p>
     * Gets the value of confidence of the individual
     * </p>
     * @return                  Value of confidence of the individual
     */
    public double getCnfValue(){
        return medidas.getCnf();
    }

    /**
     * <p>
     * Returns the indicated gene of the Chromosome
     * </p>
     * @param pos               Position of the gene
     * @return                  Value of the gene
     */
    public abstract int getCromElem(int pos);

    /**
     * Sets a value in a given position in the chromosome.
     * @param pos given position.
     * @param val given value.
     */
    public abstract void setCromElem (int pos, int val);

    /**
     * <p>
     * Retuns the value of the gene indicated
     * </p>
     * @param pos          Position of the variable in the chromosome
     * @param elem              Position of the gene of the variable
     * @return                  Value of the gene
     */
    public abstract boolean getCromGeneElem(int pos, int elem);

    /**
     * <p>
     * Sets the value of the indicated gene of the Chromosome
     * </p>
     * @param pos               Position of the variable
     * @param elem              Position of the gene
     * @param val               Value of the variable
     */
    public abstract void setCromGeneElem(int pos, int elem, boolean val);

    /**
     * <p>
     * Returns the Canonical Chromosome
     * </p>
     * @return             Canonical Chromosome
     */
    public abstract CromCAN getIndivCromCAN();

    /**
     * <p>
     * Returns the DNF Chromosome
     * </p>
     * @return             DNF Chromosome
     */
    public abstract CromDNF getIndivCromDNF();

    /**
     * Copies an individual given.
     * @param indi given individual to be copied.
     * @param nobj object number.
     * @param neje examples number.
     */
    public abstract void copyIndiv (Individual indi, int nobj, int neje);

    /**
     * <p>
     * Evaluate a individual. This function evaluates an individual.
     * </p>
     * @param AG                Genetic algorithm
     * @param Variables         Variables structure
     * @param Examples          Ejemplos structure
     */
    public abstract void evalInd (Genetic AG, TableVar Variables, TableDat Examples);
    
    /**
     * <p>
     * Returns the number of the interval of the indicated variable to which belongs
     * the value. It is performed seeking the greater belonging degree of the
     * value to the fuzzy sets defined for the variable
     * </p>
     * @param valor                 Value to calculate
     * @param num_var               Number of the variable
     * @param Variables             Variables structure
     * @return                      Number of the interval
     */
    public abstract int NumInterv (float valor, int num_var, TableVar Variables);

    /**
     * <p>
     * Method to print the contents of the individual
     * </p>
     * @param nFile             File to write the individual
     */
    public abstract void Print(String nFile);

    
}
