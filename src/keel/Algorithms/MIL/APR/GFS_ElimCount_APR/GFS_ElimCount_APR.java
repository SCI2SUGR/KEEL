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

package keel.Algorithms.MIL.APR.GFS_ElimCount_APR;

import java.util.ArrayList;

import keel.Algorithms.MIL.APR.AbstractAPR;

import net.sourceforge.jclec.util.dataset.IDataset.IInstance;

public class GFS_ElimCount_APR extends AbstractAPR
{
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public Methods
	/////////////////////////////////////////////////////////////////
	
	public void execute()
	{
		loadTrainDataset();
		loadTestDataset();
		
		double[][] positiveRectangle = new double[numberFeatures][2];
		
		for(int i = 0; i < numberFeatures; i++)
		{
			positiveRectangle[i][0] = min(i,0);	// Min value for feature i from positive instances
			positiveRectangle[i][1] = max(i,0); // Max value for feature i from positive instances
		}
		
		removeNegativeInstances(positiveRectangle);
		
		greedyFeatureSelection(positiveRectangle,1);
		
		report(trainReportFileName, trainDataset, trainInstancesCopy, positiveRectangle, 0, bestFeatures);
		report(testReportFileName, testDataset, testInstances, positiveRectangle, 0, bestFeatures);
	}
	
	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------  Private Methods
	/////////////////////////////////////////////////////////////////
	
	private void removeNegativeInstances(double[][] positiveRectangle)
	{
		int numNegativeInstances = 0;
		
		do
		{
			ArrayList<int[]> count = new ArrayList<int[]>();
			ArrayList<IInstance> negativeInstances = new ArrayList<IInstance>();
			
			for(int i = 0; i < trainInstances.size(); i++)
				for(int j = 0; j < trainInstances.get(i).size(); j++)
					if(trainInstances.get(i).get(j).getValue(classIndex) == 1 && contains(positiveRectangle, trainInstances.get(i).get(j)))
					{
						negativeInstances.add(trainInstances.get(i).get(j));
						count.add(excludeCount(trainInstances.get(i).get(j),positiveRectangle));
					}
			
			numNegativeInstances = negativeInstances.size();
			
			if(numNegativeInstances != 0)
			{
				int min = Integer.MAX_VALUE;
				int bestInstance = -1;
				int feature = -1;
				
				for(int i = 0; i < count.size(); i++)
					for(int j = 0; j < numberFeatures; j++)
						if(count.get(i)[j] < min)
						{
							feature = j;
							min = count.get(i)[j];
							bestInstance = i;
						}
				
				removeAffectedInstances(negativeInstances.get(bestInstance), feature, positiveRectangle);
				
				for(int i = 0; i < trainInstances.size(); i++)
					if(trainInstances.get(i).contains(negativeInstances.get(bestInstance)))
							trainInstances.get(i).remove(negativeInstances.get(bestInstance));
				
				for(int i = 0; i < numberFeatures; i++)
				{
					positiveRectangle[i][0] = min(i,0);
					positiveRectangle[i][1] = max(i,0);
				}
			}

		}while(numNegativeInstances != 0);
	}
	
	@SuppressWarnings("unused")
	private void removePositiveInstances(double[][] negativeRectangle)
	{
		int numPositiveInstances = 0;
		
		do
		{
			ArrayList<int[]> count = new ArrayList<int[]>();
			ArrayList<IInstance> positiveInstances = new ArrayList<IInstance>();
			
			for(int i = 0; i < trainInstances.size(); i++)
				for(int j = trainInstances.get(i).size()-1; j >= 0; j--)
					if(trainInstances.get(i).get(j).getValue(classIndex) == 0 && contains(negativeRectangle, trainInstances.get(i).get(j)))
					{
						positiveInstances.add(trainInstances.get(i).get(j));
						count.add(excludeCount(trainInstances.get(i).get(j),negativeRectangle));
					}
			
			numPositiveInstances = positiveInstances.size();
			
			if(numPositiveInstances != 0)
			{
				int min = Integer.MAX_VALUE;
				int bestInstance = -1;
				int feature = -1;
				
				for(int i = 0; i < count.size(); i++)
					for(int j = 0; j < numberFeatures; j++)
						if(count.get(i)[j] < min)
						{
							min = count.get(i)[j];
							feature = j;
							bestInstance = i;
						}
				
				removeAffectedInstances(positiveInstances.get(bestInstance), feature, negativeRectangle);
				
				for(int i = 0; i < trainInstances.size(); i++)
					if(trainInstances.get(i).contains(positiveInstances.get(bestInstance)))
							trainInstances.get(i).remove(positiveInstances.get(bestInstance));
				
				for(int i = 0; i < numberFeatures; i++)
				{
					negativeRectangle[i][0] = min(i,1);
					negativeRectangle[i][1] = max(i,1);
				}
				
				count.clear();
				positiveInstances.clear();
			}

		}while(numPositiveInstances != 0);
	}
	
	private int[] excludeCount(IInstance instance, double[][] rectangle)
	{
		int Class = (int) instance.getValue(classIndex);
		int[] count = new int[numberFeatures];
		
		for(int i = 0; i < numberFeatures; i++)
		{
			if(Math.abs(instance.getValue(i+1) - rectangle[i][0]) < Math.abs(rectangle[i][1] - instance.getValue(i+1)))
			{
				for(int j = 0; j < trainInstances.size(); j++)
					for(int k = 0; k < trainInstances.get(j).size(); k++)
						if(trainInstances.get(j).get(k).getValue(classIndex) != Class && trainInstances.get(j).get(k).getValue(i+1) <= instance.getValue(i+1))
							count[i]++;
			}
			else
			{
				for(int j = 0; j < trainInstances.size(); j++)
					for(int k = 0; k < trainInstances.get(j).size(); k++)
						if(trainInstances.get(j).get(k).getValue(classIndex) != Class && trainInstances.get(j).get(k).getValue(i+1) >= instance.getValue(i+1))
							count[i]++;
			}
		}
		
		return count;
	}
	
	private void removeAffectedInstances(IInstance instance, int feature, double[][] rectangle)
	{
		int Class = (int) instance.getValue(classIndex);
		
		if(Math.abs(instance.getValue(feature+1) - rectangle[feature][0]) < Math.abs(rectangle[feature][1] - instance.getValue(feature+1)))
		{
			for(int j = 0; j < trainInstances.size(); j++)
				for(int k = trainInstances.get(j).size()-1; k >= 0; k--)
					if(trainInstances.get(j).get(k).getValue(classIndex) != Class && trainInstances.get(j).get(k).getValue(feature+1) <= instance.getValue(feature+1))
						trainInstances.get(j).remove(k);
		}
		else
		{
			for(int j = 0; j < trainInstances.size(); j++)
				for(int k = trainInstances.get(j).size()-1; k >= 0; k--)
					if(trainInstances.get(j).get(k).getValue(classIndex) != Class && trainInstances.get(j).get(k).getValue(feature+1) >= instance.getValue(feature+1))
						trainInstances.get(j).remove(k);
		}
	}
}
