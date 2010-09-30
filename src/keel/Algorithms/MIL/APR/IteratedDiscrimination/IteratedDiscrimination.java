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

package keel.Algorithms.MIL.APR.IteratedDiscrimination;

import java.util.ArrayList;

import keel.Algorithms.MIL.APR.AbstractAPR;

import net.sourceforge.jclec.util.dataset.IDataset.IInstance;

public class IteratedDiscrimination extends AbstractAPR
{
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	private ArrayList<IInstance> instancesCovered = new ArrayList<IInstance>();
	
	private double[][][] minmaxRectangles;
	
	private boolean[] bagsCovered;

	private double alpha = 1.0;
	
	private double epsilon = 0.01;
	
	private double tau = 0.99;
	
	private double densityEstimation;

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public Methods
	/////////////////////////////////////////////////////////////////
	
	public void execute()
	{
		loadTrainDataset();
		loadTestDataset();
		
		densityEstimation = densityEstimation();
		
		minmaxRectangles = new double[2][numberFeatures][2];
		
		for(int i = 0; i < numberFeatures; i++)
		{
			minmaxRectangles[0][i][0] = minmax(i,0);
			minmaxRectangles[0][i][1] = maxmin(i,0);
		}
		
		double[][] positiveRectangle = iterateDiscrim(0);
		
		report(trainReportFileName, trainDataset, trainInstancesCopy, positiveRectangle, 0, bestFeatures);
		report(testReportFileName, testDataset, testInstances, positiveRectangle, 0, bestFeatures);
	}
	
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public void setTau(double tau) {
		this.tau = tau;
	}
	
	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------  Private Methods
	/////////////////////////////////////////////////////////////////
	
	private double[][] iterateDiscrim(int Class)
	{
		double minDistance = Double.MAX_VALUE;
		int seedBag = 0, seedInstance = 0;
		
		for(int i = 0; i < trainInstances.size(); i++)
			for(int j = 0; j < trainInstances.get(i).size(); j++)
				if(trainInstances.get(i).get(j).getValue(classIndex) == Class)
				{
					double distance = distanceRectangle(trainInstances.get(i).get(j).getValues(),minmaxRectangles[Class]);
					if(distance < minDistance)
					{
						minDistance = distance;
						seedBag = i;
						seedInstance = j;
					}
				}
		
		double[][] rectangle =  iterate(seedBag,seedInstance, Class);
		double size = size(rectangle, bestFeatures);
		
		while(true)
		{
			rectangle = iterate(seedBag,seedInstance, Class);
			double newSize = size(rectangle, bestFeatures);
			
			if(size == newSize)
				break;
			else
				size = newSize;
		}
		
		expand(rectangle);
		
		return rectangle;
	}

	private void expand(double[][] rectangle)
	{
		double[] mean = new double[bestFeatures.size()];
		double[] variance = new double[bestFeatures.size()];
		
		for(int i = 0; i < bestFeatures.size(); i++)
		{
			mean[i] = variance[i] = 0.0;
			int numInstances = 0;
			
			for(int j = 0; j < trainInstances.size(); j++)
			for(int k = 0; k < trainInstances.get(j).size(); k++)
			{
				mean[i] += trainInstances.get(j).get(k).getValue(bestFeatures.get(i)+1);
				variance[i] += trainInstances.get(j).get(k).getValue(bestFeatures.get(i)+1) * trainInstances.get(j).get(k).getValue(bestFeatures.get(i)+1);
				numInstances++;
			}
			
			mean[i] = mean[i] / numInstances;
			variance[i] = (variance[i] - numInstances * mean[i] * mean[i]) / (numInstances-1);
			
			rectangle[bestFeatures.get(i)][0] = densityEstimation * Math.sqrt(variance[i]) + mean[i];
			rectangle[bestFeatures.get(i)][1] = mean[i] + Math.abs(mean[i] - rectangle[bestFeatures.get(i)][0]);
		}
	}
	
