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

package keel.Algorithms.Rule_Learning.LEM2;

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
    
    
    private LinkedList<TreeMap<Integer,Double>> calcula_lista_bloques_atributovalor(){
        
        LinkedList<TreeMap<Integer,Double>> bloques_atributo_valor = new LinkedList<TreeMap<Integer,Double>>();
        
        for (int i=0;i<train.getnData();i++){ //Para cada fila
            TreeMap<Integer,Double> valor_atributo = new TreeMap<Integer,Double>();
            for (int j=0;j<train.getnInputs();j++){ //Y para cada columna
                //Guardamos la columna y su valor correspondiente
                valor_atributo.put(j, train.getExample(i)[j]);                
            }
            //añadimos todos los valores encontrados para la fila
            bloques_atributo_valor.add(valor_atributo); 
        }
        
        return bloques_atributo_valor;
    }
    
    private LinkedList<Atributo_valor> calcula_mapa_bloques_atributovalor(
        LinkedList<TreeMap<Integer,Double>> bloques_atributo_valor){

        LinkedList<Atributo_valor> lista_auxiliar = new LinkedList<Atributo_valor>();
        
        for (int i=0;i<bloques_atributo_valor.size();i++){ //para cada fila de la tabla
            
            Iterator iter = bloques_atributo_valor.get(i).keySet().iterator();
           while(iter.hasNext()){//dentro de cada fila recorremos el mapa que acumula los atributos-valor
               
               Integer atributo=(Integer) iter.next();//saca el valor de la columna
               Double valor = bloques_atributo_valor.get(i).get(atributo);//saca el valor asociado
               
               Atributo_valor av_nuevo = new Atributo_valor(atributo,valor);//crea un nuevo Atributo_valor
               //si encuentra el valor repetido, devuelve ese valor para poder añadir en su lista,
               //en caso contrario devuelve null, para poder añadir el Atributo_valor a la nueva LinkedList
               Atributo_valor av_lista = av_nuevo.findElement(lista_auxiliar);

                if (av_lista!=null){
                    av_lista.addFila(i);//añade a la lista del Atributo_valor repetido
                }else{
                    av_nuevo.addFila(i);//añade a la lista del Atributo_valor
                    lista_auxiliar.add(av_nuevo);//añade el nuevo Atributo_valor
                }
           } 
        }
        
        return lista_auxiliar;
    }

    
    private TreeMap<Integer,LinkedList<Double>> calcula_parejas_relevantes(LinkedList<Integer> G, LinkedList<TreeMap<Integer,Double>> bloques_atributo_valor){
            TreeMap<Integer,LinkedList<Double>> parejas_relevantes = new TreeMap<Integer,LinkedList<Double>>();
            TreeMap<Integer,Double> aux = new TreeMap<Integer,Double>();
            int atributo;
            double valor;
            
            for(int i=0; i<G.size(); i++){ //Para cada valor de G
                aux = bloques_atributo_valor.get(G.get(i)); //Cogemos la/s pareja/s
                Iterator iter = aux.keySet().iterator();
                while(iter.hasNext()){ //Para cada pareja.
                    atributo = (Integer) iter.next(); //Cogemos el atributo
                    valor = aux.get(atributo); //Cogemos el valor del atributo
                    if (!parejas_relevantes.containsKey(atributo)){ //Si el atributo no esta
                        //creamos la lista:
                        LinkedList<Double> lista_valores = new LinkedList<Double>(); 
                        //Anadimos el valor:
                        lista_valores.add(valor);
                        //Anadimos el atributo y la lista al mapa final:
                        parejas_relevantes.put(atributo,lista_valores ); //Añadimos el atributo                    
                    }else if (!parejas_relevantes.get(atributo).contains(valor)){ //Si el atributo está, pero no su valor
                        //Anadimos el valor a la lista del mapa final
                        parejas_relevantes.get(atributo).add(valor);                    
                    }//else{ /*Pareja repetida. No se añade*/}                
                }
            }

            return parejas_relevantes;
    }
    
    private Atributo_valor calcula_mejor_pareja(LinkedList<Integer> G, LinkedList<Atributo_valor> bloques_atributovalor, TreeMap<Integer,LinkedList<Double>> parejas_relevantes){
         Atributo_valor resultado = new Atributo_valor();
         int atributo; double valor;
         int max=0;
         int cardinalidad=0;
         
         
         Iterator iter = parejas_relevantes.keySet().iterator();
         while (iter.hasNext()){ //Para cada atributo de una pareja relevante
             atributo = (Integer)  iter.next();
             for (int i=0;i<parejas_relevantes.get(atributo).size();i++){
                 valor = parejas_relevantes.get(atributo).get(i); //cogemos su valor
                 Atributo_valor pareja = new Atributo_valor(atributo,valor); //creamos la pareja
                 //Obtenemos en que filas aparece la pareja actual
                 LinkedList<Integer> filas = pareja.findElement(bloques_atributovalor).getListFilas();
                 pareja.addFilas((LinkedList<Integer>)filas.clone());    
                 //Calculamos la interseccion con el conjunto G
                 int intersecciones = 0;
                 for (int j=0;j<filas.size();j++){
                     for (int k=0;k<G.size();k++){
                         if (filas.get(j).equals(G.get(k))) intersecciones++;                         
                     }                     
                 }
                 
                 if(intersecciones > max){
                     max = intersecciones;
                     cardinalidad = filas.size();
                     resultado = pareja;                     
                 }else if(intersecciones == max){ //A igual n�mero de intersecciones, nos quedamos 
                                                  //con el de menor cardinalidad
                     if (filas.size()<cardinalidad){
                         max = intersecciones;
                         cardinalidad = filas.size();                         
                         resultado = pareja;   
                     }//Si tiene igual o peor cardinalidad, no hacemos nada y nos quedamos con
                      //el primero que apareci�
                 }   
             }             
         }         
         return resultado;       
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
            
        
        //##########Calculo de todos los bloques atributo-valor:################
        LinkedList<TreeMap<Integer,Double>> bloques_atributo_valor = calcula_lista_bloques_atributovalor();

        //#########Lo mismo que antes, pero en otra EEDD #######################
        LinkedList<Atributo_valor> lista_bloques_atributo_valor = calcula_mapa_bloques_atributovalor(bloques_atributo_valor);
        
        BaseReglas br = new BaseReglas();
        Attribute s[] = Attributes.getOutputAttributes();
        int numero_salidas = s[0].getNumNominalValues();
        
        for (int pasada=0;pasada<numero_salidas;pasada++){


                //######## Concepto B ##################################################
                LinkedList<Integer> B = new LinkedList<Integer>();
                for (int i = 0; i<train.getnData(); i++){
                    if(train.getOutputAsInteger(i)==pasada) B.add(i);            
                }
                LinkedList<Integer> G = (LinkedList<Integer>) B.clone();


                //################ Calculo de los complejos m�nimos
                LinkedList<LinkedList<Atributo_valor>> complejos_minimos = new LinkedList<LinkedList<Atributo_valor>>();
                TreeMap<Integer,LinkedList<Double>> parejas_relevantes = new TreeMap<Integer,LinkedList<Double>>();
                LinkedList<Integer> interseccion = new LinkedList<Integer>();
                LinkedList<Atributo_valor> lista_aux = new LinkedList<Atributo_valor>();
                boolean subconjunto;

                boolean vacio_interseccion =  false;
                //parejas_relevantes = calcula_parejas_relevantes(G, bloques_atributo_valor);
                while (G.size()>0 && !vacio_interseccion){
                    //######Conjunto T(G) de todas las parejas relevantes valor-atributo es:
                    //System.out.println("Tam paraejas relevantes " +parejas_relevantes.size());
                    if (parejas_relevantes.size()==0) parejas_relevantes = calcula_parejas_relevantes(G, bloques_atributo_valor);

                    //En base a T(G) y a las parejas, calculamos la mejor pareja
                    Atributo_valor mejor_pareja = calcula_mejor_pareja(G, lista_bloques_atributo_valor, parejas_relevantes);
                   
                    // if(mejor_pareja.getAtributo() != null) System.out.println("La mejor pareja es "+ mejor_pareja.getAtributo() +" -- "+mejor_pareja.getValor()+" -tama�o- "+mejor_pareja.getListFilas().size());

                    //Calculamos la interseccion entre la nueva pareja, y las obtenidas con anterioridad
                    if(mejor_pareja.getAtributo() != null){//Controla que T(G) != 0

                            if (lista_aux.size()==0){ //Si no hay ninguno, la interseccion sera todas las filas de la mejor pareja
                                interseccion =(LinkedList<Integer>) mejor_pareja.getListFilas().clone();
                            }else{
                                LinkedList<Integer> interseccion_aux = new LinkedList<Integer>();
                                for (int i=0;i<interseccion.size();i++){
                                    //Si esta en los dos, pertenece a la interseccion
                                    if (mejor_pareja.getListFilas().contains(interseccion.get(i))){ 
                                       interseccion_aux.add(interseccion.get(i)); 
                                    }                
                                }
                                interseccion = (LinkedList<Integer>) interseccion_aux.clone();
                            }

                          //Ahora comprobamos que la interseccion sea subconjunto de B
                          subconjunto = true;
                          if (interseccion.size()==0){//si la interseccion vacion, no se considera subconjunto
                              subconjunto = false;
                              vacio_interseccion = true;
                          }
                          else{
                              for (int i=0;i<interseccion.size();i++){
                                  if (!G.contains(interseccion.get(i))) subconjunto=false;
                              }
                          }

                          if(subconjunto){ //Si era subconjunto tenemos el primer complejo m�nimo
      
                              lista_aux.add(mejor_pareja);
                              complejos_minimos.add((LinkedList<Atributo_valor>) lista_aux.clone());
                              lista_aux.clear();

                              //Actualizamos G. Si queda vac�o acaba el algoritmo
                              for(int i=0;i<interseccion.size();i++){
                                  G.remove((Integer) interseccion.get(i));                    
                              } 
                              
                              interseccion.clear();
                              parejas_relevantes.clear();
                              // System.out.println("Me he cargao las pareja, cuanto vale " +parejas_relevantes.size());
                          }else{
                              //apuntamos la pareja atributo_valor
                              lista_aux.add(mejor_pareja);
                              //borramos la pareja ya obtenida de las relevantes
                              parejas_relevantes.get(mejor_pareja.getAtributo()).remove((Double) mejor_pareja.getValor());               
                          }
                     }else{
                           G.clear(); //como interseccion de T(G) no es subconjunto de B, forzamos la salida
                     }
                }

                for (int i=0;i<complejos_minimos.size();i++){

                    int j = 0;
                    //el while se ejecuta mientras el tamaño sea mayor de 1 o no haya comprobado todos los atributos
                    while(complejos_minimos.get(i).size()>1 && j<=complejos_minimos.get(i).size()){
                        Atributo_valor aux_av=complejos_minimos.get(i).removeFirst();

                        //Calcular la interseccion
                        LinkedList<Integer> inter = new LinkedList<Integer>();

                        for(int m=0;m<complejos_minimos.get(i).size();m++){

                            if (m==0){ //Si no hay ninguno, la interseccion sera todas las filas de la mejor pareja
                                inter =(LinkedList<Integer>) complejos_minimos.get(i).get(m).getListFilas();

                            }else{
                                LinkedList<Integer> interseccion_aux = new LinkedList<Integer>();
                                for (int n=0;n<inter.size();n++){
                                    //Si est� en los dos, pertenece a la intersecci�n
                                    if (complejos_minimos.get(i).get(m).getListFilas().contains(inter.get(n))){ 
                                       interseccion_aux.add(inter.get(n)); 
                                    }                
                                }
                                inter = (LinkedList<Integer>) interseccion_aux.clone();
                            }
                        }

                        //Ahora comprobamos que la interseccion sea subconjunto de B
                        subconjunto = true;
                        for (int k=0;k<inter.size();k++){
                            if (!B.contains(inter.get(k))) subconjunto=false;
                        }
                        if (!subconjunto){//si no es subconjunto añadimos a la lista
                            complejos_minimos.get(i).addLast(aux_av);
                            j++;
                        }
                    }

                }
                
                //se añaden las reglas a la base de reglas a partir de los complejos mínimos obtenidos
                br.anadirReglas(complejos_minimos, pasada);
                

            }   
            //finalmente guardamos la base de reglas en fichero
            String output = new String("");
            br.ficheroReglas(outputReglas,output);
            
            //###################Comprobamos con el fochero de validacion#############
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

