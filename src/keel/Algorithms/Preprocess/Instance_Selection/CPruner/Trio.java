//
//  Trio.java
//
//  Salvador García López
//
//  Created by Salvador García López 19-8-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.CPruner;

public class Trio implements Comparable {
  public int id;
  public double distancia;
  public int nVec;

  public Trio () {}
  public Trio (int a, int c, double b) {
	  
    id = a;
    distancia = b;
    nVec = c;
  }
  public int compareTo (Object o1) {
    if (this.nVec > ((Trio)o1).nVec)
      return -1;
    else if (this.nVec < ((Trio)o1).nVec)
      return 1;
    else {
      if (this.distancia > ((Trio)o1).distancia)
        return -1;
      else if (this.distancia < ((Trio)o1).distancia)
        return 1;
      else return 0;
    }
  }
  public String toString () {
    return new String ("{"+id+", "+nVec+", "+distancia+"}");
  }
}