/*
 * Created on 23-jul-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

/**
 * @author Sebas
 */
public class Entero {

    private int valor;


    /**
     * @param valor
     */
    public Entero(int valor) {
        this.valor = valor;
    }

    /**
     * @return Returns the valor.
     */
    public int getValor() {
        return this.valor;
    }

    /**
     * @param valor The valor to set.
     */
    public void setValor(int valor) {
        this.valor = valor;
    }

    /**
     *
     * @return Returns an Integer object with the same value as valor
     */
    public Integer toInteger() {
        return new Integer(valor);
    }

    public String toString() {
        return "" + valor;
    }
}
