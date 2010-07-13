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

package keel.Algorithms.Instance_Generation.HYB;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.utilities.KNN.KNN;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.BasicMethods.*;
import keel.Algorithms.Instance_Generation.LVQ.*;
import keel.Algorithms.Instance_Generation.PNN.*;
import keel.Algorithms.Instance_Generation.VQ.*;
//import keel.Algorithms.Instance_Generation.LVQ.*; Este serÃ¡ el SVM
import keel.Algorithms.Instance_Generation.BTS3.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import org.core.*;
import java.util.*;

/**
 * Hybrid algorithm
 * @author diegoj
 */
public class HYBGenerator extends PrototypeGenerator {

    /** PNNGenerator title text */
    public final static String PNN = "PNN";
    /** SVM title text */
    public final static String SVM = "SVM";
    /** CNN title text */
    public final static String CNN = "CNN";
    /** VQGenerator title text */
    public final static String VQ = "VQ";
    /** Titles of types of initial reduction */
    public final static String[] typesOfReduction = {PNN, SVM, CNN, VQ}; 
    
    /**
     * Informs if the HYBGenerator type is correct.
     * @param t Hypotetical type of initial reduction.
     * @return TRUE if type is right, FALSE in other chase.
     */
    private static boolean isCorrectHYBType(String t)
    {
        boolean found = false;
        for(int i=0; i<typesOfReduction.length && !found; ++i)
            found = typesOfReduction[i].equals(t);
        return found;
    }
    
    /** Parameters of the initial reduction process. */
    private String[] paramsOfInitialReducction = null;

    /** Default epsilon bounds. */
    final static double[] E_BOUNDS = {0.0, 1.0};
    /** Increment of epsilon in each step. */
    protected double deltaE = 0.1;
    /** Initial value of epsilon. */
    double initE = E_BOUNDS[0];
    /** End value of epsilon. */
    double endE = E_BOUNDS[1];
    
    /** Default window width bounds. */
    final static double[] W_BOUNDS = {0.0, 1.0};
    /** Increment of window width in each step. */
    protected double deltaW = 0.1;
    /** Initial value of window width. */
    double initW = W_BOUNDS[0];
    /** End value of window width. */
    double endW = W_BOUNDS[1];
    
    /** % of training data set used as training in the search process. */
    protected double partitionPercentTraining = 40.0;
    
    /** Desired final size of the generated prototype set. */
    protected int desiredFinalSize = 100;
    
    /** Iterations of each LVQ3-search for optimal epsilon and window width. */
    protected int searchingIterations = 400;
    
    /** Iterations of the final optimal LVQ3. */
    protected int iterations = 1000;
    
    /** Per one prototypes generated in the LVQ3. */
    protected double perOneGenerated = 0.1;
    
    /** Alpha0 parameter of the LVQ3 methods. */
    protected double alpha_0 = 0.01;
    
    /** Type of initial reduction. Posible values:  PNNGenerator, CNN, SVM, VQGenerator.  */
    protected String typeOfInitialReduction = HYBGenerator.PNN;
    
    
    /**
     * Constructor of HYBGenerator algorithm.
     * @param _trainingDataSet TrainingDataSet.
     * @param parameters Parameters of the algorithm.
     */
    public HYBGenerator(PrototypeSet _trainingDataSet, Parameters parameters)
    {
        super(_trainingDataSet, parameters);
        this.iterations = parameters.getNextAsInt();
        this.searchingIterations = parameters.getNextAsInt();
        this.perOneGenerated = parameters.getNextAsDouble()/100.0;
        this.desiredFinalSize = (int)Math.ceil(trainingDataSet.size()*perOneGenerated);
        this.alpha_0 = parameters.getNextAsDouble();
        this.initE = parameters.getNextAsDouble();
        this.endE = parameters.getNextAsDouble();
        this.deltaE = parameters.getNextAsDouble();
        this.initW = parameters.getNextAsDouble();
        this.endW = parameters.getNextAsDouble();
        this.deltaW = parameters.getNextAsDouble();
        this.partitionPercentTraining = parameters.getNextAsDouble();
        this.typeOfInitialReduction = parameters.getNextAsString();
        //String params = parameters.getNextAsString();
        //Debug.errorln("Cadena antes " + params);
        this.paramsOfInitialReducction = parameters.getRemainingParameters();
        this.algorithmName = "HYB-"+typeOfInitialReduction;
    }

