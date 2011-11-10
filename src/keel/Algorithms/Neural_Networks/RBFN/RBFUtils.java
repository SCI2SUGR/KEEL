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
 * RBFUtils.java

 /**
 * <p>
 * @author Writen by Victor Manuel Rivas Santos (University of Jaï¿½n) 22/07/2004
 * @author Modified by Marï¿½a Dolores Pï¿½rez Godoy (University of Jaï¿½n) 17/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
 
package keel.Algorithms.Neural_Networks.RBFN;
import org.core.*;


import java.lang.*;
import java.io.*;
import java.util.*;

public class RBFUtils {

    /**
     * <p>
     * Offers several utilities
     * </p>
     */ 
    
    static boolean verbosityValue=false;

   /**
    * <p>
    * Sets verbosity value from hashtable containing parameters
    * </p>
    * @param _hashtable The hashtable containing the parameters.
    * @return Verbosity value
    */
    
   
    public static boolean setVerbosity( Hashtable _hashtable ) {
    	return ( verbosityValue=_hashtable.containsKey ( "verbose" ) );
    }

   /**
    * <p>
    * Sets verbosity value from a given value
    * </p>
    * @param _value True or false
    * @return Verbosity value
    */
    public static boolean setVerbosity( boolean _value ) {
    	return ( verbosityValue=_value );

    }

   /**
    * <p>
    * Returns verbosity value
    * </p>
    * @return True or false
    */
    public static boolean getVerbosity() {
    	return verbosityValue;
    }

   /**
    * <p>
    * Prints the parameter without adding new line only if verbosity has been set to True.
    * </p>
    * @param _cad Parameter to be printed.
    */
	public static void verbose( String _cad )  {
		if (verbosityValue) { System.out.print( _cad ); }
	}

   /**
    * <p>
    * Prints the parameter and adds a new line only if verbosity has been set to True.
    * </p>
    * @param _cad Parameter to be printed.
    */
    public static void verboseln( String _cad )  {
		if (verbosityValue ) { System.out.println( _cad ); }
	}

   /**
    * <p>
    * Turns vector of strings into a double array.
    * </p>
    * @param _v The vector
    * @return The array containing the values turned into doubles.
    */
    public static double[] vector2doubles( Vector _v ) {
    	int tamanio=_v.size();
    	double[] toRet=new double[tamanio];
        for( int i=0; i<tamanio; ++i ) {
        	toRet[i]=Double.parseDouble( (String) _v.elementAt( i ));
        }
        return toRet;
    }
    
   /**
    * <p>
    * Turns vector of strings into two double arrays, one for inputs and other for outputs. It returns _input and _output because they change their values.
    * </p>
    * @param _v The vector
    * @param _inpDim Input dimension
    * @param _input Array in which INPUT  values are returned
    * @param _output Array in which OUTPUT values are returned
    */
    public static void vector2InputOutput( Vector _v, int _inpDim, double [] _input, double [] _output )  throws IOException {
        if( _input.length>0 ) {
            for( int i=0; i<_input.length; ++i ) {
                _input[i]=Double.parseDouble( (String) _v.elementAt( i ));
            }
        } else {
        	Exception e=new Exception( "Error: vector2InputOuput function: Dimension of inputs is 0!" );
            throw new InternalError(e.toString());
        }

        if ( _output.length>0 ) {
            for( int i=0; i<_output.length; ++i ) {
                _output[i]=Double.parseDouble( (String) _v.elementAt( i+_inpDim ));
            }
        } else {
        	Exception e=new Exception( "Error: vector2InputOuput function: Dimension of outputs is 0!" );
            throw new InternalError(e.toString());
        }
    }

   /**
    * <p>
    * Turns vector of strings into a double array containing only the inputs
    * </p>
    * @param _v The vector
    * @param _inpDim Input dimension
    * @return The array containing the values
    */
    public static double[] vector2Input( Vector _v, int _inpDim ) throws IOException {
    	if( _inpDim>0 ) {
        	double[] toRet=new double[_inpDim];
            for( int i=0; i<toRet.length; ++i ) {
            	toRet[i]=Double.parseDouble( (String) _v.elementAt( i ));
        	}
        	return toRet;
         } else {
            Exception e=new Exception( "Error: vector2Input function: Dimension of inputs is 0!" );
            throw new InternalError(e.toString());
        }

    }

   /**
    * <p>
    * Turns vector of strings into a double array containing only the outputs
    * </p>
    * @param _v The vector
    * @param _outDim Output dimension
    * @return The array containing the values
    */
    public static double[] vector2Output( Vector _v, int _outDim )  throws IOException {
        if( _outDim>0 ) {
            double[] toRet=new double[_outDim];
            for( int i=0; i<toRet.length; ++i ) {
                toRet[i]=Double.parseDouble( (String) _v.elementAt( (_v.size()-_outDim)+i ));
            }
            return toRet;
        } else {
            Exception e=new Exception( "Error: vector2Output function: Dimension of outputs is 0!" );
            throw new InternalError(e.toString());
        }
    }

   /**
    * <p>
    * Prints on console the elements of a double array.
    * </p>
    * @param _a The array
    */
    public static void printArray ( double [] _a ) {
    	System.out.println( array2string( _a) );
    }

   /**
    * <p>
    * Creates a string from an array of doubles
    * </p>
    * @param _a The array
    * @return The string.
    */
    public static String array2string ( double [] _a ) {
    	String toRet="";
    	for( int i=0; i<_a.length-1; ++i ) {
        	toRet+=Double.toString( _a[i] )+", ";
        }
		toRet+=_a[_a.length-1];
        return toRet;
    }

   /**
    * <p>
    * Computes the euclidean distance between two vector of doubles with equal size.
    * </p>
    * @param _a First vector
    * @param _b Second vector
    * @return A double that is the euclidean distance.
    */
    public static double euclidean( double[] _a, double [] _b ) {
    	double toRet=0;
		if( _a.length!=_b.length ) {
        	System.out.println( "ERROR: Euclidean can not be used if vectors have different size!" );
            toRet=-1;
        } else {
        	for( int i=0; i<_a.length; ++i ) {
            	toRet+=(_a[i]-_b[i])*(_a[i]-_b[i]);
            }
            toRet=Math.sqrt( toRet );
        }
        return toRet;
    }
    /**
     * <p>
     * Computes the average of the rows of matrix v
     * </p>
     * @param v The matrix
     * @return A vector with this average 
    */
    public static double [] medVect(double [][] v){
       int i,j;
       double [] toRet= new double [v.length];
       int numFil = v.length;
       int numCol = v[0].length;
       Rbf rbf;
       String [] vect;
       
       for(i=0;i<numCol;i++){
           toRet[i]=0;
       }
       
       for(i=0;i<numFil;i++){
          for(j=0;j<numCol;j++){
              toRet[j]+=v[i][j];
          }
       }
       for(i=0;i<numCol;i++){
           toRet[i]/=numFil;
       }
       return (toRet);
       
   }

      
   /**
    * <p>
    * Computes the maximun distance between vectors in a double[][]
    * </p>
    * @param _values The array containing the vectors
    * @return The maximun distance between vectors in a double[][]
    */

    public static double maxDistance( double [][] _values) {
    	double toRet=0;
        double distancia;
        int numDatos=_values.length;
        for( int i=0; i<numDatos; ++i ) {
        	for( int j=i+1; j<numDatos; ++j ) {
            	distancia=euclidean( _values[i],_values[j] );
            	toRet=(distancia>toRet)?distancia:toRet;
            }
        }
        return toRet;
    }

   /**
    * <p>
    * Computes the average distance between vectors in a double[][]
    * </p>
    * @param _values The array containing the vectors
    * @return The maximun distance between vectors in a double[][]
    */

    public static double avegDistance( double [][] _values) {
    	double toRet=0;
        int cont=0;
        int numDatos=_values.length;
        for( int i=0; i<numDatos; ++i ) {
        	for( int j=i+1; j<numDatos; ++j ) {
            	   toRet+=euclidean( _values[i],_values[j] );
            	   cont++;
            }
        }
        return (toRet/cont);
    }
    
    /**
     * <p>
     * Computes the geometric mean of the distance between the given center and the 2 nearest vectors in a double[][]
     * </p>
     * @param _values The array containing the vectors
     * @return The maximun distance between vectors in a double[][]
     */

     public static double geomDistance(double []center,int nCenter,double [][] _values) {
     	double toRet=0;
         int cont=0;
         double tmp;
         double min1,min2;
         int numDatos=_values.length;
         
         min1 = min2 = Double.MAX_VALUE;
         for( int i=0; i<numDatos; ++i ) {
        	 if(i!=nCenter){
        		 tmp = euclidean( _values[i],center );
        		 if((tmp < min1 || tmp < min2) && tmp != 0){
        			 if(min1 < min2)
        				 min2 = tmp;
        			 else
        				 min1 = tmp;
        		 }
        	 }
         }
         if(numDatos > 1)
        	 toRet = Math.sqrt(min1*min2);
         else if(numDatos == 1)
        	 toRet = min1;
         else
        	 toRet = avegDistance(_values);
         
         return (toRet);
     }
     
     /**
      * <p>
      * Computes the RMSdistance between the given center and the n nearest vectors in a double[][]
      * </p>
      * @param _values The array containing the vectors
      * @return The maximun distance between vectors in a double[][]
      */

      public static double RMSDistance(double []center,int nCenter,double [][] _values,int N) {
      	double toRet=0;
          int cont=0;
          double tmp;
          int numDatos=_values.length;
          Vector dist = new Vector();
          
          for( int i=0; i<numDatos; ++i ) {
         	 if(i!=nCenter){
         		 tmp = euclidean( _values[i],center );
         		 if(tmp!= 0)
         			 dist.addElement(new Double(tmp));
         	 }
          }
          Collections.sort(dist);
          for(int i=0;i<N && i<numDatos-1;i++){
        	  toRet += ((Double)dist.elementAt(i)).doubleValue();
          }
          toRet /= N;
          
          return (toRet);
      }
    
   /**
    * <p>
    * Reads a text file with parameters of the form name=value and returns a hastable containing them.
    * </p>
    * @param fileName Name of the file containing the parameters.
    * @return A hashtable with the parameters indexed by their names.
    */

    public static Hashtable parameters( String fileName ) {
    	Hashtable toRet=new Hashtable();
        StringTokenizer st = new StringTokenizer(Files.readFile( fileName ), "\n" );
     	while (st.hasMoreTokens()) {
        	StringTokenizer lin = new StringTokenizer(st.nextToken(), "=" );
            String nombre=lin.nextToken().trim();
            String valores=lin.hasMoreTokens()?lin.nextToken().trim():"" ;
            Vector vValores=new Vector();
            if ( valores!="" ) {
            	StringTokenizer val = new StringTokenizer(valores, " " );
                while( val.hasMoreTokens() ) {
                	vValores.add( val.nextToken().trim() );
                }
            }

        	toRet.put( nombre ,vValores );
     	}
        return toRet;
     }
     
    /**
     * <p>
     * Creates an output file following Keel rules: the header of the training/test file must be written into 
     * the result file.
     * </p>
     * @param _origin Name of the training/test file
     * @param _destiny Name of the output file
     */
     
     public static void createOutputFile( String _origin, String _destiny ) {
     	try {
            if ( _origin!="" ) {
                String linea="";
                BufferedReader in = new BufferedReader(new FileReader( _origin ));
                Files.writeFile( _destiny, "" );
                do {
                	if ( (linea = in.readLine()) != null  ) {
                    	Files.addToFile( _destiny, linea+"\n" );
                    	RBFUtils.verboseln( "Escrito: "+linea+"\n" );
                    }
                } while ( linea!="" &&   linea.compareTo( "@data")!=0 );
                in.close();

            } else {
                Files.writeFile( _destiny, "" );
            } 
        }catch ( IOException e) {
        }
     }
  
    /**
     * <p>
     * Returns the index of the higher value
     * </p>
     * @param _vector Vector the datos in which higer values will be find
     * @return Nothing.
     */
     
     public static int maxInVector( double [] _vector) {
     	int toRet=0;
			for( int j=0; j<_vector.length; ++j ) {
				if ( _vector[toRet]<_vector[j] ) {
					toRet=j;
				}
			}
			return toRet;
     }
}