	private double[][] iterate(int seedBag, int seedInstance, int Class)
	{
		bagsCovered = new boolean[trainInstances.size()];
		
		bagsCovered[seedBag] = true;
		
		instancesCovered = new ArrayList<IInstance>();
		
		instancesCovered.add(trainInstances.get(seedBag).get(seedInstance));
		
		int otherClass = 0;
		if(Class == 0)	otherClass = 1;
		
		double[][] rectangle = backfitting(Class);
		
		int maxCount = 0, bestFeature = 0;
		
		do
		{
			int[] features = discriminatingFeatures(rectangle,otherClass);
			
			maxCount = 0;
			
			for(int i = 0; i < numberFeatures; i++)
				if(features[i] > maxCount)
				{
					maxCount = features[i];
					bestFeature = i;
				}
			
			if(maxCount > 0)
			{
				removeInstances(rectangle,otherClass,bestFeature);
				bestFeatures.add(bestFeature);
			}
		}while(maxCount > 0);
		
		return rectangle;
	}

	@Override
	protected int[] discriminatingFeatures(double[][] rectangle, int Class)
	{
		int[] features = new int[numberFeatures];
		
		for(int i = 0; i < numberFeatures; i++)
			if(!bestFeatures.contains(i))
				for(int j = 0; j < trainInstances.size(); j++)
					for(int k = 0; k < trainInstances.get(j).size(); k++)
						if(trainInstances.get(j).get(k).getValue(classIndex) == Class)
						{
							double distance = Math.min(Math.abs(rectangle[i][0] - trainInstances.get(j).get(k).getValue(i+1)), Math.abs(trainInstances.get(j).get(k).getValue(i+1) - rectangle[i][1]));
							
							if(distance >= alpha *(rectangle[i][1] - rectangle[i][0]))
								features[i]++;
							else if(i == furtherFeature(rectangle,trainInstances.get(j).get(k)))
								features[i]++;
						}
		
		return features;
	}
	
	@Override
	protected void removeInstances(double[][] rectangle, int Class, int feature)
	{
		ArrayList<int[]> toRemove = new ArrayList<int[]>();
		
		for(int j = 0; j < trainInstances.size(); j++)
			for(int k = trainInstances.get(j).size()-1; k >= 0; k--)
				if(trainInstances.get(j).get(k).getValue(classIndex) == Class)
				{
					double distance = Math.min(Math.abs(rectangle[feature][0] - trainInstances.get(j).get(k).getValue(feature+1)), Math.abs(trainInstances.get(j).get(k).getValue(feature+1) - rectangle[feature][1]));
					
					if(distance >= alpha *(rectangle[feature][1] - rectangle[feature][0]))
						toRemove.add(new int[]{j,k});
					else if(feature == furtherFeature(rectangle,trainInstances.get(j).get(k)))
						toRemove.add(new int[]{j,k});
				}
		
		for(int i = 0; i < toRemove.size(); i++)
			trainInstances.get(toRemove.get(i)[0]).remove(toRemove.get(i)[1]);
	}
	
	private int furtherFeature(double[][] rectangle, IInstance instance)
	{
		double max = Double.MIN_VALUE;
		int feature = -1;
		
		for(int i = 0; i < numberFeatures; i++)
		{
			if(instance.getValue(i+1) < rectangle[i][0] || instance.getValue(i+1) > rectangle[i][1])	// SI LA INSTANCIA NEGATIVA CAE DENTRO NO LA DISCRIMINA POR LO TANTO NO ELIMINARA TODAS LAS NEGATIVAS, SE QEDARAN SI CAEN DENTRO DEL RECTANGULO
			{
				double distance = Math.min(Math.abs(rectangle[i][0] - instance.getValue(i+1)), Math.abs(instance.getValue(i+1) - rectangle[i][1]));
				
				if(distance > max)
				{
					max = distance;
					feature = i;
				}
			}
		}
		return feature;
	}

