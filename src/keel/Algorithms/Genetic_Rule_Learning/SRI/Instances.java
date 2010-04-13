/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Genetic_Rule_Learning.SRI;

import java.util.*;

/**
 * <p>Title: Clase Intances</p>
 *
 * <p>Description: Hace una copia de las filas del dataset y se encarga de su manejo </p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Ismael Duque García
 * @version 1.0
 */
public class Instances {
    
    LinkedList<LinkedList<Double>> examples = new LinkedList <LinkedList<Double>>();
    LinkedList<String> outputs = new LinkedList<String>();
    int num_clases;//Posibles valores de la clase salida
    
    public Instances(){}
    
     /**
     * Constructor de la clase
     * @param e Integer double[][] Filas y columnas del dataset
     * @param o String[] Valores de salida para cada fila
     * @param n_class in Numero de valores que puede tomar la salida
     */
    public Instances(double[][] e,String[] o, int num_a,int n_class){
        
        for(int i=0; i< e.length;i++){
            LinkedList<Double> aux = new LinkedList <Double> ();
            for(int j=0; j<e[i].length;j++){
                aux.add(e[i][j]);                
            }
            examples.add(aux);
        }
        
        for(int i=0; i< o.length;i++){
           outputs.add(o[i]); 
        }
        num_clases = n_class;
    }
    
    /**
     * Devuelve el valor de salida de la fila indicada
     * @param i int numero de fila
     * @return String Valores de salida de una fila i
     */
    public String getOutputAsString(int i){
        return outputs.get(i);
    }
    
    /**
     * Devuelve el numero de atributos del conjunto de entrenamiento
     * @return int numero de atributos
     */
    public int getnInputs(){
        return examples.get(0).size();
    }
    
    /**
     * Devuelve el numero de filas del dataset
     * @return int numero de filas
     */
    public int getnData(){
        return examples.size();
    }
    
    /**
     * Devuelve numero de valores de salida
     * @return int numero de valores de salida
     */
    public int getnClass(){return num_clases;}
    
    /**
     * Devuelve una fila del conjunto de datos de entrenamiento
     * @param i int numero de fila
     * @return LinkedList<Double> valores de una fila
     */
    public LinkedList<Double> getExample(int i){
        return examples.get(i);
    }
    
    /**
     * Elimina una fila del conjunto de entrenamiento
     * @param i int numero de fila
     */
    public void removeInstance(int i){
        this.examples.remove(i);
        this.outputs.remove(i);
    }
   
    /**
     * Elimina varias filas del conjunto de entrenamiento
     * @param eliminar LinkedList<Integer> vector con los numeros de fila a eliminar
     */
    public void removeInstances(LinkedList<Integer> eliminar){
        LinkedList<LinkedList<Double>> auxiliar_examples = new LinkedList<LinkedList<Double>>();
        LinkedList<String> auxiliar_outputs = new LinkedList<String>(); 
        
        int tama=0;
        for(Integer i=0; i<examples.size(); i++){
            if(!eliminar.contains(i)){
                auxiliar_examples.add(examples.get(i));
                auxiliar_outputs.add(outputs.get(i));
                tama++;
            }
        }
        examples = (LinkedList) auxiliar_examples.clone();
        outputs = (LinkedList) auxiliar_outputs.clone();
    }
    
    /**
     * Ve si esta vacio el conjuno de entrenamiento
     * @return boolean Falso si contiene elementos, Verdadero si esta vacio
     */
    public boolean isEmpty(){
        return examples.isEmpty();
    }

}
