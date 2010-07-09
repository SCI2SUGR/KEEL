package keel.Algorithms.Associative_Classification.ClassifierCPAR;

/**
 * <p>Title: RuleBase</p>
 *
 * <p>Description: This class contains the representation of a Rule Set</p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Jesus Alcala (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */

import java.util.*;
import org.core.*;

public class RuleBase {
	/**
	 * <p>
	 * This class contains the representation of a Rule Set
	 * </p>
	 */
  ArrayList<Rule> ruleBase;
  DataBase dataBase;
  myDataset train;
  int n_variables, nClasses, K;

  /**
   * <p>
   * Default Constructor
   * </p>
   */
  public RuleBase() {
  }

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param dataBase DataBase Set of training data which is necessary to generate a rule
   * @param train myDataset Training data set with information to construct the rule base (mainly, the training examples)
   * @param K int Number of best rules to use in prediction
   */
  public RuleBase(DataBase dataBase, myDataset train, int K) {
    this.dataBase = dataBase;
    this.train = train;
    this.K = K;
    this.n_variables = dataBase.numVariables();
	this.nClasses = train.getnClasses();
    this.ruleBase = new ArrayList<Rule>();
  }

  /**
   * <p>
   * It adds a rule to the rule base
   * </p>
   * @param rule Rule Rule to be added
   */
  public void add(Rule rule) {
	  this.ruleBase.add(rule);
  }

  /**
   * <p>
   * Function to get a rule from the rule base
   * </p>
   * @param pos int Position in the rule base where the desired rule is stored
   * @return Rule The desired rule
   */
  public Rule get(int pos) {
	  return (this.ruleBase.get(pos));
  }

  /**
   * <p>
   * It returns the number of rules in the rule base
   * </p>
   * @return int Rule base's size
   */
  public int size() {
	  return (this.ruleBase.size());
  }

  /**
   * <p>
   * Function to sort the rule base
   * </p>
   */
  public void sort () {
	  Collections.sort(this.ruleBase);
  }

  /**
   * <p>
   * It removes the rule stored in the given position
   * </p>
   * @param pos int Position where the rule we want to remove is
   * @return Rule Removed rule
   */
  public Rule remove(int pos) {
	  return (this.ruleBase.remove(pos));
  }

  /**
   * <p>
   * Function to evaluate the whole rule base by using the training dataset
   * </p>
   * @return double Fitness of the rule base
   */
  public double evaluate() {
    int nHits, Prediction;
	
	nHits = 0;
    for (int j = 0; j < train.size(); j++) {
      Prediction = this.FRM_WR(train.getExample(j));
      if (train.getOutputAsInteger(j) == Prediction)  nHits++;
    }

    return  ((100.0 * nHits) / (1.0 * this.train.size()));
  }

  /**
   * <p>
   * It returns the class which better fits to the given example
   * </p>
   * @param example int[] Example to be classified
   * @return int Output class
   */
  public int FRM(int[] example) {
    return FRM_WR(example);
  }

  private int FRM_WR(int[] example) {
	int i, j, count, clas;
	double bestSum, sum;
	Rule r;
	
	clas = -1;
	bestSum = -1;

	for (i = 0; i < this.nClasses; i++) {
		count = 0;
		sum = 0.0;
		for (j = 0; j < this.ruleBase.size() && count < this.K; j++) {
			r = this.ruleBase.get(j);
			if (r.getClas() == i) {
				if (r.matching(example) > 0.0) {
					count++;
					sum += r.getLaplace();
				}
			}
		}
		if (count > 0) {
			sum /= count;
			if ((clas < 0) || (bestSum < sum)) {
				clas = i;
				bestSum = sum;
			}
		}
	}

	return (clas);
  }

  /**
   * <p>
   * It prints the whole rulebase
   * </p>
   * @return String The whole rulebase
   */
  public String printString() {
    int i, j, ant;
    String [] names = train.names();
    String [] clases = train.clases();
    String stringOut = new String("");

	ant = 0;
    for (i = 0; i < this.ruleBase.size(); i++) {
      Rule r = this.ruleBase.get(i);
      stringOut += (i+1)+": ";
      for (j = 0; j < n_variables && r.antecedent[j] < 0; j++);
	  if (j < n_variables && r.antecedent[j] >= 0) {
		  stringOut += names[j]+" IS " + r.dataBase.print(j,r.antecedent[j]);
		  ant++;
	  }
      for (j++; j < n_variables-1; j++) {
		if (r.antecedent[j] >=0) {
			stringOut += " AND " + names[j]+" IS " + r.dataBase.print(j,r.antecedent[j]);
		    ant++;
		}
      }
      if (j < n_variables && r.antecedent[j] >= 0)  {
		  stringOut += " AND " + names[j]+" IS " + r.dataBase.print(j,r.antecedent[j]) + ": " + clases[r.clas] + "\n";
  		  ant++;
	  }
	  else  stringOut += ": " + clases[r.clas] + "\n";
    }

	stringOut += "\n\n";

    stringOut = "@Number of rules: " + (this.size() + 1) + " Number of Antecedents by rule: " + ant * 1.0 / this.size() + "\n\n" + stringOut;
	return (stringOut);
  }

  /**
   * <p>
   * It stores the rule base in a given file
   * </p>
   * @param filename String Name for the rulebase file
   */
  public void saveFile(String filename) {
    String stringOut = new String("");
    stringOut = printString();
    Fichero.escribeFichero(filename, stringOut);
  }

}
