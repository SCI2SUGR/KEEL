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

/*
 * ExtendedChi2Discretizer.java
 *
 */

/**
 *
 */

package keel.Algorithms.Discretizers.ExtendedChi2_Discretizer;

import keel.Dataset.*;

import java.util.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;

public abstract class Discretizer {
	protected double [][]cutPoints;
	protected double [][]cutPointsTMP;
	protected double [][]realValues;
	protected boolean []realAttributes;
	protected int []classOfInstances;
	private int iClassIndex;
	protected double inconsistencyThreshold;
	
	public void buildCutPoints(InstanceSet is) {
		int i, j;
		boolean bHit;
		double levelSig;
		double levelSig0 = 0.5;
		boolean notMergeable[];
		double sigLvl[];
		boolean allNotMergeables;
		boolean allOverflow;
		double LC_original;
		
		Vector <Vector <Interval>> intervals = new Vector <Vector<Interval>> ();
		
		Instance []instances=is.getInstances();

		int numPoints[] = new int[Parameters.numAttributes];
		int points[][] = new int[Parameters.numAttributes][instances.length];

		classOfInstances= new int[instances.length];
		for(i=0;i<instances.length;i++) 
			classOfInstances[i]=instances[i].getOutputNominalValuesInt(0);
		
		cutPoints=new double[Parameters.numAttributes][];
		cutPointsTMP=new double[Parameters.numAttributes][];
		realAttributes = new boolean[Parameters.numAttributes];
		realValues = new double[Parameters.numAttributes][];

		i = 0;
		bHit = false;
		for (int a = 0; i < Parameters.numAttributes; a++){
			Attribute at=Attributes.getAttribute(a);
			if (at.getDirectionAttribute() == Attribute.INPUT){
				if(at.getType()==Attribute.REAL || at.getType()==Attribute.INTEGER) {
					realAttributes[i]=true;
	
					realValues[i] = new double[instances.length];
					numPoints[i] = 0;
					for(j=0;j<instances.length;j++) {
						if(!instances[j].getInputMissingValues(i)) {
							points[i][numPoints[i]++] = j;
							realValues[i][j]=instances[j].getInputRealValues(i);
						}
					}
	
					sortValues(i,points[i],0,numPoints[i]-1);

					intervals.add(obtainIntervals(i,points[i],0, numPoints[i]-1));
					Vector cp=discretizeAttributePreliminary(i,points[i],intervals.elementAt(i));
					if(cp.size()>0) {
						cutPointsTMP[i]=new double[cp.size()];
						for(j=0;j<cutPointsTMP[i].length;j++) {
							cutPointsTMP[i][j]=((Double)cp.elementAt(j)).doubleValue();
						}
					} else {
						cutPointsTMP[i]=null;
					}
				} else {
					realAttributes[i]=false;
					intervals.add(null);
				}
				i++;	
			} else {
				iClassIndex = a;
				bHit = true;
				intervals.add(null);
			}
		}
		
		if (bHit == false){
			iClassIndex = Parameters.numAttributes;
		}
		
		/* PHASE 1 */
		levelSig = 0.5;
		LC_original = inconsistencyCheck(instances);
		
		while (inconsistencyCheck(instances) <= LC_original && levelSig >= 1E-13) {
			for (i=0; i<realAttributes.length; i++) {
				if (realAttributes[i]) {
					Vector cp=discretizeAttribute(i,points[i],intervals.elementAt(i), levelSig);
					if(cp.size()>0) {
						cutPointsTMP[i]=new double[cp.size()];
						for(j=0;j<cutPointsTMP[i].length;j++) {
							cutPointsTMP[i][j]=((Double)cp.elementAt(j)).doubleValue();
						}
					} else {
						cutPointsTMP[i]=null;
					}
				}
			}
			levelSig0 = levelSig;
			levelSig *= 0.9;
		}
		
		/* Preliminaries of Phase 2*/
		intervals = new Vector <Vector<Interval>> ();
		for (i=0; i<realAttributes.length; i++) {
			if (realAttributes[i]) {
					intervals.add(obtainIntervals(i,points[i],0, numPoints[i]-1));
			} else {
				intervals.add(new Vector<Interval>());
			}
		}
		
		/* PHASE 2 */
		notMergeable = new boolean[Parameters.numAttributes];
		Arrays.fill(notMergeable, false);
		sigLvl = new double[Parameters.numAttributes];
		for (i=0; i<sigLvl.length; i++) {
			sigLvl[i] = levelSig0;
		}

		do {
			for (i=0; i<realAttributes.length; i++) {
				if (realAttributes[i]) {
					Vector cp=discretizeAttribute(i,points[i],intervals.elementAt(i), levelSig);
					if(cp.size()>0) {
						cutPointsTMP[i]=new double[cp.size()];
						for(j=0;j<cutPointsTMP[i].length;j++) {
							cutPointsTMP[i][j]=((Double)cp.elementAt(j)).doubleValue();
						}
					} else {
						cutPointsTMP[i]=null;
					}
					if (inconsistencyCheck(instances) <= LC_original) {
						sigLvl[i] *= 0.9;
					} else {
						notMergeable[i] = true;
					}
				}
				else {
					notMergeable[i] = true;
				}
			}
			allNotMergeables = true;
			for (i=0; i<realAttributes.length; i++) {
				allNotMergeables &= notMergeable[i];
			}			

			allOverflow = true;
			for (i=0; i<realAttributes.length; i++) {
				if (realAttributes[i]) {
					if (sigLvl[i] >= 1E-12) {
						allOverflow = false;
					}
				}
			}			
		} while (!allNotMergeables && !allOverflow);

		for (i=0; i<cutPointsTMP.length; i++) {
			if (cutPointsTMP[i] != null) {
				cutPoints[i] = new double[cutPointsTMP[i].length];
				for (j=0; j<cutPointsTMP[i].length; j++) {
					cutPoints[i][j] = cutPointsTMP[i][j];
					LogManager.println("Cut point "+j+" of attribute "+i+" : "+cutPoints[i][j]);
				}
				LogManager.println("Number of cut points of attribute "+i+" : "+cutPoints[i].length);
			} else {
				cutPoints[i] = null;
				LogManager.println("Number of cut points of attribute "+i+" : 0");
			}
		}
	}
	
