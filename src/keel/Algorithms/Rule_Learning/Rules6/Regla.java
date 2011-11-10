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

package keel.Algorithms.Rule_Learning.Rules6;

import java.util.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

/**
 * <p>Title: Clase Regla</p>
 *
 * <p>Description: Se encarga de manejar los objeto Regla, que contienes antecedentes, consecuentes y lista </p>
 * <p>de atribtutos invalidos</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Ismael Duque García
 * @version 1.0
 */

public class Regla {
    
    private LinkedList<Atributo_valor> antecedentes = new LinkedList <Atributo_valor> ();
    private String consecuente;
    private boolean consistencia;
    //Cada posicion de la lista representa un atributo, y cada valor de su
    //lista asociada representa el valor invalido de este atributo para la regla
    private LinkedList<LinkedList<Double>> valoresinvalidos = new LinkedList <LinkedList<Double>> ();
    
    /**
     * Constructor de Regla
     * @param c String consecuente de la reglas
     * @param numAtributos int numero de atributos que hay en el dataset
     */
    public Regla(String c,int numAtributos){
        antecedentes = new LinkedList <Atributo_valor> ();
        consecuente = c;
        consistencia = false;
        for (int i=0;i<numAtributos;i++){
            valoresinvalidos.add(new LinkedList<Double>());
        }
    }
  
    /**
     * Contructor copia de Regla
     * @param r Regla regla desde la que se copia
     */
    public Regla(Regla r){
        antecedentes = (LinkedList) r.getAntecedente().clone();
        consecuente = r.getConsecuente();
        consistencia = r.getConsistencia();
        valoresinvalidos = (LinkedList) r.getValoresInvalidos().clone();
    }
    
    /**
     * Devuelve la lista de antecedentes de la regla.
     * @return LinkedList<Atributo_valor> lista de antecedentes
     */
    public LinkedList<Atributo_valor> getAntecedente(){ return antecedentes;}
    
    /**
     * Devuelve la lista de valores invalidos de la regla
     * @return LinkedList<LinkedList<Double>> lista de valores invalidos
     */
    public LinkedList<LinkedList<Double>> getValoresInvalidos(){ return valoresinvalidos;}
   
    /**
     * Devuelve el consecuente de la regla
     * @return String consecuente
     */
    public String getConsecuente(){ return consecuente;}
    
    /**
     * Devuelve el tamaño de la lista de antecedentes
     * @return int tamaño de la lista de antecedentes
     */
    public int getSizeAntecedentes(){ return antecedentes.size();}
    
    /**
     * Devuelve la consistencia de la regla
     * @return boolean consistencia de la regla
     */
    public boolean getConsistencia() { return consistencia; }
    
    /**
     * Devuelve verdadero o falso dependiendo de si un atributo  y su valor
     * están en la lista de valores invalidos
     * @param Atributo Integer Entero que representa el atributo
     * @param valor Double Doble que representa el valor del atributo
     * @return boolean Si la condicion es verdadera o falsa
     */
    public boolean contenidoValoreInval(Integer Atributo,Double valor){ 
        return valoresinvalidos.get(Atributo).contains((Double) valor);
    }
    
    /**
     * Devuelve el Atributo_valor deseado de la lista de antecedentes
     * @return Atributo_valor 
     */
    public Atributo_valor getAV(int i){
        return antecedentes.get(i);
    }
    
    /**
     * Devuelve el último Atributo_valor añadido de la lista de antecedentes
     * @return Atributo_valor 
     */
    public Atributo_valor getLastAV(){
        return antecedentes.getLast();
    }
    
    /**
     * Añade un Atributo_valor a la lista de antecedentes
     * @param av Atributo_valor Objeto a añadir a la lista 
     */
    public void addAntecedente(Atributo_valor av){
        antecedentes.add(av);
    }
    
    /**
     * Añade un Atributo_valor a la lista de valores invalidos
     * @param i Integer Atributo a añadir a la lista 
     * @param vinv Double Valor del atributo a añadir
     */
    public void addValoresInv(Integer i,Double vinv){
        if(!valoresinvalidos.get(i).contains(vinv))
            valoresinvalidos.get(i).add(vinv);
    }
    
    /**
     * Elimina un Atributo_valor a la lista de valores invalidos
     * @param i Integer Atributo a añadir a la lista 
     * @param vinv Double Valor del atributo a añadir
     */
    public void removeValorInv(Integer i,Double vinv){
        if(!valoresinvalidos.get(i).contains(vinv))
            valoresinvalidos.get(i).remove(vinv);
    }
    
