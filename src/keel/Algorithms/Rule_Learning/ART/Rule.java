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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Rule_Learning.ART;

import java.util.Vector;

/**
   Class to store a rule
   @author Ines de la Torre Quesada (UJA)
   @version 1.0 (28-02-2010)
*/
public class Rule {

    /** Antecedente de la regla, formado por los atributos y sus valores */
    private Vector<Integer> attributes;
    private Vector<Integer> values;
    
    /** Consecuente de la regla: indice a la clase correspondiente */
    private int clas;
    private double confidence;
    private int support;


    public Rule(Vector<Integer> attributes, Vector<Integer> values, int clas, double confidence) {
        this.attributes = attributes;
        this.values = values;
        this.clas = clas;
        this.confidence = confidence;
        this.support = 0;
    }

    public Rule(Vector<Integer> attributes, Vector<Integer> values) {
        this.attributes = attributes;
        this.values = values;
        this.support = 0;
    }

    public Vector<Integer> getAttributes() {
        return attributes;
    }

    public void setAttributes(Vector<Integer> attributes) {
        this.attributes = attributes;
    }

    public int getClas() {
        return clas;
    }

    public void setClas(int clas) {
        this.clas = clas;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Vector<Integer> getValues() {
        return values;
    }

    public void setValues(Vector<Integer> values) {
        this.values = values;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

}

