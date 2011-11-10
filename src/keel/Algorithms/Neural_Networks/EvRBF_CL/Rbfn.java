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
 * @file Rbfn.java
 * @author Written by Antonio Jesus Rivera Rivas (University of Jaen) 03/03/2004
 * @author Modified by Victor Manuel Rivas Santos (University of Jaen) 15/07/2004
 * @version 0.1
 * @since JDK1.5
 *</p>
 */

package keel.Algorithms.Neural_Networks.EvRBF_CL;

import org.core.*;
import java.util.*;
import java.io.*;

public class Rbfn implements Cloneable {
/**
 * <p>
 * Class representing a Radial Basis Function Neural Network for the EvRBF_CL algorithm
 * </p>
 **/

    /** Number of RBF neurons thet net contains. */
    int numRbfs;

    /**  Dimension of inputs */
    int nInputs;

    /**  Dimension of outputs (thus, number of output neuron) */
    int nOutputs;
    
    /** Whether it has been traind or not **/
    double fitness;

    /** Hashtable to store rbf neurons */
    Hashtable rbfn = new Hashtable();

   /**
    * <p>
    * Creates a instance of rbnf of fixed structure just for test.
    * </p>
    */
    public Rbfn() {

      this.numRbfs=0;
      this.nInputs=1;
      this.nOutputs=1;
      this.fitness=0;
      
      Rbf neurona=new Rbf(1,1);
      double [] aCenter=new double[1];
      double [] weights=new double[1];
      double aRadius;
      aCenter[0]=1; aRadius=0.3;weights[0]=0;
      neurona.setParam(aCenter, aRadius, weights);
      this.insertRbf((Rbf)neurona.clone());

      aCenter[0]=2; aRadius=0.2;weights[0]=0;
      neurona.setParam(aCenter, aRadius, weights);
      this.insertRbf((Rbf)neurona.clone());

      aCenter[0]=2.5; aRadius=0.1;weights[0]=0;
      neurona.setParam(aCenter, aRadius, weights);
      this.insertRbf((Rbf)neurona.clone());

      aCenter[0]=2.8; aRadius=0.3;weights[0]=0;
      neurona.setParam(aCenter, aRadius, weights);
      this.insertRbf((Rbf)neurona.clone());

      aCenter[0]=3; aRadius=0.1;weights[0]=0;
      neurona.setParam(aCenter, aRadius, weights);
      this.insertRbf((Rbf)neurona.clone());

      aCenter[0]=3.3; aRadius=0.2;weights[0]=0;
      neurona.setParam(aCenter, aRadius,weights);
      this.insertRbf((Rbf)neurona.clone());
    }



   /** 
    * <p>
    * Creates a new instance of rbfn
    * </p>
    * @param nInputs Input dimension
    * @param nOutputs Ouput dimension (thus, number of ouput neurons)
    */
    public Rbfn(int nInputs,int nOutputs) {
        this.numRbfs=0;
        this.nInputs=nInputs;
        this.nOutputs=nOutputs;
        this.fitness=0;
    }


   /** 
    * <p>
    * Creates a new instance of rbfn from a matrix of instances. Sets randomly the centres of the neurons and sets
    * its radius taking intro account the maximun distance between centres.
    * </p>
    * @param X Matrix of instances
    * @param ndatos Number of instaces in X
    * @param nEnt Number of imputs of the net
    * @param nSal Number of outputs of the net
    * @param nNeuro Number of hidden neurons the net will have.
    */
    public Rbfn(double [][] X,int ndatos,int nEnt,int nSal,int nNeuro) {
        // Setting instance variables
        this.numRbfs=0;
        this.nInputs=nEnt;
        this.nOutputs=nSal;
        this.fitness=0;
        int i,ran;
        
        //Constructing and adding _numNeurons RBF neurons
        double [] weights=new double[nOutputs];
        for(i=0; i<nOutputs; ++i ) { weights[i]=0; };
        double [][] aCenters=new double[nNeuro][nInputs];
        for(i=0; i<nNeuro; ++i ) {
						ran=(int)Randomize.Randint( 0,ndatos-1);
            aCenters[i]= X[ran];
        }

        //double aRadius=0.5*RBFUtils.maxDistance( aCenters );
				double aRadius=RBFUtils.avegDistance(aCenters)/2;
        aRadius=(aRadius<=0)?(double)Randomize.Randdouble(0,1):aRadius;
        for(i=0; i<nNeuro; ++i ) {
            Rbf neurona=new Rbf(nInputs,nOutputs);
            neurona.setParam(aCenters[i], aRadius, weights);
            this.insertRbf((Rbf)neurona.clone());
        }
    }



