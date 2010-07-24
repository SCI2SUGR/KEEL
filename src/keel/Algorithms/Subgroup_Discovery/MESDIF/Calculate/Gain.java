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
 * @author Written by Pedro González (University of Jaen) 15/08/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.MESDIF.Calculate;

public class Gain {
    /**
     * <p>
     * This class is defined to manage the information gain of each attributev of the dataset
     * </p>
     */


    /**
     * <p>
     * Computes and stores the info gain values
     * </p>
     **/
    public static void Init () {
        
        int i, j, h, v;
        boolean encontrado;
        float info_gk, suma, suma1, suma2, p_clase, logaritmo;
        int num_clase[] = new int[StCalculate.n_clases];
        float p[][] = new float[StCalculate.num_vars][StCalculate.MaxValores];
        float p_cond[][][] = new float [StCalculate.n_clases][StCalculate.num_vars][StCalculate.MaxValores];
        
        // Structure initialization
        for (i=0; i<StCalculate.n_clases; i++)
            num_clase[i] = 0;
        for (i=0; i<StCalculate.num_vars; i++)
            for (j=0; j<StCalculate.MaxValores; j++) {
                p[i][j] = 0;        // Simple probabilities matrix
                for (h=0; h<StCalculate.n_clases; h++)
                    p_cond[h][i][j]=0;     // Conditional probabilities matrix
            }
        
        for (i=0; i<StCalculate.n_eje; i++) {
            num_clase[StCalculate.tabla[i].clase]++;      // distribution by classes
            for (j=0; j<StCalculate.num_vars; j++) {      // distribution by values
                if (!StCalculate.var[j].continua)  {
                    // Discrete variable
                    if (!Calculate.getLost(i,j)) {
                        // if the value is not a lost one
                        p[j][(int)StCalculate.tabla[i].ejemplo[j]]++;
                        p_cond[(int)StCalculate.tabla[i].clase][j][(int)StCalculate.tabla[i].ejemplo[j]]++;
                    }
                }
                else {
                    // Continuous variable
                    encontrado = false;
                    h = 0;
                    while (!encontrado && h<StCalculate.var[j].n_etiq) {
                        if (StCalculate.tabla[i].ejemplo[j]<=StCalculate.intervalos[j][h])
                            encontrado = true;
                        else
                            h++;
                    }
                    if (encontrado == true) {
                        p[j][h]++;
                        p_cond[(int)StCalculate.tabla[i].clase][j][h]++;
                    }
                    else {
                      if (!Calculate.getLost(i,j)) {
                          // Lost value 
                          System.out.println("Fallo al calcular la ganancia de infor, Variable " + j + " Ejemplo " + i);
                          return;  
                      }
                    }
                }
            }
        }
        
        for (h=0; h<StCalculate.n_clases; h++)
            for (i=0; i<StCalculate.num_vars; i++) {
                if (!StCalculate.var[i].continua)
                    // Discrete variable 
                    for (j=(int)StCalculate.var[i].min; j<=(int)StCalculate.var[i].max; j++)
                        p_cond[h][i][j] = p_cond[h][i][j] / StCalculate.n_eje;
                else // Continuous variable
                    for (j=0; j<StCalculate.var[i].n_etiq; j++)
                        p_cond[h][i][j] = p_cond[h][i][j] / StCalculate.n_eje;
            }
        for (i=0; i<StCalculate.num_vars; i++) {
            if (!StCalculate.var[i].continua)  // Discrete variable
                for (j=(int)StCalculate.var[i].min; j<=(int)StCalculate.var[i].max; j++)
                    p[i][j] = p[i][j] / StCalculate.n_eje;
            else  // Continuous variable
                for (j=0; j<StCalculate.var[i].n_etiq; j++)
                    p[i][j] = p[i][j] / StCalculate.n_eje;
        }
        
        // Info Gk computation
        suma = 0;
        for (i=0; i<StCalculate.n_clases; i++) {
            p_clase = ((float)num_clase[i])/StCalculate.n_eje;
            if (p_clase>0) {
                logaritmo = (float)(Math.log((double)p_clase)/Math.log(2));
                suma += p_clase * logaritmo;
            }
        } 
        info_gk = (-1) * suma;   

        // Information gain computation for each attibute
        for (v=0; v<StCalculate.num_vars; v++) {
            suma = info_gk;
            suma1 = 0;
            if (!StCalculate.var[v].continua) {   // Discrete variable
                for (i=(int)StCalculate.var[v].min; i<=(int)StCalculate.var[v].max; i++) {
                    suma2=0;
                    for (j=0; j<StCalculate.n_clases; j++)
                        if (p_cond[j][v][i]>0){
                            logaritmo = (float) (Math.log(p_cond[j][v][i])/Math.log(2));
                            suma2+= p_cond[j][v][i]*logaritmo;
                        }
                    suma1+=p[v][i]*(-1)*suma2;
                }
            }
            else {      // Continuous variable
                for (i=0; i<StCalculate.var[v].n_etiq; i++) {
                    suma2=0;
                    for (j=0; j<StCalculate.n_clases; j++)
                        if (p_cond[j][v][i]>0) {
                            logaritmo = (float)(Math.log(p_cond[j][v][i])/Math.log(2));
                            suma2+= p_cond[j][v][i]*logaritmo ;
                        }
                    suma1+=p[v][i]*(-1)*suma2;
                }
            }
            StCalculate.GI[v] = suma + (-1)*suma1;
        }

    }


    

}