	private double[][] backfitting(int Class)
	{
		int bag = 0, instance = 0;
		double APR[][] = new double[numberFeatures][2];
		double minSize = Double.MAX_VALUE;
		
		while(true)
		{
			minSize = Double.MAX_VALUE;
			
			for(int i = 0; i < trainInstances.size(); i++)
				if(trainInstances.get(i).size() != 0 && trainInstances.get(i).get(0).getValue(classIndex) == Class)
					for(int j = 0; j < trainInstances.get(i).size(); j++)
						if(bagsCovered[i] == false)
						{
							instancesCovered.add(trainInstances.get(i).get(j));
							double auxAPR[][] = new double[numberFeatures][2];
							
							for(int k = 0; k < numberFeatures; k++)
							{
								auxAPR[k][0] = min(instancesCovered,k);
								auxAPR[k][1] = max(instancesCovered,k);
							}
							
							double size;
							
							if(bestFeatures.size() == 0)
								size = size(auxAPR);
							else
								size = size(auxAPR,bestFeatures);
							
							if(size < minSize)
							{
								minSize = size;
								bag = i;
								instance = j;
							}
							
							instancesCovered.remove(instancesCovered.size()-1);
						}
			
			instancesCovered.add(trainInstances.get(bag).get(instance));
			bagsCovered[bag] = true;
			
			int revisedInstanceIndex;
			IInstance revisedInstance, auxInstance = null;
			
			for(int i = 1; i < instancesCovered.size()-1; i++)
			{
				
				double APRAT2[][] = new double[numberFeatures][2];
				
				for(int k = 0; k < numberFeatures; k++)
				{
					APRAT2[k][0] = min(instancesCovered,k);
					APRAT2[k][1] = max(instancesCovered,k);
				}
				
				minSize = Double.MAX_VALUE;
				revisedInstanceIndex = i;
				revisedInstance = instancesCovered.remove(i);
				
				for(int j = 0; j < trainInstances.size(); j++)
					if(trainInstances.get(j).contains(revisedInstance))
					{
						for(IInstance inst : trainInstances.get(j))
						{
							instancesCovered.add(inst);
							
							double APRAT[][] = new double[numberFeatures][2];
							
							for(int k = 0; k < numberFeatures; k++)
							{
								APRAT[k][0] = min(instancesCovered,k);
								APRAT[k][1] = max(instancesCovered,k);
							}
							
							double size;
							
							if(bestFeatures.size() == 0)
								size = size(APRAT);
							else
								size = size(APRAT,bestFeatures);
							
							if(size < minSize)
							{
								minSize = size;
								auxInstance = inst;
							}
							
							instancesCovered.remove(instancesCovered.size()-1);
						}
						break;
					}
				
				instancesCovered.add(revisedInstanceIndex, auxInstance);
			}
			
			boolean finished = true;
			for(int i = 0; i < bagsCovered.length; i++)
				if(bagsCovered[i] == false && trainInstances.get(i).size() != 0 && trainInstances.get(i).get(0).getValue(classIndex) == Class)
					finished = false;
			if(finished)
				break;
		}
		
		for(int k = 0; k < numberFeatures; k++)
		{
			APR[k][0] = min(instancesCovered,k);
			APR[k][1] = max(instancesCovered,k);
		}
		
		return APR;
	}
	
	@SuppressWarnings("unused")
	private double[][] grow(int Class)
	{
		int bag = 0, instance = 0;
		double APR[][] = new double[numberFeatures][2];
		
		while(true)
		{
			double minSize = Double.MAX_VALUE;
			
			for(int i = 0; i < trainInstances.size(); i++)
				if(trainInstances.get(i).size() != 0 && trainInstances.get(i).get(0).getValue(classIndex) == Class)
					for(int j = 0; j < trainInstances.get(i).size(); j++)
						if(bagsCovered[i] == false)
						{
							instancesCovered.add(trainInstances.get(i).get(j));
							double auxAPR[][] = new double[numberFeatures][2];
							
							for(int k = 0; k < numberFeatures; k++)
							{
								auxAPR[k][0] = min(instancesCovered,k);
								auxAPR[k][1] = max(instancesCovered,k);
							}
							
							double size;
							
							if(bestFeatures.size() == 0)
								size = size(auxAPR);
							else
								size = size(auxAPR,bestFeatures);
							
							if(size < minSize)
							{
								minSize = size;
								bag = i;
								instance = j;
							}
							
							instancesCovered.remove(instancesCovered.size()-1);
						}
			
			instancesCovered.add(trainInstances.get(bag).get(instance));
			bagsCovered[bag] = true;
			
			boolean finished = true;
			for(int i = 0; i < bagsCovered.length; i++)
				if(bagsCovered[i] == false && trainInstances.get(i).size() != 0 && trainInstances.get(i).get(0).getValue(classIndex) == Class)
					finished = false;
			if(finished)
				break;
		}
		
		for(int k = 0; k < numberFeatures; k++)
		{
			APR[k][0] = min(instancesCovered,k);
			APR[k][1] = max(instancesCovered,k);
		}
		
		return APR;
	}

