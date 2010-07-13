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
 * @author Written by Luciano Sánchez (University of Oviedo) 25/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */



package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual;

import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;
import keel.Algorithms.Shared.Exceptions.*;

public abstract class GeneticIndividualForSymbRegr extends GeneticIndividual {
/**
 * <p>
 * Class for management of genetic individuals in symbolic regression
 * </p>
 */

	
    protected static FuzzyAlphaCut[][] Xfuzzy;
    protected static FuzzyAlphaCut[] Yfuzzy;
    protected static double[][] X;
    protected static double[] Y;

    protected static double[] Yo;
    protected FuzzyRegressor m;

 
    private static double ECM=0;
    private final static double MAXFIT=1e8;

    /**
     * <p>
     * Constructor. Initializes the type of fitness
     * </p>
     * @param tf the type of fitness
     */
	public GeneticIndividualForSymbRegr(int tf) { super(tf); }

	/**
	 * <p>
	 * This method calculates the fitness based in the ECM
	 * </p>
	 * @return The fitness
	 * @throws invalidFitness Message is error
	 */
    public double fitness() throws invalidFitness {
        // if (tipoFitness == STANDARD) {
		  FuzzyAlphaCut fECM = new FuzzyAlphaCut(new FuzzyNumberTRIANG(0,0,0));

          for (int i=0;i<X.length;i++) {
            FuzzyAlphaCut obtainedOutput=m.output(Xfuzzy[i]);
			FuzzyAlphaCut fERROR = obtainedOutput.subtract(Yfuzzy[i]);
			fECM = fECM.sum(fERROR.sqr());
          }
		  fECM = fECM.multiply(1.0/X.length);
	      return fECM.massCentre();
		
    }

    /**
     * <p>
     * This method is for debug the fitness
     * </p>
     */
    public void debug_fitness() {


        double ECMT=0;
        for (int i=0;i<X.length;i++) {
            FuzzyAlphaCut obtainedOutput=m.output(Xfuzzy[i]);
            double y=obtainedOutput.massCentre();
            double error=y-Y[i];
            ECMT+=error*error;

        }
        ECMT/=X.length;

        System.out.println(" Error cuadratico medio defuzzificado="+ECMT);


    }

    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        g.debug();
        m.debug();
    }

    /**
     * <p>
     * This method assign examples based on a level of tolerance
     * </p>
     * @param pX
     * @param pY
     * @param tolerance The level of tolerance
     */
    public void asignaejemplos(double[][] pX, double[] pY, double tolerance) {
		X=pX; Y=pY;
		Xfuzzy=new FuzzyAlphaCut[pX.length][];
		Yfuzzy=new FuzzyAlphaCut[pY.length];
		for (int i=0;i<pX.length;i++) {
			Xfuzzy[i] = new FuzzyAlphaCut[pX[i].length];
			for (int j=0;j<X[i].length;j++)
				Xfuzzy[i][j]=new FuzzyAlphaCut(new FuzzyNumberTRIANG(pX[i][j]*(1-tolerance),pX[i][j],pX[i][j]*(1+tolerance)));
			Yfuzzy[i]=new FuzzyAlphaCut(new FuzzyNumberTRIANG(pY[i],pY[i],pY[i]));
        }



    }

    /**
     * <p>
     * This method obtain a crips output that we can compare to punctual models
     * </p>
     * @return The crisp output
     */
	public double[] getYo() {

	    // Obtains a crisp output that we can compare to
		// punctual models

		Yo = new double[X.length];
        for (int i=0;i<X.length;i++) {
            FuzzyAlphaCut obtainedOutput=m.output(Xfuzzy[i]);
            Yo[i]=obtainedOutput.massCentre();
        }
		return Yo;

	}

	/**
	 * <p>
	 * This method calculate the mean square error
	 * @return The mean square error
	 */
	public double MSE() {

		// Mean Square Error of mass center output is calculated on the examples 

        double error=0;
		double sumY=0;
        for (int i=0;i<X.length;i++) {
            FuzzyAlphaCut obtainedOutput=m.output(Xfuzzy[i]);
			error=Math.abs(Y[i]-obtainedOutput.massCentre());
			sumY+=Y[i];
			ECM+=error*error;
        }
        ECM/=X.length;
	    return ECM;
    }
}

