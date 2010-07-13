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

import java.util.Arrays;
import java.util.Vector;

import org.core.Randomize;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;

public class classifier_hyperrect_list extends classifier {
	
	float[] predicates;
	int[] offsetPredicates;
	int numAtt;
	int[] whichAtt;
	int classValue;
	int ruleSize;

	public int getClase(){
        return classValue;
	}

	public boolean doMatch(Instance ins){
		int i,base;

		Attribute[] attrs = Attributes.getInputAttributes();

		for(i=0;i<numAtt;i++) {
			base=offsetPredicates[i];
			int att=whichAtt[i];
			
			if (attrs[att].getType() == Attribute.REAL || attrs[att].getType() == Attribute.INTEGER){
				float value = (float) ins.getInputRealValues(att);
				if(value<predicates[base] || value>predicates[base+1]) return false;
			}
			else {
				int value=ins.getInputNominalValuesInt(att);
				if(predicates[base+value]==0) return false;
			}
		}
		return true;
	}

	public int numSpecialStages(){return 2;}

	public double computeTheoryLength(){
		
		int i,j,base;
		theoryLength = 0.0;

		int ptr = 0;
		
		Attribute[] attrs = Attributes.getInputAttributes();
		

		for( i = 0 ; i < numAtt ; i++){
			int att = whichAtt[i];
			if (attrs[att].getType() == Attribute.REAL || attrs[att].getType() == Attribute.INTEGER){
				float size = (float) (attrs[att].getMaxAttribute()-attrs[att].getMinAttribute());
				if(size>0) {
					theoryLength += 1.0 - (predicates[ptr+1]-predicates[ptr])/size;
				}
			}

			else {
				double countFalses = 0;
				int numValues = Parameters.attributeSize[att];
				for(j=0;j<numValues;j++) {
					if(predicates[ptr+j]==0) countFalses++;
				}
				theoryLength+=(double)countFalses/(double)numValues;
			}
			ptr+=Parameters.attributeSize[att];
		}
		
		theoryLength/=(double)Parameters.NumAttributes;

		return theoryLength;
	}

	public classifier_hyperrect_list(){
		initializeChromosome();
	}

	public classifier_hyperrect_list(classifier_hyperrect_list orig){

		length = orig.length;
		fitness = orig.fitness;
		scaledFitness = orig.scaledFitness;
		modif = orig.modif;
		front = orig.front;
		exceptionsLength = orig.exceptionsLength;
		accuracy = orig.accuracy;
		accuracy2 = orig.accuracy2;
		coverage = orig.coverage;
		numAttributes = orig.numAttributes;
		numAttributesMC = orig.numAttributesMC;
		theoryLength = orig.theoryLength;
		
		numAtt = orig.numAtt;
		classValue = orig.classValue;
		ruleSize = orig.ruleSize;
		
		whichAtt = new int[orig.numAtt];
		offsetPredicates = new int[orig.numAtt];
		predicates = new float[orig.ruleSize];

		if(numAtt > 0){
			System.arraycopy(orig.whichAtt, 0, whichAtt, 0, orig.numAtt);
			System.arraycopy(orig.offsetPredicates, 0, offsetPredicates, 0, orig.numAtt);
			System.arraycopy(orig.predicates, 0, predicates, 0, orig.ruleSize);
		}
	}



