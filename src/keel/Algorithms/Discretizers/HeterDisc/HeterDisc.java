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

package keel.Algorithms.Discretizers.HeterDisc;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Parameters;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;


/**
 * <p>
 * This class implements the Heter-Disc discretizer
 * </p>
 * 
 * @author Written by Jose A. Saez (University of Granada), 21/12/2009
 * @version 1.0
 * @since JDK1.6
 */
public class HeterDisc extends Discretizer {
	
	// this class lets to manipulate discretization schemes
	public class DiscretizationScheme {
		
		public int[] cpSelected;	// indexes of the selected cutpoints
		public double fitness;		// fitness of the discretization
		
		public DiscretizationScheme(int v[], int tam, double f){
			cpSelected = new int[tam];
			System.arraycopy(v, 0, cpSelected, 0, tam);
			fitness = f;
		}
	}
	
//******************************************************************************************************

	private double[] cutpoints;					// possible cutpoints
	private int numcp;							// number of possible cutpoints
	
	private int numInstances;					// number of instances
	
	private int[][] matrix;						// quanta matrix
	private Vector<DiscretizationScheme>  CD;	// vector with all best discretizations
	private Vector<DiscretizationScheme> GD;	// complete neighborhood of all discretizations at CD
	
	private int[] solution;						// best discretization found
	private int[] numInterAtt;					// number of intervals of each attribute

	
//******************************************************************************************************
	
	/**
	 * <p>
	 * Constructor of the class
	 * </p>
	 */
	public HeterDisc(){
		
		int i;
		
		numInterAtt = new int[Parameters.numAttributes];
		
		
		if(Parameters.setConfig){

			for(i = 0 ; i < Parameters.numAttributes ; ++i){
				
				Attribute att = Attributes.getAttribute(i);
				
				if(att.getType() == Attribute.REAL || att.getType() == Attribute.INTEGER)
					numInterAtt[i] = Parameters.numIntervals;
				
				// default case
				else
					numInterAtt[i] = 0;
			}
		}
		
		else{
			String[] inter = Parameters.numIntrvls.split("_");
			
			int cont = 0;
			for(i = 0 ; i < Parameters.numAttributes ; ++i){
				
				Attribute att = Attributes.getAttribute(i);
				
				if(att.getType() == Attribute.REAL || att.getType() == Attribute.INTEGER)
					numInterAtt[i] = Integer.parseInt(inter[cont++]);
				
				// default case
				else
					numInterAtt[i] = 0;
			}
		}
	}

//******************************************************************************************************	

