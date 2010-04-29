package keel.Algorithms.Statistical_Tests.Shared.nonParametric;

/**
 * <p>Título: </p>
 * <p>Descripción: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Empresa: </p>
 * @author sin atribuir
 * @version 1.0
 */

public class Relation {

  public int i;
  public int j;

  public Relation() {

  }

  public Relation(int x, int y) {
    i = x;
    j = y;
  }
  
  public String toString() {
	  return "("+i+","+j+")";	  
  }

}