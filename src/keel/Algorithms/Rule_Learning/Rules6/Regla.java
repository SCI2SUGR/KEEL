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

package keel.Algorithms.Rule_Learning.Rules6;

import java.util.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

/**
 * <p>Title: Regla (Rule). </p>
 *
 * <p>Description: 
 * This class implements a rule object for this rule learning algorithm.
 * This class stores the antecedents, consequents and the list of invalid attributes. </p>

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
     * Parameter constructor. Builds a rule with the parameters given.
     * @param c String rule consequent
     * @param numAtributos int number of attributes of the dataset.
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
   * Copy Constructor. Creates a new rule object by copying the one given as argument.
   * 
   * @param r rule to be copied.
   */
    public Regla(Regla r){
        antecedentes = (LinkedList) r.getAntecedente().clone();
        consecuente = r.getConsecuente();
        consistencia = r.getConsistencia();
        valoresinvalidos = (LinkedList) r.getValoresInvalidos().clone();
    }
    
    /**
     * Returns the list with the antecedents of the rule.
     * @return the list with the antecedents of the rule.
     */
    public LinkedList<Atributo_valor> getAntecedente(){ return antecedentes;}
    
    /**
     * Returns the list with the invalid values for the rule.
     * @return the list with the invalid values for the rule.
     */
    public LinkedList<LinkedList<Double>> getValoresInvalidos(){ return valoresinvalidos;}
   
    /**
     * Returns the consequent of the rule.
     * @return String the consequent of the rule.
     */
    public String getConsecuente(){ return consecuente;}
    
    /**
     * Returns the size of the antecedents list.
     * @return the size of the antecedents list.
     */
    public int getSizeAntecedentes(){ return antecedentes.size();}
    
    /**
     * Return the consistency of the rule.
     * @return boolean the consistency of the rule.
     */
    public boolean getConsistencia() { return consistencia; }
    
    /**
     * Checks if the given attribute and its value is invalid for this rule.
     * The attribute and the value are in the invalid values list.
     * @param Atributo Integer given attribute.
     * @param valor Double given value.
     * @return boolean true if the given attribute and its value is invalid, false otherwise.
     */
    public boolean contenidoValoreInval(Integer Atributo,Double valor){ 
        return valoresinvalidos.get(Atributo).contains((Double) valor);
    }
    
    /**
     * Returns the pair attribute - values asked from the antecedents list.
     * @param i index of the antecedent asked.
     * @return the pair attribute - values asked from the antecedents list.
     */
    public Atributo_valor getAV(int i){
        return antecedentes.get(i);
    }
    
    /**
     * Returns the last pair attribute - values from the antecedents list.
     * @return the last antecedent.
     */
    public Atributo_valor getLastAV(){
        return antecedentes.getLast();
    }
    
    /**
     * Adds the given antecent to the list of the rule. (pair attribute - values)
     * @param av {@link Atributo_valor} antecedent to be added.
     */
    public void addAntecedente(Atributo_valor av){
        antecedentes.add(av);
    }
    
    /**
     * Adds an attribute with its value to the invalid values list.
     * @param i Integer attribute to be added.
     * @param vinv Double value of the attribute to be added.
     */
    public void addValoresInv(Integer i,Double vinv){
        if(!valoresinvalidos.get(i).contains(vinv))
            valoresinvalidos.get(i).add(vinv);
    }
    
    /**
     * Removes an attribute with its value to the invalid values list.
     * @param i Integer attribute to be removed.
     * @param vinv Double value of the attribute to be removed.
     */
    public void removeValorInv(Integer i,Double vinv){
        if(!valoresinvalidos.get(i).contains(vinv))
            valoresinvalidos.get(i).remove(vinv);
    }
    
  /**
   * Checks if the rule is equal to the given one.
   * @param rule Rule to compare with.
   * @return True if the rules are equal.
   */
    public boolean equals(Regla rule){
        boolean igual = true;
        //si tiene el mismo tamaÃ±o en los antecedentes
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
     * Returns the father of this rule, its ancestor.
     * @return {@link Regla} the rule without the last antecedent added. 
     */
    public Regla parentRule(){
        //generamos la nueva regla, padre de la actual
        Regla parent = new Regla(this);
        //se elimina el ultimo atributo aÃ±adido
        Atributo_valor aux = parent.antecedentes.removeLast();
        //se elimina de la lista de valores invalidos si estuviera
        parent.removeValorInv(aux.getAtributo(),aux.getValor());
        
        return parent;
    }
    
    /**
     * Prints on the standard output the rule information.
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
      * Computes the quality of the rule with the m-probability-estimation.
     * @param train myDataset training dataset.
     * @return Double the m-probability-estimation of the rule.
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

