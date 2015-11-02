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




package keel.Algorithms.Genetic_Rule_Learning.Globals;

import java.io.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.lang.*;

/**
 * FileManagement.java.
 * Generic class to manage text files
 *
 * Created on 9 de abril de 2004, 20:19
 */
public class FileManagement {
    FileInputStream fileInput;
    FileOutputStream fileOutput;
    
    /** Creates a new instance of FileManagement */
    public FileManagement() {
    }

    /**
     *  Init a file for reading.
     *  @param _name path+name of file to read.
     * @throws java.lang.Exception if the file does not exist or can not be read or written.
     */
    public void initRead(String _name) throws Exception {
	fileInput = new FileInputStream(_name);
    }
    
    /**
     *  Init a file for reading.
     *  @param _name path+name of file to read.
     * @throws java.lang.Exception if the file does not exist or can not be read or written.
     */
    public void initWrite(String _name) throws Exception {
	fileOutput = new FileOutputStream(_name);
    }
    
    /**
     *	Reades a char from file.
     *	@return Character readed.
     * @throws java.lang.Exception if the file does not exist or can not be read or written.
     */
    public char readChar() throws Exception {
        return (char)fileInput.read();
    }
    
    /**
     *	Writes a char from file.
     * @param _c char to write.
     * @throws java.lang.Exception if the file does not exist or can not be read or written.
     */
    public void writeChar(char _c) throws Exception {
        byte[] b=new byte[1];
        b[0]=(byte)_c;
        fileOutput.write(b);
    }

    /**
     *	Reads a line from the file.
     *	@return Line readed.
     * @throws java.lang.Exception if the file does not exist or can not be read or written.
     */
    public String readLine() throws Exception {
            char c;
            String a="";
            c = (char) fileInput.read();
            do {
                    a=a+c;
                    c = (char) fileInput.read();
            }while ((fileInput.available()!=0)&&(c!='\n'));
            if (c!=' ')	{
                    a=a+c;
            }
            return a;		
    }

    /**
     * Write a line given.
     * @param _line line given.
     * @throws Exception f the file does not exist or can not be read or written.
     */
    public void writeLine(String _line) throws Exception {
        for (int i=0; i<_line.length(); i++)
            writeChar(_line.charAt(i));
    }
    
    /**
     *	Indicates if we've achieved to the end of file.
     *	@return Returns 0 if we've arrived to the end.
     * @throws java.lang.Exception if the file does not exist or can not be read or written.
     */
    public int fin() throws Exception {
        return fileInput.available();	
    }

    /**
     *	Close the file we've read.
     * @throws java.lang.Exception if the file does not exist or can not be read or written.
     */
    public void closeRead() throws Exception {
	fileInput.close();
    }
    
    /**
     *	Close the file we've writen.
     * @throws java.lang.Exception if the file does not exist or can not be read or written.
     */
    public void closeWrite() throws Exception {
	fileOutput.close();
    }
    
    /**
     *	Reads a whole file
     * @return the whole file 
     */
     public String readAllFile() {
        String ret="";
        try {
            while (fin()!=0) {
                ret += readLine();
            }
            return ret;
        }catch(Exception e) {
            return null;	
        }
     }
}

