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

package keel.Algorithms.Genetic_Rule_Learning.DMEL;

/**
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */

import java.io.IOException;
import java.util.*;

import org.core.*;

public class DMEL {

    myDataset train, val, test;
    String outputTr, outputTst, outputRule;
    int nClasses;
    
    //Parameters
    long seed;

    //We may declare here the algorithm's parameters
    int popSize;
    double pCross;
    double pMut;
    int numGenerations;

    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public DMEL () {
    }

    /**
     * It reads the data from the input files (training, validation and test) and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public DMEL(parseParameters parameters) {

        train = new myDataset();
        val = new myDataset();
        test = new myDataset();
        try {
            System.out.println("\nReading the training set: " +
                               parameters.getTrainingInputFile());
            train.readClassificationSet(parameters.getTrainingInputFile(), true);
            System.out.println("\nReading the validation set: " +
                               parameters.getValidationInputFile());
            val.readClassificationSet(parameters.getValidationInputFile(), false);
            System.out.println("\nReading the test set: " +
                               parameters.getTestInputFile());
            test.readClassificationSet(parameters.getTestInputFile(), false);
        } catch (IOException e) {
            System.err.println(
                    "There was a problem while reading the input data-sets: " +
                    e);
            somethingWrong = true;
        }

        //We may check if there are some numerical attributes, because our algorithm may not handle them:
        somethingWrong = somethingWrong || train.hasRealAttributes();

        outputTr = parameters.getTrainingOutputFile();
        outputTst = parameters.getTestOutputFile();
        outputRule = parameters.getOutputFile(0);

        //Now we parse the parameters, for example:
        
         seed = Long.parseLong(parameters.getParameter(0));
         
         popSize = Integer.parseInt(parameters.getParameter(1));
         pCross = Double.parseDouble(parameters.getParameter(2));
         pMut = Double.parseDouble(parameters.getParameter(3));
         numGenerations = Integer.parseInt(parameters.getParameter(4));
        
        //...
    }

    /**
     * It launches the algorithm
     */
    public void execute() {
    	
    	int i, j, k, l;
    	int t;
    	int ele;
    	double prob[];
    	double aux;
        double NUmax = 1.5; //used for lineal ranking
        double NUmin = 0.5; //used for lineal ranking
        double pos1, pos2;
        int sel1, sel2;
    	int data[][];
    	int infoAttr[];
    	int classData[];
    	Vector <Rule> contenedor = new Vector <Rule> ();
    	Vector <Rule> conjR = new Vector <Rule> ();
    	Rule tmpRule;
    	Condition tmpCondition[] = new Condition[1];
    	RuleSet population[];
    	RuleSet hijo1, hijo2;
   	
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found, the data-set has numerical values.");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } else {
            Randomize.setSeed (seed);
           
            nClasses = train.getnClasses();

        	/*Build the nominal data information*/
        	infoAttr = new int[train.getnInputs()];
        	for (i=0; i<infoAttr.length; i++) {
        		infoAttr[i] = train.numberValues(i);
        	}
        	
        	data = new int[train.getnData()][train.getnInputs()];
        	for (i=0; i<data.length; i++) {
        		for (j=0; j<data[i].length; j++) {
        			if (train.isMissing(i, j))
        				data[i][j] = -1;
        			else
        				data[i][j] = train.valueExample(i, j);
        		}
        	}
        	
        	classData = new int[train.getnData()];
        	for (i=0; i<classData.length; i++) {
        		classData[i] = train.getOutputAsInteger(i);
        	}
        	
        	/*Find first-order rules which result interesting*/
        	
        	for (i=0; i<nClasses; i++) {
        		for (j=0; j<infoAttr.length; j++) {
        			for (k=0; k<infoAttr[j]; k++) {
        				tmpCondition[0] = new Condition(j,k);
        				tmpRule = new Rule(tmpCondition);
        				if (Math.abs(computeAdjustedResidual(data, classData, tmpRule, i)) > 1.96) {
        					if (!contenedor.contains(tmpRule)) {
        						contenedor.add(tmpRule);
        						conjR.add(tmpRule);
        					}
        				}
        			}
        		}
        	}

			//Construct the Baker selection roulette
			prob = new double[popSize];
			for (j=0; j<popSize; j++) {
				aux = (double)( NUmax-NUmin)*((double)j/(popSize-1));
				prob[j]=(double)(1.0/(popSize)) * (NUmax-aux);
			}
			for (j=1; j<popSize; j++)
				prob[j] = prob[j] + prob[j-1];

        	/*Steady-State Genetic Algorithm*/
			ele = 2;
        	population = new RuleSet[popSize];
        	while (conjR.size() >= 2) {
        		t = 0;

				System.out.println ("Producing rules of level " + ele);
        		
        		for (i=0; i<population.length; i++) {
        			population[i] = new RuleSet(conjR);
        			population[i].computeFitness(data, classData, infoAttr, contenedor, nClasses);
        		}
        		
        		Arrays.sort(population);
        		
        		while (t < numGenerations && !population[0].equals(population[popSize-1])) {
					System.out.println ("Generation " + t);
        			t++;
        			
    				/*Baker's selection*/
        			pos1 = Randomize.Rand();
        			pos2 = Randomize.Rand();
        			for (l=0; l<popSize && prob[l]<pos1; l++);
        			sel1 = l;
        			for (l=0; l<popSize && prob[l]<pos2; l++);
        			sel2 = l;
    	            
        			hijo1 = new RuleSet(population[sel1]);
        			hijo2 = new RuleSet(population[sel2]);
        			
        			if (Randomize.Rand() < pCross) {
        				RuleSet.crossover1(hijo1, hijo2);
        			} else {
        				RuleSet.crossover2(hijo1, hijo2);        				
        			}
        			
        			RuleSet.mutation(hijo1, conjR, pMut, data, classData, infoAttr, contenedor, nClasses);
        			RuleSet.mutation(hijo2, conjR, pMut, data, classData, infoAttr, contenedor, nClasses);
        			
        			hijo1.computeFitness(data, classData, infoAttr, contenedor, nClasses);
        			hijo2.computeFitness(data, classData, infoAttr, contenedor, nClasses);        			
        			
        			population[popSize-2] = new RuleSet(hijo1);
        			population[popSize-1] = new RuleSet(hijo2);
        			
        			Arrays.sort(population);
        		}
        		
        		/*Decode function*/
        		ele++;
        		conjR.removeAllElements();
				System.out.println ("Fitness of the best chromosome in rule level " + ele + ": " + population[0].fitness);
        		for (i=0; i<population[0].getRuleSet().length; i++) {
    				if (Math.abs(computeAdjustedResidual(data, classData, population[0].getRule(i), i)) > 1.96 ) {
    					if (validarRegla(population[0].getRule(i)) && !contenedor.contains(population[0].getRule(i))) {
    						contenedor.add(population[0].getRule(i));
    						conjR.add(population[0].getRule(i));
    					}
    				}
        		}
        		
        	}     	            
            
            //Finally we should fill the training and test output files
            doOutput(this.val, this.outputTr, data, classData, infoAttr, contenedor, nClasses);
            doOutput(this.test, this.outputTst, data, classData, infoAttr, contenedor, nClasses);
            
            /*Print the rule obtained*/
            for (i=contenedor.size()-1; i>=0; i--) {
            	if (reglaPositiva(this.train, data,classData,infoAttr,nClasses,contenedor.elementAt(i))) {
            		Fichero.AnadirtoFichero(outputRule, contenedor.elementAt(i).toString(train));
            		Fichero.AnadirtoFichero(outputRule, " -> " + consecuente(this.train, data,classData,infoAttr,nClasses,contenedor.elementAt(i)) + "\n");
            	}
            }
            System.out.println("Algorithm Finished");
        }
    }
    
    private boolean validarRegla (Rule regla) {
    	
    	int i, j;
    	
    	for (i=0; i<regla.getRule().length; i++) {
    		for (j=i+1; j<regla.getRule().length; j++) {
        		if (regla.getiCondition(i).getAttribute() == regla.getiCondition(j).getAttribute())
        			return false;
    		}
    	}
    	
    	return true;
    }
    
    private boolean reglaPositiva (myDataset dataset, int data[][], int classData[], int infoAttr[], int nClases, Rule rule) {

		int k, l;
		double tmp1, tmp2;
		double Waip;
		
		Waip = 0;

		tmp1 = Double.NEGATIVE_INFINITY;
		for (l=0; l<nClases; l++) {
			tmp2 = 0;
			for (k=0; k<rule.getRule().length; k++) {
				tmp2 += RuleSet.computeWeightEvidence (data, classData, rule.getiCondition(k), l, infoAttr);
			}
			if (tmp2 > tmp1) {
				tmp1 = tmp2;
			}
		}
		Waip = tmp1;
    	
    	return Waip >0?true:false;
    } 
    
    
    private String consecuente (myDataset dataset, int data[][], int classData[], int infoAttr[], int nClases, Rule rule) {

		int k, l;
		double tmp1, tmp2;
		int pos = 0, classPredicted;
		double Waip;
		
		classPredicted = -1;
		Waip = 0;

		tmp1 = Double.NEGATIVE_INFINITY;
		for (l=0; l<nClases; l++) {
			tmp2 = 0;
			for (k=0; k<rule.getRule().length; k++) {
				tmp2 += RuleSet.computeWeightEvidence (data, classData, rule.getiCondition(k), l, infoAttr);
			}
			if (tmp2 > tmp1) {
				tmp1 = tmp2;
				pos = l;
			}
		}
		classPredicted = pos;
		Waip = tmp1;
    	
    	return dataset.getOutputValue(classPredicted) + " [" + Double.toString(Waip) + "]";
    } 

    private double computeAdjustedResidual (int data[][], int classData[], Rule regla, int clase) {
    	
    	double suma = 0;
    	int i;
    	
    	for (i=0; i<regla.getRule().length; i++) {
    		suma += computeStandarizedResidual(data, classData, regla.getiCondition(i), clase) / Math.sqrt(computeMaximumLikelohoodEstimate(data, classData, regla.getiCondition(i), clase)); 
    	}    	
    	
    	return suma;  
    }

    private double computeStandarizedResidual (int data[][], int classData[], Condition cond, int clase) {
    	double tmp = computeEAipAjq(data, classData, cond, clase);
    	
    	return (computeCountAipAjq (data, classData, cond, clase) - tmp) / Math.sqrt(tmp);
    }

    private double computeMaximumLikelohoodEstimate (int data[][], int classData[], Condition cond, int clase) {
    	
    	int i;
    	double tmp1, tmp2, tmp3;
    	boolean hecho;
    	
    	tmp1 = 0;
    	for (i=0; i<classData.length; i++) {
    		if (classData[i] == clase) {
    			hecho = true;
    			if (data[i][cond.getAttribute()] == -1) {
    				hecho = false;
    			}
    			if (hecho) {
    				tmp1++;
    			}
    		}
    	}    	

    	tmp2 = 0;
    	for (i=0; i<data.length; i++) {
    		hecho = true;
    		if (data[i][cond.getAttribute()] != cond.getValue()) {
    			hecho = false;
    		}
    		if (hecho) {
    			tmp2++;
    		}
    	}
    	
    	tmp3 = 0;
    	for (i=0; i<data.length; i++) {
    		hecho = true;
    		if (data[i][cond.getAttribute()] == -1) {
    			hecho = false;
    		}
    		if (hecho) {
    			tmp3++;
    		}
    	}

    	return (1 - tmp1/tmp3) * (1 - tmp2/tmp3);
    }

    private double computeEAipAjq (int data[][], int classData[], Condition cond, int clase) {
    	
    	int i;
    	double tmp;
    	double EAipAjq;
    	boolean hecho;
    	
    	tmp = 0;
    	for (i=0; i<classData.length; i++) {
    		if (classData[i] == clase) {
    			hecho = true;
    			if (data[i][cond.getAttribute()] == -1) {
    				hecho = false;
    			}
    			if (hecho) {
    				tmp++;
    			}
    		}
    	}    	
    	EAipAjq = tmp;

    	tmp = 0;
    	for (i=0; i<data.length; i++) {
    		hecho = true;
    		if (data[i][cond.getAttribute()] != cond.getValue()) {
    			hecho = false;
    		}
    		if (hecho) {
    			tmp++;
    		}
    	}
    	
    	EAipAjq += tmp;
    	
    	tmp = 0;
    	for (i=0; i<data.length; i++) {
    		hecho = true;
    		if (data[i][cond.getAttribute()] == -1) {
    			hecho = false;
    		}
    		if (hecho) {
    			tmp++;
    		}
    	}
    	
    	return EAipAjq / tmp;
    }
    
    private int computeCountAipAjq (int data[][], int classData[], Condition cond, int clase) {

    	int i;
    	boolean entra;
    	int cont = 0;
    	
    	for (i=0; i<data.length; i++) {
    		if (classData[i] == clase) {
        		entra = true;
        		if (data[i][cond.getAttribute()] != cond.getValue()) {
        			entra = false;
        		}
    			if (entra) {
    				cont++;
    			}
    		}
    	}   	
    	
    	return cont;
    }

    /**
     * It generates the output file from a given dataset and stores it in a file
     * @param dataset myDataset input dataset
     * @param filename String the name of the file
     * @param data containing integer identifiers of nominal values
     * @param classData containing integer identifiers of classes
     * @param infoAttr containing number of values for each attribute
     * @param contenedor containing all the interesting rules
     * @param nClasses indicates number of classes
     */		
    private void doOutput(myDataset dataset, String filename, int data[][], int classData[], int infoAttr[], Vector <Rule> contenedor, int nClases) {
        String output = new String("");
        output = dataset.copyHeader(); //we insert the header in the output file
        //We write the output for each example
        for (int i = 0; i < dataset.getnData(); i++) {
            //for classification:
            output += dataset.getOutputAsString(i) + " " +
                    this.classificationOutput(dataset, i, data, classData, infoAttr, contenedor, nClasses) + "\n";
                    
        }
        Fichero.escribeFichero(filename, output);
    }

    private String classificationOutput(myDataset dataset, int ex, int data[][], int classData[], int infoAttr[], Vector <Rule> contenedor, int nClases) {

		int j, k, l;
		boolean match;
		double tmp1, tmp2;
		int pos = 0, classPredicted;
		double Waip;
		int ejemplo[] = new int[data[0].length];
		
		for (j=0; j<ejemplo.length; j++) {
			if (dataset.isMissing(ex, j))
				ejemplo[j] = -1;
			else
				ejemplo[j] = dataset.valueExample(ex, j);
		}		
		
		classPredicted = -1;
		Waip = 0;

		/*Search a match of the example (following by the container)*/
		for (j=contenedor.size()-1; j>=0; j--) {
			match = true;
			for (k=0; k<contenedor.elementAt(j).getRule().length && match; k++) {
				if (ejemplo[contenedor.elementAt(j).getiCondition(k).getAttribute()] != contenedor.elementAt(j).getiCondition(k).getValue()) {
					match = false;
				}
			}
			if (match) {
				tmp1 = Double.NEGATIVE_INFINITY;
				for (l=0; l<nClases; l++) {
					tmp2 = 0;
					for (k=0; k<contenedor.elementAt(j).getRule().length; k++) {
						tmp2 += RuleSet.computeWeightEvidence (data, classData, contenedor.elementAt(j).getiCondition(k), l, infoAttr);
					}
					if (tmp2 > tmp1) {
						tmp1 = tmp2;
						pos = l;
					}
				}
				if (tmp1 > Waip) {
					classPredicted = pos;
					Waip = tmp1;
				}
			}
		}
		if (classPredicted == -1)
			return "Unclassified";
    	
    	return dataset.getOutputValue(classPredicted);    	
    }

}

