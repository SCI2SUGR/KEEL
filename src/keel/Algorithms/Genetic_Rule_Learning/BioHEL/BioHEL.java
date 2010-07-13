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

package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

import keel.Algorithms.Genetic_Rule_Learning.Globals.FileManagement;
import java.util.Arrays;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;
import org.core.Randomize;


/**
 * <p>
 * Class of BioHEL algorithm
 * </p>
 */
public class BioHEL {
	
	boolean stop = false;
	double percentageOfLearning = 0;
	int lastIteration = 0;
	

//*******************************************************************************************************************************

	public BioHEL(){
		
		if(Parameters.defClass.equals("disabled"))
			Parameters.defaultClassOption = Parameters.DISABLED;
		else if(Parameters.defClass.equals("major"))
			Parameters.defaultClassOption = Parameters.MAJOR;
		else if(Parameters.defClass.equals("minor"))
			Parameters.defaultClassOption = Parameters.MINOR;
		
		if(Parameters.optMethod.equals("maximize"))
			Parameters.optimizationMethod = Parameters.MAXIMIZE;
		else if(Parameters.optMethod.equals("minimize"))
			Parameters.optimizationMethod = Parameters.MINIMIZE;	
		
		if(Parameters.selectAlg.equals("tournament"))
			Parameters.selectionAlg = Parameters.TOURNAMENT;
		else if(Parameters.selectAlg.equals("tournamentWOR"))
			Parameters.selectionAlg = Parameters.TOURNAMENT_WOR;
		
		if(Parameters.winMethod.equals("ilas"))
			Parameters.windowingMethod = Parameters.ILAS;
		else if(Parameters.winMethod.equals("gws"))
			Parameters.windowingMethod = Parameters.GWS;
		else if(Parameters.winMethod.equals("none"))
			Parameters.windowingMethod = Parameters.NONE;
	}
	
//*******************************************************************************************************************************

	public void run(){
		
		LogManager.initLogManager();
		Randomize.setSeed(Parameters.seed);
		
		// read the train file --------------------------------------------------------------------
		InstanceSet isTRA = null;
		try {	
			isTRA = new InstanceSet();
			isTRA.readSet(Parameters.trainInputFile, true);
        }catch(Exception e){
        	System.out.println(e.toString());
            System.exit(1);
        }
        
        Parameters.NumAttributes = Attributes.getInputNumAttributes();
        Parameters.instances = isTRA.getInstances();
        Parameters.numClasses = Attributes.getOutputAttribute(0).getNumNominalValues();
        Parameters.NumInstances = Parameters.instances.length;
        
		Parameters.attributeSize = new int[Parameters.NumAttributes];
		
		Attribute[] atts = Attributes.getInputAttributes();

		for(int i = 0 ; i < Parameters.NumAttributes ; ++i){

			if(atts[i].getType() == Attribute.NOMINAL){
				Parameters.attributeSize[i] = atts[i].getNumNominalValues();
			}
			else{
				Parameters.attributeSize[i] = 2;
				}
			}
		
		Parameters.is = new instanceSet(Parameters.TRAIN);
		
		Parameters.InstancesOfClass = new int[Parameters.numClasses];
		Arrays.fill(Parameters.InstancesOfClass, 0);
		for(int i = 0 ; i < Parameters.NumInstances ; ++i)
			Parameters.InstancesOfClass[Parameters.instances[i].getOutputNominalValuesInt(0)]++;
			
        // ------------------------------------------------------------------------------------------
		
		Parameters.timers = new timersManagement();
		Parameters.ruleSet = new classifier_aggregated();		
		int countRepeat=0;
		Parameters.cFac = new classifierFactory();

		do {
			boolean cancelled = false;
			
			classifier best = null;
			
			for(int i = 0 ; i < Parameters.numRepetitionsLearning ; i++){
				
				classifier bestIt = runGA();
				
				if(bestIt == null) {
					cancelled = true;
					break;
				}

				if(best == null || bestIt.compareToIndividual(best, Parameters.optimizationMethod)>0) {
					best = Parameters.cFac.cloneClassifier(bestIt);
				}

				if( i < Parameters.numRepetitionsLearning-1){
					Parameters.is.restart();
					Parameters.timers.reinit();
				}

			}
			
			
			if(cancelled) break;

			if(isMajority(best)) {
				Parameters.ruleSet.addClassifier(best);
				classifierBriefTest(Parameters.ruleSet,Parameters.is);

				countRepeat=0;
				Parameters.is.removeInstancesAndRestart(best);
				Parameters.timers.reinit();
				if(Parameters.is.getNumInstances()==0) break;
			} else {
				countRepeat++;
				if(countRepeat==3) {
					Parameters.ruleSet.setDefaultRule(Parameters.is);
					break;
				} else {
					Parameters.is.restart();
					Parameters.timers.reinit();
				}
			}
			
		} while(true);

		LogManager.print(Parameters.ruleSet.dumpPhenotype());
        
        double accTra = doOutput(Parameters.trainInputFile, Parameters.trainOutputFile);
        double accTst = doOutput(Parameters.testInputFile, Parameters.testOutputFile);
        System.out.println("\n\n\naccTra = " + accTra + "\naccTst = " + accTst + "\n\n\n");
	}

//*******************************************************************************************************************************

