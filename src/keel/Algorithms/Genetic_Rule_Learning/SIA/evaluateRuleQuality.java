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

package keel.Algorithms.Genetic_Rule_Learning.SIA;

/**
 * <p>Title: Evaluation of the quality of the rules</p>
 * <p>Description: This class computes the final statistics </p>
 * @author Written by José Ramón Cano de Amo (University of Jaén) 08/04/2004
 * @author Modified by Alberto Fernández (University of Granada) 02/17/2005
 * @version 1.3
 * @since JDK1.4
 */
public class evaluateRuleQuality {

  private int nClases;

  private double aciertoTst;
  private double aciertoTrn;

  private ruleSet ruleSet;

  private double [][] bounds;
  private int [] lims;
  private int [] tipos;

  private String [] nombreClases;

  /**
   * Class Builder
   * @param conjreg Final rule set
   * @param numClases Number of classes of the problem
   * @param rangs Bounds array
   * @param limites Array with the length of the bounds array
   * @param tips Array with the types of the attributes
   * @param _nombreClases String [] names of the data-set classes
   */
  public evaluateRuleQuality(ruleSet conjreg, int numClases,double [][] rangs,int [] limites, int [] tips,
                             String [] _nombreClases) {

    ruleSet = conjreg;

    nClases = numClases;
    bounds = rangs;
    lims = limites;
    tipos = tips;

    nombreClases = _nombreClases;

  }

  /**
   * Function that computes the distance from the rule to the example (for classification)
   * @param r The rule
   * @param m The example
   * @return A distance measure (between 0 and 1) that determines what next is the rule to
   * the example (the less the value, the better the approximation)
   */
  private double calculaDistancia(Rule r, Instance m) {
    double d = 0, dist = 0;
    int n_de_r = 0;
    double atts[] = m.getMuest();
    for (int i = 0; i < m.getNattributes(); i++) {
      Condition c = r.getCondition(i);
      if (c.getType() == 0) {
        dist = 0;
      }
      else {
        n_de_r++; //There are one more condition that is not from type *
        if (Double.isNaN(atts[i])){
          dist = 1;
        }
        else{

          switch (tipos[i]) {
            case 3:
            case 4: //Real or integer
              double inf = c.getLowerBound();
              double sup = c.getUpperBound();
              if ( (atts[i] >= inf) && (atts[i] <= sup)) {
                dist = 0;
              }
              else {
                if (atts[i] < inf) {
                  dist = (atts[i] - inf) /
                      (bounds[i][0] - bounds[i][lims[i] - 1]);
                }
                else {
                  dist = (sup - atts[i]) /
                      (bounds[i][0] - bounds[i][lims[i] - 1]);
                }
              }
              if (dist > 1) { //Normalization
                dist = 1;
              }
              break;
            case 2:
              if (c.getValue() == atts[i]) {
                dist = 0;
              }
              else {
                dist = 1;
              }
              break;
          }
        }
      }
      dist *= dist; //dist^2
      d += dist; //sum(di^2)
    }
    d = Math.sqrt(d);
    d /= n_de_r;
    return d;
  }

  /**
   * It generates a string with the ouput list, &lt;expected output&gt; &lt;method output&gt;
   * @param datos Data-set
   * @param entrena 0 if it refer to the training set, 1 for test set
   * @return A string with a list of pair values  &lt;original class&gt; &lt;computed class&gt;
   */
  public String salida(myDataset datos,int entrena) {
    String cadena = new String("");
    int voto[] = new int[nClases];
    double [] distancias = new double[ruleSet.size()];
    int [] minimas  = new int[ruleSet.size()];
    int numero = 0;
    double dmin;
    double fmax;
    int j, cl, max,aciertos = 0;

    for (int i = 0; i < datos.size(); i++) {
      for (j = 0; j < nClases; j++) {
        voto[j] = 0;
      }
      for (j = 0; j < ruleSet.size(); j++) { // rules that verify the instance
        distancias[j] = calculaDistancia(ruleSet.getRule(j),datos.getData(i));
      }

      numero = 0;
      dmin = Double.MAX_VALUE;
      for (j = 0; j < ruleSet.size(); j++) {
        if (distancias[j] < dmin){
          dmin = distancias[j]; //Updates the minimum distance
          numero = 1; //Number of rules with d_min
          minimas[numero-1] = j; //Position of the rules
        }
        else{
          if (distancias[j] == dmin) {
            numero++;
            minimas[numero - 1] = j;
          }
        }
      }
      fmax = Double.MIN_VALUE;
      for (j = 0; j < numero; j++){
        if ((ruleSet.getRule(minimas[j])).getStrength() > fmax){
          fmax = (ruleSet.getRule(minimas[j])).getStrength();
          voto[ruleSet.getRule(minimas[j]).getClas()]++;
        }
      }
      for (j = 0, max = -1, cl = 0; j < nClases; j++) { //Obtains the class
        if (voto[j] > max) {
          max = voto[j];
          cl = j;
        }
      }
        cadena += new String(nombreClases[datos.getData(i).getClas()] + " " + nombreClases[cl] + "\n");
      if (cl == datos.getData(i).getClas()) {
        aciertos++;
      }
    }
    if (entrena == 0)
      aciertoTrn = (double) aciertos / datos.size();
    else
      aciertoTst = (double) aciertos / datos.size();
      return cadena;
  }


  /**
   * It returns a string with the accuracy percentage in training and test
   * @return a string with the accuracy percentage in training and test
   */
  public String printString(){
    String cad = "";
    cad += "@Accuracy in Training: "+aciertoTrn;
    cad += "\n@Accuracy in Test: "+aciertoTst;
    return cad;
  }

}

