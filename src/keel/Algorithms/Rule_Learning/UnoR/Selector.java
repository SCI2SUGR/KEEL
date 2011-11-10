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

package keel.Algorithms.Rule_Learning.UnoR;
/**
 * <p>Tï¿½ulo: Conjunto de reglas</p>
 * <p>Descripciï¿½: Estructura para almacenar un conjunto completo de reglas</p>
 * @author Rosa Venzala
 * @version 1.0
 */

public class Selector implements Comparable {

    int atributo; // N de columna en el dataset (0..m-1)
    int operador; // Operador-> 0:= 1:<> 2:<= 3:>
    double[] valor;
    String []valorN; //para los valores nominales
    boolean nominal;

    /**
     *  Clase empleada para almacenar selectores de la forma
     *  (atributo operador valores)
     * @param atr atributo
     * @param op operador
     * @param val valor
     */
    public Selector(int atr, int op, double val) {
        atributo = atr;
        operador = op;
        valor = new double[1];
        valor[0] = val;
    }
    
    /**
     *  Clase empleada para almacenar selectores de la forma
     *  (atributo operador valores) donde valores son nominales
     * @param atr atributo
     * @param op operador
     * @param val valor
     * @param nominal true para indicar que los datos son nominales
     */
    
    public Selector(int atr, int op, String val,double val2,boolean nominal) {
        atributo = atr;
        operador = op;
        valorN = new String[1];
        valorN[0] = val;
	valor = new double[1];
	valor[0]=val2;
	this.nominal=nominal;
    }

    /**
     *  Clase empleada para almacenar selectores de la forma
     *  (atributo operador valores)
     * @param atr atributo
     * @param op operador
     * @param val un conjunto de valores (disjuntos -> at = a ï¿½b ï¿½c)
     */
    public Selector(int atr, int op, double[] val,int util) {
        atributo = atr;
        operador = op;
        valor = new double[util/*val.length*/];
        for (int i = 0; i < util/*val.length*/; i++) {
            valor[i] = val[i];
        }
    }


    /**
     * Funcion de comparaciï¿½ entre dos objetos de la clase selector
     * @param o Objeto selector a comparar
     * @return 0 si son iguales (mismo atributo,operador y valor ), -1 (mismo atributo, operador y menor valor), 1
     * (mismo atributo, operador y menor valor), [nuevo->] 2 (distinto atributo y operador) o 3 (distinto atributo)).
     * -2 -> Mismo atributo, distinto operador; -3 -> mismo atributo, valor = ï¿½+/- 1 (uff) mirar comentario debajo)
     */
    public int compareTo(Object o) {
        Selector s2 = (Selector) o;
        int sal = -2;
        if (atributo == s2.getAtributo()) {
            if (operador == s2.getOperador() && mismoValor(s2.getValores())) {
                sal = 0;
            } else
            if (subsumido(s2.getValores(),s2.getOperador())) {
                //valor[0] == s2.getValor() + 1) || (valor[0] == s2.getValor() - 1)) {
                sal = -3; //Si tiene el mismo valor y no es el operador contrario, o tiene el valor +/- 1
            } else
            if (operador == s2.getOperador() && valor[0] <= s2.getValor()) {
                sal = -1;
            } else
            if (operador == s2.getOperador() && valor[0] > s2.getValor()) {
                sal = 1;
            }
        } else {
            sal = 3;
            if (operador != s2.getOperador()) {
                sal = 2;
            }
        }
        return sal;
    }

    /**
     * Comprueba si dos selectores tienen el mismo valor o valores
     * @param valores double[] Conjunto de posibles valores
     * @return boolean True si tienen exactamente los mismos valores. False en caso contrario
     */
    private boolean mismoValor(double[] valores) {
        boolean salida = false;
        if (valores.length == valor.length) {
            salida = true;
            for (int i = 0; (i < valores.length) && (salida); i++) {
                salida = (valor[i] == valores[i]);
            }
        }
        return salida;
    }

    /**
     * Comprueba si los valores de un selector estï¿½ subsumidos en otro
     * @param valores double[] Valores con los que comprueba
     * @param _operador int Operador con el que se comprueba
     * @return boolean True si el selector de la clase esta subsumido dentro de la clase con que compruebo. False en otro caso.
     */
    private boolean subsumido(double[] valores,int _operador) {
        boolean salida = false;
        //Si los valores son iguales y el operador no es el contrario
        salida = (mismoValor(valores) && (!(this.opContrario(_operador))));
        if (!salida){ //No estï¿½subsumido...
            if ((operador == _operador)&&(operador == 0)) { //Son iguales y es el ==
                if (valor.length < valores.length) {
                    salida = true;
                    for (int i = 0; (i < valor.length) && (salida); i++) {
                        salida = (valor[i] == valores[i]);
                        //System.err.print(" " + valor[i] + "," + valores[i]);
                    }
                    //System.err.println("");
                }
            }
            /*else { //O son distintos o no es el igual
                salida = ((valor[0] == valores[0] + 1) ||
                         (valor[0] == valores[0] - 1));
            }
           */
        }
        return salida;
    }

    /**
     * Comprueba si el operador es el contrario ( = y <>, <= y >)
     * @param op int el operador a comparar
     * @return boolean True si es el contrario, False en otro caso
     */
    private boolean opContrario(int op) {
        if (operador == 0) {
            return ((op == 1) || (op == 2)); //tb cuento el <=
        }
        if (operador == 1) {
            return (op == 0);
        }
        if (operador == 2) {
            return ((op == 3) || (op == 0));
        }
        if (operador == 3) {
            return (op == 2);
        }
        return false;
    }

    /**
     * Devuelve el id. de atributo
     * @return atributo
     */
    public int getAtributo() {
        return atributo;
    }

    /**
     * Devuelve el id de operador (!=, &lt; ...)
     * @return operador
     */
    public int getOperador() {
        return operador;
    }

    /**
     * Devuelve el valor del atributo asociado
     * @return valor
     */
    public double getValor() {
        return valor[0];
    }
    /**
    * Devuelve el valor nominal del atributo asociado
    * @return valor
    */
    public String getValorN() {
        return valorN[0];
    }

    /**
     * Devuelve el conjunto de valores del selector
     * @return double[]
     */
    public double[] getValores() {
        return valor;
    }
    
    /**
     * Devuelve el conjunto de valores nominales del selector
     * @return double[]
     */
    public String[] getValoresN() {
        return valorN;
    }

    /**
     * Asigna el atributo
     * @param i valor del atributo
     */
    public void setAtributo(int i) {
        atributo = i;
    }

    /**
     * Asigna el operador
     * @param i valor del operador
     */
    public void setOperador(int i) {
        operador = i;
    }

    /**
     * Asigna el valor
     * @param f valor
     */
    public void setValor(double f) {
        valor[0] = f;
    }

    /**
     * Asigna los valores
     * @param f valores
     */
    public void setValores(double[] f) {
        valor = new double[f.length];
        for (int i = 0; i < f.length; i++) {
            valor[i] = f[i];
        }
    }


    /**
     * Simplemente muestra el "contenido" del selector:
     * Atributo - operador - valor.
     */
    public void print() {
        System.out.print("(Atr" + this.getAtributo() + " ");
        switch (this.getOperador()) {
        case 0:
            System.out.print("=");
            break;
        case 1:
            System.out.print("<>");
            break;
        case 2:
            System.out.print("<=");
            break;
        default:
            System.out.print(">");
        }
	if(nominal)
        System.out.print(" " + this.getValorN() + ")\n");
	else
	System.out.println(" " + this.getValor() + ")\n");
	
    };

}

