/*
 * RandomDiscretizer.java
 *
 */

/**
 *
 */

package keel.Algorithms.Discretizers.Random_Discretizer;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;

public class RandomDiscretizer extends Discretizer {
	protected Vector discretizeAttribute(int attribute,int []values,int begin,int end) {
		Vector cd=classDistribution(attribute,values,begin,end);
		if(cd.size()==1) return new Vector();

		Vector candidateCutPoints = getCandidateCutPoints(attribute,values,begin,end);
		if(candidateCutPoints.size()==0) return new Vector();
		
		int numCP=Rand.getInteger(1,candidateCutPoints.size());
		Vector cutPoints=new Vector();
		
		for(int i=0;i<numCP;i++) {
			int pos=Rand.getInteger(0,candidateCutPoints.size()-1);
			int val=((Integer)candidateCutPoints.elementAt(pos)).intValue();
			candidateCutPoints.removeElementAt(pos);
			double cutPoint=(realValues[attribute][values[val-1]]+realValues[attribute][values[val]])/2.0;
			boolean endLoop=false;
			int insertPos=-1;
			for(int j=0;j<cutPoints.size() && !endLoop;j++) {
				if(cutPoint<((Double)cutPoints.elementAt(j)).doubleValue()) {
					endLoop=true;
					insertPos=j;
				}
			}
			if(endLoop) {
				cutPoints.insertElementAt(new Double(cutPoint),insertPos);
			} else {
				cutPoints.addElement(new Double(cutPoint));
			}
		}
		return cutPoints;
	}

	Vector getCandidateCutPoints(int attribute,int []values,int begin,int end) {
		Vector cutPoints = new Vector();
		double valueAnt=realValues[attribute][values[begin]];

		for(int i=begin;i<=end;i++) {
			double val=realValues[attribute][values[i]];
			if(val!=valueAnt) cutPoints.addElement(new Integer(i));
			valueAnt=val;
		}
		return cutPoints;
	}


	Vector classDistribution(int attribute,int []values,int begin,int end) {
		int []classCount = new int[Parameters.numClasses];
		for(int i=0;i<Parameters.numClasses;i++) classCount[i]=0;

		for(int i=begin;i<=end;i++) classCount[classOfInstances[values[i]]]++;
		
		Vector res= new Vector();
		for(int i=0;i<Parameters.numClasses;i++) {
			if(classCount[i]>0) res.addElement(new Integer(classCount[i]));
		}

		return res;
	}
		

}
