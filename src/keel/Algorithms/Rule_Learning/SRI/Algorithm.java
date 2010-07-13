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

package keel.Algorithms.Rule_Learning.SRI;

/**
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernandez
 * @version 1.0
 */

import java.io.IOException;
import org.core.*;
import java.util.*;
import keel.Dataset.*;

public class Algorithm {

    myDataset train, val, test;
    String outputTr, outputTst, outputReglas;
    int BeamWidth;
    int minPos;
    int minNeg;
    //int nClasses;

    //We may declare here the algorithm's parameters

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
            train.readClassificationSet(parameters.getTrainingInputFile(), true);
            System.out.println("\nReading the validation set: " +
                               parameters.getValidationInputFile());
            val.readClassificationSet(parameters.getValidationInputFile(), false);
            System.out.println("\nReading the test set: " +
                               parameters.getTestInputFile());
            test.readClassificationSet(parameters.getTestInputFile(), false);
        } catch (IOException e) {
            System.err.println(
                    "There was a problem while reading the input data-sets: " +
                    e);
            somethingWrong = true;
        }

        //We may check if there are some numerical attributes, because our algorithm may not handle them:
        somethingWrong = somethingWrong || train.hasNumericalAttributes();
        somethingWrong = somethingWrong || train.hasMissingAttributes();
        
        

        outputTr = parameters.getTrainingOutputFile();
        outputTst = parameters.getTestOutputFile();
        outputReglas = parameters.getReglasOutputFile();

        //Now we parse the parameters, for example:
         BeamWidth = Integer.parseInt(parameters.getParameter(0));
         minPos = Integer.parseInt(parameters.getParameter(1));
         minNeg = Integer.parseInt(parameters.getParameter(2));
}
    
    /**
     * Decide cuando para el aprendizaje de reglas de una clase.
     * @param regla Regla   regla con la que evaluamos las filas del dataset
     * @param intances Instances Objeto que contiene las filas del datase
     * @param clases int El numero dela clase que estamos evaluando
     * @return boolean Si se cumple la condicion o no
     */
    private boolean Rule_Generation_Stopping_Criterion(Regla regla,Instances instances,int clases){
        
        Attribute s[] = Attributes.getOutputAttributes();
        
        Regla regla_vacia = new Regla(s[0].getNominalValue(clases),instances.getnInputs());
        
        //si el valor de la regla, es menor que la regla sin condiciones, condicion verdadera
        if (regla.getScore(instances)>regla_vacia.getScore(instances)) return false;
        else return true;  
    }
    
    /**
     * Elimina del objeto Instances las filas cubiertas por la regla.
     * Modifica, sin devolver nada, el objeto Intances
     * @param regla Regla   regla con la que evaluamos las filas del dataset
     * @param intances Instances Objeto que contiene las filas del datase
     */
    private void coveredPositives(Regla regla,Instances instances){

        LinkedList<Double> fila;
        LinkedList<Integer> aux = new LinkedList <Integer> ();
        
        if (regla.getAntecedente().size()>0){//si es vacÃ­a, no cubre nada
            for(int i=0;i<instances.getnData();i++){ //Para cada fila

                fila = instances.getExample(i); //cogemos la fila 
                String consecuente = instances.getOutputAsString(i);
                boolean filacubierta=true;
                //para cada antecedente de la regla se comprueba si es igual que
                //el de la fila del data set
                for(int j=0; j<regla.getSizeAntecedentes();j++){

                   Integer atributo = regla.getAV(j).getAtributo();
                   Double valor = regla.getAV(j).getValor();

                   if(!(valor.equals(fila.get(atributo)) )){
                       filacubierta=false;
                   } 
                }
                //si la variable permace a true, la fila es cubierta y se elimina
                if( filacubierta && (regla.getConsecuente().equals(consecuente))){
                    aux.add(i);
                }
            } 
            instances.removeInstances(aux);//borra la lista de filas aÃ±adidas a aux
        }
    }
    
    /**
     * Cuenta el nÃºmero de instancias positivas que hay en el dataset aun.
     * @param intances Instances Objeto que contiene las filas del datase
     * @param clases int El numero dela clase que estamos evaluando
     * @return int numero de instancias positvas en Instances
     */
    private Integer positivesInstances(Instances instances,int clases){
        Attribute s[] = Attributes.getOutputAttributes();
        String clase = s[0].getNominalValue(clases);
        
        Integer instancias_pos=0;
        for(int i=0; i < instances.getnData(); i++){
            if (instances.getOutputAsString(i).equals(clase)) instancias_pos++;
        }
        
        return instancias_pos;
    }
    
     /**
     * It launches the algorithm
     */
    public void execute() {
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found, either the data-set have numerical values or missing values.");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } else {

            LinkedList<Regla> RuleSet = new LinkedList <Regla> ();//conjunto de reglas
               
            //para cada valor que pueda tomar la clase objetivo
            for (int clases=0; clases< train.getnClasses(); clases++){
                //crea una copia del data, en la clase Instance
                Instances instances = new Instances(train.getX(),train.getOutputAsString(),
                        train.getnInputs(),train.getnClasses());
                
                boolean exit = false;
                while (positivesInstances(instances,clases)!= 0 && !exit){
                        //se llama al procedimiento induce_one_rul, que devuelve una regla
                        Regla regla = InduceOneRule.induce_One_Rule(instances, clases, BeamWidth, minPos, minNeg);
                        //si es verdad lo que devuelve la siguiente llamada se sale del while
                        if (Rule_Generation_Stopping_Criterion(regla,instances,clases)) exit = true;
                        else {
                             coveredPositives(regla,instances);//elimina las instancias cubiertas por la regla
                             RuleSet.add(regla);//aÃ±ade al data set
                        } 
                }
            } 
            
            //eliminamos reglas repetidas del RuleSet
            LinkedList <Regla> reglas_aux = new LinkedList <Regla> ();
            boolean repetida = false;
            for(int tam=0; tam<RuleSet.size();tam++){
                        //si la regla es igual a otra regla contenida en el conjunto
                        for(int i=0; i<reglas_aux.size();i++){
                            if(RuleSet.get(tam).equals(reglas_aux.get(i)))
                                repetida = true;//encuentra una repetida
                        }
                        //si no se repite la almacena en la lista auxiliar
                        if(!repetida) reglas_aux.add(RuleSet.get(tam));
                        repetida = false;  //se reestrablece el valor de repetida    
            }

            RuleSet = reglas_aux; //se copia el conjunto sin reglas repetidas

            BaseReglas conjunto_reglas = new BaseReglas(RuleSet);
            //muetra las reglas por pantalla
            conjunto_reglas.mostrarReglas();
            //finalmente guardamos la base de reglas en fichero
            conjunto_reglas.ficheroReglas(outputReglas);
            
            //###################Comprobamos con el fochero de validacion#############
            LinkedList<String> resultado_val = conjunto_reglas.compruebaReglas(val);
         
            //###################Comprobamos con el fochero de test#############
            LinkedList<String> resultado_test = conjunto_reglas.compruebaReglas(test);

          //Finally we should fill the training and test output files
            doOutput(this.val, this.outputTr, resultado_val);
            doOutput(this.test, this.outputTst, resultado_test);

            System.out.println("Algorithm Finished");
        }
    }

    /**
     * It generates the output file from a given dataset and stores it in a file
     * @param dataset myDataset input dataset
     * @param filename String the name of the file
     */
    private void doOutput(myDataset dataset, String filename, LinkedList<String> resultado) {
        String output = new String("");
        output = dataset.copyHeader(); //we insert the header in the output file
        Double noacertados=0.0;
        Double noclasificados=0.0;
        //We write the output for each example
        for (int i = 0; i < dataset.getnData(); i++) {
            //for classification:
            output += dataset.getOutputAsString(i) + " " +
                    resultado.get(i) + "\n";
            
            if (resultado.get(i).compareTo("No clasificado") == 0){
                noclasificados++;
            }else if(dataset.getOutputAsString(i).compareTo(resultado.get(i)) != 0){
                noacertados++;
            }     
        }
        Fichero.escribeFichero(filename, output);
    }
}

