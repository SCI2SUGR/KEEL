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

package keel.Algorithms.Rule_Learning.SRI;

import java.util.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

/**
 * <p>Title: Induce One Rule</p>
 *
 * <p>Description: Tiene como objetivo encontrar la mejor regla para un ejemplo del dataset dado</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Ismael Duque García
 * @version 1.0
 */

public class InduceOneRule {
    
    /**
     * Devuelve una la mejor regla generada en cada llamada a esta funcion
     * @param instances Instances Tabla con los datos de entrenamiento
     * @param clases int Valor de salida a analizar, posicion de la lista de valores de salida
     * @param w int, valor definido manualmente para tener en cuenta en la reduccion de reglas
     * @return Regla mejor regla generada
     */
    public static Regla induce_One_Rule(Instances instances,int clases , int w, int min_positives, int min_negatives){
        
        Attribute a[] = Attributes.getInputAttributes();
        Attribute s[] = Attributes.getOutputAttributes();
        
        LinkedList<Regla> partial_rules = new LinkedList <Regla> ();
        LinkedList<Regla> new_partial_rules = new LinkedList <Regla>();
        Regla best_rule = new Regla(s[0].getNominalValue(clases),instances.getnInputs());
        
        partial_rules.add(new Regla(best_rule));//Se añade la mjor regla a partial_rules
        
        while(!partial_rules.isEmpty()){
            //Recorre todas las reglas contenidas en partial_rules
            
            for(int i=0; i<partial_rules.size();i++){
                Regla rule =partial_rules.get(i);
                //Devuelve la lista de atributos que no están contenidos en la regla actual
                LinkedList<Integer> atributos_no_contenidos = getListAttribNotRule(rule, instances);

                for(int j=0; j<atributos_no_contenidos.size();j++){
                    //Devuelve el valor del atributo del ejemplo analizado
                    for (int k=0; k < a[atributos_no_contenidos.get(j)].getNumNominalValues();k++){
                        
                        Double valor = new Integer(k).doubleValue();
                        //Si es verdad que el valor no pertenece a los valores invalidos que no puedo tomar el atributo
                        if (!rule.contenidoValoreInval(atributos_no_contenidos.get(j),valor)){

                            Atributo_valor av_aux = new Atributo_valor(atributos_no_contenidos.get(j),valor);

                            Regla new_rule= new Regla(rule);//copia la regla que estamos viendo
                            new_rule.addAntecedente(av_aux);//añade el atributo valor
                            
                            //actualiza las instancias cubiertas de la nueva regla
                            //new_rule.coveredInstances(rule.getIntances());
                            
                            //si la comparacion es verdadera, la regla generada pasa a ser la mejor regla
                            if (new_rule.getScore(instances)>best_rule.getScore(instances)){
                                best_rule = new Regla(new_rule);
                            }

                            if ((coveredPositive(new_rule,instances)<= min_positives) || 
                               (Math.abs((coveredNegative(rule,instances))-(coveredNegative(new_rule,instances)))<= min_negatives) || 
                                new_rule.getConsistencia()) {
                                //si se cumple alguna de las condiciones anteriores, se añada como valor invalido a rule
                                rule.addValoresInv(atributos_no_contenidos.get(j), valor);
                            }else{
                                //si no se cumple alguna de las condicines, se añada la nueva regla a new_partial_rules
                                new_partial_rules.add(new Regla(new_rule));
                            }
                        }
                    }
                }
            }  
            
            partial_rules.clear();//se borran las reglas de partial_rules
            
            for(int i=0; i<new_partial_rules.size();i++){
                //si el valor de la regla de new_partial_rule es menor o igual que el de la mejor regla
                if (new_partial_rules.get(i).getScore(instances)<= best_rule.getScore(instances)){
                    //se toma el ultimo valor de la regla
                    Atributo_valor aux_av = new_partial_rules.get(i).getLastAV();
                    //a la regla antecesora a la actual evaluda se le añade el atributo valor invalido
                    new_partial_rules.get(i).parentRule().addValoresInv(aux_av.getAtributo(), aux_av.getValor());
                    //borrar la regla de new_partial_rules
                    new_partial_rules.remove(i);
                    
                }
            }
            
            for(int i=0; i<new_partial_rules.size();i++){//para cada regla contenida en new_partial_rules
                LinkedList<LinkedList<Double>> atrib_invalidos = new_partial_rules.get(i).parentRule().getValoresInvalidos();
                
                for(int j=0; j<atrib_invalidos.size(); j++){//para cada atributo
                        for(int k=0; k<atrib_invalidos.get(j).size();k++){//para cada valor del atributo
                            //se añaden a la regla de new_partial_rules todos los atibutos invalidos de su antecesor
                            new_partial_rules.get(i).addValoresInv(j,atrib_invalidos.get(j).get(k));
                        }
                } 
            }
            
            if (w>1){
                //se eliminan la reglas repetidas
                new_partial_rules = quitarRepetidas(new_partial_rules);
                //se añaden las w mejores reglas
                partial_rules = bestWRules(new_partial_rules,w,instances);
                //se limpia la lista new_partial_rules
                new_partial_rules.clear();
            }
        }

        return best_rule;
    };
 
    /**
     * Devuelve una lista de los atributos no contenidos en la regla
     * @param rule Regla regla sobre la que vamos a obtner los atributos no contenidos en esta
     * @param intances Instances Conjunto de datos que nos facilita todos los atributo que existen
     * @return LinkedList<Integer> Atributos no contenidos en la regla
     */
    private static LinkedList<Integer> getListAttribNotRule(Regla rule, Instances instances){
        
        LinkedList<Integer> list_atributos = new LinkedList<Integer>();
        //en list_atributos, cada posicion de la lista contiene el valor entero de un atributo
        for(int i=0; i<instances.getnInputs();i++){list_atributos.add(i);}
        //para todos los antecedentes de la regla, elimina de la list_atributos 
        for(int i=0; i<rule.getAntecedente().size();i++){
            //utiliza remove(Object), para eliminar el atributo de la list_atributos
            list_atributos.remove((Integer) rule.getAntecedente().get(i).getAtributo());
        }
        //devuelve la lista de atributos no contenidos en la regla
        return list_atributos;
    }
    
