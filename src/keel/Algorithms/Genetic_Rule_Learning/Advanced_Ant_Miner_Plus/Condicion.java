package keel.Algorithms.Genetic_Rule_Learning.Advanced_Ant_Miner_Plus;

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

  /**
   * Constructor
   */
  public Condicion() {
    valor=new Atributo();
    operador=0;//Operador por defecto
    c=new ComparadorCondicion();
  }

  /**
   * Constructor
   * @param at Atributo de la condicion
   * @param op Operador de la condicion
   */
  public Condicion(Atributo at, int op){
    valor=at;
    operador=op;
    c=new ComparadorCondicion();
  }

  /**
   * Modulo que inicializa el valor de la condicion
   * @param at Valor de la condicion
   */
  public void setValor(Atributo at){
    valor=at;
  }

  /**
   * Modulo que inicializa el operador de la condicion
   * @param op Operador de la condicion
   */
  public void setOperador(int op){
    operador=op;
  }

  /**
   * Funcion que devuelve el atributo de una condicion
   * @return Atributo de la condicion
   */
  public Atributo getValor(){
    return valor;
  }

  /**
   * Funcion que devuelve el operador de una condicion
   * @return Operador de la condicion
   */
  public int getOperador(){
    return operador;
  }


  /**
   * Funcion que devuelve la posicion de la condicion
   * @return Posicion de la condicion
   */
  public int getIndice(){
    return valor.getAtributo();
  }

  /**
   * Funcion que indica si la condicion cubre un atributo o no
   * @param at Atributo que debe cubrir la condicion
   * @return Booleano indicando si la condicion cubre el atributo o no
   */

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


  /**
   * Funcion que indica si la condicion tiene valor o no
   * @param indice Posicion que debe tener la condicion
   * @param op Operador que debe tener
   * @return Booleano indicando si la condicion tiene valor o no.
   */
  public boolean tieneValor(int indice,int op){
    if(operador==op && valor.getAtributo()==indice)
      return true;
    else
      return false;
  }

  /**
   * Funcion que devuelve un comparador de condiciones
   * @return Comparador de condiciones
   */
  public static ComparadorCondicion getComparadorCondiciones(){
    return c;
  }

}
