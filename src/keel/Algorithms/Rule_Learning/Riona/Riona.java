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
 * @author Written by Rosa Venzala 19/09/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.2
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.Riona;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import keel.Dataset.*;
import java.util.*;
import java.lang.*;
import java.text.DecimalFormat;
import org.core.*;


public class Riona {
/**
 * <p>
 * Main procedures of Rionasd algorithm
 * </p>
 */
	// Train file
	private Dataset train ;
	// Test file
	private Dataset test;
	private String outFile;
    private String outTrainFile;
    private String outTestFile;
	private String []classes=null;
	private String []testClasses=null;
	private long seed;
	private double data[][]=null;
	private double dataWithoutNor[][]=null;
	private double testData[][]=null;
	private double testDataWithoutNor[][]=null;
	private int clasificacionFinalTr[]=null;
	private int clasificacionFinalTst[]=null;
	private int k;
	
	public Riona(){
	}
	
/**
 * <p>
 * Riona constructor
 * </p>
 */
public Riona(String trainFile,String testFile,
               String trainOutFile,
               String testOutFile, String fOut,long lSeed){
	
	outFile = fOut;outTrainFile = trainOutFile;
        outTestFile = testOutFile;
	
	train=new Dataset();test=new Dataset();
	this.seed=lSeed;
		
	try {
		train.readSet(trainFile, true);
		test.readSet(testFile,false);
		train.calculateMostCommon();//eval.calculaMasComunes();
		test.calculateMostCommon();
			
		classes=train.giveClasses();
		testClasses=test.giveClasses();
		
		dataWithoutNor=new double[train.getNData()][];
		for(int i=0;i<train.getNData();i++)dataWithoutNor[i]=new double[train.getInPuts()];
		data=train.getX();
		for(int i=0;i<train.getNData();i++){
			for(int j=0;j<train.getInPuts();j++)dataWithoutNor[i][j]=data[i][j];
		}
		train.setNumValues();
		train.normalize();//Convierte todos los valores del conjunto de datos en el intervalo [0,1] a la hora de facilitar los calculos de las distancias
		
		testDataWithoutNor=new double[test.getNData()][];
		for(int i=0;i<test.getNData();i++)testDataWithoutNor[i]=new double[test.getInPuts()];
		testData=test.getX();
		for(int i=0;i<test.getNData();i++){
			for(int j=0;j<test.getInPuts();j++)testDataWithoutNor[i][j]=testData[i][j];
		}
		test.setNumValues();
		test.normalize();//Convierte todos los valores del conjunto de datos en el intervalo [0,1] a la hora de facilitar los calculos de las distancias
		
	
	//AQUI EMPIEZO RIONA
	train.computeSVDM();
	test.computeSVDM();
	//Clasificamos como ejemplos de test los datos de train y de test
	int neighbourSet[];
	int newK,kMax;
	Complex rule;
	boolean consistent;
	int supportSet[]=new int[train.getNClasses()];
	clasificacionFinalTr=new int[train.getNData()];
	clasificacionFinalTst=new int[test.getNData()];
	if(train.getNData()<100)kMax=train.getNData()-1;
	else kMax=100;
	k=findOptimalK(kMax);
	System.out.println("tamanio optimo vecindario "+k);
	//k=4;
	for(int i=0;i<train.getNData();i++){
		neighbourSet=train.getNeighbourSet(data[i],k);
		newK=0;//inicializar
		for(int j=0;j<train.getNData();j++){
			if(neighbourSet[j]!=-2){newK++;
			}
		}
		//System.out.println("tam final vecindario "+k+" "+nuevo_k);
		for(int c=0;c<train.getNClasses();c++){
			supportSet[c]=0;
			for(int v=0;v<newK;v++){
			  if(train.getC(neighbourSet[v])==c){
			  rule=createRuleTestTrain(data[i],i,data[neighbourSet[v]],neighbourSet[v],c,train.getNClasses(),true);
			  rule=desnormalize(rule,true);// rule.print();
			  if(isConsistent(rule,neighbourSet,newK))
			  	supportSet[c]++;
			 }
			}
			//System.out.println("clase "+c+" elems "+supportSet[c]);
		}
		clasificacionFinalTr[i]=train.getMaximum(supportSet,seed);
		//System.out.println(clases[clasificacionFinalTr[i]]);
	}
	System.out.println("------------------------------------------------");
	//supportSet=new int[test.getnclases()];
	//las clases son las de train
	for(int i=0;i<test.getNData();i++){
		neighbourSet=train.getNeighbourSet(testData[i],k);//no es test
		newK=0;//inicializar
		for(int j=0;j<train.getNData();j++)//no es test
			if(neighbourSet[j]!=-2){newK++;}
		for(int c=0;c<train.getNClasses();c++){
			supportSet[c]=0;
			for(int v=0;v<newK;v++){
			  if(train.getC(neighbourSet[v])==c){
			  rule=createRuleTestTrain(testData[i],i,data[neighbourSet[v]],neighbourSet[v],c,train.getNClasses(),false);
			 // rule=desnormalizar(rule,false);
			  //rule.print();
			  if(isConsistent(rule,neighbourSet,newK))
			  	supportSet[c]++;
			 }
			}
		}
		clasificacionFinalTst[i]=train.getMaximum(supportSet,seed);
		//System.out.println(clasesTest[clasificacionFinalTst[i]]);
	}
	
	//GENERAMOS LA SALIDA
	generateOutPut();
		
	}
	catch (IOException e) {
		System.err.println("There was a problem while trying to read the dataset files:");
		System.err.println("-> " + e);
		}
}

	
	/**
	 * <p>
	 * Calculate the statistical values necessary and creates the out-put KEEL files
	 * </p>
     */
	private void generateOutPut() {
		Fichero f = new Fichero();
		String str = "";
		String strTrain="";
		String strtest="";
		String outPut = new String("");
        outPut = train.copyTestHeader();
		double trainAcc=0.,testAcc=0.;
		
		for(int i=0;i<train.getNData();i++){
		strTrain += new String(classes[train.getC(i)] +
					" " +
		classes[clasificacionFinalTr[i]] + "\n");
		if(train.getC(i)==clasificacionFinalTr[i])trainAcc++;
		}
		
		for(int i=0;i<test.getNData();i++){
		strtest += new String(testClasses[test.getC(i)] +
					" " +
		testClasses[clasificacionFinalTst[i]] + "\n");
		if(test.getC(i)==clasificacionFinalTst[i])testAcc++;
		}
		
		f.escribeFichero(outTrainFile,
                         outPut + strTrain);
        	f.escribeFichero(outTestFile,
                         outPut + strtest);
		
		double porcTrn=(trainAcc/train.getNData());
		double porcTst=(testAcc/test.getNData());
		str += "Accuracy Training: " + porcTrn + "\n";
        	str += "Accuracy Test: " + porcTst+ "\n";	
		str += "tamanio optimo vecindario "+k; 
		f.escribeFichero(outFile, str);
		System.out.println(str);
        
	}

