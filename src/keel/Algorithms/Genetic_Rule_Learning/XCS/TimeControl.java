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

/**
 * <p>
 * @author Written by Albert Orriols (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.XCS;
import  keel.Algorithms.Genetic_Rule_Learning.XCS.KeelParser.Config;
import java.util.*;
import java.lang.*;
import java.io.*;


public class TimeControl {
/**
 * <p>
 * This class makes the XCS time control
 * </p>
 */
	
	
    /* Time control of general execution: Training, Test, Wilson, Strong Dixon and Weak Dixon reduction*/
    private long t_totalTime=0, t_Train=0, t_Test=0, t_Wilson=0, t_StrongDixon=0, t_WeakDixon=0;
    
    private long t_CVTrain=0, t_CVTest=0, t_CVTrainTest=0;





    public TimeControl(){
    }


/**
 * <p>
 * It initilizes the time averages to zero.
 * </p>
 */
   
   public void initTimeAverages(){
   	t_Train = 0;
   	t_Test = 0;
   	t_Wilson = 0;
   	t_StrongDixon = 0;
   	t_WeakDixon  = 0;	
   	t_totalTime = 0;
   	t_CVTrain = 0;
   	t_CVTest = 0; 
   	t_CVTrainTest = 0;
   }    
   
   
   
/**
 * Updates the reduction time.
 * @param iReductionTime the initial reduction time (when the reduction has started)
 */
   public void updateReductionTime(long iReductionTime){
	if (Config.typeOfReduction.toUpperCase().equals("SD"))		t_StrongDixon	+= System.currentTimeMillis() - iReductionTime;
	else if (Config.typeOfReduction.toUpperCase().equals("WD"))	t_WeakDixon 	+= System.currentTimeMillis() - iReductionTime;
	else if (Config.typeOfReduction.toUpperCase().equals("NW"))	t_Wilson 	+= System.currentTimeMillis() - iReductionTime;
	else if (Config.typeOfReduction.toUpperCase().equals("EW"))	t_Wilson 	+= System.currentTimeMillis() - iReductionTime;
   }


/**
 * Updates the training time
 * @param iTrainTime is the time at the beginning of training
 */
   public void updateTrainTime (long iTrainTime){
   	t_Train += System.currentTimeMillis() - iTrainTime; 	
   }
   


/**
 * Update the test time
 * @param iTestTime is the time at the beginning of the test 
 */
   
   public void updateTestTime(long iTestTime){
	t_Test += System.currentTimeMillis() - iTestTime;   
   }
  

   public void updateTotalTime(long iTime){
	t_totalTime += iTime;
   }  


/**
 * <p>  
 * It prints the average time wasted in every kind of run
 * </p>
 * <p>
 * @param pW is the PrintWriter associated to the file where the time
 * averages have to be printed. 
 * </p>
 */
    public void printTimes(PrintWriter pW){
    	System.out.println ("The time statistics are made");
    	pW.println (" ============================= RUN TIME INFORMATION ============================= ");
    	pW.println ("TIMES IN SECONDS ");
    	pW.println ("Total time:	                 "+ ((double)t_totalTime/1000.0));
        pW.println ("\t Time of TRAIN:                  "+ ((double)t_Train/1000.0));
        pW.println ("\t Time of TEST:                   "+ ((double)t_Test/1000.0));
        pW.println ("\t Time of WILSON REDUCTION:       "+ ((double)t_Wilson/1000.0));
        pW.println ("\t Time of WEAK   DIXON REDUCTION: "+ ((double)t_WeakDixon/1000.0));
        pW.println ("\t Time of STRONG DIXON REDUCTION: "+ ((double)t_StrongDixon/1000.0));
        //pW.println ("Temps Total del FOLD:               "+ ((double)(t_CVTest+t_CVTrain+t_CVTrainTest)/1000.0));
        //pW.println ("\t Time of TRAIN:		         "+ ((double)t_CVTrain/1000.0));
        //pW.println ("\t Time of TEST AMB Training set:  "+ ((double)t_CVTrainTest/1000.0));
        //pW.println ("\t Time of TEST AMB Test set: 	 "+ ((double)t_CVTest/1000.0));
        
        
        pW.println ("\n\n TIMES IN MINUTES ");
        pW.println ("Total time:			 "+ ((double)t_totalTime/60000));
        pW.println ("\t Time of TRAIN:                  "+ ((double)t_Train/60000));
        pW.println ("\t Time of TEST:                   "+ ((double)t_Test/60000));
        pW.println ("\t Time of WILSON REDUCTION:       "+ ((double)t_Wilson/60000));
        pW.println ("\t Time of WEAK   DIXON REDUCTION: "+ ((double)t_WeakDixon/60000));
        pW.println ("\t Time of STRONG DIXON REDUCTION: "+ ((double)t_StrongDixon/60000));
        //pW.println ("Temps Total del FOLD:               "+ ((double)(t_CVTest+t_CVTrain+t_CVTrainTest)/60000.0));
        //pW.println ("\t Time of TRAIN:		         "+ ((double)t_CVTrain/60000.0));
        //pW.println ("\t Time of TEST AMB Training set:  "+ ((double)t_CVTrainTest/60000.0));
        //pW.println ("\t Time of TEST AMB Test set: 	 "+ ((double)t_CVTest/60000.0));
    	pW.println ("================================================================================ ");
    }



/**
 * <p>  
 * It prints the average time wasted in every kind of run
 * </p>
 */
    public void printTimes(){
    	System.out.println ("============================= RUN TIME INFORMATION ============================= ");
    	System.out.println ("TIMES IN SECONDS ");
    	System.out.println ("Total time:	                "+ ((double)t_totalTime/1000.0));
        System.out.println ("\t Time of TRAIN:                  "+ ((double)t_Train/1000.0));
        System.out.println ("\t Time of TEST:                   "+ ((double)t_Test/1000.0));
        System.out.println ("\t Time of WILSON REDUCTION:       "+ ((double)t_Wilson/1000.0));
        System.out.println ("\t Time of WEAK   DIXON REDUCTION: "+ ((double)t_WeakDixon/1000.0));
        System.out.println ("\t Time of STRONG DIXON REDUCTION: "+ ((double)t_StrongDixon/1000.0));
        
        //System.out.println ("\n\n TIMES IN MINUTES ");
        //System.out.println ("Total time:	                "+ ((double)t_totalTime/60000.0));
        //System.out.println ("\t Time of TRAIN:                  "+ ((double)t_Train/60000.0));
        //System.out.println ("\t Time of TEST:                   "+ ((double)t_Test/60000.0));
        //System.out.println ("\t Time of WILSON REDUCTION:       "+ ((double)t_Wilson/60000.0));
        //System.out.println ("\t Time of WEAK   DIXON REDUCTION: "+ ((double)t_WeakDixon/60000.0));
        //System.out.println ("\t Time of STRONG DIXON REDUCTION: "+ ((double)t_StrongDixon/60000.0));
    	System.out.println ("================================================================================ ");
    }

} // end TimeControl

