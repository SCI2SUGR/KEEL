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

package keel.Algorithms.Genetic_Rule_Learning.MPLCS;

/**
 * <p>
 * @author Written by Jose A. Saez Munoz (ETSIIT, Universidad de Granada - Granada) 10/09/10
 * @version 1.0
 * @since JDK1.2
 * </p>
 */


public class matchProfileAgent {
	
	public int numInstances;
	public int numMatched;
	public boolean[] mapOK;
	public boolean[] mapKO;
	public int[] listOK;
	public int[] listKO;
	public int numOK;
	public int numKO;
	public int ruleClass;
	
	
	public matchProfileAgent(int pNumInstances,int pRuleClass){
		
		numInstances=pNumInstances;
		ruleClass=pRuleClass;

		listOK=new int[numInstances];
		listKO=new int[numInstances];
		numOK=numKO=0;
		
	}

	
	public void addOK(int instance) {listOK[numOK++]=instance;}
	
	public void addKO(int instance) {listKO[numKO++]=instance;}
	
	public void generateProfiles(){
		
		int i;

		mapOK=new boolean[numInstances];
		mapKO=new boolean[numInstances];
		
		for(i=0;i<numInstances;++i)
			mapOK[i]=mapKO[i]=false;
		
		for(i=0;i<numOK;i++) {
			mapOK[listOK[i]]=true;
		}

		for(i=0;i<numKO;i++) {
			mapKO[listKO[i]]=true;
		}

		numMatched=numOK+numKO;
	}
	
	
	
	public void removeMatched(int[]instances,int numInst)
	{
		int i;
		int[] instOK=new int[numInst];
		int [] instKO=new int[numInst];
		int removedOK=0;
		int removedKO=0;

		for(i=0;i<numInst;i++) {
			int inst=instances[i];
			if(mapOK[inst]) {
				mapOK[inst]=false;
				instOK[removedOK++]=inst;
			} else {
				mapKO[inst]=false;
				instKO[removedKO++]=inst;
			}
		}

		if(removedOK!=0) {
			int index=0;
			int numRemoved=1;
			while(listOK[index]<instOK[0]) index++;
			while(numRemoved<removedOK) {
				while(listOK[index+numRemoved]<instOK[numRemoved]) {
					listOK[index]=listOK[index+numRemoved];
					index++;
				}
				numRemoved++;
			}
			while(index+numRemoved<numOK) {
				listOK[index]=listOK[index+numRemoved];
				index++;
			}
			numOK-=removedOK;
		}

		if(removedKO!=0) {
			int index=0;
			int numRemoved=1;
			while(listKO[index]<instKO[0]) index++;
			while(numRemoved<removedKO) {
				while(listKO[index+numRemoved]<instKO[numRemoved]) {
					listKO[index]=listKO[index+numRemoved];
					index++;
				}
				
				numRemoved++;
			}
			while(index+numRemoved<numKO) {
				listKO[index]=listKO[index+numRemoved];
				index++;
			}
			numKO-=removedKO;
		}

		numMatched=numOK+numKO;
	}

	
}


