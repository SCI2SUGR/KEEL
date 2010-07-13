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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.EARMGA;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import org.core.Fichero;

public class DataB {
  int n_variables, partitions;
  int[] nIntervals;
  Intervals [][] dataBase;

  public DataB() {
  }

  public DataB(int nIntervals, myDataset train) {
    double mark, value, rank, labels;
	double[][] ranks = train.getRanks();

	this.n_variables = train.getnVars();
	this.nIntervals = new int[this.n_variables];
    this.dataBase = new Intervals[this.n_variables][];

    for (int i = 0; i < this.n_variables; i++) {
	  rank = Math.abs(ranks[i][1] - ranks[i][0]);

	  if (train.isNominal(i))  this.nIntervals[i] = (int) rank + 1;
	  else  this.nIntervals[i] = nIntervals;

	  this.dataBase[i] = new Intervals[this.nIntervals[i]];

	  if (train.isNominal(i)) {
		  for (int j = 0; j < this.nIntervals[i]; j++) {
			  this.dataBase[i][j] = new Intervals();
			  value = ranks[i][0] + j;
			  this.dataBase[i][j].x0 = this.dataBase[i][j].x1 = value;
		  }
	  }
	  else {
		  mark = rank / (this.nIntervals[i]);
		  for (int j = 0; j < this.nIntervals[i]; j++) {
			  this.dataBase[i][j] = new Intervals();
			  value = ranks[i][0] + mark * j;
			  this.dataBase[i][j].x0 = this.setValue(value, ranks[i][0]);
			  value = ranks[i][0] + mark * (j + 1);
			  this.dataBase[i][j].x1 = this.setValue(value, ranks[i][1]);
		  }
	  }
    }
  }

  public int numVariables() {
    return (this.n_variables);
  }

  public int numIntervals(int variable) {
    return (this.nIntervals[variable]);
  }

  public int[] getnIntervals() {
    return (this.nIntervals);
  }

  public Intervals getInterval(int variable, int inter) {
    return (this.dataBase[variable][inter]);
  }

  public boolean isCovered (int variable, int interval, double value) {
    return (this.dataBase[variable][interval].isCovered(value));
  }

  private double setValue(double val, double tope) {
    if (val > -1E-4 && val < 1E-4)  return (0);
    if (val > tope - 1E-4 && val < tope + 1E-4)  return (tope);
    return (val);
  }
}

