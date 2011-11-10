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
 * @author Modified by Maria Dolores Perez Godoy (University of Jaen)
 * @author Modified by Antonio J. Rivera Rivas (University of Jaen) 
 * @author Modified by Victor M. Rivas Santos (University of Jaen) 
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Neural_Networks.EvRBF_CL;

import org.core.*;
import java.io.*;
import java.util.*;


public class doEvRBF_Cl {
/**
 * <p>
 * This class allows the building of an Evolutionary Algorithm to generate
 * RBF Neural Networks.
 * This class contains a MAIN function that reads parameters, builds the net, 
 * and produces the results yielded by the net when is applied to the test 
 * data set.
 * </p>
 */

  /** Filename for training data set */
	static String trnFile;

  /** Filename for test data set  */
  static String tstFile;

  /** Filename for results of RBFN on training set  */
  static String outTrnFile;

  /** Filename for results of RBFN on test set  */
  static String outTstFile;

  /** Filename for file where RBF will be written  */
  static String outRbfFile;

  /** Rate of neurons  */
  static double neuronsRate;

  /**  rate of instances for validation  */
  static double validationRate;

  /** rate of individuals to replace  */
  static double replacementRate;

  /** rate of xOver operator  */
  static double xOverRate;
  
  /** rate of mutator operators  */
  static double mutatorRate;

  /** tournament size  */
  static int tournamentSize;
  
  /** max number of generations  */
  static int maxGenerations;
  
  /** Number of individuals  */
  static int popSize;  

  /** Seed for random generator initialization.  */
  static double seed;

  /** Whether Seed must be used ir not  */
  static boolean reallySeed;


  static double [][] X, Xtrn, Xval, Xtst;
  static double [][] Y, Ytrn, Yval, Ytst;
  static int [] auxY, auxYtrn, auxYval, auxYtst;
  static RbfnPopulation population;

  static int nEnt,nSal,i,j;
  static int nTrn, nVal, nTst;
  static int LMSLoops=1;

  /** <p>Does nothing.</p> */
  public doEvRBF_Cl() {}


/**
 * <p>
 * Splits the training ProcDataset into 2 data sets: one for training and another for validation
 * </p>
 * @param DData Original (complete) training data set
 * @returns Nothing, but modifies XTrn, YTrn, XVal, YVal
 */
private static void set_training_validation( ProcDataset DData ) {
  try {
    DData.processClassifierDataset();
    nEnt = DData.getninputs();
    nSal = 1;//PD.getnvariables()-nEnt;
    int ndatos = DData.getndata();
    Xtrn=new double[ndatos][nEnt];
    Xval=new double[ndatos][nEnt];
    Ytrn=new double[ndatos][nSal];
    Yval=new double[ndatos][nSal];
    auxYtrn=new int[ndatos];
    auxYval=new int[ndatos];
    nTrn=nVal=0;

    double [][] X, Y;
    int [] auxY;

    Y = new double [ndatos][1];
    X = DData.getX();
    auxY = DData.getC();
    for (i = 0; i < ndatos; i++) {
      Y[i][0]=auxY[i];
    }
    for ( i=0; i<ndatos; ++i ) {
      int aleat=(int)Randomize.Randint( 1, (int)100 );
      if ( aleat>=validationRate*100 ) {
        Xtrn[nTrn]=X[i];
        Ytrn[nTrn]=Y[i];
        auxYtrn[nTrn]=auxY[i];
        ++nTrn;
      } else {
        Xval[nVal]=X[i];
        Yval[nVal]=Y[i];
        auxYval[nVal]=auxY[i];
        ++nVal;
      }
    }
  } catch ( Exception e ) {
    	throw new InternalError(e.toString());
 }
}

/**
 * <p>
 * Establishes the values for XTst and YTst
 * </p>
 * @param DData Original test data set
 * @returns Nothing, but modifies XTst and Ytst
 */
private static void set_test( ProcDataset DData ) {
  try {
    DData.processClassifierDataset();
    nTst= DData.getndata();
    Xtst = DData.getX();
    Ytst=new double[nTst][nSal];
    nTst=Xtst.length;
    auxYtst = DData.getC();
    for (i = 0; i < nTst; i++) {
      Ytst[i][0]=auxYtst[i];
    }
  } catch ( Exception e ) {
    	throw new InternalError(e.toString());
 }
}