	public double inconsistencyCheck (Instance instances[]) {
		
		int data[][];
		int i, j, k;
		Vector <Integer> matches;
		Vector <Integer> seen = new Vector <Integer> ();
		boolean equal;
		int countClass[] = new int[Parameters.numClasses];
		double m1 = 1, m2 = 0, cED;
		
		data = new int[instances.length][Attributes.getInputAttributes().length];

		for(i=0;i<instances.length;i++) {
			boolean []missing=instances[i].getInputMissingValues();
			for(j=0, k=0;j<Parameters.numAttributes;j++) {
				if (j != iClassIndex){
					if(missing[j]) {
						data[i][k]=-1;
					} else {
						if(realAttributes[j]) {
							double val=instances[i].getInputRealValues(j);
							int interv=discretizeTMP(j,val);
							data[i][k] = interv;
						} else {
							data[i][k] = instances[i].getInputNominalValuesInt(j);
						}
					}
					k++;
				}					
			}
		}
		
		for (i=0; i<data.length; i++) {
			seen.add(i);
			matches = new Vector <Integer>();
			for (j=i+1; j<data.length; j++) {
				equal = true;
				for (k=0; k<data[i].length && equal; k++) {
					if (data[i][k] != data[j][k]) {
						equal = false;
					}
				}
				if (equal && !seen.contains(j)) {
					matches.add(j);
					seen.add(j);
				}
			}
			if (matches.size() > 0) {
				Arrays.fill(countClass, 0);
				countClass[classOfInstances[i]]++;
				for (j=0; j<matches.size(); j++) {
					countClass[classOfInstances[matches.elementAt(j)]]++;					
				}
				
				for (j=0; j<countClass.length; j++) {
					cED = 1.0 - ((double) countClass[j] / (double)(matches.size()+1));
					if (cED > 0.5) {
						if (cED < m1) {
							m1 = cED;
						}
					} else if (cED < 0.5) {
						if (cED > m2) {
							m2 = cED;
						}
					}
				}				
			}
		}
		
		m1 = 1 - m1;
		
		return Math.max(m1, m2);
	}

