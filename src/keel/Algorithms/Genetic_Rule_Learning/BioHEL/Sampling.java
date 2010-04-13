package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

/**
 * <p>Title: Sampling</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */

import org.core.Randomize;

public class Sampling {

    int maxSize;
    int num;
    int sample[];
    int NumInstances;
    
    public Sampling(int pMaxSize){
        maxSize=pMaxSize;
        sample=new int[maxSize];
        num=maxSize;
        initSampling();
    }
    
    private void initSampling(){
        int i;
        
        for(i=0;i<maxSize;i++)
        	sample[i]=i;
        num=maxSize;
    }


    public int getSample(){
        
        int pos=Randomize.RandintClosed(0, num-1);
        int value=sample[pos];
        sample[pos]=sample[num-1];
        num--;

        if(num==0)
            initSampling();

        return value;
    }


    public int numSamplesLeft(){
        return num;
    }

    
    public Sampling(int ni, int numNotRemoved){
    	
    	NumInstances=ni;
        maxSize=numNotRemoved;
        sample=new int[numNotRemoved];
        num=maxSize;
        initSampling2();
    }
    
    private void initSampling2(){
        int i,pos=0;
        
        for(i=0;i<NumInstances;i++)
        	if(!BioHEL.train.Removed[i]){
            	sample[pos]=i;
            	pos++;
        	}
        num=maxSize;
    }
    
    public int getSample2(){
        int pos=Randomize.RandintClosed(0, num-1);
        int value=sample[pos];
        sample[pos]=sample[num-1];
        num--;

        if(num==0)
            initSampling2();

        return value;
    }
}