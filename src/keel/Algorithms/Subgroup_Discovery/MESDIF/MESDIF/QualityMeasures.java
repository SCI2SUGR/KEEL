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

import org.core.*;

public class QualityMeasures {
    /**
     * <p>
     * Defines the quality measures of the individual
     * </p>
     */

    private float fitness;        // Performance (fitness)

    private float fcnf;           // Confidence: fuzzy
    private float ccnf;           // Confidence: crisp
    private float csup;           // Support: crisp
    private float fsup;           // Support: fuzzy (divided by class examples)
    private float comp;           // Support: our (crisp divided by class examples)p
    private float accu;           // Accuracy (confidence)
    private float cove;           // Coverage, measured as crisp rules?
    private float sign;           // Significance
    private float unus;           // Unusualness
    private float osup;           // Original support (old name: cubr) - has to be computed with the population

    private int n_objectives;     // Number of objectives
    private String name_obj[];    // Names of the objectives used
    private float value_obj[];    // Values ob the objectives


    /**
     * <p>
     * Creates a new instance of QualityMeasures
     * </p>
     * @param AG            Instance of the genetic algorithm
     * @param number         number of objectives to store
     */
    public QualityMeasures(Genetic AG, int number) {
        // both name_obj and value_obj are declared with number+1 to use
        // 1..number as objectives instead of 0..number-1
        name_obj = new String[number+1];   // creates the array to store the names of the objectives
        value_obj = new float[number+1];   // creates the array to store the valuues
        n_objectives = number;

        // Copy the names of the objectives
        for (int i=1; i<=n_objectives; i++)
            name_obj[i]= AG.getObj(i);

    }


    /**
     * <p>
     * Retuns the value of the fitness
     * </p>
     * @return                  Value of the fitness
     */
    public float getFitness () {
      return fitness;
    }

    /**
     * <p>
     * Sets the value of fitness
     * </p>
     * @param value             Value of the fitness
     */
    public void setFitness (float value ) {
      fitness = value;
    }


    /**
     * <p>
     * Retuns the value of the original support measure
     * </p>
     * @return                  Value of the original support
     */
    public float getOSup () {
      return osup;
    }

    /**
     * <p>
     * Sets the value of original support measure
     * </p>
     * @param value             Value of the original support
     */
    public void setOSup (float value ) {
      osup = value;
    }


    /**
     * <p>
     * Retuns the value of FCNF
     * </p>
     * @return       Value of FCNF
     */
    public float getFCnf() { return fcnf; }


    /**
     * <p>
     * Retuns the value of CCNF
     * </p>
     * @return       Value of CCNF
     */
    public float getCCnf() { return ccnf; }


    /**
     * <p>
     * Retuns the value of CSUP
     * </p>
     * @return       Value of CSUP
     */
    public float getCSup() { return csup; }


    /**
     * <p>
     * Retuns the value of FSUP
     * </p>
     * @return       Value of FSUP
     */
    public float getFSup() { return fsup; }


    /**
     * <p>
     * Retuns the value of COMP
     * </p>
     * @return       Value of COMP
     */
    public float getComp() { return comp; }


    /**
     * <p>
     * Retuns the value of ACCU
     * </p>
     * @return       Value of ACCU
     */
    public float getAccu() { return accu; }


    /**
     * <p>
     * Retuns the value of
     * </p>
     * @return       Value of
     */
    public float getCove() { return cove; }


    /**
     * <p>
     * Retuns the value of SIGN
     * </p>
     * @return       Value of SIGN
     */
    public float getSign() { return sign; }


    /**
     * <p>
     * Retuns the value of UNUS
     * </p>
     * @return       Value of UNUS
     */
    public float getUnus() { return unus; }




    /**
     * <p>
     * Sets the value of FCNF
     * </p>
     * @param value       Value of FCNF
     */
    public void setFCnf (float value ) { fcnf = value; }


    /**
     * <p>
     * Sets the value of CCNF
     * </p>
     * @param value       Value of CCNF
     */
    public void setCCnf (float value ) { ccnf = value; }


    /**
     * <p>
     * Sets the value of CSUP
     * </p>
     * @param value       Value of CSUP
     */
    public void setCSup (float value ) { csup = value; }


    /**
     * <p>
     * Sets the value of FSUP
     * </p>
     * @param value       Value of FSUP
     */
    public void setFSup (float value ) { fsup = value; }


    /**
     * <p>
     * Sets the value of COMP
     * </p>
     * @param value       Value of COMP
     */
    public void setComp (float value ) { comp = value; }


    /**
     * <p>
     * Sets the value of ACCU
     * </p>
     * @param value       Value of ACCU
     */
    public void setAccu (float value ) { accu = value; }


