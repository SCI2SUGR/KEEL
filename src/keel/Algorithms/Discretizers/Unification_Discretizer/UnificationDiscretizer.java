/**
 * <p>
 * File: UnificationDiscretizer.java
 * 
 * This is the class with the operations of the Unification Formula discretization. It 
 * adopts the behavior of the general discretizers and specifies its differences in this 
 * class, that has to extend the abstract methods.
 * 
 * @author Written by Victoria Lopez Morales (University of Granada) 04/01/2010 
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Discretizers.Unification_Discretizer;

import java.util.*;

import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;

public class UnificationDiscretizer extends Discretizer {
/**
 * <p>
 * This class implements the discretization unificacion dynamic programming approach.
 * </p>
 */	
    
    /**
     * <p>
     * Selects, for a given attribute, the real values that best discretize the attribute
     * according to the discretization unificacion dynamic programming approach
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
     * @param values    Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
     * @param begin First value that is considered to belong to the data considered, usually 0
     * @param end   Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
     * @return a vector with the real values that best discretize the attribute given according to 
     * the discretization unificacion dynamic programming approach
     */
    protected Vector discretizeAttribute(int attribute, int []values, int begin, int end) {
        Vector discretization = new Vector();
        Vector opt_result, intervalCut;
        HashMap opt_computations = new HashMap();

        Vector posAllCutPoints = getCandidateCutPoints(attribute, values, begin, end);
        if (posAllCutPoints.size() == 0) return discretization;
        Vector contingencyTable = computeContingencyTable (attribute, values, begin, end, posAllCutPoints);
        
        // Search for the best discretization
        opt_result = opt (0, contingencyTable.size()-2, contingencyTable, opt_computations);
        
        // Obtain the position of the cutpoints
        intervalCut = new Vector ();
        for (int i=0, size = opt_result.size()-1; i<size; i++) {
            intervalCut.add((Integer)posAllCutPoints.elementAt(((Integer)opt_result.elementAt(i)).intValue()));
        }
        Collections.sort(intervalCut.subList(0,intervalCut.size()));
        
        // Obtain the associated cutpoints to that position
        for (int i=0; i<intervalCut.size(); i++) {
            int posNewCutPoint = ((Integer)intervalCut.elementAt(i)).intValue();
            discretization.addElement(new Double ((realValues[attribute][values[posNewCutPoint-1]]+realValues[attribute][values[posNewCutPoint]])/2.0));
        }
        
        // Return the selected discretization
        return discretization;
    }

    /**
     * <p>
     * Computes, for the given discretization, the minimization function F proposed equivalent to
     * maximizing GF. This function is only used during development and debug of the algorithm.
     * </p>
     * @param posCutPoints  Discretization proposed for the attribute containing the position of the 
     * selected cut points
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
     * @param values    Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
     * @param begin First value that is considered to belong to the data considered, usually 0
     * @param end   Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
     * @return  the value of the F function for this discretization
     */
    private double F (Vector posCutPoints, int attribute, int []values, int begin, int end) {
        Vector ct = computeContingencyTable (attribute, values, begin, end, posCutPoints); 
        double sum_product = 0;
        double partial_product, result;
        int N = ((Integer)((Vector)ct.get(ct.size()-1)).elementAt(Parameters.numClasses)).intValue();
        
        // Compute the Ni x generalized entropy sum
        for (int i=0, size = ct.size()-1; i<size; i++) {
            partial_product = (double)((Integer)((Vector)ct.get(i)).elementAt(Parameters.numClasses)).intValue() * generalizedEntropy (ct, i);
            sum_product += partial_product;
        }
        
        // Weight it with the other params
        result = Parameters.alpha * (ct.size()-2) * (Parameters.numClasses-1) * (1-Math.pow((double)1/(double)N, Parameters.beta)) / Parameters.beta;
        result += sum_product;
        
        return result;
    }
    
    /**
     * <p>
     * Computes, for a pair of given values, the minimum of the F function from the partial
     * contingency table from a to b, following a dynamic programming approach.
     * </p>
     * @param a Lower bound of the partial contingency table
     * @param b Upper bound of the partial contingency table
     * @param ct    Whole contingency table used in the computation of the F function
     * @param opt_computations  HashMap that stores previous opt function values computed, which
     * is used to avoid computing again which was computed previously
     * @return  the minimum value of the F function in a table from a to b, and with the intervals
     * in the contingency table that obtains that minimum value
     */
    private Vector opt (int a, int b, Vector ct, HashMap opt_computations) {
        Vector result = new Vector();;
        Vector result2;
        
        // First, we check if we can compute the sub-contingency table
        if (b < a) {
            System.err.println("The sub-contingency table is not valid");
            System.exit(-1);
        }
        
        // Then, we check if the item was previously computed to avoid innecessary computation
        result2 = (Vector)opt_computations.get(new Integer(a*ct.size()+b));
        if (result2 != null) {
            return result2;
        }
        
        // After that, we check if we are in the base case
        if (b == a) {
            result.add(new Double (FC0 (a, b, ct)));
            return result;
        }
        
        double optji = Double.MAX_VALUE;
        // Finally, we apply the algorithm for the discretization
        for (int i=a; i<=b; i++) {
            for (int j=i; j>=a; j--) {
                // opt value supposing that the interval is merged
                optji = FC0 (j, i, ct);
                result = new Vector ();
                
                for (int k=j, top=i-1; k<=top; k++) {
                    // Check opt values supposing that the interval is split in k
                    result2 = new Vector();
                    int N = ((Integer)((Vector)ct.get(ct.size()-1)).elementAt(Parameters.numClasses)).intValue();
                    
                    Vector result_optjk = opt (j, k, ct, opt_computations);
                    Vector result_optk1i = opt (k+1, i, ct, opt_computations);
                    double newopt = ((Double)result_optjk.elementAt(result_optjk.size()-1)).doubleValue() + ((Double)result_optk1i.elementAt(result_optk1i.size()-1)).doubleValue() + (Parameters.alpha*(Parameters.numClasses-1)*(1-Math.pow((double)1/(double)N,Parameters.beta))/Parameters.beta);
                    
                    if (newopt < optji) {
                        // If the split is the best option, add the split history to the result
                        for (int v=0, size = result_optjk.size()-1; v<size; v++) {
                            Integer value = (Integer)result_optjk.elementAt(v);
                            if (!result2.contains(value)) {
                                result2.addElement(value);
                            }
                        }
                        for (int v=0, size = result_optk1i.size()-1; v<size; v++) {
                            Integer value = (Integer)result_optk1i.elementAt(v);
                            if (!result2.contains(value)) {
                                result2.addElement(value);
                            }
                        }
                        
                        // And add this split value to the history
                        if (!result2.contains(new Integer(k))) {
                            result2.addElement(new Integer(k));
                        }
                        
                        // Update the minimum found
                        result = result2;
                        optji = newopt;
                    }
                }
            }
        }
        
        // Add the function value to the result
        result.add(new Double (optji));
        
        // Add to the hash-map to speed up the computation
        opt_computations.put(new Integer(a*ct.size()+b), result); 
        
        return result;
    }
    
    /**
     * <p>
     * Computes the F function value for the merged column sum for the subcontingency table
     * from i to ik
     * </p>
     * @param i Lower bound of the partial contingency table
     * @param ik    Upper bound of the partial contingency table
     * @param ct    Whole contingency table used in the computation of the F function
     * @return  the value of the F function for this subcontingency table
     */
    private double FC0 (int i, int ik, Vector ct) {
        int Nr = 0;
        double Hb;
        double functionValue;
        
        // First, we check if we can compute the sub-contingency table
        if (ik < i) {
            System.err.println("The sub-contingency table is not valid");
            System.exit(-1);
        }
        
        // Compute the Nr accumulated value
        for (int r=i; r<=ik; r++) {
            Nr += ((Integer)((Vector)ct.get(r)).elementAt(Parameters.numClasses)).intValue();
        }
        
        // Compute the generalized entropy for the sub-contingency table
        Hb = generalizedEntropyUnion (i, ik, ct);
        
        functionValue = (double)Nr * Hb;

        return functionValue;
    }
    
    /**
     * <p>
     * Computes the generalized entropy for a given interval i
     * </p>
     * @param ct    Whole contingency table used in the computation of the generalized entropy
     * @param posInterval   Interval to which we are computing the generalized entropy
     * @return the generalized entropy value for the interval given
     */
    private double generalizedEntropy (Vector ct, int posInterval) {
        double sum = 0;
        double fraction, aux;
        
        // Compute Ni
        int Ni = ((Integer)((Vector)ct.get(posInterval)).elementAt(Parameters.numClasses)).intValue();
            
        for (int j=0; j<Parameters.numClasses; j++) {
            // Compute cij and apply the entropy formula
            int cij = ((Integer)((Vector)ct.get(posInterval)).elementAt(j)).intValue();
            fraction = (double)cij/(double)Ni;
            
            aux = fraction * (1-Math.pow(fraction, Parameters.beta)) / Parameters.beta;
            
            sum += aux;
        }
        
        return sum;
    }
    
    /**
     * <p>
     * Computes the generalized entropy union for a given interval
     * </p>
     * @param a Lower bound of the given interval
     * @param b Upper bound of the given interval
     * @param ct    Whole contingency table used in the computation of the generalized entropy
     * @return the generalized entropy union value for the interval given
     */
    private double generalizedEntropyUnion (int begin, int end, Vector ct) {
        double entropy = 0;
        
        // Compute the Mj and N values for this partition
        Vector Mj = (Vector)((Vector)ct.get(begin)).clone();
        for (int i=begin+1; i<=end; i++) {
            Vector aux = (Vector)ct.get(i);
            for (int j=0; j<aux.size(); j++) {
                int value = ((Integer)aux.elementAt(j)).intValue();
                int acc_value = ((Integer)Mj.elementAt(j)).intValue();
                Mj.set(j, new Integer(value+acc_value)); 
            }
        }
        int N = ((Integer)Mj.get(Mj.size()-1)).intValue();
        
        // Compute the entropy according to the formula
        for (int j=0; j<Parameters.numClasses; j++) {
            int Mjvalue = ((Integer)Mj.get(j)).intValue();
            
            double MjN = (double)Mjvalue/(double)N;
            
            entropy = entropy + (MjN * (1-Math.pow(MjN, Parameters.beta))/Parameters.beta);
        }
        
        return entropy;
    }
    
    /**
     * <p>
     * Computes the contingency table of the data given a current discretization 
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
     * @param values    Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
     * @param begin First value that is considered to belong to the data considered, usually 0
     * @param end   Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
     * @param allCutPoints  Discretization proposed for the attribute containing the position of the 
     * selected cut points
     * @return  the contingency table of the data given a current discretization
     */
    private Vector computeContingencyTable (int attribute, int []values, int begin, int end, Vector allCutPoints) {
        Vector cd, dd, jcd, ct, aux_ct;
        
        ct = new Vector();
        
        // Compute the joint class distribution and the derived distributions
        jcd = jointClassDistribution (attribute, values, begin, end, allCutPoints);
        cd = classDistribution (jcd);
        dd = discretizationDistribution (jcd);
        
        // Build the table from those distributions
        for (int i=0, size = dd.size(); i<size; i++) {
            aux_ct = new Vector();
            
            for (int j=0, size2 = cd.size(); j<size2; j++) {
                aux_ct.addElement(new Integer((Integer)jcd.elementAt(i*Parameters.numClasses+j)).intValue());
            }
            aux_ct.addElement(new Integer(((Integer)dd.elementAt(i)).intValue()));
        
            ct.add(aux_ct);
        }
        cd.addElement(new Integer(sumValues(cd)));
        ct.add(cd);
        
        return ct;
    }
    
    /**
     * <p>
     * Adds up the integer values stored in a vector
     * </p>
     * @param v Vector whose integer values are going to be added
     * @return sum of the addition of all integer values in the vector
     */
    private int sumValues(Vector v) {
        int sum=0;
        for(int i=0,size=v.size();i<size;i++) {
            sum+=((Integer)v.elementAt(i)).intValue();
        }
        return sum;
    }

    /**
     * <p>
     * Obtains a vector of all the possible cut points for the attribute
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
     * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
     * @param begin    First value that is considered to belong to the data considered, usually 0
     * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
     * @return a vector with all the possible cut points for the attribute
     */
    private Vector getCandidateCutPoints(int attribute,int []values,int begin,int end) {
        Vector cutPoints = new Vector();
        double valueAnt=realValues[attribute][values[begin]];
     
        // Add all the values different from its previous value
        for(int i=begin;i<=end;i++) {
            double val=realValues[attribute][values[i]];
            if(val!=valueAnt) cutPoints.addElement(new Integer(i));
            valueAnt=val;
        }
        return cutPoints;
    }

    /**
     * <p>
     * Obtains the class distribution of the data
     * </p>
     * @param jointClassDistribution    A joint distribution depending on a discretization and the 
     * class data which is the base to build the class distribution
     * @return the class distribution of the data
     */
    private Vector classDistribution (Vector jointClassDistribution) {
        Vector cd = new Vector();
        int count;
        
        for (int i=0; i<Parameters.numClasses; i++) {
            count = 0;
            for (int j=0, size=jointClassDistribution.size()/Parameters.numClasses; j<size; j++) {
                count += ((Integer)jointClassDistribution.elementAt(Parameters.numClasses*j+i)).intValue();
            }
            cd.addElement(count);
        }
        return cd;
    }
    
    /**
     * <p>
     * Obtains the distribution of the data given conditioned by a discretization
     * </p>
     * @param jointClassDistribution    A joint distribution depending on a discretization and the
     * class data which is the base to build the discretization distribution
     * @return the distribution of the data conditioned by a discretization
     */
    private Vector discretizationDistribution (Vector jointClassDistribution) {
        Vector cd = new Vector();
        int count;
        
        for (int i=0, size=jointClassDistribution.size()/Parameters.numClasses; i<size; i++) {
            count = 0;
            for (int j=0; j<Parameters.numClasses; j++) {
                count += ((Integer)jointClassDistribution.elementAt(Parameters.numClasses*i+j)).intValue();
            }
            cd.addElement(count);
        }
        return cd;
    }
    
    /**
     * <p>
     * Obtains a joint distribution of the data given a current discretization and the class the data
     * belongs to
     * </p>
     * @param attribute Position of the attribute in the list of attributes whose best real values
     * for discretization are going to be selected
     * @param values   Position of the corresponding attribute value in the real values matrix,
     * ordered by attribute value
     * @param begin    First value that is considered to belong to the data considered, usually 0
     * @param end  Last value that is considered to belong to the data considered, usually the last
     * value of the dataset
     * @param posCutPoints Discretization proposed for the attribute containing the position of the 
     * selected cut points
     * @return a joint distribution depending on the discretization and the class data
     */
    private Vector jointClassDistribution(int attribute, int []values, int begin, int end, Vector posCutPoints) {
        int []jointClassCount = new int[Parameters.numClasses*(posCutPoints.size()+1)];
        for(int i=0;i<Parameters.numClasses*(posCutPoints.size()+1);i++) jointClassCount[i]=0;

        for(int i=begin; i<((Integer)posCutPoints.elementAt(0)).intValue(); i++) {
            jointClassCount[classOfInstances[values[i]]]++;
        }
        
        for (int i=1; i<posCutPoints.size(); i++) {
            for (int j=((Integer)posCutPoints.elementAt(i-1)).intValue(); j<((Integer)posCutPoints.elementAt(i)).intValue(); j++) {
                jointClassCount[Parameters.numClasses*i+classOfInstances[values[j]]]++;
            }
        }

        for(int i=((Integer)posCutPoints.elementAt(posCutPoints.size()-1)).intValue(); i<=end; i++) {
            jointClassCount[Parameters.numClasses*posCutPoints.size()+classOfInstances[values[i]]]++;
        }

        Vector res= new Vector();
        for(int i=0;i<Parameters.numClasses*(posCutPoints.size()+1);i++) {
            res.addElement(new Integer(jointClassCount[i]));
        }

        return res;
    }
}
