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
 * @author Written by Nicola Flugy Papa (Politecnico di Milano) 24/03/2009
 * @author Modified by Cristobal J. Carmona (University of Jaen) 10/07/2010
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDMap.SDMap;

import java.text.DecimalFormat;

public class AssociationRule {
	/**
	 * <p>
	 * It is used for representing and handling an Association Rule
	 * </p>
	 */
	
	private short[] antecedent;
	private short[] consequent;
	private double ruleSupport;
	private double antecedentSupport;
	private double confidence;
        private double distrib[];
        private int distribEx[];
        private int classes;
	/**
	 * <p>
	 * It creates a new association rule by setting up its properties
	 * </p>
	 * @param antecedent       The antecedent part of the rule
	 * @param consequent       The consequent part of the rule
	 * @param ruleSupport        The value representing the rule support
	 * @param antecedentSupport        The value representing the antecedent support
	 * @param confidence        The value representing the rule confidence
         * @param clases           Values of the target variable
	 */
	public AssociationRule(short[] antecedent, short[] consequent, double ruleSupport, double antecedentSupport, double confidence, int clases) {
		this.setAntecedent(antecedent);
		this.setConsequent(consequent);
		this.ruleSupport = ruleSupport;
		this.antecedentSupport = antecedentSupport;
		this.confidence = confidence;
                classes = clases;
                distrib = new double[classes];    //Distribution of the classes
                distribEx = new int[classes];     //Distribution of the examples of the classes
                for (int i = 0; i < classes; i++) {
                    distrib[i] = 0;
                    distribEx[i] = 0;
                }
	}

	/**
	 * <p>
	 * It creates a new association rule by setting up its properties
	 * </p>
	 * @param antecedent       The antecedent part of the rule
	 * @param consequent       The consequent part of the rule
	 * @param ruleSupport        The value representing the rule support
	 * @param antecedentSupport        The value representing the antecedent support
	 * @param confidence        The value representing the rule confidence
         * @param clases           Values of the target variable
         * @param dist         Distribution values of the class
	 */
	public AssociationRule(short[] antecedent, short[] consequent, double ruleSupport, double antecedentSupport, double confidence, int clases, int[] dist) {
		this.setAntecedent(antecedent);
		this.setConsequent(consequent);
		this.ruleSupport = ruleSupport;
		this.antecedentSupport = antecedentSupport;
		this.confidence = confidence;
                classes = clases;
                distrib = new double[classes];    //Distribution of the classes
                distribEx = new int[classes];     //Distribution of the examples of the classes
                for (int i = 0; i < classes; i++) {
                    distrib[i] = 0;
                    distribEx[i] = dist[i];
                }
                adjustDistrib();
	}

        /**
         * <p>
         * Sets the antecedent of the rule
         * </p>
         * @param short[]           Antecedent
         */
	private void setAntecedent(short[] antecedent) {
		this.antecedent = new short[antecedent.length];
		
		for (int i=0; i < this.antecedent.length; i++)
			this.antecedent[i] = antecedent[i];
	}
	
        /**
         * <p>
         * Sets the consequent of the rule
         * </p>
         * @param short[]           Consequent
         */
	private void setConsequent(short[] consequent) {
		this.consequent = new short[consequent.length];
		
		for (int i=0; i < this.consequent.length; i++)
			this.consequent[i] = consequent[i];
	}
	
	/**
	 * <p>
	 * It retrieves the antecedent part of an association rule
	 * </p>
	 * @return An array of numbers representing antecedent attributes
	 */
	public short[] getAntecedent() {
		return this.antecedent;
	}
	
	/**
	 * <p>
	 * It retrieves the consequent part of an association rule
	 * </p>
	 * @return An array of numbers representing consequent attributes
	 */
	public short[] getConsequent() {
		return this.consequent;
	}
	
	/**
	 * <p>
	 * It returns the support of an association rule
	 * </p>
	 * @return A value representing the support of the association rule
	 */
	public double getRuleSupport() {
		return this.ruleSupport;
	}
	
	/**
	 * <p>
	 * It returns the antecedent support of an association rule
	 * </p>
	 * @return A value representing the antecedent support of the association rule
	 */
	public double getAntecedentSupport() {
		return this.antecedentSupport;
	}
	
	/**
	 * <p>
	 * It returns the confidence of an association rule
	 * </p>
	 * @return A value representing the confidence of the association rule
	 */
	public double getConfidence() {
		return this.confidence;
	}
		
        /**
         * <p>
         * Reset the value of the distribution
         * </p>
         */
        public void deleteDistrib() {
            for (int i = 0; i < classes; i++) {
                distribEx[i] = 0;
            }
        }

        /**
         * <p>
         * Increments the number of example for the class cover for the complex
         * </p>
         * @param value             The value of the class
         */
        public void incrementDistrib(int value) {
            distribEx[value]++;
        }

        /**
         * <p>
         * Convert the distribution between 0 and 1
         * </p>
         */
        public void adjustDistrib() {
            double total = 0;
            for (int i = 0; i < classes; i++) {
                total += distribEx[i];
                distrib[i] = 0;
            }
            if (total > 0){
                for (int i = 0; i < classes; i++) {
                    distrib[i] = (double) distribEx[i] / total;
                }
            }
        }

        /**
         * <p>
         * Return the value of a distribution
         * </p>
         * @param value         Index of the class
         * @return              The value of the distribution for this class
         */
        public double getDistribClass(int value) {
            return distrib[value];
        }

        /**
         * <p>
         * Return the value of the complete distribution
         * </p>
         * @return double[]         The value of the complete distribution
         */
        public double[] getDistrib() {
            return distrib;
        }

        /**
         * <p>
         * Return the value of the distribution for the example of a class
         * </p>
         * @param value             Index of the class
         * @return                  Value of the distribution
         */
        public int getDistribClassEx(int value) {
            return distribEx[value];
        }

        /**
         * <p>
         * Return the value of the complete distribution for the example of a class
         * </p>
         * @return                  Value of the distribution
         */
        public int[] getDistribEx() {
            return distribEx;
        }

        /**
         * <p>
         * Print in a string the distribution values of the class
         * </p>
         * @return              A string with the distribution values of the class
         */
        public String printDistribucionString() {

            DecimalFormat d = new DecimalFormat("0.00");

            String cad = new String("\n[");
            for (int i = 0; i < classes; i++) {
                cad += "  " + d.format(distrib[i]);
            }
            cad += "  ]\n";
            return cad;
        }


        /**
	 * <p>
	 * It returns a raw string representation of an association rule
	 * </p>
	 * @return A raw string representation of the association rule
	 */
	public String toString() {
		String str = "{";
		int i;
		
		for (i=0; i < this.antecedent.length - 1; i++)
			str += this.antecedent[i] + ", ";
		
		str += this.antecedent[i] + "} -> {";
		
		for (i=0; i < this.consequent.length - 1; i++)
			str += this.consequent[i] + ", ";
		
		str += this.consequent[i] + "}; Rule Support: " + this.ruleSupport + "; Antecedent Support: " + this.antecedentSupport + "; Confidence: " + this.confidence;
		
		return str;
	}
}
