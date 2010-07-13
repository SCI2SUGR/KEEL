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
 * 
 * File: Deeps.java
 * 
 * The DeEps Algorithm.
 * A new instance based classifier, wich rather than using distance, makes
 * use of the frequency of the subsets of values present in the train instances
 * 
 * @author Written by Joaquín Derrac (University of Granada) 14/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
package keel.Algorithms.Lazy_Learning.Deeps;

import keel.Algorithms.Lazy_Learning.LazyAlgorithm;

import java.util.*;
import org.core.*;

public class Deeps extends LazyAlgorithm{
	
	//Parameters

	double ALPHA;
	
	//Adictional structures
	
	double classData[][][];
	int binaryData[][][];
	Itemset maxBorder [][];
	int maxBorderSize [];
	double score[];

	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public Deeps (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="DeEps";

		//Inicialization of auxiliar structures

	    classData= new double [nClasses][][];
	    
	    for(int i=0;i<nClasses;i++){
	    	classData[i]=new double [nInstances[i]][inputAtt];
		}

	    int index []=new int[nClasses];
	    for(int i=0;i<nClasses;i++){
	    	index[i]=0;
		}

	    for(int i=0;i<trainData.length;i++){
	    	int instanceClass=trainOutput[i];
		    for(int j=0;j<trainData[i].length;j++){
		    	classData[instanceClass][index[instanceClass]][j]=trainData[i][j];
		    }
		    index[instanceClass]++;
		}

	    binaryData= new int [nClasses][][];

	    for(int i=0;i<nClasses;i++){
	    	binaryData[i]=new int[nInstances[i]][inputAtt];
		}
 
	    maxBorder= new Itemset [nClasses][]; 
	    maxBorderSize= new int[nClasses]; 
	    
	    score=new double[nClasses];
	    
	    //set Itemset max size
	    
	    Itemset.setMaxItems(inputAtt);
	    
		//Initialization stuff ends here. So, we can start time-counting
		
		setInitialTime(); 
		
	} //end-method     
	    
	/** 
	 * Reads configuration script, to extract the parameter's values.
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */	
	protected void readParameters (String script) {
		
		String file;
		String line;
		StringTokenizer fileLines, tokens;
		
	    file = Files.readFile (script);
	    fileLines = new StringTokenizer (file,"\n\r");
	    
	    //Discard in/out files definition
	    fileLines.nextToken();
	    fileLines.nextToken();
	    fileLines.nextToken();

	    //Getting the Alpha parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    ALPHA = Double.parseDouble(tokens.nextToken().substring(1));

	}//end-method
	
	/** 
	 * Evaluates a instance to predict its class.
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted 
	 * 
	 */
	protected int evaluate (double example[]) {
		
		int output=-1;
		double min;
		double max;
		double value;
		Itemset EPs [];
		
		//calculate binary data
		for(int j=0;j<example.length;j++){
			
			min=example[j]-ALPHA;
			max=example[j]+ALPHA;			
			
			for(int k=0;k<nClasses;k++){
				for(int i=0;i<nInstances[k];i++){
				
					value=classData[k][i][j];

					if((value <= max)&&(value >= min)){
						binaryData[k][i][j]=1;
					}
					else{
						binaryData[k][i][j]=0;
					}
				}
				
			}
			
			
		}
		/*for(int k=0;k<nClasses;k++){
			for(int i=0;i<nInstances[k];i++){
				System.out.println(i+" "+LazyAlgorithm.printInstance(binaryData[k][i]));
			}
		}*/
		//leave-one-out
		
		for(int k=0;k<nClasses;k++){
			for(int i=0;i<nInstances[k];i++){
				if(same(example,classData[k][i])){
					for(int j=0;j<example.length;j++){
						binaryData[k][i][j]=0;
					}
				}
			}
		}
		
		//extract horizontal borders
		
		extractHorizontals();
		for(int i=0; i< nClasses; i++){
			//System.out.println("Clase "+i+" Bordes:"+maxBorder[i].length);
			for(int j=0; j< maxBorder[i].length; j++){
				//System.out.println("Borde: "+j+" "+maxBorder[i][j].toString());
			}
		}
		
	
		//For each border
		for(int i=0; i< nClasses; i++){
			
			//System.out.println("Clase: "+i);
			//apply jepProducer
			EPs=jepProducer(i);
			
			//check score
			score[i]=calculateScore(EPs,i);
			
			//System.out.println("Patrones");
			for(int j=0; j< EPs.length; j++){
				//System.out.println(EPs[j].toString());
			}
			
			//System.out.println("Size: "+EPs.length);

		}
		
		//select class
		output=-1;
		max=Double.MIN_VALUE;
		
		for(int i=0;i<nClasses; i++){
			
			if(score[i]>max){
				output=i;
				max=score[i];
			}
		}

		return output;
		
	}//end-method
	
	/** 
	 * Extract horizontals border.
	 * This method gets the horizontal borders of each class,
	 * trying to cover with it every intance in each class train data
	 * 
	 */
	