   /**
	* <p>
	* Desnormalize the data 
	* </p>
	* @param c the rule
	* @param train true if is Train data, false otherwise
	*/
    private Complex desnormalize(Complex c,boolean train){
    	int []numI=new int[2];
    	double []limits=new double[2];
	    for (int j = 0; j < c.size();j++) {
	    Selector s = c.getSelector(j);
		int at=s.getAttribute();
		numI=s.getNumInstances();
		
		if(train){limits[0]=dataWithoutNor[numI[0]][at];
			limits[1]=dataWithoutNor[numI[1]][at];}
		else {limits[0]=testDataWithoutNor[numI[0]][at];
			limits[1]=testDataWithoutNor[numI[1]][at];}
		s.setValues(limits);
		
        }
	    return c;	
    }
    
    /**
     * <p>
     * Creates a local rule
     * </p>
     * @param tst the test example
     * @param numItst the position of the example in the set
     * @param trn the train example
     * @param numItrn the position of the example in the set
     * @param clase the consequent  of the rule
     * @param nClasses the total number of classes
     */
     public Complex createRuleTestTrain(double[]tst,int numItst,double[]trn,int numItrn,int clase,int nClasses,boolean es_train){
	    double numeric;
	    Selector s = null;
		Complex rule;
		rule=new Complex(nClasses);
		rule.setClassAttribute(clase);
		rule.adjuntNameAttributes(train.giveNames());
		for (int j = 0; j < train.getInPuts(); j++) {
			double []vnum=new double[2];
			if(tst[j]<trn[j]){vnum[0]=tst[j];vnum[1]=trn[j];}
			else {vnum[0]=trn[j];vnum[1]=tst[j];}
			if (Attributes.getInputAttribute(j).getType() == Attribute.NOMINAL){
				double []comp;
				int elems=0;
				if(es_train)comp=train.createBall(j,tst[j],trn[j]);
				else {
				double v=train.getRealValue(j,test.findNominalValue(j,tst[j]));
				comp=train.createBall(j,v,trn[j]);
				}
				if(comp!=null){
					for(int ii=0;ii<comp.length;ii++){
						if(comp[ii]!=-1)elems++;
						else break;
					}
					String []vnomi=new String[elems];
					for(int ii=0;ii<elems;ii++){
						//if(es_train)
						vnomi[ii]=train.findNominalValue(j,comp[ii]);
						//else vnomi[ii]=test.findNominalValue(j,comp[ii]);
					}
					s=new Selector(j,0,vnomi,comp,elems,/*i*/0);
				}
			}
			else {
				s = new Selector(j, 0,vnum,2,0);	}
			if(tst[j]<trn[j]){s.setNumUp(numItrn);s.setNumLow(numItst);}
			else {s.setNumUp(numItst);s.setNumLow(numItrn);}
			
			if(s!=null)
				rule.addSelector(s);
		}
	   return rule;
   }
     
