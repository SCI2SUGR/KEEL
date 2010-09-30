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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

public class Main {
	public static void main(String args[]) {
		
		Properties props = new Properties();

		try {
			InputStream paramsFile = new FileInputStream(args[0]);
			props.load(paramsFile);
			paramsFile.close();			
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
		
		// Files training and test
		String trainFile;
		String testFile;
		StringTokenizer tokenizer = new StringTokenizer(props.getProperty("inputData"));
		tokenizer.nextToken();
		trainFile = tokenizer.nextToken();
		trainFile = trainFile.substring(1, trainFile.length()-1);
		testFile = tokenizer.nextToken();
		testFile = testFile.substring(1, testFile.length()-1);
		
		tokenizer = new StringTokenizer(props.getProperty("outputData"));
		String reportTrainFile = tokenizer.nextToken();
		reportTrainFile = reportTrainFile.substring(1, reportTrainFile.length()-1);
		String reportTestFile = tokenizer.nextToken();
		reportTestFile = reportTestFile.substring(1, reportTestFile.length()-1);	
		
		try {
			
			DD algorithm = new DD();
			
			algorithm.setTrainReportFileName(reportTrainFile);
			algorithm.setTestReportFileName(reportTestFile);
			algorithm.setDatasetSettings(trainFile,testFile);

			algorithm.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}