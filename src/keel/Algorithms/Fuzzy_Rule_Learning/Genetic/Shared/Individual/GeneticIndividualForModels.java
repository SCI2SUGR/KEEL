/**
 * <p>
 * @author Written by Luciano Sánchez (University of Oviedo) 21/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual;

import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Model.*;

public abstract class GeneticIndividualForModels extends GeneticIndividual {
/**
 * <p>
 * Class for management of genetic individuals in Models
 * </p>
 */
    protected static double[][] X;
    protected static double[] Y;
    protected static double [] Yo;
    protected Model m;

    /**
     * <p>
     * Constructor. Initialize the type of fitness
     * @param tf The type of fitness
     */
    public GeneticIndividualForModels(int tf) { super(tf); }
    
    
    /**
     * <p>
     * This method calculate the model error using the examples set
     * </p>
     * @return The classification error
     * @throws invalidFitness Message if error
     */
    public double fitness() throws invalidFitness {

        Yo=new double[X.length];

        if (fitnessType==STANDARD) {
			// MSE (Mean Square Error) is calculated using the set of samples
			double square_error=0;
			for (int i=0;i<X.length;i++) {
				double output_obtained=m.output(X[i]);
				double error=output_obtained-Y[i];
                                Yo[i]=output_obtained;
				square_error += error*error;
			}
			square_error/=X.length;
			return square_error;
		}

		if (fitnessType==CUSTOM_CESAR) {
			// MSE (Mean Square Error) is calculated using the set of samples
			double squareError=0;
			double sumY=0;
			for (int i=0;i<X.length;i++) {
				double obtainedOutput=m.output(X[i]);
				double error=obtainedOutput-Y[i];
                                Yo[i]=obtainedOutput;
				squareError += error*error;
				sumY += Y[i];
			}
			squareError /= X.length;
			sumY /= X.length;
			return 100/sumY*Math.sqrt(squareError);


		}
        throw new invalidFitness("Fitness no valido");

    }

    /**
     * <p>
     * Method for debug
     * </p>
     */
    public void debug() {
        g.debug();
        m.debug();
    }

    /**
     * <p>
     * This method return the result of the model 
     * </p>
     * @return the result of the model
     */
    public double[] getYo() {
        return Yo;
    }
    
    /**
     * <p>
     * This method inicialize the examples
     * </p>
     * @param pX The set of examples
     * @param pC The sets of classes
     */
    public void setExamples(double[][] pX, double[] pY) {
        X=pX; Y=pY;
    }

}
