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

package keel.Algorithms.MIL.APR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import keel.Algorithms.MIL.AbstractMIAlgorithm;

import net.sourceforge.jclec.util.dataset.IDataset;
import net.sourceforge.jclec.util.dataset.KeelDataSet;
import net.sourceforge.jclec.util.dataset.IDataset.IInstance;

public abstract class AbstractAPR extends AbstractMIAlgorithm
{
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	protected ArrayList<Integer> bestFeatures = new ArrayList<Integer>();

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------  Protected Methods
	/////////////////////////////////////////////////////////////////
	
	protected void greedyFeatureSelection(double[][] rectangle, int Class)
	{
		int maxCount = 0, bestFeature = 0;
		
		do
		{
			int[] features = discriminatingFeatures(rectangle,Class);
			
			maxCount = 0;
			
			for(int i = 0; i < numberFeatures; i++)
				if(features[i] > maxCount)
				{
					maxCount = features[i];
					bestFeature = i;
				}
			
			if(maxCount > 0)
			{
				removeInstances(rectangle,Class,bestFeature);
				bestFeatures.add(bestFeature);
			}
		}while(maxCount > 0);
	}
	
	protected int[] discriminatingFeatures(double[][] rectangle, int Class)
	{
		int[] features = new int[numberFeatures];
		
		for(int i = 0; i < numberFeatures; i++)
			if(!bestFeatures.contains(i))
				for(int j = 0; j < trainInstances.size(); j++)
					for(int k = 0; k < trainInstances.get(j).size(); k++)
						if(trainInstances.get(j).get(k).getValue(classIndex) == Class)
							if(trainInstances.get(j).get(k).getValue(i+1) < rectangle[i][0] || trainInstances.get(j).get(k).getValue(i+1) > rectangle[i][1])
								features[i]++;
		return features;
	}
	
	protected void removeInstances(double[][] rectangle, int Class, int feature)
	{
		ArrayList<int[]> toRemove = new ArrayList<int[]>();
		
		for(int j = 0; j < trainInstances.size(); j++)
			for(int k = trainInstances.get(j).size()-1; k >= 0; k--)
				if(trainInstances.get(j).get(k).getValue(classIndex) == Class)
					if(trainInstances.get(j).get(k).getValue(feature+1) < rectangle[feature][0] || trainInstances.get(j).get(k).getValue(feature+1) > rectangle[feature][1])
						toRemove.add(new int[]{j,k});
		
		for(int i = 0; i < toRemove.size(); i++)
			trainInstances.get(toRemove.get(i)[0]).remove(toRemove.get(i)[1]);
	}
	
	protected boolean contains(double[][] rectangle, IInstance instance)
	{
		for(int i = 0; i < numberFeatures; i++)
			if(instance.getValue(i+1) < rectangle[i][0] || instance.getValue(i+1) > rectangle[i][1])
				return false;
		
		return true;
	}
	
	protected boolean contains(double[][] rectangle, IInstance instance, ArrayList<Integer> features)
	{
		for(int i = 0; i < features.size(); i++)
			if(instance.getValue(features.get(i)+1) < rectangle[features.get(i)][0] || instance.getValue(features.get(i)+1) > rectangle[features.get(i)][1])
				return false;
		
		return true;
	}

	protected double min(int attribute, int Class)
	{
		double min = Double.MAX_VALUE;
		
		for(int i = 0; i < trainInstances.size(); i++)
			for(int j = 0; j < trainInstances.get(i).size(); j++)
			{
				if(trainInstances.get(i).get(j).getValue(classIndex) == Class && trainInstances.get(i).get(j).getValue(attribute+1) < min)
					min = trainInstances.get(i).get(j).getValue(attribute+1);
			}
		
		return min;
	}
	
	protected double max(int attribute, int Class)
	{
		double max = -Double.MAX_VALUE;
		
		for(int i = 0; i < trainInstances.size(); i++)
			for(int j = 0; j < trainInstances.get(i).size(); j++)
			{
				if(trainInstances.get(i).get(j).getValue(classIndex) == Class && trainInstances.get(i).get(j).getValue(attribute+1) > max)
					max = trainInstances.get(i).get(j).getValue(attribute+1);
			}
		
		return max;
	}

	protected void report(String reportFileName, IDataset dataset, ArrayList<ArrayList<IInstance>> instances, double[][] rectangle, int Class)
	{
		int predictedClass;
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
				predictedClass = 0;
				if(Class == 0)	predictedClass = 1;
				
				for(int j = 0; j < instances.get(i).size(); j++)
				{
					if(contains(rectangle, instances.get(i).get(j)))
					{
						predictedClass = Class;
						break;
					}
				}
				
				writer.write((int)instances.get(i).get(0).getValue(classIndex) + " " + predictedClass + newline);
			}
			
			writer.close();
			
		} catch (Exception e) {e.printStackTrace();}
	}
	
	protected void report(String reportFileName, IDataset dataset, ArrayList<ArrayList<IInstance>> instances, double[][] rectangle, int Class, ArrayList<Integer> features)
	{
		int predictedClass;
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
				predictedClass = 0;
				if(Class == 0)	predictedClass = 1;
				
				for(int j = 0; j < instances.get(i).size(); j++)
				{
					if(contains(rectangle, instances.get(i).get(j), features))
					{
						predictedClass = Class;
						break;
					}
				}
				
				writer.write((int)instances.get(i).get(0).getValue(classIndex) + " " + predictedClass + newline);
			}
			
			writer.close();
			
		} catch (Exception e) {e.printStackTrace();}
	}
}