    /**
     * Construct a HYBGenerator algorithm.
     * @param tDataSet Training data set.
     * @param iterSearch Iterations performed in the internals LVQ3.
     * @param iterEnd Iterations performed in the optimal (and final) LVQ3.
     * @param percentageGeneratedByOptimalLVQ3 % of size of initial set to be generated.
     * @param alpha Alpha 0 LVQ3 parameter.
     * @param initW Initial value of window width.
     * @param endW End value of window width.
     * @param deltaW Increment of the window width.
     * @param initE Initial value of epsilon.
     * @param endE End value of epsilon.
     * @param deltaE Increment of the epsilon.
     * @param percentPartition % of training data set used as training in the search process.
     * @param type Type of initial reduction: CNN, VQGenerator, SVM, PNNGenerator.
     * @param paramsOfInitialReducction Parameters of the initial reduction.
     */
    public HYBGenerator(PrototypeSet tDataSet, int iterSearch, int iterEnd,
            double percentageGeneratedByOptimalLVQ3,            
            double alpha,
            double initW, double endW, double deltaW,
            double initE, double endE, double deltaE,
            double percentPartition,
            String type, String[] paramsOfInitialReducction)
    {
        super(tDataSet);
        this.searchingIterations = iterSearch;
        this.iterations = iterEnd;
        this.perOneGenerated = percentageGeneratedByOptimalLVQ3/100.0;
        this.desiredFinalSize = (int)Math.ceil(tDataSet.size()*perOneGenerated);
        this.alpha_0 = alpha;
        this.initE = initE;
        this.endE = endE;
        this.deltaE = deltaE;
        this.initW = initW;
        this.endW = endW;
        this.deltaW = deltaW;   
        this.partitionPercentTraining=percentPartition;
        this.typeOfInitialReduction = type;
        this.paramsOfInitialReducction = paramsOfInitialReducction;
        Debug.force(isCorrectHYBType(type), "Type of HYB " + type + " is not allowed");
        this.algorithmName = "HYB-"+type;
    }
    
     /**
     * Construct a HYBGenerator algorithm.
     * @param tDataSet Training data set.
     * @param iterSearch Iterations performed in the internals LVQ3.
     * @param iterEnd Iterations performed in the optimal (and final) LVQ3.
     * @param percentageGeneratedByOptimalLVQ3 % of size of initial set to be generated.
     * @param alpha Alpha 0 LVQ3 parameter.
     * @param initW Initial value of window width.
     * @param endW End value of window width.
     * @param deltaW Increment of the window width.
     * @param initE Initial value of epsilon.
     * @param endE End value of epsilon.
     * @param deltaE Increment of the epsilon.
     * @param percentPartition % of training data set used as training in the search process.
     * @param type Type of initial reduction: CNN, VQ, SVM, PNN.
     * @param paramsOfInitialReducction Parameters of the initial reduction. In form of plain string.
     */
    public HYBGenerator(PrototypeSet tDataSet, int iterSearch, int iterEnd,
            double percentageGeneratedByOptimalLVQ3,            
            double alpha,
            double initW, double endW, double deltaW,
            double initE, double endE, double deltaE,
            double percentPartition,
            String type, String paramsOfInitialReducction)
    {
        super(tDataSet);
        this.iterations = iterEnd;
        this.searchingIterations = iterSearch;
        this.perOneGenerated = percentageGeneratedByOptimalLVQ3/100.0;
        this.desiredFinalSize = (int)Math.ceil(tDataSet.size()*perOneGenerated);
        this.alpha_0 = alpha;
        this.initE = initE;
        this.endE = endE;
        this.deltaE = deltaE;
        this.initW = initW;
        this.endW = endW;
        this.deltaW = deltaW;   
        this.partitionPercentTraining=percentPartition;
        this.typeOfInitialReduction = type;
        this.paramsOfInitialReducction = paramsOfInitialReducction.split("\\,");
        Debug.force(isCorrectHYBType(type), "Type of HYB " + type + " is not allowed");
        this.algorithmName = "HYB-"+type;
    }
    
