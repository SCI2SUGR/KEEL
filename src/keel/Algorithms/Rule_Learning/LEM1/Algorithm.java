package keel.Algorithms.Rule_Learning.LEM1;

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
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

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

    private LinkedList<LinkedList<Integer>> calcula_conjuntos_elementales(LinkedList<Integer> atributos){        
            double[] fila;
            double[] filaux;
            LinkedList<LinkedList<Integer>> particiones = new LinkedList <LinkedList<Integer>>();
            LinkedList<Integer> particion = new LinkedList <Integer> ();
            
            for(int i=0;i<train.getnData();i++){ //para cada fila
                fila = train.getExample(i);                
                for(int k=0;k<train.getnData();k++){ //vemos con qué otras filas coincide
                    filaux = train.getExample(k);
                    boolean ok = true;
                    
                    for (int j=0;j<atributos.size();j++){                        
                        if (fila[atributos.get(j)]!=filaux[atributos.get(j)]){
                            ok=false;                            
                        }
                    }
                    if (ok) particion.add(k);
                }
                if(!particiones.contains(particion)) particiones.add((LinkedList<Integer>)particion.clone()); //Guardamos la partición si no está ya                
                particion.clear();                
            }
            
            //Mostramos las particiones
            /*for (int i=0; i<particiones.size(); i++){
                for (int j=0; j<(particiones.get(i)).size(); j++){
                     System.out.println("--------"+(particiones.get(i).get(j)));  
                }
                System.out.println("+++++++++++++++++++++++++++");   
            }*/            
            return particiones;
    }
    
    private LinkedList<LinkedList<Integer>> calcula_conjunto_elemental_salida(){
            int fila, filaux;
            LinkedList<LinkedList<Integer>> particiones = new LinkedList <LinkedList<Integer>>();
            LinkedList<Integer> particion = new LinkedList <Integer>();
        
            for(int i=0;i<train.getnData();i++){ //para cada fila
                fila = train.getOutputAsInteger(i);
                for(int k=0; k<train.getnData(); k++){ //vemos con qué otras filas coincide
                    filaux = train.getOutputAsInteger(k);
                    if (fila==filaux) particion.add(k);
                }
                
                if(!particiones.contains(particion)) particiones.add((LinkedList<Integer>)particion.clone()); //Guardamos la partición si no está ya                
                particion.clear();                
            }        
            //Mostramos las particiones
            /*for (int i=0; i<particiones.size(); i++){
                for (int j=0; j<(particiones.get(i)).size(); j++){
                     System.out.println("--------"+(particiones.get(i).get(j)));  
                }
                System.out.println("+++++++++++++++++++++++++++");   
            }            */
            return particiones;
    }
    
    private Boolean calcula_dependencia(LinkedList<LinkedList<Integer>> A, LinkedList<LinkedList<Integer>> d){
        
        boolean dependiente=true;
        
        if (A.size()== train.getnData() ){//si el conjunto esta definido con valores independiente
            return dependiente;
        }else{  
            int i=0;
            while((i<A.size())&&(dependiente)){
                int lista=-1;
                
                if(A.get(i).size()>1){//si el subconjunto es de uno no hace falta comparar
                    
                    int j=0;
                    while((j<A.get(i).size())&&(dependiente)){
                    
                            int aux=A.get(i).get(j);
                            //se busca cada valor en el conjunto d, y se almacena el subconjunto 
                            //al que pertenece, para comproba que todos los valores son del mismo
                            int m=0; boolean encontrado=false;
                            while((m<d.size()) && (!encontrado)){
                                int n=0;
                                while((n<d.get(m).size()) && (!encontrado)){
                                  if (aux==d.get(m).get(n)){
                                      if (j==0){
                                          lista=m;
                                      }else if (m!=lista){
                                         dependiente=false; 
                                      }//fin if-else
                                      encontrado=true; //para salir de while antes
                                  }//fin if
                                  n++;
                                }//fin while
                                m++;
                            }//fin while
                            j++;
                       }//fin while
                }//fin if
                i++;
            }//fin while
        }//fin if-else
        
        return dependiente;
    }
    
    /**
     * 
     * @param A Lista con todos los inputs
     * @param d Partición conjuntos elementales {d}*
     * @return Lista con los inputs que representan la cobertura global
     */
    private LinkedList<Integer> calcula_cobertura_global(LinkedList<Integer> A, LinkedList<LinkedList<Integer>> d){
        
        //################### Inicializamos P con todos los inputs##########
        LinkedList<Integer> P = A;                        
        //################### Inicializamos R como vacío####################
        LinkedList<Integer> R = new LinkedList<Integer>();
            
        if (calcula_dependencia(calcula_conjuntos_elementales(A),d)){ //Se cumplirá siempre y cuando no haya inconsistencias
            LinkedList<LinkedList<Integer>> particion = new LinkedList<LinkedList<Integer>>();
            for(int i=0;i<train.getnInputs();i++){                    
                P.remove(new Integer(i));//quitamos el atributo i
                particion = calcula_conjuntos_elementales(P); //calculamos la particion sin ese atributo
                if (calcula_dependencia(particion,d)){ //¿Sigue siendo dependiente de {d}*?
                    //P = Q; ya está hecho porque he hecho el remove antes
                }else{
                    P.add(i); //Volvemos al estado anterior de P añadiendo lo que habíamos borrado con anterioridad
                    Collections.sort(P);
                }
            }
            R = P;
        }else{ 
            System.out.println("El conjunto A de todos los atributos y la partición {d}* no son dependientes.");            
        }
        return R;
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
            
            //##################Generamos un vector con todos los inputs########
            LinkedList<Integer> all_inputs = new LinkedList <Integer>(); 
            for (int i=0;i<train.getnInputs();i++){
                all_inputs.add(i);
            }
            
            //###################Calcular partición {d}*########################
            LinkedList<LinkedList<Integer>> d = calcula_conjunto_elemental_salida();
            
            //###################Calculamos la cobertura global#################
            LinkedList<Integer> cobertura_global = calcula_cobertura_global(all_inputs, d);
            
            //###################Mostramos la cobertura global##################
            Attribute cb[] = Attributes.getInputAttributes(); 
            String output = new String("");
            
            output += "COBERTURA GLOBAL\n\n";
            output += "{";
            for (int j=0;j<cobertura_global.size();j++){ 
                output += cb[cobertura_global.get(j)].getName(); 
                if (j!=cobertura_global.size()-1) output += ",";
            }
            output += "} \n\n";
            
            //###################Inducimos la base de reglas####################
            BaseReglas br = new BaseReglas(cobertura_global,train);
            br.ficheroReglas(outputReglas,output);
            
            //###################Comprobamos con el fochero de test#############
            LinkedList<String> resultado_val = br.compruebaReglas(val);



            //###################Comprobamos con el fochero de test#############
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
