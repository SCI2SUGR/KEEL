/*
 * Created on 07-feb-2004
 *
 *
 */

/**
 * @author Jesus Alcala Fernandez
 *
 */


package keel.Algorithms.RE_SL_Methods.TSKIRL;
import java.io.*;
import java.util.*;
import java.lang.*;

public class Lanzar {

    public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println ("Remember: java Lanzar <file_configuration>");
		}
		else {
			Lear_m6 lear = new Lear_m6(args[0]);
			lear.run();
		}
    }
}
