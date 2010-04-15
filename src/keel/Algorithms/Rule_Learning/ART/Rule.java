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