  /**
   * <p>
   * Reads parameters from parameter file.</p>
   * @param _fileName  Name of file with parameters.
   * @return True if everything goes right. False otherwise.
   */
  private static boolean setParameters( String fileName ) {
    	try {
        Hashtable parametros=RBFUtils.parameters( fileName );
        RBFUtils.setVerbosity( parametros );
        String tmp;
        tmp=((String) ((Vector) parametros.get ( "inputData" )).get( 0 ));
        trnFile=tmp.substring( 1, tmp.length()-1 ); // Character " must be removed.
        tmp=(String) ((Vector) parametros.get ( "inputData" )).get( 2 );
        tstFile=tmp.substring( 1, tmp.length()-1 ); // Character " must be removed.
        tmp=(String) ((Vector) parametros.get ( "outputData" )).get( 0 );
        outTrnFile=tmp.substring( 1, tmp.length()-1 ); // Character " must be removed.
        tmp=(String) ((Vector) parametros.get ( "outputData" )).get( 1 );
        outTstFile=tmp.substring( 1, tmp.length()-1 ); // Character " must be removed.
        tmp=(String) ((Vector) parametros.get ( "outputData" )).get( 2 );
        outRbfFile=tmp.substring( 1, tmp.length()-1 ); // Character " must be removed.

        neuronsRate=(double) Double.parseDouble( (String) ((Vector) parametros.get ( "neuronsRate" )).get( 0 ));
        validationRate=(double) Double.parseDouble( (String) ((Vector) parametros.get ( "validationRate" )).get( 0 ));
        replacementRate=(double) Double.parseDouble( (String) ((Vector) parametros.get ( "replacementRate" )).get( 0 ));
        xOverRate=(double) Double.parseDouble( (String) ((Vector) parametros.get ( "xOverRate" )).get( 0 ));
        mutatorRate=(double) Double.parseDouble( (String) ((Vector) parametros.get ( "mutatorRate" )).get( 0 ));
        popSize=(int) Double.parseDouble( (String) ((Vector) parametros.get ( "popSize" )).get( 0 ));
        tournamentSize=(int) Double.parseDouble( (String) ((Vector) parametros.get ( "tournamentSize" )).get( 0 ));
        maxGenerations=(int) Double.parseDouble( (String) ((Vector) parametros.get ( "maxGenerations" )).get( 0 ));

        if ( parametros.containsKey ( "seed" ) ) {
        	reallySeed=true;
        	seed=(double) Double.parseDouble( (String) ((Vector) parametros.get ( "seed" )).get( 0 ));
        } else {
        	reallySeed=false;
                seed=(double) Math.random()*1e6;
        }
        RBFUtils.verboseln( "Training file      : "+trnFile );
        RBFUtils.verboseln( "Test file          : "+tstFile );
        RBFUtils.verboseln( "Ouput Training file: "+outTrnFile );
        RBFUtils.verboseln( "Ouput Test file    : "+outTstFile );
        RBFUtils.verboseln( "Ouput RBF file     : "+outRbfFile );
        RBFUtils.verboseln( "Population size    : "+popSize );
        RBFUtils.verboseln( "Tournament size    : "+tournamentSize );
        RBFUtils.verboseln( "Max. generations   : "+maxGenerations );
        RBFUtils.verboseln( "Neurons rate       : "+neuronsRate );
        RBFUtils.verboseln( "Validation rate    : "+validationRate );
        RBFUtils.verboseln( "Replacement rate   : "+replacementRate );
        RBFUtils.verboseln( "XOver rate         : "+xOverRate );
        RBFUtils.verboseln( "Mutator rate       : "+mutatorRate );
        RBFUtils.verbose( "Seed               : ");
        if( reallySeed ) {
        	RBFUtils.verboseln( ""+seed);
        } else {
        	RBFUtils.verboseln( "No seed, i.e., pure random execution (set to "+seed+")" );
        }
        RBFUtils.verboseln( "Verbosity          : "+ RBFUtils.getVerbosity() );
        return ( trnFile!="" && tstFile!="" && outTrnFile!="" && outTstFile!="" && neuronsRate>0) ;
      } catch ( Exception e) {
        throw new InternalError ( "Error reading parameters: Possibly, one of them is lacking;"+
          " or the parameters file is not correctly formed\n"+ e.toString() );
      }
  }