    /**
     * Compara dos reglas
     * @param rule Regla regla con la que se compara
     * @return boolea Verdadero si es igual, Falso si es diferente 
     */
    public boolean equals(Regla rule){
        boolean igual = true;
        //si tiene el mismo tamaño en los antecedentes
        if (this.antecedentes.size() == rule.getAntecedente().size()){
            for(int i=0; i<this.antecedentes.size();i++){
                if(!rule.getAntecedente().get(i).equals(antecedentes.get(i)))
                    igual = false;
            }
            if (!igual) return false; 
        }else{ return false; }
        //ahora se compara que tienen el mismo consecuente
        if (this.consecuente.equals(rule.getConsecuente())) return true;
        else return false;
    }
    
    /**
     * Devuelve el padre de la regla, su antecesor de esta
     * @return Regla La regla sin el ultimo valor añadido 
     */
    public Regla parentRule(){
        //generamos la nueva regla, padre de la actual
        Regla parent = new Regla(this);
        //se elimina el ultimo atributo añadido
        Atributo_valor aux = parent.antecedentes.removeLast();
        //se elimina de la lista de valores invalidos si estuviera
        parent.removeValorInv(aux.getAtributo(),aux.getValor());
        
        return parent;
    }
    
    /**
     * Muestra por pantalla la regla actual 
     */
    public void mostrarRegla(){
        
            Attribute a[] = Attributes.getInputAttributes();
            Attribute s[] = Attributes.getOutputAttributes();
            
            System.out.print("Regla: ");
            for (int i=0; i<this.antecedentes.size(); i++){
              
                Atributo_valor av = antecedentes.get(i);

                    System.out.print("("+a[av.getAtributo()].getName()+","
                            + a[av.getAtributo()].getNominalValue(av.getValor().intValue())
                            +")");
                    if (i<this.antecedentes.size()-1) System.out.print(" & ");
            }
            System.out.println(" -> ("+ s[0].getName()
                    +","+this.consecuente+")");
            System.out.println("------------------------------------");
        
    }
    
     /**
     * Calcula y devuelve la puntuacion de una regla, mediante la m-probability-estimate
     * @param train myDataset Contiene todas la filas del dataset de entrenamiento
     * @return Double Valor de la m-probability-estimate de la regla
     */
    public Double score(myDataset train){
        
        Integer casos_positivos=0;
        Integer casos_negativos=0;
        Integer k = train.getnClasses();
        Integer num_mismo_valor_salida=0;
        
        //si es una regla con el cuerpo vacio, se devuelve 0
        if (!this.getAntecedente().isEmpty()){
            for(int i=0;i<train.getnData();i++){ //Para cada fila

                double[] fila = train.getExample(i); //cogemos la fila 
                String valor_salida = train.getOutputAsString(i);//cogemos el valor de salida de la fila
                boolean filacubierta=true;

                for(int j=0; j<antecedentes.size();j++){//para cada para atributo_valor de la regla

                   Integer atributo = getAV(j).getAtributo();
                   Double valor = getAV(j).getValor();
                   //compara el valor del atributo de la regla con el valor del atributo de la fila actual
                   if(!(valor.equals(fila[atributo]) )){
                       filacubierta=false;//si en algun atributo es diferente el valor
                   } 
                }
                //si es de la clase objetivo, el valor de salida de la regla
                if (this.getConsecuente().equals(valor_salida)){
                    num_mismo_valor_salida++; //almacena P, que se utiliza en la P0 = P/N
                    if(filacubierta)  casos_positivos++;
                }else if(filacubierta) casos_negativos++;}
            
        }else{
            for(int i=0;i<train.getnData();i++){ //Para cada fila
                String valor_salida = train.getOutputAsString(i);
                if (this.getConsecuente().equals(valor_salida))
                    num_mismo_valor_salida++; //almacena P, que se utiliza en la P0 = P/N
            }  
        } 
        //se aprovecha el calculo para ver la consistencia de la regla
        if(casos_negativos == 0) {this.consistencia=true;}
        
        //Po = P/N
        double p = (num_mismo_valor_salida.doubleValue()/train.getnData());
        
        //m-probability-estimate
        Double salida1 = (casos_positivos.doubleValue() + (k * p));
        Double salida2 = ((casos_positivos.doubleValue()+casos_negativos.doubleValue())+k.doubleValue());
        Double salida = salida1/salida2;
        
        return salida;
    }

    
            
   

}

