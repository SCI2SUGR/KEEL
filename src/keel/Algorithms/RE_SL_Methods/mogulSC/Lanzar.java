package keel.Algorithms.RE_SL_Methods.mogulSC;

/*
 * Created on 07-feb-2004
 *
 * @author Jesus Alcala Fernandez
 *
 */

import java.lang.*;
import java.util.StringTokenizer;
import org.core.Fichero;

public class Lanzar {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Remember: java Lanzar <file_configuration>");
    }
    else {
      System.out.println(
          "Step 1: Obtaining the initial Rule Base and Data Base");
      MogulSC mogul = new MogulSC(args[0]);
      mogul.run();
      System.out.println("Step 2: Genetic Selection of the Rules");
      Sel simplificacion = new Sel(args[0]);
      simplificacion.run();
      boolean tuning = leer_conf(args[0]);
      if (tuning) {
        System.out.println("Final Step: Genetic Tuning of the FRBS");
        Tun_aprox tun = new Tun_aprox(args[0]);
        tun.run();
      }
      System.out.println("Algorithm Finished!");
    }
  }

  private static boolean leer_conf(String fichero_conf) {
    int i;
    String cadenaEntrada;
    boolean tuning = false;

    // we read the file in a String
    cadenaEntrada = Fichero.leeFichero(fichero_conf);
    StringTokenizer sT = new StringTokenizer(cadenaEntrada, "\n\r=", false);

    // we read the algorithm's name
    sT.nextToken();
    sT.nextToken();

    // we read the name of the training and test files
    sT.nextToken();
    sT.nextToken();

    // we read the name of the output files
    sT.nextToken();
    String valor = sT.nextToken();

    StringTokenizer ficheros = new StringTokenizer(valor, "\t ", false);
    ficheros.nextToken(); //salida oblig traing
    ficheros.nextToken(); //salida oblig test

    ficheros.nextToken(); //BR del primer metodo
    ficheros.nextToken(); //BD del primer metodo
    String fichero_reglas = ( (ficheros.nextToken()).replace('\"', ' ')).trim(); //BR de seleccion
    String fich_tuning = ( (ficheros.nextToken()).replace('\"', ' ')).trim(); //BR de tuning

    // we read the parameters
    for (i = 0; i < 17; i++) { //leo los 17 primeros parametros que son de los dos primeros métodos
      sT.nextToken(); //nombre parametro
      sT.nextToken(); //valor parametro
    }

    sT.nextToken();
    valor = sT.nextToken().trim();
    if (valor.compareToIgnoreCase("YES") == 0) {
      tuning = true;
    }
    else { //Copio la salida de seleccion en la de tuning (no hago tuning)
      String cadena = Fichero.leeFichero(fichero_reglas);
      Fichero.escribeFichero(fich_tuning, cadena);
    }
    return tuning;
  }

}
