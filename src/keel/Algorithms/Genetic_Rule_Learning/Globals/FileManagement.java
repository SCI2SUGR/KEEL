/*
 * FileManagement.java
 *
 * Created on 9 de abril de 2004, 20:19
 */

/**
 * Generic class to manage text files
 */

package keel.Algorithms.Genetic_Rule_Learning.Globals;

import java.io.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.lang.*;

public class FileManagement {
    FileInputStream fileInput;
    FileOutputStream fileOutput;
    
    /** Creates a new instance of FileManagement */
    public FileManagement() {
    }

    /**
     *  Init a file for reading.
     *  @param _name path+name of file to read.
     */
    public void initRead(String _name) throws Exception {
	fileInput = new FileInputStream(_name);
    }
    
    /**
     *  Init a file for reading.
     *  @param _name path+name of file to read.
     */
    public void initWrite(String _name) throws Exception {
	fileOutput = new FileOutputStream(_name);
    }
    
    /**
     *	Reades a char from file.
     *	@return Character readed.
     */
    public char readChar() throws Exception {
        return (char)fileInput.read();
    }
    
    /**
     *	Writes a char from file.
     */
    public void writeChar(char _c) throws Exception {
        byte[] b=new byte[1];
        b[0]=(byte)_c;
        fileOutput.write(b);
    }

    /**
     *	Reads a line from the file.
     *	@return Line readed.
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

    public void writeLine(String _line) throws Exception {
        for (int i=0; i<_line.length(); i++)
            writeChar(_line.charAt(i));
    }
    
    /**
     *	Indicates if we've achieved to the end of file.
     *	@return Returns 0 if we've arrived to the end.
     */
    public int fin() throws Exception {
        return fileInput.available();	
    }

    /**
     *	Close the file we've read.
     */
    public void closeRead() throws Exception {
	fileInput.close();
    }
    
    /**
     *	Close the file we've writen.
     */
    public void closeWrite() throws Exception {
	fileOutput.close();
    }
    
    /**
     *	Reads a whole file
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
