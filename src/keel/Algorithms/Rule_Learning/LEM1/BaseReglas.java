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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Rule_Learning.LEM1;
import java.io.*;
import java.util.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

/**
 *
 * @author IDG
 */
public class BaseReglas {
    
    private LinkedList<String> base_de_reglas_salida = new LinkedList<String> ();
    private LinkedList<TreeMap<Integer,Double>> base_de_reglas = new LinkedList<TreeMap<Integer,Double>>();
    private myDataset train;
    
    public BaseReglas(LinkedList<Integer> cobertura_global, myDataset atrain){
        
            double [] fila; //variable para almacenar los valores de una fila completa
            TreeMap<Integer,Double> antecedentes = new TreeMap<Integer,Double> ();
            train=atrain;
            
            int r_aux=0;
            double v_aux;
            
            for (int j=0;j<train.getnData();j++){ //para cada fila
                
                antecedentes.clear();
                
                fila = train.getExample(j); //Cogemos la fila completa 
                
                for (int i=0;i<cobertura_global.size();i++){
                  //Nos quedamos con el valor de los inputs(columnas) indicados por la cobertura global
                 // Double aux_double = fila[cobertura_global.get(i)];
                  antecedentes.put(cobertura_global.get(i), fila[cobertura_global.get(i)]);
                }
                                              
                int i = 0;
                while(antecedentes.size()>1 && i<antecedentes.size()){ //mientras que en el conjunto haya más de 1, y el índice i sea válido                  
                    
                    
                    //Borramos, guardando una copia por si luego hay que volverlo a incluir
                    Iterator iter = antecedentes.keySet().iterator();
                    for(int x=0; x<=i;x++){
                        r_aux = (Integer) iter.next();
                    }                 
                    v_aux = antecedentes.remove(r_aux);

                    //Si cubren diferentes conceptos los antecedentes (no son válidos)
                    if (!valido(antecedentes)){
                        
                        //Volvemos al estado anterior de R añadiendo lo que habíamos borrado con anterioridad
                        antecedentes.put(r_aux, v_aux);
                        //R.add(i,r_aux); 
                        i++;  //incrementamos el contador para sacer el siguiente en la próxima pasada     
 
                    }
                                     
                }//Fin del while
                //En este punto ya tenemos una regla para añadir
               if (!base_de_reglas.contains(antecedentes)){ //Si el resultado (regla) no está repetido
                    //Añadimos el par columnas-valores
                    base_de_reglas.add((TreeMap<Integer,Double>)antecedentes.clone());
                    base_de_reglas_salida.add(train.getOutputAsString(j));
                    
               }                
            } //Fin del for
        
    }
    
    private boolean valido(TreeMap<Integer,Double> antecedentes){
        boolean valido = true;
        boolean coincidente;
        int output = -1;
        
        for (int i=0;i<train.getnData();i++){
            coincidente = true;
                
            Iterator j = antecedentes.keySet().iterator();
            while(j.hasNext()){
                int aux = (Integer) j.next();
                
                //Comparamos el valor de la Fila i-columna R[j] con el valor de R[j] que tenemos
                if (train.getExample(i)[aux]!=antecedentes.get(aux)){ 
                        coincidente = false;
                }
            }
            if(coincidente){ //Estamos en una fila que coincide con los valores de los inputs que tenemos en R
                if (output==-1){ //Si es la primera vez que es coincidente
                    output = train.getOutputAsInteger(i); //guardamos la salida
                }else if(output!=train.getOutputAsInteger(i)){ //Si no, comparamos la salida de la fila actual con la anterior guardada
                    valido = false;                          //si no coincide, no es valido                    
                }
            }
            
        }
        
        return valido;
    }
    