    /**
     * <p>
     * Sets the value of COVE
     * </p>
     * @param value       Value of COVE
     */
    public void setCove (float value ) { cove = value; }


    /**
     * <p>
     * Sets the value of SIGN
     * </p>
     * @param value       Value of SIGN
     */
    public void setSign (float value ) { sign = value; }


    /**
     * <p>
     * Sets the value of UNUS
     * </p>
     * @param value       Value of UNUS
     */
    public void setUnus (float value ) { unus = value; }

    

    /**
     * <p>
     * Retuns the number of objectives used
     * </p>
     * @return          Number of objectives
     */
    public int getNObjectives () {
      return n_objectives;
    }


    /**
     * <p>
     * Method to get the name of the objective indicated
     * </p>
     * @param num         Number of the objective
     * @return             Name of objective
     */
    public String getNameObj (int num) {
        return name_obj[num];
    }

    /**
     * <p>
     * Method to set the name of the objective indicated
     * </p>
     * @param num         Number of the objective
     * @param name        Name of the objective
     */
    public void setNameObj (int num, String name) {
        name_obj[num] = name;
    }


    /**
     * <p>
     * Method to get the value of the objective indicated
     * </p>
     * @param num         Number of the objective
     * @return             Value of objective
     */
    public float getValueObj (int num) {
        return value_obj[num];
    }

    /**
     * <p>
     * Method to get the value of the objective with the name indicated
     * </p>
     * @param name        Name of the objective
     * @return             Value of objective
     */
    public float getValueObj (String name) {
        float valor=-1;
        for (int i=1;i<=n_objectives;i++)
            if (name_obj[i].equals(name))
                valor = value_obj[i];
        return valor;
    }


    /**
     * <p>
     * Method to set the value of the objective indicated
     * </p>
     * @param num         Number of the objective
     * @param value       Value of the objective
     */
    public void setValueObj (int num, float value) {
        value_obj[num] = value;
    }


    /**
     * <p>
     * Method to load the values of the objectives from the computed
     * </p>
     */
    public void loadObjValues () {

        for (int i=1;i<=n_objectives;i++)
        {
            if      (name_obj[i].equalsIgnoreCase("FCNF")) value_obj[i]=fcnf;
            else if (name_obj[i].equalsIgnoreCase("CCNF")) value_obj[i]=ccnf;
            else if (name_obj[i].equalsIgnoreCase("CSUP")) value_obj[i]=csup;
            else if (name_obj[i].equalsIgnoreCase("FSUP")) value_obj[i]=fsup;
            else if (name_obj[i].equalsIgnoreCase("COMP")) value_obj[i]=comp;
            else if (name_obj[i].equalsIgnoreCase("ACCU")) value_obj[i]=accu;
            else if (name_obj[i].equalsIgnoreCase("COVE")) value_obj[i]=cove;
            else if (name_obj[i].equalsIgnoreCase("SIGN")) value_obj[i]=sign;
            else if (name_obj[i].equalsIgnoreCase("UNUS")) value_obj[i]=unus;
            else if (name_obj[i].equalsIgnoreCase("OSUP")) value_obj[i]=osup;
        }

    }


    /**
     * <p>
     * Copy the values of the quality measures
     * </p>
     * @param medidas               QualityMeasures to copy in the object
     */
    public void copy (QualityMeasures medidas) {
        this.fitness = medidas.fitness;
        this.fcnf    = medidas.fcnf;
        this.ccnf    = medidas.ccnf;
        this.csup    = medidas.csup;
        this.fsup    = medidas.fsup;
        this.comp    = medidas.comp;
        this.accu    = medidas.accu;
        this.cove    = medidas.cove;
        this.sign    = medidas.sign;
        this.unus    = medidas.unus;
        this.osup    = medidas.osup;
        this.n_objectives = medidas.n_objectives;
        for (int i=1;i<=n_objectives;i++)
        {
            this.name_obj[i] = medidas.name_obj[i];
            this.value_obj[i] = medidas.value_obj[i];
        }
    }

    
    /**
     * <p>
     * Prints the measures
     * </p>
     * @param nFile             File to write the quality measures
     */
    public void print(String nFile) {
        String contents;
        contents = "Measures: ";
        contents+= "Fitness: " + getFitness() + ", "; 
        for (int i=1;i<=n_objectives;i++)
            contents+= "Obj " + i + " (" + name_obj[i] + "): " + value_obj[i] + ", ";
        contents+= "\n";
        if (nFile.equals(""))
            System.out.print (contents);
        else 
           Files.addToFile(nFile, contents);
    }
    
    

    
}
