/**
 * <p>
 * @author Written by Luciano Sánchez (University of Oviedo) 20/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual;

import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Classifier.*;
import keel.Algorithms.Shared.Exceptions.*;

public abstract class GeneticIndividualForClassification extends GeneticIndividual {
/**
 * <p>
 * Class for management of genetic individuals in classification
 * Need: the examples with the class, the classifier and a variable for results
 */


    protected static double[][] X;
    protected static int[] C;
    protected Classifier c;
    protected static int[]Co;

    /**
     * <p>
     * Constructor. Initialize the type of fitness
     * @param tf The type of fitness
     */
    public GeneticIndividualForClassification(int tf) { super(tf); }

    /**
     * <p>
     * This method calculate the classification error using the examples set
     * </p>
     * @return The classification error
     * @throws invalidFitness Message if error
     */
    public double fitness() throws invalidFitness {

	     if (fitnessType != STANDARD) throw new invalidFitness("Fitness no valido");

        // Classification error is calculated using the samples set
        double classificationError=0;
        for (int i=0;i<X.length;i++) {
            Co[i]=c.getMaximum(X[i]);
            if (Co[i] != C[i]) classificationError++;
        }
        classificationError/=X.length;
        return classificationError;

    }

    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        g.debug();
        c.debug();
    }

    /**
     * <p>
     * This method return the result of classification
     * </p>
     * @return The result of classification
     */
    public int[] getCo() { return Co; }

    /**
     * <p>
     * This method initialize the examples and create a new classifier
     * </p>
     * @param pX The set of examples
     * @param pC The sets of classes
     */
    public void setExamples(double[][] pX, int[] pC) {
        X=pX; C=pC; Co=new int[pC.length];
    }
}
