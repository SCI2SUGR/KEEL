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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.QAR_CIP_NSGAII;
import java.util.ArrayList;

import org.core.*;


/**
 * <p>Title: Class Sort</p>
 *
 * <p>Description: Here you have methods to sort populations</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Alvaro Enciso Ruiz (UGR) 10/10/2008
 * @version 1.0
 * @since JDK 1.5
 */
public abstract class Sort {
	
	/**
	 * Actual implementation of the randomized quick sort used to sort a population based on a particular objective chosen
	 * @param pop population to sort
	 * @param objcount objetive´s number
	 * @param obj_array objetive's array
	 * @param left left index
	 * @param right right index
	 */
	public static void q_sort_front_obj(ArrayList<Chromosome> pop, int objcount, int[] obj_array, int left, int right)
	{
	    int index;
	    int temp;
	    int i, j;
	    double pivot;
	    if (left<right)
	    {
	        index = Randomize.RandintClosed (left, right);
	        temp = obj_array[right];
	        obj_array[right] = obj_array[index];
	        obj_array[index] = temp;
	        pivot = pop.get(obj_array[right]).getObjective(objcount);
	        i = left-1;
	        for (j=left; j<right; j++)
	        {
	            if (pop.get(obj_array[j]).getObjective(objcount) <= pivot)
	            {
	                i+=1;
	                temp = obj_array[j];
	                obj_array[j] = obj_array[i];
	                obj_array[i] = temp;
	            }
	        }
	        index=i+1;
	        temp = obj_array[index];
	        obj_array[index] = obj_array[right];
	        obj_array[right] = temp;
	        q_sort_front_obj (pop, objcount, obj_array, left, index-1);
	        q_sort_front_obj (pop, objcount, obj_array, index+1, right);
	    }
	    return;
	}

	/**
	 * Actual implementation of the randomized quick sort used to sort a population based on a crowding distance
	 * @param pop population to sort
	 * @param dist distance´s vector
	 * @param left left index
	 * @param right right index
	 */
	public static void q_sort_dist(ArrayList<Chromosome> pop, int []dist, int left, int right)
	{
	    int index;
	    int temp;
	    int i, j;
	    double pivot;
	    if (left<right)
	    {
	        index = Randomize.RandintClosed (left, right);
	        temp = dist[right];
	        dist[right] = dist[index];
	        dist[index] = temp;
	        pivot = pop.get(dist[right]).crowd_dist;
	        i = left-1;
	        for (j=left; j<right; j++)
	        {
	            if (pop.get(dist[j]).crowd_dist <= pivot)
	            {
	                i+=1;
	                temp = dist[j];
	                dist[j] = dist[i];
	                dist[i] = temp;
	            }
	        }
	        index=i+1;
	        temp = dist[index];
	        dist[index] = dist[right];
	        dist[right] = temp;
	        q_sort_dist (pop, dist, left, index-1);
	        q_sort_dist (pop, dist, index+1, right);
	    }
	    return;
	}

	
	/**
	 * Randomized quick sort routine to sort a population based on crowding distance 
	 * @param pop population to sort
	 * @param dist distance´s vector
	 * @param front_size
	 */	
	public static void quicksort_dist(ArrayList<Chromosome> pop, int []dist, int front_size)
	{
	    q_sort_dist (pop, dist, 0, front_size-1);
	}

	
	/**
	 * Randomized quick sort routine to sort a population based on a particular objective chosen 
	 * @param pop population to sort
	 * @param objcount objetive´s number
	 * @param obj_array objetive's array
	 * @param obj_array_size size of objetive's array
	 */	
	public static void quicksort_front_obj(ArrayList<Chromosome>pop, int objcount, int obj_array[], int obj_array_size)
	{
	    q_sort_front_obj (pop, objcount, obj_array, 0, obj_array_size-1);
	}


}
