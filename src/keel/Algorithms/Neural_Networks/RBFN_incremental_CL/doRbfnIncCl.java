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

/*
 * doRbfnDec.java
 
/**
 * <p>
 * @author Writen by Maria Dolores Pérez Godoy, Antonio Rivera Rivas and Víctor Manuel Rivas Santos (University of Jaén) 19/03/2004
 * @author Modified by Víctor Rivas (University of Jaén)
 * @author Modified by María Dolores Pérez Godoy (University of Jaén) 17/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */


package keel.Algorithms.Neural_Networks.RBFN_incremental_CL;
import org.core.*;


import java.io.*;
import java.util.*;




public class doRbfnIncCl {
/**
 * <p>
 * This class allows the building of RBF neural networks with a decremental algorithm
 * This class contains a MAIN function that reads parameters, builds the net, and produces the results
 * yielded by the net when is applied to the test data set.
 * </p>
 */
	// Filename for training data set
    static String trnFile;

    // Filename for test data set
    static String tstFile;

    // Filename for results of RBFN on training set
    static String outTrnFile;

    // Filename for results of RBFN on test set
    static String outTstFile;

    // Filename for file where RBF will be written
    static String outRbfFile;
    
   // Minimun error to introduce a new RBF
    static double epsilon;

    // Minimun distance to introduce a new RBF
    static double delta;

    //Learning factor when a new unit is not allocated
    static double alfa;

    // Seed for random generator initialization.
    static double seed;

    // Seed must be used
    static boolean reallySeed;

    /** Does nothing. */
    public doRbfnIncCl() {
    }

   /**
    * <p>
    * Reads parameters from parameter file.
    * </p>
    * @param _fileName  Name of file with parameters.
    * @return True if everything goes right. False otherwise.
    */
    private static boolean setParameters( String fileName ) {
    	Hashtable parameters=RBFUtils.parameters( fileName );
        RBFUtils.setVerbosity( parameters );
        String tmp;
        tmp=((String) ((Vector) parameters.get ( "inputData" )).get( 0 ));
        trnFile=tmp.substring( 1, tmp.length()-1 ); // Character " must be removed.
        tmp=(String) ((Vector) parameters.get ( "inputData" )).get( 2 );
        tstFile=tmp.substring( 1, tmp.length()-1 ); // Character " must be removed.
        tmp=(String) ((Vector) parameters.get ( "outputData" )).get( 0 );
        outTrnFile=tmp.substring( 1, tmp.length()-1 ); // Character " must be removed.
        tmp=(String) ((Vector) parameters.get ( "outputData" )).get( 1 );
        outTstFile=tmp.substring( 1, tmp.length()-1 ); // Character " must be removed.
        tmp=(String) ((Vector) parameters.get ( "outputData" )).get( 2 );
        outRbfFile=tmp.substring( 1, tmp.length()-1 ); // Character " must be removed.
        epsilon= Double.parseDouble( (String) ((Vector) parameters.get ( "epsilon" )).get( 0 ));
        delta= Double.parseDouble( (String) ((Vector) parameters.get ( "delta" )).get( 0 ));
        alfa= Double.parseDouble( (String) ((Vector) parameters.get ( "alfa" )).get( 0 ));
        if ( parameters.containsKey ( "seed" ) ) {
        	reallySeed=true;
        	seed=(double) Double.parseDouble( (String) ((Vector) parameters.get ( "seed" )).get( 0 ));
        } else {
        	reallySeed=false;
        }
        RBFUtils.verboseln( "Training file      : "+trnFile );
        RBFUtils.verboseln( "Test file          : "+tstFile );
        RBFUtils.verboseln( "Ouput Training file: "+outTrnFile );
        RBFUtils.verboseln( "Ouput Test file    : "+outTstFile );
        RBFUtils.verboseln( "Ouput RBF file     : "+outRbfFile );
        RBFUtils.verboseln( "epsilon            : "+epsilon );
        RBFUtils.verboseln( "delta              : "+delta );
        RBFUtils.verboseln( "alfa               : "+alfa );
        RBFUtils.verbose( "Seed               : ");
        if( reallySeed ) {
        	RBFUtils.verboseln( ""+seed);
        } else {
        	RBFUtils.verboseln( "No seed, i.e., pure random execution");
        }
        RBFUtils.verboseln( "Verbosity          : "+ RBFUtils.getVerbosity() );
        return ( trnFile!="" && tstFile!="" && outTrnFile!="" && outTstFile!="" 
        && epsilon>0 && alfa>0 && delta>0) ;
    }


   /**
    * <p>
    * Prints help on screen when user executes with argument --help or -help or -h or -?
    * </p>
    * @return nothing
    */

