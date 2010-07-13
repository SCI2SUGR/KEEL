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

package keel.Algorithms.RE_SL_Postprocess.Post_A_T_LatAmp_FRBSs;

/**
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */

import java.io.IOException;
import org.core.*;

public class Algorithm {

    myDataset train, val, test;
    String outputTr, outputTst;
    //int nClasses;
    
    //We may declare here the algorithm's parameters
    long seed;
    int iterations,tam_poblacion ,num_bits_gen;
    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public Algorithm() {
    }

    /**
     * It reads the data from the input files (training, validation and test) and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public Algorithm(parseParameters parameters) {

        train = new myDataset();
        val = new myDataset();
        test = new myDataset();
        try {
            System.out.println("\nReading the training set: " +
                               parameters.getTrainingInputFile());
            //train.readClassificationSet(parameters.getTrainingInputFile(), true);
            train.readRegressionSet(parameters.getTrainingInputFile(), true);
            
            System.out.println("\nReading the validation set: " +
                               parameters.getValidationInputFile());
            val.readRegressionSet(parameters.getValidationInputFile(), false);
            
            //val.readClassificationSet(parameters.getValidationInputFile(), false);
            System.out.println("\nReading the test set: " +
                               parameters.getTestInputFile());
            test.readRegressionSet(parameters.getTestInputFile(), false);

            //test.readClassificationSet(parameters.getTestInputFile(), false);
        } catch (IOException e) {
            System.err.println(
                    "There was a problem while reading the input data-sets: " +
                    e);
            somethingWrong = true;
        }

        //We may check if there are some numerical attributes, because our algorithm may not handle them:
        //somethingWrong = somethingWrong || train.hasNumericalAttributes();
        //somethingWrong = somethingWrong || train.hasMissingAttributes();

        outputTr = parameters.getTrainingOutputFile();
        outputTst = parameters.getTestOutputFile();

        //Now we parse the parameters, for example:
       
         seed = Long.parseLong(parameters.getParameter(0));
         iterations = Integer.parseInt(parameters.getParameter(1));
         tam_poblacion = Integer.parseInt(parameters.getParameter(2));
         num_bits_gen = Integer.parseInt(parameters.getParameter(3));
         
        //...

    }

    /**
     * It launches the algorithm
     * @param lanzar 
     */
    public void execute(Chc lanzar,String fich) {
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found, either the data-set have numerical values or missing values.");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } else {
            //We do here the algorithm's operations

            //nClasses = train.getnOutputs();
            
            //Finally we should fill the training and test output files
            doOutput(this.train, this.outputTr,lanzar);
            doOutput(this.test, this.outputTst,lanzar);
            EscribeBCLing e = new EscribeBCLing();
            e.write(fich, lanzar.getEc_tra(), lanzar.getEc_tst(), lanzar.getP().getE().base(), lanzar.getP());
            System.out.println("Algorithm Finished");
        }
    }

    /**
     * It generates the output file from a given dataset and stores it in a file
     * @param dataset myDataset input dataset
     * @param filename String the name of the file
     */
    private void doOutput(myDataset dataset, String filename, Chc lanzar) {
        String output = new String("");
        output = dataset.copyHeader(); //we insert the header in the outputa file
        //We write the output for each example
        for (int i = 0; i < dataset.getnData(); i++) {
       	//for regression:
			output += dataset.getOutputAsReal(i) + " " +(double)this.regressionOutput(dataset.getExample(i),lanzar) + "\n";
        }
        Fichero.escribeFichero(filename, output);
        
    }

    /**
     * It returns the algorithm classification output given an input example
     * @param example double[] The input example
     * @return String the output generated by the algorithm
     */
    //private String classificationOutput(double[] example) {
      //  String output = new String("?");
        /**
          Here we should include the algorithm directives to generate the
          classification output from the input example
         */

        //return output;
   // }


	/**
     * It returns the algorithm regresion output given an input example
     * @param example double[] The input example
     * @param  lanzar 
     * @return double the output generated by the algorithm
     */
    private double regressionOutput(double[] example, Chc lanzar) {
	    double output = 0.0;
	    	output=lanzar.getP().getE().base().FLC(example,lanzar.getP().getNreglasTotal());
	    	
	    //}
	    //FLC();
        /**
          Here we should include the algorithm directives to generate the
          classification output from the input example
         */
        return output;
    }    

}

