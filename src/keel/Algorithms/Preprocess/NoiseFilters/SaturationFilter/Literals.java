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

/**
 * <p>
 * @author Written by Jose A. Saez Munoz, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * Date: 06/01/10
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Preprocess.NoiseFilters.SaturationFilter;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;


/**
 * <p>
 * This class transforms the data set into a set of literals given the positive class
 * </p>
 */
public class Literals {
	
	// tags definition
	final private static int POSITIVE = 0;	// positive class
	final private static int NEGATIVE = 1;	// negative class
	final private static int BOTH = 2;		// both positive and negative class
	
	//instance variables
	private Instance[] instances;
	
	private boolean[][] literals;
	private int numlit;

	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * Constructor of the class
	 * </p>
	 * @param inst instances of the original data set
	 * @param positiveClass positive class as integer
	 */
	public Literals(Instance[] inst, int positiveClass){
		instances = inst;
		createLiteralsSet(positiveClass);
	}

//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It creates the set of literals from the original set of instances
	 * </p>
	 * @param positiveClass the postive class
	 */
	private void createLiteralsSet(int positiveClass){
		
		int aux = 0;
		int[] index = new int[Parameters.numAttributes];
		for(int i = 0 ; i < Parameters.numAttributes ; ++i){
			index[i] = aux;
			if(Attributes.getInputAttribute(i).getType() == Attribute.INTEGER)
				aux += 2;
			else
				aux ++;	
		}

		double[][][] valuesNoRep = new double[2][aux][];
		int[][] values =  new int[2][aux];
		aux = 0;
		numlit = 0;
		
		
		//compute the possible maximal number of literals
		for(int i = 0 ; i < Parameters.numAttributes ; ++i){
			
			Attribute att = Attributes.getInputAttribute(i);
			
			if(att.getType() == Attribute.NOMINAL){
				Object[] res = nominales(i, positiveClass,true);
				values[POSITIVE][aux] = (Integer)res[0];
				valuesNoRep[POSITIVE][aux] = (double[])res[1];
				values[NEGATIVE][aux] = (Integer)res[2];
				valuesNoRep[NEGATIVE][aux++] = (double[])res[3];
				numlit += values[POSITIVE][aux-1] + values[NEGATIVE][aux-1];
			}
			
			if(att.getType() == Attribute.REAL){
				Object[] res = reales(i,positiveClass);
				values[POSITIVE][aux] = (Integer)res[0];			// num valores pos
				valuesNoRep[POSITIVE][aux] = (double[])res[1];	// valores pos
				values[NEGATIVE][aux] = (Integer)res[2];			// num valores neg
				valuesNoRep[NEGATIVE][aux++] = (double[])res[3];	// valores neg
				
				numlit += values[POSITIVE][aux-1]/2 + values[NEGATIVE][aux-1]/2;
			}
			
			if(att.getType() == Attribute.INTEGER){
				
				// handle it as categorical
				Object[] res = nominales(i, positiveClass,false);
				values[POSITIVE][aux] = (Integer)res[0];			// num valores postivos
				valuesNoRep[POSITIVE][aux] = (double[])res[1];	// valores pos
				values[NEGATIVE][aux] = (Integer)res[2];			// num neg
				valuesNoRep[NEGATIVE][aux++] = (double[])res[3];	// valores neg
				
				numlit += values[POSITIVE][aux-1] + values[NEGATIVE][aux-1];
				
				// handle it as real
				res = reales(i,positiveClass);
				values[POSITIVE][aux] = (Integer)res[0];			// num valores
				valuesNoRep[POSITIVE][aux] = (double[])res[1];	// valores
				values[NEGATIVE][aux] = (Integer)res[2];			// clases
				valuesNoRep[NEGATIVE][aux++] = (double[])res[3];	// num clases
				
				numlit += values[POSITIVE][aux-1]/2 + values[NEGATIVE][aux-1]/2;
			}
		}
		
		//System.out.println("NUMLIT estimado = " + numlit);
		
		literals = new boolean[instances.length][numlit];
		numlit = 0;
		
		//rellenar los literales
		for(int i = 0 ; i < Parameters.numAttributes ; ++i){
			
			Attribute att = Attributes.getInputAttribute(i);
			
			if(att.getType() == Attribute.NOMINAL)
				addNominalLiterals(values, valuesNoRep, index[i], i, false);

			if(att.getType() == Attribute.REAL)
				addNumericalLiterals(values, valuesNoRep, index[i], i);
			
			if(att.getType() == Attribute.INTEGER){
				addNominalLiterals(values, valuesNoRep, index[i], i, true);
				addNumericalLiterals(values, valuesNoRep, index[i]+1, i);
			}
		}

	}

//*******************************************************************************************************************************	
	
