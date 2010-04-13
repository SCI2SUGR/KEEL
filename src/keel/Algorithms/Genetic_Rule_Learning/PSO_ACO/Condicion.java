package keel.Algorithms.Genetic_Rule_Learning.PSO_ACO;

/**
 * <p>Título: Hibridación Pso Aco</p>
 * <p>Descripción: Hibridacion entre los dos algoritmos Pso y Aco</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Empresa: </p>
 * @author Vicente Rubén del Pino
 * @version 1.0
 */

public class Condicion {
    private Atributo valor; //Valor para un tipo de atributo
    private int operador; //Operador que relaciona ese atributo con la regla
    //0(=), 1(<), 2(>)
    private static ComparadorCondicion c;


    /**
     * Constructor
     */
    public Condicion() {
        valor = new Atributo();
        operador = 0; //Operador por defecto
        c = new ComparadorCondicion();
    }

    /**
     * Constructor
     * @param at Atributo
     * @param op Operador
     */
    public Condicion(Atributo at, int op) {
        valor = at;
        operador = op;
        c = new ComparadorCondicion();
    }

    /**
     * Constructor de copia
     * @param co Condicion a copiar
     */
    public Condicion(Condicion co) {
        valor = co.valor;
        operador = co.operador;
        c = co.c;
    }

    /**
     * Modulo que inicializa el valor de la condicion
     * @param at Atributo con el valor
     */
    public void setValor(Atributo at) {
        valor = at;
    }

    /**
     * Modulo que inicializa el valor de la condicion
     * @param at Valor
     */
    public void setValor(float at) {
        valor.setValor(at);
    }


    /**
     * Modulo que inicializa el operador de la condicion
     * @param op Operador
     */
    public void setOperador(int op) {
        operador = op;
    }

    /**
     * Funcion que devuelve el valor de la condicion
     * @return Valor
     */
    public Atributo getValor() {
        return valor;
    }

    /**
     * Funcion que devuelve el operador de la condicion
     * @return Operador de la condicion
     */
    public int getOperador() {
        return operador;
    }

    /**
     * Funcion que devuelve el indice del atributo de la condicion
     * @return Indice
     */
    public int getIndice() {
        return valor.getAtributo();
    }

    /**
     * Funcion que indica si la condicion cubre un atributo o no
     * @param at Atributo
     * @return True en caso de que lo cubra, False en caso contrario
     */
    public boolean cubre(Atributo at) {
        boolean devolver = false;
        double valor1;
        double valor2;

        //Si el atributo esta vacio(perdido) o la condicion es vacia(todos)
        if (valor.getValor() == -1 || at.getValor() == -1) {
            return true;
        }

        switch (operador) {
        case 0: //=
            devolver = valor.getValor() == at.getValor();
            break;
        case 1: //<
            valor1 = valor.getValor();
            valor2 = at.getValor();
            if (valor2 <= valor1) {
                devolver = true;
            } else {
                devolver = false;
            }
            break;
        case 2: //>
            valor1 = valor.getValor();
            valor2 = at.getValor();
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
     * Funcion que indica si la condicion tiene valor
     * @param indice Posicion de la condicion
     * @param op Operador
     * @return True en caso verdader, False en caso contrario
     */
    public boolean tieneValor(int indice, int op) {
        if (operador == op && valor.getAtributo() == indice) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Funcion que devuelve un comparador de condiciones
     * @return Comparador de condiciones
     */
    public static ComparadorCondicion getComparadorCondiciones() {
        return c;
    }


    /**
     * Funcion que suma un valor al valor de la condicion
     * @param v Valor a sumar a la condicion
     */
    public void sumarValor(float v) {
        valor.sumarValor(v);
    }


    /**
     * Funcion que imprime la condicion
     */
    public void imprime() {
        valor.imprime("Condicion: ");
    }

}
