/*
 * Created on 07-feb-2004
 *
 *
 */

/**
 * @author Jesus Alcala Fernandez
 *
 */

package keel.Algorithms.RE_SL_Postprocess.MamWTuning;

import java.io.*;
import java.util.*;
import java.lang.*;

public class Lanzar {

    public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println ("Remember: java Lanzar <file_configuration>");
		}
		else {
			P mogul = new P(args[0]);
			mogul.run();
		}
    }
}