	/**
	 * <p>
	 * It returns the number the literals
	 * </p>
	 * @return the number of literals
	 */	
	public int getNumLiterals(){
		return numlit;
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It returns the matrix of literals
	 * </p>
	 * @return the matrix of literals
	 */	
	public boolean[][] getLiteralsMatrix(){
		return literals;
	}

//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It computes the different number of categorical values for postive and negative examples and
	 * their values
	 * </p>
	 * @param att index of the attribute
	 * @param claspos positive class
	 * @param nominalb if the attribute is handle as nominal or not (as integer)
	 * @return the different number of categorical values for postive and negative examples and
	 * their values
	 */
	private Object[] nominales(int att, int claspos, boolean nominalb){
		
        double[] valuesPOS = new double[instances.length];
        double[] valuesNEG = new double[instances.length];
        int numPOS = 0, numNEG = 0;
		
    	for(int j = 0 ; j < instances.length ; ++j){

    		if(nominalb){
    			if(instances[j].getOutputNominalValuesInt(0) == claspos)
    				valuesPOS[numPOS++] = instances[j].getInputNominalValuesInt(att);
    			else
    				valuesNEG[numNEG++] = instances[j].getInputNominalValuesInt(att);
    		}
    		
    		else{
    			if(instances[j].getOutputNominalValuesInt(0) == claspos)
    				valuesPOS[numPOS++] = instances[j].getInputRealValues(att);
    			else
    				valuesNEG[numNEG++] = instances[j].getInputRealValues(att);
    		}
    		
    	}
    	
    	
    	double value;
    	int numValuesPOS = 0, numValuesNEG = 0;
    	double[] valuesNoRepPOS = null;
    	double[] valuesNoRepNEG = null;
    	
    	if(numPOS > 0){
    		int[] posPOS = Quicksort.sort(valuesPOS, numPOS, Quicksort.LOWEST_FIRST);
        	valuesNoRepPOS = new double[numPOS];
    		numValuesPOS = 0;
    		
    		value = valuesPOS[posPOS[0]];
    		valuesNoRepPOS[numValuesPOS++] = value;
    		
    		for(int k = 1 ; k < numPOS ; ++k){
    			if(value != valuesPOS[posPOS[k]]){
    				valuesNoRepPOS[numValuesPOS++] = valuesPOS[posPOS[k]];
    				value = valuesPOS[posPOS[k]];
    			}
    		}
    	}
    	
       	
    	if(numNEG > 0){
        	int[] posNEG = Quicksort.sort(valuesNEG, numNEG, Quicksort.LOWEST_FIRST);
    		
    		valuesNoRepNEG = new double[numNEG];
    		numValuesNEG = 0;
    		
    		value = valuesNEG[posNEG[0]];
    		valuesNoRepNEG[numValuesNEG++] = value;
    		
    		for(int k = 1 ; k < numNEG ; ++k){
    			if(value != valuesNEG[posNEG[k]]){
    				valuesNoRepNEG[numValuesNEG++] = valuesNEG[posNEG[k]];
    				value = valuesNEG[posNEG[k]];
    			}
    		}
    		
    	}

 
		Object[] res = new Object[4];
		res[1] = valuesNoRepPOS;
		res[0] = numValuesPOS;
		res[3] = valuesNoRepNEG;
		res[2] = numValuesNEG;
		
		return res;
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It computes the different number of real values for postive and negative examples and
	 * their values
	 * </p>
	 * @param att index of the attribute
	 * @param claspos positive class
	 * @return the different number of real values for postive and negative examples and
	 * their values
	 */
	private Object[] reales(int att, int positiveClass){
		
		int i;		// loop indexes
		
		double[] values = new double[Parameters.numInstances];
		for(i = 0 ; i < Parameters.numInstances ; ++i)
			values[i] = instances[i].getInputRealValues(att);
		
		// sort the values (with repeated) from lowest to highest
		int[] pos = Quicksort.sort(values, Parameters.numInstances, Quicksort.LOWEST_FIRST);
		
		
		// form a set of all distinct values of attribute in ascending order
		// and compute the class of each value
		double[] valuesNoRepeated = new double[Parameters.numInstances];
		int size = 0;
		
		// the classes of each interval: -1 = no class, -2 = multiple classes, other = his class
		int[] valueClass = new int[Parameters.numInstances];
		for(i = 0 ; i < Parameters.numInstances ; ++i)
			valueClass[i] = -1;
		
		double value = instances[pos[0]].getInputRealValues(att);
		valuesNoRepeated[size] = value;
		valueClass[size++] = instances[pos[0]].getOutputNominalValuesInt(0);
		
		for(i = 1 ; i < Parameters.numInstances ; ++i){
			
			// a new value
			if(value != instances[pos[i]].getInputRealValues(att)){
				value = instances[pos[i]].getInputRealValues(att);
				valuesNoRepeated[size] = value;
				if(instances[pos[i]].getOutputNominalValuesInt(0) == positiveClass)
					valueClass[size++] = POSITIVE;
				else
					valueClass[size++] = NEGATIVE;
			}
			
			// a repeated value
			else{
					
				if( (instances[pos[i]].getOutputNominalValuesInt(0) == positiveClass && valueClass[size-1] == NEGATIVE)
				 || (instances[pos[i]].getOutputNominalValuesInt(0) != positiveClass && valueClass[size-1] == POSITIVE))
					valueClass[size-1] = BOTH;
			}
		}
		
		//para POSITIVOS (empieza con un positivo y acaba con un negativo)
		double[][] finalValues = new double[2][size];
		int numvPOS = 0, numvNEG = 0, clase = -1, posit = 0, actualClass = POSITIVE;
		
		// coger el primer valor de clase positiva
		boolean salir = false;
		for(i = 0 ; i < size && !salir ; ++i){
			if(valueClass[i] == POSITIVE){
				actualClass = POSITIVE;
				finalValues[POSITIVE][numvPOS++] = valuesNoRepeated[i];
				clase = valueClass[i];
				posit = i;
				salir = true;
			}
		}

		
		
		if(numvPOS != 0){
			
			// me quedo siempre con el ultimo de la clase positiva y el primero de la negativa
			for(i = posit+1 ; i < size ; ++i){
				
				if(clase != valueClass[i]){

					//para los negativos
					if(actualClass == POSITIVE){
						finalValues[POSITIVE][numvPOS++] = valuesNoRepeated[i];
						clase = valueClass[i];
						actualClass = NEGATIVE;
					}
					
					// para los positivos
					else{
						finalValues[POSITIVE][numvPOS++] = valuesNoRepeated[i];
						actualClass = POSITIVE;
						clase = valueClass[i];	
					}
				}
				
				// valores repetidos
				else{

					// para positivos
					if(actualClass == POSITIVE){
						finalValues[POSITIVE][numvPOS-1] = valuesNoRepeated[i];	
					}
					
					// para los negativos
					else{
					}
				}
			}
		
			// fuerzo a que acabe en negativo, asi: P N .... P N
			if(clase == POSITIVE)
				numvPOS--;
		}

		
		// PARA NEGATIVO
		
		// coger el primer valor de clase negativo
		salir = false;
		for(i = 0 ; i < size && !salir ; ++i){
			if(valueClass[i] == NEGATIVE){
				actualClass = NEGATIVE;
				finalValues[NEGATIVE][numvNEG++] = valuesNoRepeated[i];
				clase = valueClass[i];
				posit = i;
				salir = true;
			}
		}

		if(numvNEG != 0){
			
			// me quedo siempre con el ultimo de la clase positiva y el primero de la negativa
			for(i = posit+1 ; i < size ; ++i){
				
				if(clase != valueClass[i]){
					//para los negativos
					if(actualClass == POSITIVE){
						finalValues[NEGATIVE][numvNEG++] = valuesNoRepeated[i];
						actualClass = NEGATIVE;
						clase = valueClass[i];	
					}
					
					// para los positivos
					else{
						finalValues[NEGATIVE][numvNEG++] = valuesNoRepeated[i];
						clase = valueClass[i];
						actualClass = POSITIVE;	
					}
				}
				
				// valores repetidos
				else{
					// para negativo
					if(actualClass == NEGATIVE){
						finalValues[NEGATIVE][numvNEG-1] = valuesNoRepeated[i];	
					}
					
					// para los positivo
					else{
					}
				}
			}
		
			// fuerzo a que acabe en postivo, asi: N P, ..N P
			if(clase == NEGATIVE)
				numvNEG--;
			
		}

		
		// devolver mierda
		Object[] res = new Object[4];
		res[0] = numvPOS;
		res[1] = finalValues[POSITIVE];
		res[2] = numvNEG;
		res[3] = finalValues[NEGATIVE];
		
		return res;
	}
	
//*******************************************************************************************************************************	

	/**
	 * <p>
	 * It adds the nominal literals to the matrix
	 * </p>
	 * @param values
	 * @param valuesNoRep
	 * @values index
	 * @values i
	 * @values isInteger
	 */
	private void addNominalLiterals(int[][] values, double[][][] valuesNoRep, int index, int i, boolean isInteger){
		
		//para cada valor positivo... lo a–ado
		for(int p = 0 ; p < values[POSITIVE][index] ; ++p){
			
			for(int k = 0 ; k < instances.length ; ++k){
				
				if(isInteger)
					literals[k][numlit] = instances[k].getInputRealValues(i) == (int)valuesNoRep[POSITIVE][index][p];
				else
					literals[k][numlit] = instances[k].getInputNominalValuesInt(i) == (int)valuesNoRep[POSITIVE][index][p];
			}
			
			numlit++;
		}
		
		//para cada valor negativo... lo a–ado
		for(int p = 0 ; p < values[NEGATIVE][index] ; ++p){
			
			for(int k = 0 ; k < instances.length ; ++k){

				if(isInteger)
					literals[k][numlit] = instances[k].getInputRealValues(i) != (int)valuesNoRep[NEGATIVE][index][p];
				else
					literals[k][numlit] = instances[k].getInputNominalValuesInt(i) != (int)valuesNoRep[NEGATIVE][index][p];
			}
			
			numlit++;
		}
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It adds the real literals to the matrix
	 * </p>
	 * @param values
	 * @param valuesNoRep
	 * @values index
	 * @values i
	 */
	private void addNumericalLiterals(int[][] values, double[][][] valuesNoRep, int index, int i){

		// primero los Ai <= (vp+vn)/2 solo si vp < vn
		for(int p = 0 ; p < values[POSITIVE][index]-1 ; p += 2){
			
			for(int k = 0 ; k < instances.length ; ++k){
				literals[k][numlit] = instances[k].getInputRealValues(i) <= (valuesNoRep[POSITIVE][index][p]+valuesNoRep[POSITIVE][index][p+1])/2;	
			}
			numlit++;
		}
		
		
		// despues los Ai > (vp+vn)/2 solo si vn < vp
		for(int p = 0 ; p < values[NEGATIVE][index]-1 ; p += 2){
			
			for(int k = 0 ; k < instances.length ; ++k){
				literals[k][numlit] = instances[k].getInputRealValues(i) > (valuesNoRep[NEGATIVE][index][p]+valuesNoRep[NEGATIVE][index][p+1])/2;
			}	
			numlit++;
		}
	}
	
//*******************************************************************************************************************************	

	/**
	 * <p>
	 * It prints the matrix of literals
	 * </p>
	 */
	public void printlit(){

		for(int i = 0 ; i < Parameters.numInstances ; ++i){
			for(int j = 0 ; j < numlit ; ++j){
				System.out.print(literals[i][j] + " , ");
			}
			System.out.println();
		}	
	}
	
}