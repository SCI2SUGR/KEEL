/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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

package keel.Algorithms.Instance_Generation.Basic;

import java.util.*;
import java.util.regex.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Dataset.*;
import java.io.*;
import org.core.*;

/**
 * Template used by children classes to executes its algorithms.
 * @author diegoj
 * @param T Type of the algorithm
 */
public abstract class PrototypeGenerationAlgorithm<T extends PrototypeGenerator>
{
    /** Training data set file name. */
    protected static String trainingFileName;
    /** Test data set file name. */
    protected static String testFileName;
    /** Parameters given by the console. */
    protected static ArrayList<String> parameters;
    /** Name of the parameters. */
    protected static ArrayList<String> parametersName;
    /** Complete path of input files. */
    protected static ArrayList<String> inputFilesPath;
    /** Complete path of output files. */
    protected static ArrayList<String> outputFilesPath;
    /** Name of input files. */
    protected static ArrayList<String> inputFiles;
    /** Name of output files. */
    protected static ArrayList<String> outputFiles;
    /** Type of file: training data set. */
    protected static final int TRAINING = 0;
    /** Type of file: test data set. */
    protected static final int VALIDATION = 1;  // for KEEL DEV
    /** Type of file: test data set. */
    protected static final int TEST = 2;
    /**
     * Gets the file names from one line of keel text.
     * @return Array with all file names from that line of text.
     */
    private static ArrayList<String> getFileNames(String line)
    {
        StringTokenizer inputLines = new StringTokenizer (line,"=");
        inputLines.nextToken();//inputData
        String files = inputLines.nextToken();//files
        StringTokenizer fileLine = new StringTokenizer (files," ");
        ArrayList<String> sfiles = new ArrayList<String>();
        while(fileLine.hasMoreElements())
        {
            sfiles.add(fileLine.nextToken().replace("\"", ""));
        }
        //System.out.println("getFileNames " + sfiles);
        return sfiles;
    }

    /**
     * Read the keel parameters file. Load all parameters of the algorithm.
     * @param config Name of the configuration file.
     */
    public static void readParametersFile(String config)
    {
        inputFiles = new ArrayList<String>();
        outputFiles = new ArrayList<String>();
        
        String file = KeelFile.read(config);
        StringTokenizer fileLines = new StringTokenizer (file,"\n\r");

        fileLines.nextToken();//ignore Algorithm name
        
        String line = fileLines.nextToken();//input data
        inputFilesPath = getFileNames(line);
        
        int i=0;
        Pattern pat = Pattern.compile("[/\\\\]+");
        for(String s : inputFilesPath)
        {
            System.out.println("Input " + i + " : " + s);
            i++;
            String[] splitted = pat.split(s);
            int _size = splitted.length;
            String name = splitted[_size-1];
            //name = name.substring(0, name.length()-1);
            inputFiles.add(name);
            System.out.println("Input File Name: " + name);
        }       
        
        line = fileLines.nextToken();//output data
        outputFilesPath = getFileNames(line);        

        for(String s : outputFilesPath)
        {
            String[] splitted = pat.split(s);
            int _size = splitted.length;
            String name = splitted[_size-1];
            name = name.substring(0, name.length()-1);
            outputFiles.add(name);
            System.out.println("Output File Name: " + name);
        }
        
        parameters = new ArrayList<String>();
        parametersName = new ArrayList<String>();
        while(fileLines.hasMoreElements())
        {
            String parameterLine = fileLines.nextToken();
            StringTokenizer paramToken = new StringTokenizer (parameterLine,"=");
            String p1 = paramToken.nextToken();
            parametersName.add(p1);            
            String p2 = paramToken.nextToken();
            p2 = p2.replaceAll(" ", "");
            parameters.add(p2);
            //System.out.println(p1+"="+p2);
        }
    }
    
    /**
     * Print the parameters of the algorithm.
     */
    public static void printParameters()
    {
        for(String s: inputFiles)
            System.out.println(s);
        for(String s: outputFiles)
            System.out.println(s);
        
        int _size = parameters.size();
        for(int i=0; i<_size; ++i)
            System.out.println(parametersName.get(i)+"="+parameters.get(i));
    }
    
    /**
     * Reads the prototype set from a data file.
     * @param nameOfFile Name of data file to be read.
     * @return PrototypeSet built with the data of the file.
     */
    public static PrototypeSet readPrototypeSet(String nameOfFile)
    {
        Attributes.clearAll();//BUGBUGBUG
        InstanceSet training = new InstanceSet();        
        try
        {
        	//System.out.print("PROBANDO:\n"+nameOfFile);
            training.readSet(nameOfFile, true); 
            //training.print();
            training.setAttributesAsNonStatic();
          
            InstanceAttributes att = training.getAttributeDefinitions();
            Prototype.setAttributesTypes(att);  
            //training.print();
        }
        catch(Exception e)
        {
            System.err.println("readPrototypeSet has failed!");
            e.printStackTrace();
        }
        return new PrototypeSet(training);
    }
    
