/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Rule_Learning.SRI;

/**
 * <p>Title: Clase Atributo_Valor</p>
 *
 * <p>Description: Se encarga de almacenar y manejar objetos compuesto por un entero y un double</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Ismael Duque García
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