	public void initializeChromosome(){

		Attribute[] attrs = Attributes.getInputAttributes();
		int i,j,base;

		Instance ins = null;
		if(Parameters.smartInitMethod) {
			if( Parameters.defaultClassOption != Parameters.DISABLED ) {
				ins = Parameters.is.getInstanceInit(Parameters.defaultClassInteger);
			} else {
				ins = Parameters.is.getInstanceInit(Parameters.numClasses);
			}
		}
		
		
		
		Vector<Integer> selectedAtts =  new Vector<Integer>();
		ruleSize = 0;
		
		int numExpAttpi = Parameters.expectedRuleSize;
		if(numExpAttpi > Parameters.NumAttributes) numExpAttpi = Parameters.NumAttributes;
		double probIrr = 1 - ((double)numExpAttpi/(double)Parameters.NumAttributes);
		
		for( i = 0 ; i < Parameters.NumAttributes ; i++ ){
			if( Randomize.Rand() >= probIrr) {
				selectedAtts.addElement(i);
				ruleSize += Parameters.attributeSize[i];
			}
		}
		
		numAtt = selectedAtts.size();
		whichAtt = new int[numAtt];
		offsetPredicates = new int[numAtt];
		predicates = new float[ruleSize];
		

		for( i = 0 , base = 0 ; i < numAtt ; i++){
			offsetPredicates[i]=base;
			int att=selectedAtts.get(i);
			whichAtt[i]=att;

			if (attrs[att].getType() == Attribute.REAL || attrs[att].getType() == Attribute.INTEGER){
				float max,min;
				float sizeD=(float) (attrs[att].getMaxAttribute()-attrs[att].getMinAttribute());
				float minD=(float) attrs[att].getMinAttribute();
				float maxD=(float) attrs[att].getMaxAttribute();
				float size=(float) ((Randomize.Rand()*0.5+0.25)*sizeD);

				if(ins != null) {
					float val=(float) ins.getInputRealValues(att);
					min=(float) (val-size/2.0);
					max=(float) (val+size/2.0);
					if(min<minD) {
						max+=(minD-min);
						min=minD;
					}
					if(max>maxD) {
						min-=(max-maxD);
						max=maxD;
					}
				} else {
					min=(float) (Randomize.Rand()*(sizeD-size)+minD);
					max=min+size;
				}
		
				predicates[base]=min;
				predicates[base+1]=max;
			} else {
				int value;
				if(ins != null) value = ins.getInputNominalValuesInt(att);
				else value=-1;
				for(j=0;j<Parameters.attributeSize[att];j++) {
					if(j!=value) {
						if(Randomize.Rand()<Parameters.probOne) {
							predicates[base+j]=1;
						} else {
							predicates[base+j]=0;
						}
					} else {
						predicates[base+j]=1;
					}
				}
			}

			base+=Parameters.attributeSize[att];
		}

		if(ins != null) {
			classValue=ins.getOutputNominalValuesInt(0);
		} else {
			do {
				classValue=Randomize.Randint(0,Parameters.numClasses);
			} while(Parameters.defaultClassOption != Parameters.DISABLED && classValue == Parameters.defaultClassInteger);
		}
	}

	public void crossover(classifier in, classifier out1, classifier out2){
		crossover_1px(this, (classifier_hyperrect_list) in,(classifier_hyperrect_list) out1,(classifier_hyperrect_list) out2);
	}

	
	public float mutationOffset(float geneValue, float offsetMin,float offsetMax)
	{
		float newValue;
		if (Randomize.Rand() < 0.5) {
			newValue = (float) (geneValue + Randomize.Rand() * offsetMax);
		} else {
			newValue = (float) (geneValue - Randomize.Rand() * offsetMin);
		}
		return newValue;
	}

	public void mutation(){
		
		Attribute[] attrs = Attributes.getInputAttributes();

		int i;
		int attribute, value,attIndex;

		modif = 1;

		if(Parameters.numClasses>1 && Randomize.Rand()<0.10) {
			int newValue;
			do {
				newValue = Randomize.Randint(0, Parameters.numClasses);
			} while (newValue == classValue || Parameters.defaultClassOption != Parameters.DISABLED && newValue==Parameters.defaultClassInteger);
			classValue=newValue;
		} else {
			if(numAtt>0) {
				attIndex=Randomize.Randint(0,numAtt);
				attribute=whichAtt[attIndex];
				value=Randomize.Randint(0,Parameters.attributeSize[attribute]);
				int index=offsetPredicates[attIndex]+value;
			
				if (attrs[attribute].getType() == Attribute.REAL || attrs[attribute].getType() == Attribute.INTEGER){
					float newValue,minOffset,maxOffset;
					minOffset = maxOffset = (float) (0.5 * attrs[attribute].getMaxAttribute()-attrs[attribute].getMinAttribute());
					newValue = mutationOffset(predicates[index], minOffset, maxOffset);
					if (newValue < attrs[attribute].getMinAttribute()) newValue = (float) attrs[attribute].getMinAttribute();
					if (newValue > attrs[attribute].getMaxAttribute()) newValue = (float) attrs[attribute].getMaxAttribute();
					predicates[index]=newValue;
					if(value != 0) index--;
					if(predicates[index]>predicates[index+1]) {
						float tempAux = predicates[index];
						predicates[index] = predicates[index+1];
						predicates[index+1] = tempAux;	
					}
				} else {
					if(predicates[index]==1) predicates[index]=0;
					else predicates[index]=1;
				}
			}
		}
	}

	
	public String dumpPhenotype(){
		
		String temp, temp2, tmp1, tmp2;
		Attribute[] attrs = Attributes.getInputAttributes();
		int i,j,index;

		String string = "";
		
		for (i = 0,index=0; i < numAtt; i++) {
			int attIndex=whichAtt[i];
			temp = "Att " + Attributes.getInputAttribute(attIndex).getName() + " is ";
			int irr=1;
			
			if (attrs[attIndex].getType() == Attribute.REAL || attrs[attIndex].getType() == Attribute.INTEGER){

				float minD=(float) attrs[attIndex].getMinAttribute();
				float maxD=(float) attrs[attIndex].getMaxAttribute();
				if(predicates[index]==minD) {
					if(predicates[index+1]==maxD) {
						// do nothing
					} else {
						irr=0;
						temp2 = "[<"+predicates[index+1]+"]";
						temp += temp2;
					}
				} else {
					if(predicates[index+1]==maxD) {
						irr=0;
						temp2 = "[>"+predicates[index]+"]";
						temp += temp2;
					} else {
						irr=0;
						tmp1 = "" + predicates[index];
						tmp2 = ""+ predicates[index+1];
						temp2 = "["+tmp1+","+tmp2+"]";
						temp += temp2;
					}
				}
			} else {
				for(j=0;j<Parameters.attributeSize[attIndex];j++) {
					if(predicates[index+j] == 1) {
						temp2 = attrs[attIndex].getNominalValue(j) + ",";
						temp += temp2;
						
					} else {
						irr=0;
					}
				}
				
				temp = (String) temp.subSequence(0, temp.length()-1);
				
			}

			index+=Parameters.attributeSize[attIndex];

			if(irr==0) {
				string += temp + "|";
			}
		}
		
		temp = Attributes.getOutputAttribute(0).getNominalValue(classValue)+"\n";
		string += temp;
		
		return string;
	}

