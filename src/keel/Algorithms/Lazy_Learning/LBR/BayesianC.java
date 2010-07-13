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
 * File: BayesianC.java
 * 
 * Not correctly documented yet!
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.LBR;

class BayesianC{
	
	private double [][] rawInstances;
	private int [][] instances;
	private int [] outputs;
	private boolean [] correct;
	private static int nClasses;
	private static int nInputs;
	private int [] classCount;
	private int [][][] AVC;
	private static int [] numValues;
	private static int maxNumValues;
	private int looError;
	
	
	public BayesianC(double [][] newInstances,int [] newOutputs){
		
		rawInstances=new double [newInstances.length][newInstances[0].length];
		instances=new int [newInstances.length][newInstances[0].length];
		
		for(int i=0;i<newInstances.length;i++){
			System.arraycopy(newInstances[i], 0, rawInstances[i], 0, newInstances[i].length);
		}
		
		outputs=new int[newOutputs.length];
		System.arraycopy(newOutputs, 0, outputs, 0, newOutputs.length);
		
		correct=new boolean[newOutputs.length];
		
		classCount=new int[nClasses];
		
		AVC=new int[nInputs][maxNumValues][nClasses];
		
		discretize();

	}
	
	private void discretize(){
		
		//cada instancia se multiplica
		for(int j=0;j<nInputs;j++){
			
			for(int i=0;i<rawInstances.length;i++){
				instances[i][j]=(int)(rawInstances[i][j]*(double)(numValues[j]-1));
			}
			
		}

	}
	
	public static void setNClasses(int classes, int inputs){
		
		nClasses=classes;
		nInputs=inputs;
		
	}
	
	public static void setNumValues(){
		
		numValues=new int [nInputs];
		
		for(int i=0;i<nInputs;i++){
			numValues[i]=0;
		}

	}

	public static void setNumValue(int value, int position){
		
		numValues[position]=value;
		maxNumValues=Math.max(maxNumValues, value);
		
	}
	
	public static int getNumValue(int position){
		
		return numValues[position];

	}
	
	public void calcProbabilities(){
		
		int output;
		
		//calcular probabilidades de clases
		
		for(int i=0;i<nClasses;i++){
			classCount[i]=0;
		}
		
		for(int i=0;i<outputs.length;i++){
			classCount[outputs[i]]++;
		}
		
		//calcular AVC
		
		for(int i=0;i<nInputs;i++){
			for(int j=0;j<maxNumValues;j++){
				for(int k=0;k<nClasses;k++){
					AVC[i][j][k]=0;
				}
			}			
		}
		
		for(int i=0;i<instances.length;i++){
			output=outputs[i];
			for(int j=0;j<nInputs;j++){
				AVC[j][instances[i][j]][output]++;
			}
			
		}
	}
	
	public void doLeaveOneOut(){
		
		int selected;
		
		//para cada instancia
		for(int i=0;i<correct.length;i++){
			
			//quitarla del conjunto
			drop(instances[i], outputs[i]);
			
			selected=selectClass(instances[i]);
			
			if(selected==outputs[i]){
				correct[i]=true;
			}
			else{
				correct[i]=false;
			}
			
			//ponerla en el conjunto
			add(instances[i], outputs[i]);			
		}
		
		//calcular error
		
		looError=0;
		
		for(int i=0;i<correct.length;i++){
			if(correct[i]==false){
				looError++;
			}
		}
	}
	
	private void drop(int [] instance, int output){
		
		for(int i=0;i<instance.length;i++){
			AVC[i][instance[i]][output]--;
		}
		
	}

	private void add(int [] instance, int output){
		
		for(int i=0;i<instance.length;i++){
			AVC[i][instance[i]][output]++;
		}
		
	}
	
	public int looError(){
		
		return looError;
	}
	
