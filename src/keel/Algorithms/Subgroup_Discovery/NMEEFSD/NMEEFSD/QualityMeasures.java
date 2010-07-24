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

import org.core.*;

public class QualityMeasures {
    /**
     * <p>
     * Defines the quality measures of the individual
     * </p>
     */

    private double[] v_objetivos;
    private int num_objetivos;
    private double cnf;


    /**
     * <p>
     * Creates a new instance of QualityMeasures
     * </p>
     * @param nobj              Number of objectives
     */
    public QualityMeasures(int nobj) {
        num_objetivos = nobj;
        v_objetivos = new double[num_objetivos];
        for(int i=0; i<num_objetivos; i++){
            v_objetivos[i]=0.0;
        }
    }


    /**
     * <p>
     * Returns the num_objetivos of the individual
     * </p>
     * @return              Number of objectives
     */
    public int getNumObjectives (){
        return num_objetivos;
    }

    /**
     * <p>
     * Sets the num_objetivos of the individual
     * </p>
     * @param a             Number of objectives
     */
    public void setNumObjectives (int a){
        num_objetivos = a;
    }

    /**
     * <p>
     * Gets the value of the objective pos
     * </p>
     * @param pos               Position of the objective
     * @return                  Value of the objective
     */
    public double getObjectiveValue (int pos){
        return v_objetivos[pos];
    }

    /**
     * <p>
     * Sets the value of the objective pos
     * </p>
     * @param pos               Position of the objective
     * @param value             Value of the objective
     */
    public void setObjectiveValue (int pos, double value){
        v_objetivos[pos] = value;
    }

    /**
     * <p>
     * Gets the value of the confidence
     * </p>
     * @return                  Value of the confidence
     */
    public double getCnf (){
        return cnf;
    }

    /**
     * <p>
     * Sets the value of the confidence
     * </p>
     * @param acnf              Value of the confidence
     */
    public void setCnf (double acnf){
        cnf = acnf;
    }


    /**
     * <p>
     * Copy in this object the values of qmeasures
     * </p>
     * @param qmeasures           Quality measures
     * @param nobj              Number of objectives
     */
    public void Copy (QualityMeasures qmeasures, int nobj) {
        for (int i=0; i<nobj; i++){
            this.v_objetivos[i] = qmeasures.v_objetivos[i];
        }
        this.setCnf(qmeasures.getCnf());
    }


    /**
     * <p>
     * Prints the measures
     * </p>
     * @param nFile             Fichero to write the quality measures
     * @param AG                Genetic algorithm
     */
    public void Print(String nFile, Genetic AG) {
        String contents;
        contents = "Measures: ";
        
        for(int i=0; i<AG.getNumObjectives(); i++){
            contents += AG.getNObjectives(i)+": ";
            contents += getObjectiveValue(i);
            contents += ", ";
        }

        contents += "confidence: "+getCnf();

        contents+= "\n";
        if (nFile=="")
            System.out.print (contents);
        else
           Files.addToFile(nFile, contents);
    }



}