	/**
	 * <p>
	 * Returns a vector with the discretized values
	 * </p>
	 * @param attribute index of the attribute to discretize
	 * @param values vector of indexes of the instances sorted from lowest to highest value of attribute
	 * @param begin index of the instance with the lowest value of attribute
	 * @param end index of the instance with the highest value of attribute
	 * @return vector with the discretized values
	 */
	protected Vector discretizeAttribute(int attribute, int []values, int begin, int end){
		
		Boolean continueLoop;
		int i, point;									// loop indexes
		numInstances = realValues[attribute].length; 	// number of instances
		
		
		// 1) Form a set of all distinct values of attribute in ascending order
		double[] valuesNoRepeated = new double[end+1];
		int size = 0;
		
		double value = realValues[attribute][values[begin]];
		valuesNoRepeated[size++] = value;
		
		for(i = begin+1 ; i <= end ; ++i){
			if(value != realValues[attribute][values[i]]){
				valuesNoRepeated[size++] = realValues[attribute][values[i]];
				value = realValues[attribute][values[i]];
			}
		}
		
		//System.out.println("att = " + attribute + ", size = " + size);
		if(size == 1){
			Vector cp = new Vector();
			if(valuesNoRepeated[0] != realValues[attribute][values[end]])
				cp.add(valuesNoRepeated[0]);
			return cp;
		}
			
		
		
		// 2) Get the classes of each interval: -1 = no class, -2 = multiple classes, other = his class
		int[] classOfInterval = new int[size-1];
		
		for(i = 0 ; i < classOfInterval.length ; ++i)
			classOfInterval[i] = -1;	
		
		//compute class of each interval
		for(i = 0 ; i < numInstances ; ++i){
			
			continueLoop = true;
						
			for(point = 1 ; point < size && continueLoop ; ++point){
				
				if(realValues[attribute][i] <= valuesNoRepeated[point]){
					
					if(classOfInterval[point-1] == -1){
						 classOfInterval[point-1] = classOfInstances[i];	
					}
					
					else{
						if(classOfInterval[point-1] != classOfInstances[i])
							classOfInterval[point-1] = -2;
					}
					
					continueLoop = false;
				}
			}
		}
		
		
		// 3) Join intervals if both have equal class and get possible cutpoints
		cutpoints = new double[size];
		numcp = 0;
		
		int classInter = classOfInterval[0];
		cutpoints[numcp++] = valuesNoRepeated[0];
		
		
		for(i = 1 ; i < classOfInterval.length ; ++i){
			if(classInter != classOfInterval[i] || classOfInterval[i] == -2){
				cutpoints[numcp++] = valuesNoRepeated[i];
				classInter = classOfInterval[i];
			}
		}
		
		cutpoints[numcp++] = valuesNoRepeated[size-1];
		
		
		// 4) Compute initial fitness
		int[] selected = new int[numcp];
		for(i = 0 ; i < numcp ; ++i)
			selected[i] = 0;

		int ni = 1;
		selected[0] = 1; selected[numcp-1] = 1; 	// d0 y dn are selected
		
		double GlobalOpt = computeCriterionFuction(selected, ni, attribute);
		
		solution = new int[numcp];
		System.arraycopy(selected, 0, solution, 0, numcp);
		
		
		// 5) run loop
		CD = new Vector<DiscretizationScheme>();
		CD.add(new DiscretizationScheme(selected,numcp,GlobalOpt));
		
		while(CD.size() > 0){
			
			ni++;
			
			// generate GD (all possible best neighbors from each discretization scheme in CD)
			GD = new Vector<DiscretizationScheme>();
			for(i = 0 ; i < CD.size() ; ++i)
				generateNeighborhood(CD.get(i), ni, attribute, GlobalOpt);
			
			// CD <- GD (all D in GD that criteriorFunction(D) > Globalopt)
			CD = new Vector<DiscretizationScheme>();
			double maxFitness = (-1)*Double.MIN_VALUE;

			for(i = 0 ; i < GD.size() ; ++i){
					
				CD.add(GD.get(i));
				double fit = CD.get(i).fitness;

				if(fit > maxFitness){
					maxFitness = fit;
					System.arraycopy(CD.get(i).cpSelected, 0, solution, 0, numcp);
				}
				
			}
		
			// update GlobalOpt
			GlobalOpt = maxFitness;	
		}
		
		
		ni--;
		
		solution[0] = solution[numcp-1] = 0;
		Vector cp = new Vector();
		for(i = 0 ; i < numcp ; ++i)
			if(solution[i] == 1)
				cp.add(cutpoints[i]);
		
		return cp;
	}
	
//******************************************************************************************************	

	/**
	 * <p>
	 * It generates the neighborhood of ds scheme discretization and adds each neighbor to variable GD
	 * </p>
	 * @param ds discretization scheme to generate its neighborhood
	 * @param ni number of intervals of the neighbors discretizations
	 * @param attribute index of the attribute to discretize
	 * @param GlobalOpt fitness of the best discretization scheme found
	 */
	public void generateNeighborhood(DiscretizationScheme ds, int ni, int attribute, double GlobalOpt){
		
		int i;
		double fitness, max = (-1)*Double.MIN_VALUE;
		int[] best = new int[numcp];
		
		for(i = 0 ; i < numcp ; ++i){
			
			if(ds.cpSelected[i] == 0){
				
				int[] v = new int[numcp];
				System.arraycopy(ds.cpSelected, 0, v, 0, numcp);
				v[i] = 1;
				fitness = computeCriterionFuction(v, ni, attribute);
				
				if(fitness > GlobalOpt){
					GD.add(new DiscretizationScheme(v,numcp,fitness));
				}
				
				else{
					// compute the better discretization with fitness lower than GlobalOpt
					if(fitness > max){
						System.arraycopy(v, 0, best, 0, numcp);
						max = fitness;
					}
				}
			}
			
		}
		
		
		// save the better discretization with fitness lower than GlobalOpt...
		if(ni <= numInterAtt[attribute])
			GD.add(new DiscretizationScheme(best,numcp,max));
	}
	
//******************************************************************************************************	

