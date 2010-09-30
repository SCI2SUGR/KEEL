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

package keel.Algorithms.MIL;

import java.util.ArrayList;

import net.sourceforge.jclec.util.dataset.KeelDataSet;
import net.sourceforge.jclec.util.dataset.IDataset.IInstance;

public abstract class AbstractMIAlgorithm
{
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	protected ArrayList<ArrayList<IInstance>> trainInstances, testInstances, trainInstancesCopy;
	
	protected String trainReportFileName = "reportTra.txt";
	
	protected String testReportFileName = "reportTst.txt";

	protected static KeelDataSet trainDataset, testDataset;
	
	protected int classIndex, numberFeatures;
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////	
	
	public abstract void execute() throws Exception;
	
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////	
	
	public void setTrainReportFileName(String trainReportFileName) {
		this.trainReportFileName = trainReportFileName;
	}

	public void setTestReportFileName(String testReportFileName) {
		this.testReportFileName = testReportFileName;
	}
	
	public int getNumberFeatures() {
		return numberFeatures;
	}
	
	public int getClassIndex() {
		return classIndex;
	}
	
	public ArrayList<ArrayList<IInstance>> getTrainInstances() {
		return trainInstances;
	}
	
	public void setDatasetSettings(String trainDatasetfile, String testDatasetfile) throws Exception
	{
		trainDataset = new KeelDataSet();
		trainDataset.setFileName(trainDatasetfile);
		trainDataset.open();
		trainDataset.loadInstances();
		trainDataset.close();

		testDataset = new KeelDataSet();
		testDataset.setFileName(testDatasetfile);
		testDataset.open();
		testDataset.loadInstances();
		testDataset.close();
	}
	
	@SuppressWarnings("unchecked")
	protected void loadTrainDataset()
	{
		double bag = -1.0;
		
		trainInstances = new ArrayList<ArrayList<IInstance>>();
		classIndex = trainDataset.getMetadata().numberOfAttributes() - 1;
		numberFeatures = trainDataset.getMetadata().numberOfAttributes() - 2;
		
		for(int i = 0; i < trainDataset.getInstances().length; i++)
		{
			if (bag != trainDataset.getInstances()[i].getValue(0))
				bag = trainDataset.getInstances()[i].getValue(0);
			
			ArrayList<IInstance> instances = new ArrayList<IInstance>();
			
			for(int j = i; j < trainDataset.getInstances().length; j++)
			{
				if(trainDataset.getInstances()[j].getValue(0) == bag)
					instances.add(trainDataset.getInstances()[j].copy());
				else
				{
					trainInstances.add((ArrayList<IInstance>) instances.clone());
					i = j-1;
					break;
				}
				if(j+1 == trainDataset.getInstances().length)
				{
					trainInstances.add((ArrayList<IInstance>) instances.clone());
					
					trainInstancesCopy = new ArrayList<ArrayList<IInstance>>();
					for(int k = 0; k < trainInstances.size(); k++)
						trainInstancesCopy.add((ArrayList<IInstance>) trainInstances.get(k).clone());
					return;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void loadTestDataset()
	{
		double bag = -1.0;
		
		testInstances = new ArrayList<ArrayList<IInstance>>();
		
		for(int i = 0; i < testDataset.getInstances().length; i++)
		{
			if (bag != testDataset.getInstances()[i].getValue(0))
				bag = testDataset.getInstances()[i].getValue(0);
			
			ArrayList<IInstance> instances = new ArrayList<IInstance>();
			
			for(int j = i; j < testDataset.getInstances().length; j++)
			{
				if(testDataset.getInstances()[j].getValue(0) == bag)
					instances.add(testDataset.getInstances()[j].copy());
				else
				{
					testInstances.add((ArrayList<IInstance>) instances.clone());
					i = j-1;
					break;
				}
				if(j+1 == testDataset.getInstances().length)
				{
					testInstances.add((ArrayList<IInstance>) instances.clone());
					return;
				}
			}
		}
	}
}