	public classifier runGA(){
		
		stop=false;
		lastIteration=0;
		percentageOfLearning=0;

		Parameters.pw = new populationWrapper(Parameters.popSize);
		
		/*Object[] res = Parameters.pw.getAverageAccuracies();
		double ave1 = ((Double)res[0]).doubleValue();
		double ave2 = ((Double)res[1]).doubleValue();
		System.out.println("Initial population average accuracy1: "+ ave1 + " accuracy2:" + ave2);*/
		
		int countIt=0,numIterations = Parameters.numIterations;
		if(numIterations==0) stop=true;

		for (;!stop;) {
			Parameters.timers.incIteration(lastIteration);

			if (Parameters.is.newIteration(lastIteration!=0)) {
				Parameters.pw.activateModifiedFlag();
			}

			Parameters.pw.gaIteration();

			Parameters.timers.dumpStats();
			countIt++;

			if(countIt==numIterations) stop=true;
			else if(countIt==numIterations-1) lastIteration=1;
			percentageOfLearning=(double)countIt/(double)(numIterations);
		}

		if(stop && countIt<numIterations) return null;

		classifier ind = Parameters.pw.getBestOverall();
		//System.out.println("Best acc " + ind.getAccuracy() +", " + ind.getAccuracy2());
		classifier clone = Parameters.pw.cloneClassifier(ind);

		clone.adjustFitness();	
		return clone;
	}
	
//*******************************************************************************************************************************
	
	boolean isMajority(classifier ind){
		int i;
		int numInstances = Parameters.is.getNumInstances();
		Instance[] instances=Parameters.is.getAllInstances();

		int cl=ind.getClase();

		int nc=Parameters.numClasses;
		int[] classCounts = new int[nc];
		for(i=0;i<nc;i++) classCounts[i]=0;

		int numPos=0;
		for (i = 0; i < numInstances; i++) {
			if(instances[i].getOutputNominalValuesInt(0)==cl) numPos++;	
			if(ind.doMatch(instances[i])) {
				classCounts[instances[i].getOutputNominalValuesInt(0)]++;
			}
		}

		double ratio=(double)classCounts[cl]/(double)numPos;
		if(Parameters.useMDL && ratio<Parameters.timers.tMDL.coverageBreaks[cl]/3) return false;

		int max=classCounts[0];
		int posMax=0;
		boolean tie=false;

		for(i=1;i<nc;i++) {

			if(classCounts[i]>max) {
				max=classCounts[i];
				posMax=i;
				tie=false;
			} else if(classCounts[i]==max) {
				tie=true;
			}
		}

		return (max>0 && !tie && posMax==cl);
	}
	
//*******************************************************************************************************************************
	
	void classifierBriefTest(classifier_aggregated ind, instanceSet is){
		int i;
		agentPerformance ap = new agentPerformance(ind.getNumClassifiers(), Parameters.numClasses);
		int numInstances = is.getNumInstancesOrig();
		Instance[] instances=is.getOrigInstances();

		for (i = 0; i < numInstances; i++) {
			int predictedClass = -1;
			int whichClassifier=ind.classify(instances[i]);
			if (whichClassifier != -1) {
				predictedClass = ind.getClase(whichClassifier);
			}
			ap.addPrediction(instances[i].getOutputNominalValuesInt(0), predictedClass, whichClassifier);
		}

	}
	
//*******************************************************************************************************************************
    
    /**
     * It generates the output file from a given dataset and stores it in a file
     * @param dataset myDataset input dataset
     * @param filename String the name of the file
     */
    private double doOutput(String fileIN, String fileOUT) {
    	
		InstanceSet isAux = null;
		try {	
			isAux = new InstanceSet();
			isAux.readSet(fileIN, false);
        }catch(Exception e){
        	System.out.println(e.toString());
            System.exit(1);
        }
        
        Instance[] ins = isAux.getInstances();
       
        
        double aciertos=0;
        String output = "";
        
        output += "@relation " + Attributes.getRelationName() + "\n";
        output += Attributes.getInputAttributesHeader();
        output += Attributes.getOutputAttributesHeader();
        output += Attributes.getInputHeader()+"\n";
        output += Attributes.getOutputHeader()+"\n";
        output += "@data\n";
        	
        //We write the output for each example
        for (int i = 0; i < ins.length ; i++){
            //for classification:
            output += ins[i].getOutputNominalValues(0) + " " + classificationOutput(ins[i])+"\n";
            
            if((ins[i].getOutputNominalValues(0)).equals(classificationOutput(ins[i])))
                aciertos++;
        }
        
        FileManagement fileS = new FileManagement();
        try {
			fileS.initWrite(fileOUT);
	        fileS.writeLine(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return aciertos/ins.length;
    }

 //*******************************************************************************************************************************

    /**
     * It returns the algorithm classification output given an input example
     * @param example double[] The input example
     * @return String the output generated by the algorithm
     */
    private String classificationOutput(Instance in) {
    	        
        int predictedClass = -1;
		int whichClassifier = Parameters.ruleSet.classify(in);
		
		if (whichClassifier != -1) {
			predictedClass = Parameters.ruleSet.getClase(whichClassifier);
			 return Attributes.getOutputAttribute(0).getNominalValue(predictedClass);
		}
		
		return "noClass";
    }

}
