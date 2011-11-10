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

package keel.RunKeelTxtDocente;

/**
 * <p>
 * @author Written by Juan Carlos FernÃ¡ndez and Pedro Antonio GutiÃ©rrez and(University of CÃ³rdoba) 07/07/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import keel.GraphInterKeel.experiments.EducationalDiscretizerReport;
import keel.GraphInterKeel.experiments.EducationalFSReport;
import keel.GraphInterKeel.experiments.EducationalISReport;
import keel.GraphInterKeel.experiments.EducationalMethodReport;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public class EducationalRunKeelTxt
{

    /**
     * <p>
     * This class run a iteration of a experiment when the method doIterate()
     * is invoqued. successive invocations finalize the experiment.
     * isFinished() method return true if all partitions are completed
     * </p>
     */
	
	//////////////////////////////////////////////////////
	//////////////			 					properties
	//////////////////////////////////////////////////////
	private Runtime rt;
	private Process proc;
	private List<Element> sentences;
	private List<Element> sentencesAux;
	private boolean stop;
	private boolean interrupted;
	private int i;	
    private int nJobs = 0;
	private int countJobs = 0;	
	private int contPart = 0;
	private int contPartReal = 0;
	private boolean nJobFinished = false;
	private int NumberofJobFinished = -1;
	// Number of sentences for each job (job)
    private List<Integer> partitionList = new ArrayList<Integer>();
	private long timePartition;
	private String command;
	private List line;
	static final int CLASSIFICATION = 0;
	static final int REGRESSION = 1;	
	private int tipoExp;
	private int tipoPartition;
	private String experimentName = null;
	// Number of total partitions included all jobs
	private int partitionnumber = 0;
	private boolean execExternalFinished = false;
	private boolean runFinishedAux = false;
	private String problemType = "";
	private String configFileString = "";

	//////////////////////////////////////////////////////
	//////////////							   constructor
	//////////////////////////////////////////////////////
	public EducationalRunKeelTxt(int experimentType) // CLASSIFICATION = 0, REGRESSION = 1 or UNSUPERVISED = 2
	{
		  this.tipoExp = experimentType;
		  
	      rt = null;
	      proc = null;
	      sentences = new ArrayList<Element>();
	      sentencesAux = new ArrayList<Element>();
	      stop = false;
	      interrupted = false;
	      i = 0;
	      timePartition = 0;	      	
	      command = "";
	      line = null;

	      Document doc = new Document();
	      
	      //Create the file "Runkeel.xml"
	      try 
	      {	    		    	   
	        SAXBuilder builder = new SAXBuilder ();
	        doc = builder.build(new File("./experiment/scripts/RunKeel.xml"));	        
	      }                               
	      catch (JDOMException e) 
	      {  
	        e.printStackTrace();
	        System.out.println("Error JDOM\n");
	      }
	      catch (Exception e) 
	      {
	        e.printStackTrace();
	        System.out.println("Execution XML file not found");
	      }	 
	      //Number of jobs
	      nJobs = doc.getRootElement().getChildren().size();	   
	      for (int i=0; i<nJobs; i++)	
	      {
	    	  int aux = ((Element)doc.getRootElement().getChildren().get(i)).getChildren().size();	    	  		
	    	  partitionList.add(aux);	    	
	      }
	      for(int i=0; i<nJobs; i++)	
	      {
	    	  for(int j=0; j<partitionList.get(i); j++)
	    	  {	
	    		  sentences.add( (Element) ((Element)doc.getRootElement().getChildren().
	    				  get(i)).getChildren().get(j) );	    	
	    	  }
	      }  
	      partitionnumber = sentences.size();
	}
	
	//////////////////////////////////////////////////////
	//////////////								   methods
	//////////////////////////////////////////////////////
	
	/**
     * <p>
	 * This method run a partition and creates the report
	 * when all partitions of the experiment are finished
     * </p<
	 */
	public synchronized void doIterate()
	{			
		while (interrupted == true && stop == false) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        //interrumpido = false;	
		
		if(i < sentences.size() && stop == false) //else: All partitions have been finished
		{										
			command = "";
			line = ((Element)sentences.get(i)).getChildren();
			//Caught the complet command in a String, except  algorithmType and seed (line.size()  -2)
			for (int j=0; j<line.size()-2; j++)
			{
				command += ((Element)line.get(j)).getText() + " ";
			}			
			
			problemType = ((Element)line.get(line.size()-2)).getText();
	
			Date now = Calendar.getInstance().getTime();
			System.out.println("*** BEGIN OF EXPERIMENT " + now + "\n");
			System.out.print("\nExecuting: " + command);
	
			StreamGobbler errorGobbler;
			StreamGobbler outputGobbler;
				
			//Initial time, in milliseconds
			timePartition = System.currentTimeMillis();			
			try 
			{				
				rt = Runtime.getRuntime();
			    proc = rt.exec(command);
			    errorGobbler = new StreamGobbler(proc.getErrorStream(),"ERROR");
			    outputGobbler = new StreamGobbler(proc.getInputStream(),"OUTPUT");
			    errorGobbler.start();
			    outputGobbler.start();
			    //wait to finish the extern process
			    int exitVal = proc.waitFor(); 
			    execExternalFinished = true;
			    now = Calendar.getInstance().getTime();
			    //Final time, in milliseconds
			    timePartition = System.currentTimeMillis()- timePartition;
			    System.out.println("ExitValue: " + exitVal);
			    if(nJobFinished==true)
	    			// Inicializacion de las sentencias del nuevo trabajo
	            	sentencesAux = new ArrayList<Element>();
				
			    if (exitVal != 0) //Error
			    {			    		
			            System.out.println("\n*** ERROR - END OF EXPERIMENT!! " + now + "\n");			            
			    }
			    else //Experiment correct.Increase i
			    {	
			    	sentencesAux.add(sentences.get(i));
		    		System.out.println("\n*** END OF EXPERIMENT " + now + "\n");
		    		i++;
		    		if(contPart == 0) 
		    			contPartReal = 0;		        
		    		contPart++;
		    		contPartReal = contPart; 		    		
		    		nJobFinished = false;		    		   
		    		
		            //Job completed
		            if (contPart==(int)partitionList.get(countJobs))//sentencias.size()) 
		            {				          
		            	if (problemType.equals("Method") == true)
			    		{				            				            
		            		EducationalMethodReport inf = new EducationalMethodReport((ArrayList<Element>) sentencesAux, tipoExp);
		            		inf.running();
			    		}
		            	else if (problemType.equals("Preprocess-D") == true)
		            	{		            		
		            		EducationalDiscretizerReport inf = new EducationalDiscretizerReport((ArrayList<Element>) sentencesAux, tipoExp);
		            		inf.running();
		            	}
		            	else if (problemType.equals("Preprocess-TSS") == true)
		            	{
		            		EducationalISReport inf = new EducationalISReport((ArrayList<Element>) sentencesAux, tipoExp);
		            		inf.running();
		            	}
		            	else if (problemType.equals("Preprocess-FS") == true)
		            	{
		            		EducationalFSReport inf = new EducationalFSReport((ArrayList<Element>) sentencesAux, tipoExp);
		            		inf.running();
		            	}		            			            	
		            	nJobFinished = true;
		            	NumberofJobFinished = countJobs;
		            	//It may be put in "if". Now all are methods
		            	contPart = 0;
		            	// Se pasa el siguiente trabajo que haya en RunKeel.xml
		            	countJobs++;
		            	// Se acabaron todos los trabajos
		            	if (i==sentences.size())
		            	{
		            		stop = true;
		            	}
			        }
			    }			    
			}
			catch (Throwable t) 
			{
				t.printStackTrace();
			}
		}
		else  //All partitions have finished
		{
			stop = true;
		}		
		execExternalFinished = false;
	}


    /**
     * <p>
     * Return the Report File Path
     * </p>
     * @return String Report File Path
     */
	public String obtainReportFilePath()
	{
		int posCadenaScriptFile = 0;
		String pathString = "";
		StringTokenizer st = null;
		String vectorCadenas[] = null;
		//String rutas[] = null;
		int counter = 0, tamanio = 0;
		List line;
		
		line = ((Element)sentencesAux.get(0)).getChildren();
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
		
		//Path file report.txt		
		return (pathString + "report.txt");
	}
	
	
    /**
     *
     * <p>
     * Return the path for PartitionDat File
     * Method to calculate name of partition (training or test)
	 * The config.txt file is inspected
     * </p>
     * @param countPartitions Counter for partitions
     * @return String path for PartitionDat File
     */
	public String calculatePartitionDatNameFile(int countPartitions)
	{					
		FileReader fr = null;
		BufferedReader br = null;		
		String partitionDatNameFile = "";
		StringTokenizer st = null;
		
		configFileString = this.calculateFileConfigPath(countPartitions);
		
		//File config.txt. Read training file and test file
		try 
		{
			fr = new FileReader(this.configFileString);
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
			if (cad.startsWith("inputData") == true)
			{	
					st = new StringTokenizer(cad,"\"");
					st.nextToken();
					String aux = st.nextToken();
					st = new StringTokenizer(aux,"/");
					while (st.hasMoreTokens())
						partitionDatNameFile = st.nextToken();			
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
	
		return  partitionDatNameFile;
	}
	

    /**
     * <p>
     * Method to calculate config.txt path.
     * </p>
     * @param pos position for FileConfigPath
     * @return String with the FileConfigPath
     */
	@SuppressWarnings("unused")
	private String calculateFileConfigPath(int pos)
	{	
		int posCadenaScriptFile = 0;
		String StringPath = "";
		//Configuration File "pos" configxxx.txt
		List linea = ((Element)sentences.get(pos)).getChildren();
		//posCadenaScriptFile = linea.size() -2;
		posCadenaScriptFile = linea.size() -3;
		StringPath = ((Element)linea.get(posCadenaScriptFile)).getText();
		//Path File Config.txt
		return StringPath;
	}	
	
	/**
     * <p>
	 * Get the experiment that is running
     * </p>
	 * @return  running experiment
	 */
	public String getActualNameExperiment()
	{
		int posCadenaScriptFile = 0;
		String stringPath = "";
		StringTokenizer st = null;
		String vectorCadenas[] = null;
		//String rutas[] = null;
		int counter = 0, tamanio = 0;
		List line;

		line = ((Element)sentencesAux.get(0)).getChildren();
		posCadenaScriptFile = line.size() -3;
		stringPath = ((Element)line.get(posCadenaScriptFile)).getText();
		st = new StringTokenizer(stringPath,"/");
		tamanio = st.countTokens();
		vectorCadenas = new String[tamanio];		
		while (st.hasMoreTokens()) 
		{
	         vectorCadenas[counter] = new String(st.nextToken());
	         counter++;
	    }		
		
		this.experimentName = vectorCadenas[3];
		return this.experimentName;
	}
	
	/**
     * <p>
	 * Method to calculate seed for actual partition
	 * The config.txt file is inspected
	 * </p>
	 */	
	public String calculateActualSeed()
	{
		FileReader fr = null;
		BufferedReader br = null;
		
		String seed = "";
		StringTokenizer st = null;
		boolean isSeed = false;
		//File config.txt. Read training file and test file
		try 
		{
			fr = new FileReader(configFileString);
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
			if (cad.startsWith("seed") == true)
			{
				st = new StringTokenizer(cad,"=");
				st.nextToken();
				seed = st.nextToken();
				isSeed = true;
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

		if(isSeed==true)
			return seed;
		else
			return "none";

	}
	
	
	/**
     * <p>
	 * This method is used for stop the experiment.
	 * Not Kill the experiment and the partitions,
	 * "suspend" the experiment
	 * </p>
	 */
	public void stopProcess()
	{
		interrupted = true;
		proc.destroy();
		
		if (execExternalFinished == true)
			runFinishedAux = true;
		else
			runFinishedAux = false;
	}
	
	
	/**
     * <p>
	 * This method is used for to kill the experiment.
	 * Kill the experiment and the partitions.
	 * </p>
	 */
	public void killProcess()
	{
		//The next time the thread donï¿½t invoque to doIterate(),
		//isFinished() return true
		stop = true;
		interrupted = true;
		proc.destroy();
		this.miNotify();		
	}
	
	/**
     * <p>
	 * Reanude the actual thread
	 * </p>
	 */
	private synchronized void miNotify()
	{
		notify();
	}
	
	/**
     * <p>
	 * To invoque previously stopProcess. Reanude the
	 * experiment in the partition i
     * </p>
	 */
	public synchronized void reanudeProcess()
	{	  	
	  	interrupted = false;
	  	notify();
	}

	
	//////////////////////////////////////////////////////
	//////////////							   get methods
	//////////////////////////////////////////////////////
	
	/**
     * <p>
	 * This method return the state of the experiment.
	 * True, all partitions finished
	 * False, partitions not finished 
	 * </p>
	 * @return boolean Has finished?
	 */
	public boolean isFinished()
	{		        
		return stop;
	}
	
	
	/**
     * <p>
	 * This methos return if a partition is interrupted
	 * for the extern user
	 * True, interrupted
	 * False, no interrupted
	 * </p>
	 * @return Is interrupted?
	 */
	public boolean isInterrumpted()
	{
		return interrupted;
	}
	
	
	/**
     * <p>
	 * This method return a control variable used
	 * for GUI EjecucionDocente. It controls the 
	 * conclusion of the extern process
	 * True if method doIterate() han finished
	 * <p>
	 * @return	boolean Has finished?
	 */
	public boolean getexecExternoFinalizado()
	{
		return runFinishedAux;
	}
	
	/**
     * <p>
	 * This method return number of th actual partition
	 * </p>
	 * @return	int Number of partition
	 */
	public int getNParticion()
	{
		return i;
	}
	
	/**
     * <p>
	 * Get number partition of a experiment (not global partition)
     * </p>
	 * @return number of partition in a experiement
	 */
	public int getNRealPartition()
	{
		return contPartReal;
	}
	
	/**
     * <p>
	 * This method return the compute time for a partition
	 * </p>
	 * @return	double Time of partition
	 */
	public double getPartitionTime()
	{
		//run time of partition, in milloseconds
		return (double)(timePartition/1000.0);
	}
	
	/**
     * <p>
	 * This methos return the partition total number 
	 * </p>
	 * @return	int Total Partitions
	 */
	public int getTotalPartitions()
	{
		return partitionnumber;
	}
	

	/**
     * <p>
	 * This methos get type problem, classification or regression
	 * </p>
	 * @return	int Type of Experiment
	 */
	public int getExperimentType()
	{
		return tipoExp;
	}
	
	/**
	 * Return type of problem
	 * @return Type of problem
	 */
	public String getProblemType()
	{
		return this.problemType;
	}
		
	/**
     * <p>
	 * This methos get type partitions, k-fold or 5x2 
	 * </p>
     * @return int type of partition
	 */
	public int getPartitionType()
	{
		return tipoPartition;
	}
	
	/**
     * <p>
	 * This method get number of jobs 
	 * </p>
     * @return int number of jobs
	 */
	public int getnJobs()
	{
		return nJobs;
	}
	

	
	/**
     * <p>
	 * Get the number of Jobs that is finished actually
     * </p>
	 * @return	number of job finished actually
	 */
	public int getNumberofJobFinished()
	{
		return NumberofJobFinished;
	}
	
	
	/**
     * <p>
	 * Return true if a job "n" has finished
     * </p>
	 * @return	A job "n" has finished
	 */
	public boolean nJobFinished()
	{
		return nJobFinished;
	}
	
	
	/**
     * <p>
     * Return the Actual jobs Sentences
     * </p>
     * @return List with the actual jobs Sentences
     */
	public List<Element> getActualJobSentences()
	{
		return this.sentencesAux;
	}
	
	//////////////////////////////////////////////////////
	//////////////							   set methods
	//////////////////////////////////////////////////////
	
	/**
     * <p>
	 * This method set type problem, classification or regression
	 * </p>
     * @param tipo Type of Experiment
	 */
	public void setExperimenttype(int tipo)
	{		
		tipoExp = tipo;
	}
	
	/**
     * <p>
	 * This methos set type partitions, k-fold or 5x2 
	 *
	 * 	PK = 0		P5X2 = 1;
	 * </p>
     * @param tipo Tipe of partition
	 */
	public void setpartitionType(int tipo)
	{
		tipoPartition = tipo;
		//Actually the experiments has 10 partitions
		//2 -> training and test
		/*
		int tam = (sentencias.size())*2;		
		rutasFicheros = new String[tam];
		for (int i=0; i<tam; i++)
			rutasFicheros[i] = new String();
		*/
	}

	
	/********************************************************************************
	** Internal class for output of extern process. Buffer control
	*********************************************************************************/  
	class StreamGobbler extends Thread 
	{
		protected InputStream is;
		protected String type;

		public StreamGobbler(InputStream is, String type) 
		{
			this.is = is;
			this.type = type;
		}
		
		public void run() 
		{
			try
			{
		        InputStreamReader isr = new InputStreamReader(is);
		        BufferedReader br = new BufferedReader(isr);
		        String line = null;
		        while ( (line = br.readLine()) != null) 
		        {
		        	System.out.println(line);		        	
		        	line = null;
		        }
				isr.close();
				br.close();
		    }
		    catch (IOException ioe) 
		    {
		        ioe.printStackTrace();
		    }
		}	
	}
}