    private static void doHelp() {
    	System.out.println( "Usage: doRbfnIncCl paramFile" );
        System.out.println( "       doRbfnIncCl --help" );
        System.out.println( "  Where: " );
        System.out.println( "   paramFile  Name of file containing the parameters, according to Keel format." );
        System.out.println( "              inputData = \"sintetica.trn\" \"sintetica.tst\" ");
        System.out.println( "              outputData = \"result1.trn\" \"result1.tst\" \"result1.rbf\" " );
        System.out.println( "\n---\nAuthors: Antonio Rivera (arivera@ujaen.es),  \n"+
        				    "         Loli Perez (lperez@ujaen.es), \n"+
        					"         Victor Rivas  (vrivas@ujaen.es)\n"+
                            "         Univ. of Jaen (Spain) for Keel Project.\n\n" );
    }

    /**
     * Main Function
     * @param args the Command line arguments. Only one is processed: the name of the file containing the
     *				parameters
     */


    public static void main(String[] args) throws IOException{
        double [][] X;
        double [][] Y;
        int nInpt,nOutpl,ndata,i,j;
        Rbfn net;
        try {

            // Help required
            if ( args.length>0 ) {
            	if ( args[0].equals( "--help" ) || args[0].equals( "-help" ) ||
              	 args[0].equals( "-h" )  || args[0].equals( "-?" )) {
                    doHelp();
                    return;
                }
            }

            System.out.println( "- Executing doRbfnInc "+args.length );
            // Reading parameters
            String paramFile=(args.length>0)?args[0]:"parameters.txt";
            setParameters( paramFile );
            System.out.println( "    - Parameters file: "+paramFile );
            // Random generator setup
            if ( reallySeed ) { Randomize.setSeed( (long) seed ); }
                        
           //Reading Training dataset
            ProcDataset Dtrn = new ProcDataset(trnFile,true);
            
            if (Dtrn.datasetType()==0) {//Modelling Dataset
                //Training
                System.out.println( "Modeling Dataset");
                Dtrn.processModelDataset();
                nInpt = Dtrn.getninputs();
                nOutpl = 1;//PD.getnvariables()-nInpt;
                ndata = Dtrn.getndata();
                Y = new double [ndata][1];
                X = Dtrn.getX();
                double [] auxY;
                auxY = Dtrn.getY();
                for (i = 0; i < ndata; i++)
                   Y[i][0]=auxY[i];
                //Building and training the net
                net=new Rbfn(nInpt,nOutpl);
                net.RAN(X,Y,ndata,delta,epsilon,alfa);
                double [] obtained = new double[ndata];
                net.testModeling(X,ndata,obtained);
                Dtrn.generateResultsModeling(outTrnFile,auxY,obtained);
                //TEST
                ProcDataset Dtst = new ProcDataset(tstFile,false);
                Dtst.processModelDataset();
                nInpt = Dtst.getninputs();
                nOutpl = 1;//PD.getnvariables()-nInpt;
                ndata = Dtst.getndata();
                X = Dtst.getX();
                auxY = Dtst.getY();
                Y = new double [ndata][1];
                for (i = 0; i < ndata; i++)
                    Y[i][0]=auxY[i];
                obtained = new double[ndata];
                net.testModeling(X,ndata,obtained);
                Dtst.generateResultsModeling(outTstFile,auxY,obtained);
                RBFUtils.createOutputFile( "", outRbfFile );
                net.printRbfn( outRbfFile );
            }
            if (Dtrn.datasetType()==1) { //Clasification dataset
                System.out.println( "Clasification Dataset");
                //Training
                Dtrn.processClassifierDataset();
                nInpt = Dtrn.getninputs();
                nOutpl = 1;//PD.getnvariables()-nInpt;
                ndata = Dtrn.getndata();
                Y = new double [ndata][1];
                X = Dtrn.getX();
                int [] auxY;
                auxY = Dtrn.getC();
                for (i = 0; i < ndata; i++)
                   Y[i][0]=auxY[i];
                //Building and training the net
                net=new Rbfn(nInpt,nOutpl);
                net.RAN(X,Y,ndata,delta,epsilon,alfa);
                int [] obtenido = new int[ndata];
                net.testClasification(X,ndata,obtenido,Dtrn.getnclasses()-1,0);
                Dtrn.generateResultsClasification(outTrnFile,auxY,obtenido);
                //TEST
                ProcDataset Dtst = new ProcDataset(tstFile,false);
                Dtst.processClassifierDataset();
                nInpt = Dtst.getninputs();
                nOutpl = 1;//PD.getnvariables()-nInpt;
                ndata = Dtst.getndata(); 
                Y = new double [ndata][1];
                obtenido = new int[ndata];
                X = Dtst.getX();
                auxY = Dtst.getC();
                for (i = 0; i < ndata; i++)
                    Y[i][0]=auxY[i];
                net.testClasification(X,ndata,obtenido,Dtrn.getnclasses()-1,0);
                Dtst.generateResultsClasification(outTstFile,auxY,obtenido);
                RBFUtils.createOutputFile( "", outRbfFile );
                //net.pinta( outRbfFile );
            } 
                
            if (Dtrn.datasetType()==2) 
                System.out.println( "This is not a clustering algorithm");
                
          
                
            System.out.println( "- End of doRbfnInc. See results in output files named according to "+
            					paramFile+" parameters file." );
               
         } catch ( Exception e ) {
        	 e.printStackTrace();
            	throw new InternalError(e.toString());
         }

    }


}

