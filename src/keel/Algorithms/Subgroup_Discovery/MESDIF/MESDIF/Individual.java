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
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 30/06/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.MESDIF.MESDIF;

public abstract class Individual {

      public int tamano;
      public boolean evaluado;
      public boolean dominado;         // Individual dominated or not
      public QualityMeasures medidas;
      public boolean cubre[];          // Store if the invididual covers each example


    public Individual() {
    }


    /**
     * <p>
     * Returns if the example number "pos" is covered by this individual
     * </p>
     * @return    The example "pos" is covered or not by this individual
     */
    public boolean getIndivCubre (int pos) {
        return cubre[pos];
    }


    public abstract void InitIndRnd(TableVar Variables);
    public abstract void InitIndBsd(TableVar Variables, float porcVar);


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
     * Returns if the individual is or not dominated by other individuals
     * </p>
     * @return                  Dominated or not
     */
    public boolean getIndivDom () {
        return dominado;
    }

    /**
     * <p>
     * Sets the value for the domination value
     * </p>
     */
    public void setIndivDom (boolean val) {
        dominado = val;
    }


    public abstract void copyIndiv (Individual otro);
    public abstract boolean equalTo (Individual otro);


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
     * Returns the original support of the individual
     * </p>
     * @return                  Original supportof the individual
     */
    public float getIndivOSup () {
        return medidas.getOSup();
    }

    /**
     * <p>
     * Sets the original support of the individual
     * </p>
     * @param cd                Original suport for the individual
     */
    public void setIndivOSup (float cd) {
        medidas.setOSup(cd);
    }

 
    /**
     * <p>
     * Return the quality measures of the individual
     * </p>
     * @param AG                  Instance of the genetic algorithm
     * @return                  Quality measures of the individual
     */
    public QualityMeasures getMedidas (Genetic AG) {
        int numObj = medidas.getNObjectives();
        QualityMeasures res = new QualityMeasures(AG, numObj);
        res.copy(medidas);
        return res;
    }

    public abstract void setCromElem (int pos, int val);
    public abstract void setCromElemGene (int pos, int elem, int val);

    public abstract int getCromElem(int pos);
    public abstract int getCromElemGene(int pos, int elem);

    public abstract CromDNF getIndivCromDNF();
    public abstract CromCAN getIndivCromCAN();

    public abstract void evalInd (Genetic AG, TableVar Variables, TableDat Examples);


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

    
    /**
     * <p>
     * Computes if this individual is dominated by  other
     * </p>
     * NOTE: this function can not be used before the "original support" measures (if used) is computed
     * @param other                 Individual to compare with "this"
     * @return                      "This" is dominated by "other"
     */    
    public boolean dominated (Individual other) {
          boolean dom=false;       // To store if the individual is dominated by "other"
          int worseOrEqual=0;      // Counts the number of objectives <=
          boolean anyWorse=false;  // Stores if any of the objectives are <

          /* We supose the two individuals are evaluated */
          if (!this.evaluado || !other.evaluado)
              ;  // Indicate an error

          /* If all the objectives are worse or equal and at least one is strict worse, is dominated. */
          /* If all the objectives are equal, it is not dominated. */
          for (int i=1; i<=medidas.getNObjectives(); i++)
          {
              if (this.medidas.getValueObj(i)<=other.medidas.getValueObj(i))
                  worseOrEqual++;
              if (this.medidas.getValueObj(i)<other.medidas.getValueObj(i))
                  anyWorse=true;
          }
          if (worseOrEqual==this.medidas.getNObjectives() && anyWorse)
              dom=true;

          return (dom);
    }

    /**
     * <p>
     * Calculates if "this" individual dominates "other"
     * </p>
     * NOTE: this function can not be used before the "original support" measures (if used) is computed
     * @param other                 Individual to compare with "this"
     * @return                      "This" dominates "other"
     */
    public boolean dominate (Individual other) {
          boolean domina = false;    // To store if the individual dominates "other"
          int betterOrEqual=0;       // Counts the number of objectives <=
          boolean anyBetter=false;   // Stores if any of the objectives are <

          /* We supose the two individuals are evaluated */
          if (!this.evaluado || !other.evaluado)
              ;  // Indicate an error

          /* If all the objectives are better or equal and at least one is strict better, is dominates. */
          /* If all the objectives are equal, it does not dominate. */
          for (int i=1; i<=medidas.getNObjectives(); i++)
          {
              if (this.medidas.getValueObj(i)>=other.medidas.getValueObj(i))
                  betterOrEqual++;
              if (this.medidas.getValueObj(i)>other.medidas.getValueObj(i))
                  anyBetter=true;
          }
          if (betterOrEqual==this.medidas.getNObjectives() && anyBetter)
              domina=true;

          return (domina);
    }


    /**
     * <p>
     * Computes the distance between this individual and other
     * </p>
     * NOTE: this function can not be used before the "original support" measures (if used) is computed
     * @param other                 Individual to compare with "this"
     * @return                      Distance between "This" and "other"
     */
    public float calcDist (Individual other) {
          float dist=0, d_aux;

          /* We supose the two individuals are evaluated */
          if (!this.evaluado || !other.evaluado)
              ;  // Indicate an error

          for (int i=1; i<=medidas.getNObjectives(); i++)
          {
              d_aux = this.medidas.getValueObj(i) - other.medidas.getValueObj(i);
              dist+= d_aux*d_aux;
          }

          return dist;
    }


    public abstract void Print(String nFile);


}
