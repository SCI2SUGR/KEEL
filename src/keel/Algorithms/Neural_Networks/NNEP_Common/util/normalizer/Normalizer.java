package keel.Algorithms.Neural_Networks.NNEP_Common.util.normalizer;

import keel.Algorithms.Neural_Networks.NNEP_Common.data.DoubleTransposedDataSet;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penya, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class Normalizer{
	
	/**
	 * <p>
	 * Represents a data normalizer
	 * </p>
	 */
    
	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
     * Empty constructor
     * </p>
     */
    public Normalizer() {
        super();
    }
    
    /////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
    
	/**
	 * <p>
	 * Scales a IDataSet using the the maximum and minimum scaled
	 * and unscaled values specified
	 * </p>
	 * @param dataset IDataSet to normalize
	 * @param scaledMax Array of maximum scaled values
	 * @param scaledMin Array of minimum scaled values
	 * @param unscaledMax Array of maximum unscaled values
	 * @param unscaledMin Array of minimum unscaled values
	 * @return double Unscale SEP value
	 */
    public void scaleDS (DoubleTransposedDataSet dataset, 
            double[] scaledMax, double[] scaledMin, 
            double[] unscaledMax, double[] unscaledMin) {
    	
        //Normalize all the variables
        for(int i=0; i<dataset.getNofvariables(); i++){
          
            //Obtain the observations for this variable
            double [] observations = dataset.getObservationsOf(i);

            
            //Normalize each observation
            scale(observations, unscaledMax[i], unscaledMin[i],
                    scaledMax[i], scaledMin[i]);
            
        }
               
        //Recalculate means
        dataset.calculateMeans();
    }
    
	/**
	 * <p>
	 * Scale an array of values from a specific domain [unscaledMin, unscaledMax]
	 * to other domain [scaledMin, scaledMax]
	 * </p>
	 * @param values Double array of values to be scaled
	 * @param unscaledMax Double maximum unscaled domain
	 * @param unscaledMin Double minimum unscaled domain
	 * @param scaledMax Double maximum scaled domain
	 * @param scaledMin Double minimum scaled domain
	 */
    public void scale(double [] values, 
            double unscaledMax, double unscaledMin,
            double scaledMax, double scaledMin){

    	double coefficient;
    	if(unscaledMax==unscaledMin)
    		coefficient = 0;
    	else
    		coefficient = (scaledMax-scaledMin) / (unscaledMax-unscaledMin);

        for(int i=0; i<values.length; i++)
            values[i] = coefficient*(values[i]-unscaledMin) + scaledMin;
        
    }
    
	/**
	 * <p>
	 * Scale a value from a specific domain [unscaledMin, unscaledMax] to
	 * other domain [scaledMin, scaledMax]
	 * </p>
	 * @param value Double value to be scaled
	 * @param unscaledMax Double maximum unscaled domain
	 * @param unscaledMin Double minimum unscaled domain
	 * @param scaledMax Double maximum scaled domain
	 * @param scaledMin Double minimum scaled domain
	 * @return double Value scaled
	 */
    public double scale(double value, 
            double unscaledMax, double unscaledMin,
            double scaledMax, double scaledMin){
    	
    	double coefficient;
    	if(unscaledMax==unscaledMin)
    		coefficient = 0;
    	else
    		coefficient = (scaledMax-scaledMin) / (unscaledMax-unscaledMin);
        
        return coefficient*(value-unscaledMin) + scaledMin;
    }
}