    /**
     * Performs a LVQ3-reduction of the set.
     * @param w Window width.
     * @param e Epsilon.
     * @param iter Number of iterations.
     * @param Np Number of prototypes to be generated.
     */
    private PrototypeSet makeLVQ3Reduction(PrototypeSet InitialSet, PrototypeSet training, double w, double e, int size, int iter)
    {
        if(InitialSet.size()<=2)
        {
            PrototypeSet result = new PrototypeSet();
            result.add(InitialSet.avg());
            return result;
        }
        else if(InitialSet.size() < size)
        {
            size = InitialSet.size();
        }
        
        LVQ3 lvq3 = new LVQ3(InitialSet,training, iter, size, alpha_0, w, e);
        PrototypeSet reducedByLVQ3 = lvq3.execute();
        return reducedByLVQ3;
    }
    
    /**
     * Performs a LVQ3 reduction.
     * @param set Set to be reduced.
     * @param w Widow width.
     * @param e Epsilon.
     * @return Reduced set.
     */
    private PrototypeSet searchingTypeLVQ3Reduction(PrototypeSet InitialSet, PrototypeSet training, double w, double e)
    {
        int size = (int)Math.ceil(InitialSet.size()*perOneGenerated);//set.size();//
        return makeLVQ3Reduction(InitialSet, trainingDataSet, w, e, size, searchingIterations);
    }
    
    /**
     * Performs an optimal LVQ3 reduction.
     * @param set Set to be reduced.
     * @param w Widow width.
     * @param e Epsilon.
     * @return Reduced set.
     */
    private PrototypeSet optimalLVQ3Reduction(PrototypeSet InitialSet, PrototypeSet training, double w, double e)
    {
        return makeLVQ3Reduction(InitialSet, training, w, e, desiredFinalSize, iterations);
    }
    
