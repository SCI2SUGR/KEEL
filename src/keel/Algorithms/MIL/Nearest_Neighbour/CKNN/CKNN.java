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

package keel.Algorithms.MIL.Nearest_Neighbour.CKNN;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import keel.Algorithms.MIL.Nearest_Neighbour.AbstractNearestNeighbour;

import net.sourceforge.jclec.util.dataset.IDataset;
import net.sourceforge.jclec.util.dataset.KeelDataSet;
import net.sourceforge.jclec.util.dataset.IDataset.IInstance;

public class CKNN extends AbstractNearestNeighbour
{
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	private int numberCiters = 4;

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public Methods
	/////////////////////////////////////////////////////////////////

	public void setNumberCiters(int numberCiters) {
		this.numberCiters = numberCiters;
	}

	public void execute() throws Exception
	{
		loadTrainDataset();
		loadTestDataset();
		
		report(trainReportFileName, trainDataset, trainInstances);
		report(testReportFileName, testDataset, testInstances);
	}
	
	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------  Private Methods
	/////////////////////////////////////////////////////////////////

	private int deriveClass(ArrayList<IInstance> bag, int numReferences, int numCiters)
	{
		int[] references = references(bag,numReferences);
		int[] citers = citers(bag,numCiters);
		
		int rp = 0, rn = 0, cp = 0, cn = 0;
		
		for(int i = 0; i < references.length; i++)
		{
			if(trainInstances.get(references[i]).get(0).getValue(classIndex) == 0)
				rp++;
			else
				rn++;
		}
		
		for(int i = 0; i < citers.length; i++)
		{
			if(trainInstances.get(citers[i]).get(0).getValue(classIndex) == 0)
				cp++;
			else
				cn++;
		}
		
		if(rp + cp > rn + cn)
			return 0;
		else
			return 1;
	}
	
	private int[] citers(ArrayList<IInstance> bag, int numCiters)
	{
		int numberTrainBags = trainInstances.size();
		int[][] references = new int[numberTrainBags][numCiters];
		
		trainInstances.add(bag);
		
		for(int i = 0; i < numberTrainBags; i++)
		{
			references[i] = references(trainInstances.get(i), numCiters);
		}
		
		trainInstances.remove(trainInstances.size() - 1);
		
		ArrayList<Integer> citersArray = new ArrayList<Integer>();
		
		for(int i = 0; i < numberTrainBags; i++)
			for(int j = 0; j < numCiters; j++)
				if(references[i][j] == trainInstances.size())
					citersArray.add(i);
		
		int[] citers = new int[citersArray.size()];
		
		for(int i = 0; i < citersArray.size(); i++)
			citers[i] = citersArray.get(i);
		
		return citers;
	}
	
	private void report(String reportFileName, IDataset dataset, ArrayList<ArrayList<IInstance>> instances)
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
				predictedClass = deriveClass(instances.get(i),numberReferences,numberCiters);
				
				writer.write((int)instances.get(i).get(0).getValue(classIndex) + " " + predictedClass + newline);
			}
			
			writer.close();
			
		} catch (Exception e) {e.printStackTrace();}
	}
}
