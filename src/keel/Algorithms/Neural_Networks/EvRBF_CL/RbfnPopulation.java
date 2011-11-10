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
 * @file RbfnPopulation.java
 * @author Written by Victor Manuel Rivas Santos (University of Jaen) 15/08/2007
 * @version 0.1
 * @since JDK1.5
 *</p>
**/
package keel.Algorithms.Neural_Networks.EvRBF_CL;

import org.core.*;
import java.util.*;
import java.io.*;

public class RbfnPopulation {
/**
* <p>
* Implements a population of Radial basis Function Neural Networks to be evolved with EvRBFN_CL
* </p>
*/
	/** Number of individuals thet population contains. */
  int popSize;

  /** The individuals **/
  Rbfn [] population;

  /** The individuals to who operators will be applied**/
  Rbfn [] subPopulation;

  /** Min and Max values for data (needed for operators) */
  double [] minValues,maxValues;

  double MUTATORS_INTERNAL_PROB=0.5;  

   /**
    * <p>
    * Creates a new instance of RbfnPopulation
    * </p>
    * @param _size Size of population
    */
    public RbfnPopulation(int _size ) {
        try {
          population=new Rbfn[_size];
          popSize=population.length;
        } 
        catch (Exception e) {
          throw new InternalError(e.toString());
        }
    }


    /** 
     * <p>
     * Creates and initializes a population using a set of data.   
     * </p>
     * @param _size Number of nets in the population
     * @param _X Set of input patterns
     * @param _ndatos Number of input patterns
     * @param _nEnt Dimension of input space
     * @param _nSal Dimension of output space
     * @param _neuronsPercentage Percentage of _ndatos used as upper boundary for the number of neurons
     */
    public RbfnPopulation(int _size, double [][] _X, 
                          int _ndatos,int _nEnt,int _nSal,
                          double _neuronsPercentage ) {
       try {
          RBFUtils.verboseln( "Initializing population of RBFNs with "+_size+" individuals ");
          population=new Rbfn[_size];
          popSize=population.length;
          int maxNeurons=(int)( _X.length*_neuronsPercentage);
          maxNeurons=(maxNeurons<2)?2:maxNeurons;

          for( int i=0; i< popSize; ++i ) {
            int nNeurons=(int)Randomize.Randint( 2, maxNeurons );
            population[i]=new Rbfn( _X , _ndatos , _nEnt, _nSal,nNeurons);
          }

          set_min_max_values( _X, _ndatos, _nEnt );

        } catch (Exception e) {
          throw new InternalError(e.toString());
        }
    }

   /**
    * <p>
    * Setting the mix and max values for data (needed for operators)
    * </p>
    * @param _X Data
    * @param _ndatos Number of data (could differ from _X.length)
    * @param _nEnt Input dimension
    */
  public void set_min_max_values(double [][] _X, int _ndatos, int _nEnt) {
    minValues=new double[_nEnt];
    maxValues=new double[_nEnt];
    for (int i = 0; i < _nEnt; ++i) {
      maxValues[i]=minValues[i]=_X[0][i];
    }
    for (int j = 1; j < _ndatos; ++j) {
      for (int i = 0; i <_nEnt ; ++i) {
        minValues[i]=(minValues[i]>_X[j][i])?_X[j][i]:minValues[i];
        maxValues[i]=(maxValues[i]<_X[j][i])?_X[j][i]:maxValues[i];
      }
    }
  }

   /** 
    * <p>
    * Trains a Population of RBFNs
    * </p>
    * @param _X Set ot input patterns
    * @param _Y Set of output values
    * @param _nDatos Number of patterns in _X
    * @param _LMSLoops NUmber of iterations for LMS
    * @param _delta Delta parameter for LMS
    */
    public void trainLMS(double [][] _X,  double [][] _Y, 
                    int _nDatos, int _LMSLoops, double _delta ) {
       try {
          RBFUtils.verboseln( "Training population of RBFNs with "+popSize+" individuals ");
          for( int i=0; i< popSize; ++i ) {
            population[i].LMSTrain(_X, _Y, _nDatos, _LMSLoops, _delta );
          }
        } catch (Exception e) {
          throw new InternalError(e.toString());
        }
    }

    
   /** 
    * <p>
    * Sets the fitness of a Population of RBFNs for classification problems  
    * </p>
    * @param _X array containing the input patterns
    * @param _Y array containing the desired output (although only first column is used)
    * @param _nDatos Number of patterns
    * @param _nClases Number of diferent classes
    */
    public void setFitness_Cl(double [][] _X,  double [][] _Y, int _nDatos, int _nClases ) {
       try {
          int [] yielded=new int[_nDatos];
          int [] auxY=new int[_nDatos];
          for( int i=0; i<_nDatos; ++i) {
            auxY[i]=(int) _Y[i][0];
          }

          RBFUtils.verboseln( "Setting the fitness of "+popSize+" individuals ");
          for( int i=0; i< popSize; ++i ) {
            population[i].classificationTest( _X, _nDatos, yielded, _nClases, 0);
            double tmpDoub=(double) RBFUtils.computeMatches( auxY, yielded, _nDatos )/_nDatos;
            population[i].setFitness( tmpDoub );
          }
        } catch (Exception e) {
          throw new InternalError(e.toString());
        }
    }


