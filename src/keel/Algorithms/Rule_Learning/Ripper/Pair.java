/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada)  01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.2
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.Ripper;



public class Pair  implements Comparable{
/**
 * <i>Auxiliar class.<\i>Representation of a pair of integers (key/value).
 */  
	
  public int key;
  public int value;

  public Pair() {
  }
  
  public int compareTo(Object o){
	  Pair p = (Pair)o;
	  
	  if(this.value < p.value)
		  return -1;
	  if(this.value > p.value)
		  return 1;
	  return 0;
  }

}