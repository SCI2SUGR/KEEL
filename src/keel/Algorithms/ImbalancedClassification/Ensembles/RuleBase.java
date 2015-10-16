/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. SÃ¡nchez (luciano@uniovi.es)
    J. AlcalÃ¡-Fdez (jalcala@decsai.ugr.es)
    S. GarcÃ­a (sglopez@ujaen.es)
    A. FernÃ¡ndez (alberto.fernandez@ujaen.es)
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

package keel.Algorithms.ImbalancedClassification.Ensembles;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * <p>Title: RuleBase</p>
 * <p>Description: A full rule set description
 * <p>Company: KEEL </p>
 * @author Alberto Fernandez (University of Jaen) 11/10/2012
 * @version 1.1
 * @since JDK1.6
 */
public class RuleBase {

  ArrayList<Rule> ruleBase;
  myDataset train;

  public RuleBase() {
    ruleBase = new ArrayList<Rule> ();
  }

  /**
   * To obtain the rule Base from the rule file (extracted from the C4.5 decision tree)
   * @param Rules String full rule set in "text mode"
   * @param train myDataset training set
   */
  public RuleBase(myDataset train, String Rules) {
    ruleBase = new ArrayList<Rule> ();
    this.train = train;
    StringTokenizer tokens = new StringTokenizer(Rules, "\n");
    while (tokens.hasMoreTokens()) {
      String Rule = tokens.nextToken();
      Rule r = new Rule(train, Rule);
      ruleBase.add(r);
    }
  }

  public String printString() {
    String cadena = new String("");
    cadena += "Number of Rules: " + ruleBase.size() + "\n";
    for (int i = 0; i < ruleBase.size(); i++) {
      cadena += "Rule[" + (i + 1) + "]: " + ruleBase.get(i).printString();
    }
    return cadena;
  }

  public String printStringF() {
    String cadena = new String("");
    cadena += "Number of Rules: " + ruleBase.size() + "\n";
    for (int i = 0; i < ruleBase.size(); i++) {
      cadena += "Rule[" + (i + 1) + "]: " + ruleBase.get(i).printStringF();
    }
    return cadena;
  }

  public int size() {
    return ruleBase.size();
  }

  /**
   * It detects those Rule tha cover an small-disjunt
   */
  public void coverExamples() {
    for (int i = 0; i < this.size(); i++) {
      ruleBase.get(i).coverExamples();
    }
  }

  /**
   * It computes how many examples are covered, and the weights of these examples
   * @param weights the weights of these examples
   */
  public void coverExamples(double weights[]) {
    for (int i = 0; i < this.size(); i++) {
      ruleBase.get(i).coverExamples(weights);
    }
     train = null;
  }

}
