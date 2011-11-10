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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.jdom.Element;


public class EducationalDiscretizerReport extends EducationalReport
{
 /**
 * <p>
 * This class creates a report in the experiment directory.
 * A file "report.txt" is creates in the same result directory
 * The report is for discretizers
 * </p>
 */

    /** Path for result files */
	protected List<String> pathResultFilesTxt = null;

	/**
     * <p>
	 * Constructor
	 * </p>
	 * @param	sentences of sentences for RunKeel.xml file
	 * @param	experimentType Type of experiment, clasifficacion o regression
	 */
	public EducationalDiscretizerReport(ArrayList<Element> sentences, int experimentType)
	{				
		super(sentences, experimentType);
		pathResultFilesTxt = new ArrayList<String>();
		this.calculatePathResultFilesTxt();
	}

	
	/**
     * <p>
	 * This method has to invoque for to create the report.
	 * Verify the type of problem, type partition and paths for
	 * to create the report. Read in iterative way the files of
	 * results
	 * </p>
	 */
	public void running()
	{

            String modelContents="";

                //read model
                if(listPathFilesExtra.size()>0){
                    modelContents+="\n\n===================================\n Model generated \n===================================\n";
                    modelContents+=Files.readFile((String)listPathFilesExtra.get(0));
                }
                else{
                    modelContents+="\n\nThis method does not provide information about its model.\n";
                }


		if(experimentType == CLASSIFICATION)
		{
			String cad = "";
			String cad2 = "";

			for (int i=0; i<((pathOutputFiles.length)/2); i++)
			{					
				try 
				{								
					fr = new FileReader(pathResultFilesTxt.get(i));
					br = new BufferedReader(fr);
					bw.newLine();
					bw.write("Partition " + (i+1));
					bw.newLine();
					bw.write("================");
					bw.newLine();
					bw.write("Intervals and nominal values");
					bw.newLine();
					bw.write("----------------------------");
					bw.newLine();
					bw.newLine();
					cad = br.readLine();
				}
				catch (IOException e) 
				{				
					e.printStackTrace();
				}	
				while (cad!=null)
				{				
					try
					{
							bw.write(cad);
							bw.newLine();
							cad = br.readLine();
					}
					catch (IOException e) 
					{				
						e.printStackTrace();
					}	
				}
			 				
				int init = 2*i;
				int stop = 2; // Training file + testing file
				
				while (stop>0)
				{					
					// result files
					try 
					{							
						fr = new FileReader(pathOutputFiles[init]);
						br = new BufferedReader(fr);
					}
					catch (IOException e) 
					{				
						e.printStackTrace();
					}	
						
					if(init%2 == 0)
					{
						try
						{
							bw.newLine();
							bw.write("Obtained Training Dataset");
							bw.newLine();
							bw.write("-------------------------");
						    bw.newLine();
						    bw.newLine();
						}
						catch (IOException e) 
						{				
							e.printStackTrace();
						}	
					}
					else
					{
						try
						{
							bw.newLine();
							bw.write("Obtained Testing Dataset");
							bw.newLine();
							bw.write("------------------------");
						    bw.newLine();
						    bw.newLine();
						}
						catch (IOException e) 
						{				
							e.printStackTrace();
						}	
					}
					
					try
					{
	    				cad2 = br.readLine();
					}
					catch (IOException e) 
					{				
						e.printStackTrace();
					}	
	    				
		    		while (cad2!=null)
		    		{		    		
		    			try
		    			{
		    				bw.write(cad2);
		    				bw.newLine();
		    				cad2 = br.readLine();
		    			}
		    			catch (IOException e) 
						{				
							e.printStackTrace();
						}	
		    		}													
					stop--;
					init++;
				}//while
			}//for	
			try 
			{
                            bw.write(modelContents);
				br.close();
				bw.close();
			}
			catch (IOException e) 
			{			
				e.printStackTrace();
			}
		}//if	
		
		else if(experimentType == REGRESSION)
		{
			
		}

	}



	private void calculatePathResultFilesTxt()
	{	
		for (int pos=0; pos<this.sentences.size(); pos++)
		{
			int posCadenaScriptFile = 0;
			String pathString = "";
			List linea = ((Element)sentences.get(pos)).getChildren();
			//posCadenaScriptFile = linea.size() -2;
			posCadenaScriptFile = linea.size() -3;
			pathString = ((Element)linea.get(posCadenaScriptFile)).getText();
			//Configuration file "pos" configxxx.txt
			String cadenaFicheroConfig = new String();
			//Path File Config.txt
			cadenaFicheroConfig = pathString;
							
			StringTokenizer st = null;	
			//File config.txt. Read training file and test file
			try 
			{
				fr = new FileReader(cadenaFicheroConfig);
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
				// paht of results files are going to adding  to pathResultFilesTxt
				if (cad.startsWith("outputData") == true)
				{
					st = new StringTokenizer(cad,"\"");
					st.nextToken();
					st.nextToken();
					st.nextToken();
					st.nextToken();
					st.nextToken();					
					this.pathResultFilesTxt.add(st.nextToken());
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
	 * Return paths of result.txt files
     * </p>
	 * @return pathResultFilesTxt List with the path results
	 */
	public List<String> obtainPathResultFilesTxt()
	{
		return this.pathResultFilesTxt;
	}

}//CrearInforme	