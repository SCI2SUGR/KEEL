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

package keel.Algorithms.RE_SL_Methods.SEFC;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
public class Individual implements Comparable {
/**	
 * <p>
 * It contains the definition for a individual
 * </p>
 */
 
    MemFun [] antecedente;
    double [] consecuente;
    double fitness;
    int n_e;
    int n_SistemasDifusos;

    /**
     * <p>       
     * Creates an individual containing "entradas" gaussian membership functions
     * </p>       
     * @param entradas int The number of gaussian membership functions in the individual
     */
    public Individual(int entradas) {
        antecedente = new MemFun[entradas];
        for(int i = 0; i < entradas; i++){
            antecedente[i] = new MemFun();
        }
        consecuente = new double[entradas+1];
        fitness = -1.0;
        n_e = 1;
        n_SistemasDifusos = -1;
    }

    /**
     * <p>       
     * Creates an individual as a copy of another individual
     * </p>       
     * @param indi Individual The individual used to create the new individual
     */
    public Individual(Individual indi) {
        int tam = indi.antecedente.length;
        antecedente = new MemFun[tam];
        for(int i = 0; i < tam; i++){
            antecedente[i] = new MemFun();
            antecedente[i].m = indi.antecedente[i].m;
            antecedente[i].sigma = indi.antecedente[i].sigma;
        }
        consecuente = new double[tam+1];
        for(int i = 0; i <= tam; i++){
            consecuente[i] = indi.consecuente[i];
        }

        fitness = indi.fitness;
        n_e = indi.n_e;
        n_SistemasDifusos = indi.n_SistemasDifusos;
    }

    /**
     * <p>
     * Compares the fitness value of two individuals
     * </p>
     * @return int Returns -1 if the the fitness of the first individual is lesser than the fitness of the second one.
     * 1 if the the fitness of the first individual is greater than the fitness of the second one.
     * 0 if both individuals have the same fitness value
     */
    public int compareTo(Object a) {
        if (((Individual) a).fitness < this.fitness) {
            return -1;
        }
        if (((Individual) a).fitness > this.fitness) {
            return 1;
        }
        return 0;
    }

}

