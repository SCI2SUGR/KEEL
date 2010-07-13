/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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

package keel.Algorithms.Rule_Learning.Ritio;

/**
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Jesús Jiménez
 * @version 1.0
 */

import java.io.IOException;
import org.core.*;
import java.util.*;

public class Algorithm {

    myDataset train, val, test;
    String outputTr, outputTst, outputReglas;
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
    }
    
    
    private double calcula_entropia(int atributo, LinkedList<Integer> filas){
        double[] fila;
        double valor;
        double valor_salida;
        double contador = 0;
        LinkedList<Double> valores_atributo = new LinkedList<Double>();
        LinkedList<Double> valores_salida = new LinkedList<Double>();
        
        LinkedList<Double> porcentajes = new LinkedList<Double>() ;
        LinkedList<LinkedList<Double>> porcentajes_salida = new LinkedList<LinkedList<Double>>() ;
        
        //Calculamos los posibles valores del atributo
        for(int i=0;i<filas.size();i++){        
            fila = train.getExample(filas.get(i));
            valor = fila[atributo];
            if(!valores_atributo.contains(valor)){
                valores_atributo.add(valor);
            }            
        }
        
        //Calculamos los posibles valores de la salida
        for(int i=0;i<filas.size();i++){            
            valor = train.getOutputAsInteger(i);
            if(!valores_salida.contains(valor)){
                valores_salida.add(valor);
            }            
        }       
        
        
        //Calculamos todos los porcentajes 
        for(int j=0;j<valores_atributo.size();j++){ //Para cada valor de la columna a estudio
            
            double porcentaje = 0;
            double nfilas = 0;
            valor = valores_atributo.get(j);    //Cogemos el valor
            for(int i=0;i<filas.size();i++){ // y para cada fila
                if(train.getExample(filas.get(i))[atributo] == valor){ //contamos
                    contador++;                    
                }
            }
            porcentaje = (Double)(contador/filas.size());
            porcentajes.add(porcentaje);
            
            nfilas = contador;
            contador = 0;
            
            LinkedList<Double> lista_aux = new LinkedList<Double>();
            for(int k=0;k<valores_salida.size();k++){ //Contamos ahora las salidas para el valor del atributo a estudio
                porcentaje = 0;
                valor_salida = valores_salida.get(k);
                for(int i=0;i<filas.size();i++){
                    if(train.getExample(filas.get(i))[atributo] == valor && train.getOutputAsInteger(filas.get(i)) == valor_salida){ 
                        contador++; //contamos
                    }                    
                }
                porcentaje = (Double)(contador/nfilas); //filas en las que esté 'valor'                
                lista_aux.add(porcentaje);
                contador=0;
            }
            porcentajes_salida.add(lista_aux);
            
        }
        
        //Ya tenemos lo necesario para calcular la entropia en porcentajes y en porcentajes_salida
        double entropia = 0;
        double aux = 0;
        double porcentaje = 0;
        double p = 0;
        for (int i=0;i<porcentajes.size();i++){
            
            for (int j=0;j<porcentajes_salida.get(i).size();j++){
                p = porcentajes_salida.get(i).get(j);
                if (p!=0) aux = -1*p*(Math.log10(p)/Math.log10(2)) + aux; //En aux acumulo lo del paréntesis
            }
            porcentaje = porcentajes.get(i);
            entropia = entropia + porcentaje*aux; //Multiplicamos y sumamos al resto
            aux = 0;
        }    
        
            
        /*for (int i=0;i<porcentajes.size();i++){
            System.out.println("Porcentaje "+porcentajes.get(i));
            for (int j=0;j<porcentajes_salida.get(i).size();j++){
                System.out.println("Porcentaje salida "+porcentajes_salida.get(i).get(j));
            }
            System.out.println("--------------------------------");
        }*/
        
        
        return entropia;
    }
    
    private int calcula_maxima_entropia(LinkedList<Integer> filas, LinkedList<Integer> atributos){
       int atrib_max = -1;
       double aux_entropia, entropia = -1;
       
       for(int atributo=0;atributo<atributos.size();atributo++){
           aux_entropia = calcula_entropia(atributos.get(atributo), filas);
       
           if (aux_entropia > entropia){
               atrib_max = atributos.get(atributo);
               entropia = aux_entropia;
          }           
       }       
       //System.out.println("El atributo con mayor entropia es el número "+atrib_max+" con una entropia de "+entropia); 
       return atrib_max;
    }
    
    private boolean consistencia(LinkedList<Integer> filas, LinkedList<Integer> atributos,int R){
        int contador = 0;
        for (int i=0;i<filas.size();i++){
            int rule = filas.get(i);
            if(rule!=R && train.getOutputAsInteger(rule) != train.getOutputAsInteger(R)){
                for (int j=0;j<atributos.size();j++){
                    int A = atributos.get(j);                
                    if(train.getExample(rule)[A]== train.getExample(R)[A]){
                        contador++;            
                    }
                }            
            }   
            if (contador==atributos.size()){
                //System.out.println("Inconsistente!!!");                
                return false;
            }
            contador=0;
        }        
        return true;        
    }
    
    private TreeMap<Integer,LinkedList<Integer>> partition(LinkedList<Integer> filas, LinkedList<Integer> atributos, int menos_relevante_anterior, int level){
                                                //LinkedList<LinkedList<Integer>> RS_filas, LinkedList<LinkedList<Integer>> RS_atributos){
       TreeMap<Integer,LinkedList<Integer>> RS = new TreeMap<Integer,LinkedList<Integer>>();
       boolean retain = false;
        if (level<train.getnInputs() && filas.size()>0){
           int menos_relevante;           
           
           menos_relevante = calcula_maxima_entropia(filas,atributos);
                      
           LinkedList<Integer> remove_group = new LinkedList<Integer>();
           LinkedList<Integer> retain_group = new LinkedList<Integer>();
           LinkedList<Integer> atributos_retain_group;
           LinkedList<Integer> atributos_remove_group;
          
          
           if(menos_relevante_anterior>=0){
                atributos.add(menos_relevante_anterior);//lo agregamos ahora, para que no afecte al cálculo de la máxima entropía
                retain=true; //Si lo agregamos es porque nos encontramos en un retain_group, por tanto activamos el flag, 
                             //necesario para saber contra que filas calculamos la consistencia
           }
           
           //##ahora vamos a dividir el grupo a su vez en retain y remove:           
           atributos_retain_group = (LinkedList<Integer>) atributos.clone(); //El retain group mantiene todas las columnas
           atributos.remove((Integer)menos_relevante); //quitamos el menos relevante nuevo           
           atributos_remove_group = (LinkedList<Integer>) atributos.clone(); //Al remove_group le quitamos el menos relevante          
           
           
           //Calculamos ahora qué fila va a cada grupo:
           if(!retain){ //si no es retain_group (es remove_group) hacemos el consistencia() solo con las filas actuales
               for (int i=0;i<filas.size();i++){ //Para cada fila
                   if (consistencia(filas,atributos,filas.get(i))){
                       remove_group.add(filas.get(i));
                   }else{
                       retain_group.add(filas.get(i));
                   }
               } 
           }else{ //si es un retain group, llamamos a consistencia() con todas las filas
               LinkedList<Integer> todasfilas = new LinkedList<Integer>();
               for(int j=0;j<train.getnData();j++){
                   todasfilas.add(j);
               }
               for (int i=0;i<filas.size();i++){ //Para cada fila de las actuales                      
                   if (consistencia(todasfilas,atributos,filas.get(i))){ //comprobab si son consistentes con todas las filas del dataset original
                       remove_group.add(filas.get(i));
                   }else{
                       retain_group.add(filas.get(i));
                   }                   
               }
           }
           
           level++;           
           
           if (level == train.getnInputs()){ //Si hemos llegado al final, guardamos en RS           
               
               for(int i=0;i<remove_group.size();i++){                    
                   RS.put(remove_group.get(i), atributos_remove_group);                   
               }               
               for(int i=0;i<retain_group.size();i++){                     
                   //if(!RS.containsKey(retain_group.get(i))) 
                   RS.put(retain_group.get(i), atributos_retain_group);                   
               }             
                              
           }else{
               LinkedList<Integer> atributos_aux = (LinkedList<Integer>)atributos_remove_group.clone();
               
               //No mandamos el menos_relevante porque al ser un remove_group no lo necesitará más
               TreeMap<Integer,LinkedList<Integer>> RS1 = partition(remove_group,atributos_remove_group,-1,level);
               
               //Ahora en vez de mandar los 'atributos_retain_group', mandamos por un lado los del remove, y aparte el menos relevante
               //Así conseguimos que el menos relevante no se tenga en cuenta para calcular la entropía.
               //Después se unirán (porque 'menos_relevante' es > 0) y se continuará con normalidad.
               TreeMap<Integer,LinkedList<Integer>> RS2 = partition(retain_group,atributos_aux,menos_relevante,level);
                              
               RS1.putAll(RS2); //Unimos RS1 con RS2
               RS = (TreeMap<Integer,LinkedList<Integer>>) RS1.clone(); //Copiamos a RS            
           }           
        }   
        
       return RS;
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
        
       
           //Todas las filas
           LinkedList<Integer> filas = new LinkedList<Integer>();
           for(int i=0;i<train.getnData();i++){
               filas.add(i);
           }
           //Todas las columnas
           LinkedList<Integer> atributos = new LinkedList<Integer>();
           for(int i=0;i<train.getnInputs();i++){
               atributos.add(i);
           }     

           int level = 1; //Nivel inicial      

           TreeMap<Integer,LinkedList<Integer>> RS;
           RS = partition(filas,atributos,-1,level);


           //###################A partir de las particiones obtenidas, generamos la base de reglas########
           BaseReglas br = new BaseReglas(train);        
           br.anadirReglas(RS);
           br.mostrarReglas();   

           //###################finalmente guardamos la base de reglas en fichero###########
           String output = new String("");
           br.ficheroReglas(outputReglas,output);
           
           //###################Comprobamos con el fichero de validacion#############
           LinkedList<String> resultado_val = br.compruebaReglas(val);
         
           //###################Comprobamos con el fichero de test#############
           LinkedList<String> resultado_test = br.compruebaReglas(test);
            
            

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

