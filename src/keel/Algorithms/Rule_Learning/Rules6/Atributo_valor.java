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

/**
 * <p>Title: Clase Atributo_Valor</p>
 *
 * <p>Description: Se encarga de almacenar y manejar objetos compuesto por un entero y un double</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Ismael Duque GarcÃ­a
 * @version 1.0
 */

public class Atributo_valor {
    
    private Integer atributo;
    private Double valor;
    
    public Atributo_valor(){}
    
    public Atributo_valor(Integer aAtributo, Double aValor){
        atributo=aAtributo;
        valor=aValor;
    }
    
    public Integer getAtributo(){return this.atributo;}
    
    public Double getValor(){return this.valor;}
    
    /**
     * Compara dos objetos de la clase
     * @param a Atributo_valor Objeto con el que comparar
     * @return boolean Verdadero si son iguales, Falso si son distintos
     */
    public boolean equals(Atributo_valor a){
        return ((a.atributo.equals(this.atributo)) && (a.valor.equals(this.valor)));
    }
    

}

