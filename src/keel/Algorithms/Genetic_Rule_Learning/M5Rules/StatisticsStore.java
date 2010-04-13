package keel.Algorithms.Genetic_Rule_Learning.M5Rules;

/**
 * Stores some statistics.
 */
public final class StatisticsStore {
    int numInstances; // number of the itemsets
    int missingInstances; // number of the itemsets with missing values
    int first; // index of the first itemset
    int last; // index of the last itemset
    int attr; // attribute
    double sum; // sum of the itemsets for attribute
    double sqrSum; // squared sum of the itemsets for attribute
    double va; // variance
    double sd; // standard deviation


    /**
     * Constructs an object which stores some statistics of the itemsets such
     *      as sum, squared sum, variance, standard deviation
     * @param low the index of the first itemset
     * @param high the index of the last itemset
     * @param attribute the attribute
     * @param inst the itemsets
     */

    public StatisticsStore(int low, int high, int attribute, MyDataset inst) {
      int i, count = 0;
      double value;

      numInstances = high - low + 1;
      missingInstances = 0;
      first = low;
      last = high;
      attr = attribute;
      sum = 0.0;
      sqrSum = 0.0;
      for (i = first; i <= last; i++) {
        if (inst.itemset(i).isMissing(attr) == false) {
          count++;
          value = inst.itemset(i).getValue(attr);
          sum += value;
          sqrSum += value * value;
        }
      }
      if (count > 1) {
        va = (sqrSum - sum * sum / count) / count;
        va = Math.abs(va);
        sd = Math.sqrt(va);
      } else {
        va = 0.0;
        sd = 0.0;
      }
    }

    /**
     * Converts the stats to a string
     * @return the converted string
     */
    public final String toString() {

        StringBuffer text = new StringBuffer();

        text.append("Print statistic values of itemsets (" + first + "-" +
                    last +
                    "\n");
        text.append("    Number of itemsets:\t" + numInstances + "\n");
        text.append("    NUmber of itemsets with unknowns:\t" +
                    missingInstances +
                    "\n");
        text.append("    Attribute:\t\t\t:" + attr + "\n");
        text.append("    Sum:\t\t\t" + sum + "\n");
        text.append("    Squared sum:\t\t" + sqrSum + "\n");
        text.append("    Stanard Deviation:\t\t" + sd + "\n");

        return text.toString();
    }


}

