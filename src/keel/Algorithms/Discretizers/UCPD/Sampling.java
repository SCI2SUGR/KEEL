/**
 * <p>
 * @author Written by Jose A. Saez Munoz (SCI2S research group, DECSAI in ETSIIT, University of Granada), 21/12/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Discretizers.UCPD;

import org.core.Randomize;


/**
 * <p>
 * This class helps managing a sampling without replacement process 
 * </p>
 */
public class Sampling {
	
	int maxSize;	// total number of elements
	int num;		// actual number of elements
	int []sample;	// actual elements


//******************************************************************************************************

	/**
	 * <p>
	 * Class constructor
 	 * </p>
	 * @param _maxSize number of elements
	 */	
	public Sampling(int _maxSize){
		
		maxSize = _maxSize;
		sample = new int[maxSize];
		initSampling();
	}

//******************************************************************************************************

	/**
	 * <p>
	 * Initializes the sampling
 	 * </p>
	 */		
	void initSampling(){
		
		for(int i = 0; i < maxSize; i++)
			sample[i] = i;
		
		num = maxSize;
	}

//******************************************************************************************************

	/**
	 * <p>
	 * Returns one value of the sampling
 	 * </p>
	 * @return the sampled value
	 */	
	public int getSample(){
		
		int pos = Randomize.Randint(0, num);
		int value = sample[pos];
		sample[pos] = sample[num-1];
		num--;

		if(num == 0)
			initSampling();

		return value;
	}
	
}