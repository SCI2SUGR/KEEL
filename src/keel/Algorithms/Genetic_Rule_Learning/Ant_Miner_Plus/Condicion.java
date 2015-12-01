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

package keel.Algorithms.Genetic_Rule_Learning.Ant_Miner_Plus;

/**
 * <p>Title: Condicion (Condition)</p>
 * <p>Description: Contains a value for an attribute and 
 * an operator (=,>,<) to be assigned to a rule </p>
 * @author Vicente Rubén del Pino Ruiz
 * @version 1.0
 */
public class Condicion {
    private Atributo valor; //Valor para un tipo de atributo
    private int operador; //Operador que relaciona ese atributo con la regla
    //0(=), 1(<), 2(>)
    private static ComparadorCondicion c;


      /**
     * Default constructor.
     */
    public Condicion() {
        valor = new Atributo();
        operador = 0; //Operador por defecto
        c = new ComparadorCondicion();
    }


    /**
   * Parameter constructor. Builds an condition by copying the values of the parameters given. 
     * @param at attribute to be set.
     * @param op operator used 0(=), 1(<), 2(>).
   */
    public Condicion(Atributo at, int op) {
        valor = at;
        operador = op;
        c = new ComparadorCondicion();
    }

  /**
   * Sets the value for the attribute given.
   * @param at {@link Atribute} with the value and the attribute to be set.
   */
    public void setValor(Atributo at) {
        valor = at;
    }

  /**
   * Sets the operator with the value given. 
   * @param op given operator operator used 0(=), 1(<), 2(>).
   */
    public void setOperador(int op) {
        operador = op;
    }


  /**
   * Returns the value for the attribute of this condition. 
   * @return the value for the attribute of this condition.  
   */
    public Atributo getValor() {
        return valor;
    }


  /**
   * Returns the operator of this condition.
   * @return the operator of this condition.
   */
    public int getOperador() {
        return operador;
    }


  /**
   * Returns the attribute id.
   * @return the attribute id. 
   */
    public int getIndice() {
        return valor.getAtributo();
    }

  /**
   * Checks if the attribute given is covered by the condition.
   * @param at given attribute.
   * @return True if the attribute given is covered by the condition. 
   */
    public boolean cubre(Atributo at) {
        boolean devolver = false;
        double valor1;
        double valor2;

        //Si el atributo esta vacio(perdido) o la condicion es vacia(todos)
        if (valor.getValor().equals(new String("Null")) ||
            at.getValor().equals(new String("Null"))) {
            return true;
        }

        switch (operador) {
        case 0: //=
            devolver = valor.esIgual(at);
            break;
        case 1: //<
            valor1 = Double.parseDouble(valor.getValor());
            valor2 = Double.parseDouble(at.getValor());
            if (valor2 <= valor1) {
                devolver = true;
            } else {
                devolver = false;
            }
            break;
        case 2: //>
            valor1 = Double.parseDouble(valor.getValor());
            valor2 = Double.parseDouble(at.getValor());
            if (valor2 >= valor1) {
                devolver = true;
            } else {
                devolver = false;
            }

            break;
        }

        return devolver;
    }

  /**
   * Checks if the pair attribute id and operator given are the one set on this condition.
   * @param indice given attribute id.
   * @param op given operator.
   * @return true if the pair attribute id and operator given are the one set on this condition.
   * 
   */
    public boolean tieneValor(int indice, int op) {
        if (operador == op && valor.getAtributo() == indice) {
            return true;
        } else {
            return false;
        }
    }

    
  /**
   * Returns the Condition comparative method.
   * @return the Condition comparative method. 
   */
    public static ComparadorCondicion getComparadorCondiciones() {
        return c;
    }

}