	private double size(double[][] rectangle)
	{
		double size = 0;
		for(int i = 0; i < rectangle.length; i++)
			size += rectangle[i][1] - rectangle[i][0];
		return size;
	}
	
	private double size(double[][] rectangle, ArrayList<Integer> features)
	{
		double size = 0;
		for(int i = 0; i < features.size(); i++)
			size += rectangle[features.get(i)][1] - rectangle[features.get(i)][0];
		return size;
	}

	private double distanceRectangle(double[] values, double[][] rectangle) 
	{
		double distance = 0;
		for(int i = 0; i < rectangle.length; i++)
			distance += Math.abs(values[i] - (rectangle[i][1] - rectangle[i][0])/2);
		return distance;			
	}

	protected double minmax(int attribute, int Class)
	{
		double min = Double.MAX_VALUE;
		
		for(int i = 0; i < trainInstances.size(); i++)
		{
			if(trainInstances.get(i).size() != 0 && trainInstances.get(i).get(0).getValue(classIndex) == Class)
			{
				double max = -Double.MAX_VALUE;
				
				for(int j = 0; j < trainInstances.get(i).size(); j++)
					if(trainInstances.get(i).get(j).getValue(attribute+1) > max)
						max = trainInstances.get(i).get(j).getValue(attribute+1);
				
				if(max < min)
					min = max;
			}
		}
		
		return min;
	}
	
	protected double maxmin(int attribute, int Class)
	{
		double max = -Double.MAX_VALUE;
		
		for(int i = 0; i < trainInstances.size(); i++)
		{
			if(trainInstances.get(i).size() != 0 && trainInstances.get(i).get(0).getValue(classIndex) == Class)
			{
				double min = Double.MAX_VALUE;
				
				for(int j = 0; j < trainInstances.get(i).size(); j++)
					if(trainInstances.get(i).get(j).getValue(classIndex) == Class && trainInstances.get(i).get(j).getValue(attribute+1) < min)
						min = trainInstances.get(i).get(j).getValue(attribute+1);
				
				if(min > max)
					max = min;
			}
		}
		
		return max;
	}
	
	protected double min(ArrayList<IInstance> instancesCovered, int attribute)
	{
		double min = Double.MAX_VALUE;
		
		for(int i = 0; i < instancesCovered.size(); i++)
			if(instancesCovered.get(i).getValue(attribute+1) < min)
				min = instancesCovered.get(i).getValue(attribute+1);
		
		return min;
	}
	
	protected double max(ArrayList<IInstance> instancesCovered, int attribute)
	{
		double max = -Double.MAX_VALUE;
		
		for(int i = 0; i < instancesCovered.size(); i++)
			if(instancesCovered.get(i).getValue(attribute+1) > max)
				max = instancesCovered.get(i).getValue(attribute+1);
		
		return max;
	}
	
	private double densityEstimation()
	{
		double probability = tau + epsilon/2.0;
		
		double[] normalDistribution = new double[]{0.5, 0.5398, 0.5793, 0.6179, 0.6554, 0.6915, 0.7257, 0.758, 0.7881, 0.8159, 0.8413, 0.8643, 0.8849, 0.9032, 0.9192, 0.9332, 0.9452, 0.9554, 0.9641, 0.9713, 0.9772, 0.9821, 0.9861, 0.9893, 0.9918, 0.9938, 0.9953, 0.9965, 0.9974, 0.9981, 0.9987, 0.999, 0.9993, 0.9995, 0.9997, 0.9998, 0.9998, 0.9999, 0.9999, 1};
		
		for(int i = 0; i < normalDistribution.length-1; i++)
			if(normalDistribution[i] <= probability && normalDistribution[i+1] > probability)
				return -(i+1)/10.0;
		
		return -3.3;
	}
}
