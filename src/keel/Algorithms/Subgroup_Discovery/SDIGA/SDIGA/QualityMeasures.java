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

public class QualityMeasures {
    /**
     * <p>
     * Defines the quality measures of the individual
     * </p>
     */

    private float fitness;  // Performance (fitness)
    private float sup;      // Support - Objective 1
    private float lsup;     // Local Support
    private float cnf;      // Confidence - Objective 2
    private float val3;     // Objective 3

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
     * Method to return the value of the support
     * </p>
     * @return                  Value of the support
     */
    public float getSup () {
      return sup;    
    }

    /**
     * <p>
     * Method to return the value of the local support
     * </p>
     * @return                  Value of the local support
     */
    public float getLSup () {
      return lsup;    
    }

    /**
     * <p>
     * Method to return the value of the confidence
     * </p>
     * @return                  Value of the confidence
     */
    public float getCnf () {
      return cnf;
    }

    /**
     * <p>
     * Method to return the value of the objective 3
     * </p>
     * @return                  Value of the objective 3
     */
    public float getVal3 () {
      return val3;    }
    
    /**
     * <p>
     * Method to set the value of the support
     * </p>
     * @param value                  Value of the support
     */
    public void setSup (float value ) {
      sup = value;    
    }

    /**
     * <p>
     * Method to set the value of the local support
     * </p>
     * @param value                  Value of the local support
     */
    public void setLSup (float value ) {
      lsup = value;
    }

    /**
     * <p>
     * Method to set the value of the confidence
     * </p>
     * @param value                  Value of the confidence
     */
    public void setCnf (float value ) {
      cnf = value;
    }

    /**
     * <p>
     * Method to set the value of the objective 3
     * </p>
     * @param value                  Value of the objective 3
     */
    public void setVal3 (float value ) {
      val3 = value;    
    }


    /**
     * <p>
     * Copy the values of Sets the value of interest
     * </p>
     * @param medidas               QualityMeasures to copy in the object
     */
    public void copy (QualityMeasures medidas) {
        this.fitness = medidas.getFitness(); 
        this.sup     = medidas.getSup();
        this.lsup    = medidas.getLSup();
        this.cnf     = medidas.getCnf();
        this.val3    = medidas.getVal3();
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
        contents+= "Sup: "  + getSup() + ", ";
        contents+= "Cnf: "  + getCnf() + ", ";
        contents+= "Obj3: " + getVal3() + ", ";
        contents+= "\n";
        if (nFile=="") 
            System.out.print (contents);
        else 
           Files.addToFile(nFile, contents);
    }
    
    
    /**
     * <p>
     * Creates a new instance of QualityMeasures
     * </p>
     */
    public QualityMeasures() {
        
    }
    
}
