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

package keel.Algorithms.Discretizers.CAIM_Discretizer;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


/** 
 * <p>
 * This class implements the CADD discretizer.
 * </p>
 * 
 * <p>
 * @author Written by Salvador García (University of Jaén - Jaén) 31/12/2009
 * @version 1.1
 * @since JDK1.5
 * </p>
 */
public class CAIMDiscretizer extends Discretizer {

	/**
	* Builder
	*/
	public CAIMDiscretizer () {
		
	}

	protected Vector discretizeAttribute(int attribute,int []values,int begin,int end) {
		
		int sumaAbajo[], sumaDerecha[], total[], quanta[][];
		int ordenados[];
		double fitness, mejorFitness, globalFitness;
		boolean parar = false;
		
		Vector <Double> cp = new Vector <Double>();
		Vector <Double> cpTmp = new Vector <Double>();
		Vector <Double> mejorCP = new Vector <Double>();
		
		int i, j, k;
		int mejorPos;
		
	
		/*First step of CAIM algorithm*/

		for (i=0; i < realValues[attribute].length - 1; i++) {
			double cutPoint = (realValues[attribute][values[i]] + realValues[attribute][values[i+1]]) / 2.0;
			if (cutPoint != realValues[attribute][values[i]])
				cp.addElement(new Double(cutPoint));
		}
		
		/*Second step of CAIM algorithm*/		
		
		ordenados = new int[end - begin + 1];
		for (i=begin, j=0; i<=end; i++, j++) {
			ordenados[j] = values[i];
		}
		
		quanta = new int[Parameters.numClasses][mejorCP.size()+1];
		sumaAbajo = new int[mejorCP.size()+1];
		sumaDerecha = new int[Parameters.numClasses];
		total = new int[1];		
		construyeQuanta(quanta, sumaAbajo, sumaDerecha, total, cpTmp, ordenados, attribute);
		globalFitness = computeFitness(quanta, sumaAbajo, sumaDerecha, total[0]);
		
		k = 1;		
		while (!parar && cp.size()>0) {
			quanta = new int[Parameters.numClasses][mejorCP.size()+2];
			sumaAbajo = new int[mejorCP.size()+2];
			sumaDerecha = new int[Parameters.numClasses];
			total = new int[1];
		
			mejorPos = 0;
			cpTmp = new Vector <Double>(mejorCP);
			cpTmp.addElement(cp.elementAt(0));
			Collections.sort(cpTmp);
			construyeQuanta(quanta, sumaAbajo, sumaDerecha, total, cpTmp, ordenados, attribute);
			mejorFitness = computeFitness(quanta, sumaAbajo, sumaDerecha, total[0]);
			for (i=1; i<cp.size(); i++) {
				cpTmp = new Vector <Double>(mejorCP);
				cpTmp.addElement(cp.elementAt(i));
				Collections.sort(cpTmp);
				construyeQuanta(quanta, sumaAbajo, sumaDerecha, total, cpTmp, ordenados, attribute);
				fitness = computeFitness(quanta, sumaAbajo, sumaDerecha, total[0]);
				if (fitness > mejorFitness) {
					mejorFitness = fitness;
					mejorPos = i;
				}				
			}
			
			if (k >= Parameters.numClasses) {
				if (mejorFitness > globalFitness) {
					globalFitness = mejorFitness;
					mejorCP.addElement(cp.elementAt(mejorPos));
					Collections.sort(mejorCP);
					cp.removeElementAt(mejorPos);
				} else {
					parar = true;
				}
			} else {
				globalFitness = mejorFitness;
				mejorCP.addElement(cp.elementAt(mejorPos));
				Collections.sort(mejorCP);
				cp.removeElementAt(mejorPos);				
			}
			k++;
		}
		
		return mejorCP;
	}
	
	private void construyeQuanta (int quanta[][], int sumaAbajo[], int sumaDerecha[], int total[], Vector <Double> cutPoints, int ordenados[], int attribute) {
		
		int i, j;
		int intervalo = 0;
		
		for (i=0; i<quanta.length; i++) {
			for (j=0; j<quanta[i].length; j++) {
				quanta[i][j] = 0;
				sumaAbajo[j] = 0;
			}
			sumaDerecha[i] = 0;
		}
		total[0] = 0;
		
		for (i=0; i<ordenados.length; i++) {
			if (intervalo < cutPoints.size()) {
				if (realValues[attribute][ordenados[i]] >= cutPoints.elementAt(intervalo)) {
					intervalo++;
				}
			} else {
				intervalo = cutPoints.size();
			}
			quanta[classOfInstances[ordenados[i]]][intervalo]++;
		}
		
		for (i=0; i<quanta.length; i++) {
			for (j=0; j<quanta[i].length; j++) {
				sumaAbajo[j] += quanta[i][j];
				sumaDerecha[i] += quanta[i][j];
				total[0] += quanta[i][j];
			}
		}		
	}	
	
	private double computeFitness (int quanta[][], int sumaAbajo[], int sumaDerecha[], int total) {
		
		int i, j;
		int mejorCount;
		double suma = 0.0;
		double temp;
		
		for (i=0; i<quanta[0].length; i++) {
			mejorCount = quanta[0][i];
			for (j=1; j<quanta.length; j++) {
				if (quanta[j][i] > mejorCount) {
					mejorCount = quanta[j][i];
				}
			}
			temp = (double)mejorCount / (double)sumaAbajo[i];
			temp *= (double)mejorCount;
			suma += temp;
		}
		
		return suma / (double)quanta[0].length;
	}	
}

