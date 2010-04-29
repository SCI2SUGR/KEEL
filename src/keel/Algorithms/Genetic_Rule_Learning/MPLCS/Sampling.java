/**
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 23/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.MPLCS;

import keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Globals.*;

public class Sampling {
/**
 * <p>
 * Helps managing a sampling without replacement process
 * </p>
 */
	
	
  int maxSize;
  int num;
  int[] sample;

  void initSampling() {
    for (int i = 0; i < maxSize; i++) {
      sample[i] = i;
    }
    num = maxSize;
  }

  public Sampling(int _maxSize) {
    maxSize = _maxSize;
    sample = new int[maxSize];
    initSampling();
  }

  public int numSamplesLeft() {
    return num;
  }

  public int getSample() {
    int pos = Rand.getInteger(0, num - 1);
    int value = sample[pos];
    sample[pos] = sample[num - 1];
    num--;

    if (num == 0) {
      initSampling();
    }

    return value;
  }
}
