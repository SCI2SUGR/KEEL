/*
 * Created on 07-feb-2004
 *
 *
 */

/**
 * @author Jesus Alcala Fernandez
 *
 */

package keel.Algorithms.RE_SL_Methods.MamWM;

import java.io.*;
import java.util.*;
import java.lang.*;

public class Lanzar {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Remember: java Lanzar <file_configuration>");
        } else {
          long t_ini = System.currentTimeMillis();
          WM wm = new WM(args[0]);
          wm.run();
          long t_fin = System.currentTimeMillis();
          long t_exec = t_fin - t_ini;
          long hours = t_exec / 3600000;
          long rest = t_exec % 3600000;
          long minutes = rest / 60000;
          rest %= 60000;
          long seconds = rest / 1000;
          rest %= 1000;
          System.out.println("Execution Time: " + hours + ":" + minutes + ":" +
                             seconds + "." + rest);
        }
    }
}
