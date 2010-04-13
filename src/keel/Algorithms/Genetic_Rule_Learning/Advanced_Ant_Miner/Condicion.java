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