	private void extractHorizontals(){
		
		Itemset aux;
		boolean subset;
		
		//For each class, extract maximal itemsets
		for(int k=0;k<nClasses;k++){
		
			if(nInstances[k]>0){
				
				maxBorder[k]=new Itemset[1];
				maxBorder[k][0]=new Itemset(binaryData[k][0]);
				maxBorderSize[k]=1;
				
				for(int i=1;i<nInstances[k];i++){
					
					aux=new Itemset(binaryData[k][i]);
					
					subset=false;
					for(int index=0;index< maxBorder[k].length && !subset ;index++){
						if(aux.isSubset(maxBorder[k][index])){
							subset=true;
						}
					}
					if(!subset){
						insertItemset(aux,k,i);
					}
				}
			}
			else{
				maxBorder[k]=new Itemset[1];
				maxBorder[k][0]=new Itemset();
			}
		}
		
	}//end-method
	
	/** 
	 * Inserts a Itemset in the border of a class,
	 * cleaning the itemsets subsumited
	 * 
	 * @param newIt New Itemset to insert
	 * @param inClass Class selected
	 * 
	 */
	
	private void insertItemset(Itemset newIt, int inClass,int index){
		
		Itemset copy [];
		boolean subset;
		int copySize;

		//At first, we copy the new Itemset
		copy=new Itemset [maxBorderSize[inClass]+1];
		copy[0]=newIt;

		copySize=1;
		
		for(int i=0;i <maxBorderSize[inClass];i++){
			
			subset=false;
			
			//Insert the older border only if it is not a subset
			
			if(maxBorder[inClass][i].isSubset(copy[0])){
					subset=true;
			}
						
			if(!subset){
				copy[copySize]=maxBorder[inClass][i];
				copySize++;
			}
		}
		//System.out.println("Clase: "+inClass+" "+copySize+" "+index);
		//System.out.println(newIt.toString());
		maxBorder[inClass]=new Itemset [copySize];
		
		System.arraycopy(copy, 0, maxBorder[inClass], 0, copySize);
		maxBorderSize[inClass]=copySize;
		
		
	}//end-method

	/** 
	 * Scoring method. It uses the borders discovered before
	 * to score the pertenency of the instance to the class
	 * 
	 * @param border Borders discovered
	 * @param nClass Class tested
	 * @return Score calculated
	 * 
	 */
	private double calculateScore(Itemset border[],int nClass){
		
		double score;
		int size;
		int selected [];
		Itemset aux;
		int count;
		
		size=nInstances [nClass];
		selected=new int [size];
		
		for(int i=0;i<size;i++){
			selected[i]=0;
		}
		
		//Select each instance wich contains any border discovered
		for(int pointer=0;pointer < border.length; pointer++){
			
			aux=border[pointer];
			
			for(int i=0;i<binaryData[nClass].length;i++){
				if(aux.isSubSetBinary(binaryData[nClass][i])){
					selected[i]=1;
				}
			}
		}
		
		count=0;
		for(int i=0;i<size;i++){
			count+=selected[i];
		}
		
		score=(double)count/(double)size;
		
		return score;
		
	}//end-method

	/** 
	 * The jepProducer algorithm.
	 * Discovers the EPs border from the test instance to every
	 * horizontal border from each class.
	 * 
	 * @param nClass Index to class tested
	 * @return EPs border discovered 
	 *  
	 */
	
	private Itemset [] jepProducer(int nClass){
		
		Itemset result [];
		Itemset aux [];
		Itemset jep;
		Itemset union [];
		int unionSize;
		int unionIndex;
		int auxIndex;
		boolean isSubset;
		
		aux=new Itemset [maxBorder[nClass].length];
		
		//merge enemy's horizontal borders
		
		unionSize=0;
		
		for(int i=0;i<nClasses;i++){
			unionSize+=maxBorder[i].length;		
		}
		
		unionSize-=maxBorder[nClass].length;
		
		union=new Itemset [unionSize];
		
		unionIndex=0;
		
		for(int i=0;i<nClasses;i++){
			if(i!=nClass){
				System.arraycopy(maxBorder[i], 0, union, unionIndex, maxBorder[i].length);
				unionIndex+=maxBorder[i].length;
			}		
		}	
		
		auxIndex=0;
		
		//Extract Eps
		for(int i=0; i<maxBorder[nClass].length; i++){
		
			//look if some Border is subset
			isSubset=false;
			
			for(int j=0;j< unionSize && !isSubset;j++){				
				isSubset=maxBorder[nClass][i].isSubset(union[j]);
			}
		
			if(!isSubset){
				jep=borderDiff(maxBorder[nClass][i],union);
				
				if(jep.getSize()>0){
					aux[auxIndex]=jep;
					auxIndex++;
				}
			}
		}
		
		result=new Itemset [auxIndex];

		System.arraycopy(aux, 0, result, 0, auxIndex);

		return result;
		
	}//end-method
	
	/** 
	 * The Border-Diff routine.
	 * Adds to the border any subset of the new Itemset not
	 * already covered by the border
	 * 
	 * @param target New Itemset to merge
	 * @param border Initial border
	 * @return Difference border
	 * 
	 */
	private Itemset borderDiff(Itemset target, Itemset [] border){
		
		Itemset result=new Itemset();

		//Join with the first border
		result=result.merge(target.diference(border[0]));
		
		//Join with other borders
		for(int i=1;i<border.length;i++){			
			result=result.merge(target.diference(border[i]));
		}

		return result;
		
	}//end-method
	
} //end-class 

