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

package keel.Algorithms.MIL.APR.GFS_AllPositive_APR;

import keel.Algorithms.MIL.APR.AbstractAPR;

public class GFS_AllPositive_APR extends AbstractAPR
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
		
		greedyFeatureSelection(positiveRectangle,1);
		
		report(trainReportFileName, trainDataset, trainInstancesCopy, positiveRectangle, 0, bestFeatures);
		report(testReportFileName, testDataset, testInstances, positiveRectangle, 0, bestFeatures);
	}
}
