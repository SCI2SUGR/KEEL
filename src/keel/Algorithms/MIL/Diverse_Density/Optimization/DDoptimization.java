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

import keel.Algorithms.MIL.Diverse_Density.DD.DD;

import net.sourceforge.jclec.util.dataset.IDataset.IInstance;

/**
 * DD algorithm optimization auxiliary methods
 */

public class DDoptimization extends Optimization 
{
	// ///////////////////////////////////////////////////////////////
	// ---------------------------------------------------- Properties
	// ///////////////////////////////////////////////////////////////
	
	protected DD algorithm;
	
	// ///////////////////////////////////////////////////////////////
	// --------------------------------------------------- Constructor
	// ///////////////////////////////////////////////////////////////
	
	public DDoptimization(DD algorithm)
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
		for(int i = 0; i < trainInstances.size(); i++)
		{ 
			double bag = 0.0;

			for(int j = 0; j < trainInstances.get(i).size(); j++)
			{
				double result=0.0;
				for(int k = 0; k < algorithm.getNumberFeatures(); k++)
					result += (trainInstances.get(i).get(j).getValue(k+1)-x[k*2])*(trainInstances.get(i).get(j).getValue(k+1)-x[k*2])*x[k*2+1]*x[k*2+1];

				result = Math.exp(-result);
				result = 1.0-result;

				if(trainInstances.get(i).get(0).getValue(algorithm.getClassIndex()) == 1)
					bag += Math.log(result);
				else
				{
					if(result <= getZero())
						result = getZero();
					likelihood -= Math.log(result);
				}
			}		

			if(trainInstances.get(i).get(0).getValue(algorithm.getClassIndex()) == 1)
			{
				bag = 1.0 - Math.exp(bag);
				if(bag <= getZero())
					bag = getZero();
				likelihood -= Math.log(bag);
			}
		}		
		return likelihood;
	}

	protected double[] gradient(double[] x)
	{
		ArrayList<ArrayList<IInstance>> trainInstances = algorithm.getTrainInstances();
		
		double[] gradient = new double[x.length];
		
		for(int i = 0; i < trainInstances.size(); i++)
		{ 
			int numberInstances = trainInstances.get(i).size(); 

			double[] aux = new double[x.length];
			double res = 0.0;	

			for(int j = 0; j < numberInstances; j++)
			{
				double exp=0.0;
				for(int k = 0; k < algorithm.getNumberFeatures(); k++)
					exp += (trainInstances.get(i).get(j).getValue(k+1)-x[k*2])*(trainInstances.get(i).get(j).getValue(k+1)-x[k*2])*x[k*2+1]*x[k*2+1];

				exp = Math.exp(-exp);
				exp = 1.0-exp;

				if(trainInstances.get(i).get(0).getValue(algorithm.getClassIndex()) == 1)
					res += Math.log(exp);		   		    

				if(exp<=getZero())
					exp=getZero();

				for(int p=0; p < algorithm.getNumberFeatures(); p++)
				{ 
					aux[2*p] += (1.0-exp)*2.0*(x[2*p]-trainInstances.get(i).get(j).getValue(p+1))*x[p*2+1]*x[p*2+1]/exp;
					aux[2*p+1] += 2.0*(1.0-exp)*(x[2*p]-trainInstances.get(i).get(j).getValue(p+1))*(x[2*p]-trainInstances.get(i).get(j).getValue(p+1))*x[p*2+1]/exp;
				}					    
			}		    

			res = 1.0-Math.exp(res);
			
			if(res <= getZero())
				res = getZero();

			for(int j = 0; j < algorithm.getNumberFeatures(); j++)
			{
				if(trainInstances.get(i).get(0).getValue(algorithm.getClassIndex()) == 1)
				{
					gradient[2*j] += aux[2*j]*(1.0-res)/res;
					gradient[2*j+1] += aux[2*j+1]*(1.0-res)/res;
				}
				else
				{
					gradient[2*j] -= aux[2*j];
					gradient[2*j+1] -= aux[2*j+1];
				}
			}
		}
		return gradient;
	}
}
