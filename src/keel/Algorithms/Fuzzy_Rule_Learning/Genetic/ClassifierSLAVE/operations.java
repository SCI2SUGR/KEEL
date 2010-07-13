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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierSLAVE;

import org.core.*;
import java.util.*;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */

public class operations {
/**
 * <p>
 * This class perform certain operations (swapping two values, ...)
 * </p>
 */

	/**
 	 * <p>
	 * Default constructor
	 * </p>
	 */
    operations(){
    }


	/**
 	 * <p>
	 * TRUE if a random value is less or equal to a value "x". FALSE otherwise
	 * </p>
	 * @param x double A given value
	 * @return TRUE if a random value is less or equal to a value "x". FALSE otherwise
	 */
    boolean Probability(double x){
      double a = Randomize.Rand();
      return (a<=x);
    }


	/**
	 * <p>
	 * Select a random individual
	 * </p>
	 * @param n int The number of inidivuals
	 * @param menos_este int An individual which is not allowed to be selected
	 * @param eli int The selected individual has to be greater than "eli" (the number of elite individuals)
	 * @return int The selected individual
	 */
    int Select_Random_Individual(int n, int menos_este, int eli){
      int nd=n;
      int a= Randomize.RandintClosed(0,nd);

      while (a==menos_este || a<eli)
        a=Randomize.RandintClosed(0,nd);

      return a;
    }


	/**
	 * <p>
	 * It randomly selects a cut point in the individual
	 * </p>
	 * @param n int Size of the individual
	 * @return int The selected cut point
	 */
    int CutPoint(int n){
      int nd=n;
      return (Randomize.RandintClosed(0,nd));
    }


	/**
	 * <p>
	 * It randomly selects two cut points in the individual
	 * </p>
	 * @param n int Size of the individual
	 * @param milista ArrayList<Integer> Contains the two cut points selected
	 */
    void CutPoint2(int n, ArrayList<Integer> milista){
        Integer aux1, aux2;
        aux1 = milista.get(0);
        aux2 = milista.get(1);

     int a, b;
      int nd=n;
      a=Randomize.RandintClosed(0,nd);
      b=Randomize.RandintClosed(0,nd);
      if (a>b){
        int aux=a;
        a=b;
        b=aux;
      }

      aux1 = Integer.valueOf(a);
      aux2 = Integer.valueOf(b);

      milista.add(0, aux1);
milista.add(1, aux2);
    }




	/**
	 * <p>
	 * It swaps to int values
	 * </p>
	 * @param milista ArrayList<Integer> Contains the two int values to be swapped
	 */
    void Swap_int(ArrayList<Integer> milista){
        Integer aux1, aux2;
        aux1 = milista.get(0);
        aux2 = milista.get(1);

        int a, b;
        a = aux1.intValue();
        b = aux2.intValue();

      int aux=a;
      a=b;
      b=aux;

      aux1 = Integer.valueOf(a);
      aux2 = Integer.valueOf(b);

      milista.add(0, aux1);
        milista.add(1, aux2);
    }


	/**
	 * <p>
	 * It swaps to double values
	 * </p>
	 * @param milista ArrayList<Integer> Contains the two double values to be swapped
	 */
    void Swap_double(ArrayList<Double> milista){
        Double aux1, aux2;
        aux1 = milista.get(0);
        aux2 = milista.get(1);

        double a, b;
        a = aux1.intValue();
        b = aux2.intValue();

      double aux=a;
      a=b;
      b=aux;

      aux1 = Double.valueOf(a);
      aux2 = Double.valueOf(b);

      milista.add(0, aux1);
        milista.add(1, aux2);
    }

	/**
	 * <p>
	 * It swaps to boolean values
	 * </p>
	 * @param milista ArrayList<Integer> Contains the two boolean values to be swapped
	 */
    void Swap_boolean(ArrayList<boolean[]> milista){
        boolean aux[] = new boolean[2];
        aux = milista.get(0);

        boolean a = aux[0];
        boolean b = aux[1];

      boolean auxiliar=a;
      a=b;
      b=auxiliar;

      aux[0] = a;
      aux[1] = b;

      milista.add(0, aux);
    }

}

