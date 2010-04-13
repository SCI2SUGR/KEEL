package keel.Algorithms.RE_SL_Methods.mogulIRL;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.io.*;
import java.util.*;
import java.lang.*;

public class Lanzar {

    public static void main(String[] args) {
                if (args.length != 1) {
                        System.out.println ("Remember: java Lanzar <file_configuration>");
                }
                else {
                        System.out.println("Step 1: Obtaining the initial Rule Base and Data Base");
                        MogulSC mogul = new MogulSC(args[0]);
                        mogul.run();
                        MiDataset tabla_tra = mogul.getTabla(true);
                        MiDataset tabla_tst = mogul.getTabla(false);
                        System.out.println("Step 2: Genetic Selection of the Rules");
                        Sel seleccion = new Sel(args[0],tabla_tra,tabla_tst);
                        seleccion.run();
                        System.out.println("Final Step: Genetic Tuning of the FRBS");
                        Tun_des tun = new Tun_des(args[0],tabla_tra,tabla_tst);
                        tun.run();
                        System.out.println("Algorithm Finished!");
                }
    }
}
