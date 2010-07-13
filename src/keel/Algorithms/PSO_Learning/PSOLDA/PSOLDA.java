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

package keel.Algorithms.PSO_Learning.PSOLDA;

/**
 * <p>Title: Algorithm PSOLDA</p>
 *
 * <p>Description: It contains the implementation of the algorithm PSOLDA</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */


import java.io.IOException;
import org.core.*;
import keel.Dataset.*;

public class PSOLDA{

    myDataset train, val, test;
    String outputTr, outputTst, outputFunctions;
    
    //parameters
    private long semilla;
    private int maxIterations;
    private int NumParticles;
    private int NumAttributes;
    private int NumInstances;
    private int IterationsNonImproving;
    private double vmax;
    private int numClasses;
    private double cognitiveWeight;
    private double socialWeight;
    private double inertiaFactor;
    private Particle BestParticle;
    private AD BestLDA;
    
    private Crono cronometro;
    
    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public PSOLDA() {
    }

    /**
     * It reads the data from the input files (training, validation and test) and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public PSOLDA(parseParameters parameters) {

        train = new myDataset();
        val = new myDataset();
        test = new myDataset();
        try {
            System.out.println("\nReading the training set: "+parameters.getTrainingInputFile());
            train.readClassificationSet(parameters.getTrainingInputFile(), true);
            System.out.println("\nReading the validation set: "+parameters.getValidationInputFile());
            val.readClassificationSet(parameters.getValidationInputFile(), false);
            System.out.println("\nReading the test set: "+parameters.getTestInputFile());
            test.readClassificationSet(parameters.getTestInputFile(), false);
        } catch (IOException e){
            System.err.println("There was a problem while reading the input data-sets: " + e);
            somethingWrong = true;
        }
        

        outputTr = parameters.getTrainingOutputFile();
        outputTst = parameters.getTestOutputFile();
        outputFunctions = parameters.getOutputFile(0);

        //Now we parse the parameters
        semilla = Long.parseLong(parameters.getParameter(0));
        maxIterations=Integer.parseInt(parameters.getParameter(1));
        IterationsNonImproving = Integer.parseInt(parameters.getParameter(2));
        NumParticles=Integer.parseInt(parameters.getParameter(3));
        vmax = Double.parseDouble(parameters.getParameter(4));
        cognitiveWeight = Double.parseDouble(parameters.getParameter(5));
        socialWeight = Double.parseDouble(parameters.getParameter(6));
        inertiaFactor = Double.parseDouble(parameters.getParameter(7));

        
        //inicializar parametros
        cronometro=new Crono();
        Randomize.setSeed(semilla);
        
        NumAttributes=train.getnInputs();
        NumInstances=train.getnData();
        numClasses=train.getnClasses();
        
        Particle.InitializeParameters(cognitiveWeight, socialWeight, inertiaFactor, vmax);
    }

    /**
     * It launches the algorithm
     */
    public void execute(){
        
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found, either the data-set have numerical values or missing values.");
            System.err.println("Aborting the program");
        } else {
            //We do here the algorithm's operations
            train.normalize();
            val.normalize();
            test.normalize();
            
            cronometro.inicializa();
            	PSOSLDA_Method();
            cronometro.fin();
                        
            //Finally we should fill the training and test output files
            double accTrain=doOutput(this.val, this.outputTr);
            double accTest=doOutput(this.test, this.outputTst);
            PrintOutputFunctions();

            
            //get original results for LDA to compare
            AD ldaAux=LDA_Method(train.getX());
            double accTrainLDA=trainAccuracyRate(train.getX(), ldaAux);
            double accTestLDA=testAccuracyRate(test.getX(), ldaAux);

        	
            System.out.print("\n\n************************************************");
            System.out.print("\nLDA\t=> Porcertanje acierto train:\t"+accTrainLDA);
            System.out.print("\nLDA\t=> Porcertanje acierto test:\t"+accTestLDA);
            System.out.print("\nLDA\t=> Numero de atributos inicial:\t"+NumAttributes);
            System.out.print("\nPSOLDA\t=> Porcertanje acierto train:\t"+accTrain);
            System.out.print("\nPSOLDA\t=> Porcertanje acierto test:\t"+accTest);
            System.out.print("\nPSOLDA\t=> Numero de atributos final:\t"+BestParticle.presentAttsBest());
            System.out.print("\nPSOLDA\t=> Tiempo:\t\t\t"+cronometro.tiempoTotal());
            System.out.print("\n************************************************\n\n");

            System.out.println("Algorithm Finished");
        }
    }
    
   
    //*********************************************************************
    //***************** PSOLDA Method *************************************
    //*********************************************************************
     
    private void PSOSLDA_Method(){
    	
    	AD lda;
    	Particle[] P=new Particle[NumParticles];
        Particle bestActual=new Particle(NumAttributes);
        Particle bestPrevious=new Particle(NumAttributes);
        double[][] ctoTrain, ctoTest;
        boolean mejoraItActual;
       
    	
        //inicializo las posiciones y velocidades aleatoriamente
        for(int i=0 ; i<NumParticles ; ++i){
            P[i]=new Particle(NumAttributes);
            P[i].randomInitialization();	  
        }
        
        
        int iter=0;
        int ItActOpt=0;

        do{
            mejoraItActual=false;
            bestActual.bestEvaluation=-1;		//the first particle will be the best of the swarm at start

            //1) evaluar el fitness de cada particula
            for(int i=0 ; i<NumParticles ; ++i){

            	//si tiene algun atributo...
            	if(P[i].presentAttsActual()>0){
                	// a) obtener discrimanantes con train y Pi
            		ctoTrain=train.removeAttributes(P[i].X, P[i].presentAttsActual());
            		lda=LDA_Method(ctoTrain);
            	
            		// b) calcular sobre test el fitness que se asigna a la particula
            		ctoTest=test.removeAttributes(P[i].X, P[i].presentAttsActual());
            		P[i].lastEvaluation=testAccuracyRate(ctoTest,lda);
            	}
            	
            	//si no tiene atributos...
            	else
            		P[i].lastEvaluation=-1;
            
            	
            	//2) actualizar Bp
                if(P[i].lastEvaluation>P[i].bestEvaluation)
                    P[i].setB(P[i].X,P[i].lastEvaluation, P[i].presentAttsActual());
                
                else if(P[i].lastEvaluation==P[i].bestEvaluation && P[i].presentAttsActual()<P[i].presentAttsBest())
                    P[i].setB(P[i].X,P[i].lastEvaluation, P[i].presentAttsActual());
                
                
                //3) actualizar Bg
                if(P[i].isBetter(bestActual))
                	bestActual=P[i].cloneParticle();
            }
            
            
            if(bestActual.isBetter(bestPrevious)){
                mejoraItActual=true;
            }
            
            
            
            //2) mover cada particula a su siguiente posicion
            for(int i=0 ; i<NumParticles ; ++i){
                 P[i].updateV(bestActual);
                 P[i].updateX();
            }
            
            //ver si en esta iteracion se mejoro el global
            if(mejoraItActual)
            	ItActOpt=0;
            else
            	ItActOpt++;
            
            
            bestPrevious=bestActual.cloneParticle();
		
        }while(++iter<maxIterations&&ItActOpt<IterationsNonImproving);
        
        
        //en bestActual tengo la mejor particula, ahora aplico LDA con bestP
    	ctoTrain=train.removeAttributes(bestActual.B, bestActual.presentAttsBest());
    	lda=LDA_Method(ctoTrain);
    	
    	BestParticle=bestActual.cloneParticle();
    	BestLDA=lda;
    }
    
    
    //*********************************************************************
    //***************** LDA Method ****************************************
    //*********************************************************************
    
    private AD LDA_Method(double[][] ctoTrain){
    	
        double[][] X = ctoTrain;	// Input data
        
        // marco con 1 la clase de cada instancia
        double Cbin[][] = new double[NumInstances][numClasses];
        for (int i=0;i<NumInstances;i++)
        	Cbin[i][train.getOutputAsInteger(i)]=1;
		
        //creo las funciones discrimantes
        AD adlin = new AD(X,Cbin);
        
        try {
            adlin.CalculaParametros();	//esto calcula los discriminates
        }catch (Exception e){}
		
    	return adlin;
    }
    
    
    //*********************************************************************
    //***************** Computes test accuracy ****************************
    //*********************************************************************
    
    private double testAccuracyRate(double[][]ctoTest, AD lda){
    	
    	double[][] X = ctoTest; // Input data
 		double aciertos=0;
         
         try{
                          
             for(int i=0 ; i<test.getnData() ; i++){
                 double[] resp=lda.distancias(X[i]);	//esto devuelve el resultado de cada discrimante para la instancia dada
                 int clase=lda.argmax(resp);			//esto devuelve la posicion del maximo discriminante
                 
                 if (clase==test.getOutputAsInteger(i))
                	 aciertos++;
             }
             
         }catch(Exception e){}
    	
    	return aciertos/test.getnData();
    }
    
    
    //*********************************************************************
    //***************** Computes train accuracy ***************************
    //*********************************************************************
    
    private double trainAccuracyRate(double[][]ctoTrain, AD lda){
    	
    	double[][] X = ctoTrain;	             	// Input data
 		double aciertos=0;
         
         try {
                          
             for(int i=0 ; i<train.getnData() ; i++){
                 double[] resp=lda.distancias(X[i]);	//esto devuelve el resultado de cada discrimante para la instancia dada
                 int clase=lda.argmax(resp);			//esto devuelve la posicion del maximo discriminante
                 
                 if (clase==train.getOutputAsInteger(i))
                	 aciertos++;
             }
             
         }catch (Exception e){}
    	
    	return aciertos/train.getnData();
    }
    
    
    //*********************************************************************
    //***************** To do the output files ****************************
    //*********************************************************************
    
    public void PrintOutputFunctions(){
    	
    	String cad="Warning: Examples should be standardized in the interval [0,1] to use these functions\n\n";
    	cad+="Selected attributes = {";
    	
        //atributos presentes
        for(int i=0 ; i<NumAttributes; ++i)
        	if(BestParticle.B[i]>0.5)
        		cad+=Attributes.getInputAttribute(i).getName()+", ";
        
        cad=cad.substring(0, cad.length()-2);
        cad+="}\n\n";
      
        String coef[]=new String[numClasses];
        try{
        	coef=BestLDA.Coeficientes();
        }catch(Exception e){}
        
        for(int i=0 ; i<numClasses ; ++i){
        	cad+="-----------------------------------------------------\n";
        	cad+="Discriminant function for class "+ train.getOutputValue(i) + "\n"+coef[i];
        }
        
        Fichero.escribeFichero(outputFunctions, cad);
    }
    
    
    /**
     * It generates the output file from a given dataset and stores it in a file
     * @param dataset myDataset input dataset
     * @param filename String the name of the file
     */
    private double doOutput(myDataset dataset, String filename) {
        
        double aciertos=0;
        
        String output = new String("");
        output = dataset.copyHeader(); //we insert the header in the output file
        //We write the output for each example
        for (int i = 0; i < dataset.getnData(); i++) {
            //for classification:
            output += dataset.getOutputAsString(i)+" "+this.classificationOutput(dataset.getExample(i)) + "\n";

            if(dataset.getOutputAsString(i).equals(this.classificationOutput(dataset.getExample(i))))
                aciertos++;
        }
        
        Fichero.escribeFichero(filename, output);

        return aciertos/dataset.getnData();
    }

    
    /**
     * It returns the algorithm classification output given an input example
     * @param example double[] The input example
     * @return String the output generated by the algorithm
     */
    private String classificationOutput(double[] example){
        
    	String output = "";
        double[] resp=null;
        
    	//quitar atributos de example
    	double[] exampleAux=new double[BestParticle.presentAttsBest()];
    	int pos=0;
    	
    	for(int i=0 ; i<NumAttributes ; ++i)
    		if(BestParticle.B[i]>0.5){
    			exampleAux[pos]=example[i];
    			pos++;
    		}

        try {
        	resp=BestLDA.distancias(exampleAux);	//esto devuelve el resultado de cada discrimante para la instancia dada
        }catch (Exception e){}

        int clase=BestLDA.argmax(resp);				//esto devuelve la posicion del maximo discriminante
        
        output=train.getOutputValue(clase);

        return output;
    }

}