	public void crossover_1px(classifier_hyperrect_list in1, classifier_hyperrect_list in2,
					    classifier_hyperrect_list out1, classifier_hyperrect_list out2)	{
		
		Attribute[] attrs = Attributes.getInputAttributes();

		int i;

		out1.modif = out2.modif = 1;

		if(in1.numAtt==0) {
			classifier_hyperrect_list tmp=in2;
			in2=in1;
			in1=tmp;
		}
		


		if(in1.numAtt==0) {
			out1.whichAtt=new int[out1.numAtt];
			out2.whichAtt=new int[out2.numAtt];
			out1.offsetPredicates=new int[out1.numAtt];
			out2.offsetPredicates=new int[out2.numAtt];
			out1.predicates=new float[out1.ruleSize];
			out2.predicates=new float[out2.ruleSize];
			return;
		}

		int pos1=Randomize.Randint(0,in1.numAtt);
		int selAtt1=in1.whichAtt[pos1];

		for(i=0;i<in2.numAtt && in2.whichAtt[i]<selAtt1;i++);
		int pos2=i;
		int selAtt2;
		if(pos2!=in2.numAtt) {
			selAtt2=in2.whichAtt[pos2];
		} else {
			selAtt2=-1;
		}

		out1.numAtt=pos1+1+(in2.numAtt-pos2);
		out2.numAtt=pos2+(in1.numAtt-pos1-1);
		if(selAtt1==selAtt2) {
			out1.numAtt--;
			out2.numAtt++;
		}

		out1.whichAtt=new int[out1.numAtt];
		out2.whichAtt=new int[out2.numAtt];
		out1.offsetPredicates = new int[out1.numAtt];
		out2.offsetPredicates = new int[out2.numAtt];

		out1.ruleSize=0;
		for(i=0;i<=pos1;i++) {
			out1.whichAtt[i]=in1.whichAtt[i];
			out1.offsetPredicates[i]=out1.ruleSize;
			out1.ruleSize+=Parameters.attributeSize[out1.whichAtt[i]];
		}
		int lenp1c1=out1.ruleSize;
		int base=pos2;
		if(selAtt1==selAtt2) {
			base++;
		}
		for(;i<out1.numAtt;i++,base++) {
			out1.whichAtt[i]=in2.whichAtt[base];
			out1.offsetPredicates[i]=out1.ruleSize;
			out1.ruleSize+=Parameters.attributeSize[out1.whichAtt[i]];
		}

		out2.ruleSize=0;
		for(i=0;i<pos2;i++) {
			out2.whichAtt[i]=in2.whichAtt[i];
			out2.offsetPredicates[i]=out2.ruleSize;
			out2.ruleSize+=Parameters.attributeSize[out2.whichAtt[i]];
		}
		int lenp2c1=out2.ruleSize;
		base=pos1;
		if(selAtt1!=selAtt2) {
			base++;
		}
		for(;i<out2.numAtt;i++,base++) {
			out2.whichAtt[i]=in1.whichAtt[base];
			out2.offsetPredicates[i]=out2.ruleSize;
			out2.ruleSize+=Parameters.attributeSize[out2.whichAtt[i]];
		}

		out1.predicates=new float[out1.ruleSize];
		out2.predicates=new float[out2.ruleSize];

		System.arraycopy(in1.predicates, 0, out1.predicates, 0, lenp1c1);
		System.arraycopy(in2.predicates, 0, out2.predicates, 0, lenp2c1);
		
		
		if(selAtt1==selAtt2) {
			int baseP1=in1.offsetPredicates[pos1];
			int baseP2=in2.offsetPredicates[pos2];
			int baseO1=out1.offsetPredicates[pos1];
			int baseO2=out2.offsetPredicates[pos2];

			if (attrs[selAtt1].getType() == Attribute.REAL || attrs[selAtt1].getType() == Attribute.INTEGER){
				int cutPoint=Randomize.Randint(0,3);
				if(cutPoint==0) {
					out1.predicates[baseO1]=in2.predicates[baseP2];
					out1.predicates[baseO1+1]=in2.predicates[baseP2+1];
					out2.predicates[baseO2]=in1.predicates[baseP1];
					out2.predicates[baseO2+1]=in1.predicates[baseP1+1];
				} else if(cutPoint==1) {
					float min1=in1.predicates[baseP1];
					float min2=in2.predicates[baseP2];
					float max1=in2.predicates[baseP2+1];
					float max2=in1.predicates[baseP1+1];
		
					if(min1>max1){
						float tempFloat = min1;
						min1 = max1;
						max1 = tempFloat;
					} 
					if(min2>max2){
						float tempFloat = min2;
						min2 = max2;
						max2 = tempFloat;
					}
					
					out1.predicates[baseO1]=min1;
					out1.predicates[baseO1+1]=max1;
					out2.predicates[baseO2]=min2;
					out2.predicates[baseO2+1]=max2;
				} else {
					out1.predicates[baseO1]=in1.predicates[baseP1];
					out1.predicates[baseO1+1]=in1.predicates[baseP1+1];
					out2.predicates[baseO2]=in2.predicates[baseP2];
					out2.predicates[baseO2+1]=in2.predicates[baseP2+1];
				}
			} else {
				int size=Parameters.attributeSize[selAtt1];
				int cutPoint=Randomize.Randint(0,size+1);
				
				System.arraycopy(in1.predicates, baseP1, out1.predicates, baseO1, cutPoint);
				System.arraycopy(in2.predicates, baseP2, out2.predicates, baseO2, cutPoint);
				System.arraycopy(in1.predicates, baseP1+cutPoint, out2.predicates, baseO2+cutPoint, size-cutPoint);
				System.arraycopy(in2.predicates, baseP2+cutPoint, out1.predicates, baseO1+cutPoint, size-cutPoint);
			}
			pos2++;
		} else {
			int base1=in1.offsetPredicates[pos1];
			out1.whichAtt[pos1]=selAtt1;
			System.arraycopy(in1.predicates, base1, out1.predicates, base1, Parameters.attributeSize[selAtt1]);	
		}

		pos1++;
		if(pos1<in1.numAtt) {
			System.arraycopy(in1.predicates, in1.offsetPredicates[pos1], out2.predicates, out2.offsetPredicates[pos2], in1.ruleSize-in1.offsetPredicates[pos1]);
		}
		if(pos2<in2.numAtt) {
			System.arraycopy(in2.predicates, in2.offsetPredicates[pos2], out1.predicates, out1.offsetPredicates[pos1], in2.ruleSize-in2.offsetPredicates[pos2]);
		}


		if(Randomize.Rand()<0.5) {
			out1.classValue=in1.classValue;
			out2.classValue=in2.classValue;
		} else {
			out1.classValue=in2.classValue;
			out2.classValue=in1.classValue;
		}
	}