	/**
	 * <p>
	 * It computes and returns the value of criterion function of the discretization scheme build with selectedp cutpoints
	 * </p>
	 * @param selectedp indexes of selected cutpoints
	 * @param ni number of intervals
	 * @param attribute index of the attribute
	 * @return the criterion function value
	 */	
	public double computeCriterionFuction(int[] selectedp, int ni, int attribute){
				
		int i, s;
		double fitnessDiscr = 0;
		
		// create quata matrix
		CreateQuantaMatrix(ni, attribute, selectedp);
		
		// create the conditional class probability vector for each interval
		double[][] ccpv = new double[ni][Parameters.numClasses];
		for(i = 0 ; i < ni ; ++i)
			for(s = 0 ; s < Parameters.numClasses ; ++s)
				ccpv[i][s] = (double) matrix[s][i] / (double) matrix[Parameters.numClasses][i];
		
		
		for(i = 0 ; i < ni ; ++i)
			fitnessDiscr += ((double)matrix[Parameters.numClasses][i]/(double)numInstances)*computeHeterCCPV(ccpv[i]);
		
		return fitnessDiscr/ni;
	}
	
//******************************************************************************************************	

	/**
	 * <p>
	 * It computes the heterogeneity for a conditional class probability vector given needed for compute
	 * the heterogeneity of a discretization scheme in criterion fuction calculus
	 * </p>
	 * @param ccpv conditional class probability vector
	 * @return the heterogeneity value
	 */	
	public double computeHeterCCPV(double ccpv[]){
		
		double total = 0, aux;
		int s;

		for(s = 0 ; s < Parameters.numClasses ; ++s){
			aux = ccpv[s]-(double)(1.0/Parameters.numClasses);
			total += Math.pow(aux,2);
		}

		return Math.sqrt(total);
	}
	
//******************************************************************************************************	

	/**
	 * <p>
	 * It creates the quanta matrix basis of selected cutpoints array
	 * </p>
	 * @param ni number of intervals
	 * @param attribute index of the attribute
	 * @param selected vector with indexes of selected cut-points
	 */
	public void CreateQuantaMatrix(int ni, int attribute, int[] selected){
		
		int i, j, point, clase;		// loop indexes
		
		// matrix initialization
		matrix = new int[Parameters.numClasses+1][];
		for(i = 0 ; i < Parameters.numClasses+1 ; ++i)
			matrix[i] = new int[ni+1];
		
		for(i = 0 ; i < Parameters.numClasses+1 ; ++i)
			for(j = 0 ; j < ni+1 ; ++j)
				matrix[i][j] = 0;
		
		
		// create quanta matrix
		boolean continuar = true;
		int interval = 0;

		
		for(i = 0 ; i < numInstances ; ++i){
			
			continuar = true;
			interval = 0;
			
			for(point = 1 ; point < numcp && continuar ; ++point){
				
				if(realValues[attribute][i] <= cutpoints[point] && selected[point] == 1){
					matrix[classOfInstances[i]][interval]++;
					continuar = false;
				}
				
				if(selected[point] == 1)
					interval++;
			}
		}
		
		
		//sumatory per classes
		int suma;
		for(clase = 0 ; clase < Parameters.numClasses ; ++clase){
			
			suma = 0;
			for(j = 0 ; j < ni ; ++j)
				suma += matrix[clase][j];
			
			matrix[clase][ni] = suma;
		}
		
		//sumatory per intervals
		for(j = 0 ; j < ni ; ++j){
			
			suma = 0;
			for(clase = 0 ; clase < Parameters.numClasses ; ++clase)
				suma += matrix[clase][j];
			
			matrix[Parameters.numClasses][j] = suma;
		}
		
	}
	
}
