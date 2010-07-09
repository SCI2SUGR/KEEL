package keel.Algorithms.MIL.MILR_Clas;

import org.core.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.Reader;



import weka.core.*;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;


/**
 * <p>Title: Main class of the algorithm</p>
 * <p>Description: It contains the essential methods for the MISMO algorithm</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: KEEL</p>
 * @since JDK1.5
 * @version 1.6
 */
public class MILR {

    //names of the I/O files
    private String outputFile;
    private String outputFileTr;
    private String outputFileTst;
    private String inputFileTr;
    private String inputFileTst;
    private String [] options;
    private String classifierName;
    double [] predictedTrain;
    double [] predictedTest;
    FastVector nomPredictedTrain;
    FastVector nomPredictedTest;
    Attribute attributeClass;

  
    private long seed;

  
    private boolean continousValues = false;
    private boolean problem = false;

    /**
     * It checks if some of the preconditions are not satisfied: There are any continuous value or
     * there was a problem while reading the data files
     * @return boolean true if the algorithm can run normally, false in other case
     */
    public boolean everythingOK() {
        return ((!continousValues) && (!problem));
    }

    /**
     * Default builder
     */
    public MILR() {

    };

    /**
     * MILR class builder</br>
     * It does a local copy of the filenames for their posterior use.<br/>
     * Then, it obtains all data from file and stores it in a format recognizable for the program.<br/>
     * Finally, it creates all possible selectors for the dataset and stores them.
     * @param ftrain Name of the input training file
     * @param feval Name of the input validation file
     * @param ftest Name of the input test file
     * @param foutputTr Name of the output training file
     * @param foutputTst Name of the output test file
     * @param fsal Name of the output information file
     * @param seed Seed for the random number generator
     */
    public MILR(String ftrain, String feval, String ftest, String foutputTr,
              String foutputTst,
              String fsal, long seed) {
       
        outputFile = fsal;
        outputFileTr = foutputTr;
        outputFileTst = foutputTst;
        inputFileTr = ftrain;
        inputFileTst = ftest;
        this.seed = seed;
      
            
        
        //String [] auxOptions = {"-R", String.valueOf(ridge), "-A", "0"};
        
        String [] auxOptions = {"-R", "1.0E-6", "-A", "0"};
        options = auxOptions;
        
        classifierName = "weka.classifiers.mi.MILR";
 
    }

   

    /**
     * We execute here the CitationKNN algorithm and we create the necessary output data
     */
    public void execute() {
        Randomize.setSeed(seed);
        algorithmMILR();
        writeOutput(); //We write the output files
    }

    /**
     * Main process of the MISMO algorithm
     */
    private void algorithmMILR() {
    	
    	Classifier classifier;
 		try {
 			classifier = Classifier.forName(classifierName, options);
 		
 	        Instances trainData = new Instances(new FileReader(inputFileTr));
 	        Instances testData = new Instances(new FileReader(inputFileTst));
 	
 	        int cIdx=trainData.numAttributes()-1;
 	        
 	        trainData.setClassIndex(cIdx);
 	        testData.setClassIndex(cIdx);
 	
 	        attributeClass = trainData.attribute(cIdx);
 	        //Apply the classifier
 	        classifier.buildClassifier(trainData);
 	        
 	        //Obtain data from model
 		    Evaluation evaluation = new Evaluation(trainData);
 		    predictedTrain = evaluation.evaluateModel(classifier, trainData);
 		    nomPredictedTrain = evaluation.predictions();
 		    
 		    Evaluation evaluationTest = new Evaluation(trainData);
		    predictedTest = evaluationTest.evaluateModel(classifier, testData);
 		    nomPredictedTest = evaluationTest.predictions();
 		    
 		    //confussionMatrix = evaluation.confusionMatrix();
 	      
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    };

  
     
  	/**
	 * Prints output files.
	 * 
	 * @param filename Name of output file
	 * @param realClass Real output of instances
	 * @param prediction Predicted output for instances
	 */
	private void writeOutput( ) {
	
		File f1 = new File(inputFileTr);
		Reader fileReader1;
	
		String text = "";
		
		try {
			
			fileReader1 = new BufferedReader(new FileReader(f1));
			BufferedWriter fileWriterTrain = new BufferedWriter(new FileWriter(outputFileTr));
			BufferedWriter fileWriterTest = new BufferedWriter(new FileWriter(outputFileTst));
			
			int counter = 0;
			
			//Read to relational attributes
			String line = ((BufferedReader) fileReader1).readLine();
					
			while(!line.contains("@attribute bag relational")){
				
				if(!line.equals(""))
				{
					fileWriterTrain.write(line + "\n");
					fileWriterTest.write(line + "\n");
				}
				line = ((BufferedReader) fileReader1).readLine();	
			}
			fileWriterTrain.write(line+"\n");
			fileWriterTest.write(line + "\n");
			
			//Check empty values
			line = ((BufferedReader) fileReader1).readLine();
			System.out.println("Línea: "+  line);
			while(!line.contains("@attribute")){
				
				fileWriterTrain.write(line + "\n");
				fileWriterTest.write(line + "\n");
				
				
				
				line = ((BufferedReader) fileReader1).readLine();	
			}
			
			while(!line.contains("@data"))
			{	
				if(!line.contains("@end bag") && !line.equals(""))
				{
				fileWriterTrain.write(line + "\n");
				fileWriterTest.write(line + "\n");
				}
				line = ((BufferedReader) fileReader1).readLine();	
				
			}	
		
			fileWriterTrain.write(line + "\n");
			fileWriterTest.write(line + "\n");
		
			
			//Prepare training output file
			for(int i=0; i< predictedTrain.length ; i++)
			{
				
				fileWriterTrain.write(attributeClass.value((int) Math.round(((NominalPrediction) nomPredictedTrain.elementAt(i)).actual())) + " " + attributeClass.value((int) Math.round(((NominalPrediction) nomPredictedTrain.elementAt(i)).predicted())) + "\n");
				
			}
			
			//Prepare test output file
			for(int i=0; i< predictedTest.length ; i++)
			{
				fileWriterTest.write(attributeClass.value( (int) Math.round(((NominalPrediction) nomPredictedTest.elementAt(i)).actual())) + " " + attributeClass.value( (int) Math.round(((NominalPrediction) nomPredictedTest.elementAt(i)).predicted())) + "\n");
				
			}
			
			
			fileReader1.close();
			fileWriterTrain.close();
			fileWriterTest.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
				
	}//end-method 

  
  
}
