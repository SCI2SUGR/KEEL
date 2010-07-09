/**
 * <p>
 * @author Written by Luciano Sánchez (University of Oviedo) 20/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual;

import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Genotypes.*;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Shared.Parsing.*;

public abstract class GeneticIndividual {
/**
 * <p>
 * Methods for genetic individual management
 * Need: The genotype and the type of fitness
 * </p>
 */
        public static final int STANDARD=OperatorIdent.GI_STANDARD;
		public static final int CUSTOM_CESAR=OperatorIdent.GI_CUSTOM_CESAR;

        public Genotype g;
		protected int fitnessType;

		/**
		 * <p>
		 * Constructor. Initialize the type of fitness
		 * </p>
		 * @param tf The type of fitness
		 */
	    public GeneticIndividual(int tf) { fitnessType=tf; }
	    
	    /**
	     * <p>
	     * This abstract method calculates the classification error
	     * </p>
	     * @return The classification error
	     * @throws invalidFitness Message if error
	     */
        public abstract double fitness() throws invalidFitness;
        
        /**
         * <p>
         * This abstract method clone a genetic individual
         * </p>
         * @return The cloned genetic individual
         */
        public abstract  GeneticIndividual clone();
         
         /**
          * <p>
          * This abstract method sets parameters from a genotype 
          * </p>
          */
        public abstract void parametersFromGenotype();
                
        /**
         * <p>
         * This abstract method implement the mutation operation
         * </p>
         * @param alpha Mutation index
         * @param idmutation Type of mutation
         * @throws invalidMutation Message if error
         */
        public abstract void mutation(double alpha, int idmutation) throws invalidMutation;
        /**
         * <p>
         * This abstract method implement the cross operation
         * </p>
         * @param p2 The first genetic individual
         * @param p3 The second genetic individual
         * @param p4 The third genetic individual
         * @param idcross Type of cross
         * @throws invalidCrossover Message if error
         */
        public abstract void crossover(GeneticIndividual p2, GeneticIndividual p3, GeneticIndividual p4,int idcross) throws invalidCrossover;
		
        /**
         * <p>
         * This abstract method calculate a local optimization
         * </p>
         * @param MAXITER Maximun iterations
         * @param idoptimization Type of optimization
         * @throws invalidOptim Message if error 
         */
        public abstract void localOptimization(int MAXITER, int idoptimization) throws invalidOptim;
        
        /**
         * <p>
         * This method is for debug
         * </p>
         */
        public abstract void debug();
        
        /**
         * <p>
         * This abstract method is for random generation
         * </p>
         */
        public abstract void Random();


}