    /**
     * Devuelve el numero de casos positivos cubiertos por la regla
     * Se consideran positivos aquellos casos o filas, cuyos atributos son cubiertos
     * por los antecedentes de la regla y ademas el consecuente es el mismo
     * @param rule Regla regla sobre la que vamos a obtener los antecedentes y cosecuente
     * @param intances Instances Conjunto de datos a analizar
     * @return Integer Numero de casos cubiertos positivos
     */
    private static Integer coveredPositive(Regla rule, Instances instances){
        
        Integer casos_positivos=0;
        LinkedList<Double> fila;
        String consecuente="";
        
        if (rule.getAntecedente().size()>0){
            for(int i=0;i<instances.getnData();i++){ //Para cada fila

                fila = instances.getExample(i); //cogemos la fila 
                consecuente = instances.getOutputAsString(i);

                if (rule.getConsecuente().equals(consecuente)){

                    boolean filacubierta=true;

                    for(int j=0; j<rule.getSizeAntecedentes();j++){

                       Integer atributo = rule.getAV(j).getAtributo();
                       Double valor = rule.getAV(j).getValor();

                       if(!(valor.equals(fila.get(atributo) ))){
                           filacubierta=false;
                       } 
                    }

                    if(filacubierta) casos_positivos++;
                }
            } 
        }
        return casos_positivos;
    }
    
    /**
     * Devuelve el numero de casos negativos cubiertos por la regla
     * Se consideran negativos aquellos casos o filas, cuyos atributos son cubiertos
     * por los antecedentes de la regla, pero el consecuente es distinto
     * @param rule Regla regla sobre la que vamos a obtener los antecedentes y cosecuente
     * @param intances Instances Conjunto de datos a analizar
     * @return Integer Numero de casos cubiertos negativos
     */
    private static Integer coveredNegative(Regla rule, Instances instances){
        
        Integer casos_negativos=0;
        LinkedList<Double> fila;
        String consecuente="";
        
        if (rule.getAntecedente().size()>0){
            for(int i=0;i<instances.getnData();i++){ //Para cada fila

                fila = instances.getExample(i); //cogemos la fila 
                consecuente = instances.getOutputAsString(i);

                if (!rule.getConsecuente().equals(consecuente)){

                    boolean filacubierta=true;

                    for(int j=0; j<rule.getSizeAntecedentes();j++){

                       Integer atributo = rule.getAV(j).getAtributo();
                       Double valor = rule.getAV(j).getValor();

                       if(!(valor.equals(fila.get(atributo)) )){
                           filacubierta=false;
                       } 
                    }

                    if(filacubierta) casos_negativos++;
                }
            } 
        }
        return casos_negativos;
    }
    
    /**
     * Elimina las reglas repetidas del conjunto de reglas suministrado
     * @param reglas LinkedList<Regla> Conjunto de reglas a dejar sin repetidos
     * @return LinkeList<Reglas> Conjunto de reglas sin repeticiones
     */
    private static LinkedList<Regla> quitarRepetidas(LinkedList<Regla> reglas){
        
            LinkedList <Regla> reglas_aux = new LinkedList <Regla>();//lista auxiliar
            boolean repetida = false;//controla si esta repetida o no
            for(int tam=0; tam<reglas.size();tam++){//para todas la reglas de reglas
               
                    for(int i=0; i<reglas_aux.size();i++){//comprobar con las reglas de la lista auxiliar
                        if(reglas.get(tam).equals(reglas_aux.get(i)))
                            repetida = true;//si aparece en la lista auxiliar, esta repetida
                    }
                    //si no esta repetida
                    if(!repetida) reglas_aux.add(reglas.get(tam));
                    repetida = false;  //se restablece el boolean    
            }
            
            return reglas_aux;//devuelve la lista auxiliar
    }
   
    /**
     * Seleciona de un conjunto de reglas, las w mejores reglas segun su puntuacion
     * @param reglas LinkedList<Regla> Conjunto de reglas
     * @param w int Numero de reglas a elegir
     * @return LinkeList<Reglas> Conjunto de las w mejores reglas
     */
   private static LinkedList<Regla> bestWRules(LinkedList<Regla> new_partial_rules, int w,Instances instances) {
       
       if (new_partial_rules.size()<=w) 
           return (LinkedList) new_partial_rules.clone();
       else{
          LinkedList<Regla> aux_reglas = new LinkedList <Regla>();
          //para el numero de w best rules
           for(int i=0; i<w; i++){
               Double mayor=-1.0; int posicion_mayor=-1;//se inicializa el mayor y la posicion
               //recorre el vector buscando el mejor valor de las reglas que quedan
               for(int tam_reglas=0; tam_reglas<new_partial_rules.size(); tam_reglas++){
                   Double actual = new_partial_rules.get(tam_reglas).getScore(instances);
                   if(actual >= mayor){ //se va quedando con el mejor valor
                       mayor = actual;
                       posicion_mayor=tam_reglas;
                   }
               }
               //añade el mejor valor encontrado a la lista auxiliar y lo borra de la original
               aux_reglas.add(new_partial_rules.remove(posicion_mayor)); 
           }
           return (LinkedList) aux_reglas.clone();
       }

    }
    
    
}

