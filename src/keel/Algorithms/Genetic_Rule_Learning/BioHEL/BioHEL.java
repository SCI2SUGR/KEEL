package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

/**
 * <p>Title: BioHEL</p>
 *
 * <p>Description: It contains the implementation of the algorithm BioHEL</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */


import java.io.IOException;
import keel.Dataset.Attributes;
import org.core.*;


public class BioHEL {

    static public myDataset train, val, test;
    String outputTr, outputTst, outputRules;
    
    //parameters
    private long semilla;
    private int PopSize;
    private int tournamentSize;
    private int expectedRuleSize;
    private double crossoverProbability;
    private double mutationProbability;
    private boolean elitismEnabled;
    private int numGenerations;
    private int numRepetitionsLearning;
    private double coverageBreakpoint;
    private double coverageRatio;
    private RuleSet ruleSet;
    private int NumAttributes;
    private int NumInstances;
    private double generalizeProbability;
    private double specializeProbability;
    private boolean defaultClassEnabled;
    private String defaultClassOption;
    private int defaultClass;
    private double initialTheoryLenghtRatio;
    private double mdlWeightRelaxFactor;
    private int NumberOfStrataILAS;
    private int NumStages;
    static int numItMDL;
    
    private Crono cronometro;

    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public BioHEL() {
    }

