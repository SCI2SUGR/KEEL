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
 * @author Written by Jose A. Saez Munoz, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * Date: 06/01/10
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Preprocess.NoiseFilters.SaturationFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;
import org.core.Randomize;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;
import keel.Algorithms.Genetic_Rule_Learning.Globals.FileManagement;


/**
 * <p>
 * This class implements the Gamberger's algorithm to remove class noise
 * </p>
 */
public class SaturationFilter {
	
	private boolean[][] literals;
	private int numlit;
	
	private Instance[] instancesTrain;
	private Vector noisyInstances;		// indexes of the noisy instances from training set
	
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * Constructor of the class
	 * </p>
	 */
	public SaturationFilter(){
		
		Randomize.setSeed(Parameters.seed);
				
		InstanceSet is = new InstanceSet();
		
		try {	
			is.readSet(Parameters.trainInputFile,true);
        }catch(Exception e){
            System.exit(1);
        }
        
        instancesTrain = is.getInstances();
        Parameters.numClasses = Attributes.getOutputAttributes()[0].getNumNominalValues();
        Parameters.numAttributes = Attributes.getInputAttributes().length;
        Parameters.numInstances = instancesTrain.length;	
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It runs the noise elimination algorithm for multiclass problems
	 * </p>
	 */
	public void run(){
		
		boolean[] noisyIns  = new boolean[Parameters.numInstances];
		Arrays.fill(noisyIns, false);
		
		
		for(int posClass = 0 ; posClass < Parameters.numClasses ; ++posClass){
			
			System.out.println("\n\n\n-----> POSITIVE CLASS = " + posClass);

			Literals lit = new Literals(instancesTrain, posClass);
			numlit = lit.getNumLiterals();
			literals = lit.getLiteralsMatrix();
			
			boolean[] partialNoise = saturationFilter(posClass);
			
			//clear repeated instances
			for(int i = 0 ; i < Parameters.numInstances ; ++i)
				if(partialNoise[i] == true)
					noisyIns[i] = true;
		}
		
		
		noisyInstances = new Vector();
		for( int i = 0 ; i < Parameters.numInstances ; ++i )
			if(noisyIns[i] == true)
				noisyInstances.add(i);
		
		System.out.println("Numero de ejemplos eliminados = " + noisyInstances.size());
		System.out.println(noisyInstances);
			
		createDatasets(Parameters.trainInputFile,Parameters.trainOutputFile,Parameters.testInputFile,Parameters.testOutputFile);
	}

//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * Constructor of the class
	 * </p>
	 */
	public boolean[] saturationFilter(int positiveClass){
		
		boolean salir = false;
		double[] We = new double[Parameters.numInstances];
		
		IntegerSet E = new IntegerSet(Parameters.numInstances);
		for(int i = 0 ; i < Parameters.numInstances ; ++i)
			E.addValue(i);
		
		boolean[] hasNoise = new boolean[Parameters.numInstances];	// true if the instance has noise
		Arrays.fill(hasNoise, false);
		
		
		while( E.size() > 0 && !salir ){
			
			E.print();
			
			// set weights of all instances to 0
			Arrays.fill(We, 0);
			Vector<pnPair> U = pnPair.getPNpairs(E,instancesTrain,positiveClass);
			
			
			//--------------------------------------------------------------------------
			// if only there are examples of one class, exit
			if(U.size() == 0)
				break;
			
			// to remove contradictory instances
			for(int i = 0 ; i < U.size() ; ++i){
				
				int num = U.get(i).numliteralCovers(literals, numlit);
				
				if( num == 0 ){
					
					//hasNoise[U.get(i).getPosEx()] = true;
					E.removeValue(U.get(i).getPosEx());
					
					//hasNoise[U.get(i).getNegEx()] = true;
					E.removeValue(U.get(i).getNegEx());	
				}
			}
			
			// Reconstruir el vector U
			U = pnPair.getPNpairs(E,instancesTrain,positiveClass);
			//------------------------------------------------------------------------
			
			Vector<Integer> L = minimalCov(positiveClass, U);
			
			for(int lt = 0 ; lt < L.size() ; ++lt){
				
				IntegerSet Pe = new IntegerSet(Parameters.numInstances);
				IntegerSet Ne = new IntegerSet(Parameters.numInstances);

				for(int pn = 0 ; pn < U.size() ; ++pn){
					
					if(U.get(pn).onlyOneLiteral(literals, L ,L.get(lt))){
						Pe.addValue(U.get(pn).getPosEx());
						Ne.addValue(U.get(pn).getNegEx());
					}
				}
				
				if(Pe.size() == 0){
					L.remove(lt);
					lt--;
				}
				
				else{
					for(int p = 0 ; p < Pe.size() ; ++p){We[Pe.getElement(p)] += (1.0/Pe.size());}
					for(int n = 0 ; n < Ne.size() ; ++n){We[Ne.getElement(n)] += (1.0/Ne.size());}
				}
			}
			
			// catch the example with highest weight
			int indexSelected = 0;
			double max = We[0];
			for(int i = 1 ; i < Parameters.numInstances ; ++i)
				if(We[i] > max){
					indexSelected = i;
					max = We[i];
				}
						
			if(max > Parameters.noiseSensitivity){
				hasNoise[indexSelected] = true;
				E.removeValue(indexSelected);
			}
			
			else
				salir = true;
		}
		
		return hasNoise;
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It runs the algorithm
	 * </p>
	 */
	public Vector<Integer> minimalCov(int positiveClass, Vector<pnPair> U){
		
		Vector<Integer> mL = new Vector<Integer>();

		//compute weights
		for(int i = 0 ; i < U.size() ; ++i){
			int num = U.get(i).numliteralCovers(literals,numlit);
			U.get(i).setWeight(1.0/num);
		}

		// next step
		Vector<pnPair> Uaux = new Vector<pnPair>();
		for(int i = 0 ; i < U.size() ; ++i)
			Uaux.add(U.get(i));
		
		
		int pos;
		double max;
		while(Uaux.size() > 0){
			
			// coger ejemplo de mayor peso en Uaux
			pos = 0;
			max = Uaux.get(0).getWeight();
			for(int i = 1 ; i < Uaux.size() ; ++i)
				if(Uaux.get(i).getWeight() > max){
					pos = i;
					max = Uaux.get(i).getWeight();
				}
			
			// coger literales que cubren ese par de ejemplos
			Vector<Integer> Laux = Uaux.get(pos).indexLiterals(literals, numlit);
			
			// compute the weight for each selected literal
			double[] wlit = new double[Laux.size()];
			for(int i = 0 ; i < Laux.size() ; ++i)
				wlit[i] = sumOfWeights(Laux.get(i),Uaux);
			
			// select the literal with maximal weight
			pos = 0;
			max = wlit[0];
			for(int i = 1 ; i < Laux.size() ; ++i)
				if(wlit[i] > max){
					pos = i;
					max = wlit[i];
				}
			
			// add the  literal with maximal weight
			mL.add(Laux.get(pos));
			
			// remove covered examples in Uaux covered by the last selected literal
			remove(Laux.get(pos),Uaux);
		}
		
		return mL;
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It runs the algorithm
	 * </p>
	 */
	public void remove(int indexLit, Vector<pnPair> Uaux){
		
		for(int i = 0 ; i < Uaux.size() ; ++i)
			if(Uaux.get(i).isCovered(literals, indexLit)){
				Uaux.remove(i);		
				i--;
			}
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * Constructor of the class
	 * </p>
	 * @param indexLit
	 * @param Uaux
	 */
	public double sumOfWeights(int indexLit, Vector<pnPair> Uaux){
		
		double res = 0;
		
		for(int i = 0 ; i < Uaux.size() ; ++i)
			if(Uaux.get(i).isCovered(literals, indexLit))
				res += Uaux.get(i).getWeight();
				
		return res;
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It apllies the changes to remove the noise 
	 * </p>
	 */
	public void createDatasets(String trainIN, String trainOUT, String testIN, String testOUT){
		
		
		// to create the train file-----------------------------------------
  		String header = "";
  		header = "@relation " + Attributes.getRelationName() + "\n";
  		header += Attributes.getInputAttributesHeader();
  		header += Attributes.getOutputAttributesHeader();
  		header += Attributes.getInputHeader() + "\n";
  		header += Attributes.getOutputHeader() + "\n";
        header += "@data\n";
		
		FileManagement fm = new FileManagement();
		Attribute []att = Attributes.getInputAttributes();

		try {
			
			fm.initWrite(trainOUT);
			fm.writeLine(header);
			
			int numNoisyEx = 0;
			for(int i = 0 ; i < instancesTrain.length ; i++){
				
				if( (numNoisyEx < noisyInstances.size()) && (Integer)noisyInstances.get(numNoisyEx) == i){
					numNoisyEx++;
				}
				
				else{
					
					boolean[] missing = instancesTrain[i].getInputMissingValues();
					String newInstance = "";
					
					for(int j = 0 ; j < Parameters.numAttributes ; j++){
						
						if(missing[j])
							newInstance += "?";
						
						else{
							if(att[j].getType() == Attribute.REAL)
								newInstance += instancesTrain[i].getInputRealValues(j);
							if(att[j].getType() == Attribute.INTEGER)
								newInstance += (int)instancesTrain[i].getInputRealValues(j);
							if(att[j].getType() == Attribute.NOMINAL)
								newInstance += instancesTrain[i].getInputNominalValues(j);
						}
						
						newInstance += ", "; 
					}
					
					String className = instancesTrain[i].getOutputNominalValues(0);
					newInstance += className + "\n";
					
					fm.writeLine(newInstance);
				}
			}
				
			fm.closeWrite();
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		
		
		// to create the test file-----------------------------------------
		try {
			String s;
			File Archi1 = new File(testIN);
		    File Archi2 = new File(testOUT);
		    BufferedReader in;
			in = new BufferedReader(new FileReader(Archi1));
		    PrintWriter out = new PrintWriter(new FileWriter(Archi2));
		      
		    while ((s = in.readLine()) != null)
		    	out.println(s);
		    
		    in.close();
		    out.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
}