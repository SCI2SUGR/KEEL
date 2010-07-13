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
 * @author Written by Antonio Alejandro Tortosa (University of Granada) 01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 12/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.C45Rules;



class Report {
/** 
 * <p>
 * Auxiliar class used for compact information in the serching method of
 *  the class C4.5Rules
 *  </p>
 */
	
  //it stores a combination (without repetitions) of positive numbers
  private int[] card; 
  //the length of card
  private int length; 
  //the value of a metric that evaluates the combination in card
  private double value; 

  /**
   * Constructor.
   * @param card the combination (without repetitions)
   * @param length the card's length
   * @param value the value of the combination according some metric
   */
  public Report(int[] card,int length,double value){
    this.length=length;
    this.card=new int[card.length];
    for (int i=0;i<length;i++)
      this.card[i]=card[i];
    this.value=value;
  }

  /**
   * Return a positition of the combination.
   * @param i the position
   * @return the i-th positition of the combination.
   */
  public int get(int i){return card[i];}

  /**
   * Returns the combination.
   * @return the combination.
   */
  public int[] getCard(){return card;}

  /**
   * Returns the length of the combination.
   * @return the length of the combination.
   */
  public int length(){return length;}

  /**
   * Returns the value of the combination.
   * @return the value of the combination.
   */
  public double getValue(){return value;}

  /**
   * Sets all the parameters of the report.
   * @param card the combination (without repetitions)
   * @param length the card's length
   * @param value the value of the combination according some metric
   */
  public void set(int[] card,int length,double value){
    for (int i=0;i<length;i++)
      this.card[i]=card[i];
    this.length=length;
    this.value=value;
  }

  /**
   * Returns the string representation of this report.
   * @return the string representation of this report.
   */
  public String toString(){
    String output="{";

    for (int i=0;i<length;i++){
      output+=card[i]+",";
    }

    output+="} "+"value: "+value;

    return output;
  }

}
