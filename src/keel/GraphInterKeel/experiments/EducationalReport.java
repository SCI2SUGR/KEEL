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
 * @author Written by Juan Carlos Fernández and Pedro Antonio Gutiérrez (University of Córdoba) 23/08/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package keel.GraphInterKeel.experiments;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.jdom.Element;

public abstract class EducationalReport
{
    /**
     * <p>
     * This abstract class creates a report in the experiment directory.
     * A file "report.txt" is creates in the same result directory
     * </p>
     */

	static final int CLASSIFICATION = 0;
	static final int REGRESSION = 1;	
	protected int experimentType;
	protected FileReader fr = null;
	protected BufferedReader br = null;
	protected FileWriter fw = null;
	protected BufferedWriter bw = null;
	protected List<Element> sentences;
	protected List<String> pathDatasetFiles = null;
	protected List<String> listPathFiles = null;
        protected List<String> listPathFilesExtra = null;
	protected String[] pathOutputFiles = null;
	protected String pathReporFile = "";
	protected String experimentName = "";

	/**
     * <p>
	 * Constructor
	 * </p>
	 * @param	sentences Total of sentences fo RunKeel.xml
	 * @param	experimentType Type of experiment, classification or regression
	 */
	public EducationalReport(ArrayList<Element> sentences, int experimentType)
	{				
		this.sentences = sentences;
		this.experimentType = experimentType;
		listPathFiles = new ArrayList<String>();
                listPathFilesExtra = new ArrayList<String>();
		pathDatasetFiles = new ArrayList<String>();
	    experimentName =  new String();
		pathReporFile = new String();
	    
	    // Step 1: Calcutate path for Report file and Experiment name
	    this.calculateReportFilePath();
	    
	    // Step 2: Calculate path for output files for creating the report file report.txt
	    this.calculateOutputFilePath();
	    
		// Step 3: Calculate dataset path files for when it be necessary
		this.calculateDatasetPath();
	    
		//Creation report.txt
		try
		{
			fw = new FileWriter(this.pathReporFile);
			bw = new BufferedWriter(fw);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}	   			
	}
	
	
	/**
     * <p>
	 * This method has to invoque for to create the report.
	 * Verify the type of problem, type partition and paths for
	 * to create the report. Read in iterative way the files of
	 * results
	 * </p>
	 */
	public abstract void running();
	
		
	/**
     * <p>
	 * Method to calculate report path a experiment name. The
	 * RunKeel.xml file is inspected
	 * </p>
	 */
	private void calculateReportFilePath()
	{
		int posCadenaScriptFile = 0;
		String pathString = "";
		StringTokenizer st = null;
		String vectorCadenas[] = null;
		int counter = 0, tamanio = 0;
		List line;

		line = ((Element)sentences.get(0)).getChildren();
		posCadenaScriptFile = line.size() -3;
		pathString = ((Element)line.get(posCadenaScriptFile)).getText();
		st = new StringTokenizer(pathString,"/");
		tamanio = st.countTokens();
		vectorCadenas = new String[tamanio];		
		while (st.hasMoreTokens()) 
		{
	         vectorCadenas[counter] = new String(st.nextToken());
	         counter++;
	    }		
		pathString = "./experiment/results/";
		for (int j=3; j< (vectorCadenas.length)-1; j++)
			pathString =  pathString + vectorCadenas[j] + "/" ;
		this.experimentName = vectorCadenas[3];
		
		//Path file report.txt		
		this.pathReporFile = pathString + "report.txt";
		//System.out.println("Path of Report file: " + pathReporFile);
	}
	
	
	private void calculateDatasetPath()
	{
		for (int pos=0; pos<this.sentences.size(); pos++)
		{
			int posCadenaScriptFile = 0;	
			List line = ((Element)sentences.get(pos)).getChildren();
			posCadenaScriptFile = line.size() -3;
			String pathstring = ((Element)line.get(posCadenaScriptFile)).getText();
			//Fichero de configuracion "pos" configxxx.txt
			String stringConfigFile = new String();
			//Path File Config.txt
			stringConfigFile = pathstring;
							
			StringTokenizer st = null;	
			//File config.txt. Read training file and test file
			try 
			{
				fr = new FileReader(stringConfigFile);
				br = new BufferedReader(fr);
			} 
			catch (FileNotFoundException e) 
			{		
				e.printStackTrace();
			}
			
			String cad = "";
			try 
			{
				cad = br.readLine();
			} 
			catch (IOException e) 
			{			
				e.printStackTrace();
			}
			
			while (cad!=null)
			{
				////paht of results files are going to adding  to pathResultFilesTxt
				if (cad.startsWith("inputData") == true)
				{
					st = new StringTokenizer(cad,"\"");
					st.nextToken();
					//Training file
					pathDatasetFiles.add(st.nextToken());
					//White space
					st.nextToken();				
					//Test file
					pathDatasetFiles.add(st.nextToken());
					break;
				}
				try 
				{
					cad = br.readLine();
				} 
				catch (IOException e) 
				{			
					e.printStackTrace();
				}
			}
			try 
			{
				br.close();
			}
			catch (IOException e) 
			{		
				e.printStackTrace();
			}
		}	
	}
	

