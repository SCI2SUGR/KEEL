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

package keel.Algorithms.MIL.Diverse_Density.EMDD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import keel.Algorithms.MIL.AbstractMIAlgorithm;
import keel.Algorithms.MIL.Diverse_Density.Optimization.EMDDoptimization;

import net.sourceforge.jclec.util.dataset.IDataset;
import net.sourceforge.jclec.util.dataset.KeelDataSet;
import net.sourceforge.jclec.util.dataset.IDataset.IInstance;


/**
 * MIEMDD
 * 
 * Qi Zhang, Sally A. Goldman: EM-DD: An Improved Multiple-Instance Learning Technique. In: Advances in Neural Information Processing Systems 14, 1073-108, 2001.
 */

public class EMDD extends AbstractMIAlgorithm
{
	// ///////////////////////////////////////////////////////////////
	// ---------------------------------------------------- Properties
	// ///////////////////////////////////////////////////////////////
	
	protected double[] best;
	
	protected double[][] multiInstanceData;

	protected EMDDoptimization optimization = new EMDDoptimization(this);
	
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public Methods
	/////////////////////////////////////////////////////////////////
	
	public double[][] getMultiInstanceData() {
		return multiInstanceData;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------- Implementing Algorithm abstract methods
	/////////////////////////////////////////////////////////////////

	public void execute() throws Exception
	{
		loadTrainDataset();
		loadTestDataset();

		multiInstanceData = new double[trainInstances.size()][numberFeatures];
		best = new double[2*numberFeatures];

		double[] x = new double[2*numberFeatures];
		double[][] y = new double[2][2*numberFeatures];
		double[] aux = new double[2*numberFeatures];
		double[] previous = new double[2*numberFeatures];
		double[] bestAux = new double[2*numberFeatures];

		double minError = Double.MAX_VALUE;
		double likelihood, previousLikelihood;

		for (int i = 0; i < 2*numberFeatures; i++)
		{
			y[0][i] = Double.NaN;
			y[1][i] = Double.NaN;
		}

		List<Integer> list = fill(trainInstances.size()-1);
		
		for (int i = 0; i < list.size(); i++)
		{
			for (int j = 0; j < trainInstances.get(list.get(i)).size(); j++) 
			{
				for (int k = 0; k < numberFeatures; k++)
				{
					x[2 * k] = trainInstances.get(list.get(i)).get(j).getValue(k+1);
					x[2 * k + 1] = 1.0;
				} 

				previousLikelihood = Double.MAX_VALUE;
				likelihood = Double.MAX_VALUE/10.0;
				
				for(int k = 0; k < 10 && likelihood < previousLikelihood; k++)
				{
					previousLikelihood = likelihood;

					for (int l = 0; l < trainInstances.size(); l++)
					{
						int insIndex = findInstance(l, x); 

						for (int attribute = 0; attribute < numberFeatures; attribute++)
							multiInstanceData[l][attribute] = trainInstances.get(l).get(insIndex).getValue(attribute+1);
					}

					aux = optimization.minimum(x, y);
					
					while (aux == null)
						aux = optimization.minimum(optimization.getVarValues(), y);
					
					likelihood = optimization.getMinFunction();

					previous = x;
					x = aux; 
				} 

				if (likelihood > previousLikelihood)
					best = previous; 
				else
					best = x;

				int error = 0;
				double distribution[] = new double[2];

				for (int k = 0; k < trainInstances.size(); k++)
				{
					List<IInstance> bag = new ArrayList<IInstance>();

					for(int l = 0; l < trainInstances.get(k).size(); l++)
						bag.add(trainInstances.get(k).get(l));

					distribution = computeDistribution(bag);
					
					if (distribution[1] >= 0.5 && trainInstances.get(k).get(0).getValue(classIndex) == 0)
						error++;
					else if (distribution[1] < 0.5 && trainInstances.get(k).get(0).getValue(classIndex) == 1)
						error++;
				}

				if (error < minError)
				{
					bestAux = best;
					minError = error;
				}
			}
		} 
		best = bestAux;
		
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

		double min = Double.MAX_VALUE;
		double maxProb = -1.0;

		for(int i = 0; i < numberInstances; i++)
		{
			double exp = 0.0;
			for (int j = 0; j < numberFeatures; j++)
				exp += (data[i][j]-best[j*2])*(data[i][j]-best[j*2])*best[j*2+1]*best[j*2+1];

			if (exp < min){
				min     = exp;
				maxProb = Math.exp(-exp);
			}
		}	

		double[] distribution = new double[2];
		distribution[1] = maxProb; 
		distribution[0] = 1.0 - distribution[1];

		return distribution;
	}
		
	private int findInstance(int bag, double[] x)
	{
		double min = Double.MAX_VALUE;
		int numberInstances = trainInstances.get(bag).size();

		int index = 0;
		for (int i = 0; i < numberInstances; i++)
		{
			double ins=0.0;
			for (int j = 0; j < numberFeatures; j++)
				ins += (trainInstances.get(bag).get(i).getValue(j+1)-x[j*2])*(trainInstances.get(bag).get(i).getValue(j+1)-x[j*2])*x[j*2+1]*x[j*2+1];

			if (ins < min){
				min=ins;
				index=i;
			}
		}
		return index;
	}
	
	private ArrayList<Integer> fill(int max)
	{
		Random random = new Random(1);
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		int number;
		
		for(int i = 0; i < 3; i++)
		{
			do {
				number = random.nextInt(max);
			}while(list.contains(number) && trainInstances.get(number).get(0).getValue(classIndex) == 0);
			list.add(new Integer(number)); 
		}
		
		return list;
	}
}
