/**
 * <p>
 * @author Written by Luciano Sánchez (University of Oviedo) 25/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Model;

import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

// Wrappers used with genetic algorithms
public class FuzzyGPRegSymModel extends FuzzyRegressor {
    
    NodeExprHold R;
    static double KMIN, KMAX;
    static int NOutputs;
    
    /**
     * <p>
     * Constructor. Generate a new fuzzy system of symbolic regression for GP model
     * </p>
     * @param pR The node
     * @param kmin Minimum k
     * @param kmax Maximum k
     * @param noutputs Number of outputs
     * @param typectes type of constants
     */
    public FuzzyGPRegSymModel( NodeExprHold pR, double kmin, double kmax, int noutputs, int typectes) {
        R=(NodeExprHold)pR.clone();
        KMIN=kmin; KMAX=kmax;
        NOutputs=noutputs;
        constType=typectes;
    }
    
    /**
     * <p>
     * Constructor. Generate a new fuzzy system of symbolic regression for
     * GP model from another one
     * </p>
     * @param mb The fuzzy system of symbolic regression for GP model 
     */
    public FuzzyGPRegSymModel(FuzzyGPRegSymModel mb) {
        R=(NodeExprHold)mb.R.clone();
    }
 
    /**
     * <p>
     * This method assing the properties of a fuzzy system of symbolic regression for
     * GP model to another one
     * </p>
     * @param p The fuzzy system of symbolic regression for GP model
     */  

    public void set(FuzzyGPRegSymModel mb) {
        R=(NodeExprHold)mb.R.clone();
    }
    
    /**
     * <p>
     * This method clone a fuzzy model
     * </p>
     */
    
    public FuzzyRegressor clone()  {
        return new FuzzyGPRegSymModel(this);
    }
    
    /**
     * <p>
     * This method is for debug
     * </p>
     */
    public void debug() {
        R.debug();
    }

    /**
     * <p>
     * This method return the output of the model like fuzzy alpha cuts
     * </p>
     * @param x The output
     */
    
    public FuzzyAlphaCut output(FuzzyAlphaCut [] x) {
        R.replaceTerminals(x);
        FuzzyAlphaCut[] result=R.Beval();
        
        // result.length allways will be 1, with this kind of model.
        return result[0];
        
    }
}