   /** 
    * <p>
    * Creates a new instance of rbfn from a matrix of instances. Sets randomly the centres of the neurons and sets
    * its radius taking intro account the average distance between centres.
    * </p>
    * @param nNeuro Number of hidden neurons the net will have.
    * @param X Matrix of instances
    * @param ndatos Number of instaces in X
    * @param nEnt Number of imputs of the net
    * @param nSal Number of outputs of the net
    */
    public Rbfn(int nNeuro,double [][] X,int ndatos,int nEnt,int nSal) {

        // Setting instance variables
        int ran,i,j,cont;
        int flag;
        int [] vaux = new int[ndatos];
        this.numRbfs=0;
        this.nInputs=nEnt;
        this.nOutputs=nSal;
        this.fitness=0;

        //Constructing and adding _numNeurons RBF neurons
        double [] weights=new double[nOutputs];
        for( i=0; i<nOutputs; ++i ) { weights[i]=0; };
        double [][] aCenters=new double[nNeuro][nInputs];
        for(i=0; i<nNeuro; ++i ) {
            cont=0;
            do{
                ran=(int)Randomize.Randint( 0, ndatos-1);
                flag=0;
                j=0;
                cont++;
                do{
                    if (vaux[j++]==ran) flag=1;
                }while((j<i)&&(flag==0));
            }while((flag==1)&&(cont<ndatos));    
            aCenters[i]=X[ran];
   	   }
        double aRadius=RBFUtils.avegDistance(aCenters)/2;
        aRadius=(aRadius<=0)?(double)Randomize.Randdouble(0,1):aRadius;
        for( i=0; i<nNeuro; ++i ) {
            Rbf neurona=new Rbf(nInputs,nOutputs);
            neurona.setParam(aCenters[i], aRadius, weights);
            this.insertRbf((Rbf)neurona.clone());
        }
    }
    
    
   /**
    * <p>
    * Clones a RBFN neural network
    * </p>
    */
    public Object clone  () {
        try{
            Rbfn rbfNet=(Rbfn)super.clone();
            rbfNet.numRbfs=0;
            rbfNet.nInputs=nInputs;
            rbfNet.nOutputs=nOutputs;
            rbfNet.fitness=fitness;
            rbfNet.rbfn=new Hashtable();
            String [] indexes=new String[numRbfs];
            indexes=getIndexes();
            for ( int i=0; i<indexes.length; ++i ) {      
              rbfNet.insertRbf( (Rbf)this.getRbf(indexes[i]).clone(), indexes[i] );
            }
            return (rbfNet); 
        }
        catch (CloneNotSupportedException e){
            throw new InternalError(e.toString());
        }
    }
  

   /** 
    * <p>
    * Deletes a neuron from the net
    * </p>
    * @param idRbf Identifier of neuron to delete
    */
    public void removeRbf(String idRbf){
        rbfn.remove(idRbf);
        numRbfs--;
    }

   /**
    * <p>
    * Adds a neuron to the net, assigning an automatic name
    * </p>
    * @param rbf The neuron to insert
    */
    public void insertRbf(Rbf rbf){
        //rbf.idRbf=String.valueOf(numRbfs);
        // Trying to set a unique identifier
        rbf.idRbf=RBFUtils.createIdRbf();
        rbfn.put(rbf.idRbf,rbf);
        numRbfs++;
    }

    
   /**
    * <p>
    * Adds a neuron to the net with a given name
    * </p>
    * @param rbf The neuron to insert
    * @param _key Its name     
    */
    public void insertRbf(Rbf rbf, String _key ){
        rbf.idRbf=_key;
        rbfn.put(rbf.idRbf,rbf);
        ++numRbfs;
    }
    

