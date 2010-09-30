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

package keel.Algorithms.MIL.Nearest_Neighbour;

import java.util.ArrayList;
import java.util.Arrays;

import keel.Algorithms.MIL.AbstractMIAlgorithm;

import net.sourceforge.jclec.util.dataset.IDataset.IInstance;

public abstract class AbstractNearestNeighbour extends AbstractMIAlgorithm
{
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	protected boolean HausdorffMaxDistance = false; // True -> HausdorffMaxDistance, False -> HausdorffMinDistance

	protected int numberReferences = 2;

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public Methods
	/////////////////////////////////////////////////////////////////

	public void setNumberReferences(int numberReferences) {
		this.numberReferences = numberReferences;
	}
	
	public void setHausdorffMaxDistance(boolean hausdorffMaxDistance) {
		HausdorffMaxDistance = hausdorffMaxDistance;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------  Private Methods
	/////////////////////////////////////////////////////////////////

	protected double distance(double[] instanceA, double[] instanceB)
	{
		double result = 0.0;
		
		for(int i = 1; i < instanceA.length - 1; i++)
		{
			result += Math.pow(instanceA[i] - instanceB[i],2);
		}
		
		return Math.sqrt(result);
	}
	
	protected double HausdorffMaxDistance(ArrayList<IInstance> bagA, ArrayList<IInstance> bagB)
	{
		double distance, max = Double.MIN_VALUE;
		
		for(int i = 0; i < bagA.size(); i++)
		{
			for(int j = 0; j < bagB.size(); j++)
			{
				distance = distance(bagA.get(i).getValues(),bagB.get(j).getValues());
				
				if(distance > max)
					max = distance;
			}
		}
		
		return max;
	}
	
	protected double HausdorffMinDistance(ArrayList<IInstance> bagA, ArrayList<IInstance> bagB)
	{
		double distance, min = Double.MAX_VALUE;
		
		for(int i = 0; i < bagA.size(); i++)
		{
			for(int j = 0; j < bagB.size(); j++)
			{
				distance = distance(bagA.get(i).getValues(),bagB.get(j).getValues());
				
				if(distance < min)
					min = distance;
			}
		}
		
		return min;
	}	
	
	protected int[] references(ArrayList<IInstance> bag, int numReferences)
	{
		double[] distances = new double[trainInstances.size()];
		double[] references = new double[trainInstances.size()];
		int[] results = new int[numReferences];
		
		if(HausdorffMaxDistance)
			for(int i = 0; i < trainInstances.size(); i++)
				distances[i] = HausdorffMaxDistance(bag, trainInstances.get(i));
		else
			for(int i = 0; i < trainInstances.size(); i++)
				distances[i] = HausdorffMinDistance(bag, trainInstances.get(i));
		
		for(int i = 0; i < trainInstances.size(); i++)
			references[i] = distances[i];
		
		Arrays.sort(distances);
		
		for(int i = 0; i < numReferences; i++)
		{
			for(int k = 0; k < trainInstances.size(); k++)
			{
				if(distances[i] == references[k])
				{
					results[i] = k;
					references[k] = -1;
					break;
				}
			}
		}
		
		return results;
	}
}
