package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Fingrams;

import java.util.*;
import org.core.*;

/**
 * <p>Title: DataBase</p>
 * <p>Description: Fuzzy Data Base</p>
 * <p>Copyright: Copyright KEEL (c) 2008</p>
 * <p>Company: KEEL </p>
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2011
 * @version 1.0
 * @since JDK1.6
 */

public class DataBase {
  int n_variables, partitions;
  int[] nLabels;
  boolean[] varReal;
  Fuzzy[][] dataBase;
  String names[];

    /**
     * Default constructor.
     */
    public DataBase() {
  }

/**
* <p>
* This method builds the database, creating the initial linguistic partitions
* </p>
* @param dataBaseFile Database file.
* @param train Training dataset
*/

  public DataBase(String dataBaseFile, myDataset train) {
    double rank, labels;
	double[][] ranks = train.returnRanks();

	this.n_variables = train.getnVars();
	this.names = (train.names()).clone();
	this.nLabels = new int[this.n_variables];
	this.varReal = new boolean[this.n_variables];
    this.dataBase = new Fuzzy[this.n_variables][];
	StringTokenizer data, lines;
	String dataBaseIni;

	dataBaseIni = Files.readFile(dataBaseFile);
    lines = new StringTokenizer(dataBaseIni, "\n\r");
	lines.nextToken();
	lines.nextToken();

    for (int i = 0; i < this.n_variables; i++) {
	  rank = Math.abs(ranks[i][1] - ranks[i][0]);  
	  if (train.isNominal(i)) {
		  lines.nextToken();
		  this.varReal[i] = false;
		  this.nLabels[i] = ((int) rank) + 1;
		  this.dataBase[i] = new Fuzzy[this.nLabels[i]];
		  for (int j = 0; j < this.nLabels[i]; j++) {
			  lines.nextToken();
			  this.dataBase[i][j] = new Fuzzy();
			  this.dataBase[i][j].name = new String(train.nameNominal(i, j));
			  this.dataBase[i][j].x0 = ranks[i][0] + (j - 1);
			  this.dataBase[i][j].x1 = ranks[i][0] + j;
			  this.dataBase[i][j].x3 = ranks[i][0] + (j + 1);
			  this.dataBase[i][j].y = 1.0;
		  }
		  lines.nextToken();
	  }
	  else {
		  this.varReal[i] = true;
          data = new StringTokenizer(lines.nextToken(), " = \" ");
		  data.nextToken(); data.nextToken(); data.nextToken(); data.nextToken();
		  this.nLabels[i] = Integer.parseInt(data.nextToken());
		  this.dataBase[i] = new Fuzzy[this.nLabels[i]];
		  for (int j = 0; j < this.nLabels[i]; j++) {
			  this.dataBase[i][j] = new Fuzzy();
			  data = new StringTokenizer(lines.nextToken(), " = \" ");
			  data.nextToken();
			  this.dataBase[i][j].name = new String(data.nextToken());
			  this.dataBase[i][j].x0 = Double.parseDouble(data.nextToken());
			  this.dataBase[i][j].x1 = Double.parseDouble(data.nextToken());
			  this.dataBase[i][j].x3 = Double.parseDouble(data.nextToken());
			  this.dataBase[i][j].y = 1.0;
		  }
		  lines.nextToken();
	  }
    }
  }

    /**
     * Returns the number of variables.
     * @return the number of variables.
     */
    public int numVariables() {
    return (this.n_variables);
  }

    /**
     * Returns the number of real labels
     * @return the number of real labels
     */
    public int getnLabelsReal() {
	  int i, count;

	  count = 0;

	  for (i=0; i < n_variables; i++) {
		  if (varReal[i])  count += this.nLabels[i];
	  }

	  return (count);
  }

    /**
     * Returns the number of labels of a given variable.
     * @param variable variable id.
     * @return the number of labels of a given variable.
     */
    public int numLabels(int variable) {
    return (this.nLabels[variable]);
  }

    /**
     * Returns the number of labels of each variable.
     * @return array with the number of labels of each variable.
     */
    public int[] getnLabels() {
    return (this.nLabels);
  }

    /**
     * Returns the position of a given value of a given variable.
     * @param variable given variable id.
     * @param value value asked.
     * @return the position of a given value of a given variable.
     */
    public int posValue (int variable, String value){
	  int i;
	  for (i=0; i < this.nLabels[variable]; i++) {
		  if (this.dataBase[variable][i].name.equalsIgnoreCase(value))  return (i);
      }
	  return (-1);
  }

    /**
     * Returns the fuzzied value for given variable, label and value.
     * @param variable given variable.
     * @param label given label.
     * @param value given value.
     * @return the fuzzied value for given variable, label and value.
     */
    public double matching(int variable, int label, double value) {
	if ((variable < 0) || (label < 0))  return (1);  // Don't care
    else  return (this.dataBase[variable][label].Fuzzifica(value));
  }

    /**
     * Returns the name of a given label id of a given variable id.
     * @param var given label id 
     * @param label given variable id.
     * @return  the name of a given label id of a given variable id.
     */
    public String print(int var, int label) {
	return (this.dataBase[var][label].getName());
  }
}
