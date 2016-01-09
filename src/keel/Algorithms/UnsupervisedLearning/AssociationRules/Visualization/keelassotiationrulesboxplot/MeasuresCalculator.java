/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.UnsupervisedLearning.AssociationRules.Visualization.keelassotiationrulesboxplot;

/**
 *
 * @author alberto
 */
public class MeasuresCalculator {
    
    
    public double calculateLift(double antSup, double conSup, double ruleSup)
    {
        return (ruleSup / (antSup*conSup)) ;
    }
    
    public double calculateCF(double antSup, double conSup, double ruleSup)
    {
        Double output = 0.0;
        
        Double confidence = calculateConfidence(antSup, conSup, ruleSup);
        
        if(confidence > conSup)
        {
            output = ( (confidence - conSup) / (1-conSup) );
        }
        else if(confidence < conSup)
        {
            output = ( (confidence - conSup) / conSup );
        }
        else
        {
            output = 0.0;
        }
        
        return output;
    }
    
    public double calculateConfidence(double antSup, double conSup, double ruleSup)
    {
        return ( ruleSup / antSup );
    }
    
    public double calculateConviction(double antSup, double conSup, double ruleSup)
    {
        
        if((conSup == 1) || (antSup == 0))
        {
            return 1.0;
        }
        
        Double negatedConSup = 1 - conSup;
        return ( (antSup*negatedConSup) / (antSup-ruleSup) );
    }
    
}