   /**
    * <p>
    * Trains a Population of RBFNs
    * </p>
    * @param _X Set ot input patterns
    * @param _Y Set of output values
    * @param _nDatos Number of patterns in _X
    * @param _LMSLoops NUmber of iterations for LMS
    * @param _delta Delta parameter for LMS
    */
    public void trainLMS_subPop(double [][] _X,  double [][] _Y, 
                    int _nDatos, int _LMSLoops, double _delta ) {
       try {
          RBFUtils.verboseln( "Training supPopulation of RBFNs with "+subPopulation.length+" individuals ");
          for( int i=0; i< subPopulation.length; ++i ) {
            subPopulation[i].LMSTrain(_X, _Y, _nDatos, _LMSLoops, _delta );
          }
        } catch (Exception e) {
          throw new InternalError(e.toString());
        }
    }

   /**
    * <p>
    * Sets the fitness of a Sub-Population of RBFNs for classification problems 
    * </p>
    * @param _X array containing the input patterns
    * @param _Y array containing the desired output (although only first column is used)
    * @param _nDatos Number of patterns
    * @param _nClases Number of diferent classes
    */
    public void setFitness_Cl_subPop(double [][] _X,  double [][] _Y, int _nDatos, int _nClases ) {
       try {
          int [] yielded=new int[_nDatos];
          int [] auxY=new int[_nDatos];
          for( int i=0; i<_nDatos; ++i) {
            auxY[i]=(int) _Y[i][0];
          }
          
          RBFUtils.verboseln( "Setting the fitness of "+subPopulation.length+" individuals ");
          for( int i=0; i< subPopulation.length; ++i ) {
            subPopulation[i].classificationTest( _X, _nDatos, yielded, _nClases, 0);
            subPopulation[i].setFitness( (double) RBFUtils.computeMatches( auxY, yielded, _nDatos )/_nDatos );
          }
        } catch (Exception e) {
          throw new InternalError(e.toString());
        }
    }


   /**
    * <p>
    * Creates a subPopulation of individuals using the tournament method.
    * </p>
    * @param _subPopSize  Number of individuals to select
    * @param _tournamentSize  Number of individuals to perform the tournament
    */ 
  public void selectIndividuals( int _subPopSize, int _tournamentSize ) {
    // Setting the parameters to correct numbers
    _subPopSize=(_subPopSize<1)?1:_subPopSize;
    _subPopSize=(_subPopSize>popSize)?popSize:_subPopSize;
    _tournamentSize=(_tournamentSize<2)?2:_tournamentSize;
    _tournamentSize=(_tournamentSize>popSize)?popSize:_tournamentSize;

    // Initializing subPopulation
    subPopulation=new Rbfn[_subPopSize];

    // Selecting the individuals
    for (int i=0; i<_subPopSize; ++i ) {
      // select _tournamentSize individuals
      int selected=(int)Randomize.Randint( 0,popSize-1);
      for( int j=1; j<_tournamentSize; ++j ){
        int ran=(int)Randomize.Randint( 0,popSize-1);
        selected=(population[selected].getFitness()>population[ran].getFitness())?selected:ran;
      }
      //System.out.println( "Seleccionado por torneo "+selected );
      subPopulation[i]=(Rbfn) population[selected].clone();
    }
  }


