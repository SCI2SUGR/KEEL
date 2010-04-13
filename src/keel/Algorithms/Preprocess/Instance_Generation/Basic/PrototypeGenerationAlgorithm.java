package keel.Algorithms.Preprocess.Instance_Generation.Basic;

import java.util.*;
import java.util.regex.*;
import keel.Algorithms.Preprocess.Instance_Generation.utilities.*;
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
    protected static final int TEST = 1;
    
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
    	   // training.print();
    	    PrototypeGenerator generator = buildNewPrototypeGenerator(training, new Parameters(parameters));
    	    PrototypeSet resultingSet = generator.execute();
    	  // System.out.println("Resulting size= "+ resultingSet);
    	   //resultingSet.print();
    	    resultingSet.save(outputFilesPath.get(TRAINING));
            //Copy the test input file to the output test file
            KeelFile.copy(inputFilesPath.get(TEST), outputFilesPath.get(TEST));
    	    
            System.out.println("Time elapse: " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "");
            //System.out.println("cnn" + relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");
            //System.out.println("Time elapsed: " + generator.getTime());
    	}
    	catch(Exception e)
        {
            System.out.println("ERROR");
            System.getProperty("user.dir");
            e.printStackTrace();
        }
    }
}
