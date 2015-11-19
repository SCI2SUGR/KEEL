/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010

	F. Herrera (herrera@decsai.ugr.es)
    L. SÃ¡nchez (luciano@uniovi.es)
    J. AlcalÃ¡-Fdez (jalcala@decsai.ugr.es)
    S. GarcÃ­a (sglopez@ujaen.es)
    A. FernÃ¡ndez (alberto.fernandez@ujaen.es)
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

package keel.Algorithms.Semi_Supervised_Learning.Basic;

import java.util.*;
import java.util.regex.*;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerator;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;
import keel.Dataset.*;

import java.io.*;
import org.core.*;

/**
 * Template used by children classes to executes its algorithms.
 * @author Isaac Triguero
 * @param <T> Type of the algorithm
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
    /** Type of file: trs data set. */
    protected static final int TRS = 1;
    /** Type of file: test data set. */
    protected static final int TEST = 2;
    
    /**
     * Training instances set.
     */
    protected static InstanceSet train;
    
    /**
     * Test instances set.
     */
    protected static InstanceSet Itest;
    
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
        
        train = new InstanceSet(training);
        
        return new PrototypeSet(training);
    }
    
    /**
     * Reads the prototype set from a given file name.
     * @param nameOfFile file name given.
     * @param tipo type of the prototype set.
     * @return the prototype set read.
     */
    public static PrototypeSet readPrototypeSet(String nameOfFile, String tipo)
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
        
        if(tipo.equalsIgnoreCase("train")){
        	train = new InstanceSet(training);
        }else{
        	Itest = new InstanceSet(training);
        }
        return new PrototypeSet(training);
    }
    
    /**
     * Build a new generator object for SSL.
     * @param train Training data set that will be used for the generator object.
     * @param unlabeled Unlabeled data set.
     * @param test Test data set.
     * @param params Parameters of the algorithm of reduction.
     * @return New prototype generator object with data and parameters full load.
     */
    protected abstract T buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params);

    
    /**
     * Build a new generator object for Data REduction.
     * @param train Training data set that will be used for the generator object.
     * @param params Parameters of the algorithm of reduction.
     * @return
     */
//    protected abstract T buildNewPrototypeGenerator(PrototypeSet train, Parameters params);

    
    /**
     * Assert keel-style arguments
     * @param args Console arguments.
     */
    public static void assertArguments(String[] args)
    {
        if(args.length!=1)
            System.err.println("Error in parameters. One configuration file needed");
        //Se supone que aquÃ­ lanzo una excepciÃ³n o algo asÃ­
    }
    
    /**
     * Execute the algorithm given.
     * IT HAS BEEN MODIFIED FOR SEMI-SUPERVISED LEARNING.
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
    	    PrototypeSet training = readPrototypeSet(inputFilesPath.get(TRAINING),"train");
            PrototypeSet transductive = readPrototypeSet(inputFilesPath.get(TRS));      	   
            PrototypeSet test = readPrototypeSet(inputFilesPath.get(TEST),"test");   
    	    
            
            
            // Calculate the number of labeled instance, which should be remove from the transductive accuracy.
            int numberOfClass = training.getPosibleValuesOfOutput().size();
            PrototypeSet labeled =  training.getAllDifferentFromClass(numberOfClass).clone();
            // PrototypeSet difference = new PrototypeSet(transductive.clone());
            //difference.removeWithoutClass(labeled);
            transductive.removeWithoutClass(labeled);
   
      //      System.out.println(" size = "+ transductive.size());
            
            //-------------------
            
    	    PrototypeGenerator generator = buildNewPrototypeGenerator(training, transductive,test, new Parameters(parameters));
    	    
    	    generator.setInstanceTrain(train);
    	    generator.setInstanceTest(Itest);
    	    
    	    // The method return the classification in transductive and test.
    	    Pair<PrototypeSet, PrototypeSet> resultingSet = generator.execute();
    
    	    
    	    //In SSL the resultingSet will be the (label+unlabeled prototype set, with the calculated labels)
    	   // resultingSet.save(outputFilesPath.get(TRAINING));
    	    

            
            
            int transductiveRealClass[][];
            int transductivePrediction[][];
            int testRealClass[][];
            int testPrediction[][];
            
            transductiveRealClass = new int[transductive.size()][1];
    	    transductivePrediction = new int[transductive.size()][1];	
            testRealClass = new int[test.size()][1];
    	    testPrediction = new int[test.size()][1];	
    	    
            int nClases = transductive.getPosibleValuesOfOutput().size();
                
            

            

            

            
            for (int i=0; i< transductive.size(); i++) { 
                transductiveRealClass[i][0] = (int) transductive.get(i).getOutput(0);
                transductivePrediction[i][0] = (int) resultingSet.first().get(i).getOutput(0);
            }
            for(int i=0; i<test.size(); i++){
                testRealClass[i][0] = (int) test.get(i).getOutput(0);
                testPrediction[i][0] = (int) resultingSet.second().get(i).getOutput(0);
             }
            
    	    
            Attribute entradas[];
    	    Attribute salida;
    	                     
            entradas = Attributes.getInputAttributes();
            salida = Attributes.getOutputAttribute(0);
            String relation =  Attributes.getRelationName(); 
                
    	    writeOutput(outputFilesPath.get(0), transductiveRealClass, transductivePrediction, entradas, salida, relation);
    	    writeOutput(outputFilesPath.get(1), testRealClass, testPrediction, entradas, salida, relation);   
    	    
    	    //Copy the test input file to the output test file
            //KeelFile.copy(inputFilesPath.get(TEST), outputFilesPath.get(TEST));
    	    
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
     * @param inputs Input attributes.
     * @param output Output attribute.
     * @param relation Relation string.
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
        
	
}
