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