	/**
     * <p>
	 * Method to calculate config.txt path. The
	 * RunKeel.xml file is inspected
	 * Method to calculate paths of training and test files.
	 * The config.txt file is inspected
	 * Obtaining paths for resultXXX.tra resultXXX.tst ... ConfigXXX.txt path is necessary here
	 * It is calculated for a concrete partition. Asociated a rutasFicherosLista
     * </p>
	 */

	private void calculateOutputFilePath()
	{	
		for (int pos=0; pos<this.sentences.size(); pos++)
		{
			int posCadenaScriptFile = 0;
			String stringPath = "";
			List line = ((Element)sentences.get(pos)).getChildren();
			//posCadenaScriptFile = linea.size() -2;
			posCadenaScriptFile = line.size() -3;
			stringPath = ((Element)line.get(posCadenaScriptFile)).getText();
			//Fichero de configuracion "pos" configxxx.txt
			String configFilePath = new String();
			//Path File Config.txt
			configFilePath = stringPath;
							
			StringTokenizer st = null;	
			//File config.txt. Read training file and test file
			try 
			{
				fr = new FileReader(configFilePath);
				br = new BufferedReader(fr);
			} 
			catch (FileNotFoundException e) 
			{		
				e.printStackTrace();
			}
			
			String cad = "";
			try 
			{
				cad = br.readLine();
			} 
			catch (IOException e) 
			{			
				e.printStackTrace();
			}
			
			while (cad!=null)
			{
				//paht of results files are going to adding  to pathResultFilesTxt
				if (cad.startsWith("outputData") == true)
				{
					st = new StringTokenizer(cad,"\"");
					st.nextToken();
					//Training file
					listPathFiles.add(st.nextToken());
					//White space
					st.nextToken();				
					//Test file
					listPathFiles.add(st.nextToken());
                                        //White space
                                        st.nextToken();
                                        if(st.hasMoreTokens()){
                                            listPathFilesExtra.add(st.nextToken());
                                        }
					break;
				}
				try 
				{
					cad = br.readLine();
				} 
				catch (IOException e) 
				{			
					e.printStackTrace();
				}
			}
			try 
			{
				br.close();
			}
			catch (IOException e) 
			{		
				e.printStackTrace();
			}
		}
		
		this.obtainOutputFilePaths();
		
	}
		
	/**
     * <p>
	 * Method to calculate paths of training and test files.
	 * A array of String is created
	 * </p>
	 */
	
	private void obtainOutputFilePaths()
	{
		int tam = listPathFiles.size();
		pathOutputFiles = new String[tam];
		for (int i=0; i<tam; i++)
		{
			pathOutputFiles[i] = new String();
			pathOutputFiles[i] = (String)listPathFiles.get(i);
			//System.out.println("rutasFicheros[" + i + "]:" + pathOutputFiles[i]);
                        
		}
              
	}
	
	/**
     * <p>
	 * Return dataset paths
     * </p>
	 * @return List of dataset paths
	 * 
	 */
	@SuppressWarnings("unused")
	private List<String> obtaindatasetFilePaths()
	{
		return pathDatasetFiles;
	}
	
	
	/**
     * <p>
	 * Return array of strings with file output paths
     * </p>
	 * @return	array of strings with file output paths 
	 */
	public String[] getOutputFilePaths()
	{
		this.obtainOutputFilePaths();
		return this.pathOutputFiles;
	}
	
	/**
     * <p>
	 * This method return path of report file
	 * </p>
	 * @return	String Path of report file
	 */
	public String obtainReportFilePath()
	{
		return pathReporFile;
	}
	
	/**
     * <p>
	 * Get name of the experiments
     * </p>
     * @return String Name of the experiments
	 */
	public String getNameExperiment()
	{
		return this.experimentName;
	}
	
	/**
	 * Round a double whith a decimal precision
	 * 
	 * @param	num		double
	 * @param	ndecimal	number of decimal for precision
	 * 
	 */
	public static double round(double num,int ndecimal)
    {
        double aux0 = Math.pow(10,ndecimal);
        double aux = num * aux0;
        int tmp = (int) aux;
    
        return (double) (tmp / aux0) ;     
    }    



}
