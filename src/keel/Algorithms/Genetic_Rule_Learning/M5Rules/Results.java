package keel.Algorithms.Genetic_Rule_Learning.M5Rules;

/**
 * Class for containing the evaluation results of a model
 */
public final class Results{
    int numItemsets; // number of total instances
    int missingItemsets; // number of instances with missing class values
    double sumErr; // sum of errors
    double sumAbsErr; // sum of the absolute errors
    double sumSqrErr; // sum of the squared errors
    double meanSqrErr; // mean squared error
    double rootMeanSqrErr; // sqaure root of the mean squared error
    double meanAbsErr; // mean absolute error

    /**
     * Constructs an object which could contain the evaluation results of a model
     * @param first the index of the first instance
     * @param last the index of the last instance
     */
    public Results(int first, int last) {
        numItemsets = last - first + 1;
        missingItemsets = 0;
        sumErr = 0.0;
        sumAbsErr = 0.0;
        sumSqrErr = 0.0;
        meanSqrErr = 0.0;
        rootMeanSqrErr = 0.0;
        meanAbsErr = 0.0;
    }

    /**
     * Makes a copy of the Errors object
     * @return the copy
     */
    public final Results copy() {

        Results e = new Results(0, 0);

        e.numItemsets = numItemsets;
        e.missingItemsets = missingItemsets;
        e.sumErr = sumErr;
        e.sumAbsErr = sumAbsErr;
        e.sumSqrErr = sumSqrErr;
        e.meanSqrErr = meanSqrErr;
        e.rootMeanSqrErr = rootMeanSqrErr;
        e.meanAbsErr = meanAbsErr;

        return e;
    }

    /**
     * Converts the evaluation results of a model to a string
     * @return the converted string
     */
    public final String toString() {

        StringBuffer text = new StringBuffer();

        if (this == null) {
            text.append("    Errors:\t\tnull\n");
        } else {
            text.append("    Number of instances:\t" + numItemsets + " (" +
                        missingItemsets + " missing)\n");
            text.append("    Sum of errors:\t\t" + sumErr + "\n");
            text.append("    Sum of absolute errors:\t" + sumAbsErr + "\n");
            text.append("    Sum of squared errors:\t" + sumSqrErr + "\n");
            text.append("    Mean squared error:\t\t" + meanSqrErr + "\n");
            text.append("    Root mean squared error:\t" + rootMeanSqrErr +
                        "\n");
            text.append("    Mean absolute error:\t" + meanAbsErr + "\n");
        }

        return text.toString();
    }

}

