package keel.Algorithms.RE_SL_Methods.LEL_TSK;

/*
 * Created on 07-feb-2004
 *
 * @author Jesus Alcala Fernandez
 *
 */

import java.lang.*;

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
      System.out.println("Step 2: Learning tsk-fuzzy models based on MOGUL");
      Mam2Tsk cons = new Mam2Tsk(args[0]);
      cons.run();
      System.out.println("Step 3: Genetic Selection of the Rules");
      Simplif simplificacion = new Simplif(args[0]);
      simplificacion.run();
      System.out.println("Final Step: Genetic Tuning of the FRBS");
      Tun_TSK tun = new Tun_TSK(args[0]);
      tun.run();
      System.out.println("Algorithm Finished!");
    }
  }
}
