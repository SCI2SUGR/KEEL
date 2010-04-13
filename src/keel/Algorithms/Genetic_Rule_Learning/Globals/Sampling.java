package keel.Algorithms.Genetic_Rule_Learning.Globals;

/*
 * Sampling.java
 *
 */

/**
 * This class helps managing a sampling without replacement process 
 */
public class Sampling {
	int maxSize;
	int num;
	int []sample;

	void initSampling() {
		for(int i=0;i<maxSize;i++) sample[i]=i;
		num=maxSize;
	}

	public Sampling(int _maxSize) {
		maxSize=_maxSize;
		sample=new int[maxSize];
		initSampling();
	}

	public int getSample() {
		int pos=Rand.getInteger(0,num-1);
		int value=sample[pos];
		sample[pos]=sample[num-1];
		num--;

		if(num==0) initSampling();

		return value;
	}
}
