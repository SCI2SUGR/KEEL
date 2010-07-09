/**
 * <p>
 * @author Written by Rosa Venzala (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Hyperrectangles.EACH;

import java.util.StringTokenizer;
import org.core.*;

import java.io.*;
import keel.Dataset.*;
import java.util.Arrays;


public class Main {
/**
 * <p>
 * Main class of the Each algorithm
 * </p>
 */
	
    private String trainFile;
    private String evalationFile; 
    private String testFile;
    private String outTrainFile;
    private String outTestFile;
    private String outFile;
    private long seed;
    private double delta;
    private int second_chance;


    public Main() {
    }

    /**
     * <p>
     * Gets all the information from the parameters file.
     * First, reads the name of the train and test files.
     * After, reads where we want to store the results.
     * Finally, reads the algorithm's parameters.
     * </p>
     * @param nameFile Name of th parameter's file
     */
    private void initArguments(String nameFile) {
        StringTokenizer linea, datos;
        String fichero = Fichero.leeFichero(nameFile); //guardo todo el fichero como un String para procesarlo:
        String una_linea;
        linea = new StringTokenizer(fichero, "\n\r");
        linea.nextToken(); //Paso del nombre del algoritmo
        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //inputData
        trainFile = datos.nextToken();
	trainFile = datos.nextToken();
	//System.err.println(ficheroTrain);
       // ficheroEval = datos.nextToken(); //fichero de evaluaciï¿½
        testFile = datos.nextToken();
        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //outputData
        outTrainFile = datos.nextToken();
	//System.err.println(ficheroSalidatr);
        outTestFile = datos.nextToken();
        outFile = datos.nextToken();
	
	una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); 
	seed = Long.parseLong(datos.nextToken());
	
	una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //delta
        delta = Double.parseDouble(datos.nextToken());
	
	una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
	datos.nextToken(); 
	String Aux = datos.nextToken();
        second_chance = 1;//por defecto se utiliza second chance
        if (Aux.compareTo("NO") == 0){//es no
          second_chance = 0;
	  System.out.println("EACH GREEDY");
        }
	
    };

    /**
     * <p>
     * Starts the EACH algorithm
     * </p>
     */
    private void execute() {
	EACH each=new EACH(trainFile,testFile,outTrainFile,outTestFile,outFile,seed,delta,second_chance);
    }

    /**
     * <p>
     * Main program
     * </p>
     * @param args Name of the parameter's file<br/>
     * Formato:<br/>
     * <em>algorith = &lt;nombre algoritmo></em><br/>
     * <em>inputData = "&lt;fichero training&gt;" "&lt;fichero validacion&gt;" "&lt;fichero test&gt;"</em> ...<br/>
     * <em>outputData = "&lt;fichero training&gt;" "&lt;fichero test&gt;"</em> ...<br/>
     * <br/>
     * <em>seed = valor</em> (si se usa semilla)<br/>
     * <em>&lt;Descripcion1&gt; = &lt;valor1&gt;</em><br/>
     * <em>&lt;Descripcion2&gt; = &lt;valor2&gt;</em> ... (por si hay mas argumentos)<br/>
     */
    public static void main(String args[]) {
        Main mimain = new Main();
        mimain.initArguments(args[0]); //Solo cogere el primer argumento (nombre del fichero)
        System.err.println("Executing EACH");
        mimain.execute();
    }
 }   
    
   