    /**
     * It reads the data from the input files (training, validation and test) and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public BioHEL(parseParameters parameters) {

        train = new myDataset();
        val = new myDataset();
        test = new myDataset();
        try {
            System.out.println("\nReading the training set: " + parameters.getTrainingInputFile());
            train.readClassificationSet(parameters.getTrainingInputFile(), true);
            System.out.println("\nReading the validation set: " + parameters.getValidationInputFile());
            val.readClassificationSet(parameters.getValidationInputFile(), false);
            System.out.println("\nReading the test set: " + parameters.getTestInputFile());
            test.readClassificationSet(parameters.getTestInputFile(), false);
        } catch (IOException e) {
            System.err.println("There was a problem while reading the input data-sets: " + e);
            somethingWrong = true;
        }

        outputTr = parameters.getTrainingOutputFile();
        outputTst = parameters.getTestOutputFile();
        outputRules = parameters.getOutputFile(0);

        //Now we parse the parameters:
        semilla = Long.parseLong(parameters.getParameter(0));
        PopSize = Integer.parseInt(parameters.getParameter(1));
        tournamentSize = Integer.parseInt(parameters.getParameter(2));
        expectedRuleSize = Integer.parseInt(parameters.getParameter(3));
        crossoverProbability = Double.parseDouble(parameters.getParameter(4));
        mutationProbability = Double.parseDouble(parameters.getParameter(5));
        elitismEnabled = parameters.getParameter(6).equals("true");
        numGenerations = Integer.parseInt(parameters.getParameter(7));
        numRepetitionsLearning = Integer.parseInt(parameters.getParameter(8));
        coverageBreakpoint = Double.parseDouble(parameters.getParameter(9));
        coverageRatio = Double.parseDouble(parameters.getParameter(10));
        generalizeProbability = Double.parseDouble(parameters.getParameter(11));
        specializeProbability = Double.parseDouble(parameters.getParameter(12));
        defaultClassOption = parameters.getParameter(13);
        initialTheoryLenghtRatio = Double.parseDouble(parameters.getParameter(14));
        mdlWeightRelaxFactor = Double.parseDouble(parameters.getParameter(15));
        NumberOfStrataILAS = Integer.parseInt(parameters.getParameter(16));
        NumStages = Integer.parseInt(parameters.getParameter(17));
        numItMDL = Integer.parseInt(parameters.getParameter(18));
        
        //inicializar la semilla	
        Randomize.setSeed(semilla);
        
        NumAttributes=train.getnInputs();
        NumInstances=train.getnData();
        
    	ruleSet=new RuleSet();
    	
    	cronometro=new Crono();
    	
    	defaultClassEnabled=(!defaultClassOption.equals("none"));
    	defaultClass=-1;
    }

    /**
     * It launches the algorithm
     */
    public void execute() {
        
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found, either the data-set have numerical values or missing values.");
            System.err.println("Aborting the program");
        } else {
            //We do here the algorithm's operations
        	
        	cronometro.inicializa();
            	BioHEL_Method();
            cronometro.fin();
            
            //Finally we should fill the training and test output files
            double accTrain=doOutput(this.val, this.outputTr);
            double accTest=doOutput(this.test, this.outputTst);
            PrintOutputRules();
            
            double mediaAtts=0;
            for(int i=0 ; i<ruleSet.size() ; ++i)
            	mediaAtts+=ruleSet.get(i).getNumAttributes();
            mediaAtts/=ruleSet.size();
            
            
            System.out.print("\n\n************************************************");
            System.out.print("\nPorcertanje acierto train:\t"+accTrain);
            System.out.print("\nPorcertanje acierto test:\t"+accTest);
            System.out.print("\nNumero de reglas:\t\t"+ruleSet.size());
            System.out.print("\nNumero atributos inicial:\t"+NumAttributes);
            System.out.print("\nMedia de atributos/regla:\t"+mediaAtts);
            System.out.print("\nTiempo:\t\t\t\t"+cronometro.tiempoTotal());
            System.out.print("\n************************************************\n\n");

            System.out.println("Algorithm Finished");
        }
    }
    

    //*********************************************************************
    //***************** BioHEL Method *************************************
    //*********************************************************************
    
    private void BioHEL_Method(){   	
    	
    	if(defaultClassEnabled){
    		defaultClass=train.GetDefaultClass(defaultClassOption);
    		System.out.println("Clase por defecto = "+defaultClass);
    	}
    	
      
        Boolean stop=false;
        Rule candidateRule, bestRule;
       
        do{
        	
        	System.out.println("Instancias sin clasificar: "+train.instancesNotRemoved());
            bestRule=new Rule();
            
            // get the rule
            for(int i=0 ; i<numRepetitionsLearning ; ++i){
            	GA geneticAlgorithm=new GA(	PopSize,
    					tournamentSize, expectedRuleSize,
    					crossoverProbability,mutationProbability,
    					elitismEnabled, numGenerations,
    					coverageBreakpoint, coverageRatio,
    					generalizeProbability, specializeProbability,
    					defaultClass,
    				    initialTheoryLenghtRatio, mdlWeightRelaxFactor,
    				    NumberOfStrataILAS, NumStages);
            	
            	//crear algoritmo genetico
                candidateRule=geneticAlgorithm.RunGA();
                if(candidateRule.fitness<bestRule.fitness){
                    bestRule=candidateRule.cloneRule();
                }
            }

            
        	train.resetMatched();           		//reset de matched examples
            train.setMatchedExamples(bestRule);   	//Matched = Examples from TrainingSet matched by BestRule
            
            
            if(bestRule.PredictedClass()==train.predominantClassInMatched()){
            	
            	
            	
                train.removeMatched();      //Remove Matched from TrainingSet
                ruleSet.add(bestRule);
                
                
                if(train.getInstancesPerClass()[bestRule.PredictedClass()]<0.1*train.numberInstances(bestRule.PredictedClass()))
                	train.removeInstancesOfClass(bestRule.PredictedClass());
                
            }
           
            else
                stop = true;
            
            if(train.instancesNotRemoved(defaultClass)==0)
            	stop=true;


        }while(!stop);
        
        //añadir regla por defecto
    	if(defaultClassEnabled){
    		Rule defaultRule=new Rule();
    		defaultRule.createDefaultRule(defaultClass);
            ruleSet.add(defaultRule);
    	}
    	
    	else if(ruleSet.size()==0){
    		defaultClass=train.GetDefaultClass("majority");
    		Rule defaultRule=new Rule();
    		defaultRule.createDefaultRule(defaultClass);
            ruleSet.add(defaultRule);
    	}
    	
    }
    
    //*********************************************************************
    //***************** To do the output files ****************************
    //*********************************************************************
    
    public void PrintOutputRules(){
    	
    	String cad="";
    	
        //atributos presentes
    	for(int i=0 ; i<ruleSet.size() ; ++i){
    		cad+="\n\n\nIF ";

    		if(ruleSet.get(i).getNumAttributes()==0){
    			
    			cad+="TRUE\n\tTHEN CLASS = "+train.getOutputValue(ruleSet.get(i).PredictedClass());
    			
    		}
    		
    		else{
    		
    			for(int j=0 ; j<ruleSet.get(i).getNumAttributes() ; ++j){
    			
    				String nombreAtt=Attributes.getInputAttribute(ruleSet.get(i).get(j)).getName();
    				cad+="\t"+nombreAtt+" in ["+ruleSet.get(i).getLowLimit(j)+" , "+ruleSet.get(i).getUpperLimit(j)+"]\n\tAND";
    			}
    			
    			cad=cad.substring(0, cad.length()-4);
    			
    			cad+="\tTHEN CLASS = "+train.getOutputValue(ruleSet.get(i).PredictedClass());

    		}
    		
    		
    	}

        
        Fichero.escribeFichero(outputRules, cad);
    }
    
    
    
    /**
     * It generates the output file from a given dataset and stores it in a file
     * @param dataset myDataset input dataset
     * @param filename String the name of the file
     */
    private double doOutput(myDataset dataset, String filename) {
        
        double aciertos=0;
        
        String output = new String("");
        output = dataset.copyHeader(); //we insert the header in the output file
        //We write the output for each example
        for (int i = 0; i < dataset.getnData(); i++) {
            //for classification:
            output+=dataset.getOutputAsString(i)+" "+this.classificationOutput(dataset.getExample(i))+"\n";
            
            if(dataset.getOutputAsString(i).equals(this.classificationOutput(dataset.getExample(i))))
                aciertos++;
        }
        
        Fichero.escribeFichero(filename, output);
        
        return aciertos/dataset.getnData();
    }

    /**
     * It returns the algorithm classification output given an input example
     * @param example double[] The input example
     * @return String the output generated by the algorithm
     */
    private String classificationOutput(double[] example) {
        String output = "";
        
        for(int i=0 ; i<ruleSet.size() ; ++i){
            //veo si coincide el antecedente
            if(ruleSet.get(i).CoverInstance(example)){//coincide el antecedente, devuelvo la primera clase que lo cumple
                output=train.getOutputValue(ruleSet.get(i).PredictedClass());
                return output;
            }
        }
        
        return output;
    }

}