  /**
   * <p>
   * Prints help on screen when user executes with argument 
   *  --help or -help or -h or -?
   * </p>
   * @return nothing
   */
  private static void doHelp() {
    System.out.println( "Usage: doEvRBF_Cl paramFile" );
    System.out.println( "       doEvRBF_Cl --help" );
    System.out.println( "       (doEvRBF_Cl can also be EvRBF_Cl.jar)" );
    System.out.println( "  Where: " );
    System.out.println( "   paramFile  Name of file containing the parameters according to Keel format." );
    System.out.println( "              Example of parameter file: " );
    System.out.println( "              algorithm = evrbf_cl" );
    System.out.println( "              popSize = 20" );
    System.out.println( "              tournamentSize = 2" );
    System.out.println( "              maxGenerations = 100" );
    System.out.println( "              neuronsRate = 0.1" );
    System.out.println( "              validationRate = 0.15" );
    System.out.println( "              replacementRate = 0.3" );
    System.out.println( "              xOverRate = 0.9" );
    System.out.println( "              mutatorRate = 0.1" );
    System.out.println( "              verbose = true" );
    System.out.println( "              inputData = \"sintetica.trn\" \"sintetica.tst\" ");
    System.out.println( "              outputData = \"result1.trn\" \"result1.tst\" \"result1.rbf\" " );

    System.out.println( "\n---\n"+
        "Author:  Victor Rivas  (vrivas@ujaen.es)\n"+
        "From:    Univ. of Jaen (Spain)\n"+
        "For:     Keel Project.\n\n" );
 }



