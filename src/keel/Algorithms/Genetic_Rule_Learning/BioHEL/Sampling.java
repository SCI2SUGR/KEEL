package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

import org.core.Randomize;

public class Sampling {
	
	int maxSize;
	int num;
	int[] sample;

	void initSampling() {
		int i;
		for(i=0;i<maxSize;i++) sample[i]=i;
		num=maxSize;
	}


	Sampling(int pMaxSize) {
		maxSize=pMaxSize;
		sample = new int[maxSize];
		initSampling();
	}



	int getSample() {
		int pos=Randomize.Randint(0,num);
		int value=sample[pos];
		sample[pos]=sample[num-1];
		num--;

		if(num==0) initSampling();

		return value;
	}

	int numSamplesLeft() {return num;}
}

