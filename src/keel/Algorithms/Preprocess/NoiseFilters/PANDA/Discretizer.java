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

package keel.Algorithms.Preprocess.NoiseFilters.PANDA;


import keel.Dataset.*;
import java.util.*;

public abstract class Discretizer {
	protected double [][]cutPoints;
	protected double [][]realValues;
	protected boolean []realAttributes;
	protected int []classOfInstances;
	private int iClassIndex;
	
	public void buildCutPoints(InstanceSet is) {
		int i;
		boolean bHit;
		
		Instance []instances=is.getInstances();

		classOfInstances= new int[instances.length];
		for(i=0;i<instances.length;i++) 
			classOfInstances[i]=instances[i].getOutputNominalValuesInt(0);
		
		cutPoints=new double[Parameters.numAttributes][];
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
					int []points= new int[instances.length];
					int numPoints=0;
					for(int j=0;j<instances.length;j++) {
						if(!instances[j].getInputMissingValues(i)) {
							points[numPoints++]=j;
							realValues[i][j]=instances[j].getInputRealValues(i);
						}
					}
	
					sortValues(i,points,0,numPoints-1);
	
					Vector cp=discretizeAttribute(i,points,0,numPoints-1);
					if(cp.size()>0) {
						cutPoints[i]=new double[cp.size()];
						for(int j=0;j<cutPoints[i].length;j++) {
							cutPoints[i][j]=((Double)cp.elementAt(j)).doubleValue();
							//LogManager.println("Cut point "+j+" of attribute "+i+" : "+cutPoints[i][j]);
						}
					} else {
						cutPoints[i]=null;
					}
					//LogManager.println("Number of cut points of attribute "+i+" : "+cp.size());
				} else {
					realAttributes[i]=false;
				}
				i++;	
			} else {
				iClassIndex = a;
				bHit = true;
			}
		}
		
		if (bHit == false){
			iClassIndex = Parameters.numAttributes;
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

	protected abstract Vector discretizeAttribute(int attribute,int []values,int begin,int end) ;

	public int discretize(int attribute,double value) {
		if(cutPoints[attribute]==null) return 0;
		for(int i=0;i<cutPoints[attribute].length;i++)
			if(value<cutPoints[attribute][i]) return i;
		return cutPoints[attribute].length;
	}
}
