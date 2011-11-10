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

package keel.Algorithms.Genetic_Rule_Learning.LogenPro;

/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada) 01/01/2007
 * @author Modified by Francisco José Berlanga (University of Jaén) 09/12/2008 
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
public class Condition {
/**	
 * <p>
 * Gene in the tree of the individual
 * </p>
 */
 
    public static final int ANY = -1;
    public static final int IGUAL = 0;
    public static final int DISTINTO = 1;
    public static final int MENORIGUAL = 2;
    public static final int MAYORIGUAL = 3;
    public static final int MENOR = 4;
    public static final int MAYOR = 5;
    public static final int ENTRE = 6;
    public static final int TIPOMAX = 6;

    private double valor1, valor2;
    private int tipo;
    private String nombre;

    /**
     * <p>
     * Constructor
     * </p>
     * @param valor double Value of the condition
     * @param tipo int Type of the condition
     * @param nombre String Name of the condition
     */
    public Condition(double valor, int tipo, String nombre) {
        this.valor1 = valor;
        this.tipo = tipo;
        this.nombre = nombre;
    }

    /**
     * <p>
     * Constructor
     * </p>
     * @param valor1 double Lower value of the condition "ENTRE"
     * @param valor2 double Upper value of the condition "ENTRE"
     * @param nombre String Name of the condition     
     */
    public Condition(double valor1, double valor2, String nombre) {
        this.valor1 = valor1;
        this.valor2 = valor2;
        this.tipo = this.ENTRE;
        this.nombre = nombre;
    }

    /**
     * <p>
     * Make a copy of a Constructor object 
     * </p>
     * @return Condition The copy of a Condition object
     */
    public Condition clone(){
        Condition c = new Condition(this.valor1,this.tipo, this.nombre);
        c.valor2 = this.valor2;
        return c;
    }

    /**
     * <p>
     * Determines if the value is covered by this condition
     * </p>
     * @param valor double value to compare
     * @param perdido boolean true is the value is a missing-value
     * @return boolean TRUE if the condition matches the value. FALSE otherwise.
     */
    public boolean matching(double valor, boolean perdido) {
        if (perdido){
            return (tipo == ANY); //Si es valor perdido solo devuelvo true si la condicion es ANY
        }
        switch (tipo) {
        case ANY:
            return (true);
        case ENTRE:
            return (valor >= valor1 && valor <= valor2);
        case IGUAL:
            return (valor == valor1);
        case DISTINTO:
            return (valor != valor1);
        case MENORIGUAL:
            return (valor <= valor1);
        case MAYORIGUAL:
            return (valor >= valor1);
        case MENOR:
            return (valor < valor1);
        case MAYOR:
            return (valor > valor1);
        }
        return false;
    }

    /**
     * <p>
     * Returns the type of the condition (gene)
     * </p>
     * @return int Id of the condition
     */
    public int getType() {
        return (tipo);
    }

    /**
     * <p>
     * Create a string according to the type of the condition
     * </p>
     * @return String The condition in a string form
     */
    public String print() {
        String salida = new String("");
        //if (this.tipo == this.ANY) {
        //    salida = " ANY ";
        //} else
        if (this.tipo == this.ENTRE) {
            salida = " " + valor1 + " <= "+nombre+" <= " + valor2 + " ";
        } else if (this.tipo == this.IGUAL) {
            salida = " "+nombre+" == " + valor1 + " ";
        } else if (this.tipo == this.DISTINTO) {
            salida = " "+nombre+" != " + valor1 + " ";
        } else if (this.tipo == this.MENORIGUAL) {
            salida = " "+nombre+" <= " + valor1 + " ";
        } else if (this.tipo == this.MAYORIGUAL) {
            salida = " "+nombre+" >= " + valor1 + " ";
        } else if (this.tipo == this.MENOR) {
            salida = " "+nombre+" < " + valor1 + " ";
        } else if (this.tipo == this.MAYOR) {
            salida = " "+nombre+" > " + valor1 + " ";
        }
        return salida;
    }

}

