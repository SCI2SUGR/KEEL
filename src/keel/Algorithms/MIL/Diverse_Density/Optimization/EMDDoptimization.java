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

package keel.Algorithms.MIL.Diverse_Density.Optimization;

import java.util.ArrayList;

import keel.Algorithms.MIL.Diverse_Density.EMDD.EMDD;

import net.sourceforge.jclec.util.dataset.IDataset.IInstance;

/**
 * EMDD algorithm optimization auxiliary methods
 */

public class EMDDoptimization extends Optimization 
{
	// ///////////////////////////////////////////////////////////////
	// ---------------------------------------------------- Properties
	// ///////////////////////////////////////////////////////////////
	
	protected EMDD algorithm;
	
	// ///////////////////////////////////////////////////////////////
	// --------------------------------------------------- Constructor
	// ///////////////////////////////////////////////////////////////
	
	public EMDDoptimization(EMDD algorithm)
	{
		super();
		this.algorithm = algorithm;
	}
	
	/////////////////////////////////////////////////////////////////
	// ---------------------------- Implementing Optimization methods
	/////////////////////////////////////////////////////////////////

	protected double evaluate(double[] x)
	{
		ArrayList<ArrayList<IInstance>> trainInstances = algorithm.getTrainInstances();
		
		double likelihood = 0;
		for (int i = 0; i < trainInstances.size(); i++)
		{
			double result=0.0;

			for (int j = 0; j < algorithm.getMultiInstanceData()[i].length; j++)
				result += (algorithm.getMultiInstanceData()[i][j]-x[j*2])*(algorithm.getMultiInstanceData()[i][j]-x[j*2])*x[j*2+1]*x[j*2+1];

			result = Math.exp(-result);

			if (trainInstances.get(i).get(0).getValue(algorithm.getClassIndex()) == 1)
			{
				if (result <= getZero())
					result = getZero();
				likelihood -= Math.log(result);
			}
			else
			{
				result = 1.0 - result;
				if(result <= getZero())
					result = getZero();
				likelihood -= Math.log(result);
			}
		}
		return likelihood;
	}

	protected double[] gradient(double[] x)
	{
		ArrayList<ArrayList<IInstance>> trainInstances = algorithm.getTrainInstances();
		
		double[] gradient = new double[x.length];

		for (int i = 0; i < trainInstances.size(); i++)
		{
			double[] aux = new double[x.length];
			double exp = 0.0;

			for (int k = 0; k < algorithm.getMultiInstanceData()[i].length; k++)
				exp += (algorithm.getMultiInstanceData()[i][k]-x[k*2])*(algorithm.getMultiInstanceData()[i][k]-x[k*2])*x[k*2+1]*x[k*2+1];

			exp = Math.exp(-exp);

			for (int j = 0; j < algorithm.getMultiInstanceData()[i].length; j++)
			{
				aux[2*j] = 2.0*(x[2*j]-algorithm.getMultiInstanceData()[i][j])*x[j*2+1]*x[j*2+1];
				aux[2*j+1] = 2.0*(x[2*j]-algorithm.getMultiInstanceData()[i][j])*(x[2*j]-algorithm.getMultiInstanceData()[i][j])*x[j*2+1];
			}

			for (int j = 0; j < algorithm.getMultiInstanceData()[i].length; j++)
			{
				if (trainInstances.get(i).get(0).getValue(algorithm.getClassIndex()) == 1)
				{
					gradient[2*j] += aux[2*j];
					gradient[2*j+1] += aux[2*j+1];
				}
				else
				{
					gradient[2*j] -= aux[2*j]*exp/(1.0-exp);
					gradient[2*j+1] -= aux[2*j+1]*exp/(1.0-exp);
				}
			}
		}
		return gradient;
	}
}
