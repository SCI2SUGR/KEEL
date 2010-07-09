package keel.Algorithms.Preprocess.Missing_Values.LLSImpute;

public class IndexValuePair implements Comparable{
	public double value;
	public int index;
	
	public IndexValuePair(double newvalue,int newindex){
		value = newvalue;
		index = newindex;
	}
	
	public int compareTo(Object o){
		IndexValuePair p = (IndexValuePair) o;
		if(this.value > p.value)
			return 1;
		if(this.value < p.value)
			return -1;
		return 0;
	}
}