	public void postprocess(){
	}

	public void doSpecialStage(int stage){
		int i;
		Attribute[] attrs = Attributes.getInputAttributes();


		if(stage==0) { //Generalize
			if(numAtt>0 && Randomize.Rand()<Parameters.generalizeProbability) {
				int attribute=Randomize.Randint(0,numAtt);
				int deletedSize=Parameters.attributeSize[whichAtt[attribute]];

				int[] newWhichAtt = new int[numAtt-1];
				int[] newOffsetPredicates = new int[numAtt-1];
				float[] newPredicates = new float[ruleSize-deletedSize];
				
				System.arraycopy(whichAtt, 0, newWhichAtt, 0, attribute);
				System.arraycopy(offsetPredicates, 0, newOffsetPredicates, 0, attribute);
				System.arraycopy(predicates, 0, newPredicates, 0, offsetPredicates[attribute]);
				

				if(attribute!=numAtt-1) {
					System.arraycopy(whichAtt, attribute+1, newWhichAtt, attribute, numAtt-attribute-1);
					System.arraycopy(offsetPredicates, attribute+1, newOffsetPredicates, attribute, numAtt-attribute-1);
					System.arraycopy(predicates, offsetPredicates[attribute+1], newPredicates, offsetPredicates[attribute], ruleSize-offsetPredicates[attribute+1]);
				}
				
				whichAtt = new int[newWhichAtt.length];
				System.arraycopy(newWhichAtt, 0, whichAtt, 0, newWhichAtt.length);
				offsetPredicates = new int[newOffsetPredicates.length];
				System.arraycopy(newOffsetPredicates, 0, offsetPredicates, 0, newOffsetPredicates.length);
				predicates = new float[newPredicates.length];
				System.arraycopy(newPredicates, 0, predicates, 0, newPredicates.length);
				numAtt--;
				ruleSize-=deletedSize;			

				for(i=attribute;i<numAtt;i++) {
					offsetPredicates[i]-=deletedSize;
				}
			}
		} else { //Specialize
			if(numAtt < Parameters.NumAttributes && Randomize.Rand()<Parameters.specializeProbability) {
				int[] attMap = new int[Parameters.NumAttributes];
				Arrays.fill(attMap, 0);
				for(i=0;i<numAtt;i++) {
					attMap[whichAtt[i]]=1;
				}

				int selectedAtt;
				do {
					selectedAtt=Randomize.Randint(0,Parameters.NumAttributes);
				} while(attMap[selectedAtt]==1);

				int addedSize=Parameters.attributeSize[selectedAtt];
				
				int[] newWhichAtt = new int[numAtt+1];
				int[] newOffsetPredicates = new int[numAtt+1];
				float[] newPredicates = new float[ruleSize+addedSize];
				
				int index=0,index2=0;
				while(index<numAtt && whichAtt[index]<selectedAtt) {
					newWhichAtt[index]=whichAtt[index];
					newOffsetPredicates[index]=offsetPredicates[index];	
					System.arraycopy(predicates, index2, newPredicates, index2, Parameters.attributeSize[whichAtt[index]]);
					index2+=Parameters.attributeSize[whichAtt[index]];
					index++;
				}
				newWhichAtt[index]=selectedAtt;
				newOffsetPredicates[index]=index2;

				if (attrs[selectedAtt].getType() == Attribute.REAL || attrs[selectedAtt].getType() == Attribute.INTEGER){
					float sizeD=(float) (attrs[selectedAtt].getMaxAttribute()-attrs[selectedAtt].getMinAttribute());
					float minD=(float) attrs[selectedAtt].getMinAttribute();
					float maxD=(float) attrs[selectedAtt].getMaxAttribute();
					float size=(float) ((Randomize.Rand()*0.5+0.25)*sizeD);
					float min=(float) (Randomize.Rand()*(sizeD-size)+minD);
					float max=min+size;
					newPredicates[index2]=min;
					newPredicates[index2+1]=max;
				} else {
					for(i=0;i<Parameters.attributeSize[selectedAtt];i++) {
						if(Randomize.Rand()<Parameters.probOne) {
							newPredicates[index2+i]=1;
						} else {
							newPredicates[index2+i]=0;
						}
					}

				}

				if(index!=numAtt) {
					System.arraycopy(whichAtt, index, newWhichAtt, index+1, numAtt-index);
					System.arraycopy(offsetPredicates, index, newOffsetPredicates, index+1, numAtt-index);
					System.arraycopy(predicates, index2, newPredicates, index2+addedSize, ruleSize-offsetPredicates[index]);
				}
				
				whichAtt = new int[newWhichAtt.length];
				System.arraycopy(newWhichAtt, 0, whichAtt, 0, newWhichAtt.length);
				offsetPredicates = new int[newOffsetPredicates.length];
				System.arraycopy(newOffsetPredicates, 0, offsetPredicates, 0, newOffsetPredicates.length);
				predicates = new float[newPredicates.length];
				System.arraycopy(newPredicates, 0, predicates, 0, newPredicates.length);
				numAtt++;
				ruleSize+=addedSize;

				for(i=index+1;i<numAtt;i++) {
					offsetPredicates[i]+=addedSize;
				}
			}
		}
	}



}

