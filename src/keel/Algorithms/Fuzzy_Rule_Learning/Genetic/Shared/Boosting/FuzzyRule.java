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

public class FuzzyRule {

   // terms values meanning
   //          -1 : term has no performance
   // 0..nlabel-1 : Assertion x=label
   //   >= nlabel : 'or' asserts combination

   // We handle type 2 rules with signed consecuents
   // It is equivalent to handle type 3 rules after 
   // rule bank normalization

   public int antecedente[];
   public double consecuente[];

   public FuzzyRule(int ant[], double con[]) {
     antecedente=new int[ant.length];
     for (int i=0;i<ant.length;i++) antecedente[i]=ant[i];
     consecuente=new double[con.length];
     for (int i=0;i<con.length;i++) consecuente[i]=con[i];
   }

   public double tnorma(double a, double b) {
     if (a<b) return a; else return b;
   }
}