	public int tempClassifier(int att,int value){
		
		int totalErrors=0;
		int [][][] AVCCopy;
		
		AVCCopy= new int [nInputs][maxNumValues][nClasses];
		
		for(int i=0;i<nInputs;i++){
			for(int j=0;j<maxNumValues;j++){
				System.arraycopy(AVC[i][j], 0, AVCCopy[i][j], 0, nClasses);
			}
		}
		
		//quitar instancias
		for(int i=0;i<instances.length;i++){
			if(instances[i][att]!=value){
				for(int j=0;j<nInputs;j++){
					AVC[j][instances[i][j]][outputs[i]]--;
				}
			}
		}
		
		//calcular error pequeño
		
		totalErrors=calcLooError(att,value);
		
		//devolver instancias
		for(int i=0;i<nInputs;i++){
			for(int j=0;j<maxNumValues;j++){
				System.arraycopy(AVCCopy[i][j], 0, AVC[i][j], 0, nClasses);
			}
		}
		
		return totalErrors;
	}
	
	private int calcLooError(int att,int value){
		
		int selected;
		int totalErrors=0;
		
		for(int i=0;i<instances.length;i++){
		
			if(instances[i][att]!=value){
				if(!correct[i]){
					totalErrors++;
				}
			}
			else{
			
				//quitarla del conjunto
				drop(instances[i], outputs[i]);
				
				selected=selectClass(instances[i]);
				if(selected!=outputs[i]){
					totalErrors++;
				}
				
				//ponerla en el conjunto
				add(instances[i], outputs[i]);
			}
		}
		
		return totalErrors;
	}
	
	public void prune(int att, int value){
		
		int [][] newInstances;
		int [] newOutputs;
		int count=0;
		int pointer;
		
		for(int i=0;i<nClasses;i++){
			count+=AVC[att][value][i];
		}
		
		newInstances=new int [count][nInputs];
		newOutputs=new int [count];
		
		pointer=0;
		
		for(int i=0;i<instances.length;i++){
		
			if(instances[i][att]==value){
				System.arraycopy(instances[i], 0, newInstances[pointer], 0, nInputs);
				newOutputs[pointer]=outputs[i];
				pointer++;
			}
		}
		
		instances=new int [count][nInputs];
		outputs=new int [count];
		
		for(int i=0;i<count;i++){
			System.arraycopy(newInstances[i], 0, instances[i], 0, nInputs);
		}
		System.arraycopy(newOutputs, 0, outputs, 0, count);
		

		calcProbabilities();
		correct=new boolean[outputs.length];
		
	}
	
	public int classify(int [] example){
		
		return selectClass(example);
		
	}
	
	private int selectClass(int [] example){
		
		int selected;
		double max;
		double [] points;
		
		points=new double[nClasses];
		
		//inicializar puntuaciones
		for(int j=0;j<nClasses;j++){
			points[j]=1.0;
		}
		
		//multiplicar atributos
		for(int j=0;j<example.length;j++){
			for(int k=0;k<nClasses;k++){
				points[k]*=(double)AVC[j][example[j]][k];
			}
		}
		
		//multiplicar clases
		for(int j=0;j<nClasses;j++){
			points[j]*=(double)classCount[j];
		}
		
		//encontrar maximo
		selected=-1;
		max=0;
		
		for(int j=0;j<nClasses;j++){
			if(max<=points[j]){
				max=points[j];
				selected=j;
			}
		}
		
		return selected;
		
	}
	
	public void print(){
		
		String cadena;
		
		for(int i=0;i<instances.length;i++){
			cadena="";
			for(int j=0;j<nInputs;j++){
				cadena+=instances[i][j]+" ";
			}
			System.out.println(cadena+" "+i );
		}
	}
	public void printAVC(){
		
		String cadena;
		
		for(int i=0;i<nInputs;i++){
			cadena="";
			for(int j=0;j<numValues[i];j++){
				cadena+=" ";
				for(int k=0;k<nClasses;k++){
					cadena+=AVC[i][j][k]+"*";
				}
			}
			System.out.println(cadena+" "+i );
		}
	}	
	
	//errores en old, not new
	public int getOldError(int bestAtt,int value){
		
		int errors=0;
		
		for(int i=0;i<correct.length;i++){
			if((correct[i]==false)&&(instances[i][bestAtt]!=value)){
				looError++;
			}
		}
		
		return errors;
	}
} //end-class 

