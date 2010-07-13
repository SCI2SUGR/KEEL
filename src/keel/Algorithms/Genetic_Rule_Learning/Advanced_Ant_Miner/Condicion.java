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

package keel.Algorithms.Genetic_Rule_Learning.Advanced_Ant_Miner;

/**
 * <p>Título: Ant Colony Optimization</p>
 * <p>Descripción:Clase Condicion, contiene un valor para un atributo y
 * el operador que lo asigna a la regla (=,>,<) </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * @author Vicente Rubén del Pino Ruiz
 * @version 1.0
 */

public class Condicion {
  private Atributo valor;   //Valor para un tipo de atributo
  private int operador;     //Operador que relaciona ese atributo con la regla
                            //0(=), 1(<), 2(>)
  private static ComparadorCondicion c;

  public Condicion() {
    valor=new Atributo();
    operador=0;//Operador por defecto
    c=new ComparadorCondicion();
  }

  public Condicion(Atributo at, int op){
    valor=at;
    operador=op;
    c=new ComparadorCondicion();
  }

  public void setValor(Atributo at){
    valor=at;
  }

  public void setOperador(int op){
    operador=op;
  }

  public Atributo getValor(){
    return valor;
  }

  public int getOperador(){
    return operador;
  }

  public int getIndice(){
    return valor.getAtributo();
  }

  public boolean cubre(Atributo at){
    boolean devolver=false;
    double valor1;
    double valor2;


    //Si el atributo esta vacio(perdido) o la condicion es vacia(todos)
    if(valor.getValor().equals(new String("Null")) || at.getValor().equals(new String("Null")))
       return true;



    switch(operador){
      case 0://=
        devolver=valor.esIgual(at);
      break;
      case 1://<
        valor1=Double.parseDouble(valor.getValor());
        valor2=Double.parseDouble(at.getValor());
        if(valor2<=valor1)
          devolver=true;
        else
          devolver=false;
      break;
      case 2://>
        valor1=Double.parseDouble(valor.getValor());
        valor2=Double.parseDouble(at.getValor());
        if(valor2>=valor1)
          devolver=true;
        else
          devolver=false;

      break;
    }

    return devolver;
  }

  public boolean tieneValor(int indice,int op){
    if(operador==op && valor.getAtributo()==indice)
      return true;
    else
      return false;
  }

  public static ComparadorCondicion getComparadorCondiciones(){
    return c;
  }

}

