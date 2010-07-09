//
//  ReferenciaMNV.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 25-2-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.MNV;

public class ReferenciaMNV implements Comparable {
	
  public int entero;
  public double real;
  public double dist;

  public ReferenciaMNV () {}

  public ReferenciaMNV (int a, double b, double c) {
    entero = a;
    real = b;
    dist = c;
  }

  public int compareTo (Object o1) {
    if (this.real > ((ReferenciaMNV)o1).real)
      return 1;
    else if (this.real < ((ReferenciaMNV)o1).real)
      return -1;
    else if (this.dist > ((ReferenciaMNV)o1).dist)
    	return 1;
    else if (this.dist < ((ReferenciaMNV)o1).dist)
    	return -1;
    else return 0;
  }

  public String toString () {
    return new String ("{"+entero+", "+real+", "+dist+"}");
  }
}
