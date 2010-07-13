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

package keel.Algorithms.Decision_Trees.SLIQ;
import java.io.*;

import keel.Dataset.Attributes;

/**
 * Clase base del algoritmo a implementar
 */
public abstract class Algorithm {
	/** Nombre del archivo que contiene la información para construir el modelo. */
	protected static String modelFileName = "";	 
	
	/** Nombre del archivo que contiene la información a usar para entrenamiento. */
	protected static String trainFileName = "";	 
	
	/** Nombre del archivo que contiene la información a usar para pruebas. */
	protected static String testFileName = "";	
	
	/** Nombre del archivo de salida del entrenamiento. */
	protected static String trainOutputFileName;
	
	/** Nombre del archivo de salida de las pruebas. */
	protected static String testOutputFileName;	
	
	/** Nombre del archivo de resultados. */
	protected static String resultFileName;		
	
	/** Conjunto de elementos clasificado correctamente. */
	protected int correct = 0;					
	
	/** Clasificados correctamente en las pruebas. */
	protected int testCorrect = 0;				
	
	/** El dataset modelo. */
	protected Dataset modelDataset;
	
	/** El dataset de entrenamiento. */
	protected Dataset trainDataset;				
	
	/** El dataset de pruebas. */
	protected Dataset testDataset;				
	
	/** Archivo de registro. */
	protected static BufferedWriter log;		
	
	/** Momento en que se pone en marcha el algoritmo. */
	protected long startTime = System.currentTimeMillis();
	
	/** Método de inicialización del tokenizador.
	 * 
	 * @param tokenizer		El tokenizador.
	 */
 	protected void initTokenizer(StreamTokenizer tokenizer) {
 		tokenizer.resetSyntax();         
 		tokenizer.whitespaceChars( 0, ' ' );    
 		tokenizer.wordChars( ' '+1,'\u00FF' );
 		tokenizer.whitespaceChars( ',',',' );
 		tokenizer.quoteChar( '"' );
 		tokenizer.quoteChar( '\''  );
 		tokenizer.ordinaryChar( '=' );
 		tokenizer.ordinaryChar( '{' );
 		tokenizer.ordinaryChar( '}' );
 		tokenizer.ordinaryChar( '[' );
 	  	tokenizer.ordinaryChar( ']' );
 	  	tokenizer.eolIsSignificant( true );
 	}
  	 

 	/** Método para obtener el nombre de la relación y los nombres, tipos y posibles valores
     *  de cada atributo del dataset.
  	 * 
  	 * @return El nombre y los atributos de la relación.
  	 */
	protected String getHeader() {
		String header;		
		header = "@relation "+Attributes.getRelationName()+"\n";
	    header += Attributes.getInputAttributesHeader();
	    header += Attributes.getOutputAttributesHeader();
	    header += Attributes.getInputHeader()+"\n";
	    header += Attributes.getOutputHeader()+"\n";
	    header += "@data\n";
			
		return header;
	}	
	
	/** Método para leer las opciones del archivo de ejecución y establecer los valores de configuración.
	 * 
	 * @param options 		El StreamTokenizer que lee el archivo de parámetros.
	 * 
	 * @throws Exception	Si el formato del archivo no es correcto.
	 */ 
	protected abstract void setOptions(StreamTokenizer options)  throws Exception;
	
    /** Evalúa el algoritmo y escribe los valores en el archivo.
     * 
     * @exception 	Si no es posible escribir en el archivo.
     */
	protected abstract void printResult() throws IOException;
	
    /** Evalúa el dataset de pruebas y escribe los resultdos en un archivo.
     * 
     * @exception 	Si no es posible escribir en el archivo.
     */
	protected abstract void printTest() throws IOException;
	
    /** Evalúa el dataset de entrenamiento y escribe los resultados en el archivo.
     * 
     * @exception 	Si no es posible escribir en el archivo.
     */
	protected abstract void printTrain() throws IOException;
}