    /**
     * Makes the initial reduction of the prototype set
     * @return Reduced prototype set.
     */
    protected PrototypeSet initialReduction()
    {
    	

        PrototypeSet reduced = null;
        String type = typeOfInitialReduction;
        int i=0;
        int kCNN = Integer.parseInt(paramsOfInitialReducction[i++]);
        int iterVQ = Integer.parseInt(paramsOfInitialReducction[i++]);
        double redVQ = Double.parseDouble(paramsOfInitialReducction[i++]);
        int kVQ = Integer.parseInt(paramsOfInitialReducction[i++]);
        double alpha0VQ = Double.parseDouble(paramsOfInitialReducction[i++]);
        String kernelType = paramsOfInitialReducction[i++];
        double C = Double.parseDouble(paramsOfInitialReducction[i++]);
        double eps = Double.parseDouble(paramsOfInitialReducction[i++]);
        int degree = Integer.parseInt(paramsOfInitialReducction[i++]);
        double gamma = Double.parseDouble(paramsOfInitialReducction[i++]);
        double nu = Double.parseDouble(paramsOfInitialReducction[i++]);
        double p = Double.parseDouble(paramsOfInitialReducction[i++]);
        int shrinking = Integer.parseInt(paramsOfInitialReducction[i++]);
        
        int NpVQ = (int)Math.ceil(trainingDataSet.size()*redVQ/100.0);
        if(type.equals(HYBGenerator.PNN))
        {
            PNNGenerator generator = new PNNGenerator(trainingDataSet, desiredFinalSize);
            reduced = generator.reduceSet();
            //double acc = HYBGenerator.accuracy(reduced, trainingDataSet);
            //Debug.errorln("Acurracy of PNN " + acc);
            Debug.println("PNN");
        }
        else if(type.equals(HYBGenerator.CNN))
        {
            CNN cnn = new CNN(trainingDataSet, kCNN);
            reduced = cnn.reduceSet();
            //double acc = HYBGenerator.accuracy(reduced, trainingDataSet);
            //Debug.errorln("Acurracy of CNN " + acc);
            Debug.errorln("CNN de " + reduced.size());
        }
        else if(type.equals(HYBGenerator.VQ))
        {
            //Parameters of the VQGenerator: trainingDataSet, iterations, np, alpha_0, k
            VQGenerator generator = new VQGenerator(trainingDataSet, iterVQ, NpVQ, alpha0VQ, kVQ);
            reduced = generator.reduceSet();
            Debug.println("VQ");
        }
        else if(type.equals(HYBGenerator.SVM))
        {
        	
            SVMSEL svm = new SVMSEL(trainingDataSet, kernelType, C, eps, degree, gamma, nu, p, shrinking);
            reduced = svm.reduceSet();
            System.out.println("Hemos seleccionado " + reduced.size() + " de " + trainingDataSet.size());
            Debug.println("SVM");
        }
        else
            Debug.goout("Reduction must be SVM, CNN, PNN, or VQ");

        //Debug.errorln("Initial reduction ended. " + reduced.size() + " prototypes generated");
        return reduced;
    }
    
    /**
     * Reduce the set by the Hybrid method.
     * @return Prototype set generated by Hybrid method.
     */
    @Override
    public PrototypeSet reduceSet()
    {
    	
        // We make the initial reduction
        PrototypeSet initialSet = initialReduction();
        Debug.errorln("Tamaï¿½o " + initialSet.size());
       
        // Divide the training Set into two parts,  Placement and optimizing.
        
        Pair<PrototypeSet,PrototypeSet> Tglobal = trainingDataSet.makePartitionPerClass(this.partitionPercentTraining);
        //Partition of the reduced set
        PrototypeSet TpGlobal = Tglobal.first();//placement set
        PrototypeSet ToGlobal = Tglobal.second();//optimizing set
               
        //Debug.errorln("Tamaï¿½o Tplacement " + TpGlobal.size());
        //Debug.errorln("Tamaï¿½o Toptimization " + ToGlobal.size());
        double bestE = initE;
        double bestW = initW;
        
        //double bestAcc = HYBGenerator.absoluteAccuracy(TpGlobal, ToGlobal);
        double bestAcc = accuracy(TpGlobal, ToGlobal);;
        System.out.println("InitialAcc ="+ bestAcc);
        
        //Debug.errorln("First bestAcc " + bestAcc);
        for (double w = initW; w <= endW; w += deltaW)
            for (double e = initE; e <= endE; e += deltaE)
            {
            	PrototypeSet reduced_i = searchingTypeLVQ3Reduction(initialSet, TpGlobal, w, e);
            	//double currentAcc = HYBGenerator.absoluteAccuracy(reduced_i, ToGlobal);
                double currentAcc = accuracy(reduced_i, ToGlobal);
                
                if(currentAcc >= bestAcc)
                {
                    //Debug.errorln("Acc actual " + currentAcc + " > " + bestAcc);       
                	initialSet = reduced_i.clone(); // modify YTest
                    bestAcc = currentAcc;
                    bestE = e;
                    bestW = w;                    
                 }
            }
        PrototypeSet reduced = optimalLVQ3Reduction(initialSet, TpGlobal, bestW, bestE);
        
        System.err.println("\n% de acierto en training " + KNN.classficationAccuracy(reduced,trainingDataSet,1)*100./trainingDataSet.size() );
		  
        //reduced.applyThresholds();
        return reduced;
    }
    