   /**
    * <p>
    * Main Function: reads the parameters, creates the population, evolves it, gets the best individual, writes results and finishes.
    * </p>
    * @param args the Command line arguments. Only one is processed: the name of the file containing the	parameters
    */
    public static void main(String[] args) throws IOException{
        Rbfn red;
        try {
          // Help required
          if ( args.length>0 ) {
          	if ( args[0].equals( "--help" ) || args[0].equals( "-help" ) ||
            	 args[0].equals( "-h" )  || args[0].equals( "-?" )) {
                  doHelp();
                  return;
              }
          }

          System.out.println( "- Executing doEvRBF_Cl "+args.length );

          // Reading parameters
          String paramFile=(args.length>0)?args[0]:"parametros.txt";
          setParameters( paramFile );
          System.out.println( "    - Parameters file: "+paramFile );

          // Random generator setup
          Randomize.setSeed( (long) seed );

         //Reading training and tests datasets
          ProcDataset Dtrn = new ProcDataset(trnFile,true); 
          ProcDataset Dtst = new ProcDataset(tstFile,false);

          if (Dtrn.datasetType()==1) { //Clasification dataset
              System.out.println( "Classification Dataset");

              //Split training and validation data sets
              set_training_validation( Dtrn );

              // Initialize, train and set fitness of population
              population=new RbfnPopulation(popSize, Xtrn, nTrn, 
                                            nEnt, nSal, neuronsRate );
              population.trainLMS( Xtrn, Ytrn, nTrn, LMSLoops, 0.3 );
              population.setFitness_Cl( Xval, Yval, nVal, Dtrn.getnclasses()-1 );
              population.sort_population();
              // The evolutionary algorithm does start
              for ( int ng=0; ng< maxGenerations; ++ng ) {
                  population.selectIndividuals( (int) (popSize*replacementRate), tournamentSize );
                  population.applyOperators( xOverRate, mutatorRate );
                  population.trainLMS_subPop( Xtrn, Ytrn, nTrn, LMSLoops, 0.3 );
                  population.setFitness_Cl_subPop( Xval, Yval, nVal, Dtrn.getnclasses()-1 );
                  population.replaceIndividuals((int) (popSize*replacementRate) );
                  System.out.println( " Generation "+(ng+1)+", fitness "+population.individual(0).getFitness() );
              }

              // Training the final population one more time
              population.trainLMS( Xtrn, Ytrn, nTrn, LMSLoops, 0.3 );
              population.trainLMS( Xval, Yval, nVal, LMSLoops, 0.3 );

              //red.classificationTest(X,ndatos,yieldedResults,Dtrn.getnclasses()-1,0);
              //Dtrn.creaResultadosClasificacion(outTrnFile,auxY,yieldedResults);

              //TEST
              set_test( Dtst );
              //int [] yieldedResults = new int[nTst];
              population.setFitness_Cl( Xtst, Ytst, nTst, Dtrn.getnclasses()-1 );
              population.sort_population();
              //System.out.println( "Last order: ");
              //population.paint_sort( 5 );
              
              // Process to reduce the number of neurons 
              Rbfn bestNet=(Rbfn) population.individual( 0 ).clone();
              bestNet.setFitness_Cl( Xtst, Ytst, nTst, Dtrn.getnclasses()-1 );
              String [] indexes;
              boolean nextStep=true;
              while( nextStep ) {
                  nextStep=false;
                  i=0;	
                  indexes=bestNet.getIndexes();
                  while (i<indexes.length && indexes.length>1 ) {
                      Rbfn tmpNet=(Rbfn) bestNet.clone();
                      tmpNet.removeRbf(indexes[i]);
                      tmpNet.setFitness_Cl( Xtst, Ytst, nTst, Dtrn.getnclasses()-1 );
                      if( tmpNet.getFitness()>=bestNet.getFitness() ) {
                          bestNet=(Rbfn) tmpNet.clone();
                          nextStep=true;
                          indexes=bestNet.getIndexes();
                          } else {
                          ++i;
                          }
                      }
                  }
              
              //Writting Training results
              Dtrn.processClassifierDataset();
              int ndatos = Dtrn.getndata();
              Y = new double [ndatos][1];
              X = Dtrn.getX();
              int [] auxY;
              auxY = Dtrn.getC();
              for (i = 0; i < ndatos; i++)
                  Y[i][0]=auxY[i];
              //Building and training the net
              //neurons = Dtrn.getnclasses();
              int [] yieldedResults = new int[ndatos];
              bestNet.classificationTest(X,ndatos,yieldedResults,Dtrn.getnclasses()-1,0);
              Dtrn.generateResultsClasification(outTrnFile,auxY,yieldedResults);
              
              //Writting test results
              Dtst.processClassifierDataset();
              ndatos = Dtst.getndata(); 
              Y = new double [ndatos][1];
              yieldedResults = new int[ndatos];
              X = Dtst.getX();
              auxY = Dtst.getC();
              for (i = 0; i < ndatos; i++)
                  Y[i][0]=auxY[i];
              
              bestNet.classificationTest(X,ndatos,yieldedResults,Dtrn.getnclasses()-1,0);
              Dtst.generateResultsClasification(outTstFile,auxY,yieldedResults);
              
              RBFUtils.createOutputFile( "", outRbfFile );
              bestNet.paint( outRbfFile );
              
              // System.output
              System.out.println( "        Indiv. "+0+":\t"+
                      bestNet.getFitness()+"\t-\t"+bestNet.rbfSize()+"" );
              
              /*
              for (i=0; i<popSize; ++i ) {      
              Dtst.creaResultadosClasificacion(outTstFile,auxYtst,yieldedResults);
                RBFUtils.createOutputFile( "", outRbfFile );
                //population[i].paint( outRbfFile );
              }
              */
          } // IF to test whether it is a classification problem or not
          
          if (Dtrn.datasetType()==2) System.out.println( "Clustering");

          System.out.println( "- End of doEvRBF_Cl. "+
                "See results in output files named according to "+
                  paramFile+" parameters file." );

       } catch ( Exception e ) {
          	throw new InternalError(e.toString());
       }

    }

}


          /*
  
          if (Dtrn.datasetType()==0) {//Modelling Dataset
            //Training
              System.out.println( "Modeling Dataset");
              Dtrn.processModelDataset();
              nEnt = Dtrn.getninputs();
              nSal = 1;//PD.getnvariables()-nEnt;
              ndatos = Dtrn.getndata();
              Y = new double [ndatos][1];
              X = Dtrn.getX();
              double [] auxY;
              auxY = Dtrn.getY();
              for (i = 0; i < ndatos; i++)
                 Y[i][0]=auxY[i];
                 
              //Building and training the net
              red=new Rbfn(X,ndatos,nEnt,nSal,neurons);
              red.LMSTrain(X, Y, ndatos, LMSLoops, 0.3 );
              double [] yieldedResults = new double[ndatos];
              red.modellingTest(X,ndatos,yieldedResults);
              Dtrn.creaResultadosModelado(outTrnFile,auxY,yieldedResults);
  
              //TEST
              ProcDataset Dtst = new ProcDataset(tstFile,false);
              Dtst.processModelDataset();
              nEnt = Dtst.getninputs();
              nSal = 1;//PD.getnvariables()-nEnt;
              ndatos = Dtst.getndata();
              X = Dtst.getX();
              auxY = Dtst.getY();
              Y = new double [ndatos][1];
              for (i = 0; i < ndatos; i++)
                  Y[i][0]=auxY[i];
  
              yieldedResults = new double[ndatos];
              red.modellingTest(X,ndatos,yieldedResults);
              Dtst.creaResultadosModelado(outTstFile,auxY,yieldedResults);
              RBFUtils.createOutputFile( "", outRbfFile );
              red.paint( outRbfFile );
          }
              */
  