    /**
     * Build a new generator object.
     * @param train Training data set that will be used for the generator object.
     * @param params Parameters of the algorithm of reduction.
     * @return New prototype generator object with data and parameters full load.
     */
    protected abstract T buildNewPrototypeGenerator(PrototypeSet train, Parameters params);
    
    /**
     * Assert keel-style arguments
     * @param args Console arguments.
     */
    public static void assertArguments(String[] args)
    {
        if(args.length!=1)
            System.err.println("Error in parameters. One configuration file needed");
        //Se supone que aquí lanzo una excepción o algo así
    }
    
    /**
     * Execute the algorithm given.
     * @param args Arguments given by console.
     */
    public void execute(String[] args)
    {
    	try{
    		  long tiempo = System.currentTimeMillis();
            Parameters.assertBasicArgs(args);
    	    
            PrototypeGenerationAlgorithm.readParametersFile(args[0]);
    	    PrototypeGenerationAlgorithm.printParameters();
            
            //Generate output training file from condensed input training file
    	    PrototypeSet training = readPrototypeSet(inputFilesPath.get(TRAINING));
            PrototypeSet test = readPrototypeSet(inputFilesPath.get(TEST));
            
    	   // training.print();
    	    PrototypeGenerator generator = buildNewPrototypeGenerator(training, new Parameters(parameters));
    	    PrototypeSet resultingSet = generator.execute();
            
            
            /*ADDING KNN FOR TEST FILE */
            int trainRealClass[][];
            int trainPrediction[][];
                
            trainRealClass = new int[training.size()][1];
	    trainPrediction = new int[training.size()][1];	
           
            int nClases = resultingSet.getPosibleValuesOfOutput().size();
                 
           //Working on training
            for (int i=0; i<training.size(); i++) {
                 trainRealClass[i][0] = (int) training.get(i).getOutput(0);
                 trainPrediction[i][0] = evaluate(training.get(i).getInputs(),resultingSet.prototypeSetTodouble(), nClases, resultingSet.getClases(), 1);
            }
            
            Attribute entradas[];
	    Attribute salida;
	                     
            entradas = Attributes.getInputAttributes();
            salida = Attributes.getOutputAttribute(0);
            String relation =  Attributes.getRelationName(); 
                
            writeOutput(outputFilesPath.get(TRAINING), trainRealClass, trainPrediction, entradas, salida, relation);
            
            int realClass[][] = new int[test.size()][1];
	    int prediction[][] = new int[test.size()][1];	
		
				
            for (int i=0; i<realClass.length; i++) {
		realClass[i][0] = (int) test.get(i).getOutput(0);
		prediction[i][0]= evaluate(test.get(i).getInputs(),resultingSet.prototypeSetTodouble(), nClases, resultingSet.getClases(), 1);
            }
                
            writeOutput(outputFilesPath.get(1), realClass, prediction,  entradas, salida, relation);
                
            
            
    	  // System.out.println("Resulting size= "+ resultingSet);
    	   //resultingSet.print();
    	   // resultingSet.save(outputFilesPath.get(TRAINING));
            //Copy the test input file to the output test file
           
            // KeelFile.copy(inputFilesPath.get(TEST), outputFilesPath.get(TEST));
    	    
            System.out.println("Time elapse: " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "");
           
    	}
    	catch(Exception e)
        {
            System.out.println("ERROR");
            System.getProperty("user.dir");
            e.printStackTrace();
        }
    }
    
    
    
    
    /**
	 * Prints output files.
	 * 
	 * @param filename Name of output file
	 * @param realClass Real output of instances
	 * @param prediction Predicted output for instances
	 */
	public static void writeOutput(String filename, int [][] realClass, int [][] prediction, Attribute inputs[], Attribute output, String relation) {
	
		String text = "";
		
                    
		/*Printing input attributes*/
		text += "@relation "+ relation +"\n";

		for (int i=0; i<inputs.length; i++) {
			
			text += "@attribute "+ inputs[i].getName()+" ";
			
		    if (inputs[i].getType() == Attribute.NOMINAL) {
		    	text += "{";
		        for (int j=0; j<inputs[i].getNominalValuesList().size(); j++) {
		        	text += (String)inputs[i].getNominalValuesList().elementAt(j);
		        	if (j < inputs[i].getNominalValuesList().size() -1) {
		        		text += ", ";
		        	}
		        }
		        text += "}\n";
		    } else {
		    	if (inputs[i].getType() == Attribute.INTEGER) {
		    		text += "integer";
		        } else {
		        	text += "real";
		        }
		        text += " ["+String.valueOf(inputs[i].getMinAttribute()) + ", " +  String.valueOf(inputs[i].getMaxAttribute())+"]\n";
		    }
		}

		/*Printing output attribute*/
		text += "@attribute "+ output.getName()+" ";

		if (output.getType() == Attribute.NOMINAL) {
			text += "{";
			
			for (int j=0; j<output.getNominalValuesList().size(); j++) {
				text += (String)output.getNominalValuesList().elementAt(j);
		        if (j < output.getNominalValuesList().size() -1) {
		        	text += ", ";
		        }
			}		
			text += "}\n";	    
		} else {
		    text += "integer ["+String.valueOf(output.getMinAttribute()) + ", " + String.valueOf(output.getMaxAttribute())+"]\n";
		}

		/*Printing data*/
		text += "@data\n";

		Files.writeFile(filename, text);
		
		if (output.getType() == Attribute.INTEGER) {
			
			text = "";
			
			for (int i=0; i<realClass.length; i++) {
			      
			      for (int j=0; j<realClass[0].length; j++){
			    	  text += "" + realClass[i][j] + " ";
			      }
			      for (int j=0; j<realClass[0].length; j++){
			    	  text += "" + prediction[i][j] + " ";
			      }
			      text += "\n";			      
			      if((i%10)==9){
			    	  Files.addToFile(filename, text);
			    	  text = "";
			      }     
			}			
			
			if((realClass.length%10)!=0){
				Files.addToFile(filename, text);
			}
		}
		else{
			
			text = "";
			
			for (int i=0; i<realClass.length; i++) {
			      
			      for (int j=0; j<realClass[0].length; j++){
			    	  text += "" + (String)output.getNominalValuesList().elementAt(realClass[i][j]) + " ";
			      }
			      for (int j=0; j<realClass[0].length; j++){
			    	  if(prediction[i][j]>-1){
			    		  text += "" + (String)output.getNominalValuesList().elementAt(prediction[i][j]) + " ";
			    	  }
			    	  else{
			    		  text += "" + "Unclassified" + " ";
			    	  }
			      }
			      text += "\n";
			      
			      if((i%10)==9){
			    	  Files.addToFile(filename, text);
			    	  text = "";
			      } 
			}			
			
			if((realClass.length%10)!=0){
				Files.addToFile(filename, text);
			}		
		}
		
	}//end-method
        
    
    /** 
	 * Calculates the Euclidean distance between two instances
	 * 
	 * @param instance1 First instance 
	 * @param instance2 Second instance
	 * @return The Euclidean distance
	 * 
	 */
	protected static double distance(double instance1[],double instance2[]){
		
		double length=0.0;

		for (int i=0; i<instance1.length; i++) {
			length += (instance1[i]-instance2[i])*(instance1[i]-instance2[i]);
		}
			
		length = Math.sqrt(length); 
				
		return length;
		
	} //end-method
        
             
    /** 
	 * Evaluates a instance to predict its class.
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted
	 * 
	 */
	public static int evaluate (double example[], double trainData[][],int nClasses,int trainOutput[],int k) {
	
		double minDist[];
		int nearestN[];
		int selectedClasses[];
		double dist;
		int prediction;
		int predictionValue;
		boolean stop;

		nearestN = new int[k];
		minDist = new double[k];
	
	    for (int i=0; i<k; i++) {
			nearestN[i] = 0;
			minDist[i] = Double.MAX_VALUE;
		}
		
	    //KNN Method starts here
	    
		for (int i=0; i<trainData.length; i++) {
		
		    dist = distance(trainData[i],example);

			if (dist > 0.0){ //leave-one-out
			
				//see if it's nearer than our previous selected neighbors
				stop=false;
				
				for(int j=0;j<k && !stop;j++){
				
					if (dist < minDist[j]) {
					    
						for (int l = k - 1; l >= j+1; l--) {
							minDist[l] = minDist[l - 1];
							nearestN[l] = nearestN[l - 1];
						}	
						
						minDist[j] = dist;
						nearestN[j] = i;
						stop=true;
					}
				}
			}
		}
		
		//we have check all the instances... see what is the most present class
		selectedClasses= new int[nClasses];
	
		for (int i=0; i<nClasses; i++) {
			selectedClasses[i] = 0;
		}	
		
		for (int i=0; i<k; i++) {
                 //      System.out.println("nearestN i ="+i + " =>"+nearestN[i]);
                  // System.out.println("trainOutput ="+trainOutput[nearestN[i]]);
                    
			selectedClasses[trainOutput[nearestN[i]]]+=1;
		}
		
		prediction=0;
		predictionValue=selectedClasses[0];
		
		for (int i=1; i<nClasses; i++) {
		    if (predictionValue < selectedClasses[i]) {
		        predictionValue = selectedClasses[i];
		        prediction = i;
		    }
		}
		
		return prediction;
	
	} //end-method	
    
        
}

