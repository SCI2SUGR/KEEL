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
* @author Written by Luciano Sanchez (University of Oviedo) 21/07/2008 
* @author Modified by J.R. Villar (University of Oviedo) 19/12/2008
* @version 1.0 
* @since JDK1.4 
* </p> 
*/ 

package keel.Algorithms.Fuzzy_Rule_Learning.AdHoc.ClassifierFuzzyWangMendel;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;
import org.core.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

public class ClassifierFuzzyWangMendel {
/** 
* <p> 
* ClassifierFuzzyWangMendel is intended to generate a Fuzzy Rule Based System
* (FRBS) classifier using the Wang and Mendel approach. 
* Hence, the algorithm first partitions the input and the output spaces and thus
* generates a complete Rule Base (RB). 
* Then, for each example in the training dataset the most compatible rule 
* antecedent from the RB is found and assigned with the corresponding output 
* class. Finally, this rule is chosen as a one of the FRBS rules.  
* </p> 
*/ 
	
	//The Randomize object used in this class
	static Randomize rand;
	//The best suite rules
	private static long [] reglas;
	//The number of best suite rules
	private static int numReglas;
	
/** 
* <p> 
* This private static method extract the dataset and the method's parameters  
* from the KEEL environment, learns the FRBS classifier using the Wang and Mendel 
* algorithm and print out the results with the validation dataset. 
* </p> 
* @param tty  unused boolean parameter, kept for compatibility
* @param pc   ProcessConfig object to obtain the train and test datasets
*             and the method's parameters.
*/ 	
	private static void wangMendelFuzzyClassifier(boolean tty, ProcessConfig pc) {
		
		try {
			
			String linea=new String();
			
			int default_neparticion=0;
			int ncruces=0;
			
			ProcessDataset pd=new ProcessDataset();
			
			linea=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
			
			if (pc.parNewFormat) pd.processClassifierDataset(linea,true);
			else pd.oldClusteringProcess(linea);
			
			int ndatos=pd.getNdata();           // Number of examples
			int nvariables=pd.getNvariables();   // Number of variables
			int nentradas=pd.getNinputs();     // Number of inputs
			pd.showDatasetStatistics();
			
			System.out.println("Number of input data="+ndatos);
			System.out.println("Number of output data="+nentradas);
			
			double[][] X = pd.getX();             // Input data
			int[] C = pd.getC();                  // Output data
			int nclases = pd.getNclasses();        // Number of classes
			
			double[] emaximo = pd.getImaximum();   // Maximum and Minimum for input data
			double[] eminimo = pd.getIminimum();
			int[] neparticion=new int[nentradas];
			
			pd.showDatasetStatistics();
			
			
			// Partitions definition
			FuzzyPartition[] particione=new FuzzyPartition[nentradas];
			
			for (int i=0;i<nentradas;i++) {
				System.out.print("Input Variable "+i+": ");
				neparticion[i]=pc.parPartitionLabelNum;
				particione[i]=new FuzzyPartition(eminimo[i],emaximo[i],neparticion[i]);
				System.out.println(particione[i].aString());
			}
			System.out.print("Output Variable:");
			FuzzyPartition particions=new FuzzyPartition(nclases);
			System.out.println(particions.aString());
			
			// Train results
			int [] Ct=new int[C.length];
			
			// Rule base
			RuleBase sistema=
			new RuleBase(particione,particions,
						 RuleBase.product,
						 RuleBase.sum);
			
			
			// Wang-Mendel Algorithm
			FuzzyRule [] reglas2 = new FuzzyRule[X.length];
			reglas = new long[X.length];
			numReglas = 0;
			for (int i=0;i<X.length;i++) {
				
				// For each example, More compatible antecedent is searched
				double compatibilidad=0; long winr=0;
				winr = sistema.codifyAntecents(X[i]);
				double p = sistema.evaluateMembership(winr,X[i]);
			
				// If rule was not found earlier, it's stored. 
				int numeroRegla = ruleSearching(winr);
				if (numeroRegla == -1){
					reglas[numReglas] = winr;
					reglas2[numReglas] = new FuzzyRule(C[i],p);
					numReglas++;
				}
				else if (p > reglas2[numeroRegla].weight) { //If it is better than the previous rule found				
					reglas2[numeroRegla].weight = p;
					reglas2[numeroRegla].consequent = C[i];
				}
				
			}
			//  Weights are tranformed to binary code
			for (int i = 0; i < numReglas; i++)
				//sistema.getComponente(reglas[i]).peso = 1;
				reglas2[i].weight = 1;
			
			long [] nuevasReglas = new long[numReglas];
			FuzzyRule [] nuevasReglas2 = new FuzzyRule[numReglas];
				
			// Result is printed
			for (int r = 0; r < numReglas; r++){
					System.out.println(
									   "IF "+ sistema.variableNames(reglas[r]) + " THEN " +
									   //"S" + sistema.getComponente(reglas[r]).consecuente
									   "S" + reglas2[r].consequent
									   );
				nuevasReglas[r] = reglas[r];
				nuevasReglas2[r] = new FuzzyRule(reglas2[r].consequent,reglas2[r].weight);
			}
			sistema.addRules(nuevasReglas,nuevasReglas2);
			
			// Test error 
			double error_clasificacion=0;
			for (int i=0;i<ndatos;i++) {
				
				double[] respuesta=sistema.myOutput(X[i]);
				int ganadora=0;
				for (int j=1;j<respuesta.length;j++)
					if (respuesta[j]>respuesta[ganadora]) { ganadora=j; }
						
						if (ganadora!=C[i]) error_clasificacion++;
				Ct[i]=ganadora;
				
			}
			error_clasificacion/=ndatos;
			System.out.println("Train error: "+ error_clasificacion);
			pc.trainingResults(C,Ct);
			
			ProcessDataset pdt = new ProcessDataset();
			int nprueba,npentradas,npvariables;
			linea=(String)pc.parInputData.get(ProcessConfig.IndexTest);
			
			if (pc.parNewFormat) pdt.processClassifierDataset(linea,false);
			else pdt.oldClusteringProcess(linea);
			
			nprueba = pdt.getNdata();
			npvariables = pdt.getNvariables();
			npentradas = pdt.getNinputs();
			pdt.showDatasetStatistics();
			
			if (npentradas!=nentradas) throw new IOException("Test file error");
			
			double[][] Xp=pdt.getX(); int [] Cp=pdt.getC(); int [] Co=new int[Cp.length];
			
			// Test error
			error_clasificacion=0;
			for (int i=0;i<nprueba;i++) {
				
				double[] respuesta=sistema.myOutput(Xp[i]);
				int ganadora=0;
				for (int j=1;j<respuesta.length;j++)
					if (respuesta[j]>respuesta[ganadora]) { ganadora=j; }
						
						if (ganadora!=Cp[i]) error_clasificacion++;
				Co[i]=ganadora;
				
			}
			error_clasificacion/=nprueba;
			System.out.println("Test set error: " + error_clasificacion);
			
			pc.results(Cp,Co);
			
			//We write in an output file the Data Base and Rule Base:
			String rutaSalidaBD = (String)pc.outputData.get(0); //fichero BD
			String rutaSalidaBR = (String)pc.outputData.get(1); //fichero BR
			Fichero fichSalida = new Fichero();
			String cad = new String("");
			cad += "DATA BASE:\n";
			for (int i=0;i<nentradas;i++) {
				cad += "\nInput Variable "+i+": ";
				cad += particione[i].aString();
			}
			cad += "\n\nOutput Variable:";
			cad += particions.aString();
			fichSalida.escribeFichero(rutaSalidaBD, cad);
			
			cad = "RULE BASE:\n";
			for (int r = 0; r < numReglas; r++){
				cad += "\nRule_"+(r+1)+": IF "+ sistema.variableNames(reglas[r]) + " THEN " + "S" + reglas2[r].consequent;
			}
			fichSalida.escribeFichero(rutaSalidaBR, cad);
						
		} catch(FileNotFoundException e) {
			System.err.println(e+" Train not found");
		} catch(IOException e) {
			System.err.println(e+" Read Error");
		}
		
	}

/** 
* <p> 
* This private static method searchs for a certain rule in the set of best 
* suite rule data base. The value -1 is returned if that rule is not in 
* the rule data base. 
* </p> 
* @param winr  the rule to be searched for.
* @return the position of the searched rule or -1 if it not found.
*/ 	
	private static int ruleSearching(long winr){
		boolean salir = false;
		int i;
		for (i = 0; (i < numReglas)&&(!salir); i++){
			salir = (winr == reglas[i]);
		}
		if (salir){
			return (i-1);
		}
		return -1;
	}
	
/** 
* <p> 
* This public static method runs the algorithm that this class concerns with. 
* </p> 
* @param args  Array of strings to sent parameters to the main program. The 
*              path of the algorithm's parameters file must be given.
*/ 	
	public static void main(String args[]) {
		
		boolean tty=false;
		ProcessConfig pc=new ProcessConfig();
		System.out.println("Reading configuration file: "+args[0]);
		if (pc.fileProcess(args[0])<0) return;
		int algo=pc.parAlgorithmType;
		rand=new Randomize();
		rand.setSeed(pc.parSeed);
		ClassifierFuzzyWangMendel wm=new ClassifierFuzzyWangMendel();
		wm.wangMendelFuzzyClassifier(tty,pc);
		
	}
	
	
}

