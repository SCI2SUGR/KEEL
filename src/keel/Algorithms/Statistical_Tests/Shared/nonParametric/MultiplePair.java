/**
 * File: MultiplePair.java
 * 
 * This class defines a comparable pair of two double values.
 * 
 * @author Written by Joaquín Derrac (University of Granada) 29/04/2010
 * @version 1.1 
 * @since JDK1.5
*/
package keel.Algorithms.Statistical_Tests.Shared.nonParametric;

public class MultiplePair implements Comparable {

  public double indice; //first element
  public double valor;  //second element
  
  /**
  * Default builder
  */
  public MultiplePair() {

  }//end-method

  /**
  * Builder
  *
  * @param i First double
  * @param v Second double
  */
  public MultiplePair(double i, double v) {
    indice = i;
    valor = v;
  }//end-method

  /**
  * CompareTo method
  *
  * @param Other pair
  * @return A integer representing the order
  */
  public int compareTo (Object o1) { //sort by absolute value

    if (Math.abs(this.valor) > Math.abs(((MultiplePair)o1).valor))
      return -1;
    else if (Math.abs(this.valor) < Math.abs(((MultiplePair)o1).valor))
      return 1;
    else return 0;
  }//end-method


}//end-class