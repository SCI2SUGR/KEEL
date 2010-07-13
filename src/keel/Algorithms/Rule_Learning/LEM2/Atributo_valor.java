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
import java.util.*;

/**
 *
 * @author IDG
 */
public class Atributo_valor {
    
    private Integer atributo;
    private Double valor;
    private LinkedList<Integer> filas = new LinkedList <Integer> ();
    
    public Atributo_valor(){}
    
    
    public Atributo_valor(Integer aAtributo, Double aValor){
        atributo=aAtributo;
        valor=aValor;
    }
    
    public Atributo_valor(Integer aAtributo, Double aValor, LinkedList<Integer> list){
        atributo=aAtributo;
        valor=aValor;
        filas=list;
    }
    
    public void addFilas(LinkedList<Integer> afilas){
        filas = afilas;      
    }
    
    public Integer getAtributo(){return this.atributo;}
    public Double getValor(){return this.valor;}
    
    public Atributo_valor findElement(LinkedList<Atributo_valor> lista){
        
        for(int i=0; i<lista.size();i++){
            Atributo_valor aux = (Atributo_valor) lista.get(i);
            if(this.equals(aux)) return aux;
        }
        return null;
    }

    
    public boolean findElement2(LinkedList<Atributo_valor> lista){
        
        Iterator iter = lista.iterator();
        while(iter.hasNext()){
            if((this.equals((Atributo_valor)iter.next()))) return true;
        }
        return false;
    }
    
    public void muestraLista(){
        System.out.print("{");
        for(int i=0; i<this.filas.size();i++){
            if (i==(this.filas.size()-1)) System.out.print(this.filas.get(i));
            else System.out.print(this.filas.get(i)+",");
        }
        System.out.println("}");
    }
    
    public LinkedList<Integer> getListFilas(){
        return filas;
    }
    
    public void addFila(int fila){
        filas.add(fila);
    }
    
    public Integer getPosListFilas(int pos){
        return filas.get(pos);
    }
    
    public boolean equals(Atributo_valor a){
        return ((a.atributo.equals(this.atributo)) && (a.valor.equals(this.valor)));
    }
    

}