   /** 
    * <p>
    * Changes a neuron in the net
    * </p>
    * @param idRbf Identifier of neuron to delete
    * @param rbf Neuron to insert
    */
    public void modifyRbf(Rbf rbf,String idRbf){
        removeRbf(idRbf);
        rbf.idRbf = idRbf;
        rbfn.put(rbf.idRbf,rbf);
        numRbfs++;
    }


   /**
    * <p>
    * To get the number of inputs
    * </p>
    * @return The number of INPUTS the net has
    */
   public int numInputs() {
    return nInputs;
   }

   /**
    * <p>
    * To get the number of outputs
    * </p>
    * @return The number of OUTPUTS the net has
    */
   public int numOutputs() {
    return nOutputs;
   }
        
   /** 
    * <p>
    * Returns the number of neurons in the net  
    * </p>
    * @return A integer that is the number of neurons of the net
    */
    public int rbfSize(){
        return (numRbfs);
    }


   /**
    * <p>
    * Gets an RBF from the net given its identifier
    * </p>
    * @param id RBF's identifier
    */
    public Rbf getRbf(String id){
        return ((Rbf)this.rbfn.get(id));
    }


   /**
    * <p>
    * Returns the list on index of the net neurons.
    * </p>
    */
    public String [] getIndexes(){
        String [] vect=new String[this.rbfSize()];
        Enumeration aEnum=this.rbfn.keys(); /*se obtienen las claves de las neuronas*/
        int i=0;
        while (aEnum.hasMoreElements()) {
            vect[i]=(String)aEnum.nextElement();
            i++;
        }

        return (vect);



    }


   /** 
    * <p>
    * Sets the fitness of a RBFN for classification problems    
    * </p>
    */
    public void setFitness_Cl(double [][] _X,  double [][] _Y, int _nDatos, int _nClases ) {
       try {
          int [] yielded=new int[_nDatos];
          int [] auxY=new int[_nDatos];
          for( int i=0; i<_nDatos; ++i) {
            auxY[i]=(int) _Y[i][0];
          }
          classificationTest( _X, _nDatos, yielded, _nClases, 0);
          double tmpDoub=(double) RBFUtils.computeMatches( auxY, yielded, _nDatos )/_nDatos;
					setFitness( tmpDoub );
        } catch (Exception e) {
          throw new InternalError(e.toString());
        }
    }

   /**
    * <p>
    * Sets the fitness of a RBF.
    * </p>
    * @param _fitness The value to be used for the fitness
    * @return The value _finess itself
    */
    public double setFitness( double _fitness ){
      if ( _fitness<0 ) {
        //System.err.println( "Trying to set a non-positive fitness to a RBF Neural net\n"):
        throw new InternalError("Trying to set a non-positive fitness to a RBF Neural net\n");
      } else {
        fitness=_fitness;
      }
      return fitness;
    }


   /**
    * <p>
    * Gets the fitness of a RBF.
    * </p>
    * @return The value of the finess
    */
    public double getFitness(){
      return fitness;
    }


   /**
    * <p>
    * Passes an input to the net obtaining its output
    * </p>
    * @param _input The sample
    * @return  The set of outputs provided by the net's output neurons.
    */
    public double [] evalRbfn(double [] _input ) {
      double [] aux = new double [nOutputs];
      int i;
      Enumeration it;
      Rbf rbf;

      for (i=0;i<nOutputs;i++)
      {
          aux[i]=0;
          it = rbfn.elements();
          while (it.hasMoreElements()) {
              rbf=(Rbf)it.nextElement();
              aux[i]+=rbf.evalRbf(_input)*rbf.weights[i];
          }
      }
      return (aux);
    }

   /**
    * <p>
    * Computes the difference between the ouput of the net and desired output
    * </p>
    * @param desiredOutput Desired output
    * @param netOutput Outpunt of the net
    * @return A vector of doubles with the difference between the output of the net and the desired output
    */
    public double [] errorRbfn(double [] desiredOutput,double [] netOutput) {
        int i;
        double [] error=new double[nOutputs];
        
        for (i=0;i<nOutputs;i++)
           error[i]=desiredOutput[i]-netOutput[i];
        return(error);
    }        
    
