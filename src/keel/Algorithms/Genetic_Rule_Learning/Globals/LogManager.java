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

/*
 * LogManager.java
 *
 * Class that controls the file where all log messages are sent
 */

package keel.Algorithms.Genetic_Rule_Learning.Globals;

import java.util.*;

public class LogManager {
   	static FileManagement logFile;
   
	public static void initLogManager() {
		logFile= new FileManagement();
		try {
			logFile.initWrite(Parameters.logOutputFile);
		} catch(Exception e) {
			System.err.println("Failed initializing log file");
			System.exit(1);
		}
	}
	
	public static void println(String line) {
		try {
			logFile.writeLine(line+"\n");
		} catch(Exception e) {
			System.err.println("Failed writing to log");
			System.exit(1);
		}
	}

	public static void printErr(String line) {
		try {
			System.err.println(line);
			logFile.writeLine(line+"\n");
		} catch(Exception e) {
			System.err.println("Failed writing to log");
			System.exit(1);
		}
	}


	public static void print(String line) {
		try {
			logFile.writeLine(line);
		} catch(Exception e) {
			System.err.println("Failed writing to log");
			System.exit(1);
		}
	}

	
	public static void closeLog() {
		try {
			logFile.closeWrite();
		} catch(Exception e) {
			System.err.println("Failed closing log file");
			System.exit(1);
		}
	}
}