    public LinkedList<String> compruebaReglas(myDataset test){
        
        LinkedList<String> resultados = new LinkedList<String>();
        double[] fila;
        boolean valor_bueno = false; //atributo coincide con un antecedente
        boolean encontrado = false; //regla encontrada

        for(int j=0;j<test.getnData();j++){ //Para cada fila
            encontrado = false;
            fila = test.getExample(j); //cogemos la fila           
            int i=0;
            while(i<base_de_reglas.size() && !encontrado){//bucle de base de reglas
                
                int k=0;
                int aciertos = 0;
                while (k<test.getnInputs() && !encontrado){//bucle para las columnas de las tablas
                   
                    valor_bueno = false;    
                    Iterator z = base_de_reglas.get(i).keySet().iterator();//iterador sobre las claves de cada Treemap
                    while(z.hasNext() && !encontrado  && !valor_bueno){
                        
                        int valor_atributo = (Integer) z.next();//obtenemos el atributos del antecedente
                        
                        if (valor_atributo == k){//Si el atributo es igual al antecednte
                            //coincide el valor del antecedente y atributo
                            if (fila[k]==base_de_reglas.get(i).get(valor_atributo)){
                                aciertos++;
                                valor_bueno=true; //para que no compare este mismo valor con otro antecedente
                                //Si se ha cumplido una regla, intorudimos el valor de salida en el la lista
                                if (aciertos==base_de_reglas.get(i).size()){ 
                                    resultados.add(base_de_reglas_salida.get(i));//add
                                    encontrado = true;//regla encontrada, parar la busqueda para la fila
                                }
                            }//La columna está, pero valores distintos
                        }//La columna no esta en el antecedente'
                    }
                    k++;
                }//fin while k                   
                i++;
            }
            if (encontrado==false) resultados.add("No clasificado"); //si no encuentra ninguna regla coincidente
        }               
        return resultados;
    }
    
    
    public void mostrarReglas(){
        
        //Mostramos la base de reglas:
            Attribute a[] = Attributes.getInputAttributes();
            Attribute s[] = Attributes.getOutputAttributes();
            
            System.out.println("Base de Reglas: \n");
            for (int i=0; i<base_de_reglas.size(); i++){
              
                Iterator j = base_de_reglas.get(i).keySet().iterator();
                while(j.hasNext()){
                    int atributo = (Integer) j.next();
                    int valor = (base_de_reglas.get(i).get(atributo)).intValue();
                    System.out.print("("+a[atributo].getName()+","
                            + a[atributo].getNominalValue(valor)
                            +")");
                    if (j.hasNext()) System.out.print(" & ");
                }
                System.out.println(" -> ("+ s[0].getName()
                        +","+base_de_reglas_salida.get(i)+")");
                System.out.println("------------------------------------");
            }
    }
    
        public void ficheroReglas(String ficheroReglas,String output){
        
        //Mostramos la base de reglas:
            Attribute a[] = Attributes.getInputAttributes();
            Attribute s[] = Attributes.getOutputAttributes();
            
            try {
                FileOutputStream f = new FileOutputStream(ficheroReglas);
                DataOutputStream fis = new DataOutputStream((OutputStream) f);
       
                output +="BASE DE REGLAS: \n\n";
                //Numero de reglas
                output +="Número de reglas: "+ base_de_reglas.size() + " \n\n";
                //Tamaño medio de las reglas obtenidas
                Double media_reglas = 0.0;
                for (int i=0; i<base_de_reglas.size(); i++){
                    Integer aux = base_de_reglas.get(i).size()+1;
                    media_reglas += aux.doubleValue();
                }
                output +="Tamaño medio de las reglas obtenidas: "+ media_reglas/base_de_reglas.size() + " \n\n";
                
                for (int i=0; i<base_de_reglas.size(); i++){

                    Iterator j = base_de_reglas.get(i).keySet().iterator();
                    while(j.hasNext()){
                        int atributo = (Integer) j.next();
                        output += "(" + a[atributo].getName()+ ",";//almacena atributo
                        
                        Integer valor = (base_de_reglas.get(i).get(atributo)).intValue();
                        String prueba = a[atributo].getNominalValue(valor);
                        //almacena valor atributo, si prueba==null, guarda el entero, sino la cadena
                        if (prueba == null){ 
                           output +=  valor.toString() + ")";
                        }else{
                          output +=  prueba + ")";  
                        }

                        if (j.hasNext()) output +=" & ";
                    }
                    output +=" -> ("+ s[0].getName()
                            +","+base_de_reglas_salida.get(i)+") \n";                
                    output += "------------------------------------\n";
                }
                
                fis.writeBytes(output);
                fis.close();
                
            }catch (IOException e) {
                e.printStackTrace();
                System.exit( -1);
            }
    }

}


            
           
            
                