   /**
    * <p>
    * Applies operator according to their probabilities
    * </p>
    * @param _xOverRate Probability for XOver operators
    * @param _mutatorRate Probability for Mutation operators
    */ 
    public void  applyOperators( double _xOverRate, double _mutatorRate ) {
        for (int i = 0; i < subPopulation.length; ++i) {
            double ran=(double) Randomize.Randdouble( 0, _xOverRate+_mutatorRate );
            if ( ran<=_xOverRate ) {
                // XOver operators
                //x_fix( subPopulation[i] );
           } else {
                // Mutation operators
                switch( (int) Randomize.Randint( 0,4 ) ) {
                    case 0: c_random( subPopulation[i] ); break;
                    case 1: r_random( subPopulation[i] ); break;
                    case 2: deleter( subPopulation[i] ); break;
                    case 3: adder( subPopulation[i] ); break;
                } 
            }
        }
    }

   /**
    * <p>
    * Removes worse _numIndividuals nets from Population and includes individuals from subpopulation
    * </p>
    * @param _numIndividuals Number of indivuals to replace
    */
    public void replaceIndividuals( int _numIndividuals ){
        _numIndividuals=(_numIndividuals>subPopulation.length)?subPopulation.length:_numIndividuals;
        sort_population();
        for (int i = 0; i < _numIndividuals; ++i) {
            population[popSize-i-1]=(Rbfn) subPopulation[i].clone();
            }
        sort_population();
        //System.out.println( " - Best fitness: " +population[0].getFitness() );
    }

   /**
    * <p>
    * Shows the _size first individuals (sorted by fitness)
    * </p>
    * @param _size Number of individuals to show
    */
    public void paint_sort(int _size ) {
        System.out.println( "Sorted population" );
        for( int i=0; i<((popSize>_size)?_size:popSize); ++i ){
            System.out.println( "        Indiv. "+i+":\t"+
                    population[i].getFitness()+"\t-\t"+population[i].rbfSize()+"" );
            }
    }
    

   /**
    * <p>
    * Sorts population
    * </p>
    */
    public void sort_population() {
        Rbfn tmpNet;
        for( int i=0; i<popSize; ++i ){
            for (int j = i+1; j <popSize ; ++j) {
                if( population[i].getFitness()<population[j].getFitness() ||
                        (population[i].getFitness()==population[j].getFitness() && 
                        population[i].rbfSize()>population[j].rbfSize()) ) {
                    tmpNet=population[i];
                    population[i]=population[j];
                    population[j]=tmpNet;
                }
            }
        }
    }

   /**
    * <p>
    * Performs the X_FIX crossover operator: replaces numNeurons neurons from
    * _net, taking numNeurons from a randomly chosen net.
    * </p>
    * @param _net The net to be modified
    */
    public void x_fix( Rbfn _net ) {
        try {
            // Select an individual to XOver
            Rbfn tmpNet=population[(int) Randomize.Randint( 0, population.length )];
            if (_net.rbfSize()<1 || tmpNet.rbfSize()<1 ) {
                throw new InternalError("Trying to apply x_fix operator to a net with less than 1 neuron!\n");
                }

            // Select the number of neurons to interchange; Range: [1,MinNumber of neurons]
            int numNeurons=(int) Randomize.Randint( 0, ((_net.rbfSize()<tmpNet.rbfSize() )?_net.rbfSize():tmpNet.rbfSize()) )+1;

            // sp stands for starting-point
            int sp1=(int) Randomize.Randint( 0, _net.rbfSize()-numNeurons );
            int sp2=(int) Randomize.Randint( 0, tmpNet.rbfSize()-numNeurons );

            // Remove neurons from _net
            String [] indexes=_net.getIndexes();
            for( int i=sp1; i<sp1+numNeurons; ++i ) {
                _net.removeRbf( indexes[i] );
            }

            // Add neurons from tmpNet
            indexes=tmpNet.getIndexes();
            for( int i=sp2; i<sp2+numNeurons; ++i ) {
                // Victor Rivas: 21-Aug-2007  (my mark: ????)
                // For simplicity (and lack of time) reasons, I give a new id to the neuron
                // since I am getting null neurons when inserting RBF with its id existing in the net
                // This could (should) be reviewed.
                _net.insertRbf( (Rbf) tmpNet.getRbf( indexes[i] ).clone(), RBFUtils.createIdRbf() );
            }
        } catch (Exception e){
            throw new InternalError(e.toString()); 
            }
    } // X_FIX


