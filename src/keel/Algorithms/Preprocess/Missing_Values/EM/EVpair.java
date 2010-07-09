package keel.Algorithms.Preprocess.Missing_Values.EM;

/**
 * This class implements a pair of eigenvalues and their index, in order to be
 * possible to sort them, when inserted in a Collections structure
 * @author Julián Luengo Martín
 *
 */
public class EVpair implements Comparable {

	/** the eigenvalue*/
	public double eigenValue;
	/** the index that this element has in the original structure*/
	public int evIndex;
	
	/**
	 * Copy constructor
	 * @param newvalue the original eigenvalue
	 * @param newindex the new index
	 */
	public EVpair(double newvalue,int newindex){
		eigenValue = newvalue;
		evIndex = newindex;
	}
	
	public int compareTo(Object o){
		EVpair p = (EVpair) o;
		if(this.eigenValue > p.eigenValue)
			return 1;
		if(this.eigenValue < p.eigenValue)
			return -1;
		return 0;
	}
}