   /**
    * <p>
    * Returns the nearest rbf/neuron to a vector v (pattern)
    * </p>
    * @param v vector
    * @return The index (or key) of the closest neuron
    */
    public String closestRbf (double [] v)  {
        String clave="nula";
        double distmin=10000000;
        double dist;
        int i,ind;
        Rbf rbf;
        int nrbf=this.rbfSize(); /* Get the number of neurons composing the net*/
        String [] vect=this.getIndexes(); /* Get the indexes for neurons*/
        
        for( i=0; i<numRbfs; i++)  {
            rbf=getRbf( vect[i] );
            dist=rbf.euclideanDist(v);
            if (dist<distmin){
                distmin=dist;
                clave=vect[i];
            }    
        }
        return(clave);
    }
    

    
   /** 
    * <p>
    * Uses RAN algorithm to build a net
    * </p>
    * @param X matrix of inputs instances
    * @param Y matrix of outputs instances
    * @param ndatos Number of instances
    * @param epsilon minimun error to introduce a new RBF
    * @param delta minimun distance to introduce a new RBF
    * @param alfa learning factor when a new unit is not allocated
    */
    
    public void RAN(double [][] X, double [][] Y,int ndatos,double delta,double epsilon,double alfa) {
        try {
            int numVectorSeleccionado,i;
            String clave;
            double [] aCenter=new double[nInputs];
            double aRadius=1;
            double [] weights=new double[nOutputs];
            double dist;
            double [] patronEntrada = new double[nInputs];
            double [] patronSalida = new double[nOutputs];
            double [] netOutput = new double[nOutputs];
            double [] error = new double[nOutputs];
            double errori;
            int nEnt=nInputs;
            int nSal=nOutputs;
            Rbf rbf;
            int cont=0;
            
            //Inserts first RBF
            numVectorSeleccionado=(int)Randomize.Randint( 0, ndatos-1 );
            patronEntrada=X[numVectorSeleccionado];
            patronSalida=Y[numVectorSeleccionado];
            rbf=new Rbf(nInputs,nOutputs);
            rbf.setParam(patronEntrada, 1.75*delta , patronSalida);
            this.insertRbf((Rbf)rbf.clone());
            
            do{//Major Loop
                
                numVectorSeleccionado=(int)Randomize.Randint( 0, ndatos-1 );
                patronEntrada=X[numVectorSeleccionado];
                patronSalida=Y[numVectorSeleccionado];
                netOutput=this.evalRbfn( patronEntrada );
                error = this.errorRbfn(patronSalida,netOutput);
                clave = this.closestRbf (patronEntrada);    
                rbf=getRbf( clave );
                dist=rbf.euclideanDist(patronEntrada);
                errori=0;
                for (i=0;i<nOutputs;i++)
                    if (Math.abs(error[i])>epsilon) errori=error[i];
                
                if ((Math.abs(errori)>epsilon) && (dist>delta)){ //Major Condition
                    //Inserts a new Rbf
                    rbf=new Rbf(nInputs,nOutputs);
                    rbf.setParam(patronEntrada, 1.75*dist , error);
                    this.insertRbf((Rbf)rbf.clone());
                    cont = 0;
                }
                else{
                    //Perform gradient descent on aCenters and weights of the Rbf clave
                    aCenter=rbf.getCenter();
                    weights=rbf.getWeights();
                    aRadius=rbf.getRadius();
                    for (i=0;i<nOutputs;i++)
                         weights[i]=weights[i] + (alfa * errori * rbf.evalRbf(patronEntrada));
                    for (i=0;i<nInputs;i++)
                        aCenter[i]=aCenter[i] + ( 2 * (alfa/aRadius) * (patronEntrada[i]-aCenter[i]) *
                                            rbf.evalRbf(patronEntrada) * ( errori*weights[0]) );
                    rbf.setCenter(aCenter);
                    rbf.setWeights(weights);
                    cont++;
                }
            }while(cont<(ndatos));
        } catch ( Exception e ) {
            throw new InternalError(e.toString());
       }
  }
    
