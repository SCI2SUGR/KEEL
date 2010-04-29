package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

/*
 * LogManager.java
 *
 * Class that controls the file where all log messages are sent
 */


import java.util.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.FileManagement;

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

