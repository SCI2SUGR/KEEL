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

package keel.Algorithms.MIL.Diverse_Density.DD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import keel.Algorithms.MIL.AbstractMIAlgorithm;
import keel.Algorithms.MIL.Diverse_Density.Optimization.DDoptimization;

import net.sourceforge.jclec.util.dataset.IDataset;
import net.sourceforge.jclec.util.dataset.KeelDataSet;
import net.sourceforge.jclec.util.dataset.IDataset.IInstance;


/**
 * Diverse Density
 * 
 * O. Maron and T. Lozano-Perez. A Framework for Multiple Instance Learning. Neural Information Processing Systems, 10, 1998.
 */

public class DD extends AbstractMIAlgorithm
{
	// ///////////////////////////////////////////////////////////////
	// ---------------------------------------------------- Properties
	// ///////////////////////////////////////////////////////////////

	protected double[] best;
	
	protected DDoptimization optimization = new DDoptimization(this);	

	/////////////////////////////////////////////////////////////////
	// ---------------------- Implementing Algorithm abstract methods
	/////////////////////////////////////////////////////////////////

	public void execute() throws Exception
	{
		loadTrainDataset();
		loadTestDataset();
		
		List<Integer> biggestTrainBags = new ArrayList<Integer>();
		
		int maxSize = 0;
		
		for(int i = 0; i < trainInstances.size(); i++)
		{
			if(trainInstances.get(i).get(0).getValue(classIndex) == 1)
			{  
				if(trainInstances.get(i).size() > maxSize)
				{
					biggestTrainBags.clear();
					biggestTrainBags.add(i);
					maxSize = trainInstances.get(i).size();
				}
				else if(trainInstances.get(i).size() == maxSize)
					biggestTrainBags.add(i);
			}
		}

		double[] x = new double[2*numberFeatures], aux = new double[2*numberFeatures];
		double[][] y = new double[2][2*numberFeatures]; 
		double likelihood, bestLikelihood = Double.MAX_VALUE;
		
		for (int i = 0; i < 2*numberFeatures; i++)
		{
			y[0][i] = Double.NaN; 
			y[1][i] = Double.NaN;
		}
		
		for(int i = 0; i < biggestTrainBags.size(); i++)
		{
			for(int j = 0; j < trainInstances.get(biggestTrainBags.get(i)).size(); j++)
			{
				for (int k = 0; k < numberFeatures;k++)
				{
					x[2*k] = trainInstances.get(biggestTrainBags.get(i)).get(j).getValue(k+1);
					x[2*k+1] = 1.0;
				}

				aux = optimization.minimum(x,y);
				
				while(aux==null)
					aux = optimization.minimum(optimization.getVarValues(),y);
				
				likelihood = optimization.getMinFunction();

				if(likelihood < bestLikelihood)
				{
					bestLikelihood = likelihood;
					best = aux;
					aux = new double[x.length];
				}
			}	
		}
		
		report(trainReportFileName, trainDataset, trainInstances);
		report(testReportFileName, testDataset, testInstances);
	}		
	
	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////

	private void report(String reportFileName, IDataset dataset, ArrayList<ArrayList<IInstance>> instances)
	{
		int predictedClass = 0;
		String newline = System.getProperty("line.separator");
		
		try {
			BufferedReader reader= new BufferedReader(new FileReader(((KeelDataSet) dataset).getFileName()));
			BufferedWriter writer= new BufferedWriter(new FileWriter(reportFileName));

			String line= reader.readLine();
			while(line.compareTo("@data") != 0)
			{
				writer.write(line + newline);
				line = reader.readLine();
			}
			writer.write(line + newline);
			
			reader.close();
			
			for(int i = 0; i < instances.size(); i++)
			{
				double [] dist = computeDistribution(instances.get(i));

				if (dist == null)	writer.write("Null distribution predicted");

				double max = 0;

				for (int j = 0; j < dist.length; j++)
				{
					if (dist[j] > max)
					{
						predictedClass = j;
						max = dist[j];
					}
				}

				if (max > 0)
					writer.write((int)instances.get(i).get(0).getValue(classIndex) + " " + predictedClass + newline);
				else
					writer.write((int)instances.get(i).get(0).getValue(classIndex) + " " + "Nose pudo clasificar" + newline);
			}
			
			writer.close();
			
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private double[] computeDistribution(List<IInstance> instances)
	{
		int numberInstances = instances.size();
		
		double[][] data = new double [numberInstances][numberFeatures];

		for(int i = 0; i < numberInstances; i++)
			for(int j = 0; j < numberFeatures; j++)
				data[i][j] = instances.get(i).getValue(j+1);

		double [] distribution = new double[2];
		distribution[0] = 0.0;

		for(int i = 0; i < numberInstances; i++)
		{
			double exp = 0.0;
			for(int j = 0; j < numberFeatures; j++)
				exp += (best[j*2]-data[i][j])*(best[j*2]-data[i][j])*best[j*2+1]*best[j*2+1];

			exp = Math.exp(-exp);

			distribution[0] += Math.log(1.0-exp);
		}

		distribution[0] = Math.exp(distribution[0]);
		distribution[1] = 1.0-distribution[0];

		return distribution;
	}
}