    /**
     * General main for all the prototoype generators
     * Arguments:
     * 0: Filename with the training data set to be condensed.
     * 1: Filename wich contains the test data set.
     * 3: Seed of the random generator.
     * 4: Iterations performed in the search of optimal LVQ3-parameters.
     * 5: Iterations performed in optimal LVQ3 process.
     * 6: % of prototypes generated in optimal LVQ3 process.
     * 7: Alpha0 LVQ3-parameter.
     * 8: Initial value of window width.
     * 9: Final value of window width.
     * 10: Increment in each step of the value of window width.
     * 11: Initial value of epsilon.
     * 12: Final value of epsilon.
     * 13: Increment in each step of the value of epsilon.
     * 14: % de initial partition in the training set.
     * 15: Type of initial reduction performed.
     * 16 and so on: Parameters of the initial reduction process
     * @param args Arguments of the main function.
     */
    public static void main(String[] args)
    {
        Parameters.setUse("HYB", "<seed> <num. iterations>\n<num. prototypes>\n<alpha_0>\n<initW><endW><deltaW>\n<initE><endE><deltaE>\n<% init partition training>\n<type of init reduction> <params of init reduction>");
        Parameters.assertBasicArgs(args);        
        
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        HYBGenerator.setSeed(seed);
        int iterSearch = Parameters.assertExtendedArgAsInt(args,3,"iterations of LVQ3 optimization process", 1, Integer.MAX_VALUE);
        int iterOptimal = Parameters.assertExtendedArgAsInt(args,4,"iterations of optimal-LVQ3 reduction", 1, Integer.MAX_VALUE);
        double percentOptimalLVQ3Set = Parameters.assertExtendedArgAsDouble(args,5,"% of prototypes used in optimal LVQ3", 1.0, 100.0);
        double alpha_0 = Parameters.assertExtendedArgAsDouble(args,6,"alpha_0", 0, 1);
        double initW = Parameters.assertExtendedArgAsDouble(args,7,"Initial W value", 0, 1);
        double endW = Parameters.assertExtendedArgAsDouble(args,8,"End W value", 0, 1);
        double deltaW =  Parameters.assertExtendedArgAsDouble(args,9,"delta Window Width", 0, 1);
        double initE = Parameters.assertExtendedArgAsDouble(args,10,"Initial E value", 0, 1);
        double endE = Parameters.assertExtendedArgAsDouble(args,11,"End E value", 0, 1);
        double deltaE =  Parameters.assertExtendedArgAsDouble(args,12,"delta Epsilon", 0, 1);
        double percentPart =  Parameters.assertExtendedArgAsDouble(args,13,"% of training data set used as training in the search process", 5.0, 100.0);
        String type =  Parameters.assertExtendedArgAsString(args,14,"Type of algorithm used in initial reduction", HYBGenerator.typesOfReduction);        
        String[] parametersOfInitialReduction = Arrays.copyOfRange(args, 15, args.length);
        
        /*
         * Parameters of the algorithm:
         * PrototypeSet _trainingDataSet, int iterSearch, int iterEnd,
         * double perOneGeneratedBySearchingLVQ3,
         * double perOneGeneratedByOptimalLVQ3,
         * double alpha,
         * double initW, double endW, double deltaW,
         * double initE, double endE, double deltaE,
         * double percentPart,
         * int type, String[] paramsOfInitialReducction
         */
        HYBGenerator generator = new HYBGenerator(training, iterSearch, iterOptimal,
                percentOptimalLVQ3Set,
                alpha_0,
                initW, endW, deltaW,
                initE, endE, deltaE,
                percentPart,
                type, parametersOfInitialReduction);
        
    	PrototypeSet resultingSet = generator.execute();
        
    	//resultingSet.save(args[1]);
        //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
        //generator.showResultsOfAccuracy(accuracyKNN, accuracy1NN, k, test);
    }

}

