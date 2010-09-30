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

package keel.Algorithms.MIL.Nearest_Neighbour.KNN;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import keel.Algorithms.MIL.Nearest_Neighbour.AbstractNearestNeighbour;

import net.sourceforge.jclec.util.dataset.IDataset;
import net.sourceforge.jclec.util.dataset.KeelDataSet;
import net.sourceforge.jclec.util.dataset.IDataset.IInstance;

public class KNN extends AbstractNearestNeighbour
{
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public Methods
	/////////////////////////////////////////////////////////////////

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

	private int deriveClass(ArrayList<IInstance> bag, int numReferences)
	{
		int[] references = references(bag,numReferences);
		
		int rp = 0, rn = 0;
		
		for(int i = 0; i < references.length; i++)
		{
			if(trainInstances.get(references[i]).get(0).getValue(classIndex) == 0)
				rp++;
			else
				rn++;
		}
		
		if(rp > rn)
			return 0;
		else
			return 1;
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
				predictedClass = deriveClass(instances.get(i),numberReferences);
				
				writer.write((int)instances.get(i).get(0).getValue(classIndex) + " " + predictedClass + newline);
			}
			
			writer.close();
			
		} catch (Exception e) {e.printStackTrace();}
	}
}