   /**
    * <p>
    * Inidcates if a rule is consistent with a determined set of examples
    * </p>
    * @return true if is consistent
    * @return false otherwise
    */
   public boolean isConsistent(Complex R,int[]verifySet,int util){
   	for(int i=0;i<util;i++){
		//if(train){ //no  es necesario pq el vecindario siempre es de train
		if((R.getClassAttribute()!=train.getC(verifySet[i])/*claseTst*/) && (R.ruleCoversInstance(data[verifySet[i]])))return false;
		//}
		//else if((R.getClase()!=claseTst) && (R.reglaCubreInstancia(datosTst[verifySet[i]])))return false;
	}
	return true;
   }
   
   /**
    * <p>
    * Calculates the optimum size of the neighborhood for the training set
    * </p>
    * @param kmax max malue for neighborhood size
   */
   public int findOptimalK(int kmax){
    	int A[][]=new int[train.getNData()][];
    	int max[]=new int[kmax];
    	for(int k=0;k<kmax;k++)max[k]=0;
    	for(int i=0;i<train.getNData();i++){
    		A[i]=getClassificationVector(i,kmax);
		for(int k=0;k<kmax;k++){
		if(train.getC(i)==A[i][k])max[k]++;
		}
    	}
		int k=-1;
		for (int i = 0, c = -1; i < kmax; i++) {
	            if (max[i] > c) {
	                k = i;
	                c = max[i];
	            }
	        }
		return (k+1);
    }
   
   
    private int[]getClassificationVector(int trn,int kmax){
    	int []A=new int[kmax];
    	int []NN=train.getNN(train.getX(trn),kmax);
	//NN tiene tamanio kmax
	int decStrength[]=new int[train.getNClasses()];
	for(int v=0;v<train.getNClasses();v++)decStrength[v]=0;
	int currentDec=train.getMostFrequentClass();
	for(int k=0;k<kmax;k++){
		//System.out.println("soy el vecino "+NN[k]);
		Complex rule=createRuleTestTrain(data[trn],trn,data[NN[k]],NN[k],train.getC(NN[k]),train.getNClasses(),true);
		if(isConsistent(rule,NN,kmax)){
			int v=train.getC(NN[k]);
			//System.out.print("es consistente "+decStrength[v]+" "+decStrength[currentDec]+" ");
			decStrength[v]=decStrength[v]+1;
			if(decStrength[v]>decStrength[currentDec]){currentDec=v;
			}
			
		}
		A[k]=currentDec;
		//System.out.print(A[k]+" ;; ");
		
	}
	return A;
    }
   
    
}

    
    

