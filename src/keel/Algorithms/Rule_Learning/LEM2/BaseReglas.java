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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Rule_Learning.LEM2;

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
    
    public BaseReglas(){}
    
    public void anadirReglas(LinkedList<LinkedList<Atributo_valor>> complejos_minimos, int valor_salida){
        
        Attribute s[] = Attributes.getOutputAttributes();
        
        for (int i=0; i<complejos_minimos.size();i++){
            base_de_reglas.add(new TreeMap<Integer,Double>());
            base_de_reglas_salida.add(s[0].getNominalValue(valor_salida));
            for (int j=0; j<complejos_minimos.get(i).size(); j++){
                base_de_reglas.getLast().put(complejos_minimos.get(i).get(j).getAtributo(), 
                        complejos_minimos.get(i).get(j).getValor());
            }
            
        }

        
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
                            }//La columna estÃ¡, pero valores distintos
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
            output +="NÃºmero de reglas: "+ base_de_reglas.size() + " \n\n";
            //TamaÃ±o medio de las reglas obtenidas
            Double media_reglas = 0.0;
            for (int i=0; i<base_de_reglas.size(); i++){
                Integer aux = base_de_reglas.get(i).size()+1;
                media_reglas += aux.doubleValue();
            }
            output +="TamaÃ±o medio de las reglas obtenidas: "+ media_reglas/base_de_reglas.size() + " \n\n";

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


            
           
            
                