   /**
    * <p>
    * Performs the C_RANDOM mutator operator: modifies MUTATORS_INTERNAL_PROB % of the centers of the net
    * </p>
    * @param _net The net to be modified
    */
    public void c_random( Rbfn _net ) {
        try {
            // Victor Rivas: 21-Aug-2007  (my mark: ????)
            // If X_FIX is changed, then review this method so that the RBF 
            // (once modified) have a new id
            String [] indexes=_net.getIndexes();
            for( int i=0; i<indexes.length; ++i ) {
                if( Randomize.Randdouble(0,1)<MUTATORS_INTERNAL_PROB ) { // Apply operator
                    Rbf tmpNeuron=_net.getRbf(indexes[i]);
                    double [] centers=tmpNeuron.getCenter();
                    for (int j = 0; j <centers.length ; ++j) {
                        centers[j]=(double)Randomize.Randdouble(minValues[j],maxValues[j]);
                        }
                    tmpNeuron.setCenter( centers );
                }
            }
        } catch (Exception e){
            throw new InternalError(e.toString());
            }
    } // C_RANDOM

   /**
    * <p>Performs the R_RANDOM mutator operator: modifies 50% of the Radius of the net</p>
    * @param _net The net to be modified
    */
    public void r_random( Rbfn _net ) {
        try {
            // Victor Rivas: 21-Aug-2007  (my mark: ????)
            // If X_FIX is changed, then review this method so that the RBF 
            // (once modified) have a new id
            String [] indexes=_net.getIndexes();
            for( int i=0; i<indexes.length; ++i ) {
                if( Randomize.Randdouble(0,1)<MUTATORS_INTERNAL_PROB ) { // Apply operator
                    Rbf tmpNeuron=_net.getRbf(indexes[i]);
                    double radius=(double)Randomize.Randdouble(0.5,10); // ???? porquÃ© 0.5 y 10??
                    tmpNeuron.setRadius( radius );
                }
            }
        } catch (Exception e){
            throw new InternalError(e.toString());
            }
    } // R_RANDOM


   /**
    * <p>
    * Performs the DELETER mutator operator: modifies C_DELETER% of the Radius of the net
    * </p>
    * @param _net The net to be modified
    */
    public void deleter( Rbfn _net ) {
        try {
            String [] indexes=_net.getIndexes();
            for( int i=0; i<indexes.length; ++i ) {
                if( Randomize.Randdouble(0,1)<MUTATORS_INTERNAL_PROB ) { // Apply operator
                    if ( _net.rbfSize()>1 ) {_net.removeRbf(indexes[i]);};
                }
            }
        } catch (Exception e){
            throw new InternalError(e.toString());
            }
    } // DELETER

   /**
    * <p>
    * Performs the DELETER mutator operator: modifies C_DELETER% of the Radius of the net
    * </p>
    * @param _net The net to be modified
    */
    public void adder( Rbfn _net ) {
        try {
            String [] indexes=_net.getIndexes();
            int nEnt=_net.numInputs();
            int nSal=_net.numOutputs();
            for( int i=0; i<indexes.length; ++i ) {
                if( Randomize.Randdouble(0,1)<MUTATORS_INTERNAL_PROB ) { // Apply operator
                    Rbf newNeuron=new Rbf( nEnt, nSal );
                    double [] aCenter=new double[nEnt];
                    for ( int j=0; j<nEnt; ++j ) {
                        aCenter[j]=Randomize.Randdouble( minValues[j], maxValues[j] );
                    }
                    newNeuron.setCenter( aCenter );
                    //radius is 1/2 the eucidean distance to the neuron
                    newNeuron.setRadius( RBFUtils.euclidean( newNeuron.getCenter(), _net.getRbf( indexes[i] ).getCenter() )/2 );
                    _net.insertRbf( newNeuron );
                }
            }
        } catch (Exception e){
            throw new InternalError(e.toString());
            }
    } // ADDER


   /**
    * <p>
    * Prints the ppulation on a stdout
    * </p>
    */
    public void paint( ) {
        this.paint( "" );
    }


   /**
    * <p>
    * Prints the pipulation on a file.
    * </p>
    * @param _fileName Name of the file.
    */
    public void paint( String _fileName ) {
        if ( _fileName!="" ) {
            Files.addToFile( _fileName,"Printing population of RRBFNs\n" );
            } 
        else {
            System.out.println("Printing population of RRBFNs\n");
        }
        for (int i = 0; i < popSize; ++i) {
            population[i].paint( _fileName );
        }
    }


   /**
    * <p>
    * Returns the _num-th individuals
    * </p>
    * @param _num Position of the individual to return
    * @return The _num-th individual of the population
    */
    public Rbfn individual( int _num ) {
        try {
            return population[_num];
        } 
        catch (Exception e) {
            throw new InternalError(e.toString());
        }
    }
    
} /*end of the class*/



