/*
 * Created on 07-feb-2004
 *
 *
 */

/**
 * @author Jesus Alcala Fernandez
 *
 */


package keel.Algorithms.RE_SL_Postprocess.MamSelect;
import java.io.*;
import java.util.*;
import java.lang.*;

public class Lanzar {

    public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println ("Remember: java Lanzar <file_configuration>");
		}
		else {
			Sel seleccion = new Sel(args[0]);
			seleccion.run();
		}
    }
}