   /**
    * <p>
    * Uses a decremental algorithm to buid a net. After initializing and training (with LMS)
    * a net with several neurons, the algorithm in the major loop deletes the neurons with the
    * lowest weight and train the net.
    * </p>
    * @param X matrix of inputs instances
    * @param Y matrix of outputs instances
    * @param ndatos Number of instances
    * @param percent Percent under the average of the weights to delete a neuron
    * @param alfa Learnig factor of LMS algorithm
    */
    public void decremental(double [][] X, double [][] Y,int ndatos,double percent,double alfa) {
        
    int i,j,k,cont=0,nrbf,flag;
    double [] aCenter=new double[nInputs];
    double [] weights=new double[nOutputs];
    double [] medPesos=new double[nOutputs];
    double [] patronEntrada = new double[nInputs];
    double [] patronSalida = new double[nOutputs];
    double [] netOutput = new double[nOutputs];
    double [] error = new double[nOutputs];
    double errori;
    String [] vect;
    double aRadius;
    Rbf rbf;
    double peso,pesomed;
    double [][] vaux = new double[numRbfs][nOutputs];
    
    nrbf=this.rbfSize(); 
    vect=this.getIndexes(); 
    this.LMSTrain(X,Y,ndatos,10,alfa);     
    try {
        do{
           pesomed=0;
           for (i=0;i<nrbf;i++){
               rbf=getRbf(vect[i]);
               for (j=0;j<nOutputs;j++)
                   vaux[i][j]=Math.abs(rbf.getWeight(j));
           }
           medPesos=RBFUtils.medVect(vaux);
           for (j=0;j<nOutputs;j++) {
               if (medPesos[j]>pesomed) pesomed=medPesos[j];
           }
           for (i=0;i<nrbf;i++){
                rbf=getRbf( vect[i] );
                peso=0;
                for(j=0;j<nOutputs;j++) {
                   if (Math.abs(rbf.getWeight(j))>peso) peso=Math.abs(rbf.getWeight(j));
                }
                if (Math.abs(peso)<(percent*pesomed)) {
                    removeRbf(vect[i]);
                    cont=0;
                }
           }
           vect=this.getIndexes();
           nrbf=this.rbfSize();
           for(j=0;j<nrbf;j++){      
                rbf=getRbf( vect[j] );
                weights=rbf.getWeights();
                peso=0;
                for(k=0;k<nOutputs;k++)
                    if (weights[k]>peso) peso=weights[k];
                if (peso<pesomed) {
                    aCenter=rbf.getCenter();                    
                    aRadius=rbf.getRadius();
                    for(k=0;k<nInputs;k++) {
                        aRadius+=Randomize.Randdouble(-aRadius*0.05,aRadius*0.05);
                        aCenter[k]+=Randomize.Randdouble(-aRadius*0.05,aRadius*0.05);
                    }
                    rbf.setRadius(aRadius);    
                    rbf.setCenter(aCenter);                    
                }
           }          
         this.LMSTrain(X,Y,ndatos,5,alfa);       
         cont++;
        }while((cont<10)&&(nrbf>0));    
        
        } catch ( Exception e ) {
            throw new InternalError(e.toString());
        }
    }
    

