/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcal·-Fdez (jalcala@decsai.ugr.es)
    A. Fern·ndez (alberto.fernandez@ujaen.es)
    S. GarcÌa (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. S·nchez (luciano@uniovi.es)
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

package keel.GraphInterKeel.experiments;


/**
 * <p>
 * @author Written by Juan Carlos Fern√°ndez and Pedro Antonio Guti√©rrez (University of C√≥rdoba) 23/08/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.jdom.Element;


public class EducationalFSReport extends EducationalReport
{
    /**
     * <p>
     * This class creates a report in the experiment directory.
     * A file "report.txt" is creates in the same result directory
     * The report is for Feature Selection
     * </p>
     */


	protected List<String> pathResultFilesTxt = null;

	/**
	 * Constructor
	 * 
	 * @param	sentences Total of sentences for RunKeel.xml file
	 * @param	
	 * @param	tipoExperimento Tipo experimento, clasificacion o regresion
	 * @param	
	 */
	public EducationalFSReport(ArrayList<Element> sentences, int experimentType)
	{				
		super(sentences, experimentType);
		pathResultFilesTxt = new ArrayList<String>();
		this.obtainPathResultFilesTxt();
	}

	
	/**
     * <p>
	 * This method has to invoque for to create the report.
	 * Verify the type of problem, type partition and paths for
	 * to create the report. Read in iterative way the files of
	 * results
	 * </p>
	 */
	@SuppressWarnings("hiding")
	public void running()
	{				
		if(experimentType == CLASSIFICATION)
		{
			String cad = "";
			String cad2 = "";
			
			// Reading from training dataset and results datasets			
			String originalFeatures = "";
			try 
			{							
				fr = new FileReader(pathDatasetFiles.get(0));
				br = new BufferedReader(fr);
				
				//@relation ...
				cad = br.readLine();
				while (cad!=null)
				{				
					try
					{
						cad = br.readLine();
						if(cad.startsWith("@attribute class")==false)
						{
							StringTokenizer st = null;
							st = new StringTokenizer(cad);
							st.nextToken();
							originalFeatures = originalFeatures + st.nextToken() + " ";													
						}
						else
							break;
					}
					catch (IOException e) 
					{				
						e.printStackTrace();
					}	
				}								
			}
			catch (IOException e) 
			{				
				e.printStackTrace();
			}	
			
			
			String selectedFeatures = "";
			cad = "";
			int contAux = 0;
		    boolean salida = false;
			while(salida == false)
			{
				try 
				{	
					File file = new File(pathOutputFiles[contAux]);
					if (file.exists()==true)
					{
						fr = new FileReader(pathOutputFiles[contAux]);
						br = new BufferedReader(fr);					
						
						//@relation ...
						cad = br.readLine();
						while (cad!=null)
						{				
							try
							{
								cad = br.readLine();
								if(cad.startsWith("@attribute class")==false)
								{
									StringTokenizer st = null;
									st = new StringTokenizer(cad);
									st.nextToken();
									selectedFeatures = selectedFeatures + st.nextToken() + " ";													
								}
								else
									break;
							}
							catch (IOException e) 
							{				
								e.printStackTrace();
							}	
						}
						salida = true;
					}
					else
					{
						contAux++;
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}				
			}												
									
			for (int i=0; i<((pathOutputFiles.length)/2); i++)
			{					
					try 
					{
						File file = new File(pathResultFilesTxt.get(i));
						if (file.exists()==true)
						{
							fr = new FileReader(pathResultFilesTxt.get(i));
							br = new BufferedReader(fr);
							bw.newLine();
							bw.write("Partition " + (i+1));
							bw.newLine();
							bw.write("================");
							bw.newLine();
							bw.write("Original Features:");
							bw.newLine();
							bw.write("------------------");
							bw.newLine();
							bw.write(originalFeatures);
							bw.newLine();
							bw.newLine();
							bw.write("Selected Features:");
							bw.newLine();
							bw.write("------------------");
							bw.newLine();
							bw.write(selectedFeatures);
							bw.newLine();	
							bw.newLine();
							bw.write("Errors:");
							bw.newLine();
							bw.write("-------");
							bw.newLine();
							
							
							cad = "";
							while (cad!=null)
							{				
								try
								{
									if(cad.startsWith("Error")==true)
									{
										bw.write(cad);
										bw.newLine();
									}									
									cad = br.readLine();
								}
								catch (IOException e) 
								{				
									e.printStackTrace();
								}									
							}
				 				
							
							int init = 2*i;
							int stop = 2; // Training and Testing files
					
							while (stop>0)
							{		
								int exist = 0;
			                     //result files
								try 
								{	
									file = new File(pathOutputFiles[init]);
									if (file.exists()==true)
									{
										exist = 1;
										fr = new FileReader(pathOutputFiles[init]);
										br = new BufferedReader(fr);
									}
									
								}					
								catch (IOException e) 
								{				
									e.printStackTrace();
								}	
								if(exist==1)
								{
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
								}//if exits		
							stop--;
							init++;
						}//while
					}//if
				}			
				catch (IOException e) 
				{				
					e.printStackTrace();
				}		
			}//for	
			try 
			{
				br.close();
				bw.close();
			}
			catch (IOException e) 
			{			
				e.printStackTrace();
			}
		}	
		else if(experimentType == REGRESSION)
		{
			
		}
	}




	private void obtainPathResultFilesTxt()
	{	
		for (int pos=0; pos<this.sentences.size(); pos++)
		{
			int posCadenaScriptFile = 0;
			String pathString = "";
			List linea = ((Element)sentences.get(pos)).getChildren();
			//posCadenaScriptFile = linea.size() -2;
			posCadenaScriptFile = linea.size() -3;
			pathString = ((Element)linea.get(posCadenaScriptFile)).getText();
			//Configuration File "pos" configxxx.txt
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
				//paht of results files are going to adding  to pathResultFilesTxt
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
	 * @return pathResultFilesTxt Paths of result.txt files
	 */
	public List<String> getPathResultFilesTxt()
	{
		return this.pathResultFilesTxt;
	}

}

