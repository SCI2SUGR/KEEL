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

/**
* <p>
* @author Written by Manuel Moreno (Universidad de Córdoba) 01/07/2008
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.CART.dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import keel.Algorithms.Neural_Networks.NNEP_Common.data.AttributeType;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.CategoricalAttribute;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.DatasetException;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.IAttribute;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.IMetadata;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.IntegerNumericalAttribute;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.KeelDataSet;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.RealNumericalAttribute;
import net.sf.jclec.util.intset.Interval;

/**
 * This class helps managing the conversion from KeelDataset to DoubleTransposedDataset
 *
 */
public class DataSetManager 
{	
	/** Data set header */
	private static String header;

	/** Output attribute */
	private static IAttribute outputAttribute; 
	
	
	/**
	 * It returns the header
	 * 
	 * @return the header
	 */
	public static String getHeader() {
		return header;
	}


	/**
	 * 
	 * It returns the output attribute
	 * 
	 * @return the outputAttribute
	 */
	public static IAttribute getOutputAttribute() {
		return outputAttribute;
	}

	/**
	 * Reads schema from the KEEL file
	 * 
	 * @param fileName Name of the KEEL dataset file
	 */

	public static byte[] readSchema(String fileName) throws IOException, DatasetException{

		KeelDataSet dataset = new KeelDataSet(fileName);
		dataset.open();		

		File file = new File(fileName);

		List<String> inputIds = new ArrayList<String>();
		List<String> outputIds = new ArrayList<String>();

		Reader reader = new BufferedReader(new FileReader(file));			
		String line = ((BufferedReader) reader).readLine();
		StringTokenizer elementLine = new StringTokenizer(line);
		String element = elementLine.nextToken();

		while (!element.equalsIgnoreCase("@data")){

			if(element.equalsIgnoreCase("@inputs")){
				while(elementLine.hasMoreTokens()){
					StringTokenizer commaTokenizer = new StringTokenizer(elementLine.nextToken(),",");
					while(commaTokenizer.hasMoreTokens())
						inputIds.add(commaTokenizer.nextToken());
				}
			}
			else if(element.equalsIgnoreCase("@outputs")){					
				while(elementLine.hasMoreTokens()){
					StringTokenizer commaTokenizer = new StringTokenizer(elementLine.nextToken(),",");
					while(commaTokenizer.hasMoreTokens())
						outputIds.add(commaTokenizer.nextToken());	
				}
			}

			// Next line of the file
			line = ((BufferedReader) reader).readLine();
			while(line.startsWith("%") || line.equalsIgnoreCase(""))
				line = ((BufferedReader) reader).readLine();

			elementLine = new StringTokenizer(line);
			element = elementLine.nextToken();
		}

		IMetadata metadata = dataset.getMetadata();
		byte[] schema = new byte[metadata.numberOfAttributes()];

		if(inputIds.isEmpty() || outputIds.isEmpty()){
			for(int i=0; i<schema.length; i++){
				if(i!=(schema.length-1))
					schema[i] = 1;
				else{
					outputAttribute = metadata.getAttribute(i);
					schema[i] = 2;
					//consoleReporter.setOutputAttribute(outputAttribute);
				}
			}
		}
		else{
			for(int i=0; i<schema.length; i++){
				if(inputIds.contains(metadata.getAttribute(i).getName()))
					schema[i] = 1;
				else if(outputIds.contains(metadata.getAttribute(i).getName())){
					outputAttribute = metadata.getAttribute(i);
					schema[i] = 2;
					//consoleReporter.setOutputAttribute(outputAttribute);
				}
				else
					schema[i] = -1;
			}
		}
		

		
		StringBuffer sheader = new StringBuffer();
		sheader.append("@relation " + dataset.getName() + "\n");
		for(int i=0; i<metadata.numberOfAttributes(); i++){
			IAttribute attribute = metadata.getAttribute(i);
			sheader.append("@attribute " + attribute.getName() +" ");
			if(attribute.getType() == AttributeType.Categorical ){
				CategoricalAttribute catAtt = (CategoricalAttribute) attribute;
				
				Interval interval = catAtt.intervalValues();
				
				sheader.append("{");
				for(int j=(int)interval.getLeft(); j<=interval.size()+1; j++){
					sheader.append( catAtt.show(j)+ (j!=interval.size()+1?",":"}\n"));
				}
			}
			else if(attribute.getType() == AttributeType.IntegerNumerical ){
				IntegerNumericalAttribute intAtt = (IntegerNumericalAttribute) attribute;
				sheader.append("integer[" + (int) intAtt.intervalValues().getLeft() + "," + (int) intAtt.intervalValues().getRight() +"]\n");
			}
			else if(attribute.getType() == AttributeType.DoubleNumerical ){
				RealNumericalAttribute doubleAtt = (RealNumericalAttribute) attribute;
				sheader.append("real[" + doubleAtt.intervalValues().getLeft() + "," + doubleAtt.intervalValues().getRight() +"]\n");
			}
		}
		sheader.append("@data\n");
		
		header = sheader.toString();
		// consoleReporter.setHeader(header.toString());		
		
		dataset.close();
		return schema;
	}

}