   /**
    * <p>
    * Uses LMS to train the net.
    * </p>
    * @param X matrix of inputs instances
    * @param Y matrix of outputs instances
    * @param ndatos Number of instances
    * @param iter  Number of times the set of samples will be used.
    * @param alfa  Learning factor.
    */
    public void LMSTrain(double [][] X, double [][] Y,int ndatos,int iter,double alfa){
    	try {
            int i,j,z,nrbf;
            double [] error = new double[nOutputs];
            double modulo;
            double peso;
            Rbf rbf;
            double [] evaluacion; 
            double [] entradaRed;
            double [] desiredOutput;
            double [] netOutput;
            int [] aleat=new int[ndatos];
            int nEnt=nInputs;
            int nSal=nOutputs;

            String [] vect=this.getIndexes();
            nrbf=vect.length;
            //System.out.println( "PIntando red con "+this.rbfSize()+" neuronas " );  
            //this.paint();
            evaluacion=new double[nrbf];
            RBFUtils.verboseln( "Training RBFNN using LMS" );
            for (z=0;z<iter;z++){
                int tmpNDatos=ndatos;
                RBFUtils.verboseln( " - LMS iteration num. "+(z+1) );
                RBFUtils.verboseln( " - Num. Muestras: "+ tmpNDatos );
                for (i=0; i<tmpNDatos; i++){ 
                    aleat[i]=i;
                }
                while (tmpNDatos>0){ 
                    int ran=(int)Randomize.Randint( 0, tmpNDatos-1 );
                    int numVectorSeleccionado=aleat[ran];
                    aleat[ran]=aleat[--tmpNDatos];
                    entradaRed=X[numVectorSeleccionado];
                    desiredOutput=Y[numVectorSeleccionado];
                    netOutput=this.evalRbfn( entradaRed );

                    for (i=0;i<nOutputs;i++)
                        error[i]=desiredOutput[i]-netOutput[i];

                    for (i=0;i<nOutputs;i++)
                    {
                        modulo=0.0;
                        
                        for (j=0;j<nrbf;++j){ 
                            rbf=getRbf( vect[j] );
                            evaluacion[j]=rbf.evalRbf( entradaRed );
                            modulo=modulo+evaluacion[j]*evaluacion[j];
                        }
                        modulo=Math.sqrt(modulo);
                        for (j=0;j<nrbf;j++){ 
                            rbf=getRbf( vect[j] );
                            peso=rbf.getWeight(i);
                            peso=peso+alfa*(error[i]*evaluacion[j]/modulo);
                            rbf.setWeight(i,peso);
                        }
                    }

                } 
            RBFUtils.verboseln( "Error conseguido: "+error[0] );
            }
        } catch ( Exception e ) {
            throw new InternalError(e.toString());
        }
    }



   /**
    * <p>
    * Evaluates the net for modeling problem
    * </p>
    * @param X matrix of inputs instances
    * @param ndatos Number of instances
    * @param yieldedResults Vector of results of the evaluation
    */
    public void modellingTest(double [][] X,int ndatos,double [] yieldedResults){
     int i;

     for(i=0; i<ndatos; i++){
         yieldedResults[i]=this.evalRbfn(X[i])[0];
     } 
    }

    

   /** 
    * <p>
    * Evaluates the net for clasification problem
    * </p>
    * @param X matrix of inputs instances
    * @param ndatos Number of instances
    * @param yieldedResults Vector of results of the evaluation
    * @param max Class maximun identifier
    * @param min Class minimun identifier
    */
    public void classificationTest(double [][] X,int ndatos,int [] yieldedResults,int max,int min) {
    	//System.out.println( "Test clasificaciÃ³n de red " );
			for(int i=0; i<ndatos; ++i){
					double tmpDoub=this.evalRbfn(X[i])[0];
					yieldedResults[i]= (int)Math.round(tmpDoub);
					// yieldedResults[i]= (int)RBFUtils.maxInVector(this.evalRbfn(X[i])); // Victor: 13-Oct-2005. Lo convierto en un metodo para n salidas 
					if (yieldedResults[i]>max) yieldedResults[i]=max;
					if (yieldedResults[i]<min) yieldedResults[i]=min;
					//System.out.println( "Para patrÃ³n i: " );
					//RBFUtils.printArray( X[i] );
					//System.out.println( " tmpDoub es "+tmpDoub+ " y yieldedResults es "+yieldedResults[i] );
			} 
		}

    

   /**
    * <p>
    * Prints net on a stdout
    * </p>
    */
    public void paint( ) {
		  this.paint( "" );
    }


   /**
    * <p>
    * Prints net on a file.
    * </p>
    * @param _fileName Name of the file.
    */
    public void paint( String _fileName ) {
    	int i;
      String ind;
      String [] indices=new String[6];
      indices=this.getIndexes();
      for (i=0;i<indices.length; ++i){
          ind=indices[i];
          if ( _fileName!="" ) {
          	Files.addToFile( _fileName,"Neuron: "+ind+"\n" );
          } else {
          	System.out.println("Neuron: "+ind );
          }
          Rbf neurona=this.getRbf(ind);
          neurona.paint( _fileName );
      }

    }

} /*enf of the class*/


