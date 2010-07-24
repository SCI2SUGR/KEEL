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
 * @author Writed by Pedro González (University of Jaen) 15/02/2004
 * @author Modified by Pedro González (University of Jaen) 4/08/2007
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 20/04/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDIGA.SDIGA;

public abstract class Individual {

      public int tamano;
      public boolean evaluado;
      public QualityMeasures medidas;

    public Individual() {

    }

    public abstract void RndInitInd(TableVar Variables);

    /**
     * <p>
     * Returns if the individual has been evaluated
     * </p>
     * @return                  Value of the example
     */
    public boolean getIndivEvaluated () {
        return evaluado;
    }

    /**
     * <p>
     * Sets that the individual has been evaluated
     * </p>
     * @param val               Value of the state of the individual
     */
    public void setIndivEvaluated (boolean val) {
        evaluado = val;
    }

    /**
     * <p>
     * Returns the fitness of the individual
     * </p>
     * @return                  Fitness of the individual
     */
    public float getIndivFitness () {
        return medidas.getFitness();
    }

    /**
     * <p>
     * Sets the Fitness of the individual
     * </p>
     * @param cd                Fitness for the individual
     */
    public void setIndivFitness (float cd) {
        medidas.setFitness(cd);
    }

 
    /**
     * <p>
     * Return the quality measure of the individual
     * </p>
     * @return                  Quality measures of the individual
     */
    public QualityMeasures getMedidas () {
        QualityMeasures res = new QualityMeasures();
        res.copy(medidas);
        return res;
    }

    public abstract void setCromElem (int pos, int val);
    public abstract void setCromElemGene (int pos, int elem, int val);

    public abstract int getCromElem(int pos);
    public abstract int getCromElemGene(int pos, int elem);

    public abstract CromDNF getIndivCromDNF();
    public abstract CromCAN getIndivCromCAN();

    public abstract void evalInd (Genetic AG, TableVar Variables, TableDat Examples, boolean marcar);

    /**
     * <p>
     * Returns the number of the interval of the indicated variable to which belongs
     * the value. It is performed seeking the greater belonging degree of the
     * value to the fuzzy sets defined for the variable
     * </p>
     * @param valor                 Value to calculate
     * @param num_var               Number of the variable
     * @param Variables             Variables structure
     * @return                      Number of the interval
     */
    public int NumInterv (float valor, int num_var, TableVar Variables){
        float pertenencia=0, new_pert=0;
        int interv = -1;

        for (int i=0; i<Variables.getNLabelVar(num_var); i++) {
            new_pert = Variables.Fuzzy(num_var, i, valor);
            if (new_pert>pertenencia) {
                interv = i;
                pertenencia = new_pert;
            }
        }
        return interv;
    }

    public abstract void Print(String nFile);


}
