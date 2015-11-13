/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    S. García (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
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



package keel.Algorithms.RST_Learning;

import java.util.Arrays;

import keel.Algorithms.RST_Learning.Operators;

/**
 * 
 * File: RSTData.java
 * 
 * RSTData utility class
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
public class RSTData {

	private static double data[][];
	private static int discrete[][];
	private static double prob[][][];
	private static double stdDev [];
	private static double mean [];
	private static int output[];
	
	private static int FS[];
	private static int featuresSelected;
	
	private static int IS[];
	private static int instancesSelected;
	
	private static int instances;
	private static int features;
	private static int classes;
	
	private static double normalizeFactor;
	
	private static double alpha;
	
	private static boolean nominal [];
	
	private static boolean marked [][];
	private static int markedF;
	
	private static int avc[][][];
	private static int av[][];
	private static int numValues[];
	private static int maxNumValues;
	
	public static void setNumValues(){
		
		numValues=new int [features];
		
		for(int i=0;i<features;i++){
			numValues[i]=0;
		}

	}

	public static void setNumValue(int value, int position){
		
		numValues[position]=value;
		maxNumValues=Math.max(maxNumValues, value);
		
	}
	
	public static void setNominal(boolean vec []){
		
		nominal= new boolean[features];
		
		for(int i=0;i <features;i++){
			nominal[i]=vec[i];
		}
		
		discretize();
		computeAVC();
		
	}

	/**
	 * Loads the training data into the classifier
	 * 
	 * @param newData Data represented with continuous values
	 */
	public static void setData(double newData[][]){	
		
		instances = newData.length;
		features = newData[0].length;
		
		data = new double [instances][features];
		
		for(int i=0;i<instances;i++){		
			for(int j=0;j<features;j++){		
				data[i][j]=newData[i][j];
			}
		}

		FS = new int [features];
		IS = new int [instances];

		stdDev = new double [features];
		mean = new double [features];
		
		Arrays.fill(FS, 1);
		featuresSelected=features;
		
		Arrays.fill(IS, 1);
		instancesSelected=instances;
		
		normalizeFactor=1.0;
		
		marked = new boolean [features][instances];
		computeStd();
		
		
		
	}//end-method
	
	private static void computeAVC(){
		
		avc= new int [features][maxNumValues][classes];
		av=new int [features][maxNumValues];
		
		int aux;
		
		
		//calcular AVC
		
		for(int i=0;i<features;i++){
			for(int j=0;j<maxNumValues;j++){
				for(int k=0;k<classes;k++){
					avc[i][j][k]=0;
				}
				av[i][j]=0;
			}			
		}
		
		for(int i=0;i<instances;i++){
			aux=output[i];
			for(int j=0;j<features;j++){
				if(nominal[j]){
					avc[j][discrete[i][j]][aux]++;
					av[j][discrete[i][j]]++;
				}
			}
			
		}
		
		computeProbabilities();
	}
	
	private static void computeProbabilities(){
		
		double aux;
		double val1,val2;
		double max=0.0;
		
		prob=new double[features][maxNumValues][maxNumValues];

		for(int att=0; att<features; att++){
			
			if(nominal[att]){
				
				for(int i=0;i<maxNumValues;i++){
					prob[att][i][i]=0.0;
					for(int j=i+1;j<maxNumValues;j++){
						
						aux=0.0;
					
						for(int c=0;c<classes;c++){
							
							if((av[att][i]!=0)&&(av[att][j]!=0)){
								val1=(double)avc[att][i][c]/(double)av[att][i];
								val2=(double)avc[att][j][c]/(double)av[att][j];
								
								aux+=Math.abs(val1-val2);
							}
						}
						prob[att][i][j]=aux;
						prob[att][j][i]=aux;
						if(max<prob[att][i][j]){
							max=prob[att][i][j];
						}
					}
					
					
				}
				
			}
			
		}
		
		//normalize prob matrix
		for(int att=0; att<features; att++){
			for(int i=0;i<maxNumValues;i++){
				for(int j=i+1;j<maxNumValues;j++){
					prob[att][i][j]/=max;
					prob[att][j][i]/=max;
				}
			}
		}
		
		
		
		String texto;
		for(int att=0;att<features;att++){
			
			texto="";
			for(int i=0;i<maxNumValues;i++){
				for(int j=0;j<maxNumValues;j++){
					texto+=prob[att][i][j]+" ";
				}
				texto+="\n";
			}
			//System.out.println(texto);
		}
		
	}
	
	private static void discretize(){
		
		discrete= new int[instances][features];
		
		//cada instancia se multiplica
		for(int j=0;j<features;j++){
			
			if(nominal[j]){
				for(int i=0;i<data.length;i++){
					discrete[i][j]=discrete(data[i][j],j);
				}
			}

		}

	}
	
	private static int discrete(double val, int att){
		
		int res;
		
		res=(int)(val*(double)(numValues[att]-1));
		
		return res;
	}
	
	/**
	 * Loads the training output into the classifier
	 * 
	 * @param newOutput Output attribute of the training data
	 */
	public static void setOutput(int newOutput[]){	
		
		output=new int [data.length];
		
		System.arraycopy(newOutput,0,output, 0, data.length);
		
	}//end-method 
	
	private static void computeStd(){
		
		for(int i=0;i<features;i++){
			stdDev[i]=0.0;
			mean[i]=0.0;
		}
		
		for(int i=0;i<features;i++){
			for(int j=0;j<instances;j++){
				mean[i]+=data[j][i];
			}
			mean[i]/=(double)instancesSelected;
		}
		
		for(int i=0;i<features;i++){
			for(int j=0;j<instances;j++){
				stdDev[i]+=((data[j][i]-mean[i])*(data[j][i]-mean[i]));
			}
			stdDev[i]/=instancesSelected-1; 
			stdDev[i]=Math.sqrt(stdDev[i]);
		}

	}
	
	private static double similarity(double a, double b, int att){
		
		double std;
		
		double first, second;
		
		double dist;
		
		if(nominal[att]){
			if(a==b){
				dist=0.0;
			}else{
				 
				dist=prob[att][discrete(a,att)][discrete(b,att)];

			}
			
			return 1.0-dist;
		}
		else{
			std=stdDev[att];

			
			first = (a-b+std)/std;
			second = (b-a+std)/std;

			return Math.max(Math.min(first, second),0);

		}

	}
	
	public static double instanceSimilarity(int a, int b){
		
		double value=1.0;

		for(int i=0;i<features;i++){
			if(FS[i]==1){
				//value=Operators.TNormLukasiewicz(value,similarity(data[a][i],data[b][i],i));
				value=Operators.TNormMin(value, similarity(data[a][i],data[b][i],i));
				//value=Operators.TNormProd(value, similarity(data[a][i],data[b][i],i));
				if (value == 0.0){
					break;
				}
			}
		}

		return value;

	}
	
	public static boolean isAttribute(int att){
		
		if(FS[att]==0){
			return false;
		}
		
		return true;
	}
	public static void setAttribute(int att){
		
		if(FS[att]==0){
			FS[att]=1;
			featuresSelected++;
		}

	}
	
	public static void setClasses(int val){
		
		classes=val;

	}
	
	public static void unsetAttribute(int att){
		
		if(FS[att]==1){
			FS[att]=0;
			featuresSelected--;
		}

	}
	
	public static void setAttributes(int [] atts){
		
		featuresSelected=0;
		
		for(int i=0;i<features;i++){
			FS[i]=atts[i];
			if(atts[i]==1){
				featuresSelected++;
			}
		}

	}
	
	public static void setInstances(int [] ins){

		instancesSelected=0;
		
		for(int i=0; i< instances;i++){
			IS[i]=ins[i];
			if(ins[i]==1){
				instancesSelected++;
			}
		}

	}
	
	public static int [] getAttributes(){
		 
		int newFS [];
		
		newFS= new int [FS.length];
		
		for(int i=0;i<FS.length;i++){
			newFS[i]=FS[i];
		}
		
		return newFS;

	}
	
	public static void clearAttributes(){
		
		featuresSelected=0;
		
		Arrays.fill(FS, 0);

	}
	
	public static double computeGamma(){
		
		double lower;
		double current;
		double average=0.0;
		
		for(int i=0;i<instances;i++){
			
			if(IS[i]==1){
				lower=1.0;
				
				for(int j=0;j<instances;j++){
				
					if((IS[i]==1)&&(output[i]!=output[j])){
						
						current=instanceSimilarity(i,j);
						current=1.0-current;
						
						lower=Math.min(current, lower);
						
						if (lower == 0.0){
							break;
						}
					}
					
				}

				average+=lower;
			}

		}
		
		average/=instancesSelected;
		
		average/=normalizeFactor;
		
		return average;
	}
	
	private static double computeInnerGamma(){
		
		double lower;
		double current;
		double average=0.0;
		
		for(int i=0;i<instances;i++){
			
			if(IS[i]>0){
				if(IS[i]==2){
					average+=1.0;
				}
				else{
					lower=1.0;
					
					for(int j=0;j<instances;j++){
					
						if((IS[i]>0)&&(output[i]!=output[j])){
							
							current=1.0-instanceSimilarity(i,j);
							
							lower=Math.min(current, lower);
							
							if (lower == 0.0){
								break;
							}
						}
						
					}
	
					average+=lower;
					
					if (lower == 1.0){
						marked[markedF][i]= true;
					}
				}
			}
			

		}
		
		average/=instancesSelected;
		
		average/=normalizeFactor;
		
		return average;
	}

	public static void setNormalization(double value){
		normalizeFactor=value;
	}
	
	public static int getnFeatures(){
		return featuresSelected;
	}
	
	public static void computeBestFeatures(){
		
		double best=-1.0;
		int selected;
		double newGamma, gamma;
		
		clearAttributes();
		gamma=0.0;		
		
		for(int i=0;i<features;i++){
			Arrays.fill(marked[i],false);
		}
		
		do{		
			selected=-1;
			for(int i=0;i<features;i++){

				if(FS[i]==0){
					FS[i]=1;
					markedF=i;
					newGamma=computeInnerGamma();
					FS[i]=0;
					if((newGamma>=best)){
						best=newGamma;
						selected=i;
					}
				}
			}
			
			if(selected>-1){
				FS[selected]=1;
				for(int i=0;i<instances;i++){
					if(marked[selected][i]){
						IS[i]=2;
					}
				}
				featuresSelected++;
				gamma=best;
			}
			else{
				break;
			}
			
		}while((gamma<alpha)&&(featuresSelected<features));
		
		for(int i=0;i<instances;i++){
			if(IS[i]==2){
				IS[i]=1;
			}
		}
	}
	
	public static void setAlpha(double value){
		alpha=value;
	}
	
	public static void setAllInstances(){
		Arrays.fill(IS, 1);
	}
	
} //end-class 