	public void applyDiscretization(String in,String out) {
		boolean bHit;
		
		InstanceSet is=new InstanceSet();
		try {
			is.readSet(in,false);
                } catch(Exception e) {
                        LogManager.printErr(e.toString());
                        System.exit(1);
                }

		FileManagement fm = new FileManagement();
		Instance []instances=is.getInstances();
		Attribute []att=Attributes.getInputAttributes();

		try {
			fm.initWrite(out);
			fm.writeLine("@relation "+Attributes.getRelationName()+"\n");
			bHit = false;
			for(int i=0;i<Parameters.numAttributes;i++) {
				if (i == iClassIndex){
					fm.writeLine(Attributes.getOutputAttributes()[0].toString()+"\n");
					bHit = true;
				}
				if(realAttributes[i]) {
					String def="@attribute "+att[i].getName()+" {";
					if(cutPoints[i]!=null) {
						for(int j=0;j<cutPoints[i].length+1;j++) {
							def+=j;
							if(j<cutPoints[i].length) def+=",";
						}
					} else {
						def+=0;
					}
					def+="}\n";
					fm.writeLine(def);
				} else {
					fm.writeLine(att[i].toString()+"\n");
				}
			}
			if (bHit == false){
				fm.writeLine(Attributes.getOutputAttributes()[0].toString()+"\n");
			}
			
			fm.writeLine("@inputs ");
			for (int i = 0; i < Parameters.numAttributes-1;i++){
				fm.writeLine(att[i].getName()+",");
			}
			fm.writeLine(att[Parameters.numAttributes-1].getName()+"\n");
			
			fm.writeLine("@outputs "+ Attributes.getOutputAttributes()[0].getName()+"\n");
			
			fm.writeLine("@data\n");

			bHit = false;
			for(int i=0;i<instances.length;i++) {
				boolean []missing=instances[i].getInputMissingValues();
				String newInstance="";
				for(int j=0;j<Parameters.numAttributes;j++) {
					if (j == iClassIndex){
						String className=instances[i].getOutputNominalValues(0);
						newInstance+=className+",";
						bHit = true;
					}
					
					if(missing[j]) {
						newInstance+="?";
					} else {
						if(realAttributes[j]) {
							double val=instances[i].getInputRealValues(j);
							int interv=discretize(j,val);
							newInstance+=interv;
						} else {
							newInstance+=instances[i].getInputNominalValues(j);
						}
					}
					
					if (bHit == true && j == (Parameters.numAttributes -1)){
						newInstance += "\n";
					} else {
						newInstance +=",";
					} 
				}
				if (bHit == false){
					String className=instances[i].getOutputNominalValues(0);
					newInstance+=className+"\n";					
				}

				fm.writeLine(newInstance);
			}
			fm.closeWrite();
		} catch(Exception e) {
			LogManager.printErr("Exception in doDiscretize");
			e.printStackTrace();
			System.exit(1);
		}
	}

	protected void sortValues(int attribute,int []values,int begin,int end) {
		double pivot;
		int temp;
		int i,j;

		i=begin;j=end;
		pivot=realValues[attribute][values[(i+j)/2]];
		do {
			while(realValues[attribute][values[i]]<pivot) i++;
			while(realValues[attribute][values[j]]>pivot) j--;
			if(i<=j) {
				if(i<j) {
					temp=values[i];
					values[i]=values[j];
					values[j]=temp;
				}
				i++; j--;
			}
		} while(i<=j);
		if(begin<j) sortValues(attribute,values,begin,j);
		if(i<end) sortValues(attribute,values,i,end);
	}

	public int getNumIntervals(int attribute) {
		return cutPoints[attribute].length+1;
	}

	public double getCutPoint(int attribute,int cp) {
		return cutPoints[attribute][cp];
	}

	protected abstract Vector discretizeAttribute(int attribute,int []values, Vector <Interval> intervals, double levelSig) ;

	protected abstract Vector discretizeAttributePreliminary(int attribute,int []values, Vector <Interval> intervals) ;

	protected abstract Vector <Interval> obtainIntervals(int attribute,int []values,int begin,int end) ;

	public int discretize(int attribute,double value) {
		if(cutPoints[attribute]==null) return 0;
		for(int i=0;i<cutPoints[attribute].length;i++)
			if(value<cutPoints[attribute][i]) return i;
		return cutPoints[attribute].length;
	}

	public int discretizeTMP(int attribute,double value) {
		if(cutPointsTMP[attribute]==null) return 0;
		for(int i=0;i<cutPointsTMP[attribute].length;i++)
			if(value<cutPointsTMP[attribute][i]) return i;
		return cutPointsTMP[attribute].length;
	}
}

