/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Boosting;

import org.core.*;


public class GenotypeBoosting {
  // Representation of an individual
  // The constructor with int parameter produces random gens
  public int x[]; 
  public int nlabels;
  public static Randomize r;

 
  public pair_gg cruce_raro(GenotypeBoosting b) {
      // crossover operator
      
      GenotypeBoosting tmp1=duplica();
      GenotypeBoosting tmp2=b.duplica();
      final double alfa=0.5f;
      for (int i=0;i<x.length;i++) {
          tmp1.x[i]=(int)(x[i]+(-x[i]+b.x[i])*r.Rand()*alfa);
          tmp2.x[i]=(int)(b.x[i]+(x[i]-b.x[i])*r.Rand()*alfa);
          if (tmp1.x[i]>nlabels) tmp1.x[i]=nlabels;
          if (tmp1.x[i]<0) tmp1.x[i]=0;
          if (tmp2.x[i]>nlabels) tmp2.x[i]=nlabels;
          if (tmp2.x[i]<0) tmp2.x[i]=0;
      }

      pair_gg result=new pair_gg(tmp1,tmp2);
      return result;
  }

  public pair_gg cruce(GenotypeBoosting b) {
      // two points crossover operator
      
      GenotypeBoosting tmp1=duplica();
      GenotypeBoosting tmp2=b.duplica();
      int p1=(int)(r.Rand()*tmp1.x.length);
      int p2=(int)(r.Rand()*tmp1.x.length);
      if (p2<p1) { int tmp=p1; p1=p2; p2=tmp; }
      for (int i=p1;i<=p2;i++) {
          int tmp=tmp1.x[i];
          tmp1.x[i]=tmp2.x[i];
          tmp2.x[i]=tmp;
      }

      pair_gg result=new pair_gg(tmp1,tmp2);
      return result;
  }


  public GenotypeBoosting mutacion() {
      // mutation operator
      final double alfa=0.5f;
      GenotypeBoosting tmp1=duplica();
      int i=(int)(r.Rand()*x.length);

      // tmp1.x[i]=(int)(tmp1.x[i]*r.Rand()*alfa);
      tmp1.x[i]=(int)((nlabels+1)*r.Rand());

      if (tmp1.x[i]>nlabels) tmp1.x[i]=nlabels;
      if (tmp1.x[i]<0) tmp1.x[i]=0;
      return tmp1;
  }
  

  public GenotypeBoosting(int nentradas, int nl, Randomize vr) {
      r=vr;
        x=new int[nentradas]; nlabels=nl; 
  }

  public void inicializa() {
    // init: generate random values
   for (int i=0;i<x.length;i++) x[i]=(int)(r.Rand()*(nlabels)+0.5);
  }

  public double distancia(GenotypeBoosting g) {
    // distance between two genotypes
    double suma=0;
    for (int i=0;i<x.length;i++) suma+=(x[i]-g.x[i])*(x[i]-g.x[i]);
    return suma;
  }

  public String AString() {
    String result="["; 
    for (int i=0;i<x.length;i++) result=result+x[i]+" ";
    result=result+"]";
    return result;
  }

  public GenotypeBoosting duplica() { 
    // clon
    GenotypeBoosting tmp= new GenotypeBoosting(x.length,nlabels,r); 
    for (int i=0;i<x.length;i++) tmp.x[i]=x[i];
    return tmp;
  }

}


