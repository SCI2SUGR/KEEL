package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

import java.util.Arrays;
import org.core.Randomize;
import keel.Dataset.Instance;

public class windowingGWS extends windowing{
	
	Instance[] set;
	Instance[][] instancesOfClass;
	Instance[] sample;
	int sampleSize;
	double[] classQuota;
	int[] classSizes;
	int howMuch;
	int numStrata;
	int numClasses;
	int stratum;
	int currentIteration;


	public boolean needReEval() {
		return true;
	}

	public int numVersions() {
		return numStrata;
	}

	public int getCurrentVersion() {
		return stratum;
	}

	
	public void setInstances(Instance[] pSet, int pHowMuch){
		int i;

		set=pSet;
		howMuch=pHowMuch;
		
		int[] numInstC = computeNumInstC();

		numClasses = Parameters.numClasses;
		numStrata = Parameters.numStrataWindowing;
		instancesOfClass= new Instance [numClasses][];
		classSizes = new int[numClasses];
		classQuota = new double[numClasses];

		int capacity=0;
		for(i=0;i<numClasses;i++)  {
			int num = numInstC[i];
			instancesOfClass[i] = new Instance[num];
			classSizes[i]=0;
			classQuota[i]=(double)num/(double)numStrata;
			capacity+=(int)Math.ceil(classQuota[i]);
		}
		
		sample = new Instance[capacity];

		for(i=0;i<howMuch;i++) {
			int cls=set[i].getOutputNominalValuesInt(0);
			System.out.println("caca de vaca = " + classSizes[cls]);
			instancesOfClass[cls][classSizes[cls]++]=set[i];
		}

		currentIteration=0;
	}

	
	public Object[] newIteration(){
		int i,j;

		stratum=currentIteration%numStrata;
		currentIteration++;

		sampleSize=0;
		for(i=0;i<numClasses;i++) {
			int fixQ=(int)classQuota[i];
			for(j=0;j<fixQ;j++) {
				int pos=Randomize.Randint(0,classSizes[i]);
				sample[sampleSize++]=instancesOfClass[i][pos];
			}
			double prob=classQuota[i]-fixQ;
			if(Randomize.Rand()<prob) {
				int pos=Randomize.Randint(0,classSizes[i]);
				sample[sampleSize++]=instancesOfClass[i][pos];
			}
		}

		Object[] res = new Object[2];
		res[0] = (Integer) sampleSize;
		res[1] = (Instance[]) sample;
		
		return res;	
	}
	
	public int[] computeNumInstC(){
				
		int[] countClass = new int[Parameters.numClasses];
		Arrays.fill(countClass, 0);
		
		for(int i = 0 ; i < howMuch ; ++i)
			countClass[set[i].getOutputNominalValuesInt(0)]++;
		
		return countClass;
	}

